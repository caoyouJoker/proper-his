package com.javahis.ui.mro;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;

import jdo.adm.ADMTool;
import jdo.adm.ADMXMLTool;
import jdo.bms.BMSTool;
import jdo.emr.GetWordValue;
import jdo.mro.MROPrintTool;
import jdo.mro.MRORecordTool;
import jdo.mro.MROSqlTool;
import jdo.mro.MROTool;
import jdo.ope.OPETool;
import jdo.sta.STAIdcardValidator;
import jdo.sys.CTZTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SYSPostTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFormatEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.emr.EMRTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 病案首页
 * </p>
 * 
 * <p>
 * Description: 病案首页
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008 
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author ZhangK 2009-3-24
 * @version 4.0
 */
public class MRORecordControl extends TControl {

	private String REGION_CODE = Operator.getRegion(); // 院区
	private String MR_NO;// 病患病案号
	private String CASE_NO;// 病患就诊序号
	// String SYSTEM_CODE = "MRO";//记录哪个系统调用此界面 当值为“MRO”时 该界面处于编辑状态
	private Map map; // 收据费用字典
	private TTable DiagGrid;// 诊断Table
	private boolean opeSaveFlg = false;
	/**
	 * 权限说明 1.住院部 2.医师 3.财务 4.病案室(全部权限) 其中特殊权限 医师权限只有经治医师可以修改自己的病人
	 */
	private String UserType = "";
	private String OpenUser = "";// 记录经治医师参数
	private String VS_DR = "";// 记录病患的经治医师
	private String ADMCHK_FLG = "";// 住院处提交
	
	private String DIAGCHK_FLG = "";// 医师提交
	private String BILCHK_FLG = "";// 账务提交
	private String QTYCHK_FLG = "";// 质控提交
	private String OPT_USER = "";// 操作人员
	private String OPT_TERM = "";// 操作IP
	private Timestamp dsDate;// 出院时间
	TWord word;
	
	
	
	// modified by WangQing 20170411 -start
	// 添加俩字段，用于职业和联系人信息显示问题
	String oldOccCode = "";
	String oldRelationCode = "";
	// modified by WangQing 20170411 -end
	
	
	
	/*
	 * 生成研究病例使用sql语句
	 */
	private String QUERY_EMR_SQL = "SELECT # FROM EMR_FILE_INDEX WHERE CASE_NO = '#'";
	
	//声明要添加焦点监听事件的组件名称
	private String[] cmpNames = {"TP23_RBC","TP23_PLATE","TP23_PLASMA","TP23_WHOLE_BLOOD","TP23_BANKED_BLOOD","TP23_OTH_BLOOD"};
	private boolean isFirst = true;
	
