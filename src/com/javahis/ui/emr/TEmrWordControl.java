package com.javahis.ui.emr;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

import jdo.clp.CLPEMRTool;
import jdo.emr.EMRCreateHTMLTool;
import jdo.emr.EMRCreateXMLTool;
import jdo.emr.EMRCreateXMLToolForSD;
import jdo.emr.GetWordValue;
import jdo.emr.OptLogTool;
import jdo.hl7.Hl7Communications;
import jdo.med.MEDApplyTool;
import jdo.odi.OdiOrderTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import jdo.emr.NISTool;

import org.apache.commons.lang.StringUtils;

import painting.Image_Paint;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.DMessageIO;
import com.dongyang.tui.text.CopyOperator;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EImage;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.ENumberChoose;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.ESign;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.tui.text.ETD;
import com.dongyang.tui.text.ETR;
import com.dongyang.tui.text.ETable;
import com.dongyang.tui.text.IBlock;
import com.dongyang.tui.text.MModifyNode;
import com.dongyang.tui.text.div.DIV;
import com.dongyang.tui.text.div.MV;
import com.dongyang.tui.text.div.VPic;
import com.dongyang.tui.text.div.VText;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TMovePane;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.ui.base.TWordBase;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TList;
import com.javahis.util.ClipboardTool;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;
import com.sun.awt.AWTUtilities;

/**
 * <p>
 * Title: 电子病历公共模块
 * </p>
 *
 * <p>
 * Description: 电子病历书写、护理记录、会诊病历等
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author Sundx & Lix & Zhangjg
 * @version 1.0
 */
public class TEmrWordControl extends TControl implements DMessageIO {
	//DR_SIGN
	//EFixed  superiorSignIns=(EFixed)this.getWord().findObject("SUPERIOR_SIGN", EComponent.FIXED_TYPE);
	/**
	 * 调试开关
	 */
	public static final boolean isDebug=false;
	/**
	 * 格式
	 */
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 隐藏图层名称
	 */
	public static final String UNVISIBLE_MV = "UNVISITABLE_ATTR";

	/**
	 * 公用字典表标志
	 */
	private static final String PATTERN = "P";

	/**
	 * 单独字典表标志
	 */
	private static final String DICTIONARY = "D";
	/**
	 * WORD对象
	 */
	private static final String TWORD = "WORD";
	/**
	 * 树的名字
	 */
	private static final String TREE_NAME = "TREE";
	/**
	 * 知识库前缀
	 */
	private static final String KNOWLEDGE_STORE_PREFIX="EMR9";
	/**
	 * 过敏史抓取框控件名，必须唯一
	 */
	private static final String ALLERGY_KEY="ALLERGY";

	/**
	 * WORD对象
	 */
	private TWord word;
	/**
	 * 门急住别
	 */
	private String admType;
	/**
	 * 系统类别
	 */
	private String systemType;
	/**
	 * 病案号
	 */
	private String mrNo;
	/**
	 * 住院号
	 */
	private String ipdNo;
	/**
	 * 姓名
	 */
	private String patName;
	/**
	 * 英文姓名
	 */
	private String patEnName;
	/**
	 * 拿到年
	 */
	private String admYear;
	/**
	 * 拿到月
	 */
	private String admMouth;
	/**
	 * 就诊号
	 */
	private String caseNo;
	/**
	 * 子面板数据
	 */
	private TParm emrChildParm = new TParm();
	/**
	 * 树根
	 */
	private TTreeNode treeRoot;
	/**
	 * 经治医师
	 */
	private String vsDrCode;
	/**
	 * 主治医师
	 */

	private String attendDrCode;
	/**
	 * 科主任
	 */
	private String directorDrCode;
	/**
	 * 调用科室
	 */
	private String deptCode;
	/**
	 * 调用病区
	 */
	private String stationCode;
	/**
	 * 当前编辑状态
	 */
	private String onlyEditType;
	/**
	 * 就诊日期
	 */
	private Timestamp admDate;
	/**
	 * 权限：1,只读 2,读写 3,部分读写
	 */
	private String ruleType = "1";
	/**
	 * 打开样式
	 */
	private String styleType = "2";
	/**
	 * 调用类型
	 */
	private String type = "";
	/**
	 * 医院全称
	 */
	private String hospAreaName = "";
	/**
	 * 英文全称
	 */
	private String hospEngAreaName = "";
	/**
	 * 写类型
	 */
	private String writeType;
	/**
	 * 单病种
	 */
	private String diseDesc;// add by wanglong 20121025 为了在住院证上显示单病种
	private TParm diseBasicData; // add by wanglong 20121115 记录单病种基本信息
	TParm returnParm = new TParm();// add by wanglong 20121115 此程序的返回值
	private TParm opeBasicData; // add by wanglong 20130514 记录手术基本信息
	String adm_type = "";
	/**
	 * 预约床号
	 */
	private String bedNo ;  //  add by  chenxi 20130308


	/**
	 * 宏对应
	 */
	private Map microMap = new HashMap();

	/**
	 * 隐藏元素及对应的值；
	 */
	private Map hideElemMap = new HashMap();

	private String picPath = "C:/hispic/";
	// 画图临时文件夹
	private String picTempPath = "C:/hispic/temp/";

	/**
	 * 是否包含图片
	 */
	private boolean containPic = false;
	// 加入申请单号;
	private String medApplyNo = "";

	//是否是追加标志(日常病程)
	private boolean isApplend=false;

	/**
	 * 依赖文件名称
	 */
	private String subFileName;

	/**
	 * 转诊注记
	 */
	private String refFlg;

	//日志记录时间 
	private String  recordTime;
	/**
	 * 记录时间
	 */
	private long lRecordTime;

	/**
	 * 引用文件名
	 */
	private  String strRefFileName;

	/**
	 * 抓取框集合， 用于日常病程
	 */
	private List<String> captureNamesList=new ArrayList<String>();

	/**
	 * 手术单号
	 */
	private String opbookSeq;
	
	//RACHS计算参数
	//add by yyn 2017/11/10
	private int AgeGrp1_v = 0;//入ICU年龄<=30天
	private int AgeGrp2_v = 0;//入ICU年龄介于31天到365天（1岁）之间
	private int NC_STAN_v = 0;//非心脏畸形
	private int PREM_v = 0;//早产，指妊娠小于37周
	private int CP_SOP_v = 0;//单次手术中合并多个心脏手术术式
	//private int RCat1_v = 0;//风险分类1
	private int RCat2_v = 0;//风险分类2
	private int RCat3_v = 0;//风险分类3
	private int RCat4_v = 0;//风险分类4
	private int RCat5_v = 0;//风险分类5
	private int RCat6_v = 0;//风险分类6
	private double RACHSVal = 0.00;//RACHSVal
	private double rachs = 0.00;//RACHS
	private String RACHS = "";//RACHS
	private String row_num = "0";//第一类手术中的术式个数
	private String n2 = "0";//第二风险等级术式的个数
	private String n3 = "0";//第三风险等级术式的个数
	private String n4 = "0";//第四风险等级术式的个数
	private String n5 = "0";//第五风险等级术式的个数
	private String n6 = "0";//第六风险等级术式的个数
	private boolean click1 = false;//是否对风险等级一进行了操作
	private boolean click2 = false;//是否对风险等级二进行了操作
	private boolean click3 = false;//是否对风险等级三进行了操作
	private boolean click4 = false;//是否对风险等级四进行了操作
	private boolean click5 = false;//是否对风险等级五进行了操作
	private boolean click6 = false;//是否对风险等级六进行了操作
	// yyn 2017/11/10 end 

	public String getSubFileName() {
		return subFileName;
	}

	public void setSubFileName(String subFileName) {
		this.subFileName = subFileName;
	}

	public String getMedApplyNo() {
		return medApplyNo;
	}

	public void setMedApplyNo(String medApplyNo) {
		this.medApplyNo = medApplyNo;
	}


	public boolean isApplend() {
		return isApplend;
	}

	public void setApplend(boolean isApplend) {
		this.isApplend = isApplend;
	}

	/**
	 * 
	 * @return
	 */
	public String getRecordTime() {
		return recordTime;
	}

