package com.javahis.ui.opd;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMResvTool;
import jdo.adm.ADMTool;
import jdo.bil.BIL;
import jdo.bil.BILPayTool;
import jdo.bil.BILStrike;
import jdo.ctr.CTRPanelTool;
import jdo.device.CallNo;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTNewIO;
import jdo.emr.EMRAMITool;
import jdo.emr.EMRCdrTool;
import jdo.erd.ERDLevelTool;
import jdo.hl7.Hl7Communications;
import jdo.ind.INDTool;
import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSTJTool;
import jdo.odo.Diagrec;
import jdo.odo.DrugAllergy;
import jdo.odo.MedApply;
import jdo.odo.MedHistory;
import jdo.odo.ODO;
import jdo.odo.OPDAbnormalRegTool;
import jdo.odo.OpdOrder;
import jdo.odo.OpdRxSheetTool;
import jdo.odo.PatInfo;
import jdo.odo.RegPatAdm;
import jdo.opb.OPB;
import jdo.opb.OPBTool;
import jdo.opd.DrugAllergyTool;
import jdo.opd.OPDSysParmTool;
import jdo.opd.Order;
import jdo.opd.OrderTool;
import jdo.opd.TotQtyTool;
import jdo.pha.PassTool;
import jdo.pha.PhaBaseTool;
import jdo.pha.PhaSysParmTool;
import jdo.reg.ClinicRoomTool;
import jdo.reg.PatAdmTool;
import jdo.reg.REGCcbTool;
import jdo.reg.REGSysParmTool;
import jdo.reg.REGTool;
import jdo.reg.Reg;
import jdo.reg.SessionTool;
import jdo.sys.DeptTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSAntibioticTool;
import jdo.sys.SYSCtrlDrugClassTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SYSNewRegionTool;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SYSOrderSetDetailTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;
import org.jawin.COMException;
import org.jawin.DispatchPtr;
import org.jawin.win32.Ole32;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.root.client.SocketLink;
import com.dongyang.tui.DText;
import com.dongyang.tui.text.CopyOperator;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComboNode;
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
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.combo.TComboSession;
import com.javahis.ui.ekt.testEkt.EktParam;
import com.javahis.ui.ekt.testEkt.EktTradeContext;
import com.javahis.ui.emr.EMRTool;
import com.javahis.ui.reg.REGCTTriageControl;
import com.javahis.util.ADMUtil;
import com.javahis.util.AMIUtil;
import com.javahis.util.EmrUtil;
import com.javahis.util.OdoUtil;
import com.javahis.util.OrderUtil;
import com.javahis.util.ReasonableMedUtil;
import com.javahis.util.SelectResult;
import com.javahis.util.StringUtil;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

import device.PassDriver;


/**
 * 
 *
 * 
 * <p>
 * 
 * Title: 门诊医生工作站主档
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站主档控制类
 * </p>
 *  
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 * 
 * @author ehui 2008.09.01
 * @version 1.0
 */

// modified by wangqing 20170801
// 1、胸痛患者提示修改

public class OdoMainControl extends OdoMainBaseControl {
	//颜色设置yanjing 20130614
	Color green = new Color(0,125, 0);
	private String mrNo = "";

	// 门急住别
	private String admType; // liudy
	// 病患信息
	private TParm parmpat;    
	// 左右的MOVEPANEL
	private TMovePane mp;

	// 身份全局变量
	private String[] ctz = new String[3]; // liudy
	// 处方类型
	private String rxType;
	// 公用TABLE名
	private String tableName;
	// 处方类型名称
	private String rxName;
	// 是否已读取过医疗卡
	private boolean isReadEKT = false;
	// 处方号
	// private String rxNo;
	// 开单科室
	private String realDeptCode; // 开单科室
	public final static String EXA_RX = "EXA_RX";
	public final static String OP_RX = "OP_RX";
	public final static String PREMED_RX = "PREMED_RX"; //院前用药
	public final static String MED_RX = "MED_RX";
	public final static String CHN_RX = "CHN_RX";
	public final static String CTRL_RX = "CTRL_RX";
	public final static String TABLEPAT = "TABLEPAT";
	public final static String TABLEDIAGNOSIS = "TABLEDIAGNOSIS";
	public final static String TABLEMEDHISTORY = "TABLEMEDHISTORY";
	private static final String TABLEALLERGY = "TABLEALLERGY";
	private static final String TABLE_EXA = "TABLEEXA"; // 检验检查表
	private static final String TABLE_OP = "TABLEOP"; // 诊疗项目表
	private static final String TABLE_PRE_MED = "TABLEPREMED"; // 院前用药
	private static final String TABLE_MED = "TABLEMED"; // 西成药表
	private static final String TABLE_CHN = "TABLECHN"; // 中药饮片表
	private static final String TABLE_CTRL = "TABLECTRL"; // 管制药品表
	private static final String PREMED = "6";
	private static final String MED = "1";
	private static final String CTRL = "2";
	private static final String CHN = "3";
	private static final String OP = "4";
	private static final String EXA = "5";
	private static final String NULLSTR = "";
		
	// 替换TABLE上已有的诊断代码的原值
	//private String tempIcd = "";
	// 初始默认的过敏类型
	private String allergyType = "B";
	// 上一个病人的mr_no
	private String lastMrNo = "";
	// 诊间对药房dctTakeDays
	private String phaCode = "";
	// 默认中药用量
	private String dctMediQty = "0";
	// 默认中药天数
	private String dctTakeDays = "0";
	// 默认中药频次
	private String dctFreqCode = "";
	// 默认中药用法
	private String dctRouteCode = "";
	// 默认中药煎法
	private String dctAgentCode = "";
	// 是否启用医保
	private boolean whetherCallInsItf = false;

	private String seeDrTime="";
	// opb对象
	OPB opb;
	// table使用的行的颜色集合
	Map map; // liudy
	// icd信息的全局变量
	//private TDataStore icd = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
	/**
	 *  中西医标记
	 */
	private String wc = "";
	// 调用查找病人方法的开关，给某些组件赋值时，这些组件里的点击方法有调用selectPat方法，就会递归
	boolean selectPat = false;
	// 合理用药开关
	// private boolean rsnMed;
	// 合理用药开关
	private boolean passIsReady = false;
	private boolean enforcementFlg = false;
	private int warnFlg;
	// 连接医嘱号
	//private int linkNo;
	// 诊室，医师，代诊部门
	private TComboBox clinicroom, dr, insteadDept;

	//Evan
	private TComboBox enterRoute;//入院路径
	private TComboBox pathKind;//路径分类
	private String selectCaseNo = "";
	private boolean isChange = false;

	// 界面上的TABLE
	private TTable tblPat, tblMedHistory, tblAllergy, tblDiag, tblExa, tblOp,
	tblMed, tblChn, tblCtrl,tblPreMed;
	public TLabel ekt_lable;//yanjing 20130614 医疗卡状态标签
	// 界面上的结构化病历
	private TWord word, familyWord;
	// 就诊时段combo
	TComboSession t;
	// 急诊病患列表变色的线程
	private Thread erdThread;
	// 既往史，过敏史变换为红色
	public Color red = new Color(255, 0, 0); // liudy
	// 设置主诉客诉现病史点击时的获得控件的TAB
	private String focusTag;
	// 打开已经看诊的病患的结构化病历所需要的存储路径
	private String[] saveFiles, familyHisFiles;
	// 是否英语
	private boolean isEng;
	// 服务等级
	private String serviceLevel = "";
	// 就诊号====pangben 20110914
	private String caseNo;
	public TParm sendHL7Parm;
	// 医疗卡卡号===pangben 20110914
	public String tredeNo;
	public TParm ektReadParm;// 医疗卡读卡数据
	public boolean ektExeConcel = false;// 医疗卡弹出界面取消按钮以后操作
	private TParm regSysEFFParm;//pangben 2013-4-28 挂号有效天数
	/**
	 * Socket传送门诊药房工具
	 */
	private SocketLink client1;
	private String phaRxNo;//===pangben 2013-5-17 药品审核界面添加跑马灯处方签数据 
	//医保共享信息
	private DispatchPtr app = null;
	public TParm resultData;//======pangben 2014-1-20 查询建行数据，点击保存暂存操作实现不读医疗卡提示保存成功


	private String tempPath;//add by huangjw
	private String serverPath;//add by huangjw

	public String opdUnFlg = null; 

	private String triageNo = null ;
	//	private ODOMainDrools odoMainDrools;
	private CDSSStationDrools odoMainDrools = new CDSSStationDosntWork();
		
	/**
	 * 诊间对应药房
	 */
	public void onInitParameter() {
	}

	/**
	 * 初始化方法
	 */
	public void onInit() { 
		//初始化个人权限  == zhanglei 20171201 add
		onInitPopemed();
		tempPath = "C:\\JavaHisFile\\temp\\pdf";
		serverPath = "";
		File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}

		ekt_lable = (TLabel) this.getComponent("LBL_EKT_MESSAGE");//获取显示医疗卡状态标签
		ekt_lable.setForeground(red);//======yanjing 2013-06-14设置读卡颜色
		tblPat = (TTable) this.getComponent("TABLEPAT");
		t = (TComboSession) this.getComponent("SESSION_CODE");
		clinicroom = (TComboBox) this.getComponent("CLINICROOM");
		dr = (TComboBox) this.getComponent("INSTEAD_DR");

		//Evan
		enterRoute = (TComboBox) this.getComponent("ENTERROUTE");
		pathKind = (TComboBox) this.getComponent("PATHKIND");

		insteadDept = (TComboBox) this.getComponent("INSTEAD_DEPT");
		admType = (String) this.getParameter();
		if(admType.equals("O")){
			this.callFunction("UI|singledise|setVisible", false);//add by wanglong 20121119
			this.callFunction("UI|cisQuery|setVisible", false);//wanglong add 20150519
			this.callFunction("UI|outReturn|setVisible", false);
			this.callFunction("UI|erdTriage|setVisible", false);
		}
		t.setAdmType(admType);
		t.onQuery();
		super.onInit(); 
		// 注册事件
		onInitEvent();
		// 初始化病患列表
		initOPD();
		// 初始化门急别
		initOE();
		// 判断科室权限
		if (this.getPopedem("DEPT_POPEDEM"))
			onSelectPat("INSTEAD_DEPT");
		else
			// 给TABLE放数据
			onSelectPat("");
		// if (rsnMed) {
		// initReasonbledMed();
		// }
		warnFlg = Integer.parseInt(TConfig.getSystemValue("WarnFlg"));
		enforcementFlg = "Y".equals(TConfig.getSystemValue("EnforcementFlg"));
		if (passIsReady) {
			if (!initReasonbledMed()) {
				this.messageBox("合理用药初始化失败！");
			}
		}
		initInstradCombo();
		SynLogin("1"); // 叫号登陆
		regSysEFFParm=REGTool.getInstance().getRegParm();//pangben 2013-4-28 挂号有效天数
		if(CDSSStationDrools.isCdssOnO(Operator.getRegion())){
			odoMainDrools = new ODOMainDrools(this);
		}
		/*//调医保接口
		try {		
		    if (app == null){
		    	Ole32.CoInitialize();
				app = new DispatchPtr("PB90.n_yhinterface");
				    }		    
			} catch (Exception e) {
				e.printStackTrace();
			}*/

		setMenuScroll();
		setMenuItem(this.admType);
	}
	/** 
	* 初始化个人权限   == zhanglei 20171201 add
	*/ 
	public void onInitPopemed(){ 
		TParm parm = SYSOperatorTool.getUserPopedem(Operator.getID(), getUITag()); 
		for (int i = 0; i < parm.getCount(); i++) { 
		this.setPopedem(parm.getValue("AUTH_CODE", i), true); 
		} 
	}
	/**
	 * 设置菜单
	 * @param admType
	 */
	public void setMenuItem(String admType){
		TMenuItem bloodApply= (TMenuItem) this.getComponent("bloodApply");
		TMenuItem diag= (TMenuItem) this.getComponent("diag");

		TMenuItem Observation= (TMenuItem) this.getComponent("Observation");
		TMenuItem dcis= (TMenuItem) this.getComponent("dcis");
		TMenuItem level= (TMenuItem) this.getComponent("erdTriage");
		TMenuItem temperRpt= (TMenuItem) this.getComponent("temperRpt");
		TMenuItem orderList= (TMenuItem) this.getComponent("orderList");
		if(admType.equals("O")){
			dcis.setVisible(false);
			Observation.setVisible(false);
			level.setVisible(false);
			temperRpt.setVisible(false);
			orderList.setVisible(false);
			bloodApply.setVisible(true);
			diag.setVisible(true);
		}else{
			dcis.setVisible(true);
			Observation.setVisible(true);
			level.setVisible(true);
			temperRpt.setVisible(true);
			orderList.setVisible(true);
			bloodApply.setVisible(false);
			diag.setVisible(false);
		}
	}
	/**
	 * 初始化合理用药
	 */
	public boolean initReasonbledMed() {
		try {
			if (PassDriver.init() == 0) {
				return false;
			}
			// 合理用药初始化
			if (PassDriver.PassInit(Operator.getName(), Operator.getDept(), 10) == 0) {
				return false;
			}
			// 合理用药控制参数
			if (PassDriver.PassSetControlParam(1, 2, 0, 2, 1) == 0) {
				return false;
			}
		} catch (UnsatisfiedLinkError e1) {
			return false;
		} catch (NoClassDefFoundError e2) {
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		unLockPat(); // 解锁
		pat = null;
		reg = null;
		odo = null;
		opdUnFlg = null;
		this.setValue("CHN_DSNAME", false);
		this.setValue("W_FLG", true);
		// this.setValue("FAMILY_HISTORY", NULLSTR);
		this.setValue("PREMATURE_FLG", false);
		this.setValue("HANDICAP_FLG", false);
		this.setValue("ORDER_ALLERGY", true);
		selPhaClass();
		for (int i = 0; i < OPBTool.getInstance().controlNameAmt.length; i++) {//====pangben 2013-5-2
			this.setValue(OPBTool.getInstance().controlNameAmt[i], 0.0);
		}
		for (int i = 0; i < OPBTool.getInstance().controlName.length; i++) {//====pangben 2013-5-2
			this.setValue(OPBTool.getInstance().controlName[i], NULLSTR);
		}
		String[] nullList = new String[] {};
		TComboBox combo=null;
		for (int i = 0; i < OPBTool.getInstance().controlNameCombo.length; i++) {//====pangben 2013-5-2
			combo = (TComboBox) this.getComponent(OPBTool.getInstance().controlNameCombo[i]);
			combo.setVectorData(nullList);
		}
		TTable table =null;
		for (int i = 0; i <  OPBTool.getInstance().controlNameTable.length; i++) {//====pangben 2013-5-2
			table = (TTable) this.getComponent(OPBTool.getInstance().controlNameTable[i]);
			table.removeRowAll();
		}
		word.onNewFile();
		word.update();
		ektReadParm=null;// =======pangben 20120802 
		familyWord.onNewFile();
		familyWord.update();
		ektExeConcel = false;// 医疗卡弹出界面取消按钮以后操作
		this.setValue("LBL_EKT_MESSAGE", "未读卡");//====pangben 2013-3-19 初始化读卡状态
		ekt_lable.setForeground(red);//======yanjing 2013-06-14设置读卡颜色


	}

	/**
	 * 注册控件的事件
	 */
	public void onInitEvent() {
		// 病患列表点击事件
		// ((TTable)getComponent(TABLEPAT)).addEventListener(TTableEvent.DOUBLE_CLICKED,
		// this, "onTablePatDoubleClick");
		mp = (TMovePane) callFunction("UI|MOV_MAIN|getThis");
		// 诊断的事件
		tblDiag = (TTable) this.getComponent(TABLEDIAGNOSIS);
		tblDiag.addEventListener(TABLEDIAGNOSIS + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onDiagTableChangeValue");
		tblDiag.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		tblDiag.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onDiagCreateEditComponent");
		// 医嘱的事件
		tblExa = (TTable) this.getComponent(TABLE_EXA);
		tblExa.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onExaCreateEditComponent");
		tblExa.addEventListener(TABLE_EXA + "->" + TTableEvent.CHANGE_VALUE,
				this, "onExaValueChange");
		tblExa.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		// 诊疗项目TABLE
		tblOp = (TTable) this.getComponent(TABLE_OP);
		tblOp.addEventListener(TABLE_OP + "->" + TTableEvent.CHANGE_VALUE,
				this, "onOpValueChange");
		tblOp.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onOpCreateEditComponent");
		tblOp.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		// 西药TABLE
		tblMed = (TTable) this.getComponent(TABLE_MED);
		tblMed.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onMedCreateEditComponent");
		tblMed.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		tblMed.addEventListener(TABLE_MED + "->" + TTableEvent.CHANGE_VALUE,
				this, "onMedValueChange");
		// 院前用药TABLE add by huangtt 20151201
		tblPreMed = (TTable) this.getComponent(TABLE_PRE_MED);
		tblPreMed.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onPreMedCreateEditComponent");
		tblPreMed.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");

		// 中药TABLE
		tblChn = (TTable) this.getComponent(TABLE_CHN);
		tblChn.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onChnCreateEditComponent");
		tblChn.addEventListener(TABLE_CHN + "->" + TTableEvent.CHANGE_VALUE,
				this, "onChnValueChange");
		// 管制药品TABLE
		tblCtrl = (TTable) this.getComponent(TABLE_CTRL);
		tblCtrl.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCtrlCreateEditComponent");
		tblCtrl.addEventListener(TABLE_CTRL + "->" + TTableEvent.CHANGE_VALUE,
				this, "onCtrlValueChange");
		tblCtrl.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		// 既往史TABLE
		tblMedHistory = (TTable) this.getComponent(TABLEMEDHISTORY);
		tblMedHistory.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onMedHistoryCreateEditComponent");
		tblMedHistory.addEventListener(TABLEMEDHISTORY + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onMedHistoryChangeValue");
		// 过敏史TABLE
		tblAllergy = (TTable) this.getComponent(TABLEALLERGY);
		tblAllergy.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onAllergyCreateEditComponent");
		tblAllergy.addEventListener(TABLEALLERGY + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onAllergyChangeValue");

		word = (TWord) this.getComponent("TWORD");
		familyWord = (TWord) this.getComponent("FAMILY_WORD");
	}

	/**
	 * 主诊断点选事件，判断是否可作主诊断
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onDiagMain(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		return false;
	}

	/**
	 * 诊断、既往史、过敏史TABLE行点击事件
	 * 
	 * @param tag
	 *            String
	 */
	public void onTableClick(String tag) {

		tableName = tag;

		TTable table = (TTable) this.getComponent(tableName);
		// table.acceptText();
		table.getTable().grabFocus();
		if (this.TABLEDIAGNOSIS.equalsIgnoreCase(tag)
				|| this.TABLEALLERGY.equalsIgnoreCase(tag)
				|| this.TABLEMEDHISTORY.equalsIgnoreCase(tag)
				|| this.TABLEPAT.equalsIgnoreCase(tag)) {
			return;
		}
		int row = table.getSelectedRow();

		OpdOrder order = odo.getOpdOrder();

		// zhangyong20110311
		String filter = ((TTable) this.getComponent(tableName)).getFilter();
		if (filter == null || "null".equals(filter)) {
			filter = "";
		}
		if (!tag.equals(TABLE_CHN)) {
			order.setFilter(filter);
			order.filter();

		}
		// 状态条
		//  ===============chenxi  医嘱提示问题 过敏药物的提示
		String orderCode =order.getItemString(row, "ORDER_CODE");
		String sql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC," +
				"DESCRIPTION,SPECIFICATION,REMARK_1,REMARK_2,DRUG_NOTES_DR FROM SYS_FEE" +
				" WHERE ORDER_CODE = '" +orderCode+ "'" ;
		TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql)) ;
		sqlparm = sqlparm.getRow(0);
		callFunction(
				"UI|setSysStatus",
				sqlparm.getValue("ORDER_CODE") + " " + sqlparm.getValue("ORDER_DESC")
				+ " " + sqlparm.getValue("GOODS_DESC") + " "
				+ sqlparm.getValue("DESCRIPTION") + " "
				+ sqlparm.getValue("SPECIFICATION") + " "
				+ sqlparm.getValue("REMARK_1") + " "
				+ sqlparm.getValue("REMARK_2") + " "
				+ sqlparm.getValue("DRUG_NOTES_DR"));
	}

	/**
	 * 西药，管制药品，处置的checkBox事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onCheckBox(Object obj) {

		TTable table = (TTable) obj;
		table.acceptText();
		table.setDSValue();
		return false;

	}

	/**
	 * 既往史改变事件
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onMedHistoryChangeValue(TTableNode tNode) {
		TTable table = (TTable) this.getComponent(TABLEMEDHISTORY);
		int row = table.getSelectedRow();
		if (row < 0)
			return true;
		int column = tNode.getColumn();
		String columnName = table.getParmMap(column);
		if ("ICD_DESC".equalsIgnoreCase(columnName)) {
			tNode.setValue("");
			return false;
		}
		return false;
	}
	/**
	 * 西药值改变事件 共用
	 * @param inRow
	 * @param execDept
	 * @param orderCodeFinal
	 * @param columnNameFinal
	 */
	private void setOnMedValueChange(final int inRow,final String execDept,
			final String orderCodeFinal,final String columnNameFinal){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					if (!checkStoreQty(inRow, execDept, orderCodeFinal,
							columnNameFinal)) {
						return ;
					}

				} catch (Exception e) {
				}
			}
		});
	}
	/**
	 * 西药值改变事件，算价钱
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onMedValueChange(TTableNode tNode) {
		TTable table = (TTable) this.getComponent("TABLEMED");
		int column = tNode.getColumn();
		//		int row = table.getSelectedRow();
		int row = tNode.getRow();

		String columnName = table.getParmMap(column);
		OpdOrder order = odo.getOpdOrder();
		// for (int i = 0; i < array.length; i++) {
		//
		// }
		//物联网修改
		TParm spcReturn=null;
		boolean flg=true;//管控物联网删除、修改医嘱操作
		if (Operator.getSpcFlg().equals("Y")) {
			TParm spcParm = new TParm();
			spcParm.setData("CASE_NO", caseNo);
			String rxNo = order.getRowParm(row).getValue("RX_NO");
			String seqNo = order.getRowParm(row).getValue("SEQ_NO");
			spcParm.setData("RX_NO",rxNo);
			spcParm.setData("SEQ_NO", seqNo);
			spcReturn = TIOM_AppServer.executeAction(//获得此医嘱的药品状态
					"action.opb.OPBSPCAction", "getPhaStateReturn", spcParm);
			//			if (spcReturn.getValue("PhaDosageCode").length() > 0
			//					&& spcReturn.getValue("PhaRetnCode").length() == 0) {//没有退药，已经审核，不可以删除修改操作
			//				  flg=true;// 已经退药的医嘱删除可以执行
			//			}else{
			//				// ==pangben 2013-5-14// 添加如果是已经退药的医嘱，勾选操作flg=true其他操作flg=false
			//				if ("FLG".equalsIgnoreCase(columnName)) {
			//					flg = false;
			//				} else {
			//					flg = true;
			//				}
			//			}
			if (!this.checkDrugCanUpdate(order, "MED", row, flg,spcReturn)) { // 判断是否可以修改（有没有进行审,配,发）
				if(spcReturn.getValue("PhaRetnCode").length()>0)
					this.messageBox("已经退药,请删除处方签操作");//===pangben 2013-7-17 修改物联网提示消息
				else
					this.messageBox("E0189");
				return true;
			}
		}else{
			//add by huangtt 20150603
			if (!"FLG".equalsIgnoreCase(columnName)) {
				if (checkPhaAll(order, row, "MED",true))//开立医嘱校验处方签是否可以开立
					return true;
			}
		}
		//物联网修改

		if (checkPha(order, row, "MED",flg))
			return true;
		if (!deleteOrder(order, row, "已打票,不能修改或删除医嘱","此医嘱已经登记,不能删除")) {// pangben ====2012
			// 2-28//pangben2012 2-28
			return true;
		}

		if ("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName)) {
			tNode.setValue("");
			return true;
		}

		//add by huangtt 20160908  已计费不让修改数据，使用钱数发生变化
		if (("MEDI_QTY".equalsIgnoreCase(columnName) || // 用量
				"DISPENSE_QTY".equalsIgnoreCase(columnName) || //总量
				"FREQ_CODE".equalsIgnoreCase(columnName) || // 频次
				"GIVEBOX_FLG".equalsIgnoreCase(columnName) || // 盒发药
				"TAKE_DAYS".equalsIgnoreCase(columnName))) {

			if(order.getItemString(row, "BILL_FLG").equals("Y")){
				this.messageBox("已计费，不允许修改!");
				return true;
			}
		}


		//========pangben 2012-7-18 备用药品已经收费不可以修改
		if ("RELEASE_FLG".equalsIgnoreCase(columnName)) {
			TParm parm=order.getRowParm(row);
			if (odo.getOpdOrder().getOrderCodeIsBillFlg(parm)) {
				this.messageBox("已收费,不可以修改备药操作");
				return true;	
			}
		}
		order.getRowParm(row);
		String orderCode = order.getItemString(row, "ORDER_CODE");
		double mediQty = TypeTool.getDouble(tNode.getValue());
		final int inRow = row;
		final String execDept = this.getValueString("MED_RBORDER_DEPT_CODE");
		final String orderCodeFinal = order.getItemString(row, "ORDER_CODE");
		final String columnNameFinal = columnName;
		// 日份 列
		if ("TAKE_DAYS".equalsIgnoreCase(columnName)) {
			//			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
			//				return true;
			//			}
			int day = SYSAntibioticTool.getInstance().getAntibioticTakeDays(
					orderCode);
			if (day > 0 && TypeTool.getInt(tNode.getValue()) > day) {
				if (messageBox(
						"提示信息/Tip",
						"超过抗生素类天数限制,是否继续开立?\r\nThe days you ordered is more than the standard days of antiVirus.Do you proceed anyway?",
						this.YES_NO_OPTION) == 1) {
					table.setDSValue(row);
					return true;
				}
			}

			setOnMedValueChange(inRow, execDept, orderCodeFinal, columnNameFinal);
		} else if ("EXEC_DEPT_CODE".equalsIgnoreCase(columnName)) { // 执行科室 列
			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
				return true;
			}
			final String execDeptFinal = TypeTool.getString(tNode.getValue());
			setOnMedValueChange(inRow, execDeptFinal, orderCodeFinal, columnNameFinal);
		} else if ("MEDI_QTY".equalsIgnoreCase(columnName)) { // 用量
			//			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
			//				return true;
			//			}
			if (SYSCtrlDrugClassTool.getInstance().getOrderCtrFlg(orderCode)) {
				if (!SYSCtrlDrugClassTool.getInstance().getCtrOrderMaxDosage(
						orderCode, mediQty)) {
					if (messageBox(
							"提示信息/Tip",
							"超过管制药品默认用量,是否继续开立?\r\nQty of this order is over-gived.Do you proceed anyway?",
							this.YES_NO_OPTION) == 1) {
						table.setDSValue(row);
						return true;
					}
				}
			}
			setOnMedValueChange(inRow, execDept, orderCodeFinal, columnNameFinal);
		} else if ("FREQ_CODE".equalsIgnoreCase(columnName)) { // 频次
			//			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
			//				return true;
			//			}
			setOnMedValueChange(inRow, execDept, orderCodeFinal, columnNameFinal);
		} else if ("LINKMAIN_FLG".equalsIgnoreCase(columnName)) { // 连接医嘱
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
		} else if ("LINK_NO".equalsIgnoreCase(columnName)) { // 连结号
			int value = TypeTool.getInt(tNode.getValue());
			String link_main_flg = order.getItemString(row, "LINKMAIN_FLG");
			// if(oldValue==0&&value>0){
			// return true;
			// }
			if ("0".equals(tNode.getValue().toString())) {
				return true;
			}
			if ("Y".equals(link_main_flg) || value < 0) {
				return true;
			}

			if (!StringUtil
					.isNullString(order.getItemString(row, "ORDER_CODE"))) {
				int linkNo = order.getMaxLinkNo();
				if (value >= 0 && value <= linkNo) {
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else if ("ROUTE_CODE".equalsIgnoreCase(columnName)) { // 用法
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
			String routeCode = tNode.getValue() + "";
			int result = order.isMedRoute(routeCode);
			if (result == -1) {
				this.messageBox("E0019"); // 取得用法失败
				return true;
			} else if (result != 1) {
				this.messageBox("E0020"); // 西成药不能使用该用法
				return true;
			}
			// 集合医嘱子项与主项联动
			if ("Y".equals(order.getItemString(row, "LINKMAIN_FLG"))) {
				String linkNO = order.getItemString(row, "LINK_NO"); // 连结号
				String rxNo = order.getItemString(row, "RX_NO"); // 处方签号
				// 循环查找子项
				for (int i = 0; i < order.rowCount(); i++) {
					if (linkNO.equals(order.getItemString(i, "LINK_NO"))
							&& rxNo.equals(order.getItemString(i, "RX_NO"))
							&& !"".equals(order.getItemString(i, "ORDER_CODE"))
							&& !routeCode.equals(order.getItemString(i,
									"ROUTE_CODE"))) {
						order.setItem(i, "ROUTE_CODE", routeCode);
					}
				}
				table.setDSValue();
			}
			return false;
		} else {
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
		}
		this.calculateCash(TABLE_MED, "MED_AMT");
		return false;
	}

	/**
	 * 各个TABLE中值改变事件用到的后置的库存检核方法
	 * 
	 * @param row
	 *            int
	 * @param execDept
	 *            String
	 * @param orderCode
	 *            String
	 * @param columnName
	 *            String
	 * @return boolean
	 */
	public boolean checkStoreQty(final int row, final String execDept,
			final String orderCode, final String columnName) {
		if (StringUtil.isNullString(columnName)) {
			return true;
		}
		if (!("TAKE_DAYS".equalsIgnoreCase(columnName)
				|| "MEDI_QTY".equalsIgnoreCase(columnName)
				|| "FREQ_CODE".equalsIgnoreCase(columnName) || "EXEC_DEPT_CODE"
				.equalsIgnoreCase(columnName))) {
			return true;
		}
		if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 校验物联网注记
			OpdOrder order = odo.getOpdOrder();
			order.showDebug();
			double dosageQty = TypeTool.getDouble(order.getItemData(row,
					"DOSAGE_QTY"));
			if(orderCode.length()<=0)//==pangben 2013-5-2 当前没有选择的医嘱不执行校验库存操作
				return true;
			if (isCheckKC(orderCode)) // 判断是否是“药品备注”
				// 物联网
				if (!INDTool.getInstance().inspectIndStock(execDept, orderCode,
						dosageQty)) {
					// this.messageBox("E0052"); // 库存不足
					// $$==========add by lx 2012-06-19加入存库不足，替代药提示
					TParm inParm = new TParm();
					inParm.setData("orderCode", orderCode);
					this.openDialog("%ROOT%\\config\\pha\\PHAREDrugMsg.x",
							inParm);
					return false;
				}
		}
		return true;
	}

	/**
	 * 检验检查值改变事件
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onExaValueChange(TTableNode tNode) {
		int column = tNode.getColumn();
		int row = tNode.getRow();
		String colName = tNode.getTable().getParmMap(column);
		if ("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(colName)) {
			tNode.setValue("");
		}
		OpdOrder order = odo.getOpdOrder();
		if (order == null) {
			return true;
		}
		if (!deleteOrder(order, row, "已打票,不能修改或删除医嘱","此医嘱已经登记,不能删除")) {// pangben ====2012 2-28
			return true;
		}

		if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
			return true;
		}
		if(!checkExa(order, row)){//===pangben 2013-4-28删除勾选操作，校验是否到检
			return true;
		}
		// if(column>0){
		// tNode.getTable().acceptText();
		// // order.getLabNo(row, odo);
		// }

		return false;
	}

	/**
	 * 处置值改变事件，计算价钱
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onOpValueChange(TTableNode tNode) {
		int row = tNode.getRow();
		TTable table = (TTable) this.getComponent("TABLEOP");
		String columnName = table.getParmMap(tNode.getColumn());
		OpdOrder order = odo.getOpdOrder();
		if (!deleteOrder(order, row, "已打票,不能修改或删除医嘱","此医嘱已经登记,不能删除")) {// pangben ====2012 2-28
			return true;
		}
		if ("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName)) {
			tNode.setValue("");
			return false;
		} else {
			String orderCode = odo.getOpdOrder().getItemString(row,
					"ORDER_CODE");

			if ("LINK_NO".equalsIgnoreCase(columnName)) {
				int value = TypeTool.getInt(tNode.getValue());
				int oldValue = TypeTool.getInt(tNode.getOldValue());
				if (oldValue == 0 && value > 0) {
					return true;
				}
			}
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
		}

		//add by huangtt 20160908  已计费不让修改数据，使用钱数发生变化
		if (("MEDI_QTY".equalsIgnoreCase(columnName) || // 用量
				"DISPENSE_QTY".equalsIgnoreCase(columnName) || //总量
				"FREQ_CODE".equalsIgnoreCase(columnName) || // 频次
				"TAKE_DAYS".equalsIgnoreCase(columnName))) {

			if(order.getItemString(row, "BILL_FLG").equals("Y")){
				this.messageBox("已计费，不允许修改!");
				return true;
			}
		}



		// 集合医嘱 主项修改细项要随之修改
		if (("MEDI_QTY".equalsIgnoreCase(columnName) || // 用量				
				"FREQ_CODE".equalsIgnoreCase(columnName) || // 频次
				"TAKE_DAYS".equalsIgnoreCase(columnName))) { // 日份
			// 判断该行数据是否是集合医嘱主项 如果是主项 那么循环修改细项
			if ("Y".equalsIgnoreCase(order.getItemString(row, "SETMAIN_FLG"))) {
				String rxNo = order.getItemString(row, "RX_NO");
				String ordersetCode = order.getItemString(row, "ORDER_CODE");
				String orderSetGroup = order.getItemString(row,
						"ORDERSET_GROUP_NO");
				for (int i = 0; i < order.rowCountFilter(); i++) {
					// 判断是否是数据改主项的细项
					if (rxNo.equals(order.getItemData(i, "RX_NO", order.FILTER))
							&& ordersetCode.equals(order.getItemData(i,
									"ORDERSET_CODE", order.FILTER))
							&& orderSetGroup.equals(TypeTool.getString(order
									.getItemData(i, "ORDERSET_GROUP_NO",
											order.FILTER)))) {
						if ("MEDI_QTY".equalsIgnoreCase(columnName)) {
							order.setItem(i, "MEDI_QTY", tNode.getValue(),
									order.FILTER);
						}
						if ("FREQ_CODE".equalsIgnoreCase(columnName))
							order.setItem(i, "FREQ_CODE", tNode.getValue(),
									order.FILTER);


						if ("TAKE_DAYS".equalsIgnoreCase(columnName)) {
							// this.messageBox_(tNode.getValue());
							order.setItem(i, "TAKE_DAYS", tNode.getValue(),
									order.FILTER);
						}
					}
				}
			}
		}
		this.calculateCash(TABLE_OP, "OP_AMT");
		return false;
	}

	/**
	 * 管制药品值改变事件，计算价钱
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onCtrlValueChange(TTableNode tNode) {
		int column = tNode.getColumn();
		TTable table = (TTable) this.getComponent("TABLECTRL");
		int row = tNode.getRow();
		OpdOrder order = odo.getOpdOrder();
		if (!order.checkDrugCanUpdate("EXA", row)) { // 判断是否可以修改（有没有进行审,配,发）
			this.messageBox("E0189");
			return true;
		}
		if (checkPha(order, row, "EXA",true)) {
			return true;
		}
		if (!deleteOrder(order, row, "已打票,不能修改或删除医嘱","此医嘱已经登记,不能删除")) {// pangben ====2012 2-28
			return true;
		}
		String columnName = table.getParmMap(column);

		if ("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName)) {
			tNode.setValue("");
			return false;
		}


		//add by huangtt 20160908  已计费不让修改数据，使用钱数发生变化
		if (("MEDI_QTY".equalsIgnoreCase(columnName) || // 用量
				"DISPENSE_QTY".equalsIgnoreCase(columnName) || //总量
				"FREQ_CODE".equalsIgnoreCase(columnName) || // 频次
				"GIVEBOX_FLG".equalsIgnoreCase(columnName) || // 盒发药
				"TAKE_DAYS".equalsIgnoreCase(columnName))) {

			if(order.getItemString(row, "BILL_FLG").equals("Y")){
				this.messageBox("已计费，不允许修改!");
				return true;
			}
		}


		// TParm parm = odo.getOpdOrder().getRowParm(table.getSelectedRow());
		String orderCode = odo.getOpdOrder().getItemString(row, "ORDER_CODE");
		double mediQty = StringTool.getDouble(tNode.getValue() + "");
		final int inRow = row;
		final String execDept = this.getValueString("MED_RBORDER_DEPT_CODE");
		final String orderCodeFinal = odo.getOpdOrder().getItemString(row,
				"ORDER_CODE");
		final String columnNameFinal = columnName;
		if ("MEDI_QTY".equalsIgnoreCase(columnName)) {
			if (SYSCtrlDrugClassTool.getInstance().getOrderCtrFlg(orderCode)) {
				if (!SYSCtrlDrugClassTool.getInstance().getCtrOrderMaxDosage(
						orderCode, mediQty)) {
					if (messageBox(
							"提示信息/Tip",
							"超过管制药品默认用量,是否继续开立?\r\nQty of this order is over-gived.Do you proceed anyway?",
							this.YES_NO_OPTION) == 1) {
						table.setDSValue(row);
						return true;
					}
				}
			}
			setOnMedValueChange(inRow, execDept, orderCodeFinal, columnNameFinal);
		} else if ("TAKE_DAYS".equalsIgnoreCase(columnName) || "FREQ_CODE".equalsIgnoreCase(columnName)) {
			setOnMedValueChange(inRow, execDept, orderCodeFinal, columnNameFinal);
		} else if ("LINKMAIN_FLG".equalsIgnoreCase(columnName)) {
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
		} else if ("LINK_NO".equalsIgnoreCase(columnName)) {
			int value = TypeTool.getInt(tNode.getValue());
			int oldValue = TypeTool.getInt(tNode.getOldValue());
			if (oldValue == 0 && value > 0) {
				return true;
			}
			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
				if (value > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else if ("ROUTE_CODE".equalsIgnoreCase(columnName)) {
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
			String routeCode = tNode.getValue() + "";
			int result = order.isMedRoute(routeCode);
			if (result == -1) {
				this.messageBox("E0019"); // 取得用法失败
				return true;
			} else if (result != 1) {
				this.messageBox("E0022"); // 管制药品不能使用该用法
				return true;
			}
			return false;
		}
		// table.setItem(row,columnName,tNode.getValue());
		// 4,8,12,13,16,17,18,21,22
		this.calculateCash(TABLE_CTRL, "CTRL_AMT");
		// table.setDSValue(table.getSelectedRow());
		return false;
	}

	/**
	 * 
	 * @param order
	 * @param row
	 * @param name
	 * @param spcFlg
	 * @return
	 */
	private boolean checkPhaAll(OpdOrder order, int row, String name,boolean spcFlg){
		if (null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0) {
			// this.messageBox("未确认身份,请读医疗卡");
			// return;
		} else {
			for (int i = 0; i < order.rowCount(); i++) {
				if (StringUtil.isNullString(order.getItemString(i,
						"ORDER_CODE"))) {
					continue;
				}
				if (order.getItemData(row, "RX_NO").equals(
						order.getItemData(i, "RX_NO"))) {
					TParm parm = new TParm();
					if (!order.checkDrugCanUpdate(name, i, parm, true)) { // 判断是否可以修改（有没有进行审,配,发）
						this.messageBox(parm.getValue("MESSAGE"));
						return true;
					}
				}
			}

		}
		return false;
	}

	/**
	 * 校验这个处方签的全部药品是否已经发药
	 * 
	 * @param order
	 * @param row
	 * @return
	 */
	private boolean checkPha(OpdOrder order, int row, String name,boolean spcFlg) {
		if (null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0) {
			// this.messageBox("未确认身份,请读医疗卡");
			// return;
		} else {
			if (order.getItemData(row, "BILL_FLG").equals("Y")) { 
				for (int i = 0; i < order.rowCount(); i++) {
					if (StringUtil.isNullString(order.getItemString(i,
							"ORDER_CODE"))) {
						continue;
					}
					if (order.getItemData(row, "RX_NO").equals(
							order.getItemData(i, "RX_NO"))) {
						//物联网修改
						if (Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 校验物联网注记
							if (!this.checkDrugCanUpdate(order, name, i, spcFlg,null)) { // 判断是否可以修改（有没有进行审,配,发）
								this.messageBox("此处方签有药品已审核或发药,不能删除或修改医嘱!");
								return true;
							}
							// 物联网修改
						}else {

							if (!order.checkDrugCanUpdate(name, i)) { // 判断是否可以修改（有没有进行审,配,发）
								this.messageBox("此处方签有药品已审核或发药,不能删除或修改医嘱!");
								return true;
							}


						}
					}
				}

			}
		}
		return false;
	}
	/**
	 * 检验检查校验
	 * @param order
	 * @param row
	 * @param name
	 * @return
	 */
	private boolean checkExa(OpdOrder order, int row){
		if (null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0) {
			// this.messageBox("未确认身份,请读医疗卡");
			// return;
		} else {
			if (order.getItemData(row, "BILL_FLG").equals("Y")) {

				for (int i = 0; i < order.rowCount(); i++) {
					if (StringUtil.isNullString(order.getItemString(i,
							"ORDER_CODE"))) {
						continue;
					}
					if (order.getItemData(row, "RX_NO").equals(
							order.getItemData(i, "RX_NO"))) {
						if (!"PHA".equals(order.getItemData(row, "CAT1_TYPE"))
								&& "Y".equals(order.getItemData(row, "EXEC_FLG"))) {
							this.messageBox("已到检,不能修改或删除操作!");
							return false;
						}
					}
				}

			}
		}
		return true;
	}
	/**
	 * 中医TABLE值赋值操作
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	private boolean setOnChnValueChange(TTableNode tNode,OpdOrder order,int realrow,int row,final String execDept,final String columnNameFinal ){
		if (chaCheck(order, realrow, row)) {
			return true;
		}

		//add by huangtt 20160908 
		if(order.getItemString(realrow, "BILL_FLG").equals("Y")){
			this.messageBox("已计费，不允许修改");
			return true;
		}


		final int inRow = realrow;
		final String orderCodeFinal = order.getItemString(realrow,
				"ORDER_CODE");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					if (!checkStoreQty(inRow, execDept, orderCodeFinal,
							columnNameFinal)) {

						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		order.setItem(realrow, "MEDI_QTY", TCM_Transform.getDouble(tNode
				.getValue()));
		return false;
	}
	/**
	 * 中医TABLE值赋值操作
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	private boolean setOnchnValueChange(TTableNode tNode,OpdOrder order,int realrow,int row){
		if (chaCheck(order, realrow, row)) {
			return true;
		}
		String orderCode = order.getItemString(realrow, "ORDER_CODE");
		if (StringUtil.isNullString(orderCode)) {
			return true;
		}
		order.setItem(realrow, "DCTEXCEP_CODE", TCM_Transform
				.getString(tNode.getValue()));
		return false;
	}
	/**
	 * 中医TABLE值改变事件
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onChnValueChange(TTableNode tNode) {
		int column = tNode.getColumn();
		int row = tNode.getRow();
		String colName = tNode.getTable().getParmMap(column);
		// ORDER_DESC1;MEDI_QTY1;DCTEXCEP_CODE1;ORDER_DESC2;MEDI_QTY2;DCTEXCEP_CODE2;ORDER_DESC3;MEDI_QTY3;DCTEXCEP_CODE3;ORDER_DESC4;MEDI_QTY4;DCTEXCEP_CODE4
		if ("ORDER_DESC1".equalsIgnoreCase(colName)
				|| "ORDER_DESC2".equalsIgnoreCase(colName)
				|| "ORDER_DESC3".equalsIgnoreCase(colName)
				|| "ORDER_DESC4".equalsIgnoreCase(colName)) {
			tNode.setValue("");
			return true;
		}

		OpdOrder order = odo.getOpdOrder();
		String rxNo = this.getValueString(CHN_RX);
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		int realrow;
		final String execDept = this.getValueString("CHN_EXEC_DEPT_CODE");
		final String columnNameFinal = "MEDI_QTY";
		switch (column) {
		case 1:
			realrow = row * 4 + 0;
			if(setOnChnValueChange(tNode, order, realrow, realrow, execDept, columnNameFinal)){
				return true;
			}
			break;
		case 4:
			realrow = row * 4 + 1;
			if(setOnChnValueChange(tNode, order, realrow, realrow, execDept, columnNameFinal)){
				return true;
			}
			break;
		case 7:
			realrow = row * 4 + 2;
			if (chaCheck(order, realrow, row)) {
				return true;
			}
			if(setOnChnValueChange(tNode, order, realrow, realrow, execDept, columnNameFinal)){
				return true;
			}
			break;
		case 10:
			realrow = row * 4 + 3;
			if(setOnChnValueChange(tNode, order, realrow, realrow, execDept, columnNameFinal)){
				return true;
			}
			break;
		case 2:
			realrow = row * 4 + 0;
			if(setOnchnValueChange(tNode, order, realrow, realrow)){
				return true;
			}
			break;
		case 5:
			realrow = row * 4 + 1;
			if(setOnchnValueChange(tNode, order, realrow, realrow)){
				return true;
			}
			break;
		case 8:
			realrow = row * 4 + 2;
			if(setOnchnValueChange(tNode, order, realrow, realrow)){
				return true;
			}
			break;
		case 11:
			realrow = row * 4 + 3;
			if(setOnchnValueChange(tNode, order, realrow, realrow)){
				return true;
			}
			break;

		}

		setChnPckTot();
		this.calculateChnCash(rxNo);
		this.initChnTable(rxNo);
		TTable table = (TTable) this.getComponent("TABLECHN");
		table.getTable().grabFocus();
		table.setSelectedRow(row);
		int nextColumn = -1;
		if (column < 3 && column > -1) {
			nextColumn = 2;
		} else if (column < 6 && column > 2) {
			nextColumn = 5;
		} else if (column < 9 && column > 5) {
			nextColumn = 8;
		} else {
			nextColumn = 11;
		}
		table.setSelectedColumn(nextColumn);
		return false;
	}

	/**
	 * 中药医嘱校验
	 * 
	 * @param order
	 * @param realrow
	 * @param row
	 * @return
	 */
	private boolean chaCheck(OpdOrder order, int realrow, int row) {
		TParm parm = new TParm();
		if (!order.checkDrugCanUpdate("CHN", realrow, parm, true))// 判断是否可以修改（有没有进行审,配,发）
		{
			messageBox(parm.getValue("MESSAGE"));
			return true;
		}

		if (checkPha(order, row, "CHN",true)) {
			return true;
		}


		if (!deleteOrder(order, row, "已打票,不能修改或删除医嘱","此医嘱已经登记,不能删除")) {// pangben ====2012
			// 2-28//pangben
			// ====2012 2-28
			return true;
		}
		return false;
	}

	/**
	 * 设置中药显示的总克数
	 */
	public void setChnPckTot() {
		long amt = 0;
		OpdOrder order = odo.getOpdOrder();
		for (int i = 0; i < order.rowCount(); i++) {
			amt += order.getItemDouble(i, "MEDI_QTY");
		}
		for (int i = 0; i < order.rowCount(); i++) {
			order.setItem(i, "PACKAGE_TOT", amt);
		}
		this.setValue("PACKAGE_TOT", amt);
	}

	/**
	 * 过敏史值改变事件
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onAllergyChangeValue(TTableNode tNode) {
		int column = tNode.getColumn();
		int row = tNode.getRow();
		TTable table = (TTable) this.getComponent(this.TABLEALLERGY);
		String columnName = table.getParmMap(column);
		if ("DRUGORINGRD_DESC".equalsIgnoreCase(columnName)) {
			tNode.setValue("");
			return false;
		}
		if (!"ALLERGY_NOTE".equalsIgnoreCase(columnName)) {
			return true;
		}
		if (StringUtil.isNullString(odo.getDrugAllergy().getItemString(row,
				"DRUGORINGRD_CODE"))) {
			return true;
		}
		return false;
	}

	/**
	 * 诊断TABLE值改变事件
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onDiagTableChangeValue(TTableNode tNode) {
		int column = tNode.getColumn();
		String colName = tNode.getTable().getParmMap(column);

		if ("ICD_DESC".equalsIgnoreCase(colName)) {
			tNode.setValue("");
		}
		int row = tNode.getRow();
		if ("MAIN_DIAG_FLG".equalsIgnoreCase(colName)) {
			if (StringUtil.isNullString(odo.getDiagrec().getItemString(row,
					"ICD_CODE"))) {
				odo.getDiagrec().setItem(row, "MAIN_DIAG_FLG", "N");
				return true;
			}
			int[] oldMain = new int[1];
			boolean isHavingMain = odo.getDiagrec().haveMainDiag(oldMain);
			if (!isHavingMain) {
				if ("C".equalsIgnoreCase(wc)
						&& odo.getDiagrec().isSyndromFlg(
								odo.getDiagrec().getItemString(row,
										"ICD_CODE"))) {
					this.messageBox("E0018"); // 该诊断为症候，不能做为诊UP

					return true;
				} else if ("W".equalsIgnoreCase(wc)) {
					String icdCode = odo.getDiagrec().getItemString(row,
							"ICD_CODE");
					if (!odo.getDiagrec().isMainFlg(icdCode)) {
						this.messageBox("E0132"); // 该诊断不能作为主诊断
						return true;
					}
				}

			} else {
				if ("C".equalsIgnoreCase(wc)
						&& odo.getDiagrec().isSyndromFlg(
								odo.getDiagrec().getItemString(row,
										"ICD_CODE"))) {
					this.messageBox("E0018"); // 该诊断为症候，不能做为诊UP
					return true;
				} else if ("W".equalsIgnoreCase(wc)) {
					String icdCode = odo.getDiagrec().getItemString(row,
							"ICD_CODE");
					if (!odo.getDiagrec().isMainFlg(icdCode)) {
						return true;
					}
					odo.getDiagrec().setItem(oldMain[0], "MAIN_DIAG_FLG", "N");
					tNode.getTable().setDSValue(oldMain[0]);
				}
			}
		}
		return false;
	}

	/**
	 * 初始化病患列表
	 */
	public void initOPD() {
		if (this.getParameter() == null) {
			this.messageBox("E0024"); // 初始化参数失败
			return;
		}
		String sessionCode = initSessionCode();
		Timestamp admDate = TJDODBTool.getInstance().getDBTime();
		// 根据时段判断应该显示的日期（针对于晚班夸0点的问题，跨过0点的晚班应该显示前一天的日期）
		if (!StringUtil.isNullString(sessionCode)
				&& !StringUtil.isNullString(admType)) {
			admDate = SessionTool.getInstance().getDateForSession(admType,
					sessionCode, Operator.getRegion());
		}
		this.setValue("ADM_DATE", admDate);
		TParm sysparm = OPDSysParmTool.getInstance().getSysParm();
		dctMediQty = sysparm.getValue("DCT_TAKE_QTY", 0);
		dctTakeDays = sysparm.getValue("DCT_TAKE_DAYS", 0);
		dctFreqCode = sysparm.getValue("G_FREQ_CODE", 0);
		dctRouteCode = sysparm.getValue("G_ROUTE_CODE", 0);
		dctAgentCode = sysparm.getValue("G_DCTAGENT_CODE", 0);
		this.setValue("DCT_TAKE_DAYS", dctTakeDays);
		this.setValue("DCT_TAKE_QTY", dctMediQty);
		this.setValue("CHN_FREQ_CODE", OPDSysParmTool.getInstance()
				.getGfreqCode());
		this.setValue("CHN_ROUTE_CODE", OPDSysParmTool.getInstance()
				.getGRouteCode());
		this.setValue("DCTAGENT_CODE", OPDSysParmTool.getInstance()
				.getGdctAgent());
		passIsReady = SYSNewRegionTool.getInstance().isOREASONABLEMED(
				Operator.getRegion());
		mp.onDoubleClicked(false);
		initClinicRoomCombo();
		String roomNo = PatAdmTool.getInstance().getClinicRoomByRealDept(
				StringTool.getString(admDate, "yyyy-MM-dd"), sessionCode,
				admType, Operator.getID(), Operator.getDept())
				.getValue("ID", 0);
		clinicroom.setValue(roomNo);

		selectPat = true;

		isEng = "en".equalsIgnoreCase(Operator.getLanguage());
		initORadio();
	}

	/**
	 * 门急诊区别，急诊病患列表多出检伤等级，到院时间等字段
	 */
	public void initOE() {
		TButton erd = (TButton) this.getComponent("ERD");
		TButton bodyTemp = (TButton) this.getComponent("BODY_TEMP");
		TButton orderSheet = (TButton) this.getComponent("ORDER_SHEET");
		TButton erdSheet = (TButton) this.getComponent("ERD_SHEET");
		TTable table = (TTable) this.getComponent("TABLEPAT");
		// ==========pangben 2012-6-28 添加初复诊列值VISIT_CODE
		if ("O".equalsIgnoreCase(admType)) {  
			//fux modify 20131101 添加预约挂号checkBox 
			table.setHeader("诊号,40;病案号,100;姓名,80;初复诊,50,VISIT_CODE;就诊状态,80,ADM_STATUS;报告状态,80,REPORT_STATUS;预约挂号,80,BOOLEAN");
			table.setParmMap("QUE_NO;MR_NO;PAT_NAME;VISIT_CODE;ADM_STATUS;REPORT_STATUS;APPT_CODE"); 
			table.setEnHeader("QueNo;PatNo;Name;visitName;Status;ReportStatus;Appt_Code"); 
			table.setLanguageMap("PAT_NAME|PAT_NAME1");   
			table.setLockColumns("0,1,2,3,4");
			// table.setItem("ADM_STATUS;REPORT_STATUS;ERD_LEVEL;VISIT_CODE"); 
			table.setColumnHorizontalAlignmentData("0,right;1,right;2,left;3,left;4,left;5,left");
			erd.setEnabled(false);
			bodyTemp.setEnabled(false);
			orderSheet.setEnabled(false);  
			erdSheet.setEnabled(true);
			return;
		}
		//===================== modify by wanglong 20121024 =====================================
		//		table.setHeader("诊号,40;病案号,100;姓名,80;初复诊,50,VISIT_CODE;检伤等级,80,ERD_LEVEL;到院时间,120,timestamp,yyyy-MM-dd HH:mm;最新医嘱,120,timestamp,yyyy-MM-dd HH:mm;就诊状态,80,ADM_STATUS;报告状态,80,REPORT_STATUS");
		//		table.setEnHeader("QueNo;PatNo;PatName;visitName;TriageLevel;Arrived Date;Latest OrderDate;Status;ReportStatus");
		//		table.setParmMap("QUE_NO;MR_NO;PAT_NAME;VISIT_CODE;ERD_LEVEL;REG_DATE;ORDER_DATE;ADM_STATUS;REPORT_STATUS;ADM_DATE");
		/*modified by Eric 20170604 add 挂号时间，到大门时间，检伤时间*/
		table.setHeader("诊号,40;病案号,100;姓名,80;初复诊,50,VISIT_CODE;检伤等级,80;单病种,120,DISE_CODE;挂号时间,120,Timestamp,yyyy-MM-dd HH:mm;到大门时间,120,Timestamp,yyyy-MM-dd HH:mm;接诊时间,120,Timestamp,yyyy-MM-dd HH:mm;最新医嘱,120,Timestamp,yyyy-MM-dd HH:mm;就诊状态,80,ADM_STATUS;离院方式,80,DISCHG_TYPE;报告状态,80,REPORT_STATUS");
		table.setEnHeader("QueNo;PatNo;PatName;visitName;TriageLevel;Single Disease;Arrived Date;Latest OrderDate;Status;ReportStatus");
		table.setParmMap("QUE_NO;MR_NO;PAT_NAME;VISIT_CODE;LEVEL_DESC;DISE_CODE;REG_DATE;GATE_TIME;TRIAGE_TIME;ORDER_DATE;ADM_STATUS;DISCHG_TYPE;REPORT_STATUS;ADM_DATE");
		table.setLanguageMap("PAT_NAME|PAT_NAME1");
		//		table.setLockColumns("0,1,2,3,4,5,6,7,8");
		//		table.setColumnHorizontalAlignmentData("0,right;1,right;2,left;3,left;4,left;5,left;6,left;7,right;8,right");
		table.setLockColumns("all");//modify by wanglong 20121119
		table.setColumnHorizontalAlignmentData("0,right;2,left;3,left;4,left;5,left;7,left;8,left;9,left");
		//===================== modify end ======================================================
		erd.setEnabled(true);
		bodyTemp.setEnabled(true);
		orderSheet.setEnabled(true);
		erdSheet.setEnabled(true);
	}

	/**
	 * 设置SESSION combo的门急属性，并返回当前的SESSION_CODE
	 * 
	 * @return String sessionCode
	 */
	public String initSessionCode() {
		// 为了界面的SESSION_CODE显示门急诊区别，放置一个不显示的TEXTFIELD。
		String sessionCode = SessionTool.getInstance().getDefSessionNow(
				admType, Operator.getRegion());
		this.setValue("SESSION_CODE", sessionCode);
		return sessionCode;
	}

	/**
	 * 初始化诊间combo
	 */
	public void initClinicRoomCombo() {
		Timestamp admDate = (Timestamp) this.getValue("ADM_DATE");
		String sessionCode = this.getValueString("SESSION_CODE");
		TParm comboParm = PatAdmTool.getInstance().getClinicRoomForODO(
				StringTool.getString(admDate, "yyyy-MM-dd"), sessionCode,
				admType, Operator.getDept(), Operator.getID());
		if ("en".equals(this.getLanguage()))
			clinicroom.setParmMap("id:ID;name:ENG_DESC");
		else
			clinicroom.setParmMap("id:ID;name:NAME;");
		clinicroom.setParmValue(comboParm);
	}

	/**
	 * 初始化代诊暂存完成的radioButton
	 */
	public void initORadio() {
		String date = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		String sql = "SELECT CASE_NO,SEE_DR_FLG " + "	FROM REG_PATADM "
				+ "	WHERE  ADM_TYPE='" + admType + "' AND ADM_DATE=TO_DATE('"
				+ date + "','YYYY-MM-DD') " + "  AND SESSION_CODE='"
				+ this.getValue("SESSION_CODE") + "' AND REALDR_CODE='"
				+ Operator.getID() + "' AND CLINICROOM_NO='"
				+ this.getValueString("CLINICROOM") + "' AND REGION_CODE = '"
				+ Operator.getRegion() + "'" + "  AND REGCAN_USER IS NULL ";
		TParm regSysParm = REGSysParmTool.getInstance().selectdata();
		if (regSysParm.getBoolean("CHECKIN_FLG", 0)) {
			sql += "AND ARRIVE_FLG='Y'";
		}
		TDataStore radioNo = new TDataStore();
		radioNo.setSQL(sql);
		if (radioNo.retrieve() == -1) {
			this.messageBox("E0024"); // 初始化参数失败
			return;
		}
		radioNo.setFilter(" SEE_DR_FLG='N'");
		radioNo.filter();
		TLabel label = (TLabel) this.getComponent("WAIT_NO");
		label.setZhText(radioNo.rowCount() + " 人");
		label.setEnText(radioNo.rowCount() + " P(s)");
		label.changeLanguage(Operator.getLanguage());
		radioNo.setFilter(" SEE_DR_FLG='Y'");
		radioNo.filter();
		label = (TLabel) this.getComponent("DONE_NO");
		label.setZhText(radioNo.rowCount() + " 人");
		label.setEnText(radioNo.rowCount() + " P(s)");
		label.changeLanguage(Operator.getLanguage());
		radioNo.setFilter("SEE_DR_FLG='T'");
		radioNo.filter();
		label = (TLabel) this.getComponent("TEMP_NO");
		label.setZhText(radioNo.rowCount() + " 人");
		label.setEnText(radioNo.rowCount() + " P(s)");
		label.changeLanguage(Operator.getLanguage());
	}
	

	/**
	 * 筛选病患
	 * 
	 * @param type
	 *            String 表示 那个控件调用该方法
	 */
	public void onSelectPat(String type) {
		if (!selectPat) {
			selectPat = true;
			return;
		}
		//TTable table = (TTable) this.getComponent("TABLEPAT");
		String date = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		String insteadDr = this.getValueString("INSTEAD_DR");
		//String OPT_USER = Operator.getID();//当前操作人
		//zhanglei 增加权限 20171010
		boolean vvipFlg = this.getPopedem("SPECIAL_FLG");
		if (!"INSTEAD_DR".equals(type) && !"INSTEAD_DEPT".equals(type)) {

			if ("E".equalsIgnoreCase(admType)) {
				onEPatQuery();
			} else {
				//fux modify 20131101 修改REGPatAdmModule.x 下的 selDateForODOByWait
				onPatQuery();
			}
			initORadio();
			// 如果是诊间选择事件 那么不用更新诊间
			if (!"CLINICROOM".equals(type)) {
				initClinicRoomCombo();
			}
			// 如果是“时段”combo调用或者是 看诊日期事件，那么清空 诊间combo
			if ("SESSION_CODE".equals(type) || "ADM_DATE".equals(type)) {
				this.clearValue("CLINICROOM");
			}
			if (insteadDept != null) {
				initInsteadDept();
			}

			//Evan
			if (enterRoute != null) {
				initEnterRoute();
			}

			if (pathKind != null) {
				initPathKind();
			}
			tblPat.removeRowAll();
			tblPat.setParmValue(parmpat);
			tblPat.changeLanguage(Operator.getLanguage());
			selectPat = true;
		} else if ("INSTEAD_DR".equals(type) && !"".equals(insteadDr)) {
			//fux modify 20131101 修改 加入预约标记PatAdmTool
			parmpat = PatAdmTool.getInstance()
					.getInsteadPatList(date, 
							this.getValueString("SESSION_CODE"), insteadDr,
							admType, "",vvipFlg);
//			parmpat = PatAdmTool.getInstance()
//					.getInsteadPatList(date, 
//							this.getValueString("SESSION_CODE"), insteadDr,
//							admType, "");
			tblPat.removeRowAll();
			tblPat.setParmValue(parmpat);
			tblPat.changeLanguage(Operator.getLanguage()); 
			selectPat = true;
		} else if ("INSTEAD_DEPT".equals(type)
				&& !"".equals(this.getValueString("INSTEAD_DEPT"))) {
			parmpat = PatAdmTool.getInstance().getInsteadPatList(date,
					this.getValueString("SESSION_CODE"), "", admType,
					this.getValueString("INSTEAD_DEPT"),vvipFlg);
//			parmpat = PatAdmTool.getInstance().getInsteadPatList(date,
//					this.getValueString("SESSION_CODE"), "", admType,
//					this.getValueString("INSTEAD_DEPT"));
			tblPat.removeRowAll(); 
			tblPat.setParmValue(parmpat);
			tblPat.changeLanguage(Operator.getLanguage());
			selectPat = true; 
		} 
		
		
		// modified by wangqing 20170801 start
		// 胸痛病患提示
		Color red = new Color(255, 0, 0);
		Color pink =new Color(255,170,255);
		HashMap map = new HashMap();
		HashMap wmap = new HashMap();
		SelectResult sr = new SelectResult(parmpat);
		int cnt = sr.size();
		for(int i=0;i<cnt;i++){
			Object er = sr.getRowField(i,"ENTER_ROUTE");
			Object pk = sr.getRowField(i,"PATH_KIND");
			if(er !=null && er.equals("E02")){
				map.put(i, pink);
			}
			if(pk != null && pk.equals("P01")){
				wmap.put(i, red);
			}
		}
		tblPat.setRowColorMap(map);
//		tblPat.setRowTextColorMap(wmap);
		// modified by wangqing 20170801 end
		
		
		
		
		
	}

	/**
	 * 门诊病患查询
	 */
	public void onPatQuery() {
		TParm parm = new TParm();
		parm.setData("DR_CODE", Operator.getID());
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("ADM_TYPE", admType);
		parm.setData("CLINICROOM_NO", this.getValueString("CLINICROOM"));
		String date = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		parm.setData("ADM_DATE", date);

		parm.setData("SESSION_CODE", this.getValue("SESSION_CODE"));
		if ("Y".equalsIgnoreCase(getValueString("REG_WAIT"))) {
			parm.setData("WAIT_DR", "N");
		} else if ("Y".equalsIgnoreCase(this.getValueString("REG_DONE"))) {
			parm.setData("SEE_DR", "Y");
		} else if ("Y".equalsIgnoreCase(this.getValueString("REG_TEMP"))) {
			parm.setData("TEMP_DR", "T");
		}
		// Date patd1 = new Date();
		parmpat = PatAdmTool.getInstance().selDateForODOByWait(parm);
		// Date patd2 = new Date();
		if (parmpat.getErrCode() < 0) {
			this.messageBox("E0005"); // 执行失败
			return;
		}
		jsEnd();
	}

	/**
	 * 急诊病患查询
	 */
	public void onEPatQuery() {
		TParm parm = new TParm();
		// parm.setData("DR_CODE", Operator.getID());//急诊要求能看到本科室的所有病人
		// 所以就不以医师为条件了
		parm.setData("ADM_TYPE", admType);
		parm.setData("REGION_CODE", Operator.getRegion());
		if (this.getValueString("CLINICROOM").length() > 0)
			parm.setData("CLINICROOM_NO", this.getValueString("CLINICROOM"));
		String date = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		parm.setData("ADM_DATE", date);
		if (this.getValueString("SESSION_CODE").length() > 0)
			parm.setData("SESSION_CODE", this.getValue("SESSION_CODE"));
		if ("Y".equalsIgnoreCase(getValueString("REG_WAIT"))) {
			parm.setData("WAIT_DR", "N");
		} else if ("Y".equalsIgnoreCase(this.getValueString("REG_DONE"))) {
			parm.setData("SEE_DR", "Y");
		} else if ("Y".equalsIgnoreCase(this.getValueString("REG_TEMP"))) {
			parm.setData("TEMP_DR", "T");
		}
		parm.setData("REALDEPT_CODE", Operator.getDept());
		parmpat = PatAdmTool.getInstance().selDateForODOEmgc(parm);
		// QUE_NO;MR_NO;PAT_NAME;ADM_STATUS;REPORT_STATUS
		if (parmpat.getErrCode() < 0) {
			this.messageBox("E0005"); // 执行失败
			return;
		}
		// liudy
		jsStart();
	}

	/**
	 * 急诊变色线程开启
	 */
	public void jsStart() {
		if (erdThread != null)
			return;
		erdThread = new Thread() {
			public void run() {
				try {
					erdThread.sleep(500);
				} catch (Exception e) {

				}
				while (erdThread != null) {
					jsRun();
					try {
						erdThread.sleep(100);
					} catch (Exception e) {

					}
				}
			}
		};
		erdThread.start();
	}

	/**
	 * 急诊变色线程结束
	 */
	public void jsEnd() {
		erdThread = null;
	}

	/**
	 * 急诊变色线程运行
	 */
	public void jsRun() {
		map = new HashMap();
		if (parmpat == null || parmpat.getCount("TIME_LIMIT") < 1)
			return;
		for (int i = 0; i < parmpat.getCount(); i++) {
			int limit = parmpat.getInt("TIME_LIMIT", i);
			Timestamp admDate = parmpat.getTimestamp("ADM_DATE", i);
//			Timestamp now = TJDODBTool.getInstance().getDBTime();
			// modified by wangqing 20170701
			Timestamp now = new Timestamp(System.currentTimeMillis());
			
			
			int minute = new Long(
					(now.getTime() - admDate.getTime()) / 1000 / 60).intValue();
			if (minute >= limit) {
				map.put(i, red);
			}
		}
		if (map.size() > 0) {
			tblPat.setRowTextColorMap(map);
		}
	}

	/**
	 * 就诊序号回车查询事件
	 */
	public void onQueNo() {
		if (parmpat == null || parmpat.getCount() < 1) {
			this.messageBox("E0025"); // 没有病人
			return;
		}
		String queNo = this.getValueString("QUE_NO");
		this.onClear();
		for (int i = 0; i < parmpat.getCount(); i++) {
			String tempQue = parmpat.getValue("QUE_NO", i);
			if (StringUtil.isNullString(tempQue)) {
				continue;
			}
			if (queNo.equalsIgnoreCase(tempQue)) {
				odo = new ODO(parmpat.getValue("CASE_NO", i), parmpat.getValue(
						"MR_NO", i), Operator.getDept(), Operator.getID(),
						parmpat.getValue("ADM_TYPE", i));
				//				String odo_type = "ODO";
				//				if ("E".equals(admType)) {
				//					odo_type = "ODE";
				//				}
				//				if (!onLockPat(odo_type)) {
				//					odo = null;
				//					return;
				//				}
				mp.onDoubleClicked(true);
				realDeptCode = parmpat.getValue("REALDEPT_CODE", i);
				wc = PatAdmTool.getInstance().getWestMediFlg(
						Operator.getRegion(),
						parmpat.getValue("ADM_TYPE", i),
						StringTool.getString(parmpat
								.getTimestamp("ADM_DATE", i), "yyyyMMdd"),
						getValueString("SESSION_CODE"),
						parmpat.getValue("CLINICROOM_NO", i)).getValue(
								"WEST_MEDI_FLG", 0);
				if (StringUtil.isNullString(wc)) {
					this.messageBox("E0119"); // 门急诊标记错误
					this.closeWindow();
				}
				caseNo=parmpat.getValue("CASE_NO", i);//=====就诊号重新赋值 pangben 2014-1-20
				initReg(parmpat.getValue("MR_NO", i), parmpat.getValue(
						"CASE_NO", i));
				if (!odo.onQuery()) {
					this.messageBox("E0024"); // 初始化参数失败
					return;
				}
				odo.getOpdOrder().addEventListener(
						odo.getOpdOrder().ACTION_SET_ITEM, this,
						"onSetItemEvent");
				initPatInfo(parmpat, i);
				initPanel();
				return;
			}
		}

		this.messageBox("E0025"); // 没有病人
	}

	/**
	 * 给现在的病人上锁
	 * 
	 * @param odo_type
	 *            String
	 * @return boolean
	 */
	//	public boolean onLockPat(String odo_type) {
	//		// System.out.println("odo----"+odo);
	//		if (odo == null) {
	//			return false;
	//		}
	//		// 处理上一个病人解锁
	//		if (!StringUtil.isNullString(lastMrNo)) {
	//			// 判断是否同一个病人，如不是，则为上一个病人解锁
	//			if (lastMrNo.equalsIgnoreCase(pat.getMrNo())) {
	//				// return true;
	//			} else {
	//				PatTool.getInstance().unLockPat(lastMrNo);
	//			}
	//		}
	//
	//		TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
	//		// System.out.println("parm----" + parm);
	//		// 如果本病人没有锁
	//		if (parm == null || parm.getCount() <= 0) {
	//			PatTool.getInstance().lockPat(pat.getMrNo(), odo_type,
	//					Operator.getID(), Operator.getIP());
	//
	//			lastMrNo = pat.getMrNo();
	//			return true;
	//		}
	//		// 本病人是自己加的锁
	//		if (isMyPat()) {
	//			lastMrNo = pat.getMrNo();
	//			return true;
	//		}
	//		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
	//		// 如果没有启动在线监听解锁
	//		if (!RootClientListener.getInstance().isClient()) {
	//			if (this.messageBox("是否解锁\r\nUnlock this pat?", PatTool
	//					.getInstance().getLockParmString(pat.getMrNo()), 0) != 0) {
	//				pat = null;
	//				return false;
	//			}
	//
	//			if (!PatTool.getInstance().unLockPat(pat.getMrNo())) {
	//				pat = null;
	//				return false;
	//			}
	//			PATLockTool.getInstance().log(
	//					odo_type + "->" + SystemTool.getInstance().getDate() + " "
	//							+ Operator.getID() + " " + Operator.getName()
	//							+ " 强制解锁[" + aa + " 病案号：" + pat.getMrNo() + "]");
	//			PatTool.getInstance().lockPat(pat.getMrNo(), odo_type,
	//					Operator.getID(), Operator.getIP());
	//			lastMrNo = pat.getMrNo();
	//			return true;
	//		}
	//
	//		// 在线解锁
	//		parm.setData("PRGID_U", odo_type);
	//		parm.setData("MR_NO", pat.getMrNo());
	//		String prgId = parm.getValue("PRG_ID", 0);
	//		if ("ODO".equals(prgId))
	//			parm.setData("WINDOW_ID", "OPD01");
	//		else if ("ODE".equals(prgId))
	//			parm.setData("WINDOW_ID", "ERD01");
	//		else if ("OPB".equals(prgId))
	//			parm.setData("WINDOW_ID", "OPB0101");
	//		else if ("ONW".equals(prgId))//====pangben 2013-5-14 添加护士站解锁管控：门诊
	//			parm.setData("WINDOW_ID", "ONW01");
	//		else if ("ENW".equals(prgId))//====pangben 2013-5-14 添加护士站解锁管控:急诊
	//			parm.setData("WINDOW_ID", "ONWE");
	//		String flg = (String) openDialog(
	//				"%ROOT%\\config\\sys\\SYSPatLcokMessage.x", parm);
	//		// 拒绝
	//		if ("LOCKING".equals(flg)) {
	//
	//			pat = null;
	//			return false;
	//		}
	//		// 同意
	//		if ("UNLOCKING".equals(flg)) {
	//			PatTool.getInstance().lockPat(pat.getMrNo(), odo_type,
	//					Operator.getID(), Operator.getIP());
	//			lastMrNo = pat.getMrNo();
	//			return true;
	//		}
	//		// 强解锁
	//		if ("OK".equals(flg)) {
	//			PatTool.getInstance().unLockPat(pat.getMrNo());
	//			PATLockTool.getInstance().log(
	//					odo_type + "->" + SystemTool.getInstance().getDate() + " "
	//							+ Operator.getID() + " " + Operator.getName()
	//							+ " 强制解锁[" + aa + " 病案号：" + pat.getMrNo() + "]");
	//			PatTool.getInstance().lockPat(pat.getMrNo(), odo_type,
	//					Operator.getID(), Operator.getIP());
	//			lastMrNo = pat.getMrNo();
	//			return true;
	//		}
	//		pat = null;
	//		return false;
	//	}


	/**
	 * 点选病人，初始化入院路径与路径分类
	 */
	public void onTablePatClick() {
		TTable table = (TTable) this.getComponent(TABLEPAT);
		int row = table.getSelectedRow();
		String tempCaseNo = parmpat.getValue("CASE_NO", row);
		TParm resultParm = EMRAMITool.getInstance().getEnterRouteAndPathKindByCaseNo(tempCaseNo);
		if (!selectCaseNo.equals(tempCaseNo)) {
			enterRoute.setSelectedID(resultParm.getValue("ENTER_ROUTE",0));
			pathKind.setSelectedID(resultParm.getValue("PATH_KIND",0));

			if ("E02".equals(resultParm.getValue("ENTER_ROUTE",0))) {
				this.callFunction("UI|PATHKIND|setEnabled", true);
			} else {
				this.callFunction("UI|PATHKIND|setEnabled", false);
				this.clearValue("PATHKIND");
			}
		} else {
			if (!isChange) {
				enterRoute.setSelectedID(resultParm.getValue("ENTER_ROUTE",0));
				pathKind.setSelectedID(resultParm.getValue("PATH_KIND",0));

				if ("E02".equals(resultParm.getValue("ENTER_ROUTE",0))) {
					this.callFunction("UI|PATHKIND|setEnabled", true);
				} else {
					this.callFunction("UI|PATHKIND|setEnabled", false);
					this.clearValue("PATHKIND");
				}
			}
		}
		selectCaseNo = tempCaseNo;
		isChange = false;
	}
	/**
	 * 点选病人，初始化医生站
	 */
	public void onTablePatDoubleClick(boolean clickFlg) {
		// System.out.println("onTablePatDoubleClick");
		//Date d1 = new Date();
		TTable table = (TTable) this.getComponent(TABLEPAT);
		int row = table.getSelectedRow();
		wc = PatAdmTool.getInstance().getWestMediFlg(
				Operator.getRegion(),
				parmpat.getValue("ADM_TYPE", row),
				StringTool.getString(parmpat.getTimestamp("ADM_DATE", row),
						"yyyyMMdd"), this.getValueString("SESSION_CODE"),
				parmpat.getValue("CLINICROOM_NO", row)).getValue(
						"WEST_MEDI_FLG", 0);
		if (StringUtil.isNullString(wc)) {
			this.messageBox("E0119"); // 门急诊标记错误
			this.closeWindow();
		}
		//===========pangben 2015-1-15 添加退挂校验
		String sql = "SELECT CASE_NO COUNT FROM REG_PATADM WHERE CASE_NO='"+parmpat.getValue("CASE_NO", row)+"' AND REGCAN_USER IS NOT NULL AND REGCAN_DATE IS NOT NULL";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		boolean flg= false;
		if(result.getCount()>0){
			flg=true;
		}
		if(flg){
			messageBox("该病患已退挂,请重新刷新病患列表");
			return;
		}
		/*modified by Eric 20170519 start*/ 
		//显示入院路径提示
		TLabel amiR = (TLabel) this.getComponent("tLabel_52");
		TLabel amiP = (TLabel) this.getComponent("tLabel_53");
		String erDesc = "";
		String pkDesc = "";
		String regCaseNo=parmpat.getValue("CASE_NO", row);

		TParm ami = AMIUtil.getE02byRegCaseNo(regCaseNo);
		if(ami !=null){
			int count = ami.getData("ACTION","COUNT")==null?0:ami.getInt("ACTION","COUNT");
			if(count>0){
				erDesc = ami.getValue("ER_DESC",0);
				pkDesc = ami.getValue("PK_DESC",0);

				if(!"E01".equals(ami.getValue("ENTER_ROUTE",0))){
					amiR.setForeground(Color.RED);
					amiP.setForeground(Color.RED);
				}
			}

			amiR.setText(erDesc);
			amiP.setText(pkDesc);
		}
		/*modified by Eric 20170519 end*/ 


		this.onClear();
		initOpd(parmpat, row);	
		initINS();
		// add by wangb 2017/05/17
		if ("E".equals(admType) && clickFlg) {
			// 从病患列表双击表格时，提示医生是否对急诊病患合并首诊心电医嘱
			initFirstEcgOrder();
			// add by wangqing 20170912 合并急诊抢救口头医嘱
			try{
				initOralOrder();
			}catch(Exception e){
				e.printStackTrace();
			}
		}		
	}

	/**
	 * 入院路径存档（改为按钮存档）
	 * modified by Eric 20170519
	 * */
	public void onPathSave(){
		TParm saveParm = new TParm();

		TTable table = (TTable) this.getComponent(TABLEPAT);
		int row = table.getSelectedRow();

		if(row<0){
			messageBox("请选择病患");
			return;
		}

		String enterRouteStr = enterRoute.getValue();
		String pathKindStr = pathKind.getValue();		

		saveParm.setData("CASE_NO",parmpat.getValue("CASE_NO", row));
		saveParm.setData("ENTER_ROUTE",enterRouteStr);
		saveParm.setData("PATH_KIND",pathKindStr);

		if ("".equals(enterRouteStr)) {
			messageBox("请选择该病患的入院路径");
			return;
		} else if ("E02".equals(enterRouteStr) && "".equals(pathKindStr)) {
			messageBox("请选择该病患的路径分类");
			return;
		}

		selectCaseNo = parmpat.getValue("CASE_NO", row);
		isChange = false;

		TParm resultParm = EMRAMITool.getInstance().updateEnterRouteAndPathKindToRegPatadm(saveParm);
		if (resultParm.getErrCode() < 0) {
			messageBox("更新入院路径与路径分类失败");
			return;
		}
		// add by wangqing 20170801 start
		Color pink =new Color(255,170,255);
		tblPat.removeRowColor(table.getSelectedRow());
		if(enterRouteStr != null && enterRouteStr.equals("E02")){
			tblPat.setRowColor(table.getSelectedRow(), pink);
		}
		// add by wangqing 20170801 end
		messageBox("入院路径更新成功");
	}

	public void onCheckAMI(String regcaseno){
		if(this.getComponent("tPanel_22")!=null){
			boolean isami = AMIUtil.isE02byRegCaseNo(regcaseno);
			TPanel tPanel22 = (TPanel) this.getComponent("tPanel_22");
			if(isami){

				tPanel22.setVisible(true);
			}else{
				tPanel22.setVisible(false); 
			}
		}
	}

	/**
	 * 初始化医生站病患信息
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 */
	private void initOpd(TParm parm, int row) {
		// this.onClear();
		// 初始化reg对象
		//deleteLisPosc = false;
		caseNo = parm.getValue("CASE_NO", row);// ==========pangben modify
		TParm opbParm=new TParm();
		opbParm.setData("CASE_NO",caseNo);
		resultData=REGCcbTool.getInstance().getEktCcbTradeInfo(opbParm);
		// 20110914
		initReg(parm.getValue("MR_NO", row), parm.getValue("CASE_NO", row));
		// 初始化odo对象
		odo = new ODO(parm.getValue("CASE_NO", row), parm
				.getValue("MR_NO", row), Operator.getDept(), Operator.getID(),
				parm.getValue("ADM_TYPE", row));
		//		String odo_type = "ODO";
		//		if ("E".equals(admType)) {
		//			odo_type = "ODE";
		//		}
		//		if (!onLockPat(odo_type)) {
		//			odo = null;
		//			return;
		//		}
		realDeptCode = parm.getValue("REALDEPT_CODE", row);
		odo.getOpdOrder().addEventListener(odo.getOpdOrder().ACTION_SET_ITEM,
				this, "onSetItemEvent");
		mp.onDoubleClicked(true);
		if (!odo.onQuery()) {
			this.messageBox("E0024"); // 初始化参数失败
			//deleteLisPosc = false;
			return;
		}
		// 初始化pat对象
		initPatInfo(parm, row);
		initPanel();
		setRootPanelWidth();
		phaCode = ClinicRoomTool.getInstance().getOrgByOdo(
				Operator.getRegion(),
				StringTool.getString((Timestamp) this.getValue("ADM_DATE"),
						"yyyyMMdd"), this.getValueString("SESSION_CODE"),
				admType, parm.getValue("REALDR_CODE", row),
				parm.getValue("REALDEPT_CODE", row),
				parm.getValue("CASE_NO", row)).getValue("ORG_CODE", 0);
		if (!odo.getOpdOrder().isOrgAvalible(phaCode)) {
			this.messageBox("E0117"); // 药房没有开启窗口，不能开诊
			this.closeWindow();
		}
		this.setValue("MED_RBORDER_DEPT_CODE", phaCode);
		this.setValue("CTRL_RBORDER_DEPT_CODE", phaCode);
		this.setValue("OP_EXEC_DEPT", parm.getValue("REALDEPT_CODE", row));
		this.setValue("CHN_EXEC_DEPT_CODE", phaCode);
		//		if ("C".equalsIgnoreCase(wc)) {
		//			this.setValue("CHN_EXEC_DEPT_CODE", phaCode);
		//		}
		lastMrNo = pat.getMrNo();
	}

	/**
	 * 设置ROOT_PANEL的宽度
	 */
	public void setRootPanelWidth() {
		TPanel diag = (TPanel) this.getComponent("DIAGNOSISPANEL");
		diag.setWidth(418);
		TMovePane upDown = (TMovePane) this.getComponent("tMovePane_1");
		upDown.setWidth(1088);
		TTabbedPane order = (TTabbedPane) this.getComponent("TTABPANELORDER");
		order.setWidth(1088);
		TMovePane leftRight = (TMovePane) this.getComponent("tMovePane_0");
		leftRight.setX(1100);
		TPanel menu = (TPanel) this.getComponent("tPanel_6");
		menu.setX(1110);
		menu.setWidth(1200);

	}

	/**
	 * 初始化REG对象
	 * 
	 * @param mrNo
	 *            String
	 * @param caseNo
	 *            String
	 */
	public void initReg(String mrNo, String caseNo) {
		pat = Pat.onQueryByMrNo(mrNo);
		reg = Reg.onQueryByCaseNo(pat, caseNo);
		opb = OPB.onQueryByCaseNo(reg);// ====pangben 2012-2-28
	}

	/**
	 * 初始化代诊科室
	 */
	public void initInsteadDept() {
		this.clearValue("INSTEAD_DEPT;INSTEAD_DR");
		String admDate = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		String sessionCode = this.getValueString("SESSION_CODE");
		TParm parm = PatAdmTool.getInstance().getInsteadDept(admType, admDate,
				sessionCode, Operator.getRegion());
		insteadDept.setParmValue(parm);
		this.callFunction("UI|INSTEAD_DR|setEnabled", false);
	}

	/**
	 * 代诊科别点击时间，将代诊医师combo置为可用，并初始化代诊医师combo
	 */
	public void onInsteadDept() {
		String dept = this.getValueString("INSTEAD_DEPT");
		this.clearValue("INSTEAD_DR");
		if (StringUtil.isNullString(dept)) {
			dr.setEnabled(false);
			return;
		}
		dr.setEnabled(true);
		String admDate = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		String sessionCode = this.getValueString("SESSION_CODE");
		TParm parm = PatAdmTool.getInstance().getInsteadDrByDept(admType,
				sessionCode, admDate, dept);
		if ("en".equals(this.getLanguage()))
			dr.setParmMap("id:ID;name:USER_ENG_NAME;Py1:PY1");
		else
			dr.setParmMap("id:ID;name:NAME;Py1:PY1");
		dr.setParmValue(parm);
		if ("E".equals(admType)) { // 急诊时 代诊可以显示该科室的所有病人信息
			this.onSelectPat("INSTEAD_DEPT");
		}
	}

	/**
	 * 初始化病患个人信息，包括病生理
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 */
	public void initPatInfo(TParm parm, int row) {
		this.setValue("QUE_NO", parm.getValue("QUE_NO", row));
		if (isEng) {
			this.setValue("PAT_NAME", parm.getValue("PAT_NAME1", row));
		} else {
			this.setValue("PAT_NAME", parm.getValue("PAT_NAME", row));
		}
		serviceLevel = reg.getServiceLevel();
		this.setValue("SERVICE_LEVEL", reg.getServiceLevel());
		this.setValue("CTZ_CODE", reg.getCtz1Code());
		this.setValue("CTZ_CODE2", reg.getCtz2Code());//add by huangtt 20141210		
		this.setValue("INT_PAT_TYPE", reg.getInsPatType());
		if (!StringUtil.isNullString(reg.getInsPatType())
				&& !reg.getInsPatType().equals("1")) {
			TTextFormat INS_DISEAS_CODE = (TTextFormat) this
					.getComponent("INS_DISEAS_CODE");
			TParm cat1TypeParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT DISEASE_CODE FROM INS_MZ_CONFIRM WHERE CASE_NO='"
							+ caseNo + "' AND MR_NO='"
							+ parm.getValue("MR_NO", row) + "'"));
			INS_DISEAS_CODE.setValue(cat1TypeParm.getValue("DISEASE_CODE", 0));
		}
		odo.getOpdOrder().setServiceLevel(serviceLevel); // opdorder对象设置服务等级
		this.setValue("WEIGHT", odo.getRegPatAdm().getItemString(0, "WEIGHT"));
		this.setValue("MR_NO", parm.getValue("MR_NO", row));
		this.setValue("SEX_CODE", odo.getPatInfo().getItemString(0,
				"SEX_CODE"));
		//$------------------start caoyong 20131227 add -------------

		if (odo.getRegPatAdm().getItemData(0, "SEEN_DR_TIME") != null) {
			seeDrTime=odo.getRegPatAdm().getItemString(0, "SEEN_DR_TIME").substring(0, 19).replace("-","/");
		}
		this.setValue("SEEN_DR_TIME",seeDrTime );// 添加接诊时间

		//$------------------end caoyong 20131227 add -------------

		String age = OdoUtil.showAge(parm.getTimestamp("BIRTH_DATE", row),
				SystemTool.getInstance().getDate());
		this.setValue("AGE", age);
		if (!"2".equalsIgnoreCase(pat.getSexCode())) {
			TTextFormat tFormat = (TTextFormat) this.getComponent("LMP_DATE");
			tFormat.setValue("");
			tFormat.setEnabled(false);
			tFormat = (TTextFormat) this.getComponent("BREASTFEED_STARTDATE");
			tFormat.setValue("");
			tFormat.setEnabled(false);
			tFormat = (TTextFormat) this.getComponent("BREASTFEED_ENDDATE");
			tFormat.setValue("");
			tFormat.setEnabled(false);
		} else {
			TTextFormat tFormat = (TTextFormat) this.getComponent("LMP_DATE");
			tFormat.setEnabled(true);
			tFormat = (TTextFormat) this.getComponent("BREASTFEED_STARTDATE");
			tFormat.setEnabled(true);
			tFormat = (TTextFormat) this.getComponent("BREASTFEED_ENDDATE");
			tFormat.setEnabled(true);
		}

		this.setValue("PAT1_CODE", parm.getValue("PAT1_CODE", row));
		this.setValue("PAT2_CODE", parm.getValue("PAT2_CODE", row));
		this.setValue("PAT3_CODE", parm.getValue("PAT3_CODE", row));

		this.setValue("HANDICAP_FLG", parm.getValue("HANDICAP_FLG", row));
		this.setValue("PREMATURE_FLG", parm.getValue("PREMATURE_FLG", row));
		this.setValue("LMP_DATE", parm.getTimestamp("LMP_DATE", row));

		// 将数据库中的值赋值到结构化病历中
		this.setValue("PRE_WEEK", OdoUtil.getPreWeek(parm.getTimestamp(
				"LMP_DATE", row), parm.getTimestamp("ADM_DATE", row))
				+ "");
		this.setValue("BREASTFEED_STARTDATE", parm.getTimestamp(
				"BREASTFEED_STARTDATE", row));
		this.setValue("BREASTFEED_ENDDATE", parm.getTimestamp(
				"BREASTFEED_ENDDATE", row));
		String ctz1 = parm.getValue("CTZ1_CODE", row);
		String ctz2 = parm.getValue("CTZ2_CODE", row);
		String ctz3 = parm.getValue("CTZ3_CODE", row);
		this.setValue("CTZ1_CODE", ctz1);
		this.setValue("CTZ2_CODE", ctz2);
		this.setValue("CTZ3_CODE", ctz3);
		ctz = new String[3];
		ctz[0] = ctz1;
		ctz[1] = ctz2;
		ctz[2] = ctz3;
	}

	/**
	 * 初始化所有panel
	 */
	public void initPanel() {
		// System.out.println("in initPanel");
		if (odo == null) {
			return;
		}
		// 主诉客诉
		initSubject();
		// 诊断
		initDiag();
		// 既往史
		initMedHistory();
		// 过敏史
		initAllergy();
		// 检验检查
		this.setTableInit(TABLE_EXA, false);
		// 处置
		this.setTableInit(TABLE_OP, false);
		// 西药
		this.setTableInit(TABLE_MED, false);
		// 中药
		this.setTableInit(TABLE_CHN, false);
		// 毒药

		this.setTableInit(TABLE_CTRL, false);

		// 院前用药  add by huangtt 20151201 
		this.setTableInit(TABLE_PRE_MED, false);

		// System.out.println("before tabP");
		// initCtrl();
		TTabbedPane tabP = (TTabbedPane) this.getComponent("TTABPANELORDER");
		if (tabP.getSelectedIndex() != 0) {
			tabP.setSelectedIndex(0);
		}
		//add by huangtt 20151012 添加初始化  start
		initCtrl();
		initChnMed();
		initMed();
		initOp();
		initPreMed(); //add by huangtt 20151201 院前用药初始化
		//add by huangtt 20151012 添加初始化  end
		initExa();

		onDiagPnChange();
	}

	/**
	 * 初始化管制药品
	 */
	private void initCtrl() {
		boolean isInit = isTableInit(TABLE_CTRL);
		String rxNo = "";
		if (!isInit) {
			rxNo = initRx(CTRL_RX, CTRL);
			setTableInit(TABLE_CTRL, true);
		} else {
			rxNo = this.getValueString(CTRL_RX);
		}
		if (!initNoSetTable(rxNo, TABLE_CTRL, isInit,true))
			this.messageBox("E0026"); // 初始化检验检查失败
		onChangeRx("2");
	}

	/**
	 * 初始化处置
	 */
	private void initOp() {
		boolean isInit = isTableInit(TABLE_OP);
		String rxNo = "";
		if (!isInit) {
			rxNo = initRx(OP_RX, OP);
			setTableInit(TABLE_OP, true);
		} else {
			rxNo = this.getValueString(OP_RX);
		}
		// System.out.println("rxNo================="+rxNo);
		if (!initNoSetTable(rxNo, TABLE_OP, isInit,true))
			this.messageBox("E0027"); // 初始化处置失败
		onChangeRx("4");
	}

	/**
	 * 取得RX COMBO的值
	 * 
	 * @param data
	 *            String[]
	 * @param i
	 *            int
	 * @return String
	 */
	private String getRxNo(String[] data, int i) {
		if (data == null || data.length < 1 || i < 0)
			return null;
		String rxNo = (data[i].split(","))[0];
		return rxNo;
	}

	/**
	 * 初始化中药
	 */
	private void initChnMed() {
		boolean isInit = isTableInit(TABLE_CHN);
		// System.out.println("isInit-----"+isInit);
		rxType = CHN;
		String rxNo = this.getValueString(CHN_RX);
		// System.out.println("CHN_RX-----"+CHN_RX);
		if (!isInit) {
			rxNo = initRx(CHN_RX, CHN);
			setTableInit(TABLE_CHN, true);

		} else {
			rxNo = this.getValueString(CHN_RX);

		}
		if (StringUtil.isNullString(odo.getOpdOrder().getItemString(0,
				"ORDER_CODE"))) {
			this.setValue("DCT_TAKE_DAYS", dctTakeDays);
			this.setValue("DCT_TAKE_QTY", dctMediQty);
			this.setValue("CHN_FREQ_CODE", this.dctFreqCode);
			this.setValue("CHN_ROUTE_CODE", this.dctRouteCode);
			this.setValue("DCTAGENT_CODE", this.dctAgentCode);
			this.setValue("DR_NOTE", "");
			this.setValue("CHN_EXEC_DEPT_CODE", phaCode);
			this.setValue("URGENT_FLG", "");
			this.setValue("RELEASE_FLG", "");
		} else {
			String filter = "RX_NO=" + rxNo + "";
			OpdOrder order = odo.getOpdOrder();
			order.setFilter(filter);
			order.filter();//pangben 2015-1-21 添加过滤
			this.setValue("DCT_TAKE_DAYS", odo.getOpdOrder().getItemInt(0,
					"TAKE_DAYS"));
			this.setValue("DCT_TAKE_QTY", odo.getOpdOrder().getItemDouble(0,
					"DCT_TAKE_QTY"));
			this.setValue("CHN_FREQ_CODE", odo.getOpdOrder().getItemString(0,
					"FREQ_CODE"));
			this.setValue("CHN_ROUTE_CODE", odo.getOpdOrder().getItemString(0,
					"ROUTE_CODE"));
			this.setValue("DCTAGENT_CODE", odo.getOpdOrder().getItemString(0,
					"DCTAGENT_CODE"));
			this.setValue("DR_NOTE", odo.getOpdOrder().getItemString(0,
					"DR_NOTE"));
			this.setValue("CHN_EXEC_DEPT_CODE", odo.getOpdOrder()
					.getItemString(0, "EXEC_DEPT_CODE"));
			this.setValue("URGENT_FLG", odo.getOpdOrder().getItemString(0,
					"URGENT_FLG"));
			this.setValue("RELEASE_FLG", odo.getOpdOrder().getItemString(0,
					"RELEASE_FLG"));
		}
		this.initChnTable(rxNo);
		onChangeRx("3");
	}

	/**
	 * 初始化西药
	 */
	public void initMed() {
		boolean isInit = isTableInit(TABLE_MED);
		String rxNo = "";
		if (!isInit) {
			rxNo = initRx(MED_RX, MED);
			setTableInit(TABLE_MED, true);
		} else {
			rxNo = this.getValueString(MED_RX);
		}

		if (!initNoSetTable(rxNo, TABLE_MED, isInit,true))
			this.messageBox("E0028"); // 初始化西成药失败

		onChangeRx("1");

	}

	/**
	 * 初始化院前用药   
	 */
	public void initPreMed() {
		boolean isInit = isTableInit(TABLE_PRE_MED);
		String rxNo = "";
		if (!isInit) {
			rxNo = initRx(PREMED_RX, PREMED);
			setTableInit(TABLE_PRE_MED, true);
		} else {
			rxNo = this.getValueString(PREMED_RX);
		}
		//		System.out.println("院前用药rxNo===="+rxNo);
		if (!initNoSetTable(rxNo, TABLE_PRE_MED, isInit,true))
			this.messageBox("E0028"); // 初始化西成药失败

		onChangeRx("6");

	}

	/**
	 * 中医一张处方签的所有医嘱的给定字段的值改变事件
	 * 
	 * @param fieldName
	 *            String 要改变的字段名
	 * @param type
	 *            String
	 */
	public void onChnChange(String fieldName, String type) {
		if (odo == null)
			return;
		OpdOrder order = odo.getOpdOrder();
		String rxNo = this.getValueString(CHN_RX);

		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		String value = this.getValueString(fieldName);
		if ("CHN_FREQ_CODE".equalsIgnoreCase(fieldName)) {
			fieldName = "FREQ_CODE";
		}
		if ("CHN_ROUTE_CODE".equalsIgnoreCase(fieldName)) {
			TTextFormat t = (TTextFormat) this.getComponent(fieldName);
			value = t.getValue().toString();
			fieldName = "ROUTE_CODE";
		}
		if ("DCT_TAKE_DAYS".equalsIgnoreCase(fieldName)) {
			fieldName = "TAKE_DAYS";
		}
		// 判断煎药方式是否填写 不可为空
		if ("DCTAGENT_CODE".equals(fieldName)) {
			if (this.getValueString("DCTAGENT_CODE").length() == 0) {
				this.messageBox("E0190");
				this.setValue("DCTAGENT_CODE", dctAgentCode);
				return;
			}
		}
		int count = order.rowCount();
		for (int i = 0; i < count; i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_DESC"))) {
				continue;
			}
			if ("E".equals(order.getItemString(i, "BILL_TYPE"))
					&& (StringTool.getBoolean(order
							.getItemString(i, "EXEC_FLG"))
							|| StringTool.getBoolean(order.getItemString(i,
									"PRINT_FLG")) || StringTool
							.getBoolean(order.getItemString(i, "BILL_FLG")))) {
				this.messageBox("E0055"); // 已计费医嘱不能删除
				return;
			} else {
				if (StringTool.getBoolean(order.getItemString(i, "BILL_FLG"))
						&& !"E".equals(order.getItemString(i, "BILL_TYPE"))) {
					this.messageBox("E0055"); // 已计费医嘱不能删除
					return;
				}
			}

			if ("string".equalsIgnoreCase(type)
					|| StringUtil.isNullString(type)) {
				order.setItem(i, fieldName, value);
			} else {
				order.setItem(i, fieldName, value);
			}
		}
	}

	/**
	 * 初始化中药处方签
	 * 
	 * @param rxNo
	 *            String
	 * @return boolean
	 */
	public boolean initChnTable(String rxNo) {
		if (StringUtil.isNullString(rxNo)) {
			this.messageBox("E0029"); // 没有处方签
			return false;
		}
		TTable table = (TTable) this.getComponent(TABLE_CHN);
		String filter = "RX_NO='" + rxNo + "'";
		OpdOrder order = odo.getOpdOrder();
		order.setFilter(filter);

		if (!order.filter()) {
			this.messageBox("E0030"); // 无此药品
			return false;
		}
		int totRow = order.rowCount();

		if (!StringUtil.isNullString(order.getItemString(totRow - 1,
				"ORDER_CODE"))
				|| totRow % 4 != 0 || totRow < 1) {
			for (int i = 0; i < 4 - totRow % 4; i++) {
				if (order.newOrder("3", rxNo) == -1) {
					this.messageBox("E0031"); // 显示中药失败
					return false;
				}
				order.setItem(i, "PHA_TYPE", "G");
			}
		}

		if (!order.filter()) {
			this.messageBox("E0031"); // 显示中药失败
			return false;
		}

		TParm parm = odo.getOpdOrder().getBuffer(order.PRIMARY);
		TParm tableParm = new TParm();
		//修改问题：每次传回1个医嘱却增加了一行，由于空值问题影响循环长度 start --xiongwg20150518
		int length = 1;
		for (int i = 0; i < parm.getCount("ORDER_DESC"); i++) {
			if (parm.getValue("ORDER_DESC", i).equals("")) {
				continue;
			}
			length++;
		}
		//修改问题：每次传回1个医嘱却增加了一行，由于空值问题影响循环长度  end --xiongwg20150518
		for (int i = 0; i < length; i++) {
			int idx = i % 4 + 1;
			tableParm.addData("ORDER_DESC" + idx, parm
					.getValue("ORDER_DESC", i));
			tableParm.addData("MEDI_QTY" + idx, parm.getDouble("MEDI_QTY", i));
			tableParm.addData("DCTEXCEP_CODE" + idx, parm.getValue(
					"DCTEXCEP_CODE", i));
		}
		table.setParmValue(tableParm);
		if (!StringUtil.isNullString(parm.getValue("ORDER_CODE", 0))) {

			//m用来抓取传回模板的中药处方医嘱上方的其他信息 start --xiongwg20150518 
			int m = 0;
			int count = parm.getCount("ORDER_CODE");
			for(int i=count-1;i>=0;i--){
				if(!StringUtil.isNullString(parm.getValue("ORDER_CODE", i))){
					m=i;
					break;
				}
			}
			//m用来抓取传回模板的中药处方医嘱上方的其他信息 end --xiongwg20150518 

			this.setValue("DCT_TAKE_DAYS", parm.getValue("TAKE_DAYS", m));
			this.setValue("DCT_TAKE_QTY", parm.getValue("DCT_TAKE_QTY", m));
			this.setValue("CHN_FREQ_CODE", parm.getValue("FREQ_CODE", m));
			this.setValue("CHN_ROUTE_CODE", parm.getValue("ROUTE_CODE", m));
			this.setValue("DCTAGENT_CODE", parm.getValue("DCTAGENT_CODE", m));
			this.setValue("DR_NOTE", parm.getValue("DR_NOTE", m));
			//			this.setValue("CHN_EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE",
			//					0));
			this.setValue("URGENT_FLG", parm.getValue("URGENT_FLG", m));
			this.setValue("RELEASE_FLG", parm.getValue("RELEASE_FLG", m));
		} else {
			this.setValue("DCT_TAKE_DAYS", dctTakeDays);
			this.setValue("DCT_TAKE_QTY", dctMediQty);
			this.setValue("CHN_FREQ_CODE", this.dctFreqCode);
			this.setValue("CHN_ROUTE_CODE", this.dctRouteCode);
			this.setValue("DCTAGENT_CODE", this.dctAgentCode);
			this.setValue("DR_NOTE", "");
			this.setValue("CHN_EXEC_DEPT_CODE", phaCode);
			this.setValue("URGENT_FLG", "");
			this.setValue("RELEASE_FLG", "");
		}
		setChnPckTot();
		this.calculateChnCash(rxNo);
		return true;
	}

	/**
	 * 初始化非集合医嘱的TABLE
	 * 
	 * @param rxNo
	 *            String
	 * @param tableName
	 *            String
	 * @param isInit
	 *            boolean
	 * @return boolean
	 */
	public boolean initNoSetTable(String rxNo, String tableName, boolean isInit,boolean colorFlg) {
		if (StringUtil.isNullString(tableName))
			return false;
		TTable table = (TTable) this.getComponent(tableName);
		String filter = "RX_NO='" + rxNo + "'";
		if (TABLE_OP.equalsIgnoreCase(tableName)) {
			filter += " AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='' OR HIDE_FLG='N')";
		}
		if (!isInit) {
			table.setDataStore(odo.getOpdOrder());
			table.setFilter(filter);
			if (!table.filter()) {
				this.messageBox("E0024"); // 初始化参数失败
				return false;
			}
		} else {
			odo.getOpdOrder().setFilter(filter);
			if (!odo.getOpdOrder().filter()) {
				this.messageBox("E0024"); // 初始化参数失败
				return false;
			}
		}
		if (colorFlg) {
			Map inscolor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
					whetherCallInsItf);
			Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odo.getOpdOrder());
			table.setRowTextColorMap(ctrlcolor);
		}
		return true;
	}

	/**
	 * 锁行
	 * 
	 * @param tableName
	 *            String
	 * @param isInit
	 *            boolean 是否为刚刚写入的order，如是，则此行不锁，如不是则此行锁
	 * @return String
	 */
	public String lockRows(String tableName, boolean isInit) {
		TTable table = (TTable) this.getComponent(tableName);
		StringBuffer sb = new StringBuffer();
		int index;
		if (isInit) {
			index = table.getRowCount() - 1;
		} else {
			index = table.getRowCount() - 2;
		}
		for (int i = 0; i < index; i++) {
			sb.append(i + ",");
		}
		String lockRow = sb.toString();
		if (StringUtil.isNullString(lockRow))
			return "";
		return lockRow.substring(0, lockRow.lastIndexOf(","));
	}

	private String tempRxNo;

	public boolean rxFilter(TParm parm, int row) {
		String s = parm.getValue("SETMAIN_FLG", row);
		return parm.getValue("RX_NO", row).equalsIgnoreCase(tempRxNo)
				&& (s.equalsIgnoreCase("Y") || s.length() == 0);
	}

	/**
	 * 初始化显示集合医嘱的TABLE
	 * 
	 * @param tableName
	 *            String
	 * @param isInit
	 *            boolean
	 * @return boolean
	 */
	public boolean initSetTable(String tableName, boolean isInit) {
		if (StringUtil.isNullString(tableName)) {
			return false;
		}
		TTable table = (TTable) this.getComponent(tableName);
		String rxNo = (String) this.getValue("EXA_RX");
		String filter = "RX_NO='" + rxNo
				+ "' AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')";
		if (!isInit)
			table.setDataStore(odo.getOpdOrder());
		table.setFilter(filter);
		table.filter();
		tempRxNo = (String) this.getValue("EXA_RX");
		table.getDataStore().filterObject(this, "rxFilter");
		table.setDSValue();
		this.calculateCash(TABLE_EXA, "EXA_AMT");
		if (whetherCallInsItf) {
			Map inscolor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
					whetherCallInsItf);
			table.setRowTextColorMap(inscolor);
		}
		return true;
	}

	/**
	 * 初始化制定COMBO并返回处方号
	 * 
	 * @param rxName
	 *            String
	 * @param rxType
	 *            String
	 * @return String
	 */	
	private String initRx(String rxName, String rxType) {
		if (StringUtil.isNullString(rxName) || StringUtil.isNullString(rxType)) {
			return "";
		}
		TComboBox combo = (TComboBox) this.getComponent(rxName);
		String[] data = odo.getOpdOrder().getRx(rxType);
		if (data == null || data.length < 0) {
			return "";
		}
		String rxNo = getRxNo(data, 0);
		if (StringUtil.isNullString(rxNo)) {
			data = new String[1];
			rxNo = odo.getOpdOrder().newPrsrp(rxType);
			if (StringUtil.isNullString(rxNo)) {
				this.messageBox("E0032"); // 生成处方号失败
				return "";
			}
			if ("en".equalsIgnoreCase(Operator.getLanguage())) {
				data[0] = rxNo + ",【" + 1 + "】 Rx";
			} else {
				data[0] = rxNo + ",【" + 1 + "】 处方签";
			}

		} else {
			if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)
					&& !CHN.equalsIgnoreCase(rxType)) {
				odo.getOpdOrder().newOrder(rxType, rxNo);
			}
		}

		combo.getModel().setItems(new Vector());
		TComboNode nodeNull = new TComboNode();
		combo.getModel().getItems().add(nodeNull);

		combo.setData(data, ",");
		combo.setValue(rxNo);

		return rxNo;
	}

	/**
	 * 初始化检验检查
	 */
	private void initExa() {
		boolean isInit = isTableInit(TABLE_EXA);
		String rxNo = "";
		if (!isInit) {
			rxNo = initRx(EXA_RX, EXA);
			if (StringUtil.isNullString(rxNo)) {
				this.messageBox("E0026"); // 初始化检验检查失败
				return;
			}
			setTableInit(TABLE_EXA, true);
		} else {
			rxNo = this.getValueString(this.EXA_RX);
		}
		if (!initSetTable(TABLE_EXA, isInit))
			this.messageBox("E0026"); // 初始化检验检查失败
		onChangeRx("5");

	}

	/**
	 * 判断给定TABLE是否已经初始化过
	 * 
	 * @param tableName
	 *            String
	 * @return boolean
	 */
	private boolean isTableInit(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		return TCM_Transform.getBoolean(table.getData());
	}

	/**
	 * 设置给定TABLE已经初始化的标记
	 * 
	 * @param tableName
	 *            String
	 * @param isInit
	 *            boolean
	 */
	private void setTableInit(String tableName, boolean isInit) {
		TTable table = (TTable) this.getComponent(tableName);
		table.setData(isInit);
	}

	/**
	 * 初始化主诉客诉
	 */
	public void initSubject() {
		// System.out.println("in initSubject");
		TParm parm = new TParm();
		parm.setData("CASE_NO", odo.getCaseNo());
		parm.setData("TYPE", "ZS");
		// TParm microParm=new TParm();
		TParm allParm = new TParm();
		if ("N".equalsIgnoreCase(odo.getRegPatAdm().getItemString(0,
				"SEE_DR_FLG"))) {
			// zhangyong20110427 
			saveFiles = EmrUtil.getInstance().getGSTemplet(realDeptCode,
					Operator.getID(), admType);
			word.onOpen(saveFiles[0], saveFiles[1], 2, false);
			allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,
					"onMouseRightPressed");
			word.setWordParameter(allParm);
			word.setCanEdit(true);
		} else {
			saveFiles = EmrUtil.getInstance().getGSFile(odo.getCaseNo());
			word.onOpen(saveFiles[0], saveFiles[1], 3, false);
			allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,
					"onMouseRightPressed");
			word.setWordParameter(allParm);
			word.setCanEdit(true);
		}
		// $$=========== add by lx 2012/02/24/ 加入
		word.fixedTryReset(odo.getMrNo(), odo.getCaseNo());
		// $$=========== add by lx 2012/02/24/ 加入  刷新抓取内容

		familyHisFiles = EmrUtil.getInstance().getFamilyHistoryPath(
				odo.getMrNo(), realDeptCode, admType);
		familyWord.onOpen(familyHisFiles[0], familyHisFiles[1], Integer
				.parseInt(familyHisFiles[2]), false);
		TParm familyParm = new TParm();
		familyParm
		.addListener("onDoubleClicked", this, "onFamilyDoubleClicked");
		familyWord.setWordParameter(familyParm);
		// System.out.println("after initSubject");
	}

	/**
	 * 结构化病历界面双击事件,调用片语界面.
	 * 
	 * @param pageIndex
	 *            int
	 * @param x
	 *            int
	 * @param y
	 *            int
	 */
	public void onDoubleClicked(int pageIndex, int x, int y) {
		// 如果焦点没有在主诉、客诉、体征上则什么都不做.
		String str = "";
		if (word.focusInCaptue("SUB")) {
			str = "SUB";
		} else if (word.focusInCaptue("OBJ")) {
			str = "OBJ";
		} else if (word.focusInCaptue("PHY")) {
			str = "PHY";
		} else if (word.focusInCaptue("EXA_RESULT")) {
			str = "EXA_RESULT";
		} else if (word.focusInCaptue("PROPOSAL")) {
			str = "PROPOSAL";
		}
		if (StringUtil.isNullString(str)) {
			return;
		}
		if ("EXA_RESULT".equalsIgnoreCase(str)) {
			onInsertResult();
		} else {
			onInsertPY();
		}
	}

	/**
	 * 家族史界面双击事件，调用片语界面
	 * 
	 * @param pageIndex
	 *            int
	 * @param x
	 *            int
	 * @param y
	 *            int
	 */
	public void onFamilyDoubleClicked(int pageIndex, int x, int y) {

		// 如果焦点没有在主诉、客诉、体征上则什么都不做.
		String str = "";
		if (this.familyWord.focusInCaptue("FAMILY_HISTORY")) {
			str = "FAMILY_HISTORY";
		}
		if (StringUtil.isNullString(str)) {
			return;
		}

		onInsertFamilyPY();

	}

	/**
	 * 初始化诊断
	 */
	public void initDiag() {
		TTable table = (TTable) getComponent(TABLEDIAGNOSIS);
		odo.setIcdType(wc);

		TRadioButton w = (TRadioButton) this.getComponent("W_FLG");
		TRadioButton c = (TRadioButton) this.getComponent("C_FLG");
		if ("W".equalsIgnoreCase(wc)) {
			w.setValue("Y");
		} else {
			c.setValue("Y");
		}
		//	int[] tempInt = new int[] { 0 };
		//		mainFlg = odo.getDiagrec().haveMainDiag(tempInt);
		//		if (mainFlg) {
		//			mainDiag = tempInt[0];
		//		}
		w = null;
		c = null;
		table.setDataStore(odo.getDiagrec());
		table.setDSValue();
	}

	/**
	 * 西医诊断radio注记
	 */
	public void onWFlg() {
		TTable table = (TTable) this.getComponent(this.TABLEDIAGNOSIS);
		table.acceptText();
		wc = "W";
	}

	/**
	 * 中医诊断radio注记
	 */
	public void onCFlg() {
		TTable table = (TTable) this.getComponent(this.TABLEDIAGNOSIS);
		table.acceptText();
		wc = "C";
	}

	/**
	 * 初始化既往史
	 */
	public void initMedHistory() {
		TTable table = (TTable) getComponent(TABLEMEDHISTORY);
		table.setDataStore(odo.getMedHistory());
		table.setDSValue();
	}

	/**
	 * 初始化过敏史
	 */
	public void initAllergy() {
		TTable table = (TTable) getComponent(TABLEALLERGY);
		DrugAllergy all = odo.getDrugAllergy();
		table.setDataStore(all);
		all.setFilter("DRUG_TYPE='B'");
		all.filter();
		table.setDSValue();
		// table.setLockRows(lockRows(TABLEALLERGY,true));
	}

	/**
	 * 新增一张处方签，更新combo，并初始table
	 * 
	 * @param rxType
	 *            处方类型
	 * @param rxName
	 *            combo名
	 * @param tableName
	 *            table名
	 */
	public void onAddOrderList(String rxType, String rxName, String tableName) {
		// 通过取号原则取得新处方号
		String rxNo = odo.getOpdOrder().newPrsrp(rxType);
		if (StringUtil.isNullString(rxNo)) {
			this.messageBox("E0033"); // 新增处方失败
			return;
		}
		// 设置combo的新值，并设置显示值
		TComboBox combo = (TComboBox) this.getComponent(rxName);
		String[] data = odo.getOpdOrder().getRx(rxType);
		String newData = "";
		if (isEng) {
			newData = rxNo + ",【" + (data.length) + "】 Rx";
		} else {
			newData = rxNo + ",【" + (data.length) + "】 处方签";
		}

		combo.addData(newData, ",");
		combo.setValue(rxNo);
		if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
			odo.getOpdOrder().newOrder(rxType, rxNo);
		}
		if (this.CHN.equalsIgnoreCase(rxType)) {
			this.setValue("DCT_TAKE_DAYS", dctTakeDays);
			this.setValue("DCT_TAKE_QTY", dctMediQty);
			this.setValue("CHN_FREQ_CODE", this.dctFreqCode);
			this.setValue("CHN_ROUTE_CODE", this.dctRouteCode);
			this.setValue("DCTAGENT_CODE", this.dctAgentCode);
			this.setValue("DR_NOTE", "");
			this.setValue("CHN_EXEC_DEPT_CODE", "");
			this.setValue("URGENT_FLG", "");
			this.setValue("RELEASE_FLG", "");
		}
		onChangeRx(rxType);
	}

	/**
	 * 删除整张处方签
	 * 
	 * @param rxType
	 *            处方类型
	 */
	public void onDeleteOrderList(int rxType) {
		String rxNo = "";
		String tableName = "";
		OpdOrder order = odo.getOpdOrder();
		String oldfilter = order.getFilter();
		int count = -1;
		TTable table;
		StringBuffer billFlg=new StringBuffer();//判断是否可以删除 ，同一张处方签中的状态不相同不能删除
		billFlg.append(order.getItemData(0, "BILL_FLG"));
		switch (rxType) {
		case 1:// 西药
			rxNo = (String) this.getValue("MED_RX");
			tableName = TABLE_MED;
			this.setValue("MED_AMT", "");
			if (StringUtil.isNullString(tableName)) {
				this.messageBox("E0034"); // 取得数据错误
				return;
			}
			table = (TTable) this.getComponent(tableName);
			count = order.rowCount();
			if (count <= 0) {
				return;
			}
			for (int i = count - 1; i > -1; i--) {
				if (rxType == 1 || rxType == 2) {
					//物联网start
					if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 校验物联网注记
						if (i - 1 >= 0) {
							if (!TypeTool.getBoolean(order.getItemData(i-1, "RELEASE_FLG"))) { // 先判断是否是自备药  add by huangtt 20151012
								if (!order.checkDrugCanUpdate("MED", i-1)) { // 判断是否可以修改（有没有进行审,配,发）
									this.messageBox("E0189");
									return;
								}
							}	

						}

					} else {
						if (i - 1 >= 0) {
							if (!this.checkDrugCanUpdate(order, "MED", i - 1,
									false,null)) { // 判断是否可以修改（有没有进行审,配,发）
								this.messageBox("E0189");
								return;
							}
						}
					}
					//物联网end
				}
				String tempCode = order.getItemString(i, "ORDER_CODE");
				if (StringUtil.isNullString(tempCode))
					continue;
				if (!deleteOrder(order, i, "已打票,不可以修改或删除医嘱","此医嘱已经登记,不能删除")) {
					return;
				}
				if (!TypeTool.getBoolean(order.getItemData(i, "RELEASE_FLG"))) { // 先判断是否是自备药  add by huangtt 20151012
					if(!deleteSumRxOrder(order, i, billFlg)){
						return;
					}
				}

			}
			for (int i = count - 1; i > -1; i--) {
				order.deleteRow(i);
			}
			order.newOrder(rxType + "", rxNo);
			table.setDSValue();
			break;
		case 2://管制药品

			rxNo = (String) this.getValue("CTRL_RX");
			tableName = TABLE_CTRL;
			this.setValue("CTRL_AMT", "");
			if (StringUtil.isNullString(tableName)) {
				this.messageBox("E0034"); // 取得数据错误
				return;
			}
			table = (TTable) this.getComponent(tableName);
			count = order.rowCount();
			if (count <= 0) {
				return;
			}
			for (int i = count - 1; i > -1; i--) {
				if (rxType == 1 || rxType == 2) {
					if (!order.checkDrugCanUpdate("MED", i)) { // 判断是否可以修改（有没有进行审,配,发）
						this.messageBox("E0189");
						return;
					}
				}
				String tempCode = order.getItemString(i, "ORDER_CODE");
				if (StringUtil.isNullString(tempCode))
					continue;
				if (!deleteOrder(order, i, "已打票,不可以修改或删除医嘱","此医嘱已经登记,不能删除")) {
					return;
				} 
				if(!deleteSumRxOrder(order, i, billFlg)){
					return;
				}
			}
			for (int i = count - 1; i > -1; i--) {
				order.deleteRow(i);
			}
			order.newOrder(rxType + "", rxNo);
			table.setDSValue();
			break;
		case 3: // 中药
			rxNo = (String) this.getValue("CHN_RX");
			tableName = TABLE_CHN;
			this.setValue("CHN_AMT", "");
			this.setValue("PACKAGE_TOT", "");
			count = order.rowCount();
			if (count <= 0) {
				return;
			}
			for (int i = count - 1; i > -1; i--) {
				String tempCode = order.getItemString(i, "ORDER_CODE");
				if (StringUtil.isNullString(tempCode))
					continue;				
				if (!order.checkDrugCanUpdate("CHN", i)) { // 判断是否可以修改（有没有进行审,配,发）
					this.messageBox("E0189");
					return;
				}
				if (!deleteOrder(order, i, "已打票,不可以修改或删除医嘱","此医嘱已经登记,不能删除")) {
					return;
				} 
				if(!deleteSumRxOrder(order, i, billFlg)){
					return;
				}
			}
			for (int i = count - 1; i > -1; i--) {
				order.deleteRow(i);
			}
			this.initChnTable(rxNo);
			break;
		case 4:
			rxNo = (String) this.getValue("OP_RX");
			tableName = TABLE_OP;
			this.setValue("OP_AMT", "");

			oldfilter = order.getFilter();
			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			count = order.rowCount();
			table = (TTable) this.getComponent(TABLE_OP);
			if (count <= 0) {
				return;
			}
			for (int i = count - 1; i > -1; i--) {
				String tempCode = order.getItemString(i, "ORDER_CODE");
				if (StringUtil.isNullString(tempCode))
					continue;
				if (!deleteOrder(order, i, "已打票,不可以修改或删除医嘱","此医嘱已经登记,不能删除")) {
					order.setFilter(oldfilter);
					order.filter();
					table.setDSValue();
					return;
				} 
				if(!deleteSumRxOrder(order, i, billFlg)){
					return;
				}
			}
			for (int i = count - 1; i > -1; i--) {
				String tempCode = order.getItemString(i, "ORDER_CODE");
				if (StringUtil.isNullString(tempCode))
					continue;
				order.deleteRow(i);
			}
			order.setFilter(oldfilter);
			order.filter();

			table.setDSValue();
			break;
		case 5:
			rxNo = (String) this.getValue("EXA_RX");
			tableName = TABLE_EXA;
			this.setValue("EXA_AMT", "");

			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			count = order.rowCount();
			table = (TTable) this.getComponent(TABLE_EXA);
			if (count <= 0) {
				return;
			}

			for (int i = count - 1; i > -1; i--) {
				String tempCode = order.getItemString(i, "ORDER_CODE");
				if (StringUtil.isNullString(tempCode))
					continue;
				//======pangben 2012-8-10 添加已到检
				if (!checkSendPah(order, i))// 检查已发药已到检
					return;
				//=========pangben 2013-1-29
				if (!deleteOrder(order, i, "已打票,不可以修改或删除医嘱","此处方签中存在已经登记的医嘱,不能删除")) {
					order.setFilter(oldfilter);
					order.filter();
					return;
				} 
				if(!deleteSumRxOrder(order, i, billFlg)){
					return;
				}
			}
			// 提取出已有的所有 med_apply信息的 APPLICATION_NO
			MedApply med = odo.getOpdOrder().getMedApply();
			Map appNo = new HashMap();
			for (int i = 0; i < med.rowCount(); i++) {
				String key = med.getItemString(i, "ORDER_NO")
						+ med.getItemString(i, "SEQ_NO")
						+ med.getItemString(i, "CAT1_TYPE");
				appNo.put(key, med.getItemString(i, "APPLICATION_NO"));
			}
			for (int i = count - 1; i > -1; i--) {
				String tempCode = order.getItemString(i, "ORDER_CODE");
				if (StringUtil.isNullString(tempCode))
					continue;
				// 删除med_apply的对应信息
				// 判断是检验检查医嘱的主项 med_apply记录的是主项 删除主项
				if (("LIS".equals(order.getItemString(i, "ORDER_CAT1_CODE")) || "RIS"
						.equals(order.getItemString(i, "ORDER_CAT1_CODE")))
						&& "Y".equals(order.getItemString(i, "SETMAIN_FLG"))) {
					String labMapKey = order.getItemString(i, "RX_NO")
							+ order.getItemString(i, "SEQ_NO")
							+ order.getItemString(i, "CAT1_TYPE");
					med.deleteRowBy((String) appNo.get(labMapKey), order
							.getItemString(i, "RX_NO"), order.getItemInt(i,
									"SEQ_NO"), order.getItemString(i, "CAT1_TYPE"));
				}
				order.deleteRow(i);
			}
			order.setFilter(oldfilter);
			order.filter();
			table.setDSValue();
			break;
		}
		//收费操作
		if("Y".equals(billFlg.toString()))
			onFee();// 执行删除医嘱
		else
			onTempSave();
		//onFee();
	}
	/**
	 * 删除整张处方签医嘱管控
	 * 
	 * @return
	 */
	private boolean deleteSumRxOrder(OpdOrder order,int row,StringBuffer billFlg) {

		if(!billFlg.toString().equals(order.getItemData(row, "BILL_FLG").toString())){
			this.messageBox("此处方签中医嘱状态不同,不能执行删除");
			return false;
		}
		return true;
	}

	/**
	 * combo点选事件,根据处方号初始化table
	 * 
	 * @param rxType
	 *            处方类型
	 */
	public void onChangeRx(String rxType) {
		if (StringUtil.isNullString(rxType)) {
			this.messageBox("E0035"); // 操作失败
			return;
		}
		TTabbedPane tabbedPane = (TTabbedPane) this
				.getComponent("TTABPANELORDER");
		if (!EXA.equalsIgnoreCase(rxType) && tabbedPane.getSelectedIndex() == 0) {
			return;
		}
		int type = StringTool.getInt(rxType);
		String rxNo;
		if (odo == null || odo.getOpdOrder() == null)
			return;
		switch (type) {
		// 西药
		case 1:
			rxNo = this.getValueString(MED_RX);
			if (StringUtil.isNullString(rxNo)) {
				odo.getOpdOrder().setFilter(
						"RX_TYPE='" + MED + "' AND ORDER_CODE <>''");
				odo.getOpdOrder().filter();
				tblMed.setDSValue();
				calculateCash(TABLE_MED, "MED_AMT");
				return;
			}
			if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
				odo.getOpdOrder().newOrder(rxType, rxNo);
			}
			if (!initNoSetTable(rxNo, TABLE_MED, false,true))
				this.messageBox("E0036"); // 显示西药失败
			this.setValue("MED_RBORDER_DEPT_CODE", phaCode);
			calculateCash(TABLE_MED, "MED_AMT");
			break;
			// 毒麻药
		case 2:

			rxNo = this.getValueString(CTRL_RX);
			if (StringUtil.isNullString(rxNo)) {
				odo.getOpdOrder().setFilter(
						"RX_TYPE='" + CTRL + "' AND ORDER_CODE <>''");
				odo.getOpdOrder().filter();
				tblCtrl.setDSValue();
				calculateCash(TABLE_CTRL, "CTRL_AMT");
				return;
			}
			if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
				odo.getOpdOrder().newOrder(rxType, rxNo);
			}
			if (!initNoSetTable(rxNo, TABLE_CTRL, false,true))
				this.messageBox("E0037"); // 显示毒麻药失败
			this.setValue("CTRL_RBORDER_DEPT_CODE", phaCode);
			calculateCash(TABLE_CTRL, "CTRL_AMT");
			break;
			// 中药
		case 3:
			rxNo = this.getValueString(CHN_RX);
			if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
				odo.getOpdOrder().newOrder(rxType, rxNo);
			}
			initChnTable(rxNo);
			calculateChnCash(rxNo);
			calculatePackageTot(rxNo);
			break;
			// 处置
		case 4:
			rxNo = this.getValueString(OP_RX);
			if (StringUtil.isNullString(rxNo)) {
				odo.getOpdOrder().setFilter(
						"RX_TYPE='" + OP + "' AND ORDER_CODE <>''");
				odo.getOpdOrder().filter();
				tblOp.setDSValue();
				calculateCash(TABLE_OP, "OP_AMT");
				return;
			}
			if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
				odo.getOpdOrder().newOrder(rxType, rxNo);
			}
			if (!initNoSetTable(rxNo, TABLE_OP, false,true))
				this.messageBox("E0038"); // 显示处置失败
			calculateCash(TABLE_OP, "OP_AMT");
			break;
			// 检验检查
		case 5:
			rxNo = this.getValueString(EXA_RX);
			if (StringUtil.isNullString(rxNo)) {
				odo.getOpdOrder().setFilter(
						"RX_TYPE='" + EXA + "' AND SETMAIN_FLG='Y'");
				odo.getOpdOrder().filter();
				tblExa.setDSValue();
				calculateCash(TABLE_EXA, "EXA_AMT");
				return;
			}
			odo.getOpdOrder().setFilter(
					"RX_NO='" + rxNo
					+ "' AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')");
			odo.getOpdOrder().filter();
			if (!StringUtil.isNullString(odo.getOpdOrder().getItemString(
					odo.getOpdOrder().rowCount() - 1, "ORDER_CODE"))) {
				odo.getOpdOrder().newOrder(rxType, rxNo);
			}

			if (!initSetTable(TABLE_EXA, false))
				this.messageBox("E0039"); // 显示检验检查失败

			break;
			// 院前用药   add by huangtt 20151201
		case 6:
			rxNo = this.getValueString(PREMED_RX);
			System.out.println("初始化rxNo---"+rxNo);
			if (StringUtil.isNullString(rxNo)) {
				odo.getOpdOrder().setFilter(
						"RX_TYPE='" + PREMED + "' AND ORDER_CODE <>''");
				odo.getOpdOrder().filter();
				tblPreMed.setDSValue();
				return;
			}
			if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
				odo.getOpdOrder().newOrder(rxType, rxNo);
			}
			if (!initNoSetTable(rxNo, TABLE_PRE_MED, false,true))
				this.messageBox("E0036"); // 显示西药失败

			break;
		}
	}

	/**
	 * 计算并设置金额
	 * 
	 * @param tableName
	 *            String
	 * @param tag
	 *            String
	 */
	public void calculateCash(String tableName, String tag) {
		TTable table = (TTable) this.getComponent(tableName);

		double arAmt = 0.0;
		OpdOrder order = odo.getOpdOrder();
		int count = order.rowCount();
		if (order.rowCount() < 1)
			return;
		if (TABLE_EXA.equalsIgnoreCase(tableName)) {
			String field = "AR_AMT_MAIN";
			int column = table.getColumnIndex(field);
			int countTable = table.getRowCount();
			for (int i = 0; i < countTable; i++) {
				if (StringUtil.isNullString(order
						.getItemString(i, "ORDER_DESC")))
					continue;
				if (StringTool
						.getBoolean(order.getItemString(i, "RELEASE_FLG"))) {
					continue;
				}
				if (!StringUtil.isNullString(order
						.getItemString(i, "BILL_USER"))) {
					continue;
				}
				arAmt += (Double) table.getValueAt(i, column);
			}
			this.setValue(tag, arAmt);

			return;
		}

		for (int i = 0; i < count; i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_DESC")))
				continue;
			if (StringTool.getBoolean(order.getItemString(i, "RELEASE_FLG"))) {
				continue;
			}
			if (!StringUtil.isNullString(order.getItemString(i, "BILL_USER"))) {
				continue;
			}
			arAmt += order.getItemDouble(i, "AR_AMT");
		}

		this.setValue(tag, arAmt);
	}

	/**
	 * 中药计算总金额
	 * 
	 * @param rxNo
	 *            处方号
	 */
	public void calculateChnCash(String rxNo) {
		OpdOrder order = odo.getOpdOrder();
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		int count = order.rowCount();
		double arAmt = 0.0;
		for (int i = 0; i < count; i++) {
			arAmt += order.getItemDouble(i, "AR_AMT");
		}
		this.setValue("CHN_AMT", arAmt);
	}

	/**
	 * 计算并设置OpdOrder服总量
	 * 
	 * @param rxNo
	 *            String
	 */
	public void calculatePackageTot(String rxNo) {
		OpdOrder order = odo.getOpdOrder();
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		long tot = 0;
		for (int i = 0; i < order.rowCount(); i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_CODE"))) {
				continue;
			}
			tot += order.getItemDouble(i, "MEDI_QTY");
		}
		for (int i = 0; i < order.rowCount(); i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_CODE"))) {
				continue;
			}
			order.setItem(i, "PACKAGE_TOT", tot);
		}
		this.setValue("PACKAGE_TOT", tot);
	}

	/**
	 * 添加诊断弹出窗口
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onDiagCreateEditComponent(Component com, int row, int column) {
		//selectDiagRow = row;
		if (column != 1)
			return;
		if (!(com instanceof TTextField))
			return;
		//tempIcd = odo.getDiagrec().getItemString(row, "ICD_CODE");
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// 给table上的新text增加sys_fee弹出窗口
		TParm parm = new TParm();
		parm.setData("ICD_TYPE", wc);
		textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popDiagReturn");
	}

	/**
	 * 添加诊断弹出窗口
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onMedHistoryCreateEditComponent(Component com, int row,
			int column) {
		if (column != 2) {
			return;
		}
		if (!(com instanceof TTextField)) {
			return;
		}
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		String wcMed = odo.getMedHistory().getItemString(row, "ICD_TYPE");
		TParm parm = new TParm();
		parm.setData("ICD_TYPE", wcMed);
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popMedHistoryReturn");
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
	public void onPreMedCreateEditComponent(Component com, int row, int column) {
		TTable table = (TTable) this.getComponent(TABLE_PRE_MED);
		// 求出当前列号
		column = table.getColumnModel().getColumnIndex(column);
		String columnName = table.getParmMap(column);
		if (!"ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName)) {
			return;
		}
		// 集合医嘱不可更新
		int selRow = table.getSelectedRow();
		TParm existParm = table.getDataStore().getRowParm(selRow);
		if (this.isOrderSet(existParm)) {
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			return;
		}
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		tableName = TABLE_PRE_MED;
		rxName = PREMED_RX;
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// 设置弹出菜单
		textFilter.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popPreOrderReturn");
	}

	/**
	 * 添加SYS_FEE弹出窗口
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onMedCreateEditComponent(Component com, int row, int column) {
		//selectOrderRow = row;

		TTable table = (TTable) this.getComponent(TABLE_MED);
		// 求出当前列号
		column = table.getColumnModel().getColumnIndex(column);
		String columnName = table.getParmMap(column);
		// ============xueyf modify 20120309 
		if (!("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName) || "ORDER_ENG_DESC"
				.equalsIgnoreCase(columnName))) {
			return;
		}
		// 集合医嘱不可更新
		int selRow = table.getSelectedRow();
		TParm existParm = table.getDataStore().getRowParm(selRow);
		if (this.isOrderSet(existParm)) {
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			return;
		}
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		tableName = TABLE_MED;
		rxName = MED_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 1);
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popOrderReturn");

	}

	/**
	 * 添加SYS_FEE弹出窗口
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onAllergyCreateEditComponent(Component com, int row, int column) {
		if (column != 2)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		if (TCM_Transform.getBoolean(this.getValue("ORDER_ALLERGY"))) {
			TParm parm = new TParm();
			//modify by huangtt 20150505 start 
			if("B".equals(tblAllergy.getItemString(row, 1))){
				parm.setData("ALLERGY_TYPE", "allergyType");
				// 给table上的新text增加sys_fee弹出窗口
				textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
						"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);

			}else{
				parm.setData("ALLERGY_TYPE", tblAllergy.getItemString(row, 1));
				// 给table上的新text增加sys_fee弹出窗口
				textfield.setPopupMenuParameter("", getConfigParm().newConfig(
						"%ROOT%\\config\\sys\\SYSPhaClassPopup.x"), parm);
			}
			//modify by huangtt 20150505 end

			// 给新text增加接受sys_fee弹出窗口的回传值
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popAllergyReturn");

		} else if (TCM_Transform.getBoolean(this.getValue("INGDT_ALLERGY"))) {
			TParm parm = new TParm();
			parm.addData("ALLERGY_TYPE", "A");
			// 给table上的新text增加sys_fee弹出窗口
			textfield.setPopupMenuParameter("INGDT", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SysAllergy.x"), parm);

			// 给新text增加接受sys_fee弹出窗口的回传值
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popAllergyReturn");
			//add by huangtt 20151116 start
		}else if (TCM_Transform.getBoolean(this.getValue("NO_ALLERGY"))) {	

			// 过敏记录设置
			TParm parm = new TParm();
			parm.addData("ALLERGY_TYPE", "N");
			// 设置弹出菜单
			textfield.setPopupMenuParameter("GMN", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SysAllergy.x"), parm);
			// 给新text增加接受sys_fee弹出窗口的回传值
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popAllergyReturn");

			//add by huangtt 20151116 end
		} else {
			TParm parm = new TParm();
			parm.addData("ALLERGY_TYPE", "C");
			// 给table上的新text增加sys_fee弹出窗口
			textfield.setPopupMenuParameter("OTHER", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SysAllergy.x"), parm);

			// 给新text增加接受sys_fee弹出窗口的回传值
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popAllergyReturn");

		}
	}

	/**
	 * 过敏史添加
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popAllergyReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		if (StringUtil.isNullString(TABLEALLERGY)) {
			this.messageBox("E0034"); // 取得数据错误
			return;
		}
		TTable table = (TTable) this.getComponent(TABLEALLERGY);
		table.acceptText();
		int row = table.getSelectedRow();
		String desc;
		String oldCode = odo.getDrugAllergy().getItemString(row,
				"DRUGORINGRD_CODE");
		//ADM_DATE_FORMAT;DRUG_TYPE;DRUGORINGRD_DESC;ALLERGY_NOTE;DEPT_CODE;DR_CODE;ADM_TYPE;CASE_NO

		// 判断是否已经超过看诊时限
		if (!canEdit()) {
			table.setDSValue(row);
			this.messageBox_("已超过看诊时间不可修改");
			return;
		}
		if (!StringUtil.isNullString(oldCode)) {
			this.messageBox("E0040"); // 不能替代该数据，请重新新增或删除该数据
			table.setDSValue(row);
			return;
		}
		if (StringTool.getBoolean(this.getValueString("ORDER_ALLERGY"))) {
			odo.getDrugAllergy().setItem(row, "DRUGORINGRD_CODE",
					parm.getValue("ORDER_CODE"));
			odo.getDrugAllergy().setItem(row, "DRUG_TYPE", allergyType); 
			//			desc = "B";
			desc = allergyType;  //modify by huangtt 20150505
		} else if (StringTool.getBoolean(this.getValueString("INGDT_ALLERGY"))) {
			odo.getDrugAllergy().setItem(row, "DRUGORINGRD_CODE",
					parm.getValue("ID"));
			odo.getDrugAllergy().setItem(row, "DRUG_TYPE", allergyType);
			desc = "A";
			//add by huangtt 20151111 start 添加过敏无
		} else if (StringTool.getBoolean(this.getValueString("NO_ALLERGY"))) {
			odo.getDrugAllergy().setItem(row, "DRUGORINGRD_CODE",
					parm.getValue("ID"));
			odo.getDrugAllergy().setItem(row, "DRUG_TYPE", allergyType);
			desc = "N";
			//add by huangtt 20151111 end 添加过敏无
		} else {
			odo.getDrugAllergy().setItem(row, "DRUGORINGRD_CODE",
					parm.getValue("ID"));
			odo.getDrugAllergy().setItem(row, "DRUG_TYPE", allergyType);
			desc = "C";
		}
		odo.getDrugAllergy().setActive(table.getSelectedRow(), true);
		int newRow = 0;
		if (table.getSelectedRow() == table.getRowCount() - 1) {
			newRow = odo.getDrugAllergy().insertRow();
			odo.getDrugAllergy().setItem(newRow, "DRUG_TYPE", desc);
		}
		table.setDSValue();
		table.getTable().grabFocus();
		table.setSelectedRow(newRow);
		table.setSelectedColumn(2);
	}

	/**
	 * 添加SYS_FEE弹出窗口
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCtrlCreateEditComponent(Component com, int row, int column) {
		//selectOrderRow = row;
		TTable table = (TTable) this.getComponent(TABLE_CTRL);
		// 求出当前列号
		column = table.getColumnModel().getColumnIndex(column);
		String columnName = table.getParmMap(column);
		// ============xueyf modify 20120309 
		if (!("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName) || "ORDER_ENG_DESC"
				.equalsIgnoreCase(columnName)))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		tableName = TABLE_CTRL;
		rxName = CTRL_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 2);
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popOrderReturn");

	}

	/**
	 * 添加SYS_FEE弹出窗口
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onChnCreateEditComponent(Component com, int row, int column) {
		//selectOrderRow = row;
		if (column != 0 && column != 3 && column != 6 && column != 9) {
			return;
		}
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		tableName = TABLE_CHN;
		rxName = CHN_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 3);
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popChnOrderReturn");

	}

	/**
	 * 添加SYS_FEE弹出窗口
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onOpCreateEditComponent(Component com, int row, int column) {
		//selectOrderRow = row;
		TTable table = (TTable) this.getComponent(TABLE_OP);
		// 求出当前列号
		column = table.getColumnModel().getColumnIndex(column);
		String columnName = table.getParmMap(column);
		// ============xueyf modify 20120309
		if (!("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName) || "ORDER_ENG_DESC"
				.equalsIgnoreCase(columnName)))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		tableName = TABLE_OP;
		rxName = OP_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 4);
		textfield.onInit();
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popOpReturn");

	}

	/**
	 * 添加SYS_FEE弹出窗口(检验检查窗口)
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onExaCreateEditComponent(Component com, int row, int column) {
		//selectOrderRow = row;
		TTable table = (TTable) this.getComponent(TABLE_EXA);
		// 求出当前列号
		column = table.getColumnModel().getColumnIndex(column);
		String columnName = table.getParmMap(column);
		// ============xueyf modify 20120309 
		if (!("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName) || "ORDER_ENG_DESC"
				.equalsIgnoreCase(columnName))) {
			return;
		}
		int selRow = table.getSelectedRow();
		TParm existParm = table.getDataStore().getRowParm(selRow);
		if (this.isOrderSet(existParm)) {
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			return;
		}
		if (!(com instanceof TTextField)) {
			return;
		}
		TTextField textfield = (TTextField) com;
		tableName = TABLE_EXA;
		rxName = EXA_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 5); // 检验检查 CAT1_TYPE = LIS/RIS
		textfield.onInit();
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popExaReturn");
	}

	/**
	 * 新增诊断
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popDiagReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable tableDiag = (TTable) this.getComponent(TABLEDIAGNOSIS);
		tableDiag.acceptText();
		Diagrec diagRec = odo.getDiagrec();
		int rowNo = tableDiag.getSelectedRow();
		String icdTemp = parm.getValue("ICD_CODE");
		// 判断是否已经超过看诊时限
		if (!canEdit()) {
			tableDiag.setDSValue(rowNo);
			this.messageBox_("已超过看诊时间不可修改");
			return;
		}
		tableDiag.acceptText();
		if (diagRec.isHaveSameDiag(icdTemp)) {
			diagRec.deleteRow(rowNo);
			tableDiag.acceptText();
			tableDiag.getTable().grabFocus();
			tableDiag.setSelectedRow(0);
			tableDiag.setSelectedColumn(1);
			tableDiag.addRow();
			tableDiag.setDSValue();
			this.messageBox("E0041"); // 该诊断已开立
			return;
		}
		if (!OdoUtil.isAllowDiag(parm, Operator.getDept(), pat.getSexCode(),
				pat.getBirthday(), (Timestamp) this.getValue("ADM_DATE"))) {
			this.messageBox("E0042"); // 诊断不适用于该病人，请重新开立
			diagRec.deleteRow(rowNo);
			tableDiag.acceptText();
			tableDiag.getTable().grabFocus();
			tableDiag.setSelectedRow(0);
			tableDiag.setSelectedColumn(1);
			tableDiag.addRow();
			tableDiag.setDSValue();
			return;
		}

		boolean isHavingMain = diagRec.haveMainDiag(new int[1]);
		if (!isHavingMain) {
			if ("C".equalsIgnoreCase(wc) && !OdoUtil.isAllowChnDiag(parm)) {
				this.messageBox("E0018"); // 该诊断为症候，不能做为诊断
				diagRec.deleteRow(rowNo);
				// odo.getDiagrec().insertRow();
				tableDiag.acceptText();
				tableDiag.getTable().grabFocus();
				tableDiag.setSelectedRow(0);
				tableDiag.setSelectedColumn(1);
				tableDiag.addRow();
				tableDiag.setDSValue();
				return;
			}
			String mainDiagFlg = diagRec.isMainFlg(parm.getValue("ICD_CODE")) ? "Y"
					: "N";

			diagRec.setItem(rowNo, "MAIN_DIAG_FLG", mainDiagFlg);
			//mainFlg = true;
		} else {
			diagRec.setItem(rowNo, "MAIN_DIAG_FLG", "N");
			// mainFlg=false;
		}
		diagRec.setActive(rowNo, true);
		diagRec.setItem(rowNo, "ICD_TYPE", wc);
		if (parm.getValue("DIAG_NOTE").length() > 0) { // 组套传回备注字段名称 与字典中的不同
			// 需要判断一下是不是组套回传的
			diagRec.setItem(rowNo, "DIAG_NOTE", parm.getValue("DIAG_NOTE"));
		} else {
			diagRec.setItem(rowNo, "DIAG_NOTE", parm.getValue("DESCRIPTION"));
		}
		diagRec.setItem(rowNo, "ORDER_DATE", diagRec.getDBTime());
		String fileNo = parm.getValue("MR_CODE");
		if (!StringUtil.isNullString(fileNo)) {
			diagRec.setItem(rowNo, "MR_CODE", fileNo);
		}
		tableDiag.setItem(rowNo, "ICD_CODE", parm.getValue("ICD_CODE"));
		tableDiag.setDSValue();
		if (rowNo == tableDiag.getRowCount() - 1) {
			rowNo = tableDiag.addRow();
		}
		tableDiag.getTable().grabFocus();
		tableDiag.setSelectedRow(rowNo);
		tableDiag.setSelectedColumn(1);
	}

	/**
	 * 诊断中英文显示
	 */
	public void onChnEng() {
		TCheckBox chnEng = (TCheckBox) this.getComponent("CHN_DSNAME");
		TTable tableDiag = (TTable) this.getComponent("TABLEDIAGNOSIS");
		if (chnEng.isSelected()) {
			chnEng.setText("Chinese version");
			// ============xueyf modify 20120220 
			tableDiag.setHeader("Main,30,boolean;Code,150;Notes,130;Order Dr,100,DR_CODE;Order Time,120,timestamp,yyyy/MM/dd HH:mm");
			tableDiag.setParmMap("MAIN_DIAG_FLG;ICD_ENG_DESC;DIAG_NOTE;DR_CODE;ORDER_DATE");
			tableDiag.setDSValue();
		} else {
			chnEng.setText("英文病名");
			// ============xueyf modify 20120220 
			tableDiag.setHeader("主,30,boolean;名称,150;备注,130;开立医生,100,DR_CODE;开立时间,120,timestamp,yyyy/MM/dd HH:mm");
			tableDiag.setParmMap("MAIN_DIAG_FLG;ICD_DESC;DIAG_NOTE;DR_CODE;ORDER_DATE");
			tableDiag.setDSValue();
		}
	}

	/**
	 * 商品名方法
	 * 
	 * @param tag
	 *            String
	 * @param checkBox
	 *            String
	 */
	public void onGoods(String tag, String checkBox) {
		acceptForSave();
		TCheckBox tcb = (TCheckBox) this.getComponent(checkBox);
		TTable table = (TTable) this.getComponent(tag);
		// ORDER_ENG_DESC
		if (tcb.isSelected()) {
			if (TABLE_OP.equalsIgnoreCase(tag)) {
				table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_ENG_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;OWN_PRICE_MAIN;DISPENSE_QTY;OWN_AMT_MAIN;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT_MAIN;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;NS_EXEC_DATE");
//				table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_ENG_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;NS_EXEC_DATE");
				table.setDSValue();
			}
			if (TABLE_MED.equalsIgnoreCase(tag)) {
				if(admType.equals("O")){
					table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_ENG_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE");
				}else{
					table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_ENG_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE;ORDER_DATE");
				}
//				table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_ENG_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE");
				table.setDSValue();
			}
			if (TABLE_CTRL.equalsIgnoreCase(tag)) {
				if(admType.equals("O")){
					table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_ENG_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE");
				}else{
					table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_ENG_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE;ORDER_DATE");
				}
//				table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_ENG_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE");
				table.setDSValue();
			}
		} else {
			// ORDER_DESC
			if (TABLE_OP.equalsIgnoreCase(tag)) {
//				table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;NS_EXEC_DATE");
				table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;OWN_PRICE_MAIN;DISPENSE_QTY;OWN_AMT_MAIN;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT_MAIN;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;NS_EXEC_DATE");
				table.setDSValue();
			}
			if (TABLE_MED.equalsIgnoreCase(tag)) {
				if(admType.equals("O")){
					table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE");
				}else{
					table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE;ORDER_DATE");
				}
//				table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE");
				table.setDSValue();
			}
			if (TABLE_CTRL.equalsIgnoreCase(tag)) {
				if(admType.equals("O")){
					table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE");
				}else{
					table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE;ORDER_DATE");
				}
//				table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE");
				table.setDSValue();
			}
		}
	}

	/**
	 * 新增既往史
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popMedHistoryReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable tableMedHistory = (TTable) this.getComponent(TABLEMEDHISTORY);
		tableMedHistory.acceptText();
		int rowNo = tableMedHistory.getSelectedRow();
		// 判断是否已经超过看诊时限
		if (!canEdit()) {
			tableMedHistory.setDSValue(rowNo);
			this.messageBox_("已超过看诊时间不可修改");
			return;
		}
		if (odo.getMedHistory().isSameICD(parm.getValue("ICD_CODE"))) {
			this.messageBox("E0043"); // 不允许开立重复诊断
			tableMedHistory.setDSValue(rowNo);
			return;
		}
		String oldCode = odo.getMedHistory().getItemString(rowNo, "ICD_CODE");
		if (!StringUtil.isNullString(oldCode)) {
			this.messageBox("E0040"); // 不能替代该数据，请重新新增或删除该数据`
			tableMedHistory.setDSValue(rowNo);
			return;
		}
		odo.getMedHistory().setActive(rowNo, true);

		odo.getMedHistory().setItem(rowNo, "ICD_CODE",
				parm.getValue("ICD_CODE"));
		odo.getMedHistory().setItem(rowNo, "ICD_TYPE",
				parm.getValue("ICD_TYPE"));
		odo.getMedHistory().setItem(rowNo, "SEQ_NO",
				odo.getMedHistory().getMaxSEQ(odo.getMrNo()));
		if (rowNo == tableMedHistory.getRowCount() - 1)
			odo.getMedHistory().insertRow();
		tableMedHistory.setDSValue();
		tableMedHistory.getTable().grabFocus();
		tableMedHistory.setSelectedRow(odo.getMedHistory().rowCount() - 1);
		tableMedHistory.setSelectedColumn(2);
	}

	/**
	 * 判断是否是过敏史中的药品
	 * 
	 * @param orderCode
	 *            String
	 * @return boolean
	 */
	public boolean isAllergy(String orderCode) {
		for (int i = 0; i < odo.getDrugAllergy().rowCount(); i++) {
			if (orderCode.equalsIgnoreCase(odo.getDrugAllergy().getItemString(
					i, "DRUGORINGRD_CODE"))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 过敏类型改变
	 * 
	 * @param type
	 *            String
	 */
	public void onAllg(String type) {
		TTable table = (TTable) this.getComponent(TABLEALLERGY);
		table.acceptText();
		if (TCM_Transform.getBoolean(this.getValue("OTHER_ALLERGY"))) {
			allergyType = "C";
			table.setFilter(" DRUG_TYPE='C' ");
			table.filter();
			table.setDSValue();
		} else if (TCM_Transform.getBoolean(this.getValue("INGDT_ALLERGY"))) {
			allergyType = "A";
			table.setFilter(" DRUG_TYPE='A' ");
			table.filter();
			table.setDSValue();
			//add by huangtt 20151111 start
		} else if (TCM_Transform.getBoolean(this.getValue("NO_ALLERGY"))) {
			allergyType = "N";
			table.setFilter(" DRUG_TYPE='N' ");
			table.filter();
			table.setDSValue();

			//			table.getTable().grabFocus();
			//			table.setSelectedRow(0);
			//			
			//			TParm parm = new TParm();
			//			parm.setData("ID", "N");
			//			popAllergyReturn("",  parm);
			//add by huangtt 20151111 end
		} else {
			//modify by huangtt 20150505 start
			//			allergyType = "B";
			//			table.setFilter(" DRUG_TYPE='B' ");
			allergyType = type;
			table.setFilter(" DRUG_TYPE ='"+type+"'");
			//modify by huangtt 20150505 end
			table.filter();
			table.setDSValue();
		}
	}

	/**
	 * 新增检验检查
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popExaReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable table = (TTable) this.getComponent(TABLE_EXA);

		int column = table.getSelectedColumn();
		table.acceptText();

		if (StringUtil.isNullString(tableName)) {
			this.messageBox("E0034"); // 取得数据错误
			return;
		}
		if ("N".equalsIgnoreCase(parm.getValue("ORDERSET_FLG"))) {
			this.messageBox("E0044"); // 该医嘱不是集合医嘱主项
			return;
		}

		OpdOrder order = (OpdOrder) table.getDataStore();
		// ============pangben 2012-2-29 添加管控
		int count = order.rowCount();
		if (count <= 0) {
			return;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!deleteOrder(order, i, "已经打票的处方签不可以添加医嘱","此医嘱已经登记,不能删除")) {
				return;

			}
		}
		// ============pangben 2012-2-29 stop
		int row = order.rowCount() - 1;
		String orderCode = order.getItemString(row, "ORDER_CODE");
		// 判断是否已经超过看诊时限
		if (!canEdit()) {
			table.setDSValue(row);
			this.messageBox_("已超过看诊时间不可修改");
			return;
		}
		if (orderCode != null && orderCode.trim().length() > 0) {
			this.messageBox("E0045"); // 已开立医嘱不可变更，请删除此医嘱或新开立
			table.setDSValue(row);
			return;
		}
		//		if (order.isSameOrder(parm.getValue("ORDER_CODE"))) {
		//			if (this.messageBox(
		//							"提示信息/Tip",
		//							"该医嘱已经开立，是否继续？\r\n/This order exists,Do you proceed it again?",
		//							0) == 1) {
		//				table.setDSValue(row);
		//				return;
		//			}
		//
		//		}
		insertExa(parm, row, column);
	}

	/**
	 * 新增检验检查
	 * 
	 * @param parm
	 *            SysFee
	 * @param row
	 *            TABLE 选中行
	 * @param column
	 *            TABLE 选中列
	 */
	private void insertExa(TParm parm, int row, int column) {
		int oldRow = row;
		TTable table = (TTable) this.getComponent(TABLE_EXA);
		OpdOrder order = (OpdOrder) table.getDataStore();
		// add by yanj 2013/07/17 门急诊适用校验
		// 门诊
		if ("O".equalsIgnoreCase(admType)) {
			// 判断是否住院适用医嘱
			if (!("Y".equals(parm.getValue("OPD_FIT_FLG")))) {
				// 不是门诊适用医嘱！
				this.messageBox("不是门诊适用检验检查。");
				return;
			}
		}
		// 急诊
		if ("E".equalsIgnoreCase(admType)) {
			if (!("Y".equals(parm.getValue("EMG_FIT_FLG")))) {
				// 不是门诊适用医嘱！
				this.messageBox("不是急诊适用检验检查。");
				return;
			}
		}
		// $$===========add by yanj 2013/07/17 门急诊适用校验
		if (order.isSameOrder(parm.getValue("ORDER_CODE"))) {
			if (this.messageBox(
					"提示信息/Tip",
					"该医嘱已经开立，是否继续？\r\n/This order exists,Do you proceed it again?",
					0) == 1) {
				table.setDSValue(row);
				return;
			}
		}
		String[] rxNos = order.getRx(EXA);
		if (rxNos == null || rxNos.length < 1) {
			return;
		}

		String rxNo = this.getValueString(this.EXA_RX);
		if (StringUtil.isNullString(rxNo)) {
			this.messageBox("E0029"); // 没有处方签
			return;
		}

		int groupNo = order.getMaxGroupNo();

		String execDept = parm.getValue("EXEC_DEPT_CODE");
		if (StringUtil.isNullString(execDept)) {
			execDept = Operator.getDept();
		}
		// Date d1=new Date();
		initOrder(order, row, parm, null);
		String orderCode = parm.getValue("ORDER_CODE");
		order.setItem(row, "ORDERSET_CODE", orderCode);
		order.setItem(row, "SETMAIN_FLG", "Y");
		order.setItem(row, "HIDE_FLG", "N");
		order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
		order.setItem(row, "OWN_PRICE", 0.0);
		order.setItem(row, "DR_NOTE", parm.getValue("DESCRIPTION"));
		String labNo = order.getLabNo(row, odo);
		if (StringUtil.isNullString(labNo)) {
			this.messageBox("E0049"); // 取得检验号失败
			order.deleteRow(row);
			order.newOrder(EXA, rxNo);
			table.setDSValue();
			return;
		}
		order.setItem(row, "MED_APPLY_NO", labNo);
		order.setActive(row, true);
		TParm parmDetail = SYSOrderSetDetailTool.getInstance()
				.selectByOrderSetCode(parm.getValue("ORDER_CODE"));
		// System.out.println("parmDetail==="+parmDetail);
		if (parmDetail.getErrCode() != 0) {
			System.out.println(parmDetail.getErrText());
		}
		if (parmDetail.getErrCode() != 0) {
			this.messageBox("E0050"); // 取得细相数据错误
			return;
		}
		rxType = "5";
		int count = parmDetail.getCount();
		for (int i = 0; i < count; i++) {
			row = order.newOrder(rxType, rxNo);
			initOrder(order, row, parmDetail.getRow(i), null);
			order.setItem(row, "EXEC_DEPT_CODE", execDept);
			// zhangyong20110616
			order.setItem(row, "COST_CENTER_CODE", getCostCenter(execDept));
			order.setItem(row, "HIDE_FLG", parmDetail.getValue("HIDE_FLG", i));
			order.setItem(row, "MED_APPLY_NO", labNo);
			order.setItem(row, "ORDERSET_CODE", orderCode);
			order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
			double qty = TypeTool.getDouble(parmDetail.getData("TOTQTY", i));
			order.setItem(row, "DOSAGE_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "MEDI_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "DISPENSE_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "TAKE_DAYS", 1);
			order.setItem(row, "MEDI_UNIT", parmDetail.getValue("UNIT_CODE",
					i));
			order.setItem(row, "DOSAGE_UNIT", parmDetail.getValue("UNIT_CODE",
					i));
			order.setItem(row, "DISPENSE_UNIT", parmDetail.getValue(
					"UNIT_CODE", i));
			order.setItem(row, "AR_AMT", roundAmt(BIL.chargeTotCTZ(ctz[0],
					ctz[1], ctz[2], order.getItemString(row, "ORDER_CODE"),
					order.getItemDouble(row, "DOSAGE_QTY"), serviceLevel)));
			order.setActive(row, true);
		}

		if (!StringUtil.isNullString(order.getItemString(order.rowCount() - 1,
				"ORDER_CODE"))) {
			odo.getOpdOrder().newOrder(rxType, rxNo);
		}
		initSetTable(TABLE_EXA, true);
		order.itemNow = false;
		table.getTable().grabFocus();
		table.setSelectedRow(oldRow);
		table.setSelectedColumn(table.getColumnIndex("EXEC_DEPT_CODE"));
		Map insColor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);
		table.setRowTextColorMap(insColor);
	}
	public boolean oPdSaveCountCheck() {
		//		}
		return false;
	}

	/**
	 * 新增西、成药
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popOrderReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		// ============xueyf modify 20120331
		if (parm.getValue("CAT1_TYPE") != null
				&& !parm.getValue("CAT1_TYPE").equals("PHA")) {
			this.messageBox("不允许开立非西成药医嘱。");
			return;
		}
		// add by lx 2012/05/07 门急诊适用校验
		// 门诊
		if ("O".equalsIgnoreCase(admType)) {
			// 判断是否住院适用医嘱
			if (!("Y".equals(parm.getValue("OPD_FIT_FLG")))) {
				// 不是门诊适用医嘱！
				this.messageBox("不是门诊适用医嘱。");
				return;
			}
		}
		// 急诊
		if ("E".equalsIgnoreCase(admType)) {
			if (!("Y".equals(parm.getValue("EMG_FIT_FLG")))) {
				// 不是门诊适用医嘱！
				this.messageBox("不是急诊适用医嘱。");
				return;
			}
		}
		// $$===========add by lx 2012/05/07 门急诊适用校验
		String rxNo;
		TTable table = (TTable) this.getComponent(tableName);
		table.acceptText();
		int column = table.getSelectedColumn();
		table.acceptText();
		OpdOrder order = odo.getOpdOrder();
		// ===========pangben 2012-6-15 start 注释 取消5个医嘱提示消息
		//		 if (oPdCountCheck()) {
		//		 return;
		//		 }
		int row = order.rowCount() - 1;
		int oldRow = row;
		String amtTag="";
		if (TABLE_MED.equalsIgnoreCase(tableName)) {
			rxType = MED;
			amtTag = "MED_AMT";
		}else if (TABLE_CTRL.equalsIgnoreCase(tableName)) {
			rxType = CTRL;
			amtTag = "CTRL_AMT";
		} else {
			rxType = OP;
			amtTag = "OP_AMT";
		}
		if (StringUtil.isNullString(tableName)) {
			this.messageBox("E0034"); // 取得数据错误
			return;
		}
		// 判断是否已经超过看诊时限
		if (!canEdit()) {
			table.setDSValue(row);
			this.messageBox_("已超过看诊时间不可修改");
			return;
		}
		if (!StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
			this.messageBox("E0040"); // 不能替代该数据，请重新新增或删除该数据

			table.setDSValue(row);
			return;
		}
		if (!OdoUtil.isHavingLiciense(parm.getValue("ORDER_CODE"), Operator
				.getID())) {
			this.messageBox("E0051"); // 没有证照权限
			table.setValueAt("", row, column);
			return;
		}

		if (order.isSameOrder(parm.getValue("ORDER_CODE"))) {
			if (this.messageBox("提示信息/Tip",
					"该医嘱已经开立，是否继续？\r\nThis order exist,Do you give it again?",
					0) == 1) {
				table.setValueAt("", oldRow, column);
				return;
			}
		}
		//modify by huangtt 20150506 start
		String message = DrugAllergyTool.getInstance().CBDDrugAllergyCheck(
				odo.getMrNo(), parm.getValue("ORDER_CODE"),
				parm.getValue("ORDER_DESC"));
		if(message.length() > 0){
			if (this.messageBox("提示信息/Tip",message+"，是否继续开立？\r\nThe Pat is allergic to this order.Do you proceed anyway?",0) == 1) {
				table.setDSValue(row);
				return;
			}
		}


		//		if (!isAllergy(parm.getValue("ORDER_CODE"))) {
		//			if (this.messageBox(
		//							"提示信息/Tip",
		//							"病人对该药品或成分过敏，是否继续开立？\r\nThe Pat is allergic to this order.Do you proceed anyway?",
		//							0) == 1) {
		//				table.setDSValue(row);
		//				return;
		//			}
		//		}
		//modify by huangtt 20150506 end

		//添加医保限制校验
		//yanjing 添加医保自费校验 20130719
		TParm insCheckParm=INSTJTool.getInstance().orderCheck(parm.getValue("ORDER_CODE"), reg.getCtz1Code(),admType,reg.getInsPatType());
		if (insCheckParm.getErrCode() < 0&&!(insCheckParm.getErrCode()==-6) &&!(insCheckParm.getErrCode()==-7)) {
			if(this.messageBox("提示","此药品" + insCheckParm.getErrText()+",是否继续",0)== 1){
				table.setValueAt("", row, column);
				return;
			}
		}else if(insCheckParm.getErrCode()==-6||insCheckParm.getErrCode()==-7){//-6:医保自费标记 -7：医保自费增付标记
			this.messageBox("此药品为"+insCheckParm.getErrText());		
		}
		if (!OP.equalsIgnoreCase(rxType) 
				&& !order.isDrug(parm.getValue("ORDER_CODE"), rxType)) {
			this.messageBox("E0113");
			return;
		}
		if (CTRL.equalsIgnoreCase(order.getItemString(row, "RX_TYPE"))) {
			if (!order.isSameDrug(parm.getValue("ORDER_CODE"), row)) {
				this.messageBox("E0114");
				return;
			}
		}
		// 判断要是是否能开到该处方签上（是否是专用处方药品）
		int re = order.isPrnRx(order.getItemString(row, "RX_NO"), parm
				.getValue("ORDER_CODE"));
		if (re == 1) {
			this.messageBox("E0190");
			return;
		} else if (re == 2) {
			this.messageBox("E0191");
			return;
		} else if (re == 3) {
			this.messageBox("E0192");
			return;
		}
		rxNo = (String) this.getValue(rxName);
		TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE"));
		if (parmBase.getErrCode() < 0) {
			this.messageBox("E0034");
			return;
		}
		if (!checkDOSE_TYPE(order, parmBase.getValue("DOSE_TYPE", 0))) {
			return;
		}
		String execDept = parm.getValue("EXEC_DEPT_CODE");
		int tabbedIndex = ((TTabbedPane) this.getComponent("TTABPANELORDER"))
				.getSelectedIndex();
		switch (tabbedIndex) {
		case 1:
			if (StringUtil.isNullString(execDept)) {
				execDept = this.getValueString("OP_EXEC_DEPT");
			}
			break;
		case 2:
			execDept = this.getValueString("MED_RBORDER_DEPT_CODE");
			break;
		case 3:
			execDept = this.getValueString("CHN_EXEC_DEPT_CODE");
			break;
		case 4:
			execDept = this.getValueString("CTRL_RBORDER_DEPT_CODE");
			break;
		}

		if ("PHA".equalsIgnoreCase(parm.getValue("CAT1_TYPE")) ){
			if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 校验物联网注记

				//add by huangtt 20150602 start
				if (checkPhaAll(order, row, "MED",true))//开立医嘱校验处方签是否可以开立
					return ;
				//add by huangtt 20150602 end

				if (isCheckKC(parm.getValue("ORDER_CODE"))) // 判断是否是“药品备注”
					// 物联网
					if (!INDTool.getInstance().inspectIndStock(execDept,
							parm.getValue("ORDER_CODE"), 0.0)) {
						// $$==========add by lx 2012-06-19加入存库不足，替代药提示
						TParm inParm = new TParm();
						inParm
						.setData("orderCode", parm
								.getValue("ORDER_CODE"));
						this.openDialog("%ROOT%\\config\\pha\\PHAREDrugMsg.x",
								inParm);
						// $$==========add by lx 2012-06-19加入存库不足，替代药提示
						order.setActive(row, false);
						return;
					}
			}else{
				if (!checkSpcPha(order)) {
					return;
				}
			}
			//this.checkDrugCanUpdate(order, "MED", oldRow);
		}
		order.setActive(row, true);
		parm.setData("EXEC_DEPT_CODE", execDept);
		// 根据给入条件初始化一行order
		//		System.out.println("parmBase is :"+parmBase);
		initOrder(order, row, parm, parmBase);
		if ("TRT".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				|| "PLN".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))) {
			order.setItem(row, "FREQ_CODE", "STAT");
		}
		if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
			int newRow = order.newOrder(rxType, rxNo);
			if (order.getItemInt(row, "LINK_NO") > 0) {
				// //连结医嘱 查看是否是 “医嘱备注” 如果是 则不计算总量
				order.itemNow = true;
				order.setItem(newRow, "LINK_NO", order.getItemInt(row,
						"LINK_NO"));
				TParm linkMainParm = order.getLinkMainParm(order.getItemInt(
						row, "LINK_NO"));
				order.itemNow = false;
				order.setItem(row, "TAKE_DAYS", linkMainParm
						.getData("TAKE_DAYS"));
				order.itemNow = false;
				order.setItem(row, "FREQ_CODE", linkMainParm
						.getData("FREQ_CODE"));
				order.setItem(row, "EXEC_DEPT_CODE", linkMainParm
						.getData("EXEC_DEPT_CODE"));
				order.setItem(row, "ROUTE_CODE", linkMainParm
						.getData("ROUTE_CODE"));
				order.itemNow = true;
			}
		}
		//System.out.println(":order::::dfd::"+order.getRowParm(row));
		initNoSetTable(rxNo, tableName, false,false);
		this.calculateCash(tableName, amtTag);
		table.getTable().grabFocus();
		table.setSelectedRow(oldRow);
		table.setSelectedColumn(3);
		order.itemNow = false;
		Map inscolor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);
		Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odo.getOpdOrder());
		table.setRowTextColorMap(ctrlcolor);
	}

	/**
	 * 院前用药 add by huangtt 20151209
	 * @param tag
	 * @param obj
	 */
	public void popPreOrderReturn(String tag, Object obj){
		TParm parm = (TParm) obj;
		// 判断是否已经超过看诊时限
		if (!canEdit()) {
			this.messageBox_("已超过看诊时间不可修改");
			return;
		}

		if (!"TABLEPREMED".equals(this.tableName)) {
			this.tableName = "TABLEPREMED";
		}

		TTable table = (TTable) this.getComponent(tableName);
		String rxNo;
		if (StringUtil.isNullString(tableName)) {
			this.messageBox("E0034");
			return;
		}
		int row = table.getSelectedRow();
		// int row = order.rowCount() / 4 - 1;
		int oldRow = row;
		int column = table.getSelectedColumn();
		String code = (String) table.getValueAt(row, column);
		OpdOrder order = odo.getOpdOrder();

		table.acceptText();

		rxNo = (String) this.getValue(rxName);
		if (!StringUtil
				.isNullString(order.getItemString(row, "ORDER_CODE"))) {
			this.messageBox("E0040");
			return;
		}
		TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE"));
		if (parmBase.getErrCode() < 0) {
			this.messageBox("E0034");
			return;
		}
		order.setActive(row, true);

		// 根据给入条件初始化一行order
		//		System.out.println("parmBase is :"+parmBase);
		parm.setData("CAT1_TYPE", "");
		parm.setData("EXEC_DEPT_CODE", phaCode);

		initOrder(order, row, parm, parmBase);
		order.itemNow = true;
		order.setItem(row, "CAT1_TYPE", "PHA");
		order.setItem(row, "TAKE_DAYS", 0); 
		if(row != 0){
			TParm tempParm = table.getDataStore().getRowParm(row - 1);
			order.setItem(row, "ORDER_DATE", tempParm.getData("ORDER_DATE")); 
			order.setItem(row, "DC_ORDER_DATE", tempParm.getData("DC_ORDER_DATE")); 
			order.setItem(row, "MED_REPRESENTOR", tempParm.getData("MED_REPRESENTOR")); 
		}
		order.itemNow = false;
		//		System.out.println(order.getRowParm(row));
		//		System.out.println("rxNO==="+rxNo);

		rxType=PREMED;
		order.newOrder(rxType, rxNo);
		initNoSetTable(rxNo, tableName, false,false);
		table.getTable().grabFocus();
		table.setSelectedRow(oldRow);
		table.setSelectedColumn(1);
		//	    
	}

	/**
	 * 新增中医
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popChnOrderReturn(String tag, Object obj) {		
		TParm parm = (TParm) obj;
		// 判断是否已经超过看诊时限
		if (!canEdit()) {
			this.messageBox_("已超过看诊时间不可修改");
			return;
		}

		if (!"TABLECHN".equals(this.tableName)) {
			this.tableName = "TABLECHN";
		}

		if ("TABLECHN".equals(this.tableName)) {
			if ("O".equalsIgnoreCase(this.admType))
			{
				if (!"Y".equals(parm.getValue("OPD_FIT_FLG")))
				{
					messageBox("不是门诊适用医嘱。");
					return;
				}
			}

			if (("E".equalsIgnoreCase(this.admType)) && 
					(!"Y".equals(parm.getValue("EMG_FIT_FLG"))))
			{
				messageBox("不是急诊适用医嘱。");
				return;
			}

		}


		TTable table = (TTable) this.getComponent(tableName);


		String rxNo;
		if (StringUtil.isNullString(tableName)) {
			this.messageBox("E0034");
			return;
		}
		if (StringUtil.isNullString(this.getValueString("CHN_EXEC_DEPT_CODE"))) {
			this.messageBox("E0053");
			TTextFormat t = (TTextFormat) this
					.getComponent("CHN_EXEC_DEPT_CODE");
			t.grabFocus();
			return;
		}
		if (StringUtil.isNullString(this.getValueString("CHN_FREQ_CODE"))) {
			this.messageBox("E0054");
			return;
		}
		//			//物联网
		if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 校验物联网注记

			if (isCheckKC(parm.getValue("ORDER_CODE"))) // 判断是否是“药品备注”
				if (!INDTool.getInstance().inspectIndStock(
						this.getValueString("CHN_EXEC_DEPT_CODE"),
						parm.getValue("ORDER_CODE"), 0.0)) {
					// this.messageBox("E0052");
					// $$==========add by lx 2012-06-19加入存库不足，替代药提示
					TParm inParm = new TParm();
					inParm.setData("orderCode", parm.getValue("ORDER_CODE"));
					this.openDialog("%ROOT%\\config\\pha\\PHAREDrugMsg.x",
							inParm);
					// $$==========add by lx 2012-06-19加入存库不足，替代药提示
					return;
				}
		}
		OpdOrder order = odo.getOpdOrder();
		// ============pangben 2012-2-29 添加管控
		int count = order.rowCount();
		if (count <= 0) {
			return;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!deleteOrder(order, i, "已经打票的处方签不可以添加医嘱","此医嘱已经登记,不能删除")) {
				return;

			}
			//add by huangtt 20150911 start
			if (checkPhaAll(order, i, "CHN",true))//开立医嘱校验处方签是否可以开立
				return ;
			//add by huangtt 20150911 end
		}
		// ============pangben 2012-2-29 stop
		int row = table.getSelectedRow();
		// int row = order.rowCount() / 4 - 1;
		int oldRow = row;
		int column = table.getSelectedColumn();
		String code = (String) table.getValueAt(row, column);

		if (order.isSameOrder(parm.getValue("ORDER_CODE"))) {
			if (this.messageBox("提示信息/Tip",
					"该医嘱已经开立，是否继续？\r\n/This order exist,Do you give it again?",
					0) == 1) {
				table.setValueAt(code, oldRow, column);
				return;
			}

		}
		table.acceptText();
		int realColumn = 0;
		switch (column) {
		case 0:
			realColumn = 0;
			break;
		case 3:
			realColumn = 1;
			break;
		case 6:
			realColumn = 2;
			break;
		case 9:
			realColumn = 3;
			break;
		}
		int realrow = row * 4 + realColumn;
		rxNo = (String) this.getValue(rxName);
		// System.out.println("-111111---"+order.getItemString(realrow,
		// "ORDER_CODE"));
		if (!StringUtil
				.isNullString(order.getItemString(realrow, "ORDER_CODE"))) {
			this.messageBox("E0040");
			return;
		}
		order.setItem(realrow, "PHA_TYPE", "G");
		TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE"));
		if (parmBase.getErrCode() < 0) {
			this.messageBox("E0034");
			return;
		}
		// System.out.println("realrow----"+realrow);
		order.setActive(realrow, true);
		order.setItem(realrow, "EXEC_DEPT_CODE", this
				.getValue("CHN_EXEC_DEPT_CODE"));
		parm.setData("EXEC_DEPT_CODE", this.getValue("CHN_EXEC_DEPT_CODE"));
		initOrder(order, realrow, parm, parmBase);
		setChnPckTot();
		order.setItem(realrow, "DCT_TAKE_QTY", this
				.getValue("DCT_TAKE_QTY"));
		order.setItem(realrow, "TAKE_DAYS", this
				.getValue("DCT_TAKE_DAYS"));
		order.setItem(realrow, "FREQ_CODE", this.getValue("CHN_FREQ_CODE"));
		order.setItem(realrow, "ROUTE_CODE", this.getValue("CHN_ROUTE_CODE"));
		order.setItem(realrow, "DCTAGENT_CODE", this.getValue("DCTAGENT_CODE"));

		order.setItem(realrow, "DR_NOTE", this.getValue("DR_NOTE"));
		calculateChnCash(rxNo);
		table.setValueAt(parm.getValue("ORDER_DESC"), table.getSelectedRow(),
				table.getSelectedColumn());
		table.setValueAt(order.getItemDouble(realrow, "MEDI_QTY"), row,
				column + 1);

		table.getTable().grabFocus();
		if (column == 9) {
			String oldFilter = order.getFilter();
			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			String laseOrderCode = order.getItemString(order.rowCount() - 1,
					"ORDER_CODE");
			order.setFilter(oldFilter);
			order.filter();
			if (order.getItemString(realrow, "ORDER_CODE").length() > 0
					&& laseOrderCode.length() > 0)
				addChnRow(rxNo, row);
		}
		initChnTable(rxNo);
		table.setSelectedRow(row);
		table.setSelectedColumn(column + 1);
	}

	/**
	 * 为中药TABLE新增一行
	 * 
	 * @param rxNo
	 *            处方号
	 * @param row
	 *            行号
	 */
	public void addChnRow(String rxNo, int row) {
		if (StringUtil.isNullString(rxNo))
			return;
		OpdOrder order = odo.getOpdOrder();
		int realrow = row * 4;
		for (int i = realrow; i < realrow + 4; i++) {
			order.newOrder(CHN, rxNo);
			order.setItem(i, "PHA_TYPE", "G");
		}
		// initChnTable(rxNo);
	}

	/**
	 * 新增处置
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popOpReturn(String tag, Object obj) {
		TParm sysFee = (TParm) obj;
		String rxNo;
		if (StringUtil.isNullString(tableName)) {
			this.messageBox("E0034");
			return;
		}
		TTable table = (TTable) this.getComponent(tableName);

		int row = table.getSelectedRow();
		int oldRow = row;
		// 判断是否已经超过看诊时限
		if (!canEdit()) {
			table.setDSValue(row);
			this.messageBox_("已超过看诊时间不可修改");
			return;
		}
		if (odo.getOpdOrder().getItemString(oldRow, "ORDER_CODE") != null
				&& odo.getOpdOrder().getItemString(oldRow, "ORDER_CODE").trim()
				.length() > 0) {
			this.messageBox("E0045"); // 已开立医嘱不可变更，请删除此医嘱或新开立
			table.setDSValue(row);
			return;
		}
		int column = table.getSelectedColumn();
		String code = sysFee.getValue("ORDER_CODE");
		table.acceptText();
		rxNo = (String) this.getValue(rxName);
		rxType = "4";
		OpdOrder order = odo.getOpdOrder();
		// ============pangben 2012-2-29 添加管控
		int count = order.rowCount();
		if (count <= 0) {
			return;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!deleteOrder(order, i, "已经打票的处方签不可以添加医嘱","此医嘱已经登记,不能删除")) {
				return;

			}
		}
		// ============pangben 2012-2-29 stop
		if (order.isSameOrder(code)) {
			if (this.messageBox("提示信息/Tip",
					"该医嘱已经开立，是否继续？\r\n/This order exist,Do you give it again?",
					0) == 1) {
				table.setValueAt("", oldRow, column);
				return;
			}

		}
		odo.getOpdOrder().newOpOrder(rxNo, code, ctz, row);
		// odo.getOpdOrder().showDebug();
		if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
			odo.getOpdOrder().newOrder(rxType, rxNo);
		}
		this.calculateCash(TABLE_OP, "OP_AMT");
		initNoSetTable(rxNo, tableName, false,false);
		table.getTable().grabFocus();
		table.setSelectedRow(row);
		table.setSelectedColumn(3);
		order.itemNow = false;
		Map insColor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);
		table.setRowTextColorMap(insColor);
		// table.setLockRows(lockRows(tableName,false));
	}

	/**
	 * 根据给入条件初始化一行order
	 * 
	 * @param order
	 *            OpdOrder
	 * @param row
	 *            int
	 * @param parm
	 *            TParm sysFeeParm
	 * @param parmBase
	 *            TParm phaBaseParm
	 */
	//FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE
	public void initOrder(OpdOrder order, int row, TParm parm, TParm parmBase) {
		order.itemNow = true;
		order.setItem(row, "FLG", "N");//=======pangben 2013-3-22 默认选中
		order.setItem(row, "PRESRT_NO", row + 1);
		order.setItem(row, "REGION_CODE", Operator.getRegion());
		order.setItem(row, "RELEASE_FLG", "N");
		order.setItem(row, "LINKMAIN_FLG", "N");
		order.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
		order.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
		order.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC")
				.replaceFirst("(" + parm.getValue("SPECIFICATION") + ")", ""));
		order.setItem(row, "TRADE_ENG_DESC", parm.getValue("TRADE_ENG_DESC"));
		order.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
		order.setItem(row, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
		order.setItem(row, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
		//double xxQty = 1.0; // 记录检验检查细项的总量 默认为1
		// TOTQTY是检验检查细项的总量 如果细项有总量 那么需要用这个总量来计算价钱
		//		if (parm.getDouble("TOTQTY") > 0) {
		//			xxQty = parm.getDouble("TOTQTY");
		//		}
		if ("2".equals(serviceLevel)) {
			order.setItem(row, "OWN_PRICE", parm.getDouble("OWN_PRICE2"));
		} else if ("3".equals(serviceLevel)) {
			order.setItem(row, "OWN_PRICE", parm.getDouble("OWN_PRICE3"));
		} else
			order.setItem(row, "OWN_PRICE", parm.getDouble("OWN_PRICE"));
		// order.setItem(row, "OWN_PRICE", parm.getData("OWN_PRICE"));
		order.setItem(row, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
		String REXP_CODE = BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE"),
				admType);
		// ============xueyf modify 20120419 start
		// 追踪REXP_CODE为空的数据信息
		if (StringUtil.isNullString(REXP_CODE)) {
			this.messageBox("该医嘱REXP_CODE为空，请通知信息中心。");
			System.err.println("该医嘱REXP_CODE为空，请通知信息中心。");
			System.err.println("CHARGE_HOSP_CODE="
					+ parm.getValue("CHARGE_HOSP_CODE"));
			System.err.println("admType=" + admType);
			TParm logParm = new TParm();
			logParm.setErr(-1, "CASE_NO="+caseNo+" ORDER_CODE="+parm.getValue("ORDER_CODE")+" HEXP_CODE="+parm.getValue("CHARGE_HOSP_CODE"));
			TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
					"noRexpCodeLog", logParm);
		}
		order.setItem(row, "REXP_CODE", REXP_CODE);
		// ============xueyf modify 20120419 stop
		order.setItem(row, "SETMAIN_FLG", "N");
		order.setItem(row, "ORDERSET_GROUP_NO", 0);
		order.setItem(row, "CTZ1_CODE", ctz[0]);
		order.setItem(row, "CTZ2_CODE", ctz[1]);
		order.setItem(row, "CTZ3_CODE", ctz[2]);
		order.setItem(row, "MR_CODE", parm.getValue("MR_CODE"));
		order.itemNow = false; // 是否调用计算总量方法的开关
		if (TypeTool.getDouble(parm.getData("MEDI_QTY")) > 0) {
			order.setItem(row, "MEDI_QTY", TypeTool.getDouble(parm
					.getData("MEDI_QTY")));
		} else {
			order.setItem(row, "MEDI_QTY", 1.0);
		}
		if (parm.getDouble("TOTQTY") > 0) {
			// 集合医嘱子项的用量 也要带入默认值（子项数量有可能大于1，所以当数量大于一时 拿总量作为用量）
			order.setItem(row, "MEDI_QTY", TypeTool.getDouble(parm
					.getData("TOTQTY")));
			order.itemNow = true; // 必须将setItem事件的开关关掉，否增会调用总量回推用量方法 造成用量为0
			order.setItem(row, "DISPENSE_QTY", TypeTool.getDouble(parm
					.getDouble("TOTQTY")));
			order.itemNow = false; // 总量付初始值后 打开开关 用以计算总量
			order.setItem(row, "DOSAGE_QTY", TypeTool.getDouble(parm
					.getDouble("TOTQTY")));
		} else {
			order.itemNow = true; // 必须将setItem事件的开关关掉，否增会调用总量回推用量方法 造成用量为0
			order.setItem(row, "DISPENSE_QTY", 1.0);
			order.itemNow = false; // 总量付初始值后 打开开关 用以计算总量
			order.setItem(row, "DOSAGE_QTY", 1.0);
		}
		order.setItem(row, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
		order.setItem(row, "DISPENSE_UNIT", parm.getValue("UNIT_CODE"));
		order.setItem(row, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
		order.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
		// zhangyong20110616
		order.setItem(row, "COST_CENTER_CODE", getCostCenter(parm
				.getValue("EXEC_DEPT_CODE")));
		// 判断是否是药品
		if ("PHA".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))) {
			String printNo = "";
			if (row <= 0) {
				printNo = order.getPrintNo(parm.getValue("EXEC_DEPT_CODE"));
			} else {
				printNo = order.getItemString(row - 1, "PRINT_NO");
			}
			if (StringUtil.isNullString(printNo)) {
				this.messageBox("E0112"); // 取得领药号失败
				String rxNo = order.getItemString(row, "RX_NO");
				order.deleteRow(row);
				order.newOrder(rxType, rxNo);
				return;
			}
			order.setItem(row, "PRINT_NO", printNo);
			int counterNo = order.getItemInt(row - 1, "COUNTER_NO");
			if (counterNo < 1) {
				counterNo = order.getCounterNo(parm.getValue("EXEC_DEPT_CODE"),
						printNo, rxType);

			}
			if (counterNo < 1) {
				this.messageBox("E0112"); // 取得领药号失败
				String rxNo = order.getItemString(row, "RX_NO");
				order.deleteRow(row);
				order.newOrder(rxType, rxNo);
				return;
			}
			order.setItem(row, "COUNTER_NO", counterNo);

		}
		order.setItem(row, "RPTTYPE_CODE", parm.getValue("RPTTYPE_CODE"));
		order.setItem(row, "OPTITEM_CODE", parm.getValue("OPTITEM_CODE"));
		order.setItem(row, "DEV_CODE", parm.getValue("DEV_CODE"));
		order.setItem(row, "MR_CODE", parm.getValue("MR_CODE"));
		order.setItem(row, "DEGREE_CODE", parm.getValue("DEGREE_CODE"));

		if (parmBase != null && parmBase.getCount("FREQ_CODE") > 0) {
			// double takeQty = (parmBase.getDouble("MEDI_UNIT",
			// 0)<1.0)?1.0:parmBase.getDouble("MEDI_UNIT", 0);
			order.setItem(row, "MEDI_QTY",null!=parmBase.getValue("MEDI_QTY", 0)?parmBase.getValue("MEDI_QTY", 0):"");
			// System.out.println("开药单位："+parmBase.getValue("MEDI_UNIT", 0));
			order.setItem(row, "MEDI_UNIT", null!=parmBase.getValue("MEDI_UNIT", 0)?parmBase.getValue("MEDI_UNIT", 0):"");
			order.setItem(row, "FREQ_CODE", parmBase.getValue("FREQ_CODE", 0));
			order.setItem(row, "ROUTE_CODE",  null!=parmBase.getValue("ROUTE_CODE",0)?parmBase.getValue("ROUTE_CODE",0):"");
			int takedays = TypeTool.getInt(parm.getData("TAKE_DAYS")) > 0 ? TypeTool
					.getInt(parm.getData("TAKE_DAYS"))
					: TypeTool.getInt(parmBase.getData("TAKE_DAYS", 0));
					if (takedays < 0) {
						takedays = 1;
					}
					order.setItem(row, "TAKE_DAYS", takedays);
					order.setItem(row, "CTRLDRUGCLASS_CODE", parmBase.getValue(
							"CTRLDRUGCLASS_CODE", 0));
					order.setItem(row, "GIVEBOX_FLG", parmBase.getValue("GIVEBOX_FLG",
							0));
					order.setItem(row, "DOSE_TYPE", parmBase.getValue("DOSE_TYPE", 0));
					// 判断是否是按总量给药
					if ("Y".equalsIgnoreCase(parmBase.getValue("DSPNSTOTDOSE_FLG", 0))) {
						order.setItem(row, "DISPENSE_QTY", parmBase.getValue(
								"DEFAULT_TOTQTY", 0));
					} else {
						// $$============add by lx 2012/03/03
						// 加入默认总量start=================$$//
						double tMediQty = parmBase.getDouble("MEDI_QTY", 0);// 开药数量
						String tUnitCode = parmBase.getValue("MEDI_UNIT", 0);// 开药单位
						String tFreqCode = parmBase.getValue("FREQ_CODE", 0);// 频次
						int tTakeDays = parmBase.getInt("TAKE_DAYS", 0);// 天数
						TotQtyTool qty = TotQtyTool.getInstance();
						parm.setData("TAKE_DAYS", tTakeDays);
						parm.setData("MEDI_QTY", tMediQty);
						parm.setData("FREQ_CODE", tFreqCode);
						parm.setData("MEDI_UNIT", tUnitCode);
						parm.setData("ORDER_DATE", SystemTool.getInstance().getDate());
						TParm qtyParm = qty.getTotQty(parm);
						order.setItem(row, "DISPENSE_QTY", qtyParm.getDouble("QTY"));
						order.setItem(row, "DOSAGE_QTY", qtyParm.getDouble("QTY"));
						// $$============add by lx 2012/03/03 加入默认总量
						// end=================$$//
					}
					// 判断是否按盒发药
					if ("Y".equalsIgnoreCase(parmBase.getValue("GIVEBOX_FLG", 0))) {
						order.setItem(row, "DOSAGE_UNIT", parmBase.getValue(
								"STOCK_UNIT", 0));
						order.setItem(row, "DISPENSE_UNIT", parmBase.getValue(
								"STOCK_UNIT", 0));
						order.itemNow=false;//pangben 2013-6-3  盒装药计算总计金额 总量设置 itemNow=false计算 true 不计算 
						order.setItem(row, "GIVEBOX_FLG", parmBase.getValue("GIVEBOX_FLG",
								0));
						order.itemNow=true;
					} else {
						order.setItem(row, "DOSAGE_UNIT", parmBase.getValue(
								"DOSAGE_UNIT", 0));
						order.setItem(row, "DISPENSE_UNIT", parmBase.getValue(
								"DOSAGE_UNIT", 0));
					}
		}
		double ownAmt = roundAmt(order.getItemDouble(row, "OWN_PRICE")
				* order.getItemDouble(row, "DOSAGE_QTY"));
		String orderCode = parm.getValue("ORDER_CODE");
		// ===============begin===//
		// lzk 2010.6.23 合并 BIL.chargeTotCTZ和BIL.getOwnRate
		double d[] = BILStrike.getInstance().chargeC(ctz[0], ctz[1], ctz[2],
				orderCode, parm.getValue("CHARGE_HOSP_CODE"), serviceLevel);
		double arAmt = roundAmt(d[0] * order.getItemDouble(row, "DOSAGE_QTY"));
		order.setItem(row, "DISCOUNT_RATE", d[1]);
		// ===============   chenxi modify  添加开立时间
		if (parmBase != null && parmBase.getCount("FREQ_CODE") > 0){
			order.setItem(row, "ORDER_DATE", SystemTool.getInstance().getDate()) ;
		}
		else  order.setItem(row, "ORDER_DATE", "") ;

		//==============  chenxi modify 添加开立时间
		order.setItem(row, "OWN_AMT", ownAmt);
		order.setItem(row, "AR_AMT", arAmt);
		order.setItem(row, "PAYAMOUNT", ownAmt - arAmt);
		order.itemNow = false;
	}
	/**
	 * 删除一行数据操作修改表格当前状态
	 * ===============pangben 2013-4-24
	 * @param table
	 */
	private void deleteExeTemp(TTable table, int type) {
		switch (type) {
		case 1:
			table.setDSValue();
			table.acceptText();
			table.getTable().grabFocus();
			table.setSelectedRow(0);
			break;
		case 2:
			table.acceptText();
			table.getTable().grabFocus();
			table.setSelectedRow(0);
			table.setSelectedColumn(1);
			break;
		}
	}
	/**
	 * 删除一行数据执行操作
	 * ===============pangben 2013-4-24
	 * @return
	 */
	private boolean deleteExe(TTable table){
		int row = table.getSelectedRow();
		if (TABLEDIAGNOSIS.equalsIgnoreCase(tableName)) {
			if (row < 0) {
				return true;
			}
			Diagrec dRec = odo.getDiagrec();
			if (dRec.rowCount() - 1 <= row || row == -1) {
				deleteExeTemp(table, 2);
				return true;
			}
			dRec.deleteRow(row);
			deleteExeTemp(table,1);
			table.setSelectedColumn(1);
			return true;
		}else if (TABLEALLERGY.equalsIgnoreCase(tableName)) {
			if (row < 0) {
				return true;
			}
			DrugAllergy allergy = odo.getDrugAllergy();
			if (allergy.rowCount() - 1 <= row || row == -1) {
				deleteExeTemp(table, 2);
				return true;
			}
			allergy.deleteRow(row);
			deleteExeTemp(table,1);
			table.setSelectedColumn(1);
			return true;
		}else if (TABLEMEDHISTORY.equalsIgnoreCase(tableName)) {//既往史TABLE删除
			if (row < 0) {
				return true;
			}
			MedHistory md = odo.getMedHistory();
			if (md.rowCount() - 1 <= row || row == -1) {
				deleteExeTemp(table, 2);
				return true;
			}
			md.deleteRow(row);
			deleteExeTemp(table,1);
			table.setSelectedColumn(2);
			return true;
		}
		return false;
	}
	/**
	 * 删除一行检验检查
	 * ==========pangben 2013-4-24
	 */
	private boolean deleteRowExa(OpdOrder order ,int row,TTable table){
		if (!checkSendPah(order, row))// 检查已发药已到检
			return false;
		String rxNo = this.getValueString(EXA_RX);
		if (!deleteOrder(order, row, "已打票,不可以修改或删除医嘱","此医嘱已经登记,不能删除")) {
			return false;
		} 
		String orderSetCode = order.getItemString(row, "ORDERSET_CODE");//集合医嘱代码
		int groupNo = order.getItemInt(row, "ORDERSET_GROUP_NO");//集合医嘱组号
		String orderCode = order.getItemString(row, "ORDER_CODE");//代码
		order.deleteOrderSet(rxNo, orderSetCode, groupNo, orderCode, order
				.getItemString(row, "SEQ_NO"));
		table.setDSValue();
		Map insColor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);
		table.setRowTextColorMap(insColor);
		//exaFLg = true;// 删除显示检验检查单
		this.calculateCash(tableName, "EXA_AMT");
		return true;
	}
	/**
	 * 删除一行中药医嘱
	 * ==========pangben 2013-4-24
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 */
	private boolean deleteRowChn(OpdOrder order ,int row,TTable table){
		rxType = CHN;
		int column = table.getSelectedColumn();
		int realColumn = 0;
		if (column >= 0 && column <= 2) {
			realColumn = 0;
		} else if (column >= 3 && column <= 5) {
			realColumn = 1;
		} else if (column >= 6 && column <= 8) {
			realColumn = 2;
		} else {
			realColumn = 3;
		}
		String rxNo = this.getValueString(CHN_RX);

		int realRow = row * 4 + realColumn;
		if (!deleteOrder(order, row, "已打票,不可以修改或删除医嘱","此医嘱已经登记,不能删除")) {
			return false;
		} 

		TParm parm = new TParm();
		if (!order.checkDrugCanUpdate("CHN", realRow, parm, true)) // 判断是否可以修改（有没有进行审,配,发）
		{
			messageBox(parm.getValue("MESSAGE"));
			return false;
		}

		if (realRow < order.rowCount()) {
			order.deleteRow(realRow);
		} else {
			return false;
		}
		this.initChnTable(rxNo);	
		return true;
	}
	/**
	 * 删除一行处置医嘱
	 * ==========pangben 2013-4-24
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 */
	private boolean deleteRowOp(OpdOrder order ,int row,TTable table){
		if (!checkSendPah(order, row))// 检查已发药已到检
			return false;
		if (!deleteOrder(order, row, "已打票,不可以修改或删除医嘱","此医嘱已经登记,不能删除")) {
			return false;
		}
		String rxNo = this.getValueString("OP_RX");
		String orderSetCode = table.getItemString(row, "ORDERSET_CODE");
		int groupNo = table.getItemInt(row, "ORDERSET_GROUP_NO");
		String orderCode = table.getItemString(row, "ORDER_CODE");
		order.deleteOrderSet(rxNo, orderSetCode, groupNo, orderCode, table
				.getItemString(row, "SEQ_NO"));
		table.setDSValue();
		Map insColor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);
		table.setRowTextColorMap(insColor);
		this.calculateCash(tableName, "OP_AMT");
		return true;
	}
	/**
	 * 删除一行西成药医嘱
	 * ==========pangben 2013-4-24
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 */
	private boolean deleteRowMed(OpdOrder order ,int row,TTable table){
		if (!checkSendPah(order, row))// 检查已发药已到检
			return false;
		//		if (!order.checkDrugCanUpdate("EXA", row)) { // 判断是否可以修改（有没有进行审,配,发）
		//			this.messageBox("E0189");
		//			return false;
		//		}

		//add by huangtt 20150716 判断该处方是否为退药，如是为退药不允许删除一个药，要删除整个处方
		if(checkReturnPha(order,row)){
			return false;
		}

		table.acceptText();//====pangben 2013-1-6 删除西药鼠标焦点在用量上面,打印的处方签下面的医嘱用量会变成删除的医嘱用量
		if(!deleteRowMedCtrlComm(order, row, table)){
			return false;
		}
		return true;
	}
	/**
	 * 删除一行西成药或管制药品公用
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 * =====pangben 2013-4-25
	 */
	private boolean deleteRowMedCtrlComm(OpdOrder order ,int row,TTable table){
		// 是否可以删
		if (!deleteOrder(order, row, "已打票,不可以修改或删除医嘱","此医嘱已经登记,不能删除")) {
			return false;
		} 
		int linktemp = order.getItemInt(row, "LINK_NO");
		if (linktemp > 0
				&& TCM_Transform.getBoolean(order.getItemData(row,
						"LINKMAIN_FLG"))) {
			for (int i = order.rowCount(); i > -1; i--) {
				if (linktemp == order.getItemInt(i, "LINK_NO")
						&& !StringUtil.isNullString(order.getItemString(i,
								"ORDER_CODE"))) {
					order.deleteRow(i);
				}
			}
		} else {
			order.deleteRow(row);
		}

		return true;
	}
	/**
	 * 删除一行西成药或管制药品公用,for循环执行以后操作
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 * =====pangben 2013-4-25
	 */
	private boolean deleteRowMedCtrlComm(TTable table,String rxNo,String rxType,OpdOrder order){
		if (table.getRowCount() - 1 < 0) {
			order.newOrder(rxType, rxNo);
			return false;
		}
		if (!StringUtil.isNullString(TCM_Transform.getString(table.getItemData(
				table.getRowCount() - 1, "ORDER_DESC")))) {
			order.newOrder(rxType, rxNo);
		}
		table.setDSValue();
		Map inscolor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);

		Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odo.getOpdOrder());
		table.setRowTextColorMap(ctrlcolor);
		return true;
	}
	/**
	 * 删除一行管制药品
	 * @param order
	 * @param row
	 * @param table
	 * =====pangben 2013-4-24
	 */
	private boolean deleteRowCtrl(OpdOrder order ,int row,TTable table){
		if (!checkSendPah(order, row))// 检查已发药已到检
			return false;
		//		if (!order.checkDrugCanUpdate("EXA", row)) { // 判断是否可以修改（有没有进行审,配,发）
		//			this.messageBox("E0189");
		//			return false;
		//		}
		if(!deleteRowMedCtrlComm(order, row, table)){
			return false;
		}
		return true;
	}
	/**
	 * 删除指定表格的一行数据
	 */
	public void deleteRow() {
		TTable table = (TTable) this.getComponent(tableName);
		if(deleteExe(table)){//=========pangben 2013-4-24 删除主诉现病逝、既往史、家族史
			return ;
		}
		String tag ="";//状态选择用来计算金额
		OpdOrder order = odo.getOpdOrder();
		boolean deleteRowFlg=false;//校验是否删除操作
		String billFlg="";//操作暂存还是收费
		String rxNo="";
		String rxType="";
		if(TABLE_CHN.equalsIgnoreCase(tableName)){//中药
			int row = table.getSelectedRow();
			if (row < 0) {
				return;
			}
			billFlg=order.getItemData(row, "BILL_FLG").toString();
			if(!deleteRowChn(order, row, table))
				return;
			if("Y".equals(billFlg))
				onFee();// 执行删除医嘱
			else
				onTempSave();
			return;		
		}
		for (int i = order.rowCount()-1; i>=0; i--) {
			if(null==order.getItemData(i, "ORDER_CODE"))
				continue;
			String orderCode = order.getItemData(i, "ORDER_CODE").toString();//医嘱码
			if(StringUtil.isNullString(orderCode))//校验是否空行
				continue;
			if(order.getItemData(i, "FLG").equals("Y")){//勾选状态
				deleteRowFlg=true;
				if(billFlg.length()<=0||billFlg.equals("N"))//判断操作收费还是暂存
					billFlg=order.getItemData(i, "BILL_FLG").toString();
				if (TABLE_EXA.equalsIgnoreCase(tableName)) {// 检验检查表
					if(!deleteRowExa(order, i, table)){
						return;
					}
					tag = "EXA_AMT";
				}
				if (TABLE_OP.equalsIgnoreCase(tableName)) {// 诊疗项目表
					if (!deleteRowOp(order, i, table)) {
						return;
					}
					tag = "OP_AMT";
				}
				if(TABLE_PRE_MED.equalsIgnoreCase(tableName)){//院前用药 add by huangtt 20151210 
					order.deleteRow(i);
					rxNo = this.getValueString("PREMED_RX");
					rxType=PREMED;

				}
				if (TABLE_MED.equalsIgnoreCase(tableName)) {//西成药
					if (!deleteRowMed(order, i, table)) {
						return;
					}
					rxNo = this.getValueString("MED_RX");
					tag = "MED_AMT";
					rxType=MED;
				}
				if (TABLE_CTRL.equalsIgnoreCase(tableName)) {//管制药品
					if (!deleteRowCtrl(order, i, table)) {
						return;
					}
					rxNo = this.getValueString("CTRL_RX");
					tag = "CTRL_AMT";
					rxType =CTRL;
				}
			}
		}
		if (!deleteRowFlg) {//没有操作的数据
			table.acceptText();
			table.setItem(0, 0, "");
			table.getTable().grabFocus();
			table.setSelectedRow(order.rowCount() - 1);
			table.setSelectedColumn(0);
			this.messageBox("请选择要删除的医嘱");
			return;
		}
		if(rxType.equals(MED)||rxType.equals(CTRL)  //删除一行西成药或管制药品公用,for循环执行以后操作
				||rxType.equals(PREMED) ){
			if (!deleteRowMedCtrlComm(table, rxNo, rxType, order)) {
				return ;
			}
		}
		this.calculateCash(tableName, tag);
		if("Y".equals(billFlg)){
			onFee();// 执行删除医嘱
		}else{
			onTempSave();
		}
	}
	/**
	 * 校验已发药品，已到检
	 * 
	 * @param order
	 * @param row
	 */
	private boolean checkSendPah(OpdOrder order, int row) {
		//		 System.out.println("opb::::::"+opb);
		//===zhangp 物联网修改 start
		if (!"PHA".equals(order.getItemData(row, "CAT1_TYPE"))
				&& "Y".equals(order.getItemData(row, "EXEC_FLG"))) {
			this.messageBox("已到检,不能退费!");
			return false;
		}
		if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 校验物联网注记
			if ("PHA".equals(order.getItemData(row, "CAT1_TYPE"))
					&& !opb.checkDrugCanUpdate(order.getRowParm(row), row)) {
				this.messageBox("药品已审核或发药,不能退费!");
				return false;
			}
		} else {
			if ("PHA".equals(order.getItemData(row, "CAT1_TYPE"))) {
				String caseNo = order.getCaseNo();
				String rxNo = order.getRowParm(row).getValue("RX_NO");
				String seqNo = order.getRowParm(row).getValue("SEQ_NO");
				TParm spcParm = new TParm();
				spcParm.setData("CASE_NO", caseNo);
				spcParm.setData("RX_NO", rxNo);
				spcParm.setData("SEQ_NO", seqNo);
				TParm spcReturn = TIOM_AppServer.executeAction(
						"action.opb.OPBSPCAction",
						"getPhaStateReturn", spcParm);
				//				PHADosageWsImplService_Client phaDosageWsImplServiceClient = new PHADosageWsImplService_Client();
				//				SpcOpdOrderReturnDto spcOpdOrderReturnDto = phaDosageWsImplServiceClient.getPhaStateReturn(caseNo, rxNo, seqNo);
				//				if(spcOpdOrderReturnDto == null){
				//					return true;
				//				}
				if(spcReturn.getErrCode()==-2){
					return true;
				}
				boolean needExamineFlg = false;
				// 如果是西药 审核或配药后就不可以再进行修改或者删除
				if ("W".equals(order.getRowParm(row).getValue("PHA_TYPE"))
						|| "C".equals(order.getRowParm(row)
								.getValue("PHA_TYPE"))) {
					// 判断是否审核
					needExamineFlg = PhaSysParmTool.getInstance().needExamine();
				}
				// 如果有审核流程 那么判断审核医师是否为空
				if (needExamineFlg) {
					// System.out.println("有审核");
					// 如果审核人员存在 不存在退药人员 那么表示药品已审核 不能再做修改
					//					if (spcOpdOrderReturnDto.getPhaCheckCode().length() > 0
					//							&& spcOpdOrderReturnDto.getPhaRetnCode().length() == 0) {
					//						this.messageBox("药品已审核,不能退费!");
					//						return false;
					//					}
					if (spcReturn.getValue("PhaCheckCode").length() > 0
							&& spcReturn.getValue("PhaRetnCode").length() == 0) {
						this.messageBox("药品已审核,不能退费!");
						return false;
					}
				} else {// 没有审核流程 直接配药
					// 判断是否有配药药师
					// System.out.println("无审核");
					//					if (spcOpdOrderReturnDto.getPhaDosageCode().length() > 0
					//							&& spcOpdOrderReturnDto.getPhaRetnCode().length() == 0) {
					//						this.messageBox("药品已发药,不能退费!");
					//						return false;// 已经配药不可以做修改
					//					}
					if (spcReturn.getValue("PhaDosageCode").length() > 0
							&& spcReturn.getValue("PhaRetnCode").length() == 0) {
						this.messageBox("药品已发药,不能退费!");
						return false;// 已经配药不可以做修改
					}
				}
			}
			// ===zhangp 物联网修改 end
		}
		return true;
	}

	/**
	 * 校验是否可以删除医嘱 医疗卡删除医嘱可以直接删除,如果已经扣款的医嘱删除 将直接执行扣款操作
	 * 
	 * @param order
	 * @param row
	 * boolean flg true 删除一条医嘱 
	 * @return
	 * =======pangben 2013-1-29 添加参数 校验检验检查医嘱是否已经登记
	 */
	private boolean deleteOrder(OpdOrder order, int row, String message,String medAppMessage) {
		// 医疗卡操作可以删除医嘱====pangben 2011-12-16
		if (null == ektReadParm || null == ektReadParm.getValue("MR_NO")
				|| ektReadParm.getValue("MR_NO").length() <= 0) {
			if (!order.isRemovable(row, false)) {
				this.messageBox("已计费医嘱必须读卡,才可以执行删除或修改操作"); // 已计费医嘱不能删除
				return false;
			} 
			return true;
		} else {
			// 已收费医嘱没有主诊断不能删除
			int rowMainDiag = odo.getDiagrec().getMainDiag();
			if (rowMainDiag < 0) {
				this.messageBox("请开立主诊断");
				return false;
			}
			if (!ektDelete(order, row)) {// 校验是否可以删除医嘱
				this.messageBox(message); // 已计费医嘱不能删除
				return false;
			}
			//=========pangben 2013-1-29
			if(!medAppyCheckDate(order, row)){
				this.messageBox(medAppMessage); // 校验 检验检查已经登记的数据不能删除操作
				return false;
			}
			return readEKT();

		}
	}

	/**
	 * 医嘱页签点击事件
	 */
	public void onChangeOrderTab() {
//		this.messageBox("onChangeOrderTab");
		if (odo == null) {
			return;
		}
		if (odo.isModified()) {
			this.onTempSave();
		}
		getChangeOrderTab();
	}
	/**
	 * 将切换页签方法分开
	 * -=======pangben 2013-5-15
	 */
	private void getChangeOrderTab() {
		TTabbedPane tabPanel = (TTabbedPane) this
				.getComponent("TTABPANELORDER");
		switch (tabPanel.getSelectedIndex()) {
		case 0:
			initExa();
			break;
		case 1:
			// 处置
			initOp();
			this.calculateCash(TABLE_OP, "OP_AMT");
			break;
		case 2:
			// 西药
			initMed();
			break;
		case 3:
			// 中药
			initChnMed();
			this.calculateChnCash(this.getValueString("CHN_RX"));
			break;
		case 4:
			// 毒药
			initCtrl();
			this.calculateCash(TABLE_CTRL, "CTRL_AMT");
			break;
		case 5:
			//院前用药  add by huangtt 20151201
			initPreMed();
			break;
		}
		if (!StringUtil.isNullString(tableName)) {
			TTable table = (TTable) this.getComponent(tableName);
			table.acceptText();
		}
	}
	/**
	 * 诊断页签点选方法，如有既往史或过敏史页签头就变色
	 */
	public void onDiagPnChange() {
		if (odo == null)
			return;
		// if(odo.isModified()){
		// this.onTempSave();
		// }

		MedHistory medHistory = odo.getMedHistory();

		// Color orginal=new Color(255,255,255);
		TTabbedPane p = (TTabbedPane) this.getComponent("TTABPANELDIAG");
		if (medHistory.rowCount() > 1) {
			p.setTabColor(1, red);
		} else {
			p.setTabColor(1, null);
		}
		//modify by huangtt 20151111 start 过敏史如果为无的话不显示红色 
		String filter = odo.getDrugAllergy().getFilter();
		odo.getDrugAllergy().setFilter("");
		odo.getDrugAllergy().filter();
		if(odo.getDrugAllergy().rowCount() > 1){
			boolean flg = false;
			for (int i = 0; i < odo.getDrugAllergy().rowCount(); i++) {
				if(odo.getDrugAllergy().getItemData(i, "DRUGORINGRD_CODE").toString().length() == 0){
					continue;
				}
				String drugType = odo.getDrugAllergy().getItemData(i, "DRUG_TYPE").toString();
				if(!("N".equals(odo.getDrugAllergy().getItemData(i, "DRUGORINGRD_CODE")) 
						&& "N".equals(drugType))
						){
					flg = true;
				}

			}
			if(flg){
				p.setTabColor(4, red);
			}else{
				p.setTabColor(4, null);
			}
		} else {
			p.setTabColor(4, null);
		}
		odo.getDrugAllergy().setFilter(filter);
		odo.getDrugAllergy().filter();

		//		if (odo.getDrugAllergy().rowCount() > 1) {
		//			p.setTabColor(4, red);
		//		} else {
		//			p.setTabColor(4, null);
		//		}
		//modify by huangtt 20151111 end 过敏史如果为无的话不显示红色 

	}

	/**
	 * xueyf 2012-02-28 医保门特处方查询
	 */
	public void onINSDrQuery() {
		String CASE_NO = odo.getCaseNo();
		if (StringUtil.isNullString(CASE_NO)) {
			this.messageBox("请选择病患。");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("INS_TYPE", reg.getInsPatType());
		this.openDialog("%ROOT%\\config\\ins\\INSDrQueryList.x", parm);

	}

	/**
	 * 诊断页签双击事件，结构化病历选择模板
	 */
	public void onChangeTemplate() {
		TTabbedPane panel = (TTabbedPane) this.getComponent("TTABPANELDIAG");
		if (panel.getSelectedIndex() != 0) {
			return;
		}
		if (odo == null) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "ODO");
		parm.setData("ADM_TYPE", this.admType);
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("DR_CODE", Operator.getID());
		// zhangyong20110427 begin
		Object obj = this.openDialog("%ROOT%\\config\\opd\\ODOEmrTemplet.x",
				parm);
		// zhangyong20110427 end
		if (obj == null || !(obj instanceof TParm)) {
			return;
		}
		TParm action = (TParm) obj;
		String templetPath = action.getValue("TEMPLET_PATH");
		String templetName = action.getValue("EMT_FILENAME");

		word.onOpen(templetPath, templetName, 2, false);

		if (!"N".equalsIgnoreCase(odo.getRegPatAdm().getItemString(0,
				"SEE_DR_FLG"))) {
			saveFiles = EmrUtil.getInstance().getGSFile(odo.getCaseNo());
		}
		word.setCanEdit(true);
	}

	/**
	 * 病患列表展开事件
	 */
	public void onPat() {
		// 判断科室权限
		if (this.getPopedem("DEPT_POPEDEM"))
			onSelectPat("INSTEAD_DEPT");
		else
			onSelectPat("");
		mp.onDoubleClicked(false);
	}

	/**
	 * 身份1点击事件，将身分2和身份3清空
	 */
	public void onCtz1() {
		TComboBox t = (TComboBox) this.getComponent("CTZ2_CODE");
		t.setValue("");
		t = (TComboBox) this.getComponent("CTZ3_CODE");
		t.setValue("");
	}

	/**
	 * 身份2点击事件，与身份1和身份3比较，不能和他们的值相同
	 */
	public void onCtz2() {

		TComboBox t = (TComboBox) this.getComponent("CTZ1_CODE");
		TComboBox t2 = (TComboBox) this.getComponent("CTZ2_CODE");
		if (StringUtil.isNullString(t2.getValue()))
			return;
		if (StringUtil.isNullString(t.getValue())) {
			t2.setValue("");
			return;
		}
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0057");
			t2.setValue("");
			return;
		}
		t = (TComboBox) this.getComponent("CTZ3_CODE");
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0058");
			t2.setValue("");
			return;
		}
	}

	/**
	 * 身份3点击事件，与身份1和身份2比较，不能和他们的值相同
	 */
	public void onCtz3() {
		TComboBox t = (TComboBox) this.getComponent("CTZ1_CODE");
		TComboBox t3 = (TComboBox) this.getComponent("CTZ3_CODE");
		if (StringUtil.isNullString(t3.getValue()))
			return;
		if (StringUtil.isNullString(t.getValue())) {
			t3.setValue("");
			return;
		}
		if (t3.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0057");
			t3.setValue("");
			return;
		}
		t = (TComboBox) this.getComponent("CTZ2_CODE");
		if (t3.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0059");
			t3.setValue("");
			return;
		}
	}

	/**
	 * 病生理1点击事件，清空病生理2、3
	 */
	public void onPat1() {
		TComboBox t = (TComboBox) this.getComponent("PAT2_CODE");
		t.setValue("");
		t = (TComboBox) this.getComponent("PAT3_CODE");
		t.setValue("");
	}

	/**
	 * 病生理2点击事件，与病生理1、3比较，不能和他们的值相同
	 */
	public void onPat2() {

		TComboBox t = (TComboBox) this.getComponent("PAT1_CODE");
		TComboBox t2 = (TComboBox) this.getComponent("PAT2_CODE");
		if (StringUtil.isNullString(t2.getValue()))
			return;
		if (StringUtil.isNullString(t.getValue())) {
			t2.setValue("");
			return;
		}
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0060");
			t2.setValue("");
			return;
		}
		t = (TComboBox) this.getComponent("PAT3_CODE");
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0061");
			t2.setValue("");
			return;
		}

	}

	/**
	 * 病生理3点击事件，与病生理1、2比较，不能和他们的值相同
	 */
	public void onPat3() {
		TComboBox t = (TComboBox) this.getComponent("PAT1_CODE");
		TComboBox t2 = (TComboBox) this.getComponent("PAT3_CODE");
		if (StringUtil.isNullString(t2.getValue()))
			return;
		if (StringUtil.isNullString(t.getValue())) {
			t2.setValue("");
			return;
		}
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0060");
			t2.setValue("");
			return;
		}
		t = (TComboBox) this.getComponent("PAT2_CODE");
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0062");
			t2.setValue("");
			return;
		}

	}

	/**
	 * LMP点击事件，计算怀孕周数
	 */
	public void onLmp() {

		Timestamp LMP = (Timestamp) this.getValue("LMP_DATE");
		int week = OdoUtil
				.getPreWeek(TJDODBTool.getInstance().getDBTime(), LMP);
		this.setValue("PRE_WEEK", week + "");
	}

	/**
	 * 哺乳期点击事件，不能晚于哺乳期结束日期
	 */
	public void onBreastStartDate() {
		Timestamp t2 = (Timestamp) this.getValue("BREASTFEED_ENDDATE");
		if (t2 == null)
			return;
		Timestamp t1 = (Timestamp) this.getValue("BREASTFEED_STARTDATE");
		if (t1 == null) {
			return;
		}
		if (StringTool.getDateDiffer(t1, t2) > 0) {
			this.messageBox("E0063");
			this.setValue("BREASTFEED_STARTDATE", "");
			return;
		}
	}

	/**
	 * 哺乳期点击事件，不能晚于哺乳期结束日期
	 */
	public void onBreastEndDate() {
		Timestamp t2 = (Timestamp) this.getValue("BREASTFEED_ENDDATE");
		Timestamp t1 = (Timestamp) this.getValue("BREASTFEED_STARTDATE");
		if (t1 == null) {
			this.messageBox("E0064");
			this.setValue("BREASTFEED_ENDDATE", "");
			return;
		}
		if (t2 == null) {
			return;
		}
		if (StringTool.getDateDiffer(t1, t2) > 0) {
			this.messageBox("E0063");
			this.setValue("BREASTFEED_ENDDATE", "");
			return;
		}
	}

	/**
	 * 主诉点击事件
	 * 
	 * @param tag
	 *            String
	 */
	public void onSubjText(String tag) {
		TParm inParm = new TParm();
		inParm.setData("subject", this);
		inParm.setData("TAG", tag);
		inParm.setData("DEPT_OR_DR", "1");
		inParm.setData("DEPTORDR_CODE", "2");
		this.openWindow("%ROOT%\\config\\opd\\OPDComPhraseQuote.x", inParm,
				true);
	}

	/**
	 * 判断是否已读卡 true 已读，false 未读
	 */
	private boolean isReadEKT() {
		return isReadEKT;

	}

	/**
	 * 保存
	 */
	public void onSave() {
		if (!canSave()) {
			this.messageBox_("已超过看诊时间不可修改");
			return;
		}
		if (!isMyPat()) {
			saveSubjrec();
			this.messageBox("E0193");
			return;
		}

		//add by huangtt 20150828 医嘱进行比对，查看是否发生变化,如果发生变化就不弹出医疗卡收费界面
		boolean ektShowFlg = comparisionOrder();

		if(odoMainDrools.fireRules()){
			return;
		}

		// ============xueyf modify 20120220 start
		//		if (!isReadEKT()) {
		//			this.messageBox("未读取医疗卡。");
		//			// return;
		//		}
		int rowMainDiag = odo.getDiagrec().getMainDiag();
		if (rowMainDiag < 0) {
			this.messageBox("E0065");
			return;
		}
		// xueyf add ins check
		//deleteLisPosc = false;// 检验检查删除管控 HL7退费操作修改注记
		// ============xueyf modify 20120220 stop
		acceptForSave();
		saveSubjrec();
		saveRegInfo();
		odo.getRegPatAdm().setItem(0, "SEE_DR_FLG", "Y");
		// 判断reg主档的SEEN_DR_TIME是否有数据 如果为空记录当前时间
		if (odo.getRegPatAdm().getItemData(0, "SEEN_DR_TIME") == null) {
			odo.getRegPatAdm().setItem(0, "SEEN_DR_TIME",
					SystemTool.getInstance().getDate());
		}
		//		if (!odo.isModified()) {//===pangben 2013-5-3 注释不校验是否有操作医嘱，保存操作执行收费
		//			return;
		//		}
		// if (oPdSaveCountCheck()) {
		// return;
		// }
		if (!odo.checkSave()) {
			// $$ modified by lx 加入提示替代药
			if (odo.getErrText().indexOf("库存不足") != -1) {
				String orderCode = odo.getErrText().split(";")[1];
				TParm inParm = new TParm();

				inParm.setData("orderCode", orderCode);
				this.openDialog("%ROOT%\\config\\pha\\PHAREDrugMsg.x", inParm);
			} else {
				this.messageBox(odo.getErrText());
			}
			// $$ modified by lx 加入提示替代药
			this.messageBox("E0005");
			return;
		}
		String admStatus = odo.getRegPatAdm().getItemString(0, "ADM_STATUS");

		if (!("6".equalsIgnoreCase(admStatus) || "9"
				.equalsIgnoreCase(admStatus))) {
			odo.getRegPatAdm().setItem(0, "ADM_STATUS", "2");
		}
		TParm orderParm = new TParm();
		TParm ctrlParm = new TParm();
		TParm chnParm = new TParm();
		TParm exaParm = new TParm();
		TParm opParm = new TParm();
		if (odo.getOpdOrder().isModified()) {
			orderParm = odo.getOpdOrder().getModifiedOrderRx();
			ctrlParm = odo.getOpdOrder().getModifiedCtrlRx();
			chnParm = odo.getOpdOrder().getModifiedChnRx();
			exaParm = odo.getOpdOrder().getModifiedExaRx();
			odo.getOpdOrder().updateMED(odo);
			opParm = odo.getOpdOrder().getModifiedOpRx();
			if (orderParm.getCount() > 0) {
				if (!odo.getOpdOrder().isOrgAvalible(phaCode)) {
					this.messageBox("E0117");
				}
			}
		}
		//校验数据
		if(!getTempSaveParm())
			return;
		
		odoMainDrools.fireRulesOrder();
		
		TParm ektOrderParmone = new TParm();
		// parm.setData("REGION_CODE", Operator.getRegion());
		ektOrderParmone.setData("CASE_NO", reg.caseNo());
		// 获得此次操作医疗卡所有的医嘱 在执行删除所有医嘱时使用
		ektOrderParmone = OrderTool.getInstance().selDataForOPBEKT(
				ektOrderParmone);
		if (ektOrderParmone.getErrCode() < 0) {
			return;
		}
		// 获得此次医疗卡操作所有需要执行的医嘱=====pangben 2012-4-14
		TParm ektSumExeParm = odo.getOpdOrder().getEktParam(ektOrderParmone);

		//add by huangtt 20160829 start 
		if(ektShowFlg){
			this.messageBox("医嘱发生变化，不进行收费操作");

		}else{
			if (null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0) {

			} else {
				// 判断此次操作的医嘱在数据库中是否已经存在，如果存在,在执行收费操作时，判断医疗卡中金额是否充足
				// 如果金额不足，执行此处方签所收费的医嘱退还医疗卡中
				updateOrderUnConcle(ektOrderParmone, ektSumExeParm);	
			}


			EktParam ektParam = new EktParam();
			ektParam.setType("ODO");
			ektParam.setOdoMainControl(this);
			ektParam.setReg(reg);
			ektParam.setPat(pat);
			ektParam.setOrderOldParm(ektOrderParmone);
			ektParam.setOrderParm(ektSumExeParm);

			EktTradeContext ektTradeContext = new EktTradeContext(ektParam);
			try {

				//创建参数，打开收费界面，执行收费
				ektTradeContext.openClient(odo);


			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}




		//add by huangtt 20160829 end
		
		
		
		// add by wangqing 20170624 start
		// 判断是否有新增的CT医嘱
		System.out.println("#######################新增的检验检查医嘱="+odo.getOpdOrder().getModifiedExaOrder());
		TParm newExaOrder = new TParm();
		newExaOrder = odo.getOpdOrder().getModifiedExaOrder();
		// add by wangqing 20170624 end



		boolean isChanged = false;
		try {
			if (!odo.onSave()) {
				// EKTIO.getInstance().unConsume(odo.getTredeNo(),this);
				this.messageBox("E0005");
				this.messageBox_(odo.getErrText());
				return;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			messageBox(e.getMessage());
			isChanged = true;
		}
				
		//$------------Start-add caoyong 20131227 保存以后得到接时间

		if("E".equals(admType)){
			TParm sparm=this.getParmSeeDrTime(odo.getMrNo(), odo.getCaseNo());
			if(sparm.getCount()>0){
				this.setValue("SEEN_DR_TIME", sparm.getValue("SEEN_DR_TIME",0).substring(0,19).replace("-","/"));
			}else{
				this.setValue("SEEN_DR_TIME","");
			}
		}
		if(isChanged){
			onTablePatDoubleClick(false);
			return;
		}
		//$------------end-add caoyong 20131227 保存以后得到接诊号时间
		//this.messageBox("P0005");
		//add by huangtt 20151114
		//		if(isChanged || ektShowFlg){
		//			this.messageBox("医嘱发生变化，不进行收费操作");
		//		}else{
		//			isExeFee(ektOrderParmone, ektSumExeParm);
		//		}


		//add by huangtt 20160907 start
		if(!ektShowFlg){
			System.out.println(ektShowFlg);
			if(!ektExec(ektSumExeParm)){
				return;
			}
		}


		//add by huangtt 20160907 end 



		// 通过reg和caseNo得到pat
		opb = OPB.onQueryByCaseNo(reg);// ===============pangben 20110914
		//isFee = true;// 执行成功以后可以收费===============pangben 20110914
		if (orderParm.getCount("RX_NO") > 0) {
			onPrintOrder(orderParm, MED);
		}
		if (ctrlParm.getCount("RX_NO") > 0) {
			onPrintOrder(ctrlParm, CTRL);
		}
		if (chnParm.getCount("RX_NO") > 0) {
			onPrintOrder(chnParm, CHN);
		}
		// 急诊加打印检验检查医嘱单 shibl add20120320
		// 门急诊
		// =======pangben 2012-06-28打印处方签不区分门急诊 ，都要打印
		// if (this.admType.equals("E")) {
		if (exaParm.getCount("RX_NO") > 0) {
			// 打印检验检查医嘱单
			onPrintExa(exaParm);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						onEmr();
					} catch (Exception e) {
					}
				}
			});
		}
		if (opParm.getCount("RX_NO") > 0) {
			boolean printSwitch =
					StringTool.getBoolean(IReportTool.getInstance()
							.getReportPath("OpdNewHandleSheet.printSwitch"));//wanglogn add 20150520
			if (printSwitch) {//ReportConfig.x增加处置通知单是否打印开关
				onPrintOp(opParm);
			}
		}
		int count = odo.getDiagrec().rowCount();
		for (int i = 0; i < count; i++) {
			if (odo.getDiagrec().isContagion(i)) {
				TParm can = new TParm();
				can.setData("MR_NO", odo.getMrNo());
				can.setData("CASE_NO", odo.getCaseNo());
				can.setData("ICD_CODE", odo.getDiagrec().getItemString(i,
						"ICD_CODE"));
				can.setData("DEPT_CODE", Operator.getDept());
				can.setData("USER_NAME", Operator.getName());
				this.openDialog("%ROOT%/config/mro/MROInfect.x", can);
			}
		}
		// }
		// =============pangben 2012-6-28 保存操作不打印病历
		// if (this.admType.equals("O"))
		// onPrintCase();
		onDiagPnChange();
		onContagionReport();// 传染病弹出界面

		// add by wangqing 20170626 start 如果新增的医嘱有CT医嘱，CT室跑马灯提示
		String caseNo = odo.getCaseNo();
		TParm resultParm = EMRAMITool.getInstance().getEnterRouteAndPathKindByCaseNo(caseNo);
		if ("E02".equals(resultParm.getValue("ENTER_ROUTE",0))) {
			if(newExaOrder.getCount()>0){
				for(int i=0; i<newExaOrder.getCount(); i++){
					if(newExaOrder.getValue("ORDER_CODE", i) != null && newExaOrder.getValue("ORDER_CODE", i).contains("Y0202")){
						client1 = SocketLink.running("","CT", "CT");
						if (client1.isClose()) {
							this.messageBox("#######client1.getErrText()="+client1.getErrText());
//							out(client1.getErrText());
							return;
						}
						TParm result11 = EMRAMITool.getInstance().getErdEvalutionDataByCaseNo(caseNo);
						String triageNo11 = result11.getValue("TRIAGE_NO",0);
						client1.sendMessage("CT", "CASE_NO:"//CT :SKT_USER 表添加数据
								+ caseNo + "|MR_NO:" + pat.getMrNo()+ "|TRIAGE_NO:" + triageNo11 
								+ "|PAT_NAME:" + pat.getName()+"|ORDER_CODE:"+newExaOrder.getValue("ORDER_CODE", i));
						if (client1 == null)
							return;
						client1.close();

						// modified by wangqing 20170701
//						TParm ctParm = EMRAMITool.getInstance().getAmiCTDataByCaseNo(caseNo);
//						if (ctParm.getCount() > 0) {
//							TParm saveCTParm = EMRAMITool.getInstance().updateAmiCTDataByCaseNo(caseNo);
//							if (saveCTParm.getErrCode() < 0) {
//								this.messageBox("通知CT室时间保存失败！");
//								return;
//							}
//						} else {
//							TParm saveCTParm = EMRAMITool.getInstance().saveAmiCTData(caseNo);
//							if (saveCTParm.getErrCode() < 0) {
//								this.err(saveCTParm);
//								return;
//							}
//						}
						break;
					}
				}
			}
		}
		// add by wangqing 20170626 end

		// add by wnagb 2017/5/18 首诊心电报告自动周转
		this.moveFirstEcgPdf();
	}


	/**
	 * 修改医嘱操作时，点击保存或者暂存操作执行直接收费操作
	 */
	private void isExeFee(TParm ektOrderParmone, TParm ektSumExeParm) {
		//boolean ektOnFee = false;
		if (null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0) {
			// this.messageBox("未确认身份,请读医疗卡");
			// return;

		} else {
			// 判断此次操作的医嘱在数据库中是否已经存在，如果存在,在执行收费操作时，判断医疗卡中金额是否充足
			// 如果金额不足，执行此处方签所收费的医嘱退还医疗卡中
			updateOrderUnConcle(ektOrderParmone, ektSumExeParm);	
		}
		// 删除\修改医嘱收费
		if (!onEktSave(ektOrderParmone, ektSumExeParm)) {
		}
	}
	/**
	 * 修改收费医嘱不能执行扣款界面撤销动作
	 * @return
	 */
	private boolean updateOrderUnConcle(TParm ektOrderParmone,TParm ektOrderParm){
		//点击暂存按钮 如果已收费的医嘱修改金额 执行 修改医嘱扣款或退款操作
		boolean unFlg = false;
		TParm updateParm=ektOrderParm.getParm("updateParm");
		for (int i = 0; i < updateParm.getCount("ORDER_CODE"); i++) {
			if (updateParm.getValue("CAT1_TYPE", i).equals("RIS")
					|| updateParm.getValue("CAT1_TYPE", i).equals(
							"LIS")
					|| updateParm.getValue("ORDER_CODE", i).length() <= 0) {
				continue;
			}
			for (int j = 0; j < ektOrderParmone.getCount("ORDER_CODE"); j++) {//====pangb 2013-2-28 修改医嘱操作暂存有问题
				if (updateParm.getValue("RX_NO", i).equals(
						ektOrderParmone.getValue("RX_NO", j))
						&& updateParm.getValue("SEQ_NO", i).equals(
								ektOrderParmone.getValue("SEQ_NO", j))) {
					if (updateParm.getDouble("AMT", i) != ektOrderParmone
							.getDouble("AR_AMT", j)) {
						unFlg = true;// 判断是否修改医嘱
						break;
					}
				}
			}
			if (unFlg) {
				ektOrderParmone.setData("OPBEKTFEE_FLG", "Y");
				//ektOnFee = true;
				break;
			}
		}
		return unFlg;
	}
	/**
	 * 泰心门诊医疗卡收费 只能操作医疗卡收费操作 
	 */
	public void onFee() {
		//		if (!PatTool.getInstance().isLockPat(pat.getMrNo())) {
		//			this.messageBox("病患已经被其他用户占用!");
		//			return;
		//		}
		if (null == caseNo || caseNo.length() <= 0)
			return;
		//		// 查看此就诊病患是否是医疗卡操作
		//		if (null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0) {
		//			this.messageBox("未确认身份,请读医疗卡");
		//			return;
		//
		//		}
		//		if (!ektReadParm.getValue("MR_NO").equals(this.getValue("MR_NO"))) {
		//			this.messageBox("病患信息不符,此医疗卡病患名称为:"
		//					+ ektReadParm.getValue("PAT_NAME"));
		//			return;
		//		}
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CASE_NO", reg.caseNo());
		// 获得此次操作医疗卡所有的医嘱 在执行删除所有医嘱时使用
		TParm ektOrderParm = OrderTool.getInstance().selDataForOPBEKT(parm);
		onTempSave(ektOrderParm);
		//odo.getOpdOrder().onQuery();
		//onExeFee();
	}
	/**
	 * 执行收费以后重新初始化界面数据
	 */
	private void onExeFee(){
		// 重新加载数据
		if (!odo.getOpdOrder().onQuery()) {
			return ;
		}
		//=======pangben 2013-7-10 修改界面删除空行问题
		// 删除后，增加一空行
		TTabbedPane tabPanel = (TTabbedPane) this
				.getComponent("TTABPANELORDER");
		String rxNo="";
		TTable table=null;
		String rxType="";
		String tableName="";
		switch (tabPanel.getSelectedIndex()) {
		case 0:
			rxNo = this.getValueString(this.EXA_RX);
			table=tblExa;
			rxType=EXA;
			tableName=TABLE_EXA;
			break;
		case 1:
			// 处置
			rxNo = this.getValueString(OP_RX);
			table=tblOp;
			rxType=OP;
			tableName=TABLE_OP;
			break;
		case 2:
			// 西药
			rxNo = this.getValueString(MED_RX);
			table=tblMed;
			rxType=MED;
			tableName=TABLE_MED;
			break;
		case 3:
			// 中药
			rxNo = this.getValueString(CHN_RX);
			table=tblChn;
			rxType=CHN;
			tableName=TABLE_CHN;
			break;
		case 4:
			rxNo = this.getValueString(CTRL_RX);
			table=tblCtrl;
			rxType=CTRL;
			tableName=TABLE_CTRL;
			break;
		case 5:
			rxNo = this.getValueString(PREMED_RX);
			table=tblPreMed;
			rxType=PREMED;
			tableName=TABLE_PRE_MED;
			break;
		}
		String []tableNames={TABLE_EXA,TABLE_OP,TABLE_MED,TABLE_CHN,TABLE_CTRL,TABLE_PRE_MED};
		odo.getOpdOrder().newOrder(rxType, rxNo);
		table.setDSValue();
		getTableInit(tableName, tableNames);
		//		odo.getOpdOrder().retrieve();//===pangben 2013-5-13重新获得数据库数据，加载到大对象中
		getChangeOrderTab();
	}
	/**
	 * 初始化空行
	 * @param tableName
	 */
	private void getTableInit(String tableName,String [] tableNames){
		for (int i = 0; i < tableNames.length; i++) {
			if (tableName.equals(tableNames[i])) {
				continue;
			}
			this.setTableInit(tableNames[i], false);
		}
	}
	/**
	 * 医疗卡暂存操作
	 * 
	 * @param ektOrderParm
	 */
	private void onTempSave(TParm ektOrderParm) {
		// System.out.println("HHHFHFGFGFGFG");
		if (!canSave()) {
			this.messageBox_("已超过看诊时间不可修改");
			//isEKTFee = false;// 医疗卡收费使用
			return;
		}
		if (!isMyPat()) { // 判断解锁
			saveSubjrec();
			this.messageBox("E0193");
			//isEKTFee = false;// 医疗卡收费使用
			return;
		}

		//add by huangtt 20150828 医嘱进行比对，查看是否发生变化
		boolean ektShowFlg = comparisionOrder();
		System.out.println("ektShowFlg----"+ektShowFlg);

		if(odoMainDrools.fireRules()){
			return;
		}

		int rowMainDiag = odo.getDiagrec().getMainDiag();
		if (rowMainDiag < 0) {
			this.messageBox("E0065");
			return;
		}
		acceptForSave();
		saveSubjrec();
		
		
		if(!getTempSaveParm())
			return;
		setTempSaveOdo();
		saveRegInfo();
		// 查看此就诊病患是否是医疗卡操作
		// 添加医嘱 收费
		if (!odo.isModified()) {
			//add by huangtt 20151114
			if(ektShowFlg){
				this.messageBox("医嘱发生变化，不进行收费操作");
			}
			//			else{
			//				onEktSave(ektOrderParm, ektSumExeParm); // 参数作用：医疗卡操作失败回冲金额
			//
			//			}
			return;
		}
		if (!odo.checkSave()) {
			// $$ modified by lx 加入提示替代药
			if (odo.getErrText().indexOf("库存不足") != -1) {
				String orderCode = odo.getErrText().split(";")[1];
				TParm inParm = new TParm();
				inParm.setData("orderCode", orderCode);
				this.openDialog("%ROOT%\\config\\pha\\PHAREDrugMsg.x", inParm);
			} else {
				this.messageBox(odo.getErrText());
			}

			this.messageBox("E0005");
			//isEKTFee = false;// 医疗卡收费使用
			return;
		}
		// //拿到申请单结果集
		// TParm emrParm =
		// OrderUtil.getInstance().getOrderPasEMRAll(odo.getOpdOrder(),"ODO");
		TParm exaParm = new TParm();
		TParm opParm = new TParm();
		//		TParm orderParm = new TParm();===西药处方签不打印
		TParm ctrlParm = new TParm();
		if (odo.getOpdOrder().isModified()) {
			exaParm = odo.getOpdOrder().getModifiedExaRx();
			opParm = odo.getOpdOrder().getModifiedOpRx();
			ctrlParm = odo.getOpdOrder().getModifiedCtrlRx();//管制药品
		}
		//		if (odo.getOpdOrder().isModified()) {
		//			orderParm = odo.getOpdOrder().getModifiedOrderRx();
		//			if (orderParm.getCount() > 0) {
		//				if (!odo.getOpdOrder().isOrgAvalible(phaCode)) {
		//					this.messageBox("E0117");
		//					return;
		//				}
		//			}
		//		}
		
		odoMainDrools.fireRulesOrder();
		
		// 获得此次医疗卡操作所有需要执行的医嘱
		// 需要操作的医嘱 ：删除 、添加、 修改
		TParm ektSumExeParm = odo.getOpdOrder().getEktParam(ektOrderParm);
		
		TParm ektOrderParmone = new TParm();
		if (null == ektOrderParm) {//点击暂存按钮ektOrderParm 入参为null
			TParm parmOne = new TParm();
			parmOne.setData("CASE_NO", reg.caseNo());
			// 获得此次操作医疗卡所有的医嘱 在执行删除所有医嘱时使用
			ektOrderParmone = OrderTool.getInstance().selDataForOPBEKT(parmOne);
			if (ektOrderParmone.getErrCode() < 0) {
				return;
			}
		}

		//add by huangtt 20160829 start

		if(ektShowFlg){
			this.messageBox("医嘱发生变化，不进行收费操作");
		}else{
			if (null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0) {
			} else {
				// 判断此次操作的医嘱在数据库中是否已经存在，如果存在,在执行收费操作时，判断医疗卡中金额是否充足
				// 如果金额不足，执行此处方签所收费的医嘱退还医疗卡中
				if (null == ektOrderParm) {
					//点击暂存按钮 如果已收费的医嘱修改金额 执行 修改医嘱扣款或退款操作
					if(updateOrderUnConcle(ektOrderParmone, ektSumExeParm))
						ektOrderParm=ektOrderParmone;
				}
			}

			EktParam ektParam = new EktParam();
			ektParam.setType("ODO");
			ektParam.setOdoMainControl(this);
			ektParam.setReg(reg);
			ektParam.setPat(pat);
			ektParam.setOrderOldParm(ektOrderParmone);
			ektParam.setOrderParm(ektSumExeParm);

			EktTradeContext ektTradeContext = new EktTradeContext(ektParam);
			try {

				//创建参数，打开收费界面，执行收费
				ektTradeContext.openClient(odo);


			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

		//add by huangtt 20160829 end

//		

		//保存数据
		boolean isChanged = false;
		try {
			if (!odo.onSave()) {
				this.messageBox(odo.getErrText());
				this.messageBox("E0005");
				// isEKTFee = false;// 医疗卡收费使用
				return;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			messageBox(e.getMessage());
			isChanged = true;
		}
		//$------------Start-add caoyong 20131227 暂存以后得到接诊号时间
		if ("E".equals(admType)) {
			TParm sparm = this.getParmSeeDrTime(odo.getMrNo(), odo.getCaseNo());
			if (sparm.getCount() > 0) {
				this.setValue("SEEN_DR_TIME", sparm.getValue("SEEN_DR_TIME", 0)
						.substring(0, 19).replace("-", "/"));
			} else {
				this.setValue("SEEN_DR_TIME", "");
			}
		}
		if(isChanged){
			onTablePatDoubleClick(false);
			return;
		}
		//$-------------end add caoyong 20131227 暂存以后得到接诊时间
		//this.messageBox("P0005");
		opb = OPB.onQueryByCaseNo(reg);// 执行医疗卡收费时使用===============pangben
		// 20111010
		//isFee = true;// 执行成功以后可以收费===============pangben 20111010

		//add by huangtt 20160907 start
		if(!ektShowFlg){
			if(!ektExec(ektSumExeParm)){
				return;
			}
		}

		// //医疗卡保存操作
		//		if (null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0) {
		//		} else {
		//			// 判断此次操作的医嘱在数据库中是否已经存在，如果存在,在执行收费操作时，判断医疗卡中金额是否充足
		//			// 如果金额不足，执行此处方签所收费的医嘱退还医疗卡中
		//			if (null == ektOrderParm) {
		//				//点击暂存按钮 如果已收费的医嘱修改金额 执行 修改医嘱扣款或退款操作
		//				if(updateOrderUnConcle(ektOrderParmone, ektSumExeParm))
		//					ektOrderParm=ektOrderParmone;
		//			}
		//		}
		//		
		//		
		//		
		//		// 删除\修改医嘱收费
		//		if (null!=ektOrderParm){
		//			//add by huangtt 20151114
		//			if(ektShowFlg){
		//				this.messageBox("医嘱发生变化，不进行收费操作");
		//			}else{
		//				onEktSave(ektOrderParm, ektSumExeParm);//=======pangben 2013-3-19 修改金额不足也打印处方签
		//			}
		//			
		//		}

		//add by huangtt 20160907 end 

		if (exaParm.getCount("RX_NO") > 0) {
			// 打印检验检查医嘱单
			onPrintExa(exaParm);
			getRun(1);
		}
		// =======pangben 2012-6-29 西药处方签打印
		//		if (orderParm.getCount("RX_NO") > 0) {
		//			onPrintOrder(orderParm, MED);
		//		}
		if (opParm.getCount("RX_NO") > 0) {//处置打印处方签
			boolean printSwitch =
					StringTool.getBoolean(IReportTool.getInstance()
							.getReportPath("OpdNewHandleSheet.printSwitch"));// wanglogn add
			// 20150520
			if (printSwitch) {// ReportConfig.x增加处置通知单是否打印开关
				onPrintOp(opParm);
			}
		}
		if (ctrlParm.getCount("RX_NO") > 0) {//管制药品打印处方签
			onPrintOrder(ctrlParm, CTRL);
		}
		getRun(4);
		if (ektExeConcel) {// 医疗卡 删除医嘱操作 点选取消按钮 执行撤销删除医嘱操作
			TParm parm = new TParm();
			parm.setData("MR_NO", ektReadParm.getValue("MR_NO"));
			TParm regParm = OPDAbnormalRegTool.getInstance().selectRegForOPD(
					parm);
			for (int i = 0; i < regParm.getCount("CASE_NO"); i++) {
				if (regParm.getValue("CASE_NO", i).equals(reg.caseNo())) {
					// wc = "W"; // 默认为西医
					this.initOpd(regParm, 0);// 初始化
					ektExeConcel = false;
					break;
				}
			}
		}
		onDiagPnChange();
	}
	/**
	 * 传染病报告卡
	 */
	private void getDiagrec(){
		int count = odo.getDiagrec().rowCount();
		for (int i = 0; i < count; i++) {
			if (odo.getDiagrec().isContagion(i)) {
				TParm can = new TParm();
				can.setData("MR_NO", odo.getMrNo());
				can.setData("CASE_NO", odo.getCaseNo());
				can.setData("ICD_CODE", odo.getDiagrec().getItemString(i,
						"ICD_CODE"));
				can.setData("DEPT_CODE", Operator.getDept());
				can.setData("USER_NAME", Operator.getName());
				this.openDialog("%ROOT%/config/mro/MROInfect.x", can);
			}
		}
	}
	/**
	 * 
	 * @param type 1:弹出申请单 2: 4:传染病报告卡
	 */
	public void getRun(final int type){

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					switch (type){
					case 1:
						onEmr();
						break;
					case 2:
						break;
					case 3:
						break;
					case 4:
						getDiagrec();
						break;
					}

				} catch (Exception e) {
				}
			}
		});
	}
	/**
	 * 暂存数据 
	 * 赋值操作
	 */
	private void setTempSaveOdo(){
		String admStatus = odo.getRegPatAdm().getItemString(0, "ADM_STATUS");
		if (!("6".equalsIgnoreCase(admStatus) || "9"
				.equalsIgnoreCase(admStatus))) {
			odo.getRegPatAdm().setItem(0, "ADM_STATUS", "2");
		}
		String SEE_DR_FLG = (String) odo.getRegPatAdm().getItemData(0,
				"SEE_DR_FLG");
		if (!StringUtil.isNullString(SEE_DR_FLG) && !SEE_DR_FLG.equals("Y")) {
			odo.getRegPatAdm().setItem(0, "SEE_DR_FLG", "T");
		}
		// 判断reg主档的SEEN_DR_TIME是否有数据 如果为空记录当前时间
		if (odo.getRegPatAdm().getItemData(0, "SEEN_DR_TIME") == null) {
			odo.getRegPatAdm().setItem(0, "SEEN_DR_TIME",
					SystemTool.getInstance().getDate());
		}
	}
	/**
	 * 暂存操作 校验数据
	 * @return
	 */
	private boolean getTempSaveParm(){
		// 新加的数据
		int newRow[] = odo.getOpdOrder().getNewRows();
		TParm ordParm = new TParm();
		// 合理用药检测
		if (!checkDrugAuto()) {
			TTabbedPane tabPanel = (TTabbedPane) this
					.getComponent("TTABPANELORDER");
			switch (tabPanel.getSelectedIndex()) {
			case 2:
				// 西药
				odo.getOpdOrder().setFilter(
						"RX_TYPE='"+ MED+ "' AND ORDER_CODE <>'' AND #NEW#='Y'  AND #ACTIVE#='Y'");
				odo.getOpdOrder().filter();
				break;
			case 3:
				// 中药
				odo.getOpdOrder().setFilter("RX_TYPE='"+ CHN
						+ "' AND ORDER_CODE <>'' AND #NEW#='Y'  AND #ACTIVE#='Y'");
				odo.getOpdOrder().filter();
				break;
			case 4:
				// 管制药
				odo.getOpdOrder().setFilter("RX_TYPE='"+ CTRL
						+ "' AND ORDER_CODE <>'' AND #NEW#='Y' AND #ACTIVE#='Y'");
				odo.getOpdOrder().filter();
				break;
			}
			String buff = odo.getOpdOrder().isFilter() ? odo.getOpdOrder().FILTER
					: odo.getOpdOrder().PRIMARY;
			int newDRow[] = odo.getOpdOrder().getNewRows(buff);
			for (int i : newDRow) {
				ordParm = odo.getOpdOrder().getRowParm(0);
				String order_code = ordParm.getValue("ORDER_CODE");
				if (!order_code.equals("")) {
					this.deleteorderAuto(0);
				}
			}
			return false;
		}
		for (int i : newRow) {
			ordParm = odo.getOpdOrder().getRowParm(i);
			String order_code = ordParm.getValue("ORDER_CODE");
			// 进入医令管控接口
			if (getctrflg(order_code).equals("Y")) {
				TParm crtParm = new TParm();
				crtParm.setData("ADM_TYPE", admType);
				crtParm.setData("CTZ_CODE", ordParm.getValue("CTZ1_CODE"));
				crtParm.setData("ORDER_CODE", ordParm.getValue("ORDER_CODE"));
				crtParm.setData("CASE_NO", ordParm.getValue("CASE_NO"));
				crtParm.setData("MR_NO", ordParm.getValue("MR_NO"));
				crtParm.setData("ORDER_DATE", ordParm
						.getTimestamp("ORDER_DATE"));
				if (CTRPanelTool.getInstance().selCTRPanel(crtParm)
						.getErrCode() == 100) {
					if (!CTRPanelTool.getInstance().selCTRPanel(crtParm)
							.getValue("FORCE_FLG").equals("Y")) {
						if (this.messageBox("提示信息/Tip",
								CTRPanelTool.getInstance()
								.selCTRPanel(crtParm).getValue(
										"MESSAGE") + ",继续开立?",
								0) != 0) {
							this.deleteorderAuto(i);
							return false;
						} else {
							this.messageBox(CTRPanelTool.getInstance()
									.selCTRPanel(crtParm).getValue("MESSAGE"));
						}
					} else {
						this.messageBox(CTRPanelTool.getInstance().selCTRPanel(
								crtParm).getValue("MESSAGE"));
						this.deleteorderAuto(i);
						return false;
					}

				}
			}
		}
		return true;
	}
	/**
	 * hl7发送添加参数方法
	 * 
	 * @param checkParm
	 *            TParm
	 */
	public void hl7Temp(TParm checkParm) {
		int count = checkParm.getCount("ORDER_CODE");
		for (int i = 0; i < count; i++) {
			if (!checkParm.getValue("CAT1_TYPE", i).equals("RIS")
					|| !checkParm.getValue("CAT1_TYPE", i).equals("LIS")
					|| checkParm.getValue("ORDER_CODE", i).length() <= 0) {
				continue;
			}
			if (checkParm.getBoolean("SETMAIN_FLG", i)) {// 集合医嘱主项
				String orderCode = checkParm.getValue("ORDER_CODE", i);// 医嘱代码
				String orderSetGroupNo = checkParm.getValue(
						"ORDERSET_GROUP_NO", i);// 集合医嘱组号（用于区分同时开立2个相同的集合医嘱）
				String rxNo = checkParm.getValue("RX_NO", i);// 处方签号
				for (int j = count - 1; j >= 0; j--) {
					// 如果是集合医嘱细项删除
					if (!checkParm.getBoolean("SETMAIN_FLG", j)
							&& orderCode.equals(checkParm.getValue(
									"ORDERSET_CODE", j))
							&& orderSetGroupNo.equals(checkParm.getValue(
									"ORDERSET_GROUP_NO", j))
							&& rxNo.equals(checkParm.getValue("RX_NO", j))) {
						checkParm.removeRow(j);
					}
				}
			}
		}
		// System.out.println("checkParm:::::"+checkParm);
		TParm hl7ParmEnd = new TParm();
		// double sum = 0.0;
		int hl7Count = checkParm.getCount("ORDER_CODE");
		for (int i = 0; i < hl7Count; i++) {
			if (checkParm.getData("ORDERSET_CODE", i).equals(
					checkParm.getValue("ORDER_CODE", i))
					&& checkParm.getValue("HIDE_FLG", i).equals("N")) {
				hl7ParmEnd.addData("ORDER_CAT1_CODE", checkParm.getData(
						"ORDER_CAT1_CODE", i));
				hl7ParmEnd.addData("TEMPORARY_FLG", checkParm.getData(
						"TEMPORARY_FLG", i));
				hl7ParmEnd
				.addData("ADM_TYPE", checkParm.getData("ADM_TYPE", i));
				hl7ParmEnd.addData("RX_NO", checkParm.getData("RX_NO", i));
				hl7ParmEnd.addData("SEQ_NO", checkParm.getData("SEQ_NO", i));
				hl7ParmEnd.addData("MED_APPLY_NO", checkParm.getData(
						"MED_APPLY_NO", i));
				hl7ParmEnd.addData("CAT1_TYPE", checkParm.getData("CAT1_TYPE",
						i));
				hl7ParmEnd.addData("BILL_FLG", checkParm.getData("BILL_FLG", i));
			}
		}
		// 得到收费项目
		sendHL7Parm = hl7ParmEnd;
	}
	/**
	 * 集合医嘱过滤细项
	 * 
	 * @param parm
	 *            TParm
	 */
	public TParm tableShow(TParm parm) {
		// 医嘱代码
		String orderCode = "";
		// 医嘱组号
		int groupNo = -1;
		// 计算集合医嘱的总费用
		double fee = 0.0;
		// 医嘱数量
		int count = parm.getCount("ORDER_CODE");
		// ==================pangben modify 20110804 删除按钮显示
		// 需要删除的细项列表
		int[] removeRow = new int[count < 0 ? 0 : count]; // =====pangben modify
		// 20110801
		int removeRowCount = 0;
		// 循环医嘱
		for (int i = 0; i < count; i++) {
			Order order = (Order) parm.getData("OBJECT", i);
			// 如果不是集合医嘱主项
			if (order.getSetmainFlg() != null
					&& !order.getSetmainFlg().equals("Y")) {
				continue;
			}
			groupNo = -1;
			fee = 0.0;
			// 医嘱代码
			orderCode = order.getOrderCode();
			String rxNo = order.getRxNo();
			// 组
			groupNo = order.getOrderSetGroupNo();
			// 如果是主项循环所有医嘱清理细项
			for (int j = i; j < count; j++) {
				Order orderNew = (Order) parm.getData("OBJECT", j);
				// 如果是这个主项的细项
				if (orderCode.equals(orderNew.getOrdersetCode())
						&& orderNew.getOrderSetGroupNo() == groupNo
						&& !orderNew.getOrderCode().equals(
								orderNew.getOrdersetCode())
						&& rxNo.equals(orderNew.getRxNo())) {
					// 计算费用
					fee += orderNew.getArAmt();
					// 保存要删除的行
					removeRow[removeRowCount] = j;
					// 自加
					removeRowCount++;
				}
			}
			// 细项费用绑定主项
			parm.setData("AR_AMT", i, fee);
		}
		// 删除集合医嘱细项=====pangben modify 20110801 不用去医生站就诊直接可以开立医嘱计费
		if (removeRowCount > 0) {
			for (int i = removeRowCount - 1; i >= 0; i--) {
				parm.removeRow(removeRow[i]);
			}
			// parm.setCount(parm.getCount() - removeRowCount);
		}
		// parm.setCount(parm.getCount() - removeRowCount);
		// 调用table赋值方法
		return parm;

	}

	/**
	 * 调用HL7
	 */
	private void sendHL7Mes() {
		/**
		 * 发送HL7消息
		 * 
		 * @param admType
		 *            String 门急住别
		 * @param catType
		 *            医令分类
		 * @param patName
		 *            病患姓名
		 * @param caseNo
		 *            String 就诊号
		 * @param applictionNo
		 *            String 条码号
		 * @param flg
		 *            String 状态(0,发送1,取消)
		 */
		int count = 0;
		if (null == sendHL7Parm||sendHL7Parm.getCount("ADM_TYPE")<=0) {
			return;
		} else {
			count = ((Vector) sendHL7Parm.getData("ADM_TYPE")).size();
		}
		List list = new ArrayList();
		String patName = getValue("PAT_NAME").toString();
		for (int i = 0; i < count; i++) {
			TParm temp = sendHL7Parm.getRow(i);
			// System.out.println("收费项目:"+temp);
			if (temp.getValue("TEMPORARY_FLG").length() == 0)
				continue;
			String admType = temp.getValue("ADM_TYPE");
			TParm parm = new TParm();
			parm.setData("PAT_NAME", patName);
			parm.setData("ADM_TYPE", admType);
			if (temp.getValue("BILL_FLG").equals("N")) {// 退费
				parm.setData("FLG", 1);
			}else{
				parm.setData("FLG", 0);
			}
			parm.setData("CASE_NO", opb.getReg().caseNo());
			parm.setData("LAB_NO", temp.getValue("MED_APPLY_NO"));
			parm.setData("CAT1_TYPE", temp.getValue("CAT1_TYPE"));
			parm.setData("ORDER_NO", temp.getValue("RX_NO"));
			parm.setData("SEQ_NO", temp.getValue("SEQ_NO"));
			list.add(parm);
		}
		//		 System.out.println("发送接口项目:"+list);
		if (list.size() <= 0) {
			return;
		}
		// 调用接口
		TParm resultParm = Hl7Communications.getInstance().Hl7Message(list);
		// System.out.println("resultParm::::"+resultParm);
		if (resultParm.getErrCode() < 0)
			this.messageBox(resultParm.getErrText());
	}

	/**
	 * 是否正在本人手中锁住病患
	 * 
	 * @return boolean
	 */
	public boolean isMyPat() {
		if (odo == null) {
			// System.out.println("------------"+odo.getMrNo());
			return false;
		}
		//		TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
		// System.out.println("----------------------" + parm);
		//		String odo_type = "ODO";
		//		if ("E".equals(admType)) {
		//			odo_type = "ODE";
		//		}
		//		if (!odo_type.equals(parm.getValue("PRG_ID", 0))
		//				|| !(Operator.getIP().equals(parm.getValue("OPT_TERM", 0)))
		//				|| !(Operator.getID().equals(parm.getValue("OPT_USER", 0)))) {
		//			// this.messageBox(PatTool.getInstance().getLockParmString(pat.getMrNo()));
		//			return false;
		//		}
		return true;
	}

	/**
	 * 打开医疗卡 =====pangben 2011-12-16 flg ：false 没有添加执行的医嘱 true 有添加或删除的医嘱
	 * orderParm 需要操作的医嘱 ：删除 、添加、 修改
	 * 
	 * @return TParm
	 */
	public TParm onOpenCard(TParm orderOldParm, TParm orderParm) {
		if (odo == null) {
			return null;
		}
		TParm unParm = new TParm();
		if (orderOldParm == null) {
			this.messageBox("没有需要操作的医嘱");
			unParm.setData("OP_TYPE", 5);
			return unParm;
		}
		if(orderParm.getValue("OP_FLG").length()>0 && orderParm.getInt("OP_FLG")==5){
			this.messageBox("没有需要操作的医嘱");
			unParm.setData("OP_TYPE", 5);
			return unParm;
		}
		// 准备送入医疗卡接口的数据
		// 判断此次操作的医嘱在数据库中是否已经存在，如果存在,在执行收费操作时，判断医疗卡中金额是否充足
		// 如果金额不足，执行此处方签所收费的医嘱退还医疗卡中
		TParm updateParm=orderParm.getParm("updateParm");
		boolean unFlg = updateOrderParm(updateParm, orderOldParm, unParm);
		TParm parm = new TParm();
		boolean isDelOrder = false;// 执行删除医嘱
		//boolean exeDelOrder = false;// 执行删除医嘱
		String delFlg=orderParm.getValue("DEL_FLG");
		// 如果出现所有医嘱删除也会出现IS_NEW = false 状态 所有需要在执行方法时先查询当前所有医嘱
		// 校验是否发送删除检验检查接口
		if(delFlg.equals("Y")){
			isDelOrder = true;
		}
		orderParm.setData("BUSINESS_TYPE", "ODO");
		parm.setData("CASE_NO",reg.caseNo());
		orderParm.setData("REGION_CODE", Operator.getRegion());
		orderParm.setData("MR_NO", pat.getMrNo());
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("IDNO", pat.getIdNo());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "男" : "女");
		// 送医疗卡，返回医疗卡的回传值
		orderParm.setData("INS_FLG", "N");// 医保卡操作
		orderParm.setData("UN_FLG", unFlg ? "Y" : "N");// 医生修改的医嘱超过医疗卡金额执行的操作
		orderParm.setData("unParm", unParm.getData());// 获得执行修改的医嘱
		if (null != orderOldParm.getValue("OPBEKTFEE_FLG")
				&& orderOldParm.getValue("OPBEKTFEE_FLG").equals("Y")) {
			orderParm.setData("OPBEKTFEE_FLG", "Y");
		}
		//直接收费操作如果有修改的收费医嘱 不能执行取消操作
		if(null == orderOldParm.getValue("OPBEKTFEE_FLG")
				|| orderOldParm.getValue("OPBEKTFEE_FLG").length()<=0){
			if(unFlg)
				orderParm.setData("OPBEKTFEE_FLG", "Y");
		}
		if (resultData.getCount("CASE_NO")>0) {//====pangben 2014-1-20 建行卡校验
			this.messageBox("P0001");
			parm.setData("OP_TYPE", 5);
			return parm;
		}else{
			ektReadParm = EKTIO.getInstance().TXreadEKT();
			if (null == ektReadParm || ektReadParm.getErrCode() < 0
					|| null == ektReadParm.getValue("MR_NO")) {
				parm.setData("OP_TYPE", 5);
				this.messageBox("医疗卡读卡有误。");
				this.setValue("LBL_EKT_MESSAGE", "未读卡");//====pangben 2013-5-3添加读卡
				ekt_lable.setForeground(red);//======yanjing 2013-06-14设置读卡颜色
				return parm;
			}else{
				this.setValue("LBL_EKT_MESSAGE", "已读卡");//====pangben 2013-5-3添加读卡
				ekt_lable.setForeground(green);//======yanjing 2013-06-14设置读卡颜色
			}
		}
		if (!ektReadParm.getValue("MR_NO").equals(getValue("MR_NO"))) {
			this.messageBox("病患信息不符,此医疗卡病患名称为:"
					+ ektReadParm.getValue("PAT_NAME"));
			ektReadParm = null;
			parm.setData("OP_TYPE", 5);
			return parm;
		}
		int type=0;
		//parm.setData("BILL_FLG", "Y");
		orderParm.setData("ektParm", ektReadParm.getData()); // 医疗卡数据
		try {
			boolean isNull = true;
			OpdOrder opdOrder = odo.getOpdOrder();
			String lastFilter = opdOrder.getFilter();
			opdOrder.setFilter("");
			opdOrder.filter();
			for (int i = 0; i < opdOrder.rowCount(); i++) {
				if(opdOrder.getItemString(i, "ORDER_CODE").length() > 0){
					isNull = false;
					break;
				}
			}
			opdOrder.setFilter(lastFilter);
			opdOrder.filter();
			parm = EKTNewIO.getInstance().onOPDAccntClient(orderParm, odo.getCaseNo(),
					this, isNull);
		} catch (Exception e) {
			System.out.println("医生站收费出现问题::::"+e.getMessage());
		}finally{
			type = parm.getInt("OP_TYPE");
			TParm delExeParm=orderParm.getParm("delExeParm");//存在收费删除的医嘱
			if (parm == null || type == 3 || type == -1 || type == 5) {
				if(delExeParm.getCount("ORDER_CODE")>0){
					concelDeleteOrder(delExeParm, orderOldParm, isDelOrder);// 删除医嘱选择取消操作
					ektExeConcel=true;//删除撤销操作
				}
			}
		}

		if (type == 6) {
			this.messageBox("没有需要操作的医嘱");
			parm.setData("OP_TYPE", 5);
			return parm;
		}
		if(null!=parm.getValue("OPD_UN_FLG") && parm.getValue("OPD_UN_FLG").equals("Y")){//修改医嘱操作,金额不足 将此收费交易号的所有医嘱退还回去，变成未收费状态
			orderParm.setData("newParm",parm.getParm("unParm").getData());
		}

		// 得到收费项目
		sendHL7Parm = orderParm.getParm("hl7Parm");
		//hl7Temp(checkParm);
		// 删除数据操作时不使用只有修改医嘱操作时使用
		parm.setData("orderParm", orderParm.getData());// 需要操作的医嘱
		return parm;
	}

	/**
	 * 医疗卡操作撤销删除的医嘱 =====pangben 2012-01-06
	 */
	private void concelDeleteOrder(TParm orderParm, TParm oldOrderParm,
			boolean exeDelOrder) {
		TParm tempParm = new TParm();
		int count = 0;
		// if (!exeDelOrder) {// 医嘱数据不同 不是全部删除的orderParm医嘱 没有这么多数据
		for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
			for (int j = 0; j < oldOrderParm.getCount("ORDER_CODE"); j++) {
				if (oldOrderParm.getValue("SEQ_NO", j).equals(
						orderParm.getValue("SEQ_NO", i))
						&& oldOrderParm.getValue("RX_NO", j).equals(
								orderParm.getValue("RX_NO", i))
						&& !orderParm.getBoolean("BILL_FLG", i)) {
					tempParm.setRowData(count, oldOrderParm, j);
					count++;
				}
			}
		}
		orderParm = tempParm;
		if (orderParm.getCount("ORDER_CODE") > 0) {
			// 判断是否存在修改的医嘱信息
			for (int i = orderParm.getCount("ORDER_CODE") - 1; i >= 0; i--) {
				if (!orderParm.getBoolean("BILL_FLG", i)) {
					orderParm.setData("BILL_FLG", i, "Y");
					orderParm.setData("BILL_DATE", i, SystemTool.getInstance()
							.getDate());
					orderParm.setData("BILL_USER", i, Operator.getID());
				}
			}
			ektDeleteChackOut(orderParm);
			// System.out.println("concleDeleteorder方法入参Parm:::::"+orderParm);
			orderParm.setData("MED_FLG", "Y");// 医疗卡撤销操作不执行添加MED_APPLY 表数据
			TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
					"concleDeleteOrder", orderParm);
			if (result.getErrCode() < 0) {
				System.out.println("医疗卡撤销医嘱操作失败");
			}
		} else {
			System.out.println("没有要执行的医嘱操作");
		}
	}

	/**
	 * 数据对象的值改变事件
	 * 
	 * @param columnName
	 *            String
	 * @param value
	 *            Object
	 */
	public void onSetItemEvent(String columnName, Object value) {
		if (!"MEDI_QTY".equalsIgnoreCase(columnName)
				&& !"TAKE_DAYS".equalsIgnoreCase(columnName)
				&& !"FREQ_CODE".equalsIgnoreCase(columnName))
			return;
		int rxType = odo.getOpdOrder().getItemInt(0, "RX_TYPE");
		String tableName = "", tagName = "";
		switch (rxType) {
		case 1:
			tableName = TABLE_MED;
			tagName = "MED_AMT";
			break;
		case 2:
			tableName = TABLE_CTRL;
			tagName = "CTRL_AMT";
			break;
		case 3:
			tableName = TABLE_CHN;
			tagName = "CHN_AMT";
			break;
		case 4:
			tableName = TABLE_OP;
			tagName = "OP_AMT";
			break;

		}
		this.calculateCash(tableName, tagName);

	}

	/**
	 * 打印病历
	 * 
	 * @return Object
	 */
	public Object onPrintCase() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", odo.getCaseNo());
		parm.setData("MR_NO", odo.getMrNo());
		parm.setData("MR", "TEXT", "病案号：" + odo.getMrNo());
		if (isEng) {
			parm.setData("HOSP_NAME", "TEXT", Operator
					.getHospitalENGFullName());
		} else {
			parm.setData("HOSP_NAME", "TEXT", Operator
					.getHospitalCHNFullName());
		}
		parm.setData("DR_NAME", "TEXT", "医师签字:"
				+ OpdRxSheetTool.getInstance().GetRealRegDr(odo.getCaseNo()));
		parm.setData("REALDEPT_CODE", this.realDeptCode);
		Object obj = new Object();
		if ("O".equals(admType)) {
			obj = this.openPrintDialog(
					"%ROOT%\\config\\prt\\OPD\\OPDCaseSheet1010.jhw", parm,
					false);
			// 加入EMR保存 beign
			this.saveEMR(obj, "门诊病历记录", "EMR020001", "EMR02000106");
			// 加入EMR保存 end
		} else if ("E".equals(admType)) {

			//急诊病人如进入留观后，需操作离院后才能进行打印病历  add by huangtt 20161209 start

			String sql = "SELECT TO_CHAR(OUT_DATE,'YYYY/MM/DD HH24:MI:SS') OUT_DATE  FROM ERD_RECORD WHERE CASE_NO='"+odo.getCaseNo()+"'";
			TParm outParm = new TParm(TJDODBTool.getInstance().select(sql));
			String outDate="";
			if(outParm.getCount() > 0){
				if(outParm.getValue("OUT_DATE", 0).length() == 0){
					onOutReutrn();
					TParm outParm1 = new TParm(TJDODBTool.getInstance().select(sql));
					if(outParm1.getValue("OUT_DATE", 0).length() == 0){
						this.messageBox("请办理离院后在打印急诊病历");
						return obj;
					}else{
						outDate="离院时间："+outParm1.getValue("OUT_DATE", 0);
					}
				}else{
					outDate="离院时间："+outParm.getValue("OUT_DATE", 0);
				}
			}
			parm.setData("OUT_DATE", outDate);

			//急诊病人如进入留观后，需操作离院后才能进行打印病历  add by huangtt 20161209 end

			//==========pangben 2013-7-22 急诊病例打印，接诊时间设置为当前时间，报销使用
			//parm.setData("DATE", StringTool.getString(TypeTool.getTimestamp(SystemTool.getInstance().getDate()),"yyyy/MM/dd"));
			//$----------------modify caoyong 20131219 start----------
//			TParm sparm=this.getParmSeeDrTime(odo.getMrNo(), odo.getCaseNo());
//			if(sparm.getCount()>0){
//				parm.setData("DATE", sparm.getValue("SEEN_DR_TIME",0).substring(0,16).replace("-","/"));
//				parm.setData("ARRIVE_DATE", sparm.getValue("ARRIVE_DATE",0).substring(0,16).replace("-","/"));
//				parm.setData("LEVEL_DESC", sparm.getValue("LEVEL_DESC",0));
//			} 
//			
			// add by wangqing 20170704 start
			String sql11 = " SELECT TO_CHAR(A.TRIAGE_TIME, 'yyyy/MM/dd HH24:MI') TRIAGE_TIME, "
					+ "TO_CHAR (A.ADM_DATE, 'YYYY/MM/DD')||' '||TO_CHAR (A.COME_TIME, 'HH24:MI') AS GATE_TIME, B.LEVEL_DESC FROM "
					+ " ERD_EVALUTION A, REG_ERD_LEVEL B "
					+ " WHERE A.LEVEL_CODE=B.LEVEL_CODE(+) AND A.CASE_NO='"+odo.getCaseNo()+"' ";
			TParm result11 = new TParm(TJDODBTool.getInstance().select(sql11));
			if(result11.getErrCode()<0){
				return result11;
			}
			if(result11.getCount()<=0){
				return result11;
			}
			parm.setData("DATE", result11.getValue("TRIAGE_TIME", 0));
			parm.setData("ARRIVE_DATE", result11.getValue("GATE_TIME", 0));
			parm.setData("LEVEL_DESC", result11.getValue("LEVEL_DESC", 0));
			// add by wangqing 20170704 end
			
			//$----------------modify caoyong 20131219 start----------
			obj = this.openPrintDialog("%ROOT%\\config\\prt\\OPD\\EMG.jhw",
					parm, false);

		}

		return obj;
	}

	/**
	 * 暂存，打印处置、检验检查通知单
	 */
	public void onTempSave() {
		onTempSave(null);
	}

	/**
	 * 保存后即打印处方和病历记录
	 * 
	 * @param parm
	 *            TParm
	 * @param rxType
	 *            String
	 */
	public void onPrintOrder(TParm parm, String rxType) {
		if (parm == null || parm.getCount("RX_NO") < 1) {
			return;
		}
		int count = parm.getCount("RX_NO");
		if (CHN.equalsIgnoreCase(rxType)) {
			for (int i = 0; i < count; i++) {
				// 打印处方签
				TParm inParam = OpdRxSheetTool.getInstance().getOrderPrintParm(
						realDeptCode, rxType, odo, parm.getValue("RX_NO", i),
						"");
				//add by huangtt 20170401 添加收费状态
				inParam.setData("BILL_TYPE", "TEXT", OpdRxSheetTool.getInstance().getBillType(caseNo, parm.getValue("RX_NO", i), ""));

				Object obj = this.openPrintDialog(
						"%ROOT%\\config\\prt\\OPD\\OpdChnOrderSheet.jhw",
						inParam, true);
				// 保存EMR
				String rxNo = parm.getValue("RX_NO", i);
				// 文件名加入处方签号
				this.saveEMR(obj, "中药处方签_" + rxNo, "EMR030002", "EMR03000201");
			}
			return;
		}
		OpdOrder order = odo.getOpdOrder();
		String filterString = order.getFilter();

		List<ArrayList<Object>> saveInfo = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i < count; i++) {
			ArrayList<Object> data = new ArrayList<Object>();

			String rxNo = parm.getValue("RX_NO", i);
			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			TParm inParam = OpdRxSheetTool.getInstance().getOrderPrintParm(
					realDeptCode, rxType, odo, rxNo,
					order.getItemString(0, "PSY_FLG"));

			//add by huangtt 20170401 添加收费状态 
			inParam.setData("BILL_TYPE", "TEXT", OpdRxSheetTool.getInstance().getBillType(caseNo, rxNo, ""));

			if (CTRL.equalsIgnoreCase(rxType)) {
				Object obj = this.openPrintDialog(
						"%ROOT%\\config\\prt\\OPD\\OpdDrugSheet.jhw", inParam,
						true);

				// 保存EMR
				// 文件名加入处方签号
				String fileName = "管制药品处方签_" + rxNo;
				// this.saveEMR(obj, fileName, "EMR030001", "EMR03000102");

				data.add(obj);
				data.add(fileName);
				data.add("EMR030001");
				data.add("EMR03000102");
				saveInfo.add(data);
			} else {
				// =============modify by lim begin
				String rxNo2 = rxNo;
				String caseNo2 = this.caseNo;

				String westsql = "  SELECT   CASE WHEN   OPD_ORDER.BILL_FLG='Y' THEN '√' ELSE '' END||'  '||OPD_ORDER.LINK_NO aa , "
						+ " CASE WHEN SYS_FEE.IS_REMARK = 'Y' THEN OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.ORDER_DESC  END bb , "
						+ " OPD_ORDER.SPECIFICATION cc, "
						+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '皮试' ELSE SYS_PHAROUTE.ROUTE_CHN_DESC  END dd,"
						+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '' ELSE RTRIM(RTRIM(TO_CHAR(OPD_ORDER.MEDI_QTY,'fm9999999990.000'),'0'),'.')||''||A.UNIT_CHN_DESC  END ee,"
						+ " RPAD(SYS_PHAFREQ.FREQ_CHN_DESC, (16-LENGTH(SYS_PHAFREQ.FREQ_CHN_DESC)), ' ')|| OPD_ORDER.TAKE_DAYS FF,"
						+ " CASE WHEN OPD_ORDER.DISPENSE_QTY<1 THEN TO_CHAR(OPD_ORDER.DISPENSE_QTY,'fm9999999990.0') ELSE "
						+ " TO_CHAR(OPD_ORDER.DISPENSE_QTY) END||''|| B.UNIT_CHN_DESC er,"
						//modify by wanglong 20121226
						+ " CASE WHEN OPD_ORDER.RELEASE_FLG = 'Y' THEN '自备  '|| OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.DR_NOTE END gg ,OPD_ORDER.DOSAGE_QTY,OPD_ORDER.OWN_PRICE,OPD_ORDER.DISCOUNT_RATE "
						//modify end
						+ " FROM   OPD_ORDER, SYS_PHAFREQ, SYS_PHAROUTE,SYS_UNIT A, SYS_UNIT B,SYS_FEE "
						+ " WHERE       CASE_NO = '"
						+ caseNo2
						+ "'"
						+ "  AND RX_NO = '"
						+ rxNo2
						+ "'"
						+ " and SYS_PHAROUTE.ROUTE_CODE(+) = OPD_ORDER.ROUTE_CODE "
						+ "  AND SYS_PHAFREQ.FREQ_CODE(+) = OPD_ORDER.FREQ_CODE "
						+ "  AND A.UNIT_CODE(+) =  OPD_ORDER.MEDI_UNIT "
						+ "  AND B.UNIT_CODE(+) =  OPD_ORDER.DISPENSE_UNIT "
						+ "  AND OPD_ORDER.ORDER_CODE = SYS_FEE.ORDER_CODE "
						+ " ORDER BY   LINK_NO, LINKMAIN_FLG DESC, SEQ_NO";
				TParm westResult = new TParm(TJDODBTool.getInstance().select(
						westsql));
				if (westResult.getErrCode() < 0) {
					this.messageBox("E0001");
					return;
				}
				if (westResult.getCount() < 0) {
					this.messageBox("没有处方签数据.");
					return;
				}

				TParm westParm = new TParm();
				double pageAmt2 = 0;
				DecimalFormat df2 = new DecimalFormat("############0.00");
				for (int j = 0; j < westResult.getCount(); j++) {
					westParm.addData("AA", westResult.getData("AA", j));
					westParm.addData("BB", westResult.getData("BB", j));
					westParm.addData("CC", westResult.getData("CC", j));
					westParm.addData("DD", westResult.getData("DD", j));
					westParm.addData("EE", westResult.getData("EE", j));
					westParm.addData("FF", westResult.getData("FF", j));
					westParm.addData("ER", westResult.getData("ER", j));
					westParm.addData("GG", westResult.getData("GG", j));
					pageAmt2 += (westResult.getDouble("DOSAGE_QTY", j)
							* westResult.getDouble("OWN_PRICE", j) 
							* westResult.getDouble("DISCOUNT_RATE", j));// modify by wanglong 20121226
					pageAmt2 = StringTool.round(pageAmt2, 2);// add by wanglong 20121226
					if ((j != 0 && (j + 1) % 5 == 0)
							|| j == westResult.getCount() - 1) {
						westParm.addData("AA", "");
						westParm.addData("BB", "");
						westParm.addData("CC", "");
						westParm.addData("DD", "");
						westParm.addData("EE", "");
						westParm.addData("FF", "处方金额(￥):");
						westParm.addData("ER", df2.format(pageAmt2));
						westParm.addData("GG", "");
						pageAmt2 = 0;
					}
				}
				westParm.setCount(westParm.getCount("AA"));
				westParm.addData("SYSTEM", "COLUMNS", "AA");
				westParm.addData("SYSTEM", "COLUMNS", "BB");
				westParm.addData("SYSTEM", "COLUMNS", "CC");
				westParm.addData("SYSTEM", "COLUMNS", "DD");
				westParm.addData("SYSTEM", "COLUMNS", "EE");
				westParm.addData("SYSTEM", "COLUMNS", "FF");
				westParm.addData("SYSTEM", "COLUMNS", "ER");
				westParm.addData("SYSTEM", "COLUMNS", "GG");

				inParam.setData("ORDER_TABLE", westParm.getData());
				// =============modify by lim end
				Object obj = this.openPrintDialog(
						"%ROOT%\\config\\prt\\OPD\\OpdOrderSheet.jhw", inParam,
						true);
				// // 保存EMR
				// // 文件名加入处方签号
				String fileName = "西药处方签_" + rxNo;
				// this.saveEMR(obj, fileName, "EMR030001", "EMR03000101");
				data.add(obj);
				data.add(fileName);
				data.add("EMR030001");
				data.add("EMR03000101");
				saveInfo.add(data);
			}
		}

		order.setFilter(filterString);
		order.filter();
		for (Iterator iterator = saveInfo.iterator(); iterator.hasNext();) {
			ArrayList<Object> arrayList = (ArrayList<Object>) iterator.next();
			this.saveEMR(arrayList.get(0), (String) arrayList.get(1),
					(String) arrayList.get(2), (String) arrayList.get(3));
		}
	}

	/**
	 * 暂存打印处置、检验检查通知单
	 * 
	 * @param parm
	 *            TParm
	 */
	public void onPrintExa(TParm parm) {
		TParm inParam = new TParm();
		inParam = OpdRxSheetTool.getInstance().getExaPrintParm(parm,
				realDeptCode, EXA, odo);
		//		System.out.println("inParam---"+inParam);
		// modify by lim 2012/02/23 begin
		String rxNo = inParam.getValue("RX_NO");
		String caseNo = this.caseNo;
		//modify by wanglong 20121226
		String sql = "SELECT  A.MED_APPLY_NO,A.ORDER_CODE,CASE WHEN A.BILL_FLG='Y' THEN '√' ELSE '' END AS BILL_FLG,  C.DEPT_CHN_DESC,F.AR_AMT, "
				+" A.ORDER_DESC||CASE WHEN A.DR_NOTE IS NOT NULL THEN '/'||A.DR_NOTE ELSE '' END AS ORDER_DESC,"
				+" A.MEDI_QTY,CASE WHEN A.URGENT_FLG='Y' THEN '√' ELSE '' END AS URGENT_FLG,B.DESCRIPTION,A.DISCOUNT_RATE " 
				//modify end
				+ " FROM  OPD_ORDER A,SYS_FEE B,SYS_DEPT C, (SELECT   RX_NO, ORDERSET_GROUP_NO, SUM (AR_AMT) AS AR_AMT, CASE_NO "
				+ "FROM OPD_ORDER "
				+ "WHERE CASE_NO = '"+caseNo+"' "
				+ "AND RX_NO IN ("+rxNo+") "
				+ "AND SETMAIN_FLG = 'N' "
				+ "AND ORDERSET_GROUP_NO IN ( "
				+ "  SELECT A.ORDERSET_GROUP_NO "
				+ "    FROM OPD_ORDER A, SYS_FEE B, SYS_DEPT C "
				+ "    WHERE A.CASE_NO = '"+caseNo+"' "
				+ "      AND RX_NO IN ("+rxNo+") "
				+ "       AND A.ORDER_CODE = B.ORDER_CODE "
				+ "       AND C.DEPT_CODE = A.EXEC_DEPT_CODE "
				+ "       AND A.CAT1_TYPE = 'LIS') "
				+ " GROUP BY RX_NO, ORDERSET_GROUP_NO, CASE_NO) F"
				+ " WHERE A.CASE_NO='"+ caseNo+ "' AND A.RX_NO IN ("+ rxNo
				+ ") AND A.SETMAIN_FLG='Y' AND A.ORDER_CODE=B.ORDER_CODE "
				+ "  AND A.CASE_NO = F.CASE_NO AND A.RX_NO = F.RX_NO AND A.ORDERSET_GROUP_NO = F.ORDERSET_GROUP_NO AND C.DEPT_CODE=A.EXEC_DEPT_CODE AND A.CAT1_TYPE='LIS'";
		//		System.out.println("sql::::::::"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		//===pangben 2013-8-8 检查不打印
		//		String sql2 = sql.replace("'LIS'", "'RIS'");
		//
		//		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		if (result.getErrCode() < 0) {
			return;
		}
		if (result.getCount() < 0) {
			return;
		}

		String billType = "已收费";
		for (int i = 0; i < result.getCount(); i++) {
			if(result.getValue("BILL_FLG", i).length() == 0){
				billType ="未收费";
				break;
			}
		}

		inParam.setData("BILL_TYPE", "TEXT", billType); //add by huangtt 20170401处方签显示已收费未收费状态
		//        System.out.println("billType----"+billType);

		//		if (result.getErrCode() < 0 || result2.getErrCode() < 0) {
		//			return;
		//		}
		//		if (result.getCount() < 0 && result2.getCount() < 0) {
		//			return;
		//		}
		TParm data2 = new TParm();
		boolean flg = false;
		int blankRow = 0;
		double pageAmt = 0;
		DecimalFormat df = new DecimalFormat("############0.00");
		if (result.getCount() > 0) {
			int pageCount = 11;
			data2.addData("BILL_FLG", "");
			data2.addData("DEPT_CHN_DESC", "检验通知单");
			data2.addData("ORDER_DESC", "");
			data2.addData("MEDI_QTY", "");
			data2.addData("URGENT_FLG", "");
			data2.addData("DESCRIPTION", "");
			data2.addData("MED_APPLY_NO", "");
			for (int i = 0; i < result.getCount(); i++) {
				data2.addData("BILL_FLG", result.getData("BILL_FLG", i));
				data2.addData("DEPT_CHN_DESC", result.getData("DEPT_CHN_DESC",
						i));
				data2.addData("ORDER_DESC", result.getData("ORDER_DESC", i));
				data2.addData("MEDI_QTY", result.getData("MEDI_QTY", i));
				data2.addData("URGENT_FLG", result.getData("URGENT_FLG", i));
				data2.addData("DESCRIPTION", result.getData("DESCRIPTION", i));
				data2
				.addData("MED_APPLY_NO", result.getData("MED_APPLY_NO",
						i));
				// 累计

				//				pageAmt += (result.getDouble("MEDI_QTY", i)
				//						* this.getEveryAmt(result.getValue("ORDER_CODE", i)) 
				//						* result.getDouble("DISCOUNT_RATE", i));// modify by wanglong 20121226
				pageAmt += StringTool.round(result.getDouble("AR_AMT", i), 2);// pangben 2013-4-17
				// TODO:“###########”处需要被每页计算的金额替代.用获得的数量*getEveryAmt(ORDERCODE)计算出每条记录的金额。
				int num = i + blankRow + 1 + 1;// 行数（i）+
				// 空白行(blankRow)+第一行检验通知单(1)+1
				if (!flg) {// 第一页
					if (i == 8 || i == (result.getCount() - 1)) {
						data2.addData("BILL_FLG", "");
						data2.addData("DEPT_CHN_DESC", "");
						data2.addData("ORDER_DESC", "");
						data2.addData("MEDI_QTY", "");
						data2.addData("URGENT_FLG", "");
						data2.addData("DESCRIPTION", "处方金额:");
						data2.addData("MED_APPLY_NO", df.format(pageAmt));
						flg = true;
						blankRow++;
						pageAmt = 0;
					}

				} else {// 其他页.//第11行显示金额
					if (i == result.getCount() - 1) {
						data2.addData("BILL_FLG", "");
						data2.addData("DEPT_CHN_DESC", "");
						data2.addData("ORDER_DESC", "");
						data2.addData("MEDI_QTY", "");
						data2.addData("URGENT_FLG", "");
						data2.addData("DESCRIPTION", "处方金额:");
						data2.addData("MED_APPLY_NO", df.format(pageAmt));
						blankRow++;
						pageAmt = 0;
					} else if (i != result.getCount() - 1
							&& ((num % 11) + 1) == 11) {
						data2.addData("BILL_FLG", "");
						data2.addData("DEPT_CHN_DESC", "");
						data2.addData("ORDER_DESC", "");
						data2.addData("MEDI_QTY", "");
						data2.addData("URGENT_FLG", "");
						data2.addData("DESCRIPTION", "处方金额:");
						data2.addData("MED_APPLY_NO", df.format(pageAmt));
						blankRow++;
						pageAmt = 0;
					}
				}
			}
			int resultLen1 = result.getCount() + 1 + blankRow;
			int len = (resultLen1 <= pageCount) ? (pageCount - resultLen1)
					: ((resultLen1 % pageCount == 0) ? 0
							: (((resultLen1 / pageCount) + 1) * pageCount - resultLen1));

			for (int i = 1; i <= len; i++) {
				data2.addData("BILL_FLG", "");
				data2.addData("DEPT_CHN_DESC", "");
				data2.addData("ORDER_DESC", "");
				data2.addData("MEDI_QTY", "");
				data2.addData("URGENT_FLG", "");
				data2.addData("DESCRIPTION", "");
				data2.addData("MED_APPLY_NO", "");
			}
		}
		//		if (result2.getCount() > 0) {
		//			data2.addData("BILL_FLG", "");
		//			data2.addData("DEPT_CHN_DESC", "检查通知单");
		//			data2.addData("ORDER_DESC", "");
		//			data2.addData("MEDI_QTY", "");
		//			data2.addData("URGENT_FLG", "");
		//			data2.addData("DESCRIPTION", "");
		//			data2.addData("MED_APPLY_NO", "");
		//		}
		blankRow = 0;
		flg = false;
		//		for (int i = 0; i < result2.getCount(); i++) {
		//			data2.addData("BILL_FLG", result2.getData("BILL_FLG", i));
		//			data2.addData("DEPT_CHN_DESC", result2.getData("DEPT_CHN_DESC", i));
		//			data2.addData("ORDER_DESC", result2.getData("ORDER_DESC", i));
		//			data2.addData("MEDI_QTY", result2.getData("MEDI_QTY", i));
		//			data2.addData("URGENT_FLG", result2.getData("URGENT_FLG", i));
		//			data2.addData("DESCRIPTION", result2.getData("DESCRIPTION", i));
		//			data2.addData("MED_APPLY_NO", result2.getData("MED_APPLY_NO", i));
		////			pageAmt += (result2.getDouble("MEDI_QTY", i)
		////					* this.getEveryAmt(result2.getValue("ORDER_CODE", i)) 
		////					* result2.getDouble("DISCOUNT_RATE", i));//modify by wanglong 20121226
		//			pageAmt += StringTool.round(result2.getDouble("AR_AMT", i), 2);//add by wanglong 20121226
		//			int num = i + blankRow + 1 + 1;// 行数（i）+ 空白行(blankRow)+第一行检验通知单(1)+1
		//			if (!flg) {// 第一页
		//				if (i == 6 || i == (result2.getCount() - 1)) {
		//					data2.addData("BILL_FLG", "");
		//					data2.addData("DEPT_CHN_DESC", "");
		//					data2.addData("ORDER_DESC", "");
		//					data2.addData("MEDI_QTY", "");
		//					data2.addData("URGENT_FLG", "");
		//					data2.addData("DESCRIPTION", "处方金额:");
		//					data2.addData("MED_APPLY_NO", df.format(pageAmt));
		//					flg = true;
		//					blankRow++;
		//					pageAmt = 0;
		//				}
		//
		//			} else {// 其他页.//第9行显示金额
		//				if (i == result2.getCount() - 1) {
		//					data2.addData("BILL_FLG", "");
		//					data2.addData("DEPT_CHN_DESC", "");
		//					data2.addData("ORDER_DESC", "");
		//					data2.addData("MEDI_QTY", "");
		//					data2.addData("URGENT_FLG", "");
		//					data2.addData("DESCRIPTION", "处方金额:");
		//					data2.addData("MED_APPLY_NO", df.format(pageAmt));
		//					blankRow++;
		//					pageAmt = 0;
		//				} else if (i != result2.getCount() - 1 && ((num % 9) + 1) == 9) {
		//					data2.addData("BILL_FLG", "");
		//					data2.addData("DEPT_CHN_DESC", "");
		//					data2.addData("ORDER_DESC", "");
		//					data2.addData("MEDI_QTY", "");
		//					data2.addData("URGENT_FLG", "");
		//					data2.addData("DESCRIPTION", "处方金额:");
		//					data2.addData("MED_APPLY_NO", df.format(pageAmt));
		//					blankRow++;
		//					pageAmt = 0;
		//				}
		//			}
		//		}
		data2.setCount(data2.getCount("ORDER_DESC"));
		data2.addData("SYSTEM", "COLUMNS", "BILL_FLG");
		data2.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		data2.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		data2.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		data2.addData("SYSTEM", "COLUMNS", "URGENT_FLG");
		data2.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		data2.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");
		inParam.setData("ORDER_TABLE", data2.getData());
		// modify by lim 2012/02/23 begin
		DText text = (DText) this.openPrintDialog(
				"%ROOT%\\config\\prt\\OPD\\OpdNewExaSheet.jhw", inParam, true);
		String rx_no = inParam.getValue("RX_NO");
		// 加入处方签号
		String fileName = "检验检查通知单_" + rx_no;
		this.saveEMR(text, fileName, "EMR040001", "EMR04000142");
	}

	private double getEveryAmt(String opdOrderCode) {
		String sql = "SELECT SUM(O.DOSAGE_QTY * F.OWN_PRICE) AS AMT FROM SYS_ORDERSETDETAIL O,SYS_FEE F WHERE O.ORDER_CODE = F.ORDER_CODE AND O.ORDERSET_CODE = '"
				+ opdOrderCode + "'";
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql));
		if (result2.getErrCode() < 0) {
			messageBox("单页处方金额计算错误.");
			return 0;
		}
		if (result2.getCount() <= 0) {
			return 0;
		}
		return result2.getDouble("AMT", 0);
	}

	/**
	 * 暂存打印处置、检验检查通知单
	 * 
	 * @param parm
	 *            TParm
	 */
	public void onPrintOp(TParm parm) {
		TParm inParam = new TParm();
		inParam = OpdRxSheetTool.getInstance().getExaPrintParm(parm,
				realDeptCode, OP, odo);
		// modify by liming 2012/02/23 begin
		String rxNo1 = inParam.getValue("RX_NO");
		String caseNo1 = this.caseNo;
		inParam.setData("BAR_CODE","TEXT",rxNo1.split(",")[0].replaceAll("'", ""));//处置为了和 补打统一这里都取 处方号 作为条码号  modify by huangjw 20150529 
		TParm dataParm = new TParm();
		if (rxNo1 != null && rxNo1.trim().length() > 0) {
			//modify by wanglong 20121226
			String sql = "SELECT   A.AR_AMT,A.ORDER_CODE,A.DOSAGE_QTY,A.OWN_PRICE,CASE WHEN A.BILL_FLG='Y' THEN '√' ELSE '' END AS BILL_FLG,  C.DEPT_CHN_DESC,  A.ORDER_DESC||CASE WHEN A.DR_NOTE IS NOT NULL THEN '/'||A.DR_NOTE ELSE '' END AS ORDER_DESC, A.MEDI_QTY,CASE WHEN A.URGENT_FLG='Y' THEN '√' ELSE '' END AS URGENT_FLG,B.DESCRIPTION,A.DISCOUNT_RATE,B.IS_REMARK,A.DR_NOTE"
					//modify end
					+ "   FROM  OPD_ORDER A,SYS_FEE B,SYS_DEPT C"
					+ "	WHERE A.CASE_NO='"
					+ caseNo1
					+ "' AND A.RX_NO IN ("
					+ rxNo1
					+ ") AND A.SETMAIN_FLG='Y' AND A.ORDER_CODE=B.ORDER_CODE AND C.DEPT_CODE=A.EXEC_DEPT_CODE";
			if ("5".equalsIgnoreCase(rxType)) {
				dataParm = new TParm(TJDODBTool.getInstance().select(sql));
			} else {
				//modify by wanglong 20121226
				sql = "SELECT   A.AR_AMT,A.ORDER_CODE,A.DOSAGE_QTY,A.OWN_PRICE,CASE WHEN A.BILL_FLG='Y' THEN '√' ELSE '' END AS BILL_FLG, C.DEPT_CHN_DESC,  A.ORDER_DESC||CASE WHEN A.SPECIFICATION IS NOT NULL THEN '/'||A.SPECIFICATION ELSE '' END AS ORDER_DESC,  A.MEDI_QTY,CASE WHEN A.URGENT_FLG='Y' THEN '√' ELSE '' END AS URGENT_FLG,B.DESCRIPTION,A.DISCOUNT_RATE,B.IS_REMARK,A.DR_NOTE"
						//modify end
						+ "   FROM  OPD_ORDER A,SYS_FEE B,SYS_DEPT C"
						+ "	WHERE A.CASE_NO='"
						+ caseNo1
						+ "' AND A.RX_NO IN ("
						+ rxNo1
						+ ") AND A.ORDER_CODE=B.ORDER_CODE AND C.DEPT_CODE=A.EXEC_DEPT_CODE AND (A.ORDERSET_CODE IS NULL OR A.SETMAIN_FLG ='Y')";
				dataParm = new TParm(TJDODBTool.getInstance().select(sql));
			}
		}
		if (dataParm.getErrCode() < 0) {
			this.messageBox("E0001");
			return;
		}
		if (dataParm.getCount() < 0) {
			this.messageBox("没有需要打印的处置通知单数据.");
			return;
		}

		String billType = "已收费";
		for (int i = 0; i < dataParm.getCount(); i++) {
			if(dataParm.getValue("BILL_FLG", i).length() == 0){
				billType ="未收费";
				break;
			}
		}

		inParam.setData("BILL_TYPE", "TEXT", billType); //add by huangtt 20170401处方签显示已收费未收费状态


		TParm myParm = new TParm();
		boolean flg1 = false;
		int blankRow1 = 0;
		double pageAmt1 = 0;
		DecimalFormat df1 = new DecimalFormat("############0.00");
		for (int i = 0; i < dataParm.getCount(); i++) {
			String orderDesc = dataParm.getValue("ORDER_DESC", i);
			if (orderDesc.length() <= 29) {
				StringBuilder temp = new StringBuilder();
				for (int j = 1; j <= 58 - orderDesc.length(); j++) {
					temp.append(" ");
				}
				orderDesc = dataParm.getValue("ORDER_DESC", i)
						+ temp.toString();
			}
			if (dataParm.getBoolean("IS_REMARK", i)
					&& !dataParm.getValue("DR_NOTE", i).equals("")) {//wanglong add 20150521
				orderDesc = dataParm.getValue("DR_NOTE", i);
			}
			myParm.addData("BILL_FLG", dataParm.getData("BILL_FLG", i));
			myParm.addData("DEPT_CHN_DESC", dataParm
					.getData("DEPT_CHN_DESC", i));
			myParm.addData("ORDER_DESC", orderDesc);
			myParm.addData("MEDI_QTY", dataParm.getData("MEDI_QTY", i));
			myParm.addData("URGENT_FLG", dataParm.getData("URGENT_FLG", i));
			myParm.addData("DESCRIPTION", dataParm.getData("DESCRIPTION", i));
			// 累计
			//			pageAmt1 += (dataParm.getDouble("DOSAGE_QTY", i)
			//					* dataParm.getDouble("OWN_PRICE", i) 
			//					* dataParm.getDouble("DISCOUNT_RATE", i));//modify by wanglong 20121226
			pageAmt1 += dataParm.getDouble("AR_AMT", i);//mofify by huangjw 20150211
			pageAmt1 = StringTool.round(pageAmt1, 2);//add by wanglong 20121226
			// TODO:“###########”处需要被每页计算的金额替代.用获得的数量*getEveryAmt(ORDERCODE)计算出每条记录的金额。
			int num = i + blankRow1 + 1;// 行数（i）+ 空白行(blankRow)+1
			if (!flg1) {// 第一页
				if (i == 4 || i == (dataParm.getCount() - 1)) {
					myParm.addData("BILL_FLG", "");
					myParm.addData("DEPT_CHN_DESC", "");
					myParm.addData("ORDER_DESC", "");
					myParm.addData("MEDI_QTY", "");
					myParm.addData("URGENT_FLG", "处方金额:");
					myParm.addData("DESCRIPTION", df1.format(pageAmt1));
					flg1 = true;
					blankRow1++;
					pageAmt1 = 0;
				}

			} else {// 其他页.//第5行显示金额
				if (i == dataParm.getCount() - 1) {
					myParm.addData("BILL_FLG", "");
					myParm.addData("DEPT_CHN_DESC", "");
					myParm.addData("ORDER_DESC", "");
					myParm.addData("MEDI_QTY", "");
					myParm.addData("URGENT_FLG", "处方金额:");
					myParm.addData("DESCRIPTION", df1.format(pageAmt1));
					blankRow1++;
					pageAmt1 = 0;
				} else if (i != dataParm.getCount() - 1 && ((num % 6) + 1) == 6) {
					myParm.addData("BILL_FLG", "");
					myParm.addData("DEPT_CHN_DESC", "");
					myParm.addData("ORDER_DESC", "");
					myParm.addData("MEDI_QTY", "");
					myParm.addData("URGENT_FLG", "处方金额:");
					myParm.addData("DESCRIPTION", df1.format(pageAmt1));
					blankRow1++;
					pageAmt1 = 0;
				}
			}
		}
		myParm.setCount(myParm.getCount("ORDER_DESC"));
		myParm.addData("SYSTEM", "COLUMNS", "BILL_FLG");
		myParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		myParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		myParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		myParm.addData("SYSTEM", "COLUMNS", "URGENT_FLG");
		myParm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		inParam.setData("ORDER_TABLE", myParm.getData());
		// modify by liming 2012/02/23 end

		DText text = (DText) this.openPrintDialog(
				"%ROOT%\\config\\prt\\OPD\\OpdNewHandleSheet.jhw", inParam,
				true);
		// 加入处方签号
		String fileName = "处置通知单_" + rxNo1;
		this.saveEMR(text, fileName, "EMR040002", "EMR04000203");
	}

	/**
	 * 调用补打界面
	 */
	public void onCaseSheet() {
		TParm inParm = new TParm();
		if (odo == null || pat == null)
			return;
		inParm.setData("MR_NO", odo.getMrNo());
		inParm.setData("CASE_NO", odo.getCaseNo());
		inParm.setData("DEPT_CODE", realDeptCode);
		if ("en".equals(this.getLanguage())) // 判断是否是英文界面
			inParm.setData("PAT_NAME", pat.getName1());
		else
			inParm.setData("PAT_NAME", pat.getName());
		inParm.setData("OPD_ORDER", odo.getOpdOrder());
		inParm.setData("ADM_DATE", reg.getAdmDate());
		inParm.setData("ODO", odo);
		int[] mainDiag = new int[1];
		if (odo.getDiagrec().haveMainDiag(mainDiag)) {
			String icdCode = odo.getDiagrec().getItemString(mainDiag[0],
					"ICD_CODE");
			inParm.setData("ICD_CODE", icdCode);
			inParm.setData("ICD_DESC", odo.getDiagrec().getIcdDesc(icdCode,
					this.getLanguage()));
		}
		this.openDialog("%ROOT%\\config\\opd\\ODOCaseSheet.x", inParm, false);
	}

	/**
	 * 保存主诉客诉
	 */
	public void saveSubjrec() {
		subjectHasSaved(); //add by huangtt 20151114
		odo.getSubjrec().setActive(0, true);
		odo.getSubjrec().setItem(0, "SUBJ_TEXT", word.getCaptureValue("SUB"));
		odo.getSubjrec().setItem(0, "OBJ_TEXT", word.getCaptureValue("OBJ"));
		odo.getSubjrec()
		.setItem(0, "PHYSEXAM_REC", word.getCaptureValue("PHY"));
		odo.getSubjrec().setItem(0, "PROPOSAL",
				word.getCaptureValue("PROPOSAL"));
		odo.getSubjrec().setItem(0, "EXA_RESULT",
				word.getCaptureValue("EXA_RESULT"));
		if ("N".equalsIgnoreCase(odo.getRegPatAdm().getItemString(0,
				"SEE_DR_FLG"))) {
			TParm parm = EmrUtil.getInstance().saveGSFile(odo.getMrNo(),
					odo.getCaseNo(), saveFiles[2], saveFiles[1]);
			if (parm.getErrCode() < 0) {
				this.messageBox("E0066");
				return;
			}
			String path = parm.getValue("PATH");
			String fileName = parm.getValue("FILENAME");
			word.setMessageBoxSwitch(false);
			word.onSaveAs(path, fileName, 3);
			saveFiles = EmrUtil.getInstance().getGSFile(odo.getCaseNo());
		} else {
			if (this.saveFiles != null) {
				word.setMessageBoxSwitch(false);
				word.onSaveAs(saveFiles[0], saveFiles[1], 3);
			}
			word.setCanEdit(true);
		}

	}

	/**
	 * 保存reg对象
	 */
	public void saveRegInfo() {
		PatInfo patInfo = odo.getPatInfo();
		RegPatAdm regPatAdm = odo.getRegPatAdm();
		patInfo.setActive(0, true);
		regPatAdm.setActive(0, true);
		patInfo.setItem(0, "PAT1_CODE", this.getValue("PAT1_CODE"));
		patInfo.setItem(0, "PAT2_CODE", this.getValue("PAT2_CODE"));
		patInfo.setItem(0, "PAT3_CODE", this.getValue("PAT3_CODE"));
		patInfo.setItem(0, "PREMATURE_FLG", this.getValue("PREMATURE_FLG"));
		patInfo.setItem(0, "HANDICAP_FLG", this.getValue("HANDICAP_FLG"));
		patInfo.setItem(0, "LMP_DATE", this.getValue("LMP_DATE"));
		patInfo.setItem(0, "BREASTFEED_STARTDATE", this
				.getValue("BREASTFEED_STARTDATE"));
		patInfo.setItem(0, "BREASTFEED_ENDDATE", this
				.getValue("BREASTFEED_ENDDATE"));
		String nowValue = familyWord.getCaptureValue("FAMILY_HISTORY");
		String oldValue = patInfo.getItemString(0, "FAMILY_HISTORY");
		if (!nowValue.equalsIgnoreCase(oldValue)) {
			// 2:未保存过
			if ("2".equalsIgnoreCase(this.familyHisFiles[2])) {
				familyWord.setMessageBoxSwitch(false);
				TParm familyParm = EmrUtil.getInstance()
						.getFamilyHistorySavePath(odo.getCaseNo(),
								odo.getMrNo(), familyHisFiles[3],
								familyHisFiles[1]);
				familyWord.onSaveAs(familyParm.getValue("PATH"), familyParm
						.getValue("FILENAME"), 3);
				familyWord.setCanEdit(true);
			}
			// 3:保存过
			else {
				familyWord.setMessageBoxSwitch(false);
				familyWord.onSaveAs(familyHisFiles[0], familyHisFiles[1], 3);
				familyWord.setCanEdit(true);
			}
		}
		patInfo.setItem(0, "FAMILY_HISTORY", this.familyWord
				.getCaptureValue("FAMILY_HISTORY"));
		regPatAdm.setItem(0, "CTZ1_CODE", this.getValue("CTZ1_CODE"));
		regPatAdm.setItem(0, "CTZ2_CODE", this.getValue("CTZ2_CODE"));
		regPatAdm.setItem(0, "CTZ3_CODE", this.getValue("CTZ3_CODE"));
		regPatAdm.setItem(0, "WEIGHT", TypeTool.getDouble(this
				.getValue("WEIGHT")));
	}

	/**
	 * 
	 * @param rxKind
	 *            String 处方类型
	 * @param tableName
	 *            String TABLE名
	 */
	public void onEditAll(String rxKind, String tableName) {
		TParm msg = (TParm) this
				.openDialog("%ROOT%\\config\\opd\\OPDEditAll.x");
		if (msg == null) {
			return;
		}
		// 执行医疗卡操作，判断是否已经可以使用医疗卡
		if (null == ektReadParm) {
			ektReadParm = EKTIO.getInstance().TXreadEKT();
			if (ektReadParm.getErrCode() < 0) {
				this.messageBox("未确认身份,不可删除医嘱");
				return;
			}
		}
		String freqCode = msg.getValue("FREQ_CODE");
		String routeCode = msg.getValue("ROUTE_CODE");
		double mediQty = msg.getDouble("MEDI_QTY");
		double takeDays = msg.getDouble("TAKE_DAYS");
		TTable table = (TTable) this.getComponent(tableName);
		OpdOrder order = (OpdOrder) table.getDataStore();
		// order.setFilter("RX_NO='" +rxNo+
		// "'");
		// order.filter();

		for (int i = 0; i < order.rowCount(); i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_CODE"))) {
				continue;
			}
			if ("E".equals(order.getItemString(i, "BILL_TYPE"))
					&& (StringTool.getBoolean(order
							.getItemString(i, "EXEC_FLG"))
							|| StringTool.getBoolean(order.getItemString(i,
									"PRINT_FLG")) || StringTool
							.getBoolean(order.getItemString(i, "BILL_FLG")))) {
				this.messageBox("E0055"); // 已计费医嘱不能删除
				return;
			} else {
				if (StringTool.getBoolean(order.getItemString(i, "BILL_FLG"))
						&& !"E".equals(order.getItemString(i, "BILL_TYPE"))) {
					this.messageBox("E0055"); // 已计费医嘱不能删除
					return;
				}
			}

			if (!StringUtil.isNullString(freqCode)) {
				order.setItem(i, "FREQ_CODE", freqCode);
			}
			if (!StringUtil.isNullString(routeCode)) {
				order.setItem(i, "ROUTE_CODE", routeCode);
			}
			if (mediQty > 0.0) {
				order.setItem(i, "MEDI_QTY", mediQty);
			}
			if (takeDays > 0) {
				order.setItem(i, "TAKE_DAYS", takeDays);
			}
		}
		table.setDSValue();
	}

	/**
	 * 合理用药按钮
	 */
	public void onResonablemed() {
		if (!passIsReady) {
			this.messageBox("E0067");
			return;
		}
		if (!initReasonbledMed()) {
			this.messageBox("合理用药初始化失败，此功能不能使用！");
			return;
		}
		if (odo == null) {
			this.messageBox("E0024");
			return;
		}
		if (((TTabbedPane) this.getComponent("TTABPANELORDER"))
				.getSelectedIndex() == 0
				|| ((TTabbedPane) this.getComponent("TTABPANELORDER"))
				.getSelectedIndex() == 1) {
			this.messageBox("不适用合理用药功能!");
			return;
		}
		TParm parm = setopdRecipeInfo();
		if (parm.getCount("ERR") > 0) {
			this.messageBox("E0068");
			return;
		}
		if (parm.getCount("ORDER_CODE") < 0) {
			this.messageBox("未检测到药品！");
			return;
		}
		// 门诊使用
		PassDriver.PassDoCommand(3);
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			PassDriver.PassGetWarn1(parm.getValue("SEQ", i) + "");
		}
	}

	/**
	 * 
	 * 自动检测合理用药
	 * 
	 */
	private boolean checkDrugAuto() {
		if (!passIsReady) {
			return true;
		}
		if (!initReasonbledMed()) {
			return true;
		}
		long time = System.currentTimeMillis();
		PassTool.getInstance().setPatientInfo(odo.getCaseNo());
		PassTool.getInstance().setAllergenInfo(odo.getMrNo());
		PassTool.getInstance().setMedCond(odo.getCaseNo());
		TParm parm = setopdRecipeInfoAuto();
		if (!isWarn(parm)) {
			return true;
		}
		if (enforcementFlg) {
			return false;
		}
		if (JOptionPane.showConfirmDialog(null, "有药品使用不合理,是否存档?", "信息",
				JOptionPane.YES_NO_OPTION) != 0) {
			return false;
		}
		return true;
	}

	/**
	 * 门诊医生检核药品自动
	 * 
	 * @return TParm
	 */
	public TParm setopdRecipeInfoAuto() {
		TParm parm = setopdRecipeInfo();
		PassDriver.PassDoCommand(33);
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			result.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
			result.addData("FLG", PassDriver.PassGetWarn1(parm.getValue("SEQ",
					i)));
		}
		return result;

	}

	/**
	 * 传入门诊医生药品信息
	 * 
	 * @return TParm
	 */
	public TParm setopdRecipeInfo() {
		TParm parm = new TParm();
		int j;
		OpdOrder order = odo.getOpdOrder();
		TTabbedPane tabPanel = (TTabbedPane) this
				.getComponent("TTABPANELORDER");
		switch (tabPanel.getSelectedIndex()) {
		case 2:
			// 西药
			order.setFilter("RX_TYPE='" + MED + "'");
			order.filter();
			break;
		case 3:

			// 中药
			order.setFilter("RX_TYPE='" + CHN + "'");
			order.filter();
			break;
		case 4:

			// 管制药
			order.setFilter("RX_TYPE='" + CTRL + "'");
			order.filter();
			break;
		default:
			break;
		}
		String[] orderInfo;
		ReasonableMedUtil resonableMed = new ReasonableMedUtil();
		for (int i = 0; i < order.rowCount(); i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_DESC"))) {
				continue;
			}
			orderInfo = resonableMed.getOrderInfo(order, i);
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
		return parm;
	}

	/**
	 * 删除行数据
	 * 
	 * @param row
	 */
	private void deleteorderAuto(int row) {
		TTable table = null;
		OpdOrder order = odo.getOpdOrder();
		String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
		String rx_no = "";
		TTabbedPane tabPanel = (TTabbedPane) this
				.getComponent("TTABPANELORDER");
		switch (tabPanel.getSelectedIndex()) {
		// 检验检查
		case 0:
			table = (TTable) this.getComponent("TABLEEXA");
			rx_no = this.getValueString("EXA_RX");
			order.deleteRow(row, buff);
			table.setFilter("RX_TYPE='" + EXA
					+ "' AND HIDE_FLG='N' AND RX_NO='" + rx_no + "'");
			table.filter();
			table.setDSValue();
			// 处置
		case 1:
			table = (TTable) this.getComponent("TABLEOP");
			rx_no = this.getValueString("OP_RX");
			order.deleteRow(row, buff);
			table.setFilter("RX_TYPE='" + OP + "' AND HIDE_FLG='N' AND RX_NO='"
					+ rx_no + "'");
			table.filter();
			table.setDSValue();
		case 2:

			// 西药
			table = (TTable) this.getComponent("TABLEMED");
			rx_no = this.getValueString("MED_RX");
			order.deleteRow(row);
			order.setFilter("RX_TYPE='" + MED
					+ "' AND HIDE_FLG='N' AND RX_NO='" + rx_no + "'");
			table.filter();
			table.setDSValue();
			break;
		case 3:

			// 中药
			table = (TTable) this.getComponent("TABLECHN");
			rx_no = this.getValueString("CHN_RX");
			order.setFilter("RX_TYPE='" + CHN + "'");
			order.filter();
			order.deleteRow(row);
			order.newOrder(CHN, rx_no);
			int totRow = order.rowCount();
			if (!StringUtil.isNullString(order.getItemString(totRow - 1,
					"ORDER_CODE"))
					|| totRow % 4 != 0 || totRow < 1) {
				for (int a = 0; a < 4 - totRow % 4; a++) {
					order.setItem(a, "PHA_TYPE", "G");
				}
			}
			TParm parm = odo.getOpdOrder().getBuffer(order.PRIMARY);
			TParm tableParm = new TParm();
			for (int j = 0; j < parm.getCount(); j++) {
				int idx = j % 4 + 1;
				tableParm.setData("ORDER_DESC" + idx, 1, parm.getValue(
						"ORDER_DESC", j));
				tableParm.setData("MEDI_QTY" + idx, 1, parm.getDouble(
						"MEDI_QTY", j));
				tableParm.setData("DCTEXCEP_CODE" + idx, 1, parm.getValue(
						"DCTEXCEP_CODE", j));
			}
			tableParm.setCount(1);
			callFunction("UI|TABLECHN|setParmValue", tableParm);
			break;
		case 4:

			// 管制药
			table = (TTable) this.getComponent("TABLECTRL");
			rx_no = this.getValueString("CTRL_RX");
			order.setFilter("RX_TYPE='" + CTRL + "'");
			order.deleteRow(row, buff);
			table.filter();
			table.setDSValue();
			break;
		default:
			break;
		}
		return;
	}

	/**
	 * isWarn
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
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

	/**
	 * getWarn
	 * 
	 * @param flg
	 *            int
	 * @return boolean
	 */
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
	 * 右击MENU弹出事件
	 * 
	 * @param tableName
	 *            String
	 */
	public void showPopMenu(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		if (TABLE_EXA.equalsIgnoreCase(tableName)) {
			table.setPopupMenuSyntax("显示集合医嘱细相|Show LIS/RIS Detail Items,onOrderSetShow;查看报告 \n Report,showRept");
			return;
		}
		if (TABLE_OP.equalsIgnoreCase(tableName)) {
			table.setPopupMenuSyntax("显示诊疗项目细相|Show Treatment Detail Items,onOpShow");
			return;
		}
		if (TABLE_MED.equalsIgnoreCase(tableName) || TABLE_CTRL.equalsIgnoreCase(tableName)) {// modify by wanglong 20130522
			table.setPopupMenuSyntax("显示药嘱信息|Show Med Info,onSysFeeShow;显示合理用药信息|Show Rational Drug Use,onQueryRationalDrugUse");
			this.tableName = tableName;
			return;
		}
		if (TABLE_CHN.equalsIgnoreCase(tableName)) {// add by wanglong 20130522
			table.setPopupMenuSyntax("显示合理用药信息|Show Rational Drug Use,onQueryRationalDrugUse");
			return;
		}
	}

	/**
	 * 右击MENU显示集合医嘱事件
	 */
	public void onOrderSetShow() {
		TTable table = (TTable) this.getComponent(TABLE_EXA);
		OpdOrder order = odo.getOpdOrder();
		int row = table.getSelectedRow();
		String orderCode = order.getItemString(row, "ORDER_CODE");
		int groupNo = order.getItemInt(row, "ORDERSET_GROUP_NO");
		TParm parm = order.getOrderSetDetails(groupNo, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
	}

	/**
	 * 查看报告
	 */
	public void showRept(){
		TTable table = (TTable) this.getComponent(TABLE_EXA);
		OpdOrder order = odo.getOpdOrder();
		int row = table.getSelectedRow();
		// LIS报告
		if ("LIS".equals(order.getItemString(row,"CAT1_TYPE"))) {
			String labNo = order.getItemString(row,"MED_APPLY_NO");
			if (labNo.length() == 0) {
				this.messageBox("E0188");
				return;
			}
			SystemTool.getInstance().OpenLisWeb(odo.getMrNo());
		}
		// RIS报告
		if ("RIS".equals(order.getItemString(row,"CAT1_TYPE"))) {

			if ("ECC".equals(order.getItemString(row,"ORDER_CAT1_CODE"))){//电生理调阅pdf 文件
				TParm parm = new TParm();
				parm.setData("CASE_NO",odo.getCaseNo());
				parm.setData("OPE_BOOK_NO",order.getItemString(row,"MED_APPLY_NO"));
				TParm pathData = EMRCdrTool.getInstance().getWordPath(parm);
				for (int i = 0; i < pathData.getCount(); i++) {
					String fileName = pathData.getValue("FILE_NAME", 0) + ".pdf";
					String filePath = pathData.getValue("FILE_PATH", 0);
					serverPath = TConfig.getSystemValue("FileServer.Main.Root")
							+ "\\" + TConfig.getSystemValue("EmrData") + "\\"
							+ filePath.replaceFirst("JHW", "PDF");
					parm.setData("FILE_NAME", fileName);
					Runtime runtime = Runtime.getRuntime();
					byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
							.getSocket(), serverPath + "\\" + fileName);
					if (data == null) {
						messageBox_("服务器上没有找到文件 " + serverPath + "\\" + fileName);
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
			}else{//非电生理 调阅影像
				if(order.getItemString(row,"MED_APPLY_NO")!=null && !order.getItemString(row,"MED_APPLY_NO").equals("")){
					SystemTool.getInstance().OpenRisByMrNoAndApplyNo(odo.getMrNo(), order.getItemString(row,"MED_APPLY_NO"));
				}
			}
		}
	}

	/**
	 * 右击MENU显示诊疗项目细项事件
	 */
	public void onOpShow() {
		TTable table = (TTable) this.getComponent(TABLE_OP);
		OpdOrder order = odo.getOpdOrder();
		int row = table.getSelectedRow();
		String orderCode = order.getItemString(row, "ORDER_CODE");
		int groupNo = order.getItemInt(row, "ORDERSET_GROUP_NO");
		TParm parm = order.getOrderSetDetails(groupNo, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);

	}

	/**
	 * 右击MENU显示SYS_FEE事件
	 */
	public void onSysFeeShow() {
		TTable table = (TTable) this.getComponent(tableName);
		OpdOrder order = odo.getOpdOrder();
		String orderCode = order.getItemString(table.getSelectedRow(),
				"ORDER_CODE");
		TParm parm = new TParm();
		parm.setData("FLG", "OPD");
		parm.setData("ORDER_CODE", orderCode);
		this.openDialog("%ROOT%\\config\\sys\\SYS_FEE\\SYSFEE_PHA.x", parm);
	}

	/**
	 * 调用病患详细信息界面
	 */
	public void onPatDetail() {
		TParm parm = new TParm();
		// System.out.println("科室权限" + this.getPopedem("DEPT_POPEDEM"));
		// 判断科室权限
		if (this.getPopedem("DEPT_POPEDEM"))
			parm.setData("OPD", "ONW");
		else
			parm.setData("OPD", "OPD");
		parm.setData("MR_NO", reg.getPat().getMrNo());
		this.openDialog("%ROOT%\\config\\sys\\SYSPatInfo.x", parm);
	}

	/**
	 * 调用医师常用医嘱
	 */
	public void onDrOrder() {
		TParm parm = new TParm();
		parm.setData("DEPT_DR", "2");
		parm.setData("DEPTORDR_CODE", Operator.getID());
		if ("O".equalsIgnoreCase(admType)) {
			parm.setData("FIT", "OPD_FIT_FLG");
		} else {
			parm.setData("FIT", "EMG_FIT_FLG");
		}
		TTabbedPane tabPane = (TTabbedPane) this.getComponent("TTABPANELORDER");
		int index = tabPane.getSelectedIndex();
		String orderCat1Type = "";
		String tableName = "";
		String tag = "";
		String rxTag = "";
		// 选择哪个页签
		switch (index) {
		// 检验检查
		case 0:
			parm.setData("RX_TYPE", "5");
			orderCat1Type = "EXA";
			tableName = TABLE_EXA;
			tag = "EXA_AMT";
			rxTag = EXA_RX;
			break;
			// 处置
		case 1:
			parm.setData("RX_TYPE", "4");
			orderCat1Type = "TRT";
			tableName = TABLE_OP;
			tag = "OP_AMT";
			rxTag = OP_RX;
			break;
			// 西药
		case 2:
			parm.setData("ADM_TYPE",admType);//yanjing,门急诊区分，20130716
			parm.setData("RX_TYPE", "1");
			orderCat1Type = "PHA_W";
			tableName = TABLE_MED;
			tag = "MED_AMT";
			rxTag = MED_RX;
			break;
			// 中药
		case 3:
			parm.setData("RX_TYPE", "3");
			orderCat1Type = "PHA_G";
			tableName = TABLE_CHN;
			tag = "CHN_AMT";
			rxTag = CHN_RX;
			if (StringUtil.isNullString(this
					.getValueString("CHN_EXEC_DEPT_CODE"))) {
				this.messageBox("E0053");
				return;
			}
			break;
			// 管制药品
		case 4:
			parm.setData("RX_TYPE", "2");
			orderCat1Type = "PHA_W";
			tableName = TABLE_CTRL;
			tag = "CTRL_AMT";
			rxTag = CTRL_RX;
			break;
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\CommonOrderQuote.x", parm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		// ============pangben 2012-7-12 添加管控
		if (!getCheckRxNo()) {
			return;
		}
		//deleteLisPosc = false;// 检验检查删除管控 HL7退费操作修改注记
		if (CHN_RX.equalsIgnoreCase(rxTag)) {
			// 插入中医医嘱
			insertChnOrder(result);
		} else if (!EXA_RX.equalsIgnoreCase(rxTag)) {
			// 插入医嘱
			insertOrder(tableName, tag, orderCat1Type, result, rxTag);
		} else {
			int count = result.getCount("ORDER_CODE");
			for (int i = 0; i < count; i++) {
				this.insertExaPack(result.getRow(i));
			}

		}
	}

	/**
	 * 调用科室常用医嘱
	 */
	public void onDeptOrder() {
		TParm parm = new TParm();

		parm.setData("DEPT_DR", "1");
		if ("O".equalsIgnoreCase(admType)) {
			parm.setData("FIT", "OPD_FIT_FLG");
		} else {
			parm.setData("FIT", "EMG_FIT_FLG");
		}
		TTabbedPane tabPane = (TTabbedPane) this.getComponent("TTABPANELORDER");
		int index = tabPane.getSelectedIndex();
		String orderCat1Type = "";
		String tableName = "";
		String tag = "";
		String rxTag = "";
		// 选择哪个页签
		switch (index) {
		// 检验检查
		case 0:
			parm.setData("RX_TYPE", "5");
			orderCat1Type = "EXA";
			tableName = TABLE_EXA;
			tag = "EXA_AMT";
			rxTag = EXA_RX;
			break;
			// 处置
		case 1:
			parm.setData("RX_TYPE", "4");
			orderCat1Type = "TRT";
			tableName = TABLE_OP;
			tag = "OP_AMT";
			rxTag = OP_RX;
			break;
			// 西药
		case 2:
			parm.setData("ADM_TYPE",admType);//yanjing,门急诊区分，20130716
			parm.setData("RX_TYPE", "1");
			orderCat1Type = "PHA_W";
			tableName = TABLE_MED;
			tag = "MED_AMT";
			rxTag = MED_RX;
			break;
			// 中药
		case 3:
			parm.setData("RX_TYPE", "3");
			orderCat1Type = "PHA_G";
			tableName = TABLE_CHN;
			tag = "CHN_AMT";
			rxTag = CHN_RX;
			if (StringUtil.isNullString(this
					.getValueString("CHN_EXEC_DEPT_CODE"))) {
				this.messageBox("E0053");
				return;
			}
			break;
			// 管制药品
		case 4:
			parm.setData("RX_TYPE", "2");
			orderCat1Type = "PHA_W";
			tableName = TABLE_CTRL;
			tag = "CTRL_AMT";
			rxTag = CTRL_RX;
			break;
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\CommonOrderQuote.x", parm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		// ============pangben 2012-7-12 添加管控
		if (!getCheckRxNo()) {
			return;
		}
		//deleteLisPosc = false;// 检验检查删除管控 HL7退费操作修改注记
		if (CHN_RX.equalsIgnoreCase(rxTag)) {
			// 插入中医医嘱
			insertChnOrder(result);
		}else if (OP_RX.equalsIgnoreCase(rxTag)) {//add by huangtt 20171225添加插入处置
			// 插入处置
			this.insertOpPack(result);
		}
		else if (!EXA_RX.equalsIgnoreCase(rxTag)) {
			// 插入医嘱
			insertOrder(tableName, tag, orderCat1Type, result, rxTag);
		} else {
			int count = result.getCount("ORDER_CODE");
			for (int i = 0; i < count; i++) {
				this.insertExaPack(result.getRow(i));
			}

		}
		
		
	}

	/**
	 * 校验是否可以开在同一个处方签上 ===========pangben 2012-7-12 添加管控
	 * 当前处方签管控
	 * @return
	 */
	private boolean getCheckRxNo() {
		OpdOrder order = odo.getOpdOrder();
		int count = order.rowCount();
		if (count <= 0) {
			return false;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!deleteOrder(order, i, "已经打票的处方签不可以添加医嘱","此医嘱已经登记,不能删除")) {
				return false;

			}
		}

		return true;
	}
	/**
	 * 校验是否可以开在同一个处方签上 ===========pangben 2012-7-12 添加管控
	 * 所有页签当前的处方签管控
	 * 
	 * @return false 不可以执行  true 可以执行 
	 */
	private String getCheckRxNoSum(TParm parm){
		// 检验检查
		TParm exaResult = ((TParm) parm.getData("EXA"));
		OpdOrder order = odo.getOpdOrder();
		if (exaResult != null && exaResult.getCount("ORDER_CODE") > 0) {
			TComboBox rxExa = (TComboBox) getComponent(EXA_RX);
			String rxNo = rxExa.getSelectedID();
			if (null==rxNo || rxNo.length()<=0) {
				return "检验检查没有初始化处方签";
			}
			if (!order.isExecutePrint(odo.getCaseNo(), rxNo)) {
				return "已经打票的检验检查处方签不可以添加医嘱";
			}
		}
		TParm opResult = ((TParm) parm.getData("OP"));
		if (opResult != null && opResult.getCount("ORDER_CODE") > 0) {
			String rxNo = this.getValueString(OP_RX); // 处方签号
			if (null==rxNo || rxNo.length()<=0) {
				return "诊疗项目没有初始化处方签";
			}
			if (!order.isExecutePrint(odo.getCaseNo(), rxNo)) {
				return "已经打票的诊疗项目处方签不可以添加医嘱";
			}
		}
		// 药品
		TParm orderResult = ((TParm) parm.getData("ORDER"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			String rxNo = this.getValueString(MED_RX);
			if (null==rxNo || rxNo.length()<=0) {
				return "西成药没有初始化处方签";
			}
			if (!order.isExecutePrint(odo.getCaseNo(), rxNo)) {
				return "已经打票的西成药处方签不可以添加医嘱";
			}
		}
		// 管制药品
		orderResult = ((TParm) parm.getData("CTRL"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			String rxNo = this.getValueString(CTRL_RX);
			if (null==rxNo || rxNo.length()<=0) {
				return "管制药品没有初始化处方签";
			}
			if (!order.isExecutePrint(odo.getCaseNo(), rxNo)) {
				return "已经打票的管制药品处方签不可以添加医嘱";
			}
		}
		return null;
	}
	/**
	 * 常用医嘱，组套插入医嘱
	 * 
	 * @param tableName
	 *            String
	 * @param tag
	 *            String
	 * @param orderCat1Type
	 *            String
	 * @param orderParm
	 *            TParm
	 * @param rxTag
	 *            String
	 */
	private void insertOrder(String tableName, String tag,
			String orderCat1Type, TParm orderParm, String rxTag) {

		TTable table = (TTable) this.getComponent(tableName);
		OpdOrder order = odo.getOpdOrder();
		String rxType = order.getItemString(0, "RX_TYPE");
		String rxNo = order.getItemString(0, "RX_NO");
		insertOrderData(order, orderParm, orderCat1Type, rxTag);
		if (TABLE_CHN.equalsIgnoreCase(tableName)) {
			initChnTable(rxNo);
			return;
		}
		int row = -1;
		if (order.rowCount() >= 1
				&& !StringUtil.isNullString(order.getItemString(order
						.rowCount() - 1, "ORDER_CODE"))) {
			row = order.newOrder(rxType, rxNo);
		} else if (order.rowCount() <= 0) {
			row = order.newOrder(rxType, rxNo);
		} else {
			row = order.rowCount() - 1;
		}
		table.setDSValue();
		table.getTable().grabFocus();
		table.setSelectedRow(row);
		int column = table.getColumnIndex("ORDER_DESC_SPECIFICATION");
		table.setSelectedColumn(column);
		this.calculateCash(tableName, tag);
	}

	/**
	 * 常用医嘱插入中医医嘱
	 * 
	 * @param parm
	 *            TParm
	 */
	private void insertChnOrder(TParm parm) {
		OpdOrder order = odo.getOpdOrder();
		String rxNo = this.getValueString("CHN_RX");

		String deptCode = this.getValueString("CHN_EXEC_DEPT_CODE");
		int count = order.rowCount();
		if (count <= 0) {
			return;
		}
		for (int i = count - 1; i > -1; i--) {
			order.deleteRow(i);
		}

		if (StringUtil.isNullString(rxNo)) {
			this.messageBox("E0069");
			return;
		}
		if (parm == null) {
			this.messageBox("E0070");
			return;
		}
		count = parm.getCount("ORDER_CODE");
		if (count < 1) {
			this.messageBox("E0070");
			return;
		}
		int row = -1;
		for (int i = 0; i < count; i++) {
			row = order.newOrder(CHN, rxNo);
			String orderCode = parm.getValue("ORDER_CODE", i);
			TParm phaBase = PhaBaseTool.getInstance().selectByOrder(orderCode);
			TParm reParm = parm.getRow(i);
			reParm.setData("EXEC_DEPT_CODE", deptCode);
			reParm.setData("CAT1_TYPE", "PHA");

			this.initOrder(order, row, reParm, phaBase);

			order.itemNow = false;
			order.setItem(row, "MEDI_QTY", parm.getValue("MEDI_QTY", i));
			order.setItem(row, "MEDI_UNIT", parm.getValue("MEDI_UNIT", i));

			TParm price = OdoUtil.getPrice(order.getItemString(row,
					"ORDER_CODE"));
			order.setItem(row, "OWN_PRICE", price.getDouble("OWN_PRICE"));
			order.setItem(row, "CHARGE_HOSP_CODE", price
					.getValue("CHARGE_HOSP_CODE"));
			double ownAmt = roundAmt(TypeTool.getDouble(price
					.getData("OWN_PRICE"))
					* order.getItemDouble(row, "DOSAGE_QTY"));
			double arAmt = roundAmt(BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
					orderCode, order.getItemDouble(row, "DOSAGE_QTY"),
					serviceLevel));
			order.setItem(row, "DISCN_RATE", BIL.getOwnRate(ctz[0], ctz[1],
					ctz[2], price.getValue("CHARGE_HOSP_CODE"), orderCode));
			order.setItem(row, "OWN_AMT", ownAmt);
			order.setItem(row, "AR_AMT", arAmt);
			order.setItem(row, "PAYAMOUNT", ownAmt - arAmt);
			order.setItem(row, "EXEC_DEPT_CODE", deptCode);
			order.setItem(row, "ORDER_CAT1_CODE", "PHA_G");
			order.setItem(row, "CAT1_TYPE", "PHA");

			order.setItem(row, "DCT_TAKE_QTY", this
					.getValueDouble("DCT_TAKE_QTY"));
			order.setItem(row, "TAKE_DAYS", this
					.getValueDouble("DCT_TAKE_DAYS"));
			order.setItem(row, "FREQ_CODE", this.getValue("CHN_FREQ_CODE"));
			order.setItem(row, "ROUTE_CODE", this.getValue("CHN_ROUTE_CODE"));
			order.setItem(row, "DCTAGENT_CODE", this.getValue("DCTAGENT_CODE"));
			order.setItem(row, "DR_NOTE", this.getValue("DR_NOTE"));
			order.setItem(row, "DCTEXCEP_CODE", parm.getValue("DCTEXCEP_CODE", i));
			order.setActive(row, true);
		}
		calculateChnCash(rxNo);
		if ((row + 1) % 4 == 0) {
			addChnRow(rxNo, row / 4);
		}
		this.initChnTable(rxNo);

	}

	/**
	 * 循环插入数据
	 * 
	 * @param order
	 *            OpdOrder
	 * @param orderParm
	 *            TParm
	 * @param orderCat1Type
	 *            String
	 * @param rxTag
	 *            String
	 */
	public void insertOrderData(OpdOrder order, TParm orderParm,
			String orderCat1Type, String rxTag) {
		int count = orderParm.getCount("ORDER_CODE");
		String rxNo = order.getItemString(0, "RX_NO");
		String rxType = order.getItemString(0, "RX_TYPE");
		this.rxType = rxType;
		String deptCode = "";
		if (MED.equalsIgnoreCase(rxType)) {
			deptCode = this.getValueString("MED_RBORDER_DEPT_CODE");
		} else if (CTRL.equalsIgnoreCase(rxType)) {
			deptCode = this.getValueString("CTRL_RBORDER_DEPT_CODE");
		} else if (CHN.equalsIgnoreCase(rxType)) {
			deptCode = this.getValueString("CHN_EXEC_DEPT_CODE");
		} else if (OP.equalsIgnoreCase(rxType)) {
			deptCode = this.getValueString("OP_EXEC_DEPT");
		}
		int row = order.rowCount() - 1;
		if (EXA.equalsIgnoreCase(rxType)) {
			return;
		}
		TParm price;
		for (int i = 0; i < count; i++) {
			if (i != 0) {
				row = order.newOrder(rxType, rxNo);
			}
			order.setActive(row, true);
			order.itemNow = true;
			if (order.isSameOrder(orderParm.getValue("ORDER_CODE", i))) {
				if (this.messageBox(
						"提示信息/Tip",
						"该医嘱已经开立，是否继续？\r\n/This order exist,Do you give it again?",
						0) == 1) {
					continue;
				}
			}
			String orderCode = orderParm.getValue("ORDER_CODE", i);
			TParm orderRowParm = orderParm.getRow(i);
			if ("PHA_W".equalsIgnoreCase(orderCat1Type)) {
				orderRowParm.setData("CAT1_TYPE", "PHA");
			} else if (orderCat1Type.contains("TRT")
					|| orderCat1Type.contains("PLN")) {
				orderRowParm.setData("CAT1_TYPE", orderCat1Type);
			}
			orderRowParm.setData("EXEC_DEPT_CODE", deptCode);
			TParm phaBase = PhaBaseTool.getInstance().selectByOrder(orderCode);
			if (phaBase.getCount() != 1) {
				this.initOrder(order, row, orderRowParm, null);
			} else {
				this.initOrder(order, row, orderRowParm, phaBase);
			}
			order.setItem(row, "DR_NOTE", orderParm.getValue("DESCRIPTION", i));
			order.itemNow = false;
			order.setItem(row, "MEDI_QTY", orderParm.getValue("MEDI_QTY", i));
			order.setItem(row, "MEDI_UNIT", orderParm.getValue("MEDI_UNIT", i));
			order.setItem(row, "FREQ_CODE", orderParm.getValue("FREQ_CODE", i));
			order.setItem(row, "ROUTE_CODE", orderParm
					.getValue("ROUTE_CODE", i));

			order.setItem(row, "TAKE_DAYS", orderParm.getValue("TAKE_DAYS", i));
			order.setItem(row, "PRESRT_NO", orderParm.getValue("PRESRT_NO", i));
			price = OdoUtil.getPrice(order.getItemString(row, "ORDER_CODE"));
			order.setItem(row, "OWN_PRICE", price.getDouble("OWN_PRICE"));
			order.setItem(row, "CHARGE_HOSP_CODE", price
					.getValue("CHARGE_HOSP_CODE"));
			order.itemNow = false;
			order.setItem(row, "GIVEBOX_FLG", orderParm.getValue("GIVEBOX_FLG",
					i));
			double ownAmt = roundAmt(TypeTool.getDouble(price
					.getData("OWN_PRICE"))
					* order.getItemDouble(row, "DOSAGE_QTY"));
			double arAmt = roundAmt(BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
					orderCode, order.getItemDouble(row, "DOSAGE_QTY"),
					serviceLevel));
			order.setItem(row, "DISCN_RATE", BIL.getOwnRate(ctz[0], ctz[1],
					ctz[2], price.getValue("CHARGE_HOSP_CODE"), orderCode));
			order.setItem(row, "OWN_AMT", ownAmt);
			order.setItem(row, "AR_AMT", arAmt);
			order.setItem(row, "PAYAMOUNT", ownAmt - arAmt);
			order.setItem(row, "EXEC_DEPT_CODE", deptCode);
			order.setItem(row, "ORDER_CAT1_CODE", orderCat1Type);
		}
	}

	/**
	 * 调用医师常用诊断
	 */
	public void onDrDiag() {
		String param = "2," + Operator.getID();
		if (StringTool.getBoolean(this.getValueString("W_FLG"))) {
			param += ",W";
		} else {
			param += ",C";
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\ODOCommonIcdQuote.x", param);
		if (result == null || result.getCount("ICD_CODE") < 1) {
			return;
		}
		insertDiag(result);
	}

	/**
	 * 调用科常用诊断
	 */
	public void onDeptDiag() {

		String param = "1," + Operator.getDept();
		if (StringTool.getBoolean(this.getValueString("W_FLG"))) {
			param += ",W";
		} else {
			param += ",C";
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\ODOCommonIcdQuote.x", param);
		// System.out.println("result="+result);
		if (result == null || result.getCount("ICD_CODE") < 1) {
			return;
		}
		insertDiag(result);
	}

	/**
	 * 常用医嘱，组套插入诊断
	 * 
	 * @param diagParm
	 *            TParm诊断集合
	 */
	private void insertDiag(TParm diagParm) {
		int count = diagParm.getCount("ICD_CODE");
		Diagrec diag = odo.getDiagrec();
		int row = diag.rowCount() - 1;
		TTable table = (TTable) this.getComponent(TABLEDIAGNOSIS);
		for (int i = 0; i < count; i++) {

			if (!StringUtil.isNullString(diag.getItemString(
					diag.rowCount() - 1, "ICD_CODE"))) {
				row = diag.insertRow(-1);
			}
			if (diag.isHaveSameDiag(diagParm.getValue("ICD_CODE", i))) {
				this.messageBox("E0041");
				continue;
			}
			if (!OdoUtil.isAllowDiag(diagParm.getRow(i), Operator.getDept(),
					pat.getSexCode(), pat.getBirthday(), (Timestamp) this
					.getValue("ADM_DATE"))) {
				this.messageBox("E0042"); // liudy
				diag.deleteRow(row);
				table.acceptText();
				table.getTable().grabFocus();
				table.setSelectedRow(0);
				table.setSelectedColumn(1);
				row = table.addRow();
				table.setDSValue();
				continue;
			}

			diag.setItem(row, "ICD_TYPE", wc);
			diag.setItem(row, "ICD_CODE", diagParm.getValue("ICD_CODE", i));
			diag.setItem(row, "ORDER_DATE", diag.getDBTime());
			diag.setActive(row, true);
			if (!diag.haveMainDiag(new int[1])) {
				if ("C".equalsIgnoreCase(wc)
						&& !OdoUtil.isAllowChnDiag(diagParm.getRow(i))) {
					this.messageBox("E0018");
					diag.deleteRow(row);
					// odo.getDiagrec().insertRow();
					table.acceptText();
					table.getTable().grabFocus();
					table.setSelectedRow(0);
					table.setSelectedColumn(1);
					table.addRow();
					table.setDSValue();
					return;
				}

				diag.setItem(row, "MAIN_DIAG_FLG", "Y");
			} else {
				diag.setItem(row, "MAIN_DIAG_FLG", "N");
			}

		}
		if (!StringUtil.isNullString(diag.getItemString(diag.rowCount() - 1,
				"ICD_CODE")))
			row = diag.insertRow(diag.rowCount());
		table.setDSValue();
		table.getTable().grabFocus();
		table.setSelectedRow(row);
		table.setSelectedColumn(1);
	}

	/**
	 * 调用科常用模板
	 */
	public void onDeptPack() {
		String chnExecDeptCode = getValueString("CHN_EXEC_DEPT_CODE");
		TTabbedPane orderPane = (TTabbedPane) getComponent("TTABPANELORDER");
		TParm parm = new TParm();
		parm.setData("DEPT_OR_DR", "1");
		parm.setData("DEPTORDR_CODE", Operator.getDept());
		parm.setData("ADM_TYPE",admType);//yanjing,门急诊区分，20130614
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\OPDComPackQuote.x", parm, false);
		if(null==result){
			return;
		}
		if (Operator.getSpcFlg().equals("Y")) {//pangben 2013-5-17添加物联网校验是否当前处方签已经审核
			if (!checkSpcPha(odo.getOpdOrder())) {
				return;
			}
		}
		// ============pangben 2012-7-12 添加管控
		String resultString=getCheckRxNoSum(result);
		if (null!=resultString) {		
			this.messageBox(resultString);
			return;
		}
		insertPack(result);
		TTabbedPane paneNow = (TTabbedPane)getComponent("TTABPANELORDER");
		if (paneNow.getSelectedIndex() == 3) {
			orderPane.setSelectedIndex(2);
			orderPane.setSelectedIndex(3);
		}
		setValue("CHN_EXEC_DEPT_CODE", chnExecDeptCode);

	}

	/**
	 * 调用医师常用模板
	 */
	public void onDrPack() {
		String chnExecDeptCode = getValueString("CHN_EXEC_DEPT_CODE");
		// ======pangben 2012-7-24 添加默认选中西药页签，解决在检验检查页签时，药品页签出现检验检查主项问题
		TTabbedPane orderPane = (TTabbedPane) getComponent("TTABPANELORDER");
		//		if (orderPane.getSelectedIndex() != 2
		//				&& orderPane.getSelectedIndex() != 3) {
		//			// orderPane.setSelectedIndex(2);//===默认西药页签
		//			messageBox("请从西药或中草药页签操作");
		//			orderPane.setSelectedIndex(2);// ===默认西药页签
		//			return;
		//		} else {
		//			onChangeOrderTab();// 页签切换
		//		}
		setValue("CHN_EXEC_DEPT_CODE", chnExecDeptCode);


		TParm parm = new TParm();
		parm.setData("DEPT_OR_DR", "2");
		parm.setData("ADM_TYPE",admType);//yanjing,门急诊区分，20130614
		parm.setData("DEPTORDR_CODE", Operator.getID());
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\OPDComPackQuote.x", parm, false);
		if(null==result){
			return;
		}
		if (Operator.getSpcFlg().equals("Y")) {//添加物联网校验是否当前处方签已经审核
			if (!checkSpcPha(odo.getOpdOrder())) {
				return;
			}
		}
		// ============pangben 2012-7-12 添加管控
		String resultString=getCheckRxNoSum(result);
		if (null!=resultString) {
			this.messageBox(resultString);
			return;
		}
		insertPack(result);
		TTabbedPane paneNow = (TTabbedPane)getComponent("TTABPANELORDER");
		if (paneNow.getSelectedIndex() == 3) {
			orderPane.setSelectedIndex(2);
			orderPane.setSelectedIndex(3);
		}
		setValue("CHN_EXEC_DEPT_CODE", chnExecDeptCode);

	}

	/**
	 * 取得模板后的向医生站主界面上插入数据
	 * 
	 * @param result
	 *            TParm
	 */
	private void insertPack(TParm result) {
		// System.out.println("==result=="+result);
		if (result == null)
			return;
		// =======pangben 2012-6-15
		//deleteLisPosc = false;// 检验检查删除管控 HL7退费操作修改注记
		String subjText = this.getValueString("SUBJ_TEXT")
				+ result.getValue("SUBJ_TEXT");
		String objText = this.getValueString("OBJ_TEXT")
				+ result.getValue("OBJ_TEXT");
		String psyText = this.getValueString("PHYSEXAM_REC")
				+ result.getValue("PHYSEXAM_REC");
		/**
		 * System.out.println("==subjText=="+subjText);
		 * System.out.println("==objText=="+objText);
		 * System.out.println("==psyText=="+psyText);
		 **/

		// zhangyong20110311
		String filter1 = ((TTable) this.getComponent(TABLE_EXA)).getDataStore()
				.getFilter();
		String filter2 = ((TTable) this.getComponent(TABLE_OP)).getDataStore()
				.getFilter();
		String filter3 = ((TTable) this.getComponent(TABLE_MED)).getDataStore()
				.getFilter();
		String filter4 = ((TTable) this.getComponent(TABLE_CHN)).getDataStore()
				.getFilter();
		String filter5 = ((TTable) this.getComponent(TABLE_CTRL))
				.getDataStore().getFilter();
		if (word != null) {
			if (subjText != null && !subjText.equals("")) {
				word.clearCapture("SUB");
				word.pasteString(subjText);
			}
			if (objText != null && !objText.equals("")) {
				word.clearCapture("OBJ");
				word.pasteString(objText);
			}
			if (psyText != null && !psyText.equals("")) {
				word.clearCapture("PHY");
				word.pasteString(psyText);
			}
		}
		// $$=======Modified by lx 2012-06-10 科模板传回时主诉现病史被清空 end======$$//
		// 将模板中的主诉客诉现病史赋值到结构化病例中

		// 取得诊断
		TParm diagResult = ((TParm) result.getData("DIAG"));
		// ============xueyf modify 20120312
		boolean isHasDiagFlg = false;
		int rowMainDiag = odo.getDiagrec().getMainDiag();
		if (rowMainDiag >= 0) {
			isHasDiagFlg = true;
		}
		if (diagResult != null) {
			int count = diagResult.getCount("ICD_CODE");
			for (int i = 0; i < count; i++) {
				tblDiag.setSelectedRow(tblDiag.getRowCount() - 1);
				this.popDiagReturn("", diagResult.getRow(i));
				Diagrec diagRec = odo.getDiagrec();
				int row = tblDiag.getSelectedRow() - 1;
				// ============xueyf modify 20120312 
				String MAIN_DIAG_FLG = diagResult.getData("MAIN_DIAG_FLG", i)
						.toString();
				if (MAIN_DIAG_FLG.equals("Y") && isHasDiagFlg) {
					this.messageBox("已有主诊断!\n如需要，请您手动选择主诊断。");
					MAIN_DIAG_FLG = "N";
				}
				diagRec.setItem(row, "MAIN_DIAG_FLG", MAIN_DIAG_FLG);
				tblDiag.setDSValue();
			}
		}
		// 检验检查
		TParm exaResult = ((TParm) result.getData("EXA"));
		// System.out.println("exaResult----"+exaResult);
		if (exaResult != null && exaResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_EXA;
			int count = exaResult.getCount("ORDER_CODE");
			for (int i = 0; i < count; i++) {
				insertExaPack(exaResult.getRow(i));
			}
		}
		// // 处置
		TParm opResult = ((TParm) result.getData("OP"));
		if (opResult != null && opResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_OP;
			// orderCat1Type = "TRT";
			// tag = "OP_AMT";
			insertOpPack(opResult);
		}
		// 药品
		TParm orderResult = ((TParm) result.getData("ORDER"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_MED;
			// orderCat1Type = "PHA_W";
			// tag = "MED_AMT";
			//			System.out.println("111orderResult is :"+orderResult);
			insertOrdPack(orderResult,false);
		}
		// for (int i = 0; i < odo.getOpdOrder().rowCount(); i++) {
		// System.out.println(i + "--药品--" + odo.getOpdOrder().getRowParm(i));
		// }

		// 管制药品
		orderResult = ((TParm) result.getData("CTRL"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_CTRL;
			// orderCat1Type = "PHA_W";
			// tag = "CTRL_AMT";
			insertCtrlPack(orderResult);
		}

		// 中药
		// System.out.println("chn======"+result.getData("CHN"));
		Map chnMap = (Map) result.getData("CHN");
		if (chnMap == null || chnMap.size() <= 0) {
			return;
		}
		Iterator it = chnMap.values().iterator();
		while (it.hasNext()) {
			tableName = TABLE_CHN;
			// orderCat1Type = "PHA_G";
			orderResult = (TParm) it.next();
			insertChnPack(orderResult);
		}
		((TTable) this.getComponent(TABLE_EXA)).setFilter(filter1);
		((TTable) this.getComponent(TABLE_EXA)).filter();
		((TTable) this.getComponent(TABLE_EXA)).setDSValue();
		((TTable) this.getComponent(TABLE_OP)).setFilter(filter2);
		((TTable) this.getComponent(TABLE_OP)).filter();
		((TTable) this.getComponent(TABLE_OP)).setDSValue();
		((TTable) this.getComponent(TABLE_MED)).setFilter(filter3);
		((TTable) this.getComponent(TABLE_MED)).filter();
		((TTable) this.getComponent(TABLE_MED)).setDSValue();

		((TTable) this.getComponent(TABLE_CTRL)).setFilter(filter5);
		((TTable) this.getComponent(TABLE_CTRL)).filter();
		((TTable) this.getComponent(TABLE_CTRL)).setDSValue();

		((TTable) this.getComponent(TABLE_CHN)).setFilter(filter4);
		((TTable) this.getComponent(TABLE_CHN)).filter();
		((TTable) this.getComponent(TABLE_CHN)).setDSValue();



	}
	/**
	 * 初始化parm
	 * 20130428 yanjing 
	 * @param parm
	 * @param i
	 */
	private TParm initParmBase(TParm parm,int i) {
		TParm parmBase = new TParm();
		parmBase.addData("ROUTE_CODE", parm.getValue("ROUTE_CODE",i));
		parmBase.addData("DISPENSE_UNIT", parm.getValue("DISPENSE_UNIT",i));
		parmBase.addData("RELEASE_FLG", parm.getValue("RELEASE_FLG",i));
		parmBase.addData("EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE",i));
		parmBase.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT",i));
		parmBase.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE",i));
		parmBase.addData("FREQ_CODE", parm.getValue("FREQ_CODE",i));
		parmBase.addData("ORDER_CODE", parm.getValue("ORDER_CODE",i));
		parmBase.addData("URGENT_FLG", parm.getValue("URGENT_FLG",i));
		parmBase.addData("DR_NOTE", parm.getValue("DR_NOTE",i));
		parmBase.addData("ORDER_DESC", parm.getValue("ORDER_DESC",i));
		parmBase.addData("LINK_NO", parm.getValue("LINK_NO",i));
		parmBase.addData("GIVEBOX_FLG", parm.getValue("GIVEBOX_FLG",i));
		parmBase.addData("DISPENSE_QTY", parm.getValue("DISPENSE_QTY",i));
		parmBase.addData("TAKE_DAYS", parm.getValue("TAKE_DAYS",i));
		parmBase.addData("NS_NOTE", parm.getValue("NS_NOTE",i));
		parmBase.addData("USE", parm.getValue("USE",i));
		parmBase.addData("ACTIVE_FLG", parm.getValue("ACTIVE_FLG",i));
		parmBase.addData("ORDER_CODE_FEE", parm.getValue("ORDER_CODE_FEE",i));
		parmBase.addData("MEDI_QTY", parm.getValue(" MEDI_QTY",i));
		parmBase.addData("LINKMAIN_FLG", parm.getValue("LINKMAIN_FLG",i));
		return parmBase;
	}

	/**
	 * 插入药品模板
	 * 
	 * @param parm
	 *            TParm
	 */
	private void insertOrdPack(TParm parm,boolean onFechFlg) {
		int count = parm.getCount("ORDER_CODE");
		if (count < 0) {
			return;
		}
		OpdOrder order = odo.getOpdOrder();
		String tableName = "TABLEMED";
		rxType = MED;
		tableName = TABLE_MED;

		String rxNo = this.getValueString(MED_RX);
		String execDept = this.getValueString("MED_RBORDER_DEPT_CODE");
		if (StringUtil.isNullString(rxNo)) {
			initRx(MED_RX, MED);
		}
		rxNo = this.getValueString(MED_RX);

		order.setFilter("RX_NO='" + rxNo + "'");
		if (!order.filter()) {
			return;
		}
		int row = StringUtil.isNullString(order.getItemString(
				order.rowCount() - 1, "ORDER_CODE")) ? order.rowCount() - 1
						: (order.newOrder(MED, rxNo));			
				for (int i = 0; i < count; i++) {
					//yanjing20130428 传回校验，总量
					TParm parmBase = new TParm();
					if (onFechFlg) {
						parmBase = initParmBase(parm, i);
					}else {
						parmBase = PhaBaseTool.getInstance().selectByOrder(
								parm.getValue("ORDER_CODE", i));
					}	
					TParm parmRow = parm.getRow(i);
					TParm sysFee = new TParm(TJDODBTool.getInstance().select(
							SYSSQL.getSYSFee(parmRow.getValue("ORDER_CODE"))));
					sysFee = sysFee.getRow(0);
					sysFee.setData("EXEC_DEPT_CODE", execDept);
					sysFee.setData("CAT1_TYPE", "PHA");
					this.initOrder(order, row, sysFee, parmBase);
					order.setItem(row, "MEDI_QTY", parmRow.getDouble("MEDI_QTY"));
					order.setItem(row, "MEDI_UNIT", parmRow.getValue("MEDI_UNIT"));
					order.setItem(row, "FREQ_CODE", parmRow.getValue("FREQ_CODE"));
					order.setItem(row, "TAKE_DAYS", parmRow.getInt("TAKE_DAYS"));
					order.setItem(row, "ROUTE_CODE", parmRow.getValue("ROUTE_CODE"));
					order
					.setItem(row, "LINKMAIN_FLG", parmRow
							.getValue("LINKMAIN_FLG"));
					order.setItem(row, "LINK_NO", parmRow.getValue("LINK_NO"));
					// add by wangqing 20171024 start
					// 口头医嘱
					if(parmRow.getValue("ONW_ORDER_FLG") != null 
							&& parmRow.getValue("ONW_ORDER_FLG").equals("Y")){
						order.setItem(row, "GIVEBOX_FLG", "N");// 给盒注记
					}				
					order.setItem(row, "ONW_ORDER_FLG", parmRow.getValue("ONW_ORDER_FLG"));// 口头医嘱标识
					order.setItem(row, "ONW_TRIAGE_NO", parmRow.getValue("TRIAGE_NO"));// 检伤号
					order.setItem(row, "ONW_ORDER_SEQ", parmRow.getValue("SEQ_NO"));// 医嘱序号
					// add by wangqing 20171024 end
					order.setActive(row, true);
					row = order.newOrder(MED, rxNo);
				}
				if (!StringUtil.isNullString(order.getItemString(order.rowCount() - 1,
						"ORDER_CODE"))) {
					order.newOrder(MED, rxNo);
				}
				initNoSetTable(rxNo, tableName, false,false);
				this.calculateCash(tableName, "MED_AMT");
				order.itemNow = false;
				Map inscolor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
						whetherCallInsItf);
				Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odo.getOpdOrder());
				tblMed.setRowTextColorMap(ctrlcolor);
	}

	/**
	 * 插入管制药品模板
	 * 
	 * @param parm
	 *            TParm
	 */
	private void insertCtrlPack(TParm parm) {
		OpdOrder order = odo.getOpdOrder();
		String rxNo = this.getValueString(CTRL_RX);
		if (StringUtil.isNullString(rxNo)) {
			initRx(CTRL_RX, CTRL);
		}
		rxNo = this.getValueString(CTRL_RX);
		String exec = this.getValueString("CTRL_RBORDER_DEPT_CODE");
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		int row = StringUtil.isNullString(order.getItemString(
				order.rowCount() - 1, "ORDER_CODE")) ? order.rowCount() - 1
						: (order.newOrder(CTRL, rxNo));
				int count = parm.getCount("ORDER_CODE");
				for (int i = 0; i < count; i++) {
					TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
							parm.getValue("ORDER_CODE", i));
					TParm parmRow = parm.getRow(i);
					TParm sysFee = new TParm(TJDODBTool.getInstance().select(
							SYSSQL.getSYSFee(parmRow.getValue("ORDER_CODE"))));
					sysFee = sysFee.getRow(0);
					// 判断模板传回的信息中是否有 执行科室
					// 如果有执行科室 那么用模板中的执行科室
					String execDept = "";
					if (!StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE", i))) {
						execDept = parm.getValue("EXEC_DEPT_CODE", i);
					}
					// 如果模板中没有执行科室 那么使用sys_fee中的执行科室
					else if (!StringUtil
							.isNullString(sysFee.getValue("EXEC_DEPT_CODE"))) {
						execDept = sysFee.getValue("EXEC_DEPT_CODE");
					} else { // 如果sys_fee中也没有执行科室 那么使用当前用户的算在科室
						execDept = Operator.getDept();
					}
					sysFee.setData("EXEC_DEPT_CODE", execDept);
					initOrder(order, row, sysFee, parmBase);
					order.setItem(row, "MEDI_QTY", parmRow.getDouble("MEDI_QTY"));
					order.setItem(row, "MEDI_UNIT", parmRow.getValue("MEDI_UNIT"));
					order.setItem(row, "FREQ_CODE", parmRow.getValue("FREQ_CODE"));
					order.setItem(row, "TAKE_DAYS", parmRow.getInt("TAKE_DAYS"));
					order.setItem(row, "ROUTE_CODE", parmRow.getValue("ROUTE_CODE"));
					order
					.setItem(row, "LINKMAIN_FLG", parmRow
							.getValue("LINKMAIN_FLG"));
					order.setItem(row, "LINK_NO", parmRow.getValue("LINK_NO"));
					order.setActive(row, true);
					row = order.newOrder(CTRL, rxNo);
				}
				if (!StringUtil.isNullString(order.getItemString(order.rowCount() - 1,
						"ORDER_CODE"))) {
					order.newOrder(CTRL, rxNo);
				}
				initNoSetTable(rxNo, tableName, false,false);
				calculateCash(tableName, "CTRL_AMT");
				order.itemNow = false;
				Map inscolor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
						whetherCallInsItf);
				Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odo.getOpdOrder());
				tblCtrl.setRowTextColorMap(ctrlcolor);
	}

	/**
	 * 插入组套数据
	 * 
	 * @param parmExa
	 *            TParm
	 */
	private void insertExaPack(TParm parmExa) {
		rxType = EXA;
		OpdOrder order = odo.getOpdOrder();
		TComboBox rxExa = (TComboBox) getComponent(EXA_RX);
		TTable table = (TTable) this.getComponent(TABLE_EXA);
		String rxNo = rxExa.getSelectedID();
		// this.messageBox_(rxNo);
		String orderCode = parmExa.getValue("ORDER_CODE");
		// System.out.println("orderCode---"+orderCode);
		TParm sysFee = new TParm(TJDODBTool.getInstance().select(
				SYSSQL.getSYSFee(orderCode)));
		if (sysFee == null || sysFee.getErrCode() != 0) {
			this.messageBox("E0034");
			return;
		}
		// 填充主项数据
		sysFee = sysFee.getRow(0);
		int row = table.getShowParmValue().getCount() - 1;
		order.setFilter("RX_NO='" + rxNo
				+ "' AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')");
		order.filter();
		// order.showDebug();
		order.setItem(row, "RX_NO", rxNo);
		initOrder(order, row, sysFee, null);
		// System.out.println("111111-----"+order.getRowParm(row));
		order.setItem(row, "DR_NOTE", parmExa.getValue("DESCRIPTION"));
		String labNo = order.getLabNo(row, odo);
		if (StringUtil.isNullString(labNo)) {
			this.messageBox("E0049");
			order.deleteRow(row);
			order.newOrder(EXA, rxNo);
			table.setDSValue();
			return;
		}
		order.setItem(row, "MED_APPLY_NO", labNo);
		order.setActive(row, true);
		String execDept = sysFee.getValue("EXEC_DEPT_CODE");
		if (StringUtil.isNullString(execDept)) {
			execDept = Operator.getDept();
		}
		order.setItem(row, "ORDERSET_CODE", orderCode);
		order.setItem(row, "SETMAIN_FLG", "Y");
		order.setItem(row, "HIDE_FLG", "N");
		int groupNo = order.getMaxGroupNo();
		order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
		order.setItem(row, "OWN_PRICE", 0.0);
		// add by wangb 首诊心电医嘱合并
		order.setItem(row, "VIRTUAL_FLG", parmExa.getValue("VIRTUAL_FLG"));
		if ("Y".equals(parmExa.getValue("EXEC_FLG"))) {
			order.setItem(row, "EXEC_FLG", "Y");
		}
		// add by wangqing 20171024 start
		// 口头医嘱
		order.setItem(row, "ONW_ORDER_FLG", parmExa.getValue("ONW_ORDER_FLG"));// 口头医嘱标识
		order.setItem(row, "ONW_TRIAGE_NO", parmExa.getValue("TRIAGE_NO"));// 检伤号
		order.setItem(row, "ONW_ORDER_SEQ", parmExa.getValue("SEQ_NO"));// 医嘱序号
		// add by wangqing 20171024 end
		order.setActive(row, true);

		// 查询细相数据，填充细相
		TParm parmDetail = SYSOrderSetDetailTool.getInstance()
				.selectByOrderSetCode(orderCode);
		if (parmDetail.getErrCode() != 0) {
			this.messageBox("E0050");
			return;
		}
		int count = parmDetail.getCount();
		for (int i = 0; i < count; i++) {
			row = order.newOrder(EXA, rxNo);
			// System.out.println("row----"+row);
			initOrder(order, row, parmDetail.getRow(i), null);
			order.setItem(row, "MED_APPLY_NO", labNo);
			order.setItem(row, "OPTITEM_CODE", parmDetail.getValue(
					"OPTITEM_CODE", i));
			order
			.setItem(row, "CAT1_TYPE", parmDetail.getValue("CAT1_TYPE",
					i));
			order.setItem(row, "EXEC_DEPT_CODE", execDept);
			order.setItem(row, "INSPAY_TYPE", parmDetail.getValue(
					"INSPAY_TYPE", i));
			order.setItem(row, "RPTTYPE_CODE", parmDetail.getValue(
					"RPTTYPE_CODE", i));
			order.setItem(row, "DEGREE_CODE", parmDetail.getValue(
					"DEGREE_CODE", i));
			order.setItem(row, "CHARGE_HOSP_CODE", parmDetail.getValue(
					"CHARGE_HOSP_CODE", i));
			order.setItem(row, "HIDE_FLG", parmDetail.getValue("HIDE_FLG", i));
			order.setItem(row, "ORDERSET_CODE", orderCode);
			order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
			double ownPrice = parmDetail.getDouble("OWN_PRICE", i);
			order.setItem(row, "OWN_PRICE", ownPrice);
			double qty = parmDetail.getDouble("TOTQTY", i);
			order.setItem(row, "DOSAGE_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "MEDI_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "DISPENSE_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "TAKE_DAYS", 1);
			order
			.setItem(row, "MEDI_UNIT", parmDetail.getValue("UNIT_CODE",
					i));
			order.setItem(row, "DOSAGE_UNIT", parmDetail.getValue("UNIT_CODE",
					i));
			order.setItem(row, "DISPENSE_UNIT", parmDetail.getValue(
					"UNIT_CODE", i));

			order.setItem(row, "AR_AMT", roundAmt(BIL.chargeTotCTZ(order
					.getItemString(row, "CTZ1_CODE"), order.getItemString(row,
							"CTZ2_CODE"), order.getItemString(row, "CTZ3_CODE"), order
					.getItemString(row, "ORDER_CODE"), order.getItemDouble(row,
							"DOSAGE_QTY"), serviceLevel)));

			order.setActive(row, true);
		}
		order.newOrder(EXA, rxNo);
		initSetTable(TABLE_EXA, true);
		order.itemNow = false;
		// tblExa.getTable().grabFocus();
		// tblExa.setSelectedRow(0);
		// tblExa.setSelectedColumn(tblExa.getColumnIndex("EXEC_DEPT_CODE"));
	}

	/**
	 * 插入处置模板
	 * 
	 * @param parm
	 *            TParm
	 */
	private void insertOpPack(TParm parm) {
		TTable table = (TTable) this.getComponent(TABLE_OP);
		OpdOrder order = odo.getOpdOrder();
		String rxNo = this.getValueString(OP_RX); // 处方签号
		if (StringUtil.isNullString(rxNo)) {
			initRx(OP_RX, OP);
		}
		rxNo = this.getValueString("OP_RX");
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		// 获取回传值的个数
		int count = parm.getCount("ORDER_CODE");
		// 循环处理每条 诊疗项目
		for (int i = 0; i < count; i++) {
			int row = -1;
			if (!StringUtil.isNullString(order.getItemString(
					order.rowCount() - 1, "ORDER_CODE"))) {
				row = order.newOrder(OP, rxNo);
			} else {
				row = order.rowCount() - 1;
			}
			order.itemNow = true;
			order.sysFee.setFilter("ORDER_CODE='"
					+ parm.getValue("ORDER_CODE", i) + "'");
			order.sysFee.filter();
			TParm sysFeeParm = order.sysFee.getRowParm(0);
			String orderCode = sysFeeParm.getValue("ORDER_CODE");
			// 新建一行 诊疗项目
			order.newOpOrder(rxNo, orderCode, ctz, row);
			// 判断模板传回的信息中是否有 执行科室
			// 如果有执行科室 那么用模板中的执行科室
			String execDept = "";
			if (!StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE", i))) {
				execDept = parm.getValue("EXEC_DEPT_CODE", i);
			}
			// 如果模板中没有执行科室 那么使用sys_fee中的执行科室
			else if (!StringUtil.isNullString(sysFeeParm
					.getValue("EXEC_DEPT_CODE"))) {
				execDept = sysFeeParm.getValue("EXEC_DEPT_CODE");
			} else { // 如果sys_fee中也没有执行科室 那么使用当前用户的算在科室
				execDept = Operator.getDept();
			}
			order.setItem(row, "EXEC_DEPT_CODE", execDept); // 执行科室
			order.setItem(row, "ORDER_DESC", sysFeeParm.getValue("ORDER_DESC")
					.replaceFirst(
							"(" + sysFeeParm.getValue("SPECIFICATION") + ")",
							"")); // 医嘱名称
			order.setItem(row, "CTZ1_CODE", ctz[0]);
			order.setItem(row, "CTZ2_CODE", ctz[1]);
			order.setItem(row, "CTZ3_CODE", ctz[2]);
			order.itemNow = false; // 开启总量计算
			if(!StringUtil.isNullString(parm.getValue("MEDI_QTY", i)) && parm.getDouble("MEDI_QTY", i)>0){
				order.setItem(row, "MEDI_QTY", parm.getValue("MEDI_QTY", i));
			}
			if(!StringUtil.isNullString(parm.getValue("MEDI_UNIT", i))){
				order.setItem(row, "MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
			}
			if(!StringUtil.isNullString(parm.getValue("TAKE_DAYS", i)) && parm.getDouble("TAKE_DAYS", i)>0){
				order.setItem(row, "TAKE_DAYS", parm.getValue("TAKE_DAYS", i));
			}
			if(!StringUtil.isNullString(parm.getValue("LINKMAIN_FLG", i))){
				order
				.setItem(row, "LINKMAIN_FLG", parm.getValue("LINKMAIN_FLG",
						i));
				order.setItem(row, "LINK_NO", parm.getValue("LINK_NO", i));
			}
			

			// add by wangqing 20171024 start 口头医嘱
			order.setItem(row, "ONW_ORDER_FLG", parm.getValue("ONW_ORDER_FLG", i));// 口头医嘱标识
			order.setItem(row, "ONW_TRIAGE_NO", parm.getValue("TRIAGE_NO", i));// 检伤号
			order.setItem(row, "ONW_ORDER_SEQ", parm.getValue("SEQ_NO", i));// 医嘱序号
			// add by wangqing 20171024 end
			order.setActive(row, true);
		}
		order.newOrder(OP, rxNo);
		this.initNoSetTable(rxNo, TABLE_OP, false,false);
		// table.getTable().grabFocus();
		Map insColor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);
		table.setRowTextColorMap(insColor);
		this.calculateCash(TABLE_OP, "OP_AMT");
	}

	/**
	 * 插入中医的模板数据
	 * 
	 * @param parmChn
	 *            TParm
	 */
	private void insertChnPack(TParm parmChn) {
		if (parmChn == null) {
			return;
		}
		OpdOrder order = odo.getOpdOrder();
		// 获取处方签号
		String rxNo = this.getValueString(CHN_RX);
		if (StringUtil.isNullString(rxNo)) {
			initRx(CHN_RX, CHN);
		}
		rxNo = this.getValueString(CHN_RX);
		rxType = CHN;
		int count = parmChn.getCount();
		for (int i = 0; i < count; i++) {
			String orderCode = parmChn.getValue("ORDER_CODE", i);
			TParm phaBase = PhaBaseTool.getInstance().selectByOrder(orderCode);
			TParm sysFee = new TParm(TJDODBTool.getInstance().select(
					SYSSQL.getSYSFee(orderCode)));
			TParm sysFeeParm = sysFee.getRow(0);
			// 判断模板传回的信息中是否有 执行科室
			// 如果有执行科室 那么用模板中的执行科室
			String execDept = "";
			if (!StringUtil.isNullString(parmChn.getValue("EXEC_DEPT_CODE", i))) {
				execDept = parmChn.getValue("EXEC_DEPT_CODE", i);
			} else if (!StringUtil.isNullString(getValue("CHN_EXEC_DEPT_CODE").toString())) {
				execDept = getValue("CHN_EXEC_DEPT_CODE").toString();
			}

			// 如果模板中没有执行科室 那么使用sys_fee中的执行科室
			else if (!StringUtil.isNullString(sysFeeParm
					.getValue("EXEC_DEPT_CODE"))) {
				execDept = sysFeeParm.getValue("EXEC_DEPT_CODE");
			} else { // 如果sys_fee中也没有执行科室 那么使用当前用户的算在科室
				execDept = Operator.getDept();
			}
			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			int row = 0;
			for (int h = 0; h < order.rowCount(); h++) {
				if (!StringUtil.isNullString(order.getItemString(h,
						"ORDER_CODE"))) {
					row = h + 1;
				}
			}
			sysFeeParm.setData("EXEC_DEPT_CODE", execDept);
			this.initOrder(order, row, sysFeeParm, phaBase);
			TParm price = OdoUtil.getPrice(order.getItemString(row,
					"ORDER_CODE"));
			order.setItem(row, "OWN_PRICE", price.getDouble("OWN_PRICE"));
			order.setItem(row, "CHARGE_HOSP_CODE", price
					.getValue("CHARGE_HOSP_CODE"));
			double ownAmt = roundAmt(TypeTool.getDouble(price
					.getData("OWN_PRICE"))
					* order.getItemDouble(row, "DOSAGE_QTY"));
			double arAmt = roundAmt(BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
					orderCode, order.getItemDouble(row, "DOSAGE_QTY"),
					serviceLevel));
			order.setItem(row, "DISCN_RATE", BIL.getOwnRate(ctz[0], ctz[1],
					ctz[2], price.getValue("CHARGE_HOSP_CODE"), order.getItemString(row,
							"ORDER_CODE")));
			order.setItem(row, "OWN_AMT", ownAmt);
			order.setItem(row, "AR_AMT", arAmt);
			order.setItem(row, "PAYAMOUNT", ownAmt - arAmt);

			order.setItem(row, "EXEC_DEPT_CODE", execDept);
			order.setItem(row, "ORDER_CAT1_CODE", "PHA_G");
			order.setItem(row, "DCT_TAKE_QTY", parmChn.getValue("DCT_TAKE_QTY",
					i));
			order.itemNow = false;
			order.setItem(row, "TAKE_DAYS", parmChn.getValue("TAKE_DAYS", i));
			order.itemNow = false;
			order.setItem(row, "FREQ_CODE", parmChn.getValue("FREQ_CODE", i));
			order.itemNow = false;
			order.setItem(row, "MEDI_QTY", parmChn.getValue("MEDI_QTY", i));
			order.setItem(row, "DCTEXCEP_CODE", parmChn.getValue("DCTEXCEP_CODE", i));
			order.setItem(row, "MEDI_UNIT", parmChn.getValue("MEDI_UNIT", i));
			order.setItem(row, "ROUTE_CODE", parmChn.getValue("ROUTE_CODE", i));
			order.setItem(row, "DCTAGENT_CODE", parmChn.getValue(
					"DCTAGENT_CODE", i));
			order.setItem(row, "DR_NOTE", parmChn.getValue("DR_NOTE", i));
			// add by wangqing 20171024 start
			// 口头医嘱
			order.setItem(row, "ONW_ORDER_FLG", parmChn.getValue("ONW_ORDER_FLG", i));// 口头医嘱标识
			order.setItem(row, "ONW_TRIAGE_NO", parmChn.getValue("TRIAGE_NO", i));// 检伤号
			order.setItem(row, "ONW_ORDER_SEQ", parmChn.getValue("SEQ_NO", i));// 医嘱序号
			// add by wangqing 20171024 end
			order.setActive(row, true);
			order.newOrder(CHN, rxNo);
		}
		initChnTable(rxNo);
	}

	/**
	 * 设置主诉客诉现病史点击时的获得控件的TAB
	 * 
	 * @param focusTag
	 *            String
	 */
	public void setFocusTag(String focusTag) {
		this.focusTag = focusTag;
	}

	/**
	 * 得到主诉客诉现病史点击时的获得控件的TAB
	 * 
	 * @return focusTag String
	 */
	public String getFocusTag() {
		return this.focusTag;
	}

	/**
	 * 主诉客诉现病史的点击事件，设置当前此三个控件获得焦点的控件，为了片语返回值使用
	 * 
	 * @param tag
	 *            String
	 */
	public void onClick(String tag) {
		this.setFocusTag(tag);
	}

	/**
	 * 为了保存模板公开的方法
	 * 
	 * @return odo ODO
	 */
	public ODO getOdo() {
		return this.odo;
	}

	/**
	 * 存模板
	 */
	public void onSaveTemplate() {
		TParm parm = new TParm();
		parm.setData("ODO", odo);
		Object re = this.openDialog(
				"%ROOT%\\config\\opd\\OPDComPackEnterName.x", parm, false);
		if (TypeTool.getBoolean(re)) {
			this.messageBox("P0005");
		}
	}

	/**
	 * 显示引用表单界面
	 */
	public void onShowQuoteSheet() {
		TTabbedPane orderPane = (TTabbedPane) this
				.getComponent("TTABPANELORDER");
		if (orderPane.getSelectedIndex() != 0) {
			//======pangben 2013-4-25 添加默认选中检验检查页签，解决在其他页签时，提示消息
			orderPane.setSelectedIndex(0);//===默认西药页签
			onChangeOrderTab();//页签切换
		}
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\sys\\SysExaSheetTree.x", this, true);
		window.setVisible(true);
	}

	/**
	 * 调用引用表单界面被该界面调用的增加检验检查的方法
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onQuoteSheet(Object obj) {
		//=======pangben 2013-1-11 添加校验
		TTabbedPane orderPane = (TTabbedPane) this
				.getComponent("TTABPANELORDER");
		if (orderPane.getSelectedIndex() != 0) {
			this.messageBox("E0072");
			return false;
		}
		String rxNo = this.getValueString(EXA_RX);
		if (StringUtil.isNullString(rxNo)) {
			return false;
		}
		if (!(obj instanceof TParm))
			return false;
		TParm sysFee = (TParm) obj;
		TTable table = (TTable) this.getComponent(TABLE_EXA);
		// ============pangben 2012-7-12 添加管控
		if (!getCheckRxNo()) {
			return false;
		}
		insertExa(sysFee, table.getRowCount() - 1, 0);
		return true;
	}

	/**
	 * 弹出申请单
	 */
	public void onEmr() {
		if (odo == null)
			return;

		// 拿到申请单结果集
		TParm emrParm = new TParm();
		TParm actionParm = new TParm();
		if (admType.equals("O")) {
			emrParm = OrderUtil.getInstance().getOrderPasEMRAll(
					odo.getOpdOrder(), "ODO");
			actionParm.setData("SYSTEM_CODE", "ODO");
		} else if (admType.equals("E")) {
			emrParm = OrderUtil.getInstance().getOrderPasEMRAll(
					odo.getOpdOrder(), "EMG");
			actionParm.setData("SYSTEM_CODE", "EMG");
		}
		if (emrParm.getInt("ACTION", "COUNT") > 0) {
			actionParm.setData("ADM_TYPE", admType);
			actionParm.setData("MR_NO", this.pat.getMrNo());
			if ("en".equals(this.getLanguage())) // 如果是英文 那么取英文姓名
				actionParm.setData("PAT_NAME", this.pat.getName1());
			else
				actionParm.setData("PAT_NAME", this.pat.getName());
			actionParm.setData("CASE_NO", odo.getCaseNo());
			actionParm.setData("IPD_NO", "");
			actionParm.setData("ADM_DATE", odo.getRegPatAdm().getItemData(0,
					"ADM_DATE"));
			actionParm.setData("DEPT_CODE", Operator.getDept());
			actionParm.setData("STYLETYPE", "1");
			actionParm.setData("RULETYPE", "2");
			actionParm.setData("EMR_DATA_LIST", emrParm);
			String sql="SELECT ERD_LEVEL FROM REG_PATADM WHERE CASE_NO='"+caseNo+"'";
			TParm result=new TParm(TJDODBTool.getInstance().select(sql));
			actionParm.setData("ERD_LEVEL", result.getValue("ERD_LEVEL", 0));//wanglong add 20150407
			// System.out.println("for Emr parm"+actionParm);
			// this.openDialog("%ROOT%\\config\\emr\\EMRSingleUI.x",actionParm);
			this.openWindow("%ROOT%\\config\\emr\\EMRSingleUI.x", actionParm);
		}
	}

	/**
	 * 西成药TABLE点击药品名称弹出合理化用药查询界面
	 */
	public void onQueryRsDrug() {
		TTable table = (TTable) this.getComponent(TABLE_MED);
		int row = table.getSelectedRow();
		String columnName = table.getParmMap(table.getSelectedColumn());

		// ============xueyf modify 20120309
		if (!("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName) || "ORDER_ENG_DESC"
				.equalsIgnoreCase(columnName)))
			return;
		// String orderDesc=odo.getOpdOrder().getItemString(row, "ORDER_DESC");
		// ("2C010001","25%葡萄糖酸钙针","",""));
		PassDriver.PassSetQueryDrug(odo.getOpdOrder().getItemString(row,
				"ORDER_CODE"), odo.getOpdOrder().getItemString(row,
						"ORDER_DESC"), "", "");
		PassDriver.PassDoCommand(401);
	}

	/**
	 * 住院预约
	 */
	public void onPreDate() {
		if (odo == null) {
			return;
		}
		//根据挂号参数中有效天数校验是否可以就诊，添加医嘱操作===========pangben 2013-4-28
		if(!OPBTool.getInstance().canEdit(reg, regSysEFFParm)){
			this.messageBox("超过当前就诊时间");
			this.onClear();
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", odo.getMrNo());
		parm.setData("ADM_TYPE_ZYZ",admType);//yanj,20130816,门急诊时住院证打印完自动关闭
		if ("O".equalsIgnoreCase(admType)) {
			parm.setData("ADM_SOURCE", "01");
			parm.setData("CASE_NO", caseNo);//add by wangb 20160125 门诊与住院绑定
		} else if ("E".equalsIgnoreCase(admType)) {
			parm.setData("ADM_SOURCE", "02");
			parm.setData("CASE_NO", caseNo);//add by wanglong 20121025
		}
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("DR_CODE", Operator.getID());
		int mainDiag = odo.getDiagrec().getMainDiag();
		if (mainDiag >= 0) {
			parm.setData("ICD_CODE", odo.getDiagrec().getItemString(mainDiag,
					"ICD_CODE"));
			parm.setData("DESCRIPTION", odo.getDiagrec().getItemString(
					mainDiag, "DIAG_NOTE"));
		} else {
			parm.setData("ICD_CODE", "");
			parm.setData("DESCRIPTION", "");
		}
		parm.setData("ADM_EXE_FLG","Y");//=====pangben 2013-4-26 医生站执行操作减少步骤
		this.openWindow("%ROOT%\\config\\adm\\ADMResv.x", parm);
	}

	/**
	 * 急诊留观
	 */
	public void onErd() {
		if (odo == null) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", odo.getCaseNo());
		parm.setData("MR_NO", odo.getMrNo());
		parm.setData("PAT_NAME", odo.getPatInfo().getItemString(0, "PAT_NAME"));
		parm.setData("FLG", "OPD");
		
		// add by wangqing 20170704 start
		TParm result11 = EMRAMITool.getInstance().getErdEvalutionDataByCaseNo(caseNo);
		String triageNo11 = result11.getValue("TRIAGE_NO",0);
		if(triageNo11 == null || triageNo11.trim().length()<=0){
			return;
		}
		parm.setData("TRIAGE_NO", triageNo11);
		// add by wangqing 20170704 end
		
		this.openDialog("%ROOT%\\config\\erd\\ERDDynamicRcd2.x", parm);
	}

	/**
	 * 保存前使每个TABLE都没有编辑状态
	 */
	private void acceptForSave() {
		TTable table = (TTable) getComponent(TABLE_CHN);
		table.acceptText();
		table = (TTable) getComponent(TABLE_CTRL);
		table.acceptText();
		table = (TTable) getComponent(TABLE_EXA);
		table.acceptText();
		table = (TTable) getComponent(TABLE_MED);
		table.acceptText();
		table = (TTable) getComponent(TABLE_OP);
		table.acceptText();
		table = (TTable) getComponent(TABLEALLERGY);
		table.acceptText();
		table = (TTable) getComponent(TABLEDIAGNOSIS);
		table.acceptText();
		table = (TTable) getComponent(TABLEMEDHISTORY);
		table.acceptText();
	}

	/**
	 * 就诊记录
	 */
	public void onCaseHistory() {
		if (odo == null) {
			return;
		}
		//======pangben 2012-7-24 添加默认选中西药页签，解决在检验检查页签时，药品页签出现检验检查主项问题
		TTabbedPane orderPane = (TTabbedPane) this.getComponent("TTABPANELORDER");
		if (orderPane.getSelectedIndex()!=2) {
			orderPane.setSelectedIndex(2);//===默认西药页签
			onChangeOrderTab();//页签切换	
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", odo.getMrNo());
		parm.setData("ADM_TYPE", odo.getAdmType());
		Object obj = this.openDialog("%ROOT%\\config\\opd\\OPDCaseHistory.x",
				parm);
		if (obj == null) {
			return;
		}
		if (!(obj instanceof TParm)) {
			return;
		}
		if (Operator.getSpcFlg().equals("Y")) {//添加物联网校验是否当前处方签已经审核
			if (!checkSpcPha(odo.getOpdOrder())) {
				return;
			}
		}
		//=============pangben 2012-7-24 添加管控
		if (!getCheckRxNo()) {
			return;
		}
		//deleteLisPosc = false;// =============pangben 2012-6-15
		TParm result = (TParm) obj;
		String sub = result.getValue("SUB");
		String objStr = result.getValue("OBJ");
		String phy = result.getValue("PHY");
		String exaR = result.getValue("EXA_R");// =========pangben 2012-6-28 添加检查结果\建议回传值显示
		String pro = result.getValue("PRO");
		// ======xueyf start 主诉现病史回传控制
		if (word != null) {
			if (!StringUtil.isNullString(sub)) {
				word.clearCapture("SUB");
				word.pasteString(sub);
			}
			if (!StringUtil.isNullString(objStr)) {
				word.clearCapture("OBJ");
				word.pasteString(objStr);
			}
			if (!StringUtil.isNullString(phy)) {
				word.clearCapture("PHY");
				word.pasteString(phy);
			}
			// 检查结果
			if (!StringUtil.isNullString(exaR)) {
				word.clearCapture("EXA_RESULT");
				word.pasteString(exaR);
			}
			// 建议
			if (!StringUtil.isNullString(pro)) {
				word.clearCapture("PROPOSAL");
				word.pasteString(pro);
			}
		}
		// ======xueyf stop
		// 取得诊断
		TParm diagResult = ((TParm) result.getData("DIAG"));
		if (diagResult != null) {
			int count = diagResult.getCount("ICD_CODE");
			for (int i = 0; i < count; i++) {
				tblDiag.setSelectedRow(tblDiag.getRowCount() - 1);
				this.popDiagReturn("", diagResult.getRow(i));
			}
		}

		// 检验检查
		TParm exaResult = ((TParm) result.getData("EXA"));
		if (exaResult != null && exaResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_EXA;
			int count = exaResult.getCount("ORDER_CODE");
			for (int i = 0; i < count; i++) {
				insertExaPack(exaResult.getRow(i));
			}
		}
		// 处置
		TParm opResult = ((TParm) result.getData("OP"));
		// System.out.println("opResult="+opResult);
		if (opResult != null && opResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_OP;
			insertOpPack(opResult);
		}
		// 药品
		TParm orderResult = ((TParm) result.getData("MED"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_MED;
			//			System.out.println("222orderResult is :"+orderResult);
			insertOrdPack(orderResult,true);
		}
		// 管制药品
		orderResult = ((TParm) result.getData("CTRL"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_CTRL;
			insertCtrlPack(orderResult);
		}
		Map chnResult = ((Map) result.getData("CHN"));
		if (chnResult != null) {
			Iterator it = chnResult.values().iterator();
			while (it.hasNext()) {
				TParm chn = (TParm) it.next();
				// System.out.println("chn="+chn);
				insertChnPack(chn);
			}
		}
	}

	/**
	 * 检验报告
	 */
	public void onLisReport() {
		if (odo == null) {
			return;
		}
		SystemTool.getInstance().OpenLisWeb(odo.getMrNo());
	}

	/**
	 * 检查报告
	 */
	public void onRisReport() {
		// 调用检查接口
		if (odo == null) {
			return;
		}
		SystemTool.getInstance().OpenRisWeb(odo.getMrNo());
	}

	/**
	 * 调用传染病报告卡
	 */
	public void onContagionReport() {
		if (this.tblDiag == null || odo == null) {
			return;
		}
		int row = this.tblDiag.getSelectedRow();
		if (row < 0) {
			return;
		}
		Diagrec diag = odo.getDiagrec();
		if (diag.isContagion(row)) {
			TParm can = new TParm();
			can.setData("MR_NO", odo.getMrNo());
			can.setData("CASE_NO", odo.getCaseNo());
			can.setData("ICD_CODE", diag.getItemString(row, "ICD_CODE"));
			can.setData("DEPT_CODE", Operator.getDept());
			can.setData("USER_NAME", Operator.getName());
			can.setData("ADM_TYPE", this.admType);
			this.openDialog("%ROOT%/config/mro/MROInfect.x", can);
		}
	}

	/**
	 * 调用体温单
	 */
	public void onBodyTemp() {
		if (odo == null) {
			return;
		}
		String sql = " SELECT B.CHN_DESC,A.BED_DESC FROM ERD_BED A,SYS_DICTIONARY B "
				+ " WHERE B.GROUP_ID='ERD_REGION' "
				+ " AND B.ID=A.ERD_REGION_CODE "
				+ " AND A.CASE_NO='"
				+ odo.getCaseNo() + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		TParm sumParm = new TParm();

		sumParm.setData("SUM", "CASE_NO", odo.getCaseNo());
		sumParm.setData("SUM", "MR_NO", odo.getMrNo());
		sumParm.setData("SUM", "IPD_NO", "");
		sumParm.setData("SUM", "STATION_CODE", result.getData("CHN_DESC", 0));
		sumParm.setData("SUM", "BED_NO", result.getData("BED_DESC", 0));
		sumParm.setData("SUM", "ADM_TYPE", "E");

		this.openDialog("%ROOT%\\config\\sum\\SUMVitalSign.x", sumParm);
	}

	/**
	 * 调用留观病历
	 */
	public void onErdSheet() {
		TParm parm = new TParm();
		parm.setData("ADM_TYPE_ZYZ", admType);//yanj,20130820
		if ("O".equalsIgnoreCase(this.admType)) {
			parm.setData("SYSTEM_TYPE", "ODO");
			parm.setData("ADM_TYPE", "O");
		} else {
			parm.setData("SYSTEM_TYPE", "EMG");
			parm.setData("ADM_TYPE", "E");
		}

		parm.setData("CASE_NO", odo.getCaseNo());
		parm.setData("PAT_NAME", odo.getPatInfo().getItemString(0, "PAT_NAME"));
		parm.setData("MR_NO", odo.getMrNo());
		parm.setData("IPD_NO", "");
		parm.setData("ADM_DATE", odo.getRegPatAdm().getItemData(0, "ADM_DATE"));
		parm.setData("DEPT_CODE", Operator.getDept());
		// parm.setData("STYLETYPE","1");
		parm.setData("RULETYPE", "2");
		parm.setData("EMR_DATA_LIST", new TParm());
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
	}

	/**
	 * 结构化病历调用片语
	 */
	public void onInsertPY() {
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("ROLE", "1");
		inParm.setData("DR_CODE", Operator.getID());
		inParm.setData("DEPT_CODE", Operator.getDept());
		inParm.addListener("onReturnContent", this, "onReturnContent");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRComPhraseQuote.x", inParm, true);
		window.setVisible(true);
		word.grabFocus();
	}

	/**
	 * 结构化病历调用检验检查结果
	 */
	public void onInsertResult() {
		TParm inParm = new TParm();
		inParm.setData("CASE_NO", odo.getCaseNo());
		inParm.addListener("onReturnContent", this, "onReturnContent");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRMEDDataUI.x", inParm, true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);
	}

	/**
	 * 结构化病例调用片语
	 */
	public void onInsertFamilyPY() {
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("ROLE", "1");
		inParm.setData("DR_CODE", Operator.getID());
		inParm.setData("DEPT_CODE", Operator.getDept());
		inParm.addListener("onReturnContent", this, "onFamilyReturn");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRComPhraseQuote.x", inParm, true);
		window.setVisible(true);
		this.familyWord.grabFocus();

	}

	/**
	 * 片语记录界面回传值
	 * 
	 * @param value
	 *            String
	 */
	public void onReturnContent(String value) {
		String str = "";
		if (word.focusInCaptue("SUB")) {
			str = "SUB";
		} else if (word.focusInCaptue("OBJ")) {
			str = "OBJ";
		} else if (word.focusInCaptue("PHY")) {
			str = "PHY";
		} else if (word.focusInCaptue("EXA_RESULT")) {
			str = "EXA_RESULT";
		} else if (word.focusInCaptue("PROPOSAL")) {
			str = "PROPOSAL";
		}
		if (StringUtil.isNullString(str)) {
			return;
		}
		if (!this.word.pasteString(value)) {
			this.messageBox("E0035");
		}
	}

	/**
	 * 片语记录界面回传值
	 * 
	 * @param value
	 *            String
	 */
	public void onFamilyReturn(String value) {

		String str = "";
		if (this.familyWord.focusInCaptue("FAMILY_HISTORY")) {
			str = "FAMILY_HISTORY";
		}
		if (StringUtil.isNullString(str)) {
			return;
		}
		if (!this.familyWord.pasteString(value)) {
			this.messageBox("E0035");
		}

	}

	/**
	 * 调用医嘱单
	 */
	public void onOrderSheet() {
		TParm parm = new TParm();
		parm.setData("INW", "CASE_NO", odo.getCaseNo());
		this.openDialog("%ROOT%\\config\\erd\\ERDOrderSheetPrtAndPreView.x",
				parm);
	}

	/**
	 * 手术申请
	 */
	public void onOpApply() {
		TParm parm = new TParm();
		parm.setData("MR_NO", odo.getPatInfo().getItemString(0, "MR_NO"));
		// CASE_NO
		parm.setData("CASE_NO", odo.getCaseNo());
		// ADM_TYPE
		parm.setData("ADM_TYPE", odo.getRegPatAdm()
				.getItemString(0, "ADM_TYPE"));
		// BOOK_DEPT_CODE
		parm.setData("BOOK_DEPT_CODE", Operator.getDept());
		// STATION_CODE
		parm.setData("STATION_CODE", Operator.getStation());
		// BOOK_DR_CODE
		parm.setData("BOOK_DR_CODE", Operator.getID());
		// ICD_CODE
		int i = odo.getDiagrec().getMainDiag();
		String icdCode = "";
		if (i >= 0) {
			icdCode = odo.getDiagrec().getItemString(i, "ICD_CODE");
		}
		parm.setData("ICD_CODE", icdCode);
		this.openWindow("%ROOT%\\config\\ope\\OPEOpBook.x", parm);
	}

	/**
	 * 手术记录
	 */
	public void onOpRecord() {
		if (odo == null) {
			return;
		}
		if (StringUtil.isNullString(odo.getCaseNo())) {
			return;
		}
		TParm parmR = new TParm();
		parmR.setData("SYSTEM", "OPD"); // 调用系统简称
		parmR.setData("ADM_TYPE", admType);
		parmR.setData("CASE_NO", odo.getCaseNo());
		parmR.setData("MR_NO", odo.getMrNo());
		this.openDialog("%ROOT%\\config\\ope\\OPEOpDetail.x", parmR);
	}

	/**
	 * 设置语种
	 * 
	 * @param language
	 *            String
	 */
	public void onChangeLanguage(String language) {
		isEng = "en".equalsIgnoreCase(language);
	}

	public static void main(String[] args) {
		com.javahis.util.JavaHisDebug.TBuilder();
		Operator.setData("D001", "HIS", "127.0.0.1", "20803", "001");
	}

	/**
	 * 排队叫号
	 */
	public void onCallNo() {
		if (tblPat == null) {
			return;
		}
		int row = tblPat.getSelectedRow();
		if (row < 0) {
			return;
		}
		TParm parm = tblPat.getParmValue();
		if (parm == null || parm.getCount() <= 0) {
			return;
		}
		CallNo callUtil = new CallNo();
		// System.out.println();
		if (!callUtil.init()) {
			return;
		}
		//叫号同步
		String caseNo = parm.getValue("CASE_NO", row);
		String patName = parm.getValue("PAT_NAME", row);
		String drID = Operator.getID();
		String ip = Operator.getIP();
		if (StringUtil.isNullString(caseNo) || StringUtil.isNullString(patName)
				|| StringUtil.isNullString(drID) || StringUtil.isNullString(ip)) {
			this.messageBox("E0133");
			return;
		}
		String result = callUtil.CallClinicMaster("", caseNo, "", "", patName,
				"", "", drID, ip);
		if ("true".equalsIgnoreCase(result)) {

			this.messageBox("P0119");
			return;
		}
		this.messageBox("E0133");
	}



	/**
	 * 关闭事件
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		if (odo == null) {
			return true;
		}
		unLockPat();
		SynLogin("0"); // 退出叫号登陆
		return true;
	}

	/**
	 * 预约挂号
	 */
	public void onReg() {
		if (odo == null)   
			return; 
		String MR_NO = odo.getMrNo();
		TParm parm = new TParm(); 
		parm.setData("MR_NO", MR_NO);
		parm.setData("NHI_NO", pat.getNhiNo());// 医保卡号
		parm.setData("ADM_TYPE", admType); //add by huangtt 20131203
		this.openWindow("%ROOT%\\config\\reg\\REGAdmForDr.x", parm);
	}

	//fux modify 201311014预约挂号明细
	/**
	 * 预约挂号明细查询   
	 */   
	public void onRegDetail() {    
		if (tblPat == null) {
			return;  
		}    
		//int row = tblPat.getSelectedRow(); 
		TParm parm = new TParm();     
		parm.setData("ADM_DATE", this.getValueString("ADM_DATE")); 
		parm.setData("SESSION_CODE", this.getValueString("SESSION_CODE"));
		parm.setData("CLINICROOM_NO", this.getValueString("CLINICROOM"));     
		//新加入界面        
		this.openWindow("%ROOT%\\config\\reg\\REGAdmForDetail.x",parm);
	}


	/**
	 * 叫号登陆方法
	 * 
	 * @param type
	 *            String 1登陆,0退出
	 */
	private void SynLogin(String type) {
		CallNo callUtil = new CallNo();
		if (!callUtil.init()) {
			return;
		}
	}

	/**
	 * 医疗卡读卡
	 */
	public void onEKT() {
		isReadEKT = true;
		// 执行医疗卡操作，判断是否已经可以使用医疗卡
		boolean isMrNoNull = StringUtil
				.isNullString((String) getValue("MR_NO"));
		if (null == caseNo || isMrNoNull) {
			this.messageBox("请选择一个病患");
			return;
		}

		ektReadParm = EKTIO.getInstance().TXreadEKT();
		if (ektReadParm.getErrCode() < 0) {
			this.messageBox("医疗卡读卡有误。");
			return;
		}
		if (!ektReadParm.getValue("MR_NO").equals(getValue("MR_NO"))) {
			this.messageBox("病患信息不符,此医疗卡病患名称为:"
					+ ektReadParm.getValue("PAT_NAME"));
			// // 当现在有病人的时候 读医疗卡只是用来对比 卡片是否属于该病人
			ektReadParm.setData("SEX",
					ektReadParm.getValue("SEX_CODE").equals("1") ? "男" : "女");
			this.openDialog("%ROOT%\\config\\ekt\\EKTInfoUI.x", ektReadParm);
			ektReadParm = null;
			return;
		}

		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		// parm.setData("MR_NO", getValue("MR_NO"));
		TParm reg = OPDAbnormalRegTool.getInstance().selectRegForOPD(parm);
		if (reg.getCount("CASE_NO") > 1) {
			TParm re = (TParm) this.openDialog(
					"%ROOT%\\config\\opd\\OPDRegChoose.x", reg);
			if (re == null)
				return;
			TParm result = new TParm();
			result.setRowData(0, re);
			wc = "W"; // 默认为西医
			// ============xueyf modify 20120227 start
			if (isMrNoNull) {
				this.initOpd(result, 0);
			}
		} else if (reg.getCount("CASE_NO") == 1 && isMrNoNull) {
			wc = "W"; // 默认为西医
			this.initOpd(reg, 0);
			// ============xueyf modify 20120227 stop
		}
		this.setValue("LBL_EKT_MESSAGE", "已读卡");//====pangben 2013-3-19 添加读卡
		ekt_lable.setForeground(green);//======yanjing 2013-06-14设置读卡颜色
		//isEKTFee = true;
		// }
		// }
		// txReadEKT();
	}
	/**
	 * 病患解锁
	 */
	public void unLockPat() {
		if (pat == null)
			return;
		String odo_type = "ODO";
		if ("E".equals(admType)) {
			odo_type = "ODE";
		}
		// 判断是否加锁
		//		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
		//			TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
		//			if (odo_type.equals(parm.getValue("PRG_ID", 0))
		//					&& (Operator.getIP().equals(parm.getValue("OPT_TERM", 0)))
		//					&& (Operator.getID().equals(parm.getValue("OPT_USER", 0))))
		//				PatTool.getInstance().unLockPat(pat.getMrNo());
		//		}
		pat = null;
	}

	// add by zhangk
	/**
	 * 分离HL7接口的参数 返回新增的 或 取消的 检验检查列表
	 * 
	 * @param HL7
	 *            List
	 * @param flg
	 *            String 0:新增 1:取消
	 * @return List
	 */
	public List getHL7List(List HL7, String flg) {
		List HL7List = new ArrayList();
		Iterator it = HL7.iterator();
		while (it.hasNext()) {
			TParm re = (TParm) it.next();
			if (flg.equals(re.getValue("FLG"))) {
				HL7List.add(re);
			}
		}
		return HL7List;
	}

	/**
	 * 四舍五入 保留两位小数
	 * 
	 * @param value
	 *            double
	 * @return double
	 */
	public double roundAmt(double value) {
		double result = 0;
		if (value > 0)
			result = ((int) (value * 100.0 + 0.5)) / 100.0;
		else if (value < 0)
			result = ((int) (value * 100.0 - 0.5)) / 100.0;
		return result;
	}

	/**
	 * 判断是否可以修改医嘱 门诊：ADM_DATE的当天23点59分59秒以后不可以再对病患的医嘱进行修改（可以删除）
	 * 急诊：ADM_DATE的三天后的23点59分59面以后不可以再对病患的医嘱进行修改（可以删除）
	 * 
	 * @return boolean
	 */
	private boolean canEdit() {
		Timestamp admDate = reg.getAdmDate(); // 挂号日期
		Timestamp now = SystemTool.getInstance().getDate(); // 当前时间
		if ("O".equals(admType)) {
			// 获取挂号当天23点59分59秒的时间（门诊修改医嘱的限定时间）
			Timestamp time = StringTool.getTimestamp(StringTool.getString(
					admDate, "yyyyMMdd")
					+ "235959", "yyyyMMddHHmmss");
			// 当前时间晚于修改医嘱的限定时间 则不可以修改
			if (now.getTime() > time.getTime()) {
				return false;
			}
		} else if ("E".equals(admType)) {
			// 急诊 获取医生站参数档中“限定看诊天数”的23点59分59秒的时间（急诊修改医嘱的限定时间）
			// 急诊晚班 是特殊情况 例如3/5 晚上挂号，一直到 3/6 全天都视为有效
			int OPDDay = OPDSysParmTool.getInstance().getEDays();
			String sessionCode = this.getValueString("SESSION_CODE");
			if (sessionCode.equalsIgnoreCase("8") && OPDDay == 0) { // 急诊晚班
				OPDDay++;
			}
			Timestamp time = StringTool.getTimestamp(StringTool.getString(
					StringTool.rollDate(admDate, OPDDay), "yyyyMMdd")
					+ "235959", "yyyyMMddHHmmss");
			// 当前时间晚于修改医嘱的限定时间 则不可以修改
			if (now.getTime() > time.getTime()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否可以保存
	 * 
	 * @return boolean
	 */
	private boolean canSave() {
		// 超出规定看诊时间 不可新增 不可修改
		if (!canEdit()) {
			OpdOrder order = odo.getOpdOrder();
			int[] newRows = order.getNewRows();
			if (newRows != null && newRows.length > 0) {
				return false; // 有新增行
			}
			int[] updateRows = order.getModifiedRows();
			if (updateRows != null && updateRows.length > 0) {
				return false; // 有修改行
			}
		}
		return true;
	}

	/**
	 * 非常态门诊
	 */
	public void onAbnormalReg() {
		Object obj = this.openDialog("%ROOT%\\config\\opd\\OPDAbnormalReg.x");
		if (obj == null || !(obj instanceof TParm)) {
			return;
		}
		TParm parm = (TParm) obj; // 非常态门诊挂号信息
		wc = "W"; // 默认为西医
		this.initOpd(parm, 0); // 初始化医生站信息
	}

	/**
	 * 权限初始化 是否可以代诊
	 */
	private void initInstradCombo() {
		Object obj = this.getPopedemParm();
		if (obj == null)
			return;
		TParm parm = (TParm) obj;
		for (int i = 0; i < parm.getCount(); i++) {
			if ("INSTEAD".equals(parm.getValue("ID", i))) {
				this.callFunction("UI|INSTEAD_DEPT|setEnabled", true);
			}
		}
	}

	/**
	 * 判断是否检核库存
	 * 
	 * @param orderCode
	 *            String
	 * @return boolean
	 */
	private boolean isCheckKC(String orderCode) {
		String sql = SYSSQL.getSYSFee(orderCode);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getBoolean("IS_REMARK", 0)) // 如果是药品备注那么就不检核库存
			return false;
		else
			// 不是药品备注的 要检核库存
			return true;
	}

	/**
	 * 结构化病历右键调用
	 */
	public void onMouseRightPressed() {
		if (word.focusInCaptue("EXA_RESULT")) {
			word.popupMenu("片语,onInsertPY;|;检查结果,onInsertResult", this);
		}
	}

	/**
	 * 检查药品剂型分类 是否可以开在同一处方签上
	 * 
	 * @param order
	 *            OpdOrder
	 * @param DOSE_TYPE
	 *            String
	 * @return boolean
	 */
	public boolean checkDOSE_TYPE(OpdOrder order, String DOSE_TYPE) {
		boolean flg = true;
		// ORDER_CODE为空表示第一行是空医嘱 是新处方签的开始 所以返回true
		if (order.getItemString(0, "ORDER_CODE").length() <= 0) {
			return flg;
		}
		// ============pangben 2012-2-29 添加管控
		int count = order.rowCount();
		if (count <= 0) {
			return false;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!deleteOrder(order, i, "已经打票的处方签不可以添加医嘱","此医嘱已经登记,不能删除")) {
				return false;

			}
		}
		// ============pangben 2012-2-29 stop
		// 口服处方签
		if (order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("O")) {
			if ("I".equalsIgnoreCase(DOSE_TYPE)
					|| "F".equalsIgnoreCase(DOSE_TYPE)) {
				this.messageBox("E0194"); // 不同剂型分类的药品不可开立在同一张处方签上
				flg = false;
			}
			if ("E".equalsIgnoreCase(DOSE_TYPE)) {
				this.messageBox("E0194"); // 不同剂型分类的药品不可开立在同一张处方签上
				flg = false;
			}
		}
		// 针剂，点滴处方签
		if (order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("I")
				|| order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("F")) {
			if ("E".equalsIgnoreCase(DOSE_TYPE)) {
				this.messageBox("E0194"); // 不同剂型分类的药品不可开立在同一张处方签上
				flg = false;
			}
			if ("O".equalsIgnoreCase(DOSE_TYPE)) {
				this.messageBox("E0194"); // 不同剂型分类的药品不可开立在同一张处方签上
				flg = false;
			}
		}
		// 外用药处方签
		if (order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("E")) {
			if ("I".equalsIgnoreCase(DOSE_TYPE)
					|| "F".equalsIgnoreCase(DOSE_TYPE)) {
				this.messageBox("E0194"); // 不同剂型分类的药品不可开立在同一张处方签上
				flg = false;
			}
			if ("O".equalsIgnoreCase(DOSE_TYPE)) {
				this.messageBox("E0194"); // 不同剂型分类的药品不可开立在同一张处方签上
				flg = false;
			}
		}
		return flg;
	}

	/**
	 * 解锁监听方法
	 * 
	 * @param prgId
	 *            String
	 * @param mrNo
	 *            String
	 * @param prgIdU
	 *            String
	 * @param userId
	 *            String
	 * @return Object
	 */
	public Object onListenPm(String prgId, String mrNo, String prgIdU,
			String userId) {
		if (!"ODO".equalsIgnoreCase(prgId) && !"ODE".equalsIgnoreCase(prgId)) {
			return null;
		}
		TParm parm = new TParm();
		parm.setData("PRG_ID", prgId);
		parm.setData("MR_NO", mrNo);
		parm.setData("PRG_ID_U", prgIdU);
		parm.setData("USE_ID", userId);
		String flg = (String) openDialog(
				"%ROOT%\\config\\sys\\SYSPatUnLcokMessage.x", parm);
		if ("OK".equals(flg)) {
			String odo_type = "ODO";
			if ("E".equals(admType)) {
				odo_type = "ODE";
			}
			// 判断是否加锁
			if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
				TParm parmPat = PatTool.getInstance().getLockPat(pat.getMrNo());
				if (odo_type.equals(parmPat.getValue("PRG_ID", 0))
						&& (Operator.getIP().equals(parmPat.getValue(
								"OPT_TERM", 0)))
						&& (Operator.getID().equals(parmPat.getValue(
								"OPT_USER", 0))))
					PatTool.getInstance().unLockPat(pat.getMrNo());
			}
			this.messageBox("此患者被其他人锁定，主诉现病史可以保存，其他医嘱不可保存");
			// this.closeWindow();
			return "OK";
		}

		return "";
	}

	/**
	 * 纳入既往史
	 */
	public void onImportMedHistory() {
		TTable table = (TTable) this.getComponent(this.TABLEDIAGNOSIS);
		if (table.getSelectedRow() < 0) {
			this.messageBox("请选择纳入既往史的诊断！");
		} else {
			TParm parm = table.getDataStore()
					.getRowParm(table.getSelectedRow());
			if (parm == null || "".equals(parm.getValue("ICD_CODE"))) {
				this.messageBox("不存在纳入既往史的诊断！");
			} else {
				TTable tableMedHistory = (TTable) this
						.getComponent(TABLEMEDHISTORY);
				tableMedHistory.acceptText();
				int rowNo = tableMedHistory.getRowCount() - 1;
				// 判断是否已经超过看诊时限
				if (!canEdit()) {
					tableMedHistory.setDSValue(rowNo);
					this.messageBox_("已超过看诊时间不可修改");
					return;
				}
				if (odo.getMedHistory().isSameICD(parm.getValue("ICD_CODE"))) {
					this.messageBox("E0043"); // 不允许开立重复诊断
					tableMedHistory.setDSValue(rowNo);
					return;
				}
				String oldCode = odo.getMedHistory().getItemString(rowNo,
						"ICD_CODE");
				if (!StringUtil.isNullString(oldCode)) {
					this.messageBox("E0040"); // 不能替代该数据，请重新新增或删除该数据`
					tableMedHistory.setDSValue(rowNo);
					return;
				}
				odo.getMedHistory().setActive(rowNo, true);

				odo.getMedHistory().setItem(rowNo, "ICD_CODE",
						parm.getValue("ICD_CODE"));
				odo.getMedHistory().setItem(rowNo, "ICD_TYPE",
						parm.getValue("ICD_TYPE"));
				odo.getMedHistory().setItem(rowNo, "SEQ_NO",
						odo.getMedHistory().getMaxSEQ(odo.getMrNo()));
				if (rowNo == tableMedHistory.getRowCount() - 1)
					odo.getMedHistory().insertRow();
				tableMedHistory.setDSValue();
				tableMedHistory.getTable().grabFocus();
				tableMedHistory
				.setSelectedRow(odo.getMedHistory().rowCount() - 1);
				tableMedHistory.setSelectedColumn(2);
			}
		}
	}

	// /**
	// * 为合理化用药接口准备病患基本信息
	// * @param patInfo
	// * @param regPatAdm
	// * @return
	// */
	// public String[] getPatInfo(ODO odo){
	// /**
	// * PatientID String 病人病案编号（必须传值）
	// VisitID String 当前就诊次数（必须传值）
	// Name String 病人姓名 （必须传值）
	// Sex String 病人性别 （必须传值）如：男、女，其他值传：未知。
	// Birthday String 出生日期 （必须传值）格式：2005-09-20
	// Weight String 体重 （可以不传值）单位：KG
	// Height String 身高 （可以不传值）单位：CM
	// DepartmentName String 医嘱科室ID/医嘱科室名称 （可以不传值）
	// Doctor String 主治医生ID/主治医生姓名 （可以不传值）
	// LeaveHospitalDate String 出院日期 （可以不传值）
	// */
	// String[] result=new String[10];
	// result[0]=odo.getMrNo();//MR_NO
	// result[1]="1";//当前就诊次数
	// String sexDesc=TJDODbTool.
	// result[2]="";
	// return result;
	// }

	// zhangyong20110616
	/**
	 * 取得成本中心
	 * 
	 * @param dept_code
	 *            String
	 * @return String
	 */
	private String getCostCenter(String dept_code) {
		return DeptTool.getInstance().getCostCenter(dept_code, "");
	}

	/**
	 * 上传EMR
	 * 
	 * @param obj
	 *            Object
	 * @param fileName
	 *            String
	 * @param classCode
	 *            String
	 * @param subClassCode
	 *            String
	 */
	private void saveEMR(Object obj, String fileName, String classCode,
			String subClassCode) {
		EMRTool emrTool = new EMRTool(odo.getCaseNo(), odo.getMrNo(), this);
		emrTool.saveEMR(obj, fileName, classCode, subClassCode);
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
	 * 病历浏览 ==========pangben modify 20110706
	 */
	public void onShow() {
		TTable table = ((TTable) this.getComponent(TABLEPAT));
		if (table.getSelectedRow() < 0) {
			this.messageBox("请选择一个病人");
			return;
		}
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		Runtime run = Runtime.getRuntime();
		try {
			// 得到当前使用的ip地址
			String ip = TIOM_AppServer.SOCKET
					.getServletPath("EMRWebInitServlet?Mr_No=");
			// 连接网页方法
			run.exec("IEXPLORE.EXE " + ip + parm.getValue("MR_NO"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 医疗卡保存
	 * 
	 * @param FLG
	 *            String 执行扣款
	 * @param FLG
	 *            double 总费用
	 * @return boolean ====================pangben 20110915 flg ：false 没有添加执行的医嘱
	 *         true 有添加或删除的医嘱 boolean flg 医生操作修改医嘱使用 true 删除医嘱收费操作 false 添加修改医嘱
	 *         收费
	 */
	private boolean onEktSave(TParm orderParm, TParm ektSumExeParm) {
		int type = 0;
		// 先执行暂存医嘱， 获得医疗卡中历史记录判断此次操作医生是否修改医嘱
		TParm parm = new TParm();
		// TParm detailParm = null;
		// 如果使用医疗卡，并且扣款失败，则返回不保存
		if (EKTIO.getInstance().ektSwitch()) { // 医疗卡开关，记录在后台config文件中
			parm = onOpenCard(orderParm, ektSumExeParm);
			if (parm == null) {
				this.messageBox("E0115");
				return false;
			}
			type = parm.getInt("OP_TYPE");
			if (type == 3) {
				this.messageBox("E0115");
				return false;
			}
			if (type == -1) {
				this.messageBox("读卡错误!");
				return false;
			}
			if (type == 5) {
				return false;
			}
			odo.setTredeNo(parm.getValue("TRADE_NO"));
			tredeNo = parm.getValue("TRADE_NO");
			// System.out.println("医疗卡保存入参" + parm);
			if (parm.getErrCode() < 0) {
				this.messageBox("E0005");
				return false;
			}
			if (Operator.getSpcFlg().equals("Y")
					&& ektSumExeParm.getValue("PHA_RX_NO").length() > 0) {
				// ==pangben 2013-5-21 添加预审功能
				TParm spcParm = new TParm();
				spcParm.setData("RX_NO", ektSumExeParm.getValue("PHA_RX_NO"));
				spcParm.setData("CASE_NO", reg.caseNo());
				spcParm.setData("CAT1_TYPE", "PHA");
				spcParm.setData("RX_TYPE", "7");
				// 物联网获得此次操作的医嘱，通过处方签获得
				TParm spcResult = OrderTool.getInstance().getSumOpdOrderByRxNo(
						spcParm);
				if (spcResult.getErrCode() < 0) {
					this.messageBox("物联网操作：医嘱查询出现错误");
				} else {
					spcResult.setData("SUM_RX_NO", ektSumExeParm
							.getValue("PHA_RX_NO"));
					spcResult = TIOM_AppServer.executeAction(
							"action.opd.OpdOrderSpcCAction", "saveSpcOpdOrder",
							spcResult);
					if (spcResult.getErrCode() < 0) {
						System.out.println("物联网操作:" + spcResult.getErrText());
						this.messageBox("物联网操作：医嘱添加出现错误,"
								+ spcResult.getErrText());
					} else {
						phaRxNo = ektSumExeParm.getValue("PHA_RX_NO");// =pangben2013-5-15添加药房审药显示跑马灯数据
						sendMedMessages();
					}
				}
			}
			if (null != parm.getValue("OPD_UN_FLG")
					&& parm.getValue("OPD_UN_FLG").equals("Y")) {
				TParm tempParm = new TParm();
				tempParm.setData("CASE_NO", caseNo);
				// parm.setData("MR_NO", getValue("MR_NO"));
				TParm reg = OPDAbnormalRegTool.getInstance().selectRegForOPD(
						tempParm);
				wc = "W"; // 默认为西医
				this.initOpd(reg, 0);
			} else {
				// 调用HL7
				//sendHL7Mes();
				try{
					sendHL7Mes();
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
			String re = EKTIO.getInstance().check(tredeNo, reg.caseNo());
			if (re != null && re.length() > 0) {
				this.messageBox_(re);
				this.messageBox_("请马上与信息中心联系");
				// deleteLisPosc = false;
				onExeFee();
				return false;
			}

		} else {
			this.messageBox_("医疗卡接口未开启");
			// deleteLisPosc = false;
			return false;
		}
		// 收费成功重新刷新当前病患
		// onClear();
		//ektDeleteOrder = false;// 删除可以执行
		//isFee = false;// 执行收费以后不可以再次执行收费
		//deleteLisPosc = false;
		onExeFee();
		return true;

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
	 * 门特特殊情况使用
	 */
	public void onSpecialCase() {
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CASE_NO", reg.caseNo());
		// 查询是否可以存在特殊情况:挂号医保门特操作可以使用
		TParm result = INSMZConfirmTool.getInstance().selectSpcMemo(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + "：" + result.getErrText());
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("此就诊病患非联网医保病人");
			return;
		}
		TParm spcParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\InsSpcMemoDiag.x", result);
		if (null == spcParm || null == spcParm.getValue("SPECIAL_CASE")) {
			return;
		}
		parm.setData("SPC_MEMO", spcParm.getValue("SPECIAL_CASE"));
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("OPT_USER", Operator.getID());
		result = INSMZConfirmTool.getInstance().updateInsMZConfirmSpcMemo(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + "：" + result.getErrText());
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005");
	}

	/**
	 * 医保处方查询
	 */
	public void onINSDrQueryList() {
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CASE_NO", reg.caseNo());
		// 是否存在病种
		TParm result = INSMZConfirmTool.getInstance().selectSpcMemo(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + "：" + result.getErrText());
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("此就诊病患非联网医保病人");
			return;
		}
		this.openDialog("%ROOT%\\config\\ins\\INSDrQueryList.x", result
				.getData());
	}

	/**
	 * 校验医疗卡删除操作 rxFlg false : 删除整张处方签操作使用 true :删除单个处方qi
	 */
	public boolean ektDelete(OpdOrder order, int row) {
		// 执行医疗卡操作，判断是否已经可以使用医疗卡
		if (!order.isRemovable(row, true)) {// FALSE : 已经收费 要执行 onFee() 方法
			// TRUE : 未收费 不执行onFee() 方法
			//ektDeleteOrder = false;
			return false;
		} 
		return true;
	}
	/**
	 * 校验 检验检查已经登记的数据不能删除操作
	 * STATUS=2 已登记
	 * @param row
	 * @return
	 * ===========pangben 2013-1-29
	 */
	public boolean medAppyCheckDate(OpdOrder order, int row){
		if(!order.isRemoveMedAppCheckDate(row)){
			return false;
		}
		return true;
	}
	private boolean readEKT() {
		ektReadParm = EKTIO.getInstance().TXreadEKT();
		if (ektReadParm.getErrCode() < 0) {
			this.messageBox("医疗卡读卡有误。");
			return false;
		}
		if (!ektReadParm.getValue("MR_NO").equals(getValue("MR_NO"))) {
			this.messageBox("病患信息不符,此医疗卡病患名称为:"
					+ ektReadParm.getValue("PAT_NAME"));
			ektReadParm = null;
			return false;
		}
		if (null == ektReadParm) {
			this.messageBox("未确认身份，请读医疗卡");
			return false;
		}
		return true;
	}

	// $$=================add by lx 2011/02/12
	// Start呼叫接口=============================$$//
	/**
	 * 重叫
	 */
	public void onReCallNo() {
		new Thread() {
			public void run() {
				try {
					callNo("doReCall");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}.start();
	}

	/**
	 * 下一个
	 */
	public void onNextCallNo() {
		new Thread() {
			public void run() {
				try {
					callNo("doNextCall");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}.start();
	}

	// $$=================add by lx 2011/02/12
	// 呼叫接口=============================$$//
	/**
	 * 校验医疗卡 删除医嘱 添加回冲 OPD_ORDER 数据
	 * 
	 * @param parm
	 */
	private void ektDeleteChackOut(TParm parm) {
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			for (int j = 0; j < OPBTool.getInstance().orderName.length; j++) {
				if (null == parm.getValue(OPBTool.getInstance().orderName[j], i)
						|| parm.getValue(OPBTool.getInstance().orderName[j], i).length() <= 0) {
					parm.setData(OPBTool.getInstance().orderName[j], i, "");
				}
			}

		}
	}

	public void onMTRegister() {
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("CASE_NO", reg.caseNo());
		this.openDialog("%ROOT%\\config\\ins\\INSMTReg.x", parm);
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
	 * 清空系统剪贴板
	 */
	public void onClearMenu() {
		CopyOperator.clearComList();
	}
	/**
	 * 判断此次操作的医嘱在数据库中是否已经存在，如果存在,在执行收费操作时，判断医疗卡中金额是否充足
	 * 如果金额不足，执行此处方签所收费的医嘱退还医疗卡中
	 */
	public boolean updateOrderParm(TParm orderParm ,TParm orderOldParm,TParm unParm){
		boolean unFlg = false;

		int unCount = 0;
		// System.out.println("orderParm：：：：：：：" + orderParm);
		for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
			if (orderParm.getValue("CAT1_TYPE",i).equals("RIS")
					|| orderParm.getValue("CAT1_TYPE",i).equals("LIS")
					|| orderParm.getValue("ORDER_CODE",i).length() <= 0) {
				continue;
			}
			for (int j = 0; j < orderOldParm.getCount("ORDER_CODE"); j++) {
				if (orderParm.getValue("RX_NO",i).equals(
						orderOldParm.getValue("RX_NO", j))
						&& orderParm.getValue("SEQ_NO",i).equals(
								orderOldParm.getValue("SEQ_NO", j))) {
					if (orderParm.getDouble("AMT",i) == orderOldParm
							.getDouble("AR_AMT", j)) {
						break;
					}
					unParm.setRowData(unCount, orderOldParm, j);// 获得执行修改的医嘱
					unCount++;
					unFlg = true;// 判断是否存在医嘱
					break;
				}
			}
		}
		unParm.setCount(unCount);
		return unFlg;
	}

	/**
	 * 单病种准入
	 */
	public void onSingleDise() {//add by wanglong 20121025
		TParm action=new TParm();
		action.setData("ADM_TYPE", "E");//急诊
		if(this.getValue("MR_NO").equals("")){//没有病人则不能进入
			return;
		}
		action.setData("CASE_NO", caseNo);//就诊号
		TParm result = (TParm) this.openDialog("%ROOT%\\config\\clp\\CLPSingleDise.x",// 调用其他窗体查询CASE_NO
				action);
		String diseCode=result.getValue("DISE_CODE");
		for (int row = 0; row < parmpat.getCount(); row++) {
			if(parmpat.getValue("CASE_NO",row).equals(caseNo)){
				parmpat.setData("DISE_CODE", row, diseCode);//更改病患窗口中的单病种信息
			}

		}
		this.callFunction("UI|TABLEPAT|setParmValue", parmpat);
	}

	/**
	 * 检核药品是否已经审配发 是否可以退药
	 * 
	 * @param type
	 *            String "EXA":西药 "CHN":中药
	 * @param row
	 *            int
	 *            flg =true检验当前操作是修改医嘱还是删除医嘱，修改医嘱操作退药不可以修改但是可以删除
	 * @return boolean
	 */
	public boolean checkDrugCanUpdate(OpdOrder order, String type, int row,
			boolean flg,TParm spcParm) {
		boolean needExamineFlg = false;
		// 如果是西药 审核或配药后就不可以再进行修改或者删除
		if ("MED".equals(type)) {
			// 判断是否审核
			needExamineFlg = PhaSysParmTool.getInstance().needExamine();
		}
		// 如果是中药 审核或配药后就不可以再进行修改或者删除
		if ("CHN".equals(type)) {
			// 判断是否审核
			needExamineFlg = PhaSysParmTool.getInstance().needExamineD();
		}
		TParm spcReturn=new TParm();
		if (null==spcParm) {
			String caseNo = order.getCaseNo();
			String rxNo = order.getRowParm(row).getValue("RX_NO");
			String seqNo = order.getRowParm(row).getValue("SEQ_NO");
			spcParm = new TParm();
			spcParm.setData("CASE_NO", caseNo);
			spcParm.setData("RX_NO", rxNo);
			spcParm.setData("SEQ_NO", seqNo);
			spcReturn = TIOM_AppServer.executeAction(
					"action.opb.OPBSPCAction", "getPhaStateReturn", spcParm);
		}else{
			spcReturn=spcParm;
		}
		//		PHADosageWsImplService_Client phaDosageWsImplServiceClient = new PHADosageWsImplService_Client();
		//		SpcOpdOrderReturnDto spcOpdOrderReturnDto = phaDosageWsImplServiceClient.getPhaStateReturn(caseNo, rxNo, seqNo);
		//		if(spcOpdOrderReturnDto == null){
		//			return true;
		//		}
		if(spcReturn.getErrCode()==-2){
			return true;
		}
		// 如果有审核流程 那么判断审核医师是否为空
		if (needExamineFlg) {
			// 如果审核人员存在 不存在退药人员 那么表示药品已审核 不能再做修改
			if(flg){//============pangben 2013-4-17 添加修改医嘱交验
				//				if (spcOpdOrderReturnDto.getPhaCheckCode().length() > 0) {
				//					return false;
				//				}
				if (spcReturn.getValue("PhaCheckCode").length() > 0) {
					return false;
				}
			} else {
				if (spcReturn.getValue("PhaCheckCode").length() > 0
						&& spcReturn.getValue("PhaRetnCode").length() == 0) {
					return false;
				}
			}
		} else {// 没有审核流程 直接配药
			// 判断是否有配药药师
			if (flg) {// ============pangben 2013-4-17 添加修改医嘱交验
				if (spcReturn.getValue("PhaDosageCode").length() > 0) {
					return false;
				}
			} else {
				if (spcReturn.getValue("PhaDosageCode").length() > 0
						&& spcReturn.getValue("PhaRetnCode").length() == 0) {
					return false;// 已经配药不可以做修改
				}
			}
		}
		return true;
	}
	/**
	 * 查询就诊病患金额
	 * ======pangben 2013-3-28
	 */
	public void onMrSearchFee(){
		if (null == caseNo || caseNo.length() <= 0)
			return;
		// 查看此就诊病患是否是医疗卡操作
		if(!readEKT()){
			return ;
		}
		ektReadParm.setData("CASE_NO",caseNo);
		ektReadParm.setData("EKT_TYPE_FLG",1);//1.显示病患本次就诊金额 2.扣款金额不足显示扣款不足金额
		TParm result = (TParm) this.openDialog("%ROOT%\\config\\opd\\OPDOrderPreviewAmt.x",// 调用其他窗体查询CASE_NO
				ektReadParm);
	}
	/**
	 * 向对应的门诊药房发送消息
	 * =======pangben 2013-5-13 
	 */
	public void sendMedMessages() {
		client1 = SocketLink
				.running("","ODO", "ODO");
		if (client1.isClose()) {
			out(client1.getErrText());
			return;
		}
		String [] phaArray=new String [0];
		if(phaRxNo.length()>0){//获得所有操作的处方签号码 发送数据
			phaArray=phaRxNo.split(",");
		}
		for (int i = 0; i < phaArray.length; i++) {
			client1.sendMessage("PHAMAIN", "RX_NO:"//PHAMAIN :SKT_USER 表添加数据
					+ phaArray[i] + "|MR_NO:" + pat.getMrNo()+ "|PAT_NAME:" + pat.getName());
		}
		if (client1 == null)
			return;
		client1.close();
	}
	/**
	 * 校验物联网是否已经审核
	 * @param order
	 * @return
	 */
	private boolean checkSpcPha(OpdOrder order){
		for (int i = 0; i < order.rowCount(); i++) {
			if (i - 1 >= 0) {// 已经审配发的处方签，不可以添加医嘱
				//===pangben 2013-7-23 修改物联网提示消息
				String caseNo = order.getCaseNo();
				String rxNo = order.getRowParm(i - 1).getValue("RX_NO");
				String seqNo = order.getRowParm(i - 1).getValue("SEQ_NO");
				TParm spcParm = new TParm();
				spcParm.setData("CASE_NO", caseNo);
				spcParm.setData("RX_NO", rxNo);
				spcParm.setData("SEQ_NO", seqNo);
				TParm spcReturn = TIOM_AppServer.executeAction(
						"action.opb.OPBSPCAction", "getPhaStateReturn", spcParm);
				if (!this.checkDrugCanUpdate(order, "MED", i - 1, true,spcReturn)) { // 判断是否可以新增医嘱
					if(spcReturn.getValue("PhaRetnCode").length()>0)
						this.messageBox("已经退药,请删除处方签操作");
					else
						this.messageBox("此处方已经审核,不可修改操作");
					return false;
				}
			}
		}
		return true;
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
		int tabbedIndex = ((TTabbedPane) this.getComponent("TTABPANELORDER")).getSelectedIndex();
		int row = -1;
		String orderCode = "";
		switch (tabbedIndex) {
		case 2:
			row = tblMed.getSelectedRow();
			orderCode = tblMed.getDataStore().getItemString(row, "ORDER_CODE");
			break;
		case 3:
			row = tblChn.getSelectedRow();
			orderCode = tblChn.getDataStore().getItemString(row, "ORDER_CODE");
			break;
		case 4:
			row = tblCtrl.getSelectedRow();
			orderCode = tblCtrl.getDataStore().getItemString(row, "ORDER_CODE");
			break;
		}
		if (row < 0) {
			return;
		}
		String value = (String) this.openDialog("%ROOT%\\config\\pha\\PHAOptChoose.x");
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
	 * 医保共享信息查询
	 */
	public  void onINSShareQuery() 
	{
		String nhiNo  = ""; 
		String drCode = "";
		String mzConfirmNO = "";
		//医院编码   
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); 
		if (regionParm.getErrCode() < 0) 
		{
			err(regionParm.getErrName() + "：" + regionParm.getErrText());
			return;
		}		
		nhiNo  = regionParm.getValue("NHI_NO", 0);
		//医生编码
		TParm drParm = INSMZConfirmTool.getInstance().queryDrQualifyCode(Operator.getID());
		if (drParm.getErrCode() < 0) 
		{
			err(drParm.getErrName() + "：" + drParm.getErrText());
			return;
		}			
		drCode  = drParm.getValue("DR_QUALIFY_CODE", 0);	
		//门诊挂号顺序号
		TParm parm = new TParm();
		parm.setData("CASE_NO", reg.caseNo());
		TParm result = INSMZConfirmTool.getInstance().queryInsOpd(parm);
		if (result.getErrCode() < 0) 
		{
			err(result.getErrName() + "：" + result.getErrText());
			return;   
		}
		if (result.getCount() <= 0) 
		{
			this.messageBox("此就诊病患非联网医保病人");
			return;
		}
		mzConfirmNO = ""+result.getData("CONFIRM_NO",0);
		try 
		{	
			if (app == null)
			{ 
				Ole32.CoInitialize();
				app = new DispatchPtr("PB90.n_yhinterface");
			}	
			app.invoke("f_jyxxgx", mzConfirmNO+","+nhiNo+","+drCode);
			Ole32.CoUninitialize();
		} catch (COMException e) 
		{
			e.printStackTrace();
		}		   
	}  

	/**
	 * 修改接诊时间
	 */
	public void onSeeTime(){
		TParm parm =new TParm();
		parm.setData("SEEN_DR_TIME",this.getValueString("SEEN_DR_TIME"));
		parm.setData("MR_NO",odo.getMrNo());
		parm.setData("CASE_NO",odo.getCaseNo());
		this.openDialog("%ROOT%\\config\\Zodo\\ODOMainSeeDrTime.x",parm);
		TParm sparm=this.getParmSeeDrTime(odo.getMrNo(), odo.getCaseNo());
		if(sparm.getCount()>0){
			this.setValue("SEEN_DR_TIME", sparm.getValue("SEEN_DR_TIME",0).substring(0,19).replace("-","/"));
		}else{
			this.setValue("SEEN_DR_TIME", "");
		}

	}
	/**
	 * 修改接诊时间以后再	
	 * add caoyong 添加重新查询接诊时间//huangjw add 到院时间 、检伤等级 20150604
	 * @param mrno
	 * @param caseno
	 * @return
	 */

	public TParm  getParmSeeDrTime(String mrno,String caseno){

		String sql="SELECT A.SEEN_DR_TIME,A.ARRIVE_DATE,B.LEVEL_DESC FROM REG_PATADM A,REG_ERD_LEVEL B WHERE " +
				"A.MR_NO='"+mrno+"' AND A.CASE_NO='"+caseno+"'AND  A.ADM_TYPE='E'" +
				" AND A.ERD_LEVEL=B.LEVEL_CODE";
		System.out.println("sql::"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	/**
	 * 外购处方上传
	 */
	public void onINSPrescription(){
		TParm parm =new TParm();
		parm.setData("MR_NO",odo.getMrNo());
		parm.setData("CASE_NO",odo.getCaseNo());
		parm.setData("PAT_NAME",pat.getName());
		this.openDialog("%ROOT%\\config\\ins\\INSPrescription.x",parm);	
	}

	public void selPhaClass(){
		TComboBox type = (TComboBox) this.getComponent("PHA_CLASS");
		if(TCM_Transform.getBoolean(this.getValue("ORDER_ALLERGY"))){
			type.setVisible(true);
			setValue("PHA_CLASS", "B");
			onAllg(this.getValueString("PHA_CLASS"));
		}else{
			setValue("PHA_CLASS", "");
			type.setVisible(false);
		}

	}

	public void onSelg(){
		onAllg(this.getValueString("PHA_CLASS"));

	}

	/**
	 * 病历总览
	 */
	public void onCxShow(){
		TParm parm = tblPat.getParmValue();
		String mrNo = parm.getValue("MR_NO", tblPat.getSelectedRow());
		String caseNo = parm.getValue("CASE_NO", tblPat.getSelectedRow());

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
	 * 体征采集数据查看
	 */
	public void onERDCISQuery(){//wanglong add 20150518
		TParm parm =new TParm();
		parm.setData("MR_NO",odo.getMrNo());
		parm.setData("CASE_NO",odo.getCaseNo());
		parm.setData("ADM_DATE",reg.getAdmDate());
		this.openDialog("%ROOT%\\config\\erd\\ERDCISVitalSignQuery.x",parm); 
	}

	/**
	 * CDR
	 */
	public void onQuerySummaryInfo() {
		TParm parm = new TParm();
		TTable table = (TTable)this.getComponent("TABLEPAT");
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
	 * 已完成退药的处方签不允许只删一种药，需要删除整个处方签   
	 * @param order
	 * @param row
	 * @return
	 */
	private boolean checkReturnPha(OpdOrder order, int row) {
		boolean re = false;
		String sql = "SELECT PHA_RETN_CODE,PHA_RETN_DATE FROM OPD_ORDER " +
				" WHERE CASE_NO='"+order.getItemString(row, "CASE_NO")+"' " +
				" AND RX_NO='"+order.getItemString(row, "RX_NO")+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() > 1){
			if(parm.getValue("PHA_RETN_CODE", 0).length()> 0){
				this.messageBox("处方已退药不可只删除一种药，请删除整个处方签");
				re = true;
			}
		}

		return re;
	}

	public boolean comparisionOrder(){
		//    	TParm oldParm = odo.getOpdOrder().getBuffer(odo.getOpdOrder().FILTER);

		OpdOrder opdOrder = odo.getOpdOrder();

		String lastFilter = opdOrder.getFilter();

		opdOrder.setFilter("");
		opdOrder.filter();
		TParm oldParm =  opdOrder.getBuffer(OpdOrder.PRIMARY);
		oldParm = odo.makeParmClean(oldParm);
		//		System.out.println("PRIMARY------"+oldParm);		
		String[] aNames = oldParm.getNames();
		TParm oParm = new TParm();
		for (int i = 0; i < oldParm.getCount("CASE_NO"); i++) {
			//			if(!oldParm.getBoolean("#NEW#", i)){
			//				
			//			}

			for (int j = 0; j < aNames.length; j++) {
				oParm.addData(aNames[j], oldParm.getData(aNames[j], i));
			}

		}

		oldParm =  opdOrder.getBuffer(OpdOrder.MODIFY);
		//		System.out.println("MODIFY------"+oldParm);		
		oldParm = odo.makeParmClean(oldParm);
		aNames = oldParm.getNames();
		for (int i = 0; i < oldParm.getCount("CASE_NO"); i++) {

			for (int j = 0; j < aNames.length; j++) {
				oParm.addData(aNames[j], oldParm.getData(aNames[j], i));
			}

		}

		oldParm =  opdOrder.getBuffer(OpdOrder.DELETE);
		//		System.out.println("DELETE------"+oldParm);		
		oldParm = odo.makeParmClean(oldParm);
		aNames = oldParm.getNames();
		for (int i = 0; i < oldParm.getCount("CASE_NO"); i++) {

			for (int j = 0; j < aNames.length; j++) {
				oParm.addData(aNames[j], oldParm.getData(aNames[j], i));
			}

		}

		odo.getOpdOrder().setFilter(lastFilter);
		odo.getOpdOrder().filter();
		//		System.out.println("oParm-----"+oParm);
		if(odo.onComparisionParm(oParm, reg.caseNo())){
			//			this.messageBox("医嘱发生变化，不进行收费操作");
			return true;
		}
		return false;
	}

	/**
	 * 离院  add by huangtt 20151026
	 */
	public void onOutReutrn(){
		if (odo == null) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", odo.getCaseNo());
		parm.setData("MR_NO", odo.getMrNo());
		parm.setData("PAT_NAME", odo.getPatInfo().getItemString(0, "PAT_NAME"));
		parm.setData("FLG", "OPD_OUT");
		
		// add by wangqing 20170704 start
		TParm result11 = EMRAMITool.getInstance().getErdEvalutionDataByCaseNo(caseNo);
		String triageNo11 = result11.getValue("TRIAGE_NO",0);
		if(triageNo11 == null || triageNo11.trim().length()<=0){
			return;
		}
		parm.setData("TRIAGE_NO", triageNo11);
		// add by wangqing 20170704 end
		
		this.openDialog("%ROOT%\\config\\erd\\ERDDynamicRcd2.x", parm);
	}

	/**
	 * 检伤评估表单查看
	 */
	public void onErdTriage(){
		String caseNo = odo.getCaseNo();
		String mrNo = odo.getMrNo();
		String triageNo =  odo.getRegPatAdm().getItemString(0, "TRIAGE_NO");
		if(triageNo.length() == 0){
			this.messageBox("请选择有检伤号的病患！");
			return;
		}
		String[] saveFiles = ERDLevelTool.getInstance().getELFile(triageNo);
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", mrNo);
		parm.setData("ADM_DATE", reg.getAdmDate());
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("SEX", pat.getSexString());
		parm.setData("AGE", OdoUtil.showAge(pat.getBirthday(),
				SystemTool.getInstance().getDate())); //年龄
		TParm emrFileData = new TParm();
		emrFileData.setData("FILE_PATH", saveFiles[0]);
		emrFileData.setData("FILE_NAME", saveFiles[1]);
		emrFileData.setData("FLG", true);
		parm.setData("EMR_FILE_DATA", emrFileData);
		parm.setData("SYSTEM_TYPE", "EMG");
		parm.setData("RULETYPE", "1");
		parm.setData("ERD",true);
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
	}

	/**
	 * 胸痛中心
	 * Evan
	 */
	public void onAMICenter(){
		// add by wangqing 20170701 start
		// 只有胸痛中心的病人才有胸痛中心病历
		TParm amiResult = new TParm();
		amiResult= EMRAMITool.getInstance().getEnterRouteAndPathKindByCaseNo(caseNo);
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
				
			
		String caseNo = odo.getCaseNo();
		String mrNo = odo.getMrNo();
		TParm parm = new TParm();

		TParm inParm = new TParm();
		if (odo == null || pat == null)
			return;

		if ("en".equals(this.getLanguage())) // 判断是否是英文界面
			inParm.setData("PAT_NAME", pat.getName1());
		else
			inParm.setData("PAT_NAME", pat.getName());

		inParm.setData("ODO", odo);
		inParm.setData("CASE_NO", caseNo);
		inParm.setData("MR_NO", mrNo);

		this.openWindow("%ROOT%\\config\\Zodo\\REGDoctorTriage.x", inParm,false);
	}

	/**
	 * 初始化入院路径
	 * Evan
	 */
	public void initEnterRoute() {
		this.clearValue("ENTERROUTE");

		TParm parm = SystemTool.getInstance().getEnterRoute();
		enterRoute.setParmValue(parm);
	}


	/**
	 * 
	 * 根据入院路径的结果决定路径分类是否可选
	 * Evan
	 */
	public void onEnterRoute() {
		if ("E02".equals(enterRoute.getSelectedID())) {
			this.callFunction("UI|PATHKIND|setEnabled", true);
		} else {
			this.callFunction("UI|PATHKIND|setEnabled", false);
			this.clearValue("PATHKIND");
		}
		isChange = true;
	}

	/**
	 * 
	 * 
	 * Evan
	 */
	public void onPathKind() {	
		isChange = true;
	}

	/**
	 * 初始化路径分类
	 * Evan
	 */
	public void initPathKind() {
		this.clearValue("PATHKIND");
		TParm parm = SystemTool.getInstance().getPathKind();
		pathKind.setParmValue(parm);
	}


	private void setMenuScroll(){
		String lan = this.getLanguage();
		TPanel tPanel2 = (TPanel) this.getComponent("tPanel_6");
		TRootPanel menu = (TRootPanel) this.getComponent("COMM_MENU");
		TButton button;
		for (int i = 0; i < menu.getComponentCount(); i++) {
			if(menu.getComponent(i) instanceof TButton){
				button = (TButton) menu.getComponent(i);
				if("en".equals(lan)){
					button.setText(button.getEnText());
				}else if("zh".equals(lan)){
					button.setText(button.getZhText());
				}
			}
		}
		JScrollPane scrollPane = new JScrollPane(menu);
		scrollPane.setBounds(2, 3, 135, 1300);
		menu.setPreferredSize(new Dimension(scrollPane.getWidth(),
				scrollPane.getHeight() + 200));
		tPanel2.add(scrollPane);
	}

	/**
	 * 护理记录
	 */
	public void onHLSel(){
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "INW");
		parm.setData("ADM_TYPE", this.admType);
		parm.setData("CASE_NO", odo.getCaseNo());
		parm.setData("PAT_NAME", this.getValue("PAT_NAME"));
		parm.setData("MR_NO", odo.getMrNo());
		//		parm.setData("IPD_NO", odo.getIpdNo());
		parm.setData("ADM_DATE", reg.getAdmDate());
		parm.setData("DEPT_CODE", odo.getDeptCode());
		parm.setData("RULETYPE", "1");
		parm.setData("EMR_DATA_LIST", new TParm());
		parm.addListener("EMR_LISTENER", this, "emrListener");
		parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
	}

	/**
	 * 血糖报告
	 */
	public void getXTReport(){
		SystemTool.getInstance().OpenTnbWeb(odo.getMrNo());
	}

	/**
	 * 备血申请
	 */
	public void onBXResult(){
		TParm inparm = new TParm();
		inparm.setData("MR_NO", odo.getMrNo());
		inparm.setData("ADM_TYPE", odo.getAdmType());
		inparm.setData("USE_DATE", SystemTool.getInstance().getDate());
		inparm.setData("DEPT_CODE", odo.getDeptCode());
		inparm.setData("DR_CODE", odo.getDrCode());
		String sql = "SELECT * FROM OPD_DIAGREC WHERE CASE_NO = '"
				+ odo.getCaseNo() + "' AND  MAIN_DIAG_FLG='Y' ";
		TParm diagRec = new TParm(odo.getDBTool().select(sql));
		inparm.setData("ICD_CODE", diagRec.getCount() > 0 ? diagRec.getValue(
				"ICD_CODE", 0) : "");
		inparm.setData("ICD_DESC", diagRec.getCount() > 0 ? odo
				.getDiagrec()
				.getIcdDesc(diagRec.getValue("ICD_CODE", 0), "chn") : "");
		inparm.setData("CASE_NO", odo.getCaseNo());
		// 没有历史记录
		openDialog("%ROOT%\\config\\bms\\BMSApplyNo.x", inparm);

	}

	/**
	 * 住院诊断
	 */
	public void onDiag() {

		TParm actionParm = new TParm();
		actionParm.setData("MR_NO", pat.getMrNo());
		actionParm.setData("IPD_NO", pat.getIpdNo());
		actionParm.setData("PAT_NAME", pat.getName());
		actionParm.setData("SEX", pat.getSexString());
		actionParm.setData("AGE", OdoUtil.showAge(pat.getBirthday(),
				SystemTool.getInstance().getDate())); //年龄
		actionParm.setData("CASE_NO", odo.getCaseNo());
		actionParm.setData("ADM_TYPE", "O");
		actionParm.setData("DEPT_CODE", reg.getDeptCode());
		actionParm.setData("STATION_CODE", Operator.getStation());
		//actionParm.setData("ADM_DATE", ts);
		actionParm.setData("STYLETYPE", "1");
		actionParm.setData("RULETYPE", "3");
		actionParm.setData("SYSTEM_TYPE", "ODO");
		TParm emrFileData = new TParm();
		emrFileData.setData("TEMPLET_PATH", "JHW\\门（急）诊病历\\\\门(急)诊病历");
		emrFileData.setData("EMT_FILENAME", "诊断证明");
		emrFileData.setData("SUBCLASS_CODE", "EMR02000105");
		emrFileData.setData("CLASS_CODE", "EMR020001");
		actionParm.setData("EMR_FILE_DATA", emrFileData);
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", actionParm);
	}

	/**
	 * 心电检查
	 */
	public void getPdfReport(){
		// 电生理调阅pdf
		mrNo = odo.getMrNo();
		String sql = "SELECT APPLICATION_NO  FROM MED_APPLY WHERE MR_NO = '"
				+ mrNo + "' AND ORDER_CAT1_CODE = 'ECC' AND STATUS != '9' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount() <= 0){
			this.messageBox("该病患没有心电检查医嘱");
			return;
		}
		TParm parm = new TParm();
		String opbBookNo = "";
		for(int i = 0; i < result.getCount(); i++){
			opbBookNo += "'"+result.getValue("APPLICATION_NO", i)+"'"+",";
		}
		parm.setData("OPE_BOOK_NO",opbBookNo.substring(0, opbBookNo.length()-1));
		parm.setData("CASE_NO", odo.getCaseNo());
		parm.setData("TYPE","3");
		parm.setData("MR_NO",mrNo);//wuxy 2017/05/24
		this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);
	}

	private void subjectHasSaved(){
		//    	messageBox(odo.getSubjrec().getItemData(0, "#NEW#")+"");

		if("true".equals((odo.getSubjrec().getItemData(0, "#NEW#")+""))){

			String sql = 
					" SELECT *" +
							" FROM OPD_SUBJREC" +
							" WHERE CASE_NO = '" + caseNo + "'";
			TParm p = new TParm(TJDODBTool.getInstance().select(sql));
			int c = p.getCount();
			String sub, obj, phy, propsal, exaResult;
			String sub2, obj2, phy2, propsal2, exaResult2;
			String enter = "\r\n";
			if(c > 0){

				messageBox("主诉已被其他人修改\r\n现已自动合并\r\n请重新修改并保存");

				sub = word.getCaptureValue("SUB");
				obj = word.getCaptureValue("OBJ");
				phy = word.getCaptureValue("PHY");
				propsal = word.getCaptureValue("PROPOSAL");
				exaResult = word.getCaptureValue("EXA_RESULT");

				sql = "select SEE_DR_FLG from reg_patadm where case_no = '" + caseNo + "'";
				p = new TParm(TJDODBTool.getInstance().select(sql));

				odo.getRegPatAdm().setItem(0, "SEE_DR_FLG", p.getValue("SEE_DR_FLG", 0));

				odo.getSubjrec().onQuery();

				odo.getSubjrec().showDebug();

				// 主诉客诉
				initSubject();

				sub2 = word.getCaptureValue("SUB");
				obj2 = word.getCaptureValue("OBJ");
				phy2 = word.getCaptureValue("PHY");
				propsal2 = word.getCaptureValue("PROPOSAL");
				exaResult2 = word.getCaptureValue("EXA_RESULT");

				if(sub2.length() > 0){
					word.clearCapture("SUB");
					word.pasteString(sub2 + enter + enter + sub);
				}
				if(obj2.length() > 0){
					word.clearCapture("OBJ");
					word.pasteString(obj2 + enter + enter + obj);
				}
				if(phy2.length() > 0){
					word.clearCapture("PHY");
					word.pasteString(phy2 + enter + enter + phy);
				}
				if(propsal2.length() > 0){
					word.clearCapture("PROPOSAL");
					word.pasteString(propsal2 + enter + enter + propsal);
				}
				if(exaResult2.length() > 0){
					word.clearCapture("EXA_RESULT");
					word.pasteString(exaResult2 + enter + enter + exaResult);
				}
			}

		}

	}
	/**
	 * 紧急住院
	 */
	public void onEmeHospital() {
		if(checkAdmInp(pat.getMrNo())){
			this.messageBox("此病患住院中！");
			return;
		}

		//===start===add by kangy 20160919
		TParm parm = new TParm();
		parm.setData("MR_NO",odo.getMrNo());
		// 根据病案号 查询该病人的所有预约信息
		TParm result = ADMResvTool.getInstance().selectAll(parm);
		System.out.println("ODOMain"+result);
		if(result.getCount("MR_NO")<=0){
			this.messageBox("该病人没有预约信息，不能紧急住院！");
			return;
		}
		//获取病患姓名
		String sql="SELECT PAT_NAME FROM SYS_PATINFO WHERE MR_NO='"+odo.getMrNo()+"'";
		TJDODBTool.getInstance().select(sql);
		TParm nameParm=new TParm(TJDODBTool.getInstance().select(sql));
		//获取主诊断名称
		String sql1="SELECT ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE='"+result.getValue("DIAG_CODE",0)+"'";
		TParm ZDParm=new TParm(TJDODBTool.getInstance().select(sql1));
		result.setData("DIAG_DESC",ZDParm.getValue("ICD_CHN_DESC",0));
		result.setData("NAME", nameParm.getValue("PAT_NAME",0));
		result.setData("CASE_NO", odo.getCaseNo());
		//result.setData("MR_NO", odo.getMrNo());
		result.setData("ADM_TYPE", odo.getAdmType());
		//result.setData("OPD_DEPT_CODE", odo.getDeptCode());
		//result.setData("OPD_DR_CODE", odo.getDrCode());
		//===end===add by kangy 20160919
		/*TParm sendParm = new TParm();
		 */
		//		TParm reParm = (TParm) this.openDialog(
		//				"%ROOT%\\config\\opd\\ODOemeHospital.x", sendParm);
		//		if (reParm == null)
		//			return;
		System.out.println("caseNo==="+odo.getCaseNo());
		this.openDialog("%ROOT%\\config\\opd\\ODOemeHospital.x", result);
		//		this.setValue("MR_NO", reParm.getValue("MR_NO"));
		//		this.onMrno();

	}

	/**
	 * 检查是否住院中 false 未住院 true 住院中
	 * 
	 * @param MrNo
	 *            String
	 * @return boolean
	 */
	public boolean checkAdmInp(String MrNo) {
		TParm parm = new TParm();
		parm.setData("MR_NO", MrNo);
		TParm result = ADMInpTool.getInstance().checkAdmInp(parm);
		if (result.checkEmpty("IPD_NO", result))
			return false;
		caseNo = result.getData("CASE_NO", 0).toString();
		return true;
	}

	/**
	 * 紧急住院取消
	 */
	public void onCancelEmeHospital(){
		if (!checkCanInp()) {
			this.messageBox_("此病患已经入住到床位,不可取消住院");
			return;
		}
		if (!checkBilPay()) {
			this.messageBox_("此病患还有预交金未退,不可取消住院");
			return;
		}
		//fux modify 2010805  
		if (!checkCanPay()) {
			this.messageBox_("此病患已经产生费用,不可取消住院");
			return;
		}
		int check;
		TParm parm = new TParm();
		parm.setData("MR_NO", odo.getMrNo());
		parm.setData("OPD_CASE_NO", odo.getCaseNo());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		String sql = "SELECT CASE_NO,DEPT_CODE,STATION_CODE,BED_NO FROM ADM_INP WHERE MR_NO = '"+odo.getMrNo()+"' AND URG_FLG = 'Y' AND DS_DATE IS NULL AND CANCEL_FLG <>'Y' ";
		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm2.getCount()<=0){
			this.messageBox("该患者不存在住院记录！");
			return;
		}
		parm.setData("CASE_NO", parm2.getValue("CASE_NO", 0));//住院就診號
		parm.setData("CANCEL_FLG", "Y");
		parm.setData("PSF_KIND", "");
		parm.setData("PSF_HOSP", "");
		parm.setData("CANCEL_DATE", "");
		parm.setData("CANCEL_USER", "");
		if(parm2.getCount()>0){
			check = this.messageBox("消息", "该患者已住院，是否取消？", 0);
			if(check!=0){
				return;
			}else{
				String sql2 = "SELECT OPBOOK_SEQ FROM OPE_OPBOOK WHERE CASE_NO = '"+parm2.getValue("CASE_NO", 0)+"' AND ADM_TYPE = 'I' AND STATE = '1'";
				TParm parm3 = new TParm(TJDODBTool.getInstance().select(sql2));
				parm.setData("OPBOOK_SEQ", parm3.getValue("OPBOOK_SEQ", 0));
				//parm.setData("STATE_FLG", "1");
				if(parm3.getCount()>0){
					check = this.messageBox("消息", "住院手术已经排程，是否继续操作？", 0);
					if(check!=0){
						return;
					}
				}
				TParm result = TIOM_AppServer.executeAction("action.opd.ODOEmeHospitalAction",
						"doCancel", parm);
				if (result.getErrCode() < 0) {
					this.messageBox("" + result.getErrText());
					return;
				}else{
					this.messageBox("取消成功！");
				}
			}
		}else{
			this.messageBox("该患者没有办理紧急住院！");
		}

	}

	/**
	 * 检核是否可以取消住院
	 * 
	 * @return boolean
	 */
	public boolean checkCanInp() {
		TParm parm = new TParm();
		String sql = "SELECT CASE_NO FROM ADM_INP WHERE MR_NO = '"+odo.getMrNo()+"' AND URG_FLG = 'Y' AND DS_DATE IS NULL AND CANCEL_FLG <>'Y'";
		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql));
		parm.setData("CASE_NO", parm2.getValue("CASE_NO", 0));
		//parm.setData("CASE_NO", caseNo);
		boolean result = ADMTool.getInstance().checkCancelOutInp(parm);
		return result;
	}

	/**
	 * 校验预交金余额
	 * 
	 * @return boolean
	 */
	public boolean checkBilPay() {
		String sql = "SELECT CASE_NO FROM ADM_INP WHERE MR_NO = '"+odo.getMrNo()+"' AND URG_FLG = 'Y' AND DS_DATE IS NULL AND CANCEL_FLG <>'Y' ";
		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql));
		TParm parm = BILPayTool.getInstance().selBilPayLeft( parm2.getValue("CASE_NO", 0));
		if (parm.getErrCode() < 0) {
			return false;
		}
		if (parm.getDouble("PRE_AMT", 0) > 0) {
			return false;
		}

		return true;
	}

	/**
	 * 检核是否可以已经产生费用(耗用记录)
	 * 
	 * @return boolean
	 */
	public boolean checkCanPay() {
		boolean result = true;    
		String sql = "SELECT CASE_NO FROM ADM_INP WHERE MR_NO = '"+odo.getMrNo()+"' AND URG_FLG = 'Y' AND DS_DATE IS NULL AND CANCEL_FLG <>'Y'";
		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql));
		//modify by yangjj 20151110 取消住院 费用总和小于等于0
		String sql2 = " SELECT SUM(TOT_AMT) AS COUNT FROM IBS_ORDD WHERE CASE_NO = '"+parm2.getValue("CASE_NO", 0)+"' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql2));
		if(Integer.parseInt(parm.getValue("COUNT", 0)) > 0){
			result = false;
		}
		//String sql = "SELECT CASE_NO FROM IBS_ORDM WHERE CASE_NO = '"+caseNo+"' ";
		//TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		//		parm.setData("CASE_NO", caseNo);

		//if(parm.getCount()>0){
		//result = false;
		//}
		//boolean result = ADMTool.getInstance().checkCancelOutInp(parm); 
		return result;
	}

	/**
	 * 医疗卡扣钱完成后序操作
	 * @param ektSumExeParm
	 * @return
	 */
	public boolean ektExec(TParm ektSumExeParm){
		if (Operator.getSpcFlg().equals("Y")
				&& ektSumExeParm.getValue("PHA_RX_NO").length() > 0) {
			// ==pangben 2013-5-21 添加预审功能
			TParm spcParm = new TParm();
			spcParm.setData("RX_NO", ektSumExeParm.getValue("PHA_RX_NO"));
			spcParm.setData("CASE_NO", reg.caseNo());
			spcParm.setData("CAT1_TYPE", "PHA");
			spcParm.setData("RX_TYPE", "7");
			// 物联网获得此次操作的医嘱，通过处方签获得
			TParm spcResult = OrderTool.getInstance().getSumOpdOrderByRxNo(
					spcParm);
			if (spcResult.getErrCode() < 0) {
				this.messageBox("物联网操作：医嘱查询出现错误");
			} else {
				spcResult.setData("SUM_RX_NO", ektSumExeParm
						.getValue("PHA_RX_NO"));
				spcResult = TIOM_AppServer.executeAction(
						"action.opd.OpdOrderSpcCAction", "saveSpcOpdOrder",
						spcResult);
				if (spcResult.getErrCode() < 0) {
					System.out.println("物联网操作:" + spcResult.getErrText());
					this.messageBox("物联网操作：医嘱添加出现错误,"
							+ spcResult.getErrText());
				} else {
					phaRxNo = ektSumExeParm.getValue("PHA_RX_NO");// =pangben2013-5-15添加药房审药显示跑马灯数据
					sendMedMessages();
				}
			}
		}
		System.out.println("opdUnFlg---"+opdUnFlg);
		if (null != opdUnFlg
				&& opdUnFlg.equals("Y")) {
			TParm tempParm = new TParm();
			tempParm.setData("CASE_NO", caseNo);
			// parm.setData("MR_NO", getValue("MR_NO"));
			TParm reg = OPDAbnormalRegTool.getInstance().selectRegForOPD(
					tempParm);
			wc = "W"; // 默认为西医
			this.initOpd(reg, 0);
		} else {
			// 调用HL7
			//sendHL7Mes();
			System.out.println("hl7-------------");
			try{
				sendHL7Mes();
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		String re = EKTIO.getInstance().check(tredeNo, reg.caseNo());
		if (re != null && re.length() > 0) {
			this.messageBox_(re);
			this.messageBox_("请马上与信息中心联系");
			// deleteLisPosc = false;
			onExeFee();
			return false;
		}
		onExeFee();
		return true;
	}

	/**
	 * 急诊首诊心电医嘱合并提示
	 */
	private void initFirstEcgOrder() {
		// 取得急诊检伤信息
		TParm triageNoResult = EMRAMITool.getInstance()
				.getErdEvalutionDataByCaseNo(caseNo);

		if (triageNoResult.getErrCode() < 0) {
			this.messageBox("查询急诊检伤信息错误");
			err("查询急诊检伤信息错误:" + triageNoResult.getErrText());
			return;
		}
		if (triageNoResult.getCount() <= 0) {
			return;
		}

		// 检伤号
		triageNo = triageNoResult.getValue("TRIAGE_NO", 0);

		// 查询医嘱表中是否已经存在首诊心电医嘱
		TParm firstEcgOrderResult = EMRAMITool.getInstance()
				.getVirtualOrderByCaseNo(caseNo);

		// 如果医嘱表中不存在首诊心电医嘱则去临时文件夹中验证是否有接到心电PDF报告
		if (firstEcgOrderResult.getCount() <= 0) {
			String firstEcgPdfPath = TConfig
					.getSystemValue("FIRST_ECG_PDF_PATH");
			TSocket tsocket = TIOM_FileServer.getSocket("Main");
			String pdfPath = TIOM_FileServer.getRoot() + firstEcgPdfPath;
			// 去服务器上指定的文件夹扫描文件
			String[] fileArray = TIOM_FileServer.listFile(tsocket, pdfPath);
			if (fileArray == null) {
				return;
			} else {
				int count = fileArray.length;
				for (int i = 0; i < count; i++) {
					if (fileArray[i].contains(triageNo)) {
						if (this.messageBox("询问", "是否开立心电医嘱?",
								JOptionPane.YES_NO_OPTION) == 0) {
							TParm parm = new TParm();
							Object obj = this.openDialog(
									"%ROOT%\\config\\opd\\OPDEcgOrderSheet.x",
									parm);
							if (obj == null) {
								return;
							}
							if (!(obj instanceof TParm)) {
								return;
							} else {
								TParm exeParm = (TParm) obj;
								// null_非首诊心电医嘱,0_未合并,1_已合并,2_不合并
								exeParm.setData("VIRTUAL_FLG", "0");
								exeParm.setData("EXEC_FLG", "Y");
								// 自动开立心电医嘱
								this.insertExaPack(exeParm);
								return;
							}
						}
					}
				}
				return;
			}
		}
	}

	/**
	 * 将首诊心电报告进行病历周转
	 */
	private void moveFirstEcgPdf() {
		// 查询医嘱表中是否已经存在首诊心电医嘱
		TParm firstEcgOrderResult = EMRAMITool.getInstance()
				.getVirtualOrderByCaseNo(caseNo);
		String firstEcgAppNo = "";
		for (int i = 0; i < firstEcgOrderResult.getCount(); i++) {
			if ("0".equals(firstEcgOrderResult.getValue("VIRTUAL_FLG", i))) {
				// 取得待合并合并首诊心电医嘱的申请单号
				firstEcgAppNo = firstEcgOrderResult.getValue("MED_APPLY_NO", i);
				break;
			}
		}

		if (StringUtils.isNotEmpty(firstEcgAppNo)) {
			String firstEcgPdfPath = TConfig
					.getSystemValue("FIRST_ECG_PDF_PATH");
			TSocket socket = TIOM_FileServer.getSocket("Main");
			String pdfPath = TIOM_FileServer.getRoot() + firstEcgPdfPath;
			// 去服务器上指定的文件夹扫描文件
			String[] fileArray = TIOM_FileServer.listFile(socket, pdfPath);
			if (fileArray == null) {
				return;
			} else {
				int count = fileArray.length;
				byte[] data = null;
				String pdfFileName = "";
				String targetPath = TIOM_FileServer.getRoot()
						+ TIOM_FileServer.getPath("EmrData");
				String targetFileName = "";
				// 病历周转完整路径
				String fullPath = "";

				for (int i = 0; i < count; i++) {
					if (fileArray[i].contains(triageNo)) {
						pdfFileName = fileArray[i];
						data = TIOM_FileServer.readFile(socket, pdfPath
								+ File.separator + pdfFileName);
						if (data == null) {
							messageBox("首诊心电报告文件读取错误:" + pdfPath
									+ File.separator + pdfFileName);
							return;
						} else {
							targetFileName = caseNo + "_检查报告_" + firstEcgAppNo
									+ ".pdf";
							fullPath = targetPath + "PDF" + File.separator
									+ caseNo.substring(0, 2) + File.separator
									+ caseNo.substring(2, 4) + File.separator
									+ odo.getMrNo();
							// 将PDF文件进行病历周转
							if (TIOM_FileServer.writeFile(socket, fullPath
									+ File.separator + targetFileName, data)) {
								// 更新首诊心电医嘱状态为1_已合并
								TParm result = EMRAMITool.getInstance()
										.updateVirtualFlgByCaseNo(caseNo, "1");
								if (result.getErrCode() < 0) {
									System.out.println("更新首诊心电医嘱错误："
											+ result.getErrText());
								}

								// 向EMR_THRFILE_INDEX表插入数据
								TParm inserParm = new TParm();
								inserParm.setData("CASE_NO", caseNo);
								inserParm.setData("ADM_TYPE", admType);
								inserParm.setData("MR_NO", odo.getMrNo());
								inserParm.setData("IPD_NO", "");
								inserParm.setData("FILE_PATH", "PDF\\"
										+ caseNo.substring(0, 2) + "\\"
										+ caseNo.substring(2, 4) + "\\"
										+ odo.getMrNo());
								inserParm.setData("FILE_NAME", targetFileName
										.replace(".pdf", ""));
								inserParm.setData("DESIGN_NAME", "");
								inserParm.setData("CLASS_CODE", "EGEMR");
								inserParm.setData("SUBCLASS_CODE", "");
								inserParm.setData("DISPOSAC_FLG", "N");
								inserParm.setData("CREATOR_USER", "ECC");
								inserParm.setData("PDF_CREATOR_USER", "ECC");
								inserParm.setData("OPE_BOOK_NO", firstEcgAppNo);
								inserParm.setData("OPT_USER", "JAVAHIS");
								inserParm.setData("OPT_TERM", "127.0.0.1");

								result = EMRAMITool.getInstance()
										.insertEmrThrFileIndex(inserParm);
								if (result.getErrCode() < 0) {
									System.out
									.println("首诊心电医嘱插入EMR_THRFILE_INDEX表错误："
											+ result.getErrText());
								}

								// 删除该文件
								if (!TIOM_FileServer.deleteFile(socket, pdfPath
										+ File.separator + pdfFileName)) {
									System.out.println("首诊心电报告【" + pdfFileName
											+ "】删除失败");
								}
							} else {
								this.messageBox("首诊心电报告病历周转错误");
								return;
							}
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * 叫号
	 * 
	 * @param
	 */
	private void callNo(String methodName) {
		String drCode = Operator.getID() + "|";
		String ip = Operator.getIP();
		TParm inParm = new TParm();
		inParm.setData("msg", drCode + ip);
		// modify by wangb 2017/6/19 门诊医生站叫号显示当前呼叫病患
		TParm result = TIOM_AppServer.executeAction("action.device.CallNoAction",
				methodName, inParm);
		if (StringUtils.isNotEmpty(result.getValue("PAT_NAME"))) {
			this.setValue("CURRENT_PAT", "当前病患:" + result.getValue("PAT_NAME"));
		} else {
			this.setValue("CURRENT_PAT", "");
		}
		
		// 自动选中对应病案号的数据行
		TTable table = (TTable)this.getComponent("TABLEPAT");
		int count = table.getRowCount();
		String mrNo = result.getValue("MR_NO");
		String queNo = result.getValue("QUE_NO");
		if (StringUtils.isNotEmpty(mrNo) && StringUtils.isNotEmpty(queNo)
				&& count > 0) {
			for (int i = 0; i < count; i++) {
				if (StringUtils.equals(mrNo + queNo, table.getItemString(i,
						"MR_NO")
						+ table.getItemString(i, "QUE_NO"))) {
					table.setSelectedRow(i);
					break;
				}
			}
		}
	}

	/**
	 * 弹出错误信息
	 * @param result
	 */
	public void err(TParm result){
    	err("ERR:" + result.getErrCode() + result.getErrText() +
		result.getErrName());
	}
	
	
	/**
	 * 合并急诊抢救口头医嘱 add by wangqing 20170912
	 */
	public void initOralOrder(){
//		String[] data = odo.getOpdOrder().getRx("7");
//		String rxNo = getRxNo(data, 0);
//		if(rxNo ==null || rxNo.trim().length()==0){
//			rxNo = odo.getOpdOrder().getNewRx();// 新增处方号
//		}
		if(caseNo==null || caseNo.trim().length()==0){
//			this.messageBox("就诊号为空");
			return;
		}
		String triageNo = this.getTriageNo(caseNo);
		if(triageNo == null || triageNo.trim().length()==0){
//			this.messageBox("检伤号为空");
			return;
		}
		// 判断是否有口头医嘱
		TParm onwOrderParm = this.getOralOrder(triageNo);
		if(onwOrderParm.getErrCode()<0){
			this.messageBox("onwOrderParm.getErrCode()<0");
			return;
		}
		if(onwOrderParm.getCount()<=0){
			return;
		}	
		boolean flg1 = false;// 标识护士是否有护士已经签名的医嘱
		boolean flg2 = false;// 标识否有护士已经签名但医生没有签名的医嘱
		boolean flg3 = false;// 标识否有医生已经签名的医嘱
		boolean exeFlg = false;// 标识是否有医生已经签名但没有开立的医嘱
		// 判断是否有护士已经签名的医嘱
		for(int i=0; i<onwOrderParm.getCount(); i++){		
			if(onwOrderParm.getValue("SIGN_NS", i) != null 
					&& onwOrderParm.getValue("SIGN_NS", i).trim().length()>0){
				flg1 = true;						
				break;
			}
		}
		if(!flg1){
			return;
		}
		// 判断是否有护士已经签名，但医生没有签名的医嘱
		for(int i=0; i<onwOrderParm.getCount(); i++){		
			if( (onwOrderParm.getValue("SIGN_NS", i) != null && onwOrderParm.getValue("SIGN_NS", i).trim().length()>0) 
					&& (onwOrderParm.getValue("SIGN_DR", i) == null || onwOrderParm.getValue("SIGN_DR", i).trim().length()==0) ){
				flg2 = true;
				break;
			}	
		}
		if(flg2){// 弹出审核界面
			TParm parm = new TParm();
			parm.setData("TRIAGE_NO", triageNo);
			this.openDialog("%ROOT%\\config\\onw\\ONWOrderUI.x", parm);
		}
		// 重新查询
		onwOrderParm = this.getOralOrder(triageNo);
		if(onwOrderParm.getErrCode()<0){
			this.messageBox("onwOrderParm.getErrCode()<0");
			return;
		}	
		// 校验opdOrder中是否已经有此医嘱，如果有，则onwOrderParm中删除此条医嘱
		OpdOrder order = odo.getOpdOrder();
		String storeName = order.isFilter() ? OpdOrder.FILTER : OpdOrder.PRIMARY;
		TParm orderParm2 = order.getBuffer(storeName);
		
		System.out.println("======||||||orderParm2===="+orderParm2);
		
		
		for(int i=onwOrderParm.getCount()-1; i>=0; i--){
			if(onwOrderParm.getValue("ORDER_CODE", i) != null 
					&& onwOrderParm.getValue("ORDER_CODE", i).trim().length()>0){			
				for(int j=0; j<orderParm2.getCount(); j++){
					if(orderParm2.getValue("ONW_ORDER_FLG", j) != null 
							&& orderParm2.getValue("ONW_ORDER_FLG", j).equals("Y") 
							&& orderParm2.getValue("ONW_TRIAGE_NO", j) != null 
							&& orderParm2.getValue("ONW_TRIAGE_NO", j).equals(onwOrderParm.getValue("TRIAGE_NO", i)) 
							&& orderParm2.getValue("ONW_ORDER_SEQ", j) !=null
							&& orderParm2.getValue("ONW_ORDER_SEQ", j).equals(onwOrderParm.getValue("SEQ_NO", i)) 
							&& orderParm2.getValue("ORDER_CODE", j) != null 
							&& orderParm2.getValue("ORDER_CODE", j).equals(onwOrderParm.getValue("ORDER_CODE", i))){
						onwOrderParm.removeRow(i);
						break;
					}
				}
			}		
		}
		// 判断是否有医生签字的医嘱
		for(int i=0; i<onwOrderParm.getCount(); i++){		
			if(onwOrderParm.getValue("SIGN_DR", i) != null 
					&& onwOrderParm.getValue("SIGN_DR", i).trim().length()>0){
				flg3 = true;
				break;
			}	
		}
		if(!flg3){
			return;
		}		
		// 判断是否有医生已经签字，但没有开立的医嘱
		for(int i=0; i<onwOrderParm.getCount(); i++){
			if( (onwOrderParm.getValue("SIGN_DR", i) != null && onwOrderParm.getValue("SIGN_DR", i).trim().length()>0) 
					&& (!(onwOrderParm.getValue("EXE_FLG", i) != null && onwOrderParm.getValue("EXE_FLG", i).equals("Y"))) ){
				exeFlg = true;
				break;
			}	
		}
		if(!exeFlg){
			return;
		}
		if (this.messageBox("询问", "是否合并口头医嘱?", JOptionPane.YES_NO_OPTION) == 0) {// 合并		
			this.insertOralOrder(onwOrderParm);
		}	
	}
	
	/**
	 * 查询检伤号
	 * @param caseNo 急诊就诊号
	 * @return 检伤号
	 */
	public String getTriageNo(String caseNo){
		String sql = " SELECT CASE_NO, TRIAGE_NO FROM ERD_EVALUTION WHERE CASE_NO='"+caseNo+"' ";
		System.out.println("///sql="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("///result="+result);
		if(result == null || result.getErrCode()<0 || result.getCount()<=0 || result.getValue("TRIAGE_NO", 0) == null || result.getValue("TRIAGE_NO", 0).trim().length()==0){
			return "";
		}	
		return result.getValue("TRIAGE_NO", 0);
	}
	
	/**
	 * 获得急诊抢救口头医嘱
	 * @param triageNo
	 * @return
	 */
	public TParm getOralOrder(String triageNo){
		TParm result = new TParm();
		TParm parm = new TParm();
		parm.setData("TRIAGE_NO", triageNo);
		result = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "selectOnwOrder", parm);
		if(result.getErrCode()<0){
			this.messageBox("获得急诊抢救口头医嘱");
			return result;
		}
		return result;
	}

	/**
	 * 插入口头医嘱数据
	 * @param parm
	 */
	public void insertOralOrder(TParm parm){// insertPack
		if(parm == null){
			this.messageBox("parm is null");
			return;
		}
		// 添加口头医嘱标识
		for(int i=0; i<parm.getCount(); i++){
			parm.setData("ONW_ORDER_FLG", i, "Y");
		}
		// 1、分组
		TParm allParm = new TParm();
		TParm exaParm = new TParm();// 检验、检查
		TParm opParm = new TParm();// 诊疗项目（处置）
		TParm medParm = new TParm();// 西药、中成药
		TParm chnParm = new TParm();// 中草药
		
		TParm oMedParm = new TParm();// 口服西药、中成药
		TParm eMedParm = new TParm();// 外用西药、中成药
		TParm iOrFMedParm = new TParm();// 针剂或点滴西药、中成药
			
		for(int i=0; i<parm.getCount(); i++){
			if(parm.getValue("SIGN_DR", i) != null && parm.getValue("SIGN_DR", i).trim().length()>0 
					&&  (!(parm.getValue("EXE_FLG", i) != null && parm.getValue("EXE_FLG", i).equals("Y"))) ){
				if(parm.getValue("CAT1_TYPE", i) != null 
						&& (parm.getValue("CAT1_TYPE", i).equals("LIS") || parm.getValue("CAT1_TYPE", i).equals("RIS"))){// 检验、检查
					exaParm.addRowData(parm, i);
				}
				if(parm.getValue("CAT1_TYPE", i) != null  
						&& (parm.getValue("CAT1_TYPE", i).equals("TRT") || parm.getValue("CAT1_TYPE", i).equals("PLN") || parm.getValue("CAT1_TYPE", i).equals("OTH")) ){// 诊疗项目（处置）
					opParm.addRowData(parm, i);
				}
				if(parm.getValue("ORDER_CAT1_CODE", i) != null 
						&& (parm.getValue("ORDER_CAT1_CODE", i).equals("PHA_W") || parm.getValue("ORDER_CAT1_CODE", i).equals("PHA_C")) ){// 西药、中成药
					TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
							parm.getValue("ORDER_CODE", i));
					System.out.println("//////parmBase="+parmBase);
					if(parmBase.getValue("DOSE_TYPE", 0) !=null && parmBase.getValue("DOSE_TYPE", 0).equalsIgnoreCase("O")){
						oMedParm.addRowData(parm, i);
					}
					if(parmBase.getValue("DOSE_TYPE", 0) !=null && parmBase.getValue("DOSE_TYPE", 0).equalsIgnoreCase("E")){
						eMedParm.addRowData(parm, i);
					}
					if(parmBase.getValue("DOSE_TYPE", 0) !=null && (parmBase.getValue("DOSE_TYPE", 0).equalsIgnoreCase("I") || parmBase.getValue("DOSE_TYPE", 0).equalsIgnoreCase("F"))){
						iOrFMedParm.addRowData(parm, i);
					}				
					medParm.addRowData(parm, i);
				}
				if(parm.getValue("ORDER_CAT1_CODE", i) != null 
						&& (parm.getValue("ORDER_CAT1_CODE", i).equals("PHA_G")) ){// 中草药
					chnParm.addRowData(parm, i);
				}
			}		
		}
		allParm.setData("EXA", exaParm);
		allParm.setData("OP", opParm);
		allParm.setData("ORDER", medParm);
		allParm.setData("CHN", chnParm.getData());
		
		allParm.setData("ORDER_O", oMedParm);// 口服西药、中成药
		allParm.setData("ORDER_E", eMedParm);// 外用西药、中成药
		allParm.setData("ORDER_I_OR_F", iOrFMedParm);// 针剂或点滴西药、中成药	
		
//		System.out.println("//////exaParm="+exaParm);
//		System.out.println("//////opParm="+opParm);
//		System.out.println("//////chnParm="+opParm);
//		System.out.println("//////medParm="+medParm);
//		System.out.println("//////oMedParm="+oMedParm);
//		System.out.println("//////eMedParm="+eMedParm);
//		System.out.println("//////iOrFMedParm="+iOrFMedParm);
		// 2、插入
		insertOralData(allParm);
		this.messageBox("口头医嘱插入完毕");
	}
	
	/**
	 * 插入口头医嘱数据
	 * @param result
	 */
	private void insertOralData(TParm result) {
		// System.out.println("==result=="+result);
		if (result == null){
			return;
		}		
		String tableName_ = this.tableName;
//		String filter1 = ((TTable) this.getComponent(TABLE_EXA)).getDataStore()
//				.getFilter();
//		String filter2 = ((TTable) this.getComponent(TABLE_OP)).getDataStore()
//				.getFilter();
//		String filter3 = ((TTable) this.getComponent(TABLE_MED)).getDataStore()
//				.getFilter();
//		String filter4 = ((TTable) this.getComponent(TABLE_CHN)).getDataStore()
//				.getFilter();
//		String filter5 = ((TTable) this.getComponent(TABLE_CTRL))
//				.getDataStore().getFilter();
		// 检验检查
		TParm exaResult = ((TParm) result.getData("EXA"));
		// System.out.println("exaResult----"+exaResult);
		if (exaResult != null && exaResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_EXA;
			int count = exaResult.getCount("ORDER_CODE");
			if(count>0){
				// 判断是否有空白处方签，如果有，返回；如果没有，新增处方签
				System.out.println("======插入检验检查======");
				String rxNo = this.getNullRx(EXA_RX, EXA);
				if(rxNo != null && rxNo.trim().length()>0){
//					TComboBox combo = (TComboBox) this.getComponent(rxName);
//					combo.setValue(rxNo);
				}else{
					// 新增检验检查处方签
					this.onAddOrderList(EXA, EXA_RX, TABLE_EXA);
				}		
			}
			for (int i = 0; i < count; i++) {
				insertOralExaPack(exaResult.getRow(i));
			}
		}
		// // 处置
		TParm opResult = ((TParm) result.getData("OP"));
		if (opResult != null && opResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_OP;
			// orderCat1Type = "TRT";
			// tag = "OP_AMT";
			insertOralOpPack(opResult);
		}
//		// 药品
//		TParm orderResult = ((TParm) result.getData("ORDER"));
//		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
//			tableName = TABLE_MED;
//			// orderCat1Type = "PHA_W";
//			// tag = "MED_AMT";
//			//			System.out.println("111orderResult is :"+orderResult);
//			insertOrdPack(orderResult,false);
//		}
		
		// modified by wangqing 20171106 根据医嘱剂型，分别开立到不同的处方签上
		// 口服西药、中成药
		TParm orderResult = ((TParm) result.getData("ORDER_O"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_MED;
			insertOralOrdPack(orderResult,false);
//			((TTable) this.getComponent(TABLE_MED)).setFilter(filter3);
//			((TTable) this.getComponent(TABLE_MED)).filter();
//			((TTable) this.getComponent(TABLE_MED)).setDSValue();
		}
		// 外用西药、中成药
		orderResult = ((TParm) result.getData("ORDER_E"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_MED;
			insertOralOrdPack(orderResult,false);
//			((TTable) this.getComponent(TABLE_MED)).setFilter(filter3);
//			((TTable) this.getComponent(TABLE_MED)).filter();
//			((TTable) this.getComponent(TABLE_MED)).setDSValue();
		}
		// 针剂或点滴西药、中成药
		orderResult = ((TParm) result.getData("ORDER_I_OR_F"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_MED;
			insertOralOrdPack(orderResult,false);
		}
		// 管制药品
		orderResult = ((TParm) result.getData("CTRL"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_CTRL;
			// orderCat1Type = "PHA_W";
			// tag = "CTRL_AMT";
			insertCtrlPack(orderResult);
		}

		// 中药
		// System.out.println("chn======"+result.getData("CHN"));
		Map chnMap = (Map) result.getData("CHN");
		if (chnMap == null || chnMap.size() <= 0) {
			return;
		}
		Iterator it = chnMap.values().iterator();
		while (it.hasNext()) {
			tableName = TABLE_CHN;
			// orderCat1Type = "PHA_G";
			orderResult = (TParm) it.next();
			insertOralChnPack(orderResult);
		}
//		// 刷新数据
//		((TTable) this.getComponent(TABLE_EXA)).setFilter(filter1);
//		((TTable) this.getComponent(TABLE_EXA)).filter();
//		((TTable) this.getComponent(TABLE_EXA)).setDSValue();
//		((TTable) this.getComponent(TABLE_OP)).setFilter(filter2);
//		((TTable) this.getComponent(TABLE_OP)).filter();
//		((TTable) this.getComponent(TABLE_OP)).setDSValue();
//		((TTable) this.getComponent(TABLE_MED)).setFilter(filter3);
//		((TTable) this.getComponent(TABLE_MED)).filter();
//		((TTable) this.getComponent(TABLE_MED)).setDSValue();
//		((TTable) this.getComponent(TABLE_CHN)).setFilter(filter4);
//		((TTable) this.getComponent(TABLE_CHN)).filter();
//		((TTable) this.getComponent(TABLE_CHN)).setDSValue();
//		((TTable) this.getComponent(TABLE_CTRL)).setFilter(filter5);
//		((TTable) this.getComponent(TABLE_CTRL)).filter();
//		((TTable) this.getComponent(TABLE_CTRL)).setDSValue();
		TTabbedPane tabPanel = (TTabbedPane) this.getComponent("TTABPANELORDER");
		tabPanel.setSelectedIndex(tabPanel.getSelectedIndex());	
	}
	
	/**
	 * 插入口头医嘱之检验检查
	 * @param parmExa
	 */
	private void insertOralExaPack(TParm parmExa) {
		if(parmExa==null){
			return;
		}
//		// 新增检验检查处方签
//		this.onAddOrderList(EXA, EXA_RX, TABLE_EXA);
		this.insertExaPack(parmExa);
	}
	
	/**
	 * 插入口头医嘱之处置（诊疗项目）
	 * @param parm
	 */
	private void insertOralOpPack(TParm parm) {
		if(parm==null){
			return;
		}
		// 判断是否有空白处方签，如果有，返回；如果没有，新增处方签
		String rxNo = this.getNullRx(OP_RX, OP);
		if(rxNo != null && rxNo.trim().length()>0){
//			TComboBox combo = (TComboBox) this.getComponent(rxName);
//			combo.setValue(rxNo);
		}else{
			// 新增处置（诊疗项目）处方签
			this.onAddOrderList(OP, OP_RX, TABLE_OP);
		}
		this.insertOpPack(parm);
	}
	
	/**
	 * 插入口头医嘱之中药
	 * @param parmChn
	 */
	private void insertOralChnPack(TParm parmChn) {
		if (parmChn == null) {
			return;
		}
		// 判断是否有空白处方签，如果有，返回；如果没有，新增处方签
		String rxNo = this.getNullRx(CHN_RX, CHN);
		if(rxNo != null && rxNo.trim().length()>0){
//			TComboBox combo = (TComboBox) this.getComponent(rxName);
//			combo.setValue(rxNo);
		}else{
			// 新增中药处方签
			this.onAddOrderList(CHN, CHN_RX, TABLE_CHN);
		}		
		this.insertChnPack(parmChn);
	}
	
	/**
	 * 插入口头医嘱之药品
	 * @param parm
	 * @param onFechFlg
	 */
	private void insertOralOrdPack(TParm parm,boolean onFechFlg) {
		if(parm==null){
			return;
		}
		System.out.println("======插入口头医嘱之药品======");
		// 判断是否有空白处方签，如果有，返回；如果没有，新增处方签
		String rxNo = this.getNullRx(MED_RX, MED);
		if(rxNo != null && rxNo.trim().length()>0){
//			TComboBox combo = (TComboBox) this.getComponent(rxName);
//			combo.setValue(rxNo);
		}else{
			// 新增药品处方签
			this.onAddOrderList(MED, MED_RX, TABLE_MED);
		}		
		this.insertOrdPack(parm, onFechFlg);
	}
	
	/**
	 * 口头医嘱
	 */
	public void onOnwOrder(){
		String triageNo = this.getTriageNo(caseNo);
		if(triageNo == null || triageNo.trim().length()==0){
//			this.messageBox("检伤号为空");
			return;
		}
		TParm parm = new TParm();
		parm.setData("TRIAGE_NO", triageNo);
		this.openDialog("%ROOT%\\config\\onw\\ONWOrderUI.x", parm);
		// 数据传回
		boolean flg3 = false;// 标识否有医生已经签名的医嘱
		boolean exeFlg = false;// 标识是否有医生已经签名但没有开立的医嘱		
		// 查询
		TParm onwOrderParm = this.getOralOrder(triageNo);
		if(onwOrderParm.getErrCode()<0){
			this.messageBox("onwOrderParm.getErrCode()<0");
			return;
		}	
		// 校验opdOrder中是否已经有此医嘱，如果有，则onwOrderParm中删除此条医嘱
		OpdOrder order = odo.getOpdOrder();
		String storeName = order.isFilter() ? OpdOrder.FILTER : OpdOrder.PRIMARY;
		TParm orderParm2 = order.getBuffer(storeName);
		System.out.println("======||||||orderParm2===="+orderParm2);
		for(int i=onwOrderParm.getCount()-1; i>=0; i--){
			if(onwOrderParm.getValue("ORDER_CODE", i) != null 
					&& onwOrderParm.getValue("ORDER_CODE", i).trim().length()>0){			
				for(int j=0; j<orderParm2.getCount(); j++){
					if(orderParm2.getValue("ONW_ORDER_FLG", j) != null 
							&& orderParm2.getValue("ONW_ORDER_FLG", j).equals("Y") 
							&& orderParm2.getValue("ONW_TRIAGE_NO", j) != null 
							&& orderParm2.getValue("ONW_TRIAGE_NO", j).equals(onwOrderParm.getValue("TRIAGE_NO", i)) 
							&& orderParm2.getValue("ONW_ORDER_SEQ", j) !=null
							&& orderParm2.getValue("ONW_ORDER_SEQ", j).equals(onwOrderParm.getValue("SEQ_NO", i)) 
							&& orderParm2.getValue("ORDER_CODE", j) != null 
							&& orderParm2.getValue("ORDER_CODE", j).equals(onwOrderParm.getValue("ORDER_CODE", i))){
						onwOrderParm.removeRow(i);
						break;
					}
				}
			}		
		}
		// 判断是否有医生签字的医嘱
		for(int i=0; i<onwOrderParm.getCount(); i++){		
			if(onwOrderParm.getValue("SIGN_DR", i) != null 
					&& onwOrderParm.getValue("SIGN_DR", i).trim().length()>0){
				flg3 = true;
				break;
			}	
		}
		if(!flg3){
			return;
		}
		// 判断是否有医生已经签字，但没有开立的医嘱
		for(int i=0; i<onwOrderParm.getCount(); i++){
			if( (onwOrderParm.getValue("SIGN_DR", i) != null && onwOrderParm.getValue("SIGN_DR", i).trim().length()>0) 
					&& (!(onwOrderParm.getValue("EXE_FLG", i) != null && onwOrderParm.getValue("EXE_FLG", i).equals("Y"))) ){
				exeFlg = true;
				break;
			}	
		}
		if(!exeFlg){
			return;
		}
		if (this.messageBox("询问", "是否合并口头医嘱?", JOptionPane.YES_NO_OPTION) == 0) {// 合并
			this.insertOralOrder(onwOrderParm);
		}	
	}
			
	/**
	 * 返回空白处方签
	 * @param rxName
	 * @param rxType
	 * @return
	 */
	public String getNullRx(String rxName, String rxType){		
		TComboBox combo = (TComboBox) this.getComponent(rxName);
//		System.out.println("======comboData="+combo.getModel());
//		System.out.println("======comboData="+combo.getModel().getSize());
//		System.out.println("======comboData="+combo.getModel().getItems().elementAt(1).toString().split("【")[0]);
		if(combo==null || combo.getModel()==null || combo.getModel().getSize()<=0){
			return "";
		}
		for(int i=0; i<combo.getModel().getSize(); i++){
			String rxNo = combo.getModel().getItems().elementAt(i).toString().split("【")[0].trim();
			if(rxNo==null || rxNo.trim().length()==0){
				continue;
			}
			if(this.isNullRx(rxNo)){
				System.out.println("======空白处方签："+rxNo);
				return rxNo;
			}
		}	
		return "";
	}
	
	/**
	 * 判断是否是空白处方签（1、非空 2、没有医嘱）
	 * @param rxNo
	 * @return true，是空白处方签；false，不是空白处方签
	 */
	public boolean isNullRx(String rxNo){
		System.out.println("======rxNo="+rxNo);
		if(rxNo==null || rxNo.trim().length()==0){
			return false;
		}
		OpdOrder order = odo.getOpdOrder();
		String storeName = order.isFilter() ? OpdOrder.FILTER : OpdOrder.PRIMARY;
//		storeName = OpdOrder.PRIMARY;
//		System.out.println("======storeName="+storeName);
//		System.out.println("======filter="+order.getFilter());
		TParm allParm = order.getBuffer(storeName);
		System.out.println("======//////allParm="+allParm);
		for(int i=0; i<allParm.getCount(); i++){
			if(allParm.getValue("ORDER_CODE", i) != null 
					&& allParm.getValue("ORDER_CODE", i).trim().length()>0 
					&& allParm.getValue("RX_NO", i) != null 
					&& allParm.getValue("RX_NO", i).equals(rxNo)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 测试
	 */
	public void onTest(){
//		String rxName = EXA_RX;
//		TComboBox combo = (TComboBox) this.getComponent(rxName);
//		System.out.println("======comboData="+combo.getModel());
//		System.out.println("======comboData="+combo.getModel().getSize());
//		System.out.println("======comboData="+combo.getModel().getItems().elementAt(0).toString().split("【")[0]);
		OpdOrder order = odo.getOpdOrder();
		TParm parm = order.getBuffer(OpdOrder.PRIMARY);
		System.out.println("======parm="+parm);
		parm = order.getBuffer(OpdOrder.FILTER); 
		System.out.println("======parm="+parm);
//		TTabbedPane tabPanel = (TTabbedPane) this.getComponent("TTABPANELORDER");
//		tabPanel.setSelectedIndex(0);	
	}
	
	
}
