package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import java.sql.Timestamp;
import com.dongyang.ui.TComboBox;
import jdo.sys.IReportTool;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.Operator;
import jdo.bil.BILContractRecordTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.manager.TIOM_AppServer;
import action.bil.BILContractRecordAction;
import jdo.ekt.EKTIO;
import jdo.reg.PatAdmTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.opb.OPBReceiptTool;
import jdo.util.Manager;
import jdo.opd.OrderTool;

import com.javahis.manager.sysfee.sysOdrPackDObserver;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 记账单位账务结算
 * </p>
 * 
 * <p>
 * Description: 记账单位账务结算
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author pangben 20110817
 * @version 1.0
 */
public class BILContractRecordControl extends TControl {
	public BILContractRecordControl() {
	}

	private TTable table;

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		initPage();
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// 给table中的CHECKBOX添加侦听事件
//		callFunction("UI|table|addEventListener",
//				TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxClicked");

		table = (TTable) this.getComponent("table");
		onQuery();
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyyMMddHHmmss");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("E_DATE")), "yyyyMMddHHmmss");
		if (startTime.length() <= 0 || endTime.length() <= 0) {
			this.messageBox("请输入查询的时间");
			return;
		}
		TParm parm = new TParm();
		if (this.getValue("REGION_CODE").toString().length() > 0)
			parm
					.setData("REGION_CODE", this.getValue("REGION_CODE")
							.toString());
		// 记账单位
		if (this.getValueString("CONTRACT_CODE").length() > 0)
			parm.setData("CONTRACT_CODE", this.getValue("CONTRACT_CODE")
					.toString());
		// 状态
		if (this.getValueString("BIL_STATUS").length() > 0)
			parm.setData("BIL_STATUS", this.getValue("BIL_STATUS").toString());
		parm.setData("DATE_S", startTime);
		parm.setData("DATE_E", endTime);
		TParm result = BILContractRecordTool.getInstance().recodeQuery(parm);
		table.setParmValue(result);
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {

		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		setValue("S_DATE", yesterday);
		setValue("E_DATE", SystemTool.getInstance().getDate());
		setValue("REGION_CODE", Operator.getRegion());
		this.callFunction("UI|table|removeRowAll");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearValue("CONTRACT_CODE");
		initPage();
	}

	/**
	 * 保存方法， 执行修改OPD_ORDER 表中 PRINT_FLG=Y AND PRINT_NO 保存值 修改 BIL_REG_RECP 表中
	 * PRINT_DATE 保存时间 PRINT_NO 保存值 添加 BIL_INVRCP 表数据 修改 BIL_INVOICE 表数据增加票据号操作
	 * 修改 BIL_OPB_RECP 表中 PRINT_DATE 保存时间 PRINT_NO 保存值
	 */
	public void exeContract() {
		//this.messageBox(this.getValue("CASHIER_CODE").toString());
		if (null==this.getValue("CASHIER_CODE") || this.getValue("CASHIER_CODE").toString().length()<=0) {
			this.messageBox("请选择记账人员");
			this.grabFocus("CASHIER_CODE");
			return;		
		}
		TParm newParm=new TParm();
		TParm tableParm = table.getParmValue();
		boolean isY = false;
		int count=0;
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getBoolean("FLG", i)) {
				if ("Y".equals(tableParm.getValue("FLG", i))
						&& "2".equals(tableParm.getValue("BIL_STATUS",
								i))) {
					this.messageBox("账单号为:"+tableParm.getValue("RECEIPT_NO",i)+"已经完成,不可以修改");
					break;
				}
				isY = true;
				newParm.setRowData(count,tableParm,i);
				count++;
			}
		}
		newParm.setCount(count);
		// 有执行的数据
		if (isY) {
			TParm parm = new TParm();
			parm.setData("OPT_USER", this.getValue("CASHIER_CODE"));
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("recodeParm", newParm.getData());
			TParm result = TIOM_AppServer.executeAction(
					"action.bil.BILContractRecordAction", "onSave", parm);
			if (result.getErrCode() < 0) {
				this.messageBox("执行失败");
				return;
			}
			this.messageBox("执行成功");
			

			//onQuery();
		} else
			this.messageBox("请选择要执行的数据");
		onQuery();

	}

	/**
	 * 表格值改变事件：修改状态、检核结果列值
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public void onTableCheckBoxClicked(Object obj) {

		// 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		table.acceptText();
		TTable node = (TTable) obj;
		if (node == null)
			return;
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		//int column = node.getSelectedColumn();
		int selectRow = node.getSelectedRow();
		// 如果已经结账完成，将提示不可以保存
		if ("Y".equals(node.getParmValue().getValue("FLG", selectRow))
				&& "2".equals(node.getParmValue().getValue("BIL_STATUS",
						selectRow))) {
			this.messageBox("此账单已经完成,不可以修改");
			node.getParmValue().setData("FLG", selectRow, "N");
			table.setParmValue(node.getParmValue());
		}
	}

	/**
	 * 打印方法 已经结算的数据可以打印，打印的票据分两种：挂号打票、收费打票
	 */
	public void onPrint() {
		table.acceptText();
		TParm tableParm=table.getParmValue();
		boolean flg=false;
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getBoolean("FLG",i)) {
				flg=true;
				break;
			}
		}
		if (!flg) {
			this.messageBox("请选择要打印的账单");
			return;
		}
		// 挂号打票
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getBoolean("FLG", i)) {
				// 没有结算
				if ("1".equals(tableParm.getValue("BIL_STATUS", i))) {
					this.messageBox("账单号为:"+tableParm.getValue("RECEIPT_NO",i)+"没有结算,不可以打票");
					break;
				}
				if ("REG".equals(tableParm.getValue("RECEIPT_TYPE", i))) {
					regPrint(tableParm.getRow(i));
				} else {
					// 门诊打票
					opdPrint(tableParm.getRow(i));
				}
			}
		}
