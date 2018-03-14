package com.javahis.ui.clp;

import com.dongyang.control.*;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextField;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TMenuItem;
import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.system.textFormat.TextFormatSYSCtz;
import com.dongyang.ui.event.TTableEvent;
import jdo.clp.BscInfoTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.SYSOrderSetDetailTool;
import jdo.clp.ThrpyschdmTool;
import jdo.clp.PackTool;
import jdo.sys.SystemTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TPopupMenuEvent;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.dongyang.util.TypeTool;
import jdo.opd.TotQtyTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Comparator;
import java.util.Collections;
import java.util.Vector;

import com.javahis.system.textFormat.TextFormatSYSPhaFreq;
import jdo.clp.ClpchkTypeTool;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.util.Compare;

import jdo.odo.Diagrec;
import jdo.sys.PatTool;

import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.javahis.util.OdoUtil;

/**
 * <p>
 * Title: 窗体控制器
 * </p>
 * 
 * <p>
 * Description: 临床路径标准设定
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Zhang jianguo 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangjg 2011.04.21
 * @version 1.0
 */
public class CLPBscInfoControl_bak extends TControl {
	/**
	 * 构造方法
	 */
	public CLPBscInfoControl_bak() {
	}

	/**
	 * 选项卡控件
	 */
	// 上次点选的页签索引
	private int lastSelectedIndex = 0;
	private TTabbedPane tTabbedPane_0;
	// 排序===pangben 2012-6-22
	private Compare compare = new Compare();
	private Compare compareOne = new Compare();
	private int sortColumnOne = -1;
	private boolean ascendingOne = false;
	private int sortColumn = -1;
	private boolean ascending = false;
	// 选项卡的页签控件
	// private TPanel CLP01;
	// private TPanel CLP02;
	// private TPanel CLP03;
	// private TPanel CLP04;
	// private TPanel CLP05;
	// 院区/区域
	private TTextField REGION_CODE;
	/**
	 * 第一个页签的控件 临床路径类别
	 */
	// 卢海加入诊断和手术诊断table
	private TTable diagnoseTable;
	private TTable operatorDiagnoseTable;
	// 路径代码
	private TTextField CLNCPATH_CODE;
	// 中文说明
	private TTextField CLNCPATH_CHN_DESC;
	// 英文说明
	private TTextField CLNCPATH_ENG_DESC;
	// 拼音
	private TTextField PY1;
	// 助记码
	private TTextField PY2;
	// 所属科别
	private TextFormatDept DEPT_CODE;
	// 制订日期
	private TTextFormat FRSTVRSN_DATE;
	// 最近修订日
	private TTextFormat LASTVRSN_DATE;
	// 修订次数
	private TTextField MODIFY_TIMES;
	// 版本
	private TTextField VERSION;
	// 加入路径条件
	private TTextArea ACPT_CODE;
	// 溢出路径条件
	private TTextArea EXIT_CODE;
	// 标准住院天数
	private TTextField STAYHOSP_DAYS;
	// 平均医疗费用
	private TTextField AVERAGECOST;
	// 备注
	private TTextField DESCRIPTION;
	// 是否启用
	private TCheckBox ACTIVE_FLG;
	// 临床路径类别 CLP_BSCINFO
	private TTable CLP_BSCINFO;
	/**
	 * 第二个页签的控件 治疗时程
	 */
	// 路径项目
	private TTextFormat CLNCPATH_CODE01;
	// 时程代码
	private TextFormatCLPDuration SCHD_CODE;
	// 治疗天数
	private TTextField SCHD_DAY;
	// 入院第几天
	private TTextField SUSTAINED_DAYS;
	// 治疗时程 CLP_THRPYSCHDM
	private TTable CLP_THRPYSCHDM;
	/**
	 * 第三个页签的控件 医嘱套餐
	 */
	// 路径项目
	private TTextFormat CLNCPATH_CODE02;
	// 时程代码
	private TTextFormat SCHD_CODE01;
	// 总金额
	private TTextField FEES;
	// 版本
	private TTextField VERSION01;
	// 医嘱套餐 CLP_PACK
	private TTable CLP_PACK01;
	/**
	 * 第四个页签的控件 关键诊疗套餐
	 */
	// 路径项目
	private TTextFormat CLNCPATH_CODE03;
	// 时程代码
	private TTextFormat SCHD_CODE02;
	// 版本
	private TTextField VERSION02;
	// 关键诊疗套按 CLP_PACK
	private TTable CLP_PACK02;
	// 诊断ICD desc
	private TTextField diagnose_desc;
	// 手术ICD desc
	private TTextField operation_diagnose_desc;
	// 诊断ICD
	private TTextField diagnose;
	// 手术ICD desc
	private TTextField operation_diagnose;

	/**
	 * 第五个页签的控件 护理计划
	 */
	// 路径项目
	private TTextFormat CLNCPATH_CODE04;
	// 时程代码
	private TTextFormat SCHD_CODE03;
	// 版本
	private TTextField VERSION03;
	// 护理计划 CLP_PACK
	private TTable CLP_PACK03;
	/** 声明日期格式化类对象 */
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	/** CLP_BSCINFO */
	public static final String CLP_BSCINFO_PARMMAP = "REGION_CODE;CLNCPATH_CODE;CLNCPATH_CHN_DESC;CLNCPATH_ENG_DESC;PY1;ICD_DESC;OPE_DESC;PY2;DEPT_CODE;FRSTVRSN_DATE;LASTVRSN_DATE;MODIFY_TIMES;VERSION;ACPT_CODE;EXIT_CODE;STAYHOSP_DAYS;AVERAGECOST;DESCRIPTION;ACTIVE_FLG;VERSION";
	/** CLP_THRPYSCHDM */
	public static final String CLP_THRPYSCHDM_PARMMAP = "SCHD_DESC;SUSTAINED_DAYS;SCHD_DAY;SCHD_CODE";
	/** 斩断table名称 */
	public static final String DIAGNOSE_TABLE = "diagnoseTable";
	public static final String OPERATOR_DIAGNOSE_TABLE = "operatorDiagnoseTable";
	// 病区记录
	private String regionCode = Operator.getRegion();
	private boolean udFlg = false;// 频次触发事件回滚信息总量 金额不改变状态

	/**
	 * 初始化方法 初始化查询全部
	 */
	@Override
	public void init() {
		super.init();
		// 获取全部输入框控件
		getAllComponent();
		// 注册监听事件
		initControler();
		// 打印按钮不可用
		TMenuItem print = (TMenuItem) this.getComponent("print");
		print.setEnabled(false);
		// 版本不可编辑
		VERSION.setEditable(false);
		VERSION01.setEditable(false);
		VERSION02.setEditable(false);
		VERSION03.setEditable(false);
		// 是否启用标识不可编辑
		ACTIVE_FLG.setEnabled(false);
		// 新增临床路径类别 默认当天
		if (CLP_BSCINFO.getSelectedRow() < 0) {
			FRSTVRSN_DATE.setValue(dateFormat.format(SystemTool.getInstance()
					.getDate()));
		}
		// 查询全部临床路径类别
		// 卢海修改begin
		TParm slelectParm = new TParm();
		this.putBasicSysInfoIntoParm(slelectParm);
		TParm allBscInfos = BscInfoTool.getInstance().getAllBscInfos(
				slelectParm);
		// 卢海修改 end
		// 显示在前台界面
		CLP_BSCINFO.setParmValue(allBscInfos);
		// 设置默认修改日期
		this.setValue("LASTVRSN_DATE", getDateStr("yyyy/MM/dd"));
		// 诊断和手术诊断的Table初始化和注册监听事件
		initDiagTables();
	}

	/**
	 * 诊断和手术诊断的Table初始化和注册监听事件
	 */
	private void initDiagTables() {
		TParm diagTableParm = new TParm();
		TParm operatorDiagTableParm = new TParm();
		this.diagnoseTable.setParmValue(diagTableParm);
		this.operatorDiagnoseTable.setParmValue(operatorDiagTableParm);
		// 加入默认行
		addDefaultRowForDiag(this.OPERATOR_DIAGNOSE_TABLE);
		addDefaultRowForDiag(this.DIAGNOSE_TABLE);
		// 绑定监听事件
		diagnoseTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onDiagCreateEditComponent");
		this.operatorDiagnoseTable.addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onOperateDiagCreateEditComponent");
	}

	/**
	 * 加入默认行
	 * 
	 * @param tableName
	 *            String
	 */
	private void addDefaultRowForDiag(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		table.acceptText();
		TParm tableParm = table.getParmValue();
		if (this.DIAGNOSE_TABLE.equals(tableName)) {
			if (tableParm.getCount("diagnose_desc_begin") > 0
					&& "".equals(tableParm.getValue("diagnose_desc_begin",
							(tableParm.getCount("diagnose_desc_begin") - 1))
							.trim())) {
				return;
			}
			// 默认加入一条空数据
			tableParm.addData("diagnose_desc_begin", "");
			tableParm.addData("diagnose_desc_end", "");
			tableParm.addData("diagnose_icd_begin", "");
			tableParm.addData("diagnose_icd_end", "");
			tableParm.addData("diagnose_icd_type_begin", "");
			tableParm.addData("diagnose_icd_type_end", "");

		} else if (this.OPERATOR_DIAGNOSE_TABLE.equals(tableName)) {
			if (tableParm.getCount("operator_diagnose_desc_begin") > 0
					&& ""
							.equals(tableParm
									.getValue(
											"operator_diagnose_desc_begin",
											(tableParm
													.getCount("operator_diagnose_desc_begin") - 1)))) {
				return;
			}
			// 手术ICD
			tableParm.addData("operator_diagnose_desc_begin", "");
			tableParm.addData("operator_diagnose_desc_end", "");
			tableParm.addData("operator_diagnose_icd_begin", "");
			tableParm.addData("operator_diagnose_icd_end", "");
		}
		table.setParmValue(tableParm);
	}

	/**
	 * 添加诊断弹出窗口
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onDiagCreateEditComponent(Component com, int row, int column) {
		if (column == 0 || column == 1) {
			TTextField textfield = (TTextField) com;
			textfield.onInit();
			// 给table上的新text增加sys_fee弹出窗口
			TParm parm = new TParm();
			// parm.setData("ICD_TYPE", wc);
			textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
			// 给新text增加接受sys_fee弹出窗口的回传值
			String returmMethodName = "";
			if (column == 0) {
				returmMethodName = "popDiagTableReturnBegin";
			} else if (column == 1) {
				returmMethodName = "popDiagTableReturnEnd";
			}
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					returmMethodName);
		}
	}

	/**
	 * 新增诊断
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popDiagTableReturnBegin(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable tableDiag = (TTable) this.getComponent(this.DIAGNOSE_TABLE);
		TParm tableParm = tableDiag.getParmValue();
		int selectedRow = tableDiag.getSelectedRow();
		tableDiag.acceptText();
		// int rowNo = tableDiag.getSelectedRow();
		String icdCode = parm.getValue("ICD_CODE");
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		String icdType = parm.getValue("ICD_TYPE");
		tableParm.setData("diagnose_desc_begin", selectedRow, icdDesc);
		tableParm.setData("diagnose_icd_begin", selectedRow, icdCode);
		tableParm.setData("diagnose_icd_type_begin", selectedRow, icdType);
		tableDiag.setParmValue(tableParm);
		// 加入默认行
		addDefaultRowForDiag(this.DIAGNOSE_TABLE);

	}

	/**
	 * 新增诊断
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popDiagTableReturnEnd(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable tableDiag = (TTable) this.getComponent(this.DIAGNOSE_TABLE);
		TParm tableParm = tableDiag.getParmValue();
		int selectedRow = tableDiag.getSelectedRow();
		tableDiag.acceptText();
		// int rowNo = tableDiag.getSelectedRow();
		String icdCode = parm.getValue("ICD_CODE");
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		String icdType = parm.getValue("ICD_TYPE");
		tableParm.setData("diagnose_desc_end", selectedRow, icdDesc);
		tableParm.setData("diagnose_icd_end", selectedRow, icdCode);
		tableParm.setData("diagnose_icd_type_end", selectedRow, icdType);
		tableDiag.setParmValue(tableParm);
		// 加入默认行
		addDefaultRowForDiag(this.DIAGNOSE_TABLE);
	}

	/**
	 * 添加手术诊断弹出窗口
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onOperateDiagCreateEditComponent(Component com, int row,
			int column) {
		if (column == 0 || column == 1) {
			TTextField textfield = (TTextField) com;
			textfield.onInit();
			// 给table上的新text增加sys_fee弹出窗口
			TParm parm = new TParm();
			// parm.setData("ICD_TYPE", wc);
			textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSOpICD.x"), parm);
			// 给新text增加接受sys_fee弹出窗口的回传值
			String returmMethodName = "";
			if (column == 0) {
				returmMethodName = "popOperatorDiagTableReturnBegin";
			} else if (column == 1) {
				returmMethodName = "popOperatorDiagTableReturnEnd";
			}
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					returmMethodName);
		}
	}

	/**
	 * 新增诊断
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popOperatorDiagTableReturnBegin(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable tableDiag = (TTable) this
				.getComponent(this.OPERATOR_DIAGNOSE_TABLE);
		TParm tableParm = tableDiag.getParmValue();
		int selectedRow = tableDiag.getSelectedRow();
		tableDiag.acceptText();
		// int rowNo = tableDiag.getSelectedRow();
		String icdCode = parm.getValue("OPERATION_ICD");
		String icdDesc = parm.getValue("OPT_CHN_DESC");
		tableParm.setData("operator_diagnose_desc_begin", selectedRow, icdDesc);
		tableParm.setData("operator_diagnose_icd_begin", selectedRow, icdCode);
		tableDiag.setParmValue(tableParm);
		// 加入默认行
		addDefaultRowForDiag(this.OPERATOR_DIAGNOSE_TABLE);
	}

	/**
	 * 新增诊断
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popOperatorDiagTableReturnEnd(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable tableDiag = (TTable) this
				.getComponent(this.OPERATOR_DIAGNOSE_TABLE);
		TParm tableParm = tableDiag.getParmValue();
		int selectedRow = tableDiag.getSelectedRow();
		tableDiag.acceptText();
		// int rowNo = tableDiag.getSelectedRow();
		String icdCode = parm.getValue("OPERATION_ICD");
		String icdDesc = parm.getValue("OPT_CHN_DESC");
		tableParm.setData("operator_diagnose_desc_end", selectedRow, icdDesc);
		tableParm.setData("operator_diagnose_icd_end", selectedRow, icdCode);
		tableDiag.setParmValue(tableParm);
		// 加入默认行
		addDefaultRowForDiag(this.OPERATOR_DIAGNOSE_TABLE);
	}

	/**
	 * 获取全部输入框控件
	 */
	public void getAllComponent() {
		/**
		 * 选项卡控件
		 */
		this.tTabbedPane_0 = (TTabbedPane) this.getComponent("tTabbedPane_0");
		// 选项卡的页签控件
		// this.CLP01 = (TPanel)this.getComponent("CLP01");
		// this.CLP02 = (TPanel)this.getComponent("CLP02");
		// this.CLP03 = (TPanel)this.getComponent("CLP03");
		// this.CLP04 = (TPanel)this.getComponent("CLP04");
		// this.CLP05 = (TPanel)this.getComponent("CLP05");
		// 区域/院区
		this.REGION_CODE = (TTextField) this.getComponent("REGION_CODE");
		/**
		 * 第一个页签的控件 临床路径类别 CLP_BSCINFO
		 */
		// 加入手术诊断和诊断
		this.diagnoseTable = (TTable) this.getComponent(this.DIAGNOSE_TABLE);
		this.operatorDiagnoseTable = (TTable) this
				.getComponent(this.OPERATOR_DIAGNOSE_TABLE);
		this.CLNCPATH_CODE = (TTextField) this.getComponent("CLNCPATH_CODE");
		this.CLNCPATH_CHN_DESC = (TTextField) this
				.getComponent("CLNCPATH_CHN_DESC");
		this.CLNCPATH_ENG_DESC = (TTextField) this
				.getComponent("CLNCPATH_ENG_DESC");
		this.PY1 = (TTextField) this.getComponent("PY1");
		this.PY2 = (TTextField) this.getComponent("PY2");
		this.DEPT_CODE = (TextFormatDept) this.getComponent("DEPT_CODE");
		this.FRSTVRSN_DATE = (TTextFormat) this.getComponent("FRSTVRSN_DATE");
		this.LASTVRSN_DATE = (TTextFormat) this.getComponent("LASTVRSN_DATE");
		this.MODIFY_TIMES = (TTextField) this.getComponent("MODIFY_TIMES");
		this.ACPT_CODE = (TTextArea) this.getComponent("ACPT_CODE");
		this.EXIT_CODE = (TTextArea) this.getComponent("EXIT_CODE");
		this.STAYHOSP_DAYS = (TTextField) this.getComponent("STAYHOSP_DAYS");
		this.AVERAGECOST = (TTextField) this.getComponent("AVERAGECOST");
		this.VERSION = (TTextField) this.getComponent("VERSION");
		this.DESCRIPTION = (TTextField) this.getComponent("DESCRIPTION");
		this.ACTIVE_FLG = (TCheckBox) this.getComponent("ACTIVE_FLG");
		this.CLP_BSCINFO = (TTable) this.getComponent("CLP_BSCINFO");
		this.diagnose = (TTextField) this.getComponent("diagnose");
		this.diagnose_desc = (TTextField) this.getComponent("diagnose_desc");
		this.operation_diagnose = (TTextField) this
				.getComponent("operation_diagnose");
		this.operation_diagnose_desc = (TTextField) this
				.getComponent("operation_diagnose_desc");
		/**
		 * 第二个页签的控件 治疗时程 CLP_THRPYSCHDM
		 */
		this.SCHD_CODE = (TextFormatCLPDuration) this.getComponent("SCHD_CODE");
		this.CLNCPATH_CODE01 = (TTextFormat) this
				.getComponent("CLNCPATH_CODE01");
		this.SCHD_DAY = (TTextField) this.getComponent("SCHD_DAY");
		this.SUSTAINED_DAYS = (TTextField) this.getComponent("SUSTAINED_DAYS");
		this.CLP_THRPYSCHDM = (TTable) this.getComponent("CLP_THRPYSCHDM");
		/**
		 * 第三个页签的控件 医嘱套餐 CLP_PACK
		 */
		this.CLNCPATH_CODE02 = (TTextFormat) this
				.getComponent("CLNCPATH_CODE02");
		this.SCHD_CODE01 = (TTextFormat) this.getComponent("SCHD_CODE01");
		this.FEES = (TTextField) this.getComponent("FEES");
		this.VERSION01 = (TTextField) this.getComponent("VERSION01");
		this.CLP_PACK01 = (TTable) this.getComponent("CLP_PACK01");
		/**
		 * 第四个页签的控件 关键诊疗套餐 CLP_PACK
		 */
		this.CLNCPATH_CODE03 = (TTextFormat) this
				.getComponent("CLNCPATH_CODE03");
		this.SCHD_CODE02 = (TTextFormat) this.getComponent("SCHD_CODE02");
		this.VERSION02 = (TTextField) this.getComponent("VERSION02");
		this.CLP_PACK02 = (TTable) this.getComponent("CLP_PACK02");
		/**
		 * 第五个页签的控件 护理计划 CLP_PACK
		 */
		this.CLNCPATH_CODE04 = (TTextFormat) this
				.getComponent("CLNCPATH_CODE04");
		this.SCHD_CODE03 = (TTextFormat) this.getComponent("SCHD_CODE03");
		this.VERSION03 = (TTextField) this.getComponent("VERSION03");
		this.CLP_PACK03 = (TTable) this.getComponent("CLP_PACK03");
	}