	public void onInit() {
		super.onInit();
		DiagGrid = (TTable) this.getComponent("TP21_Table2");
		OPT_USER = Operator.getID();
		OPT_TERM = Operator.getIP();
		// 模拟传参数
		// TParm parm = new TParm();
		// parm.setData("MR_NO","000000000088");
		// parm.setData("CASE_NO","090217000005");
		// 获取传入参数
		// Object obj = parm;
		Object obj = this.getParameter();
		if (obj != null) {
			TParm parmData = (TParm) obj;
			MR_NO = parmData.getValue("MR_NO");// 获取病案号
			CASE_NO = parmData.getValue("CASE_NO");// 获取就诊序号
			UserType = parmData.getValue("USER_TYPE");// 调用权限
			OpenUser = parmData.getValue("OPEN_USER");// 调用人员 参数
			if (MR_NO.trim().length() > 0 && CASE_NO.trim().length() > 0) {
				patInHospInfo();// 查询患某一次者住院信息
				// 设置页签选中状态
				TTabbedPane tP = (TTabbedPane) this.getComponent("tTabbedPane_0");
				TTabbedPane tP2 = (TTabbedPane) this.getComponent("tTabbedPane_1");
				if (UserType.equals("1")) {
					tP.setSelectedIndex(0);// 选中第一页签
					this.setEnabledPage2(false);
					this.setEnabledPage3(false);
					this.setEnabledPage4(false);
					// this.callFunction("UI|tTabbedPane_0|setEnabled", false);
				} else if (UserType.equals("2")) {
					tP.setSelectedIndex(1);// 选中第二页签
					// this.callFunction("UI|tTabbedPane_0|setEnabled", false);
					this.setEnabledPage1(false);
					this.setEnabledPage3(false);
					this.setEnabledPage4(false);
				} else if (UserType.equals("3")) {
					tP.setSelectedIndex(2);// 选中第三页签
					// this.callFunction("UI|tTabbedPane_0|setEnabled", false);
					this.setEnabledPage1(false);
					this.setEnabledPage2(false);
					this.setEnabledPage4(false);
				} else if (UserType.equals("4")) {
					tP.setSelectedIndex(0);// 选中第一页签
					// this.callFunction("UI|tTabbedPane_0|setEnabled", true);
				} else if (UserType.equals("2-4")) {
					tP.setSelectedIndex(1);
					tP2.setSelectedIndex(3);
					setEnagledPage2_2(true);
					callFunction("UI|save|setEnabled", true);
				}
				if(onCheckPatlog()){
					Color color=new Color(255,0,0);
					tP.setTabColor(0, color);
				}
                if (TCM_Transform.getTimestamp(this.getValue("TP2_OUT_DATE")) != null) {
                    onIntoICUData();// wanglong add 20150226
//                    onIntoBMSData();
                }
                
                // add by wangb 2017/06/27 初始化时自动带入输血资料和过敏记录
                onIntoBMSData();
                onIntoAllecData();
			} else {
				return;
			}
		}
		// table专用的监听 ICD10
		DiagGrid.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponent");
		// 模糊查询(调用内部类)
		OrderList orderDesc = new OrderList();
		DiagGrid.addItem("OrderList", orderDesc);
		TTable OP_Table = (TTable) this.getComponent("TP23_OP_Table");
		// 手术Table 监听
		OP_Table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponentOP");
//		 //TP23_OP_Table值改变事件
		this.addEventListener("TP23_OP_Table->" + TTableEvent.CHANGE_VALUE,
		 		"onChangeValueOP");
		// 主手术改变事件
		OP_Table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onOpTableMainCharge");
		// 诊断Grid值改变事件
		this.addEventListener("TP21_Table2->" + TTableEvent.CHANGE_VALUE,
				"onDiagTableValueCharge");
		// 病理诊断监听
		TParm popParm = new TParm();
		popParm.setData("ICD_TYPE", "W");
		popParm.setData("ICD_MIN", "M80000/0");
		popParm.setData("ICD_MAX", "M99890/1");
		callFunction("UI|TP22_PATHOLOGY_DIAG|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x", popParm);
		// textfield接受回传值
		callFunction("UI|TP22_PATHOLOGY_DIAG|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "pathology_Return");
		
		callFunction("UI|TP22_PATHOLOGY_DIAG2|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x", popParm);
		// textfield接受回传值
		callFunction("UI|TP22_PATHOLOGY_DIAG2|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "pathology_Return2");
		
		callFunction("UI|TP22_PATHOLOGY_DIAG3|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x", popParm);
		// textfield接受回传值
		callFunction("UI|TP22_PATHOLOGY_DIAG3|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "pathology_Return3");
		// 损伤中毒诊断监听
		TParm popParm2 = new TParm();
		popParm2.setData("ICD_TYPE", "W");
		popParm2.setData("ICD_START", "V");
		popParm2.setData("ICD_END", "Y");
		callFunction("UI|TP22_EX_RSN|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x", popParm2);
		// textfield接受回传值
		callFunction("UI|TP22_EX_RSN|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "ex_Return");
		callFunction("UI|TP22_EX_RSN2|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x", popParm2);
		// textfield接受回传值
		callFunction("UI|TP22_EX_RSN2|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "ex_Return2");
		callFunction("UI|TP22_EX_RSN3|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x", popParm2);
		// textfield接受回传值
		callFunction("UI|TP22_EX_RSN3|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "ex_Return3");
		callFunction("UI|TP2_TRAN_HOSP|addEventListener",
				TTextFormatEvent.SELECTED, this, "onSelectTran");
		
		word = (TWord) this.getComponent("WORD");
		setSXFYByBMSData(cmpNames);
		setCmpFocusListener(cmpNames);
		
		// modified by WangQing 20170412
		onFinance(); // 财务汇入
	}


	/**
	 * 诊断弹出界面 ICD10
	 * 
	 * @param com
	 *            Component 控件
	 * @param row
	 *            int 行数
	 * @param column
	 *            int 列数
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		// 弹出ICD10对话框的列
		if (column != 4 && column != 7)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		TParm parm = new TParm();
		parm.setData("ICD_TYPE", "W");
		parm.setData("ICD_EXCLUDE","Y");
		parm.setData("ICD_MIN_EX", "M80000/0");
		parm.setData("ICD_MAX_EX", "M99890/1");
		parm.setData("ICD_START_EX", "V");
		parm.setData("ICD_END_EX", "Y");
		// 给table上的新text增加ICD10弹出窗口
		textfield.setPopupMenuParameter("ICD10", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"),parm);
		// 给新text增加接受ICD10弹出窗口的回传值
		if (column == 4)
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"newAgentOrder");
		else if (column == 7)
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"newAdditionalOrder");
	}

	/**
	 * 取得ICD10返回值
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newAgentOrder(String tag, Object obj) {
		TTable table = (TTable) this.callFunction("UI|TP21_Table2|getThis");
		table.acceptText();
		TParm tableParm = table.getParmValue();
		Map map = new HashMap();
		// sysfee返回的数据包
		TParm parm = (TParm) obj;
		String orderCode = parm.getValue("ICD_CODE");
		for (int i = 0; i < tableParm.getCount(); i++) {
			String admType = (String) table.getValueAt(i, 0);
			String code = (String) table.getValueAt(i, 4);
			if (i == table.getSelectedRow())
				continue;
			if (!code.equals("") && map.get(admType + code) == null)
				map.put(admType + code, admType + code);
		}
		String errStr = "";
		if (map.get((String) table.getValueAt(table.getSelectedRow(), 0)
				+ orderCode) != null) {
			if (tableParm.getValue("TYPE", table.getSelectedRow()).equals("I"))
				errStr = "门急诊诊断:";
			if (tableParm.getValue("TYPE", table.getSelectedRow()).equals("M"))
				errStr = "入院诊断:";
			if (tableParm.getValue("TYPE", table.getSelectedRow()).equals("O"))
				errStr = "出院诊断:";
			if (tableParm.getValue("TYPE", table.getSelectedRow()).equals("Q"))
				errStr = "感染诊断:";
			if (tableParm.getValue("TYPE", table.getSelectedRow()).equals("W"))
				errStr = "并发诊断:";
			this.messageBox(errStr + parm.getValue("ICD_CHN_DESC") + "已开立");
			return;
		}
		table.setItem(table.getSelectedRow(), "CODE", orderCode);
		table.setItem(table.getSelectedRow(), "NAME", parm
				.getValue("ICD_CHN_DESC"));
		table
				.setItem(table.getSelectedRow(), "KIND", parm
						.getValue("ICD_TYPE"));
	}

	/**
	 * 取得ICD10返回值(附加码)
	 * 
	 * @param tag
	 *            Stringng
	 * @param obj
	 *            Object
	 */
	public void newAdditionalOrder(String tag, Object obj) {
		TTable table = (TTable) this.callFunction("UI|TP21_Table2|getThis");
		// sysfee返回的数据包
		TParm parm = (TParm) obj;
		String orderCode = parm.getValue("ICD_CODE");
		table.setItem(table.getSelectedRow(), "ADDITIONAL", orderCode);
		table.setItem(table.getSelectedRow(), "ADDITIONAL_DESC", parm
				.getValue("ICD_CHN_DESC"));
	}

	/**
	 * 手术弹出界面 OpICD
	 * 
	 * @param com
	 *            Component 控件
	 * @param row
	 *            int 行数
	 * @param column
	 *            int 列数
	 */
	public void onCreateEditComponentOP(Component com, int row, int column) {
		// 弹出ICD10对话框的列
		if (column != 3)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// 给table上的新text增加ICD10弹出窗口
		textfield.setPopupMenuParameter("OPICD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSOpICD.x"));
		// 给新text增加接受ICD10弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newOPOrder");
	}
	
	/**
	 * 判断所选手术医师是否为外院医师
	 * @param obj 表格中值改变的节点对象
	 */
    public boolean onChangeValueOP(Object obj){
        TTable table = (TTable) this.getComponent("TP23_OP_Table");
        table.acceptText();
        TTableNode node= (TTableNode) obj;
        if ("OP_DATE".equals(table.getDataStoreColumnName(table.getSelectedColumn()))) {//add by wanglong 20140314
            Timestamp outDate = (Timestamp) this.getValue("TP2_OUT_DATE");
            Timestamp opDate = (Timestamp) node.getValue();
            Timestamp indate = StringTool.rollDate((Timestamp) getValue("TP2_IN_DATE"),-1);
			if (indate.getTime() > opDate.getTime()) {
				this.messageBox_("入院日期-1天不能大于手术日期");
				return false;
			}
            if (!(outDate == null || outDate.equals(new TNull(Timestamp.class)))) {
                if (opDate.getTime() > outDate.getTime()) {
                    this.messageBox_("手术日期不能大于出院日期");
                    return true;
                }
            }
        }
        if(!"MAIN_SUGEON".equals(table.getDataStoreColumnName(
                table.getSelectedColumn())))
            return false;
        String docId = node.getValue().toString();
        TParm result = MRORecordTool.getInstance().isOutDoc(docId);
        if(result.getCount() <= 0){
            table.setLockCell(table.getSelectedRow(), 10, true);
            return false;
        }
        if(result.getData("IS_OUT_FLG" , 0) != null && 
                "Y".equals(result.getData("IS_OUT_FLG" , 0).toString())){
            table.setLockCell(table.getSelectedRow(), 10, false);
            return false;
        }
        table.setValueAt("", table.getSelectedRow(), 10);
        table.setLockCell(table.getSelectedRow(), 10, true);
        return false;
    }
	
	/**
	 * 取得手术ICD返回值
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newOPOrder(String tag, Object obj) {
		TTable table = (TTable) this.callFunction("UI|TP23_OP_Table|getThis");
		// sysfee返回的数据包
		TParm parm = (TParm) obj;
		String orderCode = parm.getValue("OPERATION_ICD");
		table.setItem(table.getSelectedRow(), "OP_CODE", orderCode);
		table.setItem(table.getSelectedRow(), "OP_DESC", parm
				.getValue("OPT_CHN_DESC"));
		table.setItem(table.getSelectedRow(), "OP_LEVEL", parm
				.getValue("OPE_LEVEL"));
	}

	/**
	 * 手术Grid 主诊断标记修改事件
	 * 
	 * @param obj
	 *            Object
	 */
	public void onOpTableMainCharge(Object obj) {
		TTable OP_Table = (TTable) this.getComponent("TP23_OP_Table");
		OP_Table.acceptText();
		if (OP_Table.getSelectedColumn() == 0) {
			int row = OP_Table.getSelectedRow();
			for (int i = 0; i < OP_Table.getRowCount(); i++) {
				OP_Table.setItem(i, "MAIN_FLG", "N");
			}
			OP_Table.setItem(row, "MAIN_FLG", "Y");
		}
	}

	/**
	 * 保存
	 */
	public void onSave() {
		// 如果CASE_NO不存在不能保存
		if (CASE_NO.length() <= 0) {
			return;
		}
		// 保存数据检核
		if (!checkSaveData()) {
			return;
		}
		// 判断诊断Grid是否符合标准
		String message = checkDiagGrid();
		if (message.length() > 0) {
			this.messageBox_(message);
			return;
		}
		// 判断首页出院主诊断和临床诊断出院主诊断是否一致 duzhw modify 20131211 /去掉校验duzhw del 20140421
//		if(!checkOMainDiag()) {
//			this.messageBox("出院主诊断和临床诊断出院主诊断不一致！");
//			return;
//		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("Page1", this.getFisrtPageInfo().getData());// 第一页签内容
		if (!onChoiceDSDate()) {// add by wanglong 20120921 住院天数随出院日期更改而联动
			return;
		}
		parm.setData("Page2", this.getSecendPageInfo().getData()); // 第二页签内容
		parm.setData("Page4", this.getFourPageInfo().getData());// 第四页签内容
//		parm.setData("PageOP", this.getOPSQL());// 手术表信息
		parm.setData("PageOP", this.insertMRO_Record_OP().getData());// 手术表信息
		// parm.setData("PageICD", this.getDiagGridData().getData());//
		// 获取诊断Grid的数据进行保存 20120623 shibl modify
		parm.setData("PageICD", this.getNewDiagGridData().getData());//获取诊断Grid的数据进行保存
		TParm result = TIOM_AppServer.executeAction(
				"action.mro.MRORecordAction", "updateDate", parm);
		//add by guoy 20150806
		// modify by wangb 2017/03/14 应床旁系统要求，已出院病患不再向其接口发送消息
//		TParm xmlParm = ADMXMLTool.getInstance().creatADMXMLFile(CASE_NO, "A03");
		if (result.getErrCode() < -1) {
			this.messageBox(result.getErrText());
			return;
		}
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
//		if (result.getErrCode() < 0) {
//			this.messageBox("保存手术记录失败" + result.getErrCode() + result.getErrText() + result.getErrName());
//			return;
//		}
		if (this.getValueString("ADMCHK_FLG").equals("Y"))
			this.setEnabledPage1(false);
		else
			this.setEnabledPage1(true);
		if (this.getValueString("DIAGCHK_FLG").equals("Y"))
			this.setEnabledPage2(false);
		else
			this.setEnabledPage2(true);
		if (this.getValueString("BILCHK_FLG").equals("Y"))
			this.setEnabledPage3(false);
		else
			this.setEnabledPage3(true);
		if (this.getValueString("QTYCHK_FLG").equals("Y"))
			this.setEnabledPage4(false);
		else
			this.setEnabledPage4(true);
		this.messageBox("P0005");
		this.OPTableBind(MR_NO, CASE_NO);
		// shibl add 20120726 避免财务页签没有被切换
		onFinance();
		patInHospInfo();//duzhw
	}

	/**
	 * 汇入病患信息
	 */
	public void onInto() {
		// zhangyong20110405 转入病患信息
		intoData(1); // 汇总外部资料转入到病人首页中
	}

	// /**
	// * 汇入医生信息
	// */
	// public void onIntoDr() {
	// // zhangyong20110405 转入医生信息
	// intoData(2); // 汇总外部资料转入到病人首页中
	// }

	/**
	 * 财务汇入
	 */
	public void onFinance() {
		if (CASE_NO.trim().length() <= 0) {
			return;
		}
		// 查询财务系统中费用汇总，保存到首页表中
		TParm result = MRORecordTool.getInstance().updateMROIbsForIBS(CASE_NO);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm mro_info = MRORecordTool.getInstance().getInHospInfo(parm);
		this.clearTP3();
		this.setValueTP3(mro_info);
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearTP1();
		this.clearTP2();
		this.clearTP3();
		this.clearTP4();
		// 清空Grid
		TTable chgTable = (TTable) this.getComponent("TP21_Table1");
		chgTable.removeRowAll();// 清空动态表
		TTable dailyTable = (TTable) this.getComponent("TP21_Table2");
		dailyTable.removeRowAll();// 清空诊断表
		TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
		opTable.removeRowAll();// 清空手术表
		opTable.resetModify();
	}

	/**
	 * 查询患某一次患者住院信息
	 */
	public void patInHospInfo() {
		if (map == null) {
			map = MRORecordTool.getInstance().getChargeName(); // 获取每条收费的中文名称
			if (map == null) {
				messageBox_("尚未设定收据费用字典!");
				return;
			}
		}
		if (MR_NO == null || MR_NO.length() == 0) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		// 查询病患住院信息 ADM_INP
		TParm adm_inp = ADMTool.getInstance().getADM_INFO(parm);
		VS_DR = adm_inp.getValue("VS_DR_CODE", 0);// 获取病患的经治医师 用于判断权限
		// 查询患者某一次的住院信息
		TParm mro_data = MRORecordTool.getInstance().getMRO_RecordInfo(parm);
		if (mro_data.getErrCode() < 0) {
			this.messageBox_("没有查询到该患者住院信息！");
			return;
		}
		TParm result = mro_data.getParm("RECORD");// 首页信息
		//	result.setData("DAY_OPE_CODE", 0, adm_inp.getValue("DAY_OPE_CODE", 0)); //取adm_inp表中的日间手术字段值
		//	this.messageBox("是否有值:"+result.getValue("DAY_OPE_FLG"));
		
		TParm admtran = mro_data.getParm("ADMTRAN");// 病患动态信息
		this.clearTP1();// 清空原有值
		this.clearTP2();
		this.clearTP3();
		this.clearTP4();
		/*---------------------赋值 ---------------------------*/
		this.setValueTP1(result);// 第一页签
		this.setValueTP2(result);// 第二页签
		this.setValueTP3(result);// 第三页签
		this.setValueTP4(result);// 第四页签
		this.setValueTran(admtran);// 病患转移Grid绑定数据

	}

	/**
	 * 第一页签赋值
	 * 
	 * @param result
	 *            TParm
	 */
	private void setValueTP1(TParm result) {
		/*-------------------第一叶签 start------------------------------*/
		this.setValue("TP1_MR_NO", result.getValue("MR_NO", 0)); // 病案号
		this.setValue("TP1_IPD_NO", result.getValue("IPD_NO", 0)); // 住院号
		this.setValue("PT1_PAT_NAME", result.getValue("PAT_NAME", 0)); // 姓名
		this.setValue("TP1_IDNO", result.getValue("IDNO", 0)); // 身份证
		this.setValue("TP1_SEX", result.getValue("SEX", 0)); // 性别
		this.setValue("TP1_BIRTH_DATE", result.getTimestamp("BIRTH_DATE", 0));// 生日
		this.setValue("TP1_CASE_NO", result.getValue("CASE_NO", 0)); // 住院序号
		this.setValue("TP1_MARRIGE", result.getValue("MARRIGE", 0)); // 婚姻
		// 显示年龄
		if (result.getData("BIRTH_DATE", 0) != null) {
			this.setValue("TP1_AGE", com.javahis.util.StringUtil.showAge(result
					.getTimestamp("BIRTH_DATE", 0), result.getTimestamp(
					"IN_DATE", 0))); // 应该是 IN_DATE
		}
		this.setValue("HOMEPLACE_CODE", result.getValue("HOMEPLACE_CODE", 0));// 出生地代码
		this.setValue("TP1_NATION", result.getValue("NATION", 0)); // 国籍
		this.setValue("TP1_FOLK", result.getValue("FOLK", 0)); // 民族
		this.setValue("TP1_PAYTYPE", result.getValue("CTZ1_CODE", 0)); // 支付方式
		// CTZ1_CODE
		this.setValue("TP1_INNUM", result.getValue("IN_COUNT", 0)); // 住院次数
		this.setValue("TP1_TEL", result.getValue("TEL", 0)); // 患者电话
		// 省市列表选定需要用邮编来确定，省是前3位，市是后3位
		this.setValue("TP1_H_ADDRESS", result.getValue("H_ADDRESS", 0)); // 户口住址
		this.setValue("TP1_H_POSTNO", result.getValue("H_POSTNO", 0)); // 户口邮编
		//	this.setValue("TP1_OCCUPATION", result.getValue("OCCUPATION", 0)); // 职业
		this.setValue("TP1_OFFICE", result.getValue("OFFICE", 0)); // 单位
		this.setValue("TP1_O_TEL", result.getValue("O_TEL", 0)); // 单位电话
		this.setValue("TP1_O_ADDRESS", result.getValue("O_ADDRESS", 0)); // 单位地址
		this.setValue("TP1_O_POSTNO", result.getValue("O_POSTNO", 0)); // 单位邮编
		this.setValue("TP1_CONTACTER", result.getValue("CONTACTER", 0)); // 联系人姓名
		//this.setValue("TP1_RELATIONSHIP", result.getValue("RELATIONSHIP", 0)); // 联系人关系

		// modified by WangQing 20170331 -start 
		// 老的职业信息、联系人关系信息显示
		TTextFormat occText = (TTextFormat) this.getComponent("TP1_OCCUPATION");
		TComboBox relationCombo= (TComboBox) this.getComponent("TP1_RELATIONSHIP");
		relationCombo.setCanEdit(true);
		String sql1 = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_OCCUPATION' AND ID ='" 
				+ result.getValue("OCCUPATION", 0) + "' ";
		String sql2 = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELATIONSHIP' AND ID ='" 
				+ result.getValue("RELATIONSHIP", 0) + "' ";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		occText.setText((String) result1.getData("CHN_DESC", 0));
		relationCombo.setText((String) result2.getData("CHN_DESC", 0));
		
		oldOccCode = result.getValue("OCCUPATION", 0);
		oldRelationCode = result.getValue("RELATIONSHIP", 0);
		// modified by WangQing 20170331 -end

		this.setValue("TP1_CONT_TEL", result.getValue("CONT_TEL", 0)); // 联系人电话
		this.setValue("TP1_CONT_ADDRESS", result.getValue("CONT_ADDRESS", 0)); // 联系人地址
		this.setValue("ADMCHK_FLG", result.getValue("ADMCHK_FLG", 0));// 住院记录标识
		/*---------------------------------开始----------------------------------------*/
		this.setValue("MRO_CTZ", result.getValue("MRO_CTZ", 0)); // 病案首页身份
		this.setValue("BIRTHPLACE", result.getValue("BIRTHPLACE", 0)); // 籍贯
		this.setValue("TP1_ADDRESS", result.getValue("ADDRESS", 0)); // 通信住址
		this.setValue("TP1_POST_NO", result.getValue("POST_NO", 0)); // 通信邮编
		this.setValue("TP1_NHI_NO", result.getValue("NHI_NO", 0)); // 医保号 add
		
		this.onRESID_POST_CODE(); // 根据邮编带入省市
		this.onCompanyPost();// 根据邮编带入省市
		this.onPost();// 根据邮编带入省市
		
		
		
		
		
		
		
		
		// by
		// wanglong
		// 2012-11-27
		this.setValue("TP1_NHI_CARDNO", result.getValue("NHI_CARDNO", 0)); // 健康卡号
		// add
		// by
		// wanglong
		// 2012-11-27
		/*---------------------------------结束----------------------------------------*/
		ADMCHK_FLG = result.getValue("ADMCHK_FLG", 0);
		/*-------------------第一叶签 end------------------------------*/
		// 设置控件是否可以编辑 如果不是 “住院部”和“病案室”调用 设置第一页签不可编辑
		if (!"1".equals(UserType) && !"4".equals(UserType)
				|| "Y".equals(ADMCHK_FLG)) {
			this.setEnabledPage1(false);
		}
		if ("1".equals(UserType) && "Y".equals(ADMCHK_FLG)) {
			callFunction("UI|save|setEnabled", false);
		}
	}

	/**
	 * 第二页签赋值
	 * 
	 * @param result
	 *            TParm
	 */
	private void setValueTP2(TParm result) {
		/*-------------------第二叶签 start----------------------------*/
		
		// modified by WangQing 20170412
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		if(table.getRowCount() <= 0 && isFirst){
			this.OPTableBind(MR_NO,CASE_NO);
			isFirst = false;
		}
		
		
		// 第一小页签
		// this.setValue("TP2_TRANS_DEPT", result.getValue("TRANS_DEPT", 0)); //
		// 转科科室
		Timestamp endTime = result.getValue("OUT_DATE", 0).equals("") ? SystemTool
				.getInstance().getDate()
				: result.getTimestamp("OUT_DATE", 0);
		int realStayDays = StringTool.getDateDiffer(StringTool.getTimestamp(
				StringTool.getString(endTime, "yyyyMMdd"), "yyyyMMdd"),
				StringTool.getTimestamp(StringTool.getString(result
						.getTimestamp("IN_DATE", 0), "yyyyMMdd"), "yyyyMMdd"));
		this.setValue("TP2_REAL_STAY_DAYS", realStayDays > 0 ? String
				.valueOf(realStayDays) : "1"); // 实际住院天数
		this.setValue("TP2_OUT_DEPT", result.getValue("OUT_DEPT", 0)); // 出院科室
		this.setValue("TP2_IN_CONDITION", result.getValue("IN_CONDITION", 0)); // 入院情况
		this.setValue("TP2_OUT_DATE", result.getTimestamp("OUT_DATE", 0));// 出院日期
		this.setValue("TP2_CONFIRM_DATE", result
				.getTimestamp("CONFIRM_DATE", 0));// 确诊日期
		/*
		 * 新增新生儿入院体重和出生体重  addBy ZhangZe
		 */
		
		this.setValue("TP2_NB_ADM_WEIGHT", result.getValue("NB_ADM_WEIGHT",0));
		this.setValue("TP2_NB_WEIGHT", result.getValue("NB_WEIGHT",0));
		
		
		/*--------------------------------20120112add  start------------------------------------------*/
		this.setValue("TP2_ADM_SOURCE", result.getValue("ADM_SOURCE", 0));// 病人来源
		this.setValue("TP2_IN_DATE", result.getTimestamp("IN_DATE", 0));// 入院日期
		this.setValue("TP2_IN_DEPT", result.getValue("IN_DEPT", 0));// 入院科室
		this.setValue("TP2_OUT_TYPE", result.getValue("OUT_TYPE", 0));// 离院方式
		this.outTypeSelect();
		this.setValue("TP2_TRAN_HOSP", result.getValue("TRAN_HOSP", 0));// 外转院所
		this.onSelectTran();
		// 20120813 shibl modify
		this.setValue("TRAN_HOSP_OTHER", result.getValue("TRAN_HOSP_OTHER", 0));// 外转院所的其他补充
		// 20121129 shibl add
		/*********************************************************************************/
		this.setValue("TP2_SPENURS_DAYS", result.getValue("SPENURS_DAYS", 0));// 特级护理天数
		this.setValue("TP2_FIRNURS_DAYS", result.getValue("FIRNURS_DAYS", 0));// 一级护理天数
		this.setValue("TP2_SECNURS_DAYS", result.getValue("SECNURS_DAYS", 0));// 二级护理天数
		this.setValue("TP2_THRNURS_DAYS", result.getValue("THRNURS_DAYS", 0));// 三级护理天数
		this.setValue("TP2_VENTI_TIME", result.getValue("VENTI_TIME", 0));// 呼吸机使用时间(待确定)
		
		// add by wangqing 20180116 start 新增呼吸机使用分钟、呼吸机使用秒
		this.setValue("TP2_VENTI_TIME_MIN", result.getValue("VENTI_TIME_MIN", 0));//呼吸机使用分钟
		this.setValue("TP2_VENTI_TIME_SEC", result.getValue("VENTI_TIME_SEC", 0));//呼吸机使用分钟
		// add by wangqing 20180116 end
		
		/*********************************************************************************/
		String betime = result.getValue("BE_COMA_TIME", 0);// 昏迷入院前时间
		String aftime = result.getValue("AF_COMA_TIME", 0);// 昏迷入院后时间
		if (betime.equals("")) {
			this.setValue("TP2_BE_IN_D", "");// 天
			this.setValue("TP2_BE_IN_H", "");// 小时
			this.setValue("TP2_BE_IN_M", "");// 分钟
		} else {
			this.setValue("TP2_BE_IN_D", TypeTool
					.getInt(betime.substring(0, 2)));// 天
			this.setValue("TP2_BE_IN_H", TypeTool
					.getInt(betime.substring(2, 4)));// 小时
			this.setValue("TP2_BE_IN_M", TypeTool
					.getInt(betime.substring(4, 6)));// 分钟
		}
		if (aftime.equals("")) {
			this.setValue("TP2_AF_IN_D", "");// 天
			this.setValue("TP2_AF_IN_H", "");// 小时
			this.setValue("TP2_AF_IN_M", "");// 分钟
		} else {
			this.setValue("TP2_AF_IN_D", TypeTool
					.getInt(aftime.substring(0, 2)));// 天
			this.setValue("TP2_AF_IN_H", TypeTool
					.getInt(aftime.substring(2, 4)));// 小时
			this.setValue("TP2_AF_IN_M", TypeTool
					.getInt(aftime.substring(4, 6)));// 分钟
		}
		this.setValue("TP2_VS_NURSE_CODE", result.getValue("VS_NURSE_CODE", 0));// 责任护士
		if (result.getValue("AGN_PLAN_FLG", 0).equals("Y")) {
			this.getCheckbox("TP2_AGN_PLAN_FLG").setSelected(true); // 31天再住院计划标记
		}
		this.setValue("TP2_AGN_PLAN_INTENTION", result.getValue(
				"AGN_PLAN_INTENTION", 0));// 31天再住院计划目的
		/*--------------------------------20120112add  end------------------------------------------*/
		// 诊断列表 4.0中的诊断列表是从病案主表中的列 转换而来，是固定的条数 为空的就不显示
		TParm diagnosis = new TParm(
				this
						.getDBTool()
						.select(
								"SELECT 'Y' AS EXEC, IO_TYPE AS TYPE,ICD_DESC AS NAME,ICD_CODE AS CODE,MAIN_FLG AS MAIN,ICD_KIND AS KIND,"	//duzhw update by 20131025  'Y' AS EXEC 标识用于诊断表数据操作
										+ " ICD_STATUS AS STATUS,IN_PAT_CONDITION,ADDITIONAL_CODE AS ADDITIONAL,ADDITIONAL_DESC,SEQ_NO,ICD_REMARK AS REMARK "
										// add by wukai on 2017/4/7 增加老的ICD_CODE 和 IO_TYPE,用于更新序号
										+ " , ICD_CODE AS OLD_ICD_CODE, IO_TYPE AS OLD_IO_TYPE "
										+ " FROM MRO_RECORD_DIAG "
										+ " WHERE CASE_NO='"
										+ this.CASE_NO
										+ "' ORDER BY IO_TYPE ASC,MAIN_FLG DESC,SEQ_NO")); // 诊断记录
		// int diagRows = 0;
		// // 门急诊诊断
		// if (result.getValue("OE_DIAG_CODE", 0).length() > 0) {
		// diagnosis.addData("TYPE", "I"); // 门急诊诊断
		// diagnosis.addData("MAIN", "Y"); // 主诊断
		// diagnosis.addData("NAME", result.getValue("OE_DIAG_CODE", 0));
		// diagnosis.addData("CODE", result.getValue("OE_DIAG_CODE", 0)); //
		// 门急诊诊断
		// diagnosis.addData("STATUS", "");
		// diagnosis.addData("REMARK", ""); // 备注
		// diagnosis.addData("ADDITIONAL", ""); // 附加码
		// diagnosis.addData("ADDITIONAL_DESC", ""); // 附加码替换中文
		// diagnosis.addData("IN_PAT_CONDITION", ""); // 入院病情
		// diagRows++;
		// }
		// // 门急诊诊断2
		// if (result.getValue("OE_DIAG_CODE2", 0).length() > 0) {
		// diagnosis.addData("TYPE", "I"); // 门急诊诊断
		// diagnosis.addData("MAIN", "N"); // 主诊断
		// diagnosis.addData("NAME", result.getValue("OE_DIAG_CODE2", 0));
		// diagnosis.addData("CODE", result.getValue("OE_DIAG_CODE2", 0)); //
		// 门急诊诊断
		// diagnosis.addData("STATUS", "");
		// diagnosis.addData("REMARK", ""); // 备注
		// diagnosis.addData("ADDITIONAL", ""); // 附加码
		// diagnosis.addData("ADDITIONAL_DESC", ""); // 附加码替换中文
		// diagnosis.addData("IN_PAT_CONDITION", ""); // 入院病情
		// diagRows++;
		// }
		// // 门急诊诊断3
		// if (result.getValue("OE_DIAG_CODE3", 0).length() > 0) {
		// diagnosis.addData("TYPE", "I"); // 门急诊诊断
		// diagnosis.addData("MAIN", "N"); // 主诊断
		// diagnosis.addData("NAME", result.getValue("OE_DIAG_CODE3", 0));
		// diagnosis.addData("CODE", result.getValue("OE_DIAG_CODE3", 0)); //
		// 门急诊诊断
		// diagnosis.addData("STATUS", "");
		// diagnosis.addData("REMARK", ""); // 备注
		// diagnosis.addData("ADDITIONAL", ""); // 附加码
		// diagnosis.addData("ADDITIONAL_DESC", ""); // 附加码替换中文
		// diagnosis.addData("IN_PAT_CONDITION", ""); // 入院病情
		// diagRows++;
		// }
		// // 入院诊断
		// if (result.getValue("IN_DIAG_CODE", 0).length() > 0) {
		// diagnosis.addData("TYPE", "M"); // 入院诊断
		// diagnosis.addData("MAIN", "Y"); // 主诊断
		// diagnosis.addData("NAME", result.getValue("IN_DIAG_CODE", 0));
		// diagnosis.addData("CODE", result.getValue("IN_DIAG_CODE", 0)); //
		// 入院诊断
		// diagnosis.addData("STATUS", "");
		// diagnosis.addData("REMARK", ""); // 备注
		// diagnosis.addData("ADDITIONAL", ""); // 附加码
		// diagnosis.addData("ADDITIONAL_DESC", ""); // 附加码替换中文
		// diagnosis.addData("IN_PAT_CONDITION", ""); // 入院病情
		// diagRows++;
		// }
		// // 入院诊断2
		// if (result.getValue("IN_DIAG_CODE2", 0).length() > 0) {
		// diagnosis.addData("TYPE", "M"); // 入院诊断
		// diagnosis.addData("MAIN", "N"); // 主诊断
		// diagnosis.addData("NAME", result.getValue("IN_DIAG_CODE2", 0));
		// diagnosis.addData("CODE", result.getValue("IN_DIAG_CODE2", 0)); //
		// 入院诊断
		// diagnosis.addData("STATUS", "");
		// diagnosis.addData("REMARK", ""); // 备注
		// diagnosis.addData("ADDITIONAL", ""); // 附加码
		// diagnosis.addData("ADDITIONAL_DESC", ""); // 附加码替换中文
		// diagnosis.addData("IN_PAT_CONDITION", ""); // 入院病情
		// diagRows++;
		// }
		// // 入院诊断3
		// if (result.getValue("IN_DIAG_CODE3", 0).length() > 0) {
		// diagnosis.addData("TYPE", "M"); // 入院诊断
		// diagnosis.addData("MAIN", "N");
		// diagnosis.addData("NAME", result.getValue("IN_DIAG_CODE3", 0));
		// diagnosis.addData("CODE", result.getValue("IN_DIAG_CODE3", 0)); //
		// 入院诊断
		// diagnosis.addData("STATUS", "");
		// diagnosis.addData("REMARK", ""); // 备注
		// diagnosis.addData("ADDITIONAL", ""); // 附加码
		// diagnosis.addData("ADDITIONAL_DESC", ""); // 附加码替换中文
		// diagnosis.addData("IN_PAT_CONDITION", ""); // 入院病情
		// diagRows++;
		// }
		// // 出院主诊断
		// if (result.getValue("OUT_DIAG_CODE1", 0).length() > 0) {
		// diagnosis.addData("TYPE", "O"); // 出院主诊断
		// diagnosis.addData("MAIN", "Y"); // 主诊断
		// diagnosis.addData("NAME", result.getValue("OUT_DIAG_CODE1", 0));
		// diagnosis.addData("CODE", result.getValue("OUT_DIAG_CODE1", 0)); //
		// 出院主诊断
		// diagnosis.addData("STATUS", result.getValue("CODE1_STATUS", 0));
		// diagnosis.addData("REMARK", result.getValue("CODE1_REMARK", 0)); //
		// 备注
		// diagnosis.addData("ADDITIONAL",
		// result.getValue("ADDITIONAL_CODE1", 0)); // 附加码
		// diagnosis.addData("ADDITIONAL_DESC",
		// result.getValue("ADDITIONAL_CODE1", 0)); // 附加码替换中文
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("OUT_DIAG_CONDITION1", 0)); // 入院病情
		// diagRows++;
		// }
		// if (result.getValue("OUT_DIAG_CODE2", 0).length() > 0) {// 出院第二诊断
		// diagnosis.addData("TYPE", "O");
		// diagnosis.addData("MAIN", "N");// 主诊断
		// diagnosis.addData("NAME", result.getValue("OUT_DIAG_CODE2", 0));
		// diagnosis.addData("CODE", result.getValue("OUT_DIAG_CODE2", 0));
		// diagnosis.addData("STATUS", result.getValue("CODE2_STATUS", 0));
		// diagnosis.addData("REMARK", result.getValue("CODE2_REMARK", 0));// 备注
		// diagnosis.addData("ADDITIONAL",
		// result.getValue("ADDITIONAL_CODE2", 0)); // 附加码
		// diagnosis.addData("ADDITIONAL_DESC",
		// result.getValue("ADDITIONAL_CODE2", 0)); // 附加码替换中文
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("OUT_DIAG_CONDITION2", 0)); // 入院病情
		// diagRows++;
		// }
		// if (result.getValue("OUT_DIAG_CODE3", 0).length() > 0) {// 出院第三诊断
		// diagnosis.addData("TYPE", "O");
		// diagnosis.addData("MAIN", "N");// 主诊断
		// diagnosis.addData("NAME", result.getValue("OUT_DIAG_CODE3", 0));
		// diagnosis.addData("CODE", result.getValue("OUT_DIAG_CODE3", 0));
		// diagnosis.addData("STATUS", result.getValue("CODE3_STATUS", 0));
		// diagnosis.addData("REMARK", result.getValue("CODE3_REMARK", 0));// 备注
		// diagnosis.addData("ADDITIONAL",
		// result.getValue("ADDITIONAL_CODE3", 0)); // 附加码
		// diagnosis.addData("ADDITIONAL_DESC",
		// result.getValue("ADDITIONAL_CODE3", 0)); // 附加码替换中文
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("OUT_DIAG_CONDITION3", 0)); // 入院病情
		// diagRows++;
		// }
		// if (result.getValue("OUT_DIAG_CODE4", 0).length() > 0) {// 出院第四诊断
		// diagnosis.addData("TYPE", "O");
		// diagnosis.addData("MAIN", "N");// 主诊断
		// diagnosis.addData("NAME", result.getValue("OUT_DIAG_CODE4", 0));
		// diagnosis.addData("CODE", result.getValue("OUT_DIAG_CODE4", 0));
		// diagnosis.addData("STATUS", result.getValue("CODE4_STATUS", 0));
		// diagnosis.addData("REMARK", result.getValue("CODE4_REMARK", 0));// 备注
		// diagnosis.addData("ADDITIONAL",
		// result.getValue("ADDITIONAL_CODE4", 0)); // 附加码
		// diagnosis.addData("ADDITIONAL_DESC",
		// result.getValue("ADDITIONAL_CODE4", 0)); // 附加码替换中文
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("OUT_DIAG_CONDITION4", 0)); // 入院病情
		// diagRows++;
		// }
		// if (result.getValue("OUT_DIAG_CODE5", 0).length() > 0) {// 出院第五诊断
		// diagnosis.addData("TYPE", "O");
		// diagnosis.addData("MAIN", "N");// 主诊断
		// diagnosis.addData("NAME", result.getValue("OUT_DIAG_CODE5", 0));
		// diagnosis.addData("CODE", result.getValue("OUT_DIAG_CODE5", 0));
		// diagnosis.addData("STATUS", result.getValue("CODE5_STATUS", 0));
		// diagnosis.addData("REMARK", result.getValue("CODE5_REMARK", 0));// 备注
		// diagnosis.addData("ADDITIONAL",
		// result.getValue("ADDITIONAL_CODE5", 0)); // 附加码
		// diagnosis.addData("ADDITIONAL_DESC",
		// result.getValue("ADDITIONAL_CODE5", 0)); // 附加码替换中文
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("OUT_DIAG_CONDITION5", 0)); // 入院病情
		// diagRows++;
		// }
		// if (result.getValue("OUT_DIAG_CODE6", 0).length() > 0) {// 出院第六诊断
		// diagnosis.addData("TYPE", "O");
		// diagnosis.addData("MAIN", "N");// 主诊断
		// diagnosis.addData("NAME", result.getValue("OUT_DIAG_CODE6", 0));
		// diagnosis.addData("CODE", result.getValue("OUT_DIAG_CODE6", 0));
		// diagnosis.addData("STATUS", result.getValue("CODE6_STATUS", 0));
		// diagnosis.addData("REMARK", result.getValue("CODE6_REMARK", 0));// 备注
		// diagnosis.addData("ADDITIONAL",
		// result.getValue("ADDITIONAL_CODE6", 0)); // 附加码
		// diagnosis.addData("ADDITIONAL_DESC",
		// result.getValue("ADDITIONAL_CODE6", 0)); // 附加码替换中文
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("OUT_DIAG_CONDITION6", 0)); // 入院病情
		// diagRows++;
		// }
		// if (result.getValue("INTE_DIAG_CODE", 0).length() > 0) {// 院内感染诊断
		// diagnosis.addData("TYPE", "Q"); // 院内感染诊断
		// diagnosis.addData("MAIN", "N"); // 主诊断
		// diagnosis.addData("NAME", result.getValue("INTE_DIAG_CODE", 0));
		// diagnosis.addData("CODE", result.getValue("INTE_DIAG_CODE", 0)); //
		// 院内感染诊断
		// diagnosis.addData("STATUS", result.getValue("INTE_DIAG_STATUS", 0));
		// diagnosis.addData("REMARK", ""); // 备注
		// diagnosis.addData("ADDITIONAL", ""); // 附加码
		// diagnosis.addData("ADDITIONAL_DESC", ""); // 附加码替换中文
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("INTE_DIAG_CONDITION", 0)); // 入院病情
		// diagRows++;
		// }
		// /*------------------------------------20120112add
		// start----------------------------------------------*/
		// if (result.getValue("COMPLICATION_DIAG", 0).length() > 0) {// 院内并发诊断
		// diagnosis.addData("TYPE", "W"); // 院内并发诊断
		// diagnosis.addData("MAIN", "N"); // 主诊断
		// diagnosis.addData("NAME", result.getValue("COMPLICATION_DIAG", 0));
		// diagnosis.addData("CODE", result.getValue("COMPLICATION_DIAG", 0));
		// // 院内并发诊断
		// diagnosis.addData("STATUS",
		// result.getValue("COMPLICATION_STATUS", 0));
		// diagnosis.addData("REMARK", ""); // 备注
		// diagnosis.addData("ADDITIONAL", ""); // 附加码
		// diagnosis.addData("ADDITIONAL_DESC", ""); // 附加码替换中文
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("COMPLICATION_DIAG_CONDITION", 0)); // 入院病情
		// diagRows++;
		// }
		// // System.out.println("-------------------------" + diagnosis);
		// diagnosis.setCount(diagRows);
		DiagGrid.removeRowAll();
		DiagGrid.setParmValue(diagnosis);
		DiagGrid.addRow();// 新建空行 用以增加诊断
		this.setValue("DIAGCHK_FLG", result.getValue("DIAGCHK_FLG", 0));// 医师提交标识
		DIAGCHK_FLG = result.getValue("DIAGCHK_FLG", 0);
		// 第二小页签
		this.setValue("TP22_PATHOLOGY_DIAG", result.getValue("PATHOLOGY_DIAG",
				0)); // 病理诊断1
		this.setValue("TP2_PATHOLOGY_NO", result.getValue("PATHOLOGY_NO", 0)); // 病理号1
		this.setValue("TP22_PATHOLOGY_DIAG2", result.getValue(
				"PATHOLOGY_DIAG2", 0)); // 病理诊断2
		this.setValue("TP2_PATHOLOGY_NO2", result.getValue("PATHOLOGY_NO2", 0)); // 病理号2
		this.setValue("TP22_PATHOLOGY_DIAG3", result.getValue(
				"PATHOLOGY_DIAG3", 0)); // 病理诊断2
		this.setValue("TP2_PATHOLOGY_NO3", result.getValue("PATHOLOGY_NO3", 0)); // 病理号2
		OrderList order = new OrderList();
		// 替换病理诊断中文
		this.setValue("PATHOLOGY_DIAG_DESC", order.getTableShowValue(result
				.getValue("PATHOLOGY_DIAG", 0)));
		this.setValue("PATHOLOGY_DIAG_DESC2", order.getTableShowValue(result
				.getValue("PATHOLOGY_DIAG2", 0)));
		this.setValue("PATHOLOGY_DIAG_DESC3", order.getTableShowValue(result
				.getValue("PATHOLOGY_DIAG3", 0)));

		this.setValue("MDIAG_BASIS", result.getValue("MDIAG_BASIS", 0)); // 最高诊断依据
		this.setValue("DIF_DEGREE", result.getValue("DIF_DEGREE", 0)); // 分化程度

		this.setValue("TP22_EX_RSN", result.getValue("EX_RSN", 0)); // 外部因素1
		this.setValue("TP22_EX_RSN2", result.getValue("EX_RSN2", 0)); // 外部因素2
		this.setValue("TP22_EX_RSN3", result.getValue("EX_RSN3", 0)); // 外部因素3
		// 替换损伤中毒中文
		this.setValue("EX_RSN_DESC", order.getTableShowValue(result.getValue(
				"EX_RSN", 0)));
		this.setValue("EX_RSN_DESC2", order.getTableShowValue(result.getValue(
				"EX_RSN2", 0)));
		this.setValue("EX_RSN_DESC3", order.getTableShowValue(result.getValue(
				"EX_RSN3", 0)));

		this.setValue("TP22_ALLEGIC", result.getValue("ALLEGIC", 0)); // 药物过敏
		// 是否有药物过敏 shibl 20120621 add
		if (result.getValue("ALLEGIC_FLG", 0).equals("1"))
			getRDBtn("ALLEGIC_1").setSelected(true);
		else if (result.getValue("ALLEGIC_FLG", 0).equals("2"))
			getRDBtn("ALLEGIC_2").setSelected(true);

		// HBsAg
		if (result.getValue("HBSAG", 0).equals("0"))
			getRDBtn("HBsAg_1").setSelected(true);
		else if (result.getValue("HBSAG", 0).equals("1"))
			getRDBtn("HBsAg_2").setSelected(true);
		else if (result.getValue("HBSAG", 0).equals("2"))
			getRDBtn("HBsAg_3").setSelected(true);
		// HCV-Ab
		if (result.getValue("HCV_AB", 0).equals("0"))
			getRDBtn("HCV-Ab_1").setSelected(true);
		else if (result.getValue("HCV_AB", 0).equals("1"))
			getRDBtn("HCV-Ab_2").setSelected(true);
		else if (result.getValue("HCV_AB", 0).equals("2"))
			getRDBtn("HCV-Ab_3").setSelected(true);
		// HIV-Ab
		if (result.getValue("HIV_AB", 0).equals("0"))
			getRDBtn("HIV-Ab_1").setSelected(true);
		else if (result.getValue("HIV_AB", 0).equals("1"))
			getRDBtn("HIV-Ab_2").setSelected(true);
		else if (result.getValue("HIV_AB", 0).equals("2"))
			getRDBtn("HIV-Ab_3").setSelected(true);
		// 门诊与住院
		if (result.getValue("QUYCHK_OI", 0).equals("0"))
			getRDBtn("TP22_myc1").setSelected(true);
		else if (result.getValue("QUYCHK_OI", 0).equals("1"))
			getRDBtn("TP22_myc2").setSelected(true);
		else if (result.getValue("QUYCHK_OI", 0).equals("2"))
			getRDBtn("TP22_myc3").setSelected(true);
		else if (result.getValue("QUYCHK_OI", 0).equals("3"))
			getRDBtn("TP22_myc4").setSelected(true);
		// 入院与出院
		if (result.getValue("QUYCHK_INOUT", 0).equals("0"))
			getRDBtn("TP22_ryc1").setSelected(true);
		else if (result.getValue("QUYCHK_INOUT", 0).equals("1"))
			getRDBtn("TP22_ryc2").setSelected(true);
		else if (result.getValue("QUYCHK_INOUT", 0).equals("2"))
			getRDBtn("TP22_ryc3").setSelected(true);
		else if (result.getValue("QUYCHK_INOUT", 0).equals("3"))
			getRDBtn("TP22_ryc4").setSelected(true);
		// 术前与术后
		if (result.getValue("QUYCHK_OPBFAF", 0).equals("0"))
			getRDBtn("TP22_sys1").setSelected(true);
		else if (result.getValue("QUYCHK_OPBFAF", 0).equals("1"))
			getRDBtn("TP22_sys2").setSelected(true);
		else if (result.getValue("QUYCHK_OPBFAF", 0).equals("2"))
			getRDBtn("TP22_sys3").setSelected(true);
		else if (result.getValue("QUYCHK_OPBFAF", 0).equals("3"))
			getRDBtn("TP22_sys4").setSelected(true);
		// 临床与病理
		if (result.getValue("QUYCHK_CLPA", 0).equals("0"))
			getRDBtn("TP22_lyb1").setSelected(true);
		else if (result.getValue("QUYCHK_CLPA", 0).equals("1"))
			getRDBtn("TP22_lyb2").setSelected(true);
		else if (result.getValue("QUYCHK_CLPA", 0).equals("2"))
			getRDBtn("TP22_lyb3").setSelected(true);
		else if (result.getValue("QUYCHK_CLPA", 0).equals("3"))
			getRDBtn("TP22_lyb4").setSelected(true);
		// 放射与病理
		if (result.getValue("QUYCHK_RAPA", 0).equals("0"))
			getRDBtn("TP22_fyb1").setSelected(true);
		else if (result.getValue("QUYCHK_RAPA", 0).equals("1"))
			getRDBtn("TP22_fyb2").setSelected(true);
		else if (result.getValue("QUYCHK_RAPA", 0).equals("2"))
			getRDBtn("TP22_fyb3").setSelected(true);
		else if (result.getValue("QUYCHK_RAPA", 0).equals("3"))
			getRDBtn("TP22_fyb4").setSelected(true);
		// 抢救
		this.setValue("TP22_GET_TIMES", result.getValue("GET_TIMES", 0));
		this
				.setValue("TP22_SUCCESS_TIMES", result.getValue(
						"SUCCESS_TIMES", 0)); // 抢救成功次数
		this.setValue("TP22_DIRECTOR_DR_CODE", result.getValue(
				"DIRECTOR_DR_CODE", 0)); // 科主任
		this.setValue("TP22_PROF_DR_CODE", result.getValue("PROF_DR_CODE", 0)); // 主(副主)任医师
		this.setValue("TP22_ATTEND_DR_CODE", result.getValue("ATTEND_DR_CODE",
				0)); // 主治医师
		this.setValue("TP2_VS_DR_CODE", result.getValue("VS_DR_CODE", 0)); // 住院医师
		this.setValue("TP22_INDUCATION_DR_CODE", result.getValue(
				"INDUCATION_DR_CODE", 0)); // 进修医师
		this.setValue("TP22_INTERN_DR_CODE", result.getValue("INTERN_DR_CODE",
				0)); // 实习医师
		this.setValue("TP22_GRADUATE_INTERN_CODE", result.getValue("GRADUATE_INTERN_CODE",
				0)); // 研究生医师  add duzhw 20131204
		// 第三小页签
		// 手术
		
		// this.setValue("DAY_OPE_FLG", result.getValue("DAY_OPE_FLG"));//日间手术 add by huangtt 20161222
		// this.messageBox(""+result);
		// this.messageBox("是否赋值:"+ result.getValue("DAY_OPE_FLG",0));
		
		if(result.getValue("DAY_OPE_FLG", 0).equals("Y")) {			// 日间手术勾选赋值   2017/3/29   BY  yanmm
	    		getCheckbox("DAY_OPE_FLG").setSelected(true);
	        }
		
