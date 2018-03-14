package com.javahis.ui.odi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import oracle.jdbc.oracore.TDSPatch;

import org.apache.commons.lang.time.DateUtils;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.clp.CLPTool;
import jdo.ctr.CTRPanelTool;
import jdo.emr.EMRAMITool;
import jdo.emr.EMRCdrTool;
import jdo.ibs.IBSOrderdTool;
import jdo.ibs.IBSTool;
import jdo.ins.INSTJTool;
import jdo.odi.ODIPICTool;
import jdo.odi.OdiDrugAllergy;
import jdo.odi.OdiMainTool;
import jdo.odi.OdiObject;
import jdo.odi.OdiOrderTool;
import jdo.odi.OdiSqlObject;
import jdo.odo.OpdRxSheetTool;
import jdo.opd.DrugAllergyTool;
import jdo.opd.TotQtyTool;
import jdo.ope.OPEDeptOpTool;
import jdo.pha.PassTool;
import jdo.sys.DictionaryTool;
import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import jdo.sys.PatTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.root.client.SocketLink;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TMovePane;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TRootPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.DateTool;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.system.combo.TComboOrgCode;
import com.javahis.system.textFormat.TextFormatINDOrg;
import com.javahis.system.textFormat.TextFormatSYSPhaFreq;
import com.javahis.system.textFormat.TextFormatSYSPhaRoute;
import com.javahis.ui.opd.CDSSStationDosntWork;
import com.javahis.ui.opd.CDSSStationDrools;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.ADMUtil;
import com.javahis.util.AMIUtil;
import com.javahis.util.OdiUtil;
import com.javahis.util.OdoUtil;
import com.javahis.util.OrderUtil;
import com.javahis.util.StringUtil;
import com.javahis.util.SysFeeUtil;

import device.PassDriver;

/**
 * <p>
 * Title: 住院管理=>住院医生站系统=>住院医生站
 * </p>
 * 
 * <p>
 * 
 * Description: 住院医生站
 * 
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
public class ODIStationControl extends TControl {
	/**
	 * 临时医嘱TABLE
	 */
	public static String TABLE1 = "TABLE1";
	/**
	 * 长期医嘱TABLE
	 */
	public static String TABLE2 = "TABLE2";
	/**
	 * 出院带药医嘱TABLE
	 */
	public static String TABLE3 = "TABLE3";
	/**
	 * 中药饮片TABLE
	 */
	public static String TABLE4 = "TABLE4";
	/**
	 * 过敏记录
	 */
	private static String GMTABLE = "DRUGALLERGY_TABLE";
	/**
	 * 院前用药
	 */
	private static String TABLE6 = "TABLE6";
	/**
	 * 术中用药
	 */
	private static String TABLE7 = "TABLE7";
	/**
	 * 用血记录
	 */
	private static String TABLE8 = "TABLE8";
	/**
	 * 就诊号
	 */
	private String caseNo;
	/**
	 * 医嘱对象
	 */
	public OdiObject odiObject = new OdiObject();
	/**
	 * SQL对象
	 */
	private OdiSqlObject odiSqlObject = new OdiSqlObject();
	/**
	 * Socket传送护士站工具
	 */
	private SocketLink client1;
	/**
	 * 病案号
	 */
	private String mrNo;
	/**
	 * 姓名
	 */
	private String patName;
	/**
	 * 设置药房
	 */
	private String orgCode;
	/**
	 * 停止划价注记
	 */
	private boolean stopBillFlg;
	/**
	 * 住院号
	 */
	private String ipdNo;
	/**
	 * 住院日期
	 */
	private Timestamp admDate;
	/**
	 * 设置姓名1
	 */
	private String patName1;
	/**
	 * 设置生日
	 */
	private Timestamp birthDay;
	/**
	 * 设置性别
	 */
	private String sexCode;
	/**
	 * 设置邮编
	 */
	private String postCode;
	/**
	 * 设置通讯地址
	 */
	private String address;
	/**
	 * 设置单位
	 */
	private String companyDesc;
	/**
	 * 设置电话
	 */
	private String tel;
	/**
	 * 设置电话
	 */
	private String idNo;
	/**
	 * 主诊断
	 */
	private String mainDiag;
	/**
	 * 身份
	 */
	private String ctzCode;
	/**
	 * 科室
	 */
	private String deptCode;
	/**
	 * 病区
	 */
	private String stationCode;
	/**
	 * 医生站保存注记
	 */
	private boolean saveFlg;
	/**
	 * 最新诊断代码
	 */
	private String icdCode;
	/**
	 * 最新诊断名称
	 */
	private String icdDesc;
	/**
	 * 颜色闪动
	 */
	Thread colorThread;
	/**
	 * 要闪动的行
	 */
	int colorRow = -1;
	/**
	 * 闪动的TABLE类型
	 */
	int colorType;
	/**
	 * 闪动状态
	 */
	boolean colorRowState;
	/**
	 * 管制药品颜色
	 */
	Color ctrlDrugClassColor = new Color(255, 0, 0);
	/**
	 * 从手术套餐传回标记 yanjing 20130908
	 */
	boolean antiFechFlg;
	/**
	 * 医保病人自费药品颜色
	 */
	Color nhiColor = new Color(128, 0, 128);
	/**
	 * 抗生素药品颜色
	 */
	Color antibioticColor = new Color(255, 0, 0);
	/**
	 * 普通颜色
	 */
	Color normalColor = new Color(0, 0, 0);
	/**
	 * 普通背景颜色
	 */
	Color normalColorBJ = new Color(255, 255, 255);

	/**
	 * DC医嘱背景颜色
	 */
	Color dcColor = new Color(192, 192, 192);
	/**
	 * 护士审核后的颜色(浅蓝)
	 */
	// Color checkColor = new Color(146, 200, 255);
	Color checkColor = new Color(193, 224, 255);
	/**
	 * 有护士备注的医嘱(粉红)
	 */
	Color nsNodeColor = new Color(255, 170, 255);
	// 过敏史变换为红色
	Color red = new Color(255, 0, 0); // ======pangben modify 20110608

	Color blue = new Color(0, 0, 255); // ==============cxcx

	private boolean antflg = true;
	/**
	 * 转换窗口开关
	 */
	boolean chagePage = true;
	/**
	 * 上一次操作页面的INDEX
	 */
	int indexPage;
	/**
	 * 当前编辑行
	 */
	int rowOnly;
	/**
	 * 引用表单注记
	 */
	boolean yyList = false;
	/**
	 * 清空注记
	 */
	String clearFlg = "N";
	/**
	 * 过敏记录
	 */
	public OdiDrugAllergy odiDrugArrergy;
	/**
	 * 出院带药处方签号COMBO
	 */
	private TParm rxNoTComboParm;
	/**
	 * 中药饮片COMBO
	 */
	private TParm igRxNoTComboParm;
	/**
	 * 当前处方签
	 */
	private String onlyRxNo;
	/**
	 * 会诊注记
	 */
	private boolean oidrFlg;
	/**
	 * 合理用药
	 */
	private boolean passIsReady = false;

	private boolean enforcementFlg = false;

	private int warnFlg;

	private boolean passFlg;
	/**
	 * ICU注记
	 */
	private boolean icuFlg;
	private boolean opeFlg = false;// 术中医嘱注记wanglong add 20140707
	private String opDeptCode = "";// 手术科室wanglong add 20140707
	private String opBookSeq = "";// 手术申请单号wanglong add 20140707
	private Compare compare = new Compare();
	private boolean ascending = false;
	private TableModel model;
	private int sortColumn = -1;
	private String phaApproveFlg = "";// 抗菌药品操作开立是否填写会诊申请单==pangben 2013-9-10
	private boolean tabSaveFlg = false;// yanjing 切换页签保存标记
	// private String arvFlg = "";//yanjing 是否越权标记

	// private ODIStationDrools odiStationDrools;
	private CDSSStationDrools odiStationDrools = new CDSSStationDosntWork();
	private boolean opeClpFlg = false;// =====pangben 2015-8-14
	// 手术医生站界面临床路径时程赋值使用

	private String tempPath;// add by huangjw
	private String serverPath;// add by huangjw
	// private TParm opeParm=new TParm();
	//private String dayOpeCode;//日间手术标记 add  by huangjw
	private String dayOpeFlg;
	private TParm caParm;
	private boolean BYXflg=true;

	// modified by WangQing 20170428
	/**
	 * 教学医生站标记
	 */
	private boolean tFlg = false;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		// 2012/03/04工具条加滚动条START========================$$//
		TPanel tPanel2 = (TPanel) this.getComponent("TPanel2");
		TRootPanel tRootPanel = (TRootPanel) this.getComponent("tRootPanel_1");

		JScrollPane scrollPane = new JScrollPane(tRootPanel);
		scrollPane.setBounds(2, 3, 135, 1300);
		tRootPanel.setPreferredSize(new Dimension(scrollPane.getWidth(),
				scrollPane.getHeight() + 400));

		tPanel2.add(scrollPane);
		tRootPanel.revalidate();
		// $$=============add by lx
		// 2012/03/04工具条加滚动条END========================$$//
		tempPath = "C:\\JavaHisFile\\temp\\pdf";
		serverPath = "";
		File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		initUiView();
		// 初始化页面
		this.initPage();
		
		//machao  start  20170504 感染页面调用
		Object obj = this.getParameter();	
		if(obj instanceof TParm){
			if(!StringUtil.isNullString(((TParm)obj).getValue("INF"))){
				callFunction("UI|close|setEnabled", false); 
				return;
			}			
		}
		//machao end 20170504

		
		TDS tds = odiObject.getDS("ODI_ORDER");

		// =======pangben modify 20110608 过敏史标签变换颜色
		onDiagPnChange(true);

		// $$=============add by lx

		if (CDSSStationDrools.isCdssOn(Operator.getRegion())) {
			odiStationDrools = new ODIStationDrools(this);
		}
		if (((TParm) this.getParameter()).getData("ODI", "OPERATOR") != null
				&& ((TParm) this.getParameter()).getData("ODI", "OPERATOR")
				.equals("OPE_N")) {// add by huangjw 20151105

		} else {
			odiStationDrools.fireRules(odiObject, 2);
		}
		
		
		
	}

	/**
	 * 由病患管理的查询调用 再次赋值参数
	 */
	public void onInitReset() {
		// 初始化页面
		this.initPage();
	}

	/**
	 * 初始化页面
	 */
	public void initPage() {
		// modified by WangQing 20170518 T_FLG
//		this.tFlg = (boolean) (((TParm) this.getParameter()).getData("ODI", "T_FLG"));	
		this.tFlg =  ((TParm) this.getParameter()).getBoolean("ODI", "T_FLG");
		System.out.println("------20170518:tFlg="+tFlg);
		
		// 停止划价注记STOP_BILL_FLG
		this.setStopBillFlg(stopFlg());
		// 设置就诊号
		this.setCaseNo(((TParm) this.getParameter()).getData("ODI", "CASE_NO")
				.toString());
		// 设置病案号
		this.setMrNo(((TParm) this.getParameter()).getData("ODI", "MR_NO")
				.toString());
		// 设置病患姓名
		this.setPatName(((TParm) this.getParameter())
				.getData("ODI", "PAT_NAME").toString());
		// 设置住院号
		this.setIpdNo(((TParm) this.getParameter()).getData("ODI", "IPD_NO")
				.toString());
		// 设置药房代码
		this.setOrgCode(((TParm) this.getParameter())
				.getData("ODI", "ORG_CODE").toString());
		// 住院日期
		this.setAdmDate((Timestamp) ((TParm) this.getParameter()).getData(
				"ODI", "ADM_DATE"));
		// 设置姓名1
		this.setPatName1(((TParm) this.getParameter()).getValue("ODI",
				"PAT_NAME1"));
		// 设置生日
		this.setBirthDay((Timestamp) ((TParm) this.getParameter()).getData(
				"ODI", "BIRTH_DATE"));
		// 设置性别
		this.setSexCode(((TParm) this.getParameter())
				.getData("ODI", "SEX_CODE").toString());
		// 设置邮编
		this.setPostCode(((TParm) this.getParameter()).getData("ODI",
				"POST_CODE").toString());
		// 设置通讯地址
		this.setAddress(((TParm) this.getParameter()).getData("ODI", "ADDRESS")
				.toString());
		// 设置单位
		this.setCompanyDesc(((TParm) this.getParameter()).getData("ODI",
				"COMPANY_DESC").toString());
		// 设置电话
		this.setTel(((TParm) this.getParameter()).getData("ODI", "TEL1")
				.toString());
		// 设置身份证号
		this.setIdNo(((TParm) this.getParameter()).getData("ODI", "IDNO")
				.toString());
		// 设置科室
		this.setDeptCode(((TParm) this.getParameter()).getData("ODI",
				"DEPT_CODE").toString());
		// 设置病区
		this.setStationCode(((TParm) this.getParameter()).getData("ODI",
				"STATION_CODE").toString());
		// 保存注记
		this.setSaveFlg(((TParm) this.getParameter()).getBoolean("ODI",
				"SAVE_FLG"));
		// 最新诊断代码
		this.setIcdCode(((TParm) this.getParameter()).getValue("ODI",
				"ICD_CODE"));
		// 最新诊断名称
		this.setIcdDesc(((TParm) this.getParameter()).getValue("ODI",
				"ICD_DESC"));
		// 会诊注记
		this.setOidrFlg(((TParm) this.getParameter()).getBoolean("ODI",
				"OIDRFLG"));
		this.setDayOpeFlg(((TParm) this.getParameter()).getValue("ODI",
				"DAY_OPE_FLG"));
		// 身份
		this.setCtzCode(((TParm) this.getParameter())
				.getData("ODI", "CTZ_CODE").toString());
		passIsReady = ((TParm) this.getParameter()).getBoolean("ODI", "PASS");
		enforcementFlg = ((TParm) this.getParameter()).getBoolean("ODI",
				"FORCE");
		warnFlg = ((TParm) this.getParameter()).getInt("ODI", "WARN");
		passFlg = ((TParm) this.getParameter()).getBoolean("ODI", "passflg");
		if (null != ((TParm) this.getParameter()).getValue("ODI", "OPECLP_FLG")) {// ====pangben
			// 2015-8-14
			// 手术医生站时程赋值
			opeClpFlg = ((TParm) this.getParameter()).getBoolean("ODI",
					"OPECLP_FLG");
		}
		// ICU注记
		this.setIcuFlg(((TParm) this.getParameter()).getBoolean("ODI",
				"ICU_FLG"));
		// 术中医嘱注记wanglong add 20140707
		this.setOpeFlg(((TParm) this.getParameter()).getBoolean("ODI",
				"OPE_FLG"));
		// 手术科室wanglong add 20140707
		this.setOpDeptCode(((TParm) this.getParameter()).getValue("ODI",
				"OP_DEPT_CODE"));
		// 手术申请单号 wanglong add 20140707
		this.setOpBookSeq(((TParm) this.getParameter()).getValue("ODI",
				"OPBOOK_SEQ"));
		
		// 初始化页面组件
		this.initPageComponent();
		// 查询初始化DataStore
		initDataStoreToTable();

		TDS tds = odiObject.getDS("ODI_ORDER");
		// 注册SYSFeePopup
		initSYSFeePopup();
		// 初始化TABLE
		initTableData();

	}

	/**
	 * 初始化界面
	 */
	public void initUiView() {
		if (((TParm) this.getParameter()).getData("ODI", "OPERATOR") != null) {
			if (((TParm) this.getParameter()).getData("ODI", "OPERATOR")
					.equals("DR")) {
				((TButton) this.getComponent("DR_CHECK")).setVisible(true);// 显示手术医生确认医嘱按钮
			}
			if (((TParm) this.getParameter()).getData("ODI", "OPERATOR")
					.equals("OPE_N")) {
				TMenuItem menuItem = (TMenuItem) getComponent("close");
				menuItem.setActionMessage("onClose");// 改变关闭方式
			}
			return;
		}
		TMenuItem charge = (TMenuItem) getComponent("charge");
		charge.setVisible(false);
		TTable table = (TTable) this.getComponent(TABLE1);
		table.setLockColumns("4,12,18,19,20,21,22,23,24,25,26,27,28,29,30,31");
	}

	/**
	 * RadioButton选中
	 */
	public void onRadioSel() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		int type = 0;
		switch (tab.getSelectedIndex()) {
		// 临时
		case 0:
			type = getRadioSelType(0);
			break;
			// 长期
		case 1:
			type = getRadioSelType(1);
			break;
		}
		setQueryTime(type);
		// System.out.println("0000998765"+this.getTRadioButton("DCORDER").isSelected());
		// System.out.println("qqqqqqqqqq98765===="+type);
		if (this.getTRadioButton("DCORDER").isSelected() && type == 5)// shibl
		{
			// System.out.println("===============qqqqqqqqqq98765===="+type);
			dcOrderShow();
		}
	}

	/**
	 * 设置时间
	 * 
	 * @param type
	 *            int
	 */
	public void setQueryTime(int type) {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp startDate = null;
		Timestamp endDate = null;
		switch (type) {
		// 使用中临时
		case 1:
			// 起日
			startDate = StringTool.getTimestamp(StringTool.getString(sysDate,
					"yyyy/MM/dd")
					+ " 00:00:00", "yyyy/MM/dd HH:mm:ss");
			// 迄日
			endDate = StringTool.getTimestamp("9999/12/31 23:59:59",
					"yyyy/MM/dd HH:mm:ss");
			// 起日YYYY/MM/DD
			this.setValue("START_DATEST", startDate);
			// 迄日YYYY/MM/DD
			this.setValue("END_DATEST", endDate);
			break;
			// 已停用临时
		case 2:
			// 起日
			startDate = this.getAdmDate();
			// 迄日
			endDate = StringTool.getTimestamp("9999/12/31 23:59:59",
					"yyyy/MM/dd HH:mm:ss");
			// 起日YYYY/MM/DD
			this.setValue("START_DATEST", startDate);
			// 迄日YYYY/MM/DD
			this.setValue("END_DATEST", endDate);
			break;
			// 全部临时
		case 3:
			// 起日
			startDate = this.getAdmDate();
			// 迄日
			endDate = StringTool.getTimestamp("9999/12/31 23:59:59",
					"yyyy/MM/dd HH:mm:ss");
			// 起日YYYY/MM/DD
			this.setValue("START_DATEST", startDate);
			// 迄日YYYY/MM/DD
			this.setValue("END_DATEST", endDate);
			break;
			// //使用中长期
		case 4:
			// 起日
			startDate = StringTool.getTimestamp(StringTool.getString(sysDate,
					"yyyy/MM/dd")
					+ " 00:00:00", "yyyy/MM/dd HH:mm:ss");
			// 迄日
			endDate = StringTool.getTimestamp("9999/12/31 23:59:59",
					"yyyy/MM/dd HH:mm:ss");
			// 起日YYYY/MM/DD
			this.setValue("START_DATEUD", startDate);
			// 迄日YYYY/MM/DD
			this.setValue("END_DATEUD", endDate);
			break;
			// 已停用长期
		case 5:
			// 起日
			startDate = this.getAdmDate();
			// 迄日
			endDate = StringTool.getTimestamp("9999/12/31 23:59:59",
					"yyyy/MM/dd HH:mm:ss");
			// 起日YYYY/MM/DD
			this.setValue("START_DATEUD", startDate);
			// 迄日YYYY/MM/DD
			this.setValue("END_DATEUD", endDate);
			break;
			// 全部长期
		case 6:
			// 起日
			startDate = this.getAdmDate();
			// 迄日
			endDate = StringTool.getTimestamp("9999/12/31 23:59:59",
					"yyyy/MM/dd HH:mm:ss");
			// 起日YYYY/MM/DD
			this.setValue("START_DATEUD", startDate);
			// 迄日YYYY/MM/DD
			this.setValue("END_DATEUD", endDate);
			break;
		}
	}

	/**
	 * 找到选中的RadionButton
	 * 
	 * @param type
	 *            int
	 * @return int
	 */
	public int getRadioSelType(int type) {
		switch (type) {
		case 0:
			if (this.getTRadioButton("YXORDER").isSelected())
				return 1;
			if (this.getTRadioButton("STOPORDER").isSelected())
				return 2;
			if (this.getTRadioButton("ALLORDER").isSelected())
				return 3;
			break;
		case 1:
			if (this.getTRadioButton("UDUTIL").isSelected()) {// shibl modify
				// 20130515
				callFunction("UI|TABLE22|setVisible", false);
				callFunction("UI|TABLE2|setVisible", true);
				return 4;
			}
			if (this.getTRadioButton("DCORDER").isSelected()) {// shibl modify
				// 20130515
				callFunction("UI|TABLE2|setVisible", false);
				callFunction("UI|TABLE22|setVisible", true);
				// dcOrderShow();
				return 5;
			}
			if (this.getTRadioButton("UDALLORDER").isSelected()) {// shibl
				// modify
				// 20130515
				callFunction("UI|TABLE22|setVisible", false);
				callFunction("UI|TABLE2|setVisible", true);
				return 6;
			}
			break;
		case 6:
			return 5;
		}
		return 0;
	}

	/**
	 * dc医嘱显示方法 shibl modify 20130515
	 */
	public void dcOrderShow() {
		String sline = StringTool.getString((Timestamp) this
				.getValue("START_DATEUD"), "yyyyMMdd HHmmss");
		String eline = StringTool.getString((Timestamp) this
				.getValue("END_DATEUD"), "yyyyMMdd HHmmss");
		String nline = StringTool.getString((Timestamp) SystemTool
				.getInstance().getDate(), "yyyyMMdd HHmmss");
		String sql = "SELECT A.*, A.ORDER_DESC||A.GOODS_DESC||A.SPECIFICATION AS ORDER_DESCCHN,A.EFF_DATE AS EFF_DATEDAY FROM ODI_ORDER A "
				+ " WHERE A.CASE_NO='"
				+ this.caseNo
				+ "' AND A.DC_DATE IS NOT NULL AND "
				+ " A.DC_DATE <= TO_DATE('"
				+ nline
				+ "','YYYYMMDD HH24MISS') AND "
				+ // yanjing 20131212 modify 停用时间与当前时间比较
				"A.ORDER_DATE>=TO_DATE('"
				+ sline
				+ "','YYYYMMDD HH24MISS') AND A.ORDER_DATE<=TO_DATE('"
				+ eline
				+ "','YYYYMMDD HH24MISS')"
				+ " AND A.RX_KIND='UD' AND A.HIDE_FLG='N'";
		TTable table = this.getTTable("TABLE22");
		// System.out.println("parm长期:::;PANGBEN:::"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("parm:::;PANGBEN:::"+parm);
		if (parm.getCount() <= 0) {
			table.removeRowAll();
			return;
		}
		int rowCount = parm.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) {
			// System.out.println("rrrrrrrowCount is::"+rowCount);
			TParm action = parm.getRow(i);
			// System.out.println("------++++++action is :::"+action);
			// ================= chenxi 药品提示信息
			String orderCode = action.getValue("ORDER_CODE");
			String sql1 = "SELECT ORDER_CODE,DRUG_NOTES_DR FROM SYS_FEE WHERE ORDER_CODE = '"
					+ orderCode + "' ";
			TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql1));
			sqlparm = sqlparm.getRow(0);
			// System.out.println("===========sqlparm========"+sqlparm);
			// ============ chenxi

			// 是否是医保药品
			if (OrderUtil.getInstance().isNhiPat(this.getCaseNo())) {
				if ("C".equals(action.getValue("INSPAY_TYPE"))) {
					table.setRowTextColor(i, nhiColor);
				}
			} else {
				table.setRowTextColor(i, normalColor);
			}
			// 判断管制药品等级
			if ("PHA".equals(action.getValue("CAT1_TYPE"))) {
				// 是否是管制药品
				if (action.getValue("CTRLDRUGCLASS_CODE").length() != 0) {
					table.setRowTextColor(i, ctrlDrugClassColor);
				}
			} else {
				table.setRowTextColor(i, normalColor);
			}
			// 判断抗生素
			// System.out.println("898989action is::"+action);
			if (action.getValue("ANTIBIOTIC_CODE").length() != 0) {// =====pangben
				// 2013-11-14
				// 停用医嘱校验抗菌药品字体颜色
				// System.out.println("======++++=====");
				table.setRowTextColor(i, antibioticColor);
			} else {
				if (sqlparm.getValue("DRUG_NOTES_DR").length() != 0) {// ===========cxcx
					table.setRowTextColor(i, blue);
					// continue;
				} else {
					table.setRowTextColor(i, normalColor);
				}
			}
			// 是否是停用医嘱
			// 取得系统与停用时间比较,停用时间小于等于系统时间时,背景设置成灰色
			// 取得系统时间
			String sysdate = SystemTool.getInstance().getDate().toString();
			String dc_date = action.getValue("DC_DATE");
			if (action.getValue("DC_DR_CODE").length() != 0
					&& sysdate.compareTo(dc_date) >= 0) {
				// this.messageBox("2222222222222222");
				table.setRowColor(i, dcColor);
				continue;
			} else {
				table.setRowColor(i, normalColorBJ);
			}

			// 是否有护士备注NS_NOTE
			if (action.getValue("NS_NOTE").length() != 0) {
				table.setRowColor(i, nsNodeColor);
				continue;
			} else {
				table.setRowColor(i, normalColorBJ);
			}
			// 护士是否审核
			if (action.getValue("NS_CHECK_CODE").length() != 0) {
				table.setRowColor(i, checkColor);
				continue;
			} else {
				table.setRowColor(i, normalColorBJ);
			}
		}
		this.getTTable("TABLE22").setParmValue(parm);
	}

	/**
	 * 得到RadioButton
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
	}

	/**
	 * 初始化TABLE设置
	 */
	public void initTableData() {
		if (this.isStopBillFlg())
			return;
		// 初始化
		TTable table = this.getTTable(TABLE1);
		table.getTable().grabFocus();
		table.setSelectedRow(rowOnly);
		table.setSelectedColumn(2);
	}

	/**
	 * 初始化出院带药COMBO
	 */
	public void initDSRxNoCombo() {
		this.getTComboBox("RX_NO").setParmMap(
				"id:ID;name:NAME;text:TEXT;value:ACTIVE;py1:PRESRT_NO");
		rxNoTComboParm = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT DISTINCT RX_NO AS ID,'处方' AS NAME,'【'||PRESRT_NO||'】' AS TEXT,'N' AS ACTIVE,PRESRT_NO FROM ODI_ORDER WHERE RX_KIND='DS' AND CASE_NO='"
								+ this.getCaseNo() + "' ORDER BY ID"));
		this.getTComboBox("RX_NO").setParmValue(rxNoTComboParm);
	}

	/**
	 * 初始化中药饮片COMBO
	 */
	public void initIGRxNoCombo() {
		this.getTComboBox("IG_RX_NO").setParmMap(
				"id:ID;name:NAME;text:TEXT;value:ACTIVE;py1:PRESRT_NO");
		igRxNoTComboParm = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT DISTINCT RX_NO AS ID,'处方' AS NAME,'【'||PRESRT_NO||'】' AS TEXT,'N' AS ACTIVE,PRESRT_NO FROM ODI_ORDER WHERE RX_KIND='IG' AND CASE_NO='"
								+ this.getCaseNo() + "' ORDER BY ID"));
		this.getTComboBox("IG_RX_NO").setParmValue(igRxNoTComboParm);
	}

	/**
	 * 得到COMBO
	 * 
	 * @param tag
	 *            String
	 * @return TComboBox
	 */
	public TComboBox getTComboBox(String tag) {
		return (TComboBox) this.getComponent(tag);
	}

	/**
	 * 初始化页面组件
	 */
	public void initPageComponent() {
		// 设置感染标记 -start machao 20170615
		TParm p = new TParm(TJDODBTool.getInstance().select("SELECT INF_FLG FROM ADM_INP WHERE CASE_NO='"+this.getCaseNo()+"'"));
		if(!StringUtil.isNullString(p.getData("INF_FLG", 0)+"") &&
				"Y".equals(p.getData("INF_FLG", 0))){
			this.setValue("INF_FLG", "Y");
		}
		//-end machao 20170615
		// 设置临时医嘱的药房
		this.setValue("DEPT_CODEST", this.getOrgCode());
		// 设置长期医嘱的药房
		this.setValue("DEPT_CODEUD", this.getOrgCode());
		// 设置出院带药医嘱的药房
		this.setValue("DEPT_CODEDS", this.getOrgCode());
		// 设置中药饮片医嘱的药房
		this.setValue("DEPT_CODEIG", this.getOrgCode());
		// 设置出院带药处方签
		initDSRxNoCombo();
		// 初始化中药饮片COMBO
		initIGRxNoCombo();
		// 起日
		Timestamp startDate = StringTool.getTimestamp(StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy/MM/dd")
				+ " 00:00:00", "yyyy/MM/dd HH:mm:ss");
		// 迄日
		Timestamp endDate = StringTool.getTimestamp("9999/12/31 23:59:59",
				"yyyy/MM/dd HH:mm:ss");
		// 起日YYYY/MM/DD
		this.setValue("START_DATEST", startDate);
		// 起日HH:mm:ss
		// this.setValue("START_MISSST",startDate);
		// 迄日YYYY/MM/DD
		this.setValue("END_DATEST", endDate);
		// 迄日HH:mm:ss
		// this.setValue("END_MISSST",endDate);
		// 开立状态
		((TComboBox) this.getComponent("KLSTAR")).setSelectedIndex(0);
		// 起日YYYY/MM/DD
		this.setValue("START_DATEUD", this.getAdmDate());
		// 起日HH:mm:ss
		// this.setValue("START_MISSUD",startDate);
		// 迄日YYYY/MM/DD
		this.setValue("END_DATEUD", endDate);
		// 迄日HH:mm:ss
		// this.setValue("END_MISSUD",endDate);
		// 开立状态
		((TComboBox) this.getComponent("KLSTARUD")).setSelectedIndex(0);
		// 开立状态
		((TComboBox) this.getComponent("KLSTARDS")).setSelectedIndex(0);
		// 中药部分初始化
		// 日/份
		this.setValue("RF", odiObject.getAttribute(odiObject.DCT_TAKE_DAYS));
		// 饮片计量
		this.setValue("YPJL", odiObject.getAttribute(odiObject.DCT_TAKE_QTY));
		// 中药默认频次
		this.setValue("IGFREQCODE", odiObject
				.getAttribute(odiObject.G_FREQ_CODE));
		// 中药默认用法
		this.setValue("IG_ROUTE", odiObject
				.getAttribute(odiObject.G_ROUTE_CODE));
		// 中药煎药方式
		this.setValue("IG_DCTAGENT", odiObject
				.getAttribute(odiObject.G_DCTAGENT_CODE));
		// 锁住住院中医TABLE
		this.getTTable(TABLE4).getTable().getTableHeader()
		.setReorderingAllowed(false);
		// =================术中医嘱 begin wanglong add 20140707
		if (isOpeFlg()) {
			TLabel label = (TLabel) this.getComponent("tLabel_9");
			label.setText("执行科室");// 更改“发药科室”为“执行科室”
			label.setZhText("执行科室");
			TComboBox deptCodeST = (TComboBox) this.getComponent("DEPT_CODEST");
			String deptSql = "SELECT DEPT_CODE ID, DEPT_CHN_DESC NAME, DEPT_ENG_DESC ENNAME, PY1, PY2 "
					+ " FROM SYS_DEPT WHERE OP_FLG = 'Y' ORDER BY DEPT_CODE";
			TParm deptParm = new TParm(TJDODBTool.getInstance().select(deptSql));
			if (deptParm.getErrCode() < 0) {
				this.messageBox_("取得执行科室信息失败");
				return;
			}
			deptCodeST.setParmValue(deptParm);// “执行科室”只列出手术科室
			deptCodeST.setParmMap("id:ID;name:NAME");
			deptCodeST.setShowID(true);
			deptCodeST.setShowName(true);
			deptCodeST.setTableShowList("name");


			//modify by yangjj 20161230 #4648
			//if (!getOpDeptCode().equals("")
			//&& deptParm.getValue("ID").contains(getOpDeptCode())) {
			//deptCodeST.setSelectedID(getOpDeptCode());
			//} else {
			deptCodeST.setSelectedID(Operator.getDept());
			//}
			((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(1,
					false);
			((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(2,
					false);
			((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(3,
					false);
			// 手术医生站禁用院前用药
			((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(5,
					false);
			// 手术医生站禁用术中用药
			((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(6,
					false);
			// 手术医生站禁用用血记录
			((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(7,
					false);
			if (((TParm) this.getParameter()).getData("ODI", "OPERATOR") != null
					&& ((TParm) this.getParameter()).getData("ODI", "OPERATOR")
					.equals("OPE_N")) {
				((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(5,
						false);
				((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(6,
						false);
				((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(7,
						false);
			}
			TPanel panel_2 = (TPanel) getComponent("tPanel_2");
			Component[] component_2 = panel_2.getComponents();
			for (Component component2 : component_2) {
				component2.setEnabled(false);// 禁用第二个页签中的所有组件
			}
			getTTable("TABLE2").setLockColumns("all");
			TPanel panel_3 = (TPanel) getComponent("tPanel_3");
			Component[] component_3 = panel_3.getComponents();
			for (Component component3 : component_3) {
				component3.setEnabled(false);// 禁用第三个页签中的所有组件
			}
			getTTable("TABLE3").setLockColumns("all");
			TPanel panel_4 = (TPanel) getComponent("tPanel_4");
			Component[] component_4 = panel_4.getComponents();
			for (Component component4 : component_4) {
				component4.setEnabled(false);// 禁用第四个页签中的所有组件
			}
			getTTable("TABLE4").setLockColumns("all");
		} else {
			callFunction("UI|medApplyNo|setVisible", false);// 显示
		}
		// =================术中医嘱 end
	}

	/**
	 * 注册SYSFeePopup事件
	 */
	public void initSYSFeePopup() {
		// TABLE1双击事件
		callFunction("UI|" + TABLE1 + "|addEventListener", TABLE1 + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// TABLE2双击事件
		callFunction("UI|" + TABLE2 + "|addEventListener", TABLE2 + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// TABLE3双击事件
		callFunction("UI|" + TABLE3 + "|addEventListener", TABLE3 + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// TABLE4双击事件
		callFunction("UI|" + TABLE4 + "|addEventListener", TABLE4 + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// TABLE4双击事件
		callFunction("UI|" + GMTABLE + "|addEventListener", GMTABLE + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// 院前用药TABLE6单击事件
		callFunction("UI|" + TABLE6 + "|addEventListener", TABLE6 + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// 术中用药TABLE7单击事件
		callFunction("UI|" + TABLE7 + "|addEventListener", TABLE7 + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// 用血记录TABLE8单击事件
		callFunction("UI|" + TABLE8 + "|addEventListener", TABLE8 + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");

		// 临时TABLE1监听事件
		getTTable(TABLE1).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "onCreateEditComoponentST");
		// 长期监听事件
		getTTable(TABLE2).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "onCreateEditComoponentUD");
		// 出院带药监听事件
		getTTable(TABLE3).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "onCreateEditComoponentDS");
		// 中药饮片监听事件
		getTTable(TABLE4).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "onCreateEditComoponentIG");
		// 中药饮片监听事件
		getTTable(GMTABLE).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "onCreateEditComoponentGM");
		// 院前用药监听事件
		getTTable(TABLE6).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "onCreateEditComoponentPA");

		// 临时TABLE值改变监听
		addEventListener(TABLE1 + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValueST");
		// 长期TABLE值改变监听
		addEventListener(TABLE2 + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValueUD");
		// 出院带药TABLE值改变监听
		addEventListener(TABLE3 + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValueDS");
		// 中药饮片TABLE值改变监听
		addEventListener(TABLE4 + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValueIG");
		// 临时医嘱CHECK_BOX监听事件
		getTTable(TABLE1).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValueST");
		// 长期医嘱CHECK_BOX监听事件
		getTTable(TABLE2).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValueUD");
		// 出院带药医嘱CHECK_BOX监听事件
		getTTable(TABLE3).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValueDS");
		// 排序监听
		addListener(getTTable("TABLE22"));
		addListener(getTTable("TABLE7"));
	}

	/**
	 * 下拉选择事件SYSPOU
	 * 
	 * @param obj
	 *            Object
	 */
	public void onComboxSelect(Object obj) {
		int tableType = StringTool.getInt("" + obj);
		switch (tableType) {
		case 0:
			this.getTTable(TABLE1).acceptText();
			break;
		case 1:
			this.getTTable(TABLE2).acceptText();
			break;
		case 2:
			this.getTTable(TABLE3).acceptText();
			break;
		}
	}

	/**
	 * 点击事件
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		// this.messageBox("======onTableClicked======");
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");

		/**
		 * if (tab.getSelectedIndex() == 0) { TTable table=getTTable(TABLE1);
		 * //xueyf begin if(row==table.getSelectedRow() &&
		 * 2==table.getSelectedColumn()){ TParm parm=table.getShowParmValue();
		 * if(parm.getValue("ORDER_DESCCHN",row).startsWith("*")){
		 * table.setLockCell(table.getSelectedRow(),2,true);
		 * table.getDataStore().setActive(row, false); if(!table.isLockCell(row,
		 * 2)){ table.getDataStore().setActive(row, false); table.acceptText();
		 * } table.getDataStore().setActive(row, true); //return ; }else{
		 * table.setLockCell(table.getSelectedRow(),2,false); } } //xueyf end }
		 **/

		if (tab.getSelectedIndex() == 4) {
			TParm parm = this.getTTable(GMTABLE).getDataStore().getRowParm(
					this.getTTable(GMTABLE).getSelectedRow());
			callFunction("UI|setSysStatus", parm.getValue("DRUGORINGRD_CODE")
					+ ":" + parm.getValue("ORDER_DESC"));
			return;
		}
		TDS ds = odiObject.getDS("ODI_ORDER");
		TParm parm = ds.getRowParm(row, ds.PRIMARY);
		if (parm.getValue("ORDER_CODE").length() == 0)
			return;

		// ===============chenxi 医嘱提示问题start
		String orderCode = parm.getValue("ORDER_CODE");
		String sql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC,"
				+ "DESCRIPTION,SPECIFICATION,REMARK_1,REMARK_2,DRUG_NOTES_DR FROM SYS_FEE"
				+ " WHERE ORDER_CODE = '" + orderCode + "'";
		TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
		sqlparm = sqlparm.getRow(0);
		// System.out.println("=====sqlparm======"+sqlparm);
		// 状态条显示
		callFunction("UI|setSysStatus", sqlparm.getValue("ORDER_CODE") + " "
				+ sqlparm.getValue("ORDER_DESC") + " "
				+ sqlparm.getValue("GOODS_DESC") + " "
				+ sqlparm.getValue("DESCRIPTION") + " "
				+ sqlparm.getValue("SPECIFICATION") + " "
				+ sqlparm.getValue("REMARK_1") + " "
				+ sqlparm.getValue("REMARK_2") + " "
				+ sqlparm.getValue("DRUG_NOTES_DR")); // chexi modified
		// DRUG_NOTES_DR
		// ================= chenxi modify 医嘱提示问题2012.06.06 f
		// 状态条显示
		/**
		 * callFunction( "UI|setSysStatus", parm.getValue("ORDER_CODE") + " " +
		 * parm.getValue("ORDER_DESC") + " " + parm.getValue("GOODS_DESC") + " "
		 * + parm.getValue("DESCRIPTION") + " " + parm.getValue("SPECIFICATION")
		 * + " " + parm.getValue("REMARK_1") + " " + parm.getValue("REMARK_2"));
		 **/
	}

	/**
	 * 临时医嘱CHECK_BOX监听事件
	 * 
	 * @param obj
	 *            Object
	 */
	public void onCheckBoxValueST(Object obj) {
		// this.messageBox("onCheckBoxValueST====");
		//TODO 修改临时医嘱checkbox监听事件
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable(TABLE1).getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		TParm linkParm = table.getDataStore().getRowParm(row);
		if ("LINKMAIN_FLG".equals(columnName)) {
			if ("Y".equals(linkParm.getValue("LINKMAIN_FLG"))) {
				// System.out.println("LINKMAIN_FLG Y====");
				if (linkParm.getValue("ORDER_CODE").length() == 0) {
					// 请开立医嘱
					this.messageBox("E0152");
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkFlg = new TParm();
					linkFlg.setData("LINKMAIN_FLG", "N");
					odiObject.setItem(ds, row, linkFlg);
					table.setDSValue(row);
					return;
				}
				// 查询最大连结号
				int maxLinkNo = getMaxLinkNo("ST");
				if (opeFlg) {
					maxLinkNo = getMaxLinkNo("OP");
				}
				TDS ds = odiObject.getDS("ODI_ORDER");
				// 设置最大连结号
				TParm linkP = new TParm();
				linkP.setData("LINK_NO", maxLinkNo);
				// $$========add by lx 2012-06-13 连嘱自动一致 start==========$$//
				linkP.setData("FREQ_CODE", linkParm.getValue("FREQ_CODE"));
				linkP.setData("ROUTE_CODE", linkParm.getValue("ROUTE_CODE"));
				linkP.setData("EFF_DATE", linkParm.getTimestamp("EFF_DATE"));
				//==================add by chenhj start
				linkP.setData("INFLUTION_RATE",linkParm.getValue("INFLUTION_RATE"));
				//==================add by chenhj end

				// =================add by huangjw
				if (((TParm) this.getParameter()).getData("ODI", "OPERATOR") != null
						&& ((TParm) this.getParameter()).getData("ODI",
								"OPERATOR").equals("OPE_N")) {
					linkP.setData("NS_CHECK_DATE", linkParm
							.getTimestamp("NS_CHECK_DATE"));
					linkP.setData("ORDER_DATE", linkParm
							.getTimestamp("ORDER_DATE"));
					linkP.setData("ORDER_DR_CODE", linkParm
							.getValue("ORDER_DR_CODE"));
				}

				// =================add by huangjw
				// $$========add by lx 2012-06-13 end==========$$//

				// $$==============Del by lx 2012/02/23 Start
				// 找到本医嘱的默认设置
				/**
				 * TParm actionParm = this.getPhaBaseData(
				 * 
				 * linkParm.getData("ORDER_CODE").toString(), linkParm
				 * .getData("CAT1_TYPE").toString(), "ST", linkParm);
				 * linkP.setData("FREQ_CODE", actionParm.getValue("FREQ_CODE"));
				 * linkP.setData("ROUTE_CODE",
				 * actionParm.getValue("ROUTE_CODE"));
				 **/
				// $$==============Del by lx 2012/02/23 end================$$//
				odiObject.setItem(ds, row, linkP);
				table.setDSValue(row);
				for (int i = row; i < ds.rowCount(); i++) {
					// this.messageBox("设置编号");
					if (i > row) {
						linkP.setData("LINKMAIN_FLG", "N");
					}
					if (ds.getItemString(i, "SETMAIN_FLG").equals("Y"))
						continue;
					linkP
					.setData("EXEC_DEPT_CODE", this.getExeDeptCodeST(ds
							.getItemString(row, "ORDER_CODE"), row,
							TABLE1, ""));
					odiObject.setItem(ds, i, linkP);
					table.setLockCellRow(i, true);
					table.setLockCell(i, 0, false);// 连接主
					table.setLockCell(i, 1, false);// 组号
					table.setLockCell(i, 2, false);// 医嘱
					table.setLockCell(i, 3, false);// 用量
					//===========================add by wukai on 20160527 start
					table.setLockCell(i, 6, false);//泵入方式
					table.setLockCell(i, 7, false);//输液速率
					//===========================add by wukai on 20160527 end
					if (ds.getItemString(i, "LINK_NO").equals("")
							|| ds.getItemString(i, "LINKMAIN_FLG").equals("Y")) {
						table.setLockCell(i, 5, false);// 用法
						table.setLockCell(i, 8, false);// 频次
						table.setLockCell(i, 9, false);// 执行科室
						table.setLockCell(i, 10, false);// 启用时间

						// ===================================
						if (((TParm) this.getParameter()).getData("ODI",
								"OPERATOR") != null
								&& ((TParm) this.getParameter()).getData("ODI",
										"OPERATOR").equals("OPE_N")) {
							table.setLockCell(i, 22, false);
							table.setLockCell(i, 23, false);
							table.setLockCell(i, 25, false);
						}
						// ===================================
					}
					table.setLockCell(i, 11, false);// 医嘱备注
					table.setLockCell(i, 13, false);// 抗菌标识
					table.setLockCell(i, 14, false);// 续
					table.setLockCell(i, 15, false);// 急
					table.setLockCell(i, 16, false);// 备
					table.setLockCell(i, 17, false);// 给
					table.setDSValue(i);
				}

				// shibl 20121228 modify start 保持所有连接号医嘱相同启用时间
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm temp = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					if (temp.getInt("LINK_NO") == maxLinkNo) {
						temp.setData("EFF_DATE", linkParm
								.getTimestamp("EFF_DATE"));
						temp.setData("EFF_DATEDAY", linkParm
								.getTimestamp("EFF_DATE"));
						// ===================================add by huangjw
						if (((TParm) this.getParameter()).getData("ODI",
								"OPERATOR") != null
								&& ((TParm) this.getParameter()).getData("ODI",
										"OPERATOR").equals("OPE_N")) {
							temp.setData("NS_CHECK_DATE", linkParm
									.getTimestamp("NS_CHECK_DATE"));
							temp.setData("ORDER_DR_CODE", linkParm
									.getValue("ORDER_DR_CODE"));
							temp.setData("ORDER_DATE", linkParm
									.getTimestamp("ORDER_DATE"));
						}

						// ===================================add by huangjw
						// 集合医嘱
						if (this.isOrderSet(temp)) {
							int groupNo = temp.getInt("ORDERSET_GROUP_NO");
							Object objT = odiObject
									.getAttribute(odiObject.OID_DSPN_TIME);
							TDS dsT = odiObject.getDS("ODI_ORDER");
							String buffT = dsT.isFilter() ? ds.FILTER
									: ds.PRIMARY;
							// 新加的数据
							int newRowT[] = dsT.getNewRows(buffT);
							for (int j : newRowT) {
								TParm tempT = dsT.getRowParm(j, buffT);
								if (!dsT.isActive(j, buffT))
									continue;
								if (tempT.getInt("ORDERSET_GROUP_NO") == groupNo) {
									tempT.setData("EFF_DATE", linkParm
											.getTimestamp("EFF_DATE"));
									tempT.setData("EFF_DATEDAY", linkParm
											.getTimestamp("EFF_DATE"));
									// ===================================add by
									// huangjw
									if (((TParm) this.getParameter()).getData(
											"ODI", "OPERATOR") != null
											&& ((TParm) this.getParameter())
											.getData("ODI", "OPERATOR")
											.equals("OPE_N")) {
										tempT.setData("NS_CHECK_DATE", linkParm
												.getTimestamp("NS_CHECK_DATE"));
										tempT.setData("ORDER_DR_CODE", linkParm
												.getValue("ORDER_DR_CODE"));
										tempT.setData("ORDER_DATE", linkParm
												.getTimestamp("ORDER_DATE"));
									}
									// ===================================add by
									// huangjw
									odiObject.setItem(dsT, j, tempT, buffT);
									this.getTTable(TABLE1).setDSValue(i);
								}
							}
						} else {
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
						// shibl 20121228 modify end
					}
				}
			} else {
				// this.messageBox("LINKMAIN_FLG N====");
				TDS ds = odiObject.getDS("ODI_ORDER");
				// 设置最大连结号
				TParm linkP = new TParm();
				linkP.setData("LINK_NO", "");
				// 找到本医嘱的默认设置
				TParm actionParm = this.getPhaBaseData(linkParm.getData(
						"ORDER_CODE").toString(), linkParm.getData("CAT1_TYPE")
						.toString(), "ST", linkParm);
				linkP.setData("FREQ_CODE", actionParm.getValue("FREQ_CODE"));
				linkP.setData("ROUTE_CODE", actionParm.getValue("ROUTE_CODE"));

				// odiObject.setItem(ds, row, linkP);
				// table.setDSValue(row);
				TParm linkBlankP = new TParm();
				linkBlankP.setData("LINK_NO", "");
				TParm linkMainP = new TParm();
				linkMainP.setData("LINKMAIN_FLG", "N");
				if (ds.getItemInt(row, "LINK_NO") > 0) {
					int linktemp = ds.getItemInt(row, "LINK_NO");
					for (int i = row; i < ds.rowCount(); i++) {
						if (linktemp == ds.getItemInt(i, "LINK_NO")) {
							// this.messageBox("i"+i);
							if (i > row)
								linkBlankP.setData("EXEC_DEPT_CODE", this
										.getExeDeptCodeST(ds.getItemString(i,
												"ORDER_CODE"), i, TABLE1, ""));
							table.setLockCellRow(i, true);
							table.setLockCell(i, 0, false);// 连接主
							table.setLockCell(i, 1, false);// 组号
							table.setLockCell(i, 2, false);// 医嘱
							table.setLockCell(i, 3, false);// 用量
							table.setLockCell(i, 5, false);// 用法
							//===================add by wukai on 20160527 start
							table.setLockCell(i, 6, false);// 泵入方式
							table.setLockCell(i, 7, false);// 输液速率
							//===================add by wukai on 20160527 end
							table.setLockCell(i, 8, false);// 频次
							table.setLockCell(i, 9, false);// 执行科室
							table.setLockCell(i, 10, false);// 启用时间
							table.setLockCell(i, 11, false);// 医嘱备注
							table.setLockCell(i, 13, false);// 抗菌标识
							table.setLockCell(i, 14, false);// 续
							table.setLockCell(i, 15, false);// 急
							table.setLockCell(i, 16, false);// 备
							table.setLockCell(i, 17, false);// 给
							odiObject.setItem(ds, i, linkBlankP);
							odiObject.setItem(ds, i, linkMainP);
							table.setDSValue(i);
						}
					}
				}
			}
		}
		// 续用
		if ("CONTINUOUS_FLG".equals(columnName)) {
			if ("Y".equals(linkParm.getValue("CONTINUOUS_FLG"))) {
				if (isCheckOrderContinuousFlg(linkParm, 0)) {
					// 不可续用
					this.messageBox("E0154");
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkFlg = new TParm();
					linkFlg.setData("CONTINUOUS_FLG", "N");
					odiObject.setItem(ds, row, linkFlg);
					table.setDSValue(row);
					return;
				} else {
					TDS ds = odiObject.getDS("ODI_ORDER");
					linkParm.setData("CONTINUOUS_FLG", "Y");
					linkParm.setData("#NEW#", true);
					TParm orderParmCon = this.getTempStartQty(linkParm);
					odiObject.setItem(ds, row, orderParmCon);
					table.setDSValue(row);
				}
			} else {
				TDS ds = odiObject.getDS("ODI_ORDER");
				linkParm.setData("CONTINUOUS_FLG", "N");
				linkParm.setData("#NEW#", true);
				TParm orderParmCon = this.getTempStartQty(linkParm);
				odiObject.setItem(ds, row, orderParmCon);
				table.setDSValue(row);
			}
		}
	}

	/**
	 * 长期医嘱CHECK_BOX监听事件
	 * 
	 * @param obj
	 *            Object
	 */
	public void onCheckBoxValueUD(Object obj) {
		//TODO CHECK_BOX监听事件
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable(TABLE2).getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		TParm linkParm = table.getDataStore().getRowParm(row);
		// System.out.println("000000000000000000linkParm linkParm is :::"+linkParm);
		if ("LINKMAIN_FLG".equals(columnName)) {
			if ("Y".equals(linkParm.getValue("LINKMAIN_FLG"))) {
				if (linkParm.getValue("ORDER_CODE").length() == 0) {
					// 请开立医嘱
					this.messageBox("E0152");
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkFlg = new TParm();
					linkFlg.setData("LINKMAIN_FLG", "N");
					odiObject.setItem(ds, row, linkFlg);
					table.setDSValue(row);
					return;
				}
				// 查询最大连结号
				int maxLinkNo = getMaxLinkNo("UD");

				TDS ds = odiObject.getDS("ODI_ORDER");
				// 设置最大连结号
				TParm linkP = new TParm();
				linkP.setData("LINK_NO", maxLinkNo);
				// $$========add by lx 2012-06-13 start 连嘱自动一致==========$$//
				linkP.setData("FREQ_CODE", linkParm.getValue("FREQ_CODE"));
				linkP.setData("ROUTE_CODE", linkParm.getValue("ROUTE_CODE"));
				linkP.setData("EFF_DATE", linkParm.getTimestamp("EFF_DATE"));
				//====add by chenhj 2017.4.10 start
				linkP.setData("INFLUTION_RATE", linkParm.getValue("INFLUTION_RATE"));
				//====add by chenhj 2017.4.10 
				// ======yanjing 20140213 抗菌药物连嘱自动为停用时间、抗菌标识和停用医生赋值
				if (!"".equals(linkParm.getValue("ANTIBIOTIC_WAY"))
						&& !linkParm.getValue("ANTIBIOTIC_WAY").equals(null)) {
					linkP.setData("DC_DATE", linkParm.getTimestamp("DC_DATE"));
					linkP.setData("ANTIBIOTIC_WAY", linkParm
							.getValue("ANTIBIOTIC_WAY"));
					linkP
					.setData("DC_DR_CODE", linkParm
							.getValue("DC_DR_CODE"));
					linkP.setData("DC_DEPT_CODE", linkParm
							.getValue("DC_DEPT_CODE"));// =====yanjing 20140901
					// 停用科室
				}

				// $$========add by lx 2012-06-13 end==========$$//
				// 找到本医嘱的默认设置
				// $$==============Del by lx 2012/02/23 Start
				// 连时不取记录================$$//
				/**
				 * TParm actionParm = this.getPhaBaseData(
				 * linkParm.getData("ORDER_CODE").toString(), linkParm
				 * .getData("CAT1_TYPE").toString(), "UD", linkParm);
				 * linkP.setData("FREQ_CODE", actionParm.getValue("FREQ_CODE"));
				 * linkP.setData("ROUTE_CODE",
				 * actionParm.getValue("ROUTE_CODE"));
				 **/
				// $$==============Del by lx 2012/02/23 end================$$//
				odiObject.setItem(ds, row, linkP);
				table.setDSValue(row);
				// $$==========add by lx 2012/02/23 选中连
				// 出START===============$$//
				// this.messageBox("table.getDataStore().rowCount()"+table.getDataStore().rowCount());
				for (int i = row; i < ds.rowCount(); i++) {
					if (i > row) {
						linkP.setData("LINKMAIN_FLG", "N");
					}
					if (!ds.getItemString(i, "ORDER_CODE").equals("")
							&& (ds.getItemString(i, "SETMAIN_FLG").equals("Y") || !ds
									.getItemString(i, "CAT1_TYPE")
									.equals("PHA")))
						continue;
					linkP
					.setData("EXEC_DEPT_CODE", this.getExeDeptCodeUD(ds
							.getItemString(row, "ORDER_CODE"), row,
							TABLE1, ""));
					odiObject.setItem(ds, i, linkP);
					this.getTTable(TABLE2).setLockCellRow(i, true);
					this.getTTable(TABLE2).setLockCell(i, 0, false);// 连接主
					this.getTTable(TABLE2).setLockCell(i, 1, false);// 组号
					this.getTTable(TABLE2).setLockCell(i, 2, false);// 医嘱
					this.getTTable(TABLE2).setLockCell(i, 3, false);// 用量
					//&&========================add by wukai 20160525 start
					this.getTTable(TABLE2).setLockCell(i, 7, false);//泵入方式
					this.getTTable(TABLE2).setLockCell(i, 8, false);//输液速率
					//&&========================add by wukai 20160525 start
					if (ds.getItemString(i, "LINK_NO").equals("")
							|| ds.getItemString(i, "LINKMAIN_FLG").equals("Y")) {
						this.getTTable(TABLE2).setLockCell(i, 5, false);// 频次
						this.getTTable(TABLE2).setLockCell(i, 6, false);// 用法
						this.getTTable(TABLE2).setLockCell(i, 12, false);// 执行科室
						this.getTTable(TABLE2).setLockCell(i, 13, false);// 启用时间
						this.getTTable(TABLE2).setLockCell(i, 14, false);// 停用时间

					}
					this.getTTable(TABLE2).setLockCell(i, 9, false);// 急
					this.getTTable(TABLE2).setLockCell(i, 10, false);// 备
					this.getTTable(TABLE2).setLockCell(i, 11, false);// 医嘱备注
					this.getTTable(TABLE2).setLockCell(i, 13, false);// 停用时间
					this.getTTable(TABLE2).setLockCell(i, 16, false);// 抗菌
					table.setDSValue(i);
				}
				// $$==========add by lx 2012/02/23 选中连 出END===============$$//

			} else {
				TDS ds = odiObject.getDS("ODI_ORDER");
				// 设置最大连结号
				TParm linkP = new TParm();
				linkP.setData("LINK_NO", "");
				// 找到本医嘱的默认设置
				TParm actionParm = this.getPhaBaseData(linkParm.getData(
						"ORDER_CODE").toString(), linkParm.getData("CAT1_TYPE")
						.toString(), "UD", linkParm);
				linkP.setData("FREQ_CODE", actionParm.getValue("FREQ_CODE"));
				linkP.setData("ROUTE_CODE", actionParm.getValue("ROUTE_CODE"));
				// odiObject.setItem(ds, row, linkP);
				// table.setDSValue(row);
				// $$==========add by lx 2012/02/23 选中连
				// 出START===============$$//
				// TParm linkBlankP = new TParm();
				// linkBlankP.setData("LINK_NO", "");
				TParm linkMainP = new TParm();
				linkMainP.setData("LINKMAIN_FLG", "N");

				// this.messageBox("LINK_NO===="+ds.getItemInt(row, "LINK_NO"));
				if (ds.getItemInt(row, "LINK_NO") > 0) {
					int linktemp = ds.getItemInt(row, "LINK_NO");
					// this.messageBox("LINK_NO===="+ds.getItemInt(row,
					// "LINK_NO"));

					for (int i = 0; i < ds.rowCount(); i++) {
						TParm linkBlankP = new TParm();
						linkBlankP.setData("LINK_NO", "");
						if (linktemp == ds.getItemInt(i, "LINK_NO")) {
							// this.messageBox("i"+i)
							if (i > row)

								linkBlankP.setData("EXEC_DEPT_CODE", this
										.getExeDeptCodeUD(ds.getItemString(i,
												"ORDER_CODE"), i, TABLE2, ""));
							// 取消连嘱时清除部分字段的值 yanjing 20140217
							this.onClearLinkAnti(ds.getItemString(i,
									"ORDER_CODE"), linkBlankP);
							this.getTTable(TABLE2).setLockCellRow(i, true);
							this.getTTable(TABLE2).setLockCell(i, 0, false);// 连接主
							this.getTTable(TABLE2).setLockCell(i, 1, false);// 组号
							this.getTTable(TABLE2).setLockCell(i, 2, false);// 医嘱
							this.getTTable(TABLE2).setLockCell(i, 3, false);// 用量
							this.getTTable(TABLE2).setLockCell(i, 5, false);// 频次
							this.getTTable(TABLE2).setLockCell(i, 6, false);// 用法
							//&&========================add by wukai 20160525 start
							this.getTTable(TABLE2).setLockCell(i, 7, false);//泵入方式
							this.getTTable(TABLE2).setLockCell(i, 8, false);//输液速率
							//&&========================add by wukai 20160525 start
							this.getTTable(TABLE2).setLockCell(i, 9, false);// 急
							this.getTTable(TABLE2).setLockCell(i, 10, false);// 备
							this.getTTable(TABLE2).setLockCell(i, 11, false);// 医嘱备注
							this.getTTable(TABLE2).setLockCell(i, 12, false);// 执行科室
							this.getTTable(TABLE2).setLockCell(i, 13, false);// 启用时间
							this.getTTable(TABLE2).setLockCell(i, 14, false);// 停用时间
							this.getTTable(TABLE2).setLockCell(i, 16, false);// 抗菌
							odiObject.setItem(ds, i, linkBlankP);
							odiObject.setItem(ds, i, linkMainP);
							table.setDSValue(i);
						}
					}
				}
				// $$==========add by lx 2012/02/23 选中连 出END===============$$//
			}
		}
	}

	/**
	 * 出院带药
	 */
	public void onCheckBoxValueDS(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable(TABLE3).getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		TParm linkParm = table.getDataStore().getRowParm(row);
		// 连结医嘱
		//		if ("LINKMAIN_FLG".equals(columnName)) {
		//			if ("Y".equals(linkParm.getValue("LINKMAIN_FLG"))) {
		//				if (linkParm.getValue("ORDER_CODE").length() == 0) {
		//					// 请开立医嘱
		//					this.messageBox("E0152");
		//					TDS ds = odiObject.getDS("ODI_ORDER");
		//					TParm linkFlg = new TParm();
		//					linkFlg.setData("LINKMAIN_FLG", "N");
		//					odiObject.setItem(ds, row, linkFlg);
		//					table.setDSValue(row);
		//					return;
		//				}
		//				// 查询最大连结号
		//				int maxLinkNo = getMaxLinkNo("DS");
		//				TDS ds = odiObject.getDS("ODI_ORDER");
		//				// 设置最大连结号
		//				TParm linkP = new TParm();
		//				linkP.setData("LINK_NO", maxLinkNo);
		//				// 找到本医嘱的默认设置
		//				TParm actionParm = this.getPhaBaseData(linkParm.getData(
		//						"ORDER_CODE").toString(), linkParm.getData("CAT1_TYPE")
		//						.toString(), "DS", linkParm);
		//				linkP.setData("FREQ_CODE", actionParm.getValue("FREQ_CODE"));
		//				linkP.setData("ROUTE_CODE", actionParm.getValue("ROUTE_CODE"));
		//				odiObject.setItem(ds, row, linkP);
		//				table.setDSValue(row);
		//			} else {
		//				TDS ds = odiObject.getDS("ODI_ORDER");
		//				// 设置最大连结号
		//				TParm linkP = new TParm();
		//				linkP.setData("LINK_NO", "");
		//				// 找到本医嘱的默认设置
		//				TParm actionParm = this.getPhaBaseData(linkParm.getData(
		//						"ORDER_CODE").toString(), linkParm.getData("CAT1_TYPE")
		//						.toString(), "DS", linkParm);
		//				linkP.setData("FREQ_CODE", actionParm.getValue("FREQ_CODE"));
		//				linkP.setData("ROUTE_CODE", actionParm.getValue("ROUTE_CODE"));
		//				odiObject.setItem(ds, row, linkP);
		//				table.setDSValue(row);
		//			}
		//	}
		//========== add by chenhj start 2017.4.10
		if ("LINKMAIN_FLG".equals(columnName)) {
			if ("Y".equals(linkParm.getValue("LINKMAIN_FLG"))) {
				if (linkParm.getValue("ORDER_CODE").length() == 0) {
					// 请开立医嘱
					this.messageBox("E0152");
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkFlg = new TParm();
					linkFlg.setData("LINKMAIN_FLG", "N");
					odiObject.setItem(ds, row, linkFlg);
					table.setDSValue(row);
					return;
				}
				// 查询最大连结号
				int maxLinkNo = getMaxLinkNo("DS");
				TDS ds = odiObject.getDS("ODI_ORDER");
				// 设置最大连结号
				TParm linkP = new TParm();
				linkP.setData("LINK_NO", maxLinkNo);
				// $$========add by lx 2012-06-13 start 连嘱自动一致==========$$//
				linkP.setData("FREQ_CODE", linkParm.getValue("FREQ_CODE"));
				linkP.setData("ROUTE_CODE", linkParm.getValue("ROUTE_CODE"));
				linkP.setData("EFF_DATE", linkParm.getTimestamp("EFF_DATE"));
				//====add by chenhj 2017.4.10 start
				linkP.setData("INFLUTION_RATE", linkParm.getValue("INFLUTION_RATE"));
				//====add by chenhj 2017.4.10 
				if (!"".equals(linkParm.getValue("ANTIBIOTIC_WAY"))
						&& !linkParm.getValue("ANTIBIOTIC_WAY").equals(null)) {
					linkP.setData("DC_DATE", linkParm.getTimestamp("DC_DATE"));
					linkP.setData("ANTIBIOTIC_WAY", linkParm
							.getValue("ANTIBIOTIC_WAY"));
					linkP
					.setData("DC_DR_CODE", linkParm
							.getValue("DC_DR_CODE"));
					linkP.setData("DC_DEPT_CODE", linkParm
							.getValue("DC_DEPT_CODE"));
					// 停用科室
				}
				odiObject.setItem(ds, row, linkP);
				table.setDSValue(row);
				for (int i = row; i < ds.rowCount(); i++) {
					if (i > row) {
						linkP.setData("LINKMAIN_FLG", "N");
					}
					if (!ds.getItemString(i, "ORDER_CODE").equals("")
							&& (ds.getItemString(i, "SETMAIN_FLG").equals("Y") || !ds
									.getItemString(i, "CAT1_TYPE")
									.equals("PHA")))
						continue;
					linkP
					.setData("EXEC_DEPT_CODE", this.getExeDeptCodeUD(ds
							.getItemString(row, "ORDER_CODE"), row,
							TABLE1, ""));
					odiObject.setItem(ds, i, linkP);
					this.getTTable(TABLE2).setLockCellRow(i, true);
					this.getTTable(TABLE2).setLockCell(i, 0, false);// 连接主
					this.getTTable(TABLE2).setLockCell(i, 1, false);// 组号
					this.getTTable(TABLE2).setLockCell(i, 2, false);// 医嘱
					this.getTTable(TABLE2).setLockCell(i, 3, false);// 用量
					this.getTTable(TABLE2).setLockCell(i, 7, false);//泵入方式
					this.getTTable(TABLE2).setLockCell(i, 8, false);//输液速率
					if (ds.getItemString(i, "LINK_NO").equals("")
							|| ds.getItemString(i, "LINKMAIN_FLG").equals("Y")) {
						this.getTTable(TABLE2).setLockCell(i, 5, false);// 频次
						this.getTTable(TABLE2).setLockCell(i, 6, false);// 用法
						this.getTTable(TABLE2).setLockCell(i, 12, false);// 执行科室
						this.getTTable(TABLE2).setLockCell(i, 13, false);// 启用时间
						this.getTTable(TABLE2).setLockCell(i, 14, false);// 停用时间

					}
					this.getTTable(TABLE2).setLockCell(i, 9, false);// 急
					this.getTTable(TABLE2).setLockCell(i, 10, false);// 备
					this.getTTable(TABLE2).setLockCell(i, 11, false);// 医嘱备注
					this.getTTable(TABLE2).setLockCell(i, 13, false);// 停用时间
					this.getTTable(TABLE2).setLockCell(i, 16, false);// 抗菌
					table.setDSValue(i);
				}
			} else {
				TDS ds = odiObject.getDS("ODI_ORDER");
				// 设置最大连结号
				TParm linkP = new TParm();
				linkP.setData("LINK_NO", "");
				// 找到本医嘱的默认设置
				TParm actionParm = this.getPhaBaseData(linkParm.getData(
						"ORDER_CODE").toString(), linkParm.getData("CAT1_TYPE")
						.toString(), "UD", linkParm);
				linkP.setData("FREQ_CODE", actionParm.getValue("FREQ_CODE"));
				linkP.setData("ROUTE_CODE", actionParm.getValue("ROUTE_CODE"));
				TParm linkMainP = new TParm();
				linkMainP.setData("LINKMAIN_FLG", "N");

				if (ds.getItemInt(row, "LINK_NO") > 0) {
					int linktemp = ds.getItemInt(row, "LINK_NO");
					for (int i = 0; i < ds.rowCount(); i++) {
						TParm linkBlankP = new TParm();
						linkBlankP.setData("LINK_NO", "");
						if (linktemp == ds.getItemInt(i, "LINK_NO")) {
							if (i > row)
								linkBlankP.setData("EXEC_DEPT_CODE", this
										.getExeDeptCodeUD(ds.getItemString(i,
												"ORDER_CODE"), i, TABLE2, ""));
							this.onClearLinkAnti(ds.getItemString(i,
									"ORDER_CODE"), linkBlankP);
							this.getTTable(TABLE2).setLockCellRow(i, true);
							this.getTTable(TABLE2).setLockCell(i, 0, false);// 连接主
							this.getTTable(TABLE2).setLockCell(i, 1, false);// 组号
							this.getTTable(TABLE2).setLockCell(i, 2, false);// 医嘱
							this.getTTable(TABLE2).setLockCell(i, 3, false);// 用量
							this.getTTable(TABLE2).setLockCell(i, 5, false);// 频次
							this.getTTable(TABLE2).setLockCell(i, 6, false);// 用法
							this.getTTable(TABLE2).setLockCell(i, 7, false);//泵入方式
							this.getTTable(TABLE2).setLockCell(i, 8, false);//输液速率
							this.getTTable(TABLE2).setLockCell(i, 9, false);// 急
							this.getTTable(TABLE2).setLockCell(i, 10, false);// 备
							this.getTTable(TABLE2).setLockCell(i, 11, false);// 医嘱备注
							this.getTTable(TABLE2).setLockCell(i, 12, false);// 执行科室
							this.getTTable(TABLE2).setLockCell(i, 13, false);// 启用时间
							this.getTTable(TABLE2).setLockCell(i, 14, false);// 停用时间
							this.getTTable(TABLE2).setLockCell(i, 16, false);// 抗菌
							odiObject.setItem(ds, i, linkBlankP);
							odiObject.setItem(ds, i, linkMainP);
							table.setDSValue(i);
						}
					}
				}
			}
		}

		//========== add by chenhj end 2017.4.10

		// 盒药注记
		if ("GIVEBOX_FLG".equals(columnName)) {
			if (linkParm.getValue("ORDER_CODE").length() == 0) {
				// 请开立医嘱
				this.messageBox("E0152");
				TDS ds = odiObject.getDS("ODI_ORDER");
				TParm linkFlg = new TParm();
				linkFlg.setData("GIVEBOX_FLG", "N");
				odiObject.setItem(ds, row, linkFlg);
				table.setDSValue(row);
				return;
			}
			TDS ds = odiObject.getDS("ODI_ORDER");
			odiObject.setItem(ds, row, getOutHospStartQty(linkParm));
			table.setDSValue(row);
		}
	}

	/**
	 * 拿到最大连结号
	 * 
	 * @param table
	 *            TTable
	 */
	public int getMaxLinkNo(String type) {
		// System.out.println("------------getMaxLinkNo--------------");
		System.out.println("------" + type);
		if (type.equalsIgnoreCase("ST")) {
			// 先从本地对象中取数据;
			TDS ds = odiObject.getDS("ODI_ORDER");
			String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			TParm parm = ds.getBuffer(buff);
			// System.out.println("===SQL==="+ds.getSQL());
			int result = 0;
			for (int i = 0; i < parm.getCount(); i++) {
				// System.out.println("=============getMaxLinkNo parm============="+parm);
				if (!parm.getBoolean("#ACTIVE#", i))
					continue;
				if (!(type.equals(parm.getValue("RX_KIND", i))))
					continue;
				if (parm.getRow(i).getInt("LINK_NO") > result)
					result = parm.getRow(i).getInt("LINK_NO");
			}
			// 存在则从对象中取数据;
			int dbresult = getMaxLinkSTNo();
			// 存在则从对象中取数据;
			if ((result + 1) > dbresult) {
				// System.out.println("----存在对象中取数据-----");
				return result + 1;
			} else {
				return dbresult;
			}
			// 20151109 WANGJC ADD START
		} else if (type.equalsIgnoreCase("OP")) {
			// 先从本地对象中取数据;
			TDS ds = odiObject.getDS("ODI_ORDER");
			String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			TParm parm = ds.getBuffer(buff);
			// System.out.println("===SQL==="+ds.getSQL());
			int result = 0;
			for (int i = 0; i < parm.getCount(); i++) {
				// System.out.println("=============getMaxLinkNo parm============="+parm);
				if (!parm.getBoolean("#ACTIVE#", i))
					continue;
				if (!(type.equals(parm.getValue("RX_KIND", i))))
					continue;
				if (parm.getRow(i).getInt("LINK_NO") > result)
					result = parm.getRow(i).getInt("LINK_NO");
			}
			// 存在则从对象中取数据;
			int dbresult = getMaxLinkOPNo();
			// 存在则从对象中取数据;
			if ((result + 1) > dbresult) {
				// System.out.println("----存在对象中取数据-----");
				return result + 1;
			} else {
				return dbresult;
			}
			// 20151109 WANGJC ADD END
		} else {
			// this.messageBox("===getMaxLinkNo222222===");
			TDS ds = odiObject.getDS("ODI_ORDER");
			String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			TParm parm = ds.getBuffer(buff);
			// System.out.println("===SQL==="+ds.getSQL());
			int result = 0;
			for (int i = 0; i < parm.getCount(); i++) {
				// System.out.println("=============getMaxLinkNo parm============="+parm);
				if (!parm.getBoolean("#ACTIVE#", i))
					continue;
				if (!(type.equals(parm.getValue("RX_KIND", i))))
					continue;
				if (parm.getRow(i).getInt("LINK_NO") > result)
					result = parm.getRow(i).getInt("LINK_NO");
			}
			// this.messageBox("返回值:"+result);
			return result == 0 ? 1 : result + 1;
		}
	}

	/**
	 * 拿到最大集合医嘱组号
	 * 
	 * @param table
	 *            TTable
	 */
	public int getMaxOrderGroupNo() {
		// 先从本地对象中取数据;
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		TParm parm = ds.getBuffer(buff);
		// System.out.println("===SQL==="+ds.getSQL());
		int result = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			// System.out.println("=============getMaxLinkNo parm============="+parm);
			if (!parm.getBoolean("#ACTIVE#", i))
				continue;
			if (parm.getRow(i).getInt("ORDERSET_GROUP_NO") > result)
				result = parm.getRow(i).getInt("ORDERSET_GROUP_NO");
		}
		int dbresult = getMaxOrderGroupSTNo();
		// 存在则从对象中取数据;
		if ((result + 1) > dbresult) {
			// System.out.println("----存在对象中取数据-----");
			return result + 1;
		} else {
			return dbresult;
		}
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	public int getMaxLinkSTNo() {

		// this.getCaseNo()
		TParm parm = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT MAX(TO_NUMBER(LINK_NO)) MAX_NO FROM ODI_ORDER  WHERE RX_KIND='ST' AND  CASE_NO='"
								+ this.getCaseNo() + "'"));
		// System.out.println("---getMaxLinkSTNo---" + parm.getInt("MAX_NO",
		// 0));
		if (parm.getCount() <= 0) {
			return 1;
		}
		return parm.getInt("MAX_NO", 0) + 1;
	}

	/**
	 * 
	 * @param type
	 * @return 20151109 WANGJC ADD
	 */
	public int getMaxLinkOPNo() {

		// this.getCaseNo()
		TParm parm = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT MAX(TO_NUMBER(LINK_NO)) MAX_NO FROM ODI_ORDER  WHERE RX_KIND='OP' AND  CASE_NO='"
								+ this.getCaseNo() + "'"));
		// System.out.println("---getMaxLinkSTNo---" + parm.getInt("MAX_NO",
		// 0));
		if (parm.getCount() <= 0) {
			return 1;
		}
		return parm.getInt("MAX_NO", 0) + 1;
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	public int getMaxOrderGroupSTNo() {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT MAX(ORDERSET_GROUP_NO) MAX_NO FROM ODI_ORDER  WHERE  CASE_NO='"
						+ this.getCaseNo() + "'"));
		if (parm.getCount() <= 0) {
			return 1;
		}
		return parm.getInt("MAX_NO", 0) + 1;
	}

	/**
	 * 检查是否有主项
	 * 
	 * @param linkNo
	 *            int
	 * @param linkMainFlg
	 *            boolean
	 * @return boolean
	 */
	public boolean checkMainLinkItem(int linkNo, boolean linkMainFlg,
			String type) {
		boolean falg = false;
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		TParm parm = ds.getBuffer(buff);
		int mainLinkCount = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			if (!parm.getBoolean("#ACTIVE#", i))
				continue;
			if (!(type.equals(parm.getValue("RX_KIND", i))))
				continue;
			if (linkMainFlg) {
				if (parm.getRow(i).getBoolean("LINKMAIN_FLG")
						&& parm.getRow(i).getInt("LINK_NO") == linkNo)
					mainLinkCount++;
			} else {
				if (parm.getRow(i).getBoolean("LINKMAIN_FLG")
						&& parm.getRow(i).getInt("LINK_NO") == linkNo) {
					falg = true;
				}
			}
		}
		// this.messageBox_("linkMainFlg"+mainLinkCount);
		if (linkMainFlg)
			if (mainLinkCount > 0)
				falg = true;
		return falg;
	}

	/**
	 * 拿到主项数据
	 * 
	 * @param linkNo
	 *            int
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm getMainLinkOrder(int linkNo, String type) {
		TParm result = new TParm();
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		TParm parm = ds.getBuffer(buff);
		for (int i = 0; i < parm.getCount(); i++) {
			if (!parm.getBoolean("#ACTIVE#", i))
				continue;
			if (!(type.equals(parm.getValue("RX_KIND", i))))
				continue;
			if (parm.getRow(i).getBoolean("LINKMAIN_FLG")
					&& parm.getRow(i).getInt("LINK_NO") == linkNo) {
				result = parm.getRow(i);
			}
		}
		return result;
	}

	/**
	 * 初始化DataStore
	 */
	public void initDataStoreToTable() {
		// add by lx 因临时医嘱数据量较大，加入时间段
		String startDate = StringTool.getString((Timestamp) this
				.getValue("START_DATEST"), "yyyy-MM-dd HH:mm:ss");
		String endDate = StringTool.getString((Timestamp) this
				.getValue("END_DATEST"), "yyyy-MM-dd HH:mm:ss");
		// System.out.println("---initDataStoreToTable startDate---"+startDate);
		// System.out.println("---initDataStoreToTable endDate---"+endDate);

		// 绑定医嘱
		TParm action = new TParm();
		// 设置查询列值CASE_NO
		action.setData("CASE_NO", getCaseNo());
		// 设置列行数
		action.setData("ACTION", "COUNT", 1);
		// =============pangben modify 20110512 start 添加参数
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			action.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop
		// 设置SQL
		odiObject
		.setSQL(
				"ODI_ORDER",
				odiSqlObject.creatSQL("ODI_ORDER", action, startDate,
						endDate, isOpeFlg()),// wanglong modify 20150106
				"CASE_NO",
				"OPT_USER;OPT_DATE:SAVE_TIME;OPT_TERM;ORDER_DATE:SAVE_TIME;ORDER_NO;ORDER_DR_CODE",
				"OPT_USER;OPT_DATE:SAVE_TIME;OPT_TERM");
		// 设置参数列表
		TParm actionParm = new TParm();
		actionParm.setData("CASE_NO", getCaseNo());
		// ORDERWHEREDATA条件参数
		odiObject.setAttribute("ORDERWHEREDATA", actionParm);
		// DSPNMWHEREDATA条件参数
		odiObject.setAttribute("DSPNMWHEREDATA", actionParm);
		// DSPNDWHEREDATA条件参数
		odiObject.setAttribute("DSPNDWHEREDATA", actionParm);
		// 设置为医生站进入
		odiObject.setOdiFlg(true);
		// 执行初始化DataStore
		odiObject.retrieve();
		// System.out.println("=========11111111111111==========="+new Date());
		// Debug
		// odiObject.getDS("ODI_ORDER").showDebug();
		TDS tds = odiObject.getDS("ODI_ORDER");
		// shibl 20120808 add 按照医嘱启用时间排序
		tds.setSort("EFF_DATE ASC,LINKMAIN_FLG DESC");
		tds.sort();
		// System.out.println("联动查询结果2222222222222:======="+new Date());
		// tds.showDebug();
		// 和TABLE1临时医嘱绑定
		this.getTTable(TABLE1).setDataStore(tds);
		this.getTTable(TABLE2).setDataStore(tds);
		this.getTTable(TABLE3).setDataStore(tds);
		this.getTTable(TABLE6).setDataStore(tds);
		// this.getTTable(TABLE4).setDataStore(tds);
		// 过敏记录DATASTORE
		odiDrugArrergy = new OdiDrugAllergy();
		// 设置过敏记录中选择哪个就诊号
		odiDrugArrergy.setCaseNo(this.getCaseNo());
		// 设置过敏记录中选择哪个病案号
		odiDrugArrergy.setMrNo(this.getMrNo());
		// 初始化过敏记录
		odiDrugArrergy.onQuery();
		this.getTTable(GMTABLE).setDataStore(odiDrugArrergy);
		selPhaClass(); // add by huangtt 20150506
		// System.out.println("过敏记录");
		// this.getTTable(GMTABLE).getDataStore().showDebug();
		// 设置CASE_NO
		odiObject.setAttribute("CASE_NO", this.getCaseNo());
		// 设置病患姓名
		odiObject.setAttribute("PAT_NAME", this.getPatName());
		// 设置姓名1
		odiObject.setAttribute("PAT_NAME1", this.getPatName1());
		// 设置生日
		odiObject.setAttribute("BIRTH_DATE", this.getBirthDay());
		// 设置性别
		odiObject.setAttribute("SEX_CODE", this.getSexCode());
		// 设置邮编
		odiObject.setAttribute("POST_CODE", this.getPostCode());
		// 设置通讯地址
		odiObject.setAttribute("ADDRESS", this.getAddress());
		// 设置单位
		odiObject.setAttribute("COMPANY_DESC", this.getCompanyDesc());
		// 设置电话
		odiObject.setAttribute("TEL", this.getTel());
		// 设置身份证号
		odiObject.setAttribute("IDNO", this.getIdNo());
		// 主诊断
		odiObject.setAttribute("MAINDIAG", this.getMainDiag());
		// 身份
		odiObject.setAttribute("CTZ_CODE", this.getCtzCode());
		// 设置术中用药表格数据
		this.setOdiOpTableData(getTTable("TABLE7"));
		// 设置用血记录表格数据
		this.setOdiBlTableData(getTTable("TABLE8"));
		//add by kangy 20170602
		TParm tdsParm=new TParm();
		if(BYXflg){
		for(int i=0;i<tds.rowCount()-1;i++){
			tdsParm.setRowData(i, tds.getRowParm(i));
		}
		tdsParm.setCount(tds.rowCount()-1);
		if(checkAntibioticsTDSParm(tdsParm)){
		if(!checkEtiolgexmParm(tdsParm)){//add by kangy 20170527
			messageBox("没有做病原学检查");
		} 
		}
		}
		onChange();
	}

	/**
	 * 是否有要保存的数据
	 * 
	 * @param buff
	 *            String
	 * @return boolean
	 */
	public boolean onSaveFlg(int index) {
		boolean falg = false;
		if (index == 5) {
			// 过敏记录是否有要保存的数据
			falg = isSaveGM();
			return falg;
		} else {
			if (index == 4) {
				TDS ds = odiObject.getDS("ODI_ORDER");
				int count = 0;
				int newRow[] = ds.getNewRows();
				for (int i : newRow) {
					if (!ds.isActive(i))
						continue;
					count++;
				}
				int modifRow[] = ds.getOnlyModifiedRows(this.getTTable(
						"TABLE" + index).getDataStore().PRIMARY);
				int delRowCount = ds.getDeleteCount() < 0 ? 0 : this.getTTable(
						"TABLE" + index).getDataStore().getDeleteCount();
				if (modifRow.length + count + delRowCount > 0)
					falg = true;
				return falg;
			}
			// 要保存数据行
			int count = 0;
			int newRow[] = this.getTTable("TABLE" + index).getDataStore()
					.getNewRows();
			for (int i : newRow) {
				if (!this.getTTable("TABLE" + index).getDataStore().isActive(i))
					continue;
				count++;
			}
			int modifRow[] = this
					.getTTable("TABLE" + index)
					.getDataStore()
					.getOnlyModifiedRows(
							this.getTTable("TABLE" + index).getDataStore().PRIMARY);
			int delRowCount = this.getTTable("TABLE" + index).getDataStore()
					.getDeleteCount() < 0 ? 0 : this.getTTable("TABLE" + index)
							.getDataStore().getDeleteCount();
			if (modifRow.length + count + delRowCount > 0)
				falg = true;
			return falg;
		}
	}

	/**
	 * 拿到TTabbedPane
	 * 
	 * @param tag
	 *            String
	 * @return TTabbedPane
	 */
	public TTabbedPane getTTabbedPane(String tag) {
		return (TTabbedPane) this.getComponent(tag);
	}

	/**
	 * 页签改变事件
	 */
	public void onChangeStart() {
		int chageTab = 0;
		// 状态条显示
		callFunction("UI|setSysStatus", "");
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		TParm pubParm = null;
		TTable table = null;
		// 移除子UIMenuBar
		callFunction("UI|removeChildMenuBar");
		// 移除子UIToolBar
		callFunction("UI|removeChildToolBar");
		// 显示UIshowTopMenu
		callFunction("UI|showTopMenu");
		if (indexPage <= 3 || indexPage == 4 || indexPage == 5) {
			// 转换窗口开关
			if (this.chagePage && onSaveFlg(indexPage + 1)) {
				if (indexPage == 4) {
					if (messageBox("提示信息 Tips", "过敏记录是否需要保存? \n Are you Save?",
							this.YES_NO_OPTION) != 0) {
						this.chagePage = false;
						tab.setSelectedIndex(indexPage);
						this.chagePage = true;
						return;
					}
				}
				if (messageBox("提示信息", "医嘱是否需要保存? \n Are you Save?",
						this.YES_NO_OPTION) != 0) {
					chageTab = tab.getSelectedIndex();
					this.chagePage = false;
					tab.setSelectedIndex(indexPage);
					this.chagePage = true;
					return;
				} else {
					tabSaveFlg = true;// 该变量传入onSave方法，标记为切换页签时的保存
					this.onSave();
					tabSaveFlg = false;
					// 页签切换成上一个页签
					chageTab = tab.getSelectedIndex();
					this.chagePage = false;
					tab.setSelectedIndex(indexPage);
					this.chagePage = true;
				}
			}
		}
		TDS ds = odiObject.getDS("ODI_ORDER");
		switch (tab.getSelectedIndex()) {
		case 0:

			// 临时
			table = getTTable(TABLE1);
			// table.setFilter("(RX_KIND='ST' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='ST' AND #ACTIVE#='N'");
			// ===add by lx 2012-07-04====//
			String f1 = "(RX_KIND='ST' AND HIDE_FLG='N' ";
			String f2 = ".0' OR (RX_KIND='ST' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='ST' AND #ACTIVE#='N'";
			if (opeFlg) {
				f1 = "(RX_KIND='OP' AND HIDE_FLG='N' ";
				f2 = ".0' OR (RX_KIND='OP' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='OP' AND #ACTIVE#='N'";
			}
			table.setFilter(f1
					+ getQueryOrderCat1Type()
					+ " "
					+ getPhaOrderUtil()
					+ " AND ORDER_DATE >= '"
					+ StringTool.getString((Timestamp) this
							.getValue("START_DATEST"), "yyyy-MM-dd HH:mm:ss")
					+ ".0' AND ORDER_DATE < '"
					+ StringTool.getString((Timestamp) this
							.getValue("END_DATEST"), "yyyy-MM-dd HH:mm:ss")
					+ f2);
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 1:
			// 长期
			table = getTTable(TABLE2);
			// ==========pangben 2013-9-10 修改显示停用时间
			table
			.setFilter("(RX_KIND='UD' AND HIDE_FLG = 'N' AND ( DC_DATE='' OR DC_DATE>'"
					+ StringTool.getString(SystemTool.getInstance()
							.getDate(), "yyyy-MM-dd HH:mm:ss")
					+ ".0')) OR RX_KIND='UD' AND #ACTIVE#='N'");

			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 2:
			// 出院带药
			table = getTTable(TABLE3);
			table.setFilter("(RX_KIND='DS' AND HIDE_FLG = 'N' "
					+ getRxNoString()
					+ " AND DC_DATE='') OR RX_KIND='DS' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			// 设置处方号
			onNewRxNo("2");
			break;
		case 3:
			// 中药饮片
			table = getTTable(TABLE4);
			ds.setFilter("(RX_KIND='IG' AND HIDE_FLG = 'N' "
					+ getRxNoIGString()
					+ " AND DC_DATE='') OR RX_KIND='IG' AND #ACTIVE#='N'");
			// ds.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(false);
			// 设置处方号
			onNewRxNoIG("2");
			break;
			// case 4:
			// //临床诊断
			// pubParm = new TParm();
			// pubParm.setData("CASE_NO",this.getCaseNo());
			// pubParm.setData("IPD_NO",this.getIpdNo());
			// pubParm.setData("MR_NO",this.getMrNo());
			// pubParm.setData("RULE_TYPE","I");
			// ((TPanel)this.getComponent("CLNDIAG_PANEL")).addItem(
			// "CLNDIAG", "%ROOT%\\config\\odi\\ODIClnDiagUI.x", pubParm,false);
			// return;
			// case 5:
			// //病案首页
			// pubParm = new TParm();
			// pubParm.setData("SYSTEM_CODE", "ODI");
			// pubParm.setData("MR_NO", this.getMrNo());
			// pubParm.setData("CASE_NO", this.getCaseNo());
			// //医师调用(USER_TYPE=2)
			// pubParm.setData("USER_TYPE","2");
			// pubParm.setData("OPEN_USER",Operator.getID());
			// ((TPanel)this.getComponent("MROINDEX_PANEL")).addItem(
			// "MROINDEX", "%ROOT%\\config\\mro\\MRORecord.x", pubParm,false);
			// return;
			// case 6:
			// //病案审核
			// pubParm = new TParm();
			// pubParm.setData("MRO", "STATE", "ODI");
			// pubParm.setData("MRO", "MR_NO", this.getMrNo());
			// pubParm.setData("MRO", "CASE_NO", this.getCaseNo());
			// ((TPanel)this.getComponent("MROCHR_PANEL")).addItem(
			// "MROCHR", "%ROOT%\\config\\mro\\MRO_Chrtvetrec.x", pubParm,false);
			// return;
		case 4:
			// 过敏记录
			table = getTTable(GMTABLE);
			table.setFilter("DRUG_TYPE='" + getDrugType() + "' AND MR_NO='"
					+ this.getMrNo() + "'");
			// table.setSort("ADM_DATE");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			// 过敏记录
			break;
		case 5:
			// 院前用药
			table = getTTable(TABLE6);
			table.setFilter(" RX_KIND='PA' ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 6:
			// 术中用药
			// 记录当前页面INDEX
			indexPage = tab.getSelectedIndex();
			this.clearFlg = "N";
			return;
		case 7:
			// 用血记录
			// 记录当前页面INDEX
			indexPage = tab.getSelectedIndex();
			this.clearFlg = "N";
			return;
		}
		// 中药饮片
		if (tab.getSelectedIndex() == 3) {
			if (!odiObject.filter(table, ds, this.isStopBillFlg())) {
				// 已经停止划价！
				this.messageBox("E0155");
			}
		} else {
			table.filter();
			// if (tab.getSelectedIndex() != 4)
			// table.sort();
			if (tab.getSelectedIndex() == 5) {
				table.setSort("ORDER_NO ASC,ORDER_SEQ ASC");
				table.sort();
			}
			table.setDSValue();
		}
		// this.antflg = false;//去掉后提示抗生素的开药天数时候合理（行闪动）
		// 初始化医嘱设置
		initOrderStart();
		// 添加一行
		this.onAddRow();
		// 锁定行
		lockRowOrder();
		// 记录当前页面INDEX
		indexPage = tab.getSelectedIndex();
		this.clearFlg = "N";
	}

	// 卢海加入 长期医嘱转出院带药功能
	public void udTods() {
		TTable table2 = getTTable(TABLE2);
		// table2.setFilter("(RX_KIND='UD' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='UD' AND #ACTIVE#='N'");
		TDS ds = odiObject.getDS("ODI_ORDER");
		String dsFilter = ds.getFilter();
		ds
		.setFilter("(RX_KIND='UD' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='UD' AND #ACTIVE#='N'");
		ds.filter();
		// 需要移动的order
		TParm result = new TParm();
		Vector ordvct = ds.getVector();
		for (int i = 0; i < ordvct.size(); i++) {
			Vector orderVectorRow = ds.getVectorRow(i, "ORDER_CODE");
			Vector cat1TypeVectorRow = ds.getVectorRow(i, "CAT1_TYPE");
			Vector routeTypeVectorRow = ds.getVectorRow(i, "ROUTE_CODE");
			String orderCode = (String) orderVectorRow.get(0);
			String cat1Type = (String) cat1TypeVectorRow.get(0);
			String routeCode = (String) routeTypeVectorRow.get(0);

			if (!"".equals(orderCode) && "PHA".equals(cat1Type)
					&& "PO".equals(routeCode)) {
				result.addData("ORDER_CODE", orderCode);
				// $$=============add by lx 2012/04/26
				// start==================$$//
				// 带入用量
				result.addData("MEDI_QTY", ds.getVectorRow(i, "MEDI_QTY")
						.get(0));
				// System.out.println("MEDI_QTY===="+ds.getVectorRow(i,
				// "MEDI_QTY").get(0));
				// 频次
				result.addData("FREQ_CODE", ds.getVectorRow(i, "FREQ_CODE")
						.get(0));
				// System.out.println("FREQ_CODE===="+ds.getVectorRow(i,
				// "FREQ_CODE").get(0));
				// 用法
				result.addData("ROUTE_CODE", ds.getVectorRow(i, "ROUTE_CODE")
						.get(0));
				// System.out.println("ROUTE_CODE===="+ds.getVectorRow(i,
				// "ROUTE_CODE").get(0));
				// $$=============add by lx 2012/04/26 end==================$$//
			}
		}
		// 恢复
		ds.setFilter(dsFilter);
		ds.filter();
		// 出院带药
		TTable table = getTTable(TABLE3);
		table.setFilter("(RX_KIND='DS' AND HIDE_FLG = 'N' " + getRxNoString()
		+ " AND DC_DATE='') OR RX_KIND='DS' AND #ACTIVE#='N'");
		int rowCount = result.getCount("ORDER_CODE");
		int row = getExitRow();
		yyList = true;
		for (int i = 0; i < rowCount; i++) {
			TParm OrderParm = OdiUtil.getInstance().getSysFeeOrder(
					result.getValue("ORDER_CODE", i));
			// $$=============add by lx 2012/04/26 start==================$$//
			OrderParm.setData("MEDI_QTY", result.getValue("MEDI_QTY", i));
			OrderParm.setData("FREQ_CODE", result.getValue("FREQ_CODE", i));
			OrderParm.setData("ROUTE_CODE", result.getValue("ROUTE_CODE", i));

			// $$=============add by lx 2012/04/26 end==================$$//
			OrderParm.setData("EXID_ROW", row);
			OrderParm.setData("CDSS_FLG", false); // add by huangtt
			// 20151116出院代药时引用长期医嘱不进行CDSS智能判断
			row++;
			// System.out.println("===OrderParm==="+i+"==="+OrderParm);
			this.popReturn("DS", OrderParm);
		}
		yyList = false;
	}

	/**
	 * 临床诊断
	 */
	public void onLcICD() {
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		TParm pubParm = new TParm();
		pubParm.setData("CASE_NO", this.getCaseNo());
		pubParm.setData("IPD_NO", this.getIpdNo());
		pubParm.setData("MR_NO", this.getMrNo());
		pubParm.setData("DEPT_CODE", this.getDeptCode());
		pubParm.setData("DR_CODE", Operator.getID());
		pubParm.setData("RULE_TYPE", "I");
		pubParm.setData("BIRTHDAY", getBirthDay());
		pubParm.setData("SEX_CODE", getSexCode());
		pubParm.setData("IN_DATE", getAdmDate());
		this.openWindow("%ROOT%\\config\\odi\\ODIClnDiagUI.x", pubParm);// shibl
		// modify
		// 20130515
		// this.openWindow("%ROOT%\\config\\odi\\ODIClnDiagUI.x", pubParm);
	}

	/**
	 * 病案编目
	 */
	public void onBASY() {
		TParm pubParm = new TParm();
		pubParm.setData("SYSTEM_CODE", "ODI");
		pubParm.setData("MR_NO", this.getMrNo());
		pubParm.setData("CASE_NO", this.getCaseNo());
		// 医师调用(USER_TYPE=2)
		pubParm.setData("USER_TYPE", "2");
		pubParm.setData("OPEN_USER", Operator.getID());
		TParm result = (TParm) this.openWindow(
				"%ROOT%\\config\\mro\\MRORecord.x", pubParm);// shibl modify
		// 20130515
	}

	/**
	 * 病案审查
	 */
	public void onBABM() {
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		TParm pubParm = new TParm();
		pubParm.setData("MRO", "STATE", "ODI");
		pubParm.setData("MRO", "MR_NO", this.getMrNo());
		pubParm.setData("MRO", "CASE_NO", this.getCaseNo());
		this.openWindow("%ROOT%\\config\\mro\\MRO_Chrtvetrec.x", pubParm);// shibl
		// modify
		// 20130515
	}

	/**
	 * 过敏史类别
	 * 
	 * @return String
	 */
	public String getDrugType() {
		String resultStr = "";
		if (((TRadioButton) this.getComponent("PHA_DRUGALLERGY")).isSelected()) {
			// resultStr = "B";
			resultStr = this.getValueString("PHA_CLASS"); // modify by huangtt
			// 20150507
		}
		if (((TRadioButton) this.getComponent("CF_DRUGALLERGY")).isSelected()) {
			resultStr = "A";
		}
		if (((TRadioButton) this.getComponent("OTHER_DRUGALLERGY"))
				.isSelected()) {
			resultStr = "C";
		}
		if (((TRadioButton) this.getComponent("NONE_DRUGALLERGY")).isSelected()) {
			resultStr = "N";
		}
		return resultStr;
	}

	/**
	 * 页签改变事件
	 */
	public void onChange() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		// 公用参数
		TParm pubParm = null;
		TTable table = null;
		// System.out.println("tab.getSelectedIndex()!!!============================="+tab.getSelectedIndex());
		TDS ds = odiObject.getDS("ODI_ORDER");
		switch (tab.getSelectedIndex()) {
		case 0:
			// 临时
			table = getTTable(TABLE1);
			// $$===========add by lx 2012-06-30 临时医嘱只显示当天,提高显示速度
			// START================$$//
			String f1 = "(RX_KIND='ST' AND HIDE_FLG='N' ";
			String f2 = ".0' OR (RX_KIND='ST' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='ST' AND #ACTIVE#='N'";
			if (opeFlg) {
				f1 = "(RX_KIND='OP' AND HIDE_FLG='N' ";
				f2 = ".0' OR (RX_KIND='OP' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='OP' AND #ACTIVE#='N'";
			}
			table.setFilter(f1
					+ getQueryOrderCat1Type()
					+ " "
					+ getPhaOrderUtil()
					+ " AND ORDER_DATE >= '"
					+ StringTool.getString((Timestamp) this
							.getValue("START_DATEST"), "yyyy-MM-dd HH:mm:ss")
					+ ".0' AND ORDER_DATE < '"
					+ StringTool.getString((Timestamp) this
							.getValue("END_DATEST"), "yyyy-MM-dd HH:mm:ss")
					+ f2);
			// $$===========add by lx 2012-06-30 临时医嘱只显示当天,提高显示速度
			// END================$$//
			// del by lx 2012-06-30 临时医嘱不显示全部内容，提高显示速度
			// table.setFilter("(RX_KIND='ST' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='ST' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 1:
			// 长期
			table = getTTable(TABLE2);
			table
			.setFilter("(RX_KIND='UD' AND HIDE_FLG = 'N' AND ( DC_DATE='' OR DC_DATE>'"
					+ StringTool.getString(SystemTool.getInstance()
							.getDate(), "yyyy-MM-dd HH:mm:ss")
					+ ".0')) OR RX_KIND='UD' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 2:
			// 出院带药
			table = getTTable(TABLE3);
			table
			.setFilter("(RX_KIND='DS' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='DS' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			onNewRxNo("2");
			break;
		case 3:
			// 中药饮片
			table = getTTable(TABLE4);
			ds
			.setFilter("(RX_KIND='IG' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='IG' AND #ACTIVE#='N'");
			// ds.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(false);
			this.onNewRxNoIG("2");
			break;
			// case 4:
			// //临床诊断
			// pubParm = new TParm();
			// pubParm.setData("CASE_NO",this.getCaseNo());
			// pubParm.setData("IPD_NO",this.getIpdNo());
			// pubParm.setData("MR_NO",this.getMrNo());
			// pubParm.setData("RULE_TYPE","I");
			// ( (TPanel)this.getComponent("CLNDIAG_PANEL")).addItem(
			// "CLNDIAG", "%ROOT%\\config\\odi\\ODIClnDiagUI.x", pubParm);
			// return;
			// case 5:
			// //病案首页
			// pubParm = new TParm();
			// pubParm.setData("SYSTEM_CODE", "ODI");
			// pubParm.setData("MR_NO", this.getMrNo());
			// pubParm.setData("CASE_NO", this.getCaseNo());
			// ((TPanel)this.getComponent("MROINDEX_PANEL")).addItem(
			// "MROINDEX", "%ROOT%\\config\\mro\\MRORecord.x", pubParm);
			// return;
			// case 6:
			// //病案审核
			// pubParm = new TParm();
			// pubParm.setData("MRO", "STATE","ODI");
			// pubParm.setData("MRO","MR_NO", this.getMrNo());
			// pubParm.setData("MRO","CASE_NO", this.getCaseNo());
			// ((TPanel)this.getComponent("MROCHR_PANEL")).addItem(
			// "MROCHR", "%ROOT%\\config\\mro\\MROChrtvetrec.x", pubParm);
			// return;
		case 4:
			// 过敏记录
			table = getTTable(GMTABLE);
			table.setFilter("DRUG_TYPE='" + getDrugType() + "' AND MR_NO='"
					+ this.getMrNo() + "'");
			// table.setSort("ADM_DATE");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			// 过敏记录
			break;
		case 5:
			// 院前用药
			table = getTTable(TABLE6);
			table.setFilter(" RX_KIND='PA' ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		}
		// 中药饮片
		if (tab.getSelectedIndex() == 3) {
			if (!odiObject.filter(table, ds, this.isStopBillFlg())) {
				// 已经停止划价！
				this.messageBox("E0155");
			}
		} else {
			table.filter();
			// table.sort();
			table.setDSValue();
		}
		this.onAddRow();
		// 初始化医嘱设置
		initOrderStart();
		// 添加一行
		// table.getDataStore().showDebug();
		// 记录当前页面INDEX
		indexPage = tab.getSelectedIndex();
		// 锁定行
		lockRowOrder();
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		BYXflg=false;
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		// 公用参数
		TTable table = null;
		TDS ds = odiObject.getDS("ODI_ORDER");
		// System.out.println("tab.getSelectedIndex():::::"+tab.getSelectedIndex());
		switch (tab.getSelectedIndex()) {
		case 0:
			// $$========add by lx 2013/01/11 临时重新加载数据==========$$//
			initDataStoreToTable();
			//
			// 临时
			table = getTTable(TABLE1);
			String filter1 = "(RX_KIND='ST' AND HIDE_FLG='N' ";
			String filter2 = ".0' OR (RX_KIND='ST' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='ST' AND #ACTIVE#='N'";
			if (opeFlg) {
				filter1 = "(RX_KIND='OP' AND HIDE_FLG='N' ";
				filter2 = ".0' OR (RX_KIND='OP' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='OP' AND #ACTIVE#='N'";
			}
			table.setFilter(filter1
					+ getQueryOrderCat1Type()
					+ " "
					+ getPhaOrderUtil()
					+ " AND ORDER_DATE >= '"
					+ StringTool.getString((Timestamp) this
							.getValue("START_DATEST"), "yyyy-MM-dd HH:mm:ss")
					+ ".0' AND ORDER_DATE < '"
					+ StringTool.getString((Timestamp) this
							.getValue("END_DATEST"), "yyyy-MM-dd HH:mm:ss")
					+ filter2);
			// table.setSort("ORDER_NO,ORDER_SEQ");
			break;
		case 1:
			// 长期
			table = getTTable(TABLE2);
			// System.out.println("DG11111111111111111111");
			if (this.getTRadioButton("DCORDER").isSelected()) {// shibl
				// System.out.println("2222222222222222222222222");
				dcOrderShow();
				break;
			}
			table
			.setFilter("(RX_KIND='UD' AND HIDE_FLG='N' "
					+ getQueryOrderCat1Type()
					+ " "
					+ getPhaOrderUtil()
					+ " AND ORDER_DATE >= '"
					+ StringTool.getString((Timestamp) this
							.getValue("START_DATEUD"),
							"yyyy-MM-dd HH:mm:ss")
					+ ".0' AND ORDER_DATE < '"
					+ StringTool.getString((Timestamp) this
							.getValue("END_DATEUD"),
							"yyyy-MM-dd HH:mm:ss")
					+ ".0' OR (RX_KIND='UD' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='UD' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			// System.out.println("输出：："+"(RX_KIND='UD' AND HIDE_FLG='N' "
			// + getQueryOrderCat1Type()
			// + " "
			// + getPhaOrderUtil()
			// + " AND ORDER_DATE >= '"
			// + StringTool.getString((Timestamp) this
			// .getValue("START_DATEUD"),
			// "yyyy-MM-dd HH:mm:ss")
			// + ".0' AND ORDER_DATE < '"
			// + StringTool.getString((Timestamp) this
			// .getValue("END_DATEUD"),
			// "yyyy-MM-dd HH:mm:ss")
			// +
			// ".0' OR (RX_KIND='UD' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='UD' AND #ACTIVE#='N'");

			break;
		case 2:
			// 出院带药
			table = getTTable(TABLE3);
			table
			.setFilter("(RX_KIND='DS' AND HIDE_FLG='N' "
					+ getRxNoString()
					+ " OR (RX_KIND='DS' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='DS' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			break;
		case 3:
			// 中药饮片
			table = getTTable(TABLE4);
			ds
			.setFilter("(RX_KIND='IG' AND HIDE_FLG='N' "
					+ getRxNoIGString()
					+ " OR (RX_KIND='IG' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='IG' AND #ACTIVE#='N'");
			// ds.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(false);
			break;
		case 4:
			// 过敏记录
			table = getTTable(GMTABLE);
			table.setFilter("DRUG_TYPE='" + getDrugType() + "' AND MR_NO='"
					+ this.getMrNo() + "'");
			// table.setSort("ADM_DATE");
			break;
		}
		if (tab.getSelectedIndex() != 3) {
			table.filter();
			// table.sort();
			table.setDSValue();
		} else {
			if (!odiObject.filter(table, ds, this.isStopBillFlg())) {
				this.messageBox("E0155");
			}
		}
		// 初始化医嘱设置
		initOrderStart();
		// 添加一行
		this.onAddRow();
		// 锁定行
		lockRowOrder();
		// 记录当前页面INDEX
		indexPage = tab.getSelectedIndex();
		// 设置清空注记(不按清空执行)
		this.clearFlg = "N";
	}

	// 4,10,16,17,18,19,20,21,22,23,24,25,26,27,28,29
	/**
	 * 得到处方号
	 * 
	 * @return String
	 */
	public String getRxNoString() {
		// if(this.getValueString("RX_NO").length()==0)
		// return "";
		return " AND RX_NO='" + this.getValueString("RX_NO") + "' ";
	}

	/**
	 * 得到处方号
	 * 
	 * @return String
	 */
	public String getRxNoIGString() {
		// if(this.getValueString("IG_RX_NO").length()==0)
		// return "";
		return " AND RX_NO='" + this.getValueString("IG_RX_NO") + "' ";
	}

	/**
	 * 拿到医嘱类别
	 * 
	 * @return String
	 */
	public String getQueryOrderCat1Type() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		String cat1Type = "";
		String type = "";
		// 根据页签切换取不同控件中的值 SHIBL 20120801 modify
		switch (tab.getSelectedIndex()) {
		case 0:
			type = this.getValueString("KLSTAR");
			break;
		case 1:
			type = this.getValueString("KLSTARUD");
			break;
		case 2:
			type = this.getValueString("KLSTARDS");
			break;
		}
		if ("A".equals(type)) {
			return cat1Type;
		}
		if ("B".equals(type)) {
			cat1Type = "AND (CAT1_TYPE='LIS' OR CAT1_TYPE='RIS')";
		}
		if ("C".equals(type)) {
			cat1Type = "AND (CAT1_TYPE='TRT' OR CAT1_TYPE='PLN' OR CAT1_TYPE='OTH')";
		}
		if ("D".equals(type)) {
			cat1Type = "AND ORDER_CAT1_CODE='PHA_W'";
		}
		if ("E".equals(type)) {
			cat1Type = "AND ORDER_CAT1_CODE='PHA_C'";
		}
		if ("F".equals(type)) {
			cat1Type = "AND ORDER_CAT1_CODE='PHA_G'";
		}
		if ("G".equals(type)) {
			cat1Type = "AND LCS_CLASS_CODE != ''";
		}
		return cat1Type;
	}

	/**
	 * 医嘱有效性
	 * 
	 * @return String
	 */
	public String getPhaOrderUtil() {
		String phaOrderUtilStr = "";
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		// System.out.println("tab.getSelectedIndex()!!!============================="+tab.getSelectedIndex());
		switch (tab.getSelectedIndex()) {
		case 0:
			// 临时
			// 有效
			if (((TRadioButton) this.getComponent("YXORDER")).isSelected()) {
				phaOrderUtilStr = "AND DC_DATE = ''";
			}
			// 已停用
			if (((TRadioButton) this.getComponent("STOPORDER")).isSelected()) {
				phaOrderUtilStr = "AND DC_DATE != ''";
			}
			// 全部
			if (((TRadioButton) this.getComponent("ALLORDER")).isSelected()) {
				return phaOrderUtilStr;
			}
			break;
		case 1:
			// 长期
			// 有效yanjing 20131119
			String nowDate = StringTool.getString((Timestamp) SystemTool
					.getInstance().getDate(), "yyyy-MM-dd HH:mm:ss");
			nowDate += ".0";
			if (((TRadioButton) this.getComponent("UDUTIL")).isSelected()) {
				// phaOrderUtilStr = "AND DC_DATE = '' ";
				// stSQL+=" AND ORDER_DATE<= TO_DATE ('"+stEndDate+"', 'YYYY-MM-DD HH24:MI:SS'))";
				// phaOrderUtilStr = "AND (DC_DATE = '' OR DC_DATE > SYSDATE) ";
				phaOrderUtilStr = "AND (DC_DATE = '' OR DC_DATE >'" + nowDate
						+ "')";
			}
			// 已停用
			if (((TRadioButton) this.getComponent("DCORDER")).isSelected()) {
				// phaOrderUtilStr =
				// "AND DC_DATE != '' AND DC_DATE <= SYSDATE ";
				// phaOrderUtilStr = "AND DC_DATE != '' ";
				phaOrderUtilStr = "AND DC_DATE != '' AND DC_DATE <= '"
						+ nowDate + "' ";
			}
			// 全部
			if (((TRadioButton) this.getComponent("UDALLORDER")).isSelected()) {
				return phaOrderUtilStr;
			}
			break;
		}
		return phaOrderUtilStr;
	}

	/**
	 * 锁定行
	 */
	public void lockRowOrder() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		TTable table = null;
		int rowCount = 0;
		switch (tab.getSelectedIndex()) {
		// 临时
		case 0:
			table = this.getTTable(TABLE1);
			rowCount = table.getDataStore().rowCount();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = table.getDataStore().getRowParm(i);
				if (table.getDataStore().isActive(i)) {

					if (temp.getValue("ORDER_CODE").length() != 0) {
						table.setLockCellRow(i, true);
						table.setLockCell(i, 9, false);
					}
					if (getTempFlg(temp).equals("Y")) { // 护士审核过医嘱锁行 shibl
						// 20120927 modify
						table.setLockCellRow(i, true);
					}
					if (temp.getValue("DC_DR_CODE").length() != 0) {
						table.setLockCell(i, 9, true);
					}
				} else {

					table.setLockCellRow(i, false);
				}
			}
			int newRowST[] = table.getDataStore().getModifiedRows();
			for (int i : newRowST) {
				TParm temp = table.getDataStore().getRowParm(i);
				// 设置连接医嘱细项
				if (temp.getValue("LINKMAIN_FLG").equals("N")
						&& !temp.getValue("LINK_NO").equals("")) {// shibl
					// 20130315
					// modify
					table.setLockCellRow(i, true);
					table.setLockCell(i, 0, false);// 连接主
					table.setLockCell(i, 1, false);// 组号
					table.setLockCell(i, 2, false);// 医嘱
					table.setLockCell(i, 3, false);// 用量
					table.setLockCell(i, 6, false);// 泵入方式
					table.setLockCell(i, 7, false);// 输液速率
					table.setLockCell(i, 11, false);// 医嘱备注
					table.setLockCell(i, 13, false);// 抗菌标识
					table.setLockCell(i, 14, false);// 续
					table.setLockCell(i, 15, false);// 急
					table.setLockCell(i, 16, false);// 备
					table.setLockCell(i, 17, false);// 给
				} else {
					table.setLockCellRow(i, false);
				}
			}
			break;
			// 长期
		case 1:
			table = this.getTTable(TABLE2);
			rowCount = table.getDataStore().rowCount();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = table.getDataStore().getRowParm(i);
				if (table.getDataStore().isActive(i)) {
					if (temp.getValue("ORDER_CODE").length() != 0) {
						table.setLockCellRow(i, true);
						table.setLockCell(i, 11, false);  //modify by wukai 9改11
					}
					if (getTempFlg(temp).equals("Y")) { // 护士审核过医嘱锁行 shibl
						// 20120927 modify
						table.setLockCellRow(i, true);
					}
					if (temp.getValue("DC_DR_CODE").length() != 0) {
						table.setLockCell(i, 11, true); //modify by wukai 9改11
					}
				} else {

					table.setLockCellRow(i, false);

				}
			}
			int newRowUD[] = table.getDataStore().getModifiedRows();
			for (int i : newRowUD) {// 设置连接医嘱细项
				TParm temp = table.getDataStore().getRowParm(i);
				if (temp.getValue("LINKMAIN_FLG").equals("N")
						&& !temp.getValue("LINK_NO").equals("")) {// shibl
					// 20130315
					// modify
					this.getTTable(TABLE2).setLockCellRow(i, true);
					this.getTTable(TABLE2).setLockCell(i, 0, false);// 连接主
					this.getTTable(TABLE2).setLockCell(i, 1, false);// 组号
					this.getTTable(TABLE2).setLockCell(i, 2, false);// 医嘱
					this.getTTable(TABLE2).setLockCell(i, 3, false);// 用量
					this.getTTable(TABLE2).setLockCell(i, 7, false);// 泵入方式
					this.getTTable(TABLE2).setLockCell(i, 8, false);// 输液速率
					this.getTTable(TABLE2).setLockCell(i, 9, false);// 急
					this.getTTable(TABLE2).setLockCell(i, 10, false);// 备
					this.getTTable(TABLE2).setLockCell(i, 11, false);// 医嘱备注
					this.getTTable(TABLE2).setLockCell(i, 16, false);// 抗菌
				} else {
					table.setLockCellRow(i, false);
				}
			}
			break;
			// 出院带药
		case 2:
			table = this.getTTable(TABLE3);
			rowCount = table.getDataStore().rowCount();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = table.getDataStore().getRowParm(i);
				if (table.getDataStore().isActive(i)) {
					if (temp.getValue("ORDER_CODE").length() != 0) {
						table.setLockCellRow(i, true);
						table.setLockCell(i, 11, false);
						table.setLockCell(i, 3, false);
						table.setLockCell(i, 5, false);
						table.setLockCell(i, 6, false);
						table.setLockCell(i, 7, false);
						table.setLockCell(i, 8, false);
						table.setLockCell(i, 9, false);
						table.setLockCell(i, 10, false);
						table.setLockCell(i, 13, false);// 抗菌标识

					}
					if (getTempFlg(temp).equals("Y")) { // 护士审核过医嘱锁行 shibl
						// 20120927 modify
						table.setLockCellRow(i, true);
					}
					if (temp.getValue("DC_DR_CODE").length() != 0) {
						table.setLockCell(i, 11, true);
						table.setLockCell(i, 3, true);
						table.setLockCell(i, 5, true);
						table.setLockCell(i, 6, true);
						//						table.setLockCell(i, 7, true);
						//						table.setLockCell(i, 8, true);
						table.setLockCell(i, 9, true);
						table.setLockCell(i, 10, true);
						table.setLockCell(i, 13, false);// 抗菌标识
					}
				} else {
					table.setLockCellRow(i, false);
				}
			}
			int newRowDS[] = table.getDataStore().getModifiedRows();
			for (int i : newRowDS) {
				table.setLockCellRow(i, false);
			}
			break;
			// 住院中医
		case 3:

			break;
			// 过敏记录
		case 4:
			table = this.getTTable(GMTABLE);
			rowCount = table.getDataStore().rowCount();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = table.getDataStore().getRowParm(i);
				if (table.getDataStore().isActive(i)) {
					if (temp.getValue("DRUGORINGRD_CODE").length() != 0) {
						table.setLockCellRow(i, true);
						table.setLockCell(i, "ALLERGY_NOTE", false);
					}
				} else {
					table.setLockCellRow(i, false);
				}
			}
			int newRowGM[] = table.getDataStore().getModifiedRows();
			for (int i : newRowGM) {
				table.setLockCellRow(i, false);
			}
			break;
		case 5:
			table = this.getTTable(TABLE6);
			rowCount = table.getDataStore().rowCount();
			for (int i = 0; i < rowCount; i++) {
				if (table.getDataStore().isActive(i)) {
					table.setLockCell(i, 0, true);
				} else {
					table.setLockCell(i, 0, false);
				}
			}
		}
	}

	/**
	 * 得到医嘱是否审核过注记
	 * 
	 * @param parm
	 * @return
	 */
	private String getTempFlg(TParm parm) {
		if (parm == null)
			return "";
		String caseNo = parm.getValue("CASE_NO");
		String orderNo = parm.getValue("ORDER_NO");
		String orderSeq = parm.getValue("ORDER_SEQ");
		String sql = "SELECT TEMPORARY_FLG FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo + "' AND ORDER_NO='" + orderNo + "' AND ORDER_SEQ='"
				+ orderSeq + "'";
		TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
		if (sqlparm.getCount() <= 0)
			return "";

		return sqlparm.getValue("TEMPORARY_FLG", 0);
	}

	/**
	 * 初始化医嘱状态
	 */
	public void initOrderStart() {
		boolean flg = true;
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		TTable table = getTTable("TABLE" + (tab.getSelectedIndex() + 1));
		// this.messageBox("2");
		// 中药饮片时不做此判断
		if (tab.getSelectedIndex() >= 3)
			return;
		TParm parm = table.getDataStore().getBuffer(
				table.getDataStore().PRIMARY);
		int rowCount = parm.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) {
			TParm action = parm.getRow(i);
			// System.out.println("111111action is:::"+action);
			// ================= chenxi 药品提示信息
			String orderCode = action.getValue("ORDER_CODE");
			String sql = "SELECT ORDER_CODE,DRUG_NOTES_DR FROM SYS_FEE WHERE ORDER_CODE = '"
					+ orderCode + "' ";
			TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
			sqlparm = sqlparm.getRow(0);
			// System.out.println("===========sqlparm========"+sqlparm);
			// ============ chenxi
			// 是否是医保药品
			if (OrderUtil.getInstance().isNhiPat(this.getCaseNo())) {
				if ("C".equals(action.getValue("INSPAY_TYPE"))) {
					table.setRowTextColor(i, nhiColor);
				}
			} else {
				table.setRowTextColor(i, normalColor);
			}
			// 判断管制药品等级
			if ("PHA".equals(action.getValue("CAT1_TYPE"))) {
				// 是否是管制药品
				if (action.getValue("CTRLDRUGCLASS_CODE").length() != 0) {
					table.setRowTextColor(i, ctrlDrugClassColor);
				}
			} else {
				table.setRowTextColor(i, normalColor);
			}
			// 判断抗生素
			if (action.getValue("ANTIBIOTIC_CODE").length() != 0) {
				flg = this.antflg;
				if ("UD".equals(action.getValue("RX_KIND"))
						&& action.getValue("DC_DATE").equals("") && flg) {// shibl
					// 20130326
					// modify
					// 非停用药品检验
					// 查看抗生素天数是否超过预设天数
					int day = OrderUtil.getInstance().checkAntibioticDay(
							action.getValue("ANTIBIOTIC_CODE"));
					int cDay = StringTool.getDateDiffer(SystemTool
							.getInstance().getDate(), (Timestamp) action
							.getData("EFF_DATE"));

					// if (day != -1 && cDay + 1 >= day) {// modify by wanglong
					// 20130801 开药天数比标准天数少一天就开始闪
					// // 闪动
					// this.changeTableRowColor(i, tab.getSelectedIndex());
					// // if ("en".equals(this.getLanguage())) {
					// // this
					// // .messageBox(action
					// // .getValue("ORDER_ENG_DESC")
					// // + ":Beyond the default number of antibiotics:"
					// // + day + "days！");
					// // } else {
					// // this.messageBox(action.getValue("ORDER_DESC")
					// // + ":抗生素超出了预设天数" + day + "天！");
					// // }
					// // 停止闪动
					// // this.stopColor();
					// }
				}
				table.setRowTextColor(i, antibioticColor);

			} else if (sqlparm.getValue("DRUG_NOTES_DR").length() != 0) {// ===========cxcx用药注意事项不为空
				table.setRowTextColor(i, blue);
				// continue;
			} else {
				table.setRowTextColor(i, normalColor);
			}
			// 是否是停用医嘱
			// 取得系统与停用时间比较,停用时间小于等于系统时间时,背景设置成灰色
			// 取得系统时间
			String sysdate = SystemTool.getInstance().getDate().toString();
			String dc_date = action.getValue("DC_DATE");
			if (action.getValue("DC_DR_CODE").length() != 0
					&& sysdate.compareTo(dc_date) >= 0) {
				// this.messageBox("1111111111111111111");
				table.setRowColor(i, dcColor);
				continue;
			} else {
				table.setRowColor(i, normalColorBJ);
			}

			// 是否有护士备注NS_NOTE
			if (action.getValue("NS_NOTE").length() != 0) {
				table.setRowColor(i, nsNodeColor);
				continue;
			} else {
				table.setRowColor(i, normalColorBJ);
			}
			// 护士是否审核
			if (action.getValue("NS_CHECK_CODE").length() != 0) {
				table.setRowColor(i, checkColor);
				continue;
			} else {
				table.setRowColor(i, normalColorBJ);
			}
		}
		// this.messageBox("22");
	}

	/**
	 * 返回实际列名
	 * 
	 * @param column
	 *            String
	 * @param column
	 *            int
	 * @return String
	 */
	public String getFactColumnName(String tableTag, int column) {
		int col = this.getThisColumnIndex(column, tableTag);
		return this.getTTable(tableTag).getDataStoreColumnName(col);
	}

	/**
	 * 拿到更变之前的列号
	 * 
	 * @param column
	 *            int
	 * @return int
	 */
	public int getThisColumnIndex(int column, String table) {
		return this.getTTable(table).getColumnModel().getColumnIndex(column);
	}

	/**
	 * 拿到当前选中列号
	 * 
	 * @param column
	 *            int
	 * @return int
	 */
	public int getNewThisColumnIndex(int column) {
		return this.getTTable(TABLE1).getColumnModel()
				.getColumnIndexNew(column);
	}

	/**
	 * 过敏记录TABLE编辑时的响应
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentGM(Component com, int row, int column) {
		// 状态条显示
		callFunction("UI|setSysStatus", "");
		// 拿到列名
		String columnName = this.getFactColumnName(GMTABLE, column);
		// if(!"DRUGORINGRD_CODE".equals(columnName))
		// return;
		if (!"ORDER_DESC".equals(columnName))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		String drugType = this.getDrugType();
		// 成分
		if ("A".equals(drugType)) {
			// 过敏记录设置
			TParm parm = new TParm();
			parm.addData("ALLERGY_TYPE", "A");
			// 设置弹出菜单
			textFilter.setPopupMenuParameter("GMA", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SysAllergy.x"), parm);
		}
		// 药品
		if ("B".equals(drugType)) {
			// 过敏记录设置
			TParm parm = new TParm();
			parm.setData("ODI_ORDER_TYPE", "A");
			// 设置弹出菜单
			textFilter.setPopupMenuParameter("GMB", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		}
		// 其他
		if ("C".equals(drugType)) {
			// 过敏记录设置
			TParm parm = new TParm();
			parm.addData("ALLERGY_TYPE", "C");
			// 设置弹出菜单
			textFilter.setPopupMenuParameter("GMC", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SysAllergy.x"), parm);
		}
		// 无过敏 add by guoy 20151112
		if ("N".equals(drugType)) {
			// 过敏记录设置
			TParm parm = new TParm();
			parm.addData("ALLERGY_TYPE", "N");
			// 设置弹出菜单
			textFilter.setPopupMenuParameter("GMN", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SysAllergy.x"), parm);
		}

		// add by huangtt 20150507 start
		// 药理大分类 add by huangtt 20150507
		if ("D".equals(drugType)) {
			TParm parm = new TParm();
			parm.setData("ALLERGY_TYPE", "D");
			// 给table上的新text增加sys_fee弹出窗口
			textFilter.setPopupMenuParameter("GMB", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSPhaClassPopup.x"), parm);
		}
		// 药理次分类 add by huangtt 20150507
		if ("E".equals(drugType)) {
			TParm parm = new TParm();
			parm.setData("ALLERGY_TYPE", "E");
			// 给table上的新text增加sys_fee弹出窗口
			textFilter.setPopupMenuParameter("GMB", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSPhaClassPopup.x"), parm);
		}

		// add by huangtt 20150507 end

		// 定义接受返回值方法
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}

	/**
	 * 当TABLE创建编辑控件时临时
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentST(Component com, int row, int column) {
		// 状态条显示
		callFunction("UI|setSysStatus", "");
		// 当前编辑行
		this.rowOnly = row;
		// 拿到列名
		String columnName = this.getFactColumnName(TABLE1, column);
		if (!"ORDER_DESCCHN".equals(columnName))
			return;

		// xueyf begin
		TTable table = getTTable(TABLE1);
		/**
		 * TParm parm1=table.getShowParmValue();d //是集合医嘱
		 * if(parm1.getValue(columnName,row).startsWith("*")){ TTextField
		 * textFilter = (TTextField) com; textFilter.setEnabled(false); return ;
		 * }
		 **/
		int selRow = this.getTTable(TABLE1).getSelectedRow();
		TParm existParm = this.getTTable(TABLE1).getDataStore().getRowParm(
				selRow);
		// System.out.println("===come in existParm==="+existParm);
		if (this.isOrderSet(existParm)) {
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			return;
		}
		if ("PHA".equals(existParm.getValue("CAT1_TYPE"))
				&& null != existParm.getValue("ANTIBIOTIC_CODE")
				&& !existParm.getValue("ANTIBIOTIC_CODE").equals("")) {// =====pangben
			// 2014-6-26
			// 修改传回的抗菌药品不可以修改
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			this.messageBox("抗菌药品不可以修改");
			return;
		}
		// xueyf end

		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// 临时医嘱设置
		TParm parm = new TParm();
		parm.setData("ODI_ORDER_TYPE", this.getValue("KLSTAR"));
		// 设置弹出菜单
		textFilter.setPopupMenuParameter("ST", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}

	/**
	 * 临时医嘱修改事件监听
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onChangeTableValueST(Object obj) {
		//this.messageBox("================onChangeTableValueST=====================");
		// 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// 拿到table上的parmmap的列名
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		//this.messageBox(""+columnName);
		// 判断当前列是否有医;
		int selRow = node.getRow();
		TParm orderP = this.getTTable(TABLE1).getDataStore().getRowParm(selRow);
		// System.out.println("=======orderP======="+orderP);

		// this.messageBox("================11111=====================");
		if (orderP.getValue("ORDER_CODE").length() == 0) {
			// 清空医嘱名称
			clearRow("ST", selRow, "ORDER_DESC");
			this.getTTable(TABLE1).setDSValue(selRow);
		}
		//this.messageBox("================22222====================");
		if ("LINK_NO".equals(columnName)) {
			if (Integer.parseInt(node.getValue().toString()) == 0) {
				node.setValue("");
			}
			//this.messageBox("================22222_1====================");
			// 连结号
			int row = this.getTTable(TABLE1).getSelectedRow();
			TParm linkParm = this.getTTable(TABLE1).getDataStore().getRowParm(
					row);
			// 如果为0清空
			linkParm.setData("LINK_NO", node.getValue());
			// this.messageBox("当前选中行数据:"+linkParm);
			if (linkParm.getValue("ORDER_CODE").length() == 0) {
				// 请开立医嘱
				this.messageBox("E0152");
				return true;
			}
			// 集合医嘱不能开连嘱
			if (Integer.parseInt(node.getValue().toString()) > 0
					&& isOrderSet(linkParm)) {
				this.messageBox("集合医嘱不能开连嘱！");
				return true;
			}
			// this.messageBox("================22222_2====================");
			//add by wukai 20160607 satrt
			this.getTTable(TABLE1).setLockCell(row, 6, false);// 频次
			this.getTTable(TABLE1).setLockCell(row, 7, false);// 执行科室
			//add by wukai 20160607 end 
			if ("N".equals(linkParm.getValue("LINKMAIN_FLG"))) {
				// 是否有主项Y就是当时编辑的值N自动给主项
				if (!checkMainLinkItem(linkParm.getInt("LINK_NO"), false, "ST")
						&& linkParm.getInt("LINK_NO") != 0) {
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkFlg = new TParm();
					linkFlg.setData("LINKMAIN_FLG", "Y");
					odiObject.setItem(ds, row, linkFlg);
					this.getTTable(TABLE1).setLockCellRow(row, true);
					this.getTTable(TABLE1).setLockCell(row, 0, false);// 连接主
					this.getTTable(TABLE1).setLockCell(row, 1, false);// 组号
					this.getTTable(TABLE1).setLockCell(row, 2, false);// 医嘱
					this.getTTable(TABLE1).setLockCell(row, 3, false);// 用量
					this.getTTable(TABLE1).setLockCell(row, 5, false);// 用法
					this.getTTable(TABLE1).setLockCell(row, 6, false);// 泵入方式
					this.getTTable(TABLE1).setLockCell(row, 7, false);// 输液速率
					this.getTTable(TABLE1).setLockCell(row, 8, false);// 频率
					this.getTTable(TABLE1).setLockCell(row, 9, false);// 执行科室
					this.getTTable(TABLE1).setLockCell(row, 10, false);// 启用时间
					this.getTTable(TABLE1).setLockCell(row, 11, false);// 医嘱备注
					this.getTTable(TABLE1).setLockCell(row, 13, false);// 抗菌标识
					this.getTTable(TABLE1).setLockCell(row, 14, false);// 续
					this.getTTable(TABLE1).setLockCell(row, 15, false);// 急
					this.getTTable(TABLE1).setLockCell(row, 16, false);// 备
					this.getTTable(TABLE1).setLockCell(row, 17, false);// 给
					this.getTTable(TABLE1).setDSValue(row);
					return false;
				} else {
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkP = getMainLinkOrder(linkParm.getInt("LINK_NO"),
							"ST");
					TParm islinkP = new TParm();
					islinkP.setData("FREQ_CODE", linkP.getData("FREQ_CODE"));
					islinkP.setData("ROUTE_CODE", linkP.getData("ROUTE_CODE"));
					if (linkParm.getInt("LINK_NO") != 0) {
						islinkP.setData("EXEC_DEPT_CODE", linkP
								.getData("EXEC_DEPT_CODE"));
						this.getTTable(TABLE1).setLockCellRow(row, true);
						this.getTTable(TABLE1).setLockCell(row, 0, false);// 连接主
						this.getTTable(TABLE1).setLockCell(row, 1, false);// 组号
						this.getTTable(TABLE1).setLockCell(row, 2, false);// 医嘱
						this.getTTable(TABLE1).setLockCell(row, 3, false);// 用量
						//add by wukai 20160607 satrt
						this.getTTable(TABLE1).setLockCell(row, 6, false);// 泵入方式
						this.getTTable(TABLE1).setLockCell(row, 7, false);// 输液速率
						//add by wukai 20160607 end
						this.getTTable(TABLE1).setLockCell(row, 11, false);// 医嘱备注
						this.getTTable(TABLE1).setLockCell(row, 13, false);// 抗菌标识
						this.getTTable(TABLE1).setLockCell(row, 14, false);// 续
						this.getTTable(TABLE1).setLockCell(row, 15, false);// 急
						this.getTTable(TABLE1).setLockCell(row, 16, false);// 备
						this.getTTable(TABLE1).setLockCell(row, 17, false);// 给
					} else {
						// 执行科室重新赋值
						islinkP.setData("EXEC_DEPT_CODE", this
								.getExeDeptCodeST(linkParm
										.getValue("ORDER_CODE"), row, TABLE1,
										""));
						islinkP.setData("ROUTE_CODE", getMediQty(new TParm(),
								linkParm).getValue("ROUTE_CODE", 0));
						this.getTTable(TABLE1).setLockCellRow(row, true);
						this.getTTable(TABLE1).setLockCell(row, 0, false);// 连接主
						this.getTTable(TABLE1).setLockCell(row, 1, false);// 组号
						this.getTTable(TABLE1).setLockCell(row, 2, false);// 医嘱
						this.getTTable(TABLE1).setLockCell(row, 3, false);// 用量
						this.getTTable(TABLE1).setLockCell(row, 5, false);// 用法
						//add by wukai 20160607 satrt
						this.getTTable(TABLE1).setLockCell(row, 6, false);
						this.getTTable(TABLE1).setLockCell(row, 7, false);
						//add by wukai 20160607 end
						this.getTTable(TABLE1).setLockCell(row, 8, false);// 频次
						this.getTTable(TABLE1).setLockCell(row, 9, false);// 执行科室
						this.getTTable(TABLE1).setLockCell(row, 10, false);// 启用时间
						this.getTTable(TABLE1).setLockCell(row, 11, false);// 医嘱备注
						this.getTTable(TABLE1).setLockCell(row, 13, false);// 抗菌标识
						this.getTTable(TABLE1).setLockCell(row, 14, false);// 续
						this.getTTable(TABLE1).setLockCell(row, 15, false);// 急
						this.getTTable(TABLE1).setLockCell(row, 16, false);// 备
						this.getTTable(TABLE1).setLockCell(row, 17, false);// 给
					}
					odiObject.setItem(ds, row, islinkP);
					this.getTTable(TABLE1).setDSValue(row);
					return false;
				}
			} else {
				this.getTTable(TABLE1).setLockCellRow(row, true);
				this.getTTable(TABLE1).setLockCell(row, 0, false);// 连接主
				this.getTTable(TABLE1).setLockCell(row, 1, false);// 组号
				this.getTTable(TABLE1).setLockCell(row, 2, false);// 医嘱
				this.getTTable(TABLE1).setLockCell(row, 3, false);// 用量
				this.getTTable(TABLE1).setLockCell(row, 5, false);// 频次
				this.getTTable(TABLE1).setLockCell(row, 6, false);// 泵入方式
				this.getTTable(TABLE1).setLockCell(row, 7, false);// 输液速率
				this.getTTable(TABLE1).setLockCell(row, 8, false);// 频次
				this.getTTable(TABLE1).setLockCell(row, 9, false);// 执行科室
				this.getTTable(TABLE1).setLockCell(row, 10, false);// 启用时间
				this.getTTable(TABLE1).setLockCell(row, 11, false);// 医嘱备注
				this.getTTable(TABLE1).setLockCell(row, 13, false);// 抗菌标识
				this.getTTable(TABLE1).setLockCell(row, 14, false);// 续
				this.getTTable(TABLE1).setLockCell(row, 15, false);// 急
				this.getTTable(TABLE1).setLockCell(row, 16, false);// 备
				this.getTTable(TABLE1).setLockCell(row, 17, false);// 给
				// 是否已经有主项如果已经有提示并去拿最大连结号
				if (checkMainLinkItem(linkParm.getInt("LINK_NO"), true, "ST")
						&& linkParm.getInt("LINK_NO") != 0) {
					// 此连结号已经有主项！
					this.messageBox("E0156");
					// int linkNo = getMaxLinkNo("ST");
					// this.messageBox_(linkNo);
					// TDS ds = odiObject.getDS("ODI_ORDER");
					// TParm linkFlg = new TParm();
					// linkFlg.setData("LINK_NO",linkNo);
					// odiObject.setItem(ds, row, linkFlg);
					// this.getTTable(TABLE1).setDSValue(row);
					return true;
				}
			}
		}
		// this.messageBox("===============33333333====================");
		// $$================add by lx 2012/02/28 加入医嘱校验
		// START========================$$//
		if (columnName.equals("ROUTE_CODE") || columnName.equals("FREQ_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			// this.messageBox("CAT1_TYPE===="+parm.getValue("CAT1_TYPE"));
			if (parm.getValue("CAT1_TYPE").equals("RIS")
					|| parm.getValue("CAT1_TYPE").equals("LIS")) {

				this.messageBox("检验检查项不可修改！");
				return true;

			}

		}
		// $$================add by lx 2012/02/28
		// 加入医嘱校验END========================$$//
		// this.messageBox("===============444444444====================");

		if (columnName.equals("MEDI_QTY") || columnName.equals("FREQ_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			parm.setData("#NEW#", true);
			// this.messageBox("当前选中行数据:"+parm);
			if (parm.getValue("ORDER_CODE").length() == 0) {
				// 请录入医嘱！
				// ============xueyf modify 20120217 start
				if (Float.valueOf(parm.getValue("MEDI_QTY")) > 0) {
					this.messageBox("E0157");
				}
				// ============xueyf modify 20120217 stop
				return true;
			}
			if (parm.getValue("CAT1_TYPE").equals("PHA")) {
				// 首日量计算
				if (columnName.equals("MEDI_QTY")) {
					parm.setData("MEDI_QTY", node.getValue());
				}
				if (columnName.equals("FREQ_CODE")) {
					// 判断频次是否可以在临时使用
					if (!OrderUtil.getInstance().isSTFreq(
							node.getValue().toString())) {
						// 不是临时用药频次！
						this.messageBox("E0158");
						return true;
					}
					// 是否是连结医嘱
					if (this.isLinkOrder(parm)) {
						int linkNo = parm.getInt("LINK_NO");
						TDS ds = odiObject.getDS("ODI_ORDER");
						String buff = ds.PRIMARY;
						int newRow[] = ds.getNewRows(buff);
						for (int i : newRow) {
							TParm linkParm = ds.getRowParm(i, buff);
							if (!ds.isActive(i, buff))
								continue;
							if (i == node.getRow())
								continue;
							if (linkParm.getInt("LINK_NO") == linkNo) {
								linkParm.setData("FREQ_CODE", node.getValue());
								linkParm.setData("#NEW#", true);
								TParm temp = this.getTempStartQty(linkParm);
								if (temp.getErrCode() < 0) {
									// 抗生素设置
									if (temp.getErrCode() == -2) {
										if (messageBox(
												"提示信息 Tips",
												"管制药品用量超过标准是否按照此用量设置? \n Qty Overproof",
												this.YES_NO_OPTION) != 0)
											return true;
										else {
											temp.setData("FREQ_CODE", node
													.getValue());
											odiObject
											.setItem(ds, i, temp, buff);
											this.getTTable(TABLE1)
											.setDSValue(i);
											return false;
										}
									}
									this.messageBox(temp.getErrText());
									return true;
								}
								temp.setData("FREQ_CODE", node.getValue());
								odiObject.setItem(ds, i, temp, buff);
								this.getTTable(TABLE1).setDSValue(i);
							}
						}
					}
					parm.setData("FREQ_CODE", node.getValue());
				}
				TParm action = this.getTempStartQty(parm);
				action.setData("LINK_NO", parm.getData("LINK_NO"));
				if (action.getErrCode() < 0) {
					// 抗生素设置
					if (action.getErrCode() == -2) {
						if (messageBox("提示信息 Tips",
								"管制药品用量超过标准是否按照此用量设置? \n Qty Overproof",
								this.YES_NO_OPTION) != 0)
							return true;
					} else {// shibl 20130123 modify 总用量未回传
						this.messageBox(action.getErrText());
						return true;
					}
				}
				// 赋值
				odiObject.setItem(odiObject.getDS("ODI_ORDER"), node.getRow(),
						action);
				// 赋值接受
				this.getTTable(TABLE1).setDSValue(node.getRow());
				return false;
			}
			// this.messageBox("===============5555555555===================");

			// 非药品
			if (!parm.getValue("CAT1_TYPE").equals("PHA")) {
				if (columnName.equals("MEDI_QTY")) {
					if (parm.getValue("CAT1_TYPE").equals("RIS")
							|| parm.getValue("CAT1_TYPE").equals("LIS")) {
						// 非药品不可以修改用量
						this.messageBox("E0159");
						return true;
					} else {
						if (this.isOrderSet(parm)) {
							parm.setData("MEDI_QTY", node.getValue());
							TParm actions = this.getTempStartQty(parm);
							actions.setData("LINK_NO", parm.getData("LINK_NO"));
							// this.messageBox_(actions);
							// 赋值
							odiObject.setItem(odiObject.getDS("ODI_ORDER"),
									node.getRow(), actions);
							// 赋值接受
							this.getTTable(TABLE1).setDSValue(node.getRow());
							int groupNo = parm.getInt("ORDERSET_GROUP_NO");
							TDS ds = odiObject.getDS("ODI_ORDER");
							String buff = ds.isFilter() ? ds.FILTER
									: ds.PRIMARY;
							int newRow[] = ds.getNewRows(buff);
							for (int i : newRow) {
								TParm linkParm = ds.getRowParm(i, buff);
								if (!ds.isActive(i, buff))
									continue;
								// 找到过滤缓冲区中此医嘱的唯一ID
								int filterId = (Integer) ds.getItemData(i,
										"#ID#", buff);
								// 找到过主冲区中此医嘱的唯一ID
								int primaryId = (Integer) ds.getItemData(node
										.getRow(), "#ID#", ds.PRIMARY);
								if (filterId == primaryId)
									continue;
								if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
									linkParm.setData("MEDI_QTY", node
											.getValue());
									odiObject.setItem(ds, i, linkParm, buff);
									TParm action = this
											.getTempStartQty(linkParm);
									action.setData("LINK_NO", linkParm
											.getData("LINK_NO"));
									// this.messageBox_(action);
									// 赋值
									odiObject.setItem(ds, i, action, buff);
									this.getTTable(TABLE1).setDSValue(i);
								}
							}
						} else {
							// 处置和治疗计划
							// 首日量计算
							if (columnName.equals("MEDI_QTY")) {
								parm.setData("MEDI_QTY", node.getValue());
							}
							if (columnName.equals("FREQ_CODE")) {
								// 判断频次是否可以在临时使用
								if (!OrderUtil.getInstance().isSTFreq(
										node.getValue().toString())) {
									// 不是临时用药频次！
									this.messageBox("E0158");
									return true;
								}
								// 是否是连结医嘱
								if (this.isLinkOrder(parm)) {
									int linkNo = parm.getInt("LINK_NO");
									TDS ds = odiObject.getDS("ODI_ORDER");
									String buff = ds.PRIMARY;
									int newRow[] = ds.getNewRows(buff);
									for (int i : newRow) {
										TParm linkParm = ds.getRowParm(i, buff);
										if (!ds.isActive(i, buff))
											continue;
										if (i == node.getRow())
											continue;
										if (linkParm.getInt("LINK_NO") == linkNo) {
											linkParm.setData("FREQ_CODE", node
													.getValue());
											linkParm.setData("#NEW#", true);
											TParm temp = this
													.getTempStartQty(linkParm);
											if (temp.getErrCode() < 0) {
												// 抗生素设置
												if (temp.getErrCode() == -2) {
													if (messageBox(
															"提示信息 Tips",
															"管制药品用量超过标准是否按照此用量设置? \n Qty Overproof",
															this.YES_NO_OPTION) != 0)
														return true;
													else {
														temp
														.setData(
																"FREQ_CODE",
																node
																.getValue());
														odiObject.setItem(ds,
																i, temp, buff);
														this.getTTable(TABLE1)
														.setDSValue(i);
														return false;
													}
												}
												this.messageBox(temp
														.getErrText());
												return true;
											}
											temp.setData("FREQ_CODE", node
													.getValue());
											odiObject
											.setItem(ds, i, temp, buff);
											this.getTTable(TABLE1)
											.setDSValue(i);
										}
									}
								}
								parm.setData("FREQ_CODE", node.getValue());
							}
							TParm action = this.getTempStartQty(parm);
							action.setData("LINK_NO", parm.getData("LINK_NO"));
							if (action.getErrCode() < 0) {
								// 抗生素设置
								if (action.getErrCode() == -2) {
									if (messageBox(
											"提示信息 Tips",
											"管制药品用量超过标准是否按照此用量设置? \n Qty Overproof",
											this.YES_NO_OPTION) != 0)
										return true;
									else
										return false;
								}
								this.messageBox(action.getErrText());
								return true;
							}
							// 赋值
							odiObject.setItem(odiObject.getDS("ODI_ORDER"),
									node.getRow(), action);
							// 赋值接受
							this.getTTable(TABLE1).setDSValue(node.getRow());
							return false;
						}
					}
				}
			}

			// this.messageBox("===============66666666===================");
			// 集合医嘱频次修改(temperr)
			if (columnName.equals("FREQ_CODE")) {
				if (this.isLinkOrder(parm)) {
					int linkNo = parm.getInt("LINK_NO");
					TDS ds = odiObject.getDS("ODI_ORDER");
					String buff = ds.PRIMARY;
					int newRow[] = ds.getNewRows(buff);
					// 是否是主项
					if (parm.getBoolean("LINKMAIN_FLG")) {
						for (int i : newRow) {
							TParm temp = ds.getRowParm(i, buff);
							if (!ds.isActive(i, buff))
								continue;
							if (temp.getInt("LINK_NO") == linkNo) {
								temp.setData("FREQ_CODE", node.getValue());
								odiObject.setItem(ds, i, temp, buff);
								this.getTTable(TABLE1).setDSValue(i);
							}
						}
					} else {
						parm.setData("FREQ_CODE", node.getValue());
						odiObject.setItem(ds, node.getRow(), parm, buff);
						this.getTTable(TABLE1).setDSValue(node.getRow());
					}
				}
				if (this.isOrderSet(parm)) {
					int groupNo = parm.getInt("ORDERSET_GROUP_NO");
					TDS ds = odiObject.getDS("ODI_ORDER");
					String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
					int newRow[] = ds.getNewRows(buff);
					for (int i : newRow) {
						TParm linkParm = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						// 找到过滤缓冲区中此医嘱的唯一ID
						int filterId = (Integer) ds
								.getItemData(i, "#ID#", buff);
						// 找到过主冲区中此医嘱的唯一ID
						int primaryId = (Integer) ds.getItemData(node.getRow(),
								"#ID#", ds.PRIMARY);
						if (filterId == primaryId)
							continue;
						if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
							linkParm.setData("FREQ_CODE", node.getValue());
							odiObject.setItem(ds, i, linkParm, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				}
			}
			return false;
		}
		// this.messageBox("===============77777777===================");
		// 连接医嘱用法修改
		// =============================================================================
		if (columnName.equals("ORDER_DR_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("ORDER_DR_CODE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				} else {
					parm.setData("ORDER_DR_CODE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			// 是否是集合医嘱
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// 找到过滤缓冲区中此医嘱的唯一ID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// 找到过主冲区中此医嘱的唯一ID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("ORDER_DR_CODE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
		}

		if (columnName.equals("ORDER_DR_CODE")) {

			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("ORDER_DR_CODE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				} else {
					parm.setData("ORDER_DR_CODE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			// 是否是集合医嘱
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// 找到过滤缓冲区中此医嘱的唯一ID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// 找到过主冲区中此医嘱的唯一ID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("ORDER_DR_CODE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
		}
		// ===========开立时间 add by huangjw
		if (columnName.equals("ORDER_DATE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("ORDER_DATE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				} else {
					parm.setData("ORDER_DATE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			// 是否是集合医嘱
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// 找到过滤缓冲区中此医嘱的唯一ID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// 找到过主冲区中此医嘱的唯一ID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("ORDER_DATE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
		}
		// ===========确认时间 add by huangjw
		if (columnName.equals("NS_CHECK_DATE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("NS_CHECK_DATE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				} else {
					parm.setData("NS_CHECK_DATE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			// 是否是集合医嘱
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// 找到过滤缓冲区中此医嘱的唯一ID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// 找到过主冲区中此医嘱的唯一ID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("NS_CHECK_DATE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
		}

		// ====================================================================
		if (columnName.equals("ROUTE_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());

			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// 是否是主项
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("ROUTE_CODE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				} else {
					parm.setData("ROUTE_CODE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			// 是否是集合医嘱
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// 找到过滤缓冲区中此医嘱的唯一ID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// 找到过主冲区中此医嘱的唯一ID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("ROUTE_CODE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
		}

		// this.messageBox("===============888888888===================");
		// EXEC_DEPT_CODE
		// 集合医嘱执行科室修改(temperr)
		if (columnName.equals("EXEC_DEPT_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// 是否是主项
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("EXEC_DEPT_CODE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				} else {
					parm.setData("EXEC_DEPT_CODE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// 找到过滤缓冲区中此医嘱的唯一ID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// 找到过主冲区中此医嘱的唯一ID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("EXEC_DEPT_CODE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
		}

		//INFLUTION_RATE
		//速率
		//add by chenhj
		if (columnName.equals("INFLUTION_RATE")){
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// 是否是主项
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("INFLUTION_RATE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				} else {
					parm.setData("INFLUTION_RATE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			// 是否是集合医嘱
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// 找到过滤缓冲区中此医嘱的唯一ID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// 找到过主冲区中此医嘱的唯一ID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("INFLUTION_RATE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
		}


		// this.messageBox("===============999999===================");

		// 启用时间不能超过首餐时间的判断
		if (columnName.equals("EFF_DATEDAY")) {
			// System.out.println("===EFF_DATEDAY 修改启用日期  ===");

			if (!DateTool.checkDate(node.getValue().toString(),
					"yyyy/MM/dd HH:mm:ss")) {
				// 时间格式不正确！
				this.messageBox("E0160");
				return true;
			}

			// $$================add by lx
			// 2012/02/29临时医嘱启用时间应不小于住院日期=================================$$//
			long leffDate = strToDate(node.getValue().toString(),
					"yyyy/MM/dd HH:mm:ss").getTime();
			if (leffDate < this.getAdmDate().getTime()) {
				this.messageBox("启用时间不能小于入院日期！");
				return true;
			}
			// $$================add by lx
			// 2012/02/29临时医嘱启用时间应不小于住院日期=================================$$//
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());

			// ==============add by lx
			// 2012/04/17连组自动改时间start========================$$//
			// 是否是连接医嘱
			if (this.isLinkOrder(parm)) {
				// System.out.println("=====isLinkOrder======");
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// 是否是主项
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("EFF_DATE", node.getValue());
							temp.setData("EFF_DATEDAY", node.getValue());
							// -----------------------------shibl 20120606
							// modify start
							// 集合医嘱
							if (this.isOrderSet(temp)) {
								int groupNo = temp.getInt("ORDERSET_GROUP_NO");
								Object objT = odiObject
										.getAttribute(odiObject.OID_DSPN_TIME);
								TDS dsT = odiObject.getDS("ODI_ORDER");
								String buffT = dsT.isFilter() ? ds.FILTER
										: ds.PRIMARY;
								// 新加的数据
								int newRowT[] = dsT.getNewRows(buffT);
								for (int j : newRowT) {
									TParm tempT = dsT.getRowParm(j, buffT);
									if (!dsT.isActive(j, buffT))
										continue;
									if (tempT.getInt("ORDERSET_GROUP_NO") == groupNo) {
										tempT.setData("EFF_DATE", node
												.getValue());
										tempT.setData("EFF_DATEDAY", node
												.getValue());
										odiObject.setItem(dsT, j, tempT, buffT);
										this.getTTable(TABLE1).setDSValue(i);
									}
								}
							} else {
								odiObject.setItem(ds, i, temp, buff);
								this.getTTable(TABLE1).setDSValue(i);
							}
							// -----------------------------shibl 20120606
							// modify end
						}
					}
				} else {
					parm.setData("EFF_DATE", node.getValue());
					parm.setData("EFF_DATEDAY", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			// ==============add by lx
			// 2012/04/17连组自动改时间end========================$$//
			if (this.isOrderSet(parm)) {
				// this.messageBox("==isOrderSet111111==");
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// 找到过滤缓冲区中此医嘱的唯一ID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// 找到过主冲区中此医嘱的唯一ID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						// System.out.println("==============linkParmOld==================="+linkParm);
						linkParm.setData("EFF_DATE", node.getValue());
						// System.out.println("==============EFF_DATE==================="+node.getValue());
						// System.out.println("==============linkParmNew==================="+linkParm);
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
			node.getTable().getDataStore().setItem(node.getRow(),
					"EFF_DATEDAY", node.getValue().toString());
		}
		if (columnName.equals("DR_NOTE")) {// shibl 20130205 add
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			boolean nsCheckFlg = odiObject
					.getAttributeBoolean("INW_NS_CHECK_FLG");
			if (checkOrderNSCheck(parm, nsCheckFlg)) {
				this.messageBox("医嘱已经展开不可以修改医嘱备注");
				return true;
			}
		}
		return false;
	}












	/**
	 * 判断医嘱是否已经审核
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public boolean checkOrderNSCheck(TParm parm, boolean ncCheckFlg) {
		boolean falg = false;
		if (ncCheckFlg) {
			TParm result = new TParm(this.getDBTool().select(
					"SELECT TEMPORARY_FLG FROM ODI_ORDER WHERE CASE_NO='"
							+ parm.getValue("CASE_NO") + "' AND ORDER_NO='"
							+ parm.getValue("ORDER_NO") + "' AND ORDER_SEQ='"
							+ parm.getValue("ORDER_SEQ") + "'"));
			if (result.getErrCode() < 0)
				falg = true;
			if (result.getValue("TEMPORARY_FLG", 0).equals("Y"))
				falg = true;
		} else {
			String startDttm = StringTool.getString(parm
					.getTimestamp("START_DTTM"), "yyyyMMddHHmmss");
			// System.out.println("startDttm"+startDttm);
			TParm resultExe = new TParm(this.getDBTool().select(
					"SELECT NS_EXEC_DATE FROM ODI_DSPNM WHERE CASE_NO='"
							+ parm.getValue("CASE_NO") + "' AND ORDER_NO='"
							+ parm.getValue("ORDER_NO") + "' AND ORDER_SEQ='"
							+ parm.getValue("ORDER_SEQ") + "' AND START_DTTM='"
							+ startDttm + "'"));
			if (resultExe.getErrCode() < 0)
				falg = true;
			if (resultExe.getTimestamp("NS_EXEC_DATE", 0) != null)
				falg = true;
		}
		return falg;
	}

	/**
	 * 计算临时首日量
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getTempStartQty(TParm parm) {

		// System.out.println("=====getTempStartQty parm====="+parm);
		TParm result = new TParm();
		if (parm.getBoolean("#NEW#")) {
			// 累用量
			parm.setData("ACUMDSPN_QTY", 0);
			// 累计开药量
			parm.setData("ACUMMEDI_QTY", 0);
		}
		String effDate = StringTool.getString(parm.getTimestamp("EFF_DATE"),
				"yyyyMMddHHmmss");
		// 拿到配药起日和迄日
		// List dispenseDttm =
		// TotQtyTool.getInstance().getDispenseDttmArrange(effDate);
		List dispenseDttm = TotQtyTool.getInstance().getNextDispenseDttm(
				parm.getTimestamp("EFF_DATE"));
		// System.out.println("===dispenseDttm==="+dispenseDttm);
		if (StringUtil.isNullList(dispenseDttm)) {
			result.setErrCode(-1);
			// 参数有错误！
			result.setErrText("E0024");
			return result;
		}
		// this.messageBox("配药时间起日:"+dispenseDttm.get(0)+"配药时间迄日:"+dispenseDttm.get(1));
		// 计算首日量
		// this.messageBox_(parm);
		TParm selLevelParm = new TParm();
		selLevelParm.setData("CASE_NO", this.caseNo);
		// System.out.println(""+this.caseNo);
		// =============pangben modify 20110512 start 添加参数
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			selLevelParm.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop

		// System.out.println("==selLevelParm=="+selLevelParm);
		TParm selLevel = ADMInpTool.getInstance().selectall(selLevelParm);
		// System.out.println("selLevel==="+selLevel+"====="+selLevel.getValue("SERVICE_LEVEL",
		// 0));
		String level = selLevel.getValue("SERVICE_LEVEL", 0);
		parm.setData("RX_KIND", "ST");
		List startQty = TotQtyTool.getInstance().getOdiStQty(effDate,
				parm.getValue("DC_DATE"), dispenseDttm.get(0).toString(),
				dispenseDttm.get(1).toString(), parm, level);
		// this.messageBox_(startQty);
		// System.out.println(""+startQty);
		// 首餐时间 START_DTTM
		List startDate = (List) startQty.get(0);
		// System.out.println("======startDate====="+startDate);
		// 其他必要参数//order表的LASTDSPN_QTY ORDER_LASTDSPN_QTY
		// order表的ACUMDSPN_QTY ORDER_ACUMDSPN_QTY
		// order表的ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
		// M表的dispenseQty M_DISPENSE_QTY
		// M表的dispenseUnit M_DISPENSE_UNIT
		// M表的dosageQty M_DOSAGE_QTY
		// M表的dosageUnit M_DOSAGE_UNIT
		// D表的MediQty D_MEDI_QTY
		// D表的MediUnit D_MEDI_UNIT
		// D表的dosageQty D_DOSAGE_QTY
		// D表的dosageUnit D_DOSAGE_UNIT
		Map otherData = (Map) startQty.get(1);
		// System.out.println("===otherData==="+otherData);
		if (StringUtil.isNullList(startDate)
				&& (otherData == null || otherData.isEmpty())) {
			result.setErrCode(-1);
			result.setErrText("E0024");
			return result;
		}

		// 首餐时间表
		result.setData("START_DTTM_LIST", startDate);
		// 首餐时间
		// this.messageBox_(startDate.get(0).toString()+":"+startDate.get(0).getClass());
		result.setData("START_DTTM", StringTool.getTimestamp(startDate.get(0)
				.toString(), "yyyyMMddHHmm"));
		// 首日量
		result.setData("FRST_QTY", otherData.get("ORDER_LASTDSPN_QTY"));
		// 最近配药量
		result.setData("LASTDSPN_QTY", otherData.get("ORDER_LASTDSPN_QTY"));
		// 累用量
		result.setData("ACUMDSPN_QTY", otherData.get("ORDER_ACUMDSPN_QTY"));
		// 累计开药量
		result.setData("ACUMMEDI_QTY", otherData.get("ORDER_ACUMMEDI_QTY"));
		// 发药数量 / 实际退药入库量《盒或是片》
		result.setData("DISPENSE_QTY", otherData.get("M_DISPENSE_QTY"));
		// 总量单位
		result.setData("DISPENSE_UNIT", otherData.get("M_DISPENSE_UNIT"));
		// 配药数量、实际扣库数量
		result.setData("DOSAGE_QTY", otherData.get("M_DOSAGE_QTY"));
		// 配药单位 < 实际扣库量 >
		result.setData("DOSAGE_UNIT", otherData.get("M_DOSAGE_UNIT"));
		// 开药数量
		result.setData("MEDI_QTY", otherData.get("D_MEDI_QTY"));
		// 开药单位
		result.setData("MEDI_UNIT", otherData.get("D_MEDI_UNIT"));
		// 发药数量
		result.setData("DOSAGE_QTY", otherData.get("D_DOSAGE_QTY"));
		// 发药单位、调配单位
		result.setData("DOSAGE_UNIT", otherData.get("D_DOSAGE_UNIT"));
		// 毒麻药是否超量
		if (!OrderUtil.getInstance().checkKssPhaQty(parm)) { // shibl 20130123
			// modify 总用量未回传
			result.setErrCode(-2);
			return result;
		}
		return result;
	}

	/**
	 * 计算出院带药总量
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getOutHospStartQty(TParm parm) {
		TParm result = new TParm();
		if (parm.getBoolean("#NEW#")) {
			// 累用量
			parm.setData("ACUMDSPN_QTY", 0);
			// 累计开药量
			parm.setData("ACUMMEDI_QTY", 0);
		}
		// this.messageBox_("========="+parm);
		// .out.println("===parm==="+parm);
		TParm totParm = TotQtyTool.getInstance().getTotQty(parm);
		// this.messageBox_("---------"+totParm);
		// 盒药注记(库存单位发药)
		if (parm.getBoolean("GIVEBOX_FLG")) {
			// 库存发药单位
			result.setData("DOSAGE_UNIT", totParm.getData("DOSAGE_UNIT"));
			// 换算总量
			result.setData("DOSAGE_QTY", totParm.getData("TOT_QTY"));
			// 配药总量
			result.setData("DISPENSE_QTY", totParm
					.getData("QTY_FOR_STOCK_UNIT"));
			// 配药单位
			result.setData("DISPENSE_UNIT", totParm.getData("STOCK_UNIT"));
		} else {
			// 发药单位
			result.setData("DOSAGE_UNIT", totParm.getData("DOSAGE_UNIT"));
			// 换算总量
			result.setData("DOSAGE_QTY", totParm.getData("QTY"));
			// 配药总量
			result.setData("DISPENSE_QTY", totParm.getData("QTY"));
			// 配药单位
			result.setData("DISPENSE_UNIT", totParm.getData("DOSAGE_UNIT"));
		}
		// 毒麻药是否超量
		if (!OrderUtil.getInstance().checkKssPhaQty(parm)) {// shibl 20130123
			// modify 总用量未回传
			result.setErrCode(-2);
			return result;
		}
		return result;
	}

	/**
	 * 出院带药总量算用量
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getOutHospDosageQty(TParm parm) {
		TParm result = new TParm();
		TParm dosageQtyParm = TotQtyTool.getInstance().getTakeQty(parm);
		// this.messageBox("出院带药总量算用量:"+dosageQtyParm);
		result.setData("MEDI_QTY", dosageQtyParm.getDouble("QTY"));
		return result;
	}

	/**
	 * 长期医嘱修改事件监听
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onChangeTableValueUD(Object obj) {
		// this.messageBox("===========onChangeTableValueUD==================");
		// 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// 拿到table上的parmmap的列名
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		// 判断当前列是否有医嘱
		int selRow = node.getRow();
		TParm orderP = this.getTTable(TABLE2).getDataStore().getRowParm(selRow);
		if (orderP.getValue("ORDER_CODE").length() == 0) {
			// 清空医嘱名称
			clearRow("UD", selRow, "ORDER_DESC");
			this.getTTable(TABLE2).setDSValue(selRow);
		}
		if ("LINK_NO".equals(columnName)) {
			if (Integer.parseInt(node.getValue().toString()) == 0) {
				node.setValue("");
			}
			// 连结号
			int row = this.getTTable(TABLE2).getSelectedRow();
			TParm linkParm = this.getTTable(TABLE2).getDataStore().getRowParm(
					row);
			linkParm.setData("LINK_NO", node.getValue());
			// this.messageBox("当前选中行数据:"+linkParm);
			if (linkParm.getValue("ORDER_CODE").length() == 0) {
				// 请开立医嘱
				this.messageBox("E0152");
				return true;
			}
			// 集合医嘱不能开连嘱
			if (Integer.parseInt(node.getValue().toString()) > 0
					&& (isOrderSet(linkParm) || !linkParm.getValue("CAT1_TYPE")
							.equals("PHA"))) {
				this.messageBox("集合医嘱不能开连嘱！");
				return true;
			}
			// this.messageBox("LINKMAIN_FLG=========================="+linkParm.getValue("LINKMAIN_FLG"));
			if ("N".equals(linkParm.getValue("LINKMAIN_FLG"))) {
				// 是否有主项Y就是当时编辑的值N自动给主项
				if (!checkMainLinkItem(linkParm.getInt("LINK_NO"), false, "UD")
						&& linkParm.getInt("LINK_NO") != 0) {
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkFlg = new TParm();
					linkFlg.setData("LINKMAIN_FLG", "Y");
					// 取消连嘱时清除部分字段的值 yanjing 20140217
					this
					.onClearLinkAnti(orderP.getValue("ORDER_CODE"),
							linkFlg);
					odiObject.setItem(ds, row, linkFlg);
					this.getTTable(TABLE2).setLockCellRow(row, true); // shibl
					// 20130315
					// modify
					this.getTTable(TABLE2).setLockCellRow(row, true);
					this.getTTable(TABLE2).setLockCell(row, 0, false);// 连接主
					this.getTTable(TABLE2).setLockCell(row, 1, false);// 组号
					this.getTTable(TABLE2).setLockCell(row, 2, false);// 医嘱
					this.getTTable(TABLE2).setLockCell(row, 3, false);// 用量
					this.getTTable(TABLE2).setLockCell(row, 5, false);// 频次
					this.getTTable(TABLE2).setLockCell(row, 6, false);// 用法
					//modify by wukai 20160607 start
					this.getTTable(TABLE2).setLockCell(row, 7, false);// 频次
					this.getTTable(TABLE2).setLockCell(row, 8, false);// 用法
					//modify by wukai 20160607 end
					this.getTTable(TABLE2).setLockCell(row, 9, false);// 急
					this.getTTable(TABLE2).setLockCell(row, 10, false);// 备
					this.getTTable(TABLE2).setLockCell(row, 11, false);// 医嘱备注
					this.getTTable(TABLE2).setLockCell(row, 12, false);// 执行科室
					this.getTTable(TABLE2).setLockCell(row, 13, false);// 启用时间
					this.getTTable(TABLE2).setLockCell(row, 14, false);// 停用时间
					this.getTTable(TABLE2).setLockCell(row, 16, false);// 抗菌
					this.getTTable(TABLE2).setDSValue(row);
					return false;
				} else {
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkP = getMainLinkOrder(linkParm.getInt("LINK_NO"),
							"UD");
					TParm islinkP = new TParm();
					islinkP.setData("FREQ_CODE", linkP.getData("FREQ_CODE"));
					islinkP.setData("ROUTE_CODE", linkP.getData("ROUTE_CODE"));
					islinkP.setData("EXEC_DEPT_CODE", linkP
							.getData("EXEC_DEPT_CODE"));
					// 判断主项是否是抗菌药物，yanjing 20140214
					if (!linkP.getValue("ANTIBIOTIC_WAY").equals(null)
							&& !"".equals(linkP.getValue("ANTIBIOTIC_WAY"))) {// 抗菌药物时赋值
						islinkP.setData("DC_DATE", linkP.getData("DC_DATE"));
						islinkP.setData("ANTIBIOTIC_WAY", linkP
								.getData("ANTIBIOTIC_WAY"));
						islinkP.setData("DC_DR_CODE", linkP
								.getData("DC_DR_CODE"));
						islinkP.setData("DC_DEPT_CODE", linkP
								.getData("DC_DEPT_CODE"));// ======20140901
						// YANJING
					}
					if (linkParm.getInt("LINK_NO") != 0) {
						this.getTTable(TABLE2).setLockCellRow(row, true); // shibl
						// 20130315
						// modify
						this.getTTable(TABLE2).setLockCellRow(row, true);
						this.getTTable(TABLE2).setLockCell(row, 0, false);// 连接主
						this.getTTable(TABLE2).setLockCell(row, 1, false);// 组号
						this.getTTable(TABLE2).setLockCell(row, 2, false);// 医嘱
						this.getTTable(TABLE2).setLockCell(row, 3, false);// 用量
						//modify by wukai 20160607 start
						this.getTTable(TABLE2).setLockCell(row, 7, false);// 频次
						this.getTTable(TABLE2).setLockCell(row, 8, false);// 用法
						//modify by wukai 20160607 end
						this.getTTable(TABLE2).setLockCell(row, 9, false);// 急
						this.getTTable(TABLE2).setLockCell(row, 10, false);// 备
						this.getTTable(TABLE2).setLockCell(row, 11, false);// 医嘱备注
						this.getTTable(TABLE2).setLockCell(row, 14, false);// 停用时间
						this.getTTable(TABLE2).setLockCell(row, 16, false);// 抗菌

					} else {
						// 执行科室重新赋值
						islinkP.setData("EXEC_DEPT_CODE", this
								.getExeDeptCodeUD(linkParm
										.getValue("ORDER_CODE"), row, TABLE2,
										""));
						islinkP.setData("ROUTE_CODE", getMediQty(new TParm(),
								linkParm).getValue("ROUTE_CODE", 0));
						// 取消连嘱时清除部分字段的值 yanjing 20140217
						this.onClearLinkAnti(orderP.getValue("ORDER_CODE"),
								islinkP);
						this.getTTable(TABLE2).setLockCellRow(row, true); // shibl
						// 20130315
						// modify
						this.getTTable(TABLE2).setLockCellRow(row, true);
						this.getTTable(TABLE2).setLockCell(row, 0, false);// 连接主
						this.getTTable(TABLE2).setLockCell(row, 1, false);// 组号
						this.getTTable(TABLE2).setLockCell(row, 2, false);// 医嘱
						this.getTTable(TABLE2).setLockCell(row, 3, false);// 用量
						this.getTTable(TABLE2).setLockCell(row, 5, false);// 频次
						this.getTTable(TABLE2).setLockCell(row, 6, false);// 用法
						//modify by wukai 20160607 start
						this.getTTable(TABLE2).setLockCell(row, 7, false);// 频次
						this.getTTable(TABLE2).setLockCell(row, 8, false);// 用法
						//modify by wukai 20160607 end
						this.getTTable(TABLE2).setLockCell(row, 9, false);// 急
						this.getTTable(TABLE2).setLockCell(row, 10, false);// 备
						this.getTTable(TABLE2).setLockCell(row, 11, false);// 医嘱备注
						this.getTTable(TABLE2).setLockCell(row, 12, false);// 执行科室
						this.getTTable(TABLE2).setLockCell(row, 13, false);// 启用时间
						this.getTTable(TABLE2).setLockCell(row, 14, false);// 停用时间
						this.getTTable(TABLE2).setLockCell(row, 16, false);// 抗菌
					}
					odiObject.setItem(ds, row, islinkP);
					this.getTTable(TABLE2).setDSValue(row);
					return false;
				}
			} else {
				this.getTTable(TABLE2).setLockCellRow(row, true); // shibl
				// 20130315
				// modify
				this.getTTable(TABLE2).setLockCellRow(row, true);
				this.getTTable(TABLE2).setLockCell(row, 0, false);// 连接主
				this.getTTable(TABLE2).setLockCell(row, 1, false);// 组号
				this.getTTable(TABLE2).setLockCell(row, 2, false);// 医嘱
				this.getTTable(TABLE2).setLockCell(row, 3, false);// 用量
				this.getTTable(TABLE2).setLockCell(row, 5, false);// 频次
				this.getTTable(TABLE2).setLockCell(row, 6, false);// 用法
				//modify by wukai 20160607 start
				this.getTTable(TABLE2).setLockCell(row, 7, false);// 频次
				this.getTTable(TABLE2).setLockCell(row, 8, false);// 用法
				//modify by wukai 20160607 end
				this.getTTable(TABLE2).setLockCell(row, 9, false);// 急
				this.getTTable(TABLE2).setLockCell(row, 10, false);// 备
				this.getTTable(TABLE2).setLockCell(row, 11, false);// 医嘱备注
				this.getTTable(TABLE2).setLockCell(row, 12, false);// 执行科室
				this.getTTable(TABLE2).setLockCell(row, 14, false);// 停用时间
				this.getTTable(TABLE2).setLockCell(row, 16, false);// 抗菌
				// 是否已经有主项如果已经有提示并去拿最大连结号
				if (checkMainLinkItem(linkParm.getInt("LINK_NO"), true, "UD")
						&& linkParm.getInt("LINK_NO") != 0) {
					this.messageBox("E0156");
					// int linkNo = getMaxLinkNo("UD");
					// TDS ds = odiObject.getDS("ODI_ORDER");
					// TParm linkFlg = new TParm();
					// linkFlg.setData("LINK_NO",linkNo);
					// odiObject.setItem(ds, row, linkFlg);
					// this.getTTable(TABLE2).setDSValue(row);
					// return false;
					return true;
				}
			}
		}
		if (columnName.equals("MEDI_QTY") || columnName.equals("FREQ_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			parm.setData("#NEW#", true);
			// this.messageBox("当前选中行数据:" + parm);
			// 请录入医嘱！
			/**
			 * if (Float.valueOf(parm.getValue("MEDI_QTY")) > 0) {
			 * this.messageBox("E0157"); return true; }
			 **/
			if (parm.getValue("ORDER_CODE").length() == 0) {
				this.messageBox("E0157");
				return true;
			}

			if (columnName.equals("FREQ_CODE")) {
				// this.messageBox("====FREQ_CODE=====");
				// add by lx 长期不能是STAT
				if (isStat((String) node.getValue())) {
					this.messageBox("长期医令不能是立即使用！");
					return true;
				}

			}

			if (parm.getValue("CAT1_TYPE").equals("PHA")) {
				// 首日量计算
				if (columnName.equals("MEDI_QTY"))
					parm.setData("MEDI_QTY", node.getValue());
				if (columnName.equals("FREQ_CODE")) {
					// 是否是连结医嘱
					if (this.isLinkOrder(parm)) {
						int linkNo = parm.getInt("LINK_NO");
						TDS ds = odiObject.getDS("ODI_ORDER");
						String buff = ds.PRIMARY;
						int newRow[] = ds.getNewRows(buff);
						// this.messageBox("==newRow=="+newRow);

						for (int i : newRow) {
							// System.out.println("=====i======"+i);
							TParm linkParm = ds.getRowParm(i, buff);
							if (!ds.isActive(i, buff))
								continue;
							if (i == node.getRow())
								continue;
							if (linkParm.getInt("LINK_NO") == linkNo) {
								linkParm.setData("FREQ_CODE", node.getValue());
								linkParm.setData("#NEW#", true);
								TParm temp = this.getLongStartQty(linkParm);
								if (temp.getErrCode() < 0) {
									// 抗生素设置
									if (temp.getErrCode() == -2) {
										if (messageBox(
												"提示信息 Tips",
												"管制药品用量超过标准是否按照此用量设置? \n Qty Overproof",
												this.YES_NO_OPTION) != 0)
											// return true;
											continue;

										else {
											temp.setData("FREQ_CODE", node
													.getValue());
											odiObject
											.setItem(ds, i, temp, buff);
											this.getTTable(TABLE2)
											.setDSValue(i);
											// return false;
											continue;
										}
									}
									this.messageBox(temp.getErrText());
									// return true;
									continue;
								}
								temp.setData("FREQ_CODE", node.getValue());
								odiObject.setItem(ds, i, temp, buff);
								this.getTTable(TABLE2).setDSValue(i);
							}
						}
					}
					parm.setData("FREQ_CODE", node.getValue());
				}
				// this.messageBox_(parm);
				TParm action = this.getLongStartQty(parm);
				if (action.getErrCode() < 0) {
					// 抗生素设置
					if (action.getErrCode() == -2) {
						if (messageBox("提示信息 Tips",
								"管制药品用量超过标准是否按照此用量设置? \n Qty Overproof",
								this.YES_NO_OPTION) != 0)
							return true;
					} else {// shibl 20130123 modify 总用量未回传
						this.messageBox(action.getErrText());
						return true;
					}
				}
				// 赋值
				odiObject.setItem(odiObject.getDS("ODI_ORDER"), node.getRow(),
						action);
				// 赋值接受
				this.getTTable(TABLE2).setDSValue(node.getRow());
				return false;
			}
			// 非药品
			if (!parm.getValue("CAT1_TYPE").equals("PHA")) {
				if (columnName.equals("MEDI_QTY")) {
					if (parm.getValue("CAT1_TYPE").equals("RIS")
							|| parm.getValue("CAT1_TYPE").equals("LIS")) {
						// 非药品不可以修改用量
						this.messageBox("E0159");
						return true;
					} else {
						if (this.isOrderSet(parm)) {
							int groupNo = parm.getInt("ORDERSET_GROUP_NO");
							TDS ds = odiObject.getDS("ODI_ORDER");
							String buff = ds.isFilter() ? ds.FILTER
									: ds.PRIMARY;
							int newRow[] = ds.getNewRows(buff);
							for (int i : newRow) {
								TParm linkParm = ds.getRowParm(i, buff);
								if (!ds.isActive(i, buff))
									continue;
								// 找到过滤缓冲区中此医嘱的唯一ID
								int filterId = (Integer) ds.getItemData(i,
										"#ID#", buff);
								// 找到过主冲区中此医嘱的唯一ID
								int primaryId = (Integer) ds.getItemData(node
										.getRow(), "#ID#", ds.PRIMARY);
								if (filterId == primaryId)
									continue;
								if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
									linkParm.setData("MEDI_QTY", node
											.getValue());
									odiObject.setItem(ds, i, linkParm, buff);
									this.getTTable(TABLE2).setDSValue(i);
								}
							}
						}
					}
				}
			}
			// 集合医嘱频次修改(temperr)
			if (columnName.equals("FREQ_CODE")) {
				if (this.isLinkOrder(parm)) {
					int linkNo = parm.getInt("LINK_NO");
					TDS ds = odiObject.getDS("ODI_ORDER");
					String buff = ds.PRIMARY;
					int newRow[] = ds.getNewRows(buff);
					// 是否是主项
					if (parm.getBoolean("LINKMAIN_FLG")) {
						for (int i : newRow) {
							TParm temp = ds.getRowParm(i, buff);
							if (!ds.isActive(i, buff))
								continue;
							if (temp.getInt("LINK_NO") == linkNo) {
								temp.setData("FREQ_CODE", node.getValue());
								odiObject.setItem(ds, i, temp, buff);
								this.getTTable(TABLE2).setDSValue(i);
							}
						}
					} else {
						parm.setData("FREQ_CODE", node.getValue());
						odiObject.setItem(ds, node.getRow(), parm, buff);
						this.getTTable(TABLE2).setDSValue(node.getRow());
					}
				}
				if (this.isOrderSet(parm)) {
					int groupNo = parm.getInt("ORDERSET_GROUP_NO");
					TDS ds = odiObject.getDS("ODI_ORDER");
					String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
					int newRow[] = ds.getNewRows(buff);
					for (int i : newRow) {
						TParm linkParm = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						// 找到过滤缓冲区中此医嘱的唯一ID
						int filterId = (Integer) ds
								.getItemData(i, "#ID#", buff);
						// 找到过主冲区中此医嘱的唯一ID
						int primaryId = (Integer) ds.getItemData(node.getRow(),
								"#ID#", ds.PRIMARY);
						if (filterId == primaryId)
							continue;
						if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
							linkParm.setData("FREQ_CODE", node.getValue());
							odiObject.setItem(ds, i, linkParm, buff);
							this.getTTable(TABLE2).setDSValue(i);
						}
					}
				}
			}
			return false;
		}
		// 连接医嘱用法修改
		if (columnName.equals("ROUTE_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			// this.messageBox("===ROUTE_CODE===");

			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// 是否是主项
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("ROUTE_CODE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE2).setDSValue(i);
						}
					}
				} else {
					parm.setData("ROUTE_CODE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE2).setDSValue(node.getRow());
				}
			}
			// 集合医嘱
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// 找到过滤缓冲区中此医嘱的唯一ID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// 找到过主冲区中此医嘱的唯一ID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("ROUTE_CODE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE2).setDSValue(i);
					}
				}
			}
		}
		// EXEC_DEPT_CODE
		// 集合医嘱执行科室修改(temperr)
		if (columnName.equals("EXEC_DEPT_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// 是否是主项
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("EXEC_DEPT_CODE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE2).setDSValue(i);
						}
					}
				} else {
					parm.setData("EXEC_DEPT_CODE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE2).setDSValue(node.getRow());
				}
			}
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// 找到过滤缓冲区中此医嘱的唯一ID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// 找到过主冲区中此医嘱的唯一ID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("EXEC_DEPT_CODE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE2).setDSValue(i);
					}
				}
			}
		}

		//INFLUTION_RATE
		//速率一致
		//add by chenhj
		if (columnName.equals("INFLUTION_RATE")){
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			// 是否是连接医嘱
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// 是否是主项
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("INFLUTION_RATE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE2).setDSValue(i);
						}
					}
				} else {
					parm.setData("INFLUTION_RATE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE2).setDSValue(node.getRow());
				}
			}
		}

		// ====yanjing 20140616 连嘱时修改停用时间保持一致 start
		if (columnName.equals("DC_DATE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			// 是否是连接医嘱
			if (this.isLinkOrder(parm)) {

				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// 是否是主项
				// System.out.println("parm parm is ::"+parm);
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("DC_DATE", node.getValue());
							temp.setData("DC_DR_CODE", Operator.getID());
							temp.setData("DC_DEPT_CODE", Operator.getDept());// ====pangben
							// 2015-1-15
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE2).setDSValue(i);
						}
					}
				} else {
					parm.setData("DC_DATE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE2).setDSValue(node.getRow());
				}
			}
		}
		// ====yanjing 20140616 连嘱时修改停用时间保持一致 end

		// 启用时间不能超过首餐时间的判断
		if (columnName.equals("EFF_DATEDAY")) {
			// this.messageBox("==come in EFF_DATEDAY==");
			if (!DateTool.checkDate(node.getValue().toString(),
					"yyyy/MM/dd HH:mm:ss")) {
				this.messageBox("E0160");
				return true;
			}
			// $$================add by lx
			// 2012/03/29临时医嘱启用时间应不小于住院日期START=================================$$//
			long leffDate = strToDate(node.getValue().toString(),
					"yyyy/MM/dd HH:mm:ss").getTime();
			if (leffDate < this.getAdmDate().getTime()) {
				this.messageBox("启用时间不能小于入院日期！");
				return true;
			}
			// $$================add by lx
			// 2012/03/29临时医嘱启用时间应不小于住院日期END=================================$$//
			// add by lx 启用时间不能大于当前时间
			// if (leffDate > new Date().getTime()) {
			// this.messageBox("启用时间不能大于当前时间！");
			// return true;
			// }

			// 拿到住院摆药时间
			String timeBY = odiObject.getAttribute(odiObject.OID_DSPN_TIME)
					.toString();
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			// this.messageBox("RX_KIND===="+parm.getValue("RX_KIND"));

			if ("UD".equals(parm.getValue("RX_KIND"))) {
				// 启用时间不能超过首餐时间
				// this.messageBox_(node.getValue().toString());
				long effDate = strToDate(node.getValue().toString(),
						"yyyy/MM/dd HH:mm:ss").getTime();
				// this.messageBox_(parm.getData("START_DTTM")+":"+parm.getData("START_DTTM").getClass());
				// System.out.println("---------dsss----------"+parm);
				/**
				 * String sDt = StringTool.getString(
				 * parm.getTimestamp("START_DTTM"), "yyyy/MM/dd HH:mm:ss");
				 * 
				 * long sDttm = strToDate(sDt, "yyyy/MM/dd HH:mm:ss").getTime();
				 **/

				// 拿当日摆药时间点
				Timestamp nowTime = SystemTool.getInstance().getDate();
				String nowTimeStr = StringTool.getString(nowTime,
						"yyyyMMddHHmmss");
				// this.messageBox_("当前时间:" + nowTimeStr);
				String nowTimeBY = StringTool.getString(nowTime, "yyyyMMdd")
						+ timeBY + "00";
				// this.messageBox_("今日摆药时间:" + nowTimeBY);
				// 预开时间首餐时间
				// this.messageBox_(node.getValue().getClass()+":"+node.getValue().toString());
				Timestamp ykTime = StringTool.getTimestamp(node.getValue()
						.toString(), "yyyy/MM/dd HH:mm:ss");
				String ykTimeStr = StringTool.getString(ykTime,
						"yyyyMMddHHmmss");
				// this.messageBox_("预开时间首餐时间:" + ykTimeStr);
				// String tomorrowTime =
				// StringTool.getString(StringTool.rollDate(
				// nowTime, 1), "yyyyMMdd")
				// + "000000";

				String yestodayTime = StringTool.getString(StringTool.rollDate(
						nowTime, -1), "yyyyMMdd HH:mm:ss");
				// System.out.println(yestodayTime+"-----------------"+StringTool.rollDate(nowTime,
				// -1));
				// this.messageBox_("0点时间:"+tomorrowTime);
				// 是药品
				// this.messageBox("cat1_type"+parm.getValue("CAT1_TYPE"));
				if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
					/**
					 * if (effDate > sDttm) { if
					 * ("en".equals(this.getLanguage())) { this.messageBox(
					 * "Opening time cannot exceed the meal time.:" + sDt +
					 * "！"); } else { this.messageBox("启用时间不能超过首餐时间:" + sDt +
					 * "！"); } return true; }
					 **/

					// 当前时间如果大于等于当日摆药时间
					if (nowTimeStr.compareTo(nowTimeBY) > 0) {
						// 预开时间小于当日摆药时间
						if (ykTimeStr.compareTo(nowTimeBY) <= 0) {
							// this.messageBox("===预开时间小于当日摆药时间===");
							if ("en".equals(this.getLanguage())) {
								this
								.messageBox("Opening time cannot put less time:"
										+ StringTool
										.getString(
												StringTool
												.getTimestamp(
														nowTimeBY,
														"yyyyMMddHHmmss"),
												"yyyy/MM/dd HH:mm:ss"));
							} else {
								this.messageBox("医嘱启用时间不能小于今日住院摆药时间:"
										+ StringTool.getString(StringTool
												.getTimestamp(nowTimeBY,
														"yyyyMMddHHmmss"),
												"yyyy/MM/dd HH:mm:ss"));
							}

							return true;
						}
					} else {

						// this.messageBox("===启用时间<前一天摆时间===");
						// 启用时间<前一天摆时间
						if (effDate < strToDate(yestodayTime,
								"yyyyMMdd HH:mm:ss").getTime()) {
							this.messageBox("医嘱启用时间不能小于昨天住院摆药时间:"
									+ StringTool.getString(StringTool
											.getTimestamp(nowTimeBY,
													"yyyyMMddHHmmss"),
											"yyyy/MM/dd HH:mm:ss"));

							return true;
						}
						// return true;
					}

				}
				// else {
				// // this.messageBox("===处置DATE是否已经超过今日0点===");
				// // 处置EFF_DATE是否已经超过今日0点
				// if (ykTimeStr.compareTo(tomorrowTime) > 0) {
				// if ("en".equals(this.getLanguage())) {
				// this.messageBox("Opening time cannot cross:"
				// + StringTool.getString(StringTool
				// .getTimestamp(tomorrowTime,
				// "yyyyMMddHHmmss"),
				// "yyyy/MM/dd HH:mm:ss"));
				// }
				// else {
				// this.messageBox("医嘱启用时间不能跨过今日零点:"
				// + StringTool.getString(StringTool
				// .getTimestamp(tomorrowTime,
				// "yyyyMMddHHmmss"),
				// "yyyy/MM/dd HH:mm:ss"));
				// }
				// return true;
				// }
				// }
			}

			// this.messageBox("是否是连接医嘱"+this.isLinkOrder(parm));
			// 是否是连接医嘱
			if (this.isLinkOrder(parm)) {

				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// 是否是主项
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("EFF_DATE", node.getValue());
							temp.setData("EFF_DATEDAY", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE2).setDSValue(i);
						}
					}
				} else {
					parm.setData("EFF_DATE", node.getValue());
					parm.setData("EFF_DATEDAY", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE2).setDSValue(node.getRow());
				}
			}
			// 集合医嘱
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// 找到过滤缓冲区中此医嘱的唯一ID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// 找到过主冲区中此医嘱的唯一ID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("EFF_DATE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE2).setDSValue(i);
					}
				}
				node.getTable().getDataStore().setItem(node.getRow(),
						"EFF_DATEDAY", node.getValue().toString());
			}
		}
		if (columnName.equals("DR_NOTE")) {// shibl 20130205 add
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			boolean nsCheckFlg = odiObject
					.getAttributeBoolean("INW_NS_CHECK_FLG");
			if (checkOrderNSCheck(parm, nsCheckFlg)) {
				this.messageBox("医嘱已经展开不可以修改医嘱备注");
				return true;
			}
		}
		if (columnName.equals("DC_DATE")) {
			TDS ds = odiObject.getDS("ODI_ORDER");
			String buff = ds.PRIMARY;
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			String value = String.valueOf(node.getValue());
			long dcDate = ((Timestamp) node.getValue()).getTime();
			Timestamp ykTime = parm.getTimestamp("EFF_DATE");
			if (dcDate < ykTime.getTime()) {
				this.messageBox("停用时间不能小于启用时间！");
				return false;
			}
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// 找到过滤缓冲区中此医嘱的唯一ID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// 找到过主冲区中此医嘱的唯一ID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo
							&& !value.equals("") && !value.equals("null")) {
						linkParm.setData("DC_DR_CODE", Operator.getID());
						linkParm.setData("DC_DEPT_CODE", Operator.getDept());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE2).setDSValue(i);
					} else if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo
							&& (value.equals("") || value.equals("null"))) {
						linkParm.setData("DC_DR_CODE", "");
						linkParm.setData("DC_DEPT_CODE", "");
						odiObject.setItem(ds, node.getRow(), parm, buff);
						this.getTTable(TABLE2).setDSValue(node.getRow());
					}
				}
			} else {
				if (!value.equals("") && !value.equals("null")) {
					parm.setData("DC_DR_CODE", Operator.getID());
					parm.setData("DC_DEPT_CODE", Operator.getDept());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE2).setDSValue(node.getRow());
				} else {
					parm.setData("DC_DR_CODE", "");
					parm.setData("DC_DEPT_CODE", "");
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE2).setDSValue(node.getRow());
				}
			}
		}
		return false;
	}

	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param strDate
	 *            String
	 * @return Date
	 */
	public Date strToDate(String strDate, String forMat) {
		SimpleDateFormat formatter = new SimpleDateFormat(forMat);
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * 出院带药
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onChangeTableValueDS(Object obj) {

		// 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// 拿到table上的parmmap的列名
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		// 判断当前列是否有医嘱
		int selRow = node.getRow();
		TParm orderP = this.getTTable(TABLE3).getDataStore().getRowParm(selRow);
		if (orderP.getValue("ORDER_CODE").length() == 0) {
			// 清空医嘱名称
			clearRow("DS", selRow, "ORDER_DESC");
			this.getTTable(TABLE3).setDSValue(selRow);
		}
		if ("LINK_NO".equals(columnName)) {
			if (Integer.parseInt(node.getValue().toString()) == 0) {
				node.setValue("");
			}
			// 连结号
			int row = this.getTTable(TABLE3).getSelectedRow();
			TParm linkParm = this.getTTable(TABLE3).getDataStore().getRowParm(
					row);
			linkParm.setData("LINK_NO", node.getValue());
			// this.messageBox("当前选中行数据:"+linkParm);
			if (linkParm.getValue("ORDER_CODE").length() == 0) {
				// 请开立医嘱
				this.messageBox("E0152");
				return true;
			}
			if ("N".equals(linkParm.getValue("LINKMAIN_FLG"))) {
				// 是否有主项Y就是当时编辑的值N自动给主项
				if (!checkMainLinkItem(linkParm.getInt("LINK_NO"), false, "DS")
						&& linkParm.getInt("LINK_NO") != 0) {
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkFlg = new TParm();
					linkFlg.setData("LINKMAIN_FLG", "Y");
					odiObject.setItem(ds, row, linkFlg);
					this.getTTable(TABLE3).setDSValue(row);
					return false;
				} else {
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkP = getMainLinkOrder(linkParm.getInt("LINK_NO"),
							"DS");
					TParm islinkP = new TParm();
					islinkP.setData("FREQ_CODE", linkP.getData("FREQ_CODE"));
					islinkP.setData("ROUTE_CODE", linkP.getData("ROUTE_CODE"));
					islinkP.setData("EXEC_DEPT_CODE", linkP
							.getData("EXEC_DEPT_CODE"));
					odiObject.setItem(ds, row, islinkP);
					this.getTTable(TABLE3).setDSValue(row);
					return false;
				}
			} else {
				// 是否已经有主项如果已经有提示并去拿最大连结号
				if (checkMainLinkItem(linkParm.getInt("LINK_NO"), true, "DS")
						&& linkParm.getInt("LINK_NO") != 0) {
					this.messageBox("E0156");
					// int linkNo = getMaxLinkNo("DS");
					// TDS ds = odiObject.getDS("ODI_ORDER");
					// TParm linkFlg = new TParm();
					// linkFlg.setData("LINK_NO",linkNo);
					// odiObject.setItem(ds, row, linkFlg);
					// this.getTTable(TABLE3).setDSValue(row);
					return false;
				}
			}
		}
		if (columnName.equals("MEDI_QTY") || columnName.equals("FREQ_CODE")
				|| columnName.equals("TAKE_DAYS")
				|| columnName.equals("DISPENSE_QTY")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			parm.setData("#NEW#", true);
			// this.messageBox("当前选中行数据:"+parm);
			if (parm.getValue("ORDER_CODE").length() == 0) {
				this.messageBox("E0157");
				return true;
			}
			if (parm.getValue("CAT1_TYPE").equals("PHA")) {
				// 首日量计算
				if (columnName.equals("MEDI_QTY"))
					parm.setData("MEDI_QTY", node.getValue());
				// 修改频次
				if (columnName.equals("FREQ_CODE")) {
					// 是否是连结医嘱
					if (this.isLinkOrder(parm)) {
						int linkNo = parm.getInt("LINK_NO");
						TDS ds = odiObject.getDS("ODI_ORDER");
						String buff = ds.PRIMARY;
						int newRow[] = ds.getNewRows(buff);
						for (int i : newRow) {
							TParm linkParm = ds.getRowParm(i, buff);
							if (!ds.isActive(i, buff))
								continue;
							if (i == node.getRow())
								continue;
							if (linkParm.getInt("LINK_NO") == linkNo) {
								linkParm.setData("FREQ_CODE", node.getValue());
								linkParm.setData("#NEW#", true);
								TParm temp = this.getOutHospStartQty(linkParm);
								if (temp.getErrCode() < 0) {
									// 抗生素设置
									if (temp.getErrCode() == -2) {
										if (messageBox(
												"提示信息 Tips",
												"管制药品用量超过标准是否按照此用量设置? \n Qty Overproof",
												this.YES_NO_OPTION) != 0)
											return true;
										else {
											temp.setData("FREQ_CODE", node
													.getValue());
											odiObject
											.setItem(ds, i, temp, buff);
											this.getTTable(TABLE3)
											.setDSValue(i);
											return false;
										}
									}
									this.messageBox(temp.getErrText());
									return true;
								}
								temp.setData("FREQ_CODE", node.getValue());
								odiObject.setItem(ds, i, temp, buff);
								this.getTTable(TABLE3).setDSValue(i);
							}
						}
					}
					parm.setData("FREQ_CODE", node.getValue());
				}
				// 修改天数
				if (columnName.equals("TAKE_DAYS")) {
					// 判断抗生素
					if (parm.getValue("ANTIBIOTIC_CODE").length() != 0) {
						int day = OrderUtil.getInstance().checkAntibioticDay(
								parm.getValue("ANTIBIOTIC_CODE"));
						int cDay = StringTool
								.getInt(node.getValue().toString());
						if (day != -1 && cDay > day) {
							if ("en".equals(this.getLanguage())) {
								this
								.messageBox(parm
										.getValue("ORDER_ENG_DESC")
										+ ":Beyond the default number of antibiotics:"
										+ day + "days！");
							} else {
								this.messageBox(parm.getValue("ORDER_DESC")
										+ ":抗生素超出了预设天数" + day + "天！");
							}
							return true;
						}
					}
					parm.setData("TAKE_DAYS", node.getValue());
				}
				// 修改总量
				if (columnName.equals("DISPENSE_QTY")) {
					parm.setData("DOSAGE_QTY", node.getValue());
					// 调用总量算用量方法
					TParm dosageQtyParm = this.getOutHospDosageQty(parm);
					if (dosageQtyParm.getErrCode() < 0) {
						this.messageBox(dosageQtyParm.getErrText());
						return true;
					}
					dosageQtyParm.setData("DOSAGE_QTY", node.getValue());
					// 赋值
					odiObject.setItem(odiObject.getDS("ODI_ORDER"), node
							.getRow(), dosageQtyParm);
					// 赋值接受
					this.getTTable(TABLE3).setDSValue(node.getRow());
					return false;
				}
				TParm action = this.getOutHospStartQty(parm);
				if (action.getErrCode() < 0) {
					// 抗生素设置
					if (action.getErrCode() == -2) {
						if (messageBox("提示信息 Tips",
								"管制药品用量超过标准是否按照此用量设置? \n Qty Overproof",
								this.YES_NO_OPTION) != 0)
							return true;
					} else { // shibl 20130123 modify 总用量未回传
						this.messageBox(action.getErrText());
						return true;
					}
				}
				// 赋值
				odiObject.setItem(odiObject.getDS("ODI_ORDER"), node.getRow(),
						action);
				// 赋值接受
				this.getTTable(TABLE3).setDSValue(node.getRow());
				return false;
			}
			// 非药品
			if (!parm.getValue("CAT1_TYPE").equals("PHA")) {
				if (columnName.equals("MEDI_QTY")) {
					if (parm.getValue("CAT1_TYPE").equals("RIS")
							|| parm.getValue("CAT1_TYPE").equals("LIS")) {
						this.messageBox("E0159");
						return true;
					} else {
						if (this.isOrderSet(parm)) {
							int groupNo = parm.getInt("ORDERSET_GROUP_NO");
							TDS ds = odiObject.getDS("ODI_ORDER");
							String buff = ds.isFilter() ? ds.FILTER
									: ds.PRIMARY;
							int newRow[] = ds.getNewRows(buff);
							for (int i : newRow) {
								TParm linkParm = ds.getRowParm(i, buff);
								if (!ds.isActive(i, buff))
									continue;
								// 找到过滤缓冲区中此医嘱的唯一ID
								int filterId = (Integer) ds.getItemData(i,
										"#ID#", buff);
								// 找到过主冲区中此医嘱的唯一ID
								int primaryId = (Integer) ds.getItemData(node
										.getRow(), "#ID#", ds.PRIMARY);
								if (filterId == primaryId)
									continue;
								if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
									linkParm.setData("MEDI_QTY", node
											.getValue());
									odiObject.setItem(ds, i, linkParm, buff);
									this.getTTable(TABLE3).setDSValue(i);
								}
							}
						}
					}
				}
			}


			// 集合医嘱频次修改
			if (columnName.equals("FREQ_CODE")) {
				if (this.isOrderSet(parm)) {
					int groupNo = parm.getInt("ORDERSET_GROUP_NO");
					TDS ds = odiObject.getDS("ODI_ORDER");
					String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
					int newRow[] = ds.getNewRows(buff);
					for (int i : newRow) {
						TParm linkParm = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						// 找到过滤缓冲区中此医嘱的唯一ID
						int filterId = (Integer) ds
								.getItemData(i, "#ID#", buff);
						// 找到过主冲区中此医嘱的唯一ID
						int primaryId = (Integer) ds.getItemData(node.getRow(),
								"#ID#", ds.PRIMARY);
						if (filterId == primaryId)
							continue;
						if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
							linkParm.setData("FREQ_CODE", node.getValue());
							odiObject.setItem(ds, i, linkParm, buff);
							this.getTTable(TABLE3).setDSValue(i);
						}
					}
				}
			}
			return false;
		}
		// 连接医嘱用法修改
		if (columnName.equals("ROUTE_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// 是否是主项
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("ROUTE_CODE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE3).setDSValue(i);
						}
					}
				} else {
					parm.setData("ROUTE_CODE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE3).setDSValue(node.getRow());
				}
			}
		}

		// 连接医嘱速率修改
		//add by chenhj 2017.4.10
		if (columnName.equals("INFLUTION_RATE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// 是否是主项
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("INFLUTION_RATE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE3).setDSValue(i);
						}
					}
				} else {
					parm.setData("INFLUTION_RATE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE3).setDSValue(node.getRow());
				}
			}
		}



		if (columnName.equals("DR_NOTE")) {// shibl 20130205 add
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			boolean nsCheckFlg = odiObject
					.getAttributeBoolean("INW_NS_CHECK_FLG");
			if (checkOrderNSCheck(parm, nsCheckFlg)) {
				this.messageBox("医嘱已经展开不可以修改医嘱备注");
				return true;
			}
		}
		return false;
	}

	/**
	 * 出院带药
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onChangeTableValueIG(Object obj) {
		// 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// 拿到table上的parmmap的列名
		String columnName = this.getFactColumnName(TABLE4, node.getColumn());
		String columnArray[] = columnName.split("_");
		TParm columnParm = this.getTTable(TABLE4).getParmValue().getRow(
				node.getRow());
		// 当前编辑行
		int onlyEditRow = getIGEditRowSetId(columnParm.getInt("ROW_ID_"
				+ columnArray[columnArray.length - 1]));
		// 当前编辑行数据
		TParm rowTParm = getIGEditRowSetParm(columnParm.getInt("ROW_ID_"
				+ columnArray[columnArray.length - 1]));
		TDS dsIG = odiObject.getDS("ODI_ORDER");
		// 用量
		if (columnName.contains("MEDI_QTY")) {
			TParm igParm = new TParm();
			igParm.setData("MEDI_QTY", node.getValue());
			rowTParm.setData("MEDI_QTY", node.getValue());
			rowTParm.setData("TAKE_DAYS", this.getValueInt("RF"));
			// 总量计算出院带药
			if ("PHA".equals(rowTParm.getValue("CAT1_TYPE"))
					&& !"Y".equals(rowTParm.getValue("IS_REMARK"))) {
				rowTParm.setData("#NEW#", true);
				TParm igIndParm = getOutHospStartQty(rowTParm);
				// 换算总量
				igParm.setData("DOSAGE_QTY", igIndParm.getData("DOSAGE_QTY"));
				// 配药总量
				igParm.setData("DISPENSE_QTY", igIndParm
						.getData("DISPENSE_QTY"));
			}
			odiObject.setItem(dsIG, onlyEditRow, igParm);
			// 计算总克数
			this.setValue("MEDI_QTYALL", getChnPhaMediQtyAll(dsIG));
		}
		// 特殊煎法
		if (columnName.contains("DCTEXCEP_CODE")) {
			TParm igParm = new TParm();
			igParm.setData("DCTEXCEP_CODE", node.getValue());
			odiObject.setItem(dsIG, onlyEditRow, igParm);
		}

		return false;
	}

	/**
	 * 计算长期首日量
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getLongStartQty(TParm parm) {
		TParm result = new TParm();
		if (parm.getBoolean("#NEW#")) {
			// 累用量
			parm.setData("ACUMDSPN_QTY", 0);
			// 累计开药量
			parm.setData("ACUMMEDI_QTY", 0);
		}
		// this.messageBox_(parm.getData("EFF_DATE"));
		// StringTool.getTimestamp(parm.getValue("EFF_DATE"),"yyyyMMddHHmmss");
		String effDate = StringTool.getString(parm.getTimestamp("EFF_DATE"),
				"yyyyMMddHHmmss");
		// this.messageBox_(effDate);
		// 拿到配药起日和迄日
		// List dispenseDttm =
		// TotQtyTool.getInstance().getDispenseDttmArrange(effDate);
		// System.out.println("==="+parm.getTimestamp("EFF_DATE"));
		List dispenseDttm = TotQtyTool.getInstance().getNextDispenseDttm(
				parm.getTimestamp("EFF_DATE"));
		if (StringUtil.isNullList(dispenseDttm)) {
			result.setErrCode(-1);
			result.setErrText("E0024");
			return result;
		}
		// this.messageBox("配药时间起日:"+dispenseDttm.get(0)+"配药时间迄日:"+dispenseDttm.get(1));
		// 计算首日量
		// this.messageBox("传入参数:"+parm);
		// this.messageBox_(dispenseDttm.get(0).toString());
		// this.messageBox_(dispenseDttm.get(1).toString());
		// this.messageBox_(parm.getValue("DC_DATE"));
		// this.messageBox_(parm);
		TParm selLevelParm = new TParm();
		selLevelParm.setData("CASE_NO", this.caseNo);
		// =============pangben modify 20110512 start 添加参数
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			selLevelParm.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop
		TParm selLevel = ADMInpTool.getInstance().selectall(selLevelParm);
		String level = selLevel.getValue("SERVICE_LEVEL", 0);
		String dcDate = parm.getTimestamp("DC_DATE") == null ? "" : StringTool
				.getString(parm.getTimestamp("DC_DATE"), "yyyyMMddHHmmss");
		List startQty = TotQtyTool.getInstance().getOdiStQty(effDate, dcDate,
				dispenseDttm.get(0).toString(), dispenseDttm.get(1).toString(),
				parm, level);
		// this.messageBox(""+startQty);
		if (StringUtil.isNullList(startQty)) {
			result.setErrCode(-1);
			result.setErrText("E0024");
			return result;
		}
		// 首餐时间 START_DTTM
		List startDate = (List) startQty.get(0);
		// 其他必要参数//order表的LASTDSPN_QTY ORDER_LASTDSPN_QTY
		// order表的ACUMDSPN_QTY ORDER_ACUMDSPN_QTY
		// order表的ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
		// M表的dispenseQty M_DISPENSE_QTY
		// M表的dispenseUnit M_DISPENSE_UNIT
		// M表的dosageQty M_DOSAGE_QTY
		// M表的dosageUnit M_DOSAGE_UNIT
		// D表的MediQty D_MEDI_QTY
		// D表的MediUnit D_MEDI_UNIT
		// D表的dosageQty D_DOSAGE_QTY
		// D表的dosageUnit D_DOSAGE_UNIT
		Map otherData = (Map) startQty.get(1);
		if (otherData == null || otherData.isEmpty()) {
			result.setErrCode(-1);
			result.setErrText("E0024");
			return result;
		}
		// 首餐时间表
		result.setData("START_DTTM_LIST", startDate);

		// this.messageBox("====startDate===="+startDate);
		// this.messageBox_(startDate.size()+""+StringUtil.isNullList(startDate));
		if (!StringUtil.isNullList(startDate)) {
			// this.messageBox_("============"+startDate.get(0)+":"+startDate.get(0).getClass());
			// 首餐时间
			result.setData("START_DTTM", StringTool.getTimestamp(startDate.get(
					0).toString(), "yyyyMMddHHmm"));
			// 首日量
			// this.messageBox_("首日量"+otherData.get("ORDER_LASTDSPN_QTY"));
			result.setData("FRST_QTY", otherData.get("ORDER_LASTDSPN_QTY"));
			// RX_KIND为F
			result.setData("RX_KIND", "F");
			// 最近配药量
			// this.messageBox_("最近配药量"+otherData.get("ORDER_LASTDSPN_QTY"));
			result.setData("LASTDSPN_QTY", otherData.get("ORDER_LASTDSPN_QTY"));
			// 累用量
			// this.messageBox_("ORDER_ACUMDSPN_QTY:"+otherData.get("ORDER_ACUMDSPN_QTY"));
			result.setData("ACUMDSPN_QTY", otherData.get("ORDER_ACUMDSPN_QTY"));
			// 累计开药量
			// this.messageBox_("ORDER_ACUMMEDI_QTY:"+otherData.get("ORDER_ACUMMEDI_QTY"));
			result.setData("ACUMMEDI_QTY", otherData.get("ORDER_ACUMMEDI_QTY"));
			// 发药数量 / 实际退药入库量《盒或是片》
			// this.messageBox_("发药数量"+otherData.get("M_DISPENSE_QTY"));
			result.setData("DISPENSE_QTY", otherData.get("M_DISPENSE_QTY"));
			// 总量单位(发药数量)
			// this.messageBox_("总量单位"+otherData.get("M_DISPENSE_UNIT"));
			result.setData("DISPENSE_UNIT", otherData.get("M_DISPENSE_UNIT"));
			// 配药数量、实际扣库数量
			// this.messageBox_("配药数量:"+otherData.get("M_DOSAGE_QTY"));
			result.setData("DOSAGE_QTY", otherData.get("M_DOSAGE_QTY"));
			// 配药单位 < 实际扣库量 >
			// this.messageBox_("配药单位:"+otherData.get("M_DOSAGE_UNIT"));
			result.setData("DOSAGE_UNIT", otherData.get("M_DOSAGE_UNIT"));
			// 开药数量
			// this.messageBox_("开药数量:"+otherData.get("D_MEDI_QTY"));
			// result.setData("MEDI_QTY",otherData.get("D_MEDI_QTY"));
			// 开药单位
			// this.messageBox_("开药单位"+otherData.get("D_MEDI_UNIT"));
			// result.setData("MEDI_UNIT",otherData.get("D_MEDI_UNIT"));
			// //发药数量
			// this.messageBox_("发药数量"+otherData.get("D_DOSAGE_QTY"));
			// result.setData("DISPENSE_QTY",otherData.get("D_DOSAGE_QTY"));
			// 发药单位、调配单位
			// this.messageBox_("发药单位、调配单位"+otherData.get("D_DOSAGE_UNIT"));
			result.setData("DOSAGE_UNIT", otherData.get("D_DOSAGE_UNIT"));
			// 配药时间起日==最近摆药日期
			// result.setData("LAST_DSPN_DATE",StringTool.getTimestamp(otherData.get("ORDER_LAST_DSPN_DATE").toString(),"yyyyMMddHHmm"));
			// 首日量注记观察者中使用
			result.setData("RX_FLG", "Y");
		} else {
			// 首餐时间
			result.setData("START_DTTM", StringTool.getString(parm
					.getTimestamp("EFF_DATE"), "yyyyMMddHHmmss"));
			// 首日量
			// this.messageBox_("首日量"+otherData.get("ORDER_LASTDSPN_QTY"));
			result.setData("FRST_QTY", 0);
			// RX_KIND为F
			result.setData("RX_KIND", "UD");
			// 最近配药量
			// this.messageBox_("最近配药量"+otherData.get("ORDER_LASTDSPN_QTY"));
			result.setData("LASTDSPN_QTY", 0);
			// 累用量
			// this.messageBox_("ORDER_ACUMDSPN_QTY:"+otherData.get("ORDER_ACUMDSPN_QTY"));
			result.setData("ACUMDSPN_QTY", 0);
			// 累计开药量
			// this.messageBox_("ORDER_ACUMMEDI_QTY:"+otherData.get("ORDER_ACUMMEDI_QTY"));
			result.setData("ACUMMEDI_QTY", 0);
			// 发药数量 / 实际退药入库量《盒或是片》
			// this.messageBox_("发药数量"+otherData.get("M_DISPENSE_QTY"));
			result.setData("DISPENSE_QTY", 0);
			// 总量单位(发药数量)
			// this.messageBox_("总量单位"+otherData.get("M_DISPENSE_UNIT"));
			result.setData("DISPENSE_UNIT", "");
			// 配药数量、实际扣库数量
			// this.messageBox_("配药数量:"+otherData.get("M_DOSAGE_QTY"));
			result.setData("DOSAGE_QTY", 0);
			// 配药单位 < 实际扣库量 >
			// this.messageBox_("配药单位:"+otherData.get("M_DOSAGE_UNIT"));
			result.setData("DOSAGE_UNIT", "");
			// 开药数量
			// this.messageBox_("开药数量:"+otherData.get("D_MEDI_QTY"));
			// result.setData("MEDI_QTY",otherData.get("D_MEDI_QTY"));
			// 开药单位
			// this.messageBox_("开药单位"+otherData.get("D_MEDI_UNIT"));
			// result.setData("MEDI_UNIT",otherData.get("D_MEDI_UNIT"));
			// //发药数量
			// this.messageBox_("发药数量"+otherData.get("D_DOSAGE_QTY"));
			// result.setData("DISPENSE_QTY",otherData.get("D_DOSAGE_QTY"));
			// 发药单位、调配单位
			// this.messageBox_("发药单位、调配单位"+otherData.get("D_DOSAGE_UNIT"));
			result.setData("DOSAGE_UNIT", "");
			// 配药时间起日==最近摆药日期
			// result.setData("LAST_DSPN_DATE",StringTool.getTimestamp(otherData.get("ORDER_LAST_DSPN_DATE").toString(),"yyyyMMddHHmm"));
			// 首日量注记观察者中使用
			result.setData("RX_FLG", "N");
		}
		// else{
		// //配药时间起日==最近摆药日期
		// result.setData("LAST_DSPN_DATE",StringTool.getTimestamp(otherData.get("ORDER_LAST_DSPN_DATE").toString(),"yyyyMMddHHmm"));
		// }
		if (!OrderUtil.getInstance().checkKssPhaQty(parm)) { // shibl 20130123
			// modify
			// 管制药品总用量未回传
			result.setErrCode(-2);
			return result;
		}
		return result;
	}

	/**
	 * 当TABLE创建编辑控件时长期
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentUD(Component com, int row, int column) {
		// 状态条显示
		callFunction("UI|setSysStatus", "");
		// 当前编辑行
		this.rowOnly = row;
		// 拿到列名
		String columnName = this.getFactColumnName(TABLE2, column);
		if (!"ORDER_DESCCHN".equals(columnName))
			return;
		// 20121109 shibl add 重复修改医嘱输入
		TTable table = getTTable(TABLE2);
		int selRow = this.getTTable(TABLE2).getSelectedRow();
		TParm existParm = this.getTTable(TABLE2).getDataStore().getRowParm(
				selRow);
		if (this.isOrderSet(existParm)) {
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			return;
		}
		if ("PHA".equals(existParm.getValue("CAT1_TYPE"))
				&& null != existParm.getValue("ANTIBIOTIC_CODE")
				&& !existParm.getValue("ANTIBIOTIC_CODE").equals("")) {// =====pangben
			// 2014-6-26
			// 修改传回的抗菌药品不可以修改
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			this.messageBox("抗菌药品不可以修改");
			return;
		}
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();

		// if (this.isOrderSet(obj)) {
		// TTextField textFilter = (TTextField) this;
		// textFilter.setEnabled(false);
		// return;
		// }
		// 长期医嘱设置
		TParm parm = new TParm();
		if ("B".equals(this.getValue("KLSTARUD"))) {
			// 此开立状态不可在出院带药中使用
			this.messageBox("E0161");
			return;
		} else {
			parm.setData("ODI_ORDER_TYPE", this.getValue("KLSTARUD"));
		}
		// 设置弹出菜单
		textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}

	/**
	 * 当TABLE创建编辑控件时出院带药
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentDS(Component com, int row, int column) {
		// 状态条显示
		callFunction("UI|setSysStatus", "");
		// 当前编辑行
		this.rowOnly = row;
		// 拿到列名
		String columnName = this.getFactColumnName(TABLE3, column);
		if (!"ORDER_DESCCHN".equals(columnName))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// 出院带药医嘱设置
		TParm parm = new TParm();
		// if("A".equals(this.getValue("KLSTARDS"))||"D".equals(this.getValue("KLSTARDS"))||"E".equals(this.getValue("KLSTARDS"))||"F".equals(this.getValue("KLSTARDS"))){
		// parm.setData("ODI_ORDER_TYPE",this.getValue("KLSTARDS"));
		// }else{
		// this.messageBox("E0161");
		// return;
		// }
		parm.setData("CAT1_TYPE", "PHA");
		// 设置弹出菜单
		textFilter.setPopupMenuParameter("DS", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");

	}

	/**
	 * 当TABLE创建编辑控件时中药饮片
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentIG(Component com, int row, int column) {
		// 状态条显示
		callFunction("UI|setSysStatus", "");
		// 拿到列名
		String columnName = this.getFactColumnName(TABLE4, column);
		String columnArray[] = columnName.split("_");
		TParm columnParm = this.getTTable(TABLE4).getParmValue().getRow(row);
		// 当前编辑行
		this.rowOnly = getIGEditRowSetId(columnParm.getInt("ROW_ID_"
				+ columnArray[columnArray.length - 1]));
		if (!columnName.contains("ORDER_DESC"))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// 中药医嘱设置
		TParm parm = new TParm();
		// 中草药设置
		parm.setData("ODI_ORDER_TYPE", "F");
		textFilter.setPopupMenuParameter("IG", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}

	/**
	 * 得到住院中医当前编辑行
	 * 
	 * @param row
	 *            int
	 * @return int
	 */
	public int getIGEditRowSetId(int row) {
		int rowId = -1;
		TDS ds = odiObject.getDS("ODI_ORDER");
		// ds.showDebug();
		int rowCount = ds.rowCount();
		for (int i = 0; i < rowCount; i++) {
			int rowSet = ds.getItemInt(i, "#ID#");
			if (row == rowSet) {
				rowId = i;
				break;
			}
		}
		return rowId;
	}

	/**
	 * 得到住院中医当前编辑行
	 * 
	 * @param row
	 *            int
	 * @return int
	 */
	public TParm getIGEditRowSetParm(int row) {
		TParm rowParm = new TParm();
		TDS ds = odiObject.getDS("ODI_ORDER");
		// ds.showDebug();
		int rowCount = ds.rowCount();
		for (int i = 0; i < rowCount; i++) {
			int rowSet = ds.getItemInt(i, "#ID#");
			if (row == rowSet) {
				rowParm = ds.getRowParm(i);
				break;
			}
		}
		return rowParm;
	}

	/**
	 * 清空指定行列数据
	 * 
	 * @param row
	 *            int
	 * @param columnName
	 *            String
	 */
	public void clearRow(String tag, int row, String columnName) {
		// 拿到当前选中列号
		if (!"IG".equals(tag)) {
			TParm orderParm = new TParm();
			orderParm.setData(columnName, "");
			odiObject.setItem(odiObject.getDS("ODI_ORDER"), row, orderParm);
		} else {
			TParm rowParm = this.getTTable(TABLE4).getParmValue().getRow(row);
			rowParm.setData(columnName, "");
			this.getTTable(TABLE4).setRowParmValue(row, rowParm);
		}

	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		// this.messageBox("==popReturn==");
		// System.out.println("接受返回值方法"+obj);
		// 判断对象是否为空和是否为TParm类型
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		boolean cdssFlg = true; // 儿童智能用药智能判断 add by huangtt 20151116
		// 类型转换成TParm
		TParm action = (TParm) obj;
		// ===================yanjing 20130908 长期和临时传回标记为
		if ("ST".equals(tag) || "UD".equals(tag)) {
			antiFechFlg = action.getBoolean("FLG");
		}

		// 状态条显示
		if (tag.equals("GMA") || tag.equals("GMC") || tag.equals("GMN")) {
			callFunction("UI|setSysStatus", action.getValue("ID") + ":"
					+ action.getValue("CHN_DESC"));
		} else {
			callFunction("UI|setSysStatus", action.getValue("ORDER_CODE") + " "
					+ action.getValue("ORDER_DESC") + " "
					+ action.getValue("GOODS_DESC") + " "
					+ action.getValue("DESCRIPTION") + " "
					+ action.getValue("SPECIFICATION") + " "
					+ action.getValue("REMARK1_1") + " "
					+ action.getValue("REMARK1_2"));
		}
		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			int selRows = -1;
			TTable tableOid = null;
			if ("ST".equals(tag)) {
				tableOid = this.getTTable(TABLE1);
			}
			if ("UD".equals(tag)) {
				tableOid = this.getTTable(TABLE2);
			}
			if ("DS".equals(tag)) {
				tableOid = this.getTTable(TABLE3);
			}
			if ("IG".equals(tag)) {
				tableOid = this.getTTable(TABLE4);
			}
			if ("PA".equals(tag)) {
				tableOid = this.getTTable(TABLE6);
			}
			if ("GMA".equals(tag) || "GMB".equals(tag) || "GMC".equals(tag)) {
				return;
			}
			// 引用表单
			if (yyList)
				selRows = ((TParm) obj).getInt("EXID_ROW");
			else
				selRows = tableOid.getSelectedRow() < 0 ? ((TParm) obj)
						.getInt("EXID_ROW") : tableOid.getSelectedRow();
						this.clearRow(tag, selRows, "ORDER_DESC");
						tableOid.setDSValue(selRows);
						// 清空选择
						tableOid.clearSelection();
						return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			int selRows = -1;
			TTable tableOid = null;
			if ("ST".equals(tag)) {
				tableOid = this.getTTable(TABLE1);
			}
			if ("UD".equals(tag)) {
				tableOid = this.getTTable(TABLE2);
			}
			if ("DS".equals(tag)) {
				tableOid = this.getTTable(TABLE3);
			}
			if ("IG".equals(tag)) {
				tableOid = this.getTTable(TABLE4);
			}
			if ("PA".equals(tag)) {
				tableOid = this.getTTable(TABLE6);
			}
			if ("GMA".equals(tag) || "GMB".equals(tag) || "GMC".equals(tag)) {
				return;
			}
			// 引用表单
			if (yyList)
				selRows = ((TParm) obj).getInt("EXID_ROW");
			else
				selRows = tableOid.getSelectedRow() < 0 ? ((TParm) obj)
						.getInt("EXID_ROW") : tableOid.getSelectedRow();
						this.clearRow(tag, selRows, "ORDER_DESC");
						tableOid.setDSValue(selRows);
						// 清空选择
						tableOid.clearSelection();
						return;
		}
		// modified by WangQing 20170428 end
		TParm isLock = this.isLockPat();
		if (isLock.getErrCode() == 0) {
			this.messageBox(isLock.getErrText());
			int selRows = -1;
			TTable tableOid = null;
			if ("ST".equals(tag)) {
				tableOid = this.getTTable(TABLE1);
			}
			if ("UD".equals(tag)) {
				tableOid = this.getTTable(TABLE2);
			}
			if ("DS".equals(tag)) {
				tableOid = this.getTTable(TABLE3);
			}
			if ("IG".equals(tag)) {
				tableOid = this.getTTable(TABLE4);
			}
			if ("PA".equals(tag)) {
				tableOid = this.getTTable(TABLE6);
			}
			if ("GMA".equals(tag) || "GMB".equals(tag) || "GMC".equals(tag)) {
				return;
			}
			// 引用表单
			if (yyList)
				selRows = ((TParm) obj).getInt("EXID_ROW");
			else
				selRows = tableOid.getSelectedRow() < 0 ? ((TParm) obj)
						.getInt("EXID_ROW") : tableOid.getSelectedRow();
						this.clearRow(tag, selRows, "ORDER_DESC");
						tableOid.setDSValue(selRows);
						// 清空选择
						tableOid.clearSelection();
						return;
		}
		// System.out.println("action"+action);
		TTable table = null;
		TDS ds = odiObject.getDS("ODI_ORDER");
		boolean falg = true;
		// 选中行
		int selRow = -1;
		// 草药选中列
		int columnIG = -1;
		// 草药选中行
		int rowIG = -1;

		// 临时
		if ("ST".equals(tag)) {
			table = this.getTTable(TABLE1);
			table.acceptText();
			// 引用表单回调
			if (action.getInt("EXID_ROW") < 0)
				return;

			// System.out.println("====yyList====="+yyList);
			// 引用表单
			if (yyList)
				selRow = action.getInt("EXID_ROW");
			else
				selRow = table.getSelectedRow() < 0 ? action.getInt("EXID_ROW")
						: table.getSelectedRow();

				// System.out.println("行号:"+selRow);

				action.setData("RX_KIND", "ST");
				// 是否成功付值
				falg = this.setNewRowOrder(selRow, action, 0);
				// 清空显示的医嘱名称
				if (!falg) {
					// 清空医嘱名称
					clearRow(tag, selRow, "ORDER_DESC");
					table.setDSValue(selRow);
					// 清空选择
					table.clearSelection();
					return;
				}
				table.setDSValue(selRow);
				table.getDataStore().setActive(selRow, true);
				table.setLockCell(table.getSelectedRow(), 20, false);
				// System.out.println("临时医嘱========="+falg);
				// table.getDataStore().showDebug();
		}
		// 长期
		if ("UD".equals(tag)) {
			// this.messageBox("popReturn=====");
			table = this.getTTable(TABLE2);
			table.acceptText();
			// 引用表单回调
			if (action.getInt("EXID_ROW") < 0)
				return;
			// 引用表单
			if (yyList)
				selRow = action.getInt("EXID_ROW");
			else
				selRow = table.getSelectedRow() < 0 ? action.getInt("EXID_ROW")
						: table.getSelectedRow();
				action.setData("RX_KIND", "UD");
				// 是否成功付值
				falg = this.setNewRowOrder(selRow, action, 1);
				// 清空显示的医嘱名称
				if (!falg) {
					// 清空医嘱名称
					clearRow(tag, selRow, "ORDER_DESC");
					table.setDSValue(selRow);
					// 清空选择
					table.clearSelection();
					return;
				}
				table.setDSValue(selRow);
				table.getDataStore().setActive(selRow, true);
		}

		// 出院带药
		if ("DS".equals(tag)) {
			table = this.getTTable(TABLE3);
			table.acceptText();
			// 引用表单回调
			if (action.getInt("EXID_ROW") < 0)
				return;
			cdssFlg = action.getBoolean("CDSS_FLG"); // add by huangtt 20151116
			// 出院代药引用长期医嘱时，不进行智能用药判断
			// 引用表单
			if (yyList)
				selRow = action.getInt("EXID_ROW");
			else
				selRow = table.getSelectedRow() < 0 ? action.getInt("EXID_ROW")
						: table.getSelectedRow();
				action.setData("RX_KIND", "DS");
				// 是否成功付值
				falg = this.setNewRowOrder(selRow, action, 2);
				// 清空显示的医嘱名称
				if (!falg) {
					// 清空医嘱名称
					clearRow(tag, selRow, "ORDER_DESC");
					table.setDSValue(selRow);
					// 清空选择
					table.clearSelection();
					return;
				}
				table.setDSValue(selRow);
				table.getDataStore().setActive(selRow, true);
		}
		// 中药饮片
		if ("IG".equals(tag)) {
			table = this.getTTable(TABLE4);
			selRow = this.rowOnly;
			action.setData("RX_KIND", "IG");
			// 是否成功付值
			falg = this.setNewRowOrder(selRow, action, 3);
			// 清空显示的医嘱名称
			if (!falg) {
				// 清空医嘱名称
				clearRow(tag, table.getSelectedRow(), this.getFactColumnName(
						TABLE4, table.getSelectedColumn()));
				// 清空选择
				table.clearSelection();
				return;
			}
			ds.setActive(selRow, true);
			columnIG = table.getSelectedColumn();
			rowIG = table.getSelectedRow();
		}
		// 院前用药
		if ("PA".equals(tag)) {
			table = this.getTTable(TABLE6);
			table.acceptText();
			// 引用表单回调
			if (action.getInt("EXID_ROW") < 0)
				return;
			// 引用表单
			if (yyList)
				selRow = action.getInt("EXID_ROW");
			else
				selRow = table.getSelectedRow() < 0 ? action.getInt("EXID_ROW")
						: table.getSelectedRow();
				action.setData("RX_KIND", "PA");
				// 是否成功付值
				falg = this.setNewRowOrder(selRow, action, 5);
				// 清空显示的医嘱名称
				if (!falg) {
					// 清空医嘱名称
					clearRow(tag, selRow, "ORDER_DESCCHN");
					table.setDSValue(selRow);
					// 清空选择
					table.clearSelection();
					return;
				}
				table.setDSValue(selRow);
				table.getDataStore().setActive(selRow, true);
		}
		// 成分过敏
		if ("GMA".equals(tag)) {
			table = this.getTTable(GMTABLE);
			table.acceptText();
			selRow = table.getSelectedRow();
			table.getDataStore().setItem(selRow, "DRUGORINGRD_CODE",
					action.getValue("ID"));
			table.getDataStore().setActive(selRow, true);
			table.setDSValue(selRow);
		}
		// 药品过敏
		if ("GMB".equals(tag)) {
			table = this.getTTable(GMTABLE);
			table.acceptText();
			selRow = table.getSelectedRow();
			table.getDataStore().setItem(selRow, "DRUGORINGRD_CODE",
					action.getValue("ORDER_CODE"));
			table.getDataStore().setActive(selRow, true);
			table.setDSValue(selRow);
		}
		// 其他过敏
		if ("GMC".equals(tag)) {
			table = this.getTTable(GMTABLE);
			table.acceptText();
			selRow = table.getSelectedRow();
			table.getDataStore().setItem(selRow, "DRUGORINGRD_CODE",
					action.getValue("ID"));
			table.getDataStore().setActive(selRow, true);
			table.setDSValue(selRow);
		}
		// 无过敏 add by guoy 20151112
		if ("GMN".equals(tag)) {
			table = this.getTTable(GMTABLE);
			table.acceptText();
			selRow = table.getSelectedRow();
			table.getDataStore().setItem(selRow, "DRUGORINGRD_CODE",
					action.getValue("ID"));
			table.getDataStore().setActive(selRow, true);
			table.setDSValue(selRow);
		}
		if (falg) {
			antflg = false;// 不提示抗生素
			// 过滤TABLE
			if ("N".equals(this.clearFlg))
				this.onChange();
			if ("Y".equals(this.clearFlg))
				this.delRowNull();
			if ("Q".equals(this.clearFlg))
				this.onQuery();
			if (!"IG".equals(tag)) {
				table.getTable().grabFocus();
				table.setSelectedRow(selRow);
				if ("PA".equals(tag)) {
					// 非本院用药
					String notOurHospDrugCode = TConfig
							.getSystemValue("NotOurHospDrugCode");
					if (StringUtils.isNotEmpty(notOurHospDrugCode)
							&& StringUtils.equals(table.getItemString(selRow,
									"ORDER_CODE"), notOurHospDrugCode)) {
						table.setSelectedColumn(1);
					} else {
						table.setSelectedColumn(2);
					}
				} else {
					table.setSelectedColumn(3);
				}
			} else {
				table.getTable().grabFocus();
				// 住院选中列
				String columnName = this.getFactColumnName(TABLE4, columnIG);
				table.setSelectedRow(rowIG);
				table.setSelectedColumn(querySetIGFocusColumn(columnName));
				// 计算总克数
				this.setValue("MEDI_QTYALL", getChnPhaMediQtyAll(ds));
			}
		}

		// add by huangtt 20151015儿童智能提醒
		if (cdssFlg) {
			odiStationDrools.onCdssCal(true);
		}

	}

	/**
	 * 检查是否被锁定
	 * 
	 * @return TParm
	 */
	public TParm isLockPat() {
		TParm result = new TParm();
		TParm parm = PatTool.getInstance().getLockPat(this.getMrNo());
		if (parm == null) {
			result.setErrCode(-1);
			return result;
		}
		if (parm.getCount() == 0) {
			result.setErrCode(-1);
			return result;
		}
		if (!Operator.getID().equals(parm.getValue("OPT_USER", 0))) {
			String userName = OperatorTool.getInstance().getOperatorName(
					parm.getValue("OPT_USER", 0));
			String time = StringTool.getString(
					parm.getTimestamp("OPT_DATE", 0), "yyyy年MM月dd日 HH:mm:ss");
			String name = PatTool.getInstance().getNameForMrno(mrNo);
			String ip = parm.getValue("OPT_TERM", 0);
			String program = parm.getValue("PRG_ID", 0);
			String errStr = userName + "在" + time + "锁定患者" + name + ",IP地址:"
					+ ip + "系统：" + program + "\n" + "In " + time + " in "
					+ name + " is locked,and IP address for " + ip
					+ " system for " + program;
			result.setErrCode(0);
			result.setErrText(errStr);
		} else {
			result.setErrCode(-1);
		}
		return result;
	}

	/**
	 * 计算中药总克数
	 * 
	 * @param ds
	 *            TDS
	 * @return double
	 */
	public double getChnPhaMediQtyAll(TDS ds) {
		double mediQty = 0.0;
		int rowCount = ds.rowCount();
		for (int i = 0; i < rowCount; i++) {
			if (!ds.isActive(i))
				continue;
			mediQty += ds.getItemDouble(i, "DOSAGE_QTY");
		}
		return mediQty;
	}

	/**
	 * 焦点放置位置
	 * 
	 * @return int
	 */
	public int querySetIGFocusColumn(String columnName) {
		int row = -1;
		String columnArray[] = columnName.split("_");
		if (columnArray[columnArray.length - 1].equals("1")) {
			row = 1;
		}
		if (columnArray[columnArray.length - 1].equals("2")) {
			row = 4;
		}
		if (columnArray[columnArray.length - 1].equals("3")) {
			row = 7;
		}
		if (columnArray[columnArray.length - 1].equals("4")) {
			row = 10;
		}
		return row;
	}

	/**
	 * 设置医嘱项
	 * 
	 * @param newRow
	 *            int
	 * @param parm
	 *            TParm
	 */
	public boolean setNewRowOrder(int newRow, TParm parm, int tabIndex) {
		TParm actionParm = new TParm();
		Object obj = null;
		switch (tabIndex) {
		// 临时
		case 0:

			TDS dsST = odiObject.getDS("ODI_ORDER");
			// 拿到需要添加的医令并检核
			actionParm = this.creatOrderInfo(parm, 0, "NEW", newRow);
			// System.out.println("====actionParm========"+actionParm);
			// modify by wangb 2016/6/12 针对一期临床科室套餐不提示相同医嘱 START
			if (!"Y".equals(parm.getValue("PHASE_I_CLINICAL_FLG"))) {
				// 判断是否有相同医嘱
				if (!isCheckOrderSame(actionParm, 0, newRow)) {
					if (messageBox("提示信息 Tips",
							"有相同医嘱是否开立此医嘱? \n Have the same project?",
							this.YES_NO_OPTION) != 0)
						return false;
				}
			}
			// modify by wangb 2016/6/12 针对一期临床科室套餐不提示相同医嘱 END
			// 判断是否有不符合条件的因素
			if (actionParm.getErrCode() < 0) {
				// 提示并返回
				this.messageBox(actionParm.getErrText());
				return false;
			}
			// 设置医嘱项到TABLE上
			// String buff = dsST.isFilter()?dsST.FILTER:dsST.PRIMARY;
			// 总量计算临时 stIndParm
			if ("PHA".equals(actionParm.getValue("CAT1_TYPE"))
					&& !"Y".equals(actionParm.getValue("IS_REMARK"))) {
				actionParm.setData("#NEW#", true);
				TParm stIndParm = getTempStartQty(actionParm);
				if (stIndParm.getErrCode() < 0) {
					// this.messageBox("===========come in============="+stIndParm.getErrCode());
					// $$============add by lx 2011 提示无内容 bug修改
					// 抗生素超量Start===================//
					// 抗生素设置
					if (stIndParm.getErrCode() == -2) {
						if (messageBox("提示信息 Tips",
								"管制药品用量超过标准是否按照此用量设置? \n Qty Overproof",
								this.YES_NO_OPTION) != 0)
							return false;
					}
					// this.messageBox(stIndParm.getErrText());
					// return true;
					// $$============add by lx 2011 提示无内容 bug修改
					// 抗生素超量END===================//
					// 提示并返回
					// this.messageBox(actionParm.getErrText());
					// return false;
				}
				actionParm.setData("START_DTTM_LIST", stIndParm
						.getData("START_DTTM_LIST"));
				actionParm.setData("START_DTTM", stIndParm
						.getData("START_DTTM"));
				actionParm.setData("FRST_QTY", stIndParm.getData("FRST_QTY"));
				actionParm.setData("LASTDSPN_QTY", stIndParm
						.getData("LASTDSPN_QTY"));

				actionParm.setData("ACUMMEDI_QTY", stIndParm
						.getData("ACUMMEDI_QTY"));
				actionParm.setData("DISPENSE_QTY", stIndParm
						.getData("DISPENSE_QTY"));
				actionParm.setData("DISPENSE_UNIT", stIndParm
						.getData("DISPENSE_UNIT"));

				actionParm.setData("DOSAGE_UNIT", stIndParm
						.getData("DOSAGE_UNIT"));
				actionParm.setData("MEDI_UNIT", stIndParm.getData("MEDI_UNIT"));
				// ====pangben 2013-9-10 非医嘱套餐开立抗菌药品提示
				// //查询该药嘱是否为皮试药嘱
				String sql = "SELECT SKINTEST_FLG, ANTIBIOTIC_CODE"
						+ " FROM PHA_BASE  WHERE ORDER_CODE = '"
						+ parm.getValue("ORDER_CODE") + "' ";
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				if ((!actionParm.getValue("ANTIBIOTIC_CODE").equals("") && result
						.getValue("SKINTEST_FLG", 0).equals("N"))
						&& (null == parm.getValue("PHA_ANTI_FLG") || !parm
						.getValue("PHA_ANTI_FLG").equals("Y"))) {// add
					String anti_flg = "01";// 抗菌标识
					parm.addData("ANTI_FLG", anti_flg);
					actionParm.setData("ANTIBIOTIC_WAY", parm.getValue(
							"ANTI_FLG", 0));
					this.messageBox("请从抗菌药物界面开立抗菌药品!");
					return false;
					// this
					// .messageBox("<html><font color=\"red\">此为抗菌药品，需填写抗菌标识</font></html>");
				} else if (!actionParm.getValue("ANTIBIOTIC_CODE").equals("")) {
					// ====donglt 2016-3-4 临时医嘱页签--开立抗菌药品提示
					String anti_flg = "01";// 抗菌标识
					parm.addData("ANTI_FLG", anti_flg);
					actionParm.setData("ANTIBIOTIC_WAY", parm.getValue(
							"ANTI_FLG", 0));// 抗菌标识自动赋值
					// this.messageBox("<html><font color=\"red\">此为抗菌药品，需填写抗菌标识</font></html>");
				}
				if(checkAntibiotics(parm.getValue("ORDER_CODE"))){//add by kangy 20170524
					if(!checkEtiolgexm(dsST)){
						messageBox("没有做病原学检查");
					}
					}
				TParm antiParm = antiOrderCheck(parm, tabIndex);// add by
				// wanglong
				// 20140401
				if (antiParm.getErrCode() < 0) {
					if (null != antiParm.getValue("ANTICHECK_FLG")
							&& antiParm.getValue("ANTICHECK_FLG").equals("Y")) {// 抗菌药品越权管理提示管控
					} else {
						this.messageBox(antiParm.getErrText());
					}
					return false;
				}
				if (!this.yyList) {
					// ==============pangben modify 20110609 start 初始用量显示
					TParm mediQTY = getMediQty(stIndParm, actionParm);
					// ==============pangben modify 20110609 stop
					actionParm.setData("MEDI_QTY",
							mediQTY.getCount() > 0 ? mediQTY.getDouble(
									"MEDI_QTY", 0) : stIndParm
									.getData("MEDI_QTY"));// ============pangben
					actionParm.setData("ACUMDSPN_QTY", stIndParm
							.getDouble("ACUMDSPN_QTY") > 0 ? stIndParm
									.getDouble("ACUMDSPN_QTY") : mediQTY.getCount());
					actionParm.setData("DOSAGE_QTY", mediQTY.getDouble(
							"DOSAGE_QTY", 0) > 0 ? mediQTY.getDouble(
									"DOSAGE_QTY", 0) : stIndParm
									.getDouble("DOSAGE_QTY"));// SHIBL 20130228 MODIFY
					// =============pangben modify 20110711 start 临时医嘱频次是立即使用
					if (actionParm.getInt("LINK_NO") == 0) {
						// 频次
						actionParm.setData("FREQ_CODE", odiObject
								.getAttribute(odiObject.ODI_UDD_STAT_CODE));
						// 用法
						actionParm.setData("ROUTE_CODE", mediQTY.getValue(
								"ROUTE_CODE", 0).trim().length() > 0 ? mediQTY
										.getValue("ROUTE_CODE", 0).trim() : null);
					}
				}
				// 套餐传入
				else {
					/**
					 * System.out.println("====dosageQty====="+stIndParm
					 * .getDouble("DOSAGE_QTY"));
					 **/
					actionParm.setData("ACUMDSPN_QTY", stIndParm
							.getDouble("ACUMDSPN_QTY"));
					actionParm.setData("DOSAGE_QTY", stIndParm
							.getDouble("DOSAGE_QTY"));

				}

				// $$=====Modified by lx 2012/04/16 end 临时不从套餐传入======$$//

			}

			// add by yangjj 20150410增加开立配血医嘱时弹出备血单选择窗口
			// 配血申请单号
			String apply_no = "";
			if (checkBMSOrder(actionParm.getValue("ORDER_CODE"))) {
				TParm parmdate = new TParm();
				String caseno = getCaseNo();
				String deptCode = getDeptCode();
				String mrno = getMrNo();
				parmdate.setData("ADM_TYPE", "");
				parmdate.setData("USE_DATE", SystemTool.getInstance().getDate());
				parmdate.setData("DR_CODE", Operator.getID());
				parmdate.setData("CASE_NO", caseno);
				parmdate.setData("MR_NO", mrno);
				parmdate.setData("DEPT_CODE", deptCode);
				parmdate.setData("new", "No");
				TParm r = new TParm(TJDODBTool.getInstance().select(
						getEXECPRGSql(actionParm.getValue("ORDER_CODE"))));
				TParm result = (TParm) this.openDialog(r
						.getValue("EXEC_PRG", 0), parmdate);

				if (result == null) {
					return false;
				}
				apply_no = result.getValue("APPLY_NO");
				actionParm.setData("APPLY_NO", apply_no);

			}

			// $$====add by lx 2012/04/17 是套餐传入的，设置备注 start=====$$//
			if (this.yyList) {
				// 设置备注
				actionParm.setData("DR_NOTE", parm.getValue("DESCRIPTION"));
			}
			if (parm.getValue("CLP_FLG").equals("Y")) {// 引入路径传回数据--xiongwg20150429
				actionParm.setData("DR_NOTE", parm.getValue("DR_NOTE"));// 医生备注
			}
			// $$====add by lx 2012/04/17 是套餐传入的，设置备注 end=====$$

			// add by wangb 增加一期临床注记 2016/6/12 START
			actionParm.setData("PHASE_I_CLINICAL_FLG", parm.getValue("PHASE_I_CLINICAL_FLG"));
			// add by wangb 增加一期临床注记 2016/6/12 END

			// add by wangb 一期临床科室套餐将时间间隔代入到医嘱备注中 START
			if ("Y".equals(parm.getValue("PHASE_I_CLINICAL_FLG")) && "LIS".equals(parm.getValue("CAT1_TYPE"))) {
				String drNote = parm.getValue("BC_INTERVAL_TIME");
				if (StringUtils.isNotEmpty(drNote)) {
					if (0 == parm.getDouble("BC_INTERVAL_TIME")) {
						drNote = "0h";
					} else {
						drNote = new BigDecimal(drNote).stripTrailingZeros() + "h";
					}
				}
				actionParm.setData("DR_NOTE", drNote);
			}
			// add by wangb 一期临床科室套餐将时间间隔代入到医嘱备注中 END

			odiObject.setItem(dsST, newRow, actionParm);// 表格医嘱传数据
			if (opeFlg) {
				dsST = odiObject.getDS("ODI_ORDER");
				dsST.setItem(newRow, "RX_KIND", "OP");
			}
			// dsST.showDebug();
			obj = actionParm.getData("TABLEROW_COLOR");
			// 设置颜色
			if (obj != null) {
				this.getTTable(TABLE1).setRowTextColor(newRow, (Color) obj);
			}

			// 判断是否是集合医嘱
			if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
				// System.out.println("===========actionParm集合医嘱主项=============="+actionParm);
				// 得到执行科室
				String exDeptCode = actionParm.getValue("EXEC_DEPT_CODE");
				// 得到医令细分类
				String orderCatCode = actionParm.getValue("ORDER_CAT1_CODE");
				// 医令分组
				String catType = actionParm.getValue("CAT1_TYPE");
				// 频次
				String freqCode = actionParm.getValue("FREQ_CODE");
				// 单位
				String dosageUnit = actionParm.getValue("DOSAGE_UNIT");
				// 集合医嘱组号
				String orderSetGroupNo = actionParm
						.getValue("ORDERSET_GROUP_NO");
				// $$============= add by lx 2012-06-12
				// 细项启用时间一致start============$$//
				Timestamp effDate = actionParm.getTimestamp("EFF_DATE");
				// this.messageBox("==effDate=="+effDate);
				// $$============= add by lx 2012-06-12
				// 细项启用时间一致end============$$//
				// 拿到集合医嘱细项
				TParm action = this.getOrderSetList(actionParm);
				if (action.getInt("ACTION", "COUNT") == 0) {
					// 没有集合医嘱细项信息!
					this.messageBox("E0162");
					return true;
				}

				// 插入集合遗嘱细项
				return this.insertOrderSetList(action, exDeptCode,
						orderCatCode, catType, freqCode, dosageUnit,
						orderSetGroupNo, tabIndex, dsST, newRow, effDate);
			}
			TTable table = (TTable) this.getComponent(TABLE1);
			table.setLockCell(20, table.getSelectedRow(), false);
			table.setLockCell(21, table.getSelectedRow(), false);
			table.setLockCell(23, table.getSelectedRow(), false);
			break;
			// 长期
		case 1:
			// System.out.println("===长期actionParm==="+parm);
			TDS dsUD = odiObject.getDS("ODI_ORDER");
			// 拿到需要添加的医令并检核
			actionParm = this.creatOrderInfo(parm, 1, "NEW", newRow);
			// 判断是否有相同医嘱
			if (!isCheckOrderSame(actionParm, 1, newRow)) {
				if (messageBox("提示信息 Tips",
						"有相同医嘱是否开立此医嘱? \n Have the same project?",
						this.YES_NO_OPTION) != 0)
					return false;
			}
			// 判断是否有不符合条件的因素
			if (actionParm.getErrCode() < 0) {
				// 提示并返回
				this.messageBox(actionParm.getErrText());
				return false;
			}
			// 总量计算长期
			if ("PHA".equals(actionParm.getValue("CAT1_TYPE"))
					&& !"Y".equals(actionParm.getValue("IS_REMARK"))) {
				actionParm.setData("#NEW#", true);

				TParm udIndParm = getLongStartQty(actionParm);

				// System.out.println("=====udIndParm======="+udIndParm);

				actionParm.setData("START_DTTM_LIST", udIndParm
						.getData("START_DTTM_LIST"));
				actionParm.setData("START_DTTM", udIndParm
						.getData("START_DTTM"));
				actionParm.setData("FRST_QTY", udIndParm.getData("FRST_QTY"));
				actionParm.setData("RX_KIND", udIndParm.getData("RX_KIND"));
				// 最近配药量
				actionParm.setData("LASTDSPN_QTY", udIndParm
						.getData("LASTDSPN_QTY"));
				// 累用量
				actionParm.setData("ACUMDSPN_QTY", udIndParm
						.getData("ACUMDSPN_QTY"));
				// 累计开药量
				actionParm.setData("ACUMMEDI_QTY", udIndParm
						.getData("ACUMMEDI_QTY"));
				// 发药数量 / 实际退药入库量《盒或是片》
				actionParm.setData("DISPENSE_QTY", udIndParm
						.getData("DISPENSE_QTY"));
				// 总量单位(发药数量)
				actionParm.setData("DISPENSE_UNIT", udIndParm
						.getData("DISPENSE_UNIT"));
				// 配药数量、实际扣库数量
				actionParm.setData("DOSAGE_QTY", udIndParm
						.getData("DOSAGE_QTY"));
				// 配药单位 < 实际扣库量 >
				actionParm.setData("DOSAGE_UNIT", udIndParm
						.getData("DOSAGE_UNIT"));

				// ====donglt 2016-3-4 临时医嘱页签--开立抗菌药品提示
				if (!actionParm.getValue("ANTIBIOTIC_CODE").equals("")
						&& ("".equals(parm.getValue("PHA_ANTI_FLG"))
								|| null == parm.getValue("PHA_ANTI_FLG") || !parm
								.getValue("PHA_ANTI_FLG").equals("Y"))) { // add
					this.messageBox("请从抗菌药物界面开立抗菌药品!");
					return false;
				} else if (!actionParm.getValue("ANTIBIOTIC_CODE").equals("")) {
					// this.messageBox("<html><font color=\"red\">此为抗菌药品，需填写抗菌标识</font></html>");
				}
				if(checkAntibiotics(parm.getValue("ORDER_CODE"))){//add by kangy 20170524
					if(!checkEtiolgexm(dsUD)){
						messageBox("没有做病原学检查");
					}
					}
				TParm antiParm = antiOrderCheck(parm, tabIndex);// ===========pangben
				// 2014-2-27
				if (antiParm.getErrCode() < 0) {
					if (null != antiParm.getValue("ANTICHECK_FLG")
							&& antiParm.getValue("ANTICHECK_FLG").equals("Y")) {// 抗菌药品越权管理提示管控
					} else {
						this.messageBox(antiParm.getErrText());
					}
					return false;
				}
				if (!actionParm.getValue("ANTIBIOTIC_CODE").equals("")) {

					// =====pangben 2013-9-10 抗菌药品开立设置停用时间
					if (parm.getValue("RADIO_FLG").equals("OPRDO")) {
						Timestamp temp = parm.getTimestamp("OP_START_DATE");
						// System.out.println("停用时间 is：：："+temp);
						actionParm.setData("DC_DATE", StringTool.rollDate(temp,
								parm.getInt("PHA_DS_DAY")));
						// System.out.println("345停用时间 is：：："+actionParm);
					} else {

						actionParm.setData("DC_DATE", StringTool.rollDate(
								SystemTool.getInstance().getDate(), parm
								.getInt("PHA_DS_DAY")));
					}
					actionParm.setData("DC_DR_CODE", Operator.getID());// yanjing,20131111
					actionParm.setData("DC_DEPT_CODE", Operator.getDept());
					actionParm.setData("ANTIBIOTIC_WAY", parm
							.getValue("ANTI_FLG"));// 抗菌标识 20131111
					String sktSql = "SELECT SKINTEST_NOTE,BATCH_NO FROM PHA_ANTI WHERE CASE_NO= '"
							+ caseNo
							+ "' AND ORDER_CODE='"
							+ actionParm.getValue("ORDER_CODE")
							+ "' "
							+ " AND ROUTE_CODE = 'PS'  AND SKINTEST_NOTE IS NOT NULL ORDER BY ORDER_DATE DESC ";
					TParm sktParm = new TParm(TJDODBTool.getInstance().select(
							sktSql));
					String nsNode = null;
					String nsRNode = null;
					if (sktParm.getCount() > 0) {
						nsRNode = ",皮试批号:" + sktParm.getValue("BATCH_NO", 0);
					}
					String sql = "SELECT ID,CHN_DESC AS NAME "
							+ "FROM SYS_DICTIONARY WHERE GROUP_ID='SKINTEST_FLG' AND ID='"
							+ sktParm.getValue("SKINTEST_NOTE", 0) + "'";
					sktParm = new TParm(TJDODBTool.getInstance().select(sql));
					if (sktParm.getCount() > 0) {
						nsNode = "皮试结果:" + sktParm.getValue("NAME", 0)
						+ nsRNode;
					}
					actionParm.setData("NS_NOTE", nsNode);// 20131108 yanjing
					// 护士备注
				}
				if (!this.yyList) {
					// ==============pangben modify 20110609 start 初始用量显示
					TParm mediQTY = getMediQty(udIndParm, actionParm);
					// 开药数量
					actionParm.setData("MEDI_QTY",
							mediQTY.getCount() > 0 ? mediQTY.getDouble(
									"MEDI_QTY", 0) : null);
					// 开药单位
					actionParm.setData("MEDI_UNIT",
							mediQTY.getCount() > 0 ? mediQTY.getValue(
									"MEDI_UNIT", 0) : null);
					// 不是连合医嘱的取默认

					if (actionParm.getInt("LINK_NO") == 0) {
						// 频次
						actionParm.setData("FREQ_CODE", mediQTY.getValue(
								"FREQ_CODE", 0).trim().length() > 0 ? mediQTY
										.getValue("FREQ_CODE", 0).trim() : odiObject
										.getAttribute(odiObject.ODI_UDD_STAT_CODE));
						// 用法
						actionParm.setData("ROUTE_CODE", mediQTY.getValue(
								"ROUTE_CODE", 0).trim().length() > 0 ? mediQTY
										.getValue("ROUTE_CODE", 0).trim() : null);
					}
				}
				// 套餐传入
				/**
				 * }else{ System.out.println("====dosageQty====="+udIndParm
				 * .getDouble("DOSAGE_QTY")); actionParm .setData(
				 * "ACUMDSPN_QTY", udIndParm .getDouble("ACUMDSPN_QTY"));
				 * actionParm.setData( "DOSAGE_QTY", udIndParm
				 * .getDouble("DOSAGE_QTY")); }
				 **/
				// 发药单位、调配单位
				// actionParm.setData("DOSAGE_UNIT",
				// mediQTY.getValue("DOSAGE_UNIT",0));
				// 配药时间起日==最近摆药日期
				// result.setData("LAST_DSPN_DATE",StringTool.getTimestamp(otherData.get("ORDER_LAST_DSPN_DATE").toString(),"yyyyMMddHHmm"));
				// 首日量注记观察者中使用
				actionParm.setData("RX_FLG", "Y");

			}
			if (actionParm.getInt("LINK_NO") > 0
					&& !"PHA".equals(actionParm.getValue("CAT1_TYPE"))) {// wanglong
				// add
				// 20140725
				this.messageBox("长期连嘱只能包含药嘱");
				return false;
			}
			// $$====add by lx 2012/04/17 是套餐传入的，设置备注 start=====$$//
			if (this.yyList) {
				// 设置备注
				actionParm.setData("DR_NOTE", parm.getValue("DESCRIPTION"));
			}
			if (parm.getValue("CLP_FLG").equals("Y")) {// 引入路径传回数据--xiongwg20150429
				actionParm.setData("DR_NOTE", parm.getValue("DR_NOTE"));// 医生备注
			}
			// $$====add by lx 2012/04/17 是套餐传入的，设置备注 end=====$$//

			// 设置医嘱项到TABLE上
			// String buffUD = dsUD.isFilter()?dsUD.FILTER:dsUD.PRIMARY;
			if ((OrderUtil.getInstance().isSTFreq(actionParm
					.getValue("FREQ_CODE")))
					&& ("PHA".equals(actionParm.getValue("CAT1_TYPE")))) {
				actionParm.setData("FREQ_CODE", "");// 长期医嘱开立医嘱时为常用频次为临时时为空
				// shibl 20130315 modify
			}
			odiObject.setItem(dsUD, newRow, actionParm);
			obj = actionParm.getData("TABLEROW_COLOR");
			// 设置颜色
			if (obj != null) {
				// this.getTTable(TABLE2).setRowColor(newRow, (Color) obj);
				this.getTTable(TABLE2).setRowTextColor(newRow, (Color) obj);
			}
			// 判断是否是集合医嘱主项
			if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
				// 得到执行科室
				String exDeptCode = actionParm.getValue("EXEC_DEPT_CODE");
				// 得到医令细分类
				String orderCatCode = actionParm.getValue("ORDER_CAT1_CODE");
				// 医令分组
				String catType = actionParm.getValue("CAT1_TYPE");
				// 频次
				String freqCode = actionParm.getValue("FREQ_CODE");
				// 用法
				String dosageUnit = actionParm.getValue("DOSAGE_UNIT");
				// 集合医嘱组号
				String orderSetGroupNo = actionParm
						.getValue("ORDERSET_GROUP_NO");

				// $$============= add by lx 2012-06-12
				// 细项启用时间一致start============$$//
				Timestamp effDate = actionParm.getTimestamp("EFF_DATE");
				// $$============= add by lx 2012-06-12
				// 细项启用时间一致end============$$//

				// 拿到集合医嘱细项
				TParm action = this.getOrderSetList(actionParm);
				if (action.getInt("ACTION", "COUNT") == 0) {
					this.messageBox("E0162");
					return true;
				}
				// 插入集合遗嘱细项
				return this.insertOrderSetList(action, exDeptCode,
						orderCatCode, catType, freqCode, dosageUnit,
						orderSetGroupNo, tabIndex, dsUD, newRow, effDate);
			}
			break;
			// 出院带药
		case 2:
			if (this.getValueString("RX_NO").length() == 0) {
				// 请选择处方号！
				this.messageBox("E0069");
				return false;
			}
			TDS dsDS = odiObject.getDS("ODI_ORDER");
			// 拿到需要添加的医令并检核
			actionParm = this.creatOrderInfo(parm, 2, "NEW", newRow);
			// 判断是否有相同医嘱
			if (!isCheckOrderSame(actionParm, 2, newRow)) {
				if (messageBox("提示信息 Tips",
						"有相同医嘱是否开立此医嘱? \n Have the same project?",
						this.YES_NO_OPTION) != 0)
					return false;
			}
			// 判断是否有不符合条件的因素
			if (actionParm.getErrCode() < 0) {
				// 提示并返回
				this.messageBox(actionParm.getErrText());
				return false;
			}
			// 总量计算出院带药
			if ("PHA".equals(actionParm.getValue("CAT1_TYPE"))
					&& !"Y".equals(actionParm.getValue("IS_REMARK"))) {
				actionParm.setData("#NEW#", true);

				TParm dsIndParm = getOutHospStartQty(actionParm);
				// 库存发药单位
				actionParm.setData("DOSAGE_UNIT", dsIndParm
						.getData("DOSAGE_UNIT"));
				// 换算总量
				actionParm.setData("DOSAGE_QTY", dsIndParm
						.getData("DOSAGE_QTY"));
				// 配药总量
				actionParm.setData("DISPENSE_QTY", dsIndParm
						.getData("DISPENSE_QTY"));
				// 配药单位
				actionParm.setData("DISPENSE_UNIT", dsIndParm
						.getData("DISPENSE_UNIT"));


				// ====donglt 2016-3-4 临时医嘱页签--开立抗菌药品提示
				if (!actionParm.getValue("ANTIBIOTIC_CODE").equals("")
						&& ("".equals(parm.getValue("PHA_ANTI_FLG"))
								|| null == parm.getValue("PHA_ANTI_FLG") || !parm
								.getValue("PHA_ANTI_FLG").equals("Y"))) { // add
					this.messageBox("请从抗菌药物界面开立抗菌药品!");
					return false;
				} else if (!actionParm.getValue("ANTIBIOTIC_CODE").equals("")) {

					String anti_flg = "02";// 抗菌标识
					parm.addData("ANTI_FLG", anti_flg);
					actionParm.setData("ANTIBIOTIC_WAY", parm.getValue(
							"ANTI_FLG", 0));// 抗菌标识自动赋值
				}
				if(checkAntibiotics(parm.getValue("ORDER_CODE"))){//add by kangy 20170524
					if(!checkEtiolgexm(dsDS)){
						messageBox("没有做病原学检查");
					}
					}
				TParm antiParm = antiOrderCheck(parm, tabIndex);// add by
				// wanglong
				// 20140401
				if (antiParm.getErrCode() < 0) {
					if (null != antiParm.getValue("ANTICHECK_FLG")
							&& antiParm.getValue("ANTICHECK_FLG").equals("Y")) {// 抗菌药品越权管理提示管控
					} else {
						this.messageBox(antiParm.getErrText());
					}
					return false;
				}
				if (!this.yyList) {
					// System.out.println("====非医嘱套餐传入的====");
					// ==============pangben modify 20110609 start 初始用量显示
					TParm mediQTY = getMediQty(dsIndParm, actionParm);
					// 开药数量
					actionParm.setData("MEDI_QTY",
							mediQTY.getCount() > 0 ? mediQTY.getDouble(
									"MEDI_QTY", 0) : null);
					// 开药单位
					actionParm.setData("MEDI_UNIT",
							mediQTY.getCount() > 0 ? mediQTY.getValue(
									"MEDI_UNIT", 0) : null);
					// 频次
					actionParm.setData("FREQ_CODE", mediQTY.getValue(
							"FREQ_CODE", 0).trim().length() > 0 ? mediQTY
									.getValue("FREQ_CODE", 0).trim() : odiObject
									.getAttribute(odiObject.ODI_UDD_STAT_CODE));
					// 用法
					actionParm.setData("ROUTE_CODE", mediQTY.getValue(
							"ROUTE_CODE", 0).trim().length() > 0 ? mediQTY
									.getValue("ROUTE_CODE", 0).trim() : null);
					// ==========pangben modify 20110609 stop
				}
			}

			// $$====add by lx 2012/04/17 是套餐传入的，设置备注 start=====$$//
			if (this.yyList) {
				// 设置备注
				actionParm.setData("DR_NOTE", parm.getValue("DESCRIPTION"));
			}
			if (parm.getValue("CLP_FLG").equals("Y")) {// 引入路径传回数据--xiongwg20150429
				actionParm.setData("DR_NOTE", parm.getValue("DR_NOTE"));// 医生备注
			}
			// $$====add by lx 2012/04/17 是套餐传入的，设置备注 end=====$$//

			// 设置医嘱项到TABLE上
			odiObject.setItem(dsDS, newRow, actionParm);
			obj = actionParm.getData("TABLEROW_COLOR");
			// 设置颜色
			if (obj != null) {
				// this.getTTable(TABLE3).setRowColor(newRow, (Color) obj);
				this.getTTable(TABLE3).setRowTextColor(newRow, (Color) obj);
			}
			break;
			// 中药饮片
		case 3:
			if (this.getValueString("IG_RX_NO").length() == 0) {
				this.messageBox("E0069");
				return false;
			}
			TDS dsIG = odiObject.getDS("ODI_ORDER");
			// 拿到需要添加的医令并检核
			actionParm = this.creatOrderInfo(parm, 3, "NEW", newRow);
			// 判断是否有相同医嘱
			if (!isCheckOrderSame(actionParm, 3, newRow)) {
				if (messageBox("提示信息 Tips",
						"有相同医嘱是否开立此医嘱? \n Have the same project?",
						this.YES_NO_OPTION) != 0)
					return false;
			}
			// 判断是否有不符合条件的因素
			if (actionParm.getErrCode() < 0) {
				// 提示并返回
				this.messageBox(actionParm.getErrText());
				return false;
			}
			if(checkAntibiotics(parm.getValue("ORDER_CODE"))){//add by kangy 20170524
				if(!checkEtiolgexm(dsIG)){
					messageBox("没有做病原学检查");
				}
				}
			// 总量计算出院带药
			if ("PHA".equals(actionParm.getValue("CAT1_TYPE"))
					&& !"Y".equals(actionParm.getValue("IS_REMARK"))) {
				actionParm.setData("#NEW#", true);
				TParm igIndParm = getOutHospStartQty(actionParm);
				// 库存发药单位
				// actionParm.setData("DOSAGE_UNIT",actionParm.getData("DOSAGE_UNIT"));
				// 换算总量
				actionParm.setData("DOSAGE_QTY", igIndParm
						.getData("DOSAGE_QTY"));
				// 配药总量
				actionParm.setData("DISPENSE_QTY", igIndParm
						.getData("DISPENSE_QTY"));
				// 配药单位
				// actionParm.setData("DISPENSE_UNIT",actionParm.getData("DISPENSE_UNIT"));

			}
			// 总量计算
			this.setValue("MEDI_QTYALL", getChnPhaMediQtyAll(dsIG));
			// this.messageBox_(actionParm);
			// 设置医嘱项到TABLE上
			odiObject.setItem(dsIG, newRow, actionParm);
			obj = actionParm.getData("TABLEROW_COLOR");
			// this.messageBox_(obj);
			// 设置颜色
			// if (obj != null) {
			// // this.getTTable(TABLE4).setRowColor(newRow, (Color) obj);
			// //errtemp(单元格变色)
			// this.getTTable(TABLE4).setRowTextColor(newRow, (Color) obj);
			// }
			break;
			// 院前用药
		case 5:
			TDS dsPa = odiObject.getDS("ODI_ORDER");
			// 拿到需要添加的医令并检核
			actionParm = this.creatOrderInfo(parm, 5, "NEW", newRow);
			// 判断是否有不符合条件的因素
			if (actionParm.getErrCode() < 0) {
				// 提示并返回
				this.messageBox(actionParm.getErrText());
				return false;
			}
			actionParm.setData("#NEW#", true);
			odiObject.setItem(dsPa, newRow, actionParm);
			break;
		}
		return true;
	}

	/**
	 * 得到集合医嘱细项
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 * @param exDeptCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderSetList(TParm parm) {
		TParm result = new TParm();
		String orderCode = parm.getValue("ORDER_CODE");
		result = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT A.ORDER_CODE,B.ORDER_DESC,B.DESCRIPTION,B.UNIT_CODE,B.INSPAY_TYPE,"
								+ " B.ORDERSET_FLG,B.INDV_FLG,ROWNUM+1 AS ORDERSET_GROUP_NO,A.ORDERSET_CODE,"
								+ " B.RPTTYPE_CODE,B.OPTITEM_CODE,B.MR_CODE,B.DEGREE_CODE,B.SPECIFICATION,'Y' AS HIDE_FLG,B.DEV_CODE,B.IPD_FIT_FLG,A.DOSAGE_QTY AS MEDI_QTY"
								+ " FROM SYS_ORDERSETDETAIL A,SYS_FEE B WHERE A.ORDERSET_CODE='"
								+ orderCode
								+ "' AND A.ORDER_CODE=B.ORDER_CODE"));
		// System.out.println("得到集合医嘱细项:"+result);
		return result;
	}

	/**
	 * 插入集合医嘱细项
	 * 
	 * @param parm
	 *            TParm
	 * @param exDeptCode
	 *            String
	 * @param orderCatCode
	 *            String
	 * @param catType
	 *            String
	 */
	public boolean insertOrderSetList(TParm parm, String exDeptCode,
			String orderCatCode, String catType, String freqCode,
			String dosageUnit, String orderSetGroupNo, int type, TDS ds,
			int selrow, Timestamp effDate) {
		int rowCount = parm.getInt("ACTION", "COUNT");
		// this.messageBox("集合医嘱个数:"+rowCount);
		if (rowCount == 0)
			return true;
		odiObject.setAttribute("CHANGE_FLG", true);
		String rxKinds[] = new String[] { "ST", "UD", "DS", "IG" };
		odiObject.setAttribute("RX_KIND", rxKinds[type]);
		// 得到集合医嘱细项实际要插入的值
		for (int i = 0; i < rowCount; i++) {
			TParm action = parm.getRow(i);
			action.setData("EXEC_DEPT_CODE", exDeptCode);
			action.setData("ORDER_CAT1_CODE", orderCatCode);
			action.setData("CAT1_TYPE", catType);
			action.setData("FREQ_CODE", freqCode);
			action.setData("RX_KIND", rxKinds[type]);
			action.setData("DOSAGE_UNIT", dosageUnit);
			action.setData("ORDERSET_GROUP_NO", orderSetGroupNo);
			// System.out.println("集合医嘱细项单项数据old============:"+action);
			action = this.creatOrderInfo(action, type, "OLD", selrow);
			if (action.getErrCode() < 0) {
				this.messageBox(action.getErrText());
				return false;
			}

			// $$ ==== add by lx 2012-06-30 集全医嘱启用时间需一致start====$$//
			action.setData("EFF_DATE", effDate);
			// $$ ==== add by lx 2012-06-30 end====$$//

			// this.messageBox("返回后的集合医嘱细项:"+action);
			// System.out.println("集合医嘱细项单项数据:"+action);
			String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			odiObject.getDS("ODI_ORDER").setAttribute("ORDERJH_FLG", true);
			// System.out.println("首餐时间:"+action.getData("START_DTTM"));
			action.setData("RX_KIND", rxKinds[type]);
			action.setData("ORDER_NO", "999999999999");
			// System.out.println("====insertOrderSetList action===="+action);
			if (opeFlg) {
				action.setData("RX_KIND", "OP");
			}
			TParm actionParm = odiObject.insertRow(ds, action, buff);
			if (actionParm.getErrCode() < 0) {
				odiObject.getDS("ODI_ORDER").setAttribute("ORDERJH_FLG", false);
				return false;
			}
		}
		odiObject.getDS("ODI_ORDER").setAttribute("ORDERJH_FLG", false);
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
	 * 拿到集合医嘱最大分组号
	 * 
	 * @return int
	 */
	public int getMaxOrderSetGroupNo() {
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		TParm parm = ds.getBuffer(buff);
		int rowCount = parm.getCount();
		int maxGroupNo = 0;
		for (int i = 0; i < rowCount; i++) {
			TParm temp = parm.getRow(i);
			if (temp.getInt("ORDERSET_GROUP_NO") > maxGroupNo)
				maxGroupNo = temp.getInt("ORDERSET_GROUP_NO");
		}
		return maxGroupNo == 0 ? 1 : maxGroupNo + 1;
	}

	/**
	 * 是否可以续用
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            int
	 * @return boolean
	 */
	public boolean isCheckOrderContinuousFlg(TParm parm, int type) {
		boolean falg = true;
		String orderCode = parm.getValue("ORDER_CODE");
		TTable table = this.getTTable("TABLE" + (type + 1));
		int selectRow = table.getSelectedRow();
		if ("N".equals(this.clearFlg)) {
			TParm action = table.getDataStore().getBuffer(
					table.getDataStore().PRIMARY);
			int rowCount = action.getCount();
			for (int i = 0; i < rowCount; i++) {
				if (i == selectRow)
					continue;
				TParm temp = action.getRow(i);
				if (temp.getValue("ORDER_CODE").equals(orderCode)) {
					falg = false;
				}
			}
		} else {
			int selId = (Integer) table.getDataStore().getItemData(selectRow,
					"#ID#", table.getDataStore().PRIMARY);
			String buff = table.getDataStore().isFilter() ? table
					.getDataStore().FILTER : table.getDataStore().PRIMARY;
					TParm action = table.getDataStore().getBuffer(buff);
					int rowCount = action.getCount();
					for (int i = 0; i < rowCount; i++) {
						int tempId = action.getInt("#ID#");
						if (tempId == selId)
							continue;
						TParm temp = action.getRow(i);
						if (temp.getValue("ORDER_CODE").equals(orderCode)) {
							falg = false;
						}
					}
		}
		return falg;
	}

	/**
	 * 当前是否有相同医嘱
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            int
	 * @return boolean
	 */
	public boolean isCheckOrderSame(TParm parm, int type, int selRow) {
		boolean falg = true;
		String orderCode = parm.getValue("ORDER_CODE");
		TTable table = this.getTTable("TABLE" + (type + 1));
		TDS ds = odiObject.getDS("ODI_ORDER");
		if (type == 3) {
			int chnNewRow[] = ds.getNewRows();
			for (int temp : chnNewRow) {
				if (temp == selRow)
					continue;
				if (ds.getRowParm(temp).getValue("ORDER_CODE")
						.equals(orderCode)) {
					falg = false;
					break;
				}
			}
			return falg;
		}
		int newRow[] = table.getDataStore().getNewRows();
		// 当前选中行
		for (int i : newRow) {
			if (i == selRow)
				continue;
			if (table.getDataStore().getRowParm(i).getValue("ORDER_CODE")
					.equals(orderCode)) {
				falg = false;
				break;
			}
		}
		return falg;
	}

	/**
	 * 抗菌药品越权管理
	 * 
	 * @param parm
	 * @param tabIndex
	 * @param result
	 * @return ===========pangben 2014-2-27
	 */
	private TParm antiOrderCheck(TParm parm, int tabIndex) {
		TParm result = new TParm();
		// 检查证照
		Object obj = parm.getData("LCS_CLASS_CODE");
		if (obj != null && obj.toString().length() != 0) {
			// System.out.println("证照输出：：："+OrderUtil.getInstance().checkLcsClassCode(Operator.getID(),""
			// + obj));
			if (!OrderUtil.getInstance().checkLcsClassCode(Operator.getID(),
					"" + obj)) {
				// result.setErrCode(-1);
				if ((obj.toString().equals("2") || obj.toString().equals("3") || obj
						.toString().equals("4"))
						&& tabIndex == 1) {// 为抗菌药物的证照tabIndex ==1长期医嘱
					// if (this.messageBox("提示信息 Tips", "是否越权? ",
					// this.YES_NO_OPTION) == 0) {// 是，越权，向pha_anti表中写数据
					// parm.addData("INDEX", "0");
					// parm.setData("CASE_NO", caseNo);
					// parm.setData("MR_NO", mrNo);
					// parm.setData("OPT_USER", Operator.getID());
					// parm.setData("OPT_TERM", Operator.getIP());
					// parm.setData("PHA_USE_FLG", "N");
					// parm.setData("NODE_FLG", "N");
					// TParm result1 = TIOM_AppServer.executeAction(
					// "action.pha.PHAAntiAction", "onSavePha", parm);
					// if(result1.getErrCode()>=0){
					// TParm drParm = new TParm();
					// drParm.setData("CASE_NO", result1.getValue("CASE_NO"));
					// drParm.setData("PHA_SEQ", result1.getValue("PHA_SEQ"));
					// drParm.setData("SEQ_NO", result1.getValue("SEQ_NO"));
					// TParm newparm = (TParm) openDialog(
					// "%ROOT%\\config\\odi\\ODIOverRideDc.x",drParm);
					// }
					// //给医生发送公告
					//                      
					//                      
					// }
					// result.setData("ANTICHECK_FLG","Y");
					// result.setErrCode(-1);
				} else {
					// 您没有此医嘱的证照！
					result.setErrCode(-1);
					result.setErrText("E0166");
				}
				return result;
			}
		}
		return result;
	}

	/**
	 * 开医嘱ORDER项赋值
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm creatOrderInfo(TParm parm, int type, String insertType, int row) {
		// this.messageBox("==============creatOrderInfo开医嘱ORDER项赋值===================");
		TParm result = new TParm();
		// 判断是否住院适用医嘱
		if (!("Y".equals(parm.getValue("IPD_FIT_FLG")))) {
			result.setErrCode(-2);
			// 不是住院适用医嘱！
			result.setErrText("E0165");
			return result;
		}
		// this.messageBox_(parm);
		// 不可以开立集合医嘱细项
		if (!"Y".equals(parm.getValue("ORDERSET_FLG"))
				&& ("RIS".equals(parm.getValue("CAT1_TYPE")) || "LIS"
						.equals(parm.getValue("CAT1_TYPE")))
				&& "NEW".equals(insertType)) {
			result.setErrCode(-3);
			// 集合医嘱细项不可以开立！
			result.setErrText("E0167");
			return result;
		}
		switch (type) {
		// 临时
		case 0:
			// 设置临时医嘱
			result = this.returnTempOrderData(parm, row);
			// 判断管制药品等级
			if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
				// 判断库存量(接口)ORDER_CODE,ORG_CODE(医嘱代码,药房代码)
				// fux modify start
				// if ("N".equals(parm.getValue("IS_REMARK"))) {
				// if (!INDTool.getInstance().inspectIndStock(
				// result.getValue("EXEC_DEPT_CODE"),
				// result.getValue("ORDER_CODE"),
				// result.getDouble("DOSAGE_QTY"))) {
				// result.setErrCode(-3);
				// // 库存不足！
				// result.setErrText("E0052");
				// return result;
				// }
				// }
				// fux modify end
				// 是否是管制药品
				if (result.getValue("CTRLDRUGCLASS_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", ctrlDrugClassColor);
					return result;
				}
				// 是否是抗生素
				if (result.getValue("ANTIBIOTIC_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", antibioticColor);
					return result;
				}
			}
			// 是否是医保药品
			if (OrderUtil.getInstance().isNhiPat(this.getCaseNo())) {
				if ("C".equals(parm.getValue("INSPAY_TYPE"))) {
					result.setData("TABLEROW_COLOR", nhiColor);
					return result;
				}
			}
			return result;
			// 长期
		case 1:
			if ("LIS".equals(parm.getData("CAT1_TYPE").toString())
					|| "RIS".equals(parm.getData("CAT1_TYPE").toString())) {
				result.setErrCode(-1);
				// 长期医嘱不得输入此医令！
				result.setErrText("E0168");
				return result;
			}
			// 设置长期医嘱
			result = this.returnLongOrderData(parm, row);
			// System.out.println("长期医令===========================>>"+result);
			// 判断管制药品等级
			if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
				// // 判断库存量(接口)ORDER_CODE,ORG_CODE(医嘱代码,药房代码)
				// if ("N".equals(parm.getValue("IS_REMARK"))) {
				// if (!INDTool.getInstance().inspectIndStock(
				// result.getValue("EXEC_DEPT_CODE"),
				// result.getValue("ORDER_CODE"),
				// result.getDouble("DOSAGE_QTY"))) {
				// result.setErrCode(-3);
				// result.setErrText("E0052");
				// return result;
				// }
				// }
				// 是否是管制药品
				if (result.getValue("CTRLDRUGCLASS_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", ctrlDrugClassColor);
					return result;

				}
				// 是否是抗生素
				if (result.getValue("ANTIBIOTIC_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", antibioticColor);
					return result;

				}
			}
			// 是否是医保药品
			if (OrderUtil.getInstance().isNhiPat(this.getCaseNo())) {
				if ("C".equals(parm.getValue("INSPAY_TYPE"))) {
					result.setData("TABLEROW_COLOR", nhiColor);
					return result;
				}
			}
			return result;
			// 出院带药
		case 2:
			if (!"PHA".equals(parm.getData("CAT1_TYPE").toString())) {
				result.setErrCode(-1);
				// 必须是药品！
				result.setErrText("E0169");
				return result;
			}
			// 设置出院带药
			result = this.returnOutPhaOrderData(parm, row);
			// 判断管制药品等级
			if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
				// 判断库存量(接口)ORDER_CODE,ORG_CODE(医嘱代码,药房代码)
				// if ("N".equals(parm.getValue("IS_REMARK"))) {
				// if (!INDTool.getInstance().inspectIndStock(
				// result.getValue("EXEC_DEPT_CODE"),
				// result.getValue("ORDER_CODE"),
				// result.getDouble("DOSAGE_QTY"))) {
				// result.setErrCode(-3);
				// result.setErrText("E0052");
				// return result;
				// }
				// }
				// 是否是管制药品
				if (result.getValue("CTRLDRUGCLASS_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", ctrlDrugClassColor);
					return result;

				}
				// 是否是抗生素
				if (result.getValue("ANTIBIOTIC_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", antibioticColor);
					return result;

				}
			}
			// 是否是医保药品
			if (OrderUtil.getInstance().isNhiPat(this.getCaseNo())) {
				if ("C".equals(parm.getValue("INSPAY_TYPE"))) {
					result.setData("TABLEROW_COLOR", nhiColor);
					return result;
				}
			}
			return result;
			// 中药饮片
		case 3:
			// 设置中药饮片
			result = this.returnChinaPhaOrderData(parm, row);
			// 判断管制药品等级
			if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
				// 判断库存量(接口)ORDER_CODE,ORG_CODE(医嘱代码,药房代码)
				// if ("N".equals(parm.getValue("IS_REMARK"))) {
				// if (!INDTool.getInstance().inspectIndStock(
				// result.getValue("EXEC_DEPT_CODE"),
				// result.getValue("ORDER_CODE"),
				// result.getDouble("DOSAGE_QTY"))) {
				// result.setErrCode(-3);
				// result.setErrText("E0052");
				// return result;
				// }
				// }
				// 是否是管制药品
				if (result.getValue("CTRLDRUGCLASS_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", ctrlDrugClassColor);
					return result;

				}
				// 是否是抗生素
				if (result.getValue("ANTIBIOTIC_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", antibioticColor);
					return result;

				}
			}
			// 是否是医保药品
			if (OrderUtil.getInstance().isNhiPat(this.getCaseNo())) {
				if ("C".equals(parm.getValue("INSPAY_TYPE"))) {
					result.setData("TABLEROW_COLOR", nhiColor);
					return result;
				}
			}
			return result;

			// 院前用药
		case 5:
			if (!"PHA".equals(parm.getData("CAT1_TYPE").toString())) {
				result.setErrCode(-1);
				// 院前用药不得输入此医令！
				result.setErrText("院前用药不得输入非药品信息！");
				return result;
			}
			// 设置院前用药医嘱
			result = this.returnPAData(parm, row);
			return result;
		}
		return result;
	}

	/**
	 * 返回长期医令的设置
	 * 
	 * @return TParm
	 */
	public TParm returnLongOrderData(TParm parm, int row) {
		// this.messageBox("============returnLongOrderData===================");
		TParm result = new TParm();
		// 拿到相关信息
		TParm actionParm = this.getPhaBaseData(parm.getData("ORDER_CODE")
				.toString(), parm.getData("CAT1_TYPE").toString(), "UD", parm);
		// System.out.println("================>>>>>>>>>>>>>>>拿到相关信息"+parm);
		// 区域代码
		result.setData("REGION_CODE", Operator.getRegion());
		// 病区代码
		result.setData("STATION_CODE", this.getStationCode());
		// 科室
		result.setData("DEPT_CODE", this.getDeptCode());
		// 经治医师
		result.setData("VS_DR_CODE", ((TParm) this.getParameter()).getData(
				"ODI", "VS_DR_CODE").toString());
		// 床位号
		result.setData("BED_NO", this.getBedIpdNo(caseNo).getValue("BED_NO"));
		// 住院号
		result.setData("IPD_NO", this.getBedIpdNo(caseNo).getValue("IPD_NO"));
		// 病案号
		result.setData("MR_NO", ((TParm) this.getParameter()).getData("ODI",
				"MR_NO").toString());
		// 暂存注记
		result.setData("TEMPORARY_FLG", "N");
		// 医嘱状态
		result.setData("ORDER_STATE", "N");
		// 连接医嘱主项
		if (!this.yyList) {
			result.setData("LINKMAIN_FLG", "N");
		} else {
			result.setData("LINKMAIN_FLG", parm.getData("LINKMAIN_FLG"));
		}
		// 连接医嘱
		if (!this.yyList) {
			if (row == 0) {
				result.setData("LINK_NO", "");
			} else {
				// this.messageBox("========LINKMAIN_FLG 是连接医嘱===========");
				TParm tempParm = this.getTTable(TABLE2).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0
						&& "Y".equals(this.getTTable(TABLE2).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {// shibl
					// this.messageBox("Link no====="+tempParm.getInt("LINK_NO"));
					result.setData("LINK_NO",
							tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
									.getInt("LINK_NO"));
				} else {
					result.setData("LINK_NO", "");
				}
			}
		} else {
			if (row == 0) {
				if (parm.getBoolean("LINKMAIN_FLG")) {
					result.setData("LINK_NO", getMaxLinkNo("UD"));
				} else {
					TParm tempParm = this.getTTable(TABLE2).getDataStore()
							.getRowParm(row - 1);
					if (parm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("LINK_NO",
								tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
										.getInt("LINK_NO"));
					} else {
						result.setData("LINK_NO", "");
					}
				}
			} else {
				if (parm.getBoolean("LINKMAIN_FLG")) {

					result.setData("LINK_NO", getMaxLinkNo("UD"));
				} else {
					TParm tempParm = this.getTTable(TABLE2).getDataStore()
							.getRowParm(row - 1);
					if (parm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("LINK_NO",
								tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
										.getInt("LINK_NO"));
					} else {
						result.setData("LINK_NO", "");
					}
				}
			}
		}
		// 医嘱代码
		result.setData("ORDER_CODE", parm.getData("ORDER_CODE"));
		// 医嘱名称
		result.setData("ORDER_DESC", parm.getData("ORDER_DESC").toString());
		// 医嘱名称（显示）
		result.setData("ORDER_DESCCHN", parm.getValue("ORDER_DESC")
				+ parm.getValue("GOODS_DESC") + parm.getValue("DESCRIPTION")
				+ parm.getValue("SPECIFICATION"));
		// 商品名
		result.setData("GOODS_DESC", parm.getData("GOODS_DESC"));
		// 规格
		result.setData("SPECIFICATION", parm.getData("SPECIFICATION"));
		// 英文名称
		result.setData("ORDER_ENG_DESC", parm.getData("TRADE_ENG_DESC"));
		if (!this.yyList) {
			// 开药数量
			if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
				result.setData("MEDI_QTY", parm.getDouble("MEDI_QTY") <= 0 ? 1
						: parm.getDouble("MEDI_QTY"));// 调整取值
			} else {
				// System.out.println("开药数量====="+actionParm.getData("MEDI_QTY"));
				result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
			}
		} else {
			if (parm.getData("MEDI_QTY") != null) {
				result.setData("MEDI_QTY", parm.getData("MEDI_QTY"));
			} else {
				// 开药数量
				if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
					result.setData("MEDI_QTY", 1);
				} else {
					result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
				}
			}
		}
		// 开药单位
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			// System.out.println("开药单位====="+actionParm.getData("MEDI_UNIT"));
			result.setData("MEDI_UNIT", actionParm.getData("MEDI_UNIT"));
		} else {
			result.setData("MEDI_UNIT", parm.getData("UNIT_CODE"));
		}
		if (!this.yyList) {
			// 频次代码
			if (row == 0) {
				result.setData("FREQ_CODE", actionParm.getData("FREQ_CODE"));
			} else {

				TParm tempParm = this.getTTable(TABLE2).getDataStore()
						.getRowParm(row - 1);

				// this.messageBox("========FREQ_CODE 是频次==========="+tempParm.getInt("LINK_NO"));
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						// this.messageBox("freq_code================="+tempParm.getData("FREQ_CODE"));

						result.setData("FREQ_CODE", tempParm
								.getData("FREQ_CODE"));
					} else {
						result.setData("FREQ_CODE", actionParm
								.getData("FREQ_CODE"));
					}
				} else {
					result
					.setData("FREQ_CODE", actionParm
							.getData("FREQ_CODE"));
				}
			}
		} else {
			if (parm.getData("FREQ_CODE") != null) {
				result.setData("FREQ_CODE", parm.getData("FREQ_CODE"));
			} else {
				// 频次代码
				if (row == 0) {
					result
					.setData("FREQ_CODE", actionParm
							.getData("FREQ_CODE"));
				} else {
					// this.messageBox("FREQ_CODE");

					TParm tempParm = this.getTTable(TABLE2).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE2)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {// shibl

							result.setData("FREQ_CODE", tempParm
									.getData("FREQ_CODE"));
						} else {
							result.setData("FREQ_CODE", actionParm
									.getData("FREQ_CODE"));
						}
					} else {
						result.setData("FREQ_CODE", actionParm
								.getData("FREQ_CODE"));
					}
				}
			}
		}

		// 停用时间 yanjing 20140613 yanjing 注20140613 连嘱停用时间
		if (!this.yyList) {
			if (row == 0) {
				result.setData("DC_DATE", actionParm.getData("DC_DATE"));
			} else {
				TParm tempParm = this.getTTable(TABLE2).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("DC_DATE", tempParm.getData("DC_DATE"));
					} else {
						// this.messageBox_(actionParm.getData("ROUTE_CODE"));
						result
						.setData("DC_DATE", actionParm
								.getData("DC_DATE"));
					}
				} else {
					result.setData("DC_DATE", actionParm.getData("DC_DATE"));
				}
			}
		} else {
			if (parm.getData("DC_DATE") != null) {
				result.setData("DC_DATE", parm.getData("DC_DATE"));
			} else {
				if (row == 0) {
					result.setData("DC_DATE", actionParm.getData("DC_DATE"));
				} else {

					TParm tempParm = this.getTTable(TABLE2).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE2)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {// shibl
							result.setData("DC_DATE", tempParm
									.getData("DC_DATE"));
						} else {
							// this.messageBox_(actionParm.getData("ROUTE_CODE"));
							result.setData("DC_DATE", actionParm
									.getData("DC_DATE"));
						}
					} else {
						result
						.setData("DC_DATE", actionParm
								.getData("DC_DATE"));
					}
				}
			}
		}

		if (!this.yyList) {
			if (row == 0) {
				result.setData("DC_DR_CODE", actionParm.getData("DC_DR_CODE"));
			} else {
				TParm tempParm = this.getTTable(TABLE2).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("DC_DR_CODE", tempParm
								.getData("DC_DR_CODE"));
					} else {
						// this.messageBox_(actionParm.getData("ROUTE_CODE"));
						result.setData("DC_DR_CODE", actionParm
								.getData("DC_DR_CODE"));
					}
				} else {
					result.setData("DC_DR_CODE", actionParm
							.getData("DC_DR_CODE"));
				}
			}
		} else {
			if (parm.getData("DC_DR_CODE") != null) {
				result.setData("DC_DR_CODE", parm.getData("DC_DR_CODE"));
			} else {
				if (row == 0) {
					result.setData("DC_DR_CODE", actionParm
							.getData("DC_DR_CODE"));
				} else {

					TParm tempParm = this.getTTable(TABLE2).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE2)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {// shibl
							result.setData("DC_DR_CODE", tempParm
									.getData("DC_DR_CODE"));
						} else {
							// this.messageBox_(actionParm.getData("ROUTE_CODE"));
							result.setData("DC_DR_CODE", actionParm
									.getData("DC_DR_CODE"));
						}
					} else {
						result.setData("DC_DR_CODE", actionParm
								.getData("DC_DR_CODE"));
					}
				}
			}
		}

		if (!this.yyList) {
			if (row == 0) {
				result.setData("DC_DEPT_CODE", actionParm
						.getData("DC_DEPT_CODE"));
			} else {
				TParm tempParm = this.getTTable(TABLE2).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("DC_DEPT_CODE", tempParm
								.getData("DC_DEPT_CODE"));
					} else {
						// this.messageBox_(actionParm.getData("ROUTE_CODE"));
						result.setData("DC_DEPT_CODE", actionParm
								.getData("DC_DEPT_CODE"));
					}
				} else {
					result.setData("DC_DEPT_CODE", actionParm
							.getData("DC_DEPT_CODE"));
				}
			}
		} else {
			if (parm.getData("DC_DEPT_CODE") != null) {
				result.setData("DC_DEPT_CODE", parm.getData("DC_DEPT_CODE"));
			} else {
				if (row == 0) {
					result.setData("DC_DEPT_CODE", actionParm
							.getData("DC_DEPT_CODE"));
				} else {

					TParm tempParm = this.getTTable(TABLE2).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE2)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {// shibl
							result.setData("DC_DEPT_CODE", tempParm
									.getData("DC_DEPT_CODE"));
						} else {
							// this.messageBox_(actionParm.getData("ROUTE_CODE"));
							result.setData("DC_DEPT_CODE", actionParm
									.getData("DC_DEPT_CODE"));
						}
					} else {
						result.setData("DC_DEPT_CODE", actionParm
								.getData("DC_DEPT_CODE"));
					}
				}
			}
		}

		if (!this.yyList) {
			// 用法
			if (row == 0) {
				result.setData("ROUTE_CODE", actionParm.getData("ROUTE_CODE"));
			} else {
				// this.messageBox("========ROUTE_CODE 是用法===========");
				TParm tempParm = this.getTTable(TABLE2).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("ROUTE_CODE", tempParm
								.getData("ROUTE_CODE"));
					} else {
						// this.messageBox_(actionParm.getData("ROUTE_CODE"));
						result.setData("ROUTE_CODE", actionParm
								.getData("ROUTE_CODE"));
					}
				} else {
					result.setData("ROUTE_CODE", actionParm
							.getData("ROUTE_CODE"));
				}
			}
		} else {
			if (parm.getData("ROUTE_CODE") != null) {
				result.setData("ROUTE_CODE", parm.getData("ROUTE_CODE"));
			} else {
				// 用法
				if (row == 0) {
					result.setData("ROUTE_CODE", actionParm
							.getData("ROUTE_CODE"));
				} else {
					TParm tempParm = this.getTTable(TABLE2).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE2)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {// shibl
							result.setData("ROUTE_CODE", tempParm
									.getData("ROUTE_CODE"));
						} else {
							// this.messageBox_(actionParm.getData("ROUTE_CODE"));
							result.setData("ROUTE_CODE", actionParm
									.getData("ROUTE_CODE"));
						}
					} else {
						result.setData("ROUTE_CODE", actionParm
								.getData("ROUTE_CODE"));
					}
				}
			}
		}

		// 天数
		if (row == 0) {
			result.setData("TAKE_DAYS", 1);
		} else {
			TParm tempParm = this.getTTable(TABLE2).getDataStore().getRowParm(
					row - 1);
			if (!parm.getBoolean("ORDERSET_FLG")
					&& parm.getValue("ORDERSET_CODE").length() == 0) {
				if (tempParm.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(TABLE2).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {// shibl
					result.setData("TAKE_DAYS", tempParm.getData("TAKE_DAYS"));
				} else {
					result.setData("TAKE_DAYS", 1);
				}
			} else {
				result.setData("TAKE_DAYS", 1);
			}
		}
		// 配药数量
		if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
			if (parm.getDouble("DOSAGE_QTY") <= 0)
				result.setData("DOSAGE_QTY", 1);
			else
				result.setData("DOSAGE_QTY", parm.getDouble("DOSAGE_QTY"));
		} else {
			result.setData("DOSAGE_QTY", 0);
		}
		// 配药单位
		result.setData("DOSAGE_UNIT", parm.getData("UNIT_CODE"));
		// 发药数量
		result.setData("DISPENSE_QTY", 0);
		// 发药单位
		result.setData("DISPENSE_UNIT", actionParm.getData("DISPENSE_UNIT"));
		// 盒发药注记
		result.setData("GIVEBOX_FLG", "N");
		// 续用注记
		result.setData("CONTINUOUS_FLG", "N");
		// 累用量
		result.setData("ACUMDSPN_QTY", 0);
		// 最近配药量
		result.setData("LASTDSPN_QTY", 0);
		// 医嘱预定启用日期
		if (row == 0) {
			result.setData("EFF_DATE", TJDODBTool.getInstance().getDBTime());
		} else {
			TParm temp = this.getTTable(TABLE2).getDataStore().getRowParm(
					row - 1);
			if (temp.getInt("LINK_NO") != 0
					&& "Y".equals(this.getTTable(TABLE2).getDataStore()
							.getItemString(row - 1, "#NEW#"))) {// shibl
				result.setData("EFF_DATE", temp.getData("EFF_DATE"));
			} else {
				result
				.setData("EFF_DATE", TJDODBTool.getInstance()
						.getDBTime());
			}
		}
		// 开单科室
		result.setData("ORDER_DEPT_CODE", Operator.getDept());// ====pangben
		// 2014-8-14
		// 开单医师
		result.setData("ORDER_DR_CODE", Operator.getID());
		// 执行科室
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			if (row == 0) {
				if (this.getValueString("DEPT_CODEUD").length() != 0) {// wanglong
					// modify
					// 20150504
					result.setData("EXEC_DEPT_CODE", this
							.getValue("DEPT_CODEUD"));
				} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
					result.setData("EXEC_DEPT_CODE", parm
							.getValue("EXEC_DEPT_CODE"));
				} else {
					result.setData("EXEC_DEPT_CODE", this.getOrgCode());
				}
			} else {
				TParm temp = this.getTTable(TABLE2).getDataStore().getRowParm(
						row - 1);
				if (!this.yyList) {
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& temp.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						if (this.getValueString("DEPT_CODEUD").length() != 0) {// wanglong
							// modify
							// 20150504
							result.setData("EXEC_DEPT_CODE", this
									.getValue("DEPT_CODEUD"));
						} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
							result.setData("EXEC_DEPT_CODE", parm
									.getValue("EXEC_DEPT_CODE"));
						} else {
							result.setData("EXEC_DEPT_CODE", this.getOrgCode());
						}
						// modify
						// 20130312取病人所在科室
					}
				} else {// 表单引用
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						if (this.getValueString("DEPT_CODEUD").length() != 0) {// wanglong
							// modify
							// 20150504
							result.setData("EXEC_DEPT_CODE", this
									.getValue("DEPT_CODEUD"));
						} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
							result.setData("EXEC_DEPT_CODE", parm
									.getValue("EXEC_DEPT_CODE"));
						} else {
							result.setData("EXEC_DEPT_CODE", this.getOrgCode());
						}
					} else if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& !parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						if (this.getValueString("DEPT_CODEUD").length() != 0) {// wanglong
							// modify
							// 20150504
							result.setData("EXEC_DEPT_CODE", this
									.getValue("DEPT_CODEUD"));
						} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
							result.setData("EXEC_DEPT_CODE", parm
									.getValue("EXEC_DEPT_CODE"));
						} else {
							result.setData("EXEC_DEPT_CODE", this.getOrgCode());
						}
					}
				}
			}
		} else {
			if (row == 0) {
				result.setData("EXEC_DEPT_CODE", parm
						.getValue("EXEC_DEPT_CODE").length() == 0 ? this
								.getDeptCode() : parm.getValue("EXEC_DEPT_CODE"));// shibl
				// modify
				// 20130312取病人所在科室
			} else {
				TParm temp = this.getTTable(TABLE2).getDataStore().getRowParm(
						row - 1);
				if (!this.yyList) {
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& temp.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						result.setData("EXEC_DEPT_CODE", parm.getValue(
								"EXEC_DEPT_CODE").length() == 0 ? this
										.getDeptCode() : parm
										.getValue("EXEC_DEPT_CODE"));// shibl
						// modify
						// 20130312取病人所在科室
					}
				} else {// 表单引用
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", parm.getValue(
								"EXEC_DEPT_CODE").length() == 0 ? this
										.getDeptCode() : parm
										.getValue("EXEC_DEPT_CODE"));
					} else if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& !parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						result.setData("EXEC_DEPT_CODE", parm.getValue(
								"EXEC_DEPT_CODE").length() == 0 ? this
										.getDeptCode() : parm
										.getValue("EXEC_DEPT_CODE"));// shibl
						// modify
						// 20130312取病人所在科室
					}
				}
			}
		}
		// 执行技师
		result.setData("EXEC_DR_CODE", "");
		// // 停用科室
		// result.setData("DC_DEPT_CODE", "");
		// // 停用医师
		// result.setData("DC_DR_CODE", "");
		// // 停用时间，yanjing 注
		// result.setData("DC_DATE", "");
		// 停用原因代码
		result.setData("DC_RSN_CODE", "");
		// 医师备注
		result.setData("DR_NOTE", "");
		// 护士备注
		result.setData("NS_NOTE", "");
		// 给付类别
		result.setData("INSPAY_TYPE", parm.getData("INSPAY_TYPE"));
		// 管制药品级别
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("CTRLDRUGCLASS_CODE", actionParm
					.getData("CTRLDRUGCLASS_CODE"));
		} else {
			result.setData("CTRLDRUGCLASS_CODE", "");
		}
		// 抗生素代码
		result
		.setData("ANTIBIOTIC_CODE", actionParm
				.getData("ANTIBIOTIC_CODE"));
		// 处方签号(草药使用)
		result.setData("RX_NO", "");
		// 服号(草药使用)
		result.setData("PRESRT_NO", 0);
		// 药品类型
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("PHA_TYPE", actionParm.getData("PHA_TYPE"));
		} else {
			result.setData("PHA_TYPE", "");
		}
		// 剂型类别
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("DOSE_TYPE", actionParm.getData("DOSE_CODE"));
		} else {
			result.setData("DOSE_TYPE", "");
		}
		// 饮片服用量
		result.setData("DCT_TAKE_QTY", 0);
		// 煎药方式(中药适用)
		result.setData("DCTAGENT_CODE", "");
		// 待煎包总数(中药适用)
		result.setData("PACKAGE_AMT", 0);
		// 集合医嘱主项注记
		result.setData("SETMAIN_FLG", parm.getData("ORDERSET_FLG"));
		// 审核护士
		result.setData("NS_CHECK_CODE", "");
		// 单独开立注记
		result.setData("INDV_FLG", parm.getData("INDV_FLG"));
		// 隐藏注记(集合医嘱细项处理)
		Object objHide = parm.getData("HIDE_FLG");
		if (objHide != null) {
			result.setData("HIDE_FLG", parm.getData("HIDE_FLG"));
		} else {
			result.setData("HIDE_FLG", "N");
		}
		// 集合医嘱顺序号
		Object obj = new Object();
		if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
			// result.setData("ORDERSET_GROUP_NO", getMaxOrderSetGroupNo());
			result.setData("ORDERSET_GROUP_NO", getMaxOrderGroupNo());
		} else {
			obj = parm.getData("ORDERSET_GROUP_NO");
			if (obj != null) {
				result.setData("ORDERSET_GROUP_NO", parm
						.getData("ORDERSET_GROUP_NO"));
			} else {
				result.setData("ORDERSET_GROUP_NO", 0);
			}
		}
		// 集合医嘱主项代码
		obj = parm.getData("ORDERSET_CODE");
		if (obj != null) {
			result.setData("ORDERSET_CODE", parm.getData("ORDERSET_CODE"));
		} else {
			if ("Y".equals(parm.getData("ORDERSET_FLG"))) {
				result.setData("ORDERSET_CODE", parm.getData("ORDER_CODE"));
			} else {
				result.setData("ORDERSET_CODE", "");
			}
		}
		// 医令细分类
		result.setData("ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
		// 医令分组代码
		result.setData("CAT1_TYPE", parm.getData("CAT1_TYPE"));
		// 报告类别
		result.setData("RPTTYPE_CODE", parm.getData("RPTTYPE_CODE"));
		// 体检代码
		result.setData("OPTITEM_CODE", parm.getData("OPTITEM_CODE"));
		// 申请单类别
		result.setData("MR_CODE", parm.getData("MR_CODE"));
		// FILE_NO
		result.setData("FILE_NO", "");
		// 绩效代码
		result.setData("DEGREE_CODE", parm.getData("DEGREE_CODE"));
		// 护士审核时间
		result.setData("NS_CHECK_DATE", "");
		// 审核护士DC确认
		result.setData("DC_NS_CHECK_CODE", "");
		// 审核护士DC确认时间
		result.setData("DC_NS_CHECK_DATE", "");
		// 最近摆药日期
		result.setData("LAST_DSPN_DATE", "");
		// 最近摆药量(最近或是首日量)
		result.setData("FRST_QTY", 0);
		// 审核药师
		result.setData("PHA_CHECK_CODE", "");
		// 审核时间
		result.setData("PHA_CHECK_DATE", "");
		// 静脉配液中心
		result.setData("INJ_ORG_CODE", "");
		// 急做
		result.setData("URGENT_FLG", "N");
		// 累计开药量
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("ACUMMEDI_QTY", 0);
		} else {
			result.setData("ACUMMEDI_QTY", 1);
		}
		// 首餐日期时间
		// result.setData("START_DTTM",StringTool.getString(TJDODBTool.getInstance().getDBTime(),"yyyyMMddHHmm"));
		result.setData("START_DTTM", StringTool.getTimestamp(StringTool
				.getString(TJDODBTool.getInstance().getDBTime(),
						"yyyyMMddHHmmss"), "yyyyMMddHHmmss"));
		// 医嘱执行时间(主键)
		// result.setData("ORDER_DATETIME",StringTool.getString(TJDODBTool.getInstance().getDBTime(),"yyyyMMddHHmm"));
		result.setData("ORDER_DATETIME", StringTool.getString(result
				.getTimestamp("START_DTTM"), "HHmmss"));

		// 医疗仪器
		result.setData("DEV_CODE", parm.getData("DEV_CODE"));
		// 给药注记
		result.setData("DISPENSE_FLG", "N");
		// 医嘱备注,药品备注
		result.setData("IS_REMARK", parm.getData("IS_REMARK"));
		return result;
	}

	/**
	 * 获得执行科室代码
	 * 
	 * @param orderCode
	 * @return
	 */
	public String getExeDeptCodeST(String orderCode, int row, String table,
			String linkNo) {
		// System.out.println("==========order_Code========="+orderCode);
		String sql = "SELECT CAT1_TYPE,EXEC_DEPT_CODE FROM SYS_FEE WHERE ORDER_CODE='"
				+ orderCode + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("==========parm========="+parm);
		String exeDeptCode = "";
		// 执行科室
		if ("PHA".equals(parm.getValue("CAT1_TYPE", 0))) {
			if (row == 0) {
				if (isOpeFlg()) {// wanglong add 20150422
					if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
					} else {
						exeDeptCode = this.getValueString("DEPT_CODEST");
					}
				} else if (this.getValueString("DEPT_CODEST").length() != 0) {
					exeDeptCode = this.getValueString("DEPT_CODEST");
				} else {
					exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
				}
			} else {
				TParm temp = this.getTTable(table).getDataStore().getRowParm(
						row - 1);
				if (temp.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(table).getDataStore()
								.getItemString(row - 1, "#NEW#"))
						&& temp.getValue("LINK_NO").equals(linkNo)) {
					exeDeptCode = temp.getValue("EXEC_DEPT_CODE");
				} else {
					if (isOpeFlg()) {// wanglong add 20150422
						if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
							exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
						} else {
							exeDeptCode = this.getValueString("DEPT_CODEST");
						}
					} else if (this.getValueString("DEPT_CODEST").length() != 0) {
						exeDeptCode = this.getValueString("DEPT_CODEST");
					} else {
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
					}
				}
			}
		} else {
			if (row == 0) {
				if (isOpeFlg()) {// wanglong add 20150422
					if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
					} else {
						exeDeptCode = this.getValueString("DEPT_CODEST");
					}
				} else
					exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0).length() == 0 ? this
							.getDeptCode()
							: parm.getValue("EXEC_DEPT_CODE", 0);// shibl
							// modify
							// 20130312取病人所在科室
			} else {
				TParm temp = this.getTTable(table).getDataStore().getRowParm(
						row - 1);
				if (temp.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(table).getDataStore()
								.getItemString(row - 1, "#NEW#"))
						&& temp.getValue("LINK_NO").equals(linkNo)) {
					exeDeptCode = temp.getValue("EXEC_DEPT_CODE");
				} else {
					if (isOpeFlg()) {// wanglong add 20150422
						if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
							exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
						} else {
							exeDeptCode = this.getValueString("DEPT_CODEST");
						}
					} else
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0)
						.length() == 0 ? this.getDeptCode() : parm
								.getValue("EXEC_DEPT_CODE", 0);// shibl
						// modify
						// 20130312取病人所在科室
				}
			}
		}
		// System.out.println("exeDeptCode========================"+exeDeptCode);
		return exeDeptCode;
	}

	/**
	 * 获得执行科室代码
	 * 
	 * @param orderCode
	 * @return
	 */
	public String getExeDeptCodeUD(String orderCode, int row, String table,
			String linkNo) {
		// System.out.println("==========order_Code========="+orderCode);
		String sql = "SELECT CAT1_TYPE,EXEC_DEPT_CODE FROM SYS_FEE WHERE ORDER_CODE='"
				+ orderCode + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("==========parm========="+parm);
		String exeDeptCode = "";
		// 执行科室
		if ("PHA".equals(parm.getValue("CAT1_TYPE", 0))) {
			if (row == 0) {
				if (isOpeFlg()) {// wanglong add 20150422
					if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
					} else {
						exeDeptCode = this.getValueString("DEPT_CODEUD");
					}
				} else if (this.getValueString("DEPT_CODEUD").length() != 0) {
					exeDeptCode = this.getValueString("DEPT_CODEUD");
				} else {
					exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
				}
			} else {
				TParm temp = this.getTTable(table).getDataStore().getRowParm(
						row - 1);
				if (temp.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(table).getDataStore()
								.getItemString(row - 1, "#NEW#"))
						&& temp.getValue("LINK_NO").equals(linkNo)) {
					exeDeptCode = temp.getValue("EXEC_DEPT_CODE");
				} else {
					if (isOpeFlg()) {// wanglong add 20150422
						if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
							exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
						} else {
							exeDeptCode = this.getValueString("DEPT_CODEUD");
						}
					} else if (this.getValueString("DEPT_CODEUD").length() != 0) {
						exeDeptCode = this.getValueString("DEPT_CODEUD");
					} else {
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
					}
				}
			}
		} else {
			if (row == 0) {
				if (isOpeFlg()) {// wanglong add 20150422
					if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
					} else {
						exeDeptCode = this.getValueString("DEPT_CODEUD");
					}
				} else
					exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0).length() == 0 ? this
							.getDeptCode()
							: parm.getValue("EXEC_DEPT_CODE", 0);// shibl
							// modify
							// 20130312取病人所在科室
			} else {
				TParm temp = this.getTTable(table).getDataStore().getRowParm(
						row - 1);
				if (temp.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(table).getDataStore()
								.getItemString(row - 1, "#NEW#"))
						&& temp.getValue("LINK_NO").equals(linkNo)) {
					exeDeptCode = temp.getValue("EXEC_DEPT_CODE");
				} else {
					if (isOpeFlg()) {// wanglong add 20150422
						if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
							exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
						} else {
							exeDeptCode = this.getValueString("DEPT_CODEUD");
						}
					} else
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0)
						.length() == 0 ? this.getDeptCode() : parm
								.getValue("EXEC_DEPT_CODE", 0);// shibl
						// modify
						// 20130312取病人所在科室
				}
			}
		}
		// System.out.println("exeDeptCode========================"+exeDeptCode);
		return exeDeptCode;
	}

	/**
	 * 获取临床路径的时程
	 * 
	 */
	private String getClpCode() {
		String clpCode = "";
		String sql = "SELECT CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"
				+ caseNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {
			clpCode = parm.getValue("CLNCPATH_CODE", 0);
		}
		return clpCode;
	}

	/**
	 * 获取临床路径
	 * 
	 */
	private String getSchdCode() {
		String schdCode = "";
		String sql = "SELECT SCHD_CODE FROM ADM_INP WHERE CASE_NO = '" + caseNo
				+ "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {
			schdCode = parm.getValue("SCHD_CODE", 0);
		}
		return schdCode;
	}

	/**
	 * 返回临时医令的设置
	 * 
	 * @return TParm
	 */
	public TParm returnTempOrderData(TParm parm, int row) {
		TParm result = new TParm();
		// 拿到相关信息
		TParm actionParm = this.getPhaBaseData(parm.getData("ORDER_CODE")
				.toString(), parm.getData("CAT1_TYPE").toString(), "ST", parm);
		// System.out.println("================>>>>>>>>>>>>>>>拿到相关信息"+actionParm);
		// this.messageBox_(actionParm.getValue("DOSE_CODE"));
		if (opeClpFlg) {
			result.setData("SCHD_CODE", this.getValue("SCHD_CODE"));// 时程代码
			// pangben
			// 20150814
		} else {
			result.setData("SCHD_CODE", getSchdCode());// 时程代码 yanjing 20140902
		}

		// 区域代码
		result.setData("REGION_CODE", Operator.getRegion());
		// 病区代码
		result.setData("STATION_CODE", this.getStationCode());
		// 科室
		result.setData("DEPT_CODE", this.getDeptCode());
		// 经治医师
		result.setData("VS_DR_CODE", ((TParm) this.getParameter()).getData(
				"ODI", "VS_DR_CODE").toString());
		// 床位号
		result.setData("BED_NO", this.getBedIpdNo(caseNo).getValue("BED_NO"));
		// 住院号
		result.setData("IPD_NO", this.getBedIpdNo(caseNo).getValue("IPD_NO"));
		// 病案号
		result.setData("MR_NO", ((TParm) this.getParameter()).getData("ODI",
				"MR_NO").toString());
		// 暂存注记
		result.setData("TEMPORARY_FLG", "N");
		// 医嘱状态
		result.setData("ORDER_STATE", "N");
		// 连接医嘱主项
		if (!this.yyList) {
			result.setData("LINKMAIN_FLG", "N");
		} else {
			result.setData("LINKMAIN_FLG", parm.getData("LINKMAIN_FLG"));
		}
		// 英文名称
		result.setData("ORDER_ENG_DESC", parm.getData("TRADE_ENG_DESC"));
		// 连接医嘱
		if (!this.yyList) {
			if (row == 0) {
				result.setData("LINK_NO", "");
			} else {
				TParm tempParm = this.getTTable(TABLE1).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0
						&& "Y".equals(this.getTTable(TABLE1).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {
					result.setData("LINK_NO",
							tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
									.getInt("LINK_NO"));
				} else {
					result.setData("LINK_NO", "");
				}
			}
		} else {
			if (row == 0) {
				if (parm.getBoolean("LINKMAIN_FLG")) {
					result.setData("LINK_NO", getMaxLinkNo("ST"));
				} else {
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {
						TParm tempParm = this.getTTable(TABLE1).getDataStore()
								.getRowParm(row - 1);
						result.setData("LINK_NO",
								tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
										.getInt("LINK_NO"));

					} else {
						result.setData("LINK_NO", "");

					}
				}
			} else {
				if (parm.getBoolean("LINKMAIN_FLG")) {
					result.setData("LINK_NO", getMaxLinkNo("ST"));

				} else {
					TParm tempParm = this.getTTable(TABLE1).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {
						result.setData("LINK_NO",
								tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
										.getInt("LINK_NO"));

					} else {
						result.setData("LINK_NO", "");

					}
				}
			}
		}
		// 医嘱代码
		result.setData("ORDER_CODE", parm.getData("ORDER_CODE"));
		// 医嘱名称
		result.setData("ORDER_DESC", parm.getData("ORDER_DESC").toString());
		// 医嘱名称（显示）
		result.setData("ORDER_DESCCHN", parm.getValue("ORDER_DESC")
				+ parm.getValue("GOODS_DESC") + parm.getValue("DESCRIPTION")
				+ parm.getValue("SPECIFICATION"));
		// 商品名
		result.setData("GOODS_DESC", parm.getData("GOODS_DESC"));
		// 规格
		result.setData("SPECIFICATION", parm.getData("SPECIFICATION"));
		if (!this.yyList) {
			// 开药数量
			if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
				result.setData("MEDI_QTY", parm.getDouble("MEDI_QTY") <= 0 ? 1
						: parm.getDouble("MEDI_QTY"));// 调整取值
			} else {
				// this.messageBox_(actionParm.getData("MEDI_QTY"));
				result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
			}
		} else {
			if (parm.getData("MEDI_QTY") != null) {
				result.setData("MEDI_QTY", parm.getData("MEDI_QTY"));
			} else {
				if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
					result.setData("MEDI_QTY", 1);
				} else {
					result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
				}
			}
		}
		// 开药单位
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("MEDI_UNIT", actionParm.getData("MEDI_UNIT"));
		} else {
			result.setData("MEDI_UNIT", parm.getData("UNIT_CODE"));
		}
		if (!this.yyList) {
			// 频次代码
			if (row == 0) {
				result.setData("FREQ_CODE", actionParm.getData("FREQ_CODE"));
			} else {
				TParm tempParm = this.getTTable(TABLE1).getDataStore()
						.getRowParm(row - 1);
				// System.out.println("tempParm"+tempParm);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("FREQ_CODE", tempParm
								.getData("FREQ_CODE"));
					} else {
						result.setData("FREQ_CODE", actionParm
								.getData("FREQ_CODE"));
					}
				} else {
					result
					.setData("FREQ_CODE", actionParm
							.getData("FREQ_CODE"));
				}
			}
		} else {
			if (parm.getData("FREQ_CODE") != null) {
				result.setData("FREQ_CODE", parm.getData("FREQ_CODE"));
			} else {
				// 频次代码
				if (row == 0) {
					result
					.setData("FREQ_CODE", actionParm
							.getData("FREQ_CODE"));
				} else {
					TParm tempParm = this.getTTable(TABLE1).getDataStore()
							.getRowParm(row - 1);
					// System.out.println("tempParm"+tempParm);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE1)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {// shibl
							result.setData("FREQ_CODE", tempParm
									.getData("FREQ_CODE"));
						} else {
							result.setData("FREQ_CODE", actionParm
									.getData("FREQ_CODE"));
						}
					} else {
						result.setData("FREQ_CODE", actionParm
								.getData("FREQ_CODE"));
					}
				}
			}
		}
		if (!this.yyList) {
			// 用法
			if (row == 0) {
				result.setData("ROUTE_CODE", actionParm.getValue("ROUTE_CODE"));
			} else {
				TParm tempParm = this.getTTable(TABLE1).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("ROUTE_CODE", tempParm
								.getValue("ROUTE_CODE"));
					} else {
						result.setData("ROUTE_CODE", actionParm
								.getValue("ROUTE_CODE"));
					}
				} else {
					result.setData("ROUTE_CODE", actionParm
							.getValue("ROUTE_CODE"));
				}
			}
		} else {
			if (parm.getData("ROUTE_CODE") != null) {
				result.setData("ROUTE_CODE", parm.getValue("ROUTE_CODE"));
			} else {
				// 用法
				if (row == 0) {
					result.setData("ROUTE_CODE", actionParm
							.getValue("ROUTE_CODE"));
				} else {
					TParm tempParm = this.getTTable(TABLE1).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE1)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {// shibl
							result.setData("ROUTE_CODE", tempParm
									.getValue("ROUTE_CODE"));
						} else {
							result.setData("ROUTE_CODE", actionParm
									.getValue("ROUTE_CODE"));
						}
					} else {
						result.setData("ROUTE_CODE", actionParm
								.getValue("ROUTE_CODE"));
					}
				}
			}
		}
		// 天数
		if (row == 0) {
			result.setData("TAKE_DAYS", 1);
		} else {
			TParm tempParm = this.getTTable(TABLE1).getDataStore().getRowParm(
					row - 1);
			if (!parm.getBoolean("ORDERSET_FLG")
					&& parm.getValue("ORDERSET_CODE").length() == 0) {
				if (tempParm.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(TABLE1).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {// shibl
					result.setData("TAKE_DAYS", tempParm.getData("TAKE_DAYS"));
				} else {
					result.setData("TAKE_DAYS", 1);
				}
			} else {
				result.setData("TAKE_DAYS", 1);
			}
		}
		// 配药数量
		if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
			if (parm.getDouble("DOSAGE_QTY") <= 0)
				result.setData("DOSAGE_QTY", 1);
			else
				result.setData("DOSAGE_QTY", parm.getDouble("DOSAGE_QTY"));
		} else {
			result.setData("DOSAGE_QTY", 0);
		}
		// 配药单位
		result.setData("DOSAGE_UNIT", parm.getData("UNIT_CODE"));
		// 发药数量
		result.setData("DISPENSE_QTY", 0);
		// 发药单位
		result.setData("DISPENSE_UNIT", actionParm.getData("DISPENSE_UNIT"));
		// 盒发药注记
		result.setData("GIVEBOX_FLG", "N");
		// 续用注记
		result.setData("CONTINUOUS_FLG", "N");
		// 累用量
		result.setData("ACUMDSPN_QTY", 0);
		// 最近配药量
		result.setData("LASTDSPN_QTY", 0);
		// 医嘱预定启用日期
		if (row == 0) {
			// $$================集合医嘱时，这有问题=====================$$//
			result.setData("EFF_DATE", TJDODBTool.getInstance().getDBTime());
		} else {
			TParm temp = this.getTTable(TABLE1).getDataStore().getRowParm(
					row - 1);
			if (temp.getInt("LINK_NO") != 0
					&& "Y".equals(this.getTTable(TABLE1).getDataStore()
							.getItemString(row - 1, "#NEW#"))) {
				result.setData("EFF_DATE", temp.getData("EFF_DATE"));
			} else {
				result
				.setData("EFF_DATE", TJDODBTool.getInstance()
						.getDBTime());
			}
		}
		// 开单科室
		result.setData("ORDER_DEPT_CODE", Operator.getDept());// ====pangben
		// 2014-8-14
		// 开单医师

		// add by huangjw
		if (((TParm) this.getParameter()).getData("ODI", "OPERATOR") != null
				&& ((TParm) this.getParameter()).getData("ODI", "OPERATOR")
				.equals("OPE_N")) {
			Date date = TJDODBTool.getInstance().getDBTime();
			if (row == 0) {
				result.setData("ORDER_DR_CODE", ((TParm) this.getParameter())
						.getData("ODI", "ORDER_DR_CODE"));
				result.setData("ORDER_DATE", date);
				result.setData("NS_CHECK_DATE", date);
			} else {
				TParm nParm = this.getTTable(TABLE1).getDataStore().getRowParm(
						row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (nParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("ORDER_DR_CODE", nParm
								.getData("ORDER_DR_CODE"));
						result.setData("ORDER_DATE", nParm
								.getTimestamp("ORDER_DATE"));
						result.setData("NS_CHECK_DATE", nParm
								.getTimestamp("NS_CHECK_DATE"));
					} else {
						result.setData("ORDER_DR_CODE", ((TParm) this
								.getParameter())
								.getData("ODI", "ORDER_DR_CODE"));
						result.setData("ORDER_DATE", date);
						result.setData("NS_CHECK_DATE", date);
					}
				} else {
					result.setData("ORDER_DR_CODE", ((TParm) this
							.getParameter()).getData("ODI", "ORDER_DR_CODE"));
					result.setData("ORDER_DATE", date);
					result.setData("NS_CHECK_DATE", date);
				}
			}
			// add by huangjw
		} else {
			result.setData("ORDER_DR_CODE", Operator.getID());
			// 护士审核时间
			result.setData("NS_CHECK_DATE", "");
		}
		// 执行科室
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			if (row == 0) {
				if (isOpeFlg()) {// wanglong add 20150422
					if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
						result.setData("EXEC_DEPT_CODE", parm
								.getValue("EXEC_DEPT_CODE"));
					} else if (this.getValueString("DEPT_CODEST").length() != 0) {
						result.setData("EXEC_DEPT_CODE", this
								.getValue("DEPT_CODEST"));
					} else {
						result.setData("EXEC_DEPT_CODE", this.getOrgCode());
					}
				} else if (this.getValueString("DEPT_CODEST").length() != 0) {// wanglong
					// modify
					// 20150504
					result.setData("EXEC_DEPT_CODE", this
							.getValue("DEPT_CODEST"));
				} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
					result.setData("EXEC_DEPT_CODE", parm
							.getValue("EXEC_DEPT_CODE"));
				} else {
					result.setData("EXEC_DEPT_CODE", this.getOrgCode());
				}
			} else {
				TParm temp = this.getTTable(TABLE1).getDataStore().getRowParm(
						row - 1);
				/**
				 * if (!parm.getBoolean("ORDERSET_FLG") &&
				 * parm.getValue("ORDERSET_CODE").length() == 0 &&
				 * temp.getInt("LINK_NO") != 0 &&
				 * "Y".equals(this.getTTable(TABLE1).getDataStore()
				 * .getItemString(row - 1, "#NEW#"))) {// shibl
				 * result.setData("EXEC_DEPT_CODE", temp
				 * .getValue("EXEC_DEPT_CODE")); } else { if
				 * (this.getValueString("DEPT_CODEST").length() == 0) {
				 * result.setData("EXEC_DEPT_CODE", this.getOrgCode()); } else {
				 * result.setData("EXEC_DEPT_CODE", this
				 * .getValue("DEPT_CODEST")); } }
				 */
				if (!this.yyList) {
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& temp.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						if (isOpeFlg()) {// wanglong add 20150422
							if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
								result.setData("EXEC_DEPT_CODE", parm
										.getValue("EXEC_DEPT_CODE"));
							} else if (this.getValueString("DEPT_CODEST")
									.length() != 0) {
								result.setData("EXEC_DEPT_CODE", this
										.getValue("DEPT_CODEST"));
							} else {
								result.setData("EXEC_DEPT_CODE", this
										.getOrgCode());
							}
						} else if (this.getValueString("DEPT_CODEST").length() != 0) {// wanglong
							// modify
							// 20150504
							result.setData("EXEC_DEPT_CODE", this
									.getValue("DEPT_CODEST"));
						} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
							result.setData("EXEC_DEPT_CODE", parm
									.getValue("EXEC_DEPT_CODE"));
						} else {
							result.setData("EXEC_DEPT_CODE", this.getOrgCode());
						}
						// modify
						// 20130312取病人所在科室
					}
				} else {// 表单引用
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						if (isOpeFlg()) {// wanglong add 20150422
							if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
								result.setData("EXEC_DEPT_CODE", parm
										.getValue("EXEC_DEPT_CODE"));
							} else if (this.getValueString("DEPT_CODEST")
									.length() != 0) {
								result.setData("EXEC_DEPT_CODE", this
										.getValue("DEPT_CODEST"));
							} else {
								result.setData("EXEC_DEPT_CODE", this
										.getOrgCode());
							}
						} else if (this.getValueString("DEPT_CODEST").length() != 0) {// wanglong
							// modify
							// 20150504
							result.setData("EXEC_DEPT_CODE", this
									.getValue("DEPT_CODEST"));
						} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
							result.setData("EXEC_DEPT_CODE", parm
									.getValue("EXEC_DEPT_CODE"));
						} else {
							result.setData("EXEC_DEPT_CODE", this.getOrgCode());
						}
					} else if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& !parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						if (isOpeFlg()) {// wanglong add 20150422
							if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
								result.setData("EXEC_DEPT_CODE", parm
										.getValue("EXEC_DEPT_CODE"));
							} else if (this.getValueString("DEPT_CODEST")
									.length() != 0) {
								result.setData("EXEC_DEPT_CODE", this
										.getValue("DEPT_CODEST"));
							} else {
								result.setData("EXEC_DEPT_CODE", this
										.getOrgCode());
							}
						} else if (this.getValueString("DEPT_CODEST").length() != 0) {// wanglong
							// modify
							// 20150504
							result.setData("EXEC_DEPT_CODE", this
									.getValue("DEPT_CODEST"));
						} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
							result.setData("EXEC_DEPT_CODE", parm
									.getValue("EXEC_DEPT_CODE"));
						} else {
							result.setData("EXEC_DEPT_CODE", this.getOrgCode());
						}
					}
				}
			}
		} else {
			if (row == 0) {
				if (isOpeFlg()) {// wanglong add 20150422
					if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
						result.setData("EXEC_DEPT_CODE", parm
								.getValue("EXEC_DEPT_CODE"));
					} else if (this.getValueString("DEPT_CODEST").length() != 0) {
						result.setData("EXEC_DEPT_CODE", this
								.getValue("DEPT_CODEST"));
					} else {
						result.setData("EXEC_DEPT_CODE", this.getDeptCode());
					}
				} else
					result.setData("EXEC_DEPT_CODE", parm.getValue(
							"EXEC_DEPT_CODE").length() == 0 ? this
									.getDeptCode() : parm.getValue("EXEC_DEPT_CODE"));// shibl
				// modify
				// 20130312取病人所在科室
			} else {
				TParm temp = this.getTTable(TABLE1).getDataStore().getRowParm(
						row - 1);
				if (!this.yyList) {
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& temp.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						if (isOpeFlg()) {// wanglong add 20150422
							if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
								result.setData("EXEC_DEPT_CODE", parm
										.getValue("EXEC_DEPT_CODE"));
							} else if (this.getValueString("DEPT_CODEST")
									.length() != 0) {
								result.setData("EXEC_DEPT_CODE", this
										.getValue("DEPT_CODEST"));
							} else {
								result.setData("EXEC_DEPT_CODE", this
										.getDeptCode());
							}
						} else
							result.setData("EXEC_DEPT_CODE", parm.getValue(
									"EXEC_DEPT_CODE").length() == 0 ? this
											.getDeptCode() : parm
											.getValue("EXEC_DEPT_CODE"));// shibl
						// modify
						// 20130312取病人所在科室
					}
				} else {// 表单引用
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						if (isOpeFlg()) {// wanglong add 20150422
							if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
								result.setData("EXEC_DEPT_CODE", parm
										.getValue("EXEC_DEPT_CODE"));
							} else if (this.getValueString("DEPT_CODEST")
									.length() != 0) {
								result.setData("EXEC_DEPT_CODE", this
										.getValue("DEPT_CODEST"));
							} else {
								result.setData("EXEC_DEPT_CODE", this
										.getDeptCode());
							}
						} else
							result.setData("EXEC_DEPT_CODE", parm.getValue(
									"EXEC_DEPT_CODE").length() == 0 ? this
											.getDeptCode() : parm
											.getValue("EXEC_DEPT_CODE"));
					} else if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& !parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						if (isOpeFlg()) {// wanglong add 20150422
							if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
								result.setData("EXEC_DEPT_CODE", parm
										.getValue("EXEC_DEPT_CODE"));
							} else if (this.getValueString("DEPT_CODEST")
									.length() != 0) {
								result.setData("EXEC_DEPT_CODE", this
										.getValue("DEPT_CODEST"));
							} else {
								result.setData("EXEC_DEPT_CODE", this
										.getDeptCode());
							}
						} else
							result.setData("EXEC_DEPT_CODE", parm.getValue(
									"EXEC_DEPT_CODE").length() == 0 ? this
											.getDeptCode() : parm
											.getValue("EXEC_DEPT_CODE"));// shibl
						// modify
						// 20130312取病人所在科室
					}
				}
			}
		}
		// 执行技师
		result.setData("EXEC_DR_CODE", "");
		// 停用科室
		result.setData("DC_DEPT_CODE", "");
		// 停用医师
		result.setData("DC_DR_CODE", "");
		// 停用时间
		result.setData("DC_DATE", "");
		// 停用原因代码
		result.setData("DC_RSN_CODE", "");
		// 医师备注
		result.setData("DR_NOTE", "");
		// 护士备注
		result.setData("NS_NOTE", "");
		// 给付类别
		result.setData("INSPAY_TYPE", parm.getData("INSPAY_TYPE"));
		// 管制药品级别
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("CTRLDRUGCLASS_CODE", actionParm
					.getData("CTRLDRUGCLASS_CODE"));
		} else {
			result.setData("CTRLDRUGCLASS_CODE", "");
		}
		// 抗生素代码
		result
		.setData("ANTIBIOTIC_CODE", actionParm
				.getData("ANTIBIOTIC_CODE"));
		// 处方签号(草药使用)
		result.setData("RX_NO", "");
		// 服号(草药使用)
		result.setData("PRESRT_NO", 0);
		// 药品类型
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("PHA_TYPE", actionParm.getData("PHA_TYPE"));
		} else {
			result.setData("PHA_TYPE", "");
		}
		// 剂型类别
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("DOSE_TYPE", actionParm.getData("DOSE_CODE"));
		} else {
			result.setData("DOSE_TYPE", "");
		}
		// 饮片服用量
		result.setData("DCT_TAKE_QTY", 0);
		// 煎药方式(中药适用)
		result.setData("DCTAGENT_CODE", "");
		// 待煎包总数(中药适用)
		result.setData("PACKAGE_AMT", 0);
		// 集合医嘱主项注记
		result.setData("SETMAIN_FLG", parm.getData("ORDERSET_FLG"));
		// 审核护士
		result.setData("NS_CHECK_CODE", "");
		// 单独开立注记
		result.setData("INDV_FLG", parm.getData("INDV_FLG"));
		// 隐藏注记(集合医嘱细项处理)
		Object objHide = parm.getData("HIDE_FLG");
		if (objHide != null) {
			result.setData("HIDE_FLG", parm.getData("HIDE_FLG"));
		} else {
			result.setData("HIDE_FLG", "N");
		}
		// 集合医嘱顺序号
		Object obj = new Object();
		if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
			result.setData("ORDERSET_GROUP_NO", getMaxOrderGroupNo());
		} else {
			obj = parm.getData("ORDERSET_GROUP_NO");
			if (obj != null) {
				result.setData("ORDERSET_GROUP_NO", parm
						.getData("ORDERSET_GROUP_NO"));
			} else {
				result.setData("ORDERSET_GROUP_NO", 0);
			}
		}
		// 集合医嘱主项代码
		obj = parm.getData("ORDERSET_CODE");
		if (obj != null) {
			result.setData("ORDERSET_CODE", parm.getData("ORDERSET_CODE"));
		} else {
			if ("Y".equals(parm.getData("ORDERSET_FLG"))) {
				result.setData("ORDERSET_CODE", parm.getData("ORDER_CODE"));
			} else {
				result.setData("ORDERSET_CODE", "");
			}
		}
		// 医令细分类
		result.setData("ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
		// 医令分组代码
		result.setData("CAT1_TYPE", parm.getData("CAT1_TYPE"));
		// 报告类别
		result.setData("RPTTYPE_CODE", parm.getData("RPTTYPE_CODE"));
		// 体检代码
		result.setData("OPTITEM_CODE", parm.getData("OPTITEM_CODE"));
		// 申请单类别
		result.setData("MR_CODE", parm.getData("MR_CODE"));
		// FILE_NO
		result.setData("FILE_NO", "");
		// 绩效代码
		result.setData("DEGREE_CODE", parm.getData("DEGREE_CODE"));

		// 审核护士DC确认
		result.setData("DC_NS_CHECK_CODE", "");
		// 审核护士DC确认时间
		result.setData("DC_NS_CHECK_DATE", "");
		// 首餐日期时间
		// result.setData("START_DTTM",StringTool.getString(TJDODBTool.getInstance().getDBTime(),"yyyyMMddHHmm"));
		result.setData("START_DTTM", StringTool.getTimestamp(StringTool
				.getString(TJDODBTool.getInstance().getDBTime(),
						"yyyyMMddHHmmss"), "yyyyMMddHHmmss"));
		// 医嘱执行时间(主键)
		// result.setData("ORDER_DATETIME",StringTool.getString(TJDODBTool.getInstance().getDBTime(),"yyyyMMddHHmm"));
		result.setData("ORDER_DATETIME", StringTool.getString(result
				.getTimestamp("START_DTTM"), "HHmmss"));
		// 最近摆药日期
		result.setData("LAST_DSPN_DATE", "");
		// 最近摆药量(最近或是首日量)
		result.setData("FRST_QTY", 0);
		// 审核药师
		result.setData("PHA_CHECK_CODE", "");
		// 审核时间
		result.setData("PHA_CHECK_DATE", "");
		// 静脉配液中心
		result.setData("INJ_ORG_CODE", "");
		// 急做
		result.setData("URGENT_FLG", "N");
		// 累计开药量
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("ACUMMEDI_QTY", 0);
		} else {
			result.setData("ACUMMEDI_QTY", 1);
		}
		// 医疗仪器
		result.setData("DEV_CODE", parm.getData("DEV_CODE"));
		// 给药注记
		result.setData("DISPENSE_FLG", "N");
		// 医嘱备注,药品备注
		result.setData("IS_REMARK", parm.getData("IS_REMARK"));
		// 手术申请单号 wanglong add 20140707
		result.setData("OPBOOK_SEQ", this.getOpBookSeq());

		return result;
	}

	/**
	 * 返回出院带药医令的设置
	 * 
	 * @return TParm
	 */
	public TParm returnOutPhaOrderData(TParm parm, int row) {
		TParm result = new TParm();
		// 拿到相关信息
		TParm actionParm = this.getPhaBaseData(parm.getData("ORDER_CODE")
				.toString(), parm.getData("CAT1_TYPE").toString(), "DS", parm);
		// System.out.println("================>>>>>>>>>>>>>>>拿到相关信息"+actionParm);
		// 区域代码
		result.setData("REGION_CODE", Operator.getRegion());
		// 病区代码
		result.setData("STATION_CODE", this.getStationCode());
		// 科室
		result.setData("DEPT_CODE", this.getDeptCode());
		// 经治医师
		result.setData("VS_DR_CODE", ((TParm) this.getParameter()).getData(
				"ODI", "VS_DR_CODE").toString());
		// 床位号
		result.setData("BED_NO", this.getBedIpdNo(caseNo).getValue("BED_NO"));
		// 住院号
		result.setData("IPD_NO", this.getBedIpdNo(caseNo).getValue("IPD_NO"));
		// 病案号
		result.setData("MR_NO", ((TParm) this.getParameter()).getData("ODI",
				"MR_NO").toString());
		// 暂存注记
		result.setData("TEMPORARY_FLG", "N");
		// 医嘱状态
		result.setData("ORDER_STATE", "N");
		// 连接医嘱主项
		if (!this.yyList) {
			result.setData("LINKMAIN_FLG", "N");
		} else {
			result.setData("LINKMAIN_FLG", parm.getData("LINKMAIN_FLG"));
		}
		if (!this.yyList) {
			if (row == 0) {
				result.setData("LINK_NO", "");
			} else {
				TParm tempParm = this.getTTable(TABLE3).getDataStore()
						.getRowParm(row - 1);
				if ("Y".equals(this.getTTable(TABLE3).getDataStore()
						.getItemString(row - 1, "#NEW#"))) {
					result.setData("LINK_NO",
							tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
									.getInt("LINK_NO"));
				} else {
					result.setData("LINK_NO", "");
				}
			}
		} else {
			if (row == 0) {
				if (parm.getBoolean("LINKMAIN_FLG")) {
					result.setData("LINK_NO", getMaxLinkNo("DS"));
				} else {
					if (parm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE3).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {
						TParm tempParm = this.getTTable(TABLE3).getDataStore()
								.getRowParm(row - 1);
						result.setData("LINK_NO",
								tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
										.getInt("LINK_NO"));
					} else {
						result.setData("LINK_NO", "");
					}
				}
			} else {
				if (parm.getBoolean("LINKMAIN_FLG")) {
					result.setData("LINK_NO", getMaxLinkNo("DS"));
				} else {
					if (parm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE3).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {
						TParm tempParm = this.getTTable(TABLE3).getDataStore()
								.getRowParm(row - 1);
						result.setData("LINK_NO",
								tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
										.getInt("LINK_NO"));
					} else {
						result.setData("LINK_NO", "");
					}
				}
			}
		}
		// 医嘱代码
		result.setData("ORDER_CODE", parm.getData("ORDER_CODE"));
		// 医嘱名称
		result.setData("ORDER_DESC", parm.getData("ORDER_DESC").toString());
		// 医嘱名称（显示）
		result.setData("ORDER_DESCCHN", parm.getValue("ORDER_DESC")
				+ parm.getValue("GOODS_DESC") + parm.getValue("DESCRIPTION")
				+ parm.getValue("SPECIFICATION"));
		// 商品名
		result.setData("GOODS_DESC", parm.getData("GOODS_DESC"));
		// 规格
		result.setData("SPECIFICATION", parm.getData("SPECIFICATION"));
		// 英文名称
		result.setData("ORDER_ENG_DESC", parm.getData("TRADE_ENG_DESC"));
		if (!this.yyList) {
			// 开药数量
			result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
		} else {
			if (parm.getData("MEDI_QTY") != null) {
				result.setData("MEDI_QTY", parm.getData("MEDI_QTY"));
			} else {
				// 开药数量
				result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
			}
		}
		// 开药单位
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("MEDI_UNIT", actionParm.getData("MEDI_UNIT"));
		} else {
			result.setData("MEDI_UNIT", parm.getData("UNIT_CODE"));
		}
		if (!this.yyList) {
			// 频次代码
			if (row == 0) {
				result.setData("FREQ_CODE", actionParm.getData("FREQ_CODE"));
			} else {
				TParm tempParm = this.getTTable(TABLE3).getDataStore()
						.getRowParm(row - 1);
				if (tempParm.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(TABLE3).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {
					result.setData("FREQ_CODE", tempParm.getData("FREQ_CODE"));
				} else {
					result
					.setData("FREQ_CODE", actionParm
							.getData("FREQ_CODE"));
				}
			}
		} else {
			if (parm.getData("FREQ_CODE") != null) {
				result.setData("FREQ_CODE", parm.getData("FREQ_CODE"));
			} else {
				// 频次代码
				if (row == 0) {
					result
					.setData("FREQ_CODE", actionParm
							.getData("FREQ_CODE"));
				} else {
					TParm tempParm = this.getTTable(TABLE3).getDataStore()
							.getRowParm(row - 1);
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE3).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {
						result.setData("FREQ_CODE", tempParm
								.getData("FREQ_CODE"));
					} else {
						result.setData("FREQ_CODE", actionParm
								.getData("FREQ_CODE"));
					}
				}
			}
		}
		if (!this.yyList) {
			// 用法
			if (row == 0) {
				result.setData("ROUTE_CODE", actionParm.getData("ROUTE_CODE"));
			} else {
				TParm tempParm = this.getTTable(TABLE3).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE3).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {
						result.setData("ROUTE_CODE", tempParm
								.getData("ROUTE_CODE"));
					} else {
						result.setData("ROUTE_CODE", actionParm
								.getData("ROUTE_CODE"));
					}
				} else {
					result.setData("ROUTE_CODE", actionParm
							.getData("ROUTE_CODE"));
				}
			}
		} else {
			if (parm.getData("ROUTE_CODE") != null) {
				result.setData("ROUTE_CODE", parm.getData("ROUTE_CODE"));
			} else {
				// 用法
				if (row == 0) {
					result.setData("ROUTE_CODE", actionParm
							.getData("ROUTE_CODE"));
				} else {
					TParm tempParm = this.getTTable(TABLE3).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE3)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {
							result.setData("ROUTE_CODE", tempParm
									.getData("ROUTE_CODE"));
						} else {
							result.setData("ROUTE_CODE", actionParm
									.getData("ROUTE_CODE"));
						}
					} else {
						result.setData("ROUTE_CODE", actionParm
								.getData("ROUTE_CODE"));
					}
				}
			}
		}
		if (!this.yyList) {
			// 天数
			if (row == 0) {
				result.setData("TAKE_DAYS", 0);
			} else {
				TParm tempParm = this.getTTable(TABLE3).getDataStore()
						.getRowParm(row - 1);
				if (tempParm.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(TABLE3).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {
					result.setData("TAKE_DAYS", tempParm.getData("TAKE_DAYS"));
				} else {
					result.setData("TAKE_DAYS", 0);
				}
			}
		} else {
			if (parm.getData("TAKE_DAYS") != null) {
				result.setData("TAKE_DAYS", parm.getData("TAKE_DAYS"));
			} else {
				// 天数
				if (row == 0) {
					result.setData("TAKE_DAYS", 0);
				} else {
					TParm tempParm = this.getTTable(TABLE3).getDataStore()
							.getRowParm(row - 1);
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE3).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {
						result.setData("TAKE_DAYS", tempParm
								.getData("TAKE_DAYS"));
					} else {
						result.setData("TAKE_DAYS", 0);
					}
				}
			}
		}
		// 配药数量
		if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
			if (parm.getDouble("DOSAGE_QTY") <= 0)
				result.setData("DOSAGE_QTY", 1);
			else
				result.setData("DOSAGE_QTY", parm.getDouble("DOSAGE_QTY"));
		} else {
			result.setData("DOSAGE_QTY", 0);
		}
		// 配药单位
		result.setData("DOSAGE_UNIT", parm.getData("UNIT_CODE"));
		// 发药数量
		result.setData("DISPENSE_QTY", 0);
		// 发药单位
		result.setData("DISPENSE_UNIT", actionParm.getData("DISPENSE_UNIT"));
		// 盒发药注记
		result.setData("GIVEBOX_FLG", "N");
		// 续用注记
		result.setData("CONTINUOUS_FLG", "N");
		// 累用量
		result.setData("ACUMDSPN_QTY", 0);
		// 最近配药量
		result.setData("LASTDSPN_QTY", 0);
		// 医嘱预定启用日期
		if (row == 0) {
			result.setData("EFF_DATE", TJDODBTool.getInstance().getDBTime());
		} else {
			TParm temp = this.getTTable(TABLE3).getDataStore().getRowParm(
					row - 1);
			if (temp.getInt("LINK_NO") != 0
					&& "Y".equals(this.getTTable(TABLE3).getDataStore()
							.getItemString(row - 1, "#NEW#"))) {
				result.setData("EFF_DATE", temp.getData("EFF_DATE"));
			} else {
				result
				.setData("EFF_DATE", TJDODBTool.getInstance()
						.getDBTime());
			}
		}
		// 开单科室
		result.setData("ORDER_DEPT_CODE", Operator.getDept());// ====pangben
		// 2014-8-14
		// 开单医师
		result.setData("ORDER_DR_CODE", Operator.getID());
		// 执行科室
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			if (row == 0) {
				if (this.getValueString("DEPT_CODEDS").length() != 0) {// wanglong
					// modify
					// 20150504
					result.setData("EXEC_DEPT_CODE", this
							.getValue("DEPT_CODEDS"));
				} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
					result.setData("EXEC_DEPT_CODE", parm
							.getValue("EXEC_DEPT_CODE"));
				} else {
					result.setData("EXEC_DEPT_CODE", this.getOrgCode());
				}
			} else {
				TParm temp = this.getTTable(TABLE3).getDataStore().getRowParm(
						row - 1);
				if (temp.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(TABLE3).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {
					result.setData("EXEC_DEPT_CODE", temp
							.getValue("EXEC_DEPT_CODE"));
				} else {
					if (this.getValueString("DEPT_CODEDS").length() != 0) {// wanglong
						// modify
						// 20150504
						result.setData("EXEC_DEPT_CODE", this
								.getValue("DEPT_CODEDS"));
					} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
						result.setData("EXEC_DEPT_CODE", parm
								.getValue("EXEC_DEPT_CODE"));
					} else {
						result.setData("EXEC_DEPT_CODE", this.getOrgCode());
					}
				}
			}
		} else {
			if (row == 0) {
				result.setData("EXEC_DEPT_CODE", parm
						.getValue("EXEC_DEPT_CODE").length() == 0 ? Operator
								.getDept() : parm.getValue("EXEC_DEPT_CODE"));
			} else {
				TParm temp = this.getTTable(TABLE3).getDataStore().getRowParm(
						row - 1);
				if (temp.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(TABLE3).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {
					result.setData("EXEC_DEPT_CODE", temp
							.getValue("EXEC_DEPT_CODE"));
				} else {
					result.setData("EXEC_DEPT_CODE", parm.getValue(
							"EXEC_DEPT_CODE").length() == 0 ? Operator
									.getDept() : parm.getValue("EXEC_DEPT_CODE"));
				}
			}
		}
		// 执行技师
		result.setData("EXEC_DR_CODE", "");
		// 停用科室
		result.setData("DC_DEPT_CODE", "");
		// 停用医师
		result.setData("DC_DR_CODE", "");
		// 停用时间
		result.setData("DC_DATE", "");
		// 停用原因代码
		result.setData("DC_RSN_CODE", "");
		// 医师备注
		result.setData("DR_NOTE", "");
		// 护士备注
		result.setData("NS_NOTE", "");
		// 给付类别
		result.setData("INSPAY_TYPE", parm.getData("INSPAY_TYPE"));
		// 管制药品级别
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("CTRLDRUGCLASS_CODE", actionParm
					.getData("CTRLDRUGCLASS_CODE"));
		} else {
			result.setData("CTRLDRUGCLASS_CODE", "");
		}
		// 抗生素代码
		result
		.setData("ANTIBIOTIC_CODE", actionParm
				.getData("ANTIBIOTIC_CODE"));
		// 处方签号(草药使用)
		result.setData("RX_NO", this.getValueString("RX_NO"));
		// 服号(草药使用)
		result.setData("PRESRT_NO", this.getTComboBox("RX_NO")
				.getSelectedNode().getPy1());
		// 药品类型
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("PHA_TYPE", actionParm.getData("PHA_TYPE"));
		} else {
			result.setData("PHA_TYPE", "");
		}
		// 剂型类别
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("DOSE_TYPE", actionParm.getData("DOSE_CODE"));
		} else {
			result.setData("DOSE_TYPE", "");
		}
		// 饮片服用量
		result.setData("DCT_TAKE_QTY", 0);
		// 煎药方式(中药适用)
		result.setData("DCTAGENT_CODE", "");
		// 待煎包总数(中药适用)
		result.setData("PACKAGE_AMT", 0);
		// 集合医嘱主项注记
		result.setData("SETMAIN_FLG", parm.getData("ORDERSET_FLG"));
		// 审核护士
		result.setData("NS_CHECK_CODE", "");
		// 单独开立注记
		result.setData("INDV_FLG", parm.getData("INDV_FLG"));
		// 隐藏注记
		result.setData("HIDE_FLG", "N");
		// 集合医嘱顺序号
		result.setData("ORDERSET_GROUP_NO", 0);
		// 集合医嘱代码
		result.setData("ORDERSET_CODE", "");
		// 医令细分类
		result.setData("ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
		// 医令分组代码
		result.setData("CAT1_TYPE", parm.getData("CAT1_TYPE"));
		// 报告类别
		result.setData("RPTTYPE_CODE", parm.getData("RPTTYPE_CODE"));
		// 体检代码
		result.setData("OPTITEM_CODE", parm.getData("OPTITEM_CODE"));
		// 申请单类别
		result.setData("MR_CODE", parm.getData("MR_CODE"));
		// FILE_NO
		result.setData("FILE_NO", "");
		// 绩效代码
		result.setData("DEGREE_CODE", parm.getData("DEGREE_CODE"));
		// 护士审核时间
		result.setData("NS_CHECK_DATE", "");
		// 审核护士DC确认
		result.setData("DC_NS_CHECK_CODE", "");
		// 审核护士DC确认时间
		result.setData("DC_NS_CHECK_DATE", "");
		// 首餐日期时间
		result.setData("START_DTTM", StringTool.getString(TJDODBTool
				.getInstance().getDBTime(), "yyyyMMddHHmmss"));
		// 最近摆药日期
		result.setData("LAST_DSPN_DATE", "");
		// 最近摆药量(最近或是首日量)
		result.setData("FRST_QTY", 0);
		// 审核药师
		result.setData("PHA_CHECK_CODE", "");
		// 审核时间
		result.setData("PHA_CHECK_DATE", "");
		// 静脉配液中心
		result.setData("INJ_ORG_CODE", "");
		// 急做
		result.setData("URGENT_FLG", "N");
		// 累计开药量
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("ACUMMEDI_QTY", 0);
		} else {
			result.setData("ACUMMEDI_QTY", 1);
		}
		// 医疗仪器
		result.setData("DEV_CODE", parm.getData("DEV_CODE"));
		// 给药注记
		result.setData("DISPENSE_FLG", "N");
		// 医嘱备注,药品备注
		result.setData("IS_REMARK", parm.getData("IS_REMARK"));
		return result;
	}

	/**
	 * 返回中药医令的设置
	 * 
	 * @return TParm
	 */
	public TParm returnChinaPhaOrderData(TParm parm, int row) {
		TParm result = new TParm();
		// 拿到相关信息
		TParm actionParm = this.getPhaBaseData(parm.getData("ORDER_CODE")
				.toString(), parm.getData("CAT1_TYPE").toString(), "IG", parm);
		// System.out.println("================>>>>>>>>>>>>>>>拿到相关信息"+actionParm);
		// 区域代码
		result.setData("REGION_CODE", Operator.getRegion());
		// 病区代码
		result.setData("STATION_CODE", this.getStationCode());
		// 科室
		result.setData("DEPT_CODE", this.getDeptCode());
		// 经治医师
		result.setData("VS_DR_CODE", ((TParm) this.getParameter()).getData(
				"ODI", "VS_DR_CODE").toString());
		// 床位号
		result.setData("BED_NO", this.getBedIpdNo(caseNo).getValue("BED_NO"));
		// 住院号
		result.setData("IPD_NO", this.getBedIpdNo(caseNo).getValue("IPD_NO"));
		// 病案号
		result.setData("MR_NO", ((TParm) this.getParameter()).getData("ODI",
				"MR_NO").toString());
		// 暂存注记
		result.setData("TEMPORARY_FLG", "N");
		// 医嘱状态
		result.setData("ORDER_STATE", "N");
		// 连接医嘱主项
		result.setData("LINKMAIN_FLG", "N");
		// 连接号
		result.setData("LINK_NO", "");
		// 医嘱代码
		result.setData("ORDER_CODE", parm.getData("ORDER_CODE"));
		// 医嘱名称
		result.setData("ORDER_DESC", parm.getData("ORDER_DESC").toString());
		// 商品名
		result.setData("GOODS_DESC", parm.getData("GOODS_DESC"));
		// 规格
		result.setData("SPECIFICATION", parm.getData("SPECIFICATION"));
		// 英文名称
		result.setData("ORDER_ENG_DESC", parm.getData("TRADE_ENG_DESC"));
		if (!this.yyList) {
			// 开药数量
			result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
		} else {
			if (parm.getData("MEDI_QTY") != null) {
				result.setData("MEDI_QTY", parm.getData("MEDI_QTY"));
			} else {
				// 开药数量
				result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
			}
		}
		// 开药单位
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("MEDI_UNIT", actionParm.getData("MEDI_UNIT"));
		} else {
			result.setData("MEDI_UNIT", parm.getData("UNIT_CODE"));
		}
		// 频次代码
		result.setData("FREQ_CODE", actionParm.getData("FREQ_CODE"));
		// 用法
		result.setData("ROUTE_CODE", actionParm.getData("ROUTE_CODE"));
		// 天数
		result.setData("TAKE_DAYS", this.getValueInt("RF"));
		// 配药数量
		if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
			result.setData("DOSAGE_QTY", 1);
		} else {
			result.setData("DOSAGE_QTY", 0);
		}
		// 配药单位
		result.setData("DOSAGE_UNIT", actionParm.getData("MEDI_UNIT"));
		// 发药数量
		result.setData("DISPENSE_QTY", 0);
		// 发药单位
		result.setData("DISPENSE_UNIT", actionParm.getData("MEDI_UNIT"));
		// 盒发药注记
		result.setData("GIVEBOX_FLG", "N");
		// 续用注记
		result.setData("CONTINUOUS_FLG", "N");
		// 累用量
		result.setData("ACUMDSPN_QTY", 0);
		// 最近配药量
		result.setData("LASTDSPN_QTY", 0);
		// 医嘱预定启用日期
		result.setData("EFF_DATE", TJDODBTool.getInstance().getDBTime());
		// 开单科室
		result.setData("ORDER_DEPT_CODE", Operator.getDept());// ====pangben
		// 2014-8-14
		// 开单医师
		result.setData("ORDER_DR_CODE", Operator.getID());
		// 执行科室
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			if (this.getValueString("DEPT_CODEIG").length() == 0) {
				result.setData("EXEC_DEPT_CODE", this.getOrgCode());
			} else {
				result.setData("EXEC_DEPT_CODE", this.getValue("DEPT_CODEIG"));
			}
		} else {
			result.setData("EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE")
					.length() == 0 ? Operator.getDept() : parm
							.getValue("EXEC_DEPT_CODE"));
		}
		// 执行技师
		result.setData("EXEC_DR_CODE", "");
		// 停用科室
		result.setData("DC_DEPT_CODE", "");
		// 停用医师
		result.setData("DC_DR_CODE", "");
		// 停用时间
		result.setData("DC_DATE", "");
		// 停用原因代码
		result.setData("DC_RSN_CODE", "");
		// 医师备注
		result.setData("DR_NOTE", this.getValueString("IG_DR_NOTE"));
		// 护士备注
		result.setData("NS_NOTE", "");
		// 给付类别
		result.setData("INSPAY_TYPE", parm.getData("INSPAY_TYPE"));
		// 管制药品级别
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("CTRLDRUGCLASS_CODE", actionParm
					.getData("CTRLDRUGCLASS_CODE"));
		} else {
			result.setData("CTRLDRUGCLASS_CODE", "");
		}
		// 抗生素代码
		result
		.setData("ANTIBIOTIC_CODE", actionParm
				.getData("ANTIBIOTIC_CODE"));
		// 处方签号(草药使用)
		result.setData("RX_NO", this.getValueString("IG_RX_NO"));
		// 服号(草药使用)
		result.setData("PRESRT_NO", this.getTComboBox("IG_RX_NO")
				.getSelectedNode().getPy1());
		// 药品类型
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("PHA_TYPE", actionParm.getData("PHA_TYPE"));
		} else {
			result.setData("PHA_TYPE", "");
		}
		// 剂型类别
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("DOSE_TYPE", actionParm.getData("DOSE_CODE"));
		} else {
			result.setData("DOSE_TYPE", "");
		}
		// 饮片服用量
		result.setData("DCT_TAKE_QTY", this.getValue("YPJL"));
		// 煎药方式(中药适用)
		result.setData("DCTAGENT_CODE", this.getValueString("IG_DCTAGENT"));
		// 特殊煎法
		// this.getTComboBox("DCTAGENT_COMBO").setSelectedIndex(0);
		if (!this.yyList) {
			result.setData("DCTEXCEP_CODE", "");
		} else {
			if (parm.getData("DCTEXCEP_CODE") != null) {
				result.setData("DCTEXCEP_CODE", parm.getData("DCTEXCEP_CODE"));
			} else {
				result.setData("DCTEXCEP_CODE", "");
			}
		}
		// 待煎包总数(中药适用副数*频次)
		int rfNum = this.getValueInt("RF");
		String freqCode = this.getValueString("IGFREQCODE");
		int rfNumCount = OdiUtil.getInstance().getPACKAGE_AMT(rfNum, freqCode);
		result.setData("PACKAGE_AMT", rfNumCount);
		// 集合医嘱主项注记
		result.setData("SETMAIN_FLG", parm.getData("ORDERSET_FLG"));
		// 审核护士
		result.setData("NS_CHECK_CODE", "");
		// 单独开立注记
		result.setData("INDV_FLG", parm.getData("INDV_FLG"));
		// 隐藏注记(集合医嘱细项处理)
		Object objHide = parm.getData("HIDE_FLG");
		if (objHide != null) {
			result.setData("HIDE_FLG", parm.getData("HIDE_FLG"));
		} else {
			result.setData("HIDE_FLG", "N");
		}
		// 集合医嘱顺序号
		result.setData("ORDERSET_GROUP_NO", 0);
		// 集合医嘱代码
		result.setData("ORDERSET_CODE", "");
		// 医令细分类
		result.setData("ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
		// 医令分组代码
		result.setData("CAT1_TYPE", parm.getData("CAT1_TYPE"));
		// 报告类别
		result.setData("RPTTYPE_CODE", parm.getData("RPTTYPE_CODE"));
		// 体检代码
		result.setData("OPTITEM_CODE", parm.getData("OPTITEM_CODE"));
		// 申请单类别
		result.setData("MR_CODE", parm.getData("MR_CODE"));
		// FILE_NO
		result.setData("FILE_NO", "");
		// 绩效代码
		result.setData("DEGREE_CODE", parm.getData("DEGREE_CODE"));
		// 护士审核时间
		result.setData("NS_CHECK_DATE", "");
		// 审核护士DC确认
		result.setData("DC_NS_CHECK_CODE", "");
		// 审核护士DC确认时间
		result.setData("DC_NS_CHECK_DATE", "");
		// 首餐日期时间
		result.setData("START_DTTM", StringTool.getString(TJDODBTool
				.getInstance().getDBTime(), "yyyyMMddHHmmss"));
		// 最近摆药日期
		result.setData("LAST_DSPN_DATE", "");
		// 最近摆药量(最近或是首日量)
		result.setData("FRST_QTY", 0);
		// 审核药师
		result.setData("PHA_CHECK_CODE", "");
		// 审核时间
		result.setData("PHA_CHECK_DATE", "");
		// 静脉配液中心
		result.setData("INJ_ORG_CODE", "");
		// 急做
		result.setData("URGENT_FLG", ((TCheckBox) this.getComponent("URGENT"))
				.isSelected() ? "Y" : "N");
		// 累计开药量
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("ACUMMEDI_QTY", 0);
		} else {
			result.setData("ACUMMEDI_QTY", 1);
		}
		// 医疗仪器
		result.setData("DEV_CODE", parm.getData("DEV_CODE"));
		// 给药注记
		result.setData("DISPENSE_FLG", "N");
		// 自备
		result.setData("RELEASE_FLG", ((TCheckBox) this.getComponent("OWNED"))
				.isSelected() ? "Y" : "N");
		// 医嘱备注,药品备注
		result.setData("IS_REMARK", parm.getData("IS_REMARK"));
		return result;
	}

	/**
	 * 查询上一医嘱频次
	 * 
	 * @return String
	 */
	public String queryFreqCodeUp() {
		TDS dsOrder = odiObject.getDS("ODI_ORDER");
		TParm parmBuff = dsOrder.getBuffer(dsOrder.PRIMARY);
		int lastRow = parmBuff.getCount("#ACTIVE#");
		Object obj = parmBuff.getData("FREQ_CODE", lastRow - 1);
		return "" + obj;
	}

	/**
	 * 根据医令细分类查找相关数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getPhaBaseData(String orderCode, String orderCat1,
			String type, TParm parm) {
		// System.out.println("根据医令细分类查找相关数据"+orderCode+"   ::   "+orderCat1);
		TParm result = new TParm();
		// 临时医嘱
		if ("ST".equals(type)) {
			if ("PHA".equals(orderCat1.trim())) {
				// 得到PHA_BASE数据
				TParm action = new TParm();
				action.setData("ORDER_CODE", orderCode);
				// =============pangben modify 20110516 start 添加参数
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0)
					action.setData("REGION_CODE", Operator.getRegion());
				// =============pangben modify 20110516 stop

				result = OdiMainTool.getInstance().queryPhaBase(action);

				// 临时用药预设频次
				if (odiObject.getAttribute(odiObject.ODI_UDD_STAT_CODE)
						.toString().length() == 0) {
					// 上一医嘱频次
					result.setData("FREQ_CODE", queryFreqCodeUp());
				} else {
					result.setData("FREQ_CODE", odiObject
							.getAttribute(odiObject.ODI_UDD_STAT_CODE));
				}
				// 用法
				int row = this.getTTable(TABLE1).getSelectedRow();
				if (row == 0) {
					result.setData("ROUTE_CODE", result
							.getData("ROUTE_CODE", 0));
				} else {
					if (this.getTTable(TABLE1).getDataStore().getRowParm(
							row - 1).getInt("LINK_NO") != 0) {
						// result.setData("ROUTE_CODE",
						// this.getTTable(TABLE1).getDataStore().getRowParm(row-1).getValue("ROUTE_CODE"));
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					} else {
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					}
				}
				result.setData("PHA_TYPE", result.getData("PHA_TYPE", 0));
				result.setData("DOSE_CODE", result.getData("DOSE_TYPE", 0));
				// 发药单位==库存单位
				result
				.setData("DISPENSE_UNIT", result.getData("STOCK_UNIT",
						0));
				// 开药单位
				result.setData("MEDI_UNIT", result.getData("MEDI_UNIT", 0));
				// 默认开药用量
				result.setData("MEDI_QTY", result.getData("MEDI_QTY", 0));
				// 管制药品级数
				result.setData("CTRLDRUGCLASS_CODE", result.getData(
						"CTRLDRUGCLASS_CODE", 0));
				// 抗生素代码
				result.setData("ANTIBIOTIC_CODE", result.getData(
						"ANTIBIOTIC_CODE", 0));
				// System.out.println("得到PHA_BASE数据" + result);
			} else {
				// 临时处置预设频次
				if (odiObject.getAttribute(odiObject.ODI_ODI_STAT_CODE)
						.toString().length() == 0) {
					// 上一医嘱频次
					result.setData("FREQ_CODE", queryFreqCodeUp());
				} else {
					result.setData("FREQ_CODE", odiObject
							.getAttribute(odiObject.ODI_ODI_STAT_CODE));
				}
				// 用法
				int row = this.getTTable(TABLE1).getSelectedRow();
				if (row == 0) {
					result.setData("ROUTE_CODE", result
							.getData("ROUTE_CODE", 0));
				} else {
					if (this.getTTable(TABLE1).getDataStore().getRowParm(
							row - 1).getInt("LINK_NO") != 0) {
						// result.setData("ROUTE_CODE",this.getTTable(TABLE1).getDataStore().getRowParm(row
						// -1).getValue("ROUTE_CODE"));
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					} else {
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					}
				}
				// DISPENSE_UNIT发药单位
				result.setData("DISPENSE_UNIT", parm.getData("UNIT_CODE"));
				// MEDI_UNIT
				result.setData("MEDI_UNIT", parm.getData("UNIT_CODE"));
			}
		}
		// 长期医嘱
		if ("UD".equals(type)) {
			// this.messageBox("========getPhaBaseData 长期医嘱============");
			if ("PHA".equals(orderCat1.trim())) {
				// 得到PHA_BASE数据
				TParm action = new TParm();
				action.setData("ORDER_CODE", orderCode);
				// =============pangben modify 20110516 start 添加参数
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0)
					action.setData("REGION_CODE", Operator.getRegion());
				// =============pangben modify 20110516 stop
				result = OdiMainTool.getInstance().queryPhaBase(action);
				// this.messageBox_(result);
				// 长期用药预设频次
				if (result.getValue("FREQ_CODE", 0).length() == 0) {
					// 上一医嘱频次
					result.setData("FREQ_CODE", queryFreqCodeUp());
				} else {
					result.setData("FREQ_CODE", result.getData("FREQ_CODE", 0));
					// //============pangben modify 20110609 start 默认频次立即使用
					// result.setData("FREQ_CODE",
					// odiObject.getAttribute(odiObject.
					// ODI_UDD_STAT_CODE));
					// //============pangben modify 20110609 stop
				}
				// 用法
				int row = this.getTTable(TABLE2).getSelectedRow();
				if (row == 0) {
					result.setData("ROUTE_CODE", result
							.getData("ROUTE_CODE", 0));
				} else {
					if (this.getTTable(TABLE2).getDataStore().getRowParm(
							row - 1).getInt("LINK_NO") != 0) {
						// result.setData("ROUTE_CODE",
						// this.getTTable(TABLE2).getDataStore().getRowParm(row-1).getValue("ROUTE_CODE"));
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					} else {
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					}
				}
				result.setData("PHA_TYPE", result.getData("PHA_TYPE", 0));
				result.setData("DOSE_CODE", result.getData("DOSE_TYPE", 0));
				// 发药单位==库存单位
				result
				.setData("DISPENSE_UNIT", result.getData("STOCK_UNIT",
						0));
				// 开药单位
				// System.out.println("开药单位"+result.getData("MEDI_UNIT", 0));
				result.setData("MEDI_UNIT", result.getData("MEDI_UNIT", 0));
				// 默认开药用量
				// System.out.println("默认开药用量"+result.getData("MEDI_QTY",0));
				result.setData("MEDI_QTY", result.getData("MEDI_QTY", 0));
				// 管制药品级数
				result.setData("CTRLDRUGCLASS_CODE", result.getData(
						"CTRLDRUGCLASS_CODE", 0));
				// System.out.println("得到PHA_BASE数据" + result);
				// 抗生素代码
				result.setData("ANTIBIOTIC_CODE", result.getData(
						"ANTIBIOTIC_CODE", 0));
			} else {
				// 长期处置预设频次
				if (odiObject.getAttribute(odiObject.ODI_ODI_DEFA_FREG)
						.toString().length() == 0) {
					// 上一医嘱频次
					result.setData("FREQ_CODE", queryFreqCodeUp());
				} else {
					// this.messageBox("come in2111111================"+odiObject.getAttribute(odiObject.ODI_ODI_DEFA_FREG).toString());
					// $$==============Modified by lx
					// 2012/02/24假如存在参数档设置取参数档START=========================$$//
					if (odiObject.getAttribute(odiObject.ODI_ODI_DEFA_FREG) != null
							&& odiObject.getAttribute(
									odiObject.ODI_ODI_DEFA_FREG).toString()
							.length() > 0) {
						result.setData("FREQ_CODE", odiObject.getAttribute(
								odiObject.ODI_ODI_DEFA_FREG).toString());
					} else {
						// ===============pangben modfiy 20110608 默认频次立即使用
						result.setData("FREQ_CODE", odiObject
								.getAttribute(odiObject.ODI_UDD_STAT_CODE));
					}
					// $$==============Modified by lx
					// 2012/02/24假如存在参数档设置取参数档END=========================$$//
				}
				// 用法
				int row = this.getTTable(TABLE2).getSelectedRow();
				if (row == 0) {
					result.setData("ROUTE_CODE", result
							.getData("ROUTE_CODE", 0));
				} else {
					if (this.getTTable(TABLE2).getDataStore().getRowParm(
							row - 1).getInt("LINK_NO") != 0) {
						// result.setData("ROUTE_CODE",
						// this.getTTable(TABLE2).getDataStore().getRowParm(row-1).getValue("ROUTE_CODE"));
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					} else {
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					}
				}
				// DISPENSE_UNIT发药单位
				result.setData("DISPENSE_UNIT", parm.getData("UNIT_CODE"));
				// MEDI_UNIT
				result.setData("MEDI_UNIT", parm.getData("UNIT_CODE"));
			}
		}
		// 出院带药医嘱
		if ("DS".equals(type)) {
			if ("PHA".equals(orderCat1.trim())) {
				// 得到PHA_BASE数据
				TParm action = new TParm();
				action.setData("ORDER_CODE", orderCode);
				// =============pangben modify 20110516 start 添加参数
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0)
					action.setData("REGION_CODE", Operator.getRegion());
				// =============pangben modify 20110516 stop

				result = OdiMainTool.getInstance().queryPhaBase(action);
				// 临时用药预设频次
				if (result.getValue("FREQ_CODE", 0).length() == 0) {
					// 上一医嘱频次
					result.setData("FREQ_CODE", queryFreqCodeUp());
				} else {
					// ===============pangben modfiy 20110608 默认频次立即使用
					// result.setData("FREQ_CODE",odiObject.getAttribute(odiObject.ODI_UDD_STAT_CODE));
					result.setData("FREQ_CODE", result.getData("FREQ_CODE", 0));
				}
				// 用法
				int row = this.getTTable(TABLE3).getSelectedRow();
				if (row == 0) {
					result.setData("ROUTE_CODE", result
							.getData("ROUTE_CODE", 0));
				} else {
					if (this.getTTable(TABLE3).getDataStore().getRowParm(
							row - 1).getInt("LINK_NO") != 0) {
						// result.setData("ROUTE_CODE",
						// this.getTTable(TABLE3).getDataStore().getRowParm(row-1).getValue("ROUTE_CODE"));
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					} else {
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					}
				}
				result.setData("PHA_TYPE", result.getData("PHA_TYPE", 0));
				result.setData("DOSE_CODE", result.getData("DOSE_TYPE", 0));
				// 发药单位==库存单位
				result
				.setData("DISPENSE_UNIT", result.getData("STOCK_UNIT",
						0));
				// 开药单位
				result.setData("MEDI_UNIT", result.getData("MEDI_UNIT", 0));
				// 默认开药用量
				result.setData("MEDI_QTY", result.getData("MEDI_QTY", 0));
				// 管制药品级数
				result.setData("CTRLDRUGCLASS_CODE", result.getData(
						"CTRLDRUGCLASS_CODE", 0));
				// 抗生素代码
				result.setData("ANTIBIOTIC_CODE", result.getData(
						"ANTIBIOTIC_CODE", 0));
				// System.out.println("得到PHA_BASE数据" + result);
			}
		}
		// 中药饮片医嘱
		if ("IG".equals(type)) {
			if ("PHA".equals(orderCat1.trim())) {
				// 得到PHA_BASE数据
				TParm action = new TParm();
				action.setData("ORDER_CODE", orderCode);
				// =============pangben modify 20110516 start 添加参数
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0)
					action.setData("REGION_CODE", Operator.getRegion());
				// =============pangben modify 20110516 stop
				result = OdiMainTool.getInstance().queryPhaBase(action);
				// 住院中药用药预设频次
				result.setData("FREQ_CODE", this.getValueString("IGFREQCODE"));
				result.setData("ROUTE_CODE", this.getValueString("IG_ROUTE"));
				result.setData("PHA_TYPE", result.getData("PHA_TYPE", 0));
				result.setData("DOSE_CODE", result.getData("DOSE_TYPE", 0));
				// 发药单位==库存单位
				result
				.setData("DISPENSE_UNIT", result.getData("STOCK_UNIT",
						0));
				// 开药单位
				result.setData("MEDI_UNIT", result.getData("MEDI_UNIT", 0));
				// 默认开药用量
				result.setData("MEDI_QTY", result.getData("MEDI_QTY", 0));
				// 管制药品级数
				result.setData("CTRLDRUGCLASS_CODE", result.getData(
						"CTRLDRUGCLASS_CODE", 0));
				// 抗生素代码
				result.setData("ANTIBIOTIC_CODE", result.getData(
						"ANTIBIOTIC_CODE", 0));
				// System.out.println("得到PHA_BASE数据" + result);
			}
		}
		// 院前用药
		if ("PA".equals(type)) {
			if ("PHA".equals(orderCat1.trim())) {
				// 得到PHA_BASE数据
				TParm action = new TParm();
				action.setData("ORDER_CODE", orderCode);
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0)
					action.setData("REGION_CODE", Operator.getRegion());

				result = OdiMainTool.getInstance().queryPhaBase(action);
				result.setData("FREQ_CODE", result.getData("FREQ_CODE", 0));
				// 用法
				int row = this.getTTable(TABLE6).getSelectedRow();
				if (row == 0) {
					result.setData("ROUTE_CODE", result
							.getData("ROUTE_CODE", 0));
				} else {
					if (this.getTTable(TABLE6).getDataStore().getRowParm(
							row - 1).getInt("LINK_NO") != 0) {
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					} else {
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					}
				}
				result.setData("PHA_TYPE", result.getData("PHA_TYPE", 0));
				result.setData("DOSE_CODE", result.getData("DOSE_TYPE", 0));
				// 发药单位==库存单位
				result
				.setData("DISPENSE_UNIT", result.getData("STOCK_UNIT",
						0));
				// 开药单位
				result.setData("MEDI_UNIT", result.getData("MEDI_UNIT", 0));
				// 默认开药用量
				result.setData("MEDI_QTY", result.getData("MEDI_QTY", 0));
				// 管制药品级数
				result.setData("CTRLDRUGCLASS_CODE", result.getData(
						"CTRLDRUGCLASS_CODE", 0));
				// 抗生素代码
				result.setData("ANTIBIOTIC_CODE", result.getData(
						"ANTIBIOTIC_CODE", 0));
			}
		}
		return result;
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
	 * 得到就诊号
	 * 
	 * @return String
	 */
	public String getCaseNo() {
		return this.caseNo;
	}

	public String getMrNo() {
		return mrNo;
	}

	public String getPatName() {
		return patName;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public boolean isStopBillFlg() {
		return stopBillFlg;
	}

	public String getIpdNo() {
		return ipdNo;
	}

	public Timestamp getAdmDate() {
		return admDate;
	}

	public Timestamp getBirthDay() {
		return birthDay;
	}

	public String getTel() {
		return tel;
	}

	public String getSexCode() {
		return sexCode;
	}

	public String getPostCode() {
		return postCode;
	}

	public String getPatName1() {
		return patName1;
	}

	public String getIdNo() {
		return idNo;
	}

	public String getCompanyDesc() {
		return companyDesc;
	}

	public String getAddress() {
		return address;
	}

	public String getMainDiag() {
		return mainDiag;
	}

	public String getCtzCode() {
		return ctzCode;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public boolean isSaveFlg() {
		return saveFlg;
	}

	public String getIcdCode() {
		return icdCode;
	}

	public String getIcdDesc() {
		return icdDesc;
	}

	public String getStationCode() {
		return stationCode;
	}

	public boolean isOidrFlg() {
		return oidrFlg;
	}

	/**
	 * 设置就诊号
	 * 
	 * @param caseNo
	 *            String
	 */
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public void setStopBillFlg(boolean stopBillFlg) {
		this.stopBillFlg = stopBillFlg;
	}

	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	public void setAdmDate(Timestamp admDate) {
		this.admDate = admDate;
	}

	public void setBirthDay(Timestamp birthDay) {
		this.birthDay = birthDay;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public void setSexCode(String sexCode) {
		this.sexCode = sexCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public void setPatName1(String patName1) {
		this.patName1 = patName1;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public void setCompanyDesc(String companyDesc) {
		this.companyDesc = companyDesc;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setMainDiag(String mainDiag) {
		this.mainDiag = mainDiag;
	}

	public void setCtzCode(String ctzCode) {
		this.ctzCode = ctzCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public void setSaveFlg(boolean saveFlg) {
		this.saveFlg = saveFlg;
	}

	public void setIcdCode(String icdCode) {
		this.icdCode = icdCode;
	}

	public void setIcdDesc(String icdDesc) {
		this.icdDesc = icdDesc;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public void setOidrFlg(boolean oidrFlg) {
		this.oidrFlg = oidrFlg;
	}

	public boolean isIcuFlg() {
		return icuFlg;
	}

	public void setIcuFlg(boolean icuFlg) {
		this.icuFlg = icuFlg;
	}

	public boolean isOpeFlg() {
		return opeFlg;
	}

	public void setOpeFlg(boolean opeFlg) {
		this.opeFlg = opeFlg;
	}

	public String getOpDeptCode() {
		return opDeptCode;
	}

	public void setOpDeptCode(String opDeptCode) {
		this.opDeptCode = opDeptCode;
	}

	public String getOpBookSeq() {
		return opBookSeq;
	}

	public void setOpBookSeq(String opBookSeq) {
		this.opBookSeq = opBookSeq;
	}

	/*public String getDayOpeCode() {
		return dayOpeCode;
	}

	public void setDayOpeCode(String dayOpeCode) {
		this.dayOpeCode = dayOpeCode;
	}*/
	public String getDayOpeFlg() {
		return dayOpeFlg;
	}

	public void setDayOpeFlg(String dayOpeFlg) {
		this.dayOpeFlg = dayOpeFlg;
	}

	/**
	 * 是否有新增修改行
	 * 
	 * @return boolean
	 */
	public boolean checkNewModifRowCount() {
		boolean falg = false;
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		int newRow[] = ds.getNewRows(buff);
		int newCount = 0;
		for (int i : newRow) {
			if (!ds.isActive(i, buff))
				continue;
			newCount++;
		}
		int modifRow[] = ds.getOnlyModifiedRows(buff);
		int delCount = ds.getDeleteCount() < 0 ? 0 : ds.getDeleteCount();
		if (newCount + modifRow.length + delCount > 0)
			falg = true;
		return falg;
	}

	/**
	 * 删除空行
	 */
	public void delRowNull() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		TTable table = null;
		TDS ds = odiObject.getDS("ODI_ORDER");
		// System.out.println("tab.getSelectedIndex()!!!============================="+tab.getSelectedIndex());
		switch (tab.getSelectedIndex()) {
		case 0:
			// 临时
			table = getTTable(TABLE1);
			table
			.setFilter("(RX_KIND='ST' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y') OR RX_KIND='ST' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 1:
			// 长期
			table = getTTable(TABLE2);
			table
			.setFilter("(RX_KIND='UD' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y') OR RX_KIND='UD' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 2:
			// 出院带药
			table = getTTable(TABLE3);
			table
			.setFilter("(RX_KIND='DS' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y') OR RX_KIND='DS' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 3:
			// 中药饮片
			table = getTTable(TABLE4);
			ds
			.setFilter("(RX_KIND='IG' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y') OR RX_KIND='IG' AND #ACTIVE#='N'");
			// ds.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(false);
			break;
		case 4:
			// 过敏记录
			table = getTTable(GMTABLE);
			table.setFilter("#NEW#='Y' OR #ACTIVE#='N'");
			// table.setSort("ADM_DATE");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;

		}
		// 中药饮片
		if (tab.getSelectedIndex() == 3) {
			if (!odiObject.filter(table, ds, this.isStopBillFlg())) {
				this.messageBox("E0155");
			}
		} else {
			table.filter();
			// table.sort();
			table.setDSValue();
		}
		this.antflg = false;
		// 初始化医嘱设置
		initOrderStart();
		// 添加一行
		// table.getDataStore().showDebug();
		this.onAddRow();
		// 记录当前页面INDEX
		indexPage = tab.getSelectedIndex();
		// 锁定行
		lockRowOrder();
	}

	/**
	 * 中医修改调用
	 */
	public void onChangeIGOrder() {
		if (this.getValueString("IG_RX_NO").length() == 0) {
			return;
		}
		TDS ds = odiObject.getDS("ODI_ORDER");
		TParm igParm = new TParm();
		// 待煎包数
		int rfNum = this.getValueInt("RF");
		String freqCode = this.getValueString("IGFREQCODE");
		int rfNumCount = OdiUtil.getInstance().getPACKAGE_AMT(rfNum, freqCode);
		igParm.setData("PACKAGE_AMT", rfNumCount);
		// 频次
		igParm.setData("FREQ_CODE", this.getValueString("IGFREQCODE"));
		// 用法
		igParm.setData("ROUTE_CODE", this.getValueString("IG_ROUTE"));
		// 药房(执行科室)
		igParm.setData("EXEC_DEPT_CODE", this.getValueString("DEPT_CODEIG"));
		// 煎药方式
		igParm.setData("DCTAGENT_CODE", this.getValueString("IG_DCTAGENT"));
		// 备注
		igParm.setData("DR_NOTE", this.getValueString("IG_DR_NOTE"));
		// 急做
		igParm.setData("URGENT_FLG", ((TCheckBox) this.getComponent("URGENT"))
				.isSelected() ? "Y" : "N");
		// 自备
		igParm.setData("RELEASE_FLG", ((TCheckBox) this.getComponent("OWNED"))
				.isSelected() ? "Y" : "N");
		// 处方号
		String rxNoIG = this.getValueString("IG_RX_NO");
		int rowCount = ds.rowCount();
		for (int i = 0; i < rowCount; i++) {
			if (!ds.isActive(i))
				continue;
			TParm temp = ds.getRowParm(i);
			if (temp.getValue("RX_NO").equals(rxNoIG)) {
				odiObject.setItem(ds, i, igParm);
			}
		}
	}

	/**
	 * 保存
	 */
	public boolean onSave() {
		BYXflg=false;
		// this.messageBox("come save.");
		// odiObject.getDS("ODI_ORDER").showDebug();
		// 开可以开医嘱的几个条件。1，病患出院 2， 会诊不可以开医嘱，
		if (!this.isSaveFlg()) {
			// 此病患已经出院不可以开立医嘱！
			this.messageBox("E0163");
			initDataStoreToTable();
			this.clearFlg = "N";
			return false;
		}

		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return false;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return false;
		}// modified by WangQing 20170428 end
		//==pangben 2016-9-13
		try {
			TParm clpChekcParm=IBSTool.getInstance().onCheckClpDiff(caseNo);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		//在ADM_INP表中更新改病患为疑似感染 ADM_INP INF_FLG置为Y感染  置为N为没有感染  -start machao 20170615 
		String sqlUpdate = "UPDATE ADM_INP SET INF_FLG ='"+this.getValueString("INF_FLG")+"' WHERE CASE_NO='"+this.getCaseNo()+"'";
		try{
			TJDODBTool.getInstance().update(sqlUpdate);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		//-end machao 20170615
		// 当前选中页签
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		// TABLE接受改变付值
		acceptTextTable(new String[] { TABLE1, TABLE2, TABLE3, TABLE4, GMTABLE,
				TABLE6 });

		if (odiStationDrools.fireRules(odiObject, 1)) {
			return false;
		}

		TDS ds = odiObject.getDS("ODI_ORDER");
		// delRowNull();
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		int newRow[] = ds.getNewRows(buff);
		// ===pangben 2015-8-14 出院操作时，临床路径校验溢出
		String orderCode = TConfig.getSystemValue("clp.orderCode");// 获得出院通知医嘱
		boolean clpDsFlg = false;// 校验是否可以提示溢出
		int newCount = 0;
		for (int i : newRow) {
			if (!ds.isActive(i, buff))
				continue;
			TParm tempParm = ds.getRowParm(i, buff);// =====pangben 2015-8-14
			// opeParm.setRowData(newCount, tempParm);
			// 添加医嘱比较，路径校验使用
			if (null != tempParm.getValue("ORDER_CODE")
					&& tempParm.getValue("ORDER_CODE").equals(orderCode)) {
				clpDsFlg = true;// 新增的数据存在设置的医嘱
			}
			newCount++;
		}
		// opeParm.setCount(newCount);
		// this.messageBox("==newCount=="+newCount);
		// ===================针对未审核医嘱细项ORDERSET_GROUP_NO与主项不同的BUG，在保存前检测并提示
		TParm vParm = ds.getBuffer(ds.FILTER);// wanglong add 20140523
		for (int i = 0; i < vParm.getCount(); i++) {
			if (vParm.getBoolean("#NEW#", i)
					&& !vParm.getValue("ORDERSET_CODE", i).equals("")
					&& vParm.getValue("SETMAIN_FLG", i).equals("N")) {
				int j = 0;
				for (; j < vParm.getCount(); j++) {
					if (j == i) {
						continue;
					}
					if (vParm.getBoolean("#NEW#", j)
							&& !vParm.getValue("ORDERSET_CODE", j).equals("")
							&& vParm.getValue("SETMAIN_FLG", j).equals("Y")
							&& vParm.getValue("ORDERSET_CODE", j).equals(
									vParm.getValue("ORDERSET_CODE", i))
							&& vParm.getValue("ORDERSET_GROUP_NO", j).equals(
									vParm.getValue("ORDERSET_GROUP_NO", i))) {
						break;
					}
				}
				if (j == vParm.getCount()) {
					System.out.println("-----------Debug begin---------");
					ds.showDebug();
					System.out.println("-----------Debug end-----------");
					this.messageBox("集合医嘱主细项存在不同的GROUP_NO，属于严重BUG，请联系蓝创");
					return false;
				}
			}
		}
		// 是否有要保存的数据  
		if (!checkNewModifRowCount() && !isSaveGM()) {
			// 没有要保存的数据！
			this.messageBox("E0164");
			return false;
		}
		// 过敏记录保存    
		if (tab.getSelectedIndex() == 4 ) {
			boolean falg = false;
//			//machao start 
//			TDataStore data = this.getTTable(GMTABLE).getDataStore();
//			Vector vector = data.getVector();
//			if(vector.size()>1){
//				Set<String> set = new HashSet<String>();
//				Vector v = new Vector();
//				String typeAgergy = "";
//				if("Y".equals(this.getValueString("PHA_DRUGALLERGY"))){
//					typeAgergy = this.getValueString("PHA_CLASS");
//				}else if("Y".equals(this.getValueString("CF_DRUGALLERGY"))){
//					typeAgergy = "A";
//				}else if("Y".equals(this.getValueString("OTHER_DRUGALLERGY"))){
//					typeAgergy = "C";
//				}else if("Y".equals(this.getValueString("NONE_DRUGALLERGY"))){
//					typeAgergy = "N";
//				}
//
//				for(int i = 0;i<vector.size()-1;i++){
//					v = (Vector) vector.get(i);
//					set.add((String) v.get(2));
//				}
//				//List list = new ArrayList(set);
//				List<String> listIsExist = new ArrayList();
//				for(String str :set){
//					String sqlAllergy = "SELECT * FROM opd_drugallergy WHERE 1=1 AND MR_NO = '"+this.getMrNo()+"' "
//							+ "AND DRUG_TYPE='"+typeAgergy+"' "
//							+ "AND DRUGORINGRD_CODE='"+str+"' "
//							+ "AND CASE_NO <>'"+this.getCaseNo()+"' ";
//					System.out.println("1111111111"+sqlAllergy);
//					TParm p = new TParm(TJDODBTool.getInstance().select(sqlAllergy));
//					
//					if(p.getCount()>0){
//						listIsExist.add(str);
//					}
//				}
//				TParm parm = new TParm();
//				SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
//				for(String str:listIsExist){
//					parm.addData("DRUG_TYPE", typeAgergy);
//					parm.addData("DRUGORINGRD_CODE", str);				
//					parm.addData("MR_NO", this.getMrNo());
//					parm.addData("ADM_DATE",df.format(SystemTool.getInstance().getDate()));
//									
//					parm.addData("ADM_TYPE", "I");
//					parm.addData("CASE_NO", this.getCaseNo());
//					parm.addData("DEPT_CODE", this.getDeptCode());
//					parm.addData("DR_CODE",Operator.getID());
//					parm.addData("ALLERGY_NOTE", "");
//					parm.addData("OPT_USER", Operator.getID());
//					parm.addData("OPT_DATE", SystemTool.getInstance().getDate().toString().substring(0,19));
//					parm.addData("OPT_TERM", Operator.getIP());
//				}
//				//this.messageBox(""+parm);
//				TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
//						"insertOpddrugallergy", parm);			
//				
////				TTable table = this.getTTable(GMTABLE);
////				Vector vector = table.getDataStore().getVector();
////				System.out.println("0000000:"+vector);
////				if(vector.size()>=2){
////					Set<String> set = new HashSet<String>();
////					SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
////					Vector v = new Vector();
////					for(int i = 0;i<vector.size()-1;i++){
////						v = (Vector) vector.get(i);
////						set.add((String) v.get(2));
////					}
////					TParm parm = new TParm();
////					v = (Vector) vector.get(0);
////					for(String str : set){
////						parm.addData("DRUG_TYPE", v.get(1));
////						parm.addData("DRUGORINGRD_CODE", str);				
////						parm.addData("MR_NO", this.getMrNo());
////						parm.addData("ADM_DATE",df.format(SystemTool.getInstance().getDate()));
////										
////						parm.addData("ADM_TYPE", "I");
////						parm.addData("CASE_NO", this.getCaseNo());
////						parm.addData("DEPT_CODE", this.getDeptCode());
////						parm.addData("DR_CODE",Operator.getID());
////						parm.addData("ALLERGY_NOTE", "");
////						parm.addData("OPT_USER", Operator.getID());
////						parm.addData("OPT_DATE", SystemTool.getInstance().getDate().toString().substring(0,19));
////						parm.addData("OPT_TERM", Operator.getIP());
////					}
////					
////					TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
////							"insertOpddrugallergy", parm);
////					this.messageBox(""+parm);
////				}
//			}
//			//machao end
			falg = this.getTTable(GMTABLE).getDataStore().update();
			if (!falg) {
				this.messageBox("E0001");
				return falg;
			}
			this.messageBox("P0001");
			// TABLE锁定
			initDataStoreToTable();
			this.clearFlg = "N";
			onDiagPnChange(false);// 过敏史标签改变颜色

			// add by wangb 2015/11/06 更新ADM_INP表的过敏字段 START
			String sql = "SELECT * FROM OPD_DRUGALLERGY WHERE MR_NO = '"
					+ this.getMrNo() + "' AND DRUG_TYPE != 'N'";
			TParm allergyParm = new TParm(TJDODBTool.getInstance().select(sql));
			String allergy = "";
			if (allergyParm.getCount() > 0) {// modify by guoy 20151116
				allergy = "Y";
			} else {
				allergy = "N";
			}
			sql = "UPDATE ADM_INP SET ALLERGY='" + allergy
					+ "' WHERE CASE_NO = '" + this.getCaseNo() + "'";
			TJDODBTool.getInstance().update(sql);
			// add by wangb 2015/11/06 更新ADM_INP表的过敏字段 END
			return falg;
		}
		// 院前用药记录保存
		if (tab.getSelectedIndex() == 5) {
			boolean flag = false;
			TParm checkParm = this.checkPADataSave();

			if (checkParm.getErrCode() < 0) {
				this.messageBox(checkParm.getErrText());
				TTable table = this.getTTable(TABLE6);
				table.getTable().grabFocus();
				table.setSelectedRow(checkParm.getInt("ROW"));
				table.setSelectedColumn(checkParm.getInt("COL"));
				return flag;
			}

			flag = this.savePAData();
			if (!flag) {
				this.messageBox("E0001");
				return flag;
			}
			this.messageBox("P0001");
			// TABLE锁定
			initDataStoreToTable();
			this.clearFlg = "N";
			return flag;
		}
		// ds.showDebug();
		// 医嘱检核
		TParm parm = checkOrderSave();
		// System.out.println("======药品提示====="+parm.getErrText());
		// fux modify start
		// $$=========add by lx 2012-07-04 替代药品提示START=============$$//
		/*
		 * if (parm.getErrText().indexOf("库存不足") != -1) { String orderCode =
		 * parm.getErrText().split(";")[1]; TParm inParm = new TParm();
		 * 
		 * inParm.setData("orderCode", orderCode);
		 * this.openDialog("%ROOT%\\config\\pha\\PHAREDrugMsg.x", inParm);
		 * return false; }
		 */
		// $$=========add by lx 2012-07-04 END=============$$//
		this.antflg = true;
		// fux modify end
		// 医保检验未通过
		if (parm.getErrCode() == -1) {
			if (!parm.getErrText().equals("")) {
				this.messageBox(parm.getErrText());
			}
			return false;
		}
		if (parm.getErrCode() < 0 && parm.getErrCode() != -6
				&& parm.getErrCode() != -9) {
			// 合理用药
			// System.out.println("7777777777777777");
			if (parm.getErrCode() == -102) {
				// TABLE锁定
				initDataStoreToTable();
				if (tab.getSelectedIndex() == 2)
					this.onQuery();
				else {
					// 过滤TABLE
					if ("N".equals(this.clearFlg))
						this.onChange();
					if ("Y".equals(this.clearFlg))
						this.delRowNull();
					if ("Q".equals(this.clearFlg))
						this.onQuery();
				}
				return false;
			}
			// 医令管控
			if (parm.getErrCode() == -101) {
				String flg = parm.getValue("FORCE_FLG", 0);
				if (flg.equals("Y")) {
					this.messageBox(parm.getErrText());
				}
				int row = parm.getInt("ROW", 0);
				this.DelOrder(row);
				// TABLE锁定
				initDataStoreToTable();
				if (tab.getSelectedIndex() == 2)
					this.onQuery();
				else {
					// 过滤TABLE
					if ("N".equals(this.clearFlg))
						this.onChange();
					if ("Y".equals(this.clearFlg))
						this.delRowNull();
					if ("Q".equals(this.clearFlg))
						this.onQuery();
				}
				return false;
			}
			// 错误处理
			// this.messageBox_("111错误处理"+parm);
			if (tab.getSelectedIndex() == 3) {
				this.messageBox(parm.getErrText());
				// 停止闪动
				this.stopColor();
				this.chagePage = true;
				return false;
			}
			errSaveOrder(parm);
			this.messageBox(parm.getErrText());
			// 停止闪动
			this.stopColor();
			this.chagePage = true;
			return false;
		}

		// 修改过的医嘱处理
		if (parm.getErrCode() == -6 || parm.getErrCode() == -9) {
			// 错误处理
			errSaveOrder(parm);
			if (this.messageBox("提示信息", parm.getErrText(), this.YES_NO_OPTION) != 0) {
				// TABLE锁定
				initDataStoreToTable();
				if (tab.getSelectedIndex() == 2)
					this.onQuery();
				else {
					// 过滤TABLE
					if ("N".equals(this.clearFlg))
						this.onChange();
					if ("Y".equals(this.clearFlg))
						this.delRowNull();
					if ("Q".equals(this.clearFlg))
						this.onQuery();
				}
				this.chagePage = true;
				return false;
			}
		}
		// 判断申请单
		TDataStore orderData = odiObject.getDS("ODI_ORDER");
		// 拿到申请单结果集
		TParm emrParm = OrderUtil.getInstance()
				.getOrderPasEMR(orderData, "ODI");
		// ====pangben 2013-7-30 添加ODI_PHA 抗菌素申请单
		TParm phaEmrParm = OrderUtil.getInstance().getOrderPasEMR(orderData,
				"ODI_PHA");
		String msg = ""; // add by wanglong 20121210
		if (!(msg = checkAntiBioticWayisFilled(odiObject)).equals("")) { // add
			// System.out.println("phaEmrParm:::::" + phaEmrParm);
			System.out.println("COUNT:::"
					+ phaEmrParm.getInt("ACTION", "COUNT"));
			if (null != phaEmrParm && phaEmrParm.getInt("ACTION", "COUNT") > 0) {
				System.out.println("phaEmrParm:::" + phaEmrParm);
				this.messageBox("保存抗菌药品存在问题，请联系信息中心");
			}
			// by
			// wanglong
			// 20121210
			// actionParm.setData("ANTIBIOTIC_WAY",parm.getValue("ANTI_FLG"));
			// this.messageBox("<html><font color=\"red\">" + msg
			// + "</font> 属于抗菌药品！</html>\n由于没填写抗菌标识，将不会被保存");
		}
		// 当前保存的数据存在抗菌药品===pangben 2013-9-10
		if (null != phaEmrParm && phaEmrParm.getInt("ACTION", "COUNT") > 0) {
			// 直接保存或者切换页签保存标记
			if (tabSaveFlg) {
				this.messageBox("含有抗菌药品,不可切换页签。");
				return false;
			}
			System.out.println("phaApproveFlg:::::" + phaApproveFlg);
			if (phaApproveFlg.equals("Y")) {// 不用弹会诊单,已经存在医嘱
			} else {
				// 检核医生是否有抗菌药物的证照重整TParm
				TParm newPhaEmrParm = phaEmrParm;
				if (tab.getSelectedIndex() == 1) {// 长期医嘱时
					newPhaEmrParm = reCheckAnti(phaEmrParm);
				}
				if (newPhaEmrParm.getErrCode() < 0) {
					this.messageBox(newPhaEmrParm.getErrText());
					return false;
				}
				System.out.println("newPhaEmrParm：：：" + newPhaEmrParm);
				System.out.println("ORDER_CODE:COUNT:::"
						+ newPhaEmrParm.getCount("ORDER_CODE"));
				if (newPhaEmrParm.getCount("ORDER_CODE") <= 0) {
					return false;
				}
				// phaEmrParm.setData("TYPE_CHANGE",typeChange);/
				TParm result = OrderUtil.getInstance().getPhaAntiCreate(
						newPhaEmrParm, caseNo, mrNo, tab.getSelectedIndex(),
						this);
				if (result.getErrCode() < 0 && result.getErrCode() != -9) {
					this.messageBox(result.getErrText());
					if (null != result.getValue("MESSAGE")
							&& result.getValue("MESSAGE").equals("Y")) {
						// onInpDepApp();
						onConsApply();
						initDataStoreToTable();
					}
					return false;
				} else if (result.getErrCode() == -9) {
					if (this.messageBox("提示", result.getErrText(), 2) != 0) {
						return false;
					}
				}
			}
			String sql = "SELECT CASE_NO FROM PHA_ANTI WHERE CASE_NO='"
					+ caseNo + "'";
			TParm phaAntiParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (phaAntiParm.getCount() <= 0) {
				this.messageBox("保存抗菌药品存在问题，请联系信息中心");
			}
			phaApproveFlg = "";
		}
		TDS newDs = odiObject.getDS("ODI_ORDER");
		ds.setFilter("");
		// ds.setSort("ORDER_NO,ORDER_SEQ");
		ds.filter();
		// 住院中药最后赋值
		if (tab.getSelectedIndex() == 3) {
			onChangeIGOrder();
		}

		// 长期医嘱时校验是否越级审批 yanjing
		if (tab.getSelectedIndex() == 1) {
			isOverRideCheck(odiObject);
		}
		if (newCount > 0) {// wanglong add 20150123
			String sql = "SELECT A.DS_DATE, B.MR_NO FROM ADM_INP A, SYS_BED B "
					+ " WHERE A.BED_NO = B.BED_NO(+)  AND A.CASE_NO = B.CASE_NO(+) "
					+ "   AND A.MR_NO = B.MR_NO(+) AND B.ACTIVE_FLG = 'Y' "
					+ "   AND A.CASE_NO = '#'";
			sql = sql.replaceFirst("#", this.getCaseNo());
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return false;
			}
			if (result.getCount() < 1) {
				this.messageBox("该病患不在床，不能开立医嘱！请向护士站询问病人状态");
				return false;
			}
			if (result.getTimestamp("DS_DATE", 0) != null) {
				this.messageBox("该病患已出院，不能开立医嘱！请向护士站询问病人状态");
				return false;
			}
		}
		if (((TParm) this.getParameter()).getData("ODI", "OPERATOR") != null
				&& ((TParm) this.getParameter()).getData("ODI", "OPERATOR")
				.equals("OPE_N")) {
			odiObject.inparam = (TParm) this.getParameter();
		}
		// 保存
		if (!odiObject.update()) {
			if (!odiObject.update()) {
				this.messageBox("E0001");
				return false;
			}
		}

		// ===================针对未审核医嘱细项ORDERSET_GROUP_NO与主项不同的BUG，在保存前检测并提示
		TDS ds1 = odiObject.getDS("ODI_ORDER");
		String buff1 = ds1.isFilter() ? ds1.FILTER : ds1.PRIMARY;
		TParm vParm1 = ds1.getBuffer(buff1);// wanglong add 20140523
		TParm updateParm = new TParm();
		for (int i = 0; i < vParm1.getCount(); i++) {

			if (vParm1.getBoolean("#NEW#", i)) {
				if (vParm1.getValue("ORDER_CODE", i).toString().length() > 0) {
					updateParm.addData("ORDER_NO", vParm1.getValue("ORDER_NO",
							i));
					updateParm.addData("ORDER_SEQ", vParm1.getValue(
							"ORDER_SEQ", i));
					updateParm.addData("ORDER_CODE", vParm1.getValue(
							"ORDER_CODE", i));
					updateParm.addData("MEDI_QTY", vParm1.getValue("MEDI_QTY",
							i));
					updateParm.addData("MEDI_UNIT", vParm1.getValue(
							"MEDI_UNIT", i));
					updateParm.addData("FREQ_CODE", vParm1.getValue(
							"FREQ_CODE", i));
					updateParm.addData("EFF_DATE", vParm1.getTimestamp(
							"EFF_DATE", i));
					updateParm.addData("CASE_NO", this.getCaseNo());
				}

			}

			if (vParm1.getBoolean("#NEW#", i)
					&& !vParm1.getValue("ORDERSET_CODE", i).equals("")
					&& vParm1.getValue("SETMAIN_FLG", i).equals("N")) {
				int j = 0;
				for (; j < vParm1.getCount(); j++) {
					if (j == i) {
						continue;
					}
					if (vParm1.getBoolean("#NEW#", j)
							&& !vParm1.getValue("ORDERSET_CODE", j).equals("")
							&& vParm1.getValue("SETMAIN_FLG", j).equals("Y")
							&& vParm1.getValue("ORDERSET_CODE", j).equals(
									vParm1.getValue("ORDERSET_CODE", i))
							&& vParm1.getValue("ORDERSET_GROUP_NO", j).equals(
									vParm1.getValue("ORDERSET_GROUP_NO", i))) {
						break;
					}
				}
				if (j == vParm1.getCount()) {
					System.out.println("-----------Debug1 begin---------");
					ds1.showDebug();
					System.out.println("-----------Debug1 end-----------");
					this.messageBox("+1集合医嘱主细项存在不同的GROUP_NO，属于严重BUG，请联系蓝创");
				}
			}
		}

		odiStationDrools.updateCkbLog(updateParm); // add by huangtt 20150819

		this.messageBox("P0001");
		// 出院带药
		if (tab.getSelectedIndex() == 2) {
			onPrintOrder();
		}
		// 中药
		if (tab.getSelectedIndex() == 3) {
			onPrintChnOrder();
		}
		// 有新增数据时向对应的护士站发送消息
		if (newCount > 0) {
			// this.sendHl7Messages();
			this.sendInwStationMessages();
		}
		// 设置出院带药处方签
		initDSRxNoCombo();
		// 设置中药饮片处方签
		initIGRxNoCombo();
		// TABLE锁定
		initDataStoreToTable();
		// 设置出院带药处方签默认选项
		this.getTComboBox("RX_NO").setSelectedID("");
		this.onRxNoSel();
		// 设置中药饮片处方签默认选项
		this.getTComboBox("IG_RX_NO").setSelectedID("");
		this.onRxNoSelIG();
		this.clearFlg = "N";
		this.setValue("MEDI_QTYALL", 0.0);
		if (emrParm.getInt("ACTION", "COUNT") > 0) {
			// 调用申请单
			onApplyList();
		}
		if (clpDsFlg) {// pangben 2015-8-14 出院操作时，1.临床路径校验手术是否开立 2.超过金额校验
			TParm clpParm = new TParm();
			clpParm.setData("CASE_NO", caseNo);
			clpParm.setData("MR_NO", mrNo);
			CLPTool.getInstance().onCheckOutAdmOpeBook(clpParm, this);
			CLPTool.getInstance().onCheckOutAmtIbs(clpParm, this);
		}
		// 住院医生站校验第一次进入路径操作
		TParm result = CLPTool.getInstance().checkOdiClpExe(
				getClpOdiStationParm(), this);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return false;
		}
		// ============pangben 2013-7-30
		// if ((msg =
		// checkAntiBioticWayisFilled(odiObject)).equals("")&&phaEmrParm.getInt("ACTION",
		// "COUNT") > 0) {
		// // 调用抗菌素申请单
		// onApplyListPha();
		// onBoardMessage(phaEmrParm);
		// }
		// 20151026 wangjc start 宕机备份
		class BackThread extends Thread {
			TControl tc;

			public BackThread(TControl control) {
				tc = control;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 传入CASE_NO
				OPDBackupDataToPDFControl opdBackupDataToPDFControl = new OPDBackupDataToPDFControl(
						caseNo);
				opdBackupDataToPDFControl.backupDataToPDF(caseNo, tc);
			}

		}
		BackThread backThread = new BackThread(this);
		backThread.start();
		// 20151026 wangjc end 宕机备份
		if (((TParm) this.getParameter()).getData("ODI", "OPERATOR") != null
				&& ((TParm) this.getParameter()).getData("ODI", "OPERATOR")
				.equals("OPE_N")) {
			try {
				onFee(newDs);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		// 20151106 wangjc add 停跳用法医嘱计费
		try {
			if (opeFlg) {
				cardioplegiaFee(newDs);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			this.messageBox("停跳用法医嘱计费失败!");
		}
		return true;
	}

	/**
	 * 术中医嘱计费
	 */
	public void onFee(TDS ds) {
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		int newRow[] = ds.getNewRows(buff);
		TParm result = new TParm();
		int count = 0;
		for (int i : newRow) {
			if (!ds.isActive(i, buff))
				continue;
			TParm tempParm = ds.getRowParm(i, buff);// =====pangben 2015-8-14
			result.setRowData(count, tempParm);
			count++;
		}
		result.setCount(count);
		onCheckOpOrder(result);
		insertInterPlatData(result);
	}

	/**
	 * 插入介入护理平台数据
	 */
	public void insertInterPlatData(TParm parm) {
		// String date =
		// TJDODBTool.getInstance().getDBTime().toString().substring(0,19).replaceAll("-",
		// "/");
		TParm result = new TParm();
		int seqNo = OPEDeptOpTool.getInstance().getMaxSeqNo(this.getCaseNo());
		int count = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getValue("HIDE_FLG", i).equals("N")) {
				System.out
				.println("ORDER_SEQ:" + parm.getValue("ORDER_SEQ", i));
				System.out.println("ORDER_NO:" + parm.getValue("ORDER_NO", i));
				result.setData("SAVE_FLG", i, "Y");
				result.setData("CASE_NO", i, this.getCaseNo());
				result.setData("SEQ_NO", i, ++seqNo);
				result.setData("TIME", i, parm.getValue("NS_CHECK_DATE", i)
						.toString().substring(0, 19).replaceAll("-", "/"));
				result.setData("ORDER_DESC", i, parm.getValue("ORDER_DESC", i));
				result.setData("HEART_RATE", i, "");
				result.setData("BREATH", i, "");
				result.setData("PRESSURE", i, "");
				result.setData("OXYGEN_SATURATION", i, "");
				result.setData("PAIN_ASSESSMENT", i, "");
				result.setData("ILLNESS_RECORD", i, "");
				result.setData("OP_DEPT_CODE", i, this.getDeptCode());
				result.setData("STATION_CODE", i, this.getStationCode());
				result.setData("ORDER_CODE", i, parm.getValue("ORDER_CODE", i));
				result.setData("MEDI_QTY", i, parm.getDouble("MEDI_QTY", i));
				result.setData("MEDI_UNIT", i, parm.getDouble("MEDI_UNIT", i));
				result.setData("ROUTE_CODE", i, parm.getValue("ROUTE_CODE", i));
				result.setData("OPT_USER", i, Operator.getID());
				result.setData("OPT_TERM", i, Operator.getIP());
				result.setData("OPBOOK_SEQ", i, this.getOpBookSeq());
				result.setData("ORDER_NO", i, parm.getValue("ORDER_NO", i));
				result.setData("ORDER_SEQ", i, parm.getInt("ORDER_SEQ", i));
				count++;
			}
		}
		result.setCount(count);
		OPEDeptOpTool.getInstance().insertInterData(result);
	}

	/**
	 *保存时,审核术中医嘱
	 */
	public void onCheckOpOrder(TParm parm) {
		TParm data = new TParm();
		String checkDate = TJDODBTool.getInstance().getDBTime().toString();
		Timestamp checkDateTime = StringTool.getTimestamp(checkDate.substring(
				0, 19).replaceAll("-", "").replaceAll(":", ""),
				"yyyyMMdd HHmmss");
		int count = 0;
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {

			data.addData("CASE_NO", this.getCaseNo());
			data.addData("ORDER_NO", parm.getValue("ORDER_NO", i));
			data.addData("ORDER_SEQ", parm.getValue("ORDER_SEQ", i));
			data.addData("SETMAIN_FLG", parm.getValue("SETMAIN_FLG", i));
			data.addData("ORDERSETGROUP_NO", parm.getValue("ORDERSET_GROUP_NO",
					i));
			data.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
			data.addData("ORDERSET_CODE", parm.getValue("ORDERSET_CODE", i));
			data.addData("CHECK_DATETIME", checkDateTime);
			data.addData("TAKEMED_ORG", parm.getValue("TAKEMED_ORG", i));
			count++;
		}
		data.setCount(count);
		TParm toData = new TParm();
		toData.setData("OPT_USER", Operator.getID());
		toData.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		toData.setData("OPT_TERM", Operator.getIP());
		toData.setData("dataParm", data.getData());
		TParm result = TIOM_AppServer.executeAction(
				"action.inw.InwOrderCheckAction", "onCheckOP", toData);
	}

	// /**
	// * 会诊单
	// * =======pangben 2013-9-10
	// */
	// public void onInpDepApp() {
	// TParm parm = new TParm();
	// parm.setData("MR_NO", mrNo);
	// parm.setData("CASE_NO", caseNo);
	// parm.setData("ADM_DATE",getAdmDate());
	// this.openDialog("%ROOT%\\config\\inp\\INPDeptApp.x", parm);
	// }
	private TParm getClpOdiStationParm() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", mrNo);
		parm.setData("PAT_NAME", this.getPatName());
		parm.setData("CTZ_CODE", this.getCtzCode());
		parm.setData("DEPT_CODE", this.getDeptCode());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());
		return parm;
	}

	/**
	 * 得到打印数据
	 * 
	 * @param type
	 *            String
	 * @param rxNo
	 *            String
	 * @return TParm
	 */
	public TParm getOrderParmAmt(String type, String rxNo) {
		TParm result = new TParm(this.getDBTool().select(
				" SELECT SUM(TOT_AMT) AMT" + " FROM ODI_ORDER A,IBS_ORDD B"
						+ " WHERE A.CASE_NO='" + this.getCaseNo() + "'"
						+ " AND A.RX_KIND='" + type + "'" + " AND A.RX_NO='"
						+ rxNo + "'" + " AND A.CASE_NO = B.CASE_NO"
						+ " AND A.ORDER_NO = B.ORDER_NO"
						+ " AND A.ORDER_SEQ = B.ORDER_SEQ"));
		return result;
	}

	/**
	 * 打印中药处方签
	 */
	public void onPrintChnOrder() {
		String rxNo = this.getTComboBox("IG_RX_NO").getSelectedID();
		if (rxNo.length() <= 0) {
			// 请选择处方签
			this.messageBox("E0029");
			return;
		}
		TParm parm = this.getOrderParm("IG", rxNo);
		if (parm.getCount() <= 0) {
			// 无打印数据！
			this.messageBox("E0010");
			return;
		}
		TParm orderParm = new TParm();
		orderParm.setData("RX_TYPE", "DS");
		orderParm.setData("CASE_NO", this.getCaseNo());
		orderParm.setData("RX_NO", rxNo);
		orderParm.setData("ADDRESS", "TEXT", "ODIStationControl LPT1");
		orderParm.setData("PRINT_TIME", "TEXT", StringTool.getString(SystemTool
				.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss"));
		if ("en".equals(this.getLanguage())) {
			orderParm.setData("HOSP_NAME", "TEXT", Manager.getOrganization()
					.getHospitalENGFullName(Operator.getRegion()));
		} else {
			orderParm.setData("HOSP_NAME", "TEXT", Manager.getOrganization()
					.getHospitalCHNFullName(Operator.getRegion()));
		}
		orderParm.setData("ORDER_TYPE", "TEXT", "住院中医");
		orderParm.setData("ORG_CODE", "TEXT", "药房:"
				+ ((TextFormatINDOrg) this.getComponent("DEPT_CODEIG"))
				.getText());
		orderParm.setData("PAY_TYPE", "TEXT", "费别："
				+ OdiOrderTool.getInstance().getCTZDesc(this.getCtzCode()));
		orderParm.setData("PAT_NAME", "TEXT", "姓名：" + this.getPatName());
		orderParm.setData("SEX_CODE", "TEXT", "性别："
				+ OdiUtil.getInstance().getDictionary("SYS_SEX",
						this.getSexCode()));
		orderParm.setData("AGE", "TEXT", "年龄："
				+ OdiUtil.getInstance().showAge(this.getBirthDay(),
						this.getAdmDate()));
		orderParm.setData("MR_NO", "TEXT", "病案号：" + this.getMrNo());
		orderParm.setData("DEPT_CODE", "TEXT", "科室："
				+ OdiUtil.getInstance().getDeptDesc(this.getDeptCode()));
		orderParm.setData("CLINIC_ROOM", "TEXT", "病区："
				+ OdiUtil.getInstance().getStationDesc(this.getStationCode()));
		orderParm.setData("DR_CODE", "TEXT", "医生：" + Operator.getName());
		orderParm.setData("ADM_DATE", "TEXT", "时间："
				+ StringTool
				.getString(this.getAdmDate(), "yyyy/MM/dd HH:mm:ss"));
		orderParm.setData("BAR_CODE", "TEXT", this.getMrNo());
		// orderParm.setData("TAKE_DAYS","TEXT","付数:"+this.getValueInt("RF"));
		orderParm.setData("TAKE_DAYS", "TEXT", "付数:"
				+ parm.getData("TAKE_DAYS", 0));
		orderParm.setData("FREQ_CODE", "TEXT", "煎次："
				+ ((TextFormatSYSPhaFreq) this.getComponent("IGFREQCODE"))
				.getText());
		orderParm.setData("ROUTE_CODE", "TEXT", "方法："
				+ ((TextFormatSYSPhaRoute) this.getComponent("IG_ROUTE"))
				.getText());
		orderParm.setData("DCT_TAKE_QTY", "TEXT", "每次服用量："
				+ this.getValueString("YPJL") + "克");
		// int rfNum = this.getValueInt("RF");
		// String freqCode = this.getValueString("IGFREQCODE");
		// int rfNumCount =
		// OdiUtil.getInstance().getPACKAGE_AMT(rfNum,freqCode);
		// orderParm.setData("PACKAGE_TOT","TEXT","每付总克数："+rfNumCount);
		orderParm.setData("PACKAGE_TOT", "TEXT", "每付总克数："
				+ this.getValueString("MEDI_QTYALL") + "克");
		int rowCount = parm.getCount();
		int orderRowCount = 1;
		int orderColumns = 1;
		for (int i = 0; i < rowCount; i++) {
			orderParm.setData("ORDER_DESC" + orderRowCount + orderColumns,
					"TEXT", parm.getData("ORDER_DESC", i));
			orderParm.setData("MEDI_QTY" + orderRowCount + orderColumns,
					"TEXT", parm.getData("MEDI_QTY", i));
			orderParm.setData("DCTAGENT" + orderRowCount + orderColumns,
					"TEXT", OdiUtil.getInstance().getDictionary("PHA_DCTEXCEP",
							parm.getValue("DCTEXCEP_CODE", i)));
			if (orderColumns == 4)
				orderColumns = 1;
			if ((i + 1) % 4 == 0)
				orderRowCount++;
			else
				orderColumns++;
		}
		TParm parmAmt = this.getOrderParmAmt("IG", rxNo);
		if (parmAmt.getCount() == 0 || parmAmt.getData("AMT", 0) == null
				|| parmAmt.getValue("AMT", 0).equalsIgnoreCase("null")
				|| parmAmt.getDouble("AMT", 0) == 0)
			orderParm.setData("AMT", "TEXT", " ");
		else
			orderParm.setData("AMT", "TEXT", "金额:"
					+ parmAmt.getDouble("AMT", 0) + "元");
		this.openPrintDialog(
				"%ROOT%\\config\\prt\\ODI\\OdiChnOrderSheetNew.jhw", orderParm,
				false);
	}

	/**
	 * 打印出院带药处方签 =============shibaoliu 20110718 修改将英文去掉
	 */
	public void onPrintOrder() {
		String rxNo = this.getTComboBox("RX_NO").getSelectedID();
		if (rxNo.length() <= 0) {
			this.messageBox("E0029");
			return;
		}
		TParm parm = this.getOrderParm("DS", rxNo);
		if (parm.getCount() <= 0) {
			this.messageBox("E0010");
			return;
		}
		TParm orderParm = new TParm();
		orderParm.setData("RX_TYPE", "DS");
		orderParm.setData("CASE_NO", this.getCaseNo());
		orderParm.setData("RX_NO", rxNo);
		orderParm.setData("ADDRESS", "TEXT", "ODIStationControl From "
				+ Operator.getID() + " To LPT1");
		orderParm.setData("PRINT_TIME", "TEXT", StringTool.getString(SystemTool
				.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss"));
		orderParm.setData("HOSP_NAME", "TEXT", OpdRxSheetTool.getInstance()
				.getHospFullName());
		orderParm.setData("HOSP_NAME_ENG", "TEXT", Operator
				.getHospitalENGShortName());
		// if("en".equals(this.getLanguage())){
		// orderParm.setData("HOSP_NAME","TEXT",Manager.getOrganization().getHospitalENGFullName(Operator.getRegion()));
		// }else{
		// orderParm.setData("HOSP_NAME","TEXT",Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()));
		// }
		orderParm.setData("ORDER_TYPE", "TEXT", "(住)出院带药");
		orderParm.setData("ORG_CODE", "TEXT", "药房:"
				+ ((TComboOrgCode) this.getComponent("DEPT_CODEDS"))
				.getSelectedName());
		orderParm.setData("PAY_TYPE", "TEXT", "费别:"
				+ OdiOrderTool.getInstance().getCTZDesc(this.getCtzCode()));
		orderParm.setData("PAY_TYPE_ENG", "TEXT", "Cate:"
				+ OpdRxSheetTool.getInstance().getPayTypeEngName(
						this.getCtzCode()));
		orderParm.setData("MR_NO", "TEXT", "病案号:" + this.getMrNo());
		orderParm.setData("MR_NO_ENG", "TEXT", "Pat ID:" + this.getMrNo());
		orderParm.setData("PAT_NAME", "TEXT", "姓名:" + this.getPatName());
		orderParm.setData("PAT_NAME_ENG", "TEXT", "Name:"
				+ OpdRxSheetTool.getInstance().getPatEngName(this.getMrNo()));
		orderParm.setData("PAT_ID", "TEXT", "病患身份证号："
				+ OpdRxSheetTool.getInstance().getId(this.getMrNo()));
		orderParm.setData("SEX_CODE", "TEXT", "性别:"
				+ OdiUtil.getInstance().getDictionary("SYS_SEX",
						this.getSexCode()));
		orderParm.setData("SEX_CODE_ENG", "TEXT", "Gender:"
				+ DictionaryTool.getInstance().getEnName("SYS_SEX",
						this.getSexCode()));
		orderParm.setData("BIRTHDAY", "TEXT", "出生日期:"
				+ StringTool.getString(this.getBirthDay(), "yyyy/MM/dd"));
		orderParm.setData("BBB", "TEXT", "Birthday:"
				+ StringTool.getString(this.getBirthDay(), "yyyy/MM/dd"));
		orderParm.setData("AGE", "TEXT", "年龄:"
				+ OdiUtil.getInstance().showAge(this.getBirthDay(),
						this.getAdmDate()));
		orderParm.setData("AGE_ENG", "TEXT", "Age:"
				+ OdoUtil.showEngAge(this.getBirthDay(), this.getAdmDate()));
		orderParm.setData("DEPT_CODE", "TEXT", "科室:"
				+ OdiUtil.getInstance().getDeptDesc(this.getDeptCode()));
		orderParm.setData("DEPT_CODE_ENG", "TEXT", "Dept:"
				+ OpdRxSheetTool.getInstance().getDeptEngName(
						this.getDeptCode()));
		orderParm.setData("CLINIC_ROOM", "TEXT", "病区:"
				+ OdiUtil.getInstance().getStationDesc(this.getStationCode()));
		orderParm.setData("CLINIC_ROOM_ENG", "TEXT", "Station:"
				+ OdiUtil.getInstance()
				.getStationDescEnd(this.getStationCode()));
		orderParm.setData("DR_CODE", "TEXT", "医生:" + Operator.getName());
		orderParm.setData("DR_CODE_ENG", "TEXT", "M.D.:" + Operator.getName());
		orderParm.setData("ADM_DATE", "TEXT", "入院时间:"
				+ StringTool
				.getString(this.getAdmDate(), "yyyy/MM/dd HH:mm:ss"));
		orderParm.setData("ADM_DATE_ENG", "TEXT", "Date:"
				+ StringTool
				.getString(this.getAdmDate(), "yyyy/MM/dd HH:mm:ss"));
		orderParm.setData("FOOT_DR", "TEXT", "医师:" + Operator.getName());
		orderParm.setData("FOOT_DR_CODE", "TEXT", "医师代码:" + Operator.getID());
		orderParm.setData("BAR_CODE", "TEXT", this.getMrNo());
		orderParm.setData("DIAG", "TEXT", "诊断："
				+ OdiOrderTool.getInstance().getICDCode(this.getCaseNo()));
		orderParm.setData("DIAG_ENG", "TEXT", "Diagnosis:"
				+ OdiOrderTool.getInstance().getICDCodeEng(this.getCaseNo()));
		this.openPrintDialog("%ROOT%\\config\\prt\\ODI\\OdiOrderSheetNew.jhw",
				orderParm, false);
	}

	/**
	 * 得到打印数据
	 * 
	 * @param type
	 *            String
	 * @param rxNo
	 *            String
	 * @return TParm
	 */
	public TParm getOrderParm(String type, String rxNo) {
		TParm result = new TParm(this.getDBTool().select(
				"SELECT * FROM ODI_ORDER WHERE CASE_NO='" + this.getCaseNo()
				+ "' AND RX_KIND='" + type + "' AND RX_NO='" + rxNo
				+ "' ORDER BY ORDER_NO,ORDER_SEQ"));
		return result;
	}

	/**
	 * 检核医嘱
	 */
	public TParm checkOrderSave() {
		TParm result = new TParm();
		// 是否经护士审核
		boolean nsCheckFlg = odiObject.getAttributeBoolean("INW_NS_CHECK_FLG");
		// 拿到住院摆药时间
		Object obj = odiObject.getAttribute(odiObject.OID_DSPN_TIME);
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		// 新加的数据
		int newRow[] = ds.getNewRows(buff);
		// 修改的数据
		int modifRow[] = ds.getOnlyModifiedRows(buff);
		// 合理用药
		if (!checkDrugAuto()) {
			result.setErrCode(-102);
			return result;
		}

		Map index = new HashMap();
		index.put("ST", 0);
		index.put("UD", 1);
		index.put("DS", 2);
		index.put("IG", 3);
		index.put("F", 1);

		// 过敏记录TABLE
		TTable tab = this.getTTable(GMTABLE);
		// 过敏记录缓冲区
		String buffGM = tab.getDataStore().isFilter() ? tab.getDataStore().FILTER
				: tab.getDataStore().PRIMARY;
		int gmRowCount = tab.getDataStore().isFilter() ? tab.getDataStore()
				.rowCountFilter() : tab.getDataStore().rowCount();
				boolean insFlg = true;
				for (int i : newRow) {
					if (!ds.isActive(i, buff))
						continue;
					String order_code = ds.getRowParm(i, buff).getValue("ORDER_CODE");
					String order_desc = ds.getRowParm(i, buff).getValue("ORDER_DESC");
					// $$====================add by lx 2012-02-13 调 用医保医嘱检查
					// START============================$$//
					if (!insOrderCheck(order_code, this.getCtzCode(), order_desc)) {
						result.setErrCode(-1);
						return result;
					}
					// $$====================add by lx 2012-02-13 调
					// 用医保医嘱检查END============================$$//
					// $$====================add by lx 2012-02-14 调 用医保医嘱检查
					// START============================$$//
					/**
					 * if (ds.getRowParm(i, buff).getValue("ORDER_DESC").equals("")) {
					 * result.setErrCode(-1); result.setErrText(ds.getRowParm(i,
					 * buff).getValue( "ORDER_DESC") + "医嘱描述不能为空!");
					 * result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
					 * buff).getValue("RX_KIND"))); result.setData("ERR", "ORDER_CODE",
					 * ds.getRowParm(i, buff) .getValue("ORDER_CODE")); return result;
					 * 
					 * }
					 **/

					/**
					 * if (ds.getRowParm(i, buff).getDouble("DOSAGE_QTY") == 0) {
					 * result.setErrCode(-1); result.setErrText(ds.getRowParm(i,
					 * buff).getValue( "ORDER_DESC") + "总量不能为:0 \n Dosage for:0");
					 * 
					 * result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
					 * buff).getValue("RX_KIND"))); result.setData("ERR", "ORDER_CODE",
					 * ds.getRowParm(i, buff) .getValue("ORDER_CODE")); return result;
					 * 
					 * }
					 **/
					// $$====================add by lx 2012-02-14 调 用医保医嘱检查
					// START============================$$//

					// 进入医令控
					if (getctrflg(order_code).equals("Y")) {
						TParm crtParm = new TParm();
						crtParm.setData("ADM_TYPE", "I");
						crtParm.setData("CTZ_CODE", this.getCtzCode());
						crtParm.setData("ORDER_CODE", ds.getRowParm(i, buff).getValue(
								"ORDER_CODE"));
						crtParm.setData("CASE_NO", ds.getRowParm(i, buff).getValue(
								"CASE_NO"));
						crtParm.setData("MR_NO", ds.getRowParm(i, buff).getValue(
								"MR_NO"));
						crtParm.setData("Parm", ds.getRowParm(i, buff).getData());
						if (CTRPanelTool.getInstance().selCTRPanel(crtParm)
								.getErrCode() == 100) {
							// shibl add 20121101 添加电子病历绑定
							String subClass = CTRPanelTool.getInstance().selCTRPanel(
									crtParm).getValue("SUBCLASS_CODE");
							if (!subClass.equals("")) {
								Object o = (Object) this.openDialog(
										"%ROOT%\\config\\emr\\EMRUIcplNG.x", subClass);
							}
							if (!CTRPanelTool.getInstance().selCTRPanel(crtParm)
									.getValue("FORCE_FLG").equals("Y")) {
								if (this.messageBox("提示信息/Tip",
										""
												+ CTRPanelTool.getInstance()
												.selCTRPanel(crtParm).getValue(
														"MESSAGE") + ",继续开立?",
												0) != 0) {
									result.setErrCode(-101);
									result.setErrText(CTRPanelTool.getInstance()
											.selCTRPanel(crtParm).getValue("MESSAGE"));
									result.setData("ERR", "INDEX", index.get(ds
											.getRowParm(i, buff).getValue("RX_KIND")));
									result.setData("ERR", "ORDER_CODE", ds.getRowParm(
											i, buff).getValue("ORDER_CODE"));
									result.addData("ROW", i);
									return result;
								}
							} else {
								result.setErrCode(-101);
								result.setErrText(CTRPanelTool.getInstance()
										.selCTRPanel(crtParm).getValue("MESSAGE"));
								result.setData("ERR", "INDEX", index.get(ds.getRowParm(
										i, buff).getValue("RX_KIND")));
								result.setData("ERR", "ORDER_CODE", ds.getRowParm(i,
										buff).getValue("ORDER_CODE"));
								result.addData("ROW", i);
								result.addData("FORCE_FLG", "Y");
								return result;
							}
						}
					}
					// 用量
					if (ds.getRowParm(i, buff).getDouble("MEDI_QTY") == 0) {
						// this.messageBox_(ds.getRowParm(i,buff).getDouble("MEDI_QTY")+"==="+i);
						result.setErrCode(-1);
						if ("en".equals(this.getLanguage())) {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_ENG_DESC")
									+ " Dosage for:0");
						} else {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_DESC")
									+ "用量不能为:0 \n Dosage for:0");
						}
						result.setData("ERR", "INDEX", index.get(ds.getRowParm(i, buff)
								.getValue("RX_KIND")));
						result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
								.getValue("ORDER_CODE"));
						return result;
					}
					// 执行科室
					if (ds.getRowParm(i, buff).getValue("EXEC_DEPT_CODE").length() == 0) {
						result.setErrCode(-2);
						if ("en".equals(this.getLanguage())) {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_ENG_DESC")
									+ " Executive Dept in not null!");
						} else {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_DESC")
									+ "执行科室不能为空");
						}
						result.setData("ERR", "INDEX", index.get(ds.getRowParm(i, buff)
								.getValue("RX_KIND")));
						result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
								.getValue("ORDER_CODE"));
						return result;
					}
					// 频次
					if (ds.getRowParm(i, buff).getValue("FREQ_CODE").length() == 0) {
						result.setErrCode(-3);
						if ("en".equals(this.getLanguage())) {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_ENG_DESC")
									+ "Freq is not null");
						} else {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_DESC")
									+ "医嘱频次不可以为空");
						}
						result.setData("ERR", "INDEX", index.get(ds.getRowParm(i, buff)
								.getValue("RX_KIND")));
						result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
								.getValue("ORDER_CODE"));
						return result;
					}
					// 出院带药天数
					if (ds.getRowParm(i, buff).getInt("TAKE_DAYS") == 0
							&& "DS".equals(ds.getRowParm(i, buff).getValue("RX_KIND"))) {
						result.setErrCode(-4);
						if ("en".equals(this.getLanguage())) {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_ENG_DESC")
									+ "Do not think 0 days");
						} else {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_DESC")
									+ "医嘱天数不可以为0");
						}
						result.setData("ERR", "INDEX", index.get(ds.getRowParm(i, buff)
								.getValue("RX_KIND")));
						result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
								.getValue("ORDER_CODE"));
						return result;
					}
					// fux modify start
					// 检核库存
					/*
					 * if ("PHA".equals(ds.getRowParm(i, buff).getValue("CAT1_TYPE")) &&
					 * !"Y" .equals(ds.getRowParm(i, buff) .getValue("IS_REMARK"))) { if
					 * (!INDTool.getInstance().inspectIndStock( ds.getRowParm(i,
					 * buff).getValue("EXEC_DEPT_CODE"), ds.getRowParm(i,
					 * buff).getValue("ORDER_CODE"), ds.getRowParm(i,
					 * buff).getDouble("DOSAGE_QTY"))) { result.setErrCode(-5); if
					 * ("en".equals(this.getLanguage())) {
					 * result.setErrText(ds.getRowParm(i, buff).getValue(
					 * "ORDER_ENG_DESC") + "The medicine is lack of Storage"); } else {
					 * result .setErrText(ds.getRowParm(i, buff).getValue( "ORDER_DESC")
					 * + "库存不足;" + ds.getRowParm(i, buff).getValue( "ORDER_CODE")); }
					 * result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
					 * buff).getValue("RX_KIND"))); result.setData("ERR", "ORDER_CODE",
					 * ds.getRowParm(i, buff) .getValue("ORDER_CODE")); return result; }
					 * }
					 */
					// fux modify end
					// 检验药品用法
					if ("PHA".equals(ds.getRowParm(i, buff).getValue("CAT1_TYPE"))) {
						if (ds.getRowParm(i, buff).getValue("ROUTE_CODE").length() == 0) {
							result.setErrCode(-10);
							if ("en".equals(this.getLanguage())) {
								result.setErrText(ds.getRowParm(i, buff).getValue(
										"ORDER_ENG_DESC")
										+ " Route is not null");
							} else {
								result.setErrText(ds.getRowParm(i, buff).getValue(
										"ORDER_DESC")
										+ "用法不能为空！");
							}
							result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
									buff).getValue("RX_KIND")));
							result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
									.getValue("ORDER_CODE"));
							return result;
						}
					}
					// PHA的执行科室满足一定条件才能保存 duzhw modify 20131202
					if ("PHA".equals(ds.getRowParm(i, buff).getValue("CAT1_TYPE"))) {
						String execDeptCode = ds.getRowParm(i, buff).getValue(
								"EXEC_DEPT_CODE");
						if (!isOpeFlg() && !getPhaDeptSql(execDeptCode)) { // 术中医嘱
							// wanglong
							// modify
							// 20140707
							result.setErrCode(-10);
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_DESC")
									+ "医嘱类型为药品的执行科室不正确！");
							// return null;
							result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
									buff).getValue("RX_KIND")));
							result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
									.getValue("ORDER_CODE"));
							return result;
						}
					}

					// 长期医嘱首餐时间检查
					if ("UD".equals(ds.getRowParm(i, buff).getValue("RX_KIND"))) {
						// 当前时间是否超过今日摆药时间
						// 拿当日摆药时间点
						Timestamp nowTime = SystemTool.getInstance().getDate();
						String nowTimeStr = StringTool.getString(nowTime,
								"yyyyMMddHHmmss");
						// this.messageBox_("当前时间:" + nowTimeStr);
						String nowTimeBY = StringTool.getString(nowTime, "yyyyMMdd")
								+ obj + "00";
						// this.messageBox_("今日摆药时间:" + nowTimeBY);
						// 预开时间首餐时间
						Timestamp ykTime = ds.getRowParm(i, buff).getTimestamp(
								"EFF_DATE");
						String ykTimeStr = StringTool.getString(ykTime,
								"yyyyMMddHHmmss");
						// this.messageBox_("预开时间首餐时间:" + ykTimeStr);
						String tomorrowTime = StringTool.getString(StringTool.rollDate(
								nowTime, 1), "yyyyMMdd")
								+ "000000";
						String nowDateTime = StringTool.getString(StringTool.rollDate(
								nowTime, 0), "yyyyMMdd")
								+ "000000";

						// 预停时间
						String ytTimeStr = ds.getRowParm(i, buff).getValue("DC_DATE");
						if (!ytTimeStr.equals("")) {// =====时间校验pangben 2014-3-5
							ytTimeStr = ytTimeStr.substring(0, ytTimeStr.indexOf("."))
									.replace("-", "").replace(" ", "").replace(":", "");
						}
						// String ytTimeStr = StringTool.getString(ytTime,
						// "yyyyMMddHHmmss");
						// String ytTimeStr = "";
						// this.messageBox_("0点时间:"+tomorrowTime);
						// 是药品
						if ("PHA".equals(ds.getRowParm(i, buff).getValue("CAT1_TYPE"))) {
							// 当前时间如果大于等于当日摆药时间
							if (nowTimeStr.compareTo(nowTimeBY) > 0) {
								// 预开时间小于当日摆药时间
								if (ykTimeStr.compareTo(nowTimeBY) <= 0) {
									result.setErrCode(-7);
									if ("en".equals(this.getLanguage())) {
										result.setErrText(ds.getRowParm(i, buff)
												.getValue("ORDER_ENG_DESC")
												+ "Opening time cannot put less time::"
												+ StringTool.getString(StringTool
														.getTimestamp(nowTimeBY,
																"yyyyMMddHHmmss"),
														"yyyy/MM/dd HH:mm:ss"));
									} else {
										result.setErrText(ds.getRowParm(i, buff)
												.getValue("ORDER_DESC")
												+ "医嘱启用时间不能小于今日住院摆药时间:"
												+ StringTool.getString(StringTool
														.getTimestamp(nowTimeBY,
																"yyyyMMddHHmmss"),
														"yyyy/MM/dd HH:mm:ss"));
									}
									result.setData("ERR", "INDEX", index.get(ds
											.getRowParm(i, buff).getValue("RX_KIND")));
									result.setData("ERR", "ORDER_CODE", ds.getRowParm(
											i, buff).getValue("ORDER_CODE"));
									result.setData("ERR", "EFF_DATE", ds.getRowParm(i,
											buff).getData("EFF_DATE"));
									result.setData("ERR", "ROWINDEX", i);
									return result;
								}
							}

						} else {
							// 处置EFF_DATE是否已经超过昨日0点
							if (ykTimeStr.compareTo(nowDateTime) < 0) {
								result.setErrCode(-8);
								if ("en".equals(this.getLanguage())) {
									result.setErrText(ds.getRowParm(i, buff).getValue(
											"ORDER_ENG_DESC")
											+ "Opening time cannot cross:"
											+ StringTool.getString(StringTool
													.getTimestamp(nowDateTime,
															"yyyyMMddHHmmss"),
													"yyyy/MM/dd HH:mm:ss"));
								} else {
									result.setErrText(ds.getRowParm(i, buff).getValue(
											"ORDER_DESC")
											+ "医嘱启用时间不能小于今日凌晨零点:"
											+ StringTool.getString(StringTool
													.getTimestamp(nowDateTime,
															"yyyyMMddHHmmss"),
													"yyyy/MM/dd HH:mm:ss"));
								}
								result.setData("ERR", "INDEX", index.get(ds.getRowParm(
										i, buff).getValue("RX_KIND")));
								result.setData("ERR", "ORDER_CODE", ds.getRowParm(i,
										buff).getValue("ORDER_CODE"));
								result.setData("ERR", "EFF_DATE", ds
										.getRowParm(i, buff).getData("EFF_DATE"));
								result.setData("ERR", "ROWINDEX", i);
								return result;
							}
						}
						// 预开时间大于预停时间
						if (!"".equals(ytTimeStr)) {
							if (ykTimeStr.compareTo(ytTimeStr) > 0) {
								result.setErrCode(-7);
								if ("en".equals(this.getLanguage())) {
									result.setErrText(ds.getRowParm(i, buff).getValue(
											"ORDER_ENG_DESC")
											+ "Stop time cannot put less time::"
											+ StringTool.getString(StringTool
													.getTimestamp(ykTimeStr,
															"yyyyMMddHHmmss"),
													"yyyy/MM/dd HH:mm:ss"));
								} else {
									result.setErrText(ds.getRowParm(i, buff).getValue(
											"ORDER_DESC")
											+ "医嘱停用时间不能小于医嘱启用时间:"
											+ StringTool.getString(StringTool
													.getTimestamp(ykTimeStr,
															"yyyyMMddHHmmss"),
													"yyyy/MM/dd HH:mm:ss"));
								}
								result.setData("ERR", "INDEX", index.get(ds.getRowParm(
										i, buff).getValue("RX_KIND")));
								result.setData("ERR", "ORDER_CODE", ds.getRowParm(i,
										buff).getValue("ORDER_CODE"));
								result.setData("ERR", "DC_DATE", ds.getRowParm(i, buff)
										.getData("DC_DATE"));
								result.setData("ERR", "ROWINDEX", i);
								return result;
							}
						}
					}

					// 检核过敏记录
					// modify by huangtt 20150507
					if ("PHA".equals(ds.getRowParm(i, buff).getValue("CAT1_TYPE"))) {
						String message = DrugAllergyTool.getInstance()
								.CBDDrugAllergyCheck(
										ds.getRowParm(i, buff).getValue("MR_NO"),
										ds.getRowParm(i, buff).getValue("ORDER_CODE"),
										ds.getRowParm(i, buff).getValue("ORDER_DESC"));
						if (message.length() > 0) {
							result.setErrCode(-9);
							if ("en".equals(this.getLanguage())) {
								result.setErrText(ds.getRowParm(i, buff).getValue(
										"ORDER_ENG_DESC")
										+ "For drug allergy patients！");
							} else {
								result.setErrText(message);
							}
							result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
									buff).getValue("RX_KIND")));
							result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
									.getValue("ORDER_CODE"));
							result.setData("ERR", "ROWINDEX", i);
							return result;
						}

					}

					// for (int j = 0; j < gmRowCount; j++) {
					// if (!tab.getDataStore().isActive(j, buffGM))
					// continue;
					// TParm gmParm = tab.getDataStore().getRowParm(j, buffGM);
					// if (ds.getRowParm(i, buff).getValue("ORDER_CODE").equals(
					// gmParm.getValue("DRUGORINGRD_CODE"))
					// && "B".equals(gmParm.getValue("DRUG_TYPE"))) {
					// result.setErrCode(-9);
					// if ("en".equals(this.getLanguage())) {
					// result.setErrText(ds.getRowParm(i, buff).getValue(
					// "ORDER_ENG_DESC")
					// + "For drug allergy patients！");
					// } else {
					// result.setErrText(ds.getRowParm(i, buff).getValue(
					// "ORDER_DESC")
					// + "病患对药品过敏！");
					// }
					// result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
					// buff).getValue("RX_KIND")));
					// result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
					// .getValue("ORDER_CODE"));
					// result.setData("ERR", "ROWINDEX", i);
					// return result;
					// }
					// }

				}

				// 修改的医嘱
				for (int i : modifRow) {
					if (!ds.getRowParm(i, buff).getValue("RX_KIND").equals("UD")) {
						if (OrderUtil.getInstance().checkOrderNSCheck(
								ds.getRowParm(i, buff), nsCheckFlg)) {
							result.setErrCode(-6);
							if ("en".equals(this.getLanguage())) {
								result
								.setErrText(ds.getRowParm(i, buff).getValue(
										"ORDER_ENG_DESC")
										+ "Already audit may not modify, whether other orders?");
							} else {
								result.setErrText(ds.getRowParm(i, buff).getValue(
										"ORDER_DESC")
										+ "医嘱已经审核不可以修改是否保存其他医嘱？");
							}
							result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
									buff).getValue("RX_KIND")));
							result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
									.getValue("ORDER_CODE"));
							result.setData("ERR", "ROWINDEX", (Integer) ds.getItemData(
									i, "#ID#", buff));
							return result;
						}
					}
				}
				// 删除的医嘱处理
				int delCount = ds.getDeleteCount() < 0 ? 0 : ds.getDeleteCount();
				if (delCount > 0) {
					TParm delParm = ds.getBuffer(ds.DELETE);
					int delRowCount = delParm.getCount("ORDER_CODE");
					for (int i = 0; i < delRowCount; i++) {
						TParm temp = delParm.getRow(i);
						if (OrderUtil.getInstance().checkOrderNSCheck(temp, nsCheckFlg)) {
							result.setErrCode(-6);
							if ("en".equals(this.getLanguage())) {
								result
								.setErrText(temp.getValue("ORDER_ENG_DESC")
										+ "Already audit may not modify, whether other orders?");
							} else {
								result.setErrText(temp.getValue("ORDER_DESC")
										+ "医嘱已经审核不可以删除是否保存其他医嘱？");
							}

							result.setData("ERR", "INDEX", index.get(temp
									.getValue("RX_KIND")));
							result.setData("ERR", "ORDER_CODE", temp
									.getValue("ORDER_CODE"));
							result.setData("ERR", "ROWINDEX", temp.getInt("#ID#"));
							return result;
						}
					}
				}
				return result;
	}

	/**
	 * 获取PHA执行科室是否满足条件sql 开立药品改变执行科室时在药房审核时看不到信息问题。 duzhw modify 20131202
	 */
	public boolean getPhaDeptSql(String deptCode) {
		boolean flag = false;
		String sql = "select t.* from sys_dept t where t.dept_code in ("
				+ "select a.dept_code from sys_dept a where a.ipd_fit_flg = 'Y' and a.active_flg = 'Y' and a.classify = '2'"
				+ ") and t.dept_code = '" + deptCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount("DEPT_CODE") > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 保存错误处理
	 * 
	 * @param parm
	 *            TParm
	 */
	public void errSaveOrder(TParm parm) {
		// 是否经护士审核
		boolean nsCheckFlg = odiObject.getAttributeBoolean("INW_NS_CHECK_FLG");
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		// 转换窗口开关
		this.chagePage = false;
		tab.setSelectedIndex(parm.getInt("ERR", "INDEX"));
		TDS ds = odiObject.getDS("ODI_ORDER");
		// 新加数据
		int newRow[] = ds.getNewRows(ds.PRIMARY);
		// 修改的数据
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		int modifRow[] = ds.getOnlyModifiedRows(buff);
		for (int i : newRow) {
			if (!ds.isActive(i, ds.PRIMARY))
				continue;
			if (parm.getErrCode() == -1) {
				if (ds.getRowParm(i, ds.PRIMARY).getValue("ORDER_CODE").equals(
						parm.getValue("ERR", "ORDER_CODE"))
						&& ds.getRowParm(i, ds.PRIMARY).getInt("MEDI_QTY") == 0) {
					changeTableRowColor(i, parm.getInt("ERR", "INDEX"));
				}
			}
			if (parm.getErrCode() == -2) {
				if (ds.getRowParm(i, ds.PRIMARY).getValue("ORDER_CODE").equals(
						parm.getValue("ERR", "ORDER_CODE"))
						&& ds.getRowParm(i, ds.PRIMARY).getValue(
								"EXEC_DEPT_CODE").length() == 0) {
					changeTableRowColor(i, parm.getInt("ERR", "INDEX"));
				}
			}
			if (parm.getErrCode() == -3) {
				if (ds.getRowParm(i, ds.PRIMARY).getValue("ORDER_CODE").equals(
						parm.getValue("ERR", "ORDER_CODE"))
						&& ds.getRowParm(i, ds.PRIMARY).getValue("FREQ_CODE")
						.length() == 0) {
					changeTableRowColor(i, parm.getInt("ERR", "INDEX"));
				}
			}
			if (parm.getErrCode() == -4) {
				if (ds.getRowParm(i, ds.PRIMARY).getValue("ORDER_CODE").equals(
						parm.getValue("ERR", "ORDER_CODE"))
						&& ds.getRowParm(i, ds.PRIMARY).getInt("TAKE_DAYS") == 0) {
					changeTableRowColor(i, parm.getInt("ERR", "INDEX"));
				}
			}
			// 库存
			// if(parm.getErrCode()==-5){
			// if(ds.getRowParm(i,ds.PRIMARY).getValue("ORDER_CODE").equals(parm.getValue("ERR","ORDER_CODE"))){
			// changeTableRowColor(i,parm.getInt("ERR","INDEX"));
			// }
			// }
			// 长期医嘱预开预停时间判断(药)
			if (parm.getErrCode() == -7) {
				if (ds.getRowParm(i, ds.PRIMARY).getValue("ORDER_CODE").equals(
						parm.getValue("ERR", "ORDER_CODE"))
						&& ds.getRowParm(i, ds.PRIMARY).getData("EFF_DATE")
						.equals(parm.getData("ERR", "EFF_DATE"))) {
					changeTableRowColor(i, parm.getInt("ERR", "INDEX"));
				}
			}
			// 长期医嘱预开预停时间判断(处置)
			if (parm.getErrCode() == -8) {
				if (ds.getRowParm(i, ds.PRIMARY).getValue("ORDER_CODE").equals(
						parm.getValue("ERR", "ORDER_CODE"))
						&& ds.getRowParm(i, ds.PRIMARY).getData("EFF_DATE")
						.equals(parm.getData("ERR", "EFF_DATE"))) {
					changeTableRowColor(i, parm.getInt("ERR", "INDEX"));
				}
			}
			// 过敏记录检核
			if (parm.getErrCode() == -9) {
				if (ds.getRowParm(i, ds.PRIMARY).getValue("ORDER_CODE").equals(
						parm.getValue("ERR", "ORDER_CODE"))) {
					// changeTableRowColor(i,parm.getInt("ERR","INDEX"));
				}
			}
		}
		if (parm.getErrCode() == -6) {
			// 修改医嘱
			for (int i : modifRow) {
				if (OrderUtil.getInstance().checkOrderNSCheck(
						ds.getRowParm(i, buff), nsCheckFlg)) {
					// 此医嘱不保存
					ds.setActive(i, false, buff);
				}
			}
			// 删除的医嘱
			int delCount = ds.getDeleteCount() < 0 ? 0 : ds.getDeleteCount();
			if (delCount > 0) {
				TParm delParm = ds.getBuffer(ds.DELETE);
				int rowCountDel = ds.getDeleteCount();
				int delRowCount = delParm.getCount("ORDER_CODE");
				List delList = new ArrayList();
				for (int i = 0; i < delRowCount; i++) {
					TParm temp = delParm.getRow(i);
					if (OrderUtil.getInstance().checkOrderNSCheck(temp,
							nsCheckFlg)) {
						int delID = temp.getInt("#ID#");
						delList.add(delID);
					}
				}
				for (int i = 0; i < delList.size(); i++) {
					int rowID = Integer.parseInt(delList.get(i).toString());
					for (int j = 0; j < rowCountDel; j++) {
						if (!ds.isActive(j, ds.DELETE))
							if ((Integer) ds.getItemData(j, "#ID#", ds.DELETE) == rowID) {
								ds.setActive(j, false, ds.DELETE);
							}
					}
				}
			}
		}
	}

	/**
	 * 改变颜色闪动TABLE
	 * 
	 * @param row
	 *            int
	 * @param flg
	 *            boolean
	 */
	public void startRowColor() {
		if (colorThread != null)
			return;
		colorThread = new Thread() {
			public void run() {
				while (colorThread != null) {
					try {
						Thread.sleep(300);
						workColor();
					} catch (InterruptedException ex) {
					}
				}
				getTTable("TABLE" + (colorType + 1)).setRowColor(colorRow,
						new Color(255, 255, 255));
				getTTable("TABLE" + (colorType + 1)).setDSValue(colorRow);
			}
		};
		colorThread.start();
	}

	/**
	 * 停止闪动
	 */
	public void stopColor() {
		colorThread = null;
	}

	/**
	 * 改变TABLE颜色
	 * 
	 * @param row
	 *            int
	 */
	public void changeTableRowColor(int row, int type) {
		colorRow = row;
		colorType = type;
		startRowColor();
	}

	/**
	 * 开始工作
	 */
	public void workColor() {
		if (colorRow == -1) {
			this.stopColor();
			return;
		}
		colorRowState = !colorRowState;
		this.getTTable("TABLE" + (colorType + 1)).setRowColor(colorRow,
				colorRowState ? new Color(255, 0, 0) : new Color(0, 0, 0));
		this.getTTable("TABLE" + (colorType + 1)).setDSValue(colorRow);
	}

	/**
	 * 向对应的护士站发送消息
	 */
	public void sendInwStationMessages() {
		// this.messageBox("station code===="+this.getStationCode());
		// this.messageBox("depart code====="+this.getDeptCode());
		// $$ ============ Modified by lx 医生按部门,护士按病区收发消息2012/02/27
		// START==================$$//
		// client1 = SocketLink.running("", "ODISTATION", "odi");
		client1 = SocketLink
				.running("", this.getDeptCode(), this.getDeptCode());

		if (client1.isClose()) {
			out(client1.getErrText());
			return;
		}
		String admDate = StringTool.getString(this.getAdmDate(),
				"yyyy/MM/dd HH:mm:ss");
		/**
		 * client1.sendMessage( "INWSTATION", "CASE_NO:" + this.getCaseNo() +
		 * "|STATION_CODE:" + this.getStationCode() + "|MR_NO:" + this.getMrNo()
		 * + "|PAT_NAME:" + this.getPatName() + "|IPD_NO:" + this.getIpdNo() +
		 * "|ADM_DATE:" + admDate);
		 **/

		/**
		 * System.out.println("+++++sendMessage++++"+"CASE_NO:" +
		 * this.getCaseNo() + "|STATION_CODE:" + this.getStationCode() +
		 * "|MR_NO:" + this.getMrNo() + "|PAT_NAME:" + this.getPatName() +
		 * "|IPD_NO:" + this.getIpdNo() + "|ADM_DATE:" + admDate);
		 **/

		client1.sendMessage(this.getStationCode(), "CASE_NO:"
				+ this.getCaseNo() + "|STATION_CODE:" + this.getStationCode()
				+ "|MR_NO:" + this.getMrNo() + "|PAT_NAME:" + this.getPatName()
				+ "|IPD_NO:" + this.getIpdNo() + "|ADM_DATE:" + admDate);

		if (client1 == null)
			return;
		client1.close();

		// $$ ============ Modified by lx 医生按部门,护士按病区收发消息2012/02/27
		// END==================$$//
	}

	/**
	 * TABLE接受改变付值
	 * 
	 * @param tag
	 *            String[]
	 */
	public void acceptTextTable(String tag[]) {
		int length = tag.length;
		if (length == 0)
			return;
		for (int i = 0; i < length; i++) {
			TTable tabTemp = (TTable) this.getComponent(tag[i]);
			tabTemp.acceptText();
		}
	}

	/**
	 * 添加医嘱
	 */
	public void onAddRow() {
		// this.messageBox_("===========onAddRow=============");
		TTabbedPane tabPane = (TTabbedPane) this.getComponent("TABLEPANE");
		int selTabIndex = tabPane.getSelectedIndex();
		// System.out.println("============"+"TABLE" + (selTabIndex + 1));
		if (selTabIndex >= 3 && selTabIndex != 4 && selTabIndex != 5)
			return;
		TTable table = null;
		if (selTabIndex < 3 || selTabIndex == 5) {
			table = getTTable("TABLE" + (selTabIndex + 1));
			if (this.isStopBillFlg()) {
				this.messageBox("E0155");
				return;
			}
			// 有未编辑行时返回
			if (!this.isNewRow()) {
				// this.messageBox("1111");
				// 将原来行设置为初始化颜色
				table
				.setRowTextColor(table.getRowCount() - 1,
						this.normalColor);
				// this.messageBox("111");
				return;
			}
			odiObject.setAttribute("CHANGE_FLG", true);
			String rxKinds[] = new String[] { "ST", "UD", "DS", "IG", "", "PA" };// SD是出院带药
			odiObject.setAttribute("RX_KIND", rxKinds[selTabIndex]);
			int row = table.addRow();
			odiObject.getDS("ODI_ORDER").setItem(row, "CASE_NO",
					this.getCaseNo());
			String rxk = rxKinds[selTabIndex];
			if (opeFlg && "ST".equals(rxKinds[selTabIndex])) {
				rxk = "OP";
			}
			odiObject.getDS("ODI_ORDER").setItem(row, "RX_KIND", rxk);
			odiObject.getDS("ODI_ORDER").setItem(row, "ORDER_NO",
					"999999999999");
			odiObject.getDS("ODI_ORDER").setItem(row, "ORDER_SEQ", "20");
			table.getDataStore().setActive(row, false);
			// System.out.println("row增行"+row);
			// 设置新增行颜色
			// this.messageBox("2222");
			table.setRowTextColor(row, this.normalColor);
			// this.messageBox("222");
			rowOnly = row;
		} else {
			table = getTTable(GMTABLE);
			int rowCountGM = table.getDataStore().rowCount();
			boolean falgAdd = true;
			for (int i = 0; i < rowCountGM; i++) {
				if (!table.getDataStore().isActive(i,
						table.getDataStore().PRIMARY)) {
					falgAdd = false;
				}
			}
			if (falgAdd) {
				int rowSet = table.getDataStore().insertRow();
				Timestamp sysDate = SystemTool.getInstance().getDate();
				table.getDataStore().setItem(rowSet, "MR_NO", this.getMrNo());
				// ============xueyf modify 20120217 start
				sysDate = SystemTool.getInstance().getDate();
				table.getDataStore().setItem(rowSet, "ADM_DATE",
						StringTool.getString(sysDate, "yyyy/MM/dd"));
				// table.getDataStore().setItem(rowSet, "ADM_DATE",
				// StringTool.getString(sysDate, "yyyyMMdd"));
				// ============xueyf modify 20120217 stop
				table.getDataStore().setItem(rowSet, "DRUG_TYPE",
						this.getDrugType());
				table.getDataStore().setItem(rowSet, "DEPT_CODE",
						this.getDeptCode());
				table.getDataStore().setItem(rowSet, "DR_CODE",
						Operator.getID());
				table.getDataStore().setItem(rowSet, "ADM_TYPE", "I");
				table.getDataStore().setItem(rowSet, "CASE_NO",
						this.getCaseNo());
				table.getDataStore().setItem(rowSet, "OPT_USER",
						Operator.getID());
				table.getDataStore().setItem(rowSet, "OPT_DATE", sysDate);
				table.getDataStore().setItem(rowSet, "OPT_TERM",
						Operator.getIP());
				table.getDataStore().setActive(rowSet, false);
				table.setDSValue();
			}
			rowOnly = 0;
		}
		// System.out.println("ORDER表");
		// odiObject.getDS("ODI_ORDER").showDebug();
		// System.out.println("DSPNM表");
		// odiObject.getDS("ODI_DSPNM").showDebug();
		// System.out.println("DSPND表");
		// odiObject.getDS("ODI_DSPND").showDebug();
	}

	/**
	 * 插入中药饮片一行
	 * 
	 * @param tab
	 *            TTable
	 */
	public void insertChnOrder(TTable tab, String rxKind) {
		int row = odiObject.addChnRow(tab.getDataStore(), rxKind);
		tab.getDataStore().setActive(row, false);
		// 设置新增行颜色
		tab.setRowTextColor(row, this.normalColor);
		rowOnly = row;
	}

	// 过敏
	int gmSelect = 1;

	/**
	 * 过敏类别选择事件
	 */
	public void onSelectRadio(Object obj) {
		// 判断是否保存
		if (isSaveGM()) {
			// 请保存数据后在选择分类！
			this.messageBox("E0171");
			switch (gmSelect) {
			case 1:
				this.getTRadioButton("PHA_DRUGALLERGY").setSelected(true);
				break;
			case 2:
				this.getTRadioButton("CF_DRUGALLERGY").setSelected(true);
				break;
			case 3:
				this.getTRadioButton("OTHER_DRUGALLERGY").setSelected(true);
				break;
			}
			return;
		}
		onChangeStart();
		gmSelect = StringTool.getInt("" + obj);
	}

	/**
	 * 是否有保存的数据
	 * 
	 * @return boolean
	 */
	public boolean isSaveGM() {
		boolean falg = false;
		TTable table = this.getTTable(GMTABLE);
		String buff = table.getDataStore().PRIMARY;
		int newRow[] = table.getDataStore().getNewRows(buff);
		int countSaveRow = 0;
		for (int i : newRow) {
			if (!table.getDataStore().isActive(i, buff))
				continue;
			countSaveRow++;
		}
		int modifRow[] = table.getDataStore().getOnlyModifiedRows(buff);
		int delCount = table.getDataStore().getDeleteCount() < 0 ? 0 : table
				.getDataStore().getDeleteCount();
		if (countSaveRow + modifRow.length + delCount > 0)
			falg = true;
		return falg;
	}

	/**
	 * 是否有未编辑行
	 * 
	 * @return boolean
	 */
	public boolean isNewRow() {
		Boolean falg = false;
		TDS dsOrder = odiObject.getDS("ODI_ORDER");
		// System.out.println("=============MMMMMMM");
		// dsOrder.showDebug();
		TParm parmBuff = dsOrder.getBuffer(dsOrder.PRIMARY);
		// System.out.println("===========");
		// dsOrder.showDebug();
		// System.out.println("parmBuff"+parmBuff);
		int lastRow = parmBuff.getCount("#ACTIVE#");
		Object obj = parmBuff.getData("#ACTIVE#", lastRow - 1);
		if (obj != null) {
			falg = (Boolean) parmBuff.getData("#ACTIVE#", lastRow - 1);
			// 赋值当前编辑行
			if (!falg)
				this.rowOnly = lastRow - 1;
		} else {
			falg = true;
		}
		// System.out.println("===================================>"+falg);
		return falg;
	}

	/**
	 * 删除医嘱
	 */
	public void onDelRow() {
		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		TTabbedPane tabPane = (TTabbedPane) this.getComponent("TABLEPANE");
		int selTabIndex = tabPane.getSelectedIndex();
		TTable tab = null;
		switch (selTabIndex) {
		// 临时医嘱
		case 0:
			tab = this.getTTable(TABLE1);
			// DC医嘱
			dcOrder(tab, 0);
			break;
			// 长期医嘱
		case 1:
			tab = this.getTTable(TABLE2);
			// DC医嘱
			dcOrder(tab, 1);
			break;
			// 出院带药
		case 2:
			tab = this.getTTable(TABLE3);
			// DC医嘱
			dcOrder(tab, 2);
			break;
			// 中药饮片
		case 3:
			tab = this.getTTable(TABLE4);
			// DC医嘱
			dcOrder(tab, 3);
			break;
			// 过敏记录
		case 4:
			tab = this.getTTable(GMTABLE);
			// DC过敏记录
			dcGMOrder(tab);
			break;
			// 院前用药
		case 5:
			// 删除院前用药
			tab = this.getTTable(TABLE6);
			// 强制失去编辑焦点
			if (tab.getTable().isEditing()) {
				tab.getTable().getCellEditor().stopCellEditing();
			}

			if (tab.getSelectedRow() < 0) {
				this.messageBox("E0173");
				return;
			}

			int seletedRow = tab.getSelectedRow();
			tab.removeRow(seletedRow);
			tab.setDSValue();
			break;
		}
		// this.onChange();
		// 过滤TABLE
		if ("N".equals(this.clearFlg))
			this.onChange();
		if ("Y".equals(this.clearFlg))
			this.delRowNull();
		if ("Q".equals(this.clearFlg))
			this.onChange(); // shibl 20130118 modify 临时修改 删除临时医嘱
		// System.out.println("删除后");
		// tab.getDataStore().showDebug();
	}

	/**
	 * 删除处方号
	 * 
	 * @param tab
	 *            TTable
	 */
	public void delRxNoAll(TTable tab) {
		if (tab.getTag().equals(TABLE4)) {
			TDS ds = odiObject.getDS("ODI_ORDER");
			String rxNo = this.getTComboBox("IG_RX_NO").getSelectedID();
			ds.setFilter("");
			ds.filter();
			int rowCountRx = ds.rowCount();
			boolean ncCheckFlg = false;
			for (int i = 0; i < rowCountRx; i++) {
				if (ds.getItemString(i, "NS_CHECK_CODE").length() != 0
						&& rxNo.equals(ds.getItemData(i, "RX_NO").toString())) {
					ncCheckFlg = true;
					break;
				}
			}
			if (ncCheckFlg) {
				// 此处方已经有审核的医嘱不可以DC！
				this.messageBox("E0172");
				return;
			}
			for (int i = rowCountRx - 1; i >= 0; i--) {
				if (!ds.isActive(i))
					continue;
				if (ds.getItemData(i, "RX_NO").toString().equals(rxNo)) {
					int delId = (Integer) ds.getItemData(i, "#ID#");
					odiObject.getDS("ODI_ORDER").setAttribute("DELROW", delId);
					odiObject.deleteRow(ds, i);
				}
			}
			if ("N".equals(this.clearFlg))
				this.onChange();
			if ("Y".equals(this.clearFlg))
				this.delRowNull();
			if ("Q".equals(this.clearFlg))
				this.onQuery();
			// this.getTComboBox("IG_RX_NO").setSelectedID("");
		} else {
			String rxNo = this.getTComboBox("RX_NO").getSelectedID();
			tab.getDataStore().setFilter("");
			tab.getDataStore().filter();
			int rowCountRx = tab.getDataStore().rowCount();
			boolean ncCheckFlg = false;
			for (int i = 0; i < rowCountRx; i++) {
				if (tab.getDataStore().getItemString(i, "NS_CHECK_CODE")
						.length() != 0
						&& rxNo.equals(tab.getDataStore().getItemData(i,
								"RX_NO").toString())) {
					ncCheckFlg = true;
					break;
				}
			}
			if (ncCheckFlg) {
				this.messageBox("E0172");
				return;
			}
			for (int i = rowCountRx - 1; i >= 0; i--) {
				if (!tab.getDataStore().isActive(i))
					continue;
				if (tab.getDataStore().getItemData(i, "RX_NO").toString()
						.equals(rxNo)) {
					int delId = (Integer) tab.getDataStore().getItemData(i,
							"#ID#");
					odiObject.getDS("ODI_ORDER").setAttribute("DELROW", delId);
					TDS ds = odiObject.getDS("ODI_ORDER");
					odiObject.deleteRow(ds, i);
					// tab.getDataStore().deleteRow(i);
				}
			}
			if ("N".equals(this.clearFlg))
				this.onChange();
			if ("Y".equals(this.clearFlg))
				this.delRowNull();
			if ("Q".equals(this.clearFlg))
				this.onQuery();
			// this.getTComboBox("RX_NO").setSelectedID("");
		}

	}

	/**
	 * 删除过敏记录
	 * 
	 * @param tab
	 *            TTable
	 */
	public void dcGMOrder(TTable tab) {
		int selRow = tab.getSelectedRow();
		if (selRow < 0)
			return;
		if (!tab.getDataStore().isActive(selRow))
			return;
		tab.getDataStore().deleteRow(selRow);
		tab.setDSValue();
	}

	/**
	 * 返回主分区删除行位置
	 * 
	 * @param rowId
	 *            int
	 * @param tabStore
	 *            TDataStore
	 * @return int
	 */
	public int getDelRowSet(int rowId, TDS ds) {
		int row = -1;
		int rowCount = ds.rowCount();
		for (int i = 0; i < rowCount; i++) {
			if ((Integer) ds.getItemData(i, "#ID#") == rowId) {
				row = i;
				break;
			}
		}
		return row;
	}

	/**
	 * DC临时医嘱
	 * 
	 * @param tab
	 *            TTable
	 */
	public void dcOrder(TTable tab, int type) {
		// this.messageBox("==type=="+type);
		// this.messageBox("==dcOrder==");
		// System.out.println("分区:" + tab.getDataStore().isFilter() + "删除前");
		// tab.getDataStore().showDebug();
		// 是否经护士审核
		boolean nsCheckFlg = odiObject.getAttributeBoolean("INW_NS_CHECK_FLG");
		Timestamp sysDate = SystemTool.getInstance().getDate();
		// this.messageBox_(tab.getTag());
		int rowOnlyS = tab.getSelectedRow();
		int id = (Integer) tab.getDataStore().getItemData(rowOnlyS, "#ID#");
		TParm orderParm = tab.getDataStore().getRowParm(rowOnlyS);
		// System.out.println("删除选中医嘱orderParm is :: "+orderParm);
		tab.getDataStore().setFilter("");
		tab.getDataStore().filter();
		rowOnlyS = getDelRowSet(id, odiObject.getDS("ODI_ORDER"));

		// this.messageBox("===rowOnlyS===="+rowOnlyS);

		if (rowOnlyS < 0) {
			return;
		}
		// 判断是否是新医嘱
		if (this.isNewOrder(rowOnlyS, tab.getDataStore().PRIMARY)) {
			// this.messageBox("新1111111111111111111"+"护士审核标记：："+OrderUtil.getInstance()
			// .checkOrderNSCheck(orderParm, nsCheckFlg));

			if (OrderUtil.getInstance()
					.checkOrderNSCheck(orderParm, nsCheckFlg)) {
				this.messageBox("医嘱已经审核不可以删除");
				return;
			}
			// 是否是集合医嘱
			if (this.isOrderSet(orderParm)) {
				// this.messageBox_("删除集合医嘱细项");
				// 删除集合医嘱细项
				this.delOrderSetList(orderParm, "NEW");
				return;
			}
			// 是否是连结医嘱
			if (this.isLinkOrder(orderParm)
					&& orderParm.getBoolean("LINKMAIN_FLG")) {
				// this.messageBox_("删除连结医嘱");
				// 删除连结医嘱
				this.delLinkOrder(orderParm, "NEW");
				return;
			}
			// 删除主页面显示的
			// this.messageBox_("删除行:"+rowOnlyS);
			int delId = (Integer) tab.getDataStore().getItemData(rowOnlyS,
					"#ID#");
			odiObject.getDS("ODI_ORDER").setAttribute("DELROW", delId);
			tab.getDataStore().deleteRow(rowOnlyS);
		} else {
			// this.messageBox("旧22222222222222222222222"+"护士审核标记：："+OrderUtil.getInstance()
			// .checkOrderNSCheck(orderParm, nsCheckFlg));
			// this.messageBox_("更新DC");
			// //20131029 yanjing 添加长期医嘱不可删除抗菌药物
			// ============yanjing 注
			// if(type == 1){
			// String sql =
			// "SELECT ANTIBIOTIC_CODE FROM PHA_BASE WHERE ORDER_CODE = '"+orderParm.getValue("ORDER_CODE")+"'";
			// TParm antiParm = new TParm(TJDODBTool.getInstance().select(sql));
			// if(!antiParm.getValue("ANTIBIOTIC_CODE",0).equals(null)&&!"".equals(antiParm.getValue("ANTIBIOTIC_CODE",0))){
			// this.messageBox("抗菌药物，不可删除。");
			// return;
			// }
			// }
			// ============yanjing 注
			if (tab.getSelectedRow() < 0) {
				// 请选中需要DC的医嘱！
				this.messageBox("E0173");
				return;
			}
			if (OrderUtil.getInstance()
					.checkOrderNSCheck(orderParm, nsCheckFlg)
					&& !orderParm.getValue("RX_KIND").equals("UD")) {// shibl
				// 20130205
				// modify

				this.messageBox("医嘱已经审核或执行不可以删除");
				return;
			} else {
				// this.messageBox("33333333333333333333333");
				// 长期医嘱不删除只修改
				if (type == 1) {
					// $$====Modifed by lx 2012/04/19/护士未审核Start===$$//
					// System.out.println("NS_CHECK_DATE护士是否审核====="+orderParm.getValue("NS_CHECK_DATE"));
					// 未审核情况
					if (orderParm.getValue("NS_CHECK_DATE").equals("")
							&& orderParm.getValue("NS_CHECK_DATE").length() == 0) {
						// 长期未审核医嘱，确实停用吗？
						if (this.messageBox("询问", "医嘱护士未审核,确实删除吗？", 2) == 0) {
							if (this.isOrderSet(orderParm)) {
								// this.messageBox_("删除集合医嘱细项");
								// 删除集合医嘱细项
								this.delOrderSetList(orderParm, "DELETE");
								return;
							}
							// 是否是连结医嘱
							if (this.isLinkOrder(orderParm)
									&& orderParm.getBoolean("LINKMAIN_FLG")) {
								// this.messageBox_("删除连结医嘱");
								// 删除连结医嘱
								this.delLinkOrder(orderParm, "DELETE");
								return;
							}
							int delId = (Integer) tab.getDataStore()
									.getItemData(rowOnlyS, "#ID#");

							// this.messageBox("==delId=="+delId);
							odiObject.getDS("ODI_ORDER").setAttribute("DELROW",
									delId);
							TDS ds = odiObject.getDS("ODI_ORDER");
							// System.out.println("删除的UT===" +
							// ds.getRowParm(rowOnlyS));
							odiObject.deleteRow(ds, rowOnlyS);

						} else {
							return;
						}

						// 已审核情况
					} else {
						// this.messageBox("44444444444444444444444444");
						// DC医嘱
						if (this.isOrderSet(orderParm)) {
							// this.messageBox("555555555555555555555");
							// DC集合医嘱细项
							this.delOrderSetList(orderParm, "MODIF");
							return;
						}
						// 是否是连结医嘱
						if (this.isLinkOrder(orderParm)
								&& orderParm.getBoolean("LINKMAIN_FLG")) {
							// this.messageBox("66666666666666666666666666");
							// DC连结医嘱
							this.delLinkOrder(orderParm, "MODIF");
							return;
						}
						TParm dcParm = new TParm();
						dcParm.setData("DC_DATE", sysDate);
						dcParm.setData("DC_DEPT_CODE", this.getDeptCode());
						dcParm.setData("DC_DR_CODE", Operator.getID());
						dcParm
						.setData("DC_STATION_CODE", this
								.getStationCode());
						dcParm.setData("OPT_DATE", sysDate);
						dcParm.setData("OPT_USER", Operator.getID());
						dcParm.setData("OPT_TERM", Operator.getIP());
						TDS ds = odiObject.getDS("ODI_ORDER");
						odiObject.setItem(ds, rowOnlyS, dcParm);
					}
					// $$====Modifed by lx 2012/04/19/护士未审核End===$$//

				} else {

					// this.messageBox_(0);
					// this.messageBox_(Operator.getID()+"=="+orderParm.getValue("ORDER_DR_CODE"));
					// 判断
					if (Operator.getID().equals(
							orderParm.getValue("ORDER_DR_CODE"))) {

					} else {
						TParm parm = new TParm(this.getDBTool().select(
								"SELECT * FROM ADM_INP WHERE CASE_NO='"
										+ this.getCaseNo() + "'"));
						// this.messageBox_(parm);
						// $$ ======Modified by lx2012/04/11 start==========$$//
						if ((!Operator.getID().equals(
								parm.getValue("VS_DR_CODE", 0)))
								&& (!Operator.getID().equals(
										parm.getValue("ATTEND_DR_CODE", 0)) && (!Operator
												.getID().equals(
														parm.getValue(
																"DIRECTOR_DR_CODE", 0))))
								&& (!isDutyDr())) {
							this.messageBox("无权限删除！");
							return;
						}
						// $$ ======Modified by lx2012/04/11 end==========$$//
					}

					// 临时出院带药中药饮片执行删除动作
					// 是否是集合医嘱
					if (this.isOrderSet(orderParm)) {
						// this.messageBox_("删除集合医嘱细项");
						// 删除集合医嘱细项
						this.delOrderSetList(orderParm, "DELETE");
						return;
					}
					// 是否是连结医嘱
					if (this.isLinkOrder(orderParm)
							&& orderParm.getBoolean("LINKMAIN_FLG")) {
						// this.messageBox_("删除连结医嘱");
						// 删除连结医嘱
						this.delLinkOrder(orderParm, "DELETE");
						return;
					}
					// 删除主页面显示的
					// this.messageBox_("删除行:"+rowOnlyS);
					int delId = (Integer) tab.getDataStore().getItemData(
							rowOnlyS, "#ID#");

					// this.messageBox("==delId=="+delId);
					odiObject.getDS("ODI_ORDER").setAttribute("DELROW", delId);
					TDS ds = odiObject.getDS("ODI_ORDER");
					// System.out.println("删除的YZ===" + ds.getRowParm(rowOnlyS));
					odiObject.deleteRow(ds, rowOnlyS);
					// if(((TParm)this.getParameter()).getData("ODI","OPERATOR")!=null
					// &&
					// ((TParm)this.getParameter()).getData("ODI","OPERATOR").equals("OPE_N")){
					// 删除介入护理平台数据删除
					/*
					 * TParm intenParm = new
					 * TParm(TJDODBTool.getInstance().select
					 * ("DELETE FROM OPE_INTERVENNURPLAT WHERE" +
					 * "  CASE_NO ='"+
					 * this.getCaseNo()+"'  AND ORDER_CODE =  '"+ds
					 * .getRowParm(rowOnlyS).getValue("ORDER_CODE")+"' " ));
					 */
					// 删除介入护理平台数据删除
					// }
					// tab.getDataStore().deleteRow(rowOnlyS);
				}
			}
		}
	}

	/**
	 * 判断是否是新医嘱
	 * 
	 * @return boolean
	 */
	public boolean isNewOrder(int row, String buff) {
		boolean falg = false;
		TDS ds = odiObject.getDS("ODI_ORDER");
		int newRow[] = ds.getNewRows(buff);
		for (int i : newRow) {
			if (row == i) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * 是否是修改的医嘱
	 * 
	 * @param row
	 *            int
	 * @param buff
	 *            String
	 * @return boolean
	 */
	public boolean isModifOrder(int row, String buff) {
		boolean falg = false;
		TDS ds = odiObject.getDS("ODI_ORDER");
		int modifRow[] = ds.getOnlyModifiedRows(buff);
		for (int i : modifRow) {
			if (row == i) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * 是否是集合医嘱
	 * 
	 * @param row
	 *            int
	 * @param buff
	 *            String
	 * @return boolean
	 */
	public boolean isOrderSet(TParm orderParm) {
		boolean falg = false;
		if (orderParm.getBoolean("SETMAIN_FLG")) {
			falg = true;
		}
		return falg;
	}

	/**
	 * 是否是连结医嘱
	 * 
	 * @param linkOrder
	 *            TParm
	 * @return boolean
	 */
	public boolean isLinkOrder(TParm linkOrder) {
		boolean falg = false;
		if (linkOrder.getInt("LINK_NO") > 0) {
			falg = true;
		}
		return falg;
	}

	/**
	 * 删除集合医嘱细项
	 * 
	 * @param orderParm
	 *            TParm
	 */
	public void delOrderSetList(TParm orderParm, String type) {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		int orderSetGroupNo = orderParm.getInt("ORDERSET_GROUP_NO");
		// ================针对组号重复增加删除条件========shibl
		// 20140710===============================
		String orderNo = orderParm.getValue("ORDER_NO");
		String orderSetCode = orderParm.getValue("ORDERSET_CODE");
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		// 新增
		if ("NEW".equals(type)) {
			int newRow[] = ds.getNewRows(buff);
			List arrayListSet = new ArrayList();
			Map setIdMap = new HashMap();
			for (int i : newRow) {
				TParm temp = ds.getRowParm(i, buff);
				if (!ds.isActive(i, buff))
					continue;
				// if(temp.getBoolean("SETMAIN_FLG"))
				// continue;
				// if(temp.getInt("ORDERSET_GROUP_NO")==orderSetGroupNo)
				// ds.setActive(i,false,buff);
				// 判断细项位置并记录在列表中
				if (temp.getInt("ORDERSET_GROUP_NO") == orderSetGroupNo
						&& temp.getValue("ORDER_NO").equals(orderNo)
						&& temp.getValue("ORDERSET_CODE").equals(orderSetCode)) {
					int delRowSetId = (Integer) ds.getItemData(i, "#ID#", buff);
					arrayListSet.add(delRowSetId);
					setIdMap.put(delRowSetId, i);
				}
			}
			// 删除细项
			for (int i = arrayListSet.size() - 1; i >= 0; i--) {
				ds.setAttribute("DELROW", arrayListSet.get(i));
				odiObject.deleteRow(ds, (Integer) setIdMap.get(arrayListSet
						.get(i)));
			}
		}
		// 修改
		if ("MODIF".equals(type)) {
			int rowCount = ds.rowCount();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = ds.getRowParm(i, buff);
				if (!ds.isActive(i, buff))
					continue;
				// if(temp.getBoolean("SETMAIN_FLG"))
				// continue;
				if (temp.getInt("ORDERSET_GROUP_NO") == orderSetGroupNo
						&& temp.getValue("ORDER_NO").equals(orderNo)
						&& temp.getValue("ORDERSET_CODE").equals(orderSetCode)) {
					TParm dcParm = new TParm();
					dcParm.setData("DC_DATE", sysDate);
					dcParm.setData("DC_DEPT_CODE", this.getDeptCode());
					dcParm.setData("DC_DR_CODE", Operator.getID());
					dcParm.setData("DC_STATION_CODE", this.getStationCode());
					dcParm.setData("OPT_DATE", sysDate);
					dcParm.setData("OPT_USER", Operator.getID());
					dcParm.setData("OPT_TERM", Operator.getIP());
					// System.out.println("行号:"+i);
					// DC医嘱
					odiObject.setItem(ds, i, dcParm, buff);
				}
			}
		}
		// 删除
		if ("DELETE".equals(type)) {
			int rowCount = ds.rowCount();
			List arrayListSet = new ArrayList();
			Map setIdMap = new HashMap();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = ds.getRowParm(i, buff);
				if (!ds.isActive(i, buff))
					continue;
				// if(temp.getBoolean("SETMAIN_FLG"))
				// continue;
				if (temp.getInt("ORDERSET_GROUP_NO") == orderSetGroupNo
						&& temp.getValue("ORDER_NO").equals(orderNo)
						&& temp.getValue("ORDERSET_CODE").equals(orderSetCode)) {
					int delRowSetId = (Integer) ds.getItemData(i, "#ID#", buff);
					arrayListSet.add(delRowSetId);
					setIdMap.put(delRowSetId, i);
				}
			}
			// 删除细项
			for (int i = arrayListSet.size() - 1; i >= 0; i--) {
				ds.setAttribute("DELROW", arrayListSet.get(i));
				odiObject.deleteRow(ds, (Integer) setIdMap.get(arrayListSet
						.get(i)));
			}
		}

	}

	/**
	 * 删除连结医嘱
	 * 
	 * @param orderParm
	 *            TParm
	 * @param type
	 *            String
	 */
	public void delLinkOrder(TParm orderParm, String type) {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		int linkNo = orderParm.getInt("LINK_NO");
		String rxKind = orderParm.getValue("RX_KIND");
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.PRIMARY;
		// 新增
		if ("NEW".equals(type)) {
			int newRow[] = ds.getNewRows(buff);
			List rowDelAttribute = new ArrayList();
			Map rowDels = new HashMap();
			for (int i : newRow) {
				TParm temp = ds.getRowParm(i, buff);
				if (!ds.isActive(i, buff))
					continue;
				// System.out.println("i:"+i);
				if (temp.getInt("LINK_NO") == linkNo
						&& temp.getValue("RX_KIND").equals(rxKind)) {
					int delId = (Integer) ds.getItemData(i, "#ID#", buff);
					rowDelAttribute.add(delId);
					rowDels.put(delId, i);
				}
			}
			for (int i = rowDelAttribute.size() - 1; i >= 0; i--) {
				ds.setAttribute("DELROW", rowDelAttribute.get(i));
				odiObject.deleteRow(ds, (Integer) rowDels.get(rowDelAttribute
						.get(i)));
			}
		}
		// 修改
		if ("MODIF".equals(type)) {
			int rowCount = ds.rowCount();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = ds.getRowParm(i, buff);
				if (temp.getInt("LINK_NO") == linkNo
						&& temp.getValue("RX_KIND").equals(rxKind)) {
					TParm dcParm = new TParm();
					dcParm.setData("DC_DATE", sysDate);
					dcParm.setData("DC_DEPT_CODE", Operator.getDept());
					dcParm.setData("DC_DR_CODE", Operator.getID());
					dcParm.setData("DC_STATION_CODE", Operator.getStation());
					dcParm.setData("OPT_DATE", sysDate);
					dcParm.setData("OPT_USER", Operator.getID());
					dcParm.setData("OPT_TERM", Operator.getIP());
					// DC医嘱
					odiObject.setItem(ds, i, dcParm, buff);
				}
			}
		}
		// 删除
		if ("DELETE".equals(type)) {
			int rowCount = ds.rowCount();
			List rowDelAttribute = new ArrayList();
			Map rowDels = new HashMap();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = ds.getRowParm(i, buff);
				if (temp.getInt("LINK_NO") == linkNo
						&& temp.getValue("RX_KIND").equals(rxKind)) {
					int delId = (Integer) ds.getItemData(i, "#ID#", buff);
					rowDelAttribute.add(delId);
					rowDels.put(delId, i);
				}
			}
			for (int i = rowDelAttribute.size() - 1; i >= 0; i--) {
				ds.setAttribute("DELROW", rowDelAttribute.get(i));
				odiObject.deleteRow(ds, (Integer) rowDels.get(rowDelAttribute
						.get(i)));
			}
		}

	}

	/**
	 * 关闭事件
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		// 状态条显示
		callFunction("UI|setSysStatus", "");
		if (!checkNewModifRowCount() && !isSaveGM()) {
			// 解锁
			if (!PatTool.getInstance().unLockPat(this.getMrNo())) {
				this.messageBox("E0170");
			}
			return true;
		} else {
			switch (messageBox("提示信息 Tips", "是否保存? \n Save it",
					this.YES_NO_CANCEL_OPTION)) {
					case 0:
						if (!onSave())
							return false;
						break;
					case 1:
						break;
					case 2:
						return false;
			}
		}
		// 解锁
		if (!PatTool.getInstance().unLockPat(this.getMrNo())) {
			// 解锁失败！
			this.messageBox("E0170");
		}
		return true;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		// 公用参数
		TTable table = null;
		TDS ds = odiObject.getDS("ODI_ORDER");
		switch (tab.getSelectedIndex()) {
		case 0:
			// 临时
			table = getTTable(TABLE1);
			// 所有数据放入主分区
			table.setFilter("");
			table.filter();
			// 删除新行
			delNewRow(table, ds, "ST");
			table.setFilter("RX_KIND='ST' AND #ACTIVE#='N'");
			break;
		case 1:
			// 长期
			table = getTTable(TABLE2);
			// 所有数据放入主分区
			table.setFilter("");
			table.filter();
			// 删除新行
			delNewRow(table, ds, "UD");
			table.setFilter("RX_KIND='UD' AND #ACTIVE#='N'");
			break;
		case 2:
			// 出院带药
			table = getTTable(TABLE3);
			// 所有数据放入主分区
			table.setFilter("");
			table.filter();
			// 删除新行
			delNewRow(table, ds, "DS");
			table.setFilter("RX_KIND='DS' AND #ACTIVE#='N'");
			break;
		case 3:
			// 中药饮片
			table = getTTable(TABLE4);
			ds.setFilter("");
			ds.filter();
			delNewRow(table, ds, "IG");
			ds.setFilter("RX_KIND='IG' AND #ACTIVE#='N'");
			odiObject.filter(table, ds, this.isStopBillFlg());
			break;
		case 4:
			// 过敏记录
			table = getTTable(GMTABLE);
			// 所有数据放入主分区
			table.setFilter("");
			table.filter();
			// 删除新行
			delNewRow(table, ds, "GM");
			table.setFilter("#ACTIVE#='N'");
			break;
		}
		// 不为中药饮片时执行
		if (tab.getSelectedIndex() != 3) {
			table.filter();
			// table.sort();
			table.setDSValue();
		}
		// 锁定行
		lockRowOrder();
		// 清空状态
		this.clearFlg = "Y";
		phaApproveFlg = "";
	}

	/**
	 * 删除新行
	 * 
	 * @param tab
	 *            TTable
	 */
	public void delNewRow(TTable tab, TDS ds, String tag) {
		if (!"IG".equals(tag)) {
			String buff = tab.getDataStore().isFilter() ? tab.getDataStore().FILTER
					: tab.getDataStore().PRIMARY;
			int newRow[] = tab.getDataStore().getNewRows(buff);
			for (int row : newRow) {
				if (!tab.getDataStore().isActive(row, buff))
					continue;
				TParm orderParm = ds.getRowParm(row, buff);
				// System.out.println("=============="+orderParm);
				tab.getDataStore().deleteRow(row, buff);
				// 是否是集合医嘱
				if (this.isOrderSet(orderParm)) {// shibl modify 20130515
					// 删除集合医嘱细项
					this.delOrderSetList(orderParm, "NEW");
					return;
				}
			}
			tab.setDSValue();
		} else {
			int newRowIG[] = ds.getNewRows();
			for (int row : newRowIG) {
				if (!ds.isActive(row))
					continue;
				ds.deleteRow(row);
			}
		}
	}

	/**
	 * 引用表单
	 */
	public void onInputList() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		switch (tab.getSelectedIndex()) {
		case 0:
			// 临时
			yyList = true;
			// Object obj =
			// this.openDialog("%ROOT%\\config\\sys\\SysExaSheetTree.x",this,true);
			TWindow window = (TWindow) this.openWindow(
					"%ROOT%\\config\\sys\\SysExaSheetTree.x", this, true);
			window.setVisible(true);
			// window.setX(ImageTool.getScreenWidth() - window.getWidth());
			// window.setY(0);
			// window.setVisible(true);
			// if(obj.toString().equals("Y")){
			// yyList = false;
			// }
			break;
		case 1:
			// 长期医嘱不可开立此医嘱！
			this.messageBox("E0175");
			break;
		case 2:
			// 出院带药不可开立此医嘱！
			this.messageBox("E0175");
			break;
		case 3:
			// 中药饮片不可开立此医嘱！
			this.messageBox("E0175");
			break;
		}
	}

	/**
	 * 引用表单返回数据
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setYYlist(Object obj) {
		if (!(obj instanceof TParm)) {
			return;
		}
		TParm action = (TParm) obj;
		if ("Y".equals(action.getValue("YYLIST"))) {
			yyList = false;
		}
	}

	/**
	 * 引用表单回调方法
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onQuoteSheet(Object obj) {
		boolean flag = false;
		// 判断是否是TPARM类型
		if (!(obj instanceof TParm)) {
			return false;
		}
		TParm action = (TParm) obj;
		action.setData("EXID_ROW", getExitRow());
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		switch (tab.getSelectedIndex()) {
		case 0:
			// 临时
			this.popReturn("ST", action);
			flag = true;
			break;
		}
		return flag;
	}

	/**
	 * 医师套餐
	 */
	public void onDrPack() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() > 3) {
			// 不可开立此医嘱
			this.messageBox("E0175");
			return;
		}
		this.yyList = true;
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "ODI");
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("USER_ID", Operator.getID());
		parm.setData("DEPT_OR_DR", 2);
		parm.setData("RULE_TYPE", tab.getSelectedIndex());
		parm.addListener("INSERT_TABLE", this, "onQuoteSheetList");
		parm.addListener("INSERT_TABLE_FLG", this, "setYYlist");
		// this.openDialog("%ROOT%\\config\\odi\\ODIPACKOrderUI.x",parm);
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\odi\\ODIPACKOrderUI.x", parm, true);
		window.setVisible(true);
	}

	/**
	 * 感染医嘱套餐
	 */
	public void onInfecPack() {
		TParm icdparm = new TParm();
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() > 2) {
			// 不可开立此医嘱
			this.yyList = false;
			this.messageBox("不能引用抗菌药品");
			return;
		}
		this.yyList = true;
		TParm parm = new TParm();
		parm.setData("TAB_FLG", tab);// ===yanjing 20130909 页签标记
		parm.setData("MR_NO", this.getMrNo());// add caoyong 2013908
		parm.setData("CASE_NO", this.getCaseNo());// add caoyong 2013908
		parm.setData("ADM_DATE", this.getAdmDate());// add caoyong 2013908
		parm.setData("IPD_NO", this.getIpdNo());// add caoyong 2013908
		parm.setData("PAT_NAME", this.getPatName());// add caoyong 2013908
		parm.setData("USER_ID", Operator.getID());
		parm.setData("RX_KIND", getRxKindString(tab.getSelectedIndex()));
		// parm.addListener("INSERT_TABLE", this, "onQuoteSheetList");
		// parm.addListener("INSERT_TABLE_FLG", this, "setYYlist");
		TParm result = null;
		if (tab.getSelectedIndex() == 0) {
			parm.setData("TYPE", "ST");// 临时页签
			String sql = "SELECT A.OP_CODE1 AS DIAG_CODE1,B.OPT_CHN_DESC FROM OPE_OPBOOK A, SYS_OPERATIONICD B WHERE A.OP_CODE1=B.OPERATION_ICD  AND CASE_NO='"
					+ caseNo
					+ "' AND PHA_PREVENCODE IS NOT NULL AND (CANCEL_FLG IS NULL OR CANCEL_FLG ='N' ) ";
			// "AND (A.STATE='0' OR A.STATE='1')";
			icdparm = new TParm(this.getDBTool().select(sql));
			sql = "SELECT A.ICD_CODE AS ICD_CODE1,A.ICD_TYPE,B.ICD_CHN_DESC AS ICD_CODE,A.CASE_NO FROM ADM_INPDIAG A,SYS_DIAGNOSIS B WHERE CASE_NO='"
					+ this.caseNo
					+ "' AND IO_TYPE='Z' AND A.ICD_CODE = B.ICD_CODE AND A.ICD_TYPE = B.ICD_TYPE";
			TParm icdparm1 = new TParm(this.getDBTool().select(sql));
			if (icdparm.getCount() <= 0 && icdparm1.getCount() <= 0) {
				this.yyList = false;
				this.messageBox("没有检测到抗菌药品手术或拟诊诊断,不能引用");
				return;
			}
			// =====pangben 2013-12-30诊断添加
			parm.setData("ICDPARM", icdparm1.getData());
			parm.setData("PACK_CODE", icdparm1.getValue("ICD_CODE1", 0));
			if (icdparm1.getCount() <= 0) {
				parm.setData("DESC1", "");
			} else {
				parm.setData("DESC1", icdparm1.getValue("ICD_CODE1", 0)
						+ icdparm1.getValue("ICD_CODE", 0));
			}
			parm.setData("PACK_CODE", icdparm.getValue("DIAG_CODE1", 0));
			parm.setData("DESC", icdparm.getValue("DIAG_CODE1", 0)
					+ icdparm.getValue("OPT_CHN_DESC", 0));
			result = (TParm) this.openDialog(
					"%ROOT%\\config\\odi\\ODIINFECPACKSheetST.x", parm, true);
		} else if (tab.getSelectedIndex() == 1 || tab.getSelectedIndex() == 2) {
			parm.setData("TYPE", "UDD");// 长期页签
			// 查询手术类型
			String sql1 = "SELECT A.OP_CODE1 AS DIAG_CODE1,B.OPT_CHN_DESC FROM OPE_OPBOOK A, SYS_OPERATIONICD B WHERE A.OP_CODE1=B.OPERATION_ICD  AND CASE_NO='"
					+ caseNo
					+ "' AND PHA_PREVENCODE IS NOT NULL AND (CANCEL_FLG IS NULL OR CANCEL_FLG ='N' )";
			// "AND (A.STATE='0' OR A.STATE='1')";yanjing 20131104 注
			TParm icdparm1 = new TParm(this.getDBTool().select(sql1));
			String sql = "SELECT A.ICD_CODE AS ICD_CODE1,A.ICD_TYPE,B.ICD_CHN_DESC AS ICD_CODE,A.CASE_NO FROM ADM_INPDIAG A,SYS_DIAGNOSIS B WHERE CASE_NO='"
					+ this.caseNo
					+ "' AND IO_TYPE='Z' AND A.ICD_CODE = B.ICD_CODE AND A.ICD_TYPE = B.ICD_TYPE";
			icdparm = new TParm(this.getDBTool().select(sql));
			if (icdparm.getCount() <= 0 && icdparm1.getCount() <= 0) {
				this.messageBox("没有检测到拟诊诊断或手术申请,不能引用");
				// 不可开立此医嘱
				this.yyList = false;
				return;
			}
			parm.setData("ICDPARM", icdparm.getData());
			parm.setData("PACK_CODE", icdparm.getValue("ICD_CODE1", 0));
			if (icdparm.getCount() <= 0) {
				parm.setData("DESC", "");
			} else {
				parm.setData("DESC", icdparm.getValue("ICD_CODE1", 0)
						+ icdparm.getValue("ICD_CODE", 0));
			}
			// 手术代码及名称
			parm.setData("PACK_CODE1", icdparm1.getValue("DIAG_CODE1", 0));
			parm.setData("DESC1", icdparm1.getValue("DIAG_CODE1", 0)
					+ icdparm1.getValue("OPT_CHN_DESC", 0));
			result = (TParm) this.openDialog(
					"%ROOT%\\config\\odi\\ODIINFECPACKSheetUDD.x", parm, true);
		} else {
			this.yyList = false;
			this.messageBox("页签选择不正确");
			return;
		}
		// System.out.println("术后预防传回结果result is：：："+result);
		if (null != result && result.getCount() > 0) {
			// 拿到使用者的证照列表
			TParm lcsParm = new TParm(
					this
					.getDBTool()
					.select(
							"SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
									+ Operator.getID()
									+ "' AND SYSDATE BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
			for (int i = 0; i < result.getCount(); i++) {
				Object obj = result.getRow(i);// =====pangben 2013-11-5
				String lcsClassCode = ((TParm) obj).getValue("LCS_CLASS_CODE");// 特殊药品添加医生证照号码校验
				if (tab.getSelectedIndex() == 1
						&& result.getValue("RADIO_FLG", i).equals("WRDO")
						&& result.getInt("PHA_DS_DAY", i) == 1) {
					if (!OrderUtil.getInstance().checkLcsClassCode(lcsParm,
							Operator.getID(), "" + lcsClassCode)) {
						lcsParm.setErrCode(-1);
						// 您没有此医嘱的证照！
						lcsParm.setErrText("E0166");
						this.messageBox("E0166");
						return;
					}
					this.messageBox("含有特殊类抗菌素，请填写会诊申请单");
					String flg = "ODI";
					TParm newparm = new TParm();
					newparm.setData("INW", "CASE_NO", caseNo);
					newparm.setData("INW", "PAT_NAME", patName);
					newparm.setData("INW", "ADM_DATE", admDate);
					newparm.setData("INW", "MR_NO", mrNo);
					newparm.setData("INW", "ODI_FLG", flg);
					newparm.setData("INW", "IPD_NO", ipdNo);
					newparm = (TParm) openDialog(
							"%ROOT%\\config\\inp\\INPConsApplication.x",
							newparm);
					return;
				}
				onQuoteSheetInsert(obj);
			}
			phaApproveFlg = result.getValue("PHA_APPROVE_FLG");// 抗菌药品操作开立是否填写会诊申请单==pangben
			// 2013-9-10
			if (phaApproveFlg.equals("Y") && result.getCount() > 0) {// 会诊建议传回的医嘱需要修改状态
				result.setData("OPT_USER", Operator.getID());
				result.setData("OPT_TERM", Operator.getIP());
				TParm result1 = TIOM_AppServer.executeAction(
						"action.pha.PHAAntiAction", "onUpdateStatePhaAnti",
						result);
				if (result1.getErrCode() < 0) {
					this.messageBox("修改抗菌药品状态失败");
					return;
				}
			}
		}
		yyList = false;// yanjing modify 20130929
	}

	/**
	 * 会诊申请界面
	 */
	public void onConsApply() {
		TParm consparm = new TParm();
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() > 2) {
			// 不可开立此医嘱
			this.messageBox("不能引用会诊申请");
			return;
		} else {
			String flg = "ODI";
			TParm parm = new TParm();
			// 获取table的值
			int row = getTTable("TABLE").getSelectedRow();
			if (row < 0)
				return;
			TParm Rowparm = getTTable("TABLE").getParmValue().getRow(row);
			String caseNo = Rowparm.getValue("CASE_NO");
			String patName = Rowparm.getValue("PAT_NAME");
			String inDate = Rowparm.getValue("IN_DATE");
			parm.setData("INW", "CASE_NO", caseNo);
			parm.setData("INW", "PAT_NAME", patName);
			parm.setData("INW", "ADM_DATE", inDate);
			parm.setData("INW", "MR_NO", mrNo);
			parm.setData("INW", "ODI_FLG", flg);
			parm.setData("INW", "IPD_NO", ipdNo);
			parm = (TParm) openDialog(
					"%ROOT%\\config\\inp\\INPConsApplication.x", parm);
		}
	}

	/**
	 * 科室套餐
	 */
	public void onDeptPack() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() > 3) {
			// 不可开立此医嘱
			this.messageBox("E0175");
			return;
		}
		this.yyList = true;
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "ODI");
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("USER_ID", Operator.getID());
		parm.setData("DEPT_OR_DR", 1);
		parm.addListener("INSERT_TABLE", this, "onQuoteSheetList");
		parm.addListener("INSERT_TABLE_FLG", this, "setYYlist");
		parm.setData("RULE_TYPE", tab.getSelectedIndex());
		// this.openDialog("%ROOT%\\config\\odi\\ODIPACKOrderUI.x",parm);
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\odi\\ODIPACKOrderUI.x", parm, true);
		window.setVisible(true);

	}

	/**
	 * 套餐赋值
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onQuoteSheetList(Object obj) {
		boolean falg = true;
		if (obj != null) {
			List orderList = (ArrayList) obj;
			Iterator iter = orderList.iterator();
			while (iter.hasNext()) {
				TParm temp = (TParm) iter.next();
				// System.out.println("====onQuoteSheetList temp===="+temp);
				falg = onQuoteSheetInsert(temp);
				if (!falg)
					break;
			}
		}
		return falg;
	}

	/**
	 * 得到当前页签
	 * 
	 * @param type
	 *            int
	 * @return String
	 */
	public String getRxKindString(int type) {
		String rxKind = "ST";
		switch (type) {
		case 0:
			rxKind = "ST";
			break;
		case 1:
			rxKind = "UD";
			break;
		case 2:
			rxKind = "DS";
			break;
		case 3:
			rxKind = "IG";
			break;
		}
		return rxKind;

	}

	/**
	 * 引用表单回调方法
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onQuoteSheetInsert(Object obj) {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		String tagStr = getRxKindString(tab.getSelectedIndex());
		boolean flag = true;
		// 判断是否是TPARM类型
		if (!(obj instanceof TParm)) {
			return false;
		}
		TParm action = (TParm) obj;
		action.setData("EXID_ROW", getExitRow());
		if (!tagStr.equals(action.getValue("RX_KIND"))) {
			this.messageBox("E0175");
			return false;
		}
		this.popReturn(action.getValue("RX_KIND"), action);
		return flag;
	}

	/**
	 * 拿到当前可编辑行
	 * 
	 * @return int
	 */
	public int getExitRow() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		TTable table = null;
		int row = -1;
		if (tab.getSelectedIndex() > 3)
			return row;
		switch (tab.getSelectedIndex()) {
		case 0:
			table = this.getTTable(TABLE1);
			break;
		case 1:
			table = this.getTTable(TABLE2);
			break;
		case 2:
			table = this.getTTable(TABLE3);
			break;
		case 3:
			table = this.getTTable(TABLE4);
			break;
		}
		int rowCount = table.getDataStore().rowCount();
		for (int i = 0; i < rowCount; i++) {
			if (!table.getDataStore().isActive(i)) {
				this.rowOnly = i;
				break;
			}
		}
		return this.rowOnly;
	}

	/**
	 * 科诊断
	 */
	public void onDeptDiag() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() != 4) {
			// 请选择临床诊断！
			this.messageBox("E0176");
			return;
		}
		if (this.getComponent("CLNDIAG") == null) {
			// 临床诊断未初始化！
			this.messageBox("E0177");
			return;
		}
		TControl control = ((TPanel) this.getComponent("CLNDIAG")).getControl();
		ODIClnDiagControl diagControl = null;
		if (control instanceof ODIClnDiagControl) {
			diagControl = ((ODIClnDiagControl) control);
		}
		// 判断是中医还是西医
		String param = "1," + Operator.getDept() + ","
				+ diagControl.getICDType();
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\ODOCommonIcdQuote.x", param);
		if (result == null || result.getCount("ICD_CODE") < 1) {
			return;
		}
		// this.messageBox_(result);
		int rowCount = result.getCount("ICD_CODE");
		List diagList = new ArrayList();
		for (int i = 0; i < rowCount; i++) {
			TParm diagParm = new TParm();
			diagParm = OdiUtil.getInstance().getDiagNosis(
					result.getValue("ICD_CODE", i));
			diagList.add(diagParm);
		}
		// this.messageBox_(diagList);
		diagControl.insertRow(diagList);
	}

	/**
	 * 医师诊断
	 */
	public void onDrDiag() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() != 4) {
			this.messageBox("E0176");
			return;
		}
		if (this.getComponent("CLNDIAG") == null) {
			this.messageBox("E0177");
			return;
		}
		TControl control = ((TPanel) this.getComponent("CLNDIAG")).getControl();
		ODIClnDiagControl diagControl = null;
		if (control instanceof ODIClnDiagControl) {
			diagControl = ((ODIClnDiagControl) control);
		}
		// 判断是中医还是西医
		String param = "2," + Operator.getDept() + ","
				+ diagControl.getICDType();
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\ODOCommonIcdQuote.x", param);
		if (result == null || result.getCount("ICD_CODE") < 1) {
			return;
		}
		// this.messageBox_(result);
		int rowCount = result.getCount("ICD_CODE");
		List diagList = new ArrayList();
		for (int i = 0; i < rowCount; i++) {
			TParm diagParm = new TParm();
			diagParm = OdiUtil.getInstance().getDiagNosis(
					result.getValue("ICD_CODE", i));
			diagList.add(diagParm);
		}
		// this.messageBox_(diagList);
		diagControl.insertRow(diagList);
	}

	/**
	 * 科医嘱
	 */
	public void onDeptOrder() {
		// this.messageBox_("科医嘱");
		TParm parm = new TParm();
		parm.setData("DEPT_DR", "1");
		parm.setData("FIT", "IPD_FIT_FLG");
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		String tag = "";
		if (tab.getSelectedIndex() > 3) {
			this.messageBox("E0178");
			return;
		}
		switch (tab.getSelectedIndex()) {
		// 临时0：补充计价。1：西药。2：管制药品。3：中药饮片。4：诊疗项目。5：检验检查
		case 0:
			parm.setData("RX_TYPE", "1,2,4,5");
			tag = "ST";
			break;
			// 长期
		case 1:
			parm.setData("RX_TYPE", "1,2,4");
			tag = "UD";
			break;
			// 出院带药
		case 2:
			parm.setData("RX_TYPE", "1,2,3");
			tag = "DS";
			break;
			// 中药
		case 3:
			parm.setData("RX_TYPE", "3");
			tag = "IG";
			break;
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\CommonOrderQuote.x", parm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		yyList = true;
		int rowCount = result.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) {
			//			this.messageBox("ORDER_CODE:"+result.getValue("ORDER_CODE",i));
			//			this.messageBox("DEPT_OR_DR:"+result.getValue("DEPT_OR_DR",i));
			//			this.messageBox("DEPTORDR_CODE:"+result.getValue("DEPTORDR_CODE",i));
			//			this.messageBox("RX_TYPE:"+result.getValue("RX_TYPE",i));
			//			this.messageBox("SEQ_NO:"+result.getValue("SEQ_NO",i));
			//start 20170228 machao 
			TParm OrderParm = OdiUtil.getInstance().getSysFeeOrderr(
					result.getValue("ORDER_CODE", i),result.getValue("DEPT_OR_DR",i),result.getValue("DEPTORDR_CODE",i),result.getValue("RX_TYPE",i),result.getValue("SEQ_NO",i));
			OrderParm.setData("EXID_ROW", getExitRow());
			this.popReturn(tag, OrderParm);
		}
		yyList = false;
	}

	/**
	 * 临床路径模板
	 */
	public void onAddCLNCPath() {
		TParm parm = new TParm();
		parm.setData("DEPT_DR", "1");
		parm.setData("FIT", "IPD_FIT_FLG");
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		String tag = "";
		if (tab.getSelectedIndex() > 3) {
			this.messageBox("E0178");
			return;
		}
		switch (tab.getSelectedIndex()) {
		// 临时0：补充计价。1：西药。2：管制药品。3：中药饮片。4：诊疗项目。5：检验检查
		case 0:
			parm.setData("RX_TYPE", "1,2,4,5");
			tag = "ST";
			break;
			// 长期
		case 1:
			parm.setData("RX_TYPE", "1,2,4");
			tag = "UD";
			break;
			// 出院带药
		case 2:
			parm.setData("RX_TYPE", "1,2,3");
			tag = "DS";
			break;
			// 中药
		case 3:
			parm.setData("RX_TYPE", "3");
			tag = "IG";
			break;
		}
		// ========pangben 2012-06 -23重新获得临床路径代码
		String clncPathCode = "";
		StringBuffer sql = new StringBuffer();
		sql
		.append("SELECT CLNCPATH_CODE,SCHD_CODE FROM ADM_INP WHERE CASE_NO='"
				+ caseNo + "'");
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		if (result.getCount("CLNCPATH_CODE") > 0) {
			clncPathCode = result.getValue("CLNCPATH_CODE", 0);
		}
		TParm inParm = new TParm();
		// =======庞犇2012-06-04 获得当前时程
		// 得到当前时程
		String schdCode = "";
		if (result.getCount("SCHD_CODE") > 0) {
			schdCode = result.getValue("SCHD_CODE", 0);
		}
		inParm.setData("CLNCPATH_CODE", clncPathCode);
		inParm.setData("SCHD_CODE", schdCode);
		inParm.setData("CASE_NO", caseNo);
		inParm.setData("PAGE_FLG", tag);
		yyList = true;
		result = (TParm) this.openDialog(
				"%ROOT%\\config\\clp\\CLPTemplateOrderQuote.x", inParm);
		// System.out.println("result::"+result);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		int rowCount = result.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) {
			// yuml s 20141031
			// TParm OrderParm = OdiUtil.getInstance().getSysFeeOrder(
			// result.getValue("ORDER_CODE", i));
			TParm OrderParm = OdiUtil.getInstance().getSysFeeAndclp(
					result.getValue("ORDER_CODE", i),
					result.getValue("CLNCPATH_CODE", i),
					result.getValue("SCHD_CODE", i),
					result.getValue("CHKTYPE_CODE", i),
					result.getValue("ORDER_SEQ_NO", i),
					result.getValue("ORDER_TYPE", i));
			// yuml e 20141031
			OrderParm.setData("CLP_FLG", "Y");
			OrderParm.setData("EXID_ROW", getExitRow());
			this.popReturn(tag, OrderParm);
		}
		yyList = false;
	}

	/**
	 * 医师医嘱
	 */
	public void onDrOrder() {
		TParm parm = new TParm();
		parm.setData("DEPT_DR", "2");
		parm.setData("FIT", "IPD_FIT_FLG");
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		String tag = "";
		if (tab.getSelectedIndex() > 3) {
			this.messageBox("E0178");
			return;
		}
		switch (tab.getSelectedIndex()) {
		// 临时
		case 0:
			parm.setData("RX_TYPE", "1,2,4,5");
			tag = "ST";
			break;
			// 长期
		case 1:
			parm.setData("RX_TYPE", "1,2,3,4");
			tag = "UD";
			break;
			// 出院带药
		case 2:
			parm.setData("RX_TYPE", "1,2,3");
			tag = "DS";
			break;
			// 中药
		case 3:
			parm.setData("RX_TYPE", "3");
			tag = "IG";
			break;
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\CommonOrderQuote.x", parm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		yyList = true;
		int rowCount = result.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) {
			TParm OrderParm = OdiUtil.getInstance().getSysFeeOrder(
					result.getValue("ORDER_CODE", i));
			OrderParm.setData("EXID_ROW", getExitRow());
			this.popReturn(tag, OrderParm);
		}
		yyList = false;
	}

	/**
	 * 医嘱单
	 */
	public void onSelYZD() {
		TParm parm = new TParm();
		parm.setData("INW", "CASE_NO", this.getCaseNo());
		this.openDialog("%ROOT%\\config\\inw\\INWOrderSheetPrtAndPreView.x",
				parm);
	}

	/**
	 * 体温表
	 */
	public void onSelTWD() {
		TParm parm = new TParm();
		parm.setData("SUM", "CASE_NO", this.getCaseNo());
		parm.setData("SUM", "ADM_TYPE", "I");
		// modified by WangQing 20170428
		if (this.isOidrFlg()|| this.tFlg) {
			parm.setData("SUM", "SAVE_FLG", true);
		} else {
			parm.setData("SUM", "SAVE_FLG", false);
		}
		this.openDialog("%ROOT%\\config\\sum\\SUMVitalSign.x", parm);
	}

	/**
	 * 申请单
	 */
	public void onApplyList() {
		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end 
		// 判断申请单
		TDataStore orderData = odiObject.getDS("ODI_ORDER");
		// 拿到申请单结果集
		TParm emrParm = OrderUtil.getInstance().getOrderPasEMRAll(orderData,
				"ODI");
		onExeApply(emrParm, "LIS");
	}

	/**
	 * 申请单公用 ========pangben 2013-7-30
	 * 
	 * @param emrParm
	 */
	private void onExeApply(TParm emrParm, String phaType) {
		if (emrParm.getInt("ACTION", "COUNT") > 0) {
			TParm actionParm = new TParm();
			actionParm.setData("SYSTEM_CODE", "ODI");
			actionParm.setData("ADM_TYPE", "I");
			actionParm.setData("MR_NO", this.getMrNo());
			actionParm.setData("IPD_NO", this.getIpdNo());
			actionParm.setData("PAT_NAME", this.getPatName());
			actionParm.setData("CASE_NO", this.getCaseNo());
			actionParm.setData("ADM_DATE", this.getAdmDate());
			actionParm.setData("DEPT_CODE", this.getDeptCode());
			actionParm.setData("STATION_CODE", this.getStationCode());
			actionParm.setData("STYLETYPE", "1");
			actionParm.setData("RULETYPE", "2");
			for(int i = 0; i < emrParm.getCount(); i++){
				emrParm.setData("DAY_OPE_FLG", i, this.getDayOpeFlg());
			}
			actionParm.setData("EMR_DATA_LIST", emrParm);
			actionParm.setData("PHATYPE", phaType);// ===pangben 2013-7-30
			//emrParm.setData("DAY_OPE_CODE",this.getDayOpeCode());
			// 抗菌药品显示申请单
			this.openWindow("%ROOT%\\config\\emr\\EMRSingleUI.x", actionParm);
		} else {
			// 没有需要编辑的申请单！
			this.messageBox("E0179");
		}
	}

	/**
	 * 抗菌药品申请单 ========pangben 2013-7-30
	 */
	public void onApplyListPha() {
		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170228 end
		// 判断申请单
		TDataStore orderData = odiObject.getDS("ODI_ORDER");
		// 拿到申请单结果集
		TParm emrParm = OrderUtil.getInstance().getOrderPasEMRAll(orderData,
				"ODI_PHA");
		onExeApply(emrParm, "PHA");

	}

	/**
	 * 抗菌药品发布公布栏 ===========pangben 2013-7-31
	 */
	private void onBoardMessage(TParm parm) {
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("MR_NO", this.getMrNo());
		parm.setData("USER_NAME", Operator.getName());
		parm.setData("PAT_NAME", this.getPatName());
		parm.setData("SQL", " WHERE DEPT_CODE in ('0111','0401') ");// 药剂科\感染控制科发送消息
		// 执行数据新增
		TParm result = TIOM_AppServer.executeAction(
				"action.mro.MROQlayControlAction", "onOdiBoardMessage", parm);
		// 保存判断
		if (result == null || result.getErrCode() < 0) {
			this.messageBox("发送失败" + " , " + result.getErrText());
			return;
		}
		// this.messageBox("发送成功");
	}

	/**
	 * 病历书写
	 */
	public void onEmrWrite() {
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "ODI");
		parm.setData("ADM_TYPE", "I");
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("PAT_NAME", this.getPatName());
		parm.setData("MR_NO", this.getMrNo());
		parm.setData("IPD_NO", this.getIpdNo());
		parm.setData("ADM_DATE", this.getAdmDate());
		parm.setData("DEPT_CODE", this.getDeptCode());
		parm.setData("STATION_CODE", this.getStationCode());
		if (this.isOidrFlg()) {
			parm.setData("RULETYPE", "3");
			// 写入类型(会诊)
			parm.setData("WRITE_TYPE", "OIDR");
		} else if(this.tFlg){// add by wangqing 20170804 规培适用
			parm.setData("RULETYPE", "3");
			// 写入类型(会诊)
			parm.setData("WRITE_TYPE", "T");
		}else {
			parm.setData("RULETYPE", "2");
			parm.setData("WRITE_TYPE", "");
		}
		parm.setData("EMR_DATA_LIST", new TParm());
		parm.addListener("EMR_LISTENER", this, "emrListener");
		parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
		if (isOpeFlg()) {
			this.openWindow("%ROOT%\\config\\emr\\TEmrWordNewUI.x", parm);// wanglong
			// modify
			// 20140707
		} else {
			this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
		}
	}

	/**
	 * 调用护理记录
	 */
	public void onHLSel() {
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "INW");
		parm.setData("ADM_TYPE", "I");
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("PAT_NAME", this.getPatName());
		parm.setData("MR_NO", this.getMrNo());
		parm.setData("IPD_NO", this.getIpdNo());
		parm.setData("ADM_DATE", this.getAdmDate());
		parm.setData("DEPT_CODE", this.getDeptCode());
		parm.setData("RULETYPE", "1");
		parm.setData("EMR_DATA_LIST", new TParm());
		parm.addListener("EMR_LISTENER", this, "emrListener");
		parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
		if (isOpeFlg()) {
			this.openWindow("%ROOT%\\config\\emr\\TEmrWordNewUI.x", parm);// wanglong
			// modify
			// 20140707
		} else {
			this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
		}
	}

	/**
	 * 费用查询
	 */
	public void onSelIbs() {
		TParm parm = new TParm();
		parm.setData("IBS", "CASE_NO", this.getCaseNo());
		parm.setData("IBS", "MR_NO", this.getMrNo());
		parm.setData("IBS", "TYPE", "ODISTATION");
		// this.openDialog()
		this.openWindow("%ROOT%\\config\\ibs\\IBSSelOrderm.x", parm);
	}

	/**
	 * EMR监听
	 * 
	 * @param parm
	 *            TParm
	 */
	public void emrListener(TParm parm) {
		TParm parmAction = new TParm();
		parm.runListener("setMicroData", "S", "A");
	}

	/**
	 * EMR保存监听
	 * 
	 * @param parm
	 *            TParm
	 */
	public void emrSaveListener(TParm parm) {
		// this.messageBox_(parm);
	}

	/**
	 * 常用选项关闭事件
	 */
	public void onCloseChickedCY() {
		TMovePane mp = (TMovePane) callFunction("UI|MOVEPANE|getThis");
		mp.onDoubleClicked(true);
	}

	/**
	 * 返回集合医嘱细相的TParm形式
	 * 
	 * @return result TParm
	 */
	public TParm getOrderSetDetails(int groupNo, String orderSetCode) {
		TParm result = new TParm();
		if (groupNo < 0) {
			System.out
			.println("OpdOrder->getOrderSetDetails->groupNo is invalie");
			return result;
		}
		if (StringUtil.isNullString(orderSetCode)) {
			System.out
			.println("OpdOrder->getOrderSetDetails->orderSetCode is invalie");
			return result;
		}
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		TParm parm = ds.getBuffer(buff);
		int count = parm.getCount();
		if (count < 0) {
			// System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return result;
		}
		// System.out.println("groupNo=-============" + groupNo);
		// System.out.println("orderSetCode===========" + orderSetCode);
		String tempCode;
		int tempNo;
		// System.out.println("count===============" + count);
		// temperr细项价格
		for (int i = 0; i < count; i++) {
			tempCode = parm.getValue("ORDERSET_CODE", i);
			tempNo = parm.getInt("ORDERSET_GROUP_NO", i);
			// System.out.println("tempCode==========" + tempCode);
			// System.out.println("tempNO============" + tempNo);
			// System.out.println("setmain_flg========" +
			// parm.getBoolean("SETMAIN_FLG", i));
			if (tempCode.equalsIgnoreCase(orderSetCode) && tempNo == groupNo
					&& !parm.getBoolean("SETMAIN_FLG", i)) {
				// ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
				result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
				result.addData("SPECIFICATION", parm.getValue("SPECIFICATION",
						i));
				result.addData("DOSAGE_QTY", parm.getValue("MEDI_QTY", i));// modify
				// by
				// by
				// 20151015
				result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				// 查询单价
				TParm ownPriceParm = new TParm(this.getDBTool().select(
						"SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE='"
								+ parm.getValue("ORDER_CODE", i) + "'"));
				// this.messageBox_(ownPriceParm);
				// 计算总价格
				double ownPrice = ownPriceParm.getDouble("OWN_PRICE", 0)// modify
						// by
						// guoy
						// 20151015
						* parm.getDouble("MEDI_QTY", i);
				result.addData("OWN_PRICE", ownPriceParm.getDouble("OWN_PRICE",
						0));
				result.addData("OWN_AMT", ownPrice);
				result.addData("EXEC_DEPT_CODE", parm.getValue(
						"EXEC_DEPT_CODE", i));
				result
				.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE",
						i));
				result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
			}
		}
		return result;
	}

	/**
	 * 打开集合医嘱细想查询
	 */
	public void openRigthPopMenu() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		int selectIndex = tab.getSelectedIndex() + 1;
		if (tab.getSelectedIndex() > 3)
			return;
		TParm action = new TParm();
		if (selectIndex == 2 && (this.getTRadioButton("DCORDER").isSelected()))
			action = this.getTTable("TABLE22").getParmValue().getRow(
					getTTable("TABLE22").getSelectedRow());
		else
			action = this.getTTable("TABLE" + selectIndex).getDataStore()
			.getRowParm(
					this.getTTable("TABLE" + selectIndex)
					.getSelectedRow());
		int groupNo = action.getInt("ORDERSET_GROUP_NO");
		String orderCode = action.getValue("ORDER_CODE");
		TParm parm = getOrderSetDetails(groupNo, orderCode);
		// this.messageBox_("集合医嘱细项"+parm);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
	}

	/**
	 * 右击MENU弹出事件
	 * 
	 * @param tableName
	 */
	public void showPopMenu(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		int selectIndex = tab.getSelectedIndex() + 1;
		if (tab.getSelectedIndex() > 3)
			return;
		TParm action = new TParm();
		if (selectIndex == 2 && (this.getTRadioButton("DCORDER").isSelected()))
			action = this.getTTable("TABLE22").getParmValue().getRow(
					getTTable("TABLE22").getSelectedRow());
		else
			action = this.getTTable("TABLE" + selectIndex).getDataStore()
			.getRowParm(
					this.getTTable("TABLE" + selectIndex)
					.getSelectedRow());
		if ("LIS".equals(action.getValue("CAT1_TYPE"))
				|| "RIS".equals(action.getValue("CAT1_TYPE"))
				|| "Y".equals(action.getValue("SETMAIN_FLG"))) {
			table
			.setPopupMenuSyntax("显示集合医嘱细相 \n Display collection details with your doctor,openRigthPopMenu;查看报告 \n Report,showRept");
			return;
		} else if ("PHA".equals(action.getValue("CAT1_TYPE"))) {// add by
			// wanglong
			// 20130522
			table
			.setPopupMenuSyntax("显示合理用药信息|Show Rational Drug Use,onQueryRationalDrugUse");
		} else {
			table.setPopupMenuSyntax("");
			return;
		}
	}

	/**
	 * 查看报告
	 */
	public void showRept() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		int selectIndex = tab.getSelectedIndex() + 1;
		if (tab.getSelectedIndex() > 3)
			return;
		TParm action = this.getTTable("TABLE" + selectIndex).getDataStore()
				.getRowParm(
						this.getTTable("TABLE" + selectIndex).getSelectedRow());
		// LIS报告
		if ("LIS".equals(action.getValue("CAT1_TYPE"))) {
			String labNo = action.getValue("MED_APPLY_NO");
			if (labNo.length() == 0) {
				this.messageBox("E0188");
				return;
			}
			SystemTool.getInstance().OpenLisWeb(this.getMrNo());
		}
		// RIS报告
		if ("RIS".equals(action.getValue("CAT1_TYPE"))) {

			if ("ECC".equals(action.getValue("ORDER_CAT1_CODE"))) {// 电生理调阅pdf
				TParm parm = new TParm();
				parm.setData("CASE_NO", action.getValue("CASE_NO"));
				parm.setData("OPE_BOOK_NO", action.getValue("MED_APPLY_NO"));
				TParm pathData = EMRCdrTool.getInstance().getWordPath(parm);
				for (int i = 0; i < pathData.getCount(); i++) {
					String fileName = pathData.getValue("FILE_NAME", i)
							+ ".pdf";
					String filePath = pathData.getValue("FILE_PATH", i);
					serverPath = TConfig.getSystemValue("FileServer.Main.Root")
							+ "\\" + TConfig.getSystemValue("EmrData") + "\\"
							+ filePath.replaceFirst("JHW", "PDF");
					parm.setData("FILE_NAME", fileName);
					Runtime runtime = Runtime.getRuntime();
					byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
							.getSocket(), serverPath + "\\" + fileName);
					if (data == null) {
						messageBox_("服务器上没有找到文件 " + serverPath + "\\"
								+ fileName);
						return;
					}
					try {
						FileTool.setByte(tempPath + "\\" + fileName, data);
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					try {
						// 打开文件
						runtime.exec("rundll32 url.dll FileProtocolHandler "
								+ tempPath + "\\" + fileName);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} else {// 非电生理 调阅影像
				SystemTool.getInstance().OpenRisByMrNoAndApplyNo(
						this.getMrNo(), action.getValue("MED_APPLY_NO"));
			}
		}

	}

	/**
	 * 处理当前TOOLBAR
	 */
	public void onShowWindowsFunction() {
		// 显示UIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	/**
	 * 整批修改
	 */
	public void onRXEditAll() {
		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		if (this.getTComboBox("RX_NO").getSelectedID().length() == 0) {
			this.messageBox("E0069");
			return;
		}
		// 判断是否已经审核
		if (isCheckNus()) {
			// 此处方已经有审核医嘱不可以整批修改！
			this.messageBox("E0180");
			return;
		}
		Object obj = this.openDialog("%ROOT%\\config\\odi\\ODIRXEditAll.x");
		if (obj == null)
			return;
		this.setZPValue(obj);
	}

	/**
	 * 设置整批修改值
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setZPValue(Object obj) {
		if (!(obj instanceof TParm))
			return;
		TParm parm = (TParm) obj;
		int rowCount = this.getTTable(TABLE3).getDataStore().rowCount();
		for (int i = 0; i < rowCount; i++) {
			if (!this.getTTable(TABLE3).getDataStore().isActive(i))
				continue;
			Object mediQtyObj = this.getTTable(TABLE3).getDataStore()
					.getItemData(i, "MEDI_QTY");
			if (parm.getDouble("MEDI_QTY") > 0)
				this.getTTable(TABLE3).getDataStore().setItem(i, "MEDI_QTY",
						parm.getDouble("MEDI_QTY"));
			if (parm.getInt("TAKE_DAYS") > 0)
				this.getTTable(TABLE3).getDataStore().setItem(i, "TAKE_DAYS",
						parm.getInt("TAKE_DAYS"));
			if (parm.getValue("FREQ_CODE").length() != 0)
				this.getTTable(TABLE3).getDataStore().setItem(i, "FREQ_CODE",
						parm.getValue("FREQ_CODE"));
			if (parm.getValue("ROUTE_CODE").length() != 0)
				this.getTTable(TABLE3).getDataStore().setItem(i, "ROUTE_CODE",
						parm.getValue("ROUTE_CODE"));
			this.getTTable(TABLE3).setDSValue(i);
			TTableNode tableNode = new TTableNode();
			tableNode.setTable(this.getTTable(TABLE3));
			tableNode.setRow(i);
			tableNode.setColumn(3);
			if (parm.getDouble("MEDI_QTY") > 0) {
				tableNode.setValue(parm.getDouble("MEDI_QTY"));
				tableNode.setOldValue(mediQtyObj);
			} else {
				tableNode.setValue(mediQtyObj);
				tableNode.setOldValue(parm.getDouble("MEDI_QTY"));
			}
			this.onChangeTableValueDS(tableNode);
		}
	}

	/**
	 * 护士是否已经审核
	 * 
	 * @return boolean
	 */
	public boolean isCheckNus() {
		boolean falg = false;
		int rowCount = this.getTTable(TABLE3).getDataStore().rowCount();
		for (int i = 0; i < rowCount; i++) {
			TParm parm = this.getTTable(TABLE3).getDataStore().getRowParm(i);
			if (parm.getValue("NS_CHECK_CODE").length() != 0) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * 处方号选择事件
	 */
	public void onRxNoSel() {
		// 防止初始化调用
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() != 2)
			return;
		if (getTComboBox("RX_NO").getSelectedID().equals(onlyRxNo)) {
			this.onQuery();
			return;
		}
		// 是否有没有保存的处方号
		if (this.onSaveFlg(3)) {
			// 请保存数据
			this.messageBox("E0181");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						getTComboBox("RX_NO").setSelectedID(onlyRxNo);
						onQuery();
					} catch (Exception e) {
					}
				}
			});
			return;
		}
		onlyRxNo = this.getValueString("RX_NO");
		this.onQuery();
		setIGParm("DS", onlyRxNo);
	}

	/**
	 * 住院中医处方号选择事件
	 */
	public void onRxNoSelIG() {
		// 防止初始化调用
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() != 3)
			return;
		if (getTComboBox("IG_RX_NO").getSelectedID().equals(onlyRxNo)) {
			this.onQuery();
			return;
		}
		// 是否有没有保存的处方号
		if (this.onSaveFlg(4)) {
			this.messageBox("E0181");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						getTComboBox("IG_RX_NO").setSelectedID(onlyRxNo);
						onQuery();
					} catch (Exception e) {
					}
				}
			});
			this.setValue("MEDI_QTYALL", this.getChnPhaMediQtyAll(odiObject
					.getDS("ODI_ORDER")));
			return;
		}
		onlyRxNo = this.getValueString("IG_RX_NO");
		this.onQuery();
		// 计算总克数
		this.setValue("MEDI_QTYALL", this.getChnPhaMediQtyAll(odiObject
				.getDS("ODI_ORDER")));
		setIGParm("IG", onlyRxNo);
	}

	/**
	 * 设置中药基础数据
	 * 
	 * @param rxNo
	 *            String
	 */
	public void setIGParm(String rxKind, String rxNo) {
		TDS ds = odiObject.getDS("ODI_ORDER");
		TParm parm = ds.getBuffer(ds.PRIMARY);
		// 出院带药
		if ("DS".equals(rxKind)) {
			if (isChangValue(parm)) {
				String orgCode = ds.getItemString(0, "EXEC_DEPT_CODE");
				this.setValue("DEPT_CODEDS", orgCode);
			} else {
				this.setValue("DEPT_CODEDS", this.getOrgCode());
			}
		}
		// 草药
		if ("IG".equals(rxKind)) {
			if (isChangValue(parm)) {
				this.setValue("DEPT_CODEIG", ds.getItemString(0,
						"EXEC_DEPT_CODE"));
				this.setValue("RF", ds.getItemString(0, "TAKE_DAYS"));
				this.setValue("YPJL", ds.getItemString(0, "DCT_TAKE_QTY"));
				this.setValue("IGFREQCODE", ds.getItemString(0, "FREQ_CODE"));
				this.setValue("IG_ROUTE", ds.getItemString(0, "ROUTE_CODE"));
				this.setValue("IG_DCTAGENT", ds.getItemString(0,
						"DCTAGENT_CODE"));
			} else {
				this.setValue("DEPT_CODEIG", this.getOrgCode());
				this.setValue("RF", odiObject
						.getAttribute(odiObject.DCT_TAKE_DAYS));
				this.setValue("YPJL", odiObject
						.getAttribute(odiObject.DCT_TAKE_QTY));
				this.setValue("IGFREQCODE", odiObject
						.getAttribute(odiObject.G_FREQ_CODE));
				this.setValue("IG_ROUTE", odiObject
						.getAttribute(odiObject.G_ROUTE_CODE));
				this.setValue("IG_DCTAGENT", odiObject
						.getAttribute(odiObject.G_DCTAGENT_CODE));
			}
		}
	}

	/**
	 * 是否改变基础设置
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public boolean isChangValue(TParm parm) {
		boolean falg = false;
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getBoolean("#ACTIVE#", i)) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * 找到新处方号
	 * 
	 * @return String
	 */
	public String getRxNoID() {
		String id = "";
		int rowCount = this.rxNoTComboParm.getCount("ACTIVE");
		for (int i = 0; i < rowCount; i++) {
			if ("Y".equals(this.rxNoTComboParm.getValue("ACTIVE", i))) {
				id = this.rxNoTComboParm.getValue("ID", i);
				break;
			}
		}
		return id;
	}

	/**
	 * 找到新处方号
	 * 
	 * @return String
	 */
	public String getRxNoIGID() {
		String id = "";
		int rowCount = this.igRxNoTComboParm.getCount("ACTIVE");
		for (int i = 0; i < rowCount; i++) {
			if ("Y".equals(this.igRxNoTComboParm.getValue("ACTIVE", i))) {
				id = this.igRxNoTComboParm.getValue("ID", i);
				break;
			}
		}
		return id;
	}

	/**
	 * 生成新处方号
	 */
	public void onNewRxNo(String type) {
		// 是否有新的处方号没有保存
		if (isRxNoID()) {
			// 还有新处方号未保存使用
			if ("1".equals(type))
				this.messageBox("E0182");
			onRxNoSel();
			return;
		}
		String rxNo = SystemTool.getInstance().getNo("ALL", "ODI", "RX_NO",
				"RX_NO");
		if (rxNo.length() == 0) {
			// 处方号生成失败！
			this.messageBox("E0183");
			return;
		}
		int presrtNo = getPresrtNo(this.rxNoTComboParm);
		this.rxNoTComboParm.addData("ID", rxNo);
		this.rxNoTComboParm.addData("NAME", "处方 Rx");
		this.rxNoTComboParm.addData("TEXT", "【" + presrtNo + "】");
		this.rxNoTComboParm.addData("ACTIVE", "Y");
		this.rxNoTComboParm.addData("PRESRT_NO", presrtNo);
		this.rxNoTComboParm.setData("COUNT", presrtNo);
		this.rxNoTComboParm.setData("ACTION", "COUNT", presrtNo);
		this.getTComboBox("RX_NO").setParmValue(this.rxNoTComboParm);
		this.getTComboBox("RX_NO").setSelectedID(rxNo);
		onRxNoSel();
	}

	/**
	 * 拿到服号
	 * 
	 * @param parm
	 *            TParm
	 * @return int
	 */
	public int getPresrtNo(TParm parm) {
		int presrtNo = 0;
		int rowCount = parm.getCount();
		for (int i = 0; i < rowCount; i++) {
			if (presrtNo > parm.getInt("PRESRT_NO", i))
				continue;
			presrtNo = parm.getInt("PRESRT_NO", i);
		}
		return presrtNo + 1;
	}

	/**
	 * 中药饮片处方号
	 */
	public void onNewRxNoIG(String type) {
		// 是否有新的处方号没有保存
		if (isRxNoIGID()) {
			if ("1".equals(type))
				this.messageBox("E0182");
			this.onRxNoSelIG();
			return;
		}
		String rxNo = SystemTool.getInstance().getNo("ALL", "ODI", "RX_NO",
				"RX_NO");
		if (rxNo.length() == 0) {
			this.messageBox("E0183");
			return;
		}
		int presrtNo = getPresrtNo(this.igRxNoTComboParm);
		this.igRxNoTComboParm.addData("ID", rxNo);
		this.igRxNoTComboParm.addData("NAME", "处方 Rx");
		this.igRxNoTComboParm.addData("TEXT", "【" + presrtNo + "】");
		this.igRxNoTComboParm.addData("ACTIVE", "Y");
		this.igRxNoTComboParm.addData("PRESRT_NO", presrtNo);
		this.igRxNoTComboParm.setData("COUNT", presrtNo);
		this.igRxNoTComboParm.setData("ACTION", "COUNT", presrtNo);
		this.getTComboBox("IG_RX_NO").setParmValue(this.igRxNoTComboParm);
		this.getTComboBox("IG_RX_NO").setSelectedID(rxNo);
		this.onRxNoSelIG();
	}

	/**
	 * 是否有新的处方号(中医)
	 * 
	 * @return boolean
	 */
	public boolean isRxNoIGID() {
		boolean falg = false;
		int rowCount = this.igRxNoTComboParm.getCount("ACTIVE");
		for (int i = 0; i < rowCount; i++) {
			if ("Y".equals(this.igRxNoTComboParm.getValue("ACTIVE", i))) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * 是否有新的处方号
	 * 
	 * @return boolean
	 */
	public boolean isRxNoID() {
		boolean falg = false;
		int rowCount = this.rxNoTComboParm.getCount("ACTIVE");
		for (int i = 0; i < rowCount; i++) {
			if ("Y".equals(this.rxNoTComboParm.getValue("ACTIVE", i))) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * 删处方
	 */
	public void onDelRxNo() {
		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		if (this.getTComboBox("RX_NO").getSelectedID().length() == 0) {
			this.messageBox("E0069");
			return;
		}
		this.delRxNoAll(this.getTTable(TABLE3));
	}

	/**
	 * 中医删处方
	 */
	public void onDelIGRxNo() {
		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		if (this.getTComboBox("IG_RX_NO").getSelectedID().length() == 0) {
			this.messageBox("E0069");
			return;
		}
		this.delRxNoAll(this.getTTable(TABLE4));
		// 计算总克数
		this.setValue("MEDI_QTYALL", this.getChnPhaMediQtyAll(odiObject
				.getDS("ODI_ORDER")));
	}

	/**
	 * 备血申请
	 */
	public void onBXResult() {
		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		TParm inparm = new TParm();
		inparm.setData("MR_NO", this.getMrNo());
		inparm.setData("ADM_TYPE", "I");
		inparm.setData("USE_DATE", SystemTool.getInstance().getDate());
		inparm.setData("DEPT_CODE", this.getDeptCode());
		inparm.setData("DR_CODE", Operator.getID());
		inparm.setData("ICD_CODE", this.getIcdCode());
		inparm.setData("ICD_DESC", this.getIcdDesc());
		inparm.setData("CASE_NO", this.getCaseNo());
		// 没有历史记录
		this.openDialog("%ROOT%\\config\\bms\\BMSApplyNo.x", inparm);
	}

	/**
	 * 取血申请 add by yangjj 20150427
	 */
	public void onQXResult() {

		TParm parmdate = new TParm();

		// add by yangjj 20150601
		boolean opeFlg = ((TParm) this.getParameter()).getBoolean("ODI",
				"OPE_FLG");

		String caseno = this.getCaseNo();
		String deptCode = this.getValueString("DEPT_CODE");
		String mrno = this.getMrNo();

		parmdate.setData("ADM_TYPE", "I");
		parmdate.setData("IPD_NO", getValueString("IPD_NO"));
		parmdate.setData("USE_DATE", SystemTool.getInstance().getDate());
		parmdate.setData("DR_CODE", Operator.getID());
		parmdate.setData("CASE_NO", caseno);
		parmdate.setData("MR_NO", mrno);

		if (opeFlg) {
			parmdate.setData("DEPT_CODE", getValueString("OP_DEPT_CODE"));
			parmdate.setData("OPE_ROOM", getValueString("ROOM_NO"));
		} else {
			parmdate.setData("DEPT_CODE", deptCode);
			parmdate.setData("OPE_ROOM", "");
		}

		parmdate.setData("OPE_FLG", opeFlg);
		this.openDialog("%ROOT%\\config\\bms\\BMSBloodTake.x", parmdate);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));

	}

	/**
	 * 手术麻醉
	 */
	public void onSSMZ() {
		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		TParm parm = new TParm();
		parm.setData("MR_NO", this.getMrNo());
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("STATION_CODE", this.getStationCode());
		parm.setData("ICD_CODE", this.getIcdCode());
		parm.setData("ADM_TYPE", "I");// 门急住别
		parm.setData("BOOK_DEPT_CODE", this.getDeptCode());// 申请部门
		parm.setData("BOOK_DR_CODE", Operator.getID());// 申请人员
		parm.setData("DAY_OPE_FLG",this.getDayOpeFlg());
		this.openDialog("%ROOT%\\config\\ope\\OPEOpBook.x", parm);
	}

	/**
	 * 出院通知
	 */
	public void onOutHosp() {
		// boolean falg = false;
		// TParm parm = new
		// TParm(this.getDBTool().select("SELECT * FROM ODI_ORDER WHERE CASE_NO='"+this.getCaseNo()+"' AND RX_KIND='UD' AND DC_DATE IS NULL"));
		// if(parm.getCount()>0){
		// if(this.messageBox("提示信息","是否停用医嘱",this.YES_NO_OPTION)!=0){
		// this.messageBox("有未停用医嘱不可以开立出院通知！");
		// return;
		// }
		// TParm saveParm = new
		// TParm(this.getDBTool().update("UPDATE ODI_ORDER SET DC_DR_CODE='"+Operator.getID()+"' ,DC_DATE=SYSDATE,DC_DEPT_CODE='"+Operator.getDept()+"' WHERE CASE_NO='"+this.getCaseNo()+"' AND DC_DATE IS NULL"));
		// if(saveParm.getErrCode()<0){
		// this.messageBox("停用失败！");
		// }else{
		// this.messageBox("停用成功！");
		// }
		// }
		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		this
		.openDialog("%ROOT%\\config\\adm\\ADMDrResvOut.x", this
				.getCaseNo());
	}

	/**
	 * 门诊病历
	 */
	public void onOpdBL() {
		TParm opdParm = new TParm(this.getDBTool().select(
				"SELECT * FROM REG_PATADM WHERE MR_NO='" + this.getMrNo()
				+ "' AND (ADM_TYPE='O' OR ADM_TYPE='E')"));
		if (opdParm.getCount() < 0) {
			// 此病患没有门诊病历！
			this.messageBox("E0184");
			return;
		}
		this.openDialog("%ROOT%\\config\\odi\\OPDInfoUi.x", opdParm);
	}

	/**
	 * 检验报告
	 */
	public void onLis() {
		SystemTool.getInstance().OpenLisWeb(this.getMrNo());
	}

	/**
	 * 检查报告
	 */
	public void onRis() {
		SystemTool.getInstance().OpenRisWeb(this.getMrNo());
	}

	/**
	 * 医嘱全停
	 */
	public void onStopUD() {
		BYXflg=false;
		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		if (this.messageBox("提示信息 Tips", "是否停用医嘱 \n Out medical advice if",
				this.YES_NO_OPTION) != 0) {
			return;
		}
		TParm saveParm = new TParm(
				this
				.getDBTool()
				.update(
						"UPDATE ODI_ORDER SET DC_DR_CODE='"
								+ Operator.getID()
								+ "' ,DC_DATE=SYSDATE,DC_DEPT_CODE='"
								+ this.getDeptCode()
								+ "' WHERE CASE_NO='"// shibl 20130312
								// modify
								// 去病人所在科室
								+ this.getCaseNo()
								+ "' AND (DC_DATE IS NULL OR (DC_DATE IS NOT NULL AND DC_DATE > SYSDATE)) "
								+ " AND RX_KIND='UD'"));
		if (saveParm.getErrCode() < 0) {
			// 停用失败
			this.messageBox("E0185");
		} else {
			// 停用成功！
			this.messageBox("E0186");
		}
		// 设置出院带药处方签
		// initDSRxNoCombo();
		// 设置中药饮片处方签
		// initIGRxNoCombo();
		// TABLE锁定
		initDataStoreToTable();
		// 设置出院带药处方签默认选项
		// this.getTComboBox("RX_NO").setSelectedID("");
		// this.onRxNoSel();
		// 设置中药饮片处方签默认选项
		// this.getTComboBox("IG_RX_NO").setSelectedID("");
		// this.onRxNoSelIG();
		this.clearFlg = "N";
		// this.setValue("MEDI_QTYALL",0.0);
	}

	/**
	 * 合理用药
	 */
	public void onRational() {
		if (!passIsReady) {
			this.messageBox("E0067");
			return;
		}
		if (!passFlg) {
			this.messageBox("合理用药初始化失败，此功能不能使用！");
			return;
		}
		if (((TTabbedPane) this.getComponent("TABLEPANE")).getSelectedIndex() == 4) {
			this.messageBox("合理用药不适用过敏记录！");
			return;
		}
		TParm parm = setodiRecipeInfo();
		// System.out.println("----------------------"+parm);
		if (parm.getCount("ERR") > 0) {
			this.messageBox("E0068");
			return;
		}
		if (!(parm.getCount("ORDER_CODE") > 0)) {
			this.messageBox("未检测到药品！");
			return;
		}
		// 住院使用
		PassDriver.PassDoCommand(3);
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			PassDriver.PassGetWarn1(parm.getValue("SEQ", i) + "");
			// System.out.println("-------------------"+PassDriver.PassGetWarn1(parm.getValue("SEQ",
			// i) + ""));
		}
	}

	/**
	 * 得到合理用药信息
	 * 
	 * @param parm
	 *            TParm
	 * @return String[]
	 */
	public String[] getOrderInfo(TParm parm) {
		String[] result = new String[12];
		// 药品唯一码：RX_NO+FILL0(SEQ,3)+ORDER_CODE
		result[0] = parm.getValue("ORDER_NO")
				+ StringTool.fill0(parm.getValue("SEQ_NO") + "", 3)
				+ parm.getValue("ORDER_CODE");
		result[1] = parm.getValue("ORDER_CODE");
		result[2] = parm.getValue("ORDER_DESC");
		result[3] = parm.getValue("MEDI_QTY");
		result[4] = OrderUtil.getInstance().getUnit(parm.getValue("MEDI_UNIT"));
		result[5] = OrderUtil.getInstance().getFreq(parm.getValue("FREQ_CODE"));
		String date = StringTool.getString(
				TJDODBTool.getInstance().getDBTime(), "yyyy-MM-dd");
		result[6] = date;
		result[7] = date;
		result[8] = OrderUtil.getInstance().getRoute(
				parm.getValue("ROUTE_CODE"));
		result[9] = "1";
		String type = "UD".equals(parm.getValue("RX_KIND")) ? "0" : "1";
		result[10] = type;
		result[11] = Operator.getName();
		return result;
	}

	/**
	 * 
	 * 自动检测合理用药
	 * 
	 */
	private boolean checkDrugAuto() {
		// 合理用药开关
		if (!passIsReady) {
			return true;
		}
		// 初始化标识
		if (!passFlg) {
			return true;
		}
		PassTool.getInstance().setadmPatientInfo(this.getCaseNo());
		PassTool.getInstance().setAllergenInfo(this.getMrNo());
		PassTool.getInstance().setadmMedCond(this.getCaseNo());
		TParm parm = setodiRecipeInfoAuto();
		if (!isWarn(parm)) {
			return true;
		}
		if (enforcementFlg) {
			return false;
		}
		if (JOptionPane.showConfirmDialog(null, "有药品使用不合理,是否存档?", "信息",
				JOptionPane.YES_NO_OPTION) != 0) {
			TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
			String rx_no = "";
			int selTabIndex = tab.getSelectedIndex();
			TDS ds = odiObject.getDS("ODI_ORDER");
			switch (selTabIndex) {
			case 0:
				// 临时
				// 所有数据放入主分区
				ds
				.setFilter("RX_KIND='ST' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y'  ");
				ds.filter();
				break;
			case 1:

				// 长期
				// 所有数据放入主分区
				ds
				.setFilter("RX_KIND='UD' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y'  ");
				ds.filter();
				break;
			case 2:

				// 出院带药
				// 所有数据放入主分区
				ds
				.setFilter("RX_KIND='DS' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y'  ");
				ds.filter();
				break;
			case 3:
				// 中药饮片
				rx_no = this.getValueString("IG_RX_NO");
				ds
				.setFilter("RX_KIND='IG' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y'  ");
				ds.filter();
			}
			String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			int newRow[] = ds.getNewRows(buff);
			for (int i : newRow) {
				this.DelOrder(newRow[0]);
			}
			return false;
		}
		return true;
	}

	/**
	 * 住院医生检核药品自动
	 * 
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @return TParm
	 */
	public TParm setodiRecipeInfoAuto() {
		TParm parm = setodiRecipeInfo();
		PassDriver.PassDoCommand(1);
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			result.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
			result.addData("FLG", PassDriver.PassGetWarn1(parm.getValue("SEQ",
					i)));
		}
		return result;
	}

	/**
	 * 传入住院医生药品信息
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm setodiRecipeInfo() {
		TParm parm = new TParm();
		int j;
		String[] orderInfo;
		String[] orderInfo1;
		String[] orderInfo2;
		Object obj = odiObject.getAttribute(odiObject.OID_DSPN_TIME);
		TDS ds = odiObject.getDS("ODI_ORDER");
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		// 出院带药
		if (tab.getSelectedIndex() == 2) {
			ds
			.setFilter("(RX_KIND='DS' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='DS' AND #ACTIVE#='N'");
			ds.filter();
		}
		String sql1 = "";
		// 长期临时
		if (tab.getSelectedIndex() == 0 || tab.getSelectedIndex() == 1) {
			sql1 = " SELECT * " + " FROM   ODI_ORDER A  "
					+ " WHERE  A.CASE_NO ='" + this.caseNo + "'"
					+ " AND (A.RX_KIND='ST' OR A.RX_KIND='UD')"
					+ " AND    A.CAT1_TYPE = 'PHA' "
					+ " AND    A.DC_DATE IS NULL ";
			// ds.setFilter("(RX_KIND='ST' AND HIDE_FLG = 'N' AND DC_DATE='' AND CAT1_TYPE = 'PHA' ) OR RX_KIND='ST' AND CAT1_TYPE = 'PHA' AND  #ACTIVE#='N'");
			// ds.filter();
			// String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			// Timestamp d1 = SystemTool.getInstance().getDate();
			// String nowtime1 = ("" + d1).substring(0, 10).replaceAll("-", "");
			// long nowStr1 = d1.getTime();
			// long startStr1 = StringTool.getTimestamp(nowtime1 + "000000",
			// "yyyyMMddHHmmss").getTime();
			// for (int i = 0; i < buff.length(); i++) {
			// TParm temp = ds.getRowParm(i);
			// String effdate = temp.getValue("EFF_DATE");
			// if (!effdate.equals("")) {
			// long leffDate = strToDate(
			// temp.getValue("EFF_DATE").substring(0, 19)
			// .replaceAll("-", "/"),
			// "yyyy/MM/dd HH:mm:ss").getTime();
			// if (temp.getValue("RX_KIND").equals("ST")
			// && leffDate < startStr1)
			// continue;
			// }
			// if (StringUtil.isNullString(temp.getValue("ORDER_DESC"))) {
			// continue;
			// }
			// System.out.println("-------ST-----------"+temp);
			// orderInfo1 = this.getOrderInfo(temp);
			// j = PassDriver.PassSetRecipeInfo(orderInfo1[0], orderInfo1[1],
			// orderInfo1[2], orderInfo1[3], orderInfo1[4], orderInfo1[5],
			// orderInfo1[6], orderInfo1[7], orderInfo1[8], orderInfo1[9],
			// orderInfo1[10], orderInfo1[11]);
			// if (j != 1) {
			// parm.addData("ERR", orderInfo1[0]);
			// break;
			// } else {
			// parm.addData("SEQ", orderInfo1[0]);
			// parm.addData("ORDER_CODE", orderInfo1[1]);
			// }
			// }
			// Object obj1 = odiObject.getAttribute(odiObject.OID_DSPN_TIME);
			// TDS dst = odiObject.getDS("ODI_ORDER");
			// dst.setFilter("(RX_KIND='UD'  AND CAT1_TYPE = 'PHA' ) OR RX_KIND='UD' AND CAT1_TYPE = 'PHA' AND #ACTIVE#='N'");
			// dst.filter();
			// buff = dst.isFilter() ? dst.FILTER : dst.PRIMARY;
			// for (int i = 0; i < buff.length(); i++) {
			// TParm temp = ds.getRowParm(i);
			// if (StringUtil.isNullString(temp.getValue("ORDER_DESC"))) {
			// continue;
			// }
			// if(!temp.getValue("DC_DATE").equals(""))
			// continue;
			// System.out.println("-------UD-----------"+temp);
			// orderInfo2 = this.getOrderInfo(temp);
			// j = PassDriver.PassSetRecipeInfo(orderInfo2[0], orderInfo2[1],
			// orderInfo2[2], orderInfo2[3], orderInfo2[4], orderInfo2[5],
			// orderInfo2[6], orderInfo2[7], orderInfo2[8], orderInfo2[9],
			// orderInfo2[10], orderInfo2[11]);
			// if (j != 1) {
			// parm.addData("ERR", orderInfo2[0]);
			// break;
			// } else {
			// parm.addData("SEQ", orderInfo2[0]);
			// parm.addData("ORDER_CODE", orderInfo2[1]);
			// }
			// }
		}
		// 中药饮片
		if (tab.getSelectedIndex() == 3) {
			ds
			.setFilter("(RX_KIND='IG' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='IG' AND #ACTIVE#='N'");
			ds.filter();
		}
		if (tab.getSelectedIndex() == 0 || tab.getSelectedIndex() == 1) {
			TParm USparm = new TParm(TJDODBTool.getInstance().select(sql1));
			if (USparm.getErrCode() < 0) {
				return null;
			}
			String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			Timestamp d1 = SystemTool.getInstance().getDate();
			String nowtime1 = ("" + d1).substring(0, 10).replaceAll("-", "");
			long nowStr1 = d1.getTime();
			long startStr1 = StringTool.getTimestamp(nowtime1 + "000000",
					"yyyyMMddHHmmss").getTime();
			for (int i = 0; i < USparm.getCount(); i++) {
				String effdate = USparm.getValue("EFF_DATE", i);
				if (!effdate.equals("")) {
					long leffDate = strToDate(
							USparm.getValue("EFF_DATE", i).substring(0, 19)
							.replaceAll("-", "/"),
							"yyyy/MM/dd HH:mm:ss").getTime();
					if (USparm.getValue("RX_KIND", i).equals("ST")
							&& leffDate < startStr1)
						continue;
				}
				TParm temp = USparm.getRow(i);
				String caseNo = temp.getValue("CASE_NO");
				String orderNo = temp.getValue("ORDER_NO");
				String orderSeq = temp.getValue("ORDER_SEQ");
				if (StringUtil.isNullString(temp.getValue("ORDER_DESC"))) {
					continue;
				}
				// ----------------------start------------------------------------------
				// 删除的医嘱处理 shibl 20120713 add 过滤删除的医嘱和替换修改的医嘱
				int delCount = ds.getDeleteCount() < 0 ? 0 : ds
						.getDeleteCount();
				boolean delflg = false;
				if (delCount > 0) {
					TParm delParm = ds.getBuffer(ds.DELETE);
					int delRowCount = delParm.getCount("ORDER_CODE");
					for (int di = 0; di < delRowCount; di++) {
						TParm dParm = delParm.getRow(di);
						String delcaseNo = dParm.getValue("CASE_NO");
						String delorderNo = dParm.getValue("ORDER_NO");
						String delorderSeq = dParm.getValue("ORDER_SEQ");
						if ((delcaseNo + delorderNo + delorderSeq)
								.equals(caseNo + orderNo + orderSeq)) {
							delflg = true;
							break;
						}
					}
				}
				if (delflg)
					continue;
				// 修改的数据
				int modifRow[] = ds.getOnlyModifiedRows(buff);
				TParm modParm = new TParm();
				for (int ti : modifRow) {
					modParm = ds.getRowParm(ti, buff);
					String modcaseNo = modParm.getValue("CASE_NO");
					String modorderNo = modParm.getValue("ORDER_NO");
					String modorderSeq = modParm.getValue("ORDER_SEQ");
					if ((modcaseNo + modorderNo + modorderSeq).equals(caseNo
							+ orderNo + orderSeq))
						temp = modParm;
				}
				orderInfo = this.getOrderInfo(temp);
				j = PassDriver.PassSetRecipeInfo(orderInfo[0], orderInfo[1],
						orderInfo[2], orderInfo[3], orderInfo[4], orderInfo[5],
						orderInfo[6], orderInfo[7], orderInfo[8], orderInfo[9],
						orderInfo[10], orderInfo[11]);
				if (j != 1) {
					parm.addData("ERR", orderInfo[0]);
					break;
				} else {
					parm.addData("SEQ", orderInfo[0]);
					parm.addData("ORDER_CODE", orderInfo[1]);
				}
			}
			// ----------------------end------------------------------------------
			// 新加的数据
			int newRow[] = ds.getNewRows(buff);
			for (int i : newRow) {
				if (!ds.isActive(i, buff))
					continue;
				TParm newParm = ds.getRowParm(i, buff);
				if (!newParm.getValue("RX_KIND").equals("ST")
						&& !newParm.getValue("RX_KIND").equals("UD"))
					continue;
				if (!newParm.getValue("CAT1_TYPE").equals("PHA"))
					continue;
				if (newParm.getValue("ORDER_DESC").equals("")) {
					continue;
				}
				orderInfo = this.getOrderInfo(newParm);
				j = PassDriver.PassSetRecipeInfo(orderInfo[0], orderInfo[1],
						orderInfo[2], orderInfo[3], orderInfo[4], orderInfo[5],
						orderInfo[6], orderInfo[7], orderInfo[8], orderInfo[9],
						orderInfo[10], orderInfo[11]);
				if (j != 1) {
					parm.addData("ERR", orderInfo[0]);
					break;
				} else {
					parm.addData("SEQ", orderInfo[0]);
					parm.addData("ORDER_CODE", orderInfo[1]);
				}
			}
		} else {
			String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			for (int i = 0; i < buff.length(); i++) {
				TParm temp = ds.getRowParm(i);
				if (StringUtil.isNullString(temp.getValue("ORDER_DESC"))) {
					continue;
				}
				orderInfo = this.getOrderInfo(temp);
				j = PassDriver.PassSetRecipeInfo(orderInfo[0], orderInfo[1],
						orderInfo[2], orderInfo[3], orderInfo[4], orderInfo[5],
						orderInfo[6], orderInfo[7], orderInfo[8], orderInfo[9],
						orderInfo[10], orderInfo[11]);
				if (j != 1) {
					parm.addData("ERR", orderInfo[0]);
					break;
				} else {
					parm.addData("SEQ", orderInfo[0]);
					parm.addData("ORDER_CODE", orderInfo[1]);
				}
			}
		}
		return parm;
	}

	private boolean isWarn(TParm parm) {
		boolean warnFlg = false;
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			int flg = parm.getInt("FLG", i);
			if (!warnFlg) {
				if (getWarn(flg)) {
					warnFlg = true;
				} else {
					warnFlg = false;
				}
			}
		}
		return warnFlg;
	}

	private boolean getWarn(int flg) {
		if (warnFlg != 3 && flg != 3) {
			if (warnFlg != 2 && flg != 2) {
				if (flg >= warnFlg) {
					return true;
				} else {
					return false;
				}
			} else if (warnFlg == 2 && flg != 2) {
				return false;
			} else if (warnFlg != 2 && flg == 2) {
				return true;
			} else if (warnFlg == 2 && flg == 2) {
				return true;
			}
		} else if (warnFlg == 3 && flg != 3) {
			return false;
		} else if (warnFlg != 3 && flg == 3) {
			return true;
		} else if (warnFlg == 3 && flg == 3) {
			return true;
		}
		return false;
	}

	/**
	 * 如有既往史或过敏史页签头就变色 参数：第一次打开界面入参是true 保存删除修改入参是false
	 * 由于第一次打开界面没有添加新行但是有一条数据的情况 删除数据时，已经添加了新行所以表格中的数据量总是大于1
	 * =============pangben modify 20110608
	 */
	public void onDiagPnChange(boolean firstOpen) {
		// 过敏记录
		TTable table = getTTable(GMTABLE);
		table.setFilter("DRUG_TYPE='" + getDrugType() + "' AND MR_NO='"
				+ this.getMrNo() + "'");
		table.getFilter();
		table.setDSValue();
		TTabbedPane p = (TTabbedPane) this.getComponent("TABLEPANE");
		// 打开界面
		// if (table.getRowCount() >= 1 && firstOpen) {
		// p.setTabColor(4, red);
		// // 保存删除修改
		// } else if (table.getRowCount() > 1 && !firstOpen) {
		// p.setTabColor(4, red);
		// } else
		// p.setTabColor(4, null);// 没有数据
		//

		String sql = " SELECT DRUG_TYPE FROM OPD_DRUGALLERGY WHERE MR_NO='"
				+ this.getMrNo() + "' AND DRUG_TYPE != 'N'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {
			p.setTabColor(4, red);
		} else {
			p.setTabColor(4, null);
		}

	}

	/**
	 * 获得当前药品的默认用量 ==============pangben modify 20110609
	 * 
	 * @return TParm
	 */
	public TParm getMediQty(TParm stIndParm, TParm actionParm) {
		TParm mediQTY = null;
		String mediQtySQL = "SELECT MEDI_QTY,MEDI_UNIT,DOSAGE_UNIT,FREQ_CODE,ROUTE_CODE FROM PHA_BASE WHERE ORDER_CODE='"
				+ actionParm.getValue("ORDER_CODE") + "'";
		mediQTY = new TParm(TJDODBTool.getInstance().select(mediQtySQL));
		return mediQTY;
	}

	/**
	 * 检测医令管控标识
	 */
	public String getctrflg(String order_code) {
		String ctr_flg;
		TParm flg = SYSFeeTool.getInstance().getCtrFlg(order_code);
		ctr_flg = flg.getValue("CRT_FLG", 0);
		return ctr_flg;
	}

	/**
	 * 删除医嘱方法
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public void DelOrder(int row) {
		// 当前选中页签
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		TTable table = null;
		String rx_no = "";
		ds.deleteRow(row, buff);
		int selTabIndex = tab.getSelectedIndex();
		switch (selTabIndex) {
		case 0:
			// 临时
			table = getTTable(TABLE1);
			// 所有数据放入主分区
			table
			.setFilter("(RX_KIND='ST' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='ST' AND #ACTIVE#='N'");
			break;
		case 1:
			// 长期
			table = getTTable(TABLE2);
			// 所有数据放入主分区
			table
			.setFilter("(RX_KIND='UD' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='UD' AND #ACTIVE#='N'");
			break;
		case 2:
			// 出院带药
			table = getTTable(TABLE3);
			rx_no = this.getValueString("RX_NO");
			// 所有数据放入主分区
			table
			.setFilter("(RX_KIND='DS' AND HIDE_FLG = 'N' AND DC_DATE='' AND RX_NO='"
					+ rx_no + "') OR RX_KIND='DS' AND #ACTIVE#='N'");
			break;
		case 3:
			// 中药饮片
			table = getTTable(TABLE4);
			rx_no = this.getValueString("IG_RX_NO");
			ds
			.setFilter("(RX_KIND='IG' AND HIDE_FLG = 'N' AND DC_DATE=''  AND RX_NO='"
					+ rx_no + "') OR RX_KIND='IG' AND #ACTIVE#='N'");
			ds.filter();
			int totRow = ds.rowCount();
			if (!StringUtil.isNullString(ds.getItemString(totRow - 1,
					"ORDER_CODE"))
					|| totRow % 4 != 0 || totRow < 1) {
				for (int a = 0; a < 4 - totRow % 4; a++) {
					ds.setItem(a, "PHA_TYPE", "G");
				}
			}
			TParm dataparm = odiObject.getDS("ODI_ORDER").getBuffer(ds.PRIMARY);
			TParm tableParm = new TParm();
			for (int j = 0; j < dataparm.getCount(); j++) {
				int idx = j % 4 + 1;
				tableParm.setData("ORDER_DESC" + idx, 1, dataparm.getValue(
						"ORDER_DESC", j));
				tableParm.setData("MEDI_QTY" + idx, 1, dataparm.getDouble(
						"MEDI_QTY", j));
				tableParm.setData("DCTEXCEP_CODE" + idx, 1, dataparm.getValue(
						"DCTEXCEP_CODE", j));
			}
			tableParm.setCount(1);
			callFunction("UI|TABLE4|setParmValue", tableParm);
			odiObject.filter(table, ds, this.isStopBillFlg());
			break;
		}
		// 不为中药饮片时执行
		if (tab.getSelectedIndex() != 3) {
			table.filter();
			table.setDSValue();
		}
		this.onAddRow();
	}

	/**
	 * 进入临床路径时程
	 */
	public void intoDuration() {
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", caseNo);
		// ==liling 20140821 modify start====
		String clncPathCode = "";
		StringBuffer sqlbf = new StringBuffer();
		sqlbf.append("SELECT CLNCPATH_CODE  FROM  ADM_INP WHERE CASE_NO ='"
				+ caseNo
				+ "' AND DS_DATE IS NULL AND CANCEL_FLG = 'N' AND ROWNUM<2 ");
		// System.out.println("得到临床路径sql:"+sqlbf.toString());
		TParm parm = new TParm(TJDODBTool.getInstance()
				.select(sqlbf.toString()));
		if (parm.getCount("CLNCPATH_CODE") > 0) {
			clncPathCode = parm.getValue("CLNCPATH_CODE", 0);
		}
		if (clncPathCode.length() <= 0) {
			this.messageBox("请执行准入路径操作");
			return;
		}
		sendParm.setData("CLNCPATH_CODE", clncPathCode);
		// ==liling 20140821 modify end====
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\odi\\ODIintoDuration.x", sendParm);

	}

	/**
	 * 进入临床知识库
	 */
	public void onLook() {
		this.openDialog("%ROOT%\\config\\emr\\EMRUIcpl.x");

	}

	/**
	 * 病历浏览 ==========pangben modify 20110706
	 */
	public void onShow() {

		if (null == this.getValueString("MR_NO")
				|| this.getValueString("MR_NO").length() <= 0) {
			this.messageBox("请选择一个病人");
			return;
		}
		Runtime run = Runtime.getRuntime();
		try {
			// 得到当前使用的ip地址
			String ip = TIOM_AppServer.SOCKET
					.getServletPath("EMRWebInitServlet?Mr_No=");
			// 连接网页方法
			run.exec("IEXPLORE.EXE " + ip + this.getValueString("MR_NO"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 病历浏览
	 */
	public void onCxShow() {
		TParm result = queryPassword();
		String user_password = result.getValue("USER_PASSWORD", 0);
		String url = "http://" + getWebServicesIp() + "?userId="
				+ Operator.getID() + "&password=" + user_password + "&mrNo="
				+ this.getMrNo() + "&caseNo=" + getCaseNo();
		try {
			Runtime.getRuntime().exec(
					String.valueOf(String.valueOf((new StringBuffer(
							"cmd.exe /c start iexplore \"")).append(url)
							.append("\""))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private TParm queryPassword() {
		String sql = "SELECT USER_PASSWORD FROM SYS_OPERATOR WHERE USER_ID = '"
				+ Operator.getID() + "' AND REGION_CODE = '"
				+ Operator.getRegion() + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 获取配置文件
	 * 
	 * @author shendr
	 */
	public static TConfig getProp() {
		TConfig config = null;
		try {
			config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}

	/**
	 * 获取配置文件中的电子病历服务器IP
	 * 
	 * @return
	 */
	public static String getWebServicesIp() {
		TConfig config = getProp();
		String url = config.getString("", "EMRIP");
		return url;
	}

	// $$=============== 2012/03/13Modified by lx医保校检
	// START=========================$$//
	/**
	 * 医保医嘱校验
	 * 
	 * @param order_code
	 * @param ctzCode
	 * @return
	 */
	public boolean insOrderCheck(String orderCode, String ctzCode,
			String orderDesc) {
		boolean flg = true;
		// 调用
		TParm parm = INSTJTool.getInstance().orderCheck(orderCode,
				this.getCtzCode(), "I", "1");
		if (parm.getErrCode() < 0 && parm.getErrCode() != -1) {
			if (messageBox("提示信息 Tips", orderDesc + parm.getErrText()
			+ "是否开立? \n Are you Save?", this.YES_NO_OPTION) != 0) {
				return false;
			} else {
				return true;
			}
		}

		return flg;
	}

	public void onSubmitPDF() {
		this.openWindow("%ROOT%\\config\\ODI\\ODIDocQuery.x", (TParm) this
				.getParameter());
	}

	// $$=============== 2012/03/13Modified by lx医保校检
	// END=========================$$//

	// $$===========Add by lx 是否加值班医生Start===============$$//
	public boolean isDutyDr() {
		boolean falg = false;
		final String sql = "SELECT * FROM ODI_DUTYDRLIST WHERE DEPT_CODE='"
				+ this.getDeptCode() + "' AND DR_CODE='" + Operator.getID()
				+ "'";
		TParm parm = new TParm(this.getDBTool().select(sql));
		// System.out.println("====sql===="+sql);
		if (parm.getCount() > 0) {
			falg = true;
		}
		return falg;

	}

	/**
	 * 是否是立即使用
	 * 
	 * @param freqCode
	 * @return
	 */
	private boolean isStat(String freqCode) {
		// this.messageBox("freqCode"+freqCode);
		final String sql = "SELECT STAT_FLG FROM SYS_PHAFREQ"
				+ " WHERE FREQ_CODE='" + freqCode + "'";
		// System.out.println("==sql=="+sql);
		TParm parm = new TParm(this.getDBTool().select(sql));
		String f = parm.getValue("STAT_FLG", 0);
		if (f.equals("Y")) {
			return true;
		}
		return false;
	}

	// $$===========Add by lx 是否加值班医生End=================$$//
	// /**
	// * 发送HL7消息
	// * @param parm
	// */
	// public void sendHl7Messages(){
	// TParm parm=new TParm();
	// TDS ds = odiObject.getDS("ODI_ORDER");
	// String buffer = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
	// int newRow[] = ds.getNewRows(buffer);
	// int newCount = 0;
	// for (int i : newRow) {
	// if (!ds.isActive(i, buffer))
	// continue;
	// newCount++;
	// parm=ds.getRowParm(i, buffer);
	// parm.setData("ADM_TYPE", i, "I");
	// }
	// parm.setCount(newCount);
	// String type="NBW";
	// List list = new ArrayList();
	// list.add(parm);
	// // 调用接口
	// TParm resultParm =
	// Hl7Communications.getInstance().Hl7MessageCIS(list,type);
	// if (resultParm.getErrCode() < 0){
	// this.messageBox(resultParm.getErrText());
	// }else{
	// this.messageBox("发送HL7文件");
	// }
	// }

	/**
	 * 临床路径准入 ================pangben 2012-6-7
	 */
	public void onClpManageM() {
		TParm parm = new TParm();
		parm.setData("CLP", "CASE_NO", caseNo);
		parm.setData("CLP", "MR_NO", mrNo);
		parm.setData("CLP", "FLG", "Y");
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\clp\\CLPManagem.x", parm);
	}

	/**
	 * 变异分析 ================pangben 2012-6-12
	 */
	public void onClpVariation() {
		// ========pangben 2012-06 -23重新获得临床路径代码
		String clncPathCode = this.getValueString("CLNCPATH_CODE");
		if (null == clncPathCode || clncPathCode.length() <= 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO='"
					+ caseNo + "'");
			TParm result = new TParm(TJDODBTool.getInstance().select(
					sql.toString()));
			if (result.getCount("CLNCPATH_CODE") > 0) {
				clncPathCode = result.getValue("CLNCPATH_CODE", 0);
			}
		}
		if (clncPathCode.length() <= 0) {
			this.messageBox("请执行准入路径操作");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CLP", "CASE_NO", caseNo);
		parm.setData("CLP", "CLNCPATH_CODE", clncPathCode);
		parm.setData("CLP", "FLG", "Y");
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\clp\\CLPVariation.x", parm);
	}

	/**
	 * 费用时程修改================xiongwg 2015-4-26
	 */
	public void onClpOrderReSchdCode() {
		String clncPathCode = this.getValueString("CLNCPATH_CODE");
		if (null == clncPathCode || clncPathCode.length() <= 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO='"
					+ caseNo + "'");
			TParm result = new TParm(TJDODBTool.getInstance().select(
					sql.toString()));
			if (result.getCount("CLNCPATH_CODE") > 0) {
				clncPathCode = result.getValue("CLNCPATH_CODE", 0);
			}
		}
		if (clncPathCode.length() <= 0) {
			this.messageBox("请执行准入路径操作");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CLP", "CASE_NO", caseNo);
		parm.setData("CLP", "MR_NO", mrNo);
		parm.setData("CLP", "FLG", "Y");
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\clp\\CLPOrderReplaceSchdCode.x", parm);
	}

	/**
	 * 检查抗菌药物是否填写已经填写抗菌标示
	 * 
	 * @param obj
	 */
	public String checkAntiBioticWayisFilled(OdiObject obj) { // add by wanglong
		// 20121210
		String result = "";
		TDS ds = obj.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		int newRow[] = ds.getNewRows(buff);// 新加的数据
		// System.out.println("buff  ppppp新增的数据 是：："+buff);
		for (int i : newRow) {
			// System.out.println("" +
			// "ds.isActive(i, buff)：："+i+ds.isActive(i, buff));
			if (!ds.isActive(i, buff))
				continue;
			String order_code = ds.getRowParm(i, buff).getValue("ORDER_CODE");
			String order_desc = ds.getRowParm(i, buff).getValue("ORDER_DESC");
			String antibiotic_code = ds.getRowParm(i, buff).getValue(
					"ANTIBIOTIC_CODE");
			String antibiotic_way = ds.getRowParm(i, buff).getValue(
					"ANTIBIOTIC_WAY");
			// System.out.println("order_code order_code新增的数据 是：："+order_code);
			if ((!antibiotic_code.equals("")) && antibiotic_way.equals("")) {
				result += ds.getRowParm(i, buff).getValue("ORDER_DESC") + ",";
				ds.deleteRow(i, buff);
			}
		}
		int modifRow[] = ds.getOnlyModifiedRows(buff);// 修改的数据
		for (int i : modifRow) {
			if (!ds.isActive(i, buff))
				continue;
			String order_code = ds.getRowParm(i, buff).getValue("ORDER_CODE");
			String order_desc = ds.getRowParm(i, buff).getValue("ORDER_DESC");
			String antibiotic_code = ds.getRowParm(i, buff).getValue(
					"ANTIBIOTIC_CODE");
			String antibiotic_way = ds.getRowParm(i, buff).getValue(
					"ANTIBIOTIC_WAY");
			if ((!antibiotic_code.equals("")) && antibiotic_way.equals("")) {
				result += ds.getRowParm(i, buff).getValue("ORDER_DESC") + ",";
				ds.deleteRow(i, buff);
			}
		}
		if (result.length() > 0)
			result = result.substring(0, result.length() - 1);
		return result;
	}

	/**
	 * 越级审批
	 * 
	 * @param obj
	 *            yanjing 20140304
	 */
	public void isOverRideCheck(OdiObject obj) {
		String result = "";
		TDS ds = obj.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		int newRow[] = ds.getNewRows(buff);// 新加的数据
		List<Integer> rowDelAttribute = new ArrayList<Integer>();
		Map rowDels = new HashMap();
		for (int i : newRow) {
			if (!ds.isActive(i, buff))
				continue;
			String iOrder_code = ds.getRowParm(i, buff).getValue("ORDER_CODE");
			String order_desc = ds.getRowParm(i, buff).getValue("ORDER_DESC");
			String linkMainFlg = ds.getRowParm(i, buff)
					.getValue("LINKMAIN_FLG");
			String linkNo = ds.getRowParm(i, buff).getValue("LINK_NO");
			String iAntiCode = ds.getRowParm(i, buff).getValue(
					"ANTIBIOTIC_CODE");
			if (linkMainFlg.equals("Y")) {// 主项时
				boolean antiFlg = false;// false:该主项对应的细项中不含有抗菌药物
				// 循环校验每组医嘱中是否含有抗菌药物
				// for(int j : newRow){
				for (int m : newRow) {// 循环校验细项是否含有抗菌药物(包括连组号相同的主项)
					TParm meTemp = ds.getRowParm(m, buff);
					String mLinkNo = meTemp.getData("LINK_NO").toString();
					if (linkNo.equals(mLinkNo)
							&& (!"".equals(meTemp.getData("ANTIBIOTIC_CODE")
									.toString()) && !meTemp.getData(
											"ANTIBIOTIC_CODE").toString().equals(null))) {
						antiFlg = true;// 细项中含有抗菌药物标记
					}
				}
				if (antiFlg) {// 该组连嘱中含有抗菌药物
					for (int j : newRow) {// 循环校验是否有证照
						TParm reTemp = ds.getRowParm(j, buff);
						String LlinkNo = reTemp.getValue("LINK_NO");
						String antiCode = reTemp.getValue("ANTIBIOTIC_CODE");
						String order_code = reTemp.getValue("ORDER_CODE");
						if (LlinkNo.equals(linkNo)
								&& (!"".equals(antiCode) && !antiCode
										.equals(null))) {// 循环判断每组的抗菌药物是否有证照
							// 根据order_code查询lcs_class_code
							String selLcsCode = "SELECT LCS_CLASS_CODE FROM SYS_FEE WHERE ORDER_CODE = '"
									+ order_code + "'";
							TParm lcsCodeParm = new TParm(this.getDBTool()
									.select(selLcsCode));
							String lcsString = lcsCodeParm.getValue(
									"LCS_CLASS_CODE", 0);

							// 拿到使用者的证照列表
							TParm lcsParm = new TParm(
									this
									.getDBTool()
									.select(
											"SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
													+ Operator.getID()
													+ "' AND SYSDATE BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
							if (!lcsString.equals("")
									&& !OrderUtil.getInstance()
									.checkLcsClassCode(lcsParm,
											Operator.getID(),
											"" + lcsString)) {

								TParm orderParm = ds.getRowParm(i, buff);
								// 是否是集合医嘱
								if (this.isOrderSet(orderParm)) {
									// this.messageBox_("删除集合医嘱细项");
									// 删除集合医嘱细项
									this.delOrderSetList(orderParm, "NEW");
									// return;
								}
								// 是否是连结医嘱
								if (this.isLinkOrder(orderParm)) {
									// this.messageBox_("删除连结医嘱");
									// 删除连结医嘱
									// this.delLinkOrder(orderParm, "NEW");
									int newRowtemp[] = ds.getNewRows(buff);
									for (int n : newRowtemp) {
										TParm temp = ds.getRowParm(n, buff);
										// System.out.println("i:"+i);
										if (temp.getInt("LINK_NO") == orderParm
												.getInt("LINK_NO")
												&& temp
												.getValue("RX_KIND")
												.equals(
														orderParm
														.getValue("RX_KIND"))) {
											int delId = (Integer) ds
													.getItemData(i, "#ID#",
															buff);
											rowDelAttribute.add(delId);
											rowDels.put(delId, i);
										}
									}
								}
							}
						}

					}
				}
				// }
			} else if (("".equals(linkNo) || linkNo.equals(null))
					&& (!"".equals(iAntiCode) && !iAntiCode.equals(null))) {// 向odi_order表中写数据时，删除非连嘱没有权限的抗菌药物
				// 循环判断每组的抗菌药物是否有证照
				// 根据order_code查询lcs_class_code
				// this.messageBox("0000000000000");
				String selLcsCode = "SELECT LCS_CLASS_CODE FROM SYS_FEE WHERE ORDER_CODE = '"
						+ iOrder_code + "'";
				TParm lcsCodeParm = new TParm(this.getDBTool().select(
						selLcsCode));
				String lcsString = lcsCodeParm.getValue("LCS_CLASS_CODE", 0);

				// 拿到使用者的证照列表
				TParm lcsParm = new TParm(
						this
						.getDBTool()
						.select(
								"SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
										+ Operator.getID()
										+ "' AND SYSDATE BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
				if (!lcsString.equals("")
						&& !OrderUtil.getInstance().checkLcsClassCode(lcsParm,
								Operator.getID(), "" + lcsString)) {
					TParm orderParm = ds.getRowParm(i, buff);
					// 是否是集合医嘱
					if (this.isOrderSet(orderParm)) {
						// this.messageBox_("删除集合医嘱细项");
						// 删除集合医嘱细项
						this.delOrderSetList(orderParm, "NEW");
						// return;
					}
					int delId = (Integer) ds.getItemData(i, "#ID#", buff);
					rowDelAttribute.add(delId);
					rowDels.put(delId, i);
				}
			}
		}

		for (int j = rowDelAttribute.size() - 1; j >= 0; j--) {
			TParm parm = ds.getRowParm(rowDelAttribute.get(j), buff);
			String orderDesc = parm.getValue("ORDER_DESC");
			this.messageBox("您没有" + orderDesc + "证照,已删除！");
			ds.setAttribute("DELROW", rowDelAttribute.get(j));
			odiObject.deleteRow(ds, (Integer) rowDels.get(rowDelAttribute
					.get(j)));
		}
	}

	/**
	 * 检核医生是否有权限,重整TParm
	 * 
	 * @param obj
	 *            yanjing 20140305
	 */
	public TParm reCheckAnti(TParm parm) {
		// System.out.println("看看入参parm 的值 parm parm parm  is：：："+parm);
		TParm result = new TParm();
		int rowCount = 0;
		String sktSql = "";
		TParm sktParm = null;
		for (int i = 0; i < parm.getCount(); i++) {
			// 添加校验皮试阳性提示
			sktSql = "SELECT SKINTEST_NOTE FROM PHA_ANTI WHERE CASE_NO= '"
					+ caseNo + "' AND ORDER_CODE='"
					+ parm.getValue("ORDER_CODE", i) + "' "
					+ " AND ROUTE_CODE = 'PS'  AND SKINTEST_NOTE IS NOT NULL ";
			sktParm = new TParm(TJDODBTool.getInstance().select(sktSql));
			if (sktParm.getCount() > 0) {
				if (sktParm.getValue("SKINTEST_NOTE", 0).equals("1")) {
					if (this.messageBox("提示", "此药品皮试结果为阳性,是否继续", 2) != 0) {
						result.setErr(-1, "取消保存");
						return result;
					}
				}
			}
		}
		// 添加医生证照的校验
		for (int i = 0; i < parm.getCount(); i++) {
			String linkNo = parm.getValue("LINK_NO", i);
			String anti = parm.getValue("ANTIBIOTIC_CODE", i);
			if (parm.getData("LINKMAIN_FLG", i).equals("Y")) {// 医嘱为主项时
				boolean onSaveFlg = false;// 同一个连嘱号的非抗菌药物是否保存过。false：为未保存过。
				if ("".equals(parm.getValue("DC_DATE", i))
						|| parm.getValue("DC_DATE", i).equals(null)) {
					// this.messageBox("连嘱主项的停用时间不能为空。");
					result.setErr(-1, "连嘱主项的停用时间不能为空。");
					return result;

				}
				for (int m = 0; m < parm.getCount(); m++) {// 循环校验连嘱号相同的医嘱
					String dLinkNo = parm.getData("LINK_NO", m).toString();
					String antiCode = parm.getData("ANTIBIOTIC_CODE", m)
							.toString();
					if (linkNo.equals(dLinkNo)
							&& (!"".equals(antiCode) && !antiCode.equals(null))) {// 连嘱号相同并且为抗菌药物时校验抗菌药物的证照
						String orderCode = parm.getValue("ORDER_CODE", m);
						// 根据order_code查询lcs_class_code
						String selLcsCode = "SELECT LCS_CLASS_CODE FROM SYS_FEE WHERE ORDER_CODE = '"
								+ orderCode + "'";
						TParm lcsCodeParm = new TParm(TJDODBTool.getInstance()
								.select(selLcsCode));
						String lcsString = lcsCodeParm.getValue(
								"LCS_CLASS_CODE", 0);
						// 拿到使用者的证照列表
						TParm lcsParm = new TParm(
								TJDODBTool
								.getInstance()
								.select(
										"SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
												+ Operator.getID()
												+ "' AND SYSDATE BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
						if (!lcsString.equals("")
								&& !OrderUtil.getInstance().checkLcsClassCode(
										lcsParm, Operator.getID(),
										"" + lcsString)) {
							lcsParm.setErrCode(-1);
							// 您没有此医嘱的证照！
							lcsParm.setErrText("E0166");
							String orderDesc = parm.getData("ORDER_DESC", m)
									.toString();
							if (this
									.messageBox("提示信息 Tips", "没有权限开立"
											+ orderDesc + ",是否越权? ",
											this.YES_NO_OPTION) != 0) {// 否，不越级,不向parm中添加数据
							} else {// 向parm中添加数据
								for (int j = 0; j < parm.getCount(); j++) {
									String rLinkNo = parm.getData("LINK_NO", j)
											.toString();
									String rAntiCode = parm.getData(
											"ANTIBIOTIC_CODE", j).toString();
									if (!onSaveFlg
											&& linkNo.equals(rLinkNo)
											&& (rAntiCode.equals(null) || ""
													.equals(rAntiCode))) {// 将连嘱中的非抗菌药物写进pha_anti表
										result.addData("LINKMAIN_FLG", parm
												.getData("LINKMAIN_FLG", j));// yanjing
										// 连嘱标记
										// 20140310
										result.addData("LINK_NO", parm.getData(
												"LINK_NO", j));// yanjing 组号标记
										// 20140310
										result.addData("ANTI_FLG", "N");// YANJING
										// 20140619
										// 抗菌标记
										result.addData("OVERRIDE_FLG", "Y");// YANJING
										// 20140311
										// 是否越级审批的标记
										result.addData("TAKE_DAYS", parm
												.getInt("TAKE_DAYS", j));
										result.addData("ANTI_TAKE_DAYS", parm
												.getInt("TAKE_DAYS", j));
										result.addData("ORDER_DATE", parm
												.getData("ORDER_DATE", j));
										result.addData("ORDER_CODE", parm
												.getData("ORDER_CODE", j));
										result.addData("ORDER_DESC", parm
												.getData("ORDER_DESC", j));
										result.addData("MR_CODE", parm.getData(
												"MR_CODE", j));
										result.addData("OPTITEM_CODE", parm
												.getData("OPTITEM_CODE", j));
										result.addData("REQUEST_NO", parm
												.getData("REQUEST_NO"));
										result.addData("SPECIFICATION", parm
												.getData("SPECIFICATION", j));// 规格
										result.addData("MEDI_UNIT", parm
												.getData("MEDI_UNIT", j));// 开药单位
										result.addData("MEDI_QTY", parm
												.getData("MEDI_QTY", j));// 开药量
										result.addData("FREQ_CODE", parm
												.getData("FREQ_CODE", j));// 频次
										result.addData("ROUTE_CODE", parm
												.getData("ROUTE_CODE", j));// 用法
										rowCount++;
									}
								}
							}
						} else {
							onSaveFlg = true;// 标记该组中的非抗菌药物已经保存
							// 将该项抗菌药物写进pha_anti表中
							result.addData("LINKMAIN_FLG", parm.getData(
									"LINKMAIN_FLG", m));// yanjing 连嘱标记 20140310
							result.addData("LINK_NO", parm
									.getData("LINK_NO", m));// yanjing 组号标记
							// 20140310
							result.addData("ANTI_FLG", "Y");// YANJING 20140619
							// 抗菌标记
							result.addData("OVERRIDE_FLG", "Y");// YANJING
							// 20140311
							// 是否越级审批的标记
							result.addData("TAKE_DAYS", parm.getInt(
									"TAKE_DAYS", m));
							result.addData("ANTI_TAKE_DAYS", parm.getInt(
									"TAKE_DAYS", m));
							result.addData("ORDER_DATE", parm.getData(
									"ORDER_DATE", m));
							result.addData("ORDER_CODE", parm.getData(
									"ORDER_CODE", m));
							result.addData("ORDER_DESC", parm.getData(
									"ORDER_DESC", m));
							result.addData("MR_CODE", parm
									.getData("MR_CODE", m));
							result.addData("OPTITEM_CODE", parm.getData(
									"OPTITEM_CODE", m));
							result.addData("REQUEST_NO", parm
									.getData("REQUEST_NO"));
							result.addData("SPECIFICATION", parm.getData(
									"SPECIFICATION", m));// 规格
							result.addData("MEDI_UNIT", parm.getData(
									"MEDI_UNIT", m));// 开药单位
							result.addData("MEDI_QTY", parm.getData("MEDI_QTY",
									m));// 开药量
							result.addData("FREQ_CODE", parm.getData(
									"FREQ_CODE", m));// 频次
							result.addData("ROUTE_CODE", parm.getData(
									"ROUTE_CODE", m));// 用法
							rowCount++;
						}
					}
				}
			} else if ("".equals(linkNo) && !"".equals(anti)) {// 保存非连嘱的抗菌药物
				String orderCode = parm.getValue("ORDER_CODE", i);
				// 根据order_code查询lcs_class_code
				String selLcsCode = "SELECT LCS_CLASS_CODE FROM SYS_FEE WHERE ORDER_CODE = '"
						+ orderCode + "'";
				TParm lcsCodeParm = new TParm(TJDODBTool.getInstance().select(
						selLcsCode));
				String lcsString = lcsCodeParm.getValue("LCS_CLASS_CODE", 0);
				// 拿到使用者的证照列表
				TParm lcsParm = new TParm(
						TJDODBTool
						.getInstance()
						.select(
								"SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
										+ Operator.getID()
										+ "' AND SYSDATE BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
				if (!lcsString.equals("")
						&& !OrderUtil.getInstance().checkLcsClassCode(lcsParm,
								Operator.getID(), "" + lcsString)) {
					lcsParm.setErrCode(-1);
					// 您没有此医嘱的证照！
					lcsParm.setErrText("E0166");
					String orderDesc = parm.getData("ORDER_DESC", i).toString();
					if (this.messageBox("提示信息 Tips", "没有权限开立" + orderDesc
							+ ",是否越权? ", this.YES_NO_OPTION) != 0) {// 否，不越级,不向parm中添加数据
					} else {
						result.addData("LINKMAIN_FLG", parm.getData(
								"LINKMAIN_FLG", i));// yanjing 连嘱标记 20140310
						result.addData("LINK_NO", parm.getData("LINK_NO", i));// yanjing
						// 组号标记
						// 20140310
						result.addData("ANTI_FLG", "Y");// YANJING 20140619 抗菌标记
						result.addData("OVERRIDE_FLG", "Y");// YANJING 20140311
						// 是否越级审批的标记
						result
						.addData("TAKE_DAYS", parm.getInt("TAKE_DAYS",
								i));
						result.addData("ANTI_TAKE_DAYS", parm.getInt(
								"TAKE_DAYS", i));
						result.addData("ORDER_DATE", parm.getData("ORDER_DATE",
								i));
						result.addData("ORDER_CODE", parm.getData("ORDER_CODE",
								i));
						result.addData("ORDER_DESC", parm.getData("ORDER_DESC",
								i));
						result.addData("MR_CODE", parm.getData("MR_CODE", i));
						result.addData("OPTITEM_CODE", parm.getData(
								"OPTITEM_CODE", i));
						result
						.addData("REQUEST_NO", parm
								.getData("REQUEST_NO"));
						result.addData("SPECIFICATION", parm.getData(
								"SPECIFICATION", i));// 规格
						result.addData("MEDI_UNIT", parm
								.getData("MEDI_UNIT", i));// 开药单位
						result.addData("MEDI_QTY", parm.getData("MEDI_QTY", i));// 开药量
						result.addData("FREQ_CODE", parm
								.getData("FREQ_CODE", i));// 频次
						result.addData("ROUTE_CODE", parm.getData("ROUTE_CODE",
								i));// 用法
						rowCount++;
					}
				} else {
					result.addData("LINKMAIN_FLG", parm.getData("LINKMAIN_FLG",
							i));// yanjing 连嘱标记 20140310
					result.addData("LINK_NO", parm.getData("LINK_NO", i));// yanjing
					// 组号标记
					// 20140310
					result.addData("ANTI_FLG", "Y");// YANJING 20140619 抗菌标记
					result.addData("OVERRIDE_FLG", "Y");// YANJING 20140311
					// 是否越级审批的标记
					result.addData("TAKE_DAYS", parm.getInt("TAKE_DAYS", i));
					result.addData("ANTI_TAKE_DAYS", parm
							.getInt("TAKE_DAYS", i));
					result.addData("ORDER_DATE", parm.getData("ORDER_DATE", i));
					result.addData("ORDER_CODE", parm.getData("ORDER_CODE", i));
					result.addData("ORDER_DESC", parm.getData("ORDER_DESC", i));
					result.addData("MR_CODE", parm.getData("MR_CODE", i));
					result.addData("OPTITEM_CODE", parm.getData("OPTITEM_CODE",
							i));
					result.addData("REQUEST_NO", parm.getData("REQUEST_NO"));
					result.addData("SPECIFICATION", parm.getData(
							"SPECIFICATION", i));// 规格
					result.addData("MEDI_UNIT", parm.getData("MEDI_UNIT", i));// 开药单位
					result.addData("MEDI_QTY", parm.getData("MEDI_QTY", i));// 开药量
					result.addData("FREQ_CODE", parm.getData("FREQ_CODE", i));// 频次
					result.addData("ROUTE_CODE", parm.getData("ROUTE_CODE", i));// 用法
					rowCount++;
				}
			}
		}
		result.setData("ACTION", "COUNT", rowCount);
		return result;
	}

	/**
	 * “合并”按钮事件
	 */
	public void onMerge() {// add by wanglong 20121025
		TParm param = new TParm();
		param.setData("MR_NO", getMrNo());
		param.setData("CASE_NO", getCaseNo());
		Object obj = this.openDialog("%ROOT%\\config\\clp\\CLPPatInfoUI.x",
				param);
		if (obj != null) {
			TParm acceptParm = (TParm) obj;
			String caseNo_old = acceptParm.getValue("CASE_NO");
			String diseCode = acceptParm.getValue("DISE_CODE");
			if (!caseNo_old.trim().equals("")) {
				TParm action = new TParm();
				action.setData("CASE_NO", caseNo);
				action.setData("CASE_NO_OLD", caseNo_old);
				// 单病种历史病历合并
				TParm result = TIOM_AppServer.executeAction(
						"action.clp.CLPSingleDiseAction", "mergeEMRhistory",
						action);
				if (result.getErrCode() < 0) {
					this.messageBox("合并失败，" + result.getErrText());
				} else {
					this.messageBox("合并成功");
				}
			}
		}
	}

	/**
	 * 单病种准入
	 */
	public void onSingleDise() {// add by wanglong 20121025
		TParm action = new TParm();
		action.setData("ADM_TYPE", "I");// 住院
		// System.out.println("============★就诊号==============="+caseNo);
		action.setData("CASE_NO", caseNo);// 就诊号
		this.openWindow("%ROOT%\\config\\clp\\CLPSingleDise.x",// 调用其他窗体查询CASE_NO
				action);
	}

	/**
	 * 合理用药--药品信息查询
	 */
	public void onQueryRationalDrugUse() {// add by wanglong 20130522
		if (!passIsReady) {
			messageBox("合理用药未启用");
			return;
		}
		if (!PassTool.getInstance().init()) {
			this.messageBox("合理用药初始化失败，此功能不能使用！");
			return;
		}
		int tabbedIndex = ((TTabbedPane) this.getComponent("TABLEPANE"))
				.getSelectedIndex();
		int row = -1;
		String orderCode = "";
		switch (tabbedIndex) {
		case 0:
			row = this.getTTable("TABLE" + (tabbedIndex + 1)).getSelectedRow();
			orderCode = this.getTTable("TABLE" + (tabbedIndex + 1))
					.getDataStore().getItemString(row, "ORDER_CODE");
			break;
		case 1:
			row = this.getTTable("TABLE" + (tabbedIndex + 1)).getSelectedRow();
			orderCode = this.getTTable("TABLE" + (tabbedIndex + 1))
					.getDataStore().getItemString(row, "ORDER_CODE");
			break;
		case 2:
			row = this.getTTable("TABLE" + (tabbedIndex + 1)).getSelectedRow();
			orderCode = this.getTTable("TABLE" + (tabbedIndex + 1))
					.getDataStore().getItemString(row, "ORDER_CODE");
			break;
		case 3:
			row = this.getTTable("TABLE" + (tabbedIndex + 1)).getSelectedRow();
			orderCode = this.getTTable("TABLE" + (tabbedIndex + 1))
					.getDataStore().getItemString(row, "ORDER_CODE");
			break;
		}
		if (row < 0) {
			return;
		}
		String value = (String) this
				.openDialog("%ROOT%\\config\\pha\\PHAOptChoose.x");
		if (value == null || value.length() == 0) {
			return;
		}
		int conmmand = Integer.parseInt(value);
		if (conmmand != 6) {
			PassTool.getInstance().setQueryDrug(orderCode, conmmand);
		} else {
			PassTool.getInstance().setWarnDrug2("", "");
		}
	}

	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		final TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (getRadioSelType(tab.getSelectedIndex()) != 5)
					return;
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
				TParm tableData = table.getParmValue();
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
				String tblColumnName = table.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);

				// getTMenuItem("save").setEnabled(false);
			}
		});
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
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, TTable table) {
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
		table.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

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

	// =================== chenxi modify 去床号
	private TParm getBedIpdNo(String caseNo) {
		TParm result = new TParm();
		String sql = "SELECT BED_NO,IPD_NO,BED_NO_DESC FROM  SYS_BED WHERE CASE_NO = '"
				+ caseNo + "' AND BED_OCCU_FLG = 'N' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() < 0) {
			result.setData("BED_NO", "");
			result.setData("IPD_NO", "");
		} else {
			result.setData("BED_NO", parm.getValue("BED_NO", 0));
			result.setData("IPD_NO", parm.getValue("IPD_NO", 0));
		}
		return result;
	}

	/**
	 * 表格单击事件 yanjing 20131119 添加状态栏显示
	 */
	public void onTableClick(String tag) {
		TTable table = (TTable) this.getComponent(tag);
		int row = table.getSelectedRow();
		TDS ds = odiObject.getDS("ODI_ORDER");
		TParm dataparm = odiObject.getDS("ODI_ORDER").getBuffer(ds.PRIMARY);
		String orderCode = dataparm.getValue("ORDER_CODE", row);
		String sql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC,"
				+ "DESCRIPTION,SPECIFICATION,REMARK_1,REMARK_2,DRUG_NOTES_DR FROM SYS_FEE"
				+ " WHERE ORDER_CODE = '" + orderCode + "'";
		TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
		callFunction("UI|setSysStatus", sqlparm.getValue("ORDER_CODE", 0) + " "
				+ sqlparm.getValue("ORDER_DESC", 0) + " "
				+ sqlparm.getValue("GOODS_DESC", 0) + " "
				+ sqlparm.getValue("DESCRIPTION", 0) + " "
				+ sqlparm.getValue("SPECIFICATION", 0) + " "
				+ sqlparm.getValue("REMARK_1", 0) + " "
				+ sqlparm.getValue("REMARK_2", 0) + " "
				+ sqlparm.getValue("DRUG_NOTES_DR", 0));
	}

	/**
	 * 抗菌药物取消连嘱时，清空停用时间、停用医生、停用科室及抗菌类型 yanjing 20140217
	 */
	private void onClearLinkAnti(String orderCode, TParm parm) {
		// 根据医嘱代码查询该医嘱是否为抗菌药物
		String selectAnti = "SELECT ANTIBIOTIC_CODE FROM PHA_BASE "
				+ "WHERE ORDER_CODE = '" + orderCode + "'";
		TParm antiParm = new TParm(TJDODBTool.getInstance().select(selectAnti));
		if ("".equals(antiParm.getValue("ANTIBIOTIC_CODE", 0))
				|| antiParm.getValue("ANTIBIOTIC_CODE", 0).equals(null)) {
			parm.setData("DC_DATE", "");
			parm.setData("ANTIBIOTIC_WAY", "");
			parm.setData("DC_DR_CODE", "");
			parm.setData("DC_DEPT_CODE", "");// ====yanjing 20140901 停用科室
		}
	}

	public void onCdssCal() {
		odiStationDrools.onCdssCal(false);
	}

	// add by yangjj 20150410 判断是否为配血医嘱，是配血医嘱为true，否则为false
	private boolean checkBMSOrder(String orderCode) {
		String sql = getCheckBMSOrderSql(orderCode);
		TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql));
		boolean b = false;
		if (!"".equals(sqlParm.getValue("EXEC_PRG", 0))) {
			b = true;
		}
		return b;
	}

	private String getCheckBMSOrderSql(String orderCode) {
		String sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE = '" + orderCode
				+ "'";
		return sql;
	}

	private String getEXECPRGSql(String orderCode) {
		String sql = "SELECT EXEC_PRG FROM SYS_FEE WHERE ORDER_CODE = '"
				+ orderCode + "'";
		return sql;
	}

	/**
	 * 药品过敏选择 add by huangtt 20150507
	 */
	public void selPhaClass() {
		TComboBox type = (TComboBox) this.getComponent("PHA_CLASS");
		if (TCM_Transform.getBoolean(this.getValue("PHA_DRUGALLERGY"))) {
			type.setVisible(true);
			setValue("PHA_CLASS", "B");
			// onAllg(this.getValueString("PHA_CLASS"));
			onSelectRadio(1);
		} else {
			setValue("PHA_CLASS", "");
			type.setVisible(false);
		}
	}

	/**
	 * 药品过敏中的分类选择 add by huangtt 20150507
	 */
	public void onSelg() {
		onSelectRadio(1);
	}

	/**
	 * 条码打印
	 */
	public void onMedApplyPrint() {// wanglong add 20150520
		TParm outsideParm = this.getInputParm();
		if (outsideParm == null) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", "I");
		parm.setData("MR_NO", outsideParm.getValue("ODI", "MR_NO"));
		parm.setData("CASE_NO", outsideParm.getValue("ODI", "CASE_NO"));
		parm.setData("IPD_NO", outsideParm.getValue("ODI", "IPD_NO"));
		parm.setData("PAT_NAME", outsideParm.getValue("ODI", "PAT_NAME"));
		parm.setData("BED_NO", outsideParm.getValue("ODI", "BED_NO"));
		parm.setData("DEPT_CODE", outsideParm.getValue("ODI", "DEPT_CODE"));
		parm.setData("STATION_CODE", outsideParm
				.getValue("ODI", "STATION_CODE"));
		parm.setData("ADM_DATE", outsideParm.getData("ODI", "ADM_DATE"));
		parm.setData("POPEDEM", "1");
		this.openDialog("%ROOT%\\config\\med\\MEDApply.x", parm);
	}

	/**
	 * 
	 * @Title: onClpPack
	 * @Description: TODO(临床路径套餐)
	 * @author pangben 2015-8-13
	 * @throws
	 */
	public void onClpPack() {
		TParm parm = new TParm();
		String clncPathCode = "";
		StringBuffer sql = new StringBuffer();
		sql
		.append("SELECT CLNCPATH_CODE,SCHD_CODE FROM ADM_INP WHERE CASE_NO='"
				+ caseNo + "'");
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		if (result.getCount("CLNCPATH_CODE") > 0) {
			clncPathCode = result.getValue("CLNCPATH_CODE", 0);
		}
		// if (clncPathCode.length() <= 0) {
		// this.messageBox("请操作路径准入");
		// return;
		// }
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() > 3) {
			this.messageBox("E0178");
			return;
		}
		String tag = "";
		switch (tab.getSelectedIndex()) {
		// 临时0：补充计价。1：西药。2：管制药品。3：中药饮片。4：诊疗项目。5：检验检查
		case 0:
			parm.setData("RX_TYPE", "1,2,4,5");
			tag = "ST";
			break;
			// 长期
		case 1:
			parm.setData("RX_TYPE", "1,2,4");
			tag = "UD";
			break;
			// 出院带药
		case 2:
			parm.setData("RX_TYPE", "1,2,3");
			tag = "DS";
			break;
			// 中药
		case 3:
			parm.setData("RX_TYPE", "3");
			tag = "IG";
			break;
		}
		TParm inParm = new TParm();
		// 得到当前时程
		String schdCode = "";
		if (result.getCount("SCHD_CODE") > 0) {
			schdCode = result.getValue("SCHD_CODE", 0);
		}
		inParm.setData("SELECT_INDEX", tab.getSelectedIndex());
		inParm.setData("CLNCPATH_CODE", clncPathCode);
		inParm.setData("SCHD_CODE", schdCode);
		inParm.setData("CASE_NO", caseNo);
		inParm.setData("MR_NO", mrNo);
		result = (TParm) this.openDialog(
				"%ROOT%\\config\\clp\\CLPPackAgeOrder.x", inParm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		yyList = true;
		int rowCount = result.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) {
			TParm OrderParm = OdiUtil.getInstance().getSysFeeAndclp(
					result.getValue("ORDER_CODE", i),
					result.getValue("CLNCPATH_CODE", i),
					result.getValue("SCHD_CODE", i),
					result.getValue("CHKTYPE_CODE", i),
					result.getValue("ORDER_SEQ_NO", i),
					result.getValue("ORDER_TYPE", i));
			OrderParm.setData("CLP_FLG", "Y");
			OrderParm.setData("EXID_ROW", getExitRow());
			this.popReturn(tag, OrderParm);
		}
		yyList = false;
	}

	/**
	 * 院前TABLE编辑时的响应
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentPA(Component com, int row, int column) {
		// 状态条显示
		callFunction("UI|setSysStatus", "");
		// 当前编辑行
		this.rowOnly = row;
		// 拿到列名
		String columnName = this.getFactColumnName(TABLE6, column);
		if (!"ORDER_DESCCHN".equals(columnName)) {
			return;
		}
		TTable table = getTTable(TABLE6);
		int selRow = this.getTTable(TABLE6).getSelectedRow();
		TParm existParm = this.getTTable(TABLE6).getDataStore().getRowParm(
				selRow);
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();

		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// 设置弹出菜单
		textFilter.setPopupMenuParameter("PA", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}

	/**
	 * 返回院前用药医令的设置
	 * 
	 * @return TParm
	 */
	private TParm returnPAData(TParm parm, int row) {
		TParm result = new TParm();
		// 拿到相关信息
		TParm actionParm = this.getPhaBaseData(parm.getData("ORDER_CODE")
				.toString(), parm.getData("CAT1_TYPE").toString(), "PA", parm);
		// 区域代码
		result.setData("REGION_CODE", Operator.getRegion());
		// 病区代码
		result.setData("STATION_CODE", this.getStationCode());
		// 科室
		result.setData("DEPT_CODE", this.getDeptCode());
		// 经治医师
		result.setData("VS_DR_CODE", ((TParm) this.getParameter()).getData(
				"ODI", "VS_DR_CODE").toString());
		// 床位号
		result.setData("BED_NO", this.getBedIpdNo(caseNo).getValue("BED_NO"));
		// 住院号
		result.setData("IPD_NO", this.getBedIpdNo(caseNo).getValue("IPD_NO"));
		// 病案号
		result.setData("MR_NO", ((TParm) this.getParameter()).getData("ODI",
				"MR_NO").toString());
		// 暂存注记
		result.setData("TEMPORARY_FLG", "N");
		// 医嘱状态
		result.setData("ORDER_STATE", "N");
		// 连接医嘱主项
		result.setData("LINKMAIN_FLG", "N");
		// 连接医嘱
		result.setData("LINK_NO", "");
		// 医嘱代码
		result.setData("ORDER_CODE", parm.getData("ORDER_CODE"));
		// 医嘱名称
		result.setData("ORDER_DESC", parm.getData("ORDER_DESC").toString());
		// 医嘱名称（显示）
		result.setData("ORDER_DESCCHN", parm.getValue("ORDER_DESC")
				+ parm.getValue("GOODS_DESC") + parm.getValue("DESCRIPTION")
				+ parm.getValue("SPECIFICATION"));
		// 商品名
		result.setData("GOODS_DESC", parm.getData("GOODS_DESC"));
		// 规格
		result.setData("SPECIFICATION", parm.getData("SPECIFICATION"));
		// 英文名称
		result.setData("ORDER_ENG_DESC", parm.getData("TRADE_ENG_DESC"));
		// 开药数量
		result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
		// 开药单位
		result.setData("MEDI_UNIT", actionParm.getData("MEDI_UNIT"));
		// 频次代码
		result.setData("FREQ_CODE", actionParm.getData("FREQ_CODE"));
		// 停用医生
		result.setData("DC_DR_CODE", "");
		// 停用执行科室
		result.setData("DC_DEPT_CODE", "");
		// 用法
		result.setData("ROUTE_CODE", actionParm.getData("ROUTE_CODE"));
		// 天数
		result.setData("TAKE_DAYS", 0);
		// 配药数量
		result.setData("DOSAGE_QTY", 0);
		// 配药单位
		result.setData("DOSAGE_UNIT", "");
		// 发药数量
		result.setData("DISPENSE_QTY", 0);
		// 发药单位
		result.setData("DISPENSE_UNIT", "");
		// 盒发药注记
		result.setData("GIVEBOX_FLG", "N");
		// 续用注记
		result.setData("CONTINUOUS_FLG", "N");
		// 累用量
		result.setData("ACUMDSPN_QTY", 0);
		// 最近配药量
		result.setData("LASTDSPN_QTY", 0);
		// 医嘱预定启用日期
		result.setData("EFF_DATE", "");
		// 开单科室
		result.setData("ORDER_DEPT_CODE", "");
		// 开单医师(数据库不许为空)
		result.setData("ORDER_DR_CODE", Operator.getID());
		// 执行科室
		result.setData("EXEC_DEPT_CODE", "");
		// 执行技师
		result.setData("EXEC_DR_CODE", "");
		// 停用原因代码
		result.setData("DC_RSN_CODE", "");
		// 医师备注
		result.setData("DR_NOTE", "");
		// 护士备注
		result.setData("NS_NOTE", "");
		// 给付类别
		result.setData("INSPAY_TYPE", parm.getData("INSPAY_TYPE"));
		// 管制药品级别
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("CTRLDRUGCLASS_CODE", actionParm
					.getData("CTRLDRUGCLASS_CODE"));
		} else {
			result.setData("CTRLDRUGCLASS_CODE", "");
		}
		// 抗生素代码
		result
		.setData("ANTIBIOTIC_CODE", actionParm
				.getData("ANTIBIOTIC_CODE"));
		// 处方签号(草药使用)
		result.setData("RX_NO", "");
		// 服号(草药使用)
		result.setData("PRESRT_NO", 0);
		// 药品类型
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("PHA_TYPE", actionParm.getData("PHA_TYPE"));
		} else {
			result.setData("PHA_TYPE", "");
		}
		// 剂型类别
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("DOSE_TYPE", actionParm.getData("DOSE_CODE"));
		} else {
			result.setData("DOSE_TYPE", "");
		}
		// 饮片服用量
		result.setData("DCT_TAKE_QTY", 0);
		// 煎药方式(中药适用)
		result.setData("DCTAGENT_CODE", "");
		// 待煎包总数(中药适用)
		result.setData("PACKAGE_AMT", 0);
		// 集合医嘱主项注记
		result.setData("SETMAIN_FLG", "");
		// 审核护士
		result.setData("NS_CHECK_CODE", "");
		// 单独开立注记
		result.setData("INDV_FLG", "");
		// 隐藏注记
		result.setData("HIDE_FLG", "N");
		// 集合医嘱顺序号
		result.setData("ORDERSET_GROUP_NO", 0);
		// 集合医嘱主项代码
		result.setData("ORDERSET_CODE", "");
		// 医令细分类
		result.setData("ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
		// 医令分组代码
		result.setData("CAT1_TYPE", parm.getData("CAT1_TYPE"));
		// 报告类别
		result.setData("RPTTYPE_CODE", "");
		// 体检代码
		result.setData("OPTITEM_CODE", "");
		// 申请单类别
		result.setData("MR_CODE", "");
		// FILE_NO
		result.setData("FILE_NO", "");
		// 绩效代码
		result.setData("DEGREE_CODE", "");
		// 护士审核时间
		result.setData("NS_CHECK_DATE", "");
		// 审核护士DC确认
		result.setData("DC_NS_CHECK_CODE", "");
		// 审核护士DC确认时间
		result.setData("DC_NS_CHECK_DATE", "");
		// 最近摆药日期
		result.setData("LAST_DSPN_DATE", "");
		// 最近摆药量(最近或是首日量)
		result.setData("FRST_QTY", 0);
		// 审核药师
		result.setData("PHA_CHECK_CODE", "");
		// 审核时间
		result.setData("PHA_CHECK_DATE", "");
		// 静脉配液中心
		result.setData("INJ_ORG_CODE", "");
		// 急做
		result.setData("URGENT_FLG", "");
		// 累计开药量
		result.setData("ACUMMEDI_QTY", 0);
		// 首餐日期时间
		result.setData("START_DTTM", "");
		// 医嘱执行时间(主键)
		result.setData("ORDER_DATETIME", "");
		// 医疗仪器
		result.setData("DEV_CODE", parm.getData("DEV_CODE"));
		// 给药注记
		result.setData("DISPENSE_FLG", "");
		// 医嘱备注,药品备注
		result.setData("IS_REMARK", parm.getData("IS_REMARK"));
		// 取药部门
		result.setData("TAKEMED_ORG", "");
		result.setData("OPT_USER", Operator.getID());
		Timestamp sysdate = TJDODBTool.getInstance().getDBTime();
		result.setData("OPT_DATE", StringTool.getString(sysdate,
				"yyyyMMddHHmmss"));
		result.setData("OPT_TERM", Operator.getIP());
		// 用药开始时间
		if (row == 0) {
			result.setData("ORDER_DATE", "");
		} else {
			TParm tempParm = this.getTTable(TABLE6).getDataStore().getRowParm(
					row - 1);
			result.setData("ORDER_DATE", tempParm.getData("ORDER_DATE"));
		}
		// 用药结束时间
		if (row == 0) {
			result.setData("DC_DATE", "");
		} else {
			TParm tempParm = this.getTTable(TABLE6).getDataStore().getRowParm(
					row - 1);
			result.setData("DC_DATE", tempParm.getData("DC_DATE"));
		}
		// 用药陈述人
		if (row == 0) {
			result.setData("MED_REPRESENTOR", "");
		} else {
			TParm tempParm = this.getTTable(TABLE6).getDataStore().getRowParm(
					row - 1);
			result.setData("MED_REPRESENTOR", tempParm
					.getData("MED_REPRESENTOR"));
		}
		return result;
	}

	/**
	 * 保存院前用药
	 * 
	 * @param tab
	 *            TTable
	 */
	private boolean savePAData() {
		TParm parm = new TParm();
		boolean flag = true;
		String orderNo = "";
		TDataStore tds = this.getTTable(TABLE6).getDataStore();
		String[] sql = tds.getUpdateSQL();
		TParm delParm = tds.getBuffer(tds.DELETE);
		TParm modifyParm = tds.getBuffer(tds.MODIFY);

		if (null != sql) {
			int newrows[] = tds.getNewRows(tds.PRIMARY);
			for (int i = 0; i < newrows.length; i++) {
				if (sql[i].contains("INSERT")) {
					if (StringUtils.isEmpty(orderNo)) {
						orderNo = SystemTool.getInstance().getNo("ALL", "ODI",
								"ORDER_NO", "ORDER_NO");
					}
					tds.setItem(newrows[i], "EFF_DATE", tds.getItemString(
							newrows[i], "ORDER_DATE").replaceAll("-", "/")
							.replaceAll(" 00:00:00.0", ""));
					tds.setItem(newrows[i], "ORDER_NO", orderNo);
					tds.setItem(newrows[i], "ORDER_SEQ", i + 1);
				}
			}

			// 新增
			flag = this.getTTable(TABLE6).getDataStore().update();
			if (!flag) {
				return flag;
			}
		}

		// 删除
		if (delParm.getCount() > 0) {
			String delSql = "";
			TParm delResult = new TParm();
			for (int i = 0; i < delParm.getCount(); i++) {
				delSql = "DELETE FROM ODI_ORDER WHERE CASE_NO = '#' AND ORDER_NO = '#' AND ORDER_SEQ = # AND RX_KIND = 'PA' ";
				delSql = delSql.replaceFirst("#",
						delParm.getValue("CASE_NO", i)).replaceFirst("#",
								delParm.getValue("ORDER_NO", i)).replaceFirst("#",
										delParm.getValue("ORDER_SEQ", i));
				delResult = new TParm(TJDODBTool.getInstance().update(delSql));

				if (delResult.getErrCode() < 0) {
					err("ERR:" + delResult.getErrText());
					return false;
				}
			}
		}

		// 修改
		if (modifyParm.getCount() > 0) {
			int modRows[] = tds.getModifiedRows();
			String delSql = "";
			TParm delResult = new TParm();
			String userId = Operator.getID();
			String userIp = Operator.getIP();

			for (int i = 0; i < modRows.length; i++) {
				if (StringUtils.isEmpty(modifyParm.getValue("CASE_NO", i))) {
					continue;
				}
				delSql = "UPDATE ODI_ORDER SET DR_NOTE='#',ORDER_DATE=TO_DATE('#','YYYY/MM/DD'),"
						+ "DC_DATE=TO_DATE('#','YYYY/MM/DD'),MED_REPRESENTOR='#',MEDI_QTY='#',MEDI_UNIT='#',"
						+ "FREQ_CODE='#',EFF_DATE=TO_DATE('#','YYYY/MM/DD'),OPT_USER='"
						+ userId
						+ "',OPT_DATE=SYSDATE,OPT_TERM='"
						+ userIp
						+ "' WHERE CASE_NO='#' AND ORDER_NO='#' AND ORDER_SEQ=# AND RX_KIND='PA' ";
				delSql = delSql.replaceFirst("#",
						tds.getItemString(modRows[i], "DR_NOTE")).replaceFirst(
								"#",
								tds.getItemString(modRows[i], "ORDER_DATE").replaceAll(
										" 00:00:00.0", "")).replaceFirst(
												"#",
												tds.getItemString(modRows[i], "DC_DATE").replaceAll(
														" 00:00:00.0", "")).replaceFirst("#",
																tds.getItemString(modRows[i], "MED_REPRESENTOR"))
						.replaceFirst("#",
								tds.getItemString(modRows[i], "MEDI_QTY"))
						.replaceFirst("#",
								tds.getItemString(modRows[i], "MEDI_UNIT"))
						.replaceFirst("#",
								tds.getItemString(modRows[i], "FREQ_CODE"))
						.replaceFirst(
								"#",
								tds.getItemString(modRows[i], "ORDER_DATE")
								.replaceAll(" 00:00:00.0", ""))
						.replaceFirst("#", modifyParm.getValue("CASE_NO", i))
						.replaceFirst("#", modifyParm.getValue("ORDER_NO", i))
						.replaceFirst("#", modifyParm.getValue("ORDER_SEQ", i));

				delResult = new TParm(TJDODBTool.getInstance().update(delSql));

				if (delResult.getErrCode() < 0) {
					err("ERR:" + delResult.getErrText());
					return false;
				}
			}
		}

		return flag;
	}

	/**
	 * 检核院前用药
	 */
	public TParm checkPADataSave() {
		TParm parm = new TParm();
		TTable table = this.getTTable(TABLE6);
		TDataStore tds = table.getDataStore();
		int rowCount = tds.rowCount();

		for (int i = 0; i < rowCount - 1; i++) {
			if (StringUtils.isEmpty(table.getItemString(i, "ORDER_DATE"))) {
				parm.setErr(-1, "用药开始时间不能为空");
				parm.setData("ROW", i);
				parm.setData("COL", 2);
				return parm;
			}
			if (StringUtils.isEmpty(table.getItemString(i, "DC_DATE"))) {
				parm.setErr(-1, "用药结束时间不能为空");
				parm.setData("ROW", i);
				parm.setData("COL", 3);
				return parm;
			}
			if (table.getItemTimestamp(i, "ORDER_DATE").getTime() > table
					.getItemTimestamp(i, "DC_DATE").getTime()) {
				parm.setErr(-1, "用药开始时间不能大于用药结束时间");
				parm.setData("ROW", i);
				parm.setData("COL", 2);
				return parm;
			}
			if (StringUtils.isEmpty(table.getItemString(i, "MED_REPRESENTOR"))) {
				parm.setErr(-1, "用药陈述人不能为空");
				parm.setData("ROW", i);
				parm.setData("COL", 4);
				return parm;
			}
			if (StringUtils.isEmpty(table.getItemString(i, "MEDI_QTY"))) {
				parm.setErr(-1, "用量不能为空");
				parm.setData("ROW", i);
				parm.setData("COL", 5);
				return parm;
			}
			if (table.getItemDouble(i, "MEDI_QTY") == 0) {
				parm.setErr(-1, "用量不能为0");
				parm.setData("ROW", i);
				parm.setData("COL", 5);
				return parm;
			}
			if (StringUtils.isEmpty(table.getItemString(i, "MEDI_UNIT"))) {
				parm.setErr(-1, "单位不能为空");
				parm.setData("ROW", i);
				parm.setData("COL", 6);
				return parm;
			}
			if (StringUtils.isEmpty(table.getItemString(i, "FREQ_CODE"))) {
				parm.setErr(-1, "频次不能为空");
				parm.setData("ROW", i);
				parm.setData("COL", 7);
				return parm;
			}
			// 非本院用药
			String notOurHospDrugCode = TConfig
					.getSystemValue("NotOurHospDrugCode");
			if (StringUtils.isNotEmpty(notOurHospDrugCode)) {
				if (StringUtils.equals(table.getItemString(i, "ORDER_CODE"),
						notOurHospDrugCode)) {
					if (StringUtils.isEmpty(table.getItemString(i, "DR_NOTE"))) {
						parm.setErr(-1, "非本院用药需在医嘱备注中注明用药名称");
						parm.setData("ROW", i);
						parm.setData("COL", 1);
						return parm;
					}
				}
			}
		}

		return parm;
	}

	/**
	 * 激发补充计价窗口
	 */
	public void onCharge() {
		TParm ibsParm = new TParm();
		String caseNo_ = "";
		String mrNo_ = "";
		String ipdNo_ = "";
		String station_ = "";
		String bedNo_ = "";
		String execDeptCode_ = "";
		String vsDrCode_ = "";

		caseNo_ = this.getCaseNo();
		mrNo_ = this.getMrNo();
		ipdNo_ = this.getIpdNo();
		station_ = this.getStationCode();
		execDeptCode_ = Operator.getDept();
		// this.getValue("");
		vsDrCode_ = this.getValueString("VC_CODE");
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo_);
		parm = ADMTool.getInstance().getADM_INFO(parm);
		bedNo_ = parm.getValue("BED_NO", 0);

		ibsParm.setData("IBS", "INWLEAD_FLG", false);
		ibsParm.setData("IBS", "SCHD_CODE", getSchdCode());// 临床路径时程
		ibsParm.setData("IBS", "CASE_NO", caseNo_);
		ibsParm.setData("IBS", "IPD_NO", ipdNo_);
		ibsParm.setData("IBS", "MR_NO", mrNo_);
		ibsParm.setData("IBS", "BED_NO", bedNo_);
		ibsParm.setData("IBS", "DEPT_CODE", ((TParm) this.getParameter())
				.getData("ODI", "DEPT_CODE"));
		ibsParm.setData("IBS", "STATION_CODE", station_);
		ibsParm.setData("IBS", "VS_DR_CODE", ((TParm) this.getParameter())
				.getData("ODI", "ORDER_DR_CODE"));
		ibsParm.setData("IBS", "TYPE", "INW");
		ibsParm.setData("IBS", "CLNCPATH_CODE", getClpCode());

		openDialog("%ROOT%\\config\\ibs\\IBSOrderm.x", ibsParm);
	}

	/**
	 * 历次就诊
	 */
	public void onQuerySummaryInfo() {
		TParm parm = new TParm();
		TTable table = (TTable) this.getComponent("TABLE");

		Container container = (Container) callFunction("UI|getThis");
		while (!(container instanceof TTabbedPane)) {
			container = container.getParent();
		}
		TTabbedPane tabbedPane = (TTabbedPane) container;

		parm.setData("MR_NO", this.getMrNo());
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
	 * 心电检查
	 */
	public void getPdfReport() {
		String sql = "SELECT APPLICATION_NO  FROM MED_APPLY WHERE MR_NO = '"
				+ mrNo + "' AND ORDER_CAT1_CODE = 'ECC' AND STATUS != '9' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("111111111"+sql);
		if (result.getCount() <= 0) {
			this.messageBox("该病患没有心电检查医嘱"); 
			return;
		}
		// 电生理调阅pdf
		TParm parm = new TParm();
		String opbBookNo = "";
		for (int i = 0; i < result.getCount(); i++) {
			opbBookNo += "'" + result.getValue("APPLICATION_NO", i) + "'" + ",";
		}
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", mrNo);
		parm.setData("TYPE", "3");
		parm.setData("OPE_BOOK_NO", opbBookNo.substring(0,
				opbBookNo.length() - 1));
		this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);

	}
	
	/**
	 * 其他报告
	 *   == 20180115 zhanglei add
	 */
	public void getPDFQiTa() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", mrNo);
		this.openDialog("%ROOT%\\config\\sys\\SYSOpeQiTaPDF.x", parm);
	}

	/**
	 * 血糖报告
	 */
	public void getXTReport() {
		SystemTool.getInstance().OpenTnbWeb(this.getMrNo());
	}

	/**
	 * 重症监护
	 */
	public void getCCEmrData() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("TYPE", "1");
		this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);
	}

	/**
	 * 手麻病历
	 */
	public void getOpeMrData() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("TYPE", "2");
		this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);
	}
	/**
	 * 其他报告
	 */
	public void onOtherReport() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", this.getCaseNo());
		//parm.setData("TYPE", "2");
		this.openDialog("%ROOT%\\config\\odi\\ODIOtherReport.x", parm);
	}


	/**
	 * 设定术中用药数据
	 */
	private void setOdiOpTableData(TTable table) {
		String sql = "SELECT A.*,NVL(MEDI_QTY,DISPENSE_QTY) AS SUM_QTY,"
				+ "CASE WHEN A.DOSAGE_UNIT IS NOT NULL THEN (SELECT UNIT_CHN_DESC FROM SYS_UNIT B WHERE A.DOSAGE_UNIT = B.UNIT_CODE) ELSE A.DISPENSE_UNIT_DESC END AS DOSAGE_UNIT_DESC,"
				+ "CASE WHEN A.MEDI_UNIT IS NOT NULL THEN (SELECT UNIT_CHN_DESC FROM SYS_UNIT B WHERE A.MEDI_UNIT = B.UNIT_CODE) ELSE A.DISPENSE_UNIT_DESC END AS UNIT_CODE FROM ODI_ORDER A WHERE RX_KIND='OP' AND CASE_NO='"
				+ this.getCaseNo() + "' AND CAT1_TYPE='PHA'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
			table.setParmValue(new TParm());
			return;
		}

		table.setParmValue(result);
	}

	/**
	 * 设定用血记录数据
	 */
	private void setOdiBlTableData(TTable table) {
		StringBuffer sbSql = new StringBuffer();
		sbSql
		.append("SELECT NVL(B.BLDTRANS_TIME,A.EFF_DATE) AS BLDTRANS_TIME,A.ORDER_DESC,NVL(B.BLDTRANS_END_TIME,A.DC_DATE) AS BLDTRANS_END_TIME,");
		sbSql
		.append("CASE WHEN A.DISPENSE_UNIT IS NOT NULL THEN (SELECT UNIT_CHN_DESC FROM SYS_UNIT B WHERE A.DISPENSE_UNIT = B.UNIT_CODE) ELSE A.DISPENSE_UNIT_DESC END AS DISPENSE_UNIT,");
		sbSql.append("A.DISPENSE_QTY,A.MR_NO,");
		sbSql
		.append("B.BLD_TYPE,B.BLOOD_NO,B.BLD_CODE,B.SUBCAT_CODE,B.RH_FLG,B.SHIT_FLG,");
		sbSql
		.append("CASE WHEN B.SUBCAT_CODE IS NOT NULL THEN (SELECT C.SUBCAT_DESC FROM BMS_BLDSUBCAT C WHERE B.SUBCAT_CODE = C.SUBCAT_CODE) ELSE A.SPECIFICATION END AS SPECIFICATION,");
		sbSql
		.append("B.CROSS_MATCH_L,B.CROSS_MATCH_S,B.RESULT,B.BLDTRANS_USER");
		sbSql.append(" FROM ODI_ORDER A, BMS_BLOOD B ");
		sbSql
		.append(" WHERE A.CASE_NO = B.CASE_NO(+) AND A.BLOOD_NO = B.BLOOD_NO(+) ");
		sbSql.append(" AND A.RX_KIND = 'BL' AND A.CASE_NO = '");
		sbSql.append(this.getCaseNo());
		sbSql
		.append("' AND (A.BLOOD_NO IS NULL OR (A.BLOOD_NO IS NOT NULL AND B.OUT_NO IS NOT NULL ");
		sbSql
		.append(" AND B.RECEIVED_USER IS NOT NULL AND B.BLDTRANS_USER IS NOT NULL))");
		sbSql.append(" ORDER BY A.ORDER_NO,A.ORDER_SEQ ");
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
			table.setParmValue(new TParm());
			return;
		}

		table.setParmValue(result);
	}

	/**
	 * 医师确认事件
	 */
	public void drCheck() {
		TParm parm = new TParm(TJDODBTool.getInstance().update(
				"UPDATE ODI_ORDER SET DR_CHECK_CODE = '" + Operator.getID()
				+ "' " + " WHERE CASE_NO = '" + this.getCaseNo() + "'"
				+ " AND RX_KIND = 'OP' AND DR_CHECK_CODE IS NULL"));
		if (parm.getErrCode() < 0) {
			this.messageBox("确认失败");
			return;
		}
		this.messageBox("确认成功");
		this.onQuery();
	}

	/**
	 * 评估单
	 */
	public void assessReport() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("PAT_NAME", this.getPatName());
		parm.setData("MR_NO", this.getMrNo());
		parm.setData("DEPT_CODE", this.getDeptCode());
		parm.setData("BED_NO_DESC", this.getBedIpdNo(caseNo).getValue(
				"BED_NO_DESC", 0));
		parm.setData("IPD_NO", this.getIpdNo());

		this.openWindow("%ROOT%\\config\\sys\\SYSAssessmentList.x", parm);// wanglong
	}

	/**
	 * 术中医嘱RX_KIND写入OP
	 */
	// private void opeChangeRxkind(){
	// if(opeFlg){
	// TDS dsb = odiObject.getDS("ODI_ORDER");
	// //1
	// int[] nr = dsb.getNewRows(TDS.PRIMARY);
	// for (int i : nr) {
	// dsb.setItem(i, "RX_KIND", "OP");
	// }
	// //2
	// // for (int i = 0; i < dsb.rowCount(); i++) {
	// // if("true".equals(dsb.getItemString(i, "#NEW#"))){
	// // dsb.setItem(i, "RX_KIND", "OP");
	// // }
	// // }
	// odiObject.setAttribute("ODI_ORDER", dsb);



	// }
	// }

	/**
	 * 停跳用法医嘱计费
	 */
	public void cardioplegiaFee(TDS tds) {
		// TDS dsb = odiObject.getDS("ODI_ORDER");
		String buff = tds.isFilter() ? tds.FILTER : tds.PRIMARY;
		TParm result = new TParm();
		// 1
		int[] nr = tds.getNewRows(buff);
		int count = 0;
		for (int i : nr) {
			String routeCode = tds.getItemData(i, "ROUTE_CODE") + "";
			if (routeCode.equals("CA")) {
				TParm tempParm = tds.getRowParm(i, buff);// =====pangben
				// 2015-8-14
				result.setRowData(count, tempParm);
				count++;
			}
		}
		result.setCount(count);
		onCheckOpOrder(result);
	}

	/**
	 * 停跳用法医嘱审核
	 */
	// public void cardioplegiaCheck(){
	// TDS dsb = odiObject.getDS("ODI_ORDER");
	// Timestamp sysdate = TJDODBTool.getInstance().getDBTime();
	// //1
	// int[] nr = dsb.getNewRows(TDS.PRIMARY);
	// int count = 0;
	// for (int i : nr) {
	// String routeCode = dsb.getItemData(i, "ROUTE_CODE")+"";
	// if(routeCode.equals("CA")){
	// dsb.setItem(i, "NS_CHECK_CODE", Operator.getID());
	// dsb.setItem(i, "NS_CHECK_DATE", StringTool.getString(sysdate,
	// "yyyyMMddHHmmss"));
	// }
	// }
	// }

	/**
	 * 调用NIS护理表单
	 */
	public void onNisFormList() {
		SystemTool.getInstance().OpeNisFormList(caseNo, mrNo);
	}

	/**
	 * 批量修改医嘱时间
	 * 
	 * @author wangb 2016/6/13
	 */
	public void onBatchModOrderDate() {
		// 当前选中页签
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() != 0) {
			return;
		}

		TTable table = (TTable) this.getComponent(TABLE1);
		int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择试验用药医嘱数据行");
			return;
		}
		TDS ds = odiObject.getDS("ODI_ORDER");
		TParm parm = ds.getBuffer(ds.FILTER);
		TParm selPhaParm = table.getDataStore().getRowParm(row);
		if (!"PHA".equalsIgnoreCase(selPhaParm.getValue("CAT1_TYPE"))) {
			this.messageBox("当前选中的数据行非药嘱数据");
			return;
		}

		// 药嘱启用时间
		Date phaDate = StringTool.getDate(selPhaParm.getValue("EFF_DATE")
				.replace("-", "/").substring(0, 19), "yyyy/MM/dd HH:mm:ss");

		TParm queryParm = new TParm();
		queryParm.setData("CASE_NO", selPhaParm.getValue("CASE_NO"));
		// 只查询检验医嘱
		queryParm.setData("CAT1_TYPE", "LIS");

		// 查询带有一期临床注记的未审核医嘱
		TParm orderResult = ODIPICTool.getInstance().queryPICOrder(queryParm);
		if (orderResult.getErrCode() < 0) {
			this.messageBox("查询一期临床医嘱错误");
			err("ERR:" + orderResult.getErrText());
			return;
		} else if (orderResult.getCount() <= 0) {  
			this.messageBox("未查询到已保存且未审核的PK采血医嘱");
			return;
		} else {
			// modify by wangb 2016/09/30 新增护士是否单次执行的校验 START
			TParm odiDspnDParm = new TParm();
			odiDspnDParm.setData("CASE_NO", orderResult.getValue("CASE_NO", 0));
			odiDspnDParm.setData("ORDER_NO", orderResult
					.getValue("ORDER_NO", 0));
			odiDspnDParm.setData("ORDER_SEQ_LIST", orderResult.getValue(
					"ORDER_SEQ").replace("[", "").replace("]", "").replace(" ",
							"").replace(",", "','"));

			// 查询护士执行细表数据
			TParm dspnDResult = ODIPICTool.getInstance().queryOdiDspnD(odiDspnDParm);

			if (dspnDResult.getErrCode() < 0) {
				this.messageBox("查询护士单次执行数据错误");
				err("ERR:" + orderResult.getErrText());
				return;
			} else {
				// 单次执行数据
				boolean execFlg = false;
				int dspnCount = dspnDResult.getCount();
				for (int i = 0; i < dspnCount; i++) {
					if (StringUtils.isNotEmpty(dspnDResult.getValue(
							"NS_EXEC_DATE_REAL", i))) {
						execFlg = true;
						break;
					}
				}

				// 如果存在单次执行数据则不允许再次更新医嘱启用时间
				if (execFlg) {
					this.messageBox("存在已单次执行的医嘱数据，不允许再次修改医嘱启用时间");
					return;
				}
			}
			// modify by wangb 2016/09/30 新增护士是否单次执行的校验 END

			int count = orderResult.getCount();
			Date date = null;
			int intervalTime = 0;
			String drNote = "";

			for (int i = 0; i < count; i++) {
				drNote = orderResult.getValue("DR_NOTE", i);
				if (StringUtils.isEmpty(drNote) || !drNote.contains("h")) {
					continue;
				}

				intervalTime = (int) Math.round(Double.valueOf(drNote
						.split("h")[0]) * 60);
				date = DateUtils.addMinutes(phaDate, intervalTime);
				orderResult.setData("EFF_DATE", i, StringTool.getString(date,
						"yyyy/MM/dd HH:mm:ss"));
			}

			// 执行保存操作
			TParm result = TIOM_AppServer.executeAction(
					"action.odi.ODIPICAction", "onSaveByBatchModOrderDate",
					orderResult);

			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				this.messageBox("E0001");
				return;
			} else {
				// 保存完成后刷新医生站界面数据
				this.onQuery();
				this.messageBox("修改成功");
			}
		}
	}


	// modified by WangQing 20170316
	// 区分住院医生站和手术医生站
	// 手术医生站助记 OPE_FLG 
	/**
	 * 胸痛中心病历  
	 */
	public void onAMI(){
		// add by wangqing 20170701 start
		// 只有胸痛中心的病人才有胸痛中心病历
		TParm amiResult = new TParm();
		amiResult= EMRAMITool.getInstance().getEnterRouteAndPathKindByCaseNo(ADMUtil.getCaseNo(caseNo));
		if(amiResult.getErrCode()<0){
			this.err(amiResult);
			return;
		}
		if(amiResult.getCount()<=0){
			this.messageBox("没有病患信息！！！");
			return;
		}
		if(!(amiResult.getValue("ENTER_ROUTE", 0) != null 
				&& amiResult.getValue("ENTER_ROUTE", 0).equals("E02"))){// 胸痛中心病患
			this.messageBox("非胸痛中心病患！！！");
			return;
		}
		// add by wangqing 20170701 end
		
		
		Object o = this.getParameter();
		if(o != null && o instanceof TParm){
			TParm sysParm = (TParm) o;
			// modified by WangQing 20170510
			boolean opeFlg = sysParm.getData("ODI", "OPE_FLG")==null ? false : true;

			if(opeFlg){// 手术医生站
				// 传递参数
				TParm sysParm1 = new TParm();
				sysParm1.setData("CASE_NO", caseNo);// 住院就诊号
				sysParm1.setData("MR_NO", mrNo);
				sysParm1.setData("PAT_NAME", patName);
				sysParm1.setData("SEX_CODE", sexCode);
				sysParm1.setData("OPBOOK_SEQ", opBookSeq);
				sysParm1.setData("AGE", OdiUtil.getInstance().showAge(this.getBirthDay(),
						this.getAdmDate()));
				
				String sql = " SELECT CASE_NO, MR_NO, OPBOOK_SEQ, TYPE_CODE FROM OPE_OPBOOK WHERE OPBOOK_SEQ='"+opBookSeq+"' AND TYPE_CODE='2' ";
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				if(result.getErrCode()<0){
					return;
				}
				if(result.getCount()<=0){
					this.messageBox("没有介入手术！！！");
					return;
				}		
			
				this.openWindow("%ROOT%\\config\\emr\\EMROpeDrStation.x", sysParm1, false);
			}else{// 住院医生站
				TParm xtParm = new TParm();
				xtParm.setData("CASE_NO", this.getCaseNo());
				xtParm.setData("MR_NO",this.getMrNo());
				xtParm.setData("SEX",this.getSexCode());
				xtParm.setData("AGE",this.getValue("AGE"));
				xtParm.setData("NAME",this.getPatName());
				this.openWindow("%ROOT%\\config\\reg\\REGXTcore.x",xtParm);
			}
		}	
	}

	/**
	 * 调阅血糖报告(强生)
	 */
	public void getBgReport() {
		// 强生血糖web展现
		TParm result = SystemTool.getInstance().OpenJNJWeb(this.getCaseNo());
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
		}
	}
	
	/**
	 * 开立抗菌素校验是否开立病原学检查
	 */
	public boolean checkEtiolgexm(TDS ds){// add by kangy 20170524
		boolean flg=false;
			int chnNewRow[] = ds.getNewRows();
			for (int temp : chnNewRow) {
				if ("Y1004".equals(ds.getRowParm(temp).getValue("RPTTYPE_CODE"))) {
					flg = true;
					break;
				}
			}
			return flg;
		
	}
	/**
	 * 校验开立医嘱是否是抗菌素
	 */
	public boolean checkAntibiotics(String orderCode){// ADD BY kangy 20170524
		boolean flg=false;
		String sql="SELECT ORDER_CODE FROM PHA_BASE WHERE ANTIBIOTIC_CODE IS NOT NULL AND ORDER_CODE='"+orderCode+"'";
		 TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		 if(parm.getCount()==0){
			 flg=false;
		 }else if(parm.getCount()==1){
			 flg=true;
		 }
		return flg;
	}
	
	/**
	 * 初始化校验医嘱是否包含病原学检查
	 */
	public boolean checkEtiolgexmParm(TParm TDSparm){// add by kangy 20170601
		boolean flg=false;
		for(int i=0;i<TDSparm.getCount();i++){
			if ("Y1004".equals(TDSparm.getValue("RPTTYPE_CODE",i))) {
				flg = true;
				break;
			}
		}
			return flg;
		
	}
	
	/**
	 * 初始化校验是否含有抗菌素
	 */
	public boolean checkAntibioticsTDSParm(TParm TDSparm){// add by kangy 20170524
		boolean flg=false;
			TParm parm=new TParm();
			for (int i=0;i<TDSparm.getCount();i++) {
				String sql="SELECT ORDER_CODE FROM PHA_BASE WHERE ANTIBIOTIC_CODE IS NOT NULL AND ORDER_CODE='"+TDSparm.getValue("ORDER_CODE",i)+"'";
				  parm=new TParm(TJDODBTool.getInstance().select(sql));
				 if(parm.getCount()==0){
					 flg=false;
				 }else if(parm.getCount()==1){
					 flg=true;
					 break;
				 }	
			}
			return flg;
		
	}

	/**
     * 警戒线校验
     * 
     * @return
     */
	public boolean stopFlg() {
		double totAmt3 = 0.00;
		double totAmt2 = 0.00;
		StringBuffer sbMsg = new StringBuffer();
		String sysDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 19).replace("-", "/");
		String caseNo = ((TParm) this.getParameter())
				.getValue("ODI", "CASE_NO");
		String sql = "SELECT UNLOCKED_FLG,STOP_BILL_FLG FROM ADM_INP WHERE CASE_NO ='"
				+ caseNo + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// 身份比例
		String sqlD = "SELECT A.CASE_NO,B.LOCK_CTZ_FLG AS LOCK_CTZO,C.LOCK_CTZ_FLG AS LOCK_CTZT,CASE WHEN B.DISCOUNT_RATE IS NULL THEN 1 ELSE B.DISCOUNT_RATE END "
				+ "DISCOUNT_RATE "
				+ "FROM ADM_INP A, SYS_CTZ_REBATE B, SYS_CTZ_REBATE C   "
				+ "WHERE A.CTZ1_CODE=B.CTZ_CODE(+)  AND A.CTZ2_CODE = C.CTZ_CODE(+) "
				+ "AND A.CASE_NO='"
				+ caseNo + "' ";
		TParm resultD = new TParm(TJDODBTool.getInstance().select(sqlD));
		
		
		// 取红黄警戒线
		String sqlSign = "SELECT YELLOW_SIGN,RED_SIGN FROM ODI_SYSPARM ";
		TParm resultSign = new TParm(TJDODBTool.getInstance().select(sqlSign));

		// 消费总金额
		String sql2 = "SELECT SUM(TOT_AMT) TOT_AMT FROM IBS_ORDD "
				+ "WHERE CASE_NO='" + caseNo + "' AND BILL_DATE <= TO_DATE('"
				+ sysDate + "','YYYY/MM/DD HH24:MI:SS')";
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		if (result2.getCount() > 0
				&& result2.getValue("TOT_AMT", 0).length() > 0) {
			totAmt2 = StringTool.round(result2.getDouble("TOT_AMT", 0), 2);
		}

		// 预交金
		String sql3 = " SELECT CASE_NO, SUM (PRE_AMT) PRE_AMT "
				+ "FROM BIL_PAY WHERE REFUND_FLG = 'N' "
				+ "AND TRANSACT_TYPE IN ('01', '03', '04') "
				+ "AND CHARGE_DATE <= TO_DATE ('" + sysDate
				+ "','YYYY/MM/DD HH24:MI:SS') AND CASE_NO = '" + caseNo
				+ "' GROUP BY CASE_NO";
		TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3));
		if (result3.getCount() > 0
				&& result3.getValue("PRE_AMT", 0).length() > 0) {
			totAmt3 = StringTool.round(result3.getDouble("PRE_AMT", 0), 2);
		}

		// 余额
		double balance = StringTool.round(totAmt3
				- (totAmt2 * resultD.getDouble("DISCOUNT_RATE", 0)), 2);

		sbMsg.append("DB时间：" + sysDate + ",客户端ID:" + Operator.getID()
				+ ",客户端IP:" + Operator.getIP());
		sbMsg.append(",就诊号：" + caseNo + ",预交金总额：" + totAmt3);
		sbMsg.append(",原始费用总额：" + totAmt2 + ",折扣:"
				+ resultD.getDouble("DISCOUNT_RATE", 0));
		sbMsg.append(",余额：" + balance + ",红色警戒线："
				+ resultSign.getDouble("RED_SIGN", 0));
		sbMsg.append(",原始停止划价注记：" + result.getValue("STOP_BILL_FLG", 0));
		TParm errLogParm = new TParm();
		//临时解锁短信次数清零
				if (balance >= resultSign.getDouble("YELLOW_SIGN", 0)) {
					String sqlS = "UPDATE ADM_INP SET SMS_COUNT='0' WHERE CASE_NO='"
							+ caseNo + "' ";
					TJDODBTool.getInstance().update(sqlS);
				}
				//当身份为不受管控时,不做处理
				if( resultD.getValue("LOCK_CTZO", 0).equals("Y") 
								|| resultD.getValue("LOCK_CTZT", 0).equals("Y") ){
					return false;
				}
		
		// 临时解锁和永久解锁的数据不做处理
		if (result.getValue("UNLOCKED_FLG", 0).equals("1")
						|| result.getValue("UNLOCKED_FLG", 0).equals("2")) {
			return false;
		}
		// 余额大于红色警戒线,进行解锁
		if (balance > resultSign.getDouble("RED_SIGN", 0)) {
			String sqlSBF = "UPDATE ADM_INP SET STOP_BILL_FLG='N',UNLOCKED_FLG='0' "
					+ " WHERE CASE_NO='" + caseNo + "' ";
			sbMsg.append(",更新sql：" + sqlSBF);
			TJDODBTool.getInstance().update(sqlSBF);
			errLogParm.setData("MSG", sbMsg.toString());
			TIOM_AppServer.executeAction("action.pha.PHAATCAction", "printLog",
					errLogParm);
			return false;
		}

		if ("Y".equals(result.getValue("STOP_BILL_FLG", 0))) {
			errLogParm.setData("MSG", sbMsg.toString());
			TIOM_AppServer.executeAction("action.pha.PHAATCAction", "printLog",
					errLogParm);
			return true;
		}

		return false;
	}
	


	/**
	 * 弹出错误信息
	 * @param result
	 */
	public void err(TParm result){
    	err("ERR:" + result.getErrCode() + result.getErrText() +
		result.getErrName());
	}


}
