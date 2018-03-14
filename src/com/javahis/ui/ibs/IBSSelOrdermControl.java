package com.javahis.ui.ibs;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.ibs.IBSOrderdTool;
import jdo.adm.ADMInpTool;

import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComboNode;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.base.TComboBoxModel;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.util.Compare;
import com.javahis.manager.sysfee.sysOdrPackDObserver;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.bil.BILComparator;
import jdo.bil.BILPayTool;

/**
 * 
 * <p>
 * Title: 费用查询控制类
 * </p>
 * 
 * <p>
 * Description: 费用查询控制类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangl
 * @version 1.0
 */
public class IBSSelOrdermControl extends TControl {
	String mrNo;
	String caseNo;
	int caseNoFirst = 0;
	int maxSeq = -1; // 最大账户号
	int minSeq = -1; // 最小账户号
	TParm selDifferentParm; // 查询不同的账号
	String user; // 当前登录用户
	String dept;// 主科室
	private int sortColumn = -1;
	private boolean ascending = false;
	private BILComparator compare = new BILComparator();// modify by wanglong
														// 20120815 更换对比类
	private TTable deptFeeTable;// 科室费用查询表
	// =======add by wanglong begin 20120815 为以下两表增加排序功能
	private TTable mergeTable;// 合并费用明细表
	private TTable feeDTable;// 费用明细表

	// ==========add end========================================
	private TTabbedPane tTabbedPane_0;//====caowl
	/**
	 * 初始化方法
	 */
	public void onInit() {
		
		super.onInit();
		this.tTabbedPane_0 = (TTabbedPane) this.getComponent("tTabbedPane_0");//====caowl
		
		callFunction("UI|print|setEnabled", false);//==caowl 20121018
		callFunction("UI|export|setEnabled", false);//-==caowl 20121018
		callFunction("UI|Refresh|setEnabled", false);//==caowl  20121018
		
		
		//callFunction("UI|PAT_NAME|setEnabled", false);
		// add begin wangzl 20120906
		String ipd="";
		TParm result = IBSOrderdTool.getInstance().initCostCenterCode();
		if (result != null && result.getCount() > 0) {
			ipd= result.getValue("IPD_FIT_FLG", 0);
		}
		// add end wangzl 20120906
		if (null==ipd||"".equals(ipd)||"N".equals(ipd)) { // 如果fig 为N
			// 表示当前登录user
			// 没有科室成本中心
			setValue("COST_CENTER_CODE", "");
		} else {
			setValue("COST_CENTER_CODE", result.getData("COST_CENTER_CODE", 0));
		}
		// 添加 wangzl 20120806 stop

		this.deptFeeTable = (TTable) this.getComponent("deptFeeTable");
		addListener(deptFeeTable);
		// =======add by wanglong begin 20120815 增加排序功能
		this.mergeTable = (TTable) this.getComponent("mergeFeeTable");
		addListener(mergeTable);// 排序监听
		this.feeDTable = (TTable) this.getComponent("feeDetailTable");
		addListener(feeDTable); // 排序监听
		// ==========add end===============================
		String aa = selInsICDCode("A30.501");
		//System.out.println("最终诊断代码" + aa);
		// ============xueyf modify 20120307 start
		Timestamp time = SystemTool.getInstance().getDate();
		time.setHours(0);
		time.setMinutes(0);
		time.setSeconds(0);
		setValue("START_DATE", time);
		setValue("END_DATE", SystemTool.getInstance().getDate());
		// ============xueyf modify 20120307 stop
		TParm initParm = new TParm();
		Object obj = this.getParameter();
		if (obj == null)
			return;
		if (obj != null) {
			initParm = (TParm) obj;
		}
		caseNo = initParm.getData("IBS", "CASE_NO").toString();
		mrNo = initParm.getData("IBS", "MR_NO").toString();
		if (initParm.getData("IBS", "TYPE") != null) {
			callFunction("UI|close|setEnabled", false);
			String sysType = initParm.getData("IBS", "TYPE").toString();
			if ("ODISTATION".equals(sysType)) {
				callFunction("UI|showpat|setEnabled", false);
				callFunction("UI|bedcard|setEnabled", false);
			}

		}
		// 只有text有这个方法，调用sys_fee弹出框
		callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
				"%ROOT%\\config\\sys\\SYSFeePopup.x");
		// 接受回传值
		callFunction("UI|ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		this.initPage();
	}

	private TTextField getTextField(String string) {
		// TODO Auto-generated method stub
		return (TTextField) this.getComponent(string);
	}

