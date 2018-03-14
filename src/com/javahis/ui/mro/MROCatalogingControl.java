package com.javahis.ui.mro;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.mro.MROCatalogingTool;
import jdo.mro.MROTransDataToDBFTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 病案编目
 * </p>
 * 
 * <p>
 * Description: 病案编目
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-5-11
 * @version 1.0
 */
public class MROCatalogingControl extends TControl {
	private TParm data = new TParm();
	private String printType = "";// 记录打印类型

	// =================排序辅助==============add by wanglong 20120921
	private BILComparator compare = new BILComparator();
	private int sortColumn = -1;
	private boolean ascending = false;

	// 初始化
	public void onInit() {
		super.onInit();
		onClear();
		// 手术弹出窗口
		callFunction("UI|OP_CODE|setPopupMenuParameter", "OPICD",
				"%ROOT%\\config\\sys\\SYSOpICD.x");
		// 接受手术弹窗回传值
		callFunction("UI|OP_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

		// ICD10弹出窗口
		callFunction("UI|OUT_DIAG_CODE1|setPopupMenuParameter", "ICD10",
				"%ROOT%\\config\\sys\\SYSICDPopup.x");
		// 接受ICD10弹窗回传值
		callFunction("UI|MR_NO|addEventListener", TTextFieldEvent.KEY_RELEASED,
				this, "onQueryMrNo");
		// mr_no 回车事件
		callFunction("UI|OUT_DIAG_CODE1|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "ICD10Return");
		// ========pangben modify 20110621 start 权限添加
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20110621 stop
		TTable table = (TTable) this.getComponent("MRO_TABLE");// add by
																// wanglong
																// 20120921
		addSortListener(table);// add by wanglong 20120921 加排序

	}

	/**
	 * mr_no 回车事件
	 */
	public void onQueryMrNo() {
		String mrNo = PatTool.getInstance().checkMrno(getValueString("MR_NO"));
		
		setValue("MR_NO", mrNo);
		// modify by huangtt 20160929 EMPI患者查重提示 start
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
			mrNo= pat.getMrNo();
			setValue("MR_NO", mrNo);
		}	
		// modify by huangtt 20160929 EMPI患者查重提示 start
		
		this.onQuery();
	}

	/**
	 * 手术ICD选择返回数据处理
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		if (obj == null) {
			this.clearValue("OP_DESC;OP_CODE");
			return;
		}
		TParm returnParm = (TParm) obj;
		this.setValue("OP_DESC", returnParm.getValue("OPT_CHN_DESC"));
		this.setValue("OP_CODE", returnParm.getValue("OPERATION_ICD"));
		if (this.getValueString("OP_CODE").trim().length() <= 0) {
			this.setValue("OP_CODE", "");
		}
	}

	/**
	 * 诊断ICD10选择返回数据处理
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void ICD10Return(String tag, Object obj) {
		if (obj == null)
			return;
		TParm returnParm = (TParm) obj;
		this.setValue("OUT_DIAG_CODE1", returnParm.getValue("ICD_CODE"));
		this.setValue("OUT_DIAG_DESC", returnParm.getValue("ICD_CHN_DESC"));
	}

	/*
	 * 
	 * 查询
	 */
	public void onQuery() {
		TParm results = new TParm();
		if (this.getValue("checkType").equals("")) {
			this.messageBox_("请选择查询类别！");
			return;
		}
		// 获取查询条件
		TParm parm = this
				.getParmForTag("checkType;OUT_DEPT;PAT_NAME;NAME_PY;SEX;IPD_NO;MR_NO;IDNO;CHARGE_START;CHARGE_END;"
						+ "OUT_DIAG_CODE1;OP_CODE;CODE1_STATUS;VS_DR_CODE;DIRECTOR_DR_CODE;OFFICE;"
						+ "CONTACTER;CONT_TEL;H_TEL");
		
		// //日间手术  add by huangtt 20161226 
		//if (null != this.getValue("DAY_OPE_CODE") && this.getValueString(			
		//"DAY_OPE_CODE").length() > 0){
		//	parm.setData("DAY_OPE_CODE",this.getValueString("DAY_OPE_CODE"));
		//	} 
		
		// 出院日期
		parm.setData("OUT_DATE_START", StringTool.getString(
				getUITime("OUT_DATE_START"), "yyyyMMdd"));
		parm.setData("OUT_DATE_END", StringTool.getString(
				getUITime("OUT_DATE_END"), "yyyyMMdd"));
		// 入院日期

		parm.setData("IN_DATE_START", StringTool.getString(
				getUITime("IN_DATE_START"), "yyyyMMdd"));
		parm.setData("IN_DATE_END", StringTool.getString(
				getUITime("IN_DATE_END"), "yyyyMMdd"));

		// 生日
		parm.setData("BIRTH_DATE", StringTool.getString(
				getUITime("BIRTH_DATE"), "yyyyMMdd"));
		// 住院处完成标记
		if (((TCheckBox) this.getComponent("ADMCHK_FLG")).isSelected())
			parm.setData("ADMCHK_FLG", "Y");
		// 医 师完成标记
		if (((TCheckBox) this.getComponent("DIAGCHK_FLG")).isSelected())
			parm.setData("DIAGCHK_FLG", "Y");
		// 财 务完成标记
		if (((TCheckBox) this.getComponent("BILCHK_FLG")).isSelected())
			parm.setData("BILCHK_FLG", "Y");
		// 病案室完成标记
		if (((TCheckBox) this.getComponent("QTYCHK_FLG")).isSelected())
			parm.setData("QTYCHK_FLG", "Y");
		/**
		 * 病案首页中，加字段临床试验病例字典、 教学病例字典同时，加上查询功能；病历维护加两个字段 TEST_EMR,TEACH_EMR
		 * Modify ZhenQin ------ 2010-05-09 //;
		 */
		TParm dataTMP = this.getParmForTag("TEST_EMR;TEACH_EMR");
		if (!dataTMP.getValue("TEST_EMR").equals("")) {
			parm.setData("TEST_EMR", dataTMP.getValue("TEST_EMR"));
		}
		if (!dataTMP.getValue("TEACH_EMR").equals("")) {
			parm.setData("TEACH_EMR", dataTMP.getValue("TEACH_EMR"));
		}
		// ===========pangben modify 20110518 start 添加区域参数
		if (null != getValueString("REGION_CODE")
				&& getValueString("REGION_CODE").length() > 0)
			parm.setData("REGION_CODE", getValueString("REGION_CODE"));
		// ===========pangben modify 20110518 start

		 //诊断日期
		 parm.setData("CONFIRM_DATE",
		 StringTool.getString(getUITime("CONFIRM_DATE"),"yyyyMMdd"));
		// //手术等级
		// if(null != this.getValue("OP_LEVEL") &&
		// this.getValueString("OP_LEVEL").length() > 0){
		// parm.setData("OP_LEVEL", getValueString("OP_LEVEL"));
		// }
		// //手术日期
		// parm.setData("OP_DATE",
		// StringTool.getString(getUITime("OP_DATE"),
		// "yyyyMMdd"));
		// 查询病案主表MRO_RECORD数据，无住院跟手术条件
		data = MROCatalogingTool.getInstance().queryMROInfo(parm);
		// 判断住院跟手术条件是否为空，如果任意一个条件不为空，则进入住院和手术分别判断过程，如果为空，直接显示结果
		if ((null != this.getValue("OUT_DIAG_CODE1") && this.getValueString(
				"OUT_DIAG_CODE1").length() > 0)
				|| (null != this.getValue("OP_LEVEL") && this.getValueString(
						"OP_LEVEL").length() > 0)
				|| (null != this.getValue("OP_CODE") && this.getValueString(
						"OP_CODE").length() > 0)
				|| (null != StringTool.getString(getUITime("OP_DATE"),
						"yyyyMMdd") && StringTool.getString(
						getUITime("OP_DATE"), "yyyyMMdd").length() > 0)
				|| (null != this.getValue("OP_HEALTH") && this.getValueString(
						"OP_HEALTH").length() > 0 )) {
			//判断住院条件是否为空，如果为空，判断手术条件，如果不为空，则进入循环，过滤主表数据
			if ((null != this.getValue("OUT_DIAG_CODE1") && this
					.getValueString("OUT_DIAG_CODE1").length() > 0)) {
				//得到MRO_RECORD_DIAG表数据，强转成Map类型，以便过滤MRO_RECORD主表数据
				//此Map集合，键为字符串，值为Vector
				Map mapDiag = ((Map) filterResultsFromDiag());
				//声明一个Vector，用来接收值
				Vector vectorDiag = null;
				//判断Map集合是否为空，如果为空，则未在MRO_RECORD_DIAG表中查询出有效数据，继续判断手术条件
				if(null != mapDiag && mapDiag.size() > 0){
					//得到从MRO_RECORD_DIAG表中查询出来的所有CASE_NO
					vectorDiag = (Vector) mapDiag.get("CASE_NO");
					//循环判断主表中是否存在从从MRO_RECORD_DIAG表中查询出来的CASE_NO
					for (int i = 0; i < data.getCount(); i++) {
						//得到主表数据的每一行数据
						TParm parmRow = data.getRow(i);
						//得到每一行数据的CASE_NO
						String caseNo = parmRow.getValue("CASE_NO");
						//判断是否有每一行的CASE_NO，如果有，添加到最终的结果results中
						if (vectorDiag.contains(caseNo)) {
							results.addRowData(data, i);
						}
					}
				}
				//判断手术条件是否为空，如果为空，则直接显示主表查询结果
				//如果不为空，则进入筛选数据过程
				if ((null != this.getValue("OP_LEVEL") && this.getValueString(
						"OP_LEVEL").length() > 0)
						|| (null != this.getValue("OP_CODE") && this
								.getValueString("OP_CODE").length() > 0)
						|| (null != StringTool.getString(getUITime("OP_DATE"),
								"yyyyMMdd") && StringTool.getString(
								getUITime("OP_DATE"), "yyyyMMdd").length() > 0)
						|| (null != this.getValue("OP_HEALTH") && this.getValueString(
							 "OP_HEALTH").length() > 0 )) {
					//此Map集合，键为字符串，值为Vector
					Map mapOp = ((Map) filterResultsFromOP());
					//声明一个Vector，用来接收值
					Vector vectorOp = null;
					//如果住院和手术两种条件都填写，那么住院条件筛选出来的CASE_NO必须存在于通过筛选手术条件后的数据中
					//所以要获得共存数据，利用中间变量vectorResult来记录要符合条件的数据
					Vector vectorResult = new Vector();
					//判断Map集合是否为空，如果为空，则未在MRO_RECORD_OP表中查询出有效数据
					//即住院和手术条件都未查询出有效数据，给出提示
					if(null != mapOp && mapOp.size() > 0){
						//得到从MRO_RECORD_OP表中查询出来的所有CASE_NO
						vectorOp = (Vector) mapOp.get("CASE_NO");
						//记录两种条件下都符合的数据，存入中间变量vectorResult中
						for (int i = 0; i < vectorDiag.size(); i++) {
							if(vectorOp.contains(vectorDiag.get(i))){
								vectorResult.add(vectorDiag.get(i));
							}
						}
						//因为住院条件如果填写，筛选符合条件的流程中就会操作最终结果集results
						//所以必须清除第一次操作results遗留的数据
						while (results.getCount() > 0) {
							for (int i = 0; i < results.getCount(); i++) {
								results.removeRow(i);
							}
						}
						//循环判断主表中是否存在过滤后的CASE_NO，如果有，添加到最终的结果results中
						for (int i = 0; i < data.getCount(); i++) {
							TParm parmRow = data.getRow(i);
							String caseNo = parmRow.getValue("CASE_NO");
							if (vectorResult.contains(caseNo)) {
								results.addRowData(data, i);
							}
						}
						setValues(results);
					}else{
//						this.messageBox_("没有查询到符合条件的信息！");
//						return;
						while (results.getCount() > 0) {
							for (int i = 0; i < results.getCount(); i++) {
								results.removeRow(i);
							}
						}
						setValues(results);
					}
				}else{
					setValues(results);
				}
				// 判断手术条件是否为空，如果为空，则显示，如果不为空，则进入循环，先获得MRO_RECORD_OP表数据，再过滤主表数据，流程同上
			} else if ((null != this.getValue("OP_LEVEL") && 
					this.getValueString("OP_LEVEL").length() > 0)
					|| (null != this.getValue("OP_CODE") && 
							this.getValueString("OP_CODE").length() > 0)
					|| (null != StringTool.getString(getUITime("OP_DATE"),"yyyyMMdd") && 
							StringTool.getString(getUITime("OP_DATE"), "yyyyMMdd").length() > 0)) {
				Map map = ((Map) filterResultsFromOP());
				Vector vector = null;
				if(null != map && map.size() > 0){
					vector = (Vector) map.get("CASE_NO");
					for (int i = 0; i < data.getCount(); i++) {
						TParm parmRow = data.getRow(i);
						String caseNo = parmRow.getValue("CASE_NO");
						if (vector.contains(caseNo)) {
							results.addRowData(data, i);
						}
					}
				}
				setValues(results);
			}
		}else{
			setValues(data);
		}
	}

	private void setValues(TParm parm) {
		if(parm.getCount() >= 0)
			((TTextField) this.getComponent("PAT_COUNT")).setText(parm.getCount() + "");
		else
			((TTextField) this.getComponent("PAT_COUNT")).setText(0 + "");
		Timestamp now = SystemTool.getInstance().getDate();
		if (parm.getCount("MR_NO") <= 0) {
			((TTable) this.getComponent("MRO_TABLE")).removeRowAll();
			this.messageBox_("没有查询到符合条件的信息！");
			return;
		}
//		Timestamp now = SystemTool.getInstance().getDate();
		// 设置住院天数和出院天数的初始值 以及初始化感染列
		TParm queryParm = new TParm();
		TParm result = new TParm();
		queryParm.setData("IO_TYPE", "Q");
		for (int i = 0; i < parm.getCount(); i++) {
			int inDays = 0;
			// 判断病人是否出院
			if (parm.getValue("OUT_DATE", i).length() > 0) {
				// 如果病人已经出院
				// 住院天数 = 出院时间 - 住院日期
				inDays = StringTool.getDateDiffer(StringTool.getTimestamp(StringTool.getString(parm.getTimestamp("OUT_DATE", i), "yyyyMMdd"),"yyyyMMdd"),
						StringTool.getTimestamp(StringTool.getString(parm.getTimestamp("IN_DATE", i),"yyyyMMdd"), "yyyyMMdd"));
				// 计算出院天数
				int outDays = StringTool.getDateDiffer(now, parm.getTimestamp(
						"OUT_DATE", i));
				// 如果出院天数为零 则自动加一
				if (outDays == 0) {
					outDays = 1;
				}
				parm.setData("OUT_DAYS", i, outDays);
			} else {// 如果病人在院
				// 住院天数 = 当前时间 - 住院日期
				inDays = StringTool.getDateDiffer(StringTool.getTimestamp(
						StringTool.getString(now, "yyyyMMdd"), "yyyyMMdd"),
						StringTool.getTimestamp(StringTool.getString(parm
								.getTimestamp("IN_DATE", i), "yyyyMMdd"),
								"yyyyMMdd"));
				// 不用计算出院天数
				parm.setData("OUT_DAYS", i, "");
			}
			if (inDays == 0)// 如果住院天数为零那么默认为1
				inDays = 1;
			parm.setData("REAL_STAY_DAYS", i, inDays);
			
			//add by wukai on 20170406 ==== start 4993 增加感染列
			queryParm.setData("CASE_NO", parm.getData("CASE_NO", i));
			result = MROCatalogingTool.getInstance().queryMRO_DIAGInfo(queryParm);
			if(result != null && result.getCount("CASE_NO") > 0) {
				parm.setData("DIAG_FLG_Q", i, "Y");
			} else {
				parm.setData("DIAG_FLG_Q", i, "N");
			}
			//add by wukai on 20170406 ==== start 4993 增加感染列
		}
		
		TComboBox combo = (TComboBox) this.getComponent("checkType");
		printType = combo.getSelectedText();
		((TTable) this.getComponent("MRO_TABLE")).setParmValue(parm);
	}

	/**
	 * 查询MRO_RECORD_DIAG表数据
	 * 
	 * @return MRO_RECORD_DIAG表的结果
	 */
	private Object filterResultsFromDiag() {
		TParm parm = new TParm();
		TParm result = null;
		parm.setData("IO_TYPE", "O");
		if (null != this.getValue("OUT_DIAG_CODE1")
				&& this.getValueString("OUT_DIAG_CODE1").length() > 0) {
			parm.setData("ICD_CODE", this.getValueString("OUT_DIAG_CODE1"));
		}
		if (null != StringTool.getString(getUITime("CONFIRM_DATE"), "yyyyMMdd")
				&& StringTool.getString(getUITime("CONFIRM_DATE"), "yyyyMMdd")
						.length() > 0) {
			parm.setData("CONFIRM_DATE", StringTool.getString(
					getUITime("CONFIRM_DATE"), "yyyyMMdd"));
		}
		result = MROCatalogingTool.getInstance().queryMRO_DIAGInfo(parm);
		return result.getData().get("Data");
	}

	/**
	 * 查询MRO_RECORD_OP表数据
	 * 
	 * @return MRO_RECORD_OP表的结果
	 */
	private Object filterResultsFromOP() {
		TParm parm = new TParm();
		TParm result = null;
		if (null != this.getValue("OP_LEVEL")
				&& this.getValueString("OP_LEVEL").length() > 0) {
			parm.setData("OP_LEVEL", this.getValueString("OP_LEVEL"));
		}
		if (null != this.getValue("OP_CODE")
				&& this.getValueString("OP_CODE").length() > 0) {
			parm.setData("OP_CODE", this.getValueString("OP_CODE"));
		}
		if (null != StringTool.getString(getUITime("OP_DATE"), "yyyyMMdd")
				&& StringTool.getString(getUITime("OP_DATE"), "yyyyMMdd")
						.length() > 0) {
			parm.setData("OP_DATE", StringTool.getString(getUITime("OP_DATE"),
					"yyyyMMdd"));
		}
		//add by wukai on 20170406
		if(null != this.getValue("OP_HEALTH") && this.getValueString(
				 "OP_HEALTH").length() > 0 ) {
			parm.setData("OP_HEALTH", this.getValueString("OP_HEALTH"));
		}
		// parm.setData("CASE_NO", caseNo);
		result = MROCatalogingTool.getInstance().queryMRO_OPInfo(parm);
		// ((Map)result.getData().get("Data")).containsValue(caseNo);
		// System.out.println("ddddddddddddddddddddd"+result);
		// System.out.println("fdfsfdsfsdfdsfds"+result.getData().get("Data"));
		return result.getData().get("Data");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this
				.clearValue("checkType;OUT_DEPT;PAT_NAME;NAME_PY;SEX;IPD_NO;MR_NO;IDNO;OUT_DATE_START;"
						+ "OUT_DATE_END;IN_DATE_START;IN_DATE_END;CHARGE_START;CHARGE_END;"
						+ "OUT_DIAG_CODE1;OUT_DIAG_DESC;OUT_DIAG_DATE;OP_CODE;OP_DESC;OP_DATE;"
						+ "CODE1_STATUS;VS_DR_CODE;DIRECTOR_DR_CODE;OFFICE;BIRTH_DATE;CONTACTER;"
						+ "CONT_TEL;H_TEL;ADMCHK_FLG;DIAGCHK_FLG;BILCHK_FLG;QTYCHK_FLG");
		((TTable) this.getComponent("MRO_TABLE")).clearSelection();// 取消选中行
		this.setValue("REGION_CODE", Operator.getRegion());// =======pangben
															// modify 20110621
	}

	/**
	 * 病案借阅
	 */
	public void onLending() {
		// 获取选中行
		TTable table = (TTable) this.getComponent("MRO_TABLE");
		int rowIndex = table.getSelectedRow();
		if (rowIndex < 0) {
			this.messageBox_("请选择一份病历！");
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", data.getValue("MR_NO", rowIndex));
		parm.setData("IPD_NO", data.getValue("IPD_NO", rowIndex));
		parm.setData("CASE_NO", data.getValue("CASE_NO", rowIndex));
		parm.setData("PAT_NAME", data.getValue("PAT_NAME", rowIndex));// 患者姓名
		parm.setData("VS_CODE", data.getValue("VS_DR_CODE", rowIndex));// 住院医师
		this.openDialog("%ROOT%\\config\\mro\\MROLendReg.x", parm);
	}

	/**
	 * 调用“病案首页”
	 */
	public void callRecordPG() {
		// 获取选中行
		int rowIndex = ((TTable) this.getComponent("MRO_TABLE"))
				.getSelectedRow();
		TParm parm = new TParm();
		// ------------modify by wanglong 20121029----------------------
		// parm.setData("MR_NO", data.getValue("MR_NO", rowIndex));
		// parm.setData("CASE_NO", data.getValue("CASE_NO",rowIndex));
		TParm patInfo = (TParm) callFunction("UI|MRO_TABLE|getShowParmValue");
		parm.setData("MR_NO", patInfo.getValue("MR_NO", rowIndex));
		parm.setData("CASE_NO", patInfo.getValue("CASE_NO", rowIndex));
		// -------------modify end--------------------------------------
		parm.setData("USER_TYPE", "4");// 病案编目调用 标示
		parm.setData("OPEN_USER", Operator.getID());
		this.openWindow("%ROOT%\\config\\mro\\MRORecord.x", parm);
		// this.onQuery();//delete by wanglong 20121029
	}

	/**
	 * 获取日期控件的返回值
	 * 
	 * @param tag
	 *            String
	 * @return Timestamp
	 */
	public Timestamp getUITime(String tag) {
		Timestamp time = (Timestamp) ((TTextFormat) this.getComponent(tag))
				.getValue();
		return time;
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		TTable table = (TTable) this.getComponent("MRO_TABLE");
		TParm data = table.getParmValue();
		TParm printData = new TParm();
		TParm TableData = new TParm();
		for (int i = 0; i < data.getCount("CASE_NO"); i++) {
			TableData.addData("MR_NO", data.getData("MR_NO", i));
			TableData.addData("PAT_NAME", data.getData("PAT_NAME", i));
			TableData.addData("SEX", data.getData("SEX", i));
			TableData.addData("AGE", StringUtil.showAge(data.getTimestamp(
					"BIRTH_DATE", i), data.getTimestamp("IN_DATE", i)));
			TableData.addData("IN_DATE", StringTool.getString(data
					.getTimestamp("IN_DATE", i), "yyyy-MM-dd"));
			TableData.addData("OUT_DATE", StringTool.getString(data
					.getTimestamp("OUT_DATE", i), "yyyy-MM-dd"));
			TableData.addData("REAL_STAY_DAYS", data.getData("REAL_STAY_DAYS",
					i));
			TableData.addData("OUT_DAYS", data.getData("OUT_DAYS", i));
			TableData.addData("OUT_DEPT", data.getData("OUT_DEPT", i));
			TableData.addData("IPD_NO", data.getData("IPD_NO", i));
		}
		TableData.setCount(data.getCount("CASE_NO"));
		TableData.addData("SYSTEM", "COLUMNS", "MR_NO");
		TableData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		TableData.addData("SYSTEM", "COLUMNS", "SEX");
		TableData.addData("SYSTEM", "COLUMNS", "AGE");
		TableData.addData("SYSTEM", "COLUMNS", "IN_DATE");
		TableData.addData("SYSTEM", "COLUMNS", "OUT_DATE");
		TableData.addData("SYSTEM", "COLUMNS", "REAL_STAY_DAYS");
		TableData.addData("SYSTEM", "COLUMNS", "OUT_DAYS");
		TableData.addData("SYSTEM", "COLUMNS", "OUT_DEPT");
		TableData.addData("SYSTEM", "COLUMNS", "IPD_NO");
		TParm basic = new TParm();
		basic.addData("PrintUser", Operator.getName());// 制表人
		printData.setData("basic", basic.getData());
		printData.setData("T1", TableData.getData());
		printData.setData("checkType", "TEXT", printType);// 打印类型
		printData.setData("printDate", "TEXT", StringTool.getString(SystemTool
				.getInstance().getDate(), "yyyy年MM月dd日"));
		this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROCatalogPring.jhw",
				printData);
	}

	/**
	 * 病历浏览 ==========pangben modify 20110706
	 */
	public void onShow() {
		TTable table = ((TTable) this.getComponent("MRO_TABLE"));
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
	 * 全文检索UI
	 */
	public void onAllSearch() {
		Runtime run = Runtime.getRuntime();
		try {
			// 得到当前使用的ip地址
			String ip = TIOM_AppServer.SOCKET
					.getServletPath("EMRSearchServlet?method=");
			// 连接网页方法 parm.getValue("MR_NO")
			run.exec("IEXPLORE.EXE " + ip + "init");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * LIS浏览 ==========pangben modify 20110706
	 */
	public void onLISShow() {
		TTable table = ((TTable) this.getComponent("MRO_TABLE"));
		if (table.getSelectedRow() < 0) {
			this.messageBox("请选择一个病人");
			return;
		}
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		Runtime run = Runtime.getRuntime();
		try {
			// 得到当前使用的ip地址http://127.0.0.1:8080/BlueCore/jsp/lis/reportMain.jsp?MR_NO=000000001112
			// String
			// ip="http://"+TIOM_AppServer.SOCKET.getIP()+":"+TIOM_AppServer.SOCKET.getPort()+"/BlueCore/jsp/lis/reportMain.jsp?MR_NO=";
			String ip = "http://192.168.1.103:"
					+ TIOM_AppServer.SOCKET.getPort()
					+ "/BlueCore/jsp/lis/reportMain.jsp?MR_NO=";
			// 连接网页方法
			run.exec("IEXPLORE.EXE " + ip + parm.getValue("MR_NO"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 导出EXECL
	 */
	public void onExecl() {
		ExportExcelUtil.getInstance().exportExcel(
				((TTable) this.getComponent("MRO_TABLE")), "病案编目");
	}

	public void saveDBF() {
		if (null == this.getValue("OUT_DATE_START")
				|| this.getValue("OUT_DATE_START").toString().length() <= 0
				|| null == this.getValue("OUT_DATE_END")
				|| this.getValue("OUT_DATE_END").toString().length() <= 0) {

			if (null == this.getValue("OUT_DATE_START")
					|| this.getValue("OUT_DATE_START").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_START");
			}
			if (null == this.getValue("OUT_DATE_END")
					|| this.getValue("OUT_DATE_END").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_END");
			}
			this.messageBox("请输入出院日期");
			return;
		}
		TParm parm = new TParm();
		parm.setData("START_DATE", SystemTool.getInstance().getDateReplace(
				this.getValueString("OUT_DATE_START"), true));
		parm.setData("END_DATE", SystemTool.getInstance().getDateReplace(
				this.getValueString("OUT_DATE_END"), false));
		MROTransDataToDBFTool.getInstance().getMRODBF(parm, this);
	}

	// ====================排序功能begin======================modify by wanglong
	// 20120921
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// 点击相同列，翻转排序
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// 取得表单中的数据
				String columnName[] = tableData.getNames("Data");// 获得列名
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
				int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * 根据列名数据，将TParm转为Vector
	 * 
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
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
	 * 返回指定列在列名数组中的index
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * 根据列名数据，将Vector转成Parm
	 * 
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */

	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
	// ====================排序功能end======================
}