//		TParm parm = table.getParmValue();
//		
//		// 没有结算
//		if ("1".equals(parm.getValue("BIL_STATUS", row))) {
//			this.messageBox("此账单没有结算,不可以打票");
//			return;
//		}
//		
//		// 挂号打票
//		if ("REG".equals(parm.getValue("RECEIPT_TYPE", row))) {
//			regPrint(parm.getRow(row));
//		} else {
//			// 门诊打票
//			opdPrint(parm.getRow(row));
//		}
		
	}

	/**
	 * 挂号打票：泰心医院
	 */
	private void regPrint(TParm parm) {
		TParm result = PatAdmTool.getInstance().getRegPringDate(
				parm.getValue("CASE_NO"), "");
		StringBuffer sql = new StringBuffer();
		// 查找此次就诊的票据号
		sql.append("SELECT PRINT_NO FROM BIL_REG_RECP WHERE CASE_NO='"
				+ parm.getValue("CASE_NO") + "' AND RECEIPT_NO='"
				+ parm.getValue("RECEIPT_NO") + "'");
		TParm printNoParm = new TParm(TJDODBTool.getInstance().select(
				sql.toString()));
		result.setData("PRINT_NO", "TEXT", printNoParm.getValue("PRINT_NO", 0));
		result.setData("DEPT_NAME", "TEXT", result.getValue("DEPT_CODE_OPB")
				+ "   (" + result.getValue("CLINICROOM_DESC_OPB") + ")"); // 科室诊室名称
																			// 显示方式:科室(诊室)
		result.setData("CLINICTYPE_NAME", "TEXT", result
				.getValue("CLINICROOM_DESC_OPB")
				+ "   (" + result.getValue("QUE_NO_OPB") + "号)"); // 号别
																	// 显示方式:号别(诊号)
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // 年月日
		result.setData("BALANCE_NAME", "TEXT", "余 额"); // 余额名称
		result.setData("CURRENT_BALANCE", "TEXT", "￥ " + "0.00"); // 医疗卡剩余金额
		result.setData("PAY_DEBIT", "TEXT", "医保"); // 医保
		result.setData("PAY_CASH", "TEXT", "现金"); // 现金
		result.setData("DATE", "TEXT", yMd); // 日期
		result.setData("USER_NAME", "TEXT", Operator.getID()); // 收款人
//		this.openPrintWindow("%ROOT%\\config\\prt\\REG\\REGRECPPrint.jhw",
//				result);
	    this.openPrintWindow(IReportTool.getInstance().getReportPath("REGRECPPrintReport"),
                             IReportTool.getInstance().getReportParm("REGRECPPrintReportTool", result));//报表合并modify by wanglong 20130730
	}

	// /**
	// * 挂号打票
	// * @param parm TParm
	// */
	// private void regPrint(TParm parm) {
	// TParm result = PatAdmTool.getInstance().getRegPringDate(parm.getValue(
	// "CASE_NO"),
	// "");
	// StringBuffer sql = new StringBuffer();
	// //查找此次就诊的票据号
	// sql.append("SELECT PRINT_NO FROM BIL_REG_RECP WHERE CASE_NO='" +
	// parm.getValue(
	// "CASE_NO") + "' AND RECEIPT_NO='" +
	// parm.getValue("RECEIPT_NO") + "'");
	// TParm printNoParm = new TParm(TJDODBTool.getInstance().select(
	// sql.toString()));
	// result.setData("PRINT_NO", "TEXT", printNoParm.getValue("NEXT_NO", 0));
	// this.openPrintDialog("%ROOT%\\config\\prt\\reg\\REG_Print1.jhw",
	// result, false);
	//
	// }

	/**
	 * 门诊打票
	 * 
	 * @param parm
	 *            TParm
	 */
	// private void opdPrint(TParm parm) {
	// TParm recpParm = OPBReceiptTool.getInstance().getOneReceipt(parm.
	// getValue("RECEIPT_NO"));
	// System.out.println("门诊收据档数据" + recpParm);
	// // TParm regParm =
	// PatAdmTool.getInstance().getRegPringDate(parm.getValue(
	// // "CASE_NO"),
	// // "");
	// TParm oneReceiptParm = new TParm();
	// //票据信息
	// //姓名
	// oneReceiptParm.setData("PAT_NAME", "TEXT", parm.getValue("PAT_NAME"));
	// //社会保障号
	// oneReceiptParm.setData("Social_NO", "TEXT", "100000001");
	// //人员类别
	// oneReceiptParm.setData("CTZ_DESC", "TEXT", "职工医保");
	// //费用类别
	// oneReceiptParm.setData("Cost_class", "TEXT", "门统");
	// //医疗机构名称
	// oneReceiptParm.setData("HOSP_DESC", "TEXT",
	// Manager.getOrganization().
	// getHospitalCHNFullName(parm.getValue(
	// "REGION_CODE")));
	// //费用合计
	// oneReceiptParm.setData("TOT_AMT", "TEXT",
	// parm.getValue("AR_AMT"));
	// //统筹支付
	// oneReceiptParm.setData("Overall_pay", "TEXT",
	// StringTool.round(recpParm.
	// getDouble("Overall_pay", 0), 2));
	// //个人支付
	// oneReceiptParm.setData("Individual_pay", "TEXT",
	// parm.getValue("AR_AMT"));
	// //现金支付
	// oneReceiptParm.setData("Cash", "TEXT",
	// StringTool.round(recpParm.
	// getDouble("PAY_CASH", 0), 2));
	// //账户余额
	// oneReceiptParm.setData("Recharge", "TEXT",
	// StringTool.round(recpParm.
	// getDouble("Recharge", 0), 2));
	// //打印日期
	// oneReceiptParm.setData("OPT_DATE", "TEXT",
	// StringTool.getString(SystemTool.getInstance().
	// getDate(), "yyyy/MM/dd"));
	// //打印人
	// oneReceiptParm.setData("OPT_USER", "TEXT", Operator.getName());
	// TParm testparm = new TParm();
	// StringBuffer sql = new StringBuffer();
	// sql.append("SELECT ORDER_CODE,HIDE_FLG FROM OPD_ORDER WHERE CASE_NO ='" +
	// parm.getValue("CASE_NO") + "' AND RECEIPT_NO='" +
	// parm.getValue("RECEIPT_NO") + "'");
	// TParm tableinparmSum = new TParm(TJDODBTool.getInstance().select(
	// sql.toString()));
	// TParm tableinparm=new TParm();
	// //删除细项方法
	// int count=0;
	// for(int i=0;i<tableinparmSum.getCount();i++){
	// if ("N".equals(tableinparmSum.getValue("HIDE_FLG",i))){
	// tableinparm.addData("ORDER_CODE",tableinparmSum.getValue("ORDER_CODE",i));
	// count++;
	// }
	// }
	// tableinparm.setCount(count);
	// int number1 = 0;
	// int number2 = 0;
	// int number3 = 0;
	// int number4 = 0;
	// int number5 = 0;
	// int number6 = 0;
	// int number7 = 0;
	// int number8 = 0;
	// int number9 = 0;
	// int number10 = 0;
	// int number11 = 0;
	// int number12 = 0;
	// int number13 = 0;
	// int number14 = 0;
	// int number15 = 0;
	// int number16 = 0;
	// int number17 = 0;
	// int number18 = 0;
	// int number19 = 0;
	// for (int i = 0; i < tableinparm.getCount("ORDER_CODE"); i++) {
	// String order_CODE = tableinparm.getValue("ORDER_CODE", i);
	// String SQL =
	// "SELECT ORDER_DESC,REXP_CODE,SPECIFICATION,MEDI_QTY,AR_AMT,NHI_PRICE FROM OPD_ORDER WHERE ORDER_CODE='"
	// +
	// order_CODE + "'";
	// TParm rexp = new TParm(TJDODBTool.getInstance().select(SQL));
	// if (!recpParm.getValue("CHARGE01").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("101")) {
	// if (number1 == 0) {
	// testparm.addData("REG_NAME", "西药费");
	// testparm.addData("REG_TOT",
	// recpParm.getDouble("CHARGE01", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number1 = 1;
	// } else {
	// testparm.addData("REG_NAME", "");
	// testparm.addData("REG_TOT",
	// "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	//
	// if (!recpParm.getValue("CHARGE02").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("102")) {
	// if (number2 == 0) {
	// testparm.addData("REG_NAME", "中成药");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE02", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number2 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// }
	// if (!recpParm.getValue("CHARGE03").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("103")) {
	// if (number3 == 0) {
	// testparm.addData("REG_NAME", "中草药");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE03", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	// if (!recpParm.getValue("CHARGE04").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("104")) {
	// if (number4 == 0) {
	// testparm.addData("REG_NAME", "检查费");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE04", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number4 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// }
	// if (!recpParm.getValue("CHARGE05").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("105")) {
	// if (number5 == 0) {
	// System.out.println("化验费");
	// testparm.addData("REG_NAME", "化验费");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE05", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number5 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// }
	// if (!recpParm.getValue("CHARGE06").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("106")) {
	// if (number6 == 0) {
	// System.out.println("治疗费");
	// testparm.addData("REG_NAME", "治疗费");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE06", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number6 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", " ");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	// if (!recpParm.getValue("CHARGE07").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("107")) {
	// if (number7 == 0) {
	// testparm.addData("REG_NAME", "护理费");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE07", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number7 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	// if (!recpParm.getValue("CHARGE08").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("108")) {
	// if (number8 == 0) {
	// testparm.addData("REG_NAME", "诊查费");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE08", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number8 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	// if (!recpParm.getValue("CHARGE09").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("109")) {
	// if (number9 == 0) {
	// testparm.addData("REG_NAME", "X光费");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE09", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number9 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	// if (!recpParm.getValue("CHARGE10").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("110")) {
	// if (number10 == 0) {
	// testparm.addData("REG_NAME", "CT费");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE10", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number10 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	// if (!recpParm.getValue("CHARGE11").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("111")) {
	// if (number11 == 0) {
	// testparm.addData("REG_NAME", "手术费");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE11", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number11 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", " ");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	// if (!recpParm.getValue("CHARGE12").equals("") &&
	// rexp.getValue("CHARGE12", 0).equals("112")) {
	// testparm.addData("REG_NAME", "输血费");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE12", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// if (!recpParm.getValue("CHARGE13").equals("") &&
	// rexp.getValue("CHARGE13", 0).equals("113")) {
	// testparm.addData("REG_NAME", "输氧费");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE13", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// if (!recpParm.getValue("CHARGE14").equals("") &&
	// rexp.getValue("CHARGE14", 0).equals("114")) {
	// testparm.addData("REG_NAME", "其他");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE14", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// if (!recpParm.getValue("CHARGE15").equals("") &&
	// rexp.getValue("CHARGE15", 0).equals("115")) {
	// testparm.addData("REG_NAME", "床位费");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE15", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// if (!recpParm.getValue("CHARGE16").equals("") &&
	// rexp.getValue("CHARGE16", 0).equals("116")) {
	// testparm.addData("REG_NAME", "冷暖费");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE16", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// if (!recpParm.getValue("CHARGE17").equals("") &&
	// rexp.getValue("CHARGE17", 0).equals("117")) {
	// testparm.addData("REG_NAME", "监护费");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE17", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// if (!recpParm.getValue("CHARGE18").equals("") &&
	// rexp.getValue("CHARGE18", 0).equals("118")) {
	// testparm.addData("REG_NAME", "接生费");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE18", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// if (!recpParm.getValue("CHARGE19").equals("") &&
	// rexp.getValue("CHARGE19", 0).equals("119")) {
	// testparm.addData("REG_NAME", "婴儿费");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE19", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	//
	// }
	// testparm.setCount(tableinparm.getCount("ORDER_CODE"));
	// testparm.addData("SYSTEM", "COLUMNS", "REG_NAME");
	// testparm.addData("SYSTEM", "COLUMNS", "REG_TOT");
	// testparm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
	// testparm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
	// testparm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
	// testparm.addData("SYSTEM", "COLUMNS", "AR_AMT");
	// testparm.addData("SYSTEM", "COLUMNS", "ORDER_CASH");
	// testparm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
	// oneReceiptParm.setData("TABLE", testparm.getData());
	// System.out.println("testparm::"+testparm);
	// if (!testparm.getData("REG_NAME", 0).equals("")) {
	// oneReceiptParm.setData("PAT_NAME1", "TEXT",
	// parm.getValue("PAT_NAME"));
	// oneReceiptParm.setData("FEE_CLASS1", "TEXT",
	// testparm.getData("REG_NAME", 0));
	// oneReceiptParm.setData("TOT1", "TEXT",
	// testparm.getData("REG_TOT", 0));
	// oneReceiptParm.setData("PRINT_DATE1", "TEXT",
	// StringTool.getString(SystemTool.getInstance().
	// getDate(), "yyyy/MM/dd"));
	// oneReceiptParm.setData("OPT_UESR", "TEXT", Operator.getName());
	// oneReceiptParm.setData("CASE_NO1", "TEXT", "门统");
	// }
	// if (null != testparm.getData("REG_NAME", 1) &&
	// !testparm.getData("REG_NAME", 1).equals("")) {
	// oneReceiptParm.setData("PAT_NAME2", "TEXT",
	// parm.getValue("PAT_NAME"));
	// oneReceiptParm.setData("FEE_CLASS2", "TEXT",
	// testparm.getData("REG_NAME", 1));
	// oneReceiptParm.setData("TOT2", "TEXT",
	// testparm.getData("REG_TOT", 1));
	// oneReceiptParm.setData("PRINT_DATE2", "TEXT",
	// StringTool.getString(SystemTool.getInstance().
	// getDate(), "yyyy/MM/dd"));
	// oneReceiptParm.setData("OPT_UESR", "TEXT", Operator.getName());
	// oneReceiptParm.setData("CASE_NO2", "TEXT", "门统");
	// }
	//
	// if (null != testparm.getData("REG_NAME", 2) &&
	// !testparm.getData("REG_NAME", 2).equals("")) {
	// oneReceiptParm.setData("PAT_NAME3", "TEXT",
	// parm.getValue("PAT_NAME"));
	// oneReceiptParm.setData("FEE_CLASS3", "TEXT",
	// testparm.getData("REG_NAME", 2));
	// oneReceiptParm.setData("TOT3", "TEXT",
	// testparm.getData("REG_TOT", 2));
	// oneReceiptParm.setData("PRINT_DATE3", "TEXT",
	// StringTool.getString(SystemTool.getInstance().
	// getDate(), "yyyy/MM/dd"));
	// oneReceiptParm.setData("OPT_UESR", "TEXT", Operator.getName());
	// oneReceiptParm.setData("CASE_NO3", "TEXT", "门统");
	// }
	// System.out.println("医疗卡打票数据" + oneReceiptParm);
	// this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPB_Print1.jhw",
	// oneReceiptParm);
	//
	// }
	/**
	 * 门诊打票:泰心医院
	 * 
	 * @param parm
	 *            TParm
	 */
	private void opdPrint(TParm parm) {
		TParm oneReceiptParm = new TParm();
		// 门诊收据档数据:现金收费打票
		TParm recpParm = OPBReceiptTool.getInstance().getOneReceipt(
				parm.getValue("RECEIPT_NO"));
		// 票据信息
		// 姓名
		oneReceiptParm.setData("PAT_NAME", "TEXT", parm.getValue("USER_NAME"));
		oneReceiptParm.setData("PAT_NAME", "TEXT", parm.getValue("USER_NAME"));
		// 医疗机构名称
		oneReceiptParm.setData("HOSP_DESC", "TEXT", Operator
				.getHospitalCHNFullName());
		// 费用合计
		oneReceiptParm.setData("TOT_AMT", "TEXT", recpParm.getDouble("TOT_AMT",
				0));
		// 费用显示大写金额
		oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
				.numberToWord(recpParm.getDouble("TOT_AMT", 0)));