	/**
	 * 费用代码下拉列表选择
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
		this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
		onQuerySingleOrder();
		// this.grabFocus("ORDER_DESC");
	}
	/**
	 * 清空
	 * */
	public void onClear(){
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		if(selectedIndex == 0){
			
			TTable table = (TTable) this.getComponent("rexpTable");
	        table.removeRowAll();
	        initPage();
		}
		if(selectedIndex == 1){
			
			TTable table = (TTable) this.getComponent("feeDetailTable");
	        table.removeRowAll();
	        initPage();
		}
		if(selectedIndex == 2){
		
			TTable table = (TTable) this.getComponent("billDetailTable");
	        table.removeRowAll();
	        initPage();
		}
		if(selectedIndex == 3){
			this.clearValue("ORDER_CODE;ORDER_DESC");
			TTable table = (TTable) this.getComponent("singleOrderTable");
	        table.removeRowAll();
	        initPage();
		}
		if(selectedIndex == 4){
		    this.clearValue("C_BILL;C_UNBILL;C_RECP_TYPE;REXP_TYPE;C_UP_FEE;TOT_AMT;MERGE_TOT_FEE");
			TTable table = (TTable) this.getComponent("mergeFeeTable");
	        table.removeRowAll();
	        initPage();
		}
		
		if(selectedIndex == 5){	
			this.clearValue("COST_CENTER_CODE;ORDER_CAT1_CODE;CTRLDRUGCLASS_CODE;DEPT_TOT_FEE");				
			initPage();			
			TTable table =  (TTable) this.getComponent("deptFeeTable");			
			TParm parm = new TParm();
			table.setParmValue(parm);
			       
		}
		
		
		
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		
		
		//===modify by caowl 20121018 start
		int selectedIndex = tTabbedPane_0.getSelectedIndex();		
		if(selectedIndex == 2){
			//计价明细			
			onQueryBillDetail();
		}
		if(selectedIndex == 4){
			//合并费用明细			
			onQueryMergeFee();
		}
		if(selectedIndex == 5){
			//科室费用查询				
			onQueryDeptFee();
		}	
		//=== modify by caowl 20121018 end
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {
		Timestamp today = SystemTool.getInstance().getDate();
		String sDateStr = StringTool.getString(today, "yyyyMMdd");
		String sTimeStr = "000000";
		String eTimeStr = StringTool.getString(today, "HHmmss");
		Timestamp sDate = StringTool.getTimestamp(sDateStr + sTimeStr,
				"yyyyMMddHHmmss");
		Timestamp eDate = StringTool.getTimestamp(sDateStr + eTimeStr,
				"yyyyMMddHHmmss");
		setValue("BILL_DATE", sDate);
		setValue("BILL_DATE_E", eDate);
		Timestamp time = SystemTool.getInstance().getDate();
		time.setHours(0);
		time.setMinutes(0);
		time.setSeconds(0);
		setValue("START_DATE", time);
		setValue("END_DATE", SystemTool.getInstance().getDate());
		// rexpTable查询数据
		TParm rexpParm = new TParm();
		rexpParm.setData("CASE_NO", caseNo);
		TParm rexpData = new TParm();
		rexpData = IBSOrderdTool.getInstance().selectdataAll(rexpParm);
		// System.out.println("rexpData收据项目" + rexpData);
		double sunTotAmt = 0.00;
		for (int i = 0; i < rexpData.getCount(); i++) {
			double totAmt = 0.00;
			totAmt = rexpData.getDouble("AR_AMT", i);
			sunTotAmt = sunTotAmt + totAmt;
		}
		// 给rexpTable配参
		this.callFunction("UI|rexpTable|setParmValue", rexpData);
		// feeDetailTable查询数据
		TParm feeDetailParm = new TParm();
		feeDetailParm.setData("CASE_NO", caseNo);
		TParm feeDetailData = new TParm();
		feeDetailData = IBSOrderdTool.getInstance().selFeeDetail(feeDetailParm);
		TParm selADMdata = new TParm();
		selADMdata.setData("CASE_NO", caseNo);
		TParm admDataParm = ADMInpTool.getInstance().selectall(selADMdata);
		TParm bilPayParm = BILPayTool.getInstance().selBilPayLeft(caseNo);
		setValue("CUR_AMT", bilPayParm.getDouble("PRE_AMT", 0)); // 可用余额
		setValue("TOTAL_BILPAY", admDataParm.getDouble("TOTAL_BILPAY", 0)); // 预交金总额
		setValue("TOTAL_AMT", sunTotAmt); // 费用总额
		setValue("NHI_AMT", ""); // 统筹支付
		setValue("OWN_AMT", sunTotAmt); // 自付金额
		setValue("Y_FEE", admDataParm.getDouble("TOTAL_BILPAY", 0) - sunTotAmt); // 应交金额
		// 给feeDetailTable配参===pangben 2011-11-15
		TParm tableinparm = new TParm();
		// 删除细项方法
		int count = 0;
		for (int i = 0; i < feeDetailData.getCount(); i++) {
			if (feeDetailData.getValue("INDV_FLG", i).length() <= 0
					|| "N".equals(feeDetailData.getValue("INDV_FLG", i))) { // 集合遗嘱
				tableinparm.addRowData(feeDetailData, i);
				count++;
			}
		}
		this.callFunction("UI|PG_UP|setEnabled", false);
		this.callFunction("UI|PG_DOWN|setEnabled", false);
		tableinparm.setCount(count);
		tableinParm(feeDetailData, tableinparm);
		// ===pangben 2011-11-15 stop
		this.callFunction("UI|feeDetailTable|setParmValue", tableinparm);
	}

	/**
	 * 查询计价明细
	 */
	public void onQueryBillDetail() {
		// billDetailTable查询数据
		TParm billDetailParm = new TParm();
		billDetailParm.setData("CASE_NO", caseNo);
		
		if (getValue("BILL_DATE") != null && getValue("BILL_DATE_E") != null) {
			Timestamp bill = (Timestamp) getValue("BILL_DATE");
			Timestamp billE = (Timestamp) getValue("BILL_DATE_E");
			String billDate = StringTool.getString(bill, "yyyyMMddHHmmss");
			String billDateE = StringTool.getString(billE, "yyyyMMddHHmmss");	
			
			billDetailParm.setData("BILL_DATE", billDate);
			billDetailParm.setData("BILL_DATE_E", billDateE);
		}
		// billDetailParm.setData("CASE_NO_SEQ", caseNoFirst);
		TParm billDetailData = new TParm();
		billDetailData = IBSOrderdTool.getInstance().selBillDetail(
				billDetailParm);
		
		if (billDetailData.getCount() <= 0) {

			this.messageBox("没有查询的资料");
			return;
		}

		// 给billDetailTable配参

		this.callFunction("UI|billDetailTable|setParmValue", billDetailData);
		TParm selMaxSeq = new TParm();
		selMaxSeq.setData("CASE_NO", caseNo);
		if (getValue("BILL_DATE") != null && getValue("BILL_DATE_E") != null) {
			Timestamp bill = (Timestamp) getValue("BILL_DATE");
			Timestamp billE = (Timestamp) getValue("BILL_DATE_E");
			String billDate = StringTool.getString(bill, "yyyyMMddHHmmss");
			String billDateE = StringTool.getString(billE, "yyyyMMddHHmmss");
			billDetailParm.setData("BILL_DATE", billDate);
			billDetailParm.setData("BILL_DATE_E", billDateE);
			selMaxSeq.setData("BILL_DATE", billDate);
			selMaxSeq.setData("BILL_DATE_E", billDateE);
		}
		TParm selMaxSeqParm = IBSOrderdTool.getInstance().selMaxSeq(selMaxSeq);
		selDifferentParm = IBSOrderdTool.getInstance().selDifferentSeq(
				selMaxSeq); // 查询不同的帐号
		maxSeq = selMaxSeqParm.getInt("CASE_NO_SEQ", 0);
		minSeq = selMaxSeqParm.getInt("MIN_CASE_NO_SEQ", 0);
		caseNoFirst = 0;
		this.setValue("PG_NO", minSeq + "");
		if (minSeq >= maxSeq) {
			// this.messageBox("最后一笔");
			this.callFunction("UI|PG_DOWN|setEnabled", false);
			// this.callFunction("UI|PG_UP|setEnabled", true);
			// return;
		} else {
			this.callFunction("UI|PG_DOWN|setEnabled", true);
		}
		this.callFunction("UI|PG_UP|setEnabled", false);

	}
	
	

	/**
	 * 下一笔
	 */
	public void pgDn() {
		// billDetailTable查询数据
		TParm billDetailParm = new TParm();
		billDetailParm.setData("CASE_NO", caseNo);
		TParm selMaxSeq = new TParm();
		selMaxSeq.setData("CASE_NO", caseNo);
		if (getValue("BILL_DATE") != null && getValue("BILL_DATE_E") != null) {
			Timestamp bill = (Timestamp) getValue("BILL_DATE");
			Timestamp billE = (Timestamp) getValue("BILL_DATE_E");
			String billDate = StringTool.getString(bill, "yyyyMMddHHmmss");
			String billDateE = StringTool.getString(billE, "yyyyMMddHHmmss");
			billDetailParm.setData("BILL_DATE", billDate);
			billDetailParm.setData("BILL_DATE_E", billDateE);
			selMaxSeq.setData("BILL_DATE", billDate);
			selMaxSeq.setData("BILL_DATE_E", billDateE);
		}
		// TParm selMaxSeqParm =
		// IBSOrderdTool.getInstance().selMaxSeq(selMaxSeq);
		// int maxSeq = selMaxSeqParm.getInt("CASE_NO_SEQ", 0);
		// int minSeq = selMaxSeqParm.getInt("MIN_CASE_NO_SEQ", 0);//最小
		caseNoFirst = caseNoFirst + 1;
		if (caseNoFirst <= selDifferentParm.getCount()) {
			if (selDifferentParm.getInt("CASE_NO_SEQ", caseNoFirst) >= maxSeq) {
				// this.messageBox("最后一笔");
				this.callFunction("UI|PG_DOWN|setEnabled", false);
				this.callFunction("UI|PG_UP|setEnabled", true);
				// return;
			} else {
				this.callFunction("UI|PG_UP|setEnabled", true);
				this.callFunction("UI|PG_DOWN|setEnabled", true);
			}
		} else {
			this.callFunction("UI|PG_DOWN|setEnabled", false);
			this.callFunction("UI|PG_UP|setEnabled", true);
		}

		this.setValue("PG_NO", selDifferentParm.getValue("CASE_NO_SEQ",
				caseNoFirst));
		billDetailParm.setData("CASE_NO_SEQ", selDifferentParm.getValue(
				"CASE_NO_SEQ", caseNoFirst));
		TParm billDetailData = new TParm();
		billDetailData = IBSOrderdTool.getInstance().selBillDetail(
				billDetailParm);
		// 给billDetailTable配参
		this.callFunction("UI|billDetailTable|setParmValue", billDetailData);

	}

	/**
	 * 上一笔
	 */
	public void pgUp() {
		// billDetailTable查询数据
		TParm billDetailParm = new TParm();
		billDetailParm.setData("CASE_NO", caseNo);
		if (getValue("BILL_DATE") != null && getValue("BILL_DATE_E") != null) {
			Timestamp bill = (Timestamp) getValue("BILL_DATE");
			Timestamp billE = (Timestamp) getValue("BILL_DATE_E");
			String billDate = StringTool.getString(bill, "yyyyMMddHHmmss");
			String billDateE = StringTool.getString(billE, "yyyyMMddHHmmss");
			billDetailParm.setData("BILL_DATE", billDate);
			billDetailParm.setData("BILL_DATE_E", billDateE);
		}

		// TParm selMaxSeqParm =
		// IBSOrderdTool.getInstance().selMaxSeq(billDetailParm);
		// int maxSeq = selMaxSeqParm.getInt("CASE_NO_SEQ", 0);
		// int minSeq = selMaxSeqParm.getInt("MIN_CASE_NO_SEQ", 0);//最小
		caseNoFirst = caseNoFirst - 1;
		if (caseNoFirst <= selDifferentParm.getCount() && caseNoFirst >= 0) {
			if (selDifferentParm.getInt("CASE_NO_SEQ", caseNoFirst) <= minSeq) {
				// this.messageBox("第一笔");
				this.callFunction("UI|PG_UP|setEnabled", false);
				this.callFunction("UI|PG_DOWN|setEnabled", true);
				// return;
			} else {
				this.callFunction("UI|PG_UP|setEnabled", true);
				this.callFunction("UI|PG_DOWN|setEnabled", true);
			}
		} else {
			caseNoFirst = 0;
			this.callFunction("UI|PG_UP|setEnabled", false);
			this.callFunction("UI|PG_DOWN|setEnabled", true);
		}

		this.setValue("PG_NO", selDifferentParm.getValue("CASE_NO_SEQ",
				caseNoFirst));
		billDetailParm.setData("CASE_NO_SEQ", selDifferentParm.getValue(
				"CASE_NO_SEQ", caseNoFirst));
		TParm billDetailData = new TParm();
		billDetailData = IBSOrderdTool.getInstance().selBillDetail(
				billDetailParm);
		// 给billDetailTable配参
		this.callFunction("UI|billDetailTable|setParmValue", billDetailData);
	}

	/**
	 * 查询单项医嘱
	 */
	public void onQuerySingleOrder() {
		// singleOrderTable查询数据
		TParm singleOrderParm = new TParm();
		singleOrderParm.setData("CASE_NO", caseNo);
		singleOrderParm.setData("ORDER_CODE", getValue("ORDER_CODE"));
		TParm singleOrderData = new TParm();
		singleOrderData = IBSOrderdTool.getInstance().selSingleOrder(
				singleOrderParm);
		if (singleOrderData.getCount() == 0
				|| getValue("ORDER_CODE").toString().length() == 0) {
			// 查无资料
			this.messageBox("E0008");
		}
		// 给singleOrderTable配参
		this.callFunction("UI|singleOrderTable|setParmValue", singleOrderData);
	}

	/**
	 * 查询合并费用明细
	 */
	public void onQueryMergeFee() {
		// mergeFeeTable查询数据
		TParm mergeFeeParm = new TParm();
		mergeFeeParm.setData("CASE_NO", caseNo);
		// 校验收据类别
		if (getValue("REXP_TYPE").toString().length() != 0) {
			mergeFeeParm.setData("REXP_CODE", getValue("REXP_TYPE"));
		}
		if (!(getValue("C_BILL").equals("Y") || getValue("C_UNBILL")
				.equals("Y"))) {
			this.messageBox("请选择“已结/未结”查询条件");
			return;
		}
		if (getValue("C_BILL").equals("N") || getValue("C_UNBILL").equals("N")) {
			if (getValue("C_BILL").equals("Y")) {
				mergeFeeParm.setData("BILL", "Y");
			}
			if (getValue("C_UNBILL").equals("Y")) {
				mergeFeeParm.setData("UNBILL", "Y");
			}

		}
		if (getValue("C_UP_FEE").equals("Y")) {
			mergeFeeParm.setData("TOT_AMT", getValue("TOT_AMT"));
		}
		TParm mergeFeeData = new TParm();
		mergeFeeData = IBSOrderdTool.getInstance().selMergeFee(mergeFeeParm);
		if (mergeFeeData.getCount() == 0) {
			// 查无资料
			this.messageBox("E0008");
		}
		// 给本次金额赋值
		int feeCount = mergeFeeData.getCount("TOT_AMT");
		double mergeTotFee = 0.00;
		for (int i = 0; i < feeCount; i++) {
			mergeTotFee = mergeTotFee + mergeFeeData.getDouble("TOT_AMT", i);
		}
		setValue("MERGE_TOT_FEE", mergeTotFee);
		
		// 给mergeFeeTable配参
		this.callFunction("UI|mergeFeeTable|setParmValue", mergeFeeData);
	}

	/**
	 * 科室费用查询
	 */
	public void onQueryDeptFee() {
		// 添加 wangzl 20120806 start
		// deptFeeTable查询数据
		TParm deptFeeParm = new TParm();
		deptFeeParm.setData("CASE_NO", caseNo);
		// 校验开始日期
		if (getValue("START_DATE") != null) {
			Timestamp start = (Timestamp) getValue("START_DATE");
			String startDate = StringTool.getString(start, "yyyyMMdd HH:mm:ss");
			deptFeeParm.setData("START_DATE", startDate);
		}
		
		// 校验结束日期
		if (getValue("END_DATE") != null) {
			Timestamp end = (Timestamp) getValue("END_DATE");
			String endDate = StringTool.getString(end, "yyyyMMdd HH:mm:ss");
			deptFeeParm.setData("END_DATE", endDate);
		}
	
		deptFeeParm.setData("cost_center_code", this
				.getValueString("COST_CENTER_CODE"));// 成本中心
		deptFeeParm.setData("order_cat1_code", this
				.getValueString("ORDER_CAT1_CODE"));// 医嘱分类
		
		deptFeeParm.setData("ctfig", this.getValue("CTRLDRUGCLASS_CODE"));// modify
																			// by
																			// caowl
																			// 20120814
		TParm deptFeeData = IBSOrderdTool.getInstance().selDeptFee(deptFeeParm);
		if (deptFeeData.getCount("BILL_DATE") < 0) {
			// 查无资料
			this.messageBox("E0008");
			return;
		}
		// 添加 wangzl 20120806 stop
		// 给本次金额赋值
		int feeCount = deptFeeData.getCount("TOT_AMT");
		double deptTotFee = 0.00;
		// ===pangben 2011-11-15
		// 删除细项方法
		int count = 0;
		TParm tableinparm = new TParm();
		for (int i = 0; i < feeCount; i++) {
			deptTotFee = deptTotFee + deptFeeData.getDouble("TOT_AMT", i);
			if (deptFeeData.getValue("INDV_FLG", i).length() <= 0
					|| "N".equals(deptFeeData.getValue("INDV_FLG", i))) { // 集合遗嘱
				tableinparm.addRowData(deptFeeData, i);
				count++;
			}
		}
		tableinparm.setCount(count);
		tableinParm(deptFeeData, tableinparm);
		// ===pangben 2011-11-15 stop
		setValue("DEPT_TOT_FEE", deptTotFee);
		callFunction("UI|print|setEnabled", true);//===caowl
		callFunction("UI|export|setEnabled", true);//===caowl
		// 给deptFeeTable配参
		this.callFunction("UI|deptFeeTable|setParmValue", tableinparm);
		// this.ORDER_CAT1_CODE="";
	}

	/**
	 * 显示集合医嘱金额
	 * 
	 * @param tempParm
	 *            TParm
	 * @param tableinparm
	 *            TParm
	 */
	private void tableinParm(TParm tempParm, TParm tableinparm) {

		for (int z = 0; z < tableinparm.getCount(); z++) {
			double sumTotAmt = 0.00;
			double sumOwnAmt = 0.00;
			double sumNhiAmt = 0.00;
			boolean isSet = false;
			double dosageQty = 0;
			for (int j = 0; j < tempParm.getCount(); j++) {
				if (("Y".equals(tempParm.getValue("INDV_FLG", j)) || tempParm
						.getValue("INDV_FLG", j).length() <= 0)
						&& tableinparm.getValue("ORDER_CODE", z).equals(
								tempParm.getValue("ORDERSET_CODE", j))
						&& tempParm.getValue("CASE_NO_SEQ", j).equals(
								tableinparm.getValue("CASE_NO_SEQ", z))
						&& tempParm.getValue("ORDERSET_GROUP_NO", j).equals(
								tableinparm.getValue("ORDERSET_GROUP_NO", z))) { // 集合遗嘱
					sumTotAmt += tempParm.getDouble("TOT_AMT", j);
//					 sumOwnAmt += tempParm.getDouble("OWN_PRICE", j);
					// modify by wangb 2017/09/29
					// 当集合医嘱主项数量大于1且集合医嘱套餐中细项数量大于1时，原计算方式会出现问题，故使用细项数量除以主项数量
					// modify by wanglong 20120808 单个医嘱单价的计算要乘以数量，否则和总价不等
					if (Math.abs(tableinparm.getDouble("DOSAGE_QTY", z)) > 0) {
						dosageQty = tempParm.getDouble("DOSAGE_QTY", j)
								/ tableinparm.getDouble("DOSAGE_QTY", z);
					} else {
						dosageQty = tempParm.getDouble("DOSAGE_QTY", j);
					}
					sumOwnAmt += tempParm.getDouble("OWN_PRICE", j) * dosageQty;
					sumNhiAmt += tempParm.getDouble("NHI_PRICE", j);
					isSet = true;
				}
			}
			if (isSet) {
				tableinparm.setData("TOT_AMT", z, sumTotAmt);
				tableinparm.setData("OWN_PRICE", z, sumOwnAmt);
				tableinparm.setData("NHI_PRICE", z, sumNhiAmt);
			}
		}
	}

	/**
	 * 类别checkBox改变事件
	 */
	public void onChangeRexpType() {
		if (getValue("C_RECP_TYPE").equals("Y"))
			this.callFunction("UI|REXP_TYPE|setEnabled", true);
		else {
			this.callFunction("UI|REXP_TYPE|setEnabled", false);
			this.setValue("REXP_TYPE", "");
		}
	}

	/**
	 * 费用checkBox改变事件
	 */
	public void onChangeUpFee() {
		if (getValue("C_UP_FEE").equals("Y"))
			this.callFunction("UI|TOT_AMT|setEnabled", true);
		else {
			this.callFunction("UI|TOT_AMT|setEnabled", false);
			this.setValue("TOT_AMT", 0);
		}
	}

	/**
	 * 处理当前TOOLBAR
	 */
	public void onShowWindowsFunction() {
		// 显示UIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	// /**
	// * 右击MENU弹出事件
	// * @param tableName
	// */
	// public void showPopMenu() {
	// TTable table = (TTable)this.getComponent("feeDetailTable");
	// int row = table.getSelectedRow();
	// TParm parm = table.getShowParmValue() ;
	// String order_code = parm.getValue("ORDER_CODE", row) ;
	// String sql =
	// "SELECT ORDERSET_FLG FROM SYS_FEE WHERE ORDER_CODE = '"+order_code+"'" ;
	// TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	// if (result.getErrCode() < 0) {
	// messageBox("数据错误 " + result.getErrText());
	// return ;
	// }
	// if(result.getCount()>0){
	// if ("Y".equals(result.getValue("ORDERSET_FLG", 0))) {
	// table
	// .setPopupMenuSyntax("显示集合医嘱细相|Show LIS/RIS Detail Items,onOrderSetShow");
	// return;
	// } else {
	// table.setPopupMenuSyntax("");
	// return;
	// }
	// }
	// }
	//
	// /**
	// * 右击MENU显示集合医嘱事件
	// */
	// public void onOrderSetShow(){
	// TTable table = (TTable)this.getComponent("feeDetailTable");
	// int row = table.getSelectedRow();
	// TParm parm = table.getShowParmValue() ;
	// String order_code = parm.getValue("ORDER_CODE", row) ;
	// String sql = "SELECT A.ORDER_DESC, A.SPECIFICATION, DOSAGE_QTY, "
	// + " UNIT_CODE AS MEDI_UNIT, OWN_PRICE, OWN_PRICE * DOSAGE_QTY "
	// + " AS OWN_AMT, EXEC_DEPT_CODE, OPTITEM_CODE, INSPAY_TYPE "
	// + " FROM SYS_FEE A, SYS_ORDERSETDETAIL B "
	// + " WHERE A.ORDER_CODE = B.ORDER_CODE "
	// + " AND B.ORDERSET_CODE = '" + order_code + "' "
	// + " ORDER BY B.ORDERSET_CODE, B.ORDER_CODE";
	// TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	// this.openDialog("%ROOT%\\config\\Zodo\\OPDOrderSetShow.x", result);
	//
	// }
	/**
	 * 右击MENU弹出事件
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("feeDetailTable");
		table.setPopupMenuSyntax("显示集合医嘱细项,openRigthPopMenu");
	}

	/**
	 * 打开集合医嘱细想查询
	 */
	public void openRigthPopMenu() {
		TTable table = (TTable) this.getComponent("feeDetailTable");
		int row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		String caseNo = tableParm.getValue("CASE_NO", row);
		String orderCode = tableParm.getValue("ORDER_CODE", row);
		String orderSetCode = tableParm.getValue("ORDERSET_CODE", row);
		String caseNoSeq = tableParm.getValue("CASE_NO_SEQ", row);
		int orderSetGroupNo = Integer.parseInt(tableParm.getValue(
				"ORDERSET_GROUP_NO", row));
		TParm parm = getOrderSetDetails(orderSetGroupNo, orderSetCode, caseNo,
				caseNoSeq, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
	}

	/**
	 * 返回集合医嘱细相的TParm形式
	 * 
	 * @param groupNo
	 *            int
	 * @param orderSetCode
	 *            String
	 * @param caseNo
	 *            String
	 * @param caseNoSeq
	 *            String
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderSetDetails(int groupNo, String orderSetCode,
			String caseNo, String caseNoSeq, String orderCode) {

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
		String sql = "SELECT * FROM IBS_ORDD WHERE CASE_NO='" + caseNo
				+ "' AND CASE_NO_SEQ='" + caseNoSeq
				+ "' AND ORDERSET_GROUP_NO=" + groupNo + "";

		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		int count = parm.getCount();
		if (count < 0) {
			//System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return result;
		}
		// temperr细项价格
		for (int i = 0; i < count; i++) {
			if (!orderCode.equals(parm.getValue("ORDER_CODE", i))
					&& orderSetCode.equals(parm.getValue("ORDERSET_CODE", i))) {
				// ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
				result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				// 查询单价
				TParm orderParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT OWN_PRICE,ORDER_DESC,SPECIFICATION,OPTITEM_CODE,INSPAY_TYPE "
								+ "FROM SYS_FEE WHERE ORDER_CODE='"
								+ parm.getValue("ORDER_CODE", i) + "'"));
				result
						.addData("OWN_PRICE", orderParm.getDouble("OWN_PRICE",
								0));
				// modify by wangbin 20141104 外部错误 #326 住院计价的显示集合医嘱细项错误 START
				// 总价直接取IBS_ORDD表的发生金额(TOT_AMT)
				result.addData("OWN_AMT", parm.getValue("TOT_AMT", i));
				// modify by wangbin 20141104 外部错误 #326 住院计价的显示集合医嘱细项错误 END
				result.addData("ORDER_DESC", orderParm
						.getValue("ORDER_DESC", 0));
				result.addData("SPECIFICATION", orderParm.getValue(
						"SPECIFICATION", 0));
				result.addData("EXEC_DEPT_CODE", parm.getValue("EXE_DEPT_CODE",
						i));
				result.addData("OPTITEM_CODE", orderParm.getValue(
						"OPTITEM_CODE", 0));
				result.addData("INSPAY_TYPE", orderParm.getValue("INSPAY_TYPE",
						0));
			}
		}
		return result;
	}

	public String selInsICDCode(String icdCode) {
		TParm result = new TParm();
		int count = icdCode.length();
		String insIcdCode = "";
		for (int i = 0; i < count; i++) {
			icdCode = icdCode.substring(0, count - i);
			String selIcdCode = " SELECT ICD_CODE, ICD_CHN_DESC "
					+ "   FROM INS_DIAGNOSIS " + "  WHERE ICD_CODE = '"
					+ icdCode + "' ";
			result = new TParm(TJDODBTool.getInstance().select(selIcdCode));
			if (result.getErrCode() < 0) {
				return "";
			}
			if (result.getCount() <= 0) {
				continue;
			} else {
				insIcdCode = result.getValue("ICD_CODE", 0);
				return insIcdCode;
			}
		}
		return insIcdCode;

	}

	/**
	 * 得到table
	 */
	public TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	/**
	 * 导出Excel
	 */
	public void onExport() {
		// 添加 wangzl 20120806 start
		if (deptFeeTable.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(deptFeeTable, "科室费用查询细表");
		} else {
			this.messageBox("无汇出数据");
			return;
		}
		// 添加 wangzl 20120806 stop
	}

	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		// 添加 wangzl 20120806 start
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
		// 添加 wangzl 20120806 stop
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		// 添加 wangzl 20120806 start
		if (deptFeeTable.getRowCount() <= 0) {
			this.messageBox("无打印数据");
			return;
		}
		TParm prtParm = new TParm();
		// 表头
		prtParm.setData("TITLE", "TEXT", "科室费用查询报表");
		String start_Date = this.getValueString("START_DATE");
		String end_Date = this.getValueString("END_DATE");
		if (this.checkNullAndEmpty(start_Date)) {
			start_Date = start_Date.substring(0, (start_Date.length() - 2));
		}
		if (this.checkNullAndEmpty(end_Date)) {
			end_Date = end_Date.substring(0, (end_Date.length() - 2));
		}
		prtParm.setData("START_DATE", "TEXT", start_Date);
		prtParm.setData("END_DATE", "TEXT", end_Date);
		// 表格数据
		TParm parm = new TParm();
		TParm tableParm = deptFeeTable.getParmValue();
		TComboBox com = (TComboBox) this.getComponent("DOSAGE_UNIT");
		for (int i = 0; i < deptFeeTable.getRowCount(); i++) {
			String blld = tableParm.getValue("BILL_DATE", i);
			blld = blld.substring(0, 19);
			parm.addData("BILL_DATE", blld);
			parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
			parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
			String ownFig = tableParm.getValue("OWN_FLG", i);
			if (ownFig.equals("Y")) {
				ownFig = "是";
			} else {
				ownFig = "否";
			}
			parm.addData("OWN_FLG", ownFig);
			parm.addData("DOSAGE_QTY", tableParm.getValue("DOSAGE_QTY", i));
			String rn = tableParm.getValue("DOSAGE_UNIT", i);
			TComboBoxModel tbm = com.getModel();
			Vector v = tbm.getItems();
			for (int j = 0; j < v.size(); j++) {
				TComboNode tn = (TComboNode) v.get(j);
				if (rn.equals(tn.getID())) {
					rn = tn.getName();
					break;
				}
			}
			parm.addData("DOSAGE_UNIT", rn);
			double o = tableParm.getDouble("OWN_PRICE", i);
			o = StringTool.round(o, 4);
			parm.addData("OWN_PRICE", o);
			double t = tableParm.getDouble("TOT_AMT", i);
			t = StringTool.round(t, 2);
			parm.addData("TOT_AMT", t);
			String optdate = tableParm.getValue("OPT_DATE", i);
			optdate = optdate.substring(0, 11);
			parm.addData("OPT_DATE", optdate);
			parm.addData("USER_NAME", tableParm.getValue("USER_NAME", i));
		}
		parm.setCount(parm.getCount("BILL_DATE"));
		parm.addData("SYSTEM", "COLUMNS", "BILL_DATE");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "OWN_FLG");
		parm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
		parm.addData("SYSTEM", "COLUMNS", "DOSAGE_UNIT");
		parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
		parm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
		parm.addData("SYSTEM", "COLUMNS", "OPT_DATE");
		parm.addData("SYSTEM", "COLUMNS", "USER_NAME");
		// 把表格数据添加进要打印的parm
		prtParm.setData("TABLE", parm.getData());
		prtParm.setData("NAME", "TEXT", "制作人：" + Operator.getName());
		// 调用打印方法,报表路径
		this.openPrintWindow("%ROOT%\\config\\prt\\IBS\\IBSSelOrderm.jhw",
				prtParm);
		// 添加 wangzl 20120806 stop
	}

	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) { // =======modify by wanglong
													// begin 20120815
													// 修改变量名,使此方法通用
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() { // =======modify
																				// by
																				// wanglong
																				// begin
																				// 20120815
																				// 修改变量名,使此方法通用
					public void mouseClicked(MouseEvent mouseevent) {
						int i = table.getTable().columnAtPoint(
								mouseevent.getPoint());// modify by wanglong
														// 20120815 使此方法通用
						int j = table.getTable().convertColumnIndexToModel(i);// modify
																				// by
																				// wanglong
																				// 20120815
																				// 使此方法通用
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
						// 表格中parm值一致,
						// 1.取paramw值;
						TParm tableData = table.getParmValue();// modify by
																// wanglong
																// 20120815
																// 使此方法通用
						// 2.转成 vector列名, 行vector ;
						String columnName[] = tableData.getNames("Data");
						String strNames = "";
						for (String tmp : columnName) {
							strNames += tmp + ";";
						}
						strNames = strNames.substring(0, strNames.length() - 1);
						Vector vct = getVector(tableData, "Data", strNames, 0);

						// 3.根据点击的列,对vector排序
						// System.out.println("sortColumn===="+sortColumn);
						// 表格排序的列名;
						String tblColumnName = table.getParmMap(sortColumn);// modify
																			// by
																			// wanglong
																			// 20120815
																			// 使此方法通用
						// 转成parm中的列
						int col = tranParmColIndex(columnName, tblColumnName);
						// System.out.println("==col=="+col);

						compare.setDes(ascending);
						compare.setCol(col);
						java.util.Collections.sort(vct, compare);
						// 将排序后的vector转成parm;
						// modify by wanglong 20120815
						// 为了使此方法通用，所以要求cloneVectoryParam()也得通用，不能仅限于deptFeeTable
						cloneVectoryParam(vct, new TParm(), strNames, table);
						// getTMenuItem("save").setEnabled(false);
					}
				});
		// 添加 wangzl 20120806 Stop
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
	@SuppressWarnings( { "rawtypes", "unchecked" })
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
		// 添加 wangzl 20120806 start
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
		// 添加 wangzl 20120806 stop
	}

	/**
	 * vectory转成param
	 */
	@SuppressWarnings("rawtypes")
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {// modify by wanglong
														// 20120815
														// 增加一个形参（为了使此方法通用）
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
		table.setParmValue(parmTable);// modify by wanglong 20120815 为了使此方法通用
	}
}