	/**
	 * 注册监听事件
	 */
	public void initControler() {
		// 临床路径类别
		callFunction("UI|CLP_BSCINFO|addEventListener", "CLP_BSCINFO->"
				+ TTableEvent.CLICKED, this, "onTableClickedForBscInfo");
		// 治疗时程
		callFunction("UI|CLP_THRPYSCHDM|addEventListener", "CLP_THRPYSCHDM->"
				+ TTableEvent.CLICKED, this, "onTableClickedForThrpyschdm");
		// 医嘱套餐
		callFunction("UI|CLP_PACK01|addEventListener", "CLP_PACK01->"
				+ TTableEvent.CLICKED, this, "onTableClickedForPack01");
		// 关键诊疗套餐
		callFunction("UI|CLP_PACK02|addEventListener", "CLP_PACK02->"
				+ TTableEvent.CLICKED, this, "onTableClickedForPack02");
		// 护理计划
		callFunction("UI|CLP_PACK03|addEventListener", "CLP_PACK03->"
				+ TTableEvent.CLICKED, this, "onTableClickedForPack03");
		// 诊断ICD
		getTextField("diagnose_desc")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// 定义诊断ICD接受返回值方法
		getTextField("diagnose_desc").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popDiagReturn");
		// 诊断ICD结束
		getTextField("diagnose_desc_end")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// 定义诊断ICD结束接受返回值方法
		getTextField("diagnose_desc_end").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popDiagReturnEnd");
		// 手术ICD
		getTextField("operation_diagnose_desc")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// 定义手术ICD接受返回值方法
		getTextField("operation_diagnose_desc").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popOperatorDiagReturn");
		// 手术ICD end
		getTextField("operation_diagnose_desc_end")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// 定义手术ICD end 接受返回值方法
		getTextField("operation_diagnose_desc_end").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popOperatorDiagReturnEnd");
		// //诊断ICD
		// callFunction("UI|diagnose_desc|addEventListener",
		// "diagnose_desc->" + TTextFieldEvent.KEY_RELEASED, this,
		// "popoDiagnose");
		// //手术ICD
		// callFunction("UI|operation_diagnose_desc|addEventListener",
		// "operation_diagnose_desc->" + TTextFieldEvent.KEY_RELEASED, this,
		// "popoOperatorDiagnose");
		// 医嘱套餐 临床路径项目 医嘱名称 列触发
		CLP_PACK01.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreatePACK01");
		// 医嘱套餐 用量 单位 日份 列触发
		this.addEventListener("CLP_PACK01->" + TTableEvent.CHANGE_VALUE, this,
				"onTableChangeValueForPack01");
		// 关键诊疗项目 临床路径项目 医嘱名称 列触发
		CLP_PACK02.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreatePACK02");
		// 护理计划 临床路径项目 护嘱名称 列触发
		CLP_PACK03.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreatePACK03");
		// 卢海加入20110621 begin
		// 长期TABLE值改变监听
		addEventListener("CLP_PACK01->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableCLPPACK01Value");
		// 卢海加入20110621 end
		// ====pangben 2012-6-22
		addListener(CLP_BSCINFO);
		addListenerOne(CLP_PACK01);
	}

	/**
	 * 计算总价
	 */
	public void onCountFees() {
		double fees = 0.0;
		for (int i = 0; i < CLP_PACK01.getRowCount(); i++) {
			String fee = String.valueOf(CLP_PACK01.getItemData(i, "FEES"));
			if (checkInputString(fee)) {
				fees = fees + Double.parseDouble(fee);
			}
		}
		TTextField FEES = (TTextField) this.getComponent("FEES");
		FEES.setValue(String.valueOf(StringTool.round(fees, 2)));
	}