	/**
	 * 
	 * @param recordTime
	 */
	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}

	/**
	 * 
	 * @return
	 */
	public long getlRecordTime() {
		return lRecordTime;
	}

	/**
	 * 
	 * @param lRecordTime
	 */
	public void setlRecordTime(long lRecordTime) {
		this.lRecordTime = lRecordTime;
	}


	/**
	 * 获得引用文件
	 * @return
	 */
	public String getStrRefFileName() {
		return strRefFileName;
	}

	/**
	 * 设置引用文件
	 * @param strRefFileName
	 */
	public void setStrRefFileName(String strRefFileName) {
		this.strRefFileName = strRefFileName;
	}



	public String getOpbookSeq() {
		return opbookSeq;
	}

	public void setOpbookSeq(String opbookSeq) {
		this.opbookSeq = opbookSeq;
	}



	private String TREE_TYPE = null;//wuh add
	private final String TREE_TYPE_1 = "卫生部分类";
	private final String TREE_TYPE_2 = "医疗过程";
	private final String TREE_TYPE_3 = "科室分类";

	/**
	 * 动作类名字
	 */
	private static final String actionName = "action.odi.ODIAction";

	/**
	 * 手术安全核查单保存时校验术间绑定
	 * add by wangjc 20171013
	 */
	private boolean opeSaveCheck = false;
	
	public boolean isOpeSaveCheck() {
		return opeSaveCheck;
	}

	public void setOpeSaveCheck(boolean opeSaveCheck) {
		this.opeSaveCheck = opeSaveCheck;
	}

	public void onInit() {
		super.onInit();

		this.setOpbookSeq(((TParm)this.getParameter()).getValue("OPBOOK_SEQ"));
		if("Y".equals(((TParm)this.getParameter()).getValue("OPE_SAVE_CHECK"))){
			this.setOpeSaveCheck(true);
		}
		// 初始化WORD
		initWord();
		// 初始化界面
		initPage();
		// 注册事件
		initEven();
		// 三级检诊医师及值班医师：电子病历操作日志/EMR_OPTLOG/登陆
		if (this.isCheckUserDr() || this.isDutyDrList()) {
			TParm emrParm = new TParm();
			emrParm.setData("FILE_SEQ", "0");
			emrParm.setData("FILE_NAME", "");
			TParm result = OptLogTool.getInstance().writeOptLog(
					this.getParameter(), "L", emrParm);

		}
		//
		// 非三级检诊医师及值班医师：电子病历操作日志/EMR_OPTLOG/调阅
		else {
			TParm emrParm = new TParm();
			emrParm.setData("FILE_SEQ", "0");
			emrParm.setData("FILE_NAME", "");
			TParm result = OptLogTool.getInstance().writeOptLog(
					this.getParameter(), "R", emrParm);
		}

		// 自动保存定时器初始化
		// initTimer();
		initMicroMap();
		// 屏幕居中,并放大
		callFunction("UI|showMaxWindow");
		String[] btnStr = // wuh
			{ "SVAE_PAT", "CLINIC_DATA", "SUB_TEMPLATE", "SPECIAL_CHARS",
					"NATION_STANDARD", "CUSTOM_SORT1", "CUSTOM_SORT2","DEL_LOG"};
		for (int i = 0; i < btnStr.length; i++) {
			TButton newBtn = (TButton) this.getComponent(btnStr[i]);

			newBtn.setBorder(BorderFactory.createLineBorder(Color.black));
			newBtn.setContentAreaFilled(false);
		}
		//
		this.getWord().getPM().setUser(Operator.getID(), getOperatorName(Operator.getID()));
		//		
		// 对于转诊为避免编辑病历将菜单隐藏
		if ("Y".equals(refFlg)) {
			this.callFunction("UI|setMenuConfig",
					"%ROOT%\\config\\emr\\EMRForRefMenu.x"); // 菜单加载

			// 创建线程以满足在页面加载完成之后提示弹出框
			class MessageTread extends Thread {
				public void run() {
					try {
						Thread.sleep(2000);
						JOptionPane.showMessageDialog((Component)getComponent(), "尚未提取转诊病历", "消息", 1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			Object obj = this.getParameter();
			// 未提取转诊病历时给予提示
			if (StringUtils.equals("N", ((TParm) obj)
					.getValue("EMR_FILE_EXTRACT_FLG"))) {
				MessageTread messageTread = new MessageTread();
				messageTread.start();
			}
		}
	}

	private void initMicroMap() {
		TParm microParm = new TParm(getDBTool().select(
				"SELECT MICRO_NAME,DATA_ELEMENT_CODE FROM EMR_MICRO_CONVERT"));
		int count = microParm.getCount();
		for (int i = 0; i < count; i++) {
			microMap.put(microParm.getValue("MICRO_NAME", i), microParm
					.getValue("DATA_ELEMENT_CODE", i));
		}
	}

	/**
	 * 获取提示信息;
	 *
	 * @param elementGroup
	 *            String
	 * @param elementCode
	 *            String
	 * @return String
	 */
	private String getShowMessage(String elementGroup, String elementCode) {
		TParm nameParm = new TParm(getDBTool().select(
				"SELECT DATA_DESC FROM EMR_CLINICAL_DATAGROUP WHERE GROUP_CODE='"
						+ elementGroup + "' AND DATA_CODE='" + elementCode
						+ "'"));
		int count = nameParm.getCount();
		if (count > 0) {
			return nameParm.getValue("DATA_DESC", 0);
		}
		return elementCode;

	}

	/**
	 * 初始化WORD
	 */
	public void initWord() {
		word = this.getTWord(TWORD);
		this.setWord(word);
		// 字体
		this.getWord().setFontComboTag("ModifyFontCombo");
		// 字体
		this.getWord().setFontSizeComboTag("ModifyFontSizeCombo");

		// 变粗
		this.getWord().setFontBoldButtonTag("FontBMenu");

		// 斜体
		this.getWord().setFontItalicButtonTag("FontIMenu");

		//saveWord();
		//

	}

	/**
	 * 得到WORD对象
	 *
	 * @param tag
	 *            String
	 * @return TWord
	 */
	public TWord getTWord(String tag) {
		return (TWord) this.getComponent(tag);
	}

	/**
	 * 注册事件
	 */
	public void initEven() {
		// 单击选中树项目
		addEventListener(TREE_NAME + "->" + TTreeEvent.DOUBLE_CLICKED,
				"onTreeDoubled");

		//
		TParm evenParm = new TParm();
		evenParm.addListener("onDoubleClicked", this, "onDoubleClicked");
		//
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {

		this.hospAreaName = Manager.getOrganization().getHospitalCHNFullName(
				Operator.getRegion());
		this.hospEngAreaName = Manager.getOrganization()
				.getHospitalENGFullName(Operator.getRegion());

		// 得到系统类别
		Object obj = this.getParameter();
		if (obj != null) {
			this.setSystemType(((TParm) obj).getValue("SYSTEM_TYPE"));
			this.setMrNo(((TParm) obj).getValue("MR_NO"));
			this.patEnName = getPatEnName(this.getMrNo()).getValue("PAT_NAME1",
					0);
			this.setIpdNo(((TParm) obj).getValue("IPD_NO"));
			this.setPatName(((TParm) obj).getValue("PAT_NAME"));
			adm_type = (((TParm) obj).getValue("ADM_TYPE_ZYZ"));//yanjing 20130807
			if (((TParm) obj).getValue("PAT_NAME").equals("")) {
				this.setPatName(getPatEnName(this.getMrNo()).getValue(
						"PAT_NAME", 0));
			}
			this.setCaseNo(((TParm) obj).getValue("CASE_NO"));
			/**
			 * System.out.println("==CASE_NO==" + ((TParm)
			 * obj).getValue("CASE_NO"));
			 **/
			String yearStr = ((TParm) obj).getValue("CASE_NO").substring(0, 2);
			this.setAdmYear(yearStr);
			String mouthStr = ((TParm) obj).getValue("CASE_NO").substring(2, 4);
			this.setAdmMouth(mouthStr);
			this.setAdmType(((TParm) obj).getValue("ADM_TYPE"));
			this.setDeptCode(((TParm) obj).getValue("DEPT_CODE"));
			this.setStationCode(((TParm) obj).getValue("STATION_CODE"));
			this.setAdmDate(((TParm) obj).getTimestamp("ADM_DATE"));
			this.ruleType = ((TParm) obj).getValue("RULETYPE");
			this.styleType = ((TParm) obj).getValue("STYLETYPE");
			String typeStr = ((TParm) obj).getValue("TYPE");
			if (typeStr.length() == 0) {
				this.type = this.getAdmType();
			} else {
				this.type = typeStr;
			}
			this.writeType = ((TParm) obj).getValue("WRITE_TYPE");
			this.bedNo = ((TParm) obj).getValue("BED_NO"); //  chenxi add 20130308  预约床号
			this.diseDesc = ((TParm) obj).getValue("DISE_DESC");//add by wanglong 20121025 为了在住院证上显示单病种

			if(((TParm) obj).getData("diseData")!=null){ //add by wanglong 20121115 记录单病种基本信息
				this.diseBasicData=(TParm)((TParm) obj).getData("diseData");
			}
			if (((TParm) obj).getData("OPE_DATA") != null) { // add by wanglong 20130514 给病历传入手术基本信息
				// this.opeBasicData = ((TParm) obj).getParm("OPE_DATA");
				this.opeBasicData = (TParm) ((TParm) obj).getData("OPE_DATA");
			}
			setTMenuItem(false);
			this.refFlg = ((TParm)obj).getValue("REF_FLG"); // add by wangb 20150828 转诊注记

			// 拿到调用数据
			TParm emrFileDataParm = (TParm) ((TParm) obj)
					.getData("EMR_FILE_DATA");
			// 其它表单进入
			if (emrFileDataParm != null) {
//				System.out.println("11111111111111111111111111111111111");
				// 修改的单据
				if (emrFileDataParm.getBoolean("FLG")) {
//					System.out.println("22222222222222222222222222222222");
					// 打开病历
					if (!this.getWord().onOpen(
							emrFileDataParm.getValue("FILE_PATH"),
							emrFileDataParm.getValue("FILE_NAME"), 3, false)) {
						return;
					}
					
					// add by wangqing 20180122  start 急诊检查申请单新增跌倒评估、疼痛评估
					String sql = "SELECT CASE_NO, TRIAGE_NO, MR_NO, FALL_SCORE, FALL_DESC, PAIN_SCORE, PAIN_DESC "
							+ "FROM ERD_EVALUTION WHERE CASE_NO='"+caseNo+"' ";
					TParm result = new TParm(TJDODBTool.getInstance().select(sql));
					if(result.getErrCode()<0){
						return;
					}
					if(result.getCount()>0){
						Map<String, String> FALL_DESC_VALUE_MAP=new HashMap<String, String>();
						Map<String, String> PAIN_DESC_VALUE_MAP=new HashMap<String, String>();
						// MAP init start
						FALL_DESC_VALUE_MAP.put("", "");
						FALL_DESC_VALUE_MAP.put("1", "零危险");
						FALL_DESC_VALUE_MAP.put("2", "低度危险");
						FALL_DESC_VALUE_MAP.put("3", "高度危险");
						
						PAIN_DESC_VALUE_MAP.put("", "");
						PAIN_DESC_VALUE_MAP.put("1", "无痛");
						PAIN_DESC_VALUE_MAP.put("2", "轻度疼痛");
						PAIN_DESC_VALUE_MAP.put("3", "中度疼痛");
						PAIN_DESC_VALUE_MAP.put("4", "重度疼痛");
						PAIN_DESC_VALUE_MAP.put("5", "剧烈疼痛");
						PAIN_DESC_VALUE_MAP.put("6", "无法忍受");
						// MAP init end
						
						this.setECaptureValue(word, "FALL_DESC", FALL_DESC_VALUE_MAP.get(result.getValue("FALL_DESC", 0)));
						this.setECaptureValue(word, "PAIN_DESC", PAIN_DESC_VALUE_MAP.get(result.getValue("PAIN_DESC", 0)));
					}
					// add by wangqing 20180122 end
					
					TParm allParm = new TParm();
					allParm.setData("FILE_TITLE_TEXT", "TEXT",
							this.hospAreaName);
					allParm.setData("FILE_TITLEENG_TEXT", "TEXT",
							this.hospEngAreaName);
					allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this
							.getMrNo());
					/*allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this
							.getIpdNo());*/
					//住院号 -> 病人姓名
					allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getPatName());
					//
					allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
					// 加入申请单急
					allParm.setData("FLG", "TEXT", emrFileDataParm.getValue(
							"URGENT_FLG").equalsIgnoreCase("Y") ? "急" : "");

					allParm.setData("BILL_TYPE", "TEXT", emrFileDataParm.getValue("BILL_TYPE")); //add by huangtt 20170401显示医嘱收费状态

					if (((TParm) obj).getValue("ERD_LEVEL").equals("1")
							|| ((TParm) obj).getValue("ERD_LEVEL").equals("2")) {// wanglong add
						// 20150407
						allParm.setData("ERD_URGENT", "TEXT", "(急)");
					}
					allParm.addListener("onDoubleClicked", this,
							"onDoubleClicked");
					allParm.addListener("onMouseRightPressed", this,
							"onMouseRightPressed");
					this.getWord().setWordParameter(allParm);
					// $$=======add by lx 2012/03/20加入申请单号start=============$$//
					this
					.setMedApplyNo(emrFileDataParm
							.getValue("MED_APPLY_NO"));
					// $$=======add by lx 2012/03/20加入申请单号end=============$$//
					// 设置不可编辑
					this.getWord().setCanEdit(false);
					// 设置宏(有旧病历不更新宏)
					// setMicroField();
					// 设置编辑状态
					this.setOnlyEditType("ONLYONE");
					TParm emrParm = new TParm();
					emrParm.setData("FILE_SEQ", emrFileDataParm
							.getValue("FILE_SEQ"));
					emrParm.setData("FILE_NAME", emrFileDataParm
							.getValue("FILE_NAME"));
					emrParm.setData("CLASS_CODE", emrFileDataParm
							.getValue("CLASS_CODE"));
					emrParm.setData("SUBCLASS_CODE", emrFileDataParm
							.getValue("SUBCLASS_CODE"));
					emrParm.setData("CASE_NO", this.getCaseNo());
					emrParm.setData("MR_NO", this.getMrNo());
					emrParm.setData("IPD_NO", this.getIpdNo());
					emrParm.setData("FILE_PATH", emrFileDataParm
							.getValue("FILE_PATH"));
					emrParm.setData("DESIGN_NAME", emrFileDataParm
							.getValue("DESIGN_NAME"));
					emrParm.setData("DISPOSAC_FLG", emrFileDataParm
							.getValue("DISPOSAC_FLG"));

					// 设置当前编辑数据
					this.setEmrChildParm(emrParm);
					setTMenuItem(false);

					//add by huangtt 20160104 检伤评估单更新姓名，性别，年龄
					if(((TParm) obj).getBoolean("ERD")){
						// 设置宏
						setMicroField(true);
					}

					//
					// 申请编辑
					if ((ruleType.equals("2") || ruleType.equals("3"))
							&& ("O".equals(this.getAdmType()) || "E"
									.equals(this.getAdmType()))
							|| ((ruleType.equals("2") || ruleType.equals("3")) && isCheckUserDr())) {
//						System.out.println("33333333333333333333333333333333333333333333333333");
						onEdit();
					}
				}
				// 新建的单据
				else {
//					System.out.println("4444444444444444444444444444444444444444444");
					// 打开病历
					if (!this.getWord().onOpen(
							emrFileDataParm.getValue("TEMPLET_PATH"),
							emrFileDataParm.getValue("EMT_FILENAME"), 2, false)) {

						return;
					}
					
					// add by wangqing 20180122  start 急诊检查申请单新增跌倒评估、疼痛评估
					String sql = "SELECT CASE_NO, TRIAGE_NO, MR_NO, FALL_SCORE, FALL_DESC, PAIN_SCORE, PAIN_DESC "
							+ "FROM ERD_EVALUTION WHERE CASE_NO='"+caseNo+"' ";
					TParm result = new TParm(TJDODBTool.getInstance().select(sql));
					if(result.getErrCode()<0){
						return;
					}
					if(result.getCount()>0){
						Map<String, String> FALL_DESC_VALUE_MAP=new HashMap<String, String>();
						Map<String, String> PAIN_DESC_VALUE_MAP=new HashMap<String, String>();
						// MAP init start
						FALL_DESC_VALUE_MAP.put("", "");
						FALL_DESC_VALUE_MAP.put("1", "零危险");
						FALL_DESC_VALUE_MAP.put("2", "低度危险");
						FALL_DESC_VALUE_MAP.put("3", "高度危险");
						
						PAIN_DESC_VALUE_MAP.put("", "");
						PAIN_DESC_VALUE_MAP.put("1", "无痛");
						PAIN_DESC_VALUE_MAP.put("2", "轻度疼痛");
						PAIN_DESC_VALUE_MAP.put("3", "中度疼痛");
						PAIN_DESC_VALUE_MAP.put("4", "重度疼痛");
						PAIN_DESC_VALUE_MAP.put("5", "剧烈疼痛");
						PAIN_DESC_VALUE_MAP.put("6", "无法忍受");
						// MAP init end
						
						this.setECaptureValue(word, "FALL_DESC", FALL_DESC_VALUE_MAP.get(result.getValue("FALL_DESC", 0)));
						this.setECaptureValue(word, "PAIN_DESC", PAIN_DESC_VALUE_MAP.get(result.getValue("PAIN_DESC", 0)));
					}
					// add by wangqing 20180122 end

					TParm allParm = new TParm();
					allParm.setData("FILE_TITLE_TEXT", "TEXT",
							this.hospAreaName);
					allParm.setData("FILE_TITLEENG_TEXT", "TEXT",
							this.hospEngAreaName);
					allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this
							.getMrNo());
					/*allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this
							.getIpdNo());*/
					//住院号 -> 病人姓名
					allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getPatName());

					allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
					// 加入申请单急
					allParm.setData("FLG", "TEXT", emrFileDataParm.getValue(
							"URGENT_FLG").equalsIgnoreCase("Y") ? "急" : "");

					allParm.setData("BILL_TYPE", "TEXT", emrFileDataParm.getValue("BILL_TYPE")); //add  by huangtt 20170401显示医嘱收费状态


					if (((TParm) obj).getValue("ERD_LEVEL").equals("1")
							|| ((TParm) obj).getValue("ERD_LEVEL").equals("2")) {// wanglong add
						// 20150407
						allParm.setData("ERD_URGENT", "TEXT", "(急)");
					}
					allParm.addListener("onDoubleClicked", this,
							"onDoubleClicked");
					allParm.addListener("onMouseRightPressed", this,
							"onMouseRightPressed");

					// $$=======add by lx 2012/03/20加入申请单号start=============$$//
					this
					.setMedApplyNo(emrFileDataParm
							.getValue("MED_APPLY_NO"));
					// $$=======add by lx 2012/03/20加入申请单号end=============$$//
					this.getWord().setWordParameter(allParm);
					// 设置不可编辑
					this.getWord().setCanEdit(false);
					// 设置宏
					setMicroField(true);
					// 设置编辑状态
					this.setOnlyEditType("NEW");
					// 设置节点值
					this.setEmrChildParm(emrFileDataParm);
					// TParm emrNameP = this.getFileServerEmrName();
					this.setEmrChildParm(this.getFileServerEmrName());
					setTMenuItem(false);

					// 申请编辑
					if ((ruleType.equals("2") || ruleType.equals("3"))
							&& ("O".equals(this.getAdmType()) || "E"
									.equals(this.getAdmType()))
							|| (ruleType.equals("2") || ruleType.equals("3"))
							&& (isCheckUserDr() || this.isDutyDrList())) {
						onEdit();
					}
					// 刷新抓文本
					this.getWord().fixedTryReset(this.getMrNo(),
							this.getCaseNo());
				}

			} else {
				this.getWord().setCanEdit(false);
			}
			// 调用其他宏
			TParm ioParm = new TParm();
			// 设置文件是否是新文件
			if (emrFileDataParm == null) {
				ioParm.setData("FLG", false);
			} else {
				ioParm.setData("FLG", emrFileDataParm.getBoolean("FLG"));
			}

			// 调用宏
			ioParm.addListener("setMicroData", this, "setMicroData");
			// 调用抓取
			ioParm.addListener("getCaptureValue", this, "getCaptureValue");
			// 调用抓取组
			ioParm.addListener("getCaptureValueArray", this,
					"getCaptureValueArray");
			// 设置抓取框值
			ioParm.addListener("setCaptureValue", this, "setCaptureValueArray");
			// ((TParm)obj).runListener("MicroListener",ioParm);
			// EMR_LISTENER(null Object[])
			// ((TParm)obj).runListener("EMR_LISTENER","S");
			((TParm) obj).runListener("EMR_LISTENER", ioParm);
			if ("1".equals(this.styleType)) {
				onCloseChickedCY();
			}
			if (emrFileDataParm != null) {
				if (!emrFileDataParm.getBoolean("FLG")) {
					this.setOnlyEditType("NEW");
				}
			} else {
				this.setOnlyEditType("NEW");
			}
		} else {
			//TEST
			// this.setSystemType("EMG");
			// this.setMrNo("000000000052");
			// this.setIpdNo("");
			// this.setPatName("韩雪儿");
			// this.setCaseNo("100414000022");
			// this.setAdmYear("2010");
			// this.setAdmMouth("04");
			// this.setAdmType("E");
			// this.ruleType="2";
			// this.setDeptCode("10101");
		}
		// 加载树
		//loadTree();
		//=======add by yyn 2017/11/10增加监听 start=======//
		this.getWord().addListener(TWordBase.SINGLE_CHOOSE_SELECTED, this, "singleChooseSelected");//单选监听
		//=======add by yyn 2017/11/10增加监听 end=======//
		this.doInitTree();
	}

	/**
	 * 清空树
	 */
	private void doInitTree(){//wuh
		treeRoot = this.getTTree(TREE_NAME).getRoot();
		treeRoot.setText(null);
		treeRoot.removeAllChildren();
		this.setValue("MR_NO", this.getMrNo());
		if ("en".equals(this.getLanguage()) && patEnName != null && patEnName.length() > 0) {
			this.setValue("PAT_NAME", this.patEnName);
		} else {
			this.setValue("PAT_NAME", this.getPatName());
		}
		this.setValue("IPD_NO", this.getIpdNo());
		isCheckUserDr();
		onNationStandard();
	}

	/**
	 * 拿到病患英文名
	 *
	 * @param mrNo
	 *            String
	 * @return String
	 */
	public TParm getPatEnName(String mrNo) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT PAT_NAME,PAT_NAME1"
						+ " FROM SYS_PATINFO"
						+ " WHERE MR_NO='" + mrNo + "'"));
		return parm;
	}

	/**
	 * 常用选项关闭事件
	 */
	public void onCloseChickedCY() {

		TMovePane mp = (TMovePane) callFunction("UI|MOVEPANE|getThis");
		mp.onDoubleClicked(true);
		((TMovePane) this.getComponent("MOVEPANE")).setEnabled(false);
	}

	/**
	 * 拿到抓取值
	 *
	 * @param name
	 *            String
	 * @return String
	 */
	public String getCaptureValue(String name) {
		return this.getWord().getCaptureValue(name);
	}

	/**
	 * 组调用
	 *
	 * @param names
	 *            String[]
	 * @return TParm
	 */
	public Map getCaptureValueArray(List name) {
		Map result = new HashMap();
		int rowCount = name.size();
		for (int i = 0; i < rowCount; i++) {
			result.put(name.get(i), this.getCaptureValue("" + name.get(i)));
		}
		return result;
	}

	/**
	 * 设置抓取框
	 *
	 * @param name
	 *            String
	 * @param value
	 *            String
	 */
	public void setCaptureValueArray(String name, String value) {
		//原来名子无重复的,现在需要在Tword类中加个方法 通过宏名取控件方法， 加值； 同名会覆盖以前的值；
		boolean isSetCaptureValue = this.setCaptureValue(name, value);
		if (!isSetCaptureValue) {
			ECapture ecap = this.getWord().findCapture(name);
			if (ecap == null) {
				return;
			}
			ecap.setFocusLast();
			ecap.clear();
			ecap.getFocusManager().pasteString(value);
			this.getWord().getFocusManager().update();
			//this.getWord().pasteString(value);
		}

	}

	/**
	 * 设置宏数据
	 *
	 * @param name
	 *            String
	 * @param value
	 *            String
	 */
	public void setMicroData(String name, String value) {
		this.getWord().setMicroField(name, value);
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
	 * 三级检诊医生编辑权限
	 */
	private void setEditLevel() {
		/*String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy年MM月dd日 HH时mm分ss秒");*/

		int stuts = this.getWord().getNodeIndex();
		//int stutsOnly = this.getWord().getNodeIndex();
		if ("ODI".equals(this.getSystemType())) {
			// 经治医师
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == -1) {
				stuts = 0;
			}
			// 主治医师
			if (this.getAttendDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == 0) {
				stuts = 1;
			}
			// 主任医师
			if (this.getDirectorDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == 1) {
				stuts = 2;
			}
			// 即是经治医师又是主治医师
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getAttendDrCode().equals(Operator.getID())) {
				stuts = 1;
			}
			// 即是经治医师又是主治医师又是主任医师
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getAttendDrCode().equals(Operator.getID())
					&& this.getDirectorDrCode().equals(Operator.getID())) {
				stuts = 2;
			}
		}
		// 修改记录
		MModifyNode modifyNode = this.getWord().getPM().getModifyNodeManager();
		//
		if( null==modifyNode ){
			this.getWord().getPM().setModifyNodeManager( new MModifyNode() );
		}

		if (stuts != this.getWord().getNodeIndex()) {
			this.getWord().setNodeIndex(stuts);
		}
		saveWord();
	}

	/**
	 * 三级检诊医生编辑权限
	 */
	private void setEditLevelCancel() {
		String creatFileUser = this.getWord().getFileLastEditUser();
		int stuts = this.getWord().getNodeIndex();
		if ("ODI".equals(this.getSystemType())) {
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == 0
					&& creatFileUser.equals(Operator.getID())) {
				stuts = -1;
			}
			if (this.getAttendDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == 1
					&& creatFileUser.equals(Operator.getID())) {
				stuts = 0;
			}
			if (this.getDirectorDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == 2
					&& creatFileUser.equals(Operator.getID())) {
				stuts = 1;
			}
			// 即是经治医师又是主治医师
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getAttendDrCode().equals(Operator.getID())) {
				stuts = -1;
			}
			// 即是经治医师又是主治医师又是主任医师
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getAttendDrCode().equals(Operator.getID())
					&& this.getDirectorDrCode().equals(Operator.getID())) {
				stuts = -1;
			}
		}
		if (stuts == this.getWord().getNodeIndex()) {
			return;
		}
		this.getWord().setNodeIndex(stuts);
		saveWord();
	}

	/**
	 * 
	 */
	private void saveWord() {
		boolean mSwitch = this.getWord().getMessageBoxSwitch();
		// this.messageBox("mSwitch" + mSwitch);
		this.getWord().setMessageBoxSwitch(false);
		this.getWord().onSave();
		this.getWord().setMessageBoxSwitch(mSwitch);
	}

	/**
	 * 删除病程日志
	 */
	public void delDayLog() {
		// 1.是引用类型的文件（日常病程）
		if (emrChildParm.getValue("CLASS_STYLE").equals("4")) {
			// 假如是引用文件模版，双击处理
			String captureName = getInCaptureName();
			// this.messageBox_("11111111captureName"+captureName);
			if (StringUtils.isEmpty(captureName)) {
				this.messageBox("请将光标移到日志抓取框中,进行清除！");
				return;
			}
			//
			if (this.messageBox("询问", "确认清除抓取框中病程日志？", 2) == 0) {
				// 1.校验
				if (this.getWord().getFileOpenName() == null) {
					// 没有需要编辑的文件
					this.messageBox("E0100");
					return;
				}
				//


				//
				ECapture captureStart = this.getWord().findCapture(captureName);
				captureStart.setFocus(0);
				captureStart.onFocusToLeft();
				//.onFocusToLeft();
				EComponent comp=this.getWord().getFocusManager().getFocus();
				//this.messageBox("---comp---"+comp);
				//无签名情况
				if(comp!=null){
					//有签名控件
					if(comp instanceof EFixed){
						((EFixed)comp).deleteFixed();
					}
				}
				//
				String fileName = captureStart.getRefFileName();
				//this.messageBox("===fileName=="+fileName);
				//
				ECapture captureEnd = captureStart.getEndCapture();
				captureStart.clear();
				captureStart.deleteFixed();
				captureEnd.deleteFixed();
				this.getWord().update();
				this.onsaveEmr(false);
				// 加入询问删除框
				TParm emrParm=this.getEmrChildParm();

				// 6、病历删除权限控制：

				// 忽略删除文件的错误
				try {
					// 删除文件
					delFileTempletFile(emrParm.getValue("FILE_PATH"), fileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 删除数据库数据
				Map del = this.getDBTool().update(
						"DELETE EMR_FILE_INDEX WHERE CASE_NO='"
								+ emrParm.getValue("CASE_NO")
								+ "' AND FILE_NAME='"
								+ fileName + "'");
				TParm delParm = new TParm(del);
				if (delParm.getErrCode() < 0) {
					// 删除失败
					//this.messageBox("E0003");
					return;
				}
				// 删除成功
				//this.messageBox("P0003");
				// 加载树
				loadTree();
			} else {

				return;
			}

		}else{
			this.messageBox("引用主文件才能使用！例如：日常病程记录");
			return;
		}
	}


	/**
	 * 签名
	 */
	public void onSign() {
		//1.校验
		if (this.getWord().getFileOpenName() == null) {
			// 没有需要编辑的文件
			this.messageBox("E0100");
			return;
		}
		//
		//第一次病程文件直接签名的情况
		if(this.isApplend()){
			//查找 SIGN插入点
			EFixed  fixed=(EFixed)this.getWord().findObject("SIGN", EComponent.FIXED_TYPE);
			fixed.onFocusToRight();
			String text=Operator.getName();
			// 1. 插入签名 控件
			Timestamp nowDate = SystemTool.getInstance().getDate();
			word.getFocusManager().insertSign("H.09",Operator.getID(), Operator.getID(), text, Long.toString(nowDate.getTime()));
			//保存文件
			onsaveEmr(false); 

			//再做提交操作
			setEditLevel();
			TParm emrParm = this.getEmrChildParm();
			//
			//2：代表签名并提交
			this.setOptDataParm(emrParm, 2);
			//
			if (this.saveEmrFile(emrParm)) {
				this.setEmrChildParm(emrParm);
				this.messageBox("签名成功！");
				this.getWord().setCanEdit(false);
				this.getWord().onPreviewWord();
				//
				//假如是日常病程文件，新建追加文件。
				if(this.isApplend()){
					onSave();
				}
				return;
			} else {
				this.messageBox("签名失败！");
				return;
			}
		}
		//第二种情况
		//上级医师直接签名
		//1.是引用类型的文件（日常病程）
		if(emrChildParm.getValue("CLASS_STYLE").equals("4")){
			// 假如是引用文件模版，双击处理
			String captureName = getInCaptureName();
			/**
			 * 
			 */
			if(StringUtils.isEmpty(captureName)){
				this.messageBox("请将光标移到日志抓取框中,进行签名！");
				return;
			}
			//2.隐藏打开 引用文件
			popupEmrEditWindw(false,true);
			//3.进行签名操作（模版   固定文件  标出签名位置， ）

			//4.判断有没有  其它签名， 有则插入    固定文本后加入    XXX/
			return;
		}
		//第三种情况
		//判断是否已签名
		// 判断是否有下一级对象
		/*		if (!this.checkonSumbit()) {
			this.messageBox("无上级医师,无法提交！");
			return;
		}*/
		// 4、病历提交权限控制：
		/*		if (!this.checkDrForSubmit()) {
			this.messageBox("E0011"); // 权限不足
			return;
		}*/
		//1.
		//判断医师是否已经签名
		ESign  sign1=(ESign)this.getWord().findObject(Operator.getID(), EComponent.SIGN_TYPE);
		ESign  sign2=(ESign)this.getWord().findObject(Operator.getID()+"_1", EComponent.SIGN_TYPE);
		//this.messageBox("--sign1---"+sign1);
		String signName=Operator.getID();
		//普通病历签名的情况
		if(sign1!=null){
			signName=Operator.getID()+"_1";

		}
		if(sign2!=null){
			this.messageBox("您已签名！");
			return;
		}
		//
		//0.直接签名的情况    有两个插入点的情况
		//1.DR_SIGN
		//2.SUPERIOR_SIGN
		//假如有 DR_SIGN插入点  并且  右侧无签名
		Timestamp nowDate = SystemTool.getInstance().getDate();
		//
		EFixed  drSignIns=(EFixed)this.getWord().findObject("DR_SIGN", EComponent.FIXED_TYPE);
		if(drSignIns!=null){
			drSignIns.setFocusLast();
			drSignIns.onFocusToRight();
			EComponent comp=this.getWord().getFocusManager().getFocus();
			//this.messageBox("---comp---"+comp);
			//无签名情况
			if(comp!=null){
				//有签名控件
				if(comp instanceof ESign){

				}else{
					drSignIns.setFocusLast();
					word.insertSign("H.09",signName, Operator.getID(), Operator
							.getName(), Long.toString(nowDate.getTime()));
					//保存文件
					onsaveEmr(false); 
					//再做提交操作
					setEditLevel();
					TParm emrParm = this.getEmrChildParm();
					//
					//2：代表签名并提交
					this.setOptDataParm(emrParm, 2);
					//
					if (this.saveEmrFile(emrParm)) {
						this.setEmrChildParm(emrParm);
						this.messageBox("签名成功！");
						this.getWord().setCanEdit(false);
						this.getWord().onPreviewWord();
						return;
					} else {
						this.messageBox("签名失败！");
						return;
					}
				}

			}

		}
		//假如有 SUPERIOR_SIGN插入点  并且  右侧无签名
		EFixed  superiorSignIns=(EFixed)this.getWord().findObject("SUPERIOR_SIGN", EComponent.FIXED_TYPE);
		if(superiorSignIns!=null){
			superiorSignIns.setFocusLast();
			superiorSignIns.onFocusToRight();
			EComponent comp=this.getWord().getFocusManager().getFocus();
			//无签名情况
			if(comp!=null){
				if(comp instanceof ESign){

				}else{
					superiorSignIns.setFocusLast();
					word.insertSign("H.09",signName, Operator.getID(), Operator
							.getName(), Long.toString(nowDate.getTime()));
					//
					//保存文件
					onsaveEmr(false); 
					//再做提交操作
					setEditLevel();
					TParm emrParm = this.getEmrChildParm();
					//
					//2：代表签名并提交
					this.setOptDataParm(emrParm, 2);
					//
					if (this.saveEmrFile(emrParm)) {
						this.setEmrChildParm(emrParm);
						this.messageBox("签名成功！");
						this.getWord().setCanEdit(false);
						this.getWord().onPreviewWord();
						return;
					} else {
						this.messageBox("签名失败！");
						return;
					}
				}
			}    		
		}   	
		//
		// 以下为无插入点的情况下， 直接插入到插入点右侧
		if (messageBox("提示信息",
				"病历模版中未设置签名插入点，确认以您光标位置为签名位置吗? \n",
				this.YES_NO_OPTION) != 0) {
			return;
		}
		// 1. 插入签名 控件		
		word.insertSign("H.09",signName, Operator.getID(), Operator
				.getName(), Long.toString(nowDate.getTime()));
		//
		//保存文件
		onsaveEmr(false); 

		//再做提交操作
		setEditLevel();
		TParm emrParm = this.getEmrChildParm();
		//
		//2：代表签名并提交
		this.setOptDataParm(emrParm, 2);
		//
		if (this.saveEmrFile(emrParm)) {
			this.setEmrChildParm(emrParm);
			this.messageBox("签名成功！");
			this.getWord().setCanEdit(false);
			this.getWord().onPreviewWord();
			//
			//假如是日常病程文件，新建追加文件。
			if(this.isApplend()){
				onSave();
			}
			return;
		} else {
			this.messageBox("签名失败！");
			return;
		}
		//

	}	

	/**
	 * 
	 * 取消签名
	 * 
	 */
	public void onSignCancel(){
		//
		if (this.getWord().getFileOpenName() == null) {
			// 没有需要编辑的文件
			this.messageBox("E0100");
			return;
		}

		//引用类型的病历(病程文件处理)
		if(emrChildParm.getValue("CLASS_STYLE").equals("4")){
			this.messageBox("请双击进入病程记录详情后再取消签名！");
			return;
		}
		//控件  判断下   是否有上级医师签名 与 操作者不一样者提示
		//,
		//假如有 SUPERIOR_SIGN插入点  并且  右侧无签名
		EFixed  superiorSignIns=(EFixed)this.getWord().findObject("SUPERIOR_SIGN", EComponent.FIXED_TYPE);
		//
		if(superiorSignIns!=null){
			superiorSignIns.setFocusLast();
			superiorSignIns.onFocusToRight();
			EComponent comp=this.getWord().getFocusManager().getFocus();
			//无签名情况
			if(comp!=null){
				if(comp instanceof ESign){
					String s1=((ESign)comp).getSignCode();
					if(!s1.equals(Operator.getID())){
						this.messageBox("请上级医师取消签名后，您才可以取消签名！");
						return;
					}       			
				}
			}    		
		}
		//
		List<ESign> signs=getSign();
		//this.messageBox("signs size:"+signs.size());
		if(signs!=null&&signs.size()>0){
			for(ESign theSign:signs){
				//有签名情况，删除掉
				theSign.setFocus(1);
				theSign.deleteFixed();
			}

		}else{
			this.messageBox("您没有签名，不用取消签名！");
			return;
		}

		//
		//保存病历
		onsaveEmr(false); 
		//取消提交操作
		// 5、病历取消提交权限控制：
		/*		if (!this.checkDrForSubmitCancel()) {
			this.messageBox("E0011"); // 权限不足
			return;
		}*/

		//
		TParm emrParm = this.getEmrChildParm();
		//setEditLevelCancel();
		if (this.saveEmrFile(emrParm)) {
			this.setEmrChildParm(emrParm);
			this.getWord().setCanEdit(true);
			this.getWord().onEditWord();
			this.messageBox("取消签名成功！");
			return;
		} else {
			this.messageBox("取消签名失败！");
			return;
		}

	}

	/**
	 * 提交
	 */
	public void onSubmit() {
		if (this.getWord().getFileOpenName() == null) {
			// 没有需要编辑的文件
			this.messageBox("E0100");
			return;
		}
		// 4、病历提交权限控制：
		if (!this.checkDrForSubmit()) {
			this.messageBox("E0011"); // 权限不足
			return;
		}
		// 判断是否有下一级对象
		if (!this.checkonSumbit()) {
			this.messageBox("无上级医师,无法提交！");
			return;
		}
		// 泰心暂不需要
		setEditLevel();
		TParm emrParm = this.getEmrChildParm();
		this.setOptDataParm(emrParm, 2);
		if (this.saveEmrFile(emrParm)) {
			this.setEmrChildParm(emrParm);
			this.messageBox("提交成功！");
			this.getWord().setCanEdit(false);
			this.getWord().onPreviewWord();
			return;
		} else {
			this.messageBox("提交失败！");
			return;
		}
	}

	public boolean checkonSumbit() {
		if (Operator.getID().equals("")) {
			return false;
		} else if (Operator.getID().equals(this.getVsDrCode())) {
			if (this.getAttendDrCode().equals("")) {
				return false;
			}
		} else if (Operator.getID().equals(this.getAttendDrCode())) {
			if (this.getDirectorDrCode().equals("")) {
				return false;
			}
		} else if (Operator.getID().equals(this.getDirectorDrCode())) {
			return false;
		}
		return true;

	}

	/**
	 * 取消提交
	 */
	public void onSubmitCancel() {
		if (this.getWord().getFileOpenName() == null) {
			// 没有需要编辑的文件
			this.messageBox("E0100");
			return;
		}
		// 5、病历取消提交权限控制：
		if (!this.checkDrForSubmitCancel()) {
			this.messageBox("E0011"); // 权限不足
			return;
		}
		TParm emrParm = this.getEmrChildParm();
		if (this.checkUserSubmit(emrParm, Operator.getID())) {
			if (this.messageBox("询问", "当前用户已提交，是否取消提交？", 2) != 0) {
				return;
			}
			this.setOptDataParm(emrParm, 3);
		} else {
			if (this.messageBox("询问", "当前用户未提交，是否取消提交？", 2) != 0) {
				return;
			}
			this.setOptDataParm(emrParm, 4);
		}
		// 泰心暂去掉
		setEditLevelCancel();
		if (this.saveEmrFile(emrParm)) {
			this.setEmrChildParm(emrParm);
			this.getWord().setCanEdit(true);
			this.getWord().onEditWord();
			this.messageBox("取消提交成功！");
			return;
		} else {
			this.messageBox("取消提交失败！");
			return;
		}
	}

	/**
	 * 申请编辑
	 */
	public boolean onEdit() { //modify by wanglong 20121205
		if (this.getWord().getFileOpenName() == null) {
			// 没有需要编辑的文件
			this.messageBox("E0100");
			return false; //modify by wanglong 20121205
		}
		// 读写权限
		if (ruleType.equals("1")) {
			// 您没有编辑权限
			this.messageBox("E0102");
			return false; //modify by wanglong 20121205

		} else {
			if (!ruleType.equals("3")) {
				//是住院则，按角色
				if (this.getSystemType().equals("ODI")) {
					// 角色判断 必须是同等职位的 才可修改
					/**
					 * String userRule = this.getWord().getFileLastEditUser();
					 * TParm ruleParm = new TParm(this.getDBTool().select("SELECT B.POS_TYPE FROM SYS_OPERATOR A,SYS_POSITION B WHERE A.POS_CODE=B.POS_CODE AND USER_ID='"
					 * + userRule + "'"));
					 *
					 * TParm ruleParmM = new TParm(this.getDBTool().select("SELECT B.POS_TYPE FROM SYS_OPERATOR A,SYS_POSITION B WHERE A.POS_CODE=B.POS_CODE AND USER_ID='"
					 * + Operator.getID() + "'"));
					 *
					 * if (ruleParm.getCount() > 0 && ruleParmM.getCount() > 0)
					 * { String posType = ruleParm.getValue("POS_TYPE", 0);
					 * String posTypeM = ruleParmM.getValue("POS_TYPE", 0); if
					 * (!posType.equals(posTypeM)) { this.messageBox("E0102");
					 * return; } }
					 **/
				} else {
					// 非住院
				}

			}
		}
		// 新建模版
		if (this.getOnlyEditType().equals("NEW")) {
			//可编辑
			this.getWord().setCanEdit(true);
			//编辑状态(非整洁)
			this.getWord().onEditWord();
			setTMenuItem(true);
			this.getWord().getPM().setModifyNodeManager( new MModifyNode() );
			return true; //modify by wanglong 20121205
		}
		// 打开
		if (this.getOnlyEditType().equals("ONLYONE")) {
			TParm emrChildParm = this.getEmrChildParm();
			//
			//假如class_style不等于4的
			//this.messageBox("CLASS_STYLE====="+emrChildParm.getValue("CLASS_STYLE"));
			//
			if(this.getSystemType().equals("ODI") &&!emrChildParm.getValue("CLASS_STYLE").equals("4")){
				//已签名
				if(isSign()){
					this.messageBox("您已签名，不可编辑，请先取消签名！"); 
					this.getWord().onPreviewWord();
					return false;
				}
			}

			//add by huangtt 20161115 封存病历不允许编辑 start
			if(this.systemType.equals("ODI") && emrChildParm.getValue("SEALED_STATUS").equals("3") && !"Y".equals(refFlg)){
				this.messageBox("已封存病历，不可修改");
				this.getWord().onPreviewWord();
				return false;
			}

			//add by huangtt 20161115 封存病历不允许编辑 end


			//
			// 是住院系统的进行三级检诊;
			if (this.getSystemType().equals("ODI") ) {
				//普通病历进行三级检诊判断，保证唯一一人操作
				if(!emrChildParm.getValue("CLASS_STYLE").equals("4")){
					//三级检诊判断    去掉三级医师判断
					/*if(!this.checkDrForEdit()){
						// 3、病历编辑保存权限控制：
						this.messageBox("E0102"); // 您没有编辑权限
						this.getWord().onPreviewWord();
						return false; //modify by wanglong 20121205
					}*/
				}
			}
			//
			if ("3".equals(this.ruleType)) {			
				if ("OIDR".equals(this.writeType)) {
					if ("N".equals(emrChildParm.getValue("OIDR_FLG"))) {
						this.callFunction("UI|save|setEnabled", false);
						this.messageBox("E0102");
						return false; //modify by wanglong 20121205
					}
				}
				if ("NSS".equals(this.writeType)) {
					if ("N".equals(emrChildParm.getValue("NSS_FLG"))) {
						this.callFunction("UI|save|setEnabled", false);
						this.messageBox("E0102");
						return false; //modify by wanglong 20121205
					}
				}
			}

			//修改病例时不需要痕迹(红线)
			/*TParm emrData =  this.getEmrChildParm();
			String commitUser = emrData.getValue("COMMIT_USER");
			//
			this.messageBox("----111commitUser111----"+commitUser);
			//
            if( TiString.isEmpty(commitUser) ){
            	this.getWord().getPM().setModifyNodeManager( new MModifyNode() );
            }*/
			this.getWord().getPM().getModifyNodeManager();

			// 可编辑
			this.getWord().setCanEdit(true);
			// PIC
			this.setContainPic(false);
			// 编辑状态(非整洁)
			this.getWord().onEditWord();
			setTMenuItem(true);

			if ("3".equals(this.ruleType)) {
				this.callFunction("UI|save|setEnabled", true);
			}
			//取所有抓取框控件
			setAllCapture();
		}
		return true; //modify by wanglong 20121205
	}

	/**
	 * 是否是值班医生
	 *
	 * @return boolean
	 */
	public boolean isDutyDrList() {
		boolean falg = false;
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT * FROM ODI_DUTYDRLIST WHERE DR_CODE='"
						+ Operator.getID() + "'"));
		if (parm.getCount() > 0) {
			falg = true;
		}
		return falg;
	}

	/**
	 * 是否是值班医生
	 */
	public boolean isDutyDrList(String user) {
		boolean falg = false;
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT * FROM ODI_DUTYDRLIST WHERE STATION_CODE='"
						+ this.getStationCode() + "' AND DR_CODE='" + user
						+ "'"));
		if (parm.getCount() > 0) {
			falg = true;
		}
		return falg;
	}

	/**
	 * 取消编辑
	 */
	public void onCancelEdit() {
		if (!this.getWord().canEdit()) {
			// 已经是取消编辑状态
			this.messageBox("E0104");
			return;
		}
		if (this.checkSave()) {
			// 不可编辑
			this.getWord().setCanEdit(false);
			setTMenuItem(false);
		}
	}

	/**
	 * 是否是三级检诊医师
	 */
	public boolean isCheckUserDr() {
		// 判断是否有转科
		if (isAdmChg(this.getCaseNo())) {
			// 当前操作者医师科室
			this.setVsDrCode(this.getDr(this.getCaseNo(), this.getDeptCode(),
					"VS_DR_CODE"));
			// 主治医师
			this.setAttendDrCode(this.getDr(this.getCaseNo(), this
					.getDeptCode(), "ATTEND_DR_CODE"));
			// 科主任
			this.setDirectorDrCode(this.getDr(this.getCaseNo(), this
					.getDeptCode(), "DIRECTOR_DR_CODE"));
			if (Operator.getID().equals(this.getVsDrCode())) {
				return true;
			}
			if (Operator.getID().equals(this.getAttendDrCode())) {
				return true;
			}
			if (Operator.getID().equals(this.getDirectorDrCode())) {
				return true;
			}

			// 非转科从ADM_INP中判断
		} else {
			String sql = " SELECT VS_DR_CODE,ATTEND_DR_CODE,DIRECTOR_DR_CODE "
					+ " FROM ADM_INP " + " WHERE CASE_NO = '"
					+ this.getCaseNo() + "'";

			TParm parm = new TParm(this.getDBTool().select(sql));
			// 经治医师
			this.setVsDrCode(parm.getValue("VS_DR_CODE", 0));
			// 主治医师
			this.setAttendDrCode(parm.getValue("ATTEND_DR_CODE", 0));
			// 科主任
			this.setDirectorDrCode(parm.getValue("DIRECTOR_DR_CODE", 0));
			if (Operator.getID().equals(parm.getValue("VS_DR_CODE", 0))) {
				return true;
			}
			if (Operator.getID().equals(parm.getValue("ATTEND_DR_CODE", 0))) {
				return true;
			}
			if (Operator.getID().equals(parm.getValue("DIRECTOR_DR_CODE", 0))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 加载树
	 */
	public void loadTree() {
		treeRoot = this.getTTree(TREE_NAME).getRoot();
		this.setValue("MR_NO", this.getMrNo());
		if ("en".equals(this.getLanguage()) && patEnName != null
				&& patEnName.length() > 0) {
			this.setValue("PAT_NAME", this.patEnName);
		} else {
			this.setValue("PAT_NAME", this.getPatName());
		}
		boolean queryFlg = false;
		// 住院
		if (this.getSystemType().equals("ODI")) {
			this.setValue("IPD_NO", this.getIpdNo());
			// 拿到树根
			treeRoot.setText("住院");
			treeRoot.setEnText("Hospital");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			isCheckUserDr();
		}
		// 门诊
		if (this.getSystemType().equals("ODO")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			// 拿到树根
			treeRoot.setText("门诊");
			treeRoot.setEnText("Outpatient");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("O")) {
				queryFlg = true;
			}
		}
		// 急诊
		if (this.getSystemType().equals("EMG")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			// 拿到树根
			treeRoot.setText("急诊");
			treeRoot.setEnText("Emergency");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("E")) {
				queryFlg = true;
			}
		}
		// 健康检查
		if (this.getSystemType().equals("HRM")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			// 拿到树根
			treeRoot.setText("健康检查");
			treeRoot.setEnText("Health Check");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("H")) {
				queryFlg = true;
			}
		}
		// 门诊护士站
		if (this.getSystemType().equals("ONW")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			// 拿到树根
			treeRoot.setText("门诊护士站");
			treeRoot.setEnText("Out-patient nurse station");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("O")) {
				queryFlg = true;
			}
		}
		// 住院护士站
		if (this.getSystemType().equals("INW")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			// 拿到树根
			treeRoot.setText("住院护士站");
			treeRoot.setEnText("Hospital nurse station");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("I")) {
				queryFlg = true;
			}
		}
		// 感染控制
		if (this.getSystemType().equals("INF")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			// 拿到树根
			treeRoot.setText("感染控制");
			treeRoot.setEnText("Infection Control");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("F")) {
				queryFlg = true;
			}
		}
		// 手术
		if (this.getSystemType().equals("OPE")) {//add by wanglong 20121220
			// 拿到树根
			treeRoot.setText("手术记录");
			treeRoot.setEnText("Op Record");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("F")) {
				queryFlg = true;
			}
		}
		// 单病种
		if (this.getSystemType().equals("SD")) {//add by wanglong 20121120
			// 拿到树根
			treeRoot.setText("单病种");
			treeRoot.setEnText("Single Disease");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("F")) {
				queryFlg = true;
			}
		}
		// 拿到节点数据
		TParm parm = this.getRootNodeData();
		if (parm.getInt("ACTION", "COUNT") == 0) {
			// 没有设置EMR模板！请检查数据库设置
			this.messageBox("E0105");
			return;
		}
		int rowCount = parm.getInt("ACTION", "COUNT");
		for (int i = 0; i < rowCount; i++) {
			TParm mainParm = parm.getRow(i);
			// 加节点 EMR_CLASS表中的STYLE字段
			TTreeNode node = new TTreeNode();
			node.setID(mainParm.getValue("CLASS_CODE"));
			node.setText(mainParm.getValue("CLASS_DESC"));
			node.setEnText(mainParm.getValue("ENG_DESC"));
			// 设置菜单显示样式
			node.setType("" + mainParm.getInt("CLASS_STYLE"));
			node.setGroup(mainParm.getValue("CLASS_CODE"));
			node.setValue(mainParm.getValue("SYS_PROGRAM"));
			node.setData(mainParm);

			// 加入树根
			treeRoot.add(node);

			// $$=============start add by lx 病历模版分类树修改; ================$$//
			TParm allChildsParm = new TParm();
			this.getAllChildsByParent(mainParm.getValue("CLASS_CODE"),
					allChildsParm);
			int childRowCount = allChildsParm.getCount();
			for (int j = 0; j < childRowCount; j++) {
				//TParm childDirParm = allChildsParm.getRow(j);

				TTreeNode childrenNode = new TTreeNode(allChildsParm.getValue(
						"CLASS_DESC", j), allChildsParm.getValue("CLASS_STYLE",
								j));
				childrenNode.setID(allChildsParm.getValue("CLASS_CODE", j));
				childrenNode.setGroup(allChildsParm.getValue("CLASS_CODE", j));
				// mainParm.getValue("SYS_PROGRAM")?????暂时没有
				treeRoot.findNodeForID(
						allChildsParm.getValue("PARENT_CLASS_CODE", j)).add(
								childrenNode);

				// 是叶节点的加入子文件
				if (((String) allChildsParm.getValue("LEAF_FLG", j))
						.equalsIgnoreCase("Y")) {
					//$$=====Add by lx 2012/10/10 start=======$$//
					TParm childParm=new TParm();
					//判断是否是临床知识库，
					//假如是则,加入对应的对应的知识库文件
					if(isKnowLedgeStore(allChildsParm.getValue("CLASS_CODE", j))){

						childParm=getChildNodeDataByKnw(allChildsParm.getValue("CLASS_CODE", j));
						//$$=====Add by lx 2012/10/10 end=======$$//
					}else{
						// 查询子文件
						childParm = this.getRootChildNodeData(
								allChildsParm.getValue("CLASS_CODE", j),
								queryFlg);
					}
					int rowCldCount = childParm.getInt("ACTION", "COUNT");
					for (int z = 0; z < rowCldCount; z++) {
						TParm chlidParmTemp = childParm.getRow(z);
						TTreeNode nodeChlid = new TTreeNode();
						nodeChlid
						.setID(chlidParmTemp.getValue("FILE_NAME"));
						nodeChlid.setText(chlidParmTemp
								.getValue("DESIGN_NAME"));
						nodeChlid.setType("4");
						nodeChlid.setGroup(chlidParmTemp
								.getValue("CLASS_CODE"));
						nodeChlid.setValue(chlidParmTemp
								.getValue("SUBCLASS_CODE"));
						nodeChlid.setData(chlidParmTemp);
						childrenNode.add(nodeChlid);
					}
				}

			}
			// $$=============end add by lx 病历模版分类树修改; ================$$//
		}
		this.getTTree(TREE_NAME).update();
	}

	/**
	 * 拿到根节点数据
	 *
	 * @return TParm
	 */
	public TParm getRootNodeData() {
		TParm result = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT B.CLASS_DESC,B.ENG_DESC,B.CLASS_STYLE,A.CLASS_CODE,A.SYS_PROGRAM,A.SEQ "
								+ " FROM EMR_TREE A,EMR_CLASS B"
								+ " WHERE A.SYSTEM_CODE = '"+ this.getSystemType()
								+ "' AND A.CLASS_CODE=B.CLASS_CODE ORDER BY B.SEQ,A.CLASS_CODE"));
		return result;
	}

	/**
	 * 拿到根节点数据
	 *
	 * @return TParm
	 */
	public TParm getRootChildNodeData(String classCode, boolean queryFlg) {
		TParm result = new TParm();
		String myTemp =
				// 公共模板
				" AND ((B.DEPT_CODE IS NULL AND B.USER_ID IS NULL) "
				// 科室模板
				+ " OR (B.DEPT_CODE = '" + Operator.getDept()
				+ "' AND B.USER_ID IS NULL) "
				// 个人模板
				+ " OR B.USER_ID = '" + Operator.getID() + "') ";
		String sql = "SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,"
				+ " A.CREATOR_USER,A.CREATOR_DATE,A.CURRENT_USER,A.CANPRINT_FLG,A.MODIFY_FLG,"
				+ " A.CHK_USER1,A.CHK_DATE1,A.CHK_USER2,A.CHK_DATE2,A.CHK_USER3,A.CHK_DATE3,"
				+ " A.COMMIT_USER,A.COMMIT_DATE,A.IN_EXAMINE_USER,A.IN_EXAMINE_DATE,A.DS_EXAMINE_USER,A.DS_EXAMINE_DATE,A.PDF_CREATOR_USER,A.PDF_CREATOR_DATE,"
				+ " B.SUBCLASS_DESC,B.DEPT_CODE,B.RUN_PROGARM,B.SUBTEMPLET_CODE,B.CLASS_STYLE,B.OIDR_FLG,B.NSS_FLG,A.REPORT_FLG ";

		// 转诊查阅病历
		if ("Y".equals(refFlg)) {
			if ("I".equals(this.getAdmType())) {
				sql = sql
						+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='"
						+ this.getCaseNo()
						+ "' AND A.CLASS_CODE='"
						+ classCode
						+ "' AND A.DISPOSAC_FLG<>'Y' AND IPD_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
						+ myTemp
						+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ";
				result = new TParm(this.getDBTool().select("javahisREF", sql));
				return result;
			}
		}

		sql += ",A.SEALED_STATUS ";//add by huangtt 20161114

		if (queryFlg) {
			if ("O".equals(this.getAdmType())) {
				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.MR_NO='"
								+ this.getMrNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND OPD_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
			}
			if ("I".equals(this.getAdmType())) {
				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.MR_NO='"
								+ this.getMrNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND IPD_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
			}
			if ("E".equals(this.getAdmType())) {
				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.MR_NO='"
								+ this.getMrNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND EMG_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
			}
			if ("H".equals(this.getAdmType())) {
				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.MR_NO='"
								+ this.getMrNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND HRM_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
			}
		} else {
			if ("O".equals(this.getAdmType())) {

				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='"
								+ this.getCaseNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND OPD_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));

			}
			if ("I".equals(this.getAdmType())) {

				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='"
								+ this.getCaseNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND IPD_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));

			}
			if ("E".equals(this.getAdmType())) {
				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='"
								+ this.getCaseNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND EMG_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
			}
			if ("H".equals(this.getAdmType())) {
				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='"
								+ this.getCaseNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND HRM_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
			}
		}
		return result;
	}

	/**
	 * 通过知识库叶节点取对应知识库模版
	 * @param classCode
	 * @return
	 */
	public TParm getChildNodeDataByKnw(String classCode){
		TParm result = new TParm();
		String sql = "SELECT   SUBCLASS_CODE, CLASS_CODE, EMT_FILENAME FILE_NAME,";
		sql += "TEMPLET_PATH FILE_PATH, SUBCLASS_DESC DESIGN_NAME ";
		sql += "FROM EMR_TEMPLET ";
		sql += "WHERE CLASS_CODE = '" + classCode + "' ";
		sql += "ORDER BY SEQ";
		result = new TParm(this.getDBTool().select(sql));
		return result;
	}

	/**
	 * 点击树
	 *
	 * @param parm
	 *            Object
	 */
	public void onTreeDoubled(Object parm) {
		//初始化，追加标记
		this.setApplend(false);
		TTreeNode node = (TTreeNode) parm;
		if (null == node) {
			return;
		}		
	
		TParm emrParm = (TParm) node.getData();	
		int type = 3;
		if ("Y".equals(refFlg)) {
			// add by wangb 2015/09/07 新增type4用于读取转诊文件夹文件
			type = 4;
		}
		if (emrParm != null && emrParm.getValue("REPORT_FLG").equals("Y")) {
			System.out.println("55555555555555555555555555555555555555555555555");
			// 打开病历
			if (!this.getWord().onOpen(emrParm.getValue("FILE_PATH"),
					emrParm.getValue("FILE_NAME"), type, true)) {
				return;
			}

			// 设置不可编辑
			this.getWord().setCanEdit(false);
			TParm allParm = new TParm();
			allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
			allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
			//allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
			//住院号 -> 病人姓名
			allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getPatName());
			//
			allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
			allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,
					"onMouseRightPressed");
			this.getWord().setWordParameter(allParm);

		} else {
			//System.out.println("======双击打开======"+emrParm);
			if (!this.checkInputObject(parm)) {
				return;
			}
			// 1.病历查看权限控制：
			if (!checkDrForOpen()) {
				this.messageBox("E0011"); // 权限不足
				return;
			}

			if (!node.getType().equals("4")) {
				return;
			}
			//
			this.getWord().setNodeIndex(-1);
			//=====add by lx 2012/10/10加入是临床知识库 ====start//
			if(this.isKnowLedgeStore(emrParm.getValue("SUBCLASS_CODE"))){
				System.out.println("6666666666666666666666666666666666666666666666666666666");
				//打开知识库模版
				if (!this.getWord().onOpen(
						emrParm.getValue("FILE_PATH"),
						emrParm.getValue("FILE_NAME"), 2, false))
					// 设置不可编辑
					this.getWord().setCanEdit(false);
				this.getTMenuItem("save").setEnabled(false);
				setTMenuItem(false);
				this.getWord().onPreviewWord();
				return;
			}
			//=====add by lx 2012/10/10加入临床知识库 ====End//
			//System.out.println("777777777777777777");
			//this.messageBox("-stuts777777777777777777--====="+this.getWord().getNodeIndex());

			// 打开病历
			if (!this.getWord().onOpen(emrParm.getValue("FILE_PATH"),
					emrParm.getValue("FILE_NAME"), type, true)) {
				return;
			}

			//test
			/*this.getWord().setNodeIndex(-1);
			saveWord();*/			
			//this.messageBox("-stuts8888888888888888888--====="+this.getWord().getNodeIndex());			
			//this.messageBox("-stuts8888888888888888888--====="+this.getWord().getNodeIndex());
			//
			setSingleDiseBasicData();//add by wanglong 20121115  设置单病种基本信息
			setOPEData();//add by wanglong 20130514 设置手术基本信息
			// 设置不可编辑
			this.getWord().setCanEdit(false);

			TParm allParm = new TParm();
			allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
			allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
			//allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
			//
			//住院号 -> 病人姓名
			allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getPatName());
			//
			allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
			allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,
					"onMouseRightPressed");
			this.getWord().setWordParameter(allParm);

			if ("Y".equals(refFlg)) {
				return;
			}

			// 设置宏
			setMicroField(false);
			TParm sexP = new TParm(this.getDBTool().select(
					"SELECT SEX_CODE FROM SYS_PATINFO WHERE MR_NO='"
							+ this.getMrNo() + "'"));

			if (sexP.getInt("SEX_CODE", 0) == 9) {
				this.getWord().setSexControl(0);
			} else {
				this.getWord().setSexControl(sexP.getInt("SEX_CODE", 0));
			}
			// 设置编辑状态
			this.setOnlyEditType("ONLYONE");
			// 设置当前编辑数据
			this.setEmrChildParm(emrParm);
			setTMenuItem(false);
			//$$===========add by lx 2012-06-18 加入病历已经提交不可修改start===============$$//
			if(getSubmitFLG()){
				this.getWord().onPreviewWord();
				LogOpt(emrParm);
				setPrintAndMfyFlg(emrParm);
				//this.messageBox("已提交病历，不可修改");
			}

			//add by huangtt 20161115 封存病历不允许编辑 start
			else if(this.systemType.equals("ODI") && emrParm.getValue("SEALED_STATUS").equals("3") && !"Y".equals(refFlg)){
				this.messageBox("已封存病历，不可修改");
				this.getWord().onPreviewWord();
				LogOpt(emrParm);
				setPrintAndMfyFlg(emrParm);
			}

			//add by huangtt 20161115 封存病历不允许编辑 end

			//$$===========add by lx 2012-06-18加入病历已经提交不可修改 end===============$$//
			// 申请编辑
			else if (ruleType.equals("2") || ruleType.equals("3")) {
				onEdit();
				setCanEdit(emrParm);
				LogOpt(emrParm);
				setPrintAndMfyFlg(emrParm);
			} else {
				this.getWord().onPreviewWord();
				LogOpt(emrParm);
				setPrintAndMfyFlg(emrParm);
			}
			
			// add by wangqing 20170817 start			
			// 规培医生权限
			if (this.ruleType.equals("3") && "T".equals(this.writeType)) {
				String tSql = " SELECT B.INTERNSHIP_FLG "
						+ "FROM EMR_FILE_INDEX A, EMR_TEMPLET B "
						+ "WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE(+) AND A.CASE_NO = '"+caseNo+"' AND A.SUBCLASS_CODE = '"+emrParm.getValue("SUBCLASS_CODE")+"' ";
				TParm tResult = new TParm(TJDODBTool.getInstance().select(tSql));
				if(tResult.getCount()<=0 || tResult.getErrCode()<0){
					return;
				}
				if(tResult.getValue("INTERNSHIP_FLG", 0) != null && tResult.getValue("INTERNSHIP_FLG", 0).equals("N")){// 设置不可编辑
					this.getWord().setCanEdit(false);
					this.messageBox("规培医生不可编辑！！！");
				}
			}
			// add by wangqing 20170817 end		

		}
	}

	/**
	 * 日志记录
	 */
	private void LogOpt(TParm emrParm) {
		// 三级检诊医师及值班医师：电子病历操作日志/EMR_OPTLOG/打开
		if (this.isCheckUserDr() || this.isDutyDrList()) {
			TParm result = OptLogTool.getInstance().writeOptLog(
					this.getParameter(), "O", emrParm);
		}
		// 非三级检诊医师及值班医师：电子病历操作日志/EMR_OPTLOG/调阅
		else {
			TParm result = OptLogTool.getInstance().writeOptLog(
					this.getParameter(), "R", emrParm);
		}

	}

	/**
	 * 设置允许打印标识和允许修改标识
	 *
	 * @param emrParm
	 *            TParm
	 */
	private void setPrintAndMfyFlg(TParm emrParm) {
		// 设置允许打印标识和允许修改标识
		String caseNo = ((TParm) this.getParameter()).getValue("CASE_NO");
		String fileSeq = emrParm.getValue("FILE_SEQ");
		if (!this.checkDrForDel()) {
			setCanPrintFlgAndModifyFlg("", "", false);
		} else {
			setCanPrintFlgAndModifyFlg(caseNo, fileSeq, true);
		}

	}

	private void setCanEdit(TParm emrParm) {
		int fileSeq = emrParm.getInt("FILE_SEQ");
		String subTempletCode = emrParm.getValue("SUBTEMPLET_CODE");
		String subClassCode = emrParm.getValue("SUBCLASS_CODE");
		String caseNo = emrParm.getValue("CASE_NO");
		if (subTempletCode.length() != 0) {
			TParm result = new TParm(getDBTool().select(
					" SELECT MAX(FILE_SEQ) FILE_SEQ" + " FROM EMR_FILE_INDEX "
							+ " WHERE CASE_NO='" + caseNo + "' "
							+ " AND   SUBCLASS_CODE='" + subClassCode + "'"));
			if (fileSeq != result.getInt("FILE_SEQ", 0)) {
				this.getWord().setCanEdit(false);
				setTMenuItem(false);

			}
		} else {
			TParm result = new TParm(getDBTool().select(
					" SELECT B.FILE_SEQ"
							+ " FROM EMR_TEMPLET A,EMR_FILE_INDEX B "
							+ " WHERE A.SUBTEMPLET_CODE='" + subClassCode + "'"
							+ " AND   A.SUBCLASS_CODE = B.SUBCLASS_CODE"
							+ " AND   B.CASE_NO = '" + caseNo + "'"));
			if (result.getCount() > 0) {
				this.getWord().setCanEdit(false);
				setTMenuItem(false);

			}
		}
	}

	/**
	 * 得到树
	 *
	 * @param tag
	 *            String
	 * @return TTree
	 */
	public TTree getTTree(String tag) {
		return (TTree) this.getComponent(tag);
	}

	/**
	 * 新建病历(多个)
	 */
	public void onCreatMenu() {
		// 部分读写权限管控
		openChildDialog();
		this.setOnlyEditType("NEW");
		this.getWord().fixedTryReset(this.getMrNo(), this.getCaseNo());
	}

	/**
	 * 打开病历(唯一)
	 */
	public void onOpenMenu() {
		// 部分读写权限管控
		openChildDialog();
		this.setOnlyEditType("NEW");
	}

	/**
	 * 新增病历(可以追加)
	 */
	public void onAddMenu() {
		// 部分读写权限管控
		openChildDialog();
		this.getWord().fixedTryReset(this.getMrNo(), this.getCaseNo());
	}

	/**
	 * 
	 * 打开子窗口
	 * 
	 */
	public void openChildDialog() {
		this.setApplend(false);
		// 2、病历新建保存权限控制：
		if (!this.checkDrForNew()) {
			this.messageBox("E0011"); // 权限不足
			return;
		}

		// 模板类型
		String emrClass = this.getTTree(TREE_NAME).getSelectNode().getGroup();
		String nodeName = this.getTTree(TREE_NAME).getSelectNode().getText();
		String emrType = this.getTTree(TREE_NAME).getSelectNode().getType();
		String programName = this.getTTree(TREE_NAME).getSelectNode()
				.getValue();

		// 如果是调用HIS程序则这样处理
		if (programName.length() != 0) {
			TParm hisParm = new TParm();
			hisParm.setData("CASE_NO", this.getCaseNo());
			this.openDialog(programName, hisParm);
			return;
		}
		TParm parm = new TParm();
		String whereOtherStr = "";
		if (this.ruleType.equals("3") && "OIDR".equals(this.writeType)) {
			whereOtherStr += " AND OIDR_FLG='Y'";
		}
		if (this.ruleType.equals("3") && "NSS".equals(this.writeType)) {
			whereOtherStr += " AND NSS_FLG='Y'";
		}
		
		// add by wangqing 20170804  规培适用
		if (this.ruleType.equals("3") && "T".equals(this.writeType)) {
			whereOtherStr += " AND INTERNSHIP_FLG='Y'";
		}
			
		if (this.getSystemType() != null && this.getSystemType().length() != 0) {
			whereOtherStr += " AND (SYSTEM_CODE='" + this.getSystemType()
			+ "' OR SYSTEM_CODE IS NULL)";
		}
		String myTemp =
				// 公共模板
				" AND ((DEPT_CODE IS NULL AND USER_ID IS NULL) "
				// 科室模板
				+ " OR (DEPT_CODE = '" + Operator.getDept()
				+ "' AND USER_ID IS NULL) "
				// 个人模板
				+ " OR USER_ID = '" + Operator.getID() + "') ";
		
		// 住院
		if (this.getAdmType().equals("I")) {
			parm = new TParm(
					this
					.getDBTool()
					.select(
							"SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE" +
									" FROM EMR_TEMPLET" +
									" WHERE CLASS_CODE='"
									+ emrClass
									+ "' AND IPD_FLG='Y' "
									+ whereOtherStr
									+ myTemp
									+ " ORDER BY SEQ"));

			parm.setData("SYSTEM_CODE", this.getSystemType());
			parm.setData("NODE_NAME", nodeName);
			parm.setData("TYPE", emrType);
			parm.setData("DEPT_CODE", this.getDeptCode());
		}
		// 门诊
		if (this.getAdmType().equals("O")) {
			parm = new TParm(
					this
					.getDBTool()
					.select(
							"SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE" +
									" FROM EMR_TEMPLET" +
									" WHERE CLASS_CODE='"
									+ emrClass
									+ "' AND OPD_FLG='Y' "
									+ whereOtherStr
									+ myTemp
									+ " ORDER BY SEQ"));
			parm.setData("SYSTEM_CODE", this.getSystemType());
			parm.setData("NODE_NAME", nodeName);
			parm.setData("TYPE", emrType);
			parm.setData("DEPT_CODE", this.getDeptCode());
		}
		// 急诊
		if (this.getAdmType().equals("E")) {
			parm = new TParm(
					this
					.getDBTool()
					.select(
							"SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE" +
									" FROM EMR_TEMPLET" +
									" WHERE CLASS_CODE='"
									+ emrClass
									+ "' AND EMG_FLG='Y' "
									+ whereOtherStr
									+ myTemp
									+ " ORDER BY SEQ"));
			parm.setData("SYSTEM_CODE", this.getSystemType());
			parm.setData("NODE_NAME", nodeName);
			parm.setData("TYPE", emrType);
			parm.setData("DEPT_CODE", this.getDeptCode());
		}
		if (this.getAdmType().equals("H")) {
			parm = new TParm(
					this
					.getDBTool()
					.select(
							"SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE" +
									" FROM EMR_TEMPLET" +
									" WHERE CLASS_CODE='"
									+ emrClass
									+ "' AND HRM_FLG='Y' "
									+ whereOtherStr
									+ myTemp
									+ " ORDER BY SEQ"));
			parm.setData("SYSTEM_CODE", this.getSystemType());
			parm.setData("NODE_NAME", nodeName);
			parm.setData("TYPE", emrType);
			parm.setData("DEPT_CODE", this.getDeptCode());
		}
		Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRChildUI.x", parm);
		if (obj == null || !(obj instanceof TParm)) {
			return;
		}

		TParm action = (TParm) obj;
		// 预启动程序名
		String runProgarmName = action.getValue("RUN_PROGARM");

		TParm runParm = new TParm();
		// 判断类型是否可以打开
		String styleClass = action.getValue("CLASS_STYLE");
		// 只能新建
		if ("1".equals(styleClass)) {
			if (runProgarmName.length() != 0) {
				TParm ermParm = new TParm();
				ermParm.setData("TYPE", "1");
				ermParm.setData("FILE_NAME", action.getValue("SUBCLASS_DESC"));
				ermParm.setData("ADM_DATE", this.getAdmDate());
				ermParm.setData("CASE_NO", this.getCaseNo());
				// 出院时间
				TParm tem = this.getAdmInpDSData();
				if (tem != null) {
					ermParm.setData("DS_DATE", tem.getTimestamp("DS_DATE", 0));
				} else {
					ermParm.setData("DS_DATE", "");
				}
				Object objParm = this.openDialog(runProgarmName, ermParm);
				if (objParm != null) {
					runParm = (TParm) objParm;
				} else {
					return;
				}
			}
		}
		// 唯一只能建立一次
		if ("2".equals(styleClass)) {
			String subclassCode = action.getValue("SUBCLASS_CODE");
			TParm fileParm = new TParm(
					this
					.getDBTool()
					.select(
							"SELECT CASE_NO,FILE_SEQ,MR_NO,IPD_NO,FILE_PATH,FILE_NAME,DESIGN_NAME,CLASS_CODE,SUBCLASS_CODE,DISPOSAC_FLG" +
									" FROM EMR_FILE_INDEX" +
									" WHERE CASE_NO='"
									+ this.getCaseNo()
									+ "' AND SUBCLASS_CODE='"
									+ subclassCode + "'"));
			if (fileParm.getCount() > 0) {
				// 此文件已经存在不可以在新建
				this.messageBox("E0106");
				return;
			}
			if (runProgarmName.length() != 0) {
				TParm ermParm = new TParm();
				ermParm.setData("TYPE", "1");
				ermParm.setData("FILE_NAME", action.getValue("SUBCLASS_DESC"));
				ermParm.setData("ADM_DATE", this.getAdmDate());
				ermParm.setData("CASE_NO", this.getCaseNo());

				// 出院时间
				TParm tem = this.getAdmInpDSData();
				if (tem != null) {
					ermParm.setData("DS_DATE", tem.getTimestamp("DS_DATE", 0));
				} else {
					ermParm.setData("DS_DATE", "");
				}
				Object objParm = this.openDialog(runProgarmName, ermParm);
				if (objParm != null) {
					runParm = (TParm) objParm;
				} else {
					return;
				}
			}
		}
		// 文件内容追加锁定前内容dd
		if ("3".equals(styleClass)) {
			// 拿到附属模版文件名
			String subtempletCode = action.getValue("SUBTEMPLET_CODE");
			String emtFileName = new TParm(this.getDBTool().select(
					"SELECT EMT_FILENAME FROM EMR_TEMPLET WHERE SUBCLASS_CODE ='"
							+ subtempletCode + "'"))
					.getValue("EMT_FILENAME", 0);

			// 当前模版
			String subclassCode = action.getValue("SUBCLASS_CODE");

			if (subtempletCode.length() != 0) {
				// 查看附属文件病例是否存在
				//存在当前模版;
				if (subclassCode!=null&&!subclassCode.equals("")) {
					// 打开文件
					if (runProgarmName.length() != 0) {
						TParm ermParm = new TParm();
						ermParm.setData("TYPE", "2");
						ermParm.setData("FILE_NAME", action
								.getValue("SUBCLASS_DESC"));
						ermParm.setData("ADM_DATE", this.getAdmDate());
						ermParm.setData("CASE_NO", this.getCaseNo());
						ermParm.setData("SUBTEMPLET_FILE", emtFileName);

						// 出院时间
						TParm tem = this.getAdmInpDSData();
						if (tem != null) {
							ermParm.setData("DS_DATE", tem.getTimestamp(
									"DS_DATE", 0));
						} else {
							ermParm.setData("DS_DATE", "");
						}

						// 弹出外部程序;
						Object objParm = this.openDialog(runProgarmName,
								ermParm);

						if (objParm != null) {
							runParm = (TParm) objParm;
							// 设置主文件名
							this.setSubFileName(runParm
									.getValue("FILE_NAME_MAIN"));
							//
							// 取设置的 病程记录时间
							this.setRecordTime(runParm.getValue("RECORD_TIME"));
							//
							if(isDebug){
								System.out.println("===FILE_NAME_MAIN===="+runParm
										.getValue("FILE_NAME_MAIN"));
								System.out.println("=====RECORD_TIME====="
										+ this.getRecordTime());
								System.out.println("=====RECORD_TIME====="
										+ Timestamp.valueOf(this.getRecordTime()));
							}
							//
							//33


							//包括主文件，当前时间
							//1.打开模版文件;
							String templetPath = action.getValue("TEMPLET_PATH");

							String templetName = action.getValue("EMT_FILENAME");
							try{
								openTempletNoEdit(templetPath, templetName, runParm);
							}catch (Exception e) {

							}
							this.setEmrChildParm(action);
							// 可能出问题
							this.setEmrChildParm(this.getFileServerEmrName());
							// 设置允许打印和允许修改标识（隐藏）
							setCanPrintFlgAndModifyFlg("", "", false);

							//
							//设置抓取框名称
							//ECapture dayLogCapLeft=this.getWord().findCapture(DAYLOG_CAPTURE_NAME);

							//
							String time=getLogName(this.getRecordTime());
							//
							//System.out.println("===111111222222 time==="+time);							
							//
							//加入记录时间
							/*EFixed recordTime = this.getWord().findFixed(
							"RECORD_TIME");*/
							//
							//recordTime.setName(time);

							//抓取框，记录时间
							//dayLogCapLeft.setName(time);
							//this.getWord().update();						
							//System.out.println("=======getCaptureType======="+dayLogCapLeft.getCaptureType());

							//dayLogCap.getEndCapture().setName(time);
							/*ECapture dayLogCapRight=this.getWord().findCapture("DayLog");
							dayLogCapRight.setName(time);	*/						

							//加入记录时间
							EFixed fixedDate = this.getWord().findFixed(
									"DATETIME");

							if (fixedDate != null) {
								fixedDate.setText(runParm.getValue("EMRADD_DATE"));
							}

							//是否是追加 FLG
							this.setApplend(true);
							this.getWord().update();
							//2.生成文件，保存；
							//3.打开依赖文件，this.word追加新的病程记录(路径，文件);
						} else {
							return;
						}
					}
					return;
				} else {
					// 主病历未填写无法创建
					this.messageBox("E0108！");
					return;
				}
			} else {
				// 此模版类型没有设置附属模版请设置后填写！
				this.messageBox("E0109");
				return;
			}
		}
		//
		//是文件内容引用的文件（日常病程文件）
		/*		if ("4".equals(styleClass)) {
			//1.屏蔽掉  主界面   菜单
			this.getTMenuItem("save").setEnabled(false);
			this.getTMenuItem("sign").setEnabled(false);
			this.getTMenuItem("signCancel").setEnabled(false);
			//
			//2.新建日常病程    按扭正常显示
			TButton  btnNewDayLog=(TButton) this.getComponent("NEW_DAYLOG");
			btnNewDayLog.setEnabled(true);
			//
			if (runProgarmName.length() != 0) {
				TParm ermParm = new TParm();
				ermParm.setData("TYPE", "1");
				ermParm.setData("FILE_NAME", action.getValue("SUBCLASS_DESC"));
				ermParm.setData("ADM_DATE", this.getAdmDate());
				ermParm.setData("CASE_NO", this.getCaseNo());
				// 出院时间
				TParm tem = this.getAdmInpDSData();
				if (tem != null) {
					ermParm.setData("DS_DATE", tem.getTimestamp("DS_DATE", 0));
				} else {
					ermParm.setData("DS_DATE", "");
				}
				Object objParm = this.openDialog(runProgarmName, ermParm);
				if (objParm != null) {
					runParm = (TParm) objParm;
				} else {
					return;
				}
			}						
		}*/
		//
		String templetPath = action.getValue("TEMPLET_PATH");
		String templetName = action.getValue("EMT_FILENAME");

		try{
			openTempletNoEdit(templetPath, templetName, runParm);
		}catch (Exception e) {
			e.printStackTrace();
		}
		this.setEmrChildParm(action);
		// 可能出问题
		//this.setEmrChildParm(this.getFileServerEmrName());
		// 设置允许打印和允许修改标识（隐藏）
		setCanPrintFlgAndModifyFlg("", "", false);
		
		// CCPC用药指导记录
		if (StringUtils.equals(TConfig.getSystemValue("CCPC_MG_SUBCLASSCODE"),
				action.getValue("SUBCLASS_CODE"))) {
			// 自动抓取CCPC用药指导数据
			this.getCcpcMedicationGuideData();
		}
	}



	/**
	 * 打开病历
	 *
	 * @param parm
	 *            Object
	 */
	public void onOpenEmrFile(TParm parm) {
		if (parm == null) {
			return;
		}
		// 三级检诊  (不太一样了，应该有变化)
		if ("ODI".equals(this.getSystemType())) {
			// 是否有查看的权限
			if (!isCheckUserDr() && !this.isDutyDrList()
					&& !"2".equals(this.ruleType) && !"3".equals(this.ruleType)) {
				// 权限不足
				this.messageBox("E0011");
				return;
			}
		}
		// 打开病历
		if (!this.getWord().onOpen(parm.getValue("FILE_PATH"),
				parm.getValue("FILE_NAME"), 3, false)) {
			return;
		}
		// 设置不可编辑
		this.getWord().setCanEdit(false);
		TParm allParm = new TParm();
		allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
		allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		//allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		//住院号 -> 病人姓名
		allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getPatName());
		//
		allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
		allParm.addListener("onMouseRightPressed", this, "onMouseRightPressed");
		this.getWord().setWordParameter(allParm);
		// 设置宏
		if (!parm.getBoolean("FLG")) {
			setMicroField(true);
		}
		// 设置编辑状态
		this.setOnlyEditType("NEW");
		// 设置当前编辑数据
		this.setEmrChildParm(parm);
		setTMenuItem(false);

		// 申请编辑
		if (ruleType.equals("2") || ruleType.equals("3")) {
			onEdit();
		}
	}

	/**
	 * 判断是否要创建文件
	 *
	 * @param fileParm
	 *            TParm
	 * @param subclassCode
	 *            String
	 * @return boolean
	 */
	public boolean isEmrCreatFile(TParm fileParm, String subclassCode) {
		boolean falg = true;
		int rowCount = fileParm.getCount();
		for (int i = 0; i < rowCount; i++) {
			TParm temp = fileParm.getRow(i);
			if (subclassCode.equals(temp.getValue("SUBCLASS_CODE"))) {
				falg = false;
				break;
			}
		}
		return falg;
	}

	/**
	 * 拿到出院时间
	 *
	 * @return TParm
	 */
	public TParm getAdmInpDSData() {
		TParm admParm = new TParm(this.getDBTool().select(
				"SELECT DS_DATE FROM ADM_INP WHERE CASE_NO='"
						+ this.getCaseNo() + "'"));
		if (admParm.getCount() < 0) {
			return null;
		}
		return admParm;
	}

	/**
	 * 删除病例
	 */
	public void onDelFile() {
		// 加入询问删除框
		if (this.messageBox("询问", "是否删除？", 2) == 0) {
			TTreeNode node = this.getTTree(TREE_NAME).getSelectionNode();
			TParm fileData = (TParm) node.getData();

			// 6、病历删除权限控制：
			if (!this.checkDrForDel()) {
				this.messageBox("E0107"); // 不可以删除
				return;
			}
			// 忽略删除文件的错误
			try {
				// 删除文件
				delFileTempletFile(fileData.getValue("FILE_PATH"), fileData
						.getValue("FILE_NAME"));
			} catch (Exception e) {

			}
			// 删除数据库数据
			Map del = this.getDBTool().update(
					"DELETE EMR_FILE_INDEX WHERE CASE_NO='"
							+ fileData.getValue("CASE_NO") + "' AND FILE_SEQ='"
							+ fileData.getValue("FILE_SEQ") + "'");
			TParm delParm = new TParm(del);
			if (delParm.getErrCode() < 0) {
				// 删除失败
				this.messageBox("E0003");
				this.setOnlyEditType("NEW");
				return;
			}
			// 删除成功
			this.messageBox("P0003");
			this.getWord().onNewFile();
			this.setOnlyEditType("NEW");
			this.getWord().setCanEdit(false);
			// 非三级检诊医师及值班医师：电子病历操作日志/EMR_OPTLOG/删除
			TParm result = OptLogTool.getInstance().writeOptLog(
					this.getParameter(), "D", fileData);
			// 加载树
			loadTree();
		} else {
			return;
		}
	}

	/**
	 * 片语
	 */
	public void onInsertPY() {
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("ROLE", "1");
		inParm.setData("DR_CODE", Operator.getID());
		inParm.setData("DEPT_CODE", this.getDeptCode());
		inParm.addListener("onReturnContent", this, "onReturnContent");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRComPhraseQuote.x", inParm, true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);
	}

	/**
	 * 插入模版片语
	 */
	public void onInsertTemplatePY() {
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("DEPT_CODE", this.getDeptCode());
		inParm.setData("TWORD", this.getWord());
		inParm.addListener("onReturnTemplateContent", this,
				"onReturnTemplateContent");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRTemplateComPhraseQuote.x", inParm,
				true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);

	}

	public void onReturnTemplateContent(TWord templateWord) {
		this.word.onPaste();
	}

	/**
	 * 片语记录
	 *
	 * @param value
	 *            String
	 */
	public void onReturnContent(String value) {
		if (!this.getWord().pasteString(value)) {
			// 执行失败
			this.messageBox("E0005");
		}
	}

	/**
	 * 临床数据
	 */
	public void onInsertLCSJ() {
		if (!word.canEdit()) {
			messageBox("先选择病例模版!");
			return;
		} 
		TParm inParm = new TParm();
		inParm.setData("ADM_TYPE", this.getAdmType());//wanglong 20140908
		inParm.setData("CASE_NO", this.getCaseNo());
		inParm.addListener("onReturnContent", this, "onReturnContent");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRMEDDataUI.x", inParm, true);
		/*TPanel wordPanel = ((TPanel) this.getComponent("PANEL"));
        window.setX(wordPanel.getX() + 10);
        window.setY(130);*/
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);		
		//
		AnimationWindowUtils.show(window);
		AWTUtilities.setWindowOpacity(window, 0.9f);
		window.setVisible(true);
		//
	}

	/**
	 * 删除病历文件
	 *
	 * @param templetPath
	 *            String
	 * @param templetName
	 *            String
	 * @return boolean
	 */
	public boolean delFileTempletFile(String templetPath, String templetName) {
		// 目录表第一个根目录FILESERVER
		String rootName = TIOM_FileServer.getRoot();
		// 模板路径服务器
		String templetPathSer = TIOM_FileServer.getPath("EmrData");
		// 拿到Socket通讯工具
		TSocket socket = TIOM_FileServer.getSocket();

		// 删除文件
		boolean isDelFile = TIOM_FileServer.deleteFile(socket, rootName
				+ templetPathSer + templetPath + "\\" + templetName + ".jhw");

		// 删除图片文件夹中的上传图片;
		String dir = rootName + templetPathSer + templetPath + "\\"
				+ templetName + "\\";
		String s[] = TIOM_FileServer.listFile(socket, dir);
		if (s != null) {
			for (int i = 0; i < s.length; i++) {
				String path = dir + "\\" + s[i];
				TIOM_FileServer.deleteFile(socket, path);
			}
		}
		if (isDelFile) {
			return true;
		}

		return false;
	}

	/**
	 * 打开模版
	 *
	 * @param templetPath
	 *            String
	 * @param templetName
	 *            String
	 */
	public void openTempletNoEdit(String templetPath, String templetName,
			TParm parm) {
		// 新建清除修改标志;
		this.getWord().onOpen(templetPath, templetName, 2, false);
		TParm allParm = new TParm();
		allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
		allParm.setData("FILE_TITLE_EN_TEXT", "TEXT", this.hospEngAreaName);
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		//allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		//住院号 -> 病人姓名
		allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getPatName());
		//
		allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
		// add by wangb 2015/12/17 部分手术同意书中增加病案号条形码
		allParm.setData("MR_NO_BAR_CODE", "TEXT", this.getMrNo());
		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
		allParm.addListener("onMouseRightPressed", this, "onMouseRightPressed");
		this.getWord().setWordParameter(allParm);
		// 设置宏
		setMicroField(true);
		String comlunName[] = parm.getNames();
		for (String temp : comlunName) {
			this.getWord().setMicroField(temp, parm.getValue(temp));
			this.setCaptureValueArray(temp, parm.getValue(temp));
		}
		setSingleDiseBasicData();//add by wanglong 20121115  设置单病种基本信息
		setOPEData();//add by wanglong 20130514 设置手术基本信息
		// 设置编辑状态
		this.setOnlyEditType("NEW");
		// 设置不可以编辑
		this.getWord().setCanEdit(false);
		setTMenuItem(false);

		// 申请编辑
		if (ruleType.equals("2") || ruleType.equals("3")) {
			onEdit();
		}
	}

	/**
	 * 设置宏
	 */
	public void setMicroField(boolean falg) {
		Map map = this.getDBTool().select(
				"SELECT * FROM MACRO_PATINFO_VIEW WHERE 1=1 AND MR_NO='"
						+ this.getMrNo() + "'");

		TParm parm = new TParm(map);
		if (parm.getErrCode() < 0) {
			// 取得病人基本资料失败
			this.messageBox("E0110");
			return;
		}

		Timestamp tempBirth = parm.getValue("出生日期", 0).length() == 0 ? SystemTool
				.getInstance().getDate()
				: StringTool.getTimestamp(parm.getValue("出生日期", 0),
						"yyyy-MM-dd");
				// 计算年龄
				String age = "0";
				if (this.getAdmDate() != null) {
					age = OdiUtil.getInstance().showAge(tempBirth, this.getAdmDate());
				} else {
					age = "";
				}
				String dateStr = StringTool.getString(SystemTool.getInstance()
						.getDate(), "yyyy/MM/dd HH:mm:ss");
				parm.addData("年龄", age);
				parm.addData("就诊号", this.getCaseNo());
				parm.addData("病案号", this.getMrNo());
				parm.addData("住院号", this.getIpdNo());
				parm.addData("科室", this.getDeptDesc(Operator.getDept()));
				parm.addData("操作者", Operator.getName());
				parm.addData("申请日期", dateStr);
				parm.addData("日期", StringTool.getString(SystemTool.getInstance()
						.getDate(), "yyyy/MM/dd"));
				parm.addData("时间", StringTool.getString(SystemTool.getInstance()
						.getDate(), "HH:mm:ss"));
				// parm.addData("EMR_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd HH:mm:ssS"));
				parm.addData("病历时间", dateStr);
				parm.addData("入院时间", StringTool.getString(this.getAdmDate(),
						"yyyy/MM/dd HH:mm:ss"));

				parm.addData("出院时间", StringTool.getString(new java.sql.Timestamp(System
						.currentTimeMillis()), "yyyy/MM/dd"));

				parm.addData("调用科室", this.getDeptDesc(this.getDeptCode()));
				parm.addData("SYSTEM", "COLUMNS", "年龄");
				parm.addData("SYSTEM", "COLUMNS", "就诊号");
				parm.addData("SYSTEM", "COLUMNS", "病案号");
				parm.addData("SYSTEM", "COLUMNS", "住院号");
				parm.addData("SYSTEM", "COLUMNS", "科室");
				parm.addData("SYSTEM", "COLUMNS", "操作者");
				parm.addData("SYSTEM", "COLUMNS", "申请日期");
				parm.addData("SYSTEM", "COLUMNS", "日期");
				parm.addData("SYSTEM", "COLUMNS", "时间");
				// parm.addData("SYSTEM","COLUMNS","EMR_DATE");
				parm.addData("SYSTEM", "COLUMNS", "病历时间");
				parm.addData("SYSTEM", "COLUMNS", "入院时间");
				parm.addData("SYSTEM", "COLUMNS", "调用科室");
				//		parm.addData("SYSTEM", "COLUMNS", "传筛结果");// ===pangben 2015-11-4
				//		parm.addData("SYSTEM", "COLUMNS", "传筛结果内容");// ===pangben 2015-11-4
				// 查询住院基本信息(床号，住院诊断)
				TParm odiParm = new TParm(this.getDBTool().select(
						"SELECT * FROM MACRO_ADMINP_VIEW WHERE CASE_NO='"
								+ this.getCaseNo() + "'"));

				if (odiParm.getCount() > 0) {
					for (String parmName : odiParm.getNames()) {
						parm.addData(parmName, odiParm.getValue(parmName, 0));
					}

				} else {
					for (String parmName : odiParm.getNames()) {
						parm.addData(parmName, "");
					}

				}

				// $$====add by lx 2012/02/21 住院预约 start======$$//

				// 查询预约相关的值的视图;
				TParm admRsvParm = new TParm(this.getDBTool().select(
						"SELECT * FROM ADM_RESV_VIEW WHERE RESV_NO='"
								+ this.getCaseNo() // ===== chenxi modify
								+ "'"));

				if (admRsvParm.getCount() > 0) {
					for (String parmName : admRsvParm.getNames()) {
						parm.addData(parmName, admRsvParm.getValue(parmName, 0));
					}
				} else {
					for (String parmName : admRsvParm.getNames()) {
						parm.addData(parmName, "");
					}
				}
				// $$=====add by lx 2012/02/21 end =====$$//

				// 住院诊断
				if ("ODI".equals(this.getSystemType())
						|| "INW".equals(this.getSystemType())) {
					parm.addData("门急住别", "住院");
					String sql = "SELECT A.ICD_CODE,B.ICD_CHN_DESC,A.IO_TYPE,A.MAINDIAG_FLG,A.ICD_TYPE"
							+ " FROM ADM_INPDIAG A,SYS_DIAGNOSIS B"
							+ " WHERE A.ICD_CODE=B.ICD_CODE AND CASE_NO='"
							+ this.getCaseNo() + "' ORDER BY MAINDIAG_FLG DESC";
					TParm odiDiagParm = new TParm(this.getDBTool().select(sql));
					// 所有入院诊断
					StringBuffer inDiagAll = new StringBuffer();
					int inDiags = 1;
					// 所有出院诊断
					StringBuffer diag = new StringBuffer();
					int diags = 1;
					if (odiDiagParm.getCount() > 0) {
						int rowCount = odiDiagParm.getCount();
						for (int i = 0; i < rowCount; i++) {
							TParm temp = odiDiagParm.getRow(i);
							// 门急主诊断
							if ("I".equals(temp.getValue("IO_TYPE"))
									&& "Y".equals(temp.getValue("MAINDIAG_FLG"))) {
								parm.addData("门急诊诊断", temp.getValue("ICD_CHN_DESC"));
								inDiagAll.append("门急诊诊断:"
										+ temp.getValue("ICD_CHN_DESC") + ",");

							}
							// 入院诊断
							if ("M".equals(temp.getValue("IO_TYPE"))
									&& "Y".equals(temp.getValue("MAINDIAG_FLG"))) {
								parm.addData("入院诊断", temp.getValue("ICD_CHN_DESC"));
								inDiagAll.append("入院主诊断:"
										+ temp.getValue("ICD_CHN_DESC") + ",");
							}
							// add
							if ("M".equals(temp.getValue("IO_TYPE"))
									&& "N".equals(temp.getValue("MAINDIAG_FLG"))) {
								inDiagAll.append("入院非主诊断" + inDiags + ":"
										+ temp.getValue("ICD_CHN_DESC") + ",");
								inDiags++;
							}

							// 出院诊断
							if ("O".equals(temp.getValue("IO_TYPE"))
									&& "Y".equals(temp.getValue("MAINDIAG_FLG"))) {
								parm.addData("出院诊断", temp.getValue("ICD_CHN_DESC"));

								diag.append("出院主诊断:" + temp.getValue("ICD_CHN_DESC")
								+ ",");
							}
							if ("O".equals(temp.getValue("IO_TYPE"))
									&& "N".equals(temp.getValue("MAINDIAG_FLG"))) {
								parm.addData("出院诊断附属", temp.getValue("ICD_CHN_DESC"));
								diag.append("出院诊断附属" + diags + ":"
										+ temp.getValue("ICD_CHN_DESC") + ",");
								diags++;
							}
						}

						parm.addData("出院诊断所有", diag.toString());
						parm.addData("入院诊断所有", inDiagAll.toString());
					} else {
						parm.addData("门急诊诊断", "");
						// parm.addData("入院诊断", "");
						parm.addData("出院诊断", "");

						parm.addData("出院诊断所有", "");
						parm.addData("入院诊断所有", "");
					}

					// 计算住院天数
					if (this.getAdmDate() != null) {
						// ==============modify by wanglong 20121128
						Timestamp nowDate = SystemTool.getInstance().getDate();
						Timestamp admDate = this.getAdmDate();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 格式化日期类
						String strNowDate = sdf.format(nowDate);
						String strAdmDate = sdf.format(admDate);
						nowDate = java.sql.Timestamp.valueOf(strNowDate
								+ " 00:00:00.000");
						admDate = java.sql.Timestamp.valueOf(strAdmDate
								+ " 00:00:00.000");
						int inDays = StringTool.getDateDiffer(nowDate, admDate);
						// ==============modify end
						parm.setData("住院天数", 0, inDays == 0 ? "1" : inDays + " 天");
					} else {
						parm.setData("住院天数", 0, 1);
					}

					parm.addData("SYSTEM", "COLUMNS", "门急住别");
					parm.addData("SYSTEM", "COLUMNS", "床号");
					parm.addData("SYSTEM", "COLUMNS", "病区");
					parm.addData("SYSTEM", "COLUMNS", "住院最新诊断");
					parm.addData("SYSTEM", "COLUMNS", "门急诊诊断");
					parm.addData("SYSTEM", "COLUMNS", "入院诊断");
					parm.addData("SYSTEM", "COLUMNS", "出院诊断");
					parm.addData("SYSTEM", "COLUMNS", "出院诊断附属");
					parm.addData("SYSTEM", "COLUMNS", "入院时间");
					parm.addData("SYSTEM", "COLUMNS", "经治医师");
					parm.addData("SYSTEM", "COLUMNS", "主治医师");
					parm.addData("SYSTEM", "COLUMNS", "主任医师");
					parm.addData("SYSTEM", "COLUMNS", "住院天数");
				}
				// 门诊诊断
				if ("ODO".equals(this.getSystemType())
						|| "EMG".equals(this.getSystemType())) {
					parm.addData("门急住别", "门诊");
					// 门诊主诊断
					StringBuffer mainDiag = new StringBuffer();
					// 本次所有诊断
					StringBuffer DiagAll = new StringBuffer();
					// 所有诊断个数
					int odoDiagAllCount = 1;
					// 门诊本次最新诊断(CASE_NO中西医主诊断)
					// 门诊本次所有诊断(CASE_NO)
					TParm odoDiagParm = new TParm(this.getDBTool().select(
							"SELECT A.ICD_CODE,B.ICD_CHN_DESC,A.ICD_TYPE,A.MAIN_DIAG_FLG,A.DIAG_NOTE"
									+ " FROM OPD_DIAGREC A,SYS_DIAGNOSIS B"
									+ " WHERE A.ICD_CODE=B.ICD_CODE AND CASE_NO='"
									+ this.getCaseNo() + "'"));
					if (odoDiagParm.getCount() > 0) {
						int rowCount = odoDiagParm.getCount();
						for (int i = 0; i < rowCount; i++) {
							TParm temp = odoDiagParm.getRow(i);
							//add by huangtt 20160122 start  门诊诊断将备注显示出来
							String diagNote= temp.getValue("DIAG_NOTE");  
							String icdName = temp.getValue("ICD_CHN_DESC"); 
							if (StringUtil.isNullString(icdName)) {
								icdName = diagNote;
							}else{   
								if(diagNote.length() > 0){
									icdName=icdName+"("+diagNote+")";
								}	   	
							} 
							//add by huangtt 20160122 end

							if ("Y".equals(temp.getValue("MAIN_DIAG_FLG"))) {
								if ("W".equals(temp.getValue("ICD_TYPE"))) {
									mainDiag.append("西医主诊断:"
											+ icdName + "|");
									//									+ temp.getValue("ICD_CHN_DESC") + "|");
								}
								if ("C".equals(temp.getValue("ICD_TYPE"))) {
									mainDiag.append("中医主诊断:"
											//									+ temp.getValue("ICD_CHN_DESC") + "|");
											+ icdName + "|");
								}
							} else {
								DiagAll.append("其他诊断" + odoDiagAllCount + ":"
										+ icdName + "|");
								//								+ temp.getValue("ICD_CHN_DESC") + "|");
								odoDiagAllCount++;
							}
						}
						parm.addData("门诊诊断", mainDiag.toString());
						parm
						.addData("门诊诊断所有", mainDiag.toString()
								+ DiagAll.toString());
					} else {
						parm.addData("门诊诊断", "");
						parm.addData("门诊诊断所有", "");
					}
					parm.addData("单病种", diseDesc);// add by wanglong 20121025
					// 为了在住院证上显示单病种
					parm.addData("预约床号", bedNo); // add by chenxi 20130308 预约床号
					parm.addData("SYSTEM", "COLUMNS", "门急住别");
					parm.addData("SYSTEM", "COLUMNS", "门诊诊断");
					parm.addData("SYSTEM", "COLUMNS", "门诊诊断所有");
				}
				// 急诊部分
				if ("EMG".equals(this.getSystemType())) {
					parm.addData("门急住别", "急诊");
					parm.addData("预约床号", bedNo); // add by chenxi 20130308 预约床号
					parm.addData("单病种", diseDesc);// add by wanglong 20121025
					// 为了在住院证上显示单病种
					parm.addData("SYSTEM", "COLUMNS", "门急住别");
				}
				// 健康检查
				if ("HRM".equals(this.getSystemType())) {
					parm.addData("门急住别", "健康检查");
					parm.addData("SYSTEM", "COLUMNS", "门急住别");
				}

				// 公用信息
				// 过敏史(MR_NO)
				// 暂从库中取数据 的公用宏(可能影响速度,需进一步测试);
				StringBuffer drugStr = new StringBuffer();
				TParm drugParm = new TParm(
						this
						.getDBTool()
						.select(
								"SELECT A.CASE_NO,A.MR_NO,CASE A.DRUG_TYPE "
										+ " WHEN 'A' THEN (SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE B.GROUP_ID='PHA_INGREDIENT' AND B.ID=A.DRUGORINGRD_CODE) "
										+ " WHEN 'B' THEN (SELECT B.ORDER_DESC FROM SYS_FEE B WHERE B.ORDER_CODE=A.DRUGORINGRD_CODE) "
										+ " WHEN 'C' THEN (SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE B.GROUP_ID='SYS_ALLERGYTYPE' AND B.ID=A.DRUGORINGRD_CODE) "
										+ " WHEN 'D' THEN (SELECT B.CATEGORY_CHN_DESC FROM SYS_CATEGORY B WHERE RULE_TYPE='PHA_RULE' AND B.CATEGORY_CODE=A.DRUGORINGRD_CODE) "
										+ " WHEN 'E' THEN (SELECT B.CATEGORY_CHN_DESC FROM SYS_CATEGORY B WHERE RULE_TYPE='PHA_RULE' AND B.CATEGORY_CODE=A.DRUGORINGRD_CODE) "
										+ " WHEN 'N' THEN '无' "
										+ " ELSE '' END AS ALLERGY_NAME,OPT_DATE  "
										+ " FROM OPD_DRUGALLERGY A "
										+ " WHERE A.MR_NO='" + this.getMrNo()
										+ "'" + " ORDER BY A.ADM_DATE,A.OPT_DATE "));
				if (drugParm.getCount() > 0) {
					drugStr.append("过敏物质:");
					int rowCount = drugParm.getCount();
					for (int i = 0; i < rowCount; i++) {
						TParm temp = drugParm.getRow(i);
						drugStr.append(temp.getValue("ALLERGY_NAME") + ",");
					}
					String allergy = drugStr.toString();
					allergy = allergy.substring(0, allergy.length() - 1);
					parm.addData("过敏史", allergy);
				} else {
					parm.addData("过敏史", "-");
				}
				parm.addData("SYSTEM", "COLUMNS", "过敏史");
				// 本次看诊门诊主诉现病史(CASE_NO)
				TParm subjParm = new TParm(this.getDBTool().select(
						"SELECT SUBJ_TEXT,OBJ_TEXT FROM OPD_SUBJREC WHERE CASE_NO='"
								+ this.getCaseNo() + "'"));
				if (subjParm.getCount() > 0) {
					parm.addData("主诉现病史", subjParm.getValue("SUBJ_TEXT", 0) + ";"
							+ subjParm.getValue("OBJ_TEXT", 0));
				} else {
					parm.addData("主诉现病史", "");
				}
				parm.addData("SYSTEM", "COLUMNS", "主诉现病史");
				// 既往史(MR_NO)
				StringBuffer medhisStr = new StringBuffer();
				TParm medhisParm = new TParm(
						this
						.getDBTool()
						.select(
								"SELECT B.ICD_CHN_DESC,A.DESCRIPTION FROM OPD_MEDHISTORY A,SYS_DIAGNOSIS B WHERE A.MR_NO='"
										+ this.getMrNo()
										+ "' AND A.ICD_CODE=B.ICD_CODE"));
				if (medhisParm.getCount() > 0) {
					int rowCount = medhisParm.getCount();
					for (int i = 0; i < rowCount; i++) {
						TParm temp = medhisParm.getRow(i);
						medhisStr
						.append(temp.getValue("ICD_CHN_DESC").length() != 0 ? temp
								.getValue("ICD_CHN_DESC")
								: "" + temp.getValue("DESCRIPTION"));
					}
					parm.addData("既往史", medhisStr.toString());
				} else {
					parm.addData("既往史", "");
				}
				parm.addData("SYSTEM", "COLUMNS", "既往史");
				// modify by wangb 2015/11/6 体征数据统一由MACRO_PHYSIDX_VIEW视图提供
				/*// 体征
		TParm sumParm = new TParm(
				this
						.getDBTool()
						.select(
								"SELECT TEMPERATURE,PLUSE,RESPIRE,SYSTOLICPRESSURE,DIASTOLICPRESSURE FROM SUM_INFO_VIEW WHERE CASE_NO='"
										+ this.getCaseNo() + "'"));
		if (sumParm.getCount() > 0) {
			parm.addData("体温", sumParm.getValue("TEMPERATURE", 0));
			parm.addData("脉搏", sumParm.getValue("PLUSE", 0));
			parm.addData("呼吸", sumParm.getValue("RESPIRE", 0));
			parm.addData("收缩压", sumParm.getValue("SYSTOLICPRESSURE", 0));
			parm.addData("舒张压", sumParm.getValue("DIASTOLICPRESSURE", 0));
		} else {
			parm.addData("体温", "");
			parm.addData("脉搏", "");
			parm.addData("呼吸", "");
			parm.addData("收缩压", "");
			parm.addData("舒张压", "");
		}
		parm.addData("SYSTEM", "COLUMNS", "体温");
		parm.addData("SYSTEM", "COLUMNS", "脉搏");
		parm.addData("SYSTEM", "COLUMNS", "呼吸");
		parm.addData("SYSTEM", "COLUMNS", "收缩压");
		parm.addData("SYSTEM", "COLUMNS", "舒张压");*/
				//this.messageBox("come in222222");

				// 检验异常====pangben 2015-11-4
				TParm lisDParm = new TParm(
						this
						.getDBTool()
						.select(
								"SELECT TESTITEM_CHN_DESC,TEST_VALUE,UPPE_LIMIT,LOWER_LIMIT FROM MED_LIS_DEVIANT_VIEW WHERE CASE_NO='"
										+ this.getCaseNo() + "' "));
				// System.out.println("lisDParm手术室：：："+lisDParm);
				if (lisDParm.getCount() > 0) {
					StringBuffer lisString = new StringBuffer();
					lisString.append("项目:");
					for (int i = 0; i < lisDParm.getCount(); i++) {
						if (i != 0) {
							lisString.append("    "
									+ lisDParm.getValue("TESTITEM_CHN_DESC", i)
									+ ",结果值:" + lisDParm.getValue("TEST_VALUE", i));
						} else {
							lisString.append(lisDParm.getValue("TESTITEM_CHN_DESC", i)
									+ ",结果值:" + lisDParm.getValue("TEST_VALUE", i));
						}

					}
					parm.addData("检验异常值", lisString.toString());
				} else {
					parm.addData("检验异常值", "");
				}
				parm.addData("SYSTEM", "COLUMNS", "检验异常值");
				// 检查异常====pangben 2015-11-4
				TParm pasDParm = new TParm(this.getDBTool().select(
						"SELECT OUTCOME_CONCLUSION FROM MED_RIS_DEVIANT_VIEW WHERE CASE_NO='"
								+ this.getCaseNo() + "' "));
				// System.out.println("pasDParmdddfd：：："+pasDParm);
				if (pasDParm.getCount() > 0) {
					StringBuffer lisString = new StringBuffer();
					lisString.append("检查结果:");
					for (int i = 0; i < pasDParm.getCount(); i++) {
						lisString.append(pasDParm.getValue("OUTCOME_CONCLUSION", i)
								+ "\n");
					}
					parm.addData("检查异常值", lisString.toString());
				} else {
					parm.addData("检查异常值", "");
				}
				parm.addData("SYSTEM", "COLUMNS", "检查异常值");

				//this.messageBox("come in11111");	
				// add by wangb 2015/11/4 新增宏列表 START
				// 查询新增宏列表视图
				List<String> macroNameList = new ArrayList<String>();
				String sql = "SELECT MACRO_NAME,MACRO_VALUE,INFECT_FLG FROM MACRO_PHYSIDX_VIEW A, EMR_MICRO_CONVERT B WHERE CASE_NO = '"
						+ this.getCaseNo()
						+ "' AND A.MACRO_NAME = B.MICRO_NAME AND A.MACRO_CODE = B.MACRO_CODE ORDER BY EPISODE_DATE DESC";
				TParm macroViewParm = new TParm(this.getDBTool().select(sql));
				for (int i = 0; i < macroViewParm.getCount(); i++) {
					if (!macroNameList
							.contains(macroViewParm.getValue("MACRO_NAME", i))) {
						macroNameList.add(macroViewParm.getValue("MACRO_NAME", i));
						parm.addData(macroViewParm.getValue("MACRO_NAME", i),
								macroViewParm.getValue("MACRO_VALUE", i));
						parm.addData("SYSTEM", "COLUMNS", macroViewParm.getValue(
								"MACRO_NAME", i));
					}
				}
				// add by wangb 2015/11/4 新增宏列表 END

				// add by wangb 2015/11/4 传筛结果 START
				this.setInfectResult(parm, macroViewParm);
				// add by wangb 2015/11/4 传筛结果 END

				String names[] = parm.getNames();
				TParm obj = (TParm) this.getWord().getFileManager().getParameter();
				TParm macroCodeParm = new TParm(
						this
						.getDBTool()
						.select(
								"SELECT MICRO_NAME,HIS_ATTR,HIS_TABLE_NAME FROM EMR_MICRO_CONVERT WHERE CODE_FLG='Y'"));
				for (String temp : names) {
					// 赋值标志;
					boolean flag = false;
					// ?泛晟柚?对存编号的处理)
					for (int j = 0; j < macroCodeParm.getCount(); j++) {
						// 字典类型 P 公用的,D自定义的 字典;
						String dictionaryType = macroCodeParm.getValue("HIS_ATTR", j);
						// 对应的表名;
						String tableName = macroCodeParm.getValue("HIS_TABLE_NAME", j);
						if (macroCodeParm.getValue("MICRO_NAME", j).equals(temp)) {
							if ("性别".equals(temp)) {
								if (parm.getInt(temp, 0) == 9) {
									this.getWord().setSexControl(0);
								} else {
									// 1.男 2.女
									this.getWord().setSexControl(parm.getInt(temp, 0));
								}
							}
							if (falg) {
								// 设置宏的中文显示名
								this.getWord()
								.setMicroFieldCode(
										temp,
										getDictionary(tableName, parm.getValue(
												temp, 0)),
										this.getEMRCode(dictionaryType,
												tableName, parm.getValue(temp,
														0)));

								// 设置隐藏元素 CODE
								hideElemMap.put(temp, this.getEMRCode(dictionaryType,
										tableName, parm.getValue(temp, 0)));

								// 设置抓取框值;
								this.setCaptureValueArray(temp, getDictionary(
										tableName, parm.getValue(temp, 0)));

								obj.setData(temp, "TEXT", getDictionary(tableName, parm
										.getValue(temp, 0)));

							} else {
								obj.setData(temp, "TEXT", getDictionary(tableName, parm
										.getValue(temp, 0)));
							}
							// 已赋值;
							flag = true;
							break;

						}
					}
					// 已经赋值,继续循环下一个宏
					if (flag) {
						continue;
					}

					String tempValue = parm.getValue(temp, 0);
					if (tempValue == null) {
						continue;
					}
					if (falg) {
						this.getWord().setMicroField(temp, tempValue);
						this.setCaptureValueArray(temp, tempValue);
						// 设置隐藏元素 CODE
						hideElemMap.put(temp, tempValue);

						obj.setData(temp, "TEXT", tempValue);
					} else {
						obj.setData(temp, "TEXT", tempValue);
					}
				}

				this.setHiddenElements(obj);
				// 其它系统需要设置
				// 设置临床路径
				this.setCLPValue();
				// 设置床位证的值;
				this.setAdmResv(parm);

				this.getWord().setWordParameter(obj);

	}

	/**
	 * 宏名称
	 */
	private EFixed fixed;

	private EMacroroutine macroroutine;

	/**
	 * 结构化病历右键调用
	 */
	public void onMouseRightPressed() {
		// add by wangb 2015/09/02 转诊病历不允许右键编辑
		if ("Y".equals(refFlg)) {
			return;
		}
		
		EComponent e = this.getWord().getFocusManager().getFocus();
		if (e == null) {
			return;
		}
		// 是否可编辑
		if(!this.onEdit()){ //modify by wanglong 20121205
			return;
		}
		// $$======Modified by lx 2012/02/23START=========$$//
		TTreeNode node = getTTree(TREE_NAME).getSelectionNode();
		// 病历分类树
		if (node != null) {
			if (node.getType().equals("4")) {
				TParm emrParm = (TParm) node.getData();
				setCanEdit(emrParm);
				
				// add by wangqing 20170817 start
				// 规培医生权限
				if (this.ruleType.equals("3") && "T".equals(this.writeType)) {
					String tSql = " SELECT B.INTERNSHIP_FLG "
							+ "FROM EMR_FILE_INDEX A, EMR_TEMPLET B "
							+ "WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE(+) AND A.CASE_NO = '"+caseNo+"' AND A.SUBCLASS_CODE = '"+emrParm.getValue("SUBCLASS_CODE")+"' ";
					TParm tResult = new TParm(TJDODBTool.getInstance().select(tSql));
					if(tResult.getCount()<=0 || tResult.getErrCode()<0){
						return;
					}
					if(tResult.getValue("INTERNSHIP_FLG", 0) != null && tResult.getValue("INTERNSHIP_FLG", 0).equals("N")){// 设置不可编辑
						this.getWord().setCanEdit(false);
						this.messageBox("规培医生不可编辑！！！");
					}
				}		
				// add by wangqing 20170817 end
				
			}
			// 无分类树情况
		} else {
			setCanEdit(this.getEmrChildParm());
		}

		// $$========Modified by lx 2012/02/23END=============$$//

		if (!this.getWord().canEdit()) {
			return;
		}

		// 抓取框
		if (e instanceof ECapture) {
			return;
		}

		// 宏
		if (e instanceof EFixed) {
			fixed = (EFixed) e;
			this.getWord().popupMenu(fixed.getName() + "修改,onModify"+";上下标修改,onMarkTextProperty", this);
		}
		// 图片
		if (e instanceof EMacroroutine) {
			macroroutine = (EMacroroutine) e;
			this.getWord().popupMenu(
					macroroutine.getName() + "编辑,onModifyMacroroutine", this);

		}

	}

	/**
	 * 构造图片FileServer对应全路径;
	 *
	 * @return String
	 */
	private String getImgFullPath() {
		// 文件服务器路径;
		// fileserver+/JHW/caseNo两位/caseNo月/MR_NO/fileName/
		// 目录表第一个根目录FILESERVER
		String rootName = TIOM_FileServer.getRoot();
		// 模板路径服务器
		String templetPathSer = TIOM_FileServer.getPath("EmrData");
		String filePath = this.getEmrChildParm().getValue("FILE_PATH");
		String foldName = this.getEmrChildParm().getValue("FILE_NAME");
		String fileFullPath = rootName + templetPathSer + filePath + "/"
				+ foldName;
		return fileFullPath;
	}

	/**
	 * 获得模版路径
	 *
	 * @return String
	 */
	private String getTemplatePath() {
		// 模板路径服务器
		String templetPathSer = TIOM_FileServer.getPath("EmrData");
		String filePath = this.getEmrChildParm().getValue("FILE_PATH");
		String foldName = this.getEmrChildParm().getValue("FILE_NAME");
		String templatePath = templetPathSer + filePath + "/" + foldName;
		templatePath = templatePath.replaceAll("\\\\", "/");
		return templatePath;
	}

	/**
	 * 上传word中处理过的用来编辑的图片文件;
	 *
	 * @return boolean
	 */
	private boolean uploadPictureToFileServer() {
		boolean flag = true;
		// 遍历word文件，pic元件，如果是本地临时路径的，则上传到fileServer.
		TList components = this.getWord().getPageManager().getComponentList();
		int size = components.size();
		for (int i = 0; i < size; i++) {
			EPage ePage = (EPage) components.get(i);
			// this.messageBox("EPanel size" + ePage.getComponentList().size());
			for (int j = 0; j < ePage.getComponentList().size(); j++) {
				EPanel ePanel = (EPanel) ePage.getComponentList().get(j);
				//
				for (int k = 0; k < ePanel.getBlockSize(); k++) {
					IBlock block = (IBlock) ePanel.get(k);
					if (block != null) {
						if (block.getObjectType() == EComponent.TABLE_TYPE) {
							// System.out.println("包含表格元素.....");
							for (int row = 0; row < ((ETable) block)
									.getComponentList().size(); row++) {
								int columnCount = ((ETable) block).get(row)
										.getComponentList().size();
								// 处理表格列;
								for (int column = 0; column < columnCount; column++) {
									int tPanelCount = ((ETable) block).get(row)
											.get(column).getComponentList()
											.size();
									// 取表格列数据;
									for (int tpc = 0; tpc < tPanelCount; tpc++) {
										IBlock tBlock = ((ETable) block).get(
												row).get(column).get(tpc)
												.get(0);
										if (tBlock != null) {
											// 处理图片数据,并上传;
											flag = this
													.processPicElement(tBlock);
											if (!flag) {
												return false;
											}

										}
									}
								}
							}

						} else {
							flag = this.processPicElement(block);
							if (!flag) {
								return false;
							}

						}

					}

				}

			}

		}
		// 上传文件成功，则清除监时编辑文件夹中文件;
		if (flag) {
			try {
				this.delDir(picTempPath);
			} catch (IOException ex) {
			}
		}

		return flag;
	}

	/**
	 * 处理PIC元素,并上传到服务器;
	 *
	 * @param block
	 *            IBlock
	 * @return boolean
	 */
	private boolean processPicElement(IBlock block) {
		TSocket socket = TIOM_FileServer.getSocket();
		String filePath = this.getImgFullPath();
		// System.out.println("===filePath===" + filePath);
		if (block instanceof EMacroroutine && ((EMacroroutine) block).isPic()) {
			// this.messageBox("is pic");
			VPic pic = ((VPic) ((EMacroroutine) block).getModel().getMVList()
					.get(0).get(0));
			String fullPathName = pic.getPictureName();
			if (fullPathName != null) {
				// 假如包含本地临时路径的,说明需要上传
				if (pic.getPictureName().indexOf(picTempPath) != -1) {
					// 构造文件名开始上传；
					String finalPictureName = fullPathName.substring(
							fullPathName.lastIndexOf("/") + 1, fullPathName
							.lastIndexOf("."));
					String fileName = finalPictureName.substring(0,
							finalPictureName.lastIndexOf("_"));
					// System.out.println("fileName" + fileName);
					// 构造背景图；构造前景图；构造合成图；
					String bgImage = fileName + "_BG.gif";
					String ticImage = fileName + "_FG.TIC";
					String finalImage = fileName + "_FINAL.jpg";

					byte[] bgData = TIOM_FileServer.readFile(picTempPath
							+ bgImage);
					// 创建图片文件目录;
					boolean isMkDir = TIOM_FileServer.mkdir(socket, filePath);
					if (isMkDir) {
						boolean isflag1 = TIOM_FileServer.writeFile(socket,
								filePath + "/" + bgImage, bgData);

						byte[] fgData = TIOM_FileServer.readFile(picTempPath
								+ ticImage);
						boolean isflag2 = TIOM_FileServer.writeFile(socket,
								filePath + "/" + ticImage, fgData);

						byte[] finalData = TIOM_FileServer.readFile(picTempPath
								+ finalImage);
						boolean isflag3 = TIOM_FileServer.writeFile(socket,
								filePath + "/" + finalImage, finalData);

						if (!isflag1 || !isflag2 || !isflag3) {
							return false;
						}
						// pic path变更为服务器文件路径；
						pic.setPictureName("%FILE.ROOT%" + getTemplatePath()
						+ "/" + finalImage);
						this.getWord().update();

					}

				} // 只是本地直接上传的背景图
				else if (pic.getPictureName().indexOf(":") == 1) {
					String finalPictureName = fullPathName.substring(
							fullPathName.lastIndexOf("/") + 1, fullPathName
							.length());

					boolean isMkDir = TIOM_FileServer.mkdir(socket, filePath);
					if (isMkDir) {
						byte[] bgData = TIOM_FileServer.readFile(fullPathName);
						boolean isflag1 = TIOM_FileServer.writeFile(socket,
								filePath + "/" + finalPictureName, bgData);
						if (!isflag1) {
							return false;
						}
						pic.setPictureName("%FILE.ROOT%" + getTemplatePath()
						+ "/" + finalPictureName);
						this.getWord().update();

					}

				}
			}
		}

		return true;
	}

	/**
	 * 编辑图片功能;
	 */
	public void onModifyMacroroutine() {
		// 设置一个临时画图文件夹， 保存时清除临时文件;
		File f = new File(picTempPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		// 文件服务器路径;
		// fileserver+/JHW/caseNo两位/caseNo月/MR_NO/fileName/
		String fileFullPath = this.getImgFullPath();

		// 取对应的图片;
		MV mv = macroroutine.getModel().getMVList().get("图层");
		if (mv == null) {
			return;
		}
		DIV div = mv.get("图片");
		if (!(div instanceof VPic)) {
			return;
		}
		VPic pic = (VPic) div;

		// 合成图;
		String finalImg = pic.getPictureName();

		// 合成图是否为null; _FINAL.jp
		// 合成图是null，则报请先上传背景图片；
		if (finalImg == null) {
			this.messageBox("请先上传背景图片!");
			return;
			// 合成图不是null,
		} else {
			String fullFileName = finalImg.substring(
					finalImg.lastIndexOf("/") + 1, finalImg.length());
			String fileName = fullFileName.substring(0, fullFileName
					.indexOf("."));
			String extendName = fullFileName.substring(fullFileName
					.indexOf(".") + 1, fullFileName.length());

			//构造三个文件的文件名
			String bgImage = "";
			String ticImage = "";
			String finalImage = "";
			// 包含_FINAL为处理过的文件;
			if (fileName.indexOf("_FINAL") != -1) {
				fileName = fileName.substring(0, fileName.lastIndexOf("_"));
			} else {
				if (!extendName.equalsIgnoreCase("gif")) {
					this.messageBox("非gif背景图不可以编辑!");
					return;
				}
			}
			// extendName
			bgImage = fileName + "_BG.gif";
			ticImage = fileName + "_FG.TIC";
			finalImage = fileName + "_FINAL.jpg";

			// 判断tic是前景图是否存在， 不存在新增；
			String flg = "NEW";
			// 先判断本地临时文件夹中是有编辑的文件；
			// 有则为 flg=NEW;
			File ticFile = new File(picTempPath + ticImage);
			// 存在TIC文件
			if (ticFile.exists()) {
				flg = "OPEN";
			} else {
				// 没有，则看文件服务器上是否有；
				// 有，则下载到本地临时文件夹下。 flg=OPEN
				// 拿到Socket通讯工具
				TSocket socket = TIOM_FileServer.getSocket();
				byte ticByte[] = TIOM_FileServer.readFile(socket, fileFullPath
						+ "/" + ticImage);
				// 应该能下下来的
				if (ticByte != null) {
					// 写临时文件;
					TIOM_FileServer.writeFile(picTempPath + ticImage, ticByte);
					// 下载到本地;
					byte bgByte[] = TIOM_FileServer.readFile(socket,
							fileFullPath + "/" + bgImage);
					// 写临时文件;
					TIOM_FileServer.writeFile(picTempPath + bgImage, bgByte);
					flg = "OPEN";

					// 服务器上没有的，
				} else {
					long timestamp = new Date().getTime();
					// 新增文件名;
					bgImage = fileName + "_" + timestamp + "_BG." + extendName;
					ticImage = fileName + "_" + timestamp + "_FG.TIC";
					finalImage = fileName + "_" + timestamp + "_FINAL.jpg";
					// 本机
					if (pic.getPictureName().indexOf(":") == 1) {
						byte bgByte[] = TIOM_AppServer.readFile(pic
								.getPictureName());
						TIOM_FileServer
						.writeFile(picTempPath + bgImage, bgByte);
						// 否则为文件服务器上背景
					} else {
						// 远程文件如何读取的，描述的格式；
						byte bgByte[] = null;
						String imageFilePath = "";
						if (pic.getPictureName().indexOf("%ROOT%") != -1) {
							bgByte = TIOM_AppServer
									.readFile(TIOM_AppServer.SOCKET, pic
											.getPictureName());
						} else if (pic.getPictureName().indexOf("%FILE.ROOT%") != -1) {
							imageFilePath = pic.getPictureName().replace(
									"%FILE.ROOT%", TIOM_FileServer.getRoot());
							bgByte = TIOM_FileServer.readFile(TIOM_FileServer
									.getSocket(), imageFilePath);
						}

						TIOM_FileServer
						.writeFile(picTempPath + bgImage, bgByte);
					}
					// 写临时文件;
					flg = "NEW";
				}

			}

			BufferedImage bfImage = this.readImage(picTempPath + bgImage);
			int imgWidth = bfImage.getWidth();
			int imgHeight = bfImage.getHeight();

			// OPEN NEW
			// 假如不存在tic 则为新增；
			if (flg.equals("NEW")) {
				// 本地是否存在 文件服务器上 800 /600
				Image_Paint image_Paint1 = new Image_Paint(this.getWord(),
						picTempPath, ticImage, imgWidth, imgHeight,
						picTempPath, bgImage, picTempPath, finalImage, "NEW",
						pic);
				// 1024*768
				image_Paint1.setSize(ImageTool.getScreenWidth(), ImageTool
						.getScreenHeight());
				image_Paint1.setVisible(true);
				image_Paint1.setTitle("图片编辑");
				// 存在 为打开修改;
			} else {
				Image_Paint image_Paint1 = new Image_Paint(this.getWord(),
						picTempPath, ticImage, imgWidth, imgHeight,
						picTempPath, bgImage, picTempPath, finalImage, "OPEN",
						pic);
				// 1024*768
				image_Paint1.setSize(ImageTool.getScreenWidth(), ImageTool
						.getScreenHeight());
				image_Paint1.setVisible(true);
				image_Paint1.setTitle("图片编辑");

			}

		}
		// 设置图片对应属性(服务器对应的路径即属性)；

	}

	/**
	 * 宏修改
	 */
	public void onModify() {
		if (fixed == null) {
			return;
		}
		Object obj = this.openDialog("%ROOT%\\config\\emr\\ModifUI.x", fixed
				.getText());
		if (obj != null) {
			fixed.setText(obj.toString());
		}
	}

	/**
	 * 拿到字典信息
	 *
	 * @param groupId
	 *            String
	 * @param id
	 *            String
	 * @return String
	 */
	public String getDictionary(String groupId, String id) {
		String result = "";
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"
						+ groupId + "' AND ID='" + id + "'"));
		result = parm.getValue("CHN_DESC", 0);
		return result;
	}

	/**
	 * 拿到科室
	 *
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}

	/**
	 * 拿到用户名
	 *
	 * @param userID
	 *            String
	 * @return String
	 */
	public String getOperatorName(String userID) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='" + userID
				+ "'"));
		return parm.getValue("USER_NAME", 0);
	}

	/**
	 * 保存
	 */
	public boolean onSave() {
		//是追加记录
		if(this.isApplend()){
			//1.保存历史病历记录
			boolean flg=onsaveEmr(false);		
			if(flg){				
				//取追加文件内容
				TParm saveParm=this.getEmrChildParm();
				final String filePath=saveParm.getValue("FILE_PATH");
				final String fileName=saveParm.getValue("FILE_NAME");
				//
				if(isDebug){
					System.out.println("==onSave save filePath=="
							+ saveParm.getValue("FILE_PATH"));
					System.out.println("==onSave save fileName=="
							+ saveParm.getValue("FILE_NAME"));
				}
				//			

				// 2.打开依赖病历;
				TParm fileParm = new TParm(
						this
						.getDBTool()
						.select(
								"SELECT CASE_NO,FILE_SEQ,MR_NO,IPD_NO,FILE_PATH,FILE_NAME,DESIGN_NAME,CLASS_CODE,SUBCLASS_CODE,DISPOSAC_FLG"
										+ " FROM EMR_FILE_INDEX"
										+ " WHERE CASE_NO='"
										+ this.getCaseNo()
										+ "' AND FILE_NAME='"
										+ this.getSubFileName()
										+ "' ORDER BY FILE_SEQ DESC"));
				TParm fParm = fileParm.getRow(0);

				fParm.setData("FLG", true);
				//this.messageBox("111111"+saveParm.getValue("CLASS_STYLE"));
				fParm.setData("CLASS_STYLE","4");
				this.onOpenEmrFile(fParm);
				//1.通过    记录time时间   与    现有所有的日志记录对比   确认插入点
				List<EFixed> allInsFixed=getAllInsertfixed(this.getWord());			
				//this.messageBox("====size===="+allInsFixed.size());
				//
				EFixed fixed=null;
				if(allInsFixed.size()>0){
					for(EFixed theInsFixed:allInsFixed){
						if(Long.valueOf(theInsFixed.getName())>=this.getlRecordTime()){
							fixed=theInsFixed;
							break;
						}
					}
				}

				if(fixed==null){
					// TEMP
					fixed = this.getWord().findFixed(
							"APPLEND");
				}
				//
				if (fixed == null) {
					this.messageBox("无插入点！");
					return false;
				}
				fixed.setFocus();
				if (!this.getWord().separatePanel()) {
					return false;
				}

				//
				//动态生成 插入点 
				EFixed insEFixed=this.getWord().insertFixed(String.valueOf(this.getlRecordTime()), " ");
				insEFixed.setInsert(true);
				insEFixed.setLocked(true);				
				insEFixed.onFocusToRight();	
				//
				this.getWord().update();
				//动态生成抓取框
				ECapture objEnd = word.insertCaptureObject();
				ECapture objStart=word.insertCaptureObject();
				objEnd.setCaptureType(1);
				String theCaptureName=UUID.randomUUID().toString();
				objStart.setName(theCaptureName);
				objStart.setLocked(true);
				//
				objEnd.setName(theCaptureName);
				objEnd.setLocked(true);
				//
				//objEnd.onFocusToEnd();
				/*EFixed fixed = findFixed(name);
	        if (fixed == null) {
	            return false;
	        }*/
				//objEnd.onFocusToRight();
				objEnd.setFocus();
				//
				this.getWord().getFocusManager().onInsertFile(filePath, fileName, 3, false);

				//删除一行开始的回车
				objStart.setFocusLast();
				objStart.deleteChar();
				//删除最后一行的回车
				objEnd.setFocus();
				objEnd.backspaceChar();
				objEnd.setFocusLast();
				this.getWord().getFocusManager().separatePanel();
				//
				//this.getWord().getPM().setUser(Operator.getID(), getOperatorName(Operator.getID()));
				//
				this.getWord().update();
				//
				//System.out.println("===filePath==="+filePath);
				//System.out.println("===fileName==="+fileName);
				/*this.getWord().onInsertFileFrontFixed(
					fixed.getName(),
					filePath,
					fileName, 3,
					false);*/
				this.setEmrChildParm(fParm);
				this.setOnlyEditType("ONLYONE");
				//this.getWord().update();
				//
				//查找 抓取框设置引用
				//加入 引用名称属性，  以便能弹出   编辑
				//this.messageBox("name--"+String.valueOf(this.getlRecordTime()));
				//ECapture capture = this.getWord().findCapture(String.valueOf(this.getlRecordTime()));
				//this.messageBox("111  capture  111"+capture.getName());
				//this.messageBox("fileName==="+this.getStrRefFileName());
				//设置引用文件名，以便修改使用
				objStart.setRefFileName(this.getStrRefFileName());
				//追加修改人的名子
				this.getWord().update();

				//自动保存  依赖的主病程文件
				boolean flg1=onsaveEmr(true);

				//打开的日常病程主记录，追加标志false;
				this.setApplend(false);

				return flg1;

			}else{
				this.messageBox("保存失败！");
				return false;
			}

			//非追加记录，按原方法保存;
		}else{
			boolean flg=onsaveEmr(true);
			return flg;
		}
		//return false;

	}

	/**
	 * 保存EMR文件
	 *
	 * @param parm
	 *            TParm
	 */
	public boolean saveEmrFile(TParm parm) {
		boolean falg = true;
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", this.getDBTool().getDBTime());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REPORT_FLG", "N");
		parm.setData("CURRENT_USER", Operator.getID());
		// $$=================假如是值班医生=======================$$//
		if (isDutyDrList()) {
			parm.setData("CURRENT_USER", Operator.getID());
			/*			parm.setData("CHK_USER1", "");
			parm.setData("CHK_DATE1", "");
			parm.setData("CHK_USER2", "");
			parm.setData("CHK_DATE2", "");
			parm.setData("CHK_USER3", "");
			parm.setData("CHK_DATE3", "");
			parm.setData("COMMIT_USER", "");
			parm.setData("COMMIT_DATE", "");*/
		}
		// $$=================假如是值班医生=======================$$//

		if(this.isOpeSaveCheck()){//add by wangjc 20171013
			parm.setData("OPBOOK_SEQ", this.getOpbookSeq());
			parm.setData("OPE_SAVE_CHECK", "Y");
		}
		
		if (this.getOnlyEditType().equals("NEW")) {
			parm.setData("CREATOR_USER", Operator.getID());
			TParm result = TIOM_AppServer.executeAction(actionName,
					"saveNewEmrFile", parm);
			if (result.getErrCode() < 0) {
				falg = false;
			}
			return falg;
		}
		// this.messageBox("type"+this.getOnlyEditType());

		if (this.getOnlyEditType().equals("ONLYONE")) {
			TParm result = TIOM_AppServer.executeAction(actionName,
					"writeEmrFile", parm);
			if (result.getErrCode() < 0) {
				falg = false;
			}
		}
		return falg;
	}

	/**
	 * 拿到文件服务器路径
	 *
	 * @param rootPath
	 *            String
	 * @param fileServerPath
	 *            String
	 * @return String
	 */
	public TParm getFileServerEmrName() {
		TParm emrParm = new TParm();
		String emrName = "";
		TParm childParm = this.getEmrChildParm();
		String templetName = childParm.getValue("EMT_FILENAME");
		//System.out.println("=============getFileServerEmrName templetName================"+templetName);

		TParm action = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO" +
								" FROM EMR_FILE_INDEX" +
								" WHERE CASE_NO='"
								+ this.getCaseNo() + "'"));
		int index = action.getInt("MAXFILENO", 0);
		emrName = this.getCaseNo() + "_" + templetName + "_" + index;
		//this.messageBox("==emrName=="+emrName);
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		emrParm.setData("FILE_SEQ", index);
		emrParm.setData("FILE_NAME", emrName);
		emrParm.setData("CLASS_CODE", childParm.getData("CLASS_CODE"));
		emrParm.setData("SUBCLASS_CODE", childParm.getData("SUBCLASS_CODE"));
		emrParm.setData("CASE_NO", this.getCaseNo());
		emrParm.setData("MR_NO", this.getMrNo());
		emrParm.setData("IPD_NO", this.getIpdNo());
		emrParm.setData("FILE_PATH", "JHW" + "\\" + this.getAdmYear() + "\\"
				+ this.getAdmMouth() + "\\" + this.getMrNo());
		emrParm.setData("DESIGN_NAME", templetName + "(" + dateStr + ")");
		emrParm.setData("DISPOSAC_FLG", "N");
		emrParm.setData("TYPEEMR", childParm.getValue("TYPEEMR"));
		emrParm.setData("EMT_FILENAME", childParm.getValue("EMT_FILENAME"));
		emrParm.setData("OPBOOK_SEQ", this.getOpbookSeq());
		return emrParm;
	}

	/**
	 * 检核是否保存
	 */
	public boolean checkSave() {
		if (this.getWord().getFileOpenName() != null
				&& (ruleType.equals("2") || ruleType.equals("3"))
				&& this.getTMenuItem("save").isEnabled()) {
			// P0009提示信息
			if (this.messageBox("提示信息 Tips", "是否保存"
					+ this.getWord().getFileOpenName() + "文件?\n To save"
					+ this.getWord().getFileOpenName() + "File?",
					this.YES_NO_OPTION) == 0) {
				// 插入数据库数据
				if (onSave()) {
					this.loadTree();
					return true;
				} else {
					// 保存失败
					this.messageBox("E0001");
					return false;
				}
			} else {

			}
		}
		return true;
	}

	public static void main(String args[]) {
		/*
		 * JFrame f = new JFrame(); f.getRootPane().add(new EMRPad30());
		 * f.getRootPane().setLayout(new BorderLayout()); f.setVisible(true);
		 */
		JavaHisDebug.TBuilder();
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
	 * 得到系统类别
	 *
	 * @return String
	 */
	public String getSystemType() {
		return systemType;
	}

	public String getIpdNo() {
		return ipdNo;
	}

	public String getMrNo() {
		return mrNo;
	}

	public String getPatName() {
		return patName;
	}

	public String getAdmMouth() {
		return admMouth;
	}

	public String getAdmYear() {
		return admYear;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public TParm getEmrChildParm() {
		return emrChildParm;
	}

	public String getDirectorDrCode() {
		return directorDrCode;
	}

	public String getAttendDrCode() {
		return attendDrCode;
	}

	public String getVsDrCode() {
		return vsDrCode;
	}

	public String getAdmType() {
		return admType;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public String getOnlyEditType() {
		return onlyEditType;
	}

	public Timestamp getAdmDate() {
		return admDate;
	}

	public String getType() {
		return type;
	}

	public String getStationCode() {
		return stationCode;
	}

	/**
	 * 得到系统类别
	 *
	 * @param systemType
	 *            String
	 */
	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public void setAdmMouth(String admMouth) {
		this.admMouth = admMouth;
	}

	public void setAdmYear(String admYear) {
		this.admYear = admYear;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public void setEmrChildParm(TParm emrChildParm) {
		this.emrChildParm = emrChildParm;
	}

	public void setDirectorDrCode(String directorDrCode) {
		this.directorDrCode = directorDrCode;
	}

	public void setAttendDrCode(String attendDrCode) {
		this.attendDrCode = attendDrCode;
	}

	public void setVsDrCode(String vsDrCode) {
		this.vsDrCode = vsDrCode;
	}

	public void setAdmType(String admType) {
		this.admType = admType;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public void setOnlyEditType(String onlyEditType) {
		this.onlyEditType = onlyEditType;
	}

	public void setAdmDate(Timestamp admDate) {
		this.admDate = admDate;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	/**
	 *
	 * @param word
	 *            TWord
	 */
	public void setWord(TWord word) {
		this.word = word;
	}

	public TWord getWord() {
		return this.word;
	}

	/**
	 * 插入表格
	 */
	public void onInsertTable() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().insertBaseTableDialog();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 删除表格
	 */
	public void onDelTable() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().deleteTable();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 插入表格行
	 */
	public void onInsertTableRow() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().insertTR();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 追加表格行
	 */
	public void onAddTableRow() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().appendTR();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 删除表格行
	 */
	public void onDelTableRow() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().deleteTR();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 打印设置
	 */
	public void onPrintSetup() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().printSetup();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onPreviewWord();
			this.getWord().print();
			//yanjing 20130807 modify
			if(adm_type.equals("E")||adm_type.equals("O")){
				this.closeWindow();
			}
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 整洁打印
	 */
	public void onPrintClear() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onPreviewWord();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 剪切
	 */
	public void onCutMenu() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onCut();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 复制
	 */
	public void onCopyMenu() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onCopy();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 粘贴
	 */
	public void onPasteMenu() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onPaste();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 删除
	 */
	public void onDeleteMenu() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onDelete();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 按页打印
	 */
	public void onPrintIndex() {
		if (this.getWord().getFileOpenName() != null) {
			//add by huangtt 20151217 
			if(medApplyNo.length() > 0){
				onSave();
			}
			this.getWord().onPreviewWord();
			this.getWord().getPM().getPageManager().printDialog();
			//yanjing 20130807 modify
			if(adm_type.equals("E")||adm_type.equals("O")){
				this.closeWindow();
			}
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 宏刷新
	 */
	public void onHongR() {
		if (this.getWord().getFileOpenName() != null) {
			this.setMicroField(true);
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 续印
	 */
	public void onPrintXDDialog() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onPreviewWord();
			this.getWord().printXDDialog();

		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 显示行号开关
	 */
	public void onShowRowIDSwitch() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().setShowRowID(!word.isShowRowID());
			this.getWord().update();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 加入段落
	 */
	public void onAddDLText() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onParagraphDialog();
		} else {
			this.messageBox("请选择模版！");
		}
	}

	public void onChangeLanguage(String language) {

	}

	/****************************** 张建国 非空验证 ******************************/
	/**
	 * 对象非空验证
	 *
	 * @param str
	 *            String
	 * @return boolean
	 */
	public boolean checkInputObject(Object obj) {
		if (obj == null) {
			return false;
		}
		String str = String.valueOf(obj);
		if (str == null) {
			return false;
		} else if ("".equals(str.trim())) {
			return false;
		} else {
			return true;
		}
	}

	/****************************** 张建国 是否允许修改、是否允许打印设置 ******************************/
	/**
	 * 查询电子病历文件存档
	 *
	 * @param caseNo
	 *            String
	 * @param fileSeq
	 *            String
	 * @return TParm
	 */
	public TParm getEmrFileIndex(String caseNo, String fileSeq) {
		String sql = " SELECT EFI.* FROM EMR_FILE_INDEX EFI WHERE EFI.CASE_NO = '"
				+ caseNo + "' AND FILE_SEQ = '" + fileSeq + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * 设置允许打印标识和允许修改标识
	 *
	 * @param isVisible
	 * @param canPrintFlg
	 */
	public void setCanPrintFlgAndModifyFlg(String caseNo, String fileSeq,
			boolean isVisible) {
		TCheckBox CANPRINT_FLG = (TCheckBox) this.getComponent("CANPRINT_FLG");
		TCheckBox MODIFY_FLG = (TCheckBox) this.getComponent("MODIFY_FLG");
		TMenuItem PrintShow = (TMenuItem) this.getComponent("PrintShow");
		TMenuItem PrintSetup = (TMenuItem) this.getComponent("PrintSetup");
		TMenuItem PrintClear = (TMenuItem) this.getComponent("PrintClear");
		TMenuItem printIndex = (TMenuItem) this.getComponent("printIndex");
		TMenuItem LinkPrint = (TMenuItem) this.getComponent("LinkPrint");
		TMenuItem save = (TMenuItem) this.getComponent("save");
		// 是否显示
		if (isVisible) {
			CANPRINT_FLG.setVisible(true);
			MODIFY_FLG.setVisible(true);
			// 查询电子病历文件存档
			TParm result = getEmrFileIndex(caseNo, fileSeq);
			// 设置允许打印标识
			CANPRINT_FLG.setValue(result.getValue("CANPRINT_FLG", 0));
			// 设置允许修改标识
			MODIFY_FLG.setValue(result.getValue("MODIFY_FLG", 0));
			// 当前用户是否为最后编辑人
			if (Operator.getID().equals(result.getValue("CURRENT_USER", 0))) {
				CANPRINT_FLG.setEnabled(true);
				MODIFY_FLG.setEnabled(true);
				// 允许打印
				PrintShow.setEnabled(true);
				PrintSetup.setEnabled(true);
				PrintClear.setEnabled(true);
				printIndex.setEnabled(true);
				LinkPrint.setEnabled(true);
				// 允许修改
				save.setEnabled(true);
			} else {
				CANPRINT_FLG.setEnabled(false);
				MODIFY_FLG.setEnabled(false);
				// 设置是否允许打印
				if ("Y".equals(result.getValue("CANPRINT_FLG", 0))) {
					PrintShow.setEnabled(true);
					PrintSetup.setEnabled(true);
					PrintClear.setEnabled(true);
					printIndex.setEnabled(true);
					LinkPrint.setEnabled(true);
				} else {
					PrintShow.setEnabled(false);
					PrintSetup.setEnabled(false);
					PrintClear.setEnabled(false);
					printIndex.setEnabled(false);
					LinkPrint.setEnabled(false);
				}
				// 设置是否允许修改
				if ("Y".equals(result.getValue("MODIFY_FLG", 0))) {
					save.setEnabled(true);
				} else {
					save.setEnabled(false);
				}
			}
		} else {
			CANPRINT_FLG.setVisible(false);
			MODIFY_FLG.setVisible(false);
			CANPRINT_FLG.setEnabled(false);
			MODIFY_FLG.setEnabled(false);
			PrintShow.setEnabled(true);
			PrintSetup.setEnabled(true);
			PrintClear.setEnabled(true);
			printIndex.setEnabled(true);
			LinkPrint.setEnabled(true);
			save.setEnabled(true);
		}
	}
	//$$==========del by lx2012/03/23==============$$//

	/**
	 * 窗口关闭时取消Timer定时器
	 *
	 * @return boolean
	 * 
	 */
	public boolean onClosing() {
		//yanjing 20130808 门急诊自动关闭，不需要询问
		if (!(adm_type.equals("E")) && !(adm_type.equals("O"))) {
			// add by wangb 介入安全核查单病历书写关闭时提示是否保存 2017/1/6
			Object obj = this.getParameter();
			TParm parm = (TParm) obj;
			if ("Y".equals(parm.getValue("EMR_SAVE_MSG_FLG"))) {
				switch (messageBox("提示信息", "是否保存?", this.YES_NO_CANCEL_OPTION)) {
				case 0:
					if (!onSave()) {
						return false;
					}
					break;
				case 1:
					break;
				case 2:
					return false;
				}

				// add by wangb 2017/1/9  介入护理平台介入安全核查单关闭时自动调用清空按钮
				((TParm) obj).runListener("CLEAR_LISTENER", new TParm());
			} else {
				if (this.messageBox("询问", "是否关闭？", 2) != 0) {
					return false;
				}
			}
		}

		super.onClosing();
		this.setReturnValue(returnParm);//add by wanglong 20121115
		// 退出自动保存定时器
		// this.cancel();
		return true;
	}

	/****************************** 张建国 三级检诊 ******************************/
	/**
	 * 三级检诊权限控制概要 1、ruleType=1 无三级检诊，所有记录只能查看 2、ruleType=2且systemType!=ODI
	 * 无三级检诊，可以查看、新建、编辑、删除护理记录，非护理记录只能查看
	 * 3、ruleType=3且systemType=ODI、writeType=OIDR
	 * 无三级检诊，可以查看、新建、编辑、删除会诊病历，非会诊病历只能查看 4、ruleType=2且systemType=ODI 三级检诊权限控制
	 */
	/**
	 * 验证用户是否提交了病历
	 */
	public boolean checkUserSubmit(TParm parm, String user) {
		String chkUser1 = parm.getValue("CHK_USER1");
		String chkUser2 = parm.getValue("CHK_USER2");
		String chkUser3 = parm.getValue("CHK_USER3");
		if (user.equals(chkUser1)) {
			return true;
		}
		if (user.equals(chkUser2)) {
			return true;
		}
		if (user.equals(chkUser3)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取用户的职责代码 
	 *   
	 * 
	 */
	public int getUserDuty(String user) {
		// 1：非经治医师 非主治医师 非主任医师
		if (!user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 1;
		}
		//2：经治医师 非主治医师 非主任医师
		if (user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 2;
		}
		// 3：非经治医师   是主治医师     非主任医师
		if (!user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 3;
		}
		//4：非经治医师  非主治医师   主任医师
		if (!user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 4;
		}
		//5：经治医师      主治医师       非主任医师
		if (user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 5;
		}
		//6：经治医师    非主治医师     主任医师
		if (user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 6;
		}
		//7：非经治医师   主治医师     主任医师
		if (!user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 7;
		}
		//8：经治医师  主治医师  主任医师
		if (user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 8;
		}
		return 0;
	}

	/**
	 * 组装操作数据
	 *
	 * @param parm
	 *            操作数据
	 * @param optType
	 *            操作类型 1:编辑保存 2:提交 3:取消提交(当前用户已提交) 4:取消提交(当前用户未提交)
	 */
	public void setOptDataParm(TParm parm, int optType) {
		TParm data = this.getEmrChildParm();
		int operDuty = this.getUserDuty(Operator.getID());
		// 保存时
		if (optType == 1) {
			// 需设置CANPRINT_FLG和MODIFY_FLG
			TCheckBox CANPRINT_FLG = (TCheckBox) this
					.getComponent("CANPRINT_FLG");
			TCheckBox MODIFY_FLG = (TCheckBox) this.getComponent("MODIFY_FLG");
			parm.setData("CANPRINT_FLG", CANPRINT_FLG.getValue());
			parm.setData("MODIFY_FLG", MODIFY_FLG.getValue());
		}
		// 提交时
		if (optType == 2) {
			// 允许打印、允许修改
			parm.setData("CANPRINT_FLG", "Y");
			parm.setData("MODIFY_FLG", "Y");
			// CURRENT_USER为上级医师（当前用户为主任医师时为零），签名后，进入到上级医师
			if (operDuty == 1) {
			}
			if (operDuty == 2) {
				parm.setData("CURRENT_USER", this.getAttendDrCode());
			}
			if (operDuty == 3) {
				parm.setData("CURRENT_USER", this.getDirectorDrCode());
			}
			if (operDuty == 4) {
				parm.setData("CURRENT_USER", "0");
			}
			if (operDuty == 5) {
				parm.setData("CURRENT_USER", this.getDirectorDrCode());
			}
			if (operDuty == 6) {
				parm.setData("CURRENT_USER", "0");
			}
			if (operDuty == 7) {
				parm.setData("CURRENT_USER", "0");
			}
			if (operDuty == 8) {
				parm.setData("CURRENT_USER", "0");
			}
			// 填写本级检诊人和时间
			if (Operator.getID().equals(this.getVsDrCode())) {
				parm.setData("CHK_USER1", Operator.getID());
				parm.setData("CHK_DATE1", this.getDBTool().getDBTime());
			}
			if (Operator.getID().equals(this.getAttendDrCode())) {
				parm.setData("CHK_USER2", Operator.getID());
				parm.setData("CHK_DATE2", this.getDBTool().getDBTime());
			}
			if (Operator.getID().equals(this.getDirectorDrCode())) {
				parm.setData("CHK_USER3", Operator.getID());
				parm.setData("CHK_DATE3", this.getDBTool().getDBTime());
			}
			// 填写提交人和时间
			parm.setData("COMMIT_USER", Operator.getID());
			parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
		}
		// 取消提交(当前用户已提交)   都应该走  这个3，只能取消自已的签名
		if (optType == 3) {
			// 允许打印、允许修改
			parm.setData("CANPRINT_FLG", "Y");
			parm.setData("MODIFY_FLG", "Y");
			// CURRENT_USER为当前用户（当前用户为经治医师时清空），提交人和时间为下级医师（当前用户为经治医师时清空）
			if (operDuty == 1) {
			}
			if (operDuty == 2) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
			}
			if (operDuty == 3) {
				parm.setData("CURRENT_USER", this.getAttendDrCode());
				parm.setData("COMMIT_USER", this.getVsDrCode());
				parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
			}
			if (operDuty == 4) {
				parm.setData("CURRENT_USER", this.getDirectorDrCode());
				parm.setData("COMMIT_USER", this.getAttendDrCode());
				parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
			}
			if (operDuty == 5) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
			}
			if (operDuty == 6) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
			}
			if (operDuty == 7) {
				parm.setData("CURRENT_USER", this.getAttendDrCode());
				parm.setData("COMMIT_USER", this.getVsDrCode());
				parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
			}
			if (operDuty == 8) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
			}
			// 本级检诊人和时间清空
			if (Operator.getID().equals(this.getVsDrCode())) {
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (Operator.getID().equals(this.getAttendDrCode())) {
				parm.setData("CHK_USER2", "");
				parm.setData("CHK_DATE2", "");
			}
			if (Operator.getID().equals(this.getDirectorDrCode())) {
				parm.setData("CHK_USER3", "");
				parm.setData("CHK_DATE3", "");
			}
		}
		// 取消提交(当前用户未提交)  签名情况
		if (optType == 4) {
			// 允许打印、允许修改
			parm.setData("CANPRINT_FLG", "Y");
			parm.setData("MODIFY_FLG", "Y");
			// CURRENT_USER为下级医师（当前用户为经治医师或主治医师时清空），提交人和时间为下级医师的下级医师（当前用户为经治医师或主治医师时清空），下级检诊人和时间清空
			if (operDuty == 1) {
			}
			if (operDuty == 2) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (operDuty == 3) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (operDuty == 4) {
				parm.setData("CURRENT_USER", this.getAttendDrCode());
				parm.setData("COMMIT_USER", this.getVsDrCode());
				parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
				parm.setData("CHK_USER2", "");
				parm.setData("CHK_DATE2", "");
			}
			if (operDuty == 5) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (operDuty == 6) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (operDuty == 7) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (operDuty == 8) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
		}

		// 一级检诊人
		if (!parm.existData("CHK_USER1")) {
			parm.setData("CHK_USER1", data.getValue("CHK_USER1"));
		}
		// 一级检诊时间
		if (!parm.existData("CHK_DATE1")) {
			parm.setData("CHK_DATE1", data.getValue("CHK_DATE1"));
		}
		// 二级检诊人
		if (!parm.existData("CHK_USER2")) {
			parm.setData("CHK_USER2", data.getValue("CHK_USER2"));
		}
		// 二级检诊时间
		if (!parm.existData("CHK_DATE2")) {
			parm.setData("CHK_DATE2", data.getValue("CHK_DATE2"));
		}
		// 三级检诊人
		if (!parm.existData("CHK_USER3")) {
			parm.setData("CHK_USER3", data.getValue("CHK_USER3"));
		}
		// 三级检诊时间
		if (!parm.existData("CHK_DATE3")) {
			parm.setData("CHK_DATE3", data.getValue("CHK_DATE3"));
		}
		// 提交人
		if (!parm.existData("COMMIT_USER")) {
			parm.setData("COMMIT_USER", data.getValue("COMMIT_USER"));
		}
		// 提交时间
		if (!parm.existData("COMMIT_DATE")) {
			parm.setData("COMMIT_DATE", data.getValue("COMMIT_DATE"));
		}
		// 在院审核人
		if (!parm.existData("IN_EXAMINE_USER")) {
			parm.setData("IN_EXAMINE_USER", data.getValue("IN_EXAMINE_USER"));
		}
		// 在院审核时间
		if (!parm.existData("IN_EXAMINE_DATE")) {
			parm.setData("IN_EXAMINE_DATE", data.getValue("IN_EXAMINE_DATE"));
		}
		// 出院审核人
		if (!parm.existData("DS_EXAMINE_USER")) {
			parm.setData("DS_EXAMINE_USER", data.getValue("DS_EXAMINE_USER"));
		}
		// 出院审核时间
		if (!parm.existData("DS_EXAMINE_DATE")) {
			parm.setData("DS_EXAMINE_DATE", data.getValue("DS_EXAMINE_DATE"));
		}
		// PDF生成人
		if (!parm.existData("PDF_CREATOR_USER")) {
			parm.setData("PDF_CREATOR_USER", data.getValue("PDF_CREATOR_USER"));
		}
		// PDF生成时间
		if (!parm.existData("PDF_CREATOR_DATE")) {
			parm.setData("PDF_CREATOR_DATE", data.getValue("PDF_CREATOR_DATE"));
		}
	}

	/**
	 * 1.病历查看权限控制：
	 */
	public boolean checkDrForOpen() {
		return true;
	}

	/**
	 * 2.病历新建保存权限控制： ruleType=1不可新建 保存时，填写创建人和时间
	 */
	public boolean checkDrForNew() {
		if ("1".equals(this.ruleType)) {
			return false;
		}
		return true;
	}

	/**
	 * 3.病历编辑保存权限控制： ruleType=1不可编辑 ruleType=2且systemType!=ODI
	 * EMR_FILE_INDEX.SYSTEM_CODE!=INW不可编辑，否则可以编辑
	 * ruleType=3且systemType=ODI且writeType=OIDR
	 * EmrFileIndex.OIDR_FLG!=Y不可编辑，否则可以编辑 ruleType=2且systemType=ODI，
	 * 非三级检诊医师及值班医师不可编辑 若CURRNET_USER为空（经治医师），则非三级检诊医师及值班医师不可编辑 否则，
	 * 若当前用户为CURRENT_USER可以编辑 否则， 若MODIFY_FLG=N不可编辑 否则，
	 * 若经治医师未提交，则非三级检诊医师及值班医师不可编辑 否则，若主治医师未提交，则非主治医师且非主任医师不可编辑
	 * 否则，若主任医师未提交，则非主任医师不可编辑 否则，（主任医师已提交），则三级检诊医师及值班医师均不可编辑 否则不可编辑
	 * 保存时，需设置CANPRINT_FLG和MODIFY_FLG
	 */
	public boolean checkDrForEdit() {
		TParm data = this.getEmrChildParm();
		if ("1".equals(this.ruleType)) {
			return false;
		}
		if ("2".equals(this.ruleType) && !"ODI".equals(this.getSystemType())) {
			if (!"INW".equals(data.getValue("SYSTEM_CODE"))) {
				return false;
			} else {
				return true;
			}
		}
		if ("3".equals(this.ruleType) && "ODI".equals(this.getSystemType())
				&& "OIDR".equals(this.writeType)) {
			if (!"Y".equals(data.getValue("OIDR_FLG"))) {
				return false;
			} else {
				return true;
			}
		}
		if ("2".equals(this.ruleType) && "ODI".equals(this.getSystemType())) {
			// this.messageBox("current_user" + data.getValue("CURRENT_USER"));
			if (!this.isCheckUserDr() && !this.isDutyDrList()) {
				return false;
			}
			if (!this.checkInputObject(data.getValue("CURRENT_USER"))) {
				return true;
			} else {
				if (Operator.getID().equals(data.getValue("CURRENT_USER"))) {
					return true;
				} else {

					//
					if ("N".equals(data.getValue("MODIFY_FLG"))) {
						return false;
					} else {
						// 假如是值班医师，可以编辑；
						if (isDutyDrList()) {
							return true;
						}
						// 经治医师没有提交病历          (经冶医没有签名)
						if (!this.checkUserSubmit(data, this.getVsDrCode())) {
							this.messageBox("经冶医没有签名");
							return false;
							// 主治医师没有提交病历    (主治医师没有签名)
						} else if (!this.checkUserSubmit(data, this
								.getAttendDrCode())) {
							if (!Operator.getID()
									.equals(this.getAttendDrCode())
									&& !Operator.getID().equals(
											this.getDirectorDrCode())) {
								this.messageBox("主冶医师没有签名");
								return false;
							} else {
								return true;
							}
							// 科主任没有提交病历     (科主任没有签名)
						} else if (!this.checkUserSubmit(data, this
								.getDirectorDrCode())) {
							if (!Operator.getID().equals(
									this.getDirectorDrCode())) {
								this.messageBox("科主任没有签名");
								return false;
							} else {
								return true;
							}
							// 都没提交
						} else {
							return false;
						}
						// 都没提交
						// return true;
					}
				}
			}
		} else {
			return false;
		}
	}

	/**
	 * 4.
	 * 
	 * 提交时，CURRENT_USER为上级医师（当前用户为主任医师时为零），允许打印、允许修改，填写本级检诊人和时间，填写提交人和时间
	 */
	public boolean checkDrForSubmit() {
		TParm data = this.getEmrChildParm();
		//1.病历提交权限控制： ruleType!=2或systemType!=ODI不可提交 非  三级检诊医师不可提交(签名)
		if (!"2".equals(this.ruleType) || !"ODI".equals(this.getSystemType())) {
			System.out.println("111111checkDrForSubmit  is  false;");
			return false;
		}
		if (!this.isCheckUserDr()) {
			System.out.println("222222222checkDrForSubmit  is  false;");
			return false;
		}

		if (!this.checkInputObject(data.getValue("CURRENT_USER"))) {
			//若CURRENT_USER为空（经治医师），则非经治医师      不可提交 
			if (!Operator.getID().equals(this.getVsDrCode())) {
				System.out.println("333333333checkDrForSubmit  is  false;");
				return false;

			} else {
				System.out.println("44444444444444checkDrForSubmit  is  true;");
				return true;
			}
			//否则， 若当前用户非CURRENT_USER不可提交      否则可以提交
		} else {
			if (!Operator.getID().equals(data.getValue("CURRENT_USER"))) {
				System.out.println("55555555555555checkDrForSubmit  is  false;");
				return false;
			} else {

				return true;
			}
		}
	}

	/**
	 * 5.病历取消提交权限控制： ruleType!=2或systemType!=ODI不可取消提交 非三级检诊医师不可取消提交
	 * 若CURRNET_USER为空（经治医师），则不可取消提交 否则， 若当前用户为CURRENT_USER，则可以取消提交 否则，
	 * 若主任医师已提交，则非主任医师不可取消提交 否则，若主治医师已提交，则非主治医师且非主任医师不可提交
	 * 否则，若经治医师已提交，则非经治医师且非主治医师不可提交 否则，（经治医师未提交），不可取消提交
	 * 当前用户已提交：CURRENT_USER为当前用户
	 * （当前用户为经治医师时清空），允许打印、允许修改，本级检诊人和时间清空，提交人和时间为下级医师（当前用户为经治医师时清空）
	 * 当前用户未提交：CURRENT_USER为下级医师
	 * （当前用户为主治医师时清空），允许打印、允许修改，下级检诊人和时间清空，提交人和时间为下级医师的下级医师（当前用户为主治医师时清空）
	 */
	public boolean checkDrForSubmitCancel() {
		TParm data = this.getEmrChildParm();
		if (!"2".equals(this.ruleType) || !"ODI".equals(this.getSystemType())) {
			return false;
		}
		if (!this.isCheckUserDr()) {
			return false;
		}
		if (!this.checkInputObject(data.getValue("CURRENT_USER"))) {
			return false;
		} else {
			if (Operator.getID().equals(data.getValue("CURRENT_USER"))) {
				return true;
			} else {
				if (this.checkUserSubmit(data, this.getDirectorDrCode())) {
					if (!Operator.getID().equals(this.getDirectorDrCode())) {
						return false;
					} else {
						return true;
					}
				} else if (this.checkUserSubmit(data, this.getAttendDrCode())) {
					if (!Operator.getID().equals(this.getAttendDrCode())
							&& !Operator.getID().equals(
									this.getDirectorDrCode())) {
						return false;
					} else {
						return true;
					}
				} else if (this.checkUserSubmit(data, this.getVsDrCode())) {
					if (!Operator.getID().equals(this.getVsDrCode())
							&& !Operator.getID().equals(this.getAttendDrCode())) {
						return false;
					} else {
						return true;
					}
				} else {
					return false;
				}
			}
		}
	}

	/**
	 * 6.病历删除、是否允许打印设置、是否允许修改设置权限控制： ruleType=1不可删除 当前用户非CURRENT_USER不可删除
	 */
	public boolean checkDrForDel() {
		TParm data = this.getEmrChildParm();
		// TTreeNode node = this.getTTree(TREE_NAME).getSelectionNode();
		// TParm data = (TParm) node.getData();
		// this.messageBox("data"+data);
		// CURRENT_USER
		String currentUser = data.getValue("CURRENT_USER");
		// this.messageBox("ruleType==="+this.ruleType);
		if ("1".equals(this.ruleType)) {
			return false;
		}
		if (!Operator.getID().equals(currentUser)) {
			return false;
		}
		return true;
	}

	/**
	 * 获得父节点下的所有子节点
	 *
	 * @param parentClassCode
	 *            String 父节点
	 * @param allChilds
	 *            TParm 最终返回所有节点
	 */

	private void getAllChildsByParent(String parentClassCode, TParm allChilds) {
		TParm childs = this.getChildsByParentNode(parentClassCode);
		if (childs != null && childs.getCount() > 0) {
			// 初始值；
			if (allChilds.getCount() == -1) {
				allChilds.setCount(0);
			}
			allChilds.setCount(allChilds.getCount() + childs.getCount());

			for (int i = 0; i < childs.getCount(); i++) {
				allChilds
				.addData("CLASS_CODE", childs.getData("CLASS_CODE", i));
				allChilds
				.addData("CLASS_DESC", childs.getData("CLASS_DESC", i));
				allChilds.addData("CLASS_STYLE", childs.getData("CLASS_STYLE",
						i));
				allChilds.addData("LEAF_FLG", childs.getData("LEAF_FLG", i));
				allChilds.addData("PARENT_CLASS_CODE", childs.getData(
						"PARENT_CLASS_CODE", i));
				allChilds.addData("SEQ", childs.getData("SEQ", i));
				this.getAllChildsByParent(((String) childs.getData(
						"CLASS_CODE", i)), allChilds);
			}
		}

	}

	/**
	 * add by lx 通过父节点得到子节点;
	 *
	 * @param parentClassCode
	 *            String
	 * @return TParm
	 */
	private TParm getChildsByParentNode(String parentClassCode) {
		TParm result = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ" +
								" FROM EMR_CLASS" +
								" WHERE PARENT_CLASS_CODE='"
								+ parentClassCode
								+ "' ORDER BY SEQ,CLASS_CODE"));
		return result;
	}

	/**
	 * 插入图片编辑区
	 */
	public void onInsertImageEdit() {

		if (this.getWord().getFileOpenName() == null) {
			// 请选择一个需要编辑的病历
			this.messageBox("E0111");
			return;
		}
		this.getWord().insertImage();

	}

	/**
	 * 删除图片编辑区
	 */
	public void onDeleteImageEdit() {
		if (this.getWord().getFileOpenName() == null) {
			// 请选择一个需要编辑的病历
			this.messageBox("E0111");
			return;
		}
		this.getWord().deleteImage();

	}

	/**
	 * 插入块
	 */
	public void onInsertGBlock() {
		if (this.getWord().getFileOpenName() == null) {
			// 请选择一个需要编辑的病历
			this.messageBox("E0111");
			return;
		}

		this.getWord().insertGBlock();

	}

	/**
	 * 插入线
	 */
	public void onInsertGLine() {
		if (this.getWord().getFileOpenName() == null) {
			// 请选择一个需要编辑的病历
			this.messageBox("E0111");
			return;
		}
		this.getWord().insertGLine();

	}

	/**
	 * 调整块尺寸
	 *
	 * @param index
	 *            int
	 */
	public void onSizeBlockMenu(String index) {
		if (this.getWord().getFileOpenName() == null) {
			// 请选择一个需要编辑的病历
			this.messageBox("E0111");
			return;
		}
		this.getWord().onSizeBlockMenu(StringTool.getInt(index));
	}

	/**
	 * 获取国家标准的code，如不存在返回his系统code;
	 *
	 * @param HisAttr
	 *            String P:pattern公用字典|D：dictionary单独字典
	 * @param hisTableName
	 *            String his系统表名
	 * @param hisCode
	 *            String his系统编码
	 * @return String 对应的国家标准的code
	 */
	private String getEMRCode(String HisAttr, String hisTableName,
			String hisCode) {

		String sql = "SELECT EMR_CODE FROM EMR_CODESYSTEM_D";
		sql += " WHERE HIS_ATTR='" + HisAttr + "'";
		sql += " AND HIS_TABLE_NAME='" + hisTableName + "'";
		sql += " AND HIS_CODE='" + hisCode + "'";
		TParm emrCodeParm = new TParm(getDBTool().select(sql));
		int count = emrCodeParm.getCount();
		// 有对应
		if (count > 0) {
			return emrCodeParm.getValue("EMR_CODE", 0);
		}

		return hisCode;
	}

	/**
	 * 设置隐藏元素值；
	 *
	 * @param objParameter
	 *            TParm
	 */
	private void setHiddenElements(TParm objParameter) {
		for (int i = 0; i < this.getWord().getPageManager().getMVList().size(); i++) {
			MV mv = this.getWord().getPageManager().getMVList().get(i);
			if (mv.getName().equals("UNVISITABLE_ATTR")) {
				for (int j = 0; j < mv.size(); j++) {
					DIV div = mv.get(j);
					if (div instanceof VText) {
						// this.messageBox("是传过来的值:"+((VText)div).isTextT());
						if (((VText) div).isTextT()) {
							// 设置隐藏值传入的值；
							// objParameter.setData(div.getName(), "TEXT",
							// hideElemMap.get(div.getName()));
							// System.out.println("隐藏的宏名++++"+ ( (VText)
							// div).getMicroName());
							// System.out.println("隐藏的宏名值++++"+ hideElemMap.get(
							// ( (VText) div).getMicroName()));

							objParameter.setData(((VText) div).getMicroName(),
									"TEXT", hideElemMap.get(((VText) div)
											.getMicroName()));

						} else {
							// 死值;
							objParameter.setData(div.getName(), "TEXT",
									((VText) div).getText());

						}

					}
				}

			}

		}

	}

	/**
	 * 通过宏名设置抓取框值；
	 *
	 * @param macroName
	 *            String
	 * @param value
	 *            String
	 */
	private boolean setCaptureValue(String macroName, String value) {
		boolean isSetValue = false;
		TList components = this.getWord().getPageManager().getComponentList();
		int size = components.size();
		for (int i = 0; i < size; i++) {
			EPage ePage = (EPage) components.get(i);
			// EComponent
			// this.messageBox("EPanel size"+ePage.getComponentList().size());
			for (int j = 0; j < ePage.getComponentList().size(); j++) {
				EPanel ePanel = (EPanel) ePage.getComponentList().get(j);
				if (ePanel != null) {
					for (int z = 0; z < ePanel.getBlockSize(); z++) {
						IBlock block = (IBlock) ePanel.get(z);
						// this.messageBox("type"+block.getObjectType());
						// this.messageBox("value"+block.getBlockValue());
						// 9为抓取框;
						if (block != null) {
							if (block.getObjectType() == EComponent.CAPTURE_TYPE) {
								EComponent com = block;
								ECapture capture = (ECapture) com;

								if (capture.getMicroName().equals(macroName)) {
									// this.messageBox("microName"+capture.getMicroName());
									// 是开始，则赋值;
									if (capture.getCaptureType() == 0) {
										capture.setFocusLast();
										capture.clear();
										this.getWord().pasteString(value);
										isSetValue = true;
										break;
									}
								}
							}
							//固定文本赋值
							if(block.getObjectType() == EComponent.FIXED_TYPE){
								EComponent com = block;
								EFixed efix = (EFixed) com;
								if("DAY_OPE_FLG".equals(efix.getName()) && efix.getName().equals(macroName)){
									efix.setText(value);
									isSetValue = true;
									break;
								}
							}

						}

						if (isSetValue) {
							break;
						}
					}
					if (isSetValue) {
						break;
					}

				}

			}

		}
		return isSetValue;

	}


	/**
	 * 设置住院证(暂时这样写，在宏抓取设置成活的)
	 */
	private void setAdmResv(TParm parm) {
		// 设置
		String patSource = (String) parm.getData("病患来源", 0);
		if (patSource != null && !patSource.equals("")) {
			if (patSource.equalsIgnoreCase("01")) {
				EComponent com = this.getWord().getPageManager().findObject(
						"门诊入院", EComponent.CHECK_BOX_CHOOSE_TYPE);
				ECheckBoxChoose checkbox = (ECheckBoxChoose) com;
				if (checkbox != null) {
					checkbox.setChecked(true);
				}
			} else if (patSource.equalsIgnoreCase("02")) {
				EComponent com = this.getWord().getPageManager().findObject(
						"急诊入院", EComponent.CHECK_BOX_CHOOSE_TYPE);
				ECheckBoxChoose checkbox = (ECheckBoxChoose) com;
				if (checkbox != null) {
					checkbox.setChecked(true);
				}

			} else if (patSource.equalsIgnoreCase("09")) {
				EComponent com = this.getWord().getPageManager().findObject(
						"他院转入", EComponent.CHECK_BOX_CHOOSE_TYPE);
				ECheckBoxChoose checkbox = (ECheckBoxChoose) com;
				if (checkbox != null) {
					checkbox.setChecked(true);
				}
			}
		}

		this.getWord().update();
	}

	/**
	 * 设置临床路径值;
	 */
	private void setCLPValue() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("REGION_CODE", Operator.getRegion());

		TParm result = CLPEMRTool.getInstance().getEMRManagedData(parm);
		// System.out.println("====临床路径result===="+result);
		String names[] = result.getNames();
		// this.messageBox("setCLPValue===="+names.length);

		for (int i = 0; i < names.length; i++) {
			// this.messageBox("name"+names[i]);
			// this.messageBox("value"+result.getValue(names[i]));
			EComponent com = this.getWord().getPageManager().findObject(
					names[i], EComponent.CHECK_BOX_CHOOSE_TYPE);
			ECheckBoxChoose checkbox = (ECheckBoxChoose) com;
			if (checkbox != null) {
				if (result.getValue(names[i]).equalsIgnoreCase("Y")) {
					checkbox.setChecked(true);

				}
			}
		}
		this.getWord().update();
	}

	/**
	 * 删除临时文件目录;
	 *
	 * @param filepath
	 *            String
	 * @throws IOException
	 */
	private void delDir(String filepath) throws IOException {
		File f = new File(filepath); // 定义文件路径
		if (f.exists() && f.isDirectory()) { // 判断是文件还是目录
			// 若目录下没有文件则直接删除
			if (f.listFiles().length == 0) {
				f.delete();
			} else { // 若有则把文件放进数组，并判断是否有下级目录
				File delFile[] = f.listFiles();
				int i = f.listFiles().length;
				for (int j = 0; j < i; j++) {
					if (delFile[j].isDirectory()) {
						// 递归调用del方法并取得子目录路径
						delDir(delFile[j].getAbsolutePath());
					}
					// 删除文件dddd
					delFile[j].delete();
				}
			}
		}
	}

	/**
	 * 读图片文件
	 *
	 * @param url
	 * @return
	 */
	private BufferedImage readImage(String fileName) {
		File file = new File(fileName);
		BufferedImage image = null;
		if (file != null && file.isFile() && file.exists()) {
			try {
				image = ImageIO.read(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image;
	}

	/**
	 * 删除固定文本
	 */
	public void onDelFixText() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().deleteFixed();
		} else {
			this.messageBox("请选择模版！");
		}
	}

	/**
	 * 插入图片
	 */
	public void onInsertPictureObject() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().insertPicture();
			this.setContainPic(true);
		} else {
			this.messageBox("请选择模版！");
		}
	}

	/**
	 * 插入当前时间
	 */
	public void onInsertCurrentTime() {
		// 获取时间
		Timestamp sysDate = StringTool.getTimestamp(new Date());
		String strSysDate = StringTool
				.getString(sysDate, "yyyy/MM/dd HH:mm:ss");
		// 焦点处加入时间;
		// EComponent e = word.getFocusManager().getFocus();
		this.getWord().pasteString(strSysDate);
	}

	/**
	 * 粘帖图片
	 */
	public void onPastePicture() {
		ClipboardTool tool = new ClipboardTool();
		try {
			Image img = tool.getImageFromClipboard();
			// 判断图片格式是否正确
			if (img == null || img.getWidth(null) == -1) {
				this.messageBox("剪切板中没有图片内容,请先抓取!");
				return;

			}
			this.getWord().onPaste();

		} catch (Exception ex) {
		}

	}

	/**
	 * 清空系统剪贴板
	 */
	public void onClearMenu() {
		CopyOperator.clearComList();
	}

	/**
	 * 病患信息收藏功能
	 */
	public void onSavePatInfo() {
		this.getWord().onCopy();
		// 本系统剪切板中是否存在数据；
		if (CopyOperator.getComList() == null
				|| CopyOperator.getComList().size() == 0) {
			this.messageBox("请先选择要保存的病患信息！");
			return;
		}
		TParm inParm = new TParm();
		inParm.setData("MR_NO", this.getMrNo());
		inParm.setData("PAT_NAME", this.getPatName());
		inParm.setData("OP_TYPE", "SavePatInfo");
		inParm.addListener("onReturnContent", this, "onReturnContent");

		TWindow window = (TWindow)this.openWindow("%ROOT%\\config\\emr\\EMRPatPhrase.x", inParm, true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		/*TPanel wordPanel = ((TPanel) this.getComponent("PANEL"));
        window.setX(wordPanel.getX() + 10);
        window.setY(130);*/
		/*	this.openDialog("%ROOT%\\config\\emr\\EMRPatPhrase.x", inParm, true);*/

		AnimationWindowUtils.show(window);       
		AWTUtilities.setWindowOpacity(window, 0.9f);
		window.setVisible(true);
	}

	/**
	 * 插入病患信息;
	 */
	public void onInsertPatInfo() {
		TParm inParm = new TParm();
		inParm.setData("MR_NO", this.getMrNo());
		inParm.setData("PAT_NAME", this.getPatName());
		inParm.setData("OP_TYPE", "InsertPatInfo");
		inParm.setData("TWORD", this.getWord());
		//
		//TDialog dialog=(TDialog)this.openDialog("%ROOT%\\config\\emr\\EMRPatPhrase.x", inParm, false);
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRPatPhrase.x", inParm, false);
		//
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		//
		AnimationWindowUtils.show(window);       
		AWTUtilities.setWindowOpacity(window, 0.9f);
		window.setVisible(true);
	}

	/**
	 * 判断是否修要上传
	 *
	 * @return boolean
	 */
	private boolean isUloadPic() {
		boolean isUpload = false;
		if ("NEW".equals(this.getOnlyEditType())) {
			if (this.isContainPic()) {
				isUpload = true;
				this.getWord().setFileContainPic("Y");
			} else {
				isUpload = false;
				this.getWord().setFileContainPic("N");
			}
		}

		if ("ONLYONE".equals(this.getOnlyEditType())) {
			if (this.isContainPic()
					|| (this.getWord().getFileContainPic() != null && this
					.getWord().getFileContainPic().equals("Y"))) {
				isUpload = true;
				this.getWord().setFileContainPic("Y");
			} else {
				isUpload = false;
				this.getWord().setFileContainPic("N");
			}

		}

		return isUpload;

	}

	public void setContainPic(boolean containPic) {
		this.containPic = containPic;
	}

	public boolean isContainPic() {
		return this.containPic;
	}

	/**
	 * 保存病历
	 *
	 * @param isShow
	 *            boolean 是否显示保存成功
	 * @return boolean
	 */
	private boolean onsaveEmr(boolean isShow) {
		if (this.getWord().getFileOpenName() == null) {
			// 请选择一个需要编辑的病历
			this.messageBox("E0111");
			return false;
		}
		// 2、病历新建保存权限控制：
		if ("NEW".equals(this.getOnlyEditType())) {
			if (!this.checkDrForNew()) {
				this.messageBox("E0102");
				return false;
			}
		}
		// 3、病历编辑保存权限控制：
		if ("ONLYONE".equals(this.getOnlyEditType())) {
			// 住院类型 校验三级检诊        去掉三级检诊 保存   && !this.checkDrForEdit()
			if (this.getAdmType().equals("ODI")) {
				// this.messageBox("aaaaa");
				this.messageBox("E0102");
				return false;
			}
		}

		// TODO
		// 库中应该加入显示消息字段，给用户正确的消息提示;
		TParm nullParm = GetWordValue.getInstance().checkValue(this.getWord());
		// 假如有值；
		// 查找对应的文字，报错误；
		/**
		 * Vector nullDataGroup = (Vector) nullParm.getData("NULL_GROUP_NAME");
		 * Vector nullDataCode = (Vector) nullParm.getData("NULL_NAME"); String
		 * strNull = ""; if (nullDataGroup != null && nullDataGroup.size() > 0)
		 * { for (int i = 0; i < nullDataGroup.size(); i++) { strNull +=
		 * this.getShowMessage( (String) nullDataGroup.get(i),(String)
		 * nullDataCode.get(i)) + "\r\n"; } this.messageBox(strNull + "必须填写！");
		 * return false;
		 *
		 * }
		 **/
		// 加入超出范围
		Vector numDataGroup = (Vector) nullParm.getData("RANG_GROUP_NAME");
		Vector numDataCode = (Vector) nullParm.getData("RANG_NAME");
		String strNum = "";
		if (numDataGroup != null && numDataGroup.size() > 0) {
			for (int i = 0; i < numDataGroup.size(); i++) {
				strNum += this.getShowMessage((String) numDataGroup.get(i),
						(String) numDataCode.get(i))
						+ "\r\n";
			}
			this.messageBox(strNum + "已超出范围！");
			return false;

		}

		// this.messageBox("文件名" + this.getWord().getFileName());
		if (this.isUloadPic()) {
			boolean isUploadSuccess = uploadPictureToFileServer();
			if (!isUploadSuccess) {
				// 保存失败
				this.messageBox("图片上传失败.");
				return false;
			}
		}
		//$$=== add by lx 2012/09/11 解决取seq冲突问题 ===$$//
		//System.out.println("==========OnlyEditType========="+this.getOnlyEditType());

		if ("NEW".equals(this.getOnlyEditType())) {
			this.setEmrChildParm(this.getFileServerEmrName());
		}

		// 另存到用户文件
		TParm asSaveParm = this.getEmrChildParm();
		
		//System.out.println("================asSaveParm=========="+asSaveParm);
		// 日期
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy年MM月dd日 HH时mm分ss秒");
		if ("NEW".equals(this.getOnlyEditType())) {
			this.getWord().setMessageBoxSwitch(false);
			// 作者(第一次保存)
			// if(this.word.getFileAuthor()==null||this.word.getFileAuthor().length()==0||"admin".equals(this.word.getFileAuthor()))
			this.getWord().setFileAuthor(Operator.getID());
			// 公司
			this.getWord().setFileCo("JAVAHIS");
			// 标题
			this.getWord().setFileTitle(asSaveParm.getValue("DESIGN_NAME"));
			// 备注
			this.getWord().setFileRemark(
					asSaveParm.getValue("CLASS_CODE") + "|"
							+ asSaveParm.getValue("FILE_PATH") + "|"
							+ asSaveParm.getValue("FILE_NAME"));
			// 创建时间
			this.getWord().setFileCreateDate(dateStr);
			// 最后修改人
			this.getWord().setFileLastEditUser(Operator.getID());
			// 最后修改日期
			this.getWord().setFileLastEditDate(dateStr);
			// 最后修改IP
			this.getWord().setFileLastEditIP(Operator.getIP());
			/*System.out.println("==save filePath=="
					+ asSaveParm.getValue("FILE_PATH"));
			System.out.println("==save fileName=="
					+ asSaveParm.getValue("FILE_NAME"));*/
			// 另存为
			boolean success = this.getWord().onSaveAs(
					asSaveParm.getValue("FILE_PATH"),
					asSaveParm.getValue("FILE_NAME"), 3);
			//是追加日常病历
			if(this.isApplend()){
				this.setStrRefFileName(asSaveParm.getValue("FILE_NAME"));
			}
			//
			if (diseBasicData != null) {//add by wanglong 20121128
				EMRCreateXMLToolForSD.getInstance().createXML(
						asSaveParm.getValue("FILE_PATH"),
						asSaveParm.getValue("FILE_NAME"), "EmrData",
						this.getWord());
			} else//add by wanglong 20121128
				EMRCreateXMLTool.getInstance()
				.createXML(asSaveParm.getValue("FILE_PATH"),
						asSaveParm.getValue("FILE_NAME"), "EmrData",
						this.getWord());
			// $$===========生成检查申请单html文件Start===========$$//
			// 假如存在检查申请号,则生成html
			if (this.getMedApplyNo() != null
					&& !this.getMedApplyNo().equals("")) {
				EMRCreateHTMLTool.getInstance().createHTML(
						asSaveParm.getValue("FILE_PATH"),
						asSaveParm.getValue("FILE_NAME"), this.getMedApplyNo(),
						"EmrData");
			}
			// $$===========生成检查申请单html文件End===========$$//
			if (!success) {
				// 文件服务器异常
				this.messageBox("E0103");
				this.getWord().setMessageBoxSwitch(true);
				// 不可编辑
				this.getWord().setCanEdit(false);
				setTMenuItem(false);
				return false;
			}
			this.getWord().setMessageBoxSwitch(true);
			// 不可编辑
			this.getWord().setCanEdit(false);
			setTMenuItem(false);
			
			// 插入数据库数据
			if (saveEmrFile(asSaveParm)) {
				//TParm returnParm=new TParm();//add by wanglong 20121115
				returnParm.addData("FILE_PATH", asSaveParm.getValue("FILE_PATH"));//add by wanglong 20121115
				returnParm.addData("FILE_NAME", asSaveParm.getValue("FILE_NAME"));//add by wanglong 20121115
				//this.setReturnValue(returnParm);//add by wanglong 20121115
				if (isShow) {
					// 保存成功
					this.messageBox("P0001");
				}
				// 启动自动保存定时器
				// this.schedule();
				//this.loadTree();
				//新增直接展开，刷新分类树
				TTreeNode nodeChlid = new TTreeNode();
				nodeChlid
				.setID(asSaveParm.getValue("FILE_NAME"));
				nodeChlid.setText(asSaveParm
						.getValue("DESIGN_NAME"));
				nodeChlid.setType("4");
				nodeChlid.setGroup(asSaveParm
						.getValue("CLASS_CODE"));
				nodeChlid.setValue(asSaveParm
						.getValue("SUBCLASS_CODE"));
				nodeChlid.setData(asSaveParm);				
				//父节点
				TTreeNode faterNode=treeRoot.findNodeForID(
						asSaveParm.getValue("CLASS_CODE"));

				faterNode.add(nodeChlid);
				this.getTTree(TREE_NAME).update();
				//节点是否是展开的
				this.getTTree(TREE_NAME).expandRow(faterNode);			
				//

				//
				Object obj = this.getParameter();
				TParm emrFileDataParm = this.getEmrChildParm();
				if (obj != null
						&& "SQD".equals(emrFileDataParm.getValue("TYPEEMR"))) {
					emrFileDataParm = (TParm) ((TParm) obj)
							.getData("EMR_FILE_DATA");
					emrFileDataParm.setData("FILE_SEQ", asSaveParm
							.getValue("FILE_SEQ"));
					emrFileDataParm.setData("CASE_NO", this.getCaseNo());
					emrFileDataParm.setData("ADM_TYPE", this.getAdmType());
				}
				// 申请单调用
				if ("SQD".equals(emrFileDataParm.getValue("TYPEEMR"))) {

					((TParm) obj).runListener("EMR_SAVE_LISTENER",
							emrFileDataParm);
				} else {
					((TParm) obj).runListener("EMR_SAVE_LISTENER", new TParm());
				}
				this.setOnlyEditType("ONLYONE");
				// 非三级检诊医师及值班医师：电子病历操作日志/EMR_OPTLOG/新建
				TParm result = OptLogTool.getInstance().writeOptLog(
						this.getParameter(), "C", emrFileDataParm);
				// 设置允许打印标识和允许修改标识
				String caseNo = ((TParm) this.getParameter())
						.getValue("CASE_NO");
				String fileSeq = emrFileDataParm.getValue("FILE_SEQ");
				if (!this.checkDrForDel()) {
					setCanPrintFlgAndModifyFlg("", "", false);
				} else {
					setCanPrintFlgAndModifyFlg(caseNo, fileSeq, true);
				}

				// add by wangb 2015/10/20 发送检查申请单HTML START
				this.sendRisHtml();
				// add by wangb 2015/10/20 发送检查申请单HTML END
				//
				//$$同步数据库方法   EDIT
				doSaveEvalutionRec("NEW");

				// add by wangb 2015/12/21 介入安全核查单保存发送手术状态 START
				if (StringUtils.equals("Y", ((TParm) obj)
						.getValue("OPE_MSG_SEND_FLG"))) {
					((TParm) obj).runListener("OPE_LISTENER", new TParm());
				}
				// add by wangb 2015/12/21 介入安全核查单保存发送手术状态 END

				return true;
			} else {
				if (isShow) {
					// 保存失败
					this.messageBox("E0001");
				}
				return false;
			}



		}
		if ("ONLYONE".equals(this.getOnlyEditType())) {
			// 设置提示不可用
			this.getWord().setMessageBoxSwitch(false);
			// 最后修改人
			this.getWord().setFileLastEditUser(Operator.getID());
			// 最后修改日期
			this.getWord().setFileLastEditDate(dateStr);
			// 最后修改IP
			this.getWord().setFileLastEditIP(Operator.getIP());

			// 保存
			boolean success = this.getWord().onSaveAs(
					asSaveParm.getValue("FILE_PATH"),
					asSaveParm.getValue("FILE_NAME"), 3);
			if (diseBasicData != null) {//add by wanglong 20121128
				EMRCreateXMLToolForSD.getInstance().createXML(
						asSaveParm.getValue("FILE_PATH"),
						asSaveParm.getValue("FILE_NAME"), "EmrData",
						this.getWord());
			} else//add by wanglong 20121128
				EMRCreateXMLTool.getInstance()
				.createXML(asSaveParm.getValue("FILE_PATH"),
						asSaveParm.getValue("FILE_NAME"), "EmrData",
						this.getWord());
			// $$===========生成检查申请单html文件Start===========$$//
			// 假如存在检查申请号,则生成html
			if (this.getMedApplyNo() != null
					&& !this.getMedApplyNo().equals("")) {
				EMRCreateHTMLTool.getInstance().createHTML(
						asSaveParm.getValue("FILE_PATH"),
						asSaveParm.getValue("FILE_NAME"), this.getMedApplyNo(),
						"EmrData");
			}
			// $$===========生成检查申请单html文件End===========$$//
			if (!success) {
				// 文件服务器异常
				this.messageBox("E0103");
				// 设置提示框为可提示
				this.getWord().setMessageBoxSwitch(true);
				// 不可编辑
				this.getWord().setCanEdit(false);
				setTMenuItem(false);
				return false;
			}
			this.getWord().setMessageBoxSwitch(true);
			// 设置提示框为可提示
			//this.getWord().setMessageBoxSwitch(true);
			// 不可编辑
			this.getWord().setCanEdit(false);
			setTMenuItem(false);

			// 组装操作数据TParm（编辑保存）
			this.setOptDataParm(asSaveParm, 1);
			// 保存数据
			if (saveEmrFile(asSaveParm)) {
				//TParm returnParm=new TParm();//add by wanglong 20121115
				returnParm.addData("FILE_PATH", asSaveParm.getValue("FILE_PATH"));//add by wanglong 20121115
				returnParm.addData("FILE_NAME", asSaveParm.getValue("FILE_NAME"));//add by wanglong 20121115
				//this.setReturnValue(returnParm);//add by wanglong 20121115
				if (isShow) {
					// 保存成功
					this.messageBox("P0001");
				}
				Object obj = this.getParameter();
				((TParm) obj).runListener("EMR_SAVE_LISTENER", new TParm());

				//this.loadTree();
				// 非三级检诊医师及值班医师：电子病历操作日志/EMR_OPTLOG/修改
				TParm result = OptLogTool.getInstance().writeOptLog(
						this.getParameter(), "M", asSaveParm);

				// add by wangb 2015/10/20 发送检查申请单HTML START
				this.sendRisHtml();
				// add by wangb 2015/10/20 发送检查申请单HTML END
				doSaveEvalutionRec("EDIT");
				return true;
			} else {
				if (isShow) {
					// 保存失败
					this.messageBox("E0001");
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * 设置菜单项目权限
	 *
	 * @param flg
	 */
	private void setTMenuItem(boolean flg) {
		if ("Y".equals(refFlg)) {
			return;
		}
		this.getTMenuItem("InsertTable").setEnabled(flg);
		this.getTMenuItem("DelTable").setEnabled(flg);
		this.getTMenuItem("InsertTableRow").setEnabled(flg);
		this.getTMenuItem("AddTableRow").setEnabled(flg);
		this.getTMenuItem("DelTableRow").setEnabled(flg);
		this.getTMenuItem("InsertPY").setEnabled(flg);
		this.getTMenuItem("InsertLCSJ").setEnabled(flg);

		this.getTMenuItem("InsertPictureObject").setEnabled(flg);
		this.getTMenuItem("PastePictureObject").setEnabled(flg);
		this.getTMenuItem("InsertTemplatePY").setEnabled(flg);
		this.getTMenuItem("InsertPatInfo").setEnabled(flg);
		this.getTMenuItem("InsertCurrentTime").setEnabled(flg);

		this.getTMenuItem("ClearMenu").setEnabled(flg);
		this.getTMenuItem("delFixText").setEnabled(flg);

	}

	/**
	 * 是否存在转科
	 *
	 * @param caseNo
	 * @return
	 */
	private boolean isAdmChg(String caseNo) {
		boolean flg = false;
		// 存在入科操作,说明INDP;
		String sql = "SELECT COUNT(*) FROM ADM_CHG";
		sql += " WHERE PSF_KIND='INDP' AND CASE_NO='" + caseNo + "'";
		sql += " AND CANCEL_FLG='N'";
		//System.out.println("==sql----=="+sql);
		TParm parm = new TParm(this.getDBTool().select(sql));
		//System.out.println("==parm=="+parm);
		//System.out.println("===count==="+parm.getCount());
		// >1,说明是转过科
		if (parm.getCount() > 1) {
			flg = true;
		}

		return flg;
	}

	/**
	 * 获取病患所在科室时的对应医师(经治,主治,科主任)
	 *
	 * @param caseNo
	 * @param dept
	 * @param drType
	 *            (VS_DR_CODE,ATTEND_DR_CODE,DIRECTOR_DR_CODE)
	 * @return 医生代码
	 */
	private String getDr(String caseNo, String dept, String drType) {
		String sql = "SELECT " + drType + " FROM ADM_CHG";
		sql += " WHERE CASE_NO='" + caseNo + "' AND DEPT_CODE='" + dept
				+ "' AND " + drType + " IS NOT NULL";
		sql += " ORDER BY SEQ_NO DESC";
		// System.out.println("====sql===="+sql);
		TParm parm = new TParm(this.getDBTool().select(sql));
		// 该科室存在 三级检医师
		if (parm.getCount() > 0) {
			for (int i = 0; i < parm.getCount(); i++) {
				if (Operator.getID().equals(parm.getValue(drType, i))) {
					return parm.getValue(drType, i);
				}
			}
		}
		return "";
	}

	/**
	 * 病历是否已经提交
	 * @return
	 */
	private boolean getSubmitFLG(){
		String sql="SELECT CHECK_FLG";
		sql+=" FROM MRO_MRV_TECH";
		sql+=" WHERE CASE_NO='"+this.getCaseNo()+"'";
		TParm parm = new TParm(this.getDBTool().select(sql));
		if (parm.getCount() > 0) {
			if( parm.getValue("CHECK_FLG", 0).equals("1")){
				this.messageBox("已提交病历，不可修改!");
				return true;
			}else if(parm.getValue("CHECK_FLG", 0).equals("2")){
				this.messageBox("已审核病历，不可修改!");
				return true;
			}else if(parm.getValue("CHECK_FLG", 0).equals("3")){
				this.messageBox("已归档病历，不可修改!");
				return true;
			}
		}

		return false;
	}

	/**
	 * 是否是知识库
	 * @param classcode
	 * @return
	 */
	private boolean isKnowLedgeStore(String classcode){
		if(classcode.startsWith(KNOWLEDGE_STORE_PREFIX)){
			return true;
		}
		return false;
	}

	/**
	 * 设置单病种基本信息
	 */
	public void setSingleDiseBasicData(){//add by wanglong 20121115
		if (diseBasicData != null) {
			word.setMicroField("PAT_NAME",diseBasicData.getValue("PAT_NAME"));//姓名
			ESingleChoose esc=(ESingleChoose)word.findObject("HR02.02.001", EComponent.SINGLE_CHOOSE_TYPE);
			String sexCode=diseBasicData.getValue("SEX_CODE");// 性别代码
			if (sexCode.equals("1")) {
				esc.setText("男");
			} else if (sexCode.equals("2")) {
				esc.setText("女");
			} else if (sexCode.equals("9")) {
				esc.setText("未说明");
			} else {
				esc.setText("未知");
			}

			ENumberChoose en=(ENumberChoose)word.findObject("HR02.03.001", EComponent.NUMBER_CHOOSE_TYPE);
			en.setText(diseBasicData.getValue("AGE"));//年龄
			//System.out.println("==================入院日期======================"+diseBasicData.getValue("IN_DATE"));/////////////////////
			word.setMicroField("IN_DATE",diseBasicData.getValue("IN_DATE").split("\\.")[0]);//入院日期
			word.setMicroField("OUT_DATE",diseBasicData.getValue("OUT_DATE").split("\\.")[0]);//出院日期
			word.setMicroField("STAY_DAYS",diseBasicData.getValue("STAY_DAYS"));//住院天数
			word.setMicroField("ICD_CODE",diseBasicData.getValue("ICD_CODE"));//诊断代码
			word.setMicroField("ICD_CHN_DESC",diseBasicData.getValue("ICD_CHN_DESC"));//诊断名称
			word.setMicroField("TBYS",diseBasicData.getValue("TBYS_CHN"));//填表医师
			word.setMicroField("出生日期",diseBasicData.getValue("BIRTH_DATE"));//出生日期
			word.setMicroField("病案号",diseBasicData.getValue("MR_NO"));//病案号

			ECheckBoxChoose WYZR_Y = (ECheckBoxChoose) word.findObject("WYZR_Y", EComponent.CHECK_BOX_CHOOSE_TYPE);
			ECheckBoxChoose WYZR_N = (ECheckBoxChoose) word.findObject("WYZR_N", EComponent.CHECK_BOX_CHOOSE_TYPE);
			if (diseBasicData.getValue("ADM_SOURCE").equals("09")) {
				WYZR_Y.setChecked(true);// 外院转入_是（选择框）
				WYZR_N.setChecked(false);// 外院转入_否（选择框）
			} else {
				WYZR_Y.setChecked(false);// 外院转入_是（选择框）
				WYZR_N.setChecked(true);// 外院转入_否（选择框）
			}
			ECheckBoxChoose WXPF_Y = (ECheckBoxChoose) word.findObject("WXPF_Y", EComponent.CHECK_BOX_CHOOSE_TYPE);
			ECheckBoxChoose WXPF_N = (ECheckBoxChoose) word.findObject("WXPF_N", EComponent.CHECK_BOX_CHOOSE_TYPE);
			if (diseBasicData.getValue("PATIENT_CONDITION").equals("1")) {
				WXPF_Y.setChecked(true);// 危险评分_是（选择框）
				WXPF_N.setChecked(false);// 危险评分_否（选择框）
			} else {
				WXPF_Y.setChecked(false);// 危险评分_是（选择框）
				WXPF_N.setChecked(true);// 危险评分_否（选择框）
			}

			//ECheckBoxChoose ST_Y=(ECheckBoxChoose)word.findObject("ST_Y", EComponent.CHECK_BOX_CHOOSE_TYPE);//ST（选择框）
			//ST_Y.setChecked(true);//ST（选择框）

			TParm allParm = new TParm();
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT",diseBasicData.getValue("MR_NO"));//页眉-病案号
			//allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT",diseBasicData.getValue("IPD_NO"));//页眉-住院号
			allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT",diseBasicData.getValue("PAT_NAME"));
			//
			allParm.addListener("onDoubleClicked", this,"onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,"onMouseRightPressed");
			word.setWordParameter(allParm);

		}
	}

	/**
	 * 设置手术信息
	 */
	public void setOPEData() {// add by wanglong 20130514
		if (opeBasicData != null) {
			GetWordValue.getInstance().setWordValueByParm(word, opeBasicData);
		}
	}

	/**
	 * 计算表达式
	 */
	public void onCalculateExpression(){

		if (this.getWord().getFileOpenName() != null) {
			word.onCalculateExpression();
		}
		else {
			this.messageBox("请选择病历！");
		}
	}

	/**
	 * 发送检查申请单HTML
	 * 
	 * @author wangb
	 */
	private void sendRisHtml() {
		if (StringUtils.isNotEmpty(this.getMedApplyNo())) {
			TParm sendHtmlParm = new TParm();
			sendHtmlParm.setData("CASE_NO", this.getCaseNo());
			sendHtmlParm.setData("MR_NO", this.getMrNo());
			sendHtmlParm.setData("APPLICATION_NO", this.getMedApplyNo());

			// 查询检验检查医嘱信息
			TParm result = MEDApplyTool.getInstance().queryMedApplyInfo(sendHtmlParm);

			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
				+ result.getErrName());
				return;
			}

			sendHtmlParm.setData("ORDER_CAT1_CODE", result.getValue("ORDER_CAT1_CODE", 0));

			// 发送申请单HTML文件
			Hl7Communications.getInstance().CreatRisHtml(sendHtmlParm);
		}
	}

	/**
	 * “子模板”单击事件
	 */
	public void onSubTemplate() {
		if (!word.canEdit()) {
			messageBox("先选择病例模版!");
			return;
		} else {
			openSubTemplate();
		}
	}

	/**
	 * 打开子模板选择窗口
	 */
	public void openSubTemplate() {
		TParm parm = new TParm();
		parm.addListener("onReturnTemplateContent", this, "onReturnTemplateContent");
		parm.setData("TWORD", this.getWord());
		TWindow window = (TWindow) openWindow("%ROOT%\\config\\emr\\EMRSubTemplate.x", parm, true);
		/* TPanel wordPanel = ((TPanel) this.getComponent("TREEPANEL"));
        window.setX(wordPanel.getX() + 12);
        window.setY(132);
        window.setVisible(true);*/
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(131);
		//
		AnimationWindowUtils.show(window);       
		AWTUtilities.setWindowOpacity(window, 0.9f);
		window.setVisible(true);

	}

	/**
	 * 插入上下标功能
	 */
	public void onInsertMarkText() {
		//
		if (this.getWord().getFileOpenName() != null) {
			word.insertFixed();
			word.onOpenMarkProperty();
			//
		} else {
			this.messageBox("请选择病历！");
		}
	}

	/**
	 * 上下标文本属性
	 */
	public void onMarkTextProperty() {
		if (this.getWord().getFileOpenName() != null) {
			word.onOpenMarkProperty();
		}
		else {
			this.messageBox("请选择病历！");
		}
	}
	//

	/**
	 * “特殊字符”单击事件
	 */
	public void onSpecialChars() {
		if (!word.canEdit()) {
			messageBox("先选择病例模版!");
			return;
		} 
		TParm parm = new TParm();
		parm.addListener("onReturnContent", this, "onReturnContent");
		TWindow window = (TWindow)openWindow("%ROOT%\\config\\emr\\EMRSpecialChars.x", parm, true);// wanglong add 20140908

		/*TPanel wordPanel = ((TPanel) this.getComponent("PANEL"));
        window.setX(wordPanel.getX() + 10);
        window.setY(130);*/
		//
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		//
		AnimationWindowUtils.show(window);
		AWTUtilities.setWindowOpacity(window, 0.9f);
		window.setVisible(true);
	}

	/**
	 * “卫生部分类”单击事件
	 */
	public void onNationStandard() {
		TREE_TYPE = TREE_TYPE_1;
		this.loadTree();
	}

	/**
	 * “医疗过程”单击事件
	 */
	public void onCustomSort1() {
		TREE_TYPE = TREE_TYPE_2;
		this.loadTreeNew();
	}

	/**
	 * 载入用户自定义的其他树
	 */
	private void loadTreeNew() {

		boolean queryFlg = false;
		// 住院
		if (this.getSystemType().equals("ODI")) {
			this.setValue("IPD_NO", this.getIpdNo());
			isCheckUserDr();
		}
		// 门诊
		if (this.getSystemType().equals("ODO")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			if (!this.getType().equals("O")) {
				queryFlg = true;
			}
		}
		// 急诊
		if (this.getSystemType().equals("EMG")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			if (!this.getType().equals("E")) {
				queryFlg = true;
			}
		}
		// 健康检查
		if (this.getSystemType().equals("HRM")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			if (!this.getType().equals("H")) {
				queryFlg = true;
			}
		}
		// 门诊护士站
		if (this.getSystemType().equals("ONW")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			if (!this.getType().equals("O")) {
				queryFlg = true;
			}
		}
		// 住院护士站
		if (this.getSystemType().equals("INW")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			if (!this.getType().equals("I")) {
				queryFlg = true;
			}
		}
		// 感染控制
		if (this.getSystemType().equals("INF")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			if (!this.getType().equals("F")) {
				queryFlg = true;
			}
		}
		// 手术
		if (this.getSystemType().equals("OPE")) {// add by wanglong 20121220
			if (!this.getType().equals("F")) {
				queryFlg = true;
			}
		}
		// 单病种
		if (this.getSystemType().equals("SD")) {// add by wanglong 20121120
			if (!this.getType().equals("F")) {
				queryFlg = true;
			}
		}

		TParm rootParm = this.getTreeNewRoot(); // 取根节点数据
		treeRoot.setText(rootParm.getValue("CLASS_DESC", 0));
		treeRoot.setType("Root");// 节点类型
		treeRoot.setID(rootParm.getValue("CLASS_CODE", 0));
		treeRoot.removeAllChildren(); // 清理掉所有叶子节点
		TParm childrenParm = this.getTreeNewChildren(); // 得到Tree的基础数据
		// System.out.println("查询出的树的数据:" + children);
		// 开始初始化树的数据
		for (int i = 0; i < childrenParm.getCount(); i++) {
			TParm childParm = childrenParm.getRow(i);
			this.putNodeInTree(childParm, treeRoot);
		}
		//
		this.visitAllNodes(treeRoot,queryFlg);
		getTTree(TREE_NAME).update();
	}

	/**
	 * 取得用户自定义树的根节点数据
	 * @return
	 */
	private TParm getTreeNewRoot() {
		String sql = "";
		if (TREE_TYPE.equals(TREE_TYPE_2)) {
			sql =
					"SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ "
							+ " FROM EMR_CLASS_OTHER WHERE EMR_VIEW = '2' AND PARENT_CLASS_CODE IS NULL";
		} else if (TREE_TYPE.equals(TREE_TYPE_3)) {
			sql =
					"SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ "
							+ " FROM EMR_CLASS_OTHER WHERE EMR_VIEW = '3' AND PARENT_CLASS_CODE IS NULL";
		}
		TParm result = new TParm(this.getDBTool().select(sql));
		return result;
	}

	/**
	 * 取得用户自定义树的子节点数据
	 * @return
	 */
	private TParm getTreeNewChildren() {
		return this.getTreeNewChildren("");
	}

	/**
	 * 取得用户自定义树的子节点数据
	 * @return
	 */
	private TParm getTreeNewChildren(String classCode) {
		String sql = "";
		if (TREE_TYPE.equals(TREE_TYPE_2)) {
			sql =
					"SELECT CLASS_CODE, CLASS_DESC, ENG_DESC, PY1, PY2, DESCRIPTION, "
							+ "SEQ, CLASS_STYLE, LEAF_FLG, PARENT_CLASS_CODE "
							+ " FROM EMR_CLASS_OTHER WHERE EMR_VIEW = '2' # ORDER BY LEAF_FLG,SEQ";
		} else if (TREE_TYPE.equals(TREE_TYPE_3)) {
			sql =
					"SELECT CLASS_CODE, CLASS_DESC, ENG_DESC, PY1, PY2, DESCRIPTION, "
							+ "SEQ, CLASS_STYLE, LEAF_FLG, PARENT_CLASS_CODE "
							+ " FROM EMR_CLASS_OTHER WHERE EMR_VIEW = '3' # ORDER BY LEAF_FLG,SEQ";
		}
		if (!StringUtil.isNullString(classCode)) {
			sql = sql.replaceFirst("#", " AND CLASS_CODE = '@' ".replaceFirst("@", classCode));
		} else {
			sql = sql.replaceFirst("#", "");
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * 将子节点加入到树中
	 * @param childParm
	 * @param root
	 */
	private void putNodeInTree(TParm childParm, TTreeNode root) {
		String noteType = childParm.getValue("CLASS_STYLE"); // 节点类型
		if (childParm.getValue("LEAF_FLG").equals("Y")) {
			noteType = "3";
		}
		TTreeNode node = new TTreeNode(childParm.getValue("CLASS_DESC"), noteType);
		node.setID(childParm.getValue("CLASS_CODE"));
		node.setGroup(childParm.getValue("CLASS_CODE"));
		//        node.setData(childParm);
		String parentID = childParm.getValue("PARENT_CLASS_CODE");
		// System.out.println("parentID-------------:" + parentID);
		if (root.findNodeForID(childParm.getValue("CLASS_CODE")) != null) {
			// System.out.println("已经含有此节点不用执行添加操作");
		} else if (root.findNodeForID(parentID) != null) {
			root.findNodeForID(parentID).add(node);
			//            if (root.findNodeForID(parentID) != root) {
			//                root.findNodeForID(parentID).setType("2");
			//            }
		} else {
			TParm parentParm = this.getTreeNewChildren(parentID);
			// System.out.println("查询出的父节点:" + resultParm);
			if (parentParm.getCount() <= 0) {
				root.add(node);
			} else {
				this.putNodeInTree(parentParm.getRow(0), root);
				root.findNodeForID(parentID).add(node);
			}
		}
	}

	/**
	 * 
	 * @param node
	 * @param queryFlg
	 */
	public void visitAllNodes(TTreeNode node, boolean queryFlg) {
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TTreeNode nowNode = (TTreeNode) e.nextElement();
				if (nowNode.getType().equals("3")) {
					this.getTreeNewFileIndex(nowNode, queryFlg);
				} else visitAllNodes(nowNode, queryFlg);
			}
		}
	}

	/**
	 * 叶节点的加入子文件
	 * @param leafNode
	 * @param queryFlg
	 */
	private void getTreeNewFileIndex(TTreeNode leafNode, boolean queryFlg) {
		TParm childParm = new TParm();
		// 判断是否是临床知识库，
		// 假如是则,加入对应的对应的知识库文件
		if (isKnowLedgeStore(leafNode.getID())) {
			childParm = getChildNodeDataByKnw(leafNode.getID());
		} else {
			// 查询子文件
			childParm = this.getRootChildNodeData(leafNode.getID(), queryFlg);
		}
		int rowCldCount = childParm.getCount();
		for (int z = 0; z < rowCldCount; z++) {
			TParm chlidParmTemp = childParm.getRow(z);
			TTreeNode nodeChlid = new TTreeNode();
			nodeChlid.setID(chlidParmTemp.getValue("FILE_NAME"));
			nodeChlid.setText(chlidParmTemp.getValue("DESIGN_NAME"));
			nodeChlid.setType("4");
			nodeChlid.setGroup(chlidParmTemp.getValue("CLASS_CODE"));
			nodeChlid.setValue(chlidParmTemp.getValue("SUBCLASS_CODE"));
			nodeChlid.setData(chlidParmTemp);
			leafNode.add(nodeChlid);
		}
		if (rowCldCount > 0) {
			leafNode.setText("(已完成)" + leafNode.getText());
		} else {
			leafNode.setText("(未完成)" + leafNode.getText());
		}
	}

	/**
	 * 
	 * 是否已经签名
	 * @return
	 */
	private boolean isSign(){
		boolean flg=false;   	
		//同名情况    2个相同签名
		ESign  sign1=(ESign)this.getWord().findObject(Operator.getID(), EComponent.SIGN_TYPE);
		ESign  sign2=(ESign)this.getWord().findObject(Operator.getID()+"_1", EComponent.SIGN_TYPE);
		if(sign1!=null){
			flg=true;
		}
		//
		//
		if(sign2!=null){
			flg=true;
		}
		return flg;
	}

	/**
	 * 
	 * @return
	 */
	private List<ESign> getSign(){
		List<ESign> signList=new ArrayList<ESign>();
		//
		ESign  sign1=(ESign)this.getWord().findObject(Operator.getID(), EComponent.SIGN_TYPE);
		ESign  sign2=(ESign)this.getWord().findObject(Operator.getID()+"_1", EComponent.SIGN_TYPE);
		//
		if(sign1!=null){
			signList.add(sign1);
		}
		//
		if(sign2!=null){
			signList.add(sign2);
		}
		//
		return signList;
	}
	/**
	 * 获得日志名(病程记录使用)
	 * @param s
	 * @return
	 */
	private String getLogName(String s) {
		Date d = null;
		long ltime=0;
		// String s="2014-04-30 10:46:42.0";

		try {
			d = sdf.parse(s);
			ltime=d.getTime();
			//
			this.setlRecordTime(ltime);
			// System.out.println("---time long---"+d.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return String.valueOf(ltime);
	}

	/**
	 * 加载日常病程插入点
	 * @param thisWord
	 * @return
	 */
	private List<EFixed> getAllInsertfixed(TWord thisWord) {
		List<EFixed>   insertEfixed=new ArrayList<EFixed>();
		TList components = thisWord.getPageManager().getComponentList();
		int size = components.size();
		//System.out.println("----size-----" + size);
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				EPage ePage = (EPage) components.get(i);
				for (int j = 0; j <ePage.getComponentList().size() ; j++) {
					EPanel ePanel = (EPanel) ePage.getComponentList().get(j);
					if (ePanel != null) {
						for (int z =0; z <ePanel.getBlockSize() ; z++) {
							IBlock block = (IBlock) ePanel.get(z);
							if (block != null) {
								//抓取框  汇总
								/*								if (block.getObjectType() == EComponent.CAPTURE_TYPE) {
									ECapture capture = (ECapture) block;
									if (capture.getCaptureType() == 0) {
										// 记录抓取框名子的列表
										// System.out.println("====capture name======="+capture.getName());
										//this.messageBox("=======capture======="+capture.getName());
										captureNamesList.add(capture.getName());
									}
								}*/
								//插入点汇总
								if (block.getObjectType() == EComponent.FIXED_TYPE) {
									EFixed efix = (EFixed) block;
									// 假如固定文本是数字 固定文本修改为插入点
									// this.messageBox("==========="+efix.isInsert());
									if (efix.isInsert()) {
										insertEfixed.add(efix);
									}
								}
							}
						}					
					}
				}
			}
		}
		return insertEfixed;
	}

	/**
	 * 结构化病历界面双击事件,调用日常病程编辑界面.
	 * 
	 * @param pageIndex
	 *            int
	 * @param x
	 *            int
	 * @param y
	 *            int
	 */
	public void onDoubleClicked(int pageIndex, int x, int y) {

		// 是过敏史控件 双击，弹出过敏史控件
		if (this.getWord().focusInCaptue(ALLERGY_KEY)) {
			onAllergy();
			return;
		}
		//
		//=======add by yyn 2017/11/10增加对风险类别控件的监听 start=======//
		if (this.getWord().focusInCaptue("RCat1")) {
			rcat("RCat1");
			return;
		}
		if (this.getWord().focusInCaptue("RCat2")) {
			rcat("RCat2");
			return;
		}
		if (this.getWord().focusInCaptue("RCat3")) {
			rcat("RCat3");
			return;
		}
		if (this.getWord().focusInCaptue("RCat4")) {
			rcat("RCat4");
			return;
		}
		if (this.getWord().focusInCaptue("RCat5")) {
			rcat("RCat5");
			return;
		}
		if (this.getWord().focusInCaptue("RCat6")) {
			rcat("RCat6");
			return;
		}
		if (this.getWord().focusInCaptue("STAN9")) {//非心脏畸形类 标识
			rcat("STAN9");
			return;
		}

		//=======add by yyn 2017/11/10增加对风险类别及非心脏畸形控件的监听 end=======//
		popupEmrEditWindw(true,false);
	}

	/**
	 * 弹出日常病程文件编辑器
	 * 
	 * @param isShow
	 *            是否显示
	 * @param isAutoSign
	 *            是否自动签名;
	 */
	private void popupEmrEditWindw(boolean isShow, boolean isAutoSign) {

		//		System.out.println("SEALED_STATUS======="+emrChildParm.getValue("SEALED_STATUS"));

		if (emrChildParm.getValue("CLASS_STYLE").equals("4")) {
			//this.messageBox("----11111----");
			// 假如是引用文件模版，双击处理
			String captureName = getInCaptureName();
			// this.messageBox("==captureName=="+captureName);
			if (captureName != null && !captureName.equals("")) {
				ECapture capture = this.getWord().findCapture(captureName);
				String refFileName = capture.getRefFileName();
				// this.messageBox("refFileName==="+refFileName);
				// if (StringUtil.isNullString(str)) {
				// return;
				// }
				// 打开日常病程编辑界面;
				TParm inParm = new TParm();
				inParm.setData("CAPTURE_NAME", captureName);
				inParm.setData("CASENO", this.getCaseNo());
				inParm.setData("MRNO", this.getMrNo());
				inParm.setData("IPDNO", this.getIpdNo());
				inParm.setData("ADM_YEAR", this.getAdmYear());
				inParm.setData("ADM_MOUTH", this.getAdmMouth());
				inParm.setData("PAT_NAME", this.getPatName());
				inParm.setData("DEPT_CODE", this.getDeptCode());
				// 假如引用文件属性存在, 进入编辑模式
				if (refFileName != null && !refFileName.equals("")) {
					inParm.setData("MODE", "EDIT");
					inParm.setData("REF_FILE_NAME", refFileName);
					// 假如引用文件不存，则进入新增模式;
				} else {
					inParm.setData("MODE", "NEW");
				}
				// 是否自动签名
				if (isAutoSign) {
					inParm.setData("AUTO_SIGN", "YES");
				}
				//
				inParm.addListener("onReturnEMRContent", this,
						"onReturnEMRContent");
				//
				// TWindow window
				// =(TWindow)this.openWindow("%ROOT%\\config\\emr\\EMREditUI.x",
				// inParm, false);
				TPanel wordPanel = ((TPanel) this.getComponent("PANEL"));
				Dimension screenSize = Toolkit.getDefaultToolkit()
						.getScreenSize();
				// this.messageBox("x---"+wordPanel.getX() + 10);
				TFrame frame = new TFrame();

				frame.init(getConfigParm().newConfig(
						"%ROOT%\\config\\emr\\EMREditUI.x"));
				if (inParm != null)
					frame.setParameter(inParm);
				//
				frame.onInit();
				frame.setSize(wordPanel.getWidth(), (int) (wordPanel
						.getHeight() / 2));
				frame.setLocation(wordPanel.getX() + 10, screenSize.height
						- frame.getHeight());
				//
				if (isShow) {
					AnimationWindowUtils.show(frame);
				}
				//
				if (!isShow) {
					AWTUtilities.setWindowOpacity(frame, 0.8f);
				}
				if (isShow) {
					frame.setVisible(true);
				}
				//
			} /*else {
				//
				// if(emrChildParm.getValue("CLASS_STYLE").equals("4")){
				this.messageBox("请将光标移到需签名的日志文件中！");
				return;
				// }
			}*/
		}
	}

	/**
	 * 对应抓取框加入内容
	 * @param refFileName
	 * @param captureName
	 */
	public void onReturnEMRContent(String refFileName,String captureName) {	
		//this.messageBox("---captureName----"+captureName);
		if(captureName!=null&&!captureName.equals("")){			
			this.getWord().getPM().setMrNo(this.getMrNo());
			this.getWord().getPM().setCaseNo(this.getCaseNo());
			//
			ECapture capture = this.getWord().findCapture(captureName);
			//this.messageBox("---refFileName----"+refFileName);
			capture.setRefFileName(refFileName);

			//引用
			capture.doRefFile();
			//删除一行开始的回车
			capture.setFocusLast();
			capture.deleteChar();

			//删除最后一行的回车
			ECapture objEnd=capture.getEndCapture();
			objEnd.setFocus();
			objEnd.backspaceChar();
			objEnd.setFocusLast();
			//this.getWord().getFocusManager().separatePanel();
			//
			this.getWord().update();
			//
			//创建后，直接进行保存操作
			onsaveEmr(true);
			//
		}
	}

	/**
	 * 在所属的抓取框名
	 * @return
	 */
	public String getInCaptureName(){
		String captureName="";
		for(String theCaptureName:captureNamesList){
			//this.messageBox("---theCaptureName----"+theCaptureName);
			//1.最近抓取框
			if (this.getWord().focusInCaptue(theCaptureName)) {
				captureName = theCaptureName;
				break;
			}
		}
		return captureName;
	}

	/**
	 * 设置所有的日常病抓取文件
	 */
	private void setAllCapture() {
		TList components = this.getWord().getPageManager().getComponentList();
		int size = components.size();

		for (int i = 0; i < size; i++) {
			EPage ePage = (EPage) components.get(i);
			for (int j = 0; j < ePage.getComponentList().size(); j++) {
				EPanel ePanel = (EPanel) ePage.getComponentList().get(j);
				if (ePanel != null) {
					for (int z = 0; z < ePanel.getBlockSize(); z++) {
						IBlock block = (IBlock) ePanel.get(z);
						// 9为抓取框;
						if (block != null) {
							if (block.getObjectType() == EComponent.CAPTURE_TYPE) {
								EComponent com = block;
								ECapture capture = (ECapture) com;
								if(capture.getCaptureType()==0){
									//记录抓取框名子的列表
									//System.out.println("====capture name======="+capture.getName());
									captureNamesList.add(capture.getName());
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 设定传筛结果宏
	 * 
	 * @param parm
	 * @param macroViewParm
	 * @author wangb
	 */
	private void setInfectResult(TParm parm, TParm macroViewParm) {
		// 传筛结果
		String infectResult = "有;";
		int infectCount = 0;
		int count = 0;
		TParm result = new TParm();

		for (int m = 0; m < macroViewParm.getCount(); m++) {
			if ("Y".equals(macroViewParm.getValue("INFECT_FLG", m))) {
				result.addData("MACRO_NAME", macroViewParm.getValue("MACRO_NAME", m));
				result.addData("MACRO_VALUE", macroViewParm.getValue("MACRO_VALUE", m));
				infectCount++;
			}
		}

		result.setCount(infectCount);

		// 未查到传筛数据说明尚未回传
		if (result.getCount() == 0) {
			parm.addData("传筛结果", "无");
			return;
		}

		for (int i = 0; i < result.getCount(); i++) {
			// 由于传筛回传结果为文本，暂且只能根据是否以阳性的阳字开头来判断
			if (result.getValue("MACRO_VALUE", i).startsWith("阳")) {
				infectResult = infectResult
						+ result.getValue("MACRO_NAME", i) + ";";
			} else if (result.getValue("MACRO_VALUE", i).startsWith("阴")) {
				count++;
			}
		}

		// 回传数据全为阴性
		if (count == result.getCount()) {
			parm.addData("传筛结果", "正常");
		} else {
			parm.addData("传筛结果", infectResult.substring(0, infectResult
					.length() - 1));
		}

		parm.addData("SYSTEM", "COLUMNS", "传筛结果");
	}

	/**
	 * 
	 * 过敏史控件
	 * 
	 */
	public void onAllergy() {
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("ROLE", "1");
		inParm.setData("DR_CODE", Operator.getID());
		inParm.setData("DEPT_CODE", this.getDeptCode());
		inParm.setData("CASE_NO",this.getCaseNo());
		inParm.setData("ADM_TYPE", this.getAdmType());
		inParm.addListener("onReturnAllergy", this, "onReturnAllergy");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRDrugAllergy.x", inParm, false);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);
	}

	/**
	 * 过敏史返回值
	 * @param str
	 */
	public void onReturnAllergy(String str) {	
		//查找过敏控件		
		//把值贴入
		ECapture ecap = this.getWord().findCapture(ALLERGY_KEY);
		if (ecap == null) {
			return;
		}
		ecap.setFocusLast();
		ecap.clear();
		this.getWord().pasteString(str);
		//
	}

	/**
	 * 
	 * 保存评估分数据
	 * @param mode视图  NEW新建 | EDIT修改
	 * 
	 */
	public String doSaveEvalutionRec(String mode){
		EFixed score=this.getWord().findFixed("score");
		String strNo="";

		if(score!=null){			
			//存在要平分的病历，执行保存数据库
			if(score.isSaveDB()){
				String subclsscode=this.getEmrChildParm().getValue("SUBCLASS_CODE");
				// SELECT LOGIC1,SCORE1,SCORE_DESC FROM SYS_EVALUTION_DICT WHERE
				// EVALUTION_CODE=<EVALUTION_CODE>

				TParm parm1=new TParm();
				parm1.setData("EVALUTION_CODE", subclsscode);
				TParm dicParm = null;
				try {
					parm1.setData("SCORE", score.getText());
					//this.messageBox("score"+score.getText());
					//
					dicParm=NISTool.getInstance().checkWarning1(parm1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//this.messageBox("----WARNING_FLG----"+dicParm.getValue("WARNING_FLG"));
				// WARNING_FLG,SCORE_DESC
				// 说明已有配置字典记录数据到 EMR_EVALUTION_RECORD
				if (!StringUtils.isEmpty(dicParm.getValue("WARNING_FLG"))) {
					if (mode.equals("NEW")) {
						// 1.EMR_EVALUTION_RECORD SYS_EVALUTION_DICT
						strNo = NISTool.getInstance().getNo("ALL", "INW",
								"NIS", "No");
						// 2.(<EVALUTION_ID>,<CASE_NO>,<EVALUTION_CODE>,<EVALUTION_DESC>,<SOURCE>,<EVALUTION_CLASS>,
						// <NIS_ID>,<WARNING_FLG>,<SCORE>,TO_DATE(<EVALUTION_DATE>,'yyyy/MM/dd
						// HH24:mi:ss'),
						// <SCORE_DECS>,<FILE_PATH>,<OPT_TERM>,<OPT_USER>,sysdate
						TParm parm = new TParm();
						parm.setData("EVALUTION_ID", strNo);
						parm.setData("CASE_NO", this.getCaseNo());
						parm.setData("EVALUTION_CODE", subclsscode);
						parm.setData("EVALUTION_DESC", dicParm
								.getValue("EVALUTION_DESC"));
						parm.setData("SOURCE", "1");
						parm.setData("EVALUTION_CLASS", "1");
						parm.setData("NIS_ID", new TNull(String.class));
						parm.setData("WARNING_FLG", dicParm
								.getValue("WARNING_FLG"));
						parm.setData("SCORE", score.getText());
						parm.setData("EVALUTION_DATE", StringTool.getString(
								SystemTool.getInstance().getDate(),
								"yyyyMMdd HH:mm:ss"));
						parm.setData("SCORE_DECS", dicParm
								.getData("SCORE_DESC"));
						parm.setData("FILE_PATH", this.getEmrChildParm()
								.getValue("FILE_PATH")
								+ "\\"
								+ this.getEmrChildParm().getValue("FILE_NAME"));
						//
						//String filePath = result.getValue("FILE_PATH", 0).replace("JHW", "PDF");
						//String fileName = result.getValue("FILE_NAME", 0);
						//String path = EmrDataDir + filePath + "\\" + fileName + ".pdf";
						//
						parm.setData("OPT_TERM", Operator.getIP());
						parm.setData("OPT_USER", Operator.getID());				
						//
						NISTool.getInstance().saveNis1(parm);
						//this.messageBox("---strNo----"+strNo);
						this.getWord().setFileRemark(this.getWord().getFileRemark()+ "|"+strNo);
						saveWord();
						//更新操作	
					} else {
						EFixed score1=this.getWord().findFixed("score");
						//this.messageBox("---score1---"+score1.getText());
						//SCORE,EVALUTION_DATE,EVALUTION_ID
						TParm parm = new TParm();
						parm.setData("SCORE", score1.getText());
						parm.setData("EVALUTION_DATE", StringTool.getString(
								SystemTool.getInstance().getDate(),
								"yyyyMMdd HH:mm:ss"));
						String fileRemark=this.getWord().getFileRemark();
						//this.messageBox("fileRemark=="+fileRemark);
						if(!StringUtils.isEmpty(fileRemark)){
							String remark[]=fileRemark.split("\\|");
							//this.messageBox("==length=="+remark.length);
							if(remark.length==4){
								//this.messageBox("==EVALUTION_ID=="+remark[3]);
								parm.setData("EVALUTION_ID", remark[3]);
								boolean flg=NISTool.getInstance().updateNis1(parm);
								//this.messageBox("1111"+flg);
							}

						}
					}
				}
			}
			//
		}
		return strNo;		
	}
	/**
	 * 
	 * @param capture
	 */
	public void deleteCap(ECapture capture) {
		IBlock block = capture.getNextTrueBlock();
		while (block != null) {
			if (isEndCapture(block, capture)) {
				deleteCapCom( (EComponent) block);
				return;
			}
			if (block instanceof ETable) {
				deleteCapCom( (EComponent) block);
				block = capture.getNextTrueBlock();
				continue;
			}
			IBlock nextBlock = block.getNextTrueBlock();
			if (nextBlock != null) {
				deleteCapCom( (EComponent) block);
				block = nextBlock;
				continue;
			}
			EPanel panel = block.getPanel().getNextTruePanel();
			deleteCapCom( (EComponent) block);
			if (panel == null || panel.size() == 0) {
				return;
			}
			block = panel.get(0);
		}
	}

	private boolean isEndCapture(IBlock block, ECapture captureStart) {
		if (block == null) {
			return false;
		}
		if (block.getObjectType() != EComponent.CAPTURE_TYPE) {
			return false;
		}
		ECapture capture = (ECapture) block;
		if (capture.getCaptureType() != 1) {
			return false;
		}
		if (capture.getName() == null || capture.getName().length() == 0) {
			return false;
		}
		if (capture.getName().equals(captureStart.getName())) {
			return true;
		}
		return false;
	}

	private void deleteCapCom(EComponent com) {
		if (com == null) {
			return;
		}
		if (com instanceof EFixed) {
			( (EFixed) com).deleteFixed();
		}
		else if (com instanceof EMacroroutine) {
			( (EMacroroutine) com).delete();
		}
		else if (com instanceof ETable) {
			( (ETable) com).removeThisAll();
		}
		else if (com instanceof EImage) {
			( (EImage) com).removeThis();
		}
		else {
			return;
		}
		word.update();
	}
	/**
	 * 
	 * 单选监听
	 * 
	 * add by yyn 2017/11/10
	 */
	public void singleChooseSelected(String wordName, String singleChooseName){	
		//对监听单选进行操作
		ESingleChoose AgeGrp1 = (ESingleChoose)word.findObject("AgeGrp1", EComponent.SINGLE_CHOOSE_TYPE);//入ICU年龄<=30天
		ESingleChoose AgeGrp2 = (ESingleChoose)word.findObject("AgeGrp2", EComponent.SINGLE_CHOOSE_TYPE);//入ICU年龄介于31天到365天（1岁）之间
		ESingleChoose PREM = (ESingleChoose)word.findObject("PREM", EComponent.SINGLE_CHOOSE_TYPE);//早产，指妊娠小于37周
		ESingleChoose CP_SOP = (ESingleChoose)word.findObject("CP_SOP", EComponent.SINGLE_CHOOSE_TYPE);//单次手术中合并多个心脏手术术式
		ECapture NC_STAN = (ECapture)word.findObject("STAN9", EComponent.CAPTURE_TYPE);//非心脏畸形
		ECapture rcat1 = (ECapture)word.findObject("RCat1", EComponent.CAPTURE_TYPE);//风险分类1
		ECapture rcat2 = (ECapture)word.findObject("RCat2", EComponent.CAPTURE_TYPE);//风险分类2
		ECapture rcat3 = (ECapture)word.findObject("RCat3", EComponent.CAPTURE_TYPE);//风险分类3
		ECapture rcat4 = (ECapture)word.findObject("RCat4", EComponent.CAPTURE_TYPE);//风险分类4
		ECapture rcat5 = (ECapture)word.findObject("RCat5", EComponent.CAPTURE_TYPE);//风险分类5
		ECapture rcat6 = (ECapture)word.findObject("RCat6", EComponent.CAPTURE_TYPE);//风险分类6

		//得到下列参数的取值
		AgeGrp1_v = Integer.parseInt(AgeGrp1.getSelectedValue());
		AgeGrp2_v = Integer.parseInt(AgeGrp2.getSelectedValue());
		PREM_v = Integer.parseInt(PREM.getSelectedValue());
		CP_SOP_v = Integer.parseInt(CP_SOP.getSelectedValue());
		
		//对非心脏畸形的判断
		if(!NC_STAN.getValue().equals("") & !NC_STAN.getValue().equals("无")){
			NC_STAN_v = 1;
		}
		else {
			NC_STAN_v = 0;
		}

		//对风险分类取值进行设置
		if(!rcat6.getValue().equals("") & !rcat6.getValue().equals("无")){//当RCat6不为空时，取级别最高的风险等级6用来计算
			//RCat1_v = 0;
			RCat2_v = 0;
			RCat3_v = 0;
			RCat4_v = 0;
			RCat5_v = 0;
			RCat6_v = 1;
		}
		else if(!rcat5.getValue().equals("") & !rcat5.getValue().equals("无")){//当RCat5为最高风险级别
			//RCat1_v = 0;
			RCat2_v = 0;
			RCat3_v = 0;
			RCat4_v = 0;
			RCat5_v = 1;
			RCat6_v = 0;
		}
		else if(!rcat4.getValue().equals("") & !rcat4.getValue().equals("无")){//当Rcat4为最高风险级别
			//RCat1_v = 0;
			RCat2_v = 0;
			RCat3_v = 0;
			RCat4_v = 1;
			RCat5_v = 0;
			RCat6_v = 0;
		}
		else if(!rcat3.getValue().equals("") & !rcat3.getValue().equals("无")){//当Rcat3为最高风险级别
			//RCat1_v = 0;
			RCat2_v = 0;
			RCat3_v = 1;
			RCat4_v = 0;
			RCat5_v = 0;
			RCat6_v = 0;
		}
		else if(!rcat2.getValue().equals("") & !rcat2.getValue().equals("无")){//当Rcat2为最高风险级别
			//RCat1_v = 0;
			RCat2_v = 1;
			RCat3_v = 0;
			RCat4_v = 0;
			RCat5_v = 0;
			RCat6_v = 0;
		}
		//当仅接受了第一类手术中的单个术式时，则在计算时，年龄和手术风险分类几项均不添加参数
		else if(!rcat1.getValue().equals("") & !rcat1.getValue().equals("无") & row_num.equals("1")){
			//RCat1_v = 0;
			RCat2_v = 0;
			RCat3_v = 0;
			RCat4_v = 0;
			RCat5_v = 0;
			RCat6_v = 0;
			AgeGrp1_v = 0;
			AgeGrp2_v = 0;
		}
		else{
			//RCat1_v = 1;
			RCat2_v = 0;
			RCat3_v = 0;
			RCat4_v = 0;
			RCat5_v = 0;
			RCat6_v = 0;
		}
		//如患儿大于一岁（365天），则在计算时，年龄和手术风险分类几项均不添加参数
		if((AgeGrp1.getText().equals("否") & AgeGrp2.getText().equals("否"))){
			AgeGrp1_v = 0;
			AgeGrp2_v = 0;
			//RCat1_v = 0;
			RCat2_v = 0;
			RCat3_v = 0;
			RCat4_v = 0;
			RCat5_v = 0;
			RCat6_v = 0;
			
		}
		//年龄选择有误
		if((AgeGrp1.getText().equals("是") & AgeGrp2.getText().equals("是"))){
			this.messageBox("年龄选择有误，请重新确定患者年龄段。");
		}
		//当对风险等级进行了操作后，对CP_SOP参数有提示功能
		if(click1||click2||click3||click4||click5||click6){
			//已选择的术式数量
			int rachsnum = Integer.parseInt(n2)+Integer.parseInt(n3)+Integer.parseInt(n4)
						+Integer.parseInt(n5)+Integer.parseInt(n6)+Integer.parseInt(row_num);
			if(CP_SOP.getText().equals("是")){//单次手术中合并多个心脏手术术式
				if(rachsnum <= 1){//手术术式个数小于等于1
					this.messageBox("单次手术中未包含多个心脏手术术式。与CP_SOP参数不符合");
				}
			}
			if(CP_SOP.getText().equals("否")){//单次手术 单个心脏手术术式
				if(rachsnum != 1){//手术术式个数不为一
					this.messageBox("单次手术中包含多个或不包含心脏手术术式。与CP_SOP参数不符合");
				}
			}
		}
		
		//计算公式
		RACHSVal = -5.76+1.8871*(RCat2_v)+2.7408*(RCat3_v)+3.3393*(RCat4_v)+4.5829*(RCat5_v)+4.5369*(RCat6_v)
					+1.0986*(AgeGrp1_v)+0.6419*(AgeGrp2_v)+0.5878*(NC_STAN_v)+0.5878*(PREM_v)+0.4055*(CP_SOP_v);
		//System.out.println("RACHSVal" +RACHSVal);
		BigDecimal bg1 = new BigDecimal(RACHSVal);
		RACHSVal = bg1.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();//保留四位有效数字
		double e = Math.E;//e
		double d = Math.pow(e, RACHSVal);//e^x x=RACHSVal
		//System.out.println("d" +d);
		rachs = d/(1+d);//e^x/(1+e^x)
		//System.out.println("rachs" +rachs);
		BigDecimal bg2 = new BigDecimal(rachs);
		rachs = bg2.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();//保留四位有效数字
		RACHS = Double.toString(rachs);

		//设置抓取值
		ECapture ecap = (ECapture)word.findObject("value", EComponent.CAPTURE_TYPE);
		if (ecap == null){
			System.out.println("word--->value控件不存在");
			return;
		}
		ecap.setFocusLast();
		ecap.clear();
		word.pasteString(RACHS);	
	}
	/**
	 * 
	 * 双击风险分类
	 * 
	 * add by yyn 2017/11/10
	 */
	public void rcat(String level) {
		TParm parm = new TParm();
		String sublevel = level.substring(4, 5);//数字识别
		//对风险等级1-6进行选择，对非心脏畸形进行选择(标识为9)
		if(sublevel.equals("1")||sublevel.equals("2")||sublevel.equals("3")||sublevel.equals("4")||
				sublevel.equals("5")||sublevel.equals("6")||sublevel.equals("9")){
			parm.setData("CHOOSE_NAME", sublevel);
			Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRRCatUI.x", parm);//打开选择界面
			if (obj == null || !(obj instanceof TParm)) {
				System.out.println("obj is null");
				return;
			}
			TParm rcat = (TParm) obj;
			String ract_name = rcat.getValue("RACHS_DESC");//手术术式名称
			//System.out.println("rachs desc = "+ract_name);
			String num_value = "";
			if(rcat.getValue("ROW_NUM").equals("")){//当在新打开的界面中，未选择任何选项时，数量计为零
				num_value = "0";
			}
			else{
				num_value = rcat.getValue("ROW_NUM");
			}
			
			//风险等级中术式选择的数目
			if(sublevel.equals("1")){
				row_num = num_value;//手术术式选择的数量
				click1 = true;//对风险等级一进行了操作
			}
			if(sublevel.equals("2")){
				n2 = num_value;
				click2 = true;//对风险等级二进行了操作
			}			
			if(sublevel.equals("3")){
				n3 = num_value;
				click3 = true;//对风险等级三进行了操作
			}
			if(sublevel.equals("4")){
				n4 = num_value;
				click4 = true;//对风险等级四进行了操作
			}
			if(sublevel.equals("5")){
				n5 = num_value;
				click5 = true;//对风险等级五进行了操作
			}
			if(sublevel.equals("6")){
				n6 = num_value;
				click6 = true;//对风险等级六进行了操作
			}
			
			ECapture ecap = (ECapture)word.findObject(level, EComponent.CAPTURE_TYPE);
			if(ecap == null){
				System.out.println("word--->"+level+"控件不存在");
			}
			ecap.setFocusLast();
			ecap.clear();
			word.pasteString(ract_name);
		}

		ESingleChoose AgeGrp1 = (ESingleChoose)word.findObject("AgeGrp1", EComponent.SINGLE_CHOOSE_TYPE);//入ICU年龄<=30天
		ESingleChoose AgeGrp2 = (ESingleChoose)word.findObject("AgeGrp2", EComponent.SINGLE_CHOOSE_TYPE);//入ICU年龄介于31天到365天（1岁）之间
		ESingleChoose PREM = (ESingleChoose)word.findObject("PREM", EComponent.SINGLE_CHOOSE_TYPE);//早产，指妊娠小于37周
		ESingleChoose CP_SOP = (ESingleChoose)word.findObject("CP_SOP", EComponent.SINGLE_CHOOSE_TYPE);//单次手术中合并多个心脏手术术式
		ECapture NC_STAN = (ECapture)word.findObject("STAN9", EComponent.CAPTURE_TYPE);//非心脏畸形
		ECapture rcat1 = (ECapture)word.findObject("RCat1", EComponent.CAPTURE_TYPE);//风险分类1
		ECapture rcat2 = (ECapture)word.findObject("RCat2", EComponent.CAPTURE_TYPE);//风险分类2
		ECapture rcat3 = (ECapture)word.findObject("RCat3", EComponent.CAPTURE_TYPE);//风险分类3
		ECapture rcat4 = (ECapture)word.findObject("RCat4", EComponent.CAPTURE_TYPE);//风险分类4
		ECapture rcat5 = (ECapture)word.findObject("RCat5", EComponent.CAPTURE_TYPE);//风险分类5
		ECapture rcat6 = (ECapture)word.findObject("RCat6", EComponent.CAPTURE_TYPE);//风险分类6		

		
		//得到下列参数的取值
		AgeGrp1_v = Integer.parseInt(AgeGrp1.getSelectedValue());
		AgeGrp2_v = Integer.parseInt(AgeGrp2.getSelectedValue());
		PREM_v = Integer.parseInt(PREM.getSelectedValue());
		CP_SOP_v = Integer.parseInt(CP_SOP.getSelectedValue());

		//对非心脏畸形的判断
		if(!NC_STAN.getValue().equals("") & !NC_STAN.getValue().equals("无")){
			NC_STAN_v = 1;
		}
		else {
			NC_STAN_v = 0;
		}
		
		//对风险分类取值进行设置
		if(!rcat6.getValue().equals("") & !rcat6.getValue().equals("无")){//当RCat6不为空时，取级别最高的风险等级6用来计算
			//RCat1_v = 0;
			RCat2_v = 0;
			RCat3_v = 0;
			RCat4_v = 0;
			RCat5_v = 0;
			RCat6_v = 1;
		}
		else if(!rcat5.getValue().equals("") & !rcat5.getValue().equals("无")){//当RCat5为最高风险级别
			//RCat1_v = 0;
			RCat2_v = 0;
			RCat3_v = 0;
			RCat4_v = 0;
			RCat5_v = 1;
			RCat6_v = 0;
		}
		else if(!rcat4.getValue().equals("") & !rcat4.getValue().equals("无")){//当Rcat4为最高风险级别
			//RCat1_v = 0;
			RCat2_v = 0;
			RCat3_v = 0;
			RCat4_v = 1;
			RCat5_v = 0;
			RCat6_v = 0;
		}
		else if(!rcat3.getValue().equals("") & !rcat3.getValue().equals("无")){//当Rcat3为最高风险级别
			//RCat1_v = 0;
			RCat2_v = 0;
			RCat3_v = 1;
			RCat4_v = 0;
			RCat5_v = 0;
			RCat6_v = 0;
		}
		else if(!rcat2.getValue().equals("") & !rcat2.getValue().equals("无")){//当Rcat2为最高风险级别
			//RCat1_v = 0;
			RCat2_v = 1;
			RCat3_v = 0;
			RCat4_v = 0;
			RCat5_v = 0;
			RCat6_v = 0;
		}
		//当仅接受了第一类手术中的单个术式时，则在计算时，年龄和手术风险分类几项均不添加参数
		else if(!rcat1.getValue().equals("") & !rcat1.getValue().equals("无") & row_num.equals("1")){
			//RCat1_v = 0;
			RCat2_v = 0;
			RCat3_v = 0;
			RCat4_v = 0;
			RCat5_v = 0;
			RCat6_v = 0;
			AgeGrp1_v = 0;
			AgeGrp2_v = 0;
		}
		else{
			//RCat1_v = 1;
			RCat2_v = 0;
			RCat3_v = 0;
			RCat4_v = 0;
			RCat5_v = 0;
			RCat6_v = 0;
		}
		//如患儿大于一岁（365天），则在计算时，年龄和手术风险分类几项均不添加参数
		if((AgeGrp1.getText().equals("否") & AgeGrp2.getText().equals("否"))){
			AgeGrp1_v = 0;
			AgeGrp2_v = 0;
			//RCat1_v = 0;
			RCat2_v = 0;
			RCat3_v = 0;
			RCat4_v = 0;
			RCat5_v = 0;
			RCat6_v = 0;
			
		}
		
		//当对风险等级进行了操作后，对CP_SOP参数有提示功能
		if(click1||click2||click3||click4||click5||click6){
			//已选择的术式数量
			int rachsnum = Integer.parseInt(n2)+Integer.parseInt(n3)+Integer.parseInt(n4)
						+Integer.parseInt(n5)+Integer.parseInt(n6)+Integer.parseInt(row_num);
			if(CP_SOP.getText().equals("是")){//单次手术中合并多个心脏手术术式
				if(rachsnum <= 1){//手术术式个数小于等于1
					this.messageBox("单次手术中未包含多个心脏手术术式。与CP_SOP参数不符合");
				}
			}
			if(CP_SOP.getText().equals("否")){//单次手术 单个心脏手术术式
				if(rachsnum != 1){//手术术式个数不为一
					this.messageBox("单次手术中包含多个或不包含心脏手术术式。与CP_SOP参数不符合");
				}
			}
		}
		
		//计算公式
		RACHSVal = -5.76+1.8871*(RCat2_v)+2.7408*(RCat3_v)+3.3393*(RCat4_v)+4.5829*(RCat5_v)+4.5369*(RCat6_v)
					+1.0986*(AgeGrp1_v)+0.6419*(AgeGrp2_v)+0.5878*(NC_STAN_v)+0.5878*(PREM_v)+0.4055*(CP_SOP_v);
		//System.out.println("RACHSVal" +RACHSVal);
		BigDecimal bg1 = new BigDecimal(RACHSVal);
		RACHSVal = bg1.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();//保留四位有效数字
		double e = Math.E;//e
		double d = Math.pow(e, RACHSVal);//e^x x=RACHSVal
		//System.out.println("d" +d);
		rachs = d/(1+d);//e^x/(1+e^x)
		//System.out.println("rachs" +rachs);
		BigDecimal bg2 = new BigDecimal(rachs);
		rachs = bg2.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();//保留四位有效数字
		RACHS = Double.toString(rachs);

		//设置抓取值
		ECapture ecap = (ECapture)word.findObject("value", EComponent.CAPTURE_TYPE);
		if (ecap == null){
			System.out.println("word--->value控件不存在");
			return;
		}
		ecap.setFocusLast();
		ecap.clear();
		word.pasteString(RACHS);
		//System.out.println("row_num" +row_num);
	}
	
	/**
	 * 获得CCPC用药指导数据,并带入到表格
	 */
	private void getCcpcMedicationGuideData() {
		ETR tr = null;
        ETD td = null;
        // 院前用药
        ETable paTable = (ETable) word.findObject("PA_TABLE", EComponent.TABLE_TYPE);
        if (paTable != null) {
        	// 查询院前用药数据
        	TParm paResult = OdiOrderTool.getInstance().queryPhaOrderData(this.getCaseNo(), "PA");
        	int count = paResult.getCount();
        	if (count > 0) {
        		for (int i = 0; i < count; i++) {
        			tr = paTable.appendTR();
        			td = tr.get(0);
        			td.setString(paResult.getValue("ORDER_DESC", i));
        			td = tr.get(1);
        			td.setString(paResult.getValue("DR_NOTE", i));
        			td = tr.get(2);
        			td.setString(paResult.getValue("EFF_DATE", i));
        			td = tr.get(3);
        			td.setString(paResult.getValue("DC_DATE", i));
        			td = tr.get(4);
        			td.setString(paResult.getValue("MED_REPRESENTOR", i));
        			td = tr.get(5);
        			td.setString(paResult.getValue("MEDI_QTY", i));
        			td = tr.get(6);
        			td.setString(paResult.getValue("UNIT_CHN_DESC", i));
        			td = tr.get(7);
        			td.setString(paResult.getValue("FREQ_CHN_DESC", i));
        		}
        		
        		paTable.update();
        	}
        }
        
        // 长期
        ETable udTable = (ETable) word.findObject("UD_TABLE", EComponent.TABLE_TYPE);
        if (udTable != null) {
        	// 查询长期药嘱数据
        	TParm udResult = OdiOrderTool.getInstance().queryPhaOrderData(this.getCaseNo(), "UD");
        	int count = udResult.getCount();
        	if (count > 0) {
        		for (int i = 0; i < count; i++) {
        			tr = udTable.appendTR();
        			td = tr.get(0);
        			td.setString(udResult.getValue("ORDER_DESC", i));
        			td = tr.get(1);
        			td.setString(udResult.getValue("MEDI_QTY", i));
        			td = tr.get(2);
        			td.setString(udResult.getValue("UNIT_CHN_DESC", i));
        			td = tr.get(3);
        			td.setString(udResult.getValue("FREQ_CHN_DESC", i));
        			td = tr.get(4);
        			td.setString(udResult.getValue("ROUTE_CHN_DESC", i));
        			td = tr.get(5);
        			td.setString(udResult.getValue("EFF_DATE_TIME", i));
        			td = tr.get(6);
        			td.setString(udResult.getValue("DC_DATE_IIME", i));
        		}
        		
        		udTable.update();
        	}
        	
        }
        
        // 出院带药
        ETable dsTable = (ETable) word.findObject("DS_TABLE", EComponent.TABLE_TYPE);
        if (dsTable != null) {
        	// 查询出院带药数据
        	TParm dsResult = OdiOrderTool.getInstance().queryPhaOrderData(this.getCaseNo(), "DS");
        	int count = dsResult.getCount();
        	if (count > 0) {
        		for (int i = 0; i < count; i++) {
        			tr = dsTable.appendTR();
        			td = tr.get(0);
        			td.setString(dsResult.getValue("ORDER_DESC", i));
        			td = tr.get(1);
        			td.setString(dsResult.getValue("MEDI_QTY", i));
        			td = tr.get(2);
        			td.setString(dsResult.getValue("UNIT_CHN_DESC", i));
        			td = tr.get(3);
        			td.setString(dsResult.getValue("FREQ_CHN_DESC", i));
        			td = tr.get(4);
        			td.setString(dsResult.getValue("ROUTE_CHN_DESC", i));
        			td = tr.get(5);
        			td.setString(dsResult.getValue("EFF_DATE_TIME", i));
        		}
        		
        		dsTable.update();
        	}
        	
        }
	}

	/**
	 * <p>设置抓取值</p>
	 * @param word
	 * @param name
	 * @param value
	 * @author wangqing 20180123
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

}