//		this.OPTableBind(MR_NO, CASE_NO);
		if (!result.getValue("OPE_TYPE_CODE", 0).equals("0")) {
			String opeStr = result.getValue("OPE_TYPE_CODE", 0);
			String[] line = opeStr.split(",");
			if (line.length > 1) {
				for (int i = 0; i < line.length; i++) {
					if (line[i].equals("1"))
						this.getCheckbox("OPE_TYPE_CODE1").setSelected(true);
					if (line[i].equals("2"))
						this.getCheckbox("OPE_TYPE_CODE2").setSelected(true);
				}
			} else {
				if (opeStr.equals("1"))
					this.getCheckbox("OPE_TYPE_CODE1").setSelected(true);
				else if (opeStr.equals("2"))
					this.getCheckbox("OPE_TYPE_CODE2").setSelected(true);
			}
		} else {
			getCheckbox("OPE_TYPE_CODE1").setSelected(false);// 急做手术
			getCheckbox("OPE_TYPE_CODE2").setSelected(false);// 择期手术
		}
		// 血型
		if (result.getValue("BLOOD_TYPE", 0).equals("1"))
			getRDBtn("TP23XX1").setSelected(true);
		else if (result.getValue("BLOOD_TYPE", 0).equals("2"))
			getRDBtn("TP23XX2").setSelected(true);
		else if (result.getValue("BLOOD_TYPE", 0).equals("3"))
			getRDBtn("TP23XX3").setSelected(true);
		else if (result.getValue("BLOOD_TYPE", 0).equals("4"))
			getRDBtn("TP23XX4").setSelected(true);
		else if (result.getValue("BLOOD_TYPE", 0).equals("5"))// 不详
			getRDBtn("TP23XX5").setSelected(true);
		else if (result.getValue("BLOOD_TYPE", 0).equals("6"))// 未做
			getRDBtn("TP23XX6").setSelected(true);
		// 输血反映
		if (result.getValue("TRANS_REACTION", 0).equals("1"))// 有
			getRDBtn("PT23SX1").setSelected(true);
		else if (result.getValue("TRANS_REACTION", 0).equals("2"))// 无
			getRDBtn("PT23SX2").setSelected(true);
		else if (result.getValue("TRANS_REACTION", 0).equals("0"))// 未做
			getRDBtn("PT23SX3").setSelected(true);
		// RH
		if (result.getValue("RH_TYPE", 0).equals("1"))
			getRDBtn("PT23RH1").setSelected(true);
		else if (result.getValue("RH_TYPE", 0).equals("2"))
			getRDBtn("PT23RH2").setSelected(true);
		else if (result.getValue("RH_TYPE", 0).equals("3"))
			getRDBtn("PT23RH3").setSelected(true); // 不祥
		else if (result.getValue("RH_TYPE", 0).equals("4"))
			getRDBtn("PT23RH4").setSelected(true); // 未作
		// 红细胞
		this.setValue("TP23_RBC", result.getValue("RBC", 0));
		this.setValue("TP23_PLATE", result.getValue("PLATE", 0)); // 红小板
		this.setValue("TP23_PLASMA", result.getValue("PLASMA", 0)); // 血浆
		this.setValue("TP23_WHOLE_BLOOD", result.getValue("WHOLE_BLOOD", 0)); // 全血
		this.setValue("TP23_BANKED_BLOOD", result.getValue("BANKED_BLOOD", 0)); // 全体回收
		this.setValue("TP23_OTH_BLOOD", result.getValue("OTH_BLOOD", 0)); // 其他
		// 第四小页签

		// 医院感染总次数
		this.setValue("INFECT_COUNT", result.getValue("INFECT_COUNT", 0));
		// 尸检
		this.setValue("BODY_CHECK", result.getValue("BODY_CHECK", 0));// wanglong modify 20141030
		// 本院第一例
		if (result.getValue("FIRST_CASE", 0).equals("1"))
			getRDBtn("PT24BY1").setSelected(true);
		else if (result.getValue("FIRST_CASE", 0).equals("2"))
			getRDBtn("PT24BY2").setSelected(true);
		// 随诊 如果随诊年、月、周数都为空活着为零，那么不需要随诊
		if (result.getValue("ACCOMP_DATE", 0).length() <= 0)
			(getRDBtn("PT24SZ2")).setSelected(true);
		else
			(getRDBtn("PT24SZ1")).setSelected(true);
		// 随诊年
		this.setValue("TP24_ACCOMPANY_YEAR", result.getValue("ACCOMPANY_YEAR",
				0));
		// 随诊月
		this.setValue("TP24_ACCOMPANY_MONTH", result.getValue(
				"ACCOMPANY_MONTH", 0));
		// 随诊周
		this.setValue("TP24_ACCOMPANY_WEEK", result.getValue("ACCOMPANY_WEEK",
				0));
		// 随诊日期
		this
				.setValue("TP24_ACCOMP_DATE", result.getTimestamp(
						"ACCOMP_DATE", 0));
		// 示案病例
		if (result.getValue("SAMPLE_FLG", 0).equals("1"))
			getRDBtn("PT24SJBL1").setSelected(true);
		else if (result.getValue("SAMPLE_FLG", 0).equals("2"))
			getRDBtn("PT24SJBL2").setSelected(true);
		// 临床路径与临床路径病重暂无 须看2.0代码
		/*
		 * 病案首页中，加字段临床试验病例字典、 教学病例字典同时，加上查询功能；病历维护加两个字段 TEST_EMR,TEACH_EMR
		 * ZhenQin - 2011-05-09
		 */
		this.setValue("CLNCPATH_CODE", result.getValue("CLNCPATH_CODE", 0));
		this.setValue("DISEASES_CODE", result.getValue("DISEASES_CODE", 0));
		this.setValue("TEACH_EMR", result.getValue("TEACH_EMR", 0));
		//&&==================modify by wukai 20160531  从ADM_INP中抓取临床项目信息 start
		String sql = "SELECT PATLOGY_PRO_CODE,PATLOGY_DOC_CODE,PATLOGY_DEPT_CODE FROM ADM_INP "+
					 "WHERE MR_NO=" + result.getData("MR_NO", 0) + " AND CASE_NO=" + result.getData("CASE_NO", 0);
		//System.out.println("******sql : "+sql);
		Map map = TJDODBTool.getInstance().select(sql);
		System.out.println("******map : "+map.toString());
		if(map != null && map.size() > 0) {
			Map mm = (Map) map.get("Data");
			if(mm != null) {
				List l  = (List) mm.get("PATLOGY_PRO_CODE");
				if(l != null) {
					this.setValue("TEST_EMR", l.get(0));
				}
			}
		}
		//&&==================modify by wukai 20160531  从ADM_INP中抓取临床项目信息 end
		if(result.getValue("TEST_EMR",0) != null && String.valueOf(result.getValue("TEST_EMR",0)).length() > 0) {
			this.setValue("TEST_EMR", result.getValue("TEST_EMR",0));
		}
		//第五小页签  modify by zhangh 2013-10-11
		this.setValue("VENTI_TIME", result.getValue("VENTI_TIME", 0));
		
		// add by wangqing 20180116 start 新增呼吸机使用分钟、呼吸机使用秒
		this.setValue("VENTI_TIME_MIN", result.getValue("VENTI_TIME_MIN", 0));
		this.setValue("VENTI_TIME_SEC", result.getValue("VENTI_TIME_SEC", 0));
		// add by wangqing 20180116 end
		
		TParm icuParm = new TParm();
		for (int i = 1; i <= 5; i++) {
			icuParm.setData("ICU_ROOM", i, result.getData("ICU_ROOM" + i, 0));
			icuParm.setData("IN_DATE", i, result.getData("ICU_IN_DATE" + i, 0));
			icuParm.setData("OUT_DATE", i, result.getData("ICU_OUT_DATE" + i, 0));
		}
		TTable icuTable = (TTable) this.getComponent("TP25_ICU_Table");
		icuTable.setParmValue(icuParm);
		
		/*-------------------第二叶签 end----------------------------*/
		// 设置控件是否可以编辑 如果不是 “医师”和“病案室”调用 设置第二页签不可编辑
		if (!"2".equals(UserType) && !"4".equals(UserType)
				|| "Y".equals(DIAGCHK_FLG)) {
			this.setEnabledPage2(false);
		}
		if ("2".equals(UserType) && "Y".equals(DIAGCHK_FLG)) {
			callFunction("UI|save|setEnabled", false);
		}
	}

	/**
	 * 第三页签赋值
	 * 
	 * @param result
	 *            TParm
	 */
	private void setValueTP3(TParm result) {
		/*-------------------第三叶签 start----------------------------*/
		// 财务记录 需要将列转为行显示
		TParm cw = new TParm();
		double sum = 0; // 计算总和
		double num;
		String seq = "";
		String c_name; // 列明
		String c_name1;
		// String chn_name="";//中文名称
		DecimalFormat df = new DecimalFormat("0.00");
		Map MrofeeCode = MRORecordTool.getInstance().getMROChargeName();
		for (int i = 1; i <= 30; i++) {
			c_name = "CHARGE_";
			c_name1 = "CHARGE";
			if (i < 10)// I小于10 补零
				seq = "0" + i;
			else
				seq = "" + i;
			c_name += seq;
			c_name1 += seq;
			if (result.getValue(c_name, 0).trim() == null
					|| result.getValue(c_name, 0).trim().equals(""))
				num = 0;
			else
				num = result.getDouble(c_name, 0);
			if (map.get(MrofeeCode.get(c_name1)) != null) {
				cw.setData("NO", i, i);
				cw.setData("Name", i, map.get(MrofeeCode.get(c_name1)) + "");
				cw.setData("Amount", i, df.format(num));
				sum += Double.valueOf(num);
			}
		}
		this.setValue("TP3_SUM", df.format(sum)); // 总金额
		((TTable) this.getComponent("TP3_Table1")).setParmValue(cw);
		this.setValue("BILCHK_FLG", result.getValue("BILCHK_FLG", 0));// 账务提交标识
		BILCHK_FLG = result.getValue("BILCHK_FLG", 0);
		/*-------------------第三叶签 end----------------------------*/
	}

	/**
	 * 第四页签赋值
	 * 
	 * @param result
	 *            TParm
	 */
	private void setValueTP4(TParm result) {
		/*-------------------第四叶签 start----------------------------*/
		// 传染病报告
		if (result.getValue("INFECT_REPORT", 0).equals("1"))
			getRDBtn("TP4CR1").setSelected(true);
		else if (result.getValue("INFECT_REPORT", 0).equals("2"))
			getRDBtn("TP4CR2").setSelected(true);
		// 四病报告 在新表中是 "疾病报告" 字段
		if (result.getValue("DIS_REPORT", 0).equals("1"))
			getRDBtn("TP4SB1").setSelected(true);
		else if (result.getValue("DIS_REPORT", 0).equals("2"))
			getRDBtn("TP4SB2").setSelected(true);
		// 病案质量
		if (result.getValue("QUALITY", 0).equals("1"))
			getRDBtn("TP4BA1").setSelected(true);
		else if (result.getValue("QUALITY", 0).equals("2"))
			getRDBtn("TP4BA2").setSelected(true);
		else if (result.getValue("QUALITY", 0).equals("3"))
			getRDBtn("TP4BA3").setSelected(true);
		// 质控医师 暂时不知道应该使用哪个列表框
		this.setValue("TP4_CTRL_DR", result.getValue("CTRL_DR", 0));
		// 质控护士 暂时不知道应该使用哪个列表框
		this.setValue("TP4_CTRL_NURSE", result.getValue("CTRL_NURSE", 0));
		// 日期
		this.setValue("TP4_CTRL_DATE", result.getTimestamp("CTRL_DATE", 0));
		// 编码员 暂时不知道应该使用哪个列表框
		this.setValue("TP4_ENCODER", result.getValue("ENCODER", 0));
		this.setValue("QTYCHK_FLG", result.getValue("QTYCHK_FLG", 0));// 病案室(质控)提交标识
		QTYCHK_FLG = result.getValue("QTYCHK_FLG", 0);
		/*-------------------第四叶签 end----------------------------*/
		// 设置控件是否可以编辑 如果不是 “医师”和“病案室”调用 设置第二页签不可编辑
		if (!"4".equals(UserType) || QTYCHK_FLG.equals("Y")) {
			this.setEnabledPage4(false);
		}
	}

	/**
	 * 绑定病患动态Grid
	 * 
	 * @param parm
	 *            TParm
	 */
	private void setValueTran(TParm parm) {
		SimpleDateFormat dt = new SimpleDateFormat("yyyy/MM/dd");
		for (int i = 0; i < parm.getCount(); i++) {
			parm.setData("IN_DATE", i, dt.format(StringTool.getTimestamp(parm
					.getValue("IN_DATE", i), "yyyyMMdd")));
			if (!parm.getValue("OUT_DATE", i).equals(""))
				parm.setData("OUT_DATE", i, parm.getValue("OUT_DATE", i)
						.substring(0, 19).replaceAll("-", "/"));
		}
		parm.setCount(parm.getCount());
		TTable table = (TTable) this.getComponent("TP21_Table1");
		table.setParmValue(parm);
	}

	/**
	 * 清空第一页签
	 */
	private void clearTP1() {
		this
				.clearValue("TP1_MR_NO;TP1_IPD_NO;PT1_PAT_NAME;TP1_IDNO;TP1_SEX;TP1_BIRTH_DATE;TP1_CASE_NO;TP1_MARRIGE;TP1_AGE;TP1_NATION");
		this
				.clearValue("TP1_FOLK;TP1_PAYTYPE;TP1_INNUM;TP1_TEL;TP1_PROVICE;TP1_COUNTRY;TP1_H_ADDRESS;TP1_H_POSTNO;");
		// modified by WangQing 20170401 
		// delete TP1_RELATIONSHIP
		//				this
		//				.clearValue("TP1_OCCUPATION;TP1_OFFICE;TP1_O_TEL;TP1_O_ADDRESS;TP1_O_POSTNO;TP1_CONTACTER;TP1_RELATIONSHIP;TP1_CONT_TEL;TP1_CONT_ADDRESS");
		this
		.clearValue("TP1_OCCUPATION;TP1_OFFICE;TP1_O_TEL;TP1_O_ADDRESS;TP1_O_POSTNO;TP1_CONTACTER;TP1_CONT_TEL;TP1_CONT_ADDRESS");
		// add
		this
				.clearValue("TP1_ADDRESS;TP1_POST_NO;TP1_POST_R;TP1_POST_C;TP1_O_POST_R;TP1_O_POST_C;MRO_CTZ;BIRTHPLACE_DESC;BIRTHPLACE");
	}

	/**
	 * 清空第二页签
	 */
	private void clearTP2() {
		this
				.clearValue("TP21_Table1;TP21_Table2;TP21_IsSubmit;TP2_REAL_STAY_DAYS;TP2_OUT_DEPT;TP2_IN_CONDITION;TP2_OUT_DATE;TP2_CONFIRM_DATE");
		this
				.clearValue("TP22_PATHOLOGY_DIAG;TP22_EX_RSN;TP22_ALLEGIC;ALLEGIC_1;ALLEGIC_2;HBsAg_1;HBsAg_2;HBsAg_3;HCV-Ab_1;HCV-Ab_2;HCV-Ab_3;HIV-Ab_1;HIV-Ab_2;HIV-Ab_3");
		this
				.clearValue("TP22_myc1;TP22_myc2;TP22_myc3;TP22_myc4;TP22_sys1;TP22_sys2;TP22_sys3;TP22_sys4;TP22_fyb1;TP22_fyb2;TP22_fyb3;TP22_fyb4;TP22_ryc1;TP22_ryc2;TP22_ryc3;TP22_ryc4;TP22_lyb1;TP22_lyb2;TP22_lyb3;TP22_lyb4");
		this
				.clearValue("TP22_GET_TIMES;TP22_SUCCESS_TIMES;TP22_DIRECTOR_DR_CODE;TP2_VS_DR_CODE;TP22_INTERN_DR_CODE;TP22_PROF_DR_CODE;TP22_INDUCATION_DR_CODE;TP22_ATTEND_DR_CODE;TP22_GRADUATE_INTERN_CODE");
		this
				.clearValue("PT23_Table1;TP23XX1;TP23XX2;TP23XX3;TP23XX4;TP23XX5;TP23XX6;PT23SX1;PT23SX2;PT23RH1;PT23RH2;PT23RH3;TP23_RBC;TP23_PLATE;TP23_PLASMA;TP23_WHOLE_BLOOD;TP23_OTH_BLOOD");
		this
				.clearValue("BODY_CHECK;PT24BY1;PT24BY2;PT24SZ1;PT24SZ2;TP24_ACCOMP_DATE;PT24SJBL1;PT24SJBL2;");
		this.clearValue("PATHOLOGY_DIAG_DESC;EX_RSN_DESC");
		// add
		this
				.clearValue("TP2_ADM_SOURCE;TP2_IN_DATE;TP2_IN_DEPT;TP2_OUT_TYPE;TP2_TRAN_HOSP;TP2_BE_IN_D;TP2_BE_IN_H;TP2_BE_IN_M;TP2_AF_IN_D;TP2_AF_IN_H;TP2_AF_IN_M;TP2_VS_NURSE_CODE;TP2_AGN_PLAN_INTENTION;TP2_AGN_PLAN_FLG;TP2_PATHOLOGY_NO;TRAN_HOSP_OTHER");
		//zhangh 2013-10-11
		this.clearValue("TP25_ICU_Table;VENTI_TIME;DAY_OPE_FLG");
		
		// add by wangqing 20180116 start 新增呼吸机使用分钟、呼吸机使用秒
		this.clearValue("VENTI_TIME_MIN;VENTI_TIME_SEC;TP2_VENTI_TIME_MIN;TP2_VENTI_TIME_SEC");
		
		// modoified by WangQing 20170412
		this.removeOP();
	}

	/**
	 * 清空第三页签
	 */
	private void clearTP3() {
		this.clearValue("TP3_SUM;TP3_IsSubmit;TP3_Table1");
	}

	/**
	 * 清空第四页签
	 */
	private void clearTP4() {
		this
				.clearValue("TP4CR1;TP4CR2;TP4SB1;TP4SB2;TP4_IsSubmit;TP4BA1;TP4BA2;TP4BA3;TP4_CTRL_DR;TP4_CTRL_NURSE;TP4_CTRL_DATE;TP4_ENCODER");
	}

	/**
	 * 获取第一页签的内容
	 * 
	 * @return TParm
	 */
	public TParm getFisrtPageInfo() {
		TParm parm = new TParm();
		parm.setData("PAT_NAME", this.getValue("PT1_PAT_NAME")); // 姓名
		parm.setData("IDNO", this.getValue("TP1_IDNO")); // 身份证号
		parm.setData("SEX", this.getValue("TP1_SEX")); // 性别
		if (this.getValue("TP1_BIRTH_DATE") == null)
			parm.setData("BIRTH_DATE", new TNull(Timestamp.class)); // 生日
		else
			parm.setData("BIRTH_DATE", this.getValue("TP1_BIRTH_DATE")); // 生日
		parm.setData("AGE", this.getValue("TP1_AGE")); // 年龄
		parm.setData("MARRIGE", this.getValue("TP1_MARRIGE")); // 婚姻
		parm.setData("NATION", this.getValue("TP1_NATION")); // 国籍
		parm.setData("IN_COUNT", this.getValue("TP1_INNUM")); // 住院次数
		parm.setData("FOLK", this.getValue("TP1_FOLK")); // 民族
		parm.setData("CTZ1_CODE", this.getValue("TP1_PAYTYPE")); // 付款方式
		parm.setData("HOMEPLACE_CODE", this.getValueString("HOMEPLACE_CODE"));// 出生地代码
		parm.setData("H_ADDRESS", this.getValue("TP1_H_ADDRESS")); // 户籍地址
		parm.setData("H_POSTNO", this.getValue("TP1_H_POSTNO")); // 户籍邮编
		
		
		// modified by WangQing 20170411 -start
		parm.setData("OCCUPATION", this.getValue("TP1_OCCUPATION")); // 职业
//		this.messageBox("occCode: "+this.getValueString("TP1_OCCUPATION"));
		String sql001 = "SELECT count(1) AS COUNT FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_OCCUPATION' AND ID ='" 
				+ this.getValueString("TP1_OCCUPATION") + "' ";
		TParm result001 = new TParm(TJDODBTool.getInstance().select(sql001));
		int count001 = Integer.parseInt(result001.getValue("COUNT", 0));
		if(count001 == 0){
			parm.setData("OCCUPATION", oldOccCode);
		}else{
			parm.setData("OCCUPATION", this.getValue("TP1_OCCUPATION")); // 职业
		}
		// modified by WangQing 20170411 -end
		
		
		
	
		parm.setData("OFFICE", this.getValue("TP1_OFFICE")); // 单位
		parm.setData("O_TEL", this.getValue("TP1_O_TEL")); // 单位电话
		parm.setData("O_ADDRESS", this.getValue("TP1_O_ADDRESS")); // 单位地址
		parm.setData("O_POSTNO", this.getValue("TP1_O_POSTNO")); // 单位邮编
		parm.setData("CONTACTER", this.getValue("TP1_CONTACTER")); // 联系人姓名
		
		
		// modified by WangQing 20170411 -start
		parm.setData("RELATIONSHIP", this.getValue("TP1_RELATIONSHIP")); // 联系人关系
//		this.messageBox("===guanxi: "+this.getValue("TP1_RELATIONSHIP"));
		String sql002 = "SELECT COUNT(1) AS COUNT FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELATIONSHIP' AND ID ='" 
				+ this.getValueString("TP1_RELATIONSHIP") + "' ";
		TParm result002 = new TParm(TJDODBTool.getInstance().select(sql002));
		int count002 = Integer.parseInt(result002.getValue("COUNT", 0));
		if(count002 == 0){
			parm.setData("RELATIONSHIP", oldRelationCode); // 联系人关系
		}else{
			parm.setData("RELATIONSHIP", this.getValue("TP1_RELATIONSHIP")); // 联系人关系
		}
		// modified by WangQing 20170411 -end
		
		
		
		parm.setData("CONT_TEL", this.getValue("TP1_CONT_TEL")); // 联系人电话
		parm.setData("CONT_ADDRESS", this.getValue("TP1_CONT_ADDRESS")); // 联系人地址
		parm.setData("MR_NO", this.getValue("TP1_MR_NO"));
		parm.setData("CASE_NO", this.getValue("TP1_CASE_NO"));
		parm.setData("IPD_NO", this.getValue("TP1_IPD_NO"));
		/*-------------------------------------------------------------------*/
		parm.setData("MRO_CTZ", this.getValue("MRO_CTZ"));// 病案首页身份
		parm.setData("BIRTHPLACE", this.getValue("BIRTHPLACE"));// 籍贯
		parm.setData("ADDRESS", this.getValue("TP1_ADDRESS"));// 通信地址
		parm.setData("POST_NO", this.getValue("TP1_POST_NO"));// 通信邮编
		parm.setData("TEL", this.getValue("TP1_TEL")); // 电话
		parm.setData("NHI_NO", this.getValue("TP1_NHI_NO")); // 健康卡号 add by
		// wanglong
		// 2012-11-27
		parm.setData("NHI_CARDNO", this.getValue("TP1_NHI_CARDNO")); // 医保卡号 add
		// by
		// wanglong
		// 2012-11-27
		/*--------------------------------------------------------------------*/
		if (((TCheckBox) this.getComponent("ADMCHK_FLG")).isSelected())// 住院提交标识
			parm.setData("ADMCHK_FLG", "Y");
		else
			parm.setData("ADMCHK_FLG", "N");
		return parm;
	}

	/**
	 * 获取第二页签的信息
	 * 
	 * @return TParm
	 */
	public TParm getSecendPageInfo() {
		TParm result = new TParm();
		result.setData("TRANS_DEPT", this.getTranDept()); // 转科科室
		/****************************** ADD START ************************************************/


		result.setData("ADM_SOURCE", this.getValue("TP2_ADM_SOURCE")); // 住院病人来源
		if (this.getValue("TP2_IN_DATE") == null)
			result.setData("IN_DATE", new TNull(Timestamp.class));
		else
			result.setData("IN_DATE", this.getValue("TP2_IN_DATE")); // 住院日期
		// shibl 20121130 add
		result.setData("SPENURS_DAYS", this.getValue("TP2_SPENURS_DAYS")); // 特级护理天数
		result.setData("FIRNURS_DAYS", this.getValue("TP2_FIRNURS_DAYS")); // 一级护理天数
		result.setData("SECNURS_DAYS", this.getValue("TP2_SECNURS_DAYS")); // 二级护理天数
		result.setData("THRNURS_DAYS", this.getValue("TP2_THRNURS_DAYS")); // 三级护理天数
//		result.setData("VENTI_TIME", this.getValue("TP2_VENTI_TIME")); // 呼吸机使用时间

//		result.setData("ICU_ROOM1", new TNull(String.class)); // 重症监护室1名称
//		result.setData("ICU_IN_DATE1", new TNull(Timestamp.class));// 重症监护室1进时间
//		result.setData("ICU_OUT_DATE1", new TNull(Timestamp.class));// 重症监护室1出时间
//
//		result.setData("ICU_ROOM2", new TNull(String.class));// 重症监护室2名称
//		result.setData("ICU_IN_DATE2", new TNull(Timestamp.class));// 重症监护室2进时间
//		result.setData("ICU_OUT_DATE2", new TNull(Timestamp.class));// 重症监护室2出时间
//
//		result.setData("ICU_ROOM3", new TNull(String.class));// 重症监护室3名称
//		result.setData("ICU_IN_DATE3", new TNull(Timestamp.class));// 重症监护室3进时间
//		result.setData("ICU_OUT_DATE3", new TNull(Timestamp.class));// 重症监护室3出时间
//
//		result.setData("ICU_ROOM4", new TNull(String.class));// 重症监护室4名称
//		result.setData("ICU_IN_DATE4", new TNull(Timestamp.class));// 重症监护室4进时间
//		result.setData("ICU_OUT_DATE4", new TNull(Timestamp.class));// 重症监护室4出时间
//
//		result.setData("ICU_ROOM5", new TNull(String.class));// 重症监护室5名称
//		result.setData("ICU_IN_DATE5", new TNull(Timestamp.class));// 重症监护室5进时间
//		result.setData("ICU_OUT_DATE5", new TNull(Timestamp.class));// 重症监护室5出时间
//		// 取得监护数据
//		TParm parm = MROTool.getInstance().getICUParm(this.CASE_NO);
//		int count = 1;
//		for (int i = 0; i < parm.getCount(); i++) {
//			result.setData("ICU_ROOM" + count, parm.getValue("DEPT_CODE", i));
//			result.setData("ICU_IN_DATE" + count, parm.getTimestamp("IN_DATE",
//					i) == null ? new TNull(Timestamp.class) : parm
//					.getTimestamp("IN_DATE", i));
//			result.setData("ICU_OUT_DATE" + count, parm.getTimestamp(
//					"OUT_DATE", i) == null ? new TNull(Timestamp.class) : parm
//					.getTimestamp("OUT_DATE", i));
//			count++;
//		}
		result.setData("NB_ADM_WEIGHT",this.getValue("TP2_NB_ADM_WEIGHT"));//新生儿入院体重
		result.setData("NB_WEIGHT",this.getValue("TP2_NB_WEIGHT"));//出生体重
		
		result.setData("IN_DEPT", this.getValue("TP2_IN_DEPT")); // 入院科室
		result.setData("VS_NURSE_CODE", this.getValue("TP2_VS_NURSE_CODE")); // 责任护士
		result.setData("OUT_TYPE", this.getValue("TP2_OUT_TYPE")); // 离院方式
		result.setData("TRAN_HOSP", this.getValue("TP2_TRAN_HOSP")); // 外转院所
		// 20120813 shibl modify
		result.setData("TRAN_HOSP_OTHER", this.getValue("TRAN_HOSP_OTHER")); // 外转院所其他补充
		String beday = getFormatString(this.getValueString("TP2_BE_IN_D"));
		String behour = getFormatString(this.getValueString("TP2_BE_IN_H"));
		String bemin = getFormatString(this.getValueString("TP2_BE_IN_M"));
		result.setData("BE_COMA_TIME", beday + behour + bemin); // 入院前昏迷时间
		String afday = getFormatString(this.getValueString("TP2_AF_IN_D"));
		String afhour = getFormatString(this.getValueString("TP2_AF_IN_H"));
		String afmin = getFormatString(this.getValueString("TP2_AF_IN_M"));
		result.setData("AF_COMA_TIME", afday + afhour + afmin); // 入院后昏迷时间
		if (this.getCheckbox("TP2_AGN_PLAN_FLG").isSelected()) {
			result.setData("AGN_PLAN_FLG", "Y");
		} else {
			result.setData("AGN_PLAN_FLG", "N"); // 31天再住院计划标记
		}
		result.setData("AGN_PLAN_INTENTION", this
				.getValue("TP2_AGN_PLAN_INTENTION")); // 31天再住院计划
		/****************************** ADD END ************************************************/
		result.setData("OUT_DEPT", this.getValue("TP2_OUT_DEPT")); // 出院科室
		result.setData("IN_CONDITION", this.getValue("TP2_IN_CONDITION")); // 入院情况
		if (this.getValue("TP2_OUT_DATE") == null)
			result.setData("OUT_DATE", new TNull(Timestamp.class));
		else
			result.setData("OUT_DATE", this.getValue("TP2_OUT_DATE")); // 出院时间
		result.setData("ISMODIFY_DSDATE", this
				.IsModifydsDate((Timestamp) getValue("TP2_OUT_DATE")));

		if (this.getValue("TP2_CONFIRM_DATE") == null)
			result.setData("CONFIRM_DATE", new TNull(Timestamp.class));
		else
			result.setData("CONFIRM_DATE", this.getValue("TP2_CONFIRM_DATE")); // 确诊日期
		result.setData("REAL_STAY_DAYS", this.getValue("TP2_REAL_STAY_DAYS"));// 实际住院天数
		result.setData("PATHOLOGY_DIAG", this.getValue("TP22_PATHOLOGY_DIAG")); // 病理诊断1
		// 医院感染总次数
		result.setData("INFECT_COUNT", this.getValue("INFECT_COUNT"));
		// 20120113 add shibl
		result.setData("PATHOLOGY_NO", this.getValue("TP2_PATHOLOGY_NO")); // 病理号1
		result
				.setData("PATHOLOGY_DIAG2", this
						.getValue("TP22_PATHOLOGY_DIAG2")); // 病理诊断2
		result.setData("PATHOLOGY_NO2", this.getValue("TP2_PATHOLOGY_NO2")); // 病理号2
		result
				.setData("PATHOLOGY_DIAG3", this
						.getValue("TP22_PATHOLOGY_DIAG3")); // 病理诊断3
		result.setData("PATHOLOGY_NO3", this.getValue("TP2_PATHOLOGY_NO3")); // 病理号3

		result.setData("DIF_DEGREE", this.getValue("DIF_DEGREE"));// 分化程度
		result.setData("MDIAG_BASIS", this.getValue("MDIAG_BASIS"));// 最高诊断依据

		result.setData("EX_RSN", this.getValue("TP22_EX_RSN")); // 损伤中毒外部因素1
		result.setData("EX_RSN2", this.getValue("TP22_EX_RSN2")); // 损伤中毒外部因素2
		result.setData("EX_RSN3", this.getValue("TP22_EX_RSN3")); // 损伤中毒外部因素3

		result.setData("ALLEGIC", this.getValue("TP22_ALLEGIC") == null ? "":this.getValue("TP22_ALLEGIC")); // 药物过敏
		if (getRDBtn("ALLEGIC_1").isSelected())
			result.setData("ALLEGIC_FLG", "1");
		else if (getRDBtn("ALLEGIC_2").isSelected())
			result.setData("ALLEGIC_FLG", "2");
		else
			result.setData("ALLEGIC_FLG", "");

		result.setData("GET_TIMES", this.getValue("TP22_GET_TIMES")); // 抢救次数
		result.setData("SUCCESS_TIMES", this.getValue("TP22_SUCCESS_TIMES")); // 成功次数
		result.setData("DIRECTOR_DR_CODE", this
				.getValue("TP22_DIRECTOR_DR_CODE")); // 科主任
		result.setData("PROF_DR_CODE", this.getValue("TP22_PROF_DR_CODE")); // 主(副主)任医师
		result.setData("ATTEND_DR_CODE", this.getValue("TP22_ATTEND_DR_CODE")); // 主治医师
		result.setData("VS_DR_CODE", this.getValue("TP2_VS_DR_CODE")); // 住院医师
		result.setData("INDUCATION_DR_CODE", this
				.getValue("TP22_INDUCATION_DR_CODE")); // 进修医师
		result.setData("GRADUATE_INTERN_CODE", this
				.getValue("TP22_GRADUATE_INTERN_CODE")); // 研究生实习医师
		result.setData("INTERN_DR_CODE", this.getValue("TP22_INTERN_DR_CODE")); // 实习医师
		String lineStr = "";
		if (getCheckbox("OPE_TYPE_CODE1").isSelected())
			lineStr = "1";
		if (getCheckbox("OPE_TYPE_CODE2").isSelected()) {
			if (lineStr.length() > 0) {
				lineStr += ",2";
			} else {
				lineStr = "2";
			}
		}
		
		result.setData("DAY_OPE_FLG", this.getValue("DAY_OPE_FLG")); // 日间手术 add by huangtt 20161222

		result.setData("OPE_TYPE_CODE", lineStr.equals("") ? "0" : lineStr);// 手术患者类型
		result.setData("RBC", this.getValue("TP23_RBC")); // 红细胞
		result.setData("PLATE", this.getValue("TP23_PLATE")); // 血小板
		result.setData("PLASMA", this.getValue("TP23_PLASMA")); // 血浆
		result.setData("WHOLE_BLOOD", this.getValue("TP23_WHOLE_BLOOD")); // 全 血
		result.setData("BANKED_BLOOD", this.getValue("TP23_BANKED_BLOOD")); // 全体回收
		result.setData("OTH_BLOOD", this.getValue("TP23_OTH_BLOOD")); // 其 他
		result.setData("ACCOMPANY_YEAR", this.getValue("TP24_ACCOMPANY_YEAR")); // 随诊年数
		result
				.setData("ACCOMPANY_MONTH", this
						.getValue("TP24_ACCOMPANY_MONTH")); // 随诊月数
		result.setData("ACCOMPANY_WEEK", this.getValue("TP24_ACCOMPANY_WEEK")); // 随诊周数
		setACCOMP_DATE();// 计算随诊日期
		if (this.getValue("TP24_ACCOMP_DATE") == null)
			result.setData("ACCOMP_DATE", new TNull(Timestamp.class));
		else
			result.setData("ACCOMP_DATE", this.getValue("TP24_ACCOMP_DATE")); // 出院时间

		// result.setData("",this.getValue(""));//临床路径 暂缺
		// result.setData("",this.getValue(""));//临床路径病种 暂缺
		// 单选框取值
		// HBsAg
		if (getRDBtn("HBsAg_1").isSelected())
			result.setData("HBSAG", "0");
		else if (getRDBtn("HBsAg_2").isSelected())
			result.setData("HBSAG", "1");
		else if (getRDBtn("HBsAg_3").isSelected())
			result.setData("HBSAG", "2");
		else
			result.setData("HBSAG", "");
		// HCV-Ab
		if (getRDBtn("HCV-Ab_1").isSelected())
			result.setData("HCV_AB", "0");
		else if (getRDBtn("HCV-Ab_2").isSelected())
			result.setData("HCV_AB", "1");
		else if (getRDBtn("HCV-Ab_3").isSelected())
			result.setData("HCV_AB", "2");
		else
			result.setData("HCV_AB", "");
		// HCV-Ab
		if (getRDBtn("HIV-Ab_1").isSelected())
			result.setData("HIV_AB", "0");
		else if (getRDBtn("HIV-Ab_2").isSelected())
			result.setData("HIV_AB", "1");
		else if (getRDBtn("HIV-Ab_3").isSelected())
			result.setData("HIV_AB", "2");
		else
			result.setData("HIV_AB", "");
		// 门诊与住院
		if (getRDBtn("TP22_myc1").isSelected())
			result.setData("QUYCHK_OI", "0");
		else if (getRDBtn("TP22_myc2").isSelected())
			result.setData("QUYCHK_OI", "1");
		else if (getRDBtn("TP22_myc3").isSelected())
			result.setData("QUYCHK_OI", "2");
		else if (getRDBtn("TP22_myc4").isSelected())
			result.setData("QUYCHK_OI", "3");
		else
			result.setData("QUYCHK_OI", "");
		// 入院与出院
		if (getRDBtn("TP22_ryc1").isSelected())
			result.setData("QUYCHK_INOUT", "0");
		else if (getRDBtn("TP22_ryc2").isSelected())
			result.setData("QUYCHK_INOUT", "1");
		else if (getRDBtn("TP22_ryc3").isSelected())
			result.setData("QUYCHK_INOUT", "2");
		else if (getRDBtn("TP22_ryc4").isSelected())
			result.setData("QUYCHK_INOUT", "3");
		else
			result.setData("QUYCHK_INOUT", "");
		// 术前与术后
		if (getRDBtn("TP22_sys1").isSelected())
			result.setData("QUYCHK_OPBFAF", "0");
		else if (getRDBtn("TP22_sys2").isSelected())
			result.setData("QUYCHK_OPBFAF", "1");
		else if (getRDBtn("TP22_sys3").isSelected())
			result.setData("QUYCHK_OPBFAF", "2");
		else if (getRDBtn("TP22_sys4").isSelected())
			result.setData("QUYCHK_OPBFAF", "3");
		else
			result.setData("QUYCHK_OPBFAF", "");
		// 临床与病理
		if (getRDBtn("TP22_lyb1").isSelected())
			result.setData("QUYCHK_CLPA", "0");
		else if (getRDBtn("TP22_lyb2").isSelected())
			result.setData("QUYCHK_CLPA", "1");
		else if (getRDBtn("TP22_lyb3").isSelected())
			result.setData("QUYCHK_CLPA", "2");
		else if (getRDBtn("TP22_lyb4").isSelected())
			result.setData("QUYCHK_CLPA", "3");
		else
			result.setData("QUYCHK_CLPA", "");
		// 放射与病理
		if (getRDBtn("TP22_fyb1").isSelected())
			result.setData("QUYCHK_RAPA", "0");
		else if (getRDBtn("TP22_fyb2").isSelected())
			result.setData("QUYCHK_RAPA", "1");
		else if (getRDBtn("TP22_fyb3").isSelected())
			result.setData("QUYCHK_RAPA", "2");
		else if (getRDBtn("TP22_fyb4").isSelected())
			result.setData("QUYCHK_RAPA", "3");
		else
			result.setData("QUYCHK_RAPA", "");
		// 血型
		if (getRDBtn("TP23XX1").isSelected())
			result.setData("BLOOD_TYPE", "1");
		else if (getRDBtn("TP23XX2").isSelected())
			result.setData("BLOOD_TYPE", "2");
		else if (getRDBtn("TP23XX3").isSelected())
			result.setData("BLOOD_TYPE", "3");
		else if (getRDBtn("TP23XX4").isSelected())
			result.setData("BLOOD_TYPE", "4");
		else if (getRDBtn("TP23XX5").isSelected())
			result.setData("BLOOD_TYPE", "5");// 不详
		else if (getRDBtn("TP23XX6").isSelected())
			result.setData("BLOOD_TYPE", "6");// 未做
		// 输血反映
		if (getRDBtn("PT23SX1").isSelected())
			result.setData("TRANS_REACTION", "1");
		else if (getRDBtn("PT23SX2").isSelected())
			result.setData("TRANS_REACTION", "2");
		else
			result.setData("TRANS_REACTION", "0");
		// RH
		if (getRDBtn("PT23RH1").isSelected())
			result.setData("RH_TYPE", "1");
		else if (getRDBtn("PT23RH2").isSelected())
			result.setData("RH_TYPE", "2");
		else if (getRDBtn("PT23RH3").isSelected())
			result.setData("RH_TYPE", "3");
		else if (getRDBtn("PT23RH4").isSelected())
			result.setData("RH_TYPE", "4");
		// 尸检
		result.setData("BODY_CHECK", this.getValue("BODY_CHECK"));// wanglong modify 20141030
		// 本院第一例
		if (getRDBtn("PT24BY1").isSelected())
			result.setData("FIRST_CASE", "1");
		else if (getRDBtn("PT24BY2").isSelected())
			result.setData("FIRST_CASE", "2");
		else
			result.setData("FIRST_CASE", "");
		// 示教病例
		if (getRDBtn("PT24SJBL1").isSelected())
			result.setData("SAMPLE_FLG", "1");
		else if (getRDBtn("PT24SJBL2").isSelected())
			result.setData("SAMPLE_FLG", "2");
		else
			result.setData("SAMPLE_FLG", "");
		// 必要条件
		result.setData("MR_NO", this.getValue("TP1_MR_NO"));
		result.setData("CASE_NO", this.getValue("TP1_CASE_NO"));
		result.setData("IPD_NO", this.getValue("TP1_IPD_NO"));
		// 医师提交标识
		if (((TCheckBox) this.getComponent("DIAGCHK_FLG")).isSelected())
			result.setData("DIAGCHK_FLG", "Y");
		else
			result.setData("DIAGCHK_FLG", "N");
		boolean flg = false;
		// 获取主手术信息
		TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
		if (opTable.getRowCount() > 0) {
			int row = -1;
			for (int i = 0; i < opTable.getRowCount(); i++) {
				// 判断主手术
				if ("Y".equals(opTable.getValueAt(i, 0))) {
					flg = true;
					row = i;
					break;
				}
			}
			if (flg) {
				result.setData("AGN_FLG", opTable.getValueAt(row, 1));// 重返标记
				result.setData("OP_DATE", StringTool.getString(
						(Timestamp) opTable.getValueAt(row, 2),
						"yyyyMMddHHmmss"));// 手术日期
				result.setData("OP_CODE", opTable.getValueAt(row, 3));// 手术CODE
				
				//add by yangjj 20150811
				Object opLevel = opTable.getValueAt(row, 5);
				if(opLevel == null){
					result.setData("OP_LEVEL", "");// 手术等级
				}else {
					result.setData("OP_LEVEL", opLevel);
				}
				
				
				//result.setData("OP_LEVEL", opTable.getValueAt(row, 5));// 手术等级
				result.setData("MAIN_SUGEON", opTable.getValueAt(row, 9));// 手术医师
				result.setData("HEAL_LV",
						opTable.getValueAt(row, 15) == null ? "" : opTable
								.getValueAt(row, 15));// 恢复等级 modify by wanglong 20130802 从14改为15
			} else {
				result.setData("OP_CODE", "");
				result.setData("OP_DATE", "");
				result.setData("MAIN_SUGEON", "");
				result.setData("OP_LEVEL", "");
				result.setData("HEAL_LV", "");
				result.setData("AGN_FLG", "");
			}

		} else {
			result.setData("OP_CODE", "");
			result.setData("OP_DATE", "");
			result.setData("MAIN_SUGEON", "");
			result.setData("OP_LEVEL", "");
			result.setData("HEAL_LV", "");
			result.setData("AGN_FLG", "");
		}
		result.setData("OPT_USER", OPT_USER);
		result.setData("OPT_TERM", OPT_TERM);

		/*
		 * 病案首页中，加字段临床试验病例字典、教学病例字典同时， 加上查询功能；病历维护加两个字段 Modify ZhenQin -
		 * 2011-05-09 and 2011-05-18
		 */

		result.setData("TEST_EMR", this.getValue("TEST_EMR"));
		result.setData("TEACH_EMR", this.getValueString("TEACH_EMR"));   //modify by wukai 20160602
		result.setData("CLNCPATH_CODE", this.getValue("CLNCPATH_CODE"));
		result.setData("DISEASES_CODE", this.getValue("DISEASES_CODE"));
		
		//modify by zhangh 2013-10-11 增加第二页签第五小页签  监护室入住情况
		TTable icuTable = (TTable) this.getComponent("TP25_ICU_Table");
		icuTable.acceptText();
		if(icuTable.getRowCount() > 0){
			int count = 1;
			for (int j = 0; j < icuTable.getRowCount(); j++) {
				result.setData("ICU_ROOM" + count, icuTable.getValueAt_(j, 0)
						== null ? "" : icuTable.getValueAt_(j, 0));
				result.setData("ICU_IN_DATE" + count, icuTable.getValueAt_(j, 1)
						== null ? new TNull(Timestamp.class) : icuTable.getValueAt_(j, 1));
				result.setData("ICU_OUT_DATE" + count, icuTable.getValueAt_(j, 2)
						== null ? new TNull(Timestamp.class) : icuTable.getValueAt_(j, 2));
				count++;
			}
		}
		result.setData("VENTI_TIME", this.getValue("VENTI_TIME"));
		
		// add by wangqing 20180116  start 新增呼吸机使用分钟、呼吸机使用秒
		result.setData("VENTI_TIME_MIN", this.getValue("VENTI_TIME_MIN"));
		result.setData("VENTI_TIME_SEC", this.getValue("VENTI_TIME_SEC"));
		// add by wangqing 20180116  end
		
		return result;
	}

	/**
	 * 保存第四页签的内容
	 * 
	 * @return TParm
	 */
	public TParm getFourPageInfo() {
		TParm parm = new TParm();
		// 传染病报告
		if (getRDBtn("TP4CR1").isSelected())
			parm.setData("INFECT_REPORT", "1");
		else if (getRDBtn("TP4CR2").isSelected())
			parm.setData("INFECT_REPORT", "2");
		else
			parm.setData("INFECT_REPORT", "");
		// 四病报告
		if (getRDBtn("TP4SB1").isSelected())
			parm.setData("DIS_REPORT", "1");
		else if (getRDBtn("TP4SB2").isSelected())
			parm.setData("DIS_REPORT", "2");
		else
			parm.setData("DIS_REPORT", "");
		// 病案质量
		if (getRDBtn("TP4BA1").isSelected())
			parm.setData("QUALITY", "1");
		else if (getRDBtn("TP4BA2").isSelected())
			parm.setData("QUALITY", "2");
		else if (getRDBtn("TP4BA3").isSelected())
			parm.setData("QUALITY", "3");
		else
			parm.setData("QUALITY", "");
		// 质控医师
		parm.setData("CTRL_DR", this.getValue("TP4_CTRL_DR"));
		// 质控护士
		parm.setData("CTRL_NURSE", this.getValue("TP4_CTRL_NURSE"));
		// 日期
		if (this.getValue("TP4_CTRL_DATE") == null)
			parm.setData("CTRL_DATE", new TNull(Timestamp.class));
		else
			parm.setData("CTRL_DATE", this.getValue("TP4_CTRL_DATE"));
		// 编码员
		parm.setData("ENCODER", this.getValue("TP4_ENCODER"));
		parm.setData("MR_NO", this.getValue("TP1_MR_NO"));
		parm.setData("CASE_NO", this.getValue("TP1_CASE_NO"));
		parm.setData("IPD_NO", this.getValue("TP1_IPD_NO"));
		// 病案室提交标识
		if (((TCheckBox) this.getComponent("QTYCHK_FLG")).isSelected())
			parm.setData("QTYCHK_FLG", "Y");
		else
			parm.setData("QTYCHK_FLG", "N");

		// 财务提交标识（属于第三页签内容，没有单独的保存方法，跟随第四页签保存）
		if (((TCheckBox) this.getComponent("BILCHK_FLG")).isSelected())
			parm.setData("BILCHK_FLG", "Y");
		else
			parm.setData("BILCHK_FLG", "N");
		parm.setData("OPT_USER", OPT_USER);
		parm.setData("OPT_TERM", OPT_TERM);
		return parm;
	}

	/**
	 * 获取本页的单选框
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	private TRadioButton getRDBtn(String tag) {
		TRadioButton rbt = (TRadioButton) this.getComponent(tag);
		return rbt;
	}

	/**
	 * 获取病人手术信息
	 * 
	 * @param MR_NO
	 *            String 病案号
	 * @param CASE_NO
	 *            String 就诊序号
	 */
	private void OPTableBind(String MR_NO, String CASE_NO) {
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
//		TDataStore OP_Info = table.getDataStore();
//		// 获取SQL语句
		String sql = MROSqlTool.getInstance().getOPSelectSQL(MR_NO, CASE_NO);
//		OP_Info.setSQL(sql);
//		OP_Info.retrieve();
//		table.setDSValue();
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() <= 0){
			table.removeRowAll();
			return;
		}
		for (int i = 0; i < parm.getCount(); i++) {
			//算出手术持续时间(小时和分钟)
			if(parm.getData("OPE_TIME", i) != null && 
					!"".equals(parm.getData("OPE_TIME", i).toString())){
				String[] arr;
				String ope_hour="",ope_minute="";
				String opt_time = parm.getData("OPE_TIME", i).toString();
				arr = opt_time.split("|");
				int index;
				for (int j = 0; j < arr.length; j++) {
					if(arr[j].equals("|")){
						index = j;
						for (int j2 = 0; j2 < index; j2++) {
							ope_hour += arr[j2];
						}
						for (int j3 = index + 1; j3 < arr.length; j3++) {
							ope_minute += arr[j3];
						}
						break;
					}
				}
				parm.setData("OPE_TIME_HOUR", i, ope_hour);
				parm.setData("OPE_TIME_MINUTE", i, ope_minute);
			}
			//判断表格中外院医师备注字段是否可写
			if(parm.getData("MAIN_SUGEON", i) != null && 
					parm.getData("MAIN_SUGEON", i).toString().length() > 0){
				String docId = parm.getData("MAIN_SUGEON", i).toString();
				TParm result = MRORecordTool.getInstance().isOutDoc(docId);
				if(result.getCount() <= 0){
					table.setLockCell(i, 10, true);
					continue;
				}
				if(result.getData("IS_OUT_FLG", 0) != null && 
						"Y".equals(result.getData("IS_OUT_FLG", 0).toString())){
			    	table.setLockCell(i, 10, false);
			    	continue;
			    }
			}
			table.setLockCell(i, 10, true);
		}
		table.setParmValue(parm);
	}

	/**
	 * 添加手术信息
	 */
	public void addOP() {
		/*TTable table = (TTable) this.getComponent("TP23_OP_Table");
		table.acceptText();
		TDataStore OP_Info = table.getDataStore();
		int MAX_SEQ = 0;
		// 获取最大序号
		MAX_SEQ = getMaxSeq(OP_Info, "SEQ_NO");
		MAX_SEQ = getMaxSeq();
		// 加入新行
		int row = table.addRow();
		// 在datastore中相应的新行中 加入最大序号
		OP_Info.setItem(row, "SEQ_NO", MAX_SEQ);
		// 初始化日期格式
		// table.setValueAt(SystemTool.getInstance().getDate(),row,0);
		table.setSelectedRow(row);*/
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		// 加入新行
		int row = table.addRow();
		table.setSelectedRow(row);
		table.setLockCell(row, 10, true);
//		this.messageBox(table.getParmValue().getData("SEQ_NO", row)+"");
	}

	/**
	 * 移除手术信息
	 */
	public void removeOP() {
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		// 判断是否有选中行
		if (table.getTable().getSelectedRow() < 0)
			return;
		// 删除选中行
		table.removeRow(table.getSelectedRow());
		table.clearSelection();
	}

	/**
	 * 获取 手术信息改动生成的SQL语句
	 * 
	 * @return String[]
	 */
	public String[] getOPSQL() {
		Timestamp date = StringTool.getTimestamp(new Date());
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		table.acceptText();
		TDataStore OP_Info = table.getDataStore();
		// 获得全部改动的行号
		int rows[] = OP_Info.getModifiedRows(OP_Info.PRIMARY);
		// 给固定数据配数据
		for (int i = 0; i < rows.length; i++) {
			// System.out.println("---------------"+rows[i]);
			OP_Info.setItem(rows[i], "CASE_NO", CASE_NO);
			OP_Info.setItem(rows[i], "MR_NO", MR_NO);
			OP_Info.setItem(rows[i], "IPD_NO", getValue("TP1_IPD_NO"));// 住院号
			OP_Info.setItem(rows[i], "OPT_USER", Operator.getID());
			OP_Info.setItem(rows[i], "OPT_DATE", date);
			OP_Info.setItem(rows[i], "OPT_TERM", Operator.getIP());
		}
		// 获取需要执行的SQL
		String[] upSQL = OP_Info.getUpdateSQL();
		return upSQL;
	}
	
	/**
	 * 插入mro_record_op表方法
	 * @return 参数列表
	 */
	private TParm insertMRO_Record_OP(){
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		TParm parm = new TParm();
		if(table.getRowCount() <= 0){
			parm.addData("CASE_NO", CASE_NO);
			return parm;
		}
		for (int i = 0; i < table.getRowCount(); i++) {
			parm.addData("CASE_NO", CASE_NO);
			parm.addData("SEQ_NO", i + 1);
			parm.addData("IPD_NO", getValue("TP1_IPD_NO"));
			parm.addData("MR_NO", MR_NO);
			parm.addData("OP_CODE", table.getValueAt(i, 3) == null ? "" : table.getValueAt(i, 3));
			parm.addData("OP_DESC", table.getValueAt(i, 4) == null ? "" : table.getValueAt(i, 4));
			parm.addData("OP_REMARK", "");
			parm.addData("OP_DATE", table.getValueAt(i, 2) == null ? "" : table.getValueAt(i, 2).toString().length() > 0 ? table.getValueAt(i, 2).toString().substring(0, table.getValueAt(i, 2).toString().
					lastIndexOf(".")).replace("-", "").replace(" ", "").replace(":", "") : "");
			parm.addData("ANA_WAY", table.getValueAt(i, 14) == null ? "" : table.getValueAt(i, 14));
			parm.addData("ANA_DR", table.getValueAt(i, 16) == null ? "" : table.getValueAt(i, 16));
			parm.addData("MAIN_SUGEON", table.getValueAt(i, 9) == null ? "" : table.getValueAt(i, 9));
			parm.addData("MAIN_SUGEON_REMARK", table.getValueAt(i, 10) == null ? "" : table.getValueAt(i, 10));
			parm.addData("AST_DR1", table.getValueAt(i, 11) == null ? "" : table.getValueAt(i, 11));
			parm.addData("AST_DR2", table.getValueAt(i, 12) == null ? "" : table.getValueAt(i, 12));
			parm.addData("HEALTH_LEVEL", table.getValueAt(i, 15) == null ? "" : table.getValueAt(i, 15));
			parm.addData("OP_LEVEL", table.getValueAt(i, 5) == null ? "" : table.getValueAt(i, 5));
			parm.addData("OPT_USER", Operator.getID());
			parm.addData("OPT_DATE", SystemTool.getInstance().getDate());
			parm.addData("OPT_TERM", Operator.getIP());
			parm.addData("MAIN_FLG", table.getValueAt(i, 0) == null ? "" : table.getValueAt(i, 0));
			parm.addData("AGN_FLG", table.getValueAt(i, 1) == null ? "" : table.getValueAt(i, 1));
			parm.addData("OPE_SITE", table.getValueAt(i, 6) == null ? "" : table.getValueAt(i, 6));
			//计算手术持续时间
			String ope_hour,ope_minute;
			if(table.getValueAt(i, 7) != null && !table.getValueAt(i, 7).toString().trim().equals("")){
				ope_hour = table.getValueAt(i, 7).toString();
			}else{
				ope_hour = "0";
			}
			if(table.getValueAt(i, 8) != null && !table.getValueAt(i, 8).toString().trim().equals("")){
				ope_minute = table.getValueAt(i, 8).toString();
			}else{
				ope_minute = "0";
			}
			parm.addData("OPE_TIME", ope_hour + "|" + ope_minute);
			parm.addData("ANA_LEVEL", table.getValueAt(i, 13) == null ? "" : table.getValueAt(i, 13));
			//zhangh 2013-5-13 手术风险等级
			int opeHour = 0,nnisCode = 0,anaLevel = 0;
			String healthLevel = "";
			opeHour = Integer.parseInt(ope_hour);
			if(opeHour >= 3)
				nnisCode++;
			if(table.getValueAt(i, 15) != null && !table.getValueAt(i, 15).toString().trim().equals("")){
				healthLevel = table.getValueAt(i, 15).toString();
				int healthLevelFirst = Integer.parseInt(healthLevel.substring(0, 1));
				switch (healthLevelFirst) {
				case 3:
				case 4:
					nnisCode++;
					break;
				}
			}
			if(table.getValueAt(i, 13) != null && !table.getValueAt(i, 13).toString().trim().equals("")){
				anaLevel = Integer.parseInt(table.getValueAt(i, 13).toString());
				switch (anaLevel) {
				case 3:
				case 4:
				case 5:
				case 6:
					nnisCode++;
					break;
				}
			}
			parm.addData("NNIS_CODE", nnisCode);
		}
		return parm;
	}

	/**
	 * 模糊查询（内部类）
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");

		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER
					: dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ICD_CODE");
			Vector d = (Vector) parm.getData("ICD_CHN_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}

	/**
	 * 获取诊断Grid数据（旧的）
	 * 
	 * @return TParm
	 */
	private TParm getDiagGridData() {
		TTable table = (TTable) this.callFunction("UI|TP21_Table2|getThis");
		table.acceptText();
		TParm result = new TParm();
		TParm tableData = table.getParmValue();
		int outIndex = 2;// 出院诊断数 以2作为开始值 因为出院主诊断是1
		int oeIndex = 2;// 门急诊诊断数 以2作为开始值 因为门急诊主诊断是1
		int inIndex = 2;// 入院诊断数 以2作为开始值 因为入院主诊断是1
		for (int i = 0; i < tableData.getCount(); i++) {
			// 判断是否是门急诊诊断
			if ("I".equals(tableData.getValue("TYPE", i))) {
				// 门急诊诊断ICD_CODE
				if ("Y".equals(tableData.getValue("MAIN", i))) {// 判断是否是主诊断
					result.setData("OE_DIAG_CODE", tableData
							.getValue("CODE", i));
				} else {
					result.setData("OE_DIAG_CODE" + oeIndex, tableData
							.getValue("CODE", i));
					oeIndex++;
				}
			} else if ("M".equals(tableData.getValue("TYPE", i))) {// 判断是否是入院诊断
				// 入院诊断ICD_CODE
				if ("Y".equals(tableData.getValue("MAIN", i))) {// 判断是否是主诊断
					result.setData("IN_DIAG_CODE", tableData
							.getValue("CODE", i));
				} else {
					result.setData("IN_DIAG_CODE" + inIndex, tableData
							.getValue("CODE", i));
					inIndex++;
				}
			} else if ("Q".equals(tableData.getValue("TYPE", i))) {// 判断是否是院内感染诊断
				// 院内感染诊断ICD_CODE
				result.setData("INTE_DIAG_CODE", tableData.getValue("CODE", i));
				result.setData("INTE_DIAG_STATUS", tableData.getValue("STATUS",
						i));// 转归
				result.setData("INTE_DIAG_CONDITION", tableData.getValue(
						"IN_PAT_CONDITION", i));// 入院病情
			} else if ("W".equals(tableData.getValue("TYPE", i))) {// 判断是否是院内并发诊断
				// 院内并发诊断ICD_CODE
				result.setData("COMPLICATION_DIAG", tableData.getValue("CODE",
						i));
				result.setData("COMPLICATION_STATUS", tableData.getValue(
						"STATUS", i));// 转归
				result.setData("COMPLICATION_DIAG_CONDITION", tableData
						.getValue("IN_PAT_CONDITION", i));// 入院病情
			} else if ("O".equals(tableData.getValue("TYPE", i))) {// 判断是否是出院诊断
				if ("Y".equals(tableData.getValue("MAIN", i))) {// 判断是否是主诊断
					result.setData("OUT_DIAG_CODE1", tableData.getValue("CODE",
							i));// ICD_10
					result.setData("CODE1_REMARK", tableData.getValue("REMARK",
							i));// 备注
					result.setData("CODE1_STATUS", tableData.getValue("STATUS",
							i));// 转归
					result.setData("ADDITIONAL_CODE1", tableData.getValue(
							"ADDITIONAL", i));// 附加码
					result.setData("OUT_DIAG_CONDITION1", tableData.getValue(
							"IN_PAT_CONDITION", i));// 入院病情
				} else {
					result.setData("OUT_DIAG_CODE" + outIndex, tableData
							.getValue("CODE", i));// ICD_10
					result.setData("CODE" + outIndex + "_REMARK", tableData
							.getValue("REMARK", i));// 备注
					result.setData("CODE" + outIndex + "_STATUS", tableData
							.getValue("STATUS", i));// 转归
					result.setData("ADDITIONAL_CODE" + outIndex, tableData
							.getValue("ADDITIONAL", i));// 附加码
					result.setData("OUT_DIAG_CONDITION" + outIndex, tableData
							.getValue("IN_PAT_CONDITION", i));// 入院病情
					outIndex++;
				}
			}
		}
		result.setData("CASE_NO", CASE_NO);// CASE_NO必须参数
		return result;
	}

	/**
	 * 获取诊断Grid数据
	 * 
	 * @return TParm
	 */
	private TParm getNewDiagGridData() {
		TTable table = (TTable) this.callFunction("UI|TP21_Table2|getThis");
		table.acceptText();
		TParm result = new TParm();
		// 诊断ICD替换中文
		OrderList orderDesc = new OrderList();
		TParm tableData = table.getParmValue();
		int count = 0;
		for (int i = 0; i < tableData.getCount(); i++) {
			if (!tableData.getValue("CODE", i).equals("")) {
				result.addData("CASE_NO", CASE_NO);// CASE_NO必须参数
				result.addData("MR_NO", this.MR_NO);// MR_NO必须参数
				result.addData("IPD_NO", this.getValue("TP1_IPD_NO"));// IPD_NO必须参数
				result.addData("IO_TYPE", tableData.getValue("TYPE", i));// 类型
				result.addData("ICD_KIND", tableData.getValue("KIND", i));// 诊断编码
				result.addData("MAIN_FLG", tableData.getValue("MAIN", i)
						.equals("Y") ? tableData.getValue("MAIN", i) : "N");// 主
				result.addData("ICD_CODE", tableData.getValue("CODE", i));// 诊断编码
				//add by wukai on 20170406 增加修改后的SEQ_NO
				result.addData("OLD_IO_TYPE", tableData.getValue("OLD_IO_TYPE", i));
				result.addData("OLD_ICD_CODE", tableData.getValue("OLD_ICD_CODE", i));
				//add by wukai on 20170406 增加修改后的SEQ_NO
				result.addData("SEQ_NO", tableData.getValue("SEQ_NO", i));// 序号
				result.addData("ICD_DESC", orderDesc
						.getTableShowValue(tableData.getValue("NAME", i)));// 诊断名称
				result.addData("ICD_REMARK", tableData.getValue("REMARK", i));// 备注
				result.addData("ICD_STATUS", tableData.getValue("STATUS", i));// 转归
				result.addData("ADDITIONAL_CODE", tableData.getValue(
						"ADDITIONAL", i));// 附加码
				result.addData("ADDITIONAL_DESC", tableData.getValue(
						"ADDITIONAL_DESC", i));// 附加诊断名称
				result.addData("IN_PAT_CONDITION", table.getValueAt(i, 6));// 入院病情
				result.addData("OPT_USER", Operator.getID());
				result.addData("OPT_TERM", Operator.getIP());
				result.addData("EXEC", tableData.getValue("EXEC", i));// 标识
				count++;
			}
		}
		return result;
	}

	/**
	 * 清空病理诊断
	 */
	public void onclearPatDiag() {
		this.clearValue("TP22_PATHOLOGY_DIAG;PATHOLOGY_DIAG_DESC");
	}

	/**
	 * 清空病理诊断
	 */
	public void onclearPatDiag2() {
		this.clearValue("TP22_PATHOLOGY_DIAG2;PATHOLOGY_DIAG_DESC2");
	}

	/**
	 * 清空病理诊断
	 */
	public void onclearPatDiag3() {
		this.clearValue("TP22_PATHOLOGY_DIAG3;PATHOLOGY_DIAG_DESC3");
	}

	/**
	 * 清空病毒损伤
	 */
	public void onClearEsx() {
		this.clearValue("TP22_EX_RSN;EX_RSN_DESC");
	}

	/**
	 * 清空病毒损伤
	 */
	public void onClearEsx2() {
		this.clearValue("TP22_EX_RSN2;EX_RSN_DESC2");
	}

	/**
	 * 清空病毒损伤
	 */
	public void onClearEsx3() {
		this.clearValue("TP22_EX_RSN3;EX_RSN_DESC3");
	}

	/**
	 * 检核诊断信息填写是否符合规范
	 * 
	 * @return String 返回“”表示符合标准， 返回信息表示不符合 将信息显示出来
	 */
	private String checkDiagGrid() {
		String message = "";
		TTable table = (TTable) this.callFunction("UI|TP21_Table2|getThis");
		table.acceptText();
		TParm data = table.getParmValue();
		int I_NUM = 0;// 门急诊诊断数
		int I_MainNum = 0;// 门急诊主诊断数
		int M_NUM = 0;// 入院诊断数
		int M_MainNum = 0;// 入院主诊断数
		int O_NUM = 0;// 出院诊断数
		int Q_NUM = 0;// 院内感染诊断数
		int W_NUM = 0;// 院内并发诊断数
		int O_MainNum = 0;// 出院主诊断数
		for (int i = 0; i < data.getCount(); i++) {
			if (data.getValue("CODE", i).trim().length() > 0
					&& data.getValue("TYPE", i).length() <= 0) {
				message = "请选择诊断类型";
			}
			// 判断是否是门急诊诊断
			if ("I".equals(data.getValue("TYPE", i))) {
				I_NUM++;
				if ("Y".equals(data.getValue("MAIN", i))) {// 判断是否是主诊断
					I_MainNum++;
				}
			} else if ("M".equals(data.getValue("TYPE", i))) {// 判断是否是入院诊断
				M_NUM++;
				if ("Y".equals(data.getValue("MAIN", i))) {// 判断是否是主诊断
					M_MainNum++;
				}
			} else if ("Q".equals(data.getValue("TYPE", i))) {// 判断是否是院内感染诊断
				Q_NUM++;
			} else if ("W".equals(data.getValue("TYPE", i))) {// 判断是否是院内并发诊断
				W_NUM++;
			} else if ("O".equals(data.getValue("TYPE", i))) {// 判断是否是出院诊断
				O_NUM++;
				//2013-04-09 zhangh 添加 出院状况STATUS;入院病情IN_PAT_CONDITION 非空验证
				if("Y".equals(this.getValueString("DIAGCHK_FLG"))){
					if(null == data.getValue("STATUS", i) || "".equals(data.getValue("STATUS", i))){
						message = "请将全部出院诊断的出院状况填写完整";
						return message;
					}
					if(null == data.getValue("IN_PAT_CONDITION", i) || "".equals(data.getValue("IN_PAT_CONDITION", i))){
						message = "请将全部出院诊断的入院病情填写完整";
						return message;
					}
				}
				if ("Y".equals(data.getValue("MAIN", i))) {// 判断是否是主诊断
					O_MainNum++;
				}
				// 判断出院诊断的范围是否是在 C00-C97,D00-D48 之间
				// 如果在此范围内需要填写附加码
				// String orderCode = data.getValue("CODE",i);
				// if (orderCode.substring(0, 3).compareTo("C00") >= 0 &&
				// orderCode.substring(0, 3).compareTo("D48") <= 0 &&
				// !orderCode.substring(0, 3).equals("D45")) {
				// String additional = data.getValue("ADDITIONAL",i);
				// if(additional.equals("")){
				// message = "请填写肿瘤形态学编码M码(附加码)";
				// }
				// if (additional.compareTo("M80000/0") <= 0 ||
				// additional.compareTo("M99890/1") >= 0) {
				// message = "附加码范围应该在M80000/0-M99890/1";
				// }
				// }
			}
		}
		if (I_MainNum > 1) {
			message = "只能有1条门急诊诊断作为门急诊主诊断";
			return message;
		} else if (I_MainNum == 0) {
			message = "请选择1条门急诊诊断作为门急诊主诊断";
			return message;
		}
		if (M_MainNum > 1) {
			message = "只能有1条入院诊断作为入院主诊断";
			return message;
		} else if (M_MainNum == 0) {
			message = "请选择1条入院诊断作为入院主诊断";
			return message;
		}
		if (O_MainNum > 1) {
			message = "只能有1条出院诊断作为出院主诊断";
			return message;
		} else if (O_MainNum == 0) {
			message = "请选择1条出院诊断作为出院主诊断";
			return message;
		}
		return message;
	}
	/**
	 * 判断首页出院主诊断和临床出院主诊断是否一致 duzhw modify 20131211
	 * @return
	 */
	public boolean checkOMainDiag(){
		boolean flag = false;
		String icdCodeOMain1 = "";//临床诊断出院主诊断icd_code
		String icdCodeOMain2 = "";//首页诊断出院主诊断icd_code
		//判断临床诊断是否存在出院主诊断
		String caseNo = CASE_NO;
		String checkSql = "SELECT ICD_CODE FROM ADM_INPDIAG " +
				" WHERE CASE_NO = '"+caseNo+"' AND IO_TYPE = 'O' AND MAINDIAG_FLG = 'Y'";
		TParm checkParm = new TParm(TJDODBTool.getInstance().select(checkSql));
		if(checkParm.getCount("ICD_CODE") < 0){
			flag = true;
			return flag;
		}
		icdCodeOMain1 = checkParm.getValue("ICD_CODE", 0);
		TTable table = (TTable) this.callFunction("UI|TP21_Table2|getThis");
		table.acceptText();
		TParm data = table.getParmValue();
		for (int i = 0; i < data.getCount(); i++) {
			if ("O".equals(data.getValue("TYPE", i))){//判断出院门诊
				if ("Y".equals(data.getValue("MAIN", i))){//判断主诊断
					icdCodeOMain2 = data.getValue("CODE", i);
					if(icdCodeOMain2.equals(icdCodeOMain1)){//首页诊断出院主诊断 和 临床诊断出院主诊断 相同
						flag = true;
					}
				}
			}
		}
		
		return flag;
	}

	/**
	 * 诊断Grid 值改变事件
	 * 
	 * @param obj
	 *            Object
	 */
	public void onDiagTableValueCharge(Object obj) {
		// 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		TTableNode node = (TTableNode) obj;
		int row = node.getRow();
		if (node.getColumn() == 0) {// 只对诊断类型进行判断
			// if ("I".equals(node.getValue().toString()) ||
			// "M".equals(node.getValue().toString())) { //如果是门急诊诊断或者入院诊断
			// DiagGrid.setValueAt("Y", row, 1); //默认设置为主诊断
			// }
		} else if (node.getColumn() == 4) {
			if (row == DiagGrid.getRowCount() - 1)// 如果改选中行是最后一行 那么新建行
				DiagGrid.addRow();
		}
	}

	/**
	 * 诊断表 数据删除
	 */
	public void onDiagDel() {
		int row = DiagGrid.getSelectedRow();
		int lastRow = DiagGrid.getRowCount() - 1;
		if (row >= 0) {// 有选中行
			//DiagGrid.removeRow(row);
			//duzhw 走后台直接删除数据
			TParm diagGridparm=this.DiagGrid.getParmValue();
			String caseNo = CASE_NO;
			String seqNo = diagGridparm.getValue("SEQ_NO",row);
			
			if (JOptionPane.showConfirmDialog(null, "是否删除该条数据？", "信息",
					JOptionPane.YES_NO_OPTION) == 0) {
				TParm selParm = new TParm(TJDODBTool.getInstance().update(delGridSql(caseNo,seqNo)));
			}
			
			if (row == lastRow)// 如果改选中行是最后一行 那么新建行
				DiagGrid.addRow();
			patInHospInfo();
		}
	}
	/**
	 * 删除诊断sql
	 */
	public String delGridSql(String caseNo,String seqNo){
		String sql = " delete from MRO_RECORD_DIAG " +
				" where case_no = '"+caseNo+"' and seq_no = '"+seqNo+"'";
		return sql;
	}

	/**
	 * 户口邮编得到省市
	 */
	public void onRESID_POST_CODE() {
		if (getValueString("TP1_H_POSTNO") == null
				|| "".equals(getValueString("TP1_H_POSTNO")))
			return;
		String post = getValueString("TP1_H_POSTNO");
		TParm parm = this.getPOST_CODE(post);
		if (parm.getData("POST_CODE", 0) == null
				|| "".equals(parm.getData("POST_CODE", 0)))
			return;
		setValue("TP1_PROVICE", parm.getData("POST_CODE", 0).toString()
				.substring(0, 2));
		setValue("TP1_COUNTRY", parm.getData("POST_CODE", 0).toString());
	}

	/**
	 * 通信邮编的得到省市
	 */
	public void onPost() {
		String post = getValueString("TP1_POST_NO");
		if (post == null || "".equals(post)) {
			return;
		}
		TParm parm = this.getPOST_CODE(post);
		setValue("TP1_POST_R", parm.getData("POST_CODE", 0) == null ? "" : parm
				.getValue("POST_CODE", 0).substring(0, 2));
		setValue("TP1_POST_C", parm.getValue("POST_CODE", 0).toString());
	}

	/**
	 * 单位邮编的得到省市
	 */
	public void onCompanyPost() {
		String post = getValueString("TP1_O_POSTNO");
		if (post == null || "".equals(post)) {
			return;
		}
		TParm parm = this.getPOST_CODE(post);
		setValue("TP1_O_POST_R", parm.getData("POST_CODE", 0) == null ? ""
				: parm.getValue("POST_CODE", 0).substring(0, 2));
		setValue("TP1_O_POST_C", parm.getValue("POST_CODE", 0).toString());
	}

	/**
	 * 得到省市代码
	 * 
	 * @param post
	 *            String
	 * @return TParm
	 */
	public TParm getPOST_CODE(String post) {
		TParm result = SYSPostTool.getInstance().getProvinceCity(post);
		return result;
	}

	/**
	 * 清空户籍 市COMBO
	 */
	public void clearTP1_COUNTRY() {
		this.clearValue("TP1_COUNTRY");
	}

	/**
	 * 清空通信 市COMBO
	 */
	public void clearTP1_POST_C() {
		this.clearValue("TP1_POST_C");
	}

	/**
	 * 清空单位 市COMBO
	 */
	public void clearTP1_O_POST_C() {
		this.clearValue("TP1_O_POST_C");
	}

	/**
	 * 通过城市带出邮政编码1
	 */
	public void selectCode_1() {
		this.setValue("TP1_POST_NO", this.getValue("TP1_POST_C"));
		this.onPost();
	}

	/**
	 * 通过城市带出邮政编码2
	 */
	public void selectCode_2() {
		this.setValue("TP1_H_POSTNO", this.getValue("TP1_COUNTRY"));
		this.onRESID_POST_CODE();
	}

	/**
	 * 通过城市带出邮政编码3
	 */
	public void selectCode_3() {
		this.setValue("TP1_O_POSTNO", this.getValue("TP1_O_POST_C"));
		this.onCompanyPost();
	}

	// zhangyong20110405
	/**
	 * 转入病患基本信息
	 */
	private void intoData(int page) {
		// 获取病患基本信息
		Pat pat = Pat.onQueryByMrNo(MR_NO);
		if (pat == null) { // 判断此患者 MR_NO 是否存在
			this.messageBox_("无此患者信息！");
			return;
		}

		// 获取病人住院相关的所有信息
		TParm intoData = MRORecordTool.getInstance().intoData(CASE_NO);
		if (intoData.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		TParm admInp = intoData.getParm("ADMINP");// 住院信息
		// System.out.println("inp------------------------------------------------"+admInp);
		TParm admTran = intoData.getParm("ADMTRAN");// 动态信息
		// TParm admDiag = intoData.getParm("ADMDIAG");// 诊断信息
		// System.out.println("-----------admDiag----" + admDiag);

		if (page == 1) {
			/******************** 第一页签 病患基本信息 start *********************/
			if (("1".equals(UserType) && !ADMCHK_FLG.equals("Y"))
					|| ("2".equals(UserType) && !ADMCHK_FLG.equals("Y") && VS_DR
							.equals(OpenUser)) || "4".equals(UserType)) {
				this.clearTP1();
				this.setValue("TP1_MR_NO", pat.getMrNo()); // 病案号
				this.setValue("TP1_IPD_NO", pat.getIpdNo()); // 住院号
				this.setValue("PT1_PAT_NAME", pat.getName()); // 姓名
				this.setValue("TP1_IDNO", pat.getIdNo()); // 身份证
				this.setValue("TP1_SEX", pat.getSexCode()); // 性别
				this.setValue("TP1_BIRTH_DATE", pat.getBirthday()); // 生日
				this.setValue("TP1_CASE_NO", CASE_NO); // 住院序号
				this.setValue("TP1_MARRIGE", pat.getMarriageCode()); // 婚姻
				// 显示年龄
				String[] age = StringTool.CountAgeByTimestamp(
						pat.getBirthday(), admInp.getTimestamp("IN_DATE", 0));
				this.setValue("TP1_AGE", com.javahis.util.StringUtil.showAge(
						pat.getBirthday(), admInp.getTimestamp("IN_DATE", 0))); // 应该是
				// 入院日期-生日
				this.setValue("TP1_NATION", pat.getNationCode()); // 国籍
				this.setValue("TP1_FOLK", pat.getSpeciesCode()); // 民族
				this.setValue("HOMEPLACE_CODE", pat.gethomePlaceCode()); // 出生地代码
				this.setValue("BIRTHPLACE", pat.getBirthPlace()); // 籍贯代码
				// 未处理,公用下拉框没有找到 CTZ1_CODE
				String ctz1Code = pat.getCtz1Code();
				// shibl add 20120112 start
				String mroCtz = "";
				TParm ctzParm = CTZTool.getInstance().getMroCtz(ctz1Code);
				if (ctzParm.getCount() > 0) {
					mroCtz = ctzParm.getValue("MRO_CTZ", 0);
				}
				this.setValue("TP1_PAYTYPE", ctz1Code); // 支付方式 CTZ1_CODE
				this.setValue("MRO_CTZ", mroCtz); // 病案首页身份 MRO_CTZ
				// add 20120112 end
				this.setValue("TP1_INNUM", admInp.getInt("IN_COUNT", 0)); // 住院次数
				this.setValue("TP1_TEL", pat.getTelHome()); // 患者电话
				this.setValue("TP1_H_ADDRESS", pat.getResidAddress()); // 户口住址
				this.setValue("TP1_H_POSTNO", pat.getResidPostCode()); // 户口邮编
				onRESID_POST_CODE(); // 根据邮编带入省市
				this.setValue("TP1_OCCUPATION", pat.getOccCode()); // 职业
				this.setValue("TP1_OFFICE", pat.getCompanyDesc()); // 单位
				this.setValue("TP1_O_TEL", pat.getTelCompany()); // 单位电话
				// shibl add 20120112 start
				this.setValue("TP1_O_ADDRESS", pat.getCompanyAddress()); // 单位地址
				this.setValue("TP1_O_POSTNO", pat.getCompanyPost()); // 单位邮编
				this.onCompanyPost(); // 根据邮编带入省市
				this.setValue("TP1_ADDRESS", pat.getAddress());// 通信地址
				this.setValue("TP1_POST_NO", pat.getPostCode());// 通信邮编
				this.onPost(); // 根据邮编带入省市
				// add 20120112 end
				this.setValue("TP1_CONTACTER", pat.getContactsName()); // 联系人姓名
				this.setValue("TP1_RELATIONSHIP", pat.getRelationCode()); // 联系人关系
				this.setValue("TP1_CONT_TEL", pat.getContactsTel()); // 联系人电话
				this.setValue("TP1_CONT_ADDRESS", pat.getContactsAddress()); // 联系人地址
				this.setValue("ADMCHK_FLG", "N"); // 住院记录标识 恢复为未审核状态
			}
		}
		/******************** 第一页签 病患基本信息 end *********************/
		// } else if (page == 2) {
		// /******************** 第二页签 病患住院信息 start *********************/
		// if (("2".equals(UserType) && !"Y".equals("DIAGCHK_FLG"))
		// && VS_DR.equals(OpenUser) || "4".equals(UserType)) {
		// this.clearTP2();
		// // 清空Grid
		// TTable tranTable = (TTable) this.getComponent("TP21_Table1");
		// if (tranTable.getParmValue().getCount() > 0)
		// tranTable.removeRowAll(); // 清空动态表
		// TTable dailyTable = (TTable) this.getComponent("TP21_Table2");
		// if (dailyTable.getParmValue().getCount() > 0)
		// dailyTable.removeRowAll(); // 清空诊断表
		// TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
		// // if (opTable.getParmValue().getCount() > 0)
		// opTable.removeRowAll(); // 清空手术表 不能resetModify
		// // 否则会造成以前的手术记录没有被删掉
		// // 第一小页签
		// /*************************** 第一个小页签START
		// *********************************************/
		// SimpleDateFormat dt=new SimpleDateFormat("yyyy/MM/dd");
		// for (int i = 0; i < admTran.getCount(); i++) {
		// admTran.setData("TRANS_DATE",i,dt.format(StringTool.getTimestamp(admTran.getValue("TRANS_DATE",
		// i),
		// "yyyyMMdd HHmmss")));
		// }
		// admTran.setCount(admTran.getCount());
		// // System.out.println("------------admTran--11----------" + admTran);
		// tranTable.setParmValue(admTran); // 绑定动态信息
		// // 诊断Grid绑定
		// TTable diagTable = (TTable) this.getComponent("TP21_Table2");
		// diagTable.setParmValue(admDiag);
		// diagTable.addRow();
		// // //转科科室
		// // this.setValue("TP2_TRANS_DEPT",
		// // admTran.getValue("DEPT_CODE",
		// // admTran.getCount() - 1));
		// Timestamp endTime = admInp.getValue("DS_DATE", 0).equals("") ?
		// SystemTool
		// .getInstance().getDate() : admInp.getTimestamp(
		// "DS_DATE", 0);
		// int realStayDays = StringTool.getDateDiffer(StringTool
		// .getTimestamp(
		// StringTool.getString(endTime, "yyyyMMdd"),
		// "yyyyMMdd"), StringTool.getTimestamp(StringTool
		// .getString(admInp.getTimestamp("IN_DATE", 0),
		// "yyyyMMdd"), "yyyyMMdd"));
		// this.setValue("TP2_REAL_STAY_DAYS",
		// realStayDays > 0 ? String.valueOf(realStayDays) : "1"); // 实际住院天数
		// this.setValue("TP2_OUT_DEPT",
		// admInp.getValue("DS_DEPT_CODE", 0)); // 出院科室
		// this.setValue("TP2_IN_CONDITION",
		// admInp.getValue("PATIENT_CONDITION", 0)); // 入院情况
		// this.setValue("TP2_OUT_DATE", admInp.getTimestamp("DS_DATE", 0)); //
		// 出院日期
		//
		// this.setValue("TP2_CONFIRM_DATE", ""); // 确诊日期 暂时不知道应该取哪个
		// /*----------------------add
		// start------------------------------------------------------------*/
		// this.setValue("TP2_IN_DATE", admInp.getTimestamp("IN_DATE", 0)); //
		// 入院日期
		// this.setValue("TP2_IN_DEPT", admInp.getValue("IN_DEPT_CODE", 0)); //
		// 入院科室
		// this.setValue("TP2_ADM_SOURCE",
		// admInp.getValue("ADM_SOURCE", 0)); // 入院来源
		// this.setValue("TP2_VS_NURSE_CODE",
		// admInp.getValue("VS_NURSE_CODE", 0)); // 责任护士
		// /*----------------------add
		// end------------------------------------------------------------*/
		// this.setValue("DIAGCHK_FLG", "N"); // 医师提交标识 重新传入后默认为N
		// /**************** 第一个小页签end
		// **********************************************************************/
		// /************** 第三小页签START
		// *********************************************************************/
		// //add 20120201
		// this.setValue("TP22_DIRECTOR_DR_CODE",
		// admInp.getValue("DIRECTOR_DR_CODE", 0));//科主任
		// this.setValue("TP22_ATTEND_DR_CODE",
		// admInp.getValue("ATTEND_DR_CODE", 0));//主治医师
		// this.setValue("TP2_VS_DR_CODE", admInp.getValue("VS_DR_CODE",
		// 0));//经治医师
		// this.setValue("TP22_ALLEGIC", Durgallec);//过敏药物
		// // 手术Grid绑定
		// onIntoOPData();
		// // 血液信息转入
		// onIntoBMSData();
		// /**************** 第三小页签END
		// ***********************************************************************/
		// }
		// /******************** 第二页签 病患住院信息 end *********************/
		// }
		else {
			/******************** 第三页签 病患财务信息 start *********************/
			onFinance(); // 调用财务汇入方法
			/******************** 第三页签 病患财务信息 end *********************/
		}
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		TParm data = MROPrintTool.getInstance().getNewMroRecordprintData(
				CASE_NO, this.getValue("TP2_REAL_STAY_DAYS"));
		if (data.getErrCode() < 0) {
			this.messageBox("E0005");
		}
		EMRTool emrTool = new EMRTool(this.CASE_NO, this.MR_NO, this);
		Object obj = new Object();
		obj = openPrintDialog("%ROOT%\\config\\prt\\MRO\\MRO_NEWRECORD.jhw",
				data);
		emrTool.saveEMR(obj, "病案首页", "EMR080001", "EMR08000101", false);
	}

	/**
	 * SYS_FEE替换出中文
	 * 
	 * @param comboTag
	 *            String combobox控件Tag
	 * @param code
	 *            String
	 * @return String
	 */
	private String getDescByCode(String GROUP_ID, String code) {
		String sql = "SELECT CHN_DESC,ENG_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = '"
				+ GROUP_ID + "' AND ID = '" + code + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getValue("CHN_DESC", 0);
	}

	/**
	 * 转院ID选择事件 默认选择999999时人工可以手动输入
	 */
	public void onSelectTran() {
		String tranId = (String) getValue("TP2_TRAN_HOSP");
		if (tranId.equals("999999")) {
			callFunction("UI|TP2_TRAN_HOSP|setVisible", true);
			callFunction("UI|TRAN_HOSP_OTHER|setVisible", true);
		} else {
			callFunction("UI|TP2_TRAN_HOSP|setVisible", true);
			callFunction("UI|TRAN_HOSP_OTHER|setVisible", false);
			this.setValue("TRAN_HOSP_OTHER", "");
		}
	}

	/**
	 * 用隐藏的科室Combo替换出中文
	 * 
	 * @param code
	 *            String
	 * @return String
	 */
	private String getDeptDesc(String code) {
		TComboBox cmb = (TComboBox) this.getComponent("printDept");
		cmb.setValue(code);
		return cmb.getSelectedName();
	}

	/**
	 * 用隐藏的用户ID替换出用户姓名
	 * 
	 * @param userID
	 *            String
	 * @return String
	 */
	private String getUserName(String userID) {
		TComboBox cmb = (TComboBox) this.getComponent("printUser");
		cmb.setValue(userID);
		return cmb.getSelectedName();
	}

	/**
	 * 设置第一页签的控件是否可编辑
	 * 
	 * @param flg
	 *            boolean true:可编辑 false：不可编辑
	 */
	private void setEnabledPage1(boolean flg) {
		callFunction("UI|PT1_PAT_NAME|setEnabled", flg);
		callFunction("UI|TP1_IDNO|setEnabled", flg);
		callFunction("UI|TP1_SEX|setEnabled", flg);
		callFunction("UI|TP1_BIRTH_DATE|setEnabled", flg);
		callFunction("UI|TP1_MARRIGE|setEnabled", flg);
		callFunction("UI|TP1_NATION|setEnabled", flg);
		callFunction("UI|TP1_FOLK|setEnabled", flg);
		callFunction("UI|TP1_PAYTYPE|setEnabled", flg);
		callFunction("UI|TP1_INNUM|setEnabled", flg);
		callFunction("UI|TP1_TEL|setEnabled", flg);
		callFunction("UI|HOMEPLACE_CODE|setEnabled", flg);
		callFunction("UI|TP1_H_ADDRESS|setEnabled", flg);
		callFunction("UI|TP1_H_POSTNO|setEnabled", flg);
		callFunction("UI|TP1_PROVICE|setEnabled", flg);
		callFunction("UI|TP1_COUNTRY|setEnabled", flg);
		callFunction("UI|TP1_OCCUPATION|setEnabled", flg);
		callFunction("UI|TP1_OFFICE|setEnabled", flg);
		callFunction("UI|TP1_O_TEL|setEnabled", flg);
		callFunction("UI|TP1_O_ADDRESS|setEnabled", flg);
		callFunction("UI|TP1_O_POSTNO|setEnabled", flg);
		callFunction("UI|TP1_CONTACTER|setEnabled", flg);
		callFunction("UI|TP1_RELATIONSHIP|setEnabled", flg);
		callFunction("UI|TP1_CONT_TEL|setEnabled", flg);
		callFunction("UI|TP1_CONT_ADDRESS|setEnabled", flg);
		// callFunction("UI|HOMEPLACE_DESC|setEnabled", flg);
		callFunction("UI|ADMCHK_FLG|setEnabled", flg);
		// shibl20120111modify
		callFunction("UI|BIRTHPLACE|setEnabled", flg);
		// callFunction("UI|BIRTHPLACE_DESC|setEnabled", flg);
		callFunction("UI|TP1_POST_NO|setEnabled", flg);
		callFunction("UI|TP1_ADDRESS|setEnabled", flg);
		callFunction("UI|TP1_POST_R|setEnabled", flg);
		callFunction("UI|TP1_POST_C|setEnabled", flg);
		callFunction("UI|TP1_O_POST_R|setEnabled", flg);
		callFunction("UI|TP1_O_POST_C|setEnabled", flg);
		callFunction("UI|MRO_CTZ|setEnabled", flg);
		callFunction("UI|TP1_NHI_NO|setEnabled", flg);// add by wanglong
		// 201211127
		callFunction("UI|TP1_NHI_CARDNO|setEnabled", flg);// add by wanglong
		// 201211127
		if ("4".equals(UserType)) {
			callFunction("UI|ADMCHK_FLG|setEnabled", true);
		}
	}

	/**
	 * 设置第二页签的控件是否可编辑
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnabledPage2(boolean flg) {
		setEnagledPage2_1(flg);
		setEnagledPage2_2(flg);
		setEnagledPage2_3(flg);
		setEnagledPage2_4(flg);
		setEnagledPage2_5(flg);
	}

	/**
	 * 设置第二页签的第一小页签
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnagledPage2_1(boolean flg) {
		callFunction("UI|TP2_TRANS_DEPT|setEnabled", flg);
		callFunction("UI|TP21_Table1|setEnabled", flg);
		callFunction("UI|TP21_Table2|setEnabled", flg);
		callFunction("UI|TP2_REAL_STAY_DAYS|setEnabled", flg);
		callFunction("UI|TP2_OUT_DEPT|setEnabled", flg);
		callFunction("UI|TP2_IN_CONDITION|setEnabled", flg);
		callFunction("UI|TP2_OUT_DATE|setEnabled", flg);
		callFunction("UI|TP2_CONFIRM_DATE|setEnabled", flg);
		callFunction("UI|DIAGCHK_FLG|setEnabled", flg);
		callFunction("UI|btn_Del|setEnabled", flg);
		// add 20120113 shibl
		callFunction("UI|TP2_ADM_SOURCE|setEnabled", flg);
		callFunction("UI|TP2_IN_DATE|setEnabled", flg);
		callFunction("UI|TP2_IN_DEPT|setEnabled", flg);
		callFunction("UI|TP2_OUT_TYPE|setEnabled", flg);
		callFunction("UI|TP2_TRAN_HOSP|setEnabled", flg);
		callFunction("UI|TP2_BE_IN_D|setEnabled", flg);
		callFunction("UI|TP2_BE_IN_H|setEnabled", flg);
		callFunction("UI|TP2_BE_IN_M|setEnabled", flg);
		callFunction("UI|TP2_AF_IN_D|setEnabled", flg);
		callFunction("UI|TP2_AF_IN_H|setEnabled", flg);
		callFunction("UI|TP2_AF_IN_M|setEnabled", flg);
		callFunction("UI|TP2_VS_NURSE_CODE|setEnabled", flg);
		callFunction("UI|TP2_AGN_PLAN_INTENTION|setEnabled", flg);
		callFunction("UI|TP2_AGN_PLAN_FLG|setEnabled", flg);
		callFunction("UI|INFECT_COUNT|setEnabled", flg);

		callFunction("UI|TP2_SPENURS_DAYS|setEnabled", flg);
		callFunction("UI|TP2_FIRNURS_DAYS|setEnabled", flg);
		callFunction("UI|TP2_SECNURS_DAYS|setEnabled", flg);
		callFunction("UI|TP2_THRNURS_DAYS|setEnabled", flg);
			
		// add by wangqing 20180116  start 新增呼吸机使用分钟、呼吸机使用秒
//		callFunction("UI|TP2_VENTI_TIME|setEnabled", flg);
//		callFunction("UI|TP2_VENTI_TIME_MIN|setEnabled", flg);
//		callFunction("UI|TP2_VENTI_TIME_SEC|setEnabled", flg);
		// add by wangqing 20180116  end
		
		
		
		if ("4".equals(UserType)) {
			callFunction("UI|DIAGCHK_FLG|setEnabled", true);
		}
	}

	/**
	 * 设置第二页签的第二小页签
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnagledPage2_2(boolean flg) {
		callFunction("UI|TP22_PATHOLOGY_DIAG|setEnabled", flg);
		// add 20120113 shibl
		callFunction("UI|TP2_PATHOLOGY_NO|setEnabled", flg);

		callFunction("UI|TP22_PATHOLOGY_DIAG2|setEnabled", flg);
		callFunction("UI|TP2_PATHOLOGY_NO2|setEnabled", flg);
		callFunction("UI|TP22_PATHOLOGY_DIAG3|setEnabled", flg);
		callFunction("UI|TP2_PATHOLOGY_NO3|setEnabled", flg);

		callFunction("UI|TP22_EX_RSN|setEnabled", flg);

		callFunction("UI|TP22_EX_RSN2|setEnabled", flg);
		callFunction("UI|TP22_EX_RSN3|setEnabled", flg);
		callFunction("UI|DIF_DEGREE|setEnabled", flg);
		callFunction("UI|MDIAG_BASIS|setEnabled", flg);

		callFunction("UI|TP22_ALLEGIC|setEnabled", flg);
		callFunction("UI|ALLEGIC_1|setEnabled", flg);
		callFunction("UI|ALLEGIC_2|setEnabled", flg);
		callFunction("UI|HBsAg_1|setEnabled", flg);
		callFunction("UI|HBsAg_2|setEnabled", flg);
		callFunction("UI|HBsAg_3|setEnabled", flg);
		callFunction("UI|HCV-Ab_1|setEnabled", flg);
		callFunction("UI|HCV-Ab_2|setEnabled", flg);
		callFunction("UI|HCV-Ab_3|setEnabled", flg);
		callFunction("UI|HIV-Ab_1|setEnabled", flg);
		callFunction("UI|HIV-Ab_2|setEnabled", flg);
		callFunction("UI|HIV-Ab_3|setEnabled", flg);
		callFunction("UI|TP22_myc1|setEnabled", flg);
		callFunction("UI|TP22_myc2|setEnabled", flg);
		callFunction("UI|TP22_myc3|setEnabled", flg);
		callFunction("UI|TP22_myc4|setEnabled", flg);
		callFunction("UI|TP22_sys1|setEnabled", flg);
		callFunction("UI|TP22_sys2|setEnabled", flg);
		callFunction("UI|TP22_sys3|setEnabled", flg);
		callFunction("UI|TP22_sys4|setEnabled", flg);
		callFunction("UI|TP22_fyb1|setEnabled", flg);
		callFunction("UI|TP22_fyb2|setEnabled", flg);
		callFunction("UI|TP22_fyb3|setEnabled", flg);
		callFunction("UI|TP22_fyb4|setEnabled", flg);
		callFunction("UI|TP22_ryc1|setEnabled", flg);
		callFunction("UI|TP22_ryc2|setEnabled", flg);
		callFunction("UI|TP22_ryc3|setEnabled", flg);
		callFunction("UI|TP22_ryc4|setEnabled", flg);
		callFunction("UI|TP22_lyb1|setEnabled", flg);
		callFunction("UI|TP22_lyb2|setEnabled", flg);
		callFunction("UI|TP22_lyb3|setEnabled", flg);
		callFunction("UI|TP22_lyb4|setEnabled", flg);
		callFunction("UI|TP22_GET_TIMES|setEnabled", flg);
		callFunction("UI|TP22_SUCCESS_TIMES|setEnabled", flg);
		callFunction("UI|TP22_DIRECTOR_DR_CODE|setEnabled", flg);
		callFunction("UI|TP22_PROF_DR_CODE|setEnabled", flg);
		callFunction("UI|TP22_ATTEND_DR_CODE|setEnabled", flg);
		callFunction("UI|TP2_VS_DR_CODE|setEnabled", flg);
		callFunction("UI|TP22_INDUCATION_DR_CODE|setEnabled", flg);
		callFunction("UI|TP22_GRADUATE_INTERN_CODE|setEnabled", flg);
		callFunction("UI|TP22_INTERN_DR_CODE|setEnabled", flg);

		/*
		 * Modify ZhenQin 2011-05-18 添加 临床路径,病种分类,临床测试病历,教学病历
		 */
		callFunction("UI|CLNCPATH_CODE|setEnabled", flg);
		callFunction("UI|DISEASES_CODE|setEnabled", flg);
		callFunction("UI|TEST_EMR|setEnabled", flg);
		callFunction("UI|TEACH_EMR|setEnabled", flg);

		callFunction("UI|TP4_CTRL_DR|setEnabled", flg);
		callFunction("UI|TP4_CTRL_NURSE|setEnabled", flg);
		callFunction("UI|TP4_CTRL_DATE|setEnabled", flg);
	}

	/**
	 * 设置第二页签的第三小页签
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnagledPage2_3(boolean flg) {
		callFunction("UI|TP23_OP_Table|setEnabled", flg);
		callFunction("UI|TP23_btnAdd|setEnabled", flg);
	    callFunction("UI|TP23_btnPack|setEnabled", flg);//wanglong add 20150226
		callFunction("UI|TP23_btnIn|setEnabled", flg);
		callFunction("UI|TP23_btnDel|setEnabled", flg);
//		callFunction("UI|TP23XX1|setEnabled", flg);
//		callFunction("UI|TP23XX2|setEnabled", flg);
//		callFunction("UI|TP23XX3|setEnabled", flg);
//		callFunction("UI|TP23XX4|setEnabled", flg);
//		callFunction("UI|TP23XX5|setEnabled", flg);
//		callFunction("UI|TP23XX6|setEnabled", flg);
		callFunction("UI|PT23SX1|setEnabled", flg);
		callFunction("UI|PT23SX2|setEnabled", flg);
//		callFunction("UI|PT23RH1|setEnabled", flg);
//		callFunction("UI|PT23RH2|setEnabled", flg);
//		callFunction("UI|PT23RH3|setEnabled", flg);
//		callFunction("UI|PT23RH4|setEnabled", flg);
		callFunction("UI|TP23_RBC|setEnabled", flg);
		callFunction("UI|TP23_PLATE|setEnabled", flg);
		callFunction("UI|TP23_PLASMA|setEnabled", flg);
		callFunction("UI|TP23_WHOLE_BLOOD|setEnabled", flg);
		callFunction("UI|TP23_OTH_BLOOD|setEnabled", flg);
		callFunction("UI|TP23_InBlood|setEnabled", flg);

	//	callFunction("UI|DAY_OPE_FLG|setEnabled", false); 	//2017/3/29     控件不可编辑  yanmm
		callFunction("UI|OPE_TYPE_CODE1|setEnabled", flg);
		callFunction("UI|OPE_TYPE_CODE2|setEnabled", flg);
		callFunction("UI|OPE_TYPE_CODE3|setEnabled", flg);
		callFunction("UI|TP23_BANKED_BLOOD|setEnabled", flg);
	}

	/**
	 * 设置第二页签的第四小页签
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnagledPage2_4(boolean flg) {
	    callFunction("UI|BODY_CHECK|setEnabled", flg);// wanglong modify 20141030
		callFunction("UI|PT24BY1|setEnabled", flg);
		callFunction("UI|PT24BY2|setEnabled", flg);
		callFunction("UI|PT24SZ1|setEnabled", flg);
		callFunction("UI|PT24SZ2|setEnabled", flg);
		// callFunction("UI|TP24_ACCOMPANY_YEAR|setEnabled", flg);
		// callFunction("UI|TP24_ACCOMPANY_MONTH|setEnabled", flg);
		// callFunction("UI|TP24_ACCOMPANY_WEEK|setEnabled", flg);
		callFunction("UI|PT24SJBL1|setEnabled", flg);
		callFunction("UI|PT24SJBL2|setEnabled", flg);
	}
	
	/**
	 * 设置第二页签的第五小页签
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnagledPage2_5(boolean flg) {
		callFunction("UI|TP25_ICU_Table|setEnabled", flg);
		callFunction("UI|TP25_btnAdd|setEnabled", flg);
		callFunction("UI|TP25_btnDel|setEnabled", flg);
		callFunction("UI|VENTI_TIME|setEnabled", flg);
		// add by wangqing 20180116 start 新增呼吸机使用分钟、呼吸机使用秒
		callFunction("UI|VENTI_TIME|setEnabled", flg);
		callFunction("UI|VENTI_TIME|setEnabled", flg);
		callFunction("UI|VENTI_TIME|setEnabled", flg);
		// add by wangqing 20180116 end
	}

	/**
	 * 设置第三页签的控件是否可编辑
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnabledPage3(boolean flg) {
		callFunction("UI|TP3_SUM|setEnabled", flg);
		callFunction("UI|BILCHK_FLG|setEnabled", flg);
		callFunction("UI|TP3_Table1|setEnabled", flg);
		if ("4".equals(UserType)) {
			callFunction("UI|BILCHK_FLG|setEnabled", true);
		}
	}

	/**
	 * 设置第四页签的控件是否可编辑
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnabledPage4(boolean flg) {
		callFunction("UI|TP4CR1|setEnabled", flg);
		callFunction("UI|TP4CR2|setEnabled", flg);
		callFunction("UI|TP4SB1|setEnabled", flg);
		callFunction("UI|TP4SB2|setEnabled", flg);
		callFunction("UI|TP4BA1|setEnabled", flg);
		callFunction("UI|TP4BA2|setEnabled", flg);
		callFunction("UI|TP4BA3|setEnabled", flg);
		// callFunction("UI|TP4_CTRL_DR|setEnabled", flg);
		// callFunction("UI|TP4_CTRL_NURSE|setEnabled", flg);
		// callFunction("UI|TP4_CTRL_DATE|setEnabled", flg);
		callFunction("UI|TP4_ENCODER|setEnabled", flg);
		callFunction("UI|QTYCHK_FLG|setEnabled", flg);
		if ("4".equals(UserType)) {
			callFunction("UI|QTYCHK_FLG|setEnabled", true);
		}
		
		//add by yangjj 20151224 医生登录可以选择传染病报告
		if ("2".equals(UserType)){
			callFunction("UI|TP4CR1|setEnabled", true);
			callFunction("UI|TP4CR2|setEnabled", true);
		}
	}

	/**
	 * 出院卡片
	 */
	public void onOutHospital() {
		// 获取某一病患的首页信息
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm mro = MRORecordTool.getInstance().getInHospInfo(parm);
		if (mro.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (mro.getValue("OUT_DATE", 0).length() <= 0) {
			this.messageBox_("该病患住院中");
			return;
		}
		DecimalFormat df = new DecimalFormat("0.00");
		// 查询手术信息
		TParm op_date = MRORecordTool.getInstance().queryOP_Info(CASE_NO);
		// 诊断ICD替换中文
		OrderList orderDesc = new OrderList();
		// 整理数据
		TParm printData = new TParm();
		/*************** T1 病患基本信息部分 *********************/
		printData.setData("STATION", "TEXT", getDeptDesc(mro.getValue(
				"IN_DEPT", 0)));// 目前显示的是入院科室
		printData.setData("MR_NO", "TEXT", mro.getValue("MR_NO", 0));// 病案号
		printData.setData("IN_COUNT", "TEXT", mro.getValue("IN_COUNT", 0));// 住院次数
		printData.setData("ICD10", "TEXT", mro.getValue("IN_DIAG_CODE", 0));// 入院诊断
		printData.setData("ICDTYPE", "TEXT", mro.getValue("", 0));// 疾病分类号 暂无
		// 不知道取哪
		printData.setData("CTZ", "TEXT", mro.getValue("CTZ1_CODE", 0));// 身分别
		printData.setData("IPD_NO", "TEXT", mro.getValue("IPD_NO", 0));// 住院号
		printData.setData("PAT_NAME", "TEXT", mro.getValue("PAT_NAME", 0));// 姓名
		printData.setData("SEX", "TEXT",
				mro.getValue("SEX", 0).equals("1") ? "男" : "女");// 性别
		printData.setData("MARRIGE", "TEXT", mro.getValue("MARRIGE", 0));// 婚姻
		printData.setData("BIRTH_DATE", "TEXT", StringTool.getString(mro
				.getTimestamp("BIRTH_DATE", 0), "yyyy/MM/dd"));// 生日
		String OCCUPATION = getDescByCode("SYS_OCCUPATION", mro.getValue(
				"OCCUPATION", 0));
		printData.setData("OCCUPATION", "TEXT", OCCUPATION);// 职业
		printData.setData("H_ADDRESS", "TEXT", mro.getValue("H_ADDRESS", 0));// 现住址
		printData.setData("OFFICE", "TEXT", mro.getValue("OFFICE", 0));// 单位
		printData.setData("IN_DATE", "TEXT", StringTool.getString(mro
				.getTimestamp("IN_DATE", 0), "yyyy年MM月dd日"));// 入院日期
		printData.setData("OUT_DATE", "TEXT", StringTool.getString(mro
				.getTimestamp("OUT_DATE", 0), "yyyy年MM月dd日"));// 出院日期
		printData.setData("REAL_STAY_DAYS", "TEXT", mro.getValue(
				"REAL_STAY_DAYS", 0));// 实际住院天数
		printData.setData("TRAN_DEPT", "TEXT", getDeptDesc(mro.getValue(
				"TRAN_DEPT", 0)));// 转科科别
		printData.setData("IN_DIAG_CODE", "TEXT", orderDesc
				.getTableShowValue(mro.getValue("IN_DIAG_CODE", 0)));// 入院诊断
		printData.setData("IN_CONDITION", "TEXT", getDescByCode(
				"ADM_CONDITION", mro.getValue("IN_CONDITION", 0)));// 入院情况
		printData.setData("OUT_DIAG_CODE1", "TEXT", orderDesc
				.getTableShowValue(mro.getValue("OUT_DIAG_CODE1", 0)));// 出院诊断
		printData.setData("CONFIRM_DATE", "TEXT", StringTool.getString(mro
				.getTimestamp("CONFIRM_DATE", 0), "yyyy年MM月dd日"));// 确诊日期
		printData.setData("INTE_DIAG_CODE", "TEXT", orderDesc
				.getTableShowValue(mro.getValue("INTE_DIAG_CODE", 0)));// 院内感染诊断
		printData.setData("GET_TIMES", "TEXT", mro.getValue("GET_TIMES", 0));// 抢救次数
		printData.setData("SUCCESS_TIMES", "TEXT", mro.getValue(
				"SUCCESS_TIMES", 0));// 成功次数
		printData.setData("CODE1_STATUS", "TEXT", getDescByCode("ADM_RETURN", mro
				.getValue("CODE1_STATUS", 0)));// 出院转归
		printData.setData("PROF_DR_CODE", "TEXT", getUserName(mro.getValue(
				"PROF_DR_CODE", 0)));// 主任医师
		printData.setData("ATTEND_DR_CODE", "TEXT", getUserName(mro.getValue(
				"ATTEND_DR_CODE", 0)));// 主治医师
		printData.setData("VS_DR_CODE", "TEXT", getUserName(mro.getValue(
				"VS_DR_CODE", 0)));// 住院医师
		/********************* 手术信息 **************************/
		// 手术只显示前三条信息
		printData.setData("OPICD1", "TEXT", op_date.getValue("OP_CODE", 0));// 手术ICD
		printData.setData("OPICD2", "TEXT", op_date.getValue("OP_CODE", 1));// 手术ICD
		printData.setData("OPICD3", "TEXT", op_date.getValue("OP_CODE", 2));// 手术ICD
		printData.setData("OP_DATE1", "TEXT", StringTool.getString(op_date
				.getTimestamp("OP_DATE", 0), "yyyy/MM/dd"));// 手术日期
		printData.setData("OP_DATE2", "TEXT", StringTool.getString(op_date
				.getTimestamp("OP_DATE", 1), "yyyy/MM/dd"));// 手术日期
		printData.setData("OP_DATE3", "TEXT", StringTool.getString(op_date
				.getTimestamp("OP_DATE", 2), "yyyy/MM/dd"));// 手术日期
		printData.setData("OP_DESC1", "TEXT", op_date.getValue("OP_DESC", 0));// 手术名称
		printData.setData("OP_DESC2", "TEXT", op_date.getValue("OP_DESC", 1));// 手术名称
		printData.setData("OP_DESC3", "TEXT", op_date.getValue("OP_DESC", 2));// 手术名称
		printData.setData("OP_DR1", "TEXT", getUserName(op_date.getValue(
				"MAIN_SUGEON", 0)));// 手术医师
		printData.setData("OP_DR2", "TEXT", getUserName(op_date.getValue(
				"MAIN_SUGEON", 1)));// 手术医师
		printData.setData("OP_DR3", "TEXT", getUserName(op_date.getValue(
				"MAIN_SUGEON", 2)));// 手术医师
		printData.setData("OP_M1", "TEXT", op_date.getValue("ANA_WAY", 0));// 麻醉方式
		printData.setData("OP_M2", "TEXT", op_date.getValue("ANA_WAY", 1));// 麻醉方式
		printData.setData("OP_M3", "TEXT", op_date.getValue("ANA_WAY", 2));// 麻醉方式
		printData.setData("HEALTH_LEVEL1", "TEXT", getDescByCode(
				"MRO_HEALTHLEVEL", op_date.getValue("HEALTH_LEVEL", 0)));// 恢复等级
		printData.setData("HEALTH_LEVEL2", "TEXT", getDescByCode(
				"MRO_HEALTHLEVEL", op_date.getValue("HEALTH_LEVEL", 1)));// 恢复等级
		printData.setData("HEALTH_LEVEL3", "TEXT", getDescByCode(
				"MRO_HEALTHLEVEL", op_date.getValue("HEALTH_LEVEL", 2)));// 恢复等级
        String bloodDesc = "";
        if (mro.getValue("BLOOD_TYPE", 0).equals("1")) {
            bloodDesc = "A";
        } else if (mro.getValue("BLOOD_TYPE", 0).equals("2")) {
            bloodDesc = "B";
        } else if (mro.getValue("BLOOD_TYPE", 0).equals("3")) {
            bloodDesc = "O";
        } else if (mro.getValue("BLOOD_TYPE", 0).equals("4")) {
            bloodDesc = "AB";
        } else if (mro.getValue("BLOOD_TYPE", 0).equals("5")) {
            bloodDesc = "不详";
        } else if (mro.getValue("BLOOD_TYPE", 0).equals("6")) {
            bloodDesc = "未做";
        }
		printData.setData("BLOOD_TYPE", "TEXT", bloodDesc);// 血型
		printData.setData("RBC", "TEXT", mro.getValue("RBC", 0));// 红细胞
		printData.setData("PLATE", "TEXT", mro.getValue("PLATE", 0));// 血小板
		printData.setData("PLASMA", "TEXT", mro.getValue("PLASMA", 0));// 血浆
		printData
				.setData("WHOLE_BLOOD", "TEXT", mro.getValue("WHOLE_BLOOD", 0));// 全血
		printData.setData("OTH_BLOOD", "TEXT", mro.getValue("OTH_BLOOD", 0));// 其他
		/********** 费用部分 *****************************/
		printData.setData("SUMTOT", "TEXT", df
				.format(mro.getDouble("CHARGE_01", 0)
						+ mro.getDouble("CHARGE_02", 0)
						+ mro.getDouble("CHARGE_03", 0)
						+ mro.getDouble("CHARGE_04", 0)
						+ mro.getDouble("CHARGE_05", 0)
						+ mro.getDouble("CHARGE_06", 0)
						+ mro.getDouble("CHARGE_07", 0)
						+ mro.getDouble("CHARGE_08", 0)
						+ mro.getDouble("CHARGE_09", 0)
						+ mro.getDouble("CHARGE_10", 0)
						+ mro.getDouble("CHARGE_11", 0)
						+ mro.getDouble("CHARGE_12", 0)
						+ mro.getDouble("CHARGE_13", 0)
						+ mro.getDouble("CHARGE_14", 0)
						+ mro.getDouble("CHARGE_15", 0)
						+ mro.getDouble("CHARGE_16", 0)
						+ mro.getDouble("CHARGE_17", 0)));
		printData.setData("CHARGE_01", "TEXT", df.format(mro.getDouble(
				"CHARGE_01", 0)));
		printData.setData("CHARGE_02", "TEXT", df.format(mro.getDouble(
				"CHARGE_02", 0)));
		printData.setData("CHARGE_03", "TEXT", df.format(mro.getDouble(
				"CHARGE_03", 0)));
		printData.setData("CHARGE_04", "TEXT", df.format(mro.getDouble(
				"CHARGE_04", 0)));
		printData.setData("CHARGE_05", "TEXT", df.format(mro.getDouble(
				"CHARGE_05", 0)));
		printData.setData("CHARGE_06", "TEXT", df.format(mro.getDouble(
				"CHARGE_06", 0)));
		printData.setData("CHARGE_07", "TEXT", df.format(mro.getDouble(
				"CHARGE_07", 0)));
		printData.setData("CHARGE_08", "TEXT", df.format(mro.getDouble(
				"CHARGE_08", 0)));
		printData.setData("CHARGE_09", "TEXT", df.format(mro.getDouble(
				"CHARGE_09", 0)));
		printData.setData("CHARGE_10", "TEXT", df.format(mro.getDouble(
				"CHARGE_10", 0)));
		printData.setData("CHARGE_11", "TEXT", df.format(mro.getDouble(
				"CHARGE_11", 0)));
		printData.setData("CHARGE_12", "TEXT", df.format(mro.getDouble(
				"CHARGE_12", 0)));
		printData.setData("CHARGE_13", "TEXT", df.format(mro.getDouble(
				"CHARGE_13", 0)));
		printData.setData("CHARGE_14", "TEXT", df.format(mro.getDouble(
				"CHARGE_14", 0)));
		printData.setData("CHARGE_15", "TEXT", df.format(mro.getDouble(
				"CHARGE_15", 0)));
		printData.setData("CHARGE_16", "TEXT", df.format(mro.getDouble(
				"CHARGE_16", 0)));
		printData.setData("CHARGE_17", "TEXT", df.format(mro.getDouble(
				"CHARGE_17", 0)));
		this.openPrintDialog("%ROOT%\\config\\prt\\MRO\\OutHospPrint.jhw",
				printData);
	}

	/**
	 * 病理诊断事件
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void pathology_Return(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("TP22_PATHOLOGY_DIAG", parm.getValue("ICD_CODE"));
		this.setValue("PATHOLOGY_DIAG_DESC", parm.getValue("ICD_CHN_DESC"));
	}

	/**
	 * 病理诊断2事件
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void pathology_Return2(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("TP22_PATHOLOGY_DIAG2", parm.getValue("ICD_CODE"));
		this.setValue("PATHOLOGY_DIAG_DESC2", parm.getValue("ICD_CHN_DESC"));
	}

	/**
	 * 病理诊断3事件
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void pathology_Return3(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("TP22_PATHOLOGY_DIAG3", parm.getValue("ICD_CODE"));
		this.setValue("PATHOLOGY_DIAG_DESC3", parm.getValue("ICD_CHN_DESC"));
	}

	/**
	 * 损伤中毒诊断事件
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void ex_Return(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("TP22_EX_RSN", parm.getValue("ICD_CODE"));
		this.setValue("EX_RSN_DESC", parm.getValue("ICD_CHN_DESC"));
	}

	/**
	 * 损伤中毒诊断2事件
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void ex_Return2(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("TP22_EX_RSN2", parm.getValue("ICD_CODE"));
		this.setValue("EX_RSN_DESC2", parm.getValue("ICD_CHN_DESC"));
	}

	/**
	 * 损伤中毒诊断3事件
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void ex_Return3(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("TP22_EX_RSN3", parm.getValue("ICD_CODE"));
		this.setValue("EX_RSN_DESC3", parm.getValue("ICD_CHN_DESC"));
	}

	/**
	 * 检核保存参数
	 * 
	 * @return boolean
	 */
	public boolean checkSaveData() {
		// 判断抢救成功次数不能大于抢救次数
		if (this.getValue("TP22_GET_TIMES") != null
				&& this.getValue("TP22_SUCCESS_TIMES") != null) {
			int getTimes = Integer.valueOf(this
					.getValueString("TP22_GET_TIMES")); // 抢救次数
			int succesTimes = Integer.valueOf(this
					.getValueString("TP22_SUCCESS_TIMES")); // 抢救成功次数
			if (succesTimes > getTimes) {
				this.messageBox_("抢救成功数不能大于抢救次数");
				return false;
			}
		}
		// 判断病理诊断是否是 A00.00000至TZZ.ZZZZZ
		String PATHOLOGY_DIAG = this.getValueString("TP22_PATHOLOGY_DIAG");
		if (PATHOLOGY_DIAG.length() > 0) {
			// modify by wangb 2016/12/23 两个临界值属于合理范围不应该被管控
			if (PATHOLOGY_DIAG.compareTo("M80000/0") < 0
					|| PATHOLOGY_DIAG.compareTo("M99890/1") > 0) {
				this.messageBox_("病理诊断码范围必须在M80000/0~M99890/1之间");
				return false;
			}
		}
		// 住院处提交
		if ("Y".equalsIgnoreCase(this.getValueString("ADMCHK_FLG"))) {
			String mroCtz = this.getValueString("MRO_CTZ");// 病案首页身份
			if (mroCtz.equals("") || mroCtz.length() == 0) {
				this.messageBox("病案首页不能为空");
				return false;
			}
			String marrige = this.getValueString("TP1_MARRIGE");// 婚姻状况
			if (marrige.equals("") || marrige.length() == 0) {
				this.messageBox("婚姻状况不能为空");
				return false;
			}
			String count = this.getValueString("TP1_INNUM");// 住院次数
			if (count.equals("") || count.length() == 0) {
				this.messageBox("住院天数不能为空");
				return false;
			}
			String birthday = this.getValueString("TP1_BIRTH_DATE");// 出生日期
			if (birthday.equals("") || birthday.length() == 0) {
				this.messageBox("出生日期");
				return false;
			}
			String folk = this.getValueString("TP1_FOLK");// 民族
			if (folk.equals("") || folk.length() == 0) {
				this.messageBox("民族不能为空");
				return false;
			}
			String nation = this.getValueString("TP1_NATION");// 国籍
			if (nation.equals("") || nation.length() == 0) {
				this.messageBox("国籍不能为空");
				return false;
			}
			String sex = this.getValueString("TP1_SEX");// 性别
			if (sex.equals("") || sex.length() == 0) {
				this.messageBox("性别不能为空");
				return false;
			}
			String address = this.getValueString("TP1_H_ADDRESS");// 户籍地址
			if (address.equals("") || address.length() == 0) {
				this.messageBox("户籍地址不能为空");
				return false;
			}
			String idNo = this.getValueString("TP1_IDNO");// 身份证号
			STAIdcardValidator Idcheck = new STAIdcardValidator();
			if (!idNo.equals("")) {
				if (!Idcheck.isValidatedAllIdcard(idNo)) {
					this.messageBox("身份证号不合法");
//					return false;
				}
			}
		}
		// 判断损伤中毒诊断是否是符合诊断范围
		TParm diag = this.getDiagGridData();
		String outCode = diag.getValue("OUT_DIAG_CODE1");
		String outStatus = diag.getValue("CODE1_STATUS");// 主诊断转归
		String pvtExt_Code = this.getValueString("TP22_EX_RSN");
		if (outCode.length() > 0) {
			if (outCode.substring(0, 1).equals("S")
					|| outCode.substring(0, 1).equals("T")) {
				if (pvtExt_Code.equals("")) {
					this.messageBox_("请填写损伤中毒外部因素");
					return false;
				}
				if (!(pvtExt_Code.substring(0, 1).compareTo("V") >= 0 && pvtExt_Code
						.substring(0, 1).compareTo("Y") <= 0)) { // 判断损伤中毒诊断是否是符合诊断范围
					this.messageBox_("损伤中毒外部因素范围必须在V00.00000~ YZZ.ZZZZZ之间");
					return false;
				}
			}
		}
		// 医师提交
		if ("Y".equalsIgnoreCase(this.getValueString("DIAGCHK_FLG"))) {
			// 本院第一例 必选
			if ("N".equalsIgnoreCase(this.getValueString("PT24BY1"))
					&& "N".equalsIgnoreCase(this.getValueString("PT24BY2"))) {
				this.messageBox_("请选择是否本院第一例");
				return false;
			}
			// 随诊 必选
			if ("N".equalsIgnoreCase(this.getValueString("PT24SZ1"))
					&& "N".equalsIgnoreCase(this.getValueString("PT24SZ2"))) {
				this.messageBox_("请选择是否随诊");
				return false;
			}
			// 示教病例 必选
			if ("N".equalsIgnoreCase(this.getValueString("PT24SJBL1"))
					&& "N".equalsIgnoreCase(this.getValueString("PT24SJBL2"))) {
				this.messageBox_("请选择是否示教病例");
				return false;
			}
			// 血型 必选
			if ("N".equalsIgnoreCase(this.getValueString("TP23XX1"))
					&& "N".equalsIgnoreCase(this.getValueString("TP23XX2"))
					&& "N".equalsIgnoreCase(this.getValueString("TP23XX3"))
					&& "N".equalsIgnoreCase(this.getValueString("TP23XX4"))
					&& "N".equalsIgnoreCase(this.getValueString("TP23XX5"))
					&& "N".equalsIgnoreCase(this.getValueString("TP23XX6"))) {
				this.messageBox_("请选择血型");
				return false;
			}
			// // 判断损伤中毒诊断是否是符合诊断范围
			// if (outStatus.length() <= 0) {
			// this.messageBox_("请填写出院主诊断的转归情况");
			// return false;
			// }
			// shibl 20120425 begin --------------------------
			if (this.getValueString("TP2_OUT_TYPE").equals("")) {
				this.messageBox_("请填写离院方式");
				return false;
			} else {
				if (this.getValueString("TP2_OUT_TYPE").equals("2")
						|| this.getValueString("TP2_OUT_TYPE").equals("3")) {
					if (this.getValueString("TP2_TRAN_HOSP").equals("")) {
						this.messageBox_("请填写外转院所");
						return false;
					}
				}
				if (this.getValueString("TP2_OUT_TYPE").equals("5")) {
                    if (StringUtil.isNullString(this.getValueString("BODY_CHECK"))) {// wanglong
                                                                                     // modify
                                                                                     // 20141030
                        this.messageBox("死亡出院尸检不能为空");
                        return false;
                    }
				}
			}
			if (this.getValueString("TP2_IN_CONDITION").equals("")) {
				this.messageBox_("请填写入院情况");
				return false;
			}
			if (this.getValueBoolean("TP2_AGN_PLAN_FLG")) {
				if (this.getValueString("TP2_AGN_PLAN_INTENTION").equals("")) {
					this.messageBox_("请填写再住院计划目的");
					return false;
				}
			} else {
				if (!this.getValueString("TP2_AGN_PLAN_INTENTION").equals("")) {
					this.messageBox_("无计划不能填写再住院计划目的");
					return false;
				}
			}
			if (this.getValueBoolean("ALLEGIC_1")) {
				if (!this.getValueString("TP22_ALLEGIC").equals("")) {
					this.messageBox_("请清空过敏药物记录");
					return false;
				}
			}
			if (this.getValueBoolean("ALLEGIC_2")) {
				if (this.getValueString("TP22_ALLEGIC").equals("")) {
					this.messageBox_("请填写过敏药物记录");
					return false;
				}
			}
			// shibl 20120425 end --------------------------
			// 颅脑损伤患者入院昏迷时间
			if (0 > this.getValueInt("TP2_BE_IN_D")
					|| this.getValueInt("TP2_BE_IN_D") > 31) {
				this.messageBox("入院前昏迷时间天数区间为0~31");
				return false;
			}
			if (0 > this.getValueInt("TP2_BE_IN_H")
					|| this.getValueInt("TP2_BE_IN_H") > 24) {
				this.messageBox("入院前昏迷时间小时区间为0~24");
				return false;
			}
			if (0 > this.getValueInt("TP2_BE_IN_M")
					|| this.getValueInt("TP2_BE_IN_M") > 60) {
				this.messageBox("入院前昏迷时间分钟区间为0~60");
				return false;
			}
			if (0 > this.getValueInt("TP2_AF_IN_D")
					|| this.getValueInt("TP2_AF_IN_D") > 31) {
				this.messageBox("入院后昏迷时间天数区间为0~31");
				return false;
			}
			if (0 > this.getValueInt("TP2_AF_IN_H")
					|| this.getValueInt("TP2_AF_IN_H") > 24) {
				this.messageBox("入院后昏迷时间小时区间为0~24");
				return false;
			}
			if (0 > this.getValueInt("TP2_AF_IN_M")
					|| this.getValueInt("TP2_AF_IN_M") > 60) {
				this.messageBox("入院后昏迷时间分钟区间为0~60");
				return false;
			}
			if (!getValueString("TP2_CONFIRM_DATE").equals("")) {
				Timestamp indate = (Timestamp) getValue("TP2_IN_DATE");
				Timestamp confrimDate = (Timestamp) getValue("TP2_CONFIRM_DATE");
				if (indate.getTime() > confrimDate.getTime()) {
					this.messageBox("入院时间不能大于确诊时间");
					return false;
				}
			} else {
				this.messageBox("确诊时间不能为空");
				return false;
			}
			if (!getValueString("TP2_OUT_DATE").equals("")) {
				Timestamp outdate = (Timestamp) getValue("TP2_OUT_DATE");
				Timestamp indate = (Timestamp) getValue("TP2_IN_DATE");
				if (indate.getTime() > outdate.getTime()) {
					this.messageBox("入院时间不能大于出院时间");
					return false;
				}
				Timestamp confrimDate = (Timestamp) getValue("TP2_CONFIRM_DATE");
				if (outdate.getTime() < confrimDate.getTime()) {
					this.messageBox("出院时间不能小于确诊时间");
					return false;
				}
			} else {
				this.messageBox("出院时间不能为空");
				return false;
			}
			if (this.getValueString("TP22_DIRECTOR_DR_CODE").equals("")) {
				this.messageBox("科主任不能为空");
				return false;
			}
			if (this.getValueString("TP22_PROF_DR_CODE").equals("")) {
				this.messageBox("主(副主)任医师不能为空");
				return false;
			}
			if (this.getValueString("TP22_ATTEND_DR_CODE").equals("")) {
				this.messageBox("主治医师不能为空");
				return false;
			}
			if (this.getValue("TP2_VS_DR_CODE").equals("")) {
				this.messageBox("住院医师不能为空");
				return false;
			}
			if (this.getValueString("TP2_VS_NURSE_CODE").equals("")) {
				this.messageBox("责任护士不能为空");
				return false;
			}
			if (this.getValueString("TP4_CTRL_DR").equals("")) {
				this.messageBox("质控医师不能为空");
				return false;
			}
			if (this.getValueString("TP4_CTRL_NURSE").equals("")) {
				this.messageBox("质控护士不能为空");
				return false;
			}
			if (this.getValueString("TP4_CTRL_DATE").equals("")) {
				this.messageBox("质控日期不能为空");
				return false;
			} else {
				Timestamp indate = (Timestamp) getValue("TP2_IN_DATE");
				Timestamp ctlrDate = (Timestamp) getValue("TP4_CTRL_DATE");
				if (indate.getTime() > ctlrDate.getTime()) {
					this.messageBox("入院时间不能大于质控时间");
					return false;
				}
			}
			
			if (!checkOpData()) {
				return false;
			}
		}
		// 财务提交
		if ("Y".equalsIgnoreCase(this.getValueString("BILCHK_FLG"))) {

		}
		// 病案室提交
		if ("Y".equalsIgnoreCase(this.getValueString("QTYCHK_FLG"))) {
			if ("N".equalsIgnoreCase(this.getValueString("TP4BA1"))
					&& "N".equalsIgnoreCase(this.getValueString("TP4BA2"))
					&& "N".equalsIgnoreCase(this.getValueString("TP4BA3"))) {
				this.messageBox_("请选择病案质量");
				return false;
			}
			if (this.getValue("TP4_ENCODER").equals("")) {
				this.messageBox("编码员不能为空");
				return false;
			}
		}
		
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		if(table.getRowCount() <= 0)
			return true;
		//判断是否需要填写外院医师备注
		if(checkIsOutDocRemark()){
			this.messageBox("所选手术医师存在外院医师，请填写外院医师备注！");
			return false;
		}
		
		return true;
	}

	/**
	 * 判断是否需要填写外院医师备注
	 * @return 
	 */
	public boolean checkIsOutDocRemark() {//modify by guoy 20150811
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		int rows = table.getRowCount();
		for(int i = 0;i < rows; i++){
			String docRemark = (String) table.getValueAt(i, 10);
			String docId = (String) table.getValueAt(i, 9);
			String sql = " SELECT IS_OUT_FLG FROM SYS_OPERATOR WHERE USER_ID = '" + docId + "' ";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			String outFlg = parm.getValue("IS_OUT_FLG",0);
			if("Y".equals(outFlg)){
				if("".equals(docRemark)){
					return true;
				}
			}
		}
		
		return false;
		/*
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		ArrayList<Integer> rows = new ArrayList<Integer>(table.getRowCount());//初始化表格行号集合
//		ArrayList<Integer> rows = new ArrayList<Integer>();//初始化表格行号集合
		String rowStr = "";
		int row = 0;
		for (int i = 0; i < table.getRowCount(); i++) {//为表格行号集合添加数据  表格有4行  则填充0 1 2 3
			rows.add(i);
		}
		Map<String, String> map = table.getLockCellMap();
		Set set = map.entrySet();
        for (Iterator<Entry<String, String>> it = set.iterator(); it.hasNext();) {
            Map.Entry entry = it.next();
            String rowAndCol = (String) entry.getKey();
            rowStr = rowAndCol.substring(0, rowAndCol.indexOf(":"));
            if(rowStr != null && !"".equals(rowStr))
            	row = Integer.parseInt(rowStr);//获得“外院医师备注”锁定的行号
            for (int i = 0; i < rows.size(); i++) {//从行号集合中删除“外院医师备注”锁定的行
				if(row == rows.get(i)){
					rows.remove(i);
				}
			}
        }
        for (int i = 0; i < rows.size(); i++) {
    		if(table.getValueAt(rows.get(i), 10) == null || table.getValueAt(rows.get(i), 10).toString().length() <= 0){
	        	return true;
	        }
		}
		return false;
		*/
	}

	/**
	 * 检查手术信息
	 * 
	 * @return boolean
	 */
	private boolean checkOpData() {
		TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
		opTable.acceptText();
		boolean flg = false;
		for (int i = 0; i < opTable.getRowCount(); i++) {
			if (opTable.getValueAt(i, 0).toString().equals("Y")) {
				flg = true;
			}
		}
		while (opTable.getRowCount() > 0) {
			if (opTable.getItemString(opTable.getRowCount() - 1, 2).trim()
					.equals("")
					&& opTable.getItemString(opTable.getRowCount() - 1, 3)
							.trim().equals("")) {
				opTable.removeRow(opTable.getRowCount() - 1);// 删除空行 add by
				// wanglong
				// 20121109
				continue;
			} else {
				break;
			}
		}
		if (!flg && opTable.getRowCount() > 0) {
			this.messageBox_("请选择一条手术编码作为主手术");
			return flg;
		}
		for (int i = 0; i < opTable.getRowCount(); i++) {
			if (opTable.getValueAt(i, 2) == null) {
				this.messageBox_("请填写手术ICD时间");
				return false;
			} else {
				Timestamp opDate = opTable.getItemTimestamp(i, "OP_DATE");
				Timestamp outdate = (Timestamp) getValue("TP2_OUT_DATE");
				Timestamp indate = StringTool.rollDate((Timestamp) getValue("TP2_IN_DATE"),-1);
				if (indate.getTime() > opDate.getTime()) {
					this.messageBox_("入院日期-1天不能大于手术日期");
					return false;
				}
				if (opDate.getTime() > outdate.getTime()) {
					this.messageBox_("手术日期不能大于出院日期");
					return false;
				}
			}
			if (opTable.getValueAt(i, 3) != null && "".equals(opTable.getValueAt(i, 3).toString().trim())) {
				this.messageBox_("请填写手术ICD编码");
				return false;
			}
			if (opTable.getValueAt(i, 9) != null && "".equals(opTable.getValueAt(i, 9).toString().trim())) {
				this.messageBox_("请选择手术医师");
				return false;
			}
			if (opTable.getValueAt(i, 14) != null && "".equals(opTable.getValueAt(i, 14).toString().trim())) {
				this.messageBox_("请填写麻醉方式");
				return false;
			}
			if (opTable.getValueAt(i, 15) != null && "".equals(opTable.getValueAt(i, 15).toString().trim())) {
				this.messageBox_("请填写愈合等级");
				return false;
			}
			if (opTable.getValueAt(i, 7)!= null && !"".equals(opTable.getValueAt(i, 7).toString().trim())) {
				try {
					Integer.parseInt(opTable.getValueAt(i, 7).toString().trim());
				} catch (Exception e) {
					this.messageBox("请填写正确的手术持续时间（小时）");
					return false;
				}
			}
			if (opTable.getValueAt(i, 8)!= null && !"".equals(opTable.getValueAt(i, 8).toString().trim())) {
				try {
					Integer.parseInt(opTable.getValueAt(i, 8).toString().trim());
				} catch (Exception e) {
					this.messageBox("请填写正确的手术持续时间（分钟）");
					return false;
				}
			}
		}
		if ((getCheckbox("OPE_TYPE_CODE1").isSelected() || getCheckbox(
				"OPE_TYPE_CODE2").isSelected())
				&& opTable.getRowCount() <= 0) {
			this.messageBox_("未做手术患者不能选择手术患者类型");
			return false;
		} else if (opTable.getRowCount() > 0
				&& !getCheckbox("OPE_TYPE_CODE1").isSelected()
				&& !getCheckbox("OPE_TYPE_CODE2").isSelected()) {
			this.messageBox_("已做手术患者必须选择手术患者类型");
			return false;
		}
		return true;
	}

	/**
	 * 计算随诊日期
	 */
	public void setACCOMP_DATE() {
		// 计算随诊截止日期
		if (this.getValue("TP2_OUT_DATE") != null) { // 判断出院日期是否为空，随诊截止日期是以出院日期为起始开始计算的（需要询问一下）
			int s_year = this.getValueInt("TP24_ACCOMPANY_YEAR"); // 随诊年数
			int s_month = this.getValueInt("TP24_ACCOMPANY_MONTH"); // 月数
			int s_week = this.getValueInt("TP24_ACCOMPANY_WEEK"); // 周数
			Timestamp accomp_date = StringTool.getTimestamp(this.getValue(
					"TP2_OUT_DATE").toString(), "yyyy-MM-dd"); // 获取出院日期
			if (s_week > 0) { // 出院日期加上周数
				accomp_date = StringTool.rollDate(accomp_date,
						(long) (7 * s_week));
			}
			if (s_month > 0) { // 如果随诊月数大于零则将出院日期加上月数 计算出随诊截止日期
				Calendar cal = Calendar.getInstance();
				cal.setTime(accomp_date);
				cal.add(cal.MONTH, s_month);
				accomp_date = new Timestamp(cal.getTimeInMillis()); // 增加月数后的
				// 随诊截止日期
			}
			if (s_year > 0) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(accomp_date);
				cal.add(cal.YEAR, s_year);
				accomp_date = new Timestamp(cal.getTimeInMillis()); // 增加年数后的
				// 随诊截止日期
			}
			if (s_week > 0 || s_month > 0 || s_year > 0)
				this.setValue("TP24_ACCOMP_DATE", accomp_date);
			else
				this.setValue("TP24_ACCOMP_DATE", new TNull(Timestamp.class));
		} else
			// 没有出院日期，随诊日期就为空
			this.setValue("TP24_ACCOMP_DATE", new TNull(Timestamp.class));
	}

	/**
	 * 转入过敏信息
	 */
	public void onIntoAllecData() {
		// 病患药物过敏信息
		String durgallec = "";
		TParm admDurg = MROTool.getInstance().getDrugAllErgy(MR_NO);
		if (admDurg.getCount() <= 0) {
			getRDBtn("ALLEGIC_1").setSelected(true);
			getRDBtn("ALLEGIC_2").setSelected(false);
			return;
		}
		List<String> admDurgList = new ArrayList<String>();
		for (int i = 0; i < admDurg.getCount(); i++) {
			if (!admDurgList.contains(admDurg.getValue("ALLERGY_NAME", i))) {
				admDurgList.add(admDurg.getValue("ALLERGY_NAME", i));
				if (!durgallec.equals(""))
					durgallec += ",";				
			    // add by wangqing  20180116 start
				// 医生站填写患者过敏史，只针对过敏名称填写'非本院用药'项目时，在过敏情况列填写药品名称。
				// 病案首页获取时判断过敏名称为’非本院用药'时，首页的药物过敏内容自动带入对应的‘过敏情况’列内容。
				if(admDurg.getValue("ALLERGY_NAME", i).equals("非本院用药")){
					durgallec += admDurg.getValue("ALLERGY_NOTE", i);
				}else{
					durgallec += admDurg.getValue("ALLERGY_NAME", i);
				}
				// add by wangqing  20180116 end				
			}
		}
		this.setValue("TP22_ALLEGIC", durgallec);// 过敏药物		
		
		// add by wangb 2017/06/27 自动带出过敏有无
		if (StringUtils.isEmpty(durgallec)) {
			getRDBtn("ALLEGIC_1").setSelected(true);
			getRDBtn("ALLEGIC_2").setSelected(false);
		} else {
			getRDBtn("ALLEGIC_1").setSelected(false);
			getRDBtn("ALLEGIC_2").setSelected(true);
		}
	}

	/**
	 * 转入医师信息
	 */
	public void onIntoDrData() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		// 病患住院基本信息 adm_inp
		TParm inHospInfo = ADMTool.getInstance().getADM_INFO(parm);
		if (inHospInfo.getErrCode() < 0) {
			err("ERR:" + inHospInfo.getErrCode() + inHospInfo.getErrText()
					+ inHospInfo.getErrName());
			return;
		}
		this.setValue("TP22_DIRECTOR_DR_CODE", inHospInfo.getValue(
				"DIRECTOR_DR_CODE", 0));// 科主任
		this.setValue("TP22_ATTEND_DR_CODE", inHospInfo.getValue(
				"ATTEND_DR_CODE", 0));// 主治医师
		this.setValue("TP2_VS_DR_CODE", inHospInfo.getValue("VS_DR_CODE", 0));// 经治医师
		this.setValue("TP2_VS_NURSE_CODE", inHospInfo.getValue("VS_NURSE_CODE",
				0));// 责任护士
	}

	/**
	 * 转入诊断信息
	 */
	public void onIntoDiagData() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
			if (JOptionPane.showConfirmDialog(null, "将会清除首页所有诊断再转入临床诊断信息！是否执行？", "信息",
				JOptionPane.YES_NO_OPTION) == 0) {
			TParm del = new TParm(this.getDBTool().update(getDelMroDiagSql(CASE_NO)));
			// 诊断Grid绑定
			TTable diagTable = (TTable) this.getComponent("TP21_Table2");
			if (diagTable.getParmValue().getCount() > 0)
				diagTable.setParmValue(null);
			//诊断记录
			TParm admDiag = new TParm(this.getDBTool().select(
						"SELECT 'Z' AS EXEC,A.IO_TYPE AS TYPE,B.ICD_CHN_DESC AS NAME,A.ICD_CODE AS CODE,A.MAINDIAG_FLG AS MAIN,A.ICD_TYPE AS KIND," +
						" '' AS STATUS,'' AS IN_PAT_CONDITION,'' AS ADDITIONAL,'' AS ADDITIONAL_DESC,A.SEQ_NO,A.DESCRIPTION AS REMARK" +
						" FROM ADM_INPDIAG A,SYS_DIAGNOSIS B WHERE CASE_NO = '"+this.CASE_NO+"' AND A.ICD_CODE = B.ICD_CODE  AND A.IO_TYPE<>'Z'  ORDER BY IO_TYPE ASC, SEQ_NO "));
			if (admDiag.getCount() <= 0)
				return;
			diagTable.setParmValue(admDiag);
			diagTable.addRow();
		}
	}

	/**
	 * 转入诊断-删除首页诊断sql-duzhw
	 */
	public String getDelMroDiagSql(String caseNo){
		String sql = "delete from MRO_RECORD_DIAG where case_no = '"+caseNo+"'";
		return sql;
	}

	/**
	 * 转入手术信息
	 */
	//2013-4-27修改
	public void onIntoOPData() {
		// 获取手术信息
		TParm opeData = OPETool.getInstance().intoOPEDataForMRO(CASE_NO);
		if (opeData.getCount() <= 0) {
			return;
		}
		// 手术Grid绑定
		TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
		opTable.removeRowAll();
		TParm parm = new TParm();
		for (int i = 0; i < opeData.getCount(); i++) {
			int opHour = (int)((StringTool.getTimestamp(StringTool.getString(opeData.getTimestamp("OP_END_DATE", i), "ddHHmm"),"ddHHmm").getTime() - 
					StringTool.getTimestamp(StringTool.getString(opeData.getTimestamp("OP_DATE", i),"ddHHmm"), "ddHHmm").getTime()) / 1000.0D / 60.0D / 60.0D);
			int opMinute = (int)((StringTool.getTimestamp(StringTool.getString(opeData.getTimestamp("OP_END_DATE", i), "ddHHmm"),"ddHHmm").getTime() - 
					StringTool.getTimestamp(StringTool.getString(opeData.getTimestamp("OP_DATE", i),"ddHHmm"), "ddHHmm").getTime()) / 1000.0D / 60.0D) % 60;
			parm.setData("MAIN_FLG", i, "N");
			parm.setData("AGN_FLG", i, "Y");
			parm.setData("SEQ_NO", i, i + 1);
			parm.setData("OP_DATE", i, opeData.getData("OP_DATE", i));
			parm.setData("OP_CODE", i, opeData.getData("OP_CODE", i));
			parm.setData("OP_DESC", i, opeData.getData("OP_DESC", i));
			parm.setData("OP_LEVEL", i, opeData.getData("OP_LEVEL", i));
			parm.setData("OPE_TIME_HOUR", i, opHour);
			parm.setData("OPE_TIME_MINUTE", i, opMinute);
			parm.setData("MAIN_SUGEON", i, opeData.getData("MAIN_SUGEON", i));
			parm.setData("AST_DR1", i, opeData.getData("AST_DR1", i));
			parm.setData("AST_DR2", i, opeData.getData("AST_DR2", i));
			parm.setData("HEALTH_LEVEL", i, opeData.getData("HEALTH_LEVEL", i));
			parm.setData("ANA_WAY", i, opeData.getData("ANA_WAY", i));
			parm.setData("ANA_DR", i, opeData.getData("ANA_DR", i));
			parm.setData("NNIS_CODE", i, opeData.getData("NNIS_CODE", i));
		}
//		opTable.acceptText();
		opTable.setParmValue(parm);
	}

		
	

	/**
	 * 输血信息转入
	 */
	public void onIntoBMSData() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("MR_NO", MR_NO);
		parm.setData("IPD_NO", this.getValueString("TP1_IPD_NO"));
		TParm bms = BMSTool.getInstance().getApplyInfo(parm);
		if (bms.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		this.setValue("TP23_RBC", bms.getValue("RBC"));
		this.setValue("TP23_PLATE", bms.getValue("PLATE"));
		this.setValue("TP23_PLASMA", bms.getValue("PLASMA"));
		this.setValue("TP23_WHOLE_BLOOD", bms.getValue("WHOLE_BLOOD"));
		this.setValue("TP23_OTH_BLOOD", bms.getValue("OTH_BLOOD"));
		if ("1".equals(bms.getValue("TRANS_REACTION"))) {
			this.setValue("PT23SX1", true);
		} else if ("2".equals(bms.getValue("TRANS_REACTION"))) {
			this.setValue("PT23SX2", true);
		}
		
		// add by wangb 2018/1/8 根据LIS血型鉴定结果同步病患信息表血型 START
		BMSTool.getInstance().updatePatBloodByLisData(MR_NO);
		// add by wangb 2018/1/8 根据LIS血型鉴定结果同步病患信息表血型 END
		
		Pat pat = Pat.onQueryByMrNo(MR_NO);
		if ("A".equals(pat.getBloodType())) {
			this.setValue("TP23XX1", true);
		} else if ("B".equals(pat.getBloodType())) {
			this.setValue("TP23XX2", true);
		} else if ("O".equals(pat.getBloodType())) {
			this.setValue("TP23XX3", true);
		} else if ("AB".equals(pat.getBloodType())) {
			this.setValue("TP23XX4", true);
		} else if ("".equals(pat.getBloodType())) {
			this.setValue("TP23XX6", true);
		} else {
			this.setValue("TP23XX5", true);
		}
		// =======pangben modify 20110629 start RH血型显示
		if ("+".equals(pat.getBloodRHType())) {
			this.setValue("PT23RH2", true);
		} else if ("-".equals(pat.getBloodRHType())) {
			this.setValue("PT23RH1", true);
		} else if ("".equals(pat.getBloodRHType())) {
			this.setValue("PT23RH4", true);
		} else {
			this.setValue("PT23RH3", true);
		}
		// =======pangben modify 20110629 stop
		setSXFYByBMSData(cmpNames);
	}

	/**
	 * 调用新生儿 免疫界面
	 */
	public void onChild() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("MR_NO", MR_NO);
		parm.setData("IPD_NO", this.getValueString("TP1_IPD_NO"));
		this.openDialog("%ROOT%\\config\\adm\\ADMChildImmunity.x", parm);
	}

	/**
	 * 页签选择第三个时执行汇总方法 ==========pangben modify 20110623
	 */
	public void onChangeStart() {
		TTabbedPane tP = (TTabbedPane) this.getComponent("tTabbedPane_0");
		switch (tP.getSelectedIndex()) {
		case 1:
			TTable table = (TTable) this.getComponent("TP23_OP_Table");
			if(table.getRowCount() <= 0 && isFirst){
				this.OPTableBind(MR_NO,CASE_NO);
				isFirst = false;
			}
			break;
		case 2:
			// onIntoDr(); // 汇入医生信息
			onFinance(); // 财务汇入
			break;
		}
	}

	/**
	 * 生成研究病例 ===============pangben modify 20110710
	 */
	public void onCase() {
		// shibl 20120807 提示用户是否生成研究病历 防止误点
		if (JOptionPane.showConfirmDialog(null, "是否生成研究病历?", "提示信息",
				JOptionPane.YES_NO_OPTION) != 0) {
			return;
		}
		boolean istrue = false;
		StringBuffer value = new StringBuffer();
		// 5.检核电子病历内容缺失
		String sql = QUERY_EMR_SQL.replaceFirst("#", " FILE_PATH , FILE_NAME ")
				.replaceFirst("#", CASE_NO);
		TParm queryParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (queryParm.getCount() <= 0) {
			this.messageBox("没有需要生成的数据");
			return;
		}
		for (int i = 0; i < queryParm.getCount(); i++) {
			TParm tagParm = new TParm();
			tagParm.setData("FILE_PATH", queryParm.getValue("FILE_PATH", i));
			tagParm.setData("FILE_NAME", queryParm.getValue("FILE_NAME", i));

			if (GetWordValue.getInstance().saveTestWord(tagParm)) {
				istrue = true;
			} else {
				value.append(queryParm.getValue("FILE_NAME", i)).append(",\n");
			}
		}
		if (istrue)
			this.messageBox("研究病例生成完毕");
		else {
			this.messageBox("研究病例:\n"
					+ value.substring(0, value.lastIndexOf(",")) + "\n生成失败");
		}
	}

	/**
	 * 离院方式监听
	 */
	public void outTypeSelect() {
		if (this.getValue("TP2_OUT_TYPE").equals("2")
				|| this.getValue("TP2_OUT_TYPE").equals("3")) {
			callFunction("UI|TP2_TRAN_HOSP|setEnabled", true);
		} else {
			callFunction("UI|TP2_TRAN_HOSP|setEnabled", false);
		}
		this.setValue("TP2_TRAN_HOSP", "");
		this.setValue("TRAN_HOSP_OTHER", "");
		this.onSelectTran();
	}

	/**
	 * 得到TCheckBox控件
	 * 
	 * @param tag
	 * @return
	 */
	private TCheckBox getCheckbox(String tag) {
		return (TCheckBox) this.getComponent(tag);
		
	}
	
	/**
	 * 添加0
	 * 
	 * @param str
	 * @return
	 */
	private String getFormatString(String str) {
		String datastr = "";
		if (str.length() > 0 && str.length() < 2) {
			for (int i = 0; i < (2 - str.length()); i++) {
				datastr = "0" + datastr;
			}
		}
		return datastr + str;
	}

	/**
	 * 多转科科室中间加"|"
	 * 
	 * @return
	 */
	private String getTranDept() {
		String str = "";
		TParm result = new TParm();
		TParm inparm = new TParm();
		inparm.setData("CASE_NO", CASE_NO);
		TParm trandept = MROTool.getInstance().getTranDept(inparm);
		for (int i = 0; i < trandept.getCount(); i++) {
			if (str.trim().length() > 0) {
				str += "|";
			}
			str += trandept.getValue("TRAN_DEPT", i);
		}
		return str;
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
	 * 出院日期更改时，联动更改住院天数
	 */
	public boolean onChoiceDSDate() {// modify by wanglong 20121029
		Timestamp inDate = (Timestamp) this.getValue("TP2_IN_DATE");// 入院时间
		Timestamp dsDate = (Timestamp) this.getValue("TP2_OUT_DATE");// 出院时间
		if (inDate == null) {
			messageBox("入院时间设置不正确");
			return false;
		}
		if (dsDate == null) {// 不填出院时间，住院天数则算到当前时间
			dsDate = new Timestamp(new Date().getTime());
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 格式化日期类
		String strInDate = sdf.format(inDate);
		String strDsDate = sdf.format(dsDate);
		inDate = java.sql.Timestamp.valueOf(strInDate + " 00:00:00.000");
		dsDate = java.sql.Timestamp.valueOf(strDsDate + " 00:00:00.000");
		int stayDays = StringTool.getDateDiffer(dsDate, inDate);// 计算住院天数
		if (stayDays < 0) {
			this.messageBox("出院时间不能早于入院时间");
			return false;
		} else if (stayDays == 0) {
			stayDays = 1;
			this.setValue("TP2_REAL_STAY_DAYS", stayDays + "");// 实际住院天数
			return true;
		} else {
			this.setValue("TP2_REAL_STAY_DAYS", stayDays + "");// 实际住院天数
			return true;
		}
	}

	/**
	 * 出院日期是否变化 shibl 20130108 add
	 * 
	 * @return
	 */
	private String IsModifydsDate(Timestamp dsDate) {
		String flg = "N";
		String sql = "SELECT OUT_DATE FROM MRO_RECORD WHERE CASE_NO='"
				+ this.CASE_NO + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		Timestamp olddsDate = result.getTimestamp("OUT_DATE", 0);
	    if(!StringTool.getString(olddsDate, "yyyyMMddHHmmss").equals(StringTool.getString(dsDate, "yyyyMMddHHmmss"))){
			flg = "Y";
		}
		return flg;
	}
	
	/**
	 * 通过输血信息设置输血反应的值
	 * @param cmpNames 组件名称数组
	 * @param isInit 是否为初始化
	 */
	private void setSXFYByBMSData(String[] cmpNames){
		//声明标记，记录输血信息值是否大于0或等于0，小于0未做出提示
		boolean flag = false;
		//循环判断每一项输血信息的值是否大于0 如果大于0，输血信息标记设置为true
		for (int i = 0; i < cmpNames.length; i++) {
			if(Integer.parseInt(this.getValue(cmpNames[i]).toString()) > 0) flag = true;
		}
		//判断输血信息标记设置输血反应的默认选项和选择范围
		if(flag){
			callFunction("UI|PT23SX1|setEnabled", true);
			callFunction("UI|PT23SX2|setEnabled", true);
			callFunction("UI|PT23SX3|setEnabled", false);
			//如果输血信息大于0，但是医生输血反应所选项为“有”，则不设置输血反应为“无”
			if(!getRDBtn("PT23SX1").isSelected())
				getRDBtn("PT23SX2").setSelected(true);
		}else{
			callFunction("UI|PT23SX1|setEnabled", false);
			callFunction("UI|PT23SX2|setEnabled", false);
			callFunction("UI|PT23SX3|setEnabled", true);
			getRDBtn("PT23SX3").setSelected(true);
		}
	}
	
	/**
	 * 给组件添加焦点监听
	 * @param cmpNames 组件名称数据
	 */
	private void setCmpFocusListener(final String[] cmpNames){
		//如果组件名称数组为空或者无数据，直接返回
		if(null == cmpNames || cmpNames.length <= 0) return;
		//循环为组件添加焦点监听事件
		for (int i = 0; i < cmpNames.length; i++) {
			((JComponent) this.getComponent(cmpNames[i])).addFocusListener(new FocusListener() {

				public void focusGained(FocusEvent e) {
					// TODO Auto-generated method stub
					setSXFYByBMSData(cmpNames);
				}

				public void focusLost(FocusEvent e) {
					// TODO Auto-generated method stub
				}
			});
		}
	}
	
	/**
	 * 汇入ICU信息
	 */
	public void onIntoICUData(){
		TTable icuTable = (TTable) this.getComponent("TP25_ICU_Table");
		TParm icuParm = MROTool.getInstance().getICUParm(CASE_NO);
		TParm tableParm = new TParm();
		for (int i = 0; i < 5; i++) {
			String deptCode = icuParm.getValue("DEPT_CODE", i);
//			String deptDesc = MRORecordTool.getInstance().getRoomDesc(deptCode);
			tableParm.setData("ICU_ROOM", i, deptCode);
			tableParm.setData("IN_DATE", i, icuParm.getData("IN_DATE", i));
			tableParm.setData("OUT_DATE", i, icuParm.getData("OUT_DATE", i));
		}
		int row = icuParm.getCount();
		if(row < 0)
			row = 0;
		for (int i = row; i < 5; i++) {
			tableParm.setData("ICU_ROOM", i, "");
			tableParm.setData("IN_DATE", i, new TNull(Timestamp.class));
			tableParm.setData("OUT_DATE", i, new TNull(Timestamp.class));
		}
		icuTable.setParmValue(tableParm);
	}
	/**
	 * 调用病患日志
	 */
	public  void onPatlog(){
		TParm parm=new TParm();
		parm.setData("MR_NO", this.MR_NO);
		parm.setData("IN_DATE", this.getValue("TP2_IN_DATE"));
		openDialog("%ROOT%\\config\\sys\\SYSPatLogQuery.x",parm);
	}
	/**
	 * 校验是否存在病患日志
	 * @return
	 */
	public boolean onCheckPatlog(){
		Timestamp indate=(Timestamp)getValue("TP2_IN_DATE");
		if(indate==null){
			return false;
		}
		String sql="SELECT MODI_ITEM FROM SYS_PATLOG WHERE MR_NO='"+this.MR_NO+
		"' AND OPT_DATE>=TO_DATE('"+StringTool.getString(indate, "yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS')";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getErrCode()<0||parm.getCount()<=0){
			return false;
		}
		return true;
	}
	
    /**
     * 手术套餐
     */
    public void onOpPack() {//wanglong add 20150225
        TParm parm = new TParm();
        parm.setData("DEPT_OR_DR", 2);
        parm.setData("DEPT_CODE", Operator.getDept());
        parm.setData("USER_ID", Operator.getID());
        parm.addListener("INSERT_TABLE", this, "onQuoteSheetList");
        TWindow window = (TWindow) this.openWindow("%ROOT%\\config\\ope\\OPEPackICD.x", parm, true);
//        window.setX((ImageTool.getScreenWidth() - window.getWidth())/2);
//        window.setY((ImageTool.getScreenHeight() - window.getHeight())/2);
        window.setVisible(true);
        
    }
    
    /**
     * 套餐赋值
     * 
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onQuoteSheetList(Object obj) {//wanglong add 20150225
        boolean falg = true;
        if (obj != null) {
            List orderList = (ArrayList) obj;
            Iterator iter = orderList.iterator();
            TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
            TParm parmValue = opTable.getParmValue();
            if (parmValue == null) {
                parmValue = new TParm();
            }
            Timestamp now = SystemTool.getInstance().getDate();
            while (iter.hasNext()) {
                TParm temp = (TParm) iter.next();
                parmValue.addData("CASE_NO", "");
                parmValue.addData("MR_NO", "");
                parmValue.addData("IPD_NO", "");
                parmValue.addData("MAIN_FLG", "N");
                parmValue.addData("AGN_FLG", "N");
                parmValue.addData("OP_DATE", new TNull(Timestamp.class));
                parmValue.addData("OP_CODE", temp.getValue("OP_CODE"));
                parmValue.addData("OP_DESC", temp.getValue("OP_DESC"));
                parmValue.addData("OP_LEVEL", StringUtil.getDesc("SYS_OPERATIONICD",
                                                                 "OPE_LEVEL",
                                                                 "OPERATION_ICD='"
                                                                         + temp.getValue("OP_CODE")
                                                                         + "'"));
                parmValue.addData("OP_REMARK", "");
                parmValue.addData("OPE_SITE", "");
                parmValue.addData("OPE_TIME", "");
                parmValue.addData("OPE_TIME_HOUR", "");
                parmValue.addData("OPE_TIME_MINUTE", "");
                parmValue.addData("MAIN_SUGEON", "");
                parmValue.addData("MAIN_SUGEON_REMARK", "");
                parmValue.addData("AST_DR1", "");
                parmValue.addData("AST_DR2", "");
                parmValue.addData("HEALTH_LEVEL", "");
                parmValue.addData("ANA_WAY", "");
                parmValue.addData("ANA_DR", "");
                parmValue.addData("ANA_LEVEL", "");
                parmValue.addData("NNIS_CODE", "");
                parmValue.addData("SEQ_NO",
                                  parmValue.getInt("SEQ_NO", parmValue.getCount("SEQ_NO") - 1) + 1);
                parmValue.addData("OPT_USER", Operator.getID());
                parmValue.addData("OPT_DATE", now);
                parmValue.addData("OPT_TERM", Operator.getIP());
            }
            parmValue.setCount(parmValue.getCount("SEQ_NO"));
            opTable.setParmValue(parmValue);
        }
        return falg;
    }
    
    /**
     * 手术table中某行记录上移一行
     */
    public void onUpOPRecord() {//wanglong add 20150225
        TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
        if (opTable.getRowCount() <= 0) {
            return;
        }
        TParm parmValue = opTable.getParmValue();
        int row = opTable.getSelectedRow();
        if (row == 0) {
            return;
        }
        int preRow = row - 1;
        TParm rowParm = parmValue.getRow(row);
        parmValue.removeRow(row);
        parmValue.insertRow(preRow);
        String[] names = parmValue.getNames();
        for (int i = 0; i < names.length; i++) {
            parmValue.setData(names[i], preRow, rowParm.getData(names[i]));
        }
        parmValue.setCount(parmValue.getCount(names[0]));
        opTable.setParmValue(parmValue);
        opTable.setSelectedRow(preRow);
    }
    
    /**
     * 手术table中某行记录下移一行
     */
    public void onDownOPRecord() {//wanglong add 20150225
        TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
        if (opTable.getRowCount() <= 0) {
            return;
        }
        TParm parmValue = opTable.getParmValue();
        int row = opTable.getSelectedRow();
        if (row == opTable.getRowCount() - 1) {
            return;
        }
        int nextRow = row + 1;
        TParm rowParm = parmValue.getRow(row);
        parmValue.removeRow(row);
        parmValue.insertRow(nextRow);
        String[] names = parmValue.getNames();
        for (int i = 0; i < names.length; i++) {
            parmValue.setData(names[i], nextRow, rowParm.getData(names[i]));
        }
        parmValue.setCount(parmValue.getCount(names[0]));
        opTable.setParmValue(parmValue);
        opTable.setSelectedRow(nextRow);
    }
	
    /**
     * add by wukai on 20170406
     * 诊断信息上移
     */
    public void onUpDiagRecord() {
    	 TTable table = (TTable) this.getComponent("TP21_Table2");
         if (table.getRowCount() <= 1) {
             return;
         }
         TParm parmValue = table.getParmValue();
         int row = table.getSelectedRow();
         if (row == 0 || row == table.getRowCount() - 1) {
             return;
         }
         int preRow = row - 1;
         TParm rowParm = parmValue.getRow(row);
         TParm preRowParm = parmValue.getRow(preRow);
         parmValue.removeRow(row);
         parmValue.insertRow(preRow);
         String[] names = parmValue.getNames();
         for (int i = 0; i < names.length; i++) {
        	 //对调序号
        	 if("SEQ_NO".equals(names[i])) {
        		 parmValue.setData(names[i], preRow, preRowParm.getData(names[i]));
        		 parmValue.setData(names[i], row, rowParm.getData(names[i]));
        	 } else {
        		 parmValue.setData(names[i], preRow, rowParm.getData(names[i]));
        	 }
         }
         parmValue.setCount(parmValue.getCount(names[0]));
         table.setParmValue(parmValue);
         table.setSelectedRow(preRow);
    }
    
    /**
     * add by wukai on 20170406
     * 诊断信息下移
     */
    public void onDownDiagRecord() {
    	 TTable table = (TTable) this.getComponent("TP21_Table2");
         if (table.getRowCount() <= 1) {
             return;
         }
         TParm parmValue = table.getParmValue();
         int row = table.getSelectedRow();
         if (row == table.getRowCount() - 1 || row == table.getRowCount() - 2) {
             return;
         }
         int nextRow = row + 1;
         TParm rowParm = parmValue.getRow(row);
         TParm nextRowParm = parmValue.getRow(nextRow);
         parmValue.removeRow(row);
         parmValue.insertRow(nextRow);
         String[] names = parmValue.getNames();
         for (int i = 0; i < names.length; i++) {
        	 if("SEQ_NO".equals(names[i])) {
        		 parmValue.setData(names[i], nextRow, nextRowParm.getData(names[i]));
        		 parmValue.setData(names[i], row, rowParm.getData(names[i]));
        	 } else {
        		 parmValue.setData(names[i], nextRow, rowParm.getData(names[i]));
        	 }
         }
         parmValue.setCount(parmValue.getCount(names[0]));
         table.setParmValue(parmValue);
         table.setSelectedRow(nextRow);
    }
    
    
	// modified by WangQing 20170315
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
	
	
	
	
}