//		// 个人支付
//		oneReceiptParm.setData("Individual_pay", "TEXT", recpParm.getDouble(
//				"TOT_AMT", 0));
		
		
		// 现金支付
		oneReceiptParm.setData("Cash", "TEXT", StringTool.round(recpParm
				.getDouble("PAY_CASH", 0), 2));
		// 打印日期
		oneReceiptParm.setData("OPT_DATE", "TEXT", StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy  MM  dd"));
		// 医保金额
		oneReceiptParm.setData("PAY_DEBIT", "TEXT", StringTool.round(recpParm
				.getDouble("PAY_DEBIT", 0), 2));
		// 医生名称
		oneReceiptParm.setData("DR_NAME", "TEXT","");
//		oneReceiptParm.setData("DR_NAME", "TEXT", recpParm.getValue(
//				"CASHIER_CODE", 0));
		//医保统筹支付
		oneReceiptParm.setData("PAY_DEBIT","TEXT","0.00");
		//个人账户支付
        oneReceiptParm.setData("DA_AMT","TEXT","0.00");
        //其他医保支付
        oneReceiptParm.setData("QTYL", "TEXT", "0.00");
		// 打印人
		oneReceiptParm.setData("OPT_USER", "TEXT", Operator.getName());
		oneReceiptParm.setData("USER_NAME", "TEXT", Operator.getID());
		
		//医疗机构类型
		String regionSql = "SELECT HOSP_CLASS FROM SYS_REGION WHERE REGION_CODE = '"+ Operator.getRegion()+"'";
 	    TParm regionParm = new TParm(TJDODBTool.getInstance().select(regionSql));
 	    String hospClass = regionParm.getValue("HOSP_CLASS",0);
 	    String sqlhospClass = "SELECT CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_HOSPITAL_CLASS' AND ID = '"+hospClass+"'";
 	    TParm  hospClassParm =  new TParm(TJDODBTool.getInstance().select(sqlhospClass));
 	    oneReceiptParm.setData("HOSP_CLASS","TEXT",hospClassParm.getValue("CHN_DESC",0));

		// if (this.getValue("BILL_TYPE").equals("E")){
		TParm EKTTemp = EKTIO.getInstance().TXreadEKT();
		if (null == EKTTemp || EKTTemp.getValue("MR_NO").length() <= 0) {
			this.messageBox("医疗卡没有使用不可以打票");
			return;
		}
		String mrNo = EKTTemp.getValue("MR_NO");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		oneReceiptParm.setData("SEX", "TEXT", pat.getSexString());
		oneReceiptParm.setData("MR_NO", "TEXT", EKTTemp.getValue("MR_NO"));
		oneReceiptParm.setData("START_AMT", "TEXT", ""); // 起付金额
		oneReceiptParm.setData("MAX_AMT", "TEXT", ""); // 最高限额余额
		oneReceiptParm.setData("TEXT_TITLE1", "TEXT", "(详见费用清单)");
		oneReceiptParm.setData("TEXT_TITLE", "TEXT", "门诊联网已结算");
		oneReceiptParm.setData("CARD_CODE", "TEXT", EKTTemp.getValue("MR_NO")
				+ EKTTemp.getValue("SEQ"));
		oneReceiptParm.setData("COPY", "TEXT", "(COPY)");
		for (int i = 1; i <= 30; i++) {
			if (i < 10)
				oneReceiptParm.setData("CHARGE0" + i, "TEXT", recpParm
						.getDouble("CHARGE0" + i, 0) == 0 ? "0.00" : recpParm
						.getData("CHARGE0" + i, 0));
			else
				oneReceiptParm.setData("CHARGE" + i, "TEXT", recpParm
						.getDouble("CHARGE" + i, 0) == 0 ? "0.00" : recpParm
						.getData("CHARGE" + i, 0));
		}
	    oneReceiptParm.setData("RECEIPT_NO", "TEXT", parm.getValue("RECEIPT_NO"));//add by wanglong 20121217
//		this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint.jhw",
//				oneReceiptParm);
	      this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
                               IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm));//报表合并modify by wanglong 20130730

	}

	/**
	 * 汇出
	 */
	public void onExport() {
		ExportExcelUtil.getInstance().exportExcel(table, "记账单位账务结算报表");
	}

	/**
	 * 全选按钮
	 */
	public void onSelectAll() {
		TParm parm = table.getParmValue();
		if (parm.getCount() <= 0) {
			return;
		}
		// 全选勾选
		if (this.getValueBoolean("CHK_ALL")) {
			for (int i = 0; i < parm.getCount(); i++) {
				parm.setData("FLG",i,"Y");
				
			}
		} else {
			for (int i = 0; i < parm.getCount(); i++) {
				parm.setData("FLG",i,"N");	
			}
			
		}
		table.setParmValue(parm);
	}
}