	/**
	 * 增加对CLP_BSCINFO的监听
	 * 
	 * @param row
	 *            行号
	 */
	public void onTableClickedForBscInfo(int row) {
		if (row < 0) {
			return;
		}
		// 获取正在编辑状态的数据
		CLP_BSCINFO.acceptText();
		// 路径代码不可编辑
		CLNCPATH_CODE.setEditable(false);
		// 版本不可编辑
		VERSION.setEditable(false);
		// 是否启用标识不可编辑
		ACTIVE_FLG.setEnabled(false);
		// 标准住院天数不可编辑
		STAYHOSP_DAYS.setEditable(false);
		/*
		 * 获取选中的数据，将这些数据设置到各个输入控件中
		 */
		REGION_CODE.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getParmValue().getData("REGION_CODE", row))));
		CLNCPATH_CODE.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "CLNCPATH_CODE"))));
		// ============pangben 2012-6-12 start
		SCHD_CODE.setSqlFlg("Y");
		SCHD_CODE.setClncpathCode(this.getValueString("CLNCPATH_CODE"));
		SCHD_CODE.onQuery();
		CLNCPATH_CHN_DESC.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "CLNCPATH_CHN_DESC"))));
		CLNCPATH_ENG_DESC.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "CLNCPATH_ENG_DESC"))));
		PY1.setValue(nullToString(String.valueOf(CLP_BSCINFO.getItemData(row,
				"PY1"))));
		PY2.setValue(nullToString(String.valueOf(CLP_BSCINFO.getItemData(row,
				"PY2"))));
		DEPT_CODE.setValue(nullToString(String.valueOf(CLP_BSCINFO.getItemData(
				row, "DEPT_CODE"))));
		FRSTVRSN_DATE.setValue(nullToString(String.valueOf(dateFormat
				.format(CLP_BSCINFO.getItemData(row, "FRSTVRSN_DATE")))));
		LASTVRSN_DATE.setValue(nullToString(String.valueOf(dateFormat
				.format(CLP_BSCINFO.getItemData(row, "LASTVRSN_DATE")))));
		MODIFY_TIMES.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "MODIFY_TIMES"))));
		ACPT_CODE.setValue(nullToString(String.valueOf(CLP_BSCINFO.getItemData(
				row, "ACPT_CODE"))));
		EXIT_CODE.setValue(nullToString(String.valueOf(CLP_BSCINFO.getItemData(
				row, "EXIT_CODE"))));
		STAYHOSP_DAYS.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "STAYHOSP_DAYS"))));
		AVERAGECOST.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "AVERAGECOST"))));
		VERSION.setValue(nullToString(String.valueOf(CLP_BSCINFO.getItemData(
				row, "VERSION"))));
		DESCRIPTION.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "DESCRIPTION"))));
		ACTIVE_FLG.setValue(CLP_BSCINFO.getItemData(row, "ACTIVE_FLG"));
		diagnose.setValue(nullToString(String.valueOf(CLP_BSCINFO.getItemData(
				row, "ICD_CODE"))));
		this.diagnose_desc.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "ICD_DESC"))));
		this.operation_diagnose.setValue(nullToString(String
				.valueOf(CLP_BSCINFO.getItemData(row, "OPE_ICD_CODE"))));
		this.operation_diagnose_desc.setValue(nullToString(String
				.valueOf(CLP_BSCINFO.getItemData(row, "OPE_DESC"))));
		// 诊断
		String clncPathCode = nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "CLNCPATH_CODE")));
		TParm selectTparm = new TParm();
		selectTparm.setData("CLNCPATH_CODE", clncPathCode);
		TParm resulticd = BscInfoTool.getInstance().selectDiagICDList(
				selectTparm);
		TParm newresulticd = new TParm();
		for (int i = 0; i < resulticd.getCount(); i++) {
			String[] names = resulticd.getNames();
			for (int j = 0; j < names.length; j++) {
				newresulticd.addData(names[j].toLowerCase(), resulticd
						.getValue(names[j], i));
			}
		}
		this.diagnoseTable.setParmValue(newresulticd);
		TParm resultopticd = BscInfoTool.getInstance().selectOptICDList(
				selectTparm);
		TParm newresultopticd = new TParm();
		for (int i = 0; i < resultopticd.getCount(); i++) {
			String[] names = resultopticd.getNames();
			for (int j = 0; j < names.length; j++) {
				newresultopticd.addData(names[j].toLowerCase(), resultopticd
						.getValue(names[j], i));
			}
		}
		this.operatorDiagnoseTable.setParmValue(newresultopticd);
		// 加入默认行
		addDefaultRowForDiag(this.DIAGNOSE_TABLE);
		addDefaultRowForDiag(this.OPERATOR_DIAGNOSE_TABLE);
	}

	public void onClncpathCodeEdit() {
		String s1 = CLNCPATH_CODE.getValue();
		if (checkInputString(s1)) {
			s1 = s1.toUpperCase();
			CLNCPATH_CODE.setValue(s1);
			for (int i = 0; i < CLP_BSCINFO.getRowCount(); i++) {
				String s2 = String.valueOf(CLP_BSCINFO.getItemData(i,
						"CLNCPATH_CODE"));
				if (s1.equalsIgnoreCase(s2)) {
					CLP_BSCINFO.setSelectedRow(i);
					onTableClickedForBscInfo(i);
				}
			}
			// 新增临床路径类别 默认当天
			if (CLP_BSCINFO.getSelectedRow() < 0) {
				FRSTVRSN_DATE.setValue(dateFormat.format(SystemTool
						.getInstance().getDate()));
			}
		}
	}

	/**
	 * 增加对CLP_THRPYSCHDM的监听
	 * 
	 * @param row
	 *            行号
	 */
	public void onTableClickedForThrpyschdm(int row) {
		if (row < 0) {
			return;
		}
		// 获取正在编辑状态的数据
		CLP_THRPYSCHDM.acceptText();
		// 路径代码不可编辑
		CLNCPATH_CODE01.setEnabled(false);
		// 时程代码不可编辑
		SCHD_CODE.setEnabled(false);
		/*
		 * 获取选中的数据，将这些数据设置到各个输入控件中
		 */
		String schdCode = CLP_THRPYSCHDM.getParmValue().getValue("SCHD_CODE",
				row);
		SCHD_CODE.setValue(nullToString(schdCode));
		SCHD_DAY.setValue(nullToString(String.valueOf(CLP_THRPYSCHDM
				.getItemData(row, "SCHD_DAY"))));
		SUSTAINED_DAYS.setValue(nullToString(String.valueOf(CLP_THRPYSCHDM
				.getItemData(row, "SUSTAINED_DAYS"))));
	}

	/**
	 * 监听列表单击事件 判断点击的单元格是否可以编辑
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClickedForPack01(int row) {
		if (row < 0) {
			return;
		}
		// 设置版本
		// VERSION01.setValue(String.valueOf(CLP_PACK01.getItemData(row,
		// "VERSION")));
		int column = CLP_PACK01.getSelectedColumn();
		String version = String.valueOf(CLP_PACK01.getItemData(row, "VERSION"));
		if (version == null || "".equals(version.trim())) {
			/** 新增数据 **/
			// 临床路径项目、医嘱信息校验
			String orderCode = String.valueOf(CLP_PACK01.getItemData(row,
					"ORDER_CHN_DESC"));
			String ordTypeCode = String.valueOf(CLP_PACK01.getItemData(row,
					"TYPE_CHN_DESC"));
			// 选 列不可编辑
			if (column == 0) {
				return;
			} else if (!checkOrderCode(orderCode)
					|| !checkOrdTypeCode(ordTypeCode)) {
				// 医嘱类别 临床路径类别 医嘱名称 核查类别可编辑
				if (column == 2 || column == 3 || column == 4 || column == 18) {
					setTableEnabled(CLP_PACK01, row, column);
				}
				// 其它项目不可编辑
				else {
					return;
				}
			} else {
				// 卢海删除
				// column == 1 ||
				if (column == 7 || column == 12 || column == 13 || column == 14
						|| column == 22) {
					// 医嘱类别 单位 总量 单价 总金额 版本不可编辑
					return;
				} else {
					// 其它项目可编辑
					setTableEnabled(CLP_PACK01, row, column);
				}
			}
		} else {
			/** 修改数据 **/
			// 临床路径项目、医嘱信息校验
			String orderCode = String.valueOf(CLP_PACK01.getItemData(row,
					"ORDER_CHN_DESC"));
			String ordTypeCode = String.valueOf(CLP_PACK01.getItemData(row,
					"TYPE_CHN_DESC"));
			// 选 列可以编辑
			if (column == 0) {
				setTableEnabled(CLP_PACK01, row, column);
			} else if (!checkOrderCode(orderCode)
					|| !checkOrdTypeCode(ordTypeCode)) {
				// column == 1 || 卢海删除
				if (column == 3 || column == 4 || column == 18) {
					// 医嘱类别 临床路径类别 医嘱名称 核查类别可编辑
					setTableEnabled(CLP_PACK01, row, column);
				} else {
					// 其它项目不可编辑
					return;
				}
			} else {
				// 卢海删除column == 1 ||
				if (column == 3 || column == 4 || column == 5 || column == 7
						|| column == 12 || column == 13 || column == 14
						|| column == 22) {
					// 医嘱类别 临床路径类别 医嘱名称 单位 总量 总金额 版本不可编辑
					return;
				} else {
					// 其它项目可编辑
					setTableEnabled(CLP_PACK01, row, column);
				}
			}
		}
	}

	/**
	 * 单击事件
	 */
	public void onTableClicked() {
		TParm parm = CLP_PACK01.getParmValue();
		String orderCode = parm.getValue("ORDER_CODE", CLP_PACK01
				.getSelectedRow());
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
	}

	/**
	 * 医嘱套餐 临床路径项目 医嘱名称 列触发
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreatePACK01(Component com, int row, int column) {
		if (row < 0) {
			return;
		}
		// 根据医嘱类别设定频次begin
		// setFreqSelectType(row);
		// 根据医嘱类别设定频次end
		// System.out.println(
		// "--------------------编辑单元格-----------------------------------");
		if (column == 3) {
			// 触发临床路径项目 获取医嘱默认信息
			if (!(com instanceof TTextField)) {
				return;
			}
			TTextField textFilter = (TTextField) com;
			textFilter.onInit();
			TParm parm = new TParm();
			parm.setData("ORDER_FLG", "Y");
			this.putBasicSysInfoIntoParm(parm);
			textFilter.setPopupMenuParameter("2", getConfigParm().newConfig(
					"%ROOT%\\config\\clp\\CLPOrdTypeSysFeePopup.x"), parm);
			// 定义接受返回值方法
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popReturnForPack01");
		}
		if (column == 4) {
			// 触发医嘱名称 获取医嘱默认信息
			if (!(com instanceof TTextField)) {
				return;
			}
			TTextField textFilter = (TTextField) com;
			textFilter.onInit();
			// 临床路径项目
			String ordTypeCode = CLP_PACK01.getParmValue().getValue(
					"ORDTYPE_CODE", row);
			TParm parm = new TParm();
			parm.setData("ORDER_FLG", "Y");
			parm.setData("ORDTYPE_CODE", ordTypeCode);
			parm.setData("REGION_CODE", Operator.getRegion());
			//this.putBasicSysInfoIntoParm(parm);
			textFilter.setPopupMenuParameter("3", getConfigParm().newConfig(
					"%ROOT%\\config\\clp\\CLPSysFeePopup.x"), parm);
			// 定义接受返回值方法
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popReturnForPack01");
		}
	}

	// /**
	// * 根据医嘱类型筛选不同的频次-
	// * @param selectedRow int
	// */
	// private void setFreqSelectType(int selectedRow){
	// TTable tablePack1 = (TTable)this.getComponent("CLP_PACK01");
	// tablePack1.acceptText();
	// TParm tableParm = tablePack1.getParmValue();
	// TParm rowParm = tableParm.getRow(selectedRow);
	// String orderType = rowParm.getValue("ORDER_TYPE");
	// TextFormatSYSPhaFreq freq =
	// (TextFormatSYSPhaFreq)this.getComponent("FREQ_CODE");
	// String statFlg="Y";
	// if("ST".equals(orderType)){
	// statFlg="Y";
	// }else{
	// statFlg="N";
	// }
	// System.out.println("设置值：------------"+statFlg+"orderTYe--"+orderType);
	// freq.setStatFlg(statFlg);
	// freq.onQuery();
	// }
	/**
	 * 医嘱套餐 临床路径项目 医嘱名称 列触发
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturnForPack01(String tag, Object obj) {
		// 判断对象是否为空和是否为TParm类型
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		// 类型转换成TParm
		TParm result = (TParm) obj;
		//System.out.println("result:::::::::"+result);
		// double ownPriceSingle = result.getDouble("OWN_PRICE");// 自费金额
		// 状态条获得医嘱信息
		callFunction("UI|setSysStatus", result.getValue("ORDER_CODE")
				+ result.getValue("ORDER_DESC") + result.getValue("GOODS_DESC")
				+ result.getValue("DESCRIPTION")
				+ result.getValue("SPECIFICATION"));
		// ===========pangben 2012-6-25 start 集合医嘱计算金额
		if ("Y".equals(result.getValue("ORDERSET_FLG"))) {
			double allOwnAmt = 0.00;
			TParm parmDetail = SYSOrderSetDetailTool.getInstance()
					.selectByOrderSetCode(result.getValue("ORDER_CODE"));
			for (int j = 0; j < parmDetail.getCount(); j++) {
				allOwnAmt = allOwnAmt + parmDetail.getDouble("OWN_PRICE", j)
						* parmDetail.getDouble("TOTQTY", j);
			}
			result.setData("OWN_PRICE", allOwnAmt);
		}
		// ===========pangben 2012-6-25 stop
		// 临床路径项目 调用
		if ("2".equals(tag)) {
			String ordTypeCode = result.getValue("TYPE_CODE");
			int count = getOrderByOrdTypeCode(ordTypeCode, "Y", "").getCount();
			if (count <= 0) {
				this.messageBox("此临床路径项目没有医嘱信息，请重新选择!");
				// 清空临床路径项目
				int row = CLP_PACK01.getSelectedRow();
				int column = CLP_PACK01.getSelectedColumn();
				CLP_PACK01.setItem(row, column, "");
				CLP_PACK01.setValueAt("", row, column);
				return;
			}
			// 带出并设置完整医嘱信息
			setCLP_PACK01(result);
		}
		// 医嘱名称 调用
		if ("3".equals(tag)) {
			int count = getOrdTypeCode(result.getValue("ORDER_CODE"), "Y")
					.getCount();
			if (count <= 0) {
				this.messageBox("此医嘱不属于任何一个临床路径项目，请重新选择！");
				// 清空医嘱名称
				int row = CLP_PACK01.getSelectedRow();
				int column = CLP_PACK01.getSelectedColumn();
				CLP_PACK01.setItem(row, column, "");
				CLP_PACK01.setValueAt("", row, column);
				return;
			}
			// 带出并设置完整医嘱信息
			setCLP_PACK01(result);
		}

	}

	/**
	 * 医嘱套餐
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setCLP_PACK01(TParm parm) {
		/************************* 全局信息 *************************/
		CLP_PACK01.acceptText();
		// 获取行号
		int selRow = CLP_PACK01.getSelectedRow();
		// 院区代码
		parm.setData("REGION_CODE", REGION_CODE.getValue());
		// 完整信息
		TParm result = new TParm();
		// 医嘱大分类
		String cat1Type = parm.getValue("CAT1_TYPE");
		String ordTypeCode = parm.getValue("TYPE_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		String typeChnDesc = parm.getValue("TYPE_CHN_DESC");
		/************************* 全局信息 *************************/

		/************************* 预设信息 *************************/
		// 选
		result.setData("SEL_FLG", false);
		// 医嘱类别
		// String orderType = String.valueOf(CLP_PACK01.getItemData(selRow, 1));
		String orderType = "ST";// =====pangben 2012-6-19
		result.setData("ORDER_TYPE", orderType);
		/************************* 预设信息 *************************/

		/************************* 默认信息 *************************/
		// 接收到临床路径项目和医嘱代码（即选择临床路径项目，存在默认医嘱的情形）
		if ((checkInputString(ordTypeCode) && checkInputString(typeChnDesc))
				&& checkInputString(orderCode)) {
			// 临床路径项目
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			// 医嘱名称
			String orderDesc = parm.getValue("ORDER_CHN_DESC");
			result.setData("ORDER_CHN_DESC", orderDesc);
			// 用量 频次 单位 途径（用法）
			TParm phaResult = PackTool.getInstance()
					.getDefaultPHABaseInfo(parm);
			String dose = phaResult.getValue("MEDI_QTY", 0);// old:DEFAULT_TOTQTY
			String doseUnit = phaResult.getValue("MEDI_UNIT", 0);// old:DOSAGE_UNIT
			String freqCode = phaResult.getValue("FREQ_CODE", 0);
			String routeCode = phaResult.getValue("ROUTE_CODE", 0);
			String takeDays = phaResult.getValue("TAKE_DAYS", 0);
			if ("PHA".equals(cat1Type)) {
				/** 药品类医嘱 */
				// 用量
				result.setData("DOSE", dose);
				// 单位
				result.setData("DOSE_UNIT", doseUnit);
				// 频次
				if ("ST".equals(orderType)) {
					result.setData("FREQ_CODE", "STAT");
				} else if ("UD".equals(orderType)) {
					result.setData("FREQ_CODE", freqCode);
				}
				// 途径（用法）
				result.setData("ROUT_CODE", routeCode);
				// 日份
				if ("ST".equals(orderType)) {
					result.setData("DOSE_DAYS", "1");
				} else if ("UD".equals(orderType)) {
					result.setData("DOSE_DAYS", takeDays);
				}
				result.setData("MEDI_QTY", phaResult.getDouble("MEDI_QTY", 0));//配药数量
			} else {
				/** 非药品类医嘱（其它） */
				// 用量
				result.setData("DOSE", "1");
				// 单位
				result.setData("DOSE_UNIT", parm.getValue("UNIT_CODE"));
				// 频次
				if ("ST".equals(orderType)) {
					result.setData("FREQ_CODE", "STAT");
				} else if ("UD".equals(orderType)) {
					result.setData("FREQ_CODE", ".");
				}
				// 途径（用法）
				result.setData("ROUT_CODE", "");
				// 日份
				result.setData("DOSE_DAYS", "1");
				result.setData("MEDI_QTY", "1");//配药数量
			}
			// 第几天 ====pangben 2012-6-19
			result.setData("START_DAY", "1");
			// 总量
			result.setData("TOTAL", "1");
			// 自费单价
			result.setData("OWN_PRICE", parm.getValue("OWN_PRICE"));
			// 总金额（当前记录）
			result.setData("FEES", "1");
			// 医嘱备注 手动输入
			result.setData("NOTE", "");
			// 执行人员
			result.setData("CHKUSER_CODE", "001");
			// 执行科室
			result
					.setData("RBORDER_DEPT_CODE", parm
							.getValue("EXEC_DEPT_CODE"));
			// 核查类别
			TParm chkTypeResult = PackTool.getInstance()
					.getDefaultChkTypeByOrderCat1(parm);
			String chkTypeCode = chkTypeResult.getValue("CHKTYPE_CODE", 0);
			result.setData("CHKTYPE_CODE", chkTypeCode);
			// 急坐
			result.setData("URGENT_FLG", "");
			// 必执
			result.setData("EXEC_FLG", "");
			// 变异标准差
			result.setData("STANDARD", "");
			// 版本
			result.setData("VERSION", this.getValue("VERSION01"));
			/************************* 隐藏信息 *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// CAT1_TYPE
			result.setData("CAT1_TYPE", cat1Type);
			
			result.setData("DISPENSE_UNIT", phaResult.getDouble(
					"DISPENSE_UNIT", 0));// 发药单位
			result.setData("MEDI_UNIT", phaResult.getValue("MEDI_UNIT", 0));// 
			result.setData("TAKE_DAYS", result.getDouble("DOSE_DAYS"));// 
			result.setData("DOSAGE_QTY", phaResult.getDouble("DOSAGE_QTY", 0));// 
			result.setData("ACTIVE_FLG", "Y");// 启用状态
			/************************* 隐藏信息 *************************/
			// 卢海加入
			// 计算总量和总价
			// =============pangben 2012-6-21 start 显示总量 金额
			addTotalAndTotalPrice(result);
			this.addExeDept(result);
			/************************* 其他操作 *************************/
			TParm tableParm = CLP_PACK01.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK01.setParmValue(tableParm, CLP_PACK01.getParmMap());
			if (selRow >= CLP_PACK01.getRowCount() - 1) {
				insertNewRow(CLP_PACK01, selRow);
			}
			/************************* 其他操作 *************************/
		}
		// 接收到医嘱代码，没有接受到临床路径项目代码（即选择医嘱名称，反查询临床路径项目）
		else if ((!checkInputString(ordTypeCode) || !checkInputString(typeChnDesc))
				&& checkInputString(orderCode)) {
			// 根据医嘱代码查询临床路径项目代码
			TParm ordTypeCodeResult = getOrdTypeCode(orderCode, "Y");
			ordTypeCode = ordTypeCodeResult.getValue("ORDTYPE_CODE", 0);
			typeChnDesc = ordTypeCodeResult.getValue("TYPE_CHN_DESC", 0);
			// 临床路径项目
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			// 医嘱名称
			String orderDesc = parm.getValue("ORDER_CHN_DESC");
			result.setData("ORDER_CHN_DESC", orderDesc);
			// 用量 频次 单位 途径（用法）
			TParm phaResult = PackTool.getInstance()
					.getDefaultPHABaseInfo(parm);
			String dose = phaResult.getValue("MEDI_QTY", 0);// DEFAULT_TOTQTY
			String doseUnit = phaResult.getValue("MEDI_UNIT", 0);// DOSAGE_UNIT
			String freqCode = phaResult.getValue("FREQ_CODE", 0);
			String routeCode = phaResult.getValue("ROUTE_CODE", 0);
			String takeDays = phaResult.getValue("TAKE_DAYS", 0);
			if ("PHA".equals(cat1Type)) {
				/** 药品类医嘱 */
				// 用量
				result.setData("DOSE", dose);
				// 单位
				result.setData("DOSE_UNIT", doseUnit);
				// 频次
				if ("ST".equals(orderType)) {
					result.setData("FREQ_CODE", "STAT");
				} else if ("UD".equals(orderType)) {
					result.setData("FREQ_CODE", freqCode);
				}
				// 途径（用法）
				result.setData("ROUT_CODE", routeCode);
				// 日份
				if ("ST".equals(orderType)) {
					result.setData("DOSE_DAYS", "1");
				} else if ("UD".equals(orderType)) {
					result.setData("DOSE_DAYS", takeDays);
				}
				result.setData("MEDI_QTY", phaResult.getDouble("MEDI_QTY", 0));// 配药数量
			} else {
				/** 非药品类医嘱（其它） */
				// 用量
				result.setData("DOSE", "1");
				// 单位
				result.setData("DOSE_UNIT", parm.getValue("UNIT_CODE"));
				// 频次
				if ("ST".equals(orderType)) {
					result.setData("FREQ_CODE", "STAT");
				} else if ("UD".equals(orderType)) {
					result.setData("FREQ_CODE", ".");
				}
				// 途径（用法）
				result.setData("ROUT_CODE", "");
				// 日份
				result.setData("DOSE_DAYS", "1");
				result.setData("MEDI_QTY", "1");// 配药数量
			}
			// 第几天 ====pangben 2012-6-19
			result.setData("START_DAY", "1");
			// 总量
			result.setData("TOTAL", "1");
			// 自费单价
			result.setData("OWN_PRICE", parm.getValue("OWN_PRICE"));
			// 总金额（当前记录）
			result.setData("FEES", parm.getValue("OWN_PRICE"));
			// 医嘱备注 手动输入
			result.setData("NOTE", "");
			// 执行人员
			result.setData("CHKUSER_CODE", "001");
			// 执行科室
			result
					.setData("RBORDER_DEPT_CODE", parm
							.getValue("EXEC_DEPT_CODE"));
			// 核查类别
			TParm chkTypeResult = PackTool.getInstance()
					.getDefaultChkTypeByOrderCat1(parm);
			String chkTypeCode = chkTypeResult.getValue("CHKTYPE_CODE", 0);
			result.setData("CHKTYPE_CODE", chkTypeCode);
			// 急坐
			result.setData("URGENT_FLG", "");
			// 必执
			result.setData("EXEC_FLG", "");
			// 变异标准差
			result.setData("STANDARD", "");
			// 版本
			result.setData("VERSION", this.getValue("VERSION01"));
			/************************* 隐藏信息 *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// CAT1_TYPE
			result.setData("CAT1_TYPE", cat1Type);
			
			result.setData("DISPENSE_UNIT", phaResult.getDouble(
					"DISPENSE_UNIT", 0));// 发药单位
			result.setData("MEDI_UNIT", phaResult.getValue("MEDI_UNIT", 0));// 发药单位
			result.setData("TAKE_DAYS", result.getDouble("DOSE_DAYS"));// 日份
			result.setData("DOSAGE_QTY", phaResult.getDouble("DOSAGE_QTY", 0));// 
			result.setData("ACTIVE_FLG", "Y");// 启用状态
			// 卢海加入
			addTotalAndTotalPrice(result);
			addExeDept(result);
			/************************* 隐藏信息 *************************/

			/************************* 其他操作 *************************/
			TParm tableParm = CLP_PACK01.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK01.setParmValue(tableParm, CLP_PACK01.getParmMap());
			if (selRow >= CLP_PACK01.getRowCount() - 1) {
				insertNewRow(CLP_PACK01, selRow);
			}
			/************************* 其他操作 *************************/
		}
		// 接收到临床路径项目代码，没有接收到医嘱代码（即选择临床路径项目，不存在默认医嘱的情形）
		else if ((checkInputString(ordTypeCode) && checkInputString(typeChnDesc))
				&& !checkInputString(orderCode)) {
			// 临床路径项目
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			/************************* 隐藏信息 *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// CAT1_TYPE
			result.setData("CAT1_TYPE", cat1Type);
			/************************* 隐藏信息 *************************/

			/************************* 其他操作 *************************/
			TParm tableParm = CLP_PACK01.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK01.setParmValue(tableParm, CLP_PACK01.getParmMap());
			/************************* 其他操作 *************************/

		}
		/************************* 默认信息 *************************/
		// 卢海加入-20110630 begin
		TParm tableParm = CLP_PACK01.getParmValue();
		// 删除
		// tableParm.setData("CHKTYPE_CODE", selRow, getDefaultCheckTypeCode());
		CLP_PACK01.setParmValue(tableParm, CLP_PACK01.getParmMap());
		// 卢海加入-20110630 end
	}

	/**
	 * 加入总价和总金额
	 * 
	 * @param result
	 *            TParm
	 */
	private void addTotalAndTotalPrice(TParm result) {
		boolean isvalid = false;
		if (this.checkNullAndEmpty(result.getData("DOSE") + "")
				&& this.checkNullAndEmpty(result.getData("DOSE_UNIT") + "")
				&& this.checkNullAndEmpty(String.valueOf(result
						.getData("FREQ_CODE")))
				&& this.checkNullAndEmpty(String.valueOf(result
						.getData("DOSE_DAYS")))
				&& this.checkNullAndEmpty(String.valueOf(result
						.getData("ORDER_CODE")))) {
			isvalid = true;
		}
		if (!isvalid) {
			return;
		}
		// ==============pangben 2012-6-21 start 药品计算总量 金额
		TParm totParm = TotQtyTool.getInstance().getTotQty(result);
		// result.setData("DOSE",totParm.getDouble("QTY"));
		// if (null != result.getValue("CAT1_TYPE")
		// && "PHA".equals(result.getValue("CAT1_TYPE"))) {
		// // result.setData("DOSE",result.getDouble("DOSAGE_QTY"));
		// result.setData("DOSE", result.getDouble("DOSE")
		// / result.getDouble("MEDI_QTY"));
		// }
//		TParm parm = getTotalAndFees(totParm.getDouble("QTY"), result
//				.getValue("DOSE_UNIT"), result.getValue("FREQ_CODE"), result
//				.getInt("DOSE_DAYS"),
//				getOwnPrice(result.getValue("ORDER_CODE")));
		// 设置当前记录的总量和总金额
		result.setData("TOTAL", totParm.getDouble("QTY"));
		result.setData("FEES", totParm.getDouble("QTY")*result.getDouble("OWN_PRICE"));
	}

	/**
	 * 加入执行科室
	 * 
	 * @param result
	 *            TParm
	 */
	private void addExeDept(TParm result) {
		boolean isvalid = false;
		if (this
				.checkNullAndEmpty(String.valueOf(result.getData("ORDER_CODE")))) {
			isvalid = true;
		}
		if (!isvalid) {
			return;
		}
		TParm selectParm = new TParm();
		selectParm.setData("ORDER_CODE", result.getValue("ORDER_CODE"));
		TParm resultData = PackTool.getInstance().getExecDeptWithOrderCode(
				selectParm);
		// 加入执行科室信息
		String deptCode = resultData.getValue("EXEC_DEPT_CODE", 0);
		if (deptCode != null) {
			result.setData("RBORDER_DEPT_CODE", deptCode);
		}
	}

	/**
	 * 得到默认查核类型
	 * 
	 * @return String
	 */
	private String getDefaultCheckTypeCode() {
		TParm selectParm = new TParm();
		selectParm.setData("REGION_CODE", Operator.getRegion());
		TParm result = ClpchkTypeTool.getInstance().selectAllCheckCode(
				selectParm);
		if (result.getCount() > 0) {
			return result.getValue("CHKTYPE_CODE", 0);
		} else {
			return "";
		}

	}

	/**
	 * 监听列表单击事件 判断点击的单元格是否可以编辑
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClickedForPack02(int row) {
		if (row < 0) {
			return;
		}
		// 设置版本
		VERSION02.setValue(String.valueOf(CLP_PACK02
				.getItemData(row, "VERSION")));
		int column = CLP_PACK02.getSelectedColumn();
		String version = String.valueOf(CLP_PACK02.getItemData(row, "VERSION"));
		if (version == null || "".equals(version.trim())) {
			/** 新增数据 **/
			// 临床路径项目、关键诊疗项目信息校验
			String orderCode = String.valueOf(CLP_PACK02.getItemData(row,
					"ORDER_CHN_DESC"));
			String ordTypeCode = String.valueOf(CLP_PACK02.getItemData(row,
					"TYPE_CHN_DESC"));
			// 选 列不可编辑
			if (column == 0) {
				return;
			} else if (!checkOrderCode(orderCode)
					|| !checkOrdTypeCode(ordTypeCode)) {
				// 医嘱类别 临床路径类别 医嘱名称 核查类别可编辑
				if (column == 1 || column == 2 || column == 3 || column == 8) {
					setTableEnabled(CLP_PACK02, row, column);
				}
				// 其它项目不可编辑
				else {
					return;
				}
			} else {
				// 卢海删除 column == 1 ||
				if (column == 11) {
					// 医嘱类别 版本不可编辑
					return;
				} else {
					// 其它项目可编辑
					setTableEnabled(CLP_PACK02, row, column);
				}
			}
		} else {
			/** 修改数据 **/
			// 临床路径项目、医嘱信息校验
			String orderCode = String.valueOf(CLP_PACK02.getItemData(row,
					"ORDER_CHN_DESC"));
			String ordTypeCode = String.valueOf(CLP_PACK02.getItemData(row,
					"TYPE_CHN_DESC"));
			// 选 列可以编辑
			if (column == 0) {
				setTableEnabled(CLP_PACK02, row, column);
			} else if (!checkOrderCode(orderCode)
					|| !checkOrdTypeCode(ordTypeCode)) {
				// 卢海删除 column == 1 ||
				if (column == 2 || column == 3 || column == 8) {
					// 医嘱类别 临床路径类别 医嘱名称 核查类别可编辑
					setTableEnabled(CLP_PACK02, row, column);
				} else {
					// 其它项目不可编辑
					return;
				}
			} else {
				// 卢海删除 column == 1 ||
				if (column == 2 || column == 3 || column == 8 || column == 11) {
					// 医嘱类别 临床路径类别 医嘱名称 核查类别 版本不可编辑
					return;
				} else {
					// 其它项目可编辑
					setTableEnabled(CLP_PACK02, row, column);
				}
			}
		}
	}

	/**
	 * 医嘱套餐 临床路径项目 医嘱名称 列触发
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreatePACK02(Component com, int row, int column) {
		// int rowCount = CLP_PACK02.getRowCount();
		if (row < 0) {
			return;
		}
		if (column == 2) {
			// 触发临床路径项目 获取医嘱默认信息
			if (!(com instanceof TTextField)) {
				return;
			}
			TTextField textFilter = (TTextField) com;
			textFilter.onInit();
			TParm parm = new TParm();
			parm.setData("ORDER_FLG", "N");
			this.putBasicSysInfoIntoParm(parm);
			textFilter.setPopupMenuParameter("2", getConfigParm().newConfig(
					"%ROOT%\\config\\clp\\CLPOrdTypeChkItemPopup.x"), parm);
			// 定义接受返回值方法
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popReturnForPack02");
		}
		if (column == 3) {
			// 触发医嘱名称 获取医嘱默认信息
			if (!(com instanceof TTextField)) {
				return;
			}
			TTextField textFilter = (TTextField) com;
			textFilter.onInit();
			// 临床路径项目
			String ordTypeCode = CLP_PACK02.getParmValue().getValue(
					"ORDTYPE_CODE", row);
			TParm parm = new TParm();
			parm.setData("ORDER_FLG", "N");
			parm.setData("ORDTYPE_CODE", ordTypeCode);
			this.putBasicSysInfoIntoParm(parm);
			textFilter.setPopupMenuParameter("3", getConfigParm().newConfig(
					"%ROOT%\\config\\clp\\CLPChkItemPopup.x"), parm);
			// 定义接受返回值方法
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popReturnForPack02");
		}

	}

	/**
	 * 医嘱套餐 临床路径项目 医嘱名称 列触发
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturnForPack02(String tag, Object obj) {
		// 判断对象是否为空和是否为TParm类型
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		// 类型转换成TParm
		TParm result = (TParm) obj;
		// 临床路径项目 调用
		if ("2".equals(tag)) {
			String ordTypeCode = result.getValue("ORDTYPE_CODE");
			int count = getOrderByOrdTypeCode(ordTypeCode, "N", "").getCount();
			if (count <= 0) {
				this.messageBox("此临床路径项目没有关键诊疗项目信息，请重新选择!");
				// 清空临床路径项目
				int row = CLP_PACK02.getSelectedRow();
				int column = CLP_PACK02.getSelectedColumn();
				CLP_PACK02.setItem(row, column, "");
				CLP_PACK02.setValueAt("", row, column);
				return;
			}
			// 带出并设置完整医嘱信息
			setCLP_PACK02(result);
		}
		// 医嘱名称 调用
		if ("3".equals(tag)) {
			int count = getOrdTypeCode(result.getValue("ORDER_CODE"), "N")
					.getCount();
			if (count <= 0) {
				this.messageBox("此关键诊疗项目不属于任何一个临床路径项目，请重新选择！");
				// 清空医嘱名称
				int row = CLP_PACK02.getSelectedRow();
				int column = CLP_PACK02.getSelectedColumn();
				CLP_PACK02.setItem(row, column, "");
				CLP_PACK02.setValueAt("", row, column);
				return;
			}
			// 带出并设置完整医嘱信息
			setCLP_PACK02(result);
		}
	}

	/**
	 * 医嘱套餐
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setCLP_PACK02(TParm parm) {
		/************************* 全局信息 *************************/
		CLP_PACK02.acceptText();
		// 获取行号
		int selRow = CLP_PACK02.getSelectedRow();
		// 院区代码
		parm.setData("REGION_CODE", REGION_CODE.getValue());
		// 完整信息
		TParm result = new TParm();
		String ordTypeCode = parm.getValue("ORDTYPE_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		String typeChnDesc = parm.getValue("TYPE_CHN_DESC");
		/************************* 全局信息 *************************/

		/************************* 预设信息 *************************/
		// 选
		result.setData("SEL_FLG", false);
		// 医嘱类别
		String orderType = String.valueOf(CLP_PACK02.getItemData(selRow, 1));
		result.setData("ORDER_TYPE", orderType);
		/************************* 预设信息 *************************/
		/************************* 默认信息 *************************/
		// 接收到临床路径项目和医嘱代码（即选择临床路径项目，存在默认医嘱的情形）
		if ((checkInputString(ordTypeCode) && checkInputString(typeChnDesc))
				&& checkInputString(orderCode)) {
			// 临床路径项目
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			// 医嘱名称
			String orderDesc = parm.getValue("ORDER_CHN_DESC");
			result.setData("ORDER_CHN_DESC", orderDesc);
			TParm basicResult = PackTool.getInstance().getCheckItemBasicInfo(
					orderCode);
			// 用量
			// result.setData("DOSE", "1");
			result.setData("DOSE", basicResult.getValue("CLP_QTY", 0));
			// 单位
			// result.setData("DOSE_UNIT", "");
			result.setData("DOSE_UNIT", basicResult.getValue("CLP_UNIT", 0));
			// 频次
			if ("ST".equals(orderType)) {
				result.setData("FREQ_CODE", "STAT");
			} else if ("UD".equals(orderType)) {
				result.setData("FREQ_CODE", ".");
			}
			// 执行人员
			result.setData("CHKUSER_CODE", "001");
			// 核查类别
			parm.setData("ORDER_CAT1_CODE", parm.getValue("CHKTYPE_CODE"));
			TParm chkTypeResult = PackTool.getInstance()
					.getDefaultChkTypeByOrderCat1(parm);
			String chkTypeCode = chkTypeResult.getValue("CHKTYPE_CODE", 0);
			// result.setData("CHKTYPE_CODE", chkTypeCode);
			result
					.setData(
							"CHKTYPE_CODE",
							null == parm.getValue("CHKTYPE_CODE")
									|| parm.getValue("CHKTYPE_CODE").length() <= 0 ? chkTypeCode
									: parm.getValue("CHKTYPE_CODE"));
			// 必执
			result.setData("EXEC_FLG", "");
			// 变异标准差
			result.setData("STANDARD", "");
			// 版本
			result.setData("VERSION", "");
			/************************* 隐藏信息 *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// ROUT_CODE
			result.setData("ROUT_CODE", "");
			// DOSE_DAYS
			result.setData("DOSE_DAYS", "");
			// RBORDER_DEPT_CODE
			result.setData("RBORDER_DEPT_CODE", "");
			// URGENT_FLG
			result.setData("URGENT_FLG", "");
			// NOTE
			result.setData("NOTE", "");
			/************************* 隐藏信息 *************************/

			/************************* 其他操作 *************************/
			TParm tableParm = CLP_PACK02.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK02.setParmValue(tableParm, CLP_PACK02.getParmMap());
			if (selRow >= CLP_PACK02.getRowCount() - 1) {
				insertNewRow(CLP_PACK02, selRow);
			}
			/************************* 其他操作 *************************/
		}
		// 接收到医嘱代码，没有接受到临床路径项目代码（即选择医嘱名称，反查询临床路径项目）
		else if ((!checkInputString(ordTypeCode) || !checkInputString(typeChnDesc))
				&& checkInputString(orderCode)) {
			// 根据医嘱代码查询临床路径项目代码
			TParm ordTypeCodeResult = getOrdTypeCode(orderCode, "N");
			ordTypeCode = ordTypeCodeResult.getValue("ORDTYPE_CODE", 0);
			typeChnDesc = ordTypeCodeResult.getValue("TYPE_CHN_DESC", 0);
			// 临床路径项目
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			// 医嘱名称
			String orderDesc = parm.getValue("ORDER_CHN_DESC");
			result.setData("ORDER_CHN_DESC", orderDesc);
			TParm basicResult = PackTool.getInstance().getCheckItemBasicInfo(
					orderCode);
			// 用量
			// result.setData("DOSE", "1");
			result.setData("DOSE", basicResult.getValue("CLP_QTY", 0));
			// 单位
			// result.setData("DOSE_UNIT", "");
			result.setData("DOSE_UNIT", basicResult.getValue("CLP_UNIT", 0));
			// 频次
			if ("ST".equals(orderType)) {
				result.setData("FREQ_CODE", "STAT");
			} else if ("UD".equals(orderType)) {
				result.setData("FREQ_CODE", ".");
			}
			// 执行人员
			result.setData("CHKUSER_CODE", "001");
			// 核查类别
			parm.setData("ORDER_CAT1_CODE", parm.getValue("CHKTYPE_CODE"));
			TParm chkTypeResult = PackTool.getInstance()
					.getDefaultChkTypeByOrderCat1(parm);
			String chkTypeCode = chkTypeResult.getValue("CHKTYPE_CODE", 0);
			// result.setData("CHKTYPE_CODE", chkTypeCode);
			result
					.setData(
							"CHKTYPE_CODE",
							null == parm.getValue("CHKTYPE_CODE")
									|| parm.getValue("CHKTYPE_CODE").length() <= 0 ? chkTypeCode
									: parm.getValue("CHKTYPE_CODE"));
			// 必执
			result.setData("EXEC_FLG", "");
			// 变异标准差
			result.setData("STANDARD", "");
			// 版本
			result.setData("VERSION", "");
			/************************* 隐藏信息 *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// ROUT_CODE
			result.setData("ROUT_CODE", "");
			// DOSE_DAYS
			result.setData("DOSE_DAYS", "");
			// RBORDER_DEPT_CODE
			result.setData("RBORDER_DEPT_CODE", "");
			// URGENT_FLG
			result.setData("URGENT_FLG", "");
			// NOTE
			result.setData("NOTE", "");
			/************************* 隐藏信息 *************************/

			/************************* 其他操作 *************************/
			TParm tableParm = CLP_PACK02.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK02.setParmValue(tableParm, CLP_PACK02.getParmMap());
			if (selRow >= CLP_PACK02.getRowCount() - 1) {
				insertNewRow(CLP_PACK02, selRow);
			}
			/************************* 其他操作 *************************/
		}
		// 接收到临床路径项目代码，没有接收到医嘱代码（即选择临床路径项目，不存在默认医嘱的情形）
		else if ((checkInputString(ordTypeCode) && checkInputString(typeChnDesc))
				&& !checkInputString(orderCode)) {
			// 临床路径项目
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			/************************* 隐藏信息 *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// ROUT_CODE
			result.setData("ROUT_CODE", "");
			// DOSE_DAYS
			result.setData("DOSE_DAYS", "");
			// RBORDER_DEPT_CODE
			result.setData("RBORDER_DEPT_CODE", "");
			// URGENT_FLG
			result.setData("URGENT_FLG", "");
			// NOTE
			result.setData("NOTE", "");
			/************************* 隐藏信息 *************************/

			/************************* 其他操作 *************************/
			TParm tableParm = CLP_PACK02.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK02.setParmValue(tableParm, CLP_PACK02.getParmMap());
			/************************* 其他操作 *************************/
		}
		/************************* 默认信息 *************************/
		// 卢海加入-20110630 begin
		// TParm tableParm = CLP_PACK02.getParmValue();
		// tableParm.setData("CHKTYPE_CODE", selRow, getDefaultCheckTypeCode());
		// CLP_PACK02.setParmValue(tableParm, CLP_PACK02.getParmMap());
		// //luhai 20110705 解决查核类别不能正确保存问题begin
		// result.setData("CHKTYPE_CODE", parm.getValue("CHKTYPE_CODE"));
		// //luhai 20110705 解决查核类别不能正确保存问题end

		// 卢海加入-20110630 end
	}

	/**
	 * 监听列表单击事件 判断点击的单元格是否可以编辑
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClickedForPack03(int row) {
		if (row < 0) {
			return;
		}
		// 设置版本
		VERSION03.setValue(String.valueOf(CLP_PACK03
				.getItemData(row, "VERSION")));
		int column = CLP_PACK03.getSelectedColumn();
		String version = String.valueOf(CLP_PACK03.getItemData(row, "VERSION"));
		if (version == null || "".equals(version.trim())) {
			/** 新增数据 **/
			// 临床路径项目、护嘱信息校验
			String orderCode = String.valueOf(CLP_PACK03.getItemData(row,
					"ORDER_CHN_DESC"));
			String ordTypeCode = String.valueOf(CLP_PACK03.getItemData(row,
					"TYPE_CHN_DESC"));
			// 选 列不可编辑
			if (column == 0) {
				return;
			} else if (!checkOrderCode(orderCode)
					|| !checkOrdTypeCode(ordTypeCode)) {
				// 护嘱类别 临床路径类别 护嘱名称 核查类别可编辑
				if (column == 1 || column == 2 || column == 3 || column == 9) {
					setTableEnabled(CLP_PACK03, row, column);
				}
				// 其它项目不可编辑
				else {
					return;
				}
			} else {
				if (column == 1 || column == 5 || column == 13) {
					// 护嘱类别 单位 版本不可编辑
					return;
				} else {
					// 其它项目可编辑
					setTableEnabled(CLP_PACK03, row, column);
				}
			}
		} else {
			/** 修改数据 **/
			// 临床路径项目、护嘱信息校验
			String orderCode = String.valueOf(CLP_PACK03.getItemData(row,
					"ORDER_CHN_DESC"));
			String ordTypeCode = String.valueOf(CLP_PACK03.getItemData(row,
					"TYPE_CHN_DESC"));
			// 选 列可以编辑
			if (column == 0) {
				setTableEnabled(CLP_PACK03, row, column);
			} else if (!checkOrderCode(orderCode)
					|| !checkOrdTypeCode(ordTypeCode)) {
				if (column == 1 || column == 2 || column == 3 || column == 9) {
					// 护嘱类别 临床路径类别 护嘱名称 核查类别可编辑
					setTableEnabled(CLP_PACK03, row, column);
				} else {
					// 其它项目不可编辑
					return;
				}
			} else {
				if (column == 1 || column == 2 || column == 3 || column == 9
						|| column == 5 || column == 12) {
					// 医嘱类别 临床路径类别 医嘱名称 核查类别 单位 版本不可编辑
					return;
				} else {
					// 其它项目可编辑
					setTableEnabled(CLP_PACK03, row, column);
				}
			}
		}
	}

	/**
	 * 医嘱页面的表格变动事件触发方法
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onChangeTableCLPPACK01Value(Object obj) {
		// 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// 判断频次是否符合要求
		int selectedColumn = node.getTable().getSelectedColumn();
		String freqCode = (String) node.getValue();
		boolean freqFlag = true;
		if (selectedColumn == 8) {
			TTable tablePack1 = (TTable) this.getComponent("CLP_PACK01");
			TParm tableParm = tablePack1.getParmValue();
			int selectedRow = node.getRow();
			TParm selectRowParm = tableParm.getRow(selectedRow);
			String orderType = selectRowParm.getValue("ORDER_TYPE");
			TParm parm = new TParm();
			parm.setData("FREQ_CODE", freqCode);
			parm.setData("STAT_FLG", orderType.equalsIgnoreCase("ST") ? "Y"
					: "N");
			TParm result = BscInfoTool.getInstance().checkFreq(parm);
			if (result.getCount() <= 0) {
				freqFlag = false;
			} else {
				freqFlag = true;
			}
			if (!freqFlag) {
				this.messageBox("频次不能应用于该医嘱！");
				udFlg = false;// 回滚 不操作
			} else {
				udFlg = true;// 不回滚
			}

		}
		if (freqFlag) {
			return false; // 不回滚
		} else {
			return true; // 回滚
		}
	}

	/**
	 * 医嘱套餐 临床路径项目 医嘱名称 列触发
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreatePACK03(Component com, int row, int column) {
		// int rowCount = CLP_PACK03.getRowCount();
		if (row < 0) {
			return;
		}
		if (column == 2) {
			// 触发临床路径项目 获取医嘱默认信息
			if (!(com instanceof TTextField)) {
				return;
			}
			TTextField textFilter = (TTextField) com;
			textFilter.onInit();
			TParm parm = new TParm();
			parm.setData("ORDER_FLG", "O");
			this.putBasicSysInfoIntoParm(parm);
			textFilter.setPopupMenuParameter("2", getConfigParm().newConfig(
					"%ROOT%\\config\\clp\\CLPOrdTypeNursOrderPopup.x"), parm);
			// 定义接受返回值方法
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popReturnForPack03");
		}
		if (column == 3) {
			// 触发医嘱名称 获取医嘱默认信息
			if (!(com instanceof TTextField)) {
				return;
			}
			TTextField textFilter = (TTextField) com;
			textFilter.onInit();
			// 临床路径项目
			String ordTypeCode = CLP_PACK03.getParmValue().getValue(
					"ORDTYPE_CODE", row);
			TParm parm = new TParm();
			parm.setData("ORDER_FLG", "O");
			parm.setData("ORDTYPE_CODE", ordTypeCode);
			this.putBasicSysInfoIntoParm(parm);
			textFilter.setPopupMenuParameter("3", getConfigParm().newConfig(
					"%ROOT%\\config\\clp\\CLPNursOrderPopup.x"), parm);
			// 定义接受返回值方法
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popReturnForPack03");
		}
	}

	/**
	 * 医嘱套餐 临床路径项目 医嘱名称 列触发
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturnForPack03(String tag, Object obj) {
		// 判断对象是否为空和是否为TParm类型
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		// 类型转换成TParm
		TParm result = (TParm) obj;
		// 临床路径项目 调用
		if ("2".equals(tag)) {
			String ordTypeCode = result.getValue("ORDTYPE_CODE");
			int count = getOrderByOrdTypeCode(ordTypeCode, "O", "").getCount();
			if (count <= 0) {
				this.messageBox("此临床路径项目没有护嘱信息，请重新选择!");
				// 清空临床路径项目
				int row = CLP_PACK03.getSelectedRow();
				int column = CLP_PACK03.getSelectedColumn();
				CLP_PACK03.setItem(row, column, "");
				CLP_PACK03.setValueAt("", row, column);
				return;
			}
			// 带出并设置完整医嘱信息
			setCLP_PACK03(result);
		}
		// 医嘱名称 调用
		if ("3".equals(tag)) {
			int count = getOrdTypeCode(result.getValue("ORDER_CODE"), "O")
					.getCount();
			if (count <= 0) {
				this.messageBox("此护嘱不属于任何一个临床路径项目，请重新选择！");
				// 清空医嘱名称
				int row = CLP_PACK03.getSelectedRow();
				int column = CLP_PACK03.getSelectedColumn();
				CLP_PACK03.setItem(row, column, "");
				CLP_PACK03.setValueAt("", row, column);
				return;
			}
			// 带出并设置完整医嘱信息
			setCLP_PACK03(result);
		}
	}

	/**
	 * 医嘱套餐
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setCLP_PACK03(TParm parm) {
		/************************* 全局信息 *************************/
		CLP_PACK03.acceptText();
		// 获取行号
		int selRow = CLP_PACK03.getSelectedRow();
		// 院区代码
		parm.setData("REGION_CODE", REGION_CODE.getValue());
		// 完整信息
		TParm result = new TParm();
		String ordTypeCode = parm.getValue("ORDTYPE_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		String typeChnDesc = parm.getValue("TYPE_CHN_DESC");
		/************************* 全局信息 *************************/

		/************************* 预设信息 *************************/
		// 选
		result.setData("SEL_FLG", false);
		// 医嘱类别
		String orderType = String.valueOf(CLP_PACK03.getItemData(selRow, 1));
		result.setData("ORDER_TYPE", orderType);
		/************************* 预设信息 *************************/

		/************************* 默认信息 *************************/
		// 接收到临床路径项目和医嘱代码（即选择临床路径项目，存在默认医嘱的情形）
		if ((checkInputString(ordTypeCode) && checkInputString(typeChnDesc))
				&& checkInputString(orderCode)) {
			// 临床路径项目
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			// 护嘱名称
			String orderDesc = parm.getValue("ORDER_CHN_DESC");
			result.setData("ORDER_CHN_DESC", orderDesc);
			// 用量
			result.setData("DOSE", parm.getValue("AMOUNT"));
			// 单位
			result.setData("DOSE_UNIT", parm.getValue("UNIT"));
			// 频次
			if ("ST".equals(orderType)) {
				result.setData("FREQ_CODE", "STAT");
			} else if ("UD".equals(orderType)) {
				result.setData("FREQ_CODE", parm.getValue("FREQ"));
			}
			// 护嘱备注 手动输入
			result.setData("NOTE", "");
			// 执行人员
			result.setData("CHKUSER_CODE", "001");
			// 执行科室
			result
					.setData("RBORDER_DEPT_CODE", parm
							.getValue("EXEC_DEPT_CODE"));
			// 核查类别
			parm.setData("ORDER_CAT1_CODE", parm.getValue("CHKTYPE_CODE"));
			TParm chkTypeResult = PackTool.getInstance()
					.getDefaultChkTypeByOrderCat1(parm);
			String chkTypeCode = chkTypeResult.getValue("CHKTYPE_CODE", 0);
			result.setData("CHKTYPE_CODE", chkTypeCode);
			// 必执
			result.setData("EXEC_FLG", "");
			// 变异标准差
			result.setData("STANDARD", "");
			// 版本
			result.setData("VERSION", "");
			/************************* 隐藏信息 *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// ROUT_CODE
			result.setData("ROUT_CODE", "");
			// DOSE_DAYS
			result.setData("DOSE_DAYS", "");
			// RBORDER_DEPT_CODE
			result.setData("RBORDER_DEPT_CODE", "");
			// URGENT_FLG
			result.setData("URGENT_FLG", "");
			/************************* 隐藏信息 *************************/

			/************************* 其他操作 *************************/
			TParm tableParm = CLP_PACK03.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK03.setParmValue(tableParm, CLP_PACK03.getParmMap());
			if (selRow >= CLP_PACK03.getRowCount() - 1) {
				insertNewRow(CLP_PACK03, selRow);
			}
			/************************* 其他操作 *************************/
		}
		// 接收到医嘱代码，没有接受到临床路径项目代码（即选择医嘱名称，反查询临床路径项目）
		else if ((!checkInputString(ordTypeCode) || !checkInputString(typeChnDesc))
				&& checkInputString(orderCode)) {
			// 根据医嘱代码查询临床路径项目代码
			TParm ordTypeCodeResult = getOrdTypeCode(orderCode, "O");
			ordTypeCode = ordTypeCodeResult.getValue("ORDTYPE_CODE", 0);
			typeChnDesc = ordTypeCodeResult.getValue("TYPE_CHN_DESC", 0);
			// 临床路径项目
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			// 护嘱名称
			String orderDesc = parm.getValue("ORDER_CHN_DESC");
			result.setData("ORDER_CHN_DESC", orderDesc);
			// 用量
			result.setData("DOSE", parm.getValue("AMOUNT"));
			// 单位
			result.setData("DOSE_UNIT", parm.getValue("UNIT"));
			// 频次
			if ("ST".equals(orderType)) {
				result.setData("FREQ_CODE", "STAT");
			} else if ("UD".equals(orderType)) {
				result.setData("FREQ_CODE", parm.getValue("FREQ"));
			}
			// 护嘱备注 手动输入
			result.setData("NOTE", "");
			// 执行人员
			result.setData("CHKUSER_CODE", "001");
			// 核查类别
			parm.setData("ORDER_CAT1_CODE", parm.getValue("CHKTYPE_CODE"));
			TParm chkTypeResult = PackTool.getInstance()
					.getDefaultChkTypeByOrderCat1(parm);
			String chkTypeCode = chkTypeResult.getValue("CHKTYPE_CODE", 0);
			result.setData("CHKTYPE_CODE", chkTypeCode);
			// 必执
			result.setData("EXEC_FLG", "");
			// 变异标准差
			result.setData("STANDARD", "");
			// 版本
			result.setData("VERSION", "");
			/************************* 隐藏信息 *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// ROUT_CODE
			result.setData("ROUT_CODE", "");
			// DOSE_DAYS
			result.setData("DOSE_DAYS", "");
			// RBORDER_DEPT_CODE
			result.setData("RBORDER_DEPT_CODE", "");
			// URGENT_FLG
			result.setData("URGENT_FLG", "");
			/************************* 隐藏信息 *************************/

			/************************* 其他操作 *************************/
			TParm tableParm = CLP_PACK03.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK03.setParmValue(tableParm, CLP_PACK03.getParmMap());
			if (selRow >= CLP_PACK03.getRowCount() - 1) {
				insertNewRow(CLP_PACK03, selRow);
			}
			/************************* 其他操作 *************************/
		}
		// 接收到临床路径项目代码，没有接收到医嘱代码（即选择临床路径项目，不存在默认医嘱的情形）
		else if ((checkInputString(ordTypeCode) && checkInputString(typeChnDesc))
				&& !checkInputString(orderCode)) {
			// 临床路径项目
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			/************************* 隐藏信息 *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// ROUT_CODE
			result.setData("ROUT_CODE", "");
			// DOSE_DAYS
			result.setData("DOSE_DAYS", "");
			// RBORDER_DEPT_CODE
			result.setData("RBORDER_DEPT_CODE", "");
			// URGENT_FLG
			result.setData("URGENT_FLG", "");
			/************************* 隐藏信息 *************************/

			/************************* 其他操作 *************************/
			TParm tableParm = CLP_PACK03.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK03.setParmValue(tableParm, CLP_PACK03.getParmMap());
			/************************* 其他操作 *************************/
		}
		/************************* 默认信息 *************************/
		// 卢海加入-20110630 begin
		TParm tableParm = CLP_PACK03.getParmValue();
		tableParm.setData("CHKTYPE_CODE", selRow, getDefaultCheckTypeCode());
		CLP_PACK03.setParmValue(tableParm, CLP_PACK03.getParmMap());
		// 卢海加入-20110630 end

	}

	/**
	 * 用量 单位 日份 列触发
	 * 
	 * @param obj
	 *            Object
	 */
	public void onTableChangeValueForPack01(Object obj) {
		CLP_PACK01.acceptText();
		TTableNode node = (TTableNode) obj;
		if (node == null) {
			return;
		}
		int row = node.getRow();
		int column = node.getColumn();
		// 用量 频次 日份 列触发
		if (column == 6 || column == 8 || column == 11) {
			double dose = 0.0;
			TParm tableParm = CLP_PACK01.getParmValue();

			if (column == 6) {
				// =========pangben 2012-6-21 用量更改调用出院带药计算总量方法
				TParm parm = new TParm();
				parm = tableParm.getRow(row);
				parm.setData("MEDI_QTY", node.getValue());// 发药单位
				// CLP_PACK01.setItem(row, "DOSE", node.getValue());
				// CLP_PACK01.setItem(row, "MEDI_QTY", node.getValue());
				CLP_PACK01.setItem(row, "MEDI_QTY", node.getValue());// 发药单位
				//CLP_PACK01.setItem(row, "DOSAGE_QTY", node.getValue());// 发药单位
				TParm totParm = TotQtyTool.getInstance().getTotQty(parm);
//				if (null != parm.getValue("CAT1_TYPE")
//						&& ("LIS".equals(parm.getValue("CAT1_TYPE")) || "RIS"
//								.equals(parm.getValue("CAT1_TYPE")))) {
//					dose = totParm.getDouble("QTY");
//				} else {
//					dose = totParm.getDouble("QTY") * parm.getInt("DOSE_DAYS");
//				}
				dose = totParm.getDouble("QTY");
			}
			// else {
			// TParm totParm = TotQtyTool.getInstance().getTotQty(
			// tableParm.getRow(row));
			// dose = totParm.getDouble("QTY");
			// // dose = Double.parseDouble(String.valueOf(CLP_PACK01
			// // .getItemData(row, "DOSE")));
			// }
			// String doseUnit = String.valueOf(CLP_PACK01.getItemData(row,
			// "DOSE_UNIT"));
			String freqCode = "";
			if (column == 8) {
				if (!udFlg) {
					return;
				}
				freqCode = String.valueOf(node.getValue());
				// =========pangben 2012-6-21 用量更改
				TParm parm = tableParm.getRow(row);
				parm.setData("FREQ_CODE", freqCode);// 频次
				TParm totParm = TotQtyTool.getInstance().getTotQty(parm);
				dose = totParm.getDouble("QTY");
				dose = dose * gettime(freqCode);
				// CLP_PACK01.setItem(row, "FREQ_CODE", freqCode);
			}
			// else {
			// freqCode = String.valueOf(CLP_PACK01.getItemData(row,
			// "FREQ_CODE"));
			// }
			int doseDays = 0;
			if (column == 11) {
				doseDays = Integer.parseInt(String.valueOf(node.getValue()));
				// =========pangben 2012-6-21 用量更改

				TParm parm = tableParm.getRow(row);
				parm.setData("TAKE_DAYS", doseDays);// 天数
				TParm totParm = TotQtyTool.getInstance().getTotQty(parm);
				dose = totParm.getDouble("QTY");
				CLP_PACK01.setItem(row, "TAKE_DAYS", doseDays);
			}
			// else {
			// doseDays = Integer.parseInt(String.valueOf(CLP_PACK01
			// .getItemData(row, "DOSE_DAYS")));
			// System.out.println("doseDays:::"+doseDays);
			// }
			// 计算当前记录的总量和总金额
			double ownPrice = CLP_PACK01.getParmValue().getDouble("OWN_PRICE",
					row);
			// TParm parm=getTotalAndFees(dose, doseUnit, freqCode, doseDays,
			// getOwnPrice(orderCode));
			// 设置当前记录的总量和总金额
			CLP_PACK01.setItem(row, "TOTAL", dose);
			CLP_PACK01.setItem(row, "FEES", dose * ownPrice);
		}
	}

	/**
	 * 获得频次数量
	 * 
	 * @param freqCode
	 * @return
	 */
	private int gettime(String freqCode) {
		TParm parmTrn = new TParm(TJDODBTool.getInstance().select(
				"SELECT FREQ_TIMES FROM SYS_PHAFREQ WHERE FREQ_CODE='"
						+ freqCode + "'"));
		int times = parmTrn.getInt("FREQ_TIMES", 0);
		return times;
	}

	public TParm getTotalAndFees(TParm parm, int row) {
		//double dose = Double.parseDouble(parm.getValue("DOSE", row));
		// String doseUnit = parm.getValue("DOSE_UNIT", row);// 单位
		// String freqCode = parm.getValue("FREQ_CODE", row);// 频次
		// int doseDays = Integer.parseInt(parm.getValue("DOSE_DAYS", row));//
		// 日份
		// 计算当前记录的总量和总金额
		// String orderCode = parm.getValue("ORDER_CODE", row);
		// double dosageQty = parm.getDouble("DOSAGE_QTY", row);// 发药单位
		// =============pangben 2012-6-21 start 显示总量 金额
		TParm totParm = TotQtyTool.getInstance().getTotQty(parm.getRow(row));
		double dose = totParm.getDouble("QTY");// 总量
		// TParm totalAndFees = getTotalAndFees(dose, doseUnit, freqCode,
		// doseDays, parm.getRow(row).getDouble("OWN_PRICE"));
		// if (null!=parm.getValue("ROUT_CODE", row)
		// &&("IVD".equals(parm.getValue("ROUT_CODE",
		// row))||"IVP".equals(parm.getValue("ROUT_CODE", row)))) {
		// //DOSE_UNIT=9 毫升 ML 换算总量
		// if (null!=parm.getValue("DOSE_UNIT",
		// row)&&"9".equals(parm.getValue("DOSE_UNIT", row))) {
		// //总量
		// totalAndFees.setData("TOTAL",StringTool.round(totalAndFees.getDouble("TOTAL")/parm.getDouble("DOSE",
		// row),2));
		// //总金额
		// totalAndFees.setData("FEES",StringTool.round(totalAndFees.getDouble("FEES")/parm.getDouble("DOSE",
		// row),2));
		// }
		// }
		// 设置当前记录的总量和总金额
		parm.setData("TOTAL", row, dose);
		// ===========pangben 2012-6-25
		parm.setData("OWN_PRICE", row, parm.getRow(row).getDouble("OWN_PRICE"));
		parm.setData("FEES", row,  
				StringTool.round(dose * parm.getRow(row).getDouble("OWN_PRICE"), 2));//modify by wanglong 20120924
		
		return parm;
	}

	/**
	 * 清空本行数据
	 * 
	 * @param table
	 *            TTable
	 * @param row
	 *            int
	 */
	public void clearRowData(TTable table, int row) {
		TParm clearParm = new TParm();
		clearParm.setData("ORDER_TYPE", table.getItemData(row, "ORDER_TYPE"));
		TParm tableParm = table.getParmValue();
		tableParm.setRowData(row, clearParm);
		table.setParmValue(tableParm, table.getParmMap());
	}

	/**
	 * 查询临床路径项目
	 * 
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrdTypeCode(String orderCode, String orderFlg) {
		String ordTypeCodeSql = " SELECT A.*, B.TYPE_CHN_DESC FROM CLP_ORDERTYPE A, CLP_ORDTYPE B WHERE "
				+ " A.ORDTYPE_CODE = B.TYPE_CODE"
				+ " AND A.ORDER_CODE = '"
				+ orderCode + "'" + " AND A.ORDER_FLG = '" + orderFlg + "'";
		TParm ordTypeCodeResult = new TParm(TJDODBTool.getInstance().select(
				ordTypeCodeSql));
		return ordTypeCodeResult;
	}

	/**
	 * 根据临床路径项目代码获取认医嘱信息
	 * 
	 * @param ordTypeCode
	 *            String
	 * @param orderFlg
	 *            String
	 * @return TParm
	 */
	public TParm getOrderByOrdTypeCode(String ordTypeCode, String orderFlg,
			String defaultFlg) {
		TParm parm = new TParm();
		parm.setData("ORDTYPE_CODE", ordTypeCode);
		parm.setData("ORDER_FLG", orderFlg);
		parm.setData("DEFAULT_FLG", defaultFlg);
		return PackTool.getInstance().getOrderByOrdTypeCode(parm);
	}

	/**
	 * 获取自费单价
	 * 
	 * @param table
	 *            TTable
	 * @param row
	 *            int
	 * @param column
	 *            int
	 * @return double
	 */
	public double getOwnPrice(String orderCode) {
		// 获取单价
		String sql = " SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE = '"
				+ orderCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getDouble("OWN_PRICE", 0);
	}

	/**
	 * 输入校验：医嘱代码
	 * 
	 * @param str
	 *            String
	 * @return String
	 */
	public boolean checkOrderCode(String orderDesc) {
		if (orderDesc == null || "".equals(orderDesc.trim())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 输入校验：临床路径项目
	 * 
	 * @param ordTypeCode
	 *            String
	 * @return boolean
	 */
	public boolean checkOrdTypeCode(String ordTypeCode) {
		if (ordTypeCode == null || "".equals(ordTypeCode.trim())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 新增一行
	 * 
	 * @param table
	 *            TTable
	 */
	public void insertNewRow(TTable table, int row) {
		int rowID = table.addRow(row + 1);
		table.setItem(rowID, "ORDER_TYPE", "ST");
	}

	/**
	 * O 设置列表指定单元格可编辑
	 * 
	 * @param table
	 *            TTable
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void setTableEnabled(TTable table, int row, int column) {
		int rowCount = table.getRowCount();
		String lockRows = "";
		int columnCount = table.getColumnCount();
		String lockColumns = "";
		if (row < 0 || row >= rowCount) {
			return;
		}
		if (column < 0 || column >= columnCount) {
			return;
		}
		for (int i = 0; i < rowCount; i++) {
			if (i != row) {
				lockRows = lockRows + i + ",";
			}
		}
		for (int i = 0; i < columnCount; i++) {
			if (i != column) {
				lockColumns = lockColumns + i + ",";
			}
		}
		if (lockRows.endsWith(",")) {
			lockRows = lockRows.substring(0, lockRows.length() - 1);
		}
		table.setLockRows(lockRows);
		if (lockColumns.endsWith(",")) {
			lockColumns = lockColumns.substring(0, lockColumns.length() - 1);
		}
		table.setLockColumns(lockColumns);
		table.setSelectedColumn(column);
		table.setSelectedRow(row);
	}

	/**
	 * 医嘱套餐 计算当前记录的总量和总金额
	 */
	public TParm getTotalAndFees(double dose, String doseUnit, String freqCode,
			int doseDays, double ownPrice) {
		TParm parm = new TParm();
		// 计算总量
		TParm totalAndFeesParm = TotQtyTool.getIbsQty(dose, doseUnit, freqCode,
				doseDays);
		double total = totalAndFeesParm.getDouble("DOSAGE_QTY");
		parm.setData("TOTAL", total);
		// 计算总金额
		double fees = total * ownPrice;
		parm.setData("FEES", fees);
		return parm;
	}

	/**
	 * 医嘱套餐 计算总金额（全部列表）
	 */
	public void setALLFEES() {
		double totFee = 0.0;
		int rows = CLP_PACK01.getRowCount();
		for (int i = 0; i < rows; i++) {
			totFee += TypeTool.getDouble(CLP_PACK01.getValueAt(i, 16));
		}
		FEES.setValue(String.valueOf(totFee));
	}

	/**
	 * 将null转换为空字符串
	 */
	public String nullToString(String str) {
		if (str == null || "null".equals(str)) {
			return "";
		} else {
			return str.trim();
		}
	}

	/**
	 * 模糊查询
	 */
	public void onQuery() {
		// 获取页签索引
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		if (selectedIndex == 0) {
			// 临床路径类别
			TParm parm = new TParm();
			// 获取查询条件，组装参数对象
			// 卢海修改begin regionCode
			parm.setData("REGION_CODE", REGION_CODE.getValue());
			parm.setData("REGION_CODE", regionCode);
			parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
			parm.setData("PY1", PY1.getValue());
			parm.setData("PY2", PY2.getValue());
			parm.setData("CLNCPATH_CHN_DESC", CLNCPATH_CHN_DESC.getValue());
			parm.setData("CLNCPATH_ENG_DESC", CLNCPATH_ENG_DESC.getValue());
			parm.setData("DEPT_CODE", DEPT_CODE.getValue());
			parm.setData("FRSTVRSN_DATE", FRSTVRSN_DATE.getValue());
			parm.setData("LASTVRSN_DATE", LASTVRSN_DATE.getValue());
			parm.setData("MODIFY_TIMES", MODIFY_TIMES.getValue());
			parm.setData("ACPT_CODE", ACPT_CODE.getValue());
			parm.setData("EXIT_CODE", EXIT_CODE.getValue());
			parm.setData("STAYHOSP_DAYS", STAYHOSP_DAYS.getValue());
			parm.setData("AVERAGECOST", AVERAGECOST.getValue());
			parm.setData("DESCRIPTION", DESCRIPTION.getValue());
			parm.setData("ACTIVE_FLG", ACTIVE_FLG.getValue());
			// 模糊查询临床路径类别
			TParm bscInfoList = BscInfoTool.getInstance().getBscInfoList(parm);
			// 显示在前台界面
			CLP_BSCINFO.setParmValue(bscInfoList);
		} else if (selectedIndex == 1) {
			// 治疗时程
			TParm parm = new TParm();
			// 获取查询条件，组装参数对象
			parm.setData("REGION_CODE", REGION_CODE.getValue());
			parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
			// 模糊查询治疗时程
			TParm thrpyschdmList = ThrpyschdmTool.getInstance()
					.getThrpyschdmList(parm);
			// 显示在前台界面
			CLP_THRPYSCHDM.setParmValue(thrpyschdmList);
		} else if (selectedIndex == 2) {
			// 医嘱套餐
			TParm parm = new TParm();
			// 获取查询条件，组装参数对象
			// luhai modify begin
			// parm.setData("REGION_CODE", REGION_CODE.getValue());
			parm.setData("REGION_CODE", this.regionCode);
			// luhai modify end
			parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
			parm.setData("SCHD_CODE", SCHD_CODE.getValue());
			parm.setData("ORDER_FLG", "Y");
			// 模糊查询医嘱套餐
			TParm pack01List = PackTool.getInstance().getPack01List(parm);
			for (int i = 0; i < pack01List.getCount(); i++) {
				// ============pangben 2012-6-25 集合医嘱细项显示金额 ORDERSET_FLG=Y 为集合医嘱主项
				TParm result = pack01List.getRow(i);
				if (null != result.getValue("CAT1_TYPE")&&"Y".equals(result.getValue("ORDERSET_FLG"))) {
					double allOwnAmt = 0.00;
					TParm parmDetail = SYSOrderSetDetailTool
							.getInstance()
							.selectByOrderSetCode(result.getValue("ORDER_CODE"));
					for (int j = 0; j < parmDetail.getCount(); j++) {
						allOwnAmt = allOwnAmt
								+ parmDetail.getDouble("OWN_PRICE", j)
								* parmDetail.getDouble("TOTQTY", j);
					}
					pack01List.setData("OWN_PRICE", i, allOwnAmt);
					// result.setData("OWN_PRICE",allOwnAmt);
				}
				getTotalAndFees(pack01List, i);
			}
			// 显示在前台界面
			CLP_PACK01.setParmValue(pack01List);
			insertNewRow(CLP_PACK01, CLP_PACK01.getParmValue().getCount() - 1);
		} else if (selectedIndex == 3) {
			// 关键诊疗项目
			TParm parm = new TParm();
			// 获取查询条件，组装参数对象
			// parm.setData("REGION_CODE", REGION_CODE.getValue());
			parm.setData("REGION_CODE", this.regionCode); // luhai modify
			parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
			parm.setData("SCHD_CODE", SCHD_CODE.getValue());
			parm.setData("ORDER_FLG", "N");
			// 模糊查询关键诊疗项目
			TParm pack02List = PackTool.getInstance().getPack02List(parm);
			// 显示在前台界面
			CLP_PACK02.setParmValue(pack02List, CLP_PACK02.getParmMap());
			insertNewRow(CLP_PACK02, CLP_PACK02.getParmValue().getCount() - 1);
		} else if (selectedIndex == 4) {
			// 护理计划
			TParm parm = new TParm();
			// 获取查询条件，组装参数对象
			// parm.setData("REGION_CODE", REGION_CODE.getValue());
			parm.setData("REGION_CODE", this.regionCode); // luhai modify
			parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
			parm.setData("SCHD_CODE", SCHD_CODE.getValue());
			parm.setData("ORDER_FLG", "O");
			// 模糊查询护理计划
			TParm pack03List = PackTool.getInstance().getPack03List(parm);
			// 显示在前台界面
			CLP_PACK03.setParmValue(pack03List, CLP_PACK03.getParmMap());
			insertNewRow(CLP_PACK03, CLP_PACK03.getParmValue().getCount() - 1);
		}
	}

	public void onClear() {
		// 获取页签索引
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		if (selectedIndex == 0) {
			// 临床路径类别
			CLNCPATH_CODE.setEditable(true);
			CLNCPATH_CODE.setValue("");
			PY1.setValue("");
			PY2.setValue("");
			CLNCPATH_CHN_DESC.setValue("");
			CLNCPATH_ENG_DESC.setValue("");
			DEPT_CODE.setValue("");
			FRSTVRSN_DATE.setValue("");
			LASTVRSN_DATE.setValue("");
			MODIFY_TIMES.setValue("");
			ACPT_CODE.setValue("");
			EXIT_CODE.setValue("");
			STAYHOSP_DAYS.setValue("");
			STAYHOSP_DAYS.setEditable(true);
			AVERAGECOST.setValue("");
			VERSION.setValue("");
			DESCRIPTION.setValue("");
			ACTIVE_FLG.setValue(false);
			CLP_BSCINFO.clearSelection();
			this.diagnose_desc.setValue("");
			this.diagnose.setValue("");
			this.operation_diagnose.setValue("");
			this.operation_diagnose_desc.setValue("");
			this.operatorDiagnoseTable.setParmValue(new TParm());
			this.diagnoseTable.setParmValue(new TParm());
			// 加入默认行
			addDefaultRowForDiag(this.OPERATOR_DIAGNOSE_TABLE);
			addDefaultRowForDiag(this.DIAGNOSE_TABLE);
		} else if (selectedIndex == 1) {
			// 治疗时程
			SCHD_CODE.setEnabled(true);
			SCHD_CODE.setValue("");
			SCHD_DAY.setValue("");
			SUSTAINED_DAYS.setValue("");
			CLP_THRPYSCHDM.clearSelection();
		} else if (selectedIndex == 2) {
			// 医嘱套餐
			VERSION01.setValue("");
			CLP_PACK01.clearSelection();
		} else if (selectedIndex == 3) {
			// 关键诊疗套餐
			VERSION02.setValue("");
			CLP_PACK02.clearSelection();
		} else if (selectedIndex == 4) {
			// 护理计划
			VERSION03.setValue("");
			CLP_PACK03.clearSelection();
		}

	}

	public void menuControl() {
		// 获取页签索引
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		// 获取查询菜单
		TMenuItem query = (TMenuItem) this.getComponent("query");
		TMenuItem use = (TMenuItem) this.getComponent("use");
		TMenuItem save = (TMenuItem) this.getComponent("save");
		TMenuItem delete = (TMenuItem) this.getComponent("delete");
		// 当selectedIndex=0时，查询和启用菜单可用；否则，查询和启用菜单不可用
		if (selectedIndex <= 0) {
			save.setEnabled(true);
			delete.setEnabled(true);
			query.setEnabled(true);
			use.setEnabled(true);
		}
		// 治疗时程，版本号为1的时候可以修改
		else if (selectedIndex == 1) {
//			if (!"1".equals(VERSION.getValue())) {
//				save.setEnabled(false);
//				delete.setEnabled(false);
//			} else {
//				save.setEnabled(true);
//				delete.setEnabled(true);
//			}
			save.setEnabled(true);
			delete.setEnabled(true);
			query.setEnabled(false);
			use.setEnabled(false);
		} else {
			save.setEnabled(true);
			delete.setEnabled(true);
			query.setEnabled(false);
			use.setEnabled(false);
		}
	}

	/**
	 * 当选项卡切换时 调用此方法
	 */
	public void onChange() {
		// 获取页签索引
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		// 查询菜单控制
		menuControl();
		// 若当前点选的页签索引为0（临床路径类别），则不作任何处理直接返回
		if (selectedIndex <= 0) {
			// 版本不可编辑
			VERSION.setEditable(false);
			// 是否启用标识不可编辑
			ACTIVE_FLG.setEnabled(false);
			// 记录此次点选的页签索引
			lastSelectedIndex = selectedIndex;
			return;
		}
		// 若当前点选的页签索引为1（治疗时程），则验证是否选择了临床路径类别
		if (selectedIndex == 1) {
			if (CLP_BSCINFO.getSelectedRow() < 0) {
				this.messageBox("请选择临床路径类别！");
				tTabbedPane_0.setSelectedIndex(0);
				return;
			}
			// 路径项目不可编辑
			CLNCPATH_CODE01.setEnabled(false);
			CLNCPATH_CODE01.setValue(CLNCPATH_CODE.getValue());
			// 上一次所在的选项卡索引为0（临床路径类别），则清空以前信息并重新查询时程信息
			if (lastSelectedIndex == 0) {
				// 清空以前信息
				onClear();
				// 模糊查询时程
				onQuery();
			}
		}
		// 若当前点选的页签索引为其它，则验证是否选择了临床路径类别和治疗时程
		if (selectedIndex == 2) {
			if (lastSelectedIndex == 0 && CLP_BSCINFO.getSelectedRow() < 0) {
				this.messageBox("请选择临床路径类别！");
				tTabbedPane_0.setSelectedIndex(0);
				return;
			}
			if (lastSelectedIndex == 0
					|| (lastSelectedIndex == 1 && CLP_THRPYSCHDM
							.getSelectedRow() < 0)) {
				this.messageBox("请选择治疗时程！");
				tTabbedPane_0.setSelectedIndex(1);
				return;
			}
			// 版本不可编辑
			VERSION01.setEditable(false);

			// 路径项目不可编辑
			CLNCPATH_CODE02.setEnabled(false);
			CLNCPATH_CODE02.setValue(CLNCPATH_CODE.getValue());
			// 时程代码不可编辑
			SCHD_CODE01.setEnabled(false);
			SCHD_CODE01.setValue(SCHD_CODE.getValue());
			// 总金额不可编辑
			FEES.setEditable(false);
			// 清空以前信息
			onClear();
			// 模糊查询时程
			onQuery();
			// ==============pangben 2012-6-19 start
			this.setValue("VERSION01", VERSION.getValue());
			// ==============pangben 2012-6-19 stop
			// ===============pangben 2012-5-18
			// TParm clpParm = CLP_BSCINFO.getParmValue();
			// ===============pangben stop
			// this.setValue("VERSION01", clpParm.getValue("VERSION", 0));
		}
		if (selectedIndex == 3) {
			if (lastSelectedIndex == 0 && CLP_BSCINFO.getSelectedRow() < 0) {
				this.messageBox("请选择临床路径类别！");
				tTabbedPane_0.setSelectedIndex(0);
				return;
			}
			if (lastSelectedIndex == 0
					|| (lastSelectedIndex == 1 && CLP_THRPYSCHDM
							.getSelectedRow() < 0)) {
				this.messageBox("请选择治疗时程！");
				tTabbedPane_0.setSelectedIndex(1);
				return;
			}
			// 版本不可编辑
			VERSION02.setEditable(false);
			// 路径项目不可编辑
			CLNCPATH_CODE03.setEnabled(false);
			CLNCPATH_CODE03.setValue(CLNCPATH_CODE.getValue());
			// 时程代码不可编辑
			SCHD_CODE02.setEnabled(false);
			SCHD_CODE02.setValue(SCHD_CODE.getValue());
			// 清空以前信息
			onClear();
			// 模糊查询时程
			onQuery();
			this.setValue("VERSION02", VERSION.getValue());
		}
		if (selectedIndex == 4) {
			if (lastSelectedIndex == 0 && CLP_BSCINFO.getSelectedRow() < 0) {
				this.messageBox("请选择临床路径类别！");
				tTabbedPane_0.setSelectedIndex(0);
				return;
			}
			if (lastSelectedIndex == 0
					|| (lastSelectedIndex == 1 && CLP_THRPYSCHDM
							.getSelectedRow() < 0)) {
				this.messageBox("请选择治疗时程！");
				tTabbedPane_0.setSelectedIndex(1);
				return;
			}
			// 版本不可编辑
			VERSION03.setEditable(false);
			// 路径项目不可编辑
			CLNCPATH_CODE04.setEnabled(false);
			CLNCPATH_CODE04.setValue(CLNCPATH_CODE.getValue());
			// 时程代码不可编辑
			SCHD_CODE03.setEnabled(false);
			SCHD_CODE03.setValue(SCHD_CODE.getValue());
			// 清空以前信息
			onClear();
			// 模糊查询时程
			onQuery();
			this.setValue("VERSION03", VERSION.getValue());
		}
		// 记录此次点选的页签索引
		lastSelectedIndex = selectedIndex;
	}

	/**
	 * 组装参数TParm CLP_BSCINFO
	 */
	public TParm parmFormatForBscInfo() {
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue().toUpperCase());
		parm.setData("CLNCPATH_CHN_DESC", nullToString(CLNCPATH_CHN_DESC
				.getValue()));
		parm.setData("CLNCPATH_ENG_DESC", nullToString(CLNCPATH_ENG_DESC
				.getValue()));
		parm.setData("PY1", nullToString(PY1.getValue()));
		parm.setData("PY2", nullToString(PY2.getValue()));
		parm.setData("DEPT_CODE", nullToString(String.valueOf(DEPT_CODE
				.getValue())));
		parm.setData("FRSTVRSN_DATE", dateFormat.format(FRSTVRSN_DATE
				.getValue()));
		if (!this.checkNullAndEmpty(LASTVRSN_DATE.getValue() + "")) {
			parm.setData("LASTVRSN_DATE", dateFormat.format(LASTVRSN_DATE
					.getValue()));
		}
		parm.setData("MODIFY_TIMES", MODIFY_TIMES.getValue());
		parm.setData("ACPT_CODE", nullToString(ACPT_CODE.getValue()));
		parm.setData("EXIT_CODE", nullToString(EXIT_CODE.getValue()));
		parm.setData("STAYHOSP_DAYS", STAYHOSP_DAYS.getValue());
		parm.setData("AVERAGECOST", AVERAGECOST.getValue());
		parm.setData("VERSION", VERSION.getValue());
		parm.setData("DESCRIPTION", DESCRIPTION.getValue());
		parm.setData("ACTIVE_FLG", ACTIVE_FLG.getValue());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", dateFormat.format(SystemTool.getInstance()
				.getDate()));
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("ICD_CODE", this.diagnose.getValue());
		parm.setData("OPE_ICD_CODE", this.operation_diagnose.getValue());
		return parm;
	}

	/**
	 * 组装参数TParm CLP_THRPYSCHDM
	 */
	public TParm parmFormatForThrpyschdm() {
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue().toUpperCase());
		parm.setData("SCHD_CODE", String.valueOf(SCHD_CODE.getValue())
				.toUpperCase());
		parm.setData("SCHD_DAY", SCHD_DAY.getValue());
		parm.setData("SUSTAINED_DAYS", SUSTAINED_DAYS.getValue());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", dateFormat.format(SystemTool.getInstance()
				.getDate()));
		parm.setData("OPT_TERM", Operator.getIP());
		return parm;
	}

	/**
	 * 组装参数TParm CLP_PACK
	 * 
	 * @param String
	 *            ORDER_FLG Y:医嘱套餐 N:关键诊疗项目 O:护理计划
	 */
	public TParm parmFormatForPack(String ORDER_FLG) {
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
		parm.setData("SCHD_CODE", SCHD_CODE.getValue());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", dateFormat.format(SystemTool.getInstance()
				.getDate()));
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("ORDER_FLG", ORDER_FLG);
		return parm;
	}

	public boolean checkBscInfo() {
		if (CLNCPATH_CODE.getValue() == null
				|| "".equals(CLNCPATH_CODE.getValue().trim())) {
			this.messageBox("请输入路径代码！");
			return false;
		}
		if (FRSTVRSN_DATE.getValue() == null
				|| "".equals(FRSTVRSN_DATE.getValue())) {
			this.messageBox("请输入制订日期！");
			return false;
		}
		// luhai delete 20110630 begin
		// if (LASTVRSN_DATE.getValue() == null ||
		// "".equals(LASTVRSN_DATE.getValue())) {
		// this.messageBox("请输入最近修订日！");
		// return false;
		// }
		// if (MODIFY_TIMES.getValue() == null ||
		// "".equals(MODIFY_TIMES.getValue())) {
		// this.messageBox("请输入修改次数！");
		// return false;
		// }
		// luhai delete 20110630 end
		if (STAYHOSP_DAYS.getValue() == null
				|| "".equals(STAYHOSP_DAYS.getValue())) {
			this.messageBox("请输入标准住院天数！");
			return false;
		}
		if (AVERAGECOST.getValue() == null || "".equals(AVERAGECOST.getValue())) {
			this.messageBox("请输入平均医疗费用！");
			return false;
		}
		return true;
	}

	/**
	 * 临床路径类别
	 */
	public boolean saveBscInfo(TParm parm) {
		CLP_BSCINFO.acceptText();
		// 查询本条数据（临床路径类别）
		int count = BscInfoTool.getInstance().getBscInfoObject(parm).getCount();
		// 判断本条数据在数据库中是否存在
		if (count <= 0) {
			// 如果不存在，则进行插入操作
			TParm seqParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT NVL(MAX(SEQ) + 1, 0) AS SEQ FROM CLP_BSCINFO"));
			parm.setData("SEQ", seqParm.getInt("SEQ", 0));
			// TParm result = BscInfoTool.getInstance().insert(parm);
			TParm saveParm = new TParm();
			saveParm.setData("bscinfoParm", parm.getData());
			saveParm.setData("icdParm", this.diagnoseTable.getParmValue()
					.getData());
			saveParm.setData("optParm", this.operatorDiagnoseTable
					.getParmValue().getData());
			saveParm.setData("basicInfo", this.getBasicOperatorMap());
			TParm result = TIOM_AppServer.executeAction(
					"action.clp.CLPBscInfoAction", "insertBSCInfo", saveParm);

			if (result.getErrCode() >= 0) {
				this.messageBox("保存成功！");
				return true;
			} else {
				this.messageBox("保存失败！");
				return false;
			}
		} else {
			// 如果存在，则进行更新操作
			// TParm result = BscInfoTool.getInstance().update(parm);
			TParm saveParm = new TParm();
			saveParm.setData("bscinfoParm", parm.getData());
			saveParm.setData("icdParm", this.diagnoseTable.getParmValue()
					.getData());
			saveParm.setData("optParm", this.operatorDiagnoseTable
					.getParmValue().getData());
			saveParm.setData("basicInfo", this.getBasicOperatorMap());
			TParm result = TIOM_AppServer.executeAction(
					"action.clp.CLPBscInfoAction", "updateBSCInfo", saveParm);
			if (result.getErrCode() >= 0) {
				this.messageBox("保存成功！");
				return true;
			} else {
				this.messageBox("保存失败！");
				return false;
			}
		}
	}

	public boolean checkThrpyschdm() {
		if (CLNCPATH_CODE.getValue() == null
				|| "".equals(CLNCPATH_CODE.getValue().trim())) {
			this.messageBox("请输入路径代码！");
			return false;
		}
		if (SCHD_CODE.getValue() == null || "".equals(SCHD_CODE.getValue())) {
			this.messageBox("请输入时程代码！");
			return false;
		}
		if (SCHD_DAY.getValue() == null
				|| "".equals(SCHD_DAY.getValue().trim())) {
			this.messageBox("请输入时程天数！");
			return false;
		}
		int schdDay = Integer.parseInt(SCHD_DAY.getValue());
		if (schdDay < 0) {
			this.messageBox("时程天数不能为负！");
			return false;
		}
		schdDay = getTotalSchDay();
		int stayHospDays = Integer.parseInt(STAYHOSP_DAYS.getValue());
		// System.out.println("标准住院天数：" + stayHospDays);
		if (schdDay > stayHospDays) {
			this.messageBox("时程总天数超过标准住院天数！");
			return false;
		}
		return true;
	}

	/**
	 * 得到总实际时程数
	 * 
	 * @return int
	 */
	private int getTotalSchDay() {
		int schdDay = 0;
		TTable durationTable = (TTable) this.getComponent("CLP_THRPYSCHDM");
		TParm tableParm = durationTable.getParmValue();
		// 实际开始时间
		String realsustainedDay = this.getValueString("SUSTAINED_DAYS");
		if (!this.validNumber(realsustainedDay)) {
			realsustainedDay = "0";
		}
		// 实际天数
		String realSchdDay = String.valueOf(SCHD_DAY.getValue());
		String realSchdCode = String.valueOf(SCHD_CODE.getValue());
		int realEndDay = this.getIntValue(realsustainedDay)
				+ this.getIntValue(realSchdDay) - 1;
		boolean isedit = false;
		List<Map> durationList = new ArrayList<Map>();
		for (int i = 0; i < tableParm.getCount(); i++) {
			String currentSchdCode = String.valueOf(tableParm.getValue(
					"SCHD_CODE", i));
			String currentSchdDay = String.valueOf(tableParm.getValue(
					"SCHD_DAY", i));
			String currentStartDay = tableParm.getValue("SUSTAINED_DAYS", i);
			if (realSchdCode.equalsIgnoreCase(currentSchdCode)) {
				currentSchdDay = realSchdDay; // 该行是编辑的值，使用编辑的值
				currentStartDay = realsustainedDay;
				isedit = true;
			}
			// 开始处理数据
			int currentSchdDayIntValue = this.getIntValue(currentSchdDay); // 治疗天数
			int currentStartDayIntValue = this.getIntValue(currentStartDay); // 开始天数
			int currentEndDayIntValue = currentSchdDayIntValue
					+ currentStartDayIntValue - 1;
			HashMap<String, Integer> durationMap = new HashMap<String, Integer>();
			durationMap.put("currentSchdDay", currentSchdDayIntValue);
			durationMap.put("currentStartDay", currentStartDayIntValue);
			durationMap.put("currentEndDay", currentEndDayIntValue);
			durationList.add(durationMap);
		}
		// 如果是新加入的值也要进行累加
		if (!isedit) {
			HashMap<String, Integer> durationMap = new HashMap<String, Integer>();
			durationMap.put("currentSchdDay", this.getIntValue(realSchdDay));
			durationMap.put("currentStartDay", this
					.getIntValue(realsustainedDay));
			durationMap.put("currentEndDay", realEndDay);
			durationList.add(durationMap);
		}
		Collections.sort(durationList, new comparatorDuration());
		for (Map obj : durationList) {
			// System.out.println("-----------------------开始天数---------" +
			// obj.get("currentStartDay") + "执行时间：" +
			// obj.get("currentSchdDay") + "结束时间：" +
			// obj.get("currentEndDay"));
		}
		for (int i = 0; i < durationList.size(); i++) {
			Map<String, Integer> mapObj = (Map) durationList.get(i);
			Map<String, Integer> mapObjPre = null;
			if (i > 0) {
				mapObjPre = (Map) durationList.get(i - 1);
			}
			if (mapObjPre == null) {
				schdDay += mapObj.get("currentSchdDay");
			} else {
				int currentSchdDay = mapObj.get("currentSchdDay");
				int currentStartDay = mapObj.get("currentStartDay");
				int preEndDay = mapObjPre.get("currentEndDay");
				if (currentStartDay <= preEndDay) {
					schdDay += currentSchdDay
							- (preEndDay - currentStartDay + 1);
				} else {
					schdDay += currentSchdDay;
				}
			}
		}
		// System.out.println("总的执行天数：" + schdDay);
		return schdDay;
	}
	/**
	 * 治疗时程
	 */
	public boolean saveThrpyschdm(TParm parm) {
		CLP_THRPYSCHDM.acceptText();
		// 查询本条数据（治疗时程）
		int count = ThrpyschdmTool.getInstance().getThrpyschdmObject(parm)
				.getCount();
		// 判断本条数据在数据库中是否存在
		if (count <= 0) {
			// 如果不存在，则进行插入操作
			int seq = 0;

			TParm seqParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT MAX(SEQ) AS SEQ FROM CLP_THRPYSCHDM WHERE CLNCPATH_CODE = '"
							+ parm.getValue("CLNCPATH_CODE") + "'"));
			if (null != seqParm.getValue("SEQ", 0)) {
				seq = seqParm.getInt("SEQ", 0);
				seq++;
			}
			parm.setData("SEQ", seq);
			TParm result = ThrpyschdmTool.getInstance().insert(parm);
			if (result.getErrCode() >= 0) {
				this.messageBox("保存成功！");
				return true;
			} else {
				this.messageBox("保存失败！");
				return false;
			}

		} else {
			// 如果存在，则进行更新操作
			TParm result = ThrpyschdmTool.getInstance().update(parm);
			if (result.getErrCode() >= 0) {
				this.messageBox("保存成功！");
				return true;
			} else {
				this.messageBox("保存失败！");
				return false;
			}
		}
	}

	/**
	 * 字符串非空验证
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	public boolean checkInputString(String str) {
		if (str == null) {
			return false;
		} else if ("".equals(str.trim())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 医嘱套餐数据校验
	 * 
	 * @param detail
	 *            TParm
	 * @return boolean
	 */
	public boolean checkPack01(TParm detail) {
		if (!checkInputString(detail.getValue("ORDER_TYPE"))) {
			this.messageBox("医嘱类别不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("CHKTYPE_CODE"))) {
			this.messageBox("核查类别不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("TYPE_CHN_DESC"))) {
			this.messageBox("临床路径项目不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("ORDER_CHN_DESC"))) {
			this.messageBox("医嘱名称不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("DOSE"))) {
			this.messageBox("用量不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("DOSE_UNIT"))) {
			this.messageBox("用量单位不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("FREQ_CODE"))) {
			this.messageBox("频次不能为空！");
			return false;
		}
		// 不验证途径20110707
		// if ("PHA".equals(detail.getValue("CAT1_TYPE"))) {
		// if (!checkInputString(detail.getValue("ROUT_CODE"))) {
		// this.messageBox("药品类医嘱途径不能为空！");
		// return false;
		// }
		// }
		if (!checkInputString(detail.getValue("DOSE_DAYS"))) {
			this.messageBox("日份不能为空！");
			return false;
		}
		if (checkInputString(detail.getValue("STANDARD"))) {
			if (detail.getValue("STANDARD").length() > 3) {
				this.messageBox("变异标准差输入不正确！");
				return false;
			}
		}
		return true;
	}

	/**
	 * 关键诊疗项目数据校验
	 * 
	 * @param detail
	 *            TParm
	 * @return boolean
	 */
	public boolean checkPack02(TParm detail) {
		if (!checkInputString(detail.getValue("ORDER_TYPE"))) {
			this.messageBox("医嘱类别不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("CHKTYPE_CODE"))) {
			this.messageBox("核查类别不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("TYPE_CHN_DESC"))) {
			this.messageBox("临床路径项目不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("ORDER_CHN_DESC"))) {
			this.messageBox("医嘱名称不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("DOSE"))) {
			this.messageBox("用量不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("DOSE_UNIT"))) {
			this.messageBox("用量单位不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("FREQ_CODE"))) {
			this.messageBox("频次不能为空！");
			return false;
		}
		if (checkInputString(detail.getValue("STANDARD"))) {
			if (detail.getValue("STANDARD").length() > 3) {
				this.messageBox("变异标准差输入不正确！");
				return false;
			}
		}
		return true;
	}

	/**
	 * 护理计划数据校验
	 * 
	 * @param detail
	 *            TParm
	 * @return boolean
	 */
	public boolean checkPack03(TParm detail) {
		if (!checkInputString(detail.getValue("ORDER_TYPE"))) {
			this.messageBox("护嘱类别不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("CHKTYPE_CODE"))) {
			this.messageBox("核查类别不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("TYPE_CHN_DESC"))) {
			this.messageBox("临床路径项目不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("ORDER_CHN_DESC"))) {
			this.messageBox("护嘱名称不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("DOSE"))) {
			this.messageBox("用量不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("DOSE_UNIT"))) {
			this.messageBox("用量单位不能为空！");
			return false;
		}
		if (!checkInputString(detail.getValue("FREQ_CODE"))) {
			this.messageBox("频次不能为空！");
			return false;
		}
		if (checkInputString(detail.getValue("STANDARD"))) {
			if (detail.getValue("STANDARD").length() > 3) {
				this.messageBox("变异标准差输入不正确！");
				return false;
			}
		}
		return true;
	}

	public boolean checkPack(TTable table, TParm parm) {
		table.acceptText();
		// System.out.println("table:"+table.getParmValue());
		for (int i = 0; i < table.getRowCount() - 1; i++) {
			TParm detail = parmFormatPackDetail(table, i, parm);
			// 医嘱套餐校验
			if ("CLP_PACK01".equals(table.getTag())) {
				if (!checkPack01(detail)) {
					return false;
				}
			}
			// 关键诊疗项目
			if ("CLP_PACK02".equals(table.getTag())) {
				if (!checkPack02(detail)) {
					return false;
				}
			}
			// 护理计划
			if ("CLP_PACK03".equals(table.getTag())) {
				if (!checkPack03(detail)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 临床路径
	 * 
	 * @param tableParm
	 *            TParm 列表中全部数据
	 * @param row
	 *            int 当前行号
	 * @param parm
	 *            TParm 非列表数据
	 * @return TParm result
	 */
	public TParm parmFormatPackDetail(TTable table, int row, TParm parm) {
		TParm detail = new TParm();
		TParm data = table.getParmValue();
		// 非列表数据
		detail.setData("CLNCPATH_CODE", parm.getValue("CLNCPATH_CODE"));
		detail.setData("SCHD_CODE", parm.getValue("SCHD_CODE"));
		detail.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		detail.setData("OPT_USER", parm.getValue("OPT_USER"));
		detail.setData("OPT_DATE", parm.getValue("OPT_DATE"));
		detail.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		detail.setData("ORDER_FLG", parm.getValue("ORDER_FLG"));
		// 列表显示项目
		detail.setData("ORDER_TYPE", data.getValue("ORDER_TYPE", row));
		detail.setData("ORDER_CHN_DESC", data.getValue("ORDER_CHN_DESC", row));
		detail.setData("TYPE_CHN_DESC", data.getValue("TYPE_CHN_DESC", row));
		detail.setData("CHKTYPE_CODE", data.getValue("CHKTYPE_CODE", row));
		detail.setData("DOSE", data.getValue("DOSE", row));
		detail.setData("DOSE_UNIT", data.getValue("DOSE_UNIT", row));
		detail.setData("FREQ_CODE", data.getValue("FREQ_CODE", row));
		detail.setData("ROUT_CODE", data.getValue("ROUT_CODE", row));
		detail.setData("DOSE_DAYS", data.getValue("DOSE_DAYS", row));
		detail.setData("NOTE", data.getValue("NOTE", row));
		detail.setData("RBORDER_DEPT_CODE", data.getValue("RBORDER_DEPT_CODE",
				row));
		detail.setData("CHKUSER_CODE", data.getValue("CHKUSER_CODE", row));
		detail.setData("URGENT_FLG", data.getValue("URGENT_FLG", row));
		detail.setData("EXEC_FLG", data.getValue("EXEC_FLG", row));
		detail.setData("STANDARD", data.getValue("STANDARD", row));
		detail.setData("VERSION", data.getValue("VERSION", row));
		detail.setData("ORDER_SEQ_NO", data.getValue("ORDER_SEQ_NO", row));
		detail.setData("SEQ", data.getValue("SEQ", row));
		detail.setData("ORDER_CODE", data.getValue("ORDER_CODE", row));
		detail.setData("ORDTYPE_CODE", data.getValue("ORDTYPE_CODE", row));
		detail.setData("CAT1_TYPE", data.getValue("CAT1_TYPE", row));
		return detail;
	}

	/**
	 * 保存
	 */
	public void onSave() {
		// 获取页签索引
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		// 临床路径类别
		if (selectedIndex == 0) {
			if (!checkBscInfo()) {
				return;
			}
			TParm parm = parmFormatForBscInfo();
			// 处理最近修订日期和修订次数 luhai Modify 20110630
			if (!this.checkNullAndEmpty(parm.getValue("LASTVRSN_DATE"))) {
				parm.setData("LASTVRSN_DATE", parm.getData("FRSTVRSN_DATE"));
			}
			if (!this.checkNullAndEmpty(parm.getValue("MODIFY_TIMES"))) {
				parm.setData("MODIFY_TIMES", "1");
			}
			// luhai Modify 20110630 end
			if (saveBscInfo(parm)) {
				// 查询全部临床路径信息
				TParm selectParm = new TParm();
				this.putBasicSysInfoIntoParm(selectParm);
				TParm bscInfoList = BscInfoTool.getInstance().getAllBscInfos(
						selectParm);
				// 显示在前台界面
				CLP_BSCINFO.setParmValue(bscInfoList, CLP_BSCINFO_PARMMAP);
				// 获取此次添加或者编辑的数据所在的行号
				int selectedRow = -1;
				TParm data = CLP_BSCINFO.getParmValue();
				for (int i = 0; i < CLP_BSCINFO.getRowCount(); i++) {
					if (CLNCPATH_CODE.getValue().equals(
							data.getValue("CLNCPATH_CODE", i))) {
						selectedRow = i;
					}
				}
				// 设置此行为选中状态
				CLP_BSCINFO.setSelectedRow(selectedRow);
				// 将此行的值填充到相应的输入框中
				onTableClickedForBscInfo(selectedRow);
			} else {
				return;
			}
		}
		// 治疗时程
		if (selectedIndex == 1) {
			TParm parm = parmFormatForThrpyschdm();
			if (!checkThrpyschdm()) {
				return;
			}
			if (saveThrpyschdm(parm)) {
				// 查询全部治疗时程信息
				TParm thrpyschdmList = ThrpyschdmTool.getInstance()
						.getThrpyschdmList(parm);
				// System.out.println("---------------全部治疗时程:" +
				// thrpyschdmList);
				// 显示在前台界面
				CLP_THRPYSCHDM.setParmValue(thrpyschdmList);
				// 获取此次添加或者编辑的数据所在的行号
				int selectedRow = -1;
				TParm data = CLP_THRPYSCHDM.getParmValue();
				for (int i = 0; i < CLP_THRPYSCHDM.getRowCount(); i++) {
					if (SCHD_CODE.getValue().equals(
							data.getValue("SCHD_CODE", i))) {
						selectedRow = i;
					}
				}
				// 设置此行为选中状态
				CLP_THRPYSCHDM.setSelectedRow(selectedRow);
				// 将此行的值填充到相应的输入框中
				onTableClickedForThrpyschdm(selectedRow);
			} else {
				return;
			}
		}
		// 医嘱套餐
		if (selectedIndex == 2) {
			if (CLP_PACK01.getRowCount() <= 1) {
				this.messageBox("没有需要保存的数据！");
				return;
			}
			TParm parm = parmFormatForPack("Y");
			// 医嘱套餐校验
			if (!checkPack(CLP_PACK01, parm)) {
				return;
			}
			parm.setData("CLP_PACK", CLP_PACK01.getParmValue().getData());
			TParm result = new TParm();
			if ("Y".equals(ACTIVE_FLG.getValue())) {
				// 启用状态为Y时，将医嘱套餐插入历史表，当前医嘱套餐和临床路径版本号加1，同时禁用当前临床路径
				parm.setData("CLP_BSCINFO", getUseParm("Y").getData());
				result = TIOM_AppServer.executeAction(
						"action.clp.CLPBscInfoAction", "savePack", parm);
			} else {
				// 启用状态为N时，直接更新当前医嘱套餐
				parm.setData("CLP_BSCINFO", getUseParm("N").getData());
				result = TIOM_AppServer.executeAction(
						"action.clp.CLPBscInfoAction", "savePack", parm);
			}
			// 判断错误值
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				this.messageBox("保存失败！");
				return;
			} else {
				this.messageBox("保存成功！");
				onQuery();
				resetVersionAndActiveFlg(CLP_PACK01, "Y");
				tTabbedPane_0.setSelectedIndex(0);
				return;
			}
		}
		// 关键诊疗项目
		if (selectedIndex == 3) {
			if (CLP_PACK02.getRowCount() <= 1) {
				this.messageBox("没有需要保存的数据！");
				return;
			}
			TParm parm = parmFormatForPack("N");
			// 医嘱套餐校验
			if (!checkPack(CLP_PACK02, parm)) {
				return;
			}
			parm.setData("CLP_PACK", CLP_PACK02.getParmValue().getData());
			// System.out.println("-------parm:"+parm);
			TParm result = new TParm();
			if ("Y".equals(ACTIVE_FLG.getValue())) {
				// 启用状态为Y时，将医嘱套餐插入历史表，当前医嘱套餐和临床路径版本号加1，同时禁用当前临床路径
				parm.setData("CLP_BSCINFO", getUseParm("Y").getData());
				result = TIOM_AppServer.executeAction(
						"action.clp.CLPBscInfoAction", "savePack", parm);
			} else {
				// 启用状态为N时，直接更新当前医嘱套餐
				parm.setData("CLP_BSCINFO", getUseParm("N").getData());
				result = TIOM_AppServer.executeAction(
						"action.clp.CLPBscInfoAction", "savePack", parm);
			}
			// 判断错误值
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				this.messageBox("保存失败！");
				return;
			} else {
				this.messageBox("保存成功！");
				onQuery();
				resetVersionAndActiveFlg(CLP_PACK02, "Y");
				return;
			}
		}
		// 护理计划
		if (selectedIndex == 4) {
			if (CLP_PACK03.getRowCount() <= 1) {
				this.messageBox("没有需要保存的数据！");
				return;
			}
			TParm parm = parmFormatForPack("O");
			// 医嘱套餐校验
			if (!checkPack(CLP_PACK03, parm)) {
				return;
			}
			parm.setData("CLP_PACK", CLP_PACK03.getParmValue().getData());
			TParm result = new TParm();
			if ("Y".equals(ACTIVE_FLG.getValue())) {
				// 启用状态为Y时，将医嘱套餐插入历史表，当前医嘱套餐和临床路径版本号加1，同时禁用当前临床路径
				parm.setData("CLP_BSCINFO", getUseParm("Y").getData());
				result = TIOM_AppServer.executeAction(
						"action.clp.CLPBscInfoAction", "savePack", parm);
			} else {
				// 启用状态为N时，直接更新当前医嘱套餐
				parm.setData("CLP_BSCINFO", getUseParm("N").getData());
				result = TIOM_AppServer.executeAction(
						"action.clp.CLPBscInfoAction", "savePack", parm);
			}
			// 判断错误值
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				this.messageBox("保存失败！");
				return;
			} else {
				this.messageBox("保存成功！");
				onQuery();
				resetVersionAndActiveFlg(CLP_PACK03, "Y");
				return;
			}
		}
	}

	/**
	 * 重新设置关联信息
	 * 
	 * @param table
	 *            TTable
	 * @param activeFlg
	 *            String
	 */
	public void resetVersionAndActiveFlg(TTable table, String activeFlg) {
		int selectedRow = CLP_BSCINFO.getSelectedRow();
		// 重新设置版本
		String version = String.valueOf(table.getItemData(0, "VERSION"));
		VERSION.setValue(version);
		CLP_BSCINFO.setItem(selectedRow, "VERSION", version);
		// 重新设置启用标识
		ACTIVE_FLG.setValue(activeFlg);
		CLP_BSCINFO.setItem(selectedRow, "ACTIVE_FLG", activeFlg);
		// 最后一行选中状态
		if (table.getRowCount() > 0) {
			table.setSelectedRow(table.getRowCount() - 1);
		}
	}

	/**
	 * 删除
	 */
	public void onDelete() {
		// 获取页签索引
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		if (selectedIndex == 0) {
			// 临床路径类别
			int selectedRow = CLP_BSCINFO.getSelectedRow();
			if (CLP_BSCINFO.getRowCount() > 0 && selectedRow >= 0) {
				if (this.messageBox("询问", "是否删除？", 2) == 0) {
					TParm parm = new TParm();
					parm.setData("REGION_CODE", REGION_CODE.getValue());
					parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
					TParm result = TIOM_AppServer.executeAction(
							"action.clp.CLPBscInfoAction", "deleteBscInfo",
							parm);
					// 判断错误值
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						this.messageBox("删除失败！");
						return;
					} else {
						this.messageBox("删除成功！");
						CLP_BSCINFO.removeRow(selectedRow);
						/**
						 * 若删除后表格行数为零，则清除以前信息
						 * 若删除的是最后一行，则焦点转移到已删除行的上一行（既删除后的最后一行）
						 * 若删除的不是最后一行，则焦点转移到已删除行的下一行（既当前行号）
						 */
						if (CLP_BSCINFO.getRowCount() <= 0) {
							onClear();
						} else if (selectedRow >= CLP_BSCINFO.getRowCount()) {
							// 设置选中行
							CLP_BSCINFO.setSelectedRow(CLP_BSCINFO
									.getRowCount() - 1);
							// 将此行的值填充到相应的输入框中
							onTableClickedForBscInfo(CLP_BSCINFO.getRowCount() - 1);
						} else {
							// 设置此行为选中状态
							CLP_BSCINFO.setSelectedRow(selectedRow);
							// 将此行的值填充到相应的输入框中
							onTableClickedForBscInfo(selectedRow);
						}
						return;
					}
				}
			}
		} else if (selectedIndex == 1) {
			// 治疗时程
			int selectedRow = CLP_THRPYSCHDM.getSelectedRow();
			if (CLP_THRPYSCHDM.getRowCount() > 0 && selectedRow >= 0) {
				if (this.messageBox("询问", "是否删除？", 2) == 0) {
					TParm parm = new TParm();
					parm.setData("REGION_CODE", REGION_CODE.getValue());
					parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
					parm.setData("SCHD_CODE", SCHD_CODE.getValue());
					TParm result = TIOM_AppServer.executeAction(
							"action.clp.CLPBscInfoAction", "deleteThrpyschdm",
							parm);
					// 判断错误值
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						this.messageBox("删除失败！");
						return;
					} else {
						this.messageBox("删除成功！");
						CLP_THRPYSCHDM.removeRow(selectedRow);
						/**
						 * 若删除后表格行数为零，则清除以前信息
						 * 若删除的是最后一行，则焦点转移到已删除行的上一行（既删除后的最后一行）
						 * 若删除的不是最后一行，则焦点转移到已删除行的下一行（既当前行号）
						 */
						if (CLP_THRPYSCHDM.getRowCount() <= 0) {
							onClear();
						} else if (selectedRow >= CLP_THRPYSCHDM.getRowCount()) {
							// 设置选中行
							CLP_THRPYSCHDM.setSelectedRow(CLP_THRPYSCHDM
									.getRowCount() - 1);
							// 将此行的值填充到相应的输入框中
							onTableClickedForThrpyschdm(CLP_THRPYSCHDM
									.getRowCount() - 1);
						} else {
							// 设置此行为选中状态
							CLP_THRPYSCHDM.setSelectedRow(selectedRow);
							// 将此行的值填充到相应的输入框中
							onTableClickedForThrpyschdm(selectedRow);
						}
					}
				}
			}
		} else if (selectedIndex == 2) {
			// 医嘱套餐
			int selectedRow = CLP_PACK01.getSelectedRow();
			CLP_PACK01.acceptText();
			TParm data = CLP_PACK01.getParmValue();
			TParm parm = new TParm();
			for (int i = 0; i < CLP_PACK01.getRowCount(); i++) {
				String selFlg = data.getValue("SEL_FLG", i);
				if ("Y".equals(selFlg)) {
					parm.addData("REGION_CODE", REGION_CODE.getValue());
					parm.addData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
					parm.addData("SCHD_CODE", SCHD_CODE.getValue());
					parm.addData("ORDER_TYPE", data.getValue("ORDER_TYPE", i));
					parm.addData("ORDER_CODE", data.getValue("ORDER_CODE", i));
					parm.addData("CHKTYPE_CODE", data.getValue("CHKTYPE_CODE",
							i));
					parm.addData("ORDER_SEQ_NO", data.getValue("ORDER_SEQ_NO",
							i));
				}
			}
			if (parm.getCount("REGION_CODE") <= 0) {
				return;
			}
			if (CLP_PACK01.getRowCount() > 0 && selectedRow >= 0) {
				if (this.messageBox("询问", "是否删除？", 2) == 0) {
					// 处理parm
					TParm parmsend = parmFormatForPack("Y");// 该参数没有作用，可忽略，仅使用该parm中的其他信息
					if ("Y".equals(ACTIVE_FLG.getValue())) {
						parmsend.setData("CLP_BSCINFO", getUseParm("Y")
								.getData());
					} else {
						parmsend.setData("CLP_BSCINFO", getUseParm("N")
								.getData());
					}
					parmsend.setData("saveMap", parm.getData());
					TParm result = TIOM_AppServer.executeAction(
							"action.clp.CLPBscInfoAction", "deletePack",
							parmsend);
					// 判断错误值
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						this.messageBox("删除失败！");
						return;
					} else {
						this.messageBox("删除成功！");
						onQuery();
						// 一定要查询后再更新状态，必须在调用前先onQuery
						resetVersionAndActiveFlg(CLP_PACK01, "N");
						if (CLP_PACK01.getRowCount() > 0) {
							CLP_PACK01
									.setSelectedRow(CLP_PACK01.getRowCount() - 1);
						}
					}
				}
			}
		} else if (selectedIndex == 3) {
			// 关键诊疗套餐
			int selectedRow = CLP_PACK02.getSelectedRow();
			CLP_PACK02.acceptText();
			TParm data = CLP_PACK02.getParmValue();
			TParm parm = new TParm();
			for (int i = 0; i < CLP_PACK02.getRowCount(); i++) {
				String selFlg = data.getValue("SEL_FLG", i);
				if ("Y".equals(selFlg)) {
					parm.addData("REGION_CODE", REGION_CODE.getValue());
					parm.addData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
					parm.addData("SCHD_CODE", SCHD_CODE.getValue());
					parm.addData("ORDER_TYPE", data.getValue("ORDER_TYPE", i));
					parm.addData("ORDER_CODE", data.getValue("ORDER_CODE", i));
					parm.addData("CHKTYPE_CODE", data.getValue("CHKTYPE_CODE",
							i));
					parm.addData("ORDER_SEQ_NO", data.getValue("ORDER_SEQ_NO",
							i));
				}
			}
			if (parm.getCount("REGION_CODE") <= 0) {
				return;
			}
			if (CLP_PACK02.getRowCount() > 0 && selectedRow >= 0) {
				if (this.messageBox("询问", "是否删除？", 2) == 0) {
					// 处理parm
					TParm parmsend = parmFormatForPack("N"); // 该参数没有作用，可忽略，仅使用该parm中的其他信息
					if ("Y".equals(ACTIVE_FLG.getValue())) {
						parmsend.setData("CLP_BSCINFO", getUseParm("Y")
								.getData());
					} else {
						parmsend.setData("CLP_BSCINFO", getUseParm("N")
								.getData());
					}
					parmsend.setData("saveMap", parm.getData());
					TParm result = TIOM_AppServer.executeAction(
							"action.clp.CLPBscInfoAction", "deletePack",
							parmsend);
					// 判断错误值
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						this.messageBox("删除失败！");
						return;
					} else {
						this.messageBox("删除成功！");
						onQuery();
						resetVersionAndActiveFlg(CLP_PACK02, "N");
						if (CLP_PACK02.getRowCount() > 0) {
							CLP_PACK02
									.setSelectedRow(CLP_PACK02.getRowCount() - 1);
						}
					}
				}
			}
		} else if (selectedIndex == 4) {
			// 护理计划
			int selectedRow = CLP_PACK03.getSelectedRow();
			CLP_PACK03.acceptText();
			TParm data = CLP_PACK03.getParmValue();
			TParm parm = new TParm();
			for (int i = 0; i < CLP_PACK03.getRowCount(); i++) {
				String selFlg = data.getValue("SEL_FLG", i);
				if ("Y".equals(selFlg)) {
					parm.addData("REGION_CODE", REGION_CODE.getValue());
					parm.addData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
					parm.addData("SCHD_CODE", SCHD_CODE.getValue());
					parm.addData("ORDER_TYPE", data.getValue("ORDER_TYPE", i));
					parm.addData("ORDER_CODE", data.getValue("ORDER_CODE", i));
					parm.addData("CHKTYPE_CODE", data.getValue("CHKTYPE_CODE",
							i));
					parm.addData("ORDER_SEQ_NO", data.getValue("ORDER_SEQ_NO",
							i));
				}
			}
			if (parm.getCount("REGION_CODE") <= 0) {
				return;
			}
			if (CLP_PACK03.getRowCount() > 0 && selectedRow >= 0) {
				if (this.messageBox("询问", "是否删除？", 2) == 0) {
					// 处理parm
					TParm parmsend = parmFormatForPack("O");// 该参数没有作用，可忽略，仅使用该parm中的其他信息
					if ("Y".equals(ACTIVE_FLG.getValue())) {
						parmsend.setData("CLP_BSCINFO", getUseParm("Y")
								.getData());
					} else {
						parmsend.setData("CLP_BSCINFO", getUseParm("N")
								.getData());
					}
					parmsend.setData("saveMap", parm.getData());
					TParm result = TIOM_AppServer.executeAction(
							"action.clp.CLPBscInfoAction", "deletePack",
							parmsend);
					// 判断错误值
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						this.messageBox("删除失败！");
						return;
					} else {
						this.messageBox("删除成功！");
						onQuery();
						resetVersionAndActiveFlg(CLP_PACK03, "N");
						if (CLP_PACK03.getRowCount() > 0) {
							CLP_PACK03
									.setSelectedRow(CLP_PACK03.getRowCount() - 1);
						}
					}
				}
			}
		}
	}

	/**
	 * 设置启用标识
	 * 
	 * @param ACTIVE_FLG
	 *            String Y:启用 N:禁用
	 * @return TParm
	 */
	public TParm getUseParm(String activeFlg) {
		TParm parm = new TParm();
		parm.setData("REGION_CODE", REGION_CODE.getValue());
		parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", dateFormat.format(SystemTool.getInstance()
				.getDate()));
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("ACTIVE_FLG", activeFlg);
		parm.setData("VERSION", VERSION.getValue());
		return parm;
	}

	/**
	 * 保存时，治疗天数相加<=标准住院天数
	 * 
	 * @param clncPathCode
	 *            String
	 * @return boolean
	 */
	public boolean checkStayHospDays(String clncPathCode) {
		int schdDay = 0;
		// String sql =
		// " SELECT SUM(SCHD_DAY) AS SCHD_DAY FROM CLP_THRPYSCHDM WHERE"
		// + " CLNCPATH_CODE = '" + clncPathCode + "'";
		// TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// schdDay = result.getInt("SCHD_DAY", 0);
		// luhai modify begin 20110629
		StringBuffer sqlbf = new StringBuffer();
		sqlbf.append("SELECT SCHD_DAY,SUSTAINED_DAYS FROM CLP_THRPYSCHDM  ");
		sqlbf.append(" WHERE  CLNCPATH_CODE='" + clncPathCode + "'");
		sqlbf.append("ORDER BY SUSTAINED_DAYS");
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sqlbf.toString()));
		for (int i = 0; i < result.getCount(); i++) {
			TParm rowParm = result.getRow(i);
			if (i == 0) {
				schdDay += rowParm.getInt("SCHD_DAY");
				continue;
			}
			// 非第一天的情况
			TParm beforeRowParm = result.getRow(i - 1);
			int nowSusTainedDay = rowParm.getInt("SUSTAINED_DAYS");
			int beforeSusTainedDay = beforeRowParm.getInt("SUSTAINED_DAYS");
			schdDay += rowParm.getInt("SCHD_DAY")
					+ ((nowSusTainedDay - beforeSusTainedDay) <= 0 ? (nowSusTainedDay
							- beforeSusTainedDay - 1)
							: 0);

		}
		// luhai modify end 20110629
		int stayHospDays = Integer.parseInt(STAYHOSP_DAYS.getValue());
		if (schdDay != stayHospDays) {
			this.messageBox("时程总天数和标准住院天数不一致！");
			return false;
		}
		return true;
	}

	/**
	 * 启用
	 */
	public void onUse() {
		// 获取页签索引
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		if (selectedIndex == 0) {
			// 此临床路径已经启用
			if ("Y".equals(ACTIVE_FLG.getValue())) {
				return;
			}
			// 时程总天数等于标准住院天数
			if (!checkStayHospDays(CLNCPATH_CODE.getValue())) {
				return;
			}
			// 临床路径类别
			int selectedRow = CLP_BSCINFO.getSelectedRow();
			if (CLP_BSCINFO.getRowCount() > 0 && selectedRow >= 0) {
				if (this.messageBox("询问", "是否启用？", 2) == 0) {
					TParm result = BscInfoTool.getInstance().use(
							getUseParm("Y"));
					if (result.getErrCode() >= 0) {
						// 清空以前信息
						onClear();
						// 模糊查询时程
						onQuery();
						this.messageBox("启用成功！");
						// 设置此行为选中状态
						CLP_BSCINFO.setSelectedRow(selectedRow);
						// 将此行的值填充到相应的输入框中
						onTableClickedForBscInfo(selectedRow);
					} else {
						this.messageBox("启用失败！");
					}
				}
			}
		}
	}

	// /**
	// * 诊断框popo
	// */
	// public void popoDiagnose(){
	// TParm parm = new TParm();
	// TTextField textfield = (TTextField) this.getComponent("diagnose_desc");
	// textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
	// "%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
	// // 给新text增加接受sys_fee弹出窗口的回传值
	// textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
	// "popDiagReturn");
	// }

	/**
	 * 诊断
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popDiagReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		this.setValue("diagnose", icdCode);
		this.setValue("diagnose_desc", icdDesc);
	}

	/**
	 * 诊断end
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popDiagReturnEnd(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		this.setValue("diagnose_end", icdCode);
		this.setValue("diagnose_desc_end", icdDesc);
	}

	// /**
	// * 手术诊断popo
	// */
	// public void popoOperatorDiagnose() {
	// TParm parm = new TParm();
	// TTextField textfield =
	// (TTextField)this.getComponent("operation_diagnose_desc");
	// textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
	// "%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
	// // 给新text增加接受sys_fee弹出窗口的回传值
	// textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
	// "popOperatorDiagReturn");
	// }

	/**
	 * 新增手术诊断
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popOperatorDiagReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		this.setValue("operation_diagnose", icdCode);
		this.setValue("operation_diagnose_desc", icdDesc);
	}

	/**
	 * 新增手术诊断end
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popOperatorDiagReturnEnd(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		this.setValue("operation_diagnose_end", icdCode);
		this.setValue("operation_diagnose_desc_end", icdDesc);
	}

	/**
	 * 向TParm中加入系统默认信息
	 * 
	 * @param parm
	 *            TParm
	 */
	private void putBasicSysInfoIntoParm(TParm parm) {
		int total = parm.getCount();
		// System.out.println("total" + total);
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("OPT_USER", Operator.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		parm.setData("OPT_DATE", datestr);
		parm.setData("OPT_TERM", Operator.getIP());
	}

	/**
	 * 得到当前时间
	 * 
	 * @param dataForatStr
	 *            String
	 * @return String
	 */
	private String getDateStr(String dataForatStr) {
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, dataForatStr);
		return datestr;
	}

	/**
	 * 得到当前时间（默认返回年月日信息）
	 * 
	 * @return String
	 */
	private String getDateStr() {
		return getDateStr("yyyyMMdd");
	}

	/**
	 * 数字验证方法
	 * 
	 * @param validData
	 *            String
	 * @return boolean
	 */
	private boolean validNumber(String validData) {
		Pattern p = Pattern.compile("[0-9]{1,}");
		Matcher match = p.matcher(validData);
		if (!match.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 得到int数值
	 * 
	 * @param str
	 *            String
	 * @return int
	 */
	private int getIntValue(String str) {
		int tmp = 0;
		try {
			tmp = Integer.parseInt(str);
		} catch (Exception e) {
			tmp = 0;
		}
		return tmp;
	}

	class comparatorDuration implements Comparator {
		public int compare(Object obj1, Object obj2) {
			int returnvalue = 0;
			Map<String, Integer> objMap1 = (HashMap<String, Integer>) obj1;
			Integer startDay1 = objMap1.get("currentStartDay");
			Map<String, Integer> objMap2 = (HashMap<String, Integer>) obj2;
			Integer startDay2 = objMap2.get("currentStartDay");
			return startDay1.compareTo(startDay2);
		}

	}

	/**
	 * 得到指定table的选中行
	 * 
	 * @param tableName
	 *            String
	 * @return int
	 */
	private int getSelectedRow(String tableName) {
		int selectedIndex = -1;
		if (tableName == null || tableName.length() <= 0) {
			return -1;
		}
		Object componentObj = this.getComponent(tableName);
		if (!(componentObj instanceof TTable)) {
			return -1;
		}
		TTable table = (TTable) componentObj;
		selectedIndex = table.getSelectedRow();
		return selectedIndex;
	}

	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}

	/**
	 * 取得界面TTextField
	 * 
	 * @param tag
	 *            String
	 * @return TTextField
	 */
	public TTextField getTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

	/**
	 * 根据Operator得到map
	 * 
	 * @return Map
	 */
	private Map getBasicOperatorMap() {
		Map map = new HashMap();
		map.put("REGION_CODE", Operator.getRegion());
		map.put("OPT_USER", Operator.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		map.put("OPT_DATE", datestr);
		map.put("OPT_TERM", Operator.getIP());
		return map;
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
		CLP_BSCINFO.setParmValue(parmTable);
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
				TParm tableData = CLP_BSCINFO.getParmValue();
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
				String tblColumnName = CLP_BSCINFO.getParmMap(sortColumn);
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
	 * 右击MENU弹出事件
	 * 
	 * @param tableName
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("CLP_PACK01");
		TTabbedPane tab = (TTabbedPane) this.getComponent("tTabbedPane_0");
		int selectIndex = tab.getSelectedIndex() + 1;
		if (selectIndex != 3)
			return;
		TParm action = this.CLP_PACK01.getParmValue().getRow(
				CLP_PACK01.getSelectedRow());
		if (null != action.getValue("CAT1_TYPE")
				&& ("LIS".equals(action.getValue("CAT1_TYPE")) || "RIS"
						.equals(action.getValue("CAT1_TYPE")))) {
			table.setPopupMenuSyntax("显示集合医嘱细项,openRigthPopMenu");
		} else {
			table.setPopupMenuSyntax("");
			return;
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
	 * 打开集合医嘱细想查询
	 */
	public void openRigthPopMenu() {

		TParm action = this.CLP_PACK01.getParmValue().getRow(
				CLP_PACK01.getSelectedRow());
		String orderCode = action.getValue("ORDER_CODE");
		if (StringUtil.isNullString(orderCode)) {
			System.out
					.println("OpdOrder->getOrderSetDetails->orderSetCode is invalie");
			return;
		}
		String sql = " SELECT A.ORDERSET_CODE,A.ORDER_CODE,B.ORDER_DESC,B.OWN_PRICE,B.SPECIFICATION,A.DOSAGE_QTY,B.UNIT_CODE"
				+ " FROM SYS_ORDERSETDETAIL A,SYS_FEE B WHERE A.ORDER_CODE =B.ORDER_CODE AND A.ORDERSET_CODE='"
				+ orderCode + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		int count = parm.getCount();
		if (count <= 0) {
			// System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return;
		}
		TParm result = getOrderSetDetails(parm);
		// this.messageBox_("集合医嘱细项"+parm);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", result);
	}

	/**
	 * 返回集合医嘱细相的TParm形式
	 * 
	 * @return result TParm
	 */
	public TParm getOrderSetDetails(TParm parm) {
		TParm result = new TParm();
		int count = parm.getCount();
		if (count < 0) {
			// System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return result;
		}
		// TDS ds = odiObject.getDS("ODI_ORDER");
		// String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		// TParm parm = ds.getBuffer(buff);

		// System.out.println("groupNo=-============" + groupNo);
		// System.out.println("orderSetCode===========" + orderSetCode);
		// System.out.println("count===============" + count);
		// temperr细项价格
		for (int i = 0; i < count; i++) {
			// tempNo = parm.getInt("ORDERSET_GROUP_NO", i);
			// System.out.println("tempCode==========" + tempCode);
			// System.out.println("tempNO============" + tempNo);
			// System.out.println("setmain_flg========" +
			// parm.getBoolean("SETMAIN_FLG", i));
			// ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
			result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
			result.addData("SPECIFICATION", parm.getValue("SPECIFICATION", i));
			result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
			result.addData("MEDI_UNIT", parm.getValue("UNIT_CODE", i));
			// 查询单价
			// TParm ownPriceParm = new TParm(this.getDBTool().select(
			// "SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE='"
			// + parm.getValue("ORDER_CODE", i) + "'"));
			// this.messageBox_(ownPriceParm);
			// 计算总价格
			double ownPrice = parm.getDouble("OWN_PRICE", i)
					* parm.getDouble("DOSAGE_QTY", i);
			result.addData("OWN_PRICE", parm.getDouble("OWN_PRICE", i));
			result.addData("OWN_AMT", ownPrice);
			result.addData("EXEC_DEPT_CODE", "");
			result.addData("OPTITEM_CODE", "");
			result.addData("INSPAY_TYPE", "");
		}
		return result;
	}

	/**
	 * vectory转成param
	 */
	private void cloneVectoryParamOne(Vector vectorTable, TParm parmTable,
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
		CLP_PACK01.setParmValue(parmTable);
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
	private Vector getVectorOne(TParm parm, String group, String names, int size) {
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
	private int tranParmColIndexOne(String columnName[], String tblColumnName) {
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

	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListenerOne(final TTable table) {
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
				if (j == sortColumnOne) {
					ascendingOne = !ascendingOne;
				} else {
					ascendingOne = true;
					sortColumnOne = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = CLP_PACK01.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVectorOne(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = CLP_PACK01.getParmMap(sortColumnOne);
				// 转成parm中的列
				int col = tranParmColIndexOne(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compareOne.setDes(ascendingOne);
				compareOne.setCol(col);
				java.util.Collections.sort(vct, compareOne);
				// 将排序后的vector转成parm;
				cloneVectoryParamOne(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * 导出EXCEL
	 */
	public void onExport() {
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		switch (selectedIndex){
		case 0:		
			ExportExcelUtil.getInstance().exportExcel(CLP_BSCINFO,
			"临床路径类别");
			break;
		case 1:
			ExportExcelUtil.getInstance().exportExcel(CLP_THRPYSCHDM,
			"治疗时程");
			break;
		case 2:
			ExportExcelUtil.getInstance().exportExcel(CLP_PACK01,
			"医嘱套餐-类别:"+this.getText("CLNCPATH_CODE02")+" 时程:"+this.getText("SCHD_CODE01"));
			break;
		}
	}
}
