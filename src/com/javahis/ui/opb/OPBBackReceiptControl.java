package com.javahis.ui.opb;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;

import jdo.odo.OpdOrderHistory;
import jdo.opb.OPB;
import jdo.opb.OPBTool;

import com.dongyang.data.TParm;

import jdo.sys.Pat;
import jdo.reg.REGCcbReTool;
import jdo.reg.Reg;
import jdo.opb.OPBReceiptList;

import com.dongyang.jdo.TJDODBTool;

import jdo.opd.OrderTool;
import jdo.opb.OPBReceiptTool;

import com.dongyang.manager.TIOM_AppServer;

import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.util.StringTool;

import jdo.reg.PatAdmTool;
import jdo.util.Manager;



import com.javahis.ui.testOpb.tools.AssembleTool;
import com.javahis.util.StringUtil;
import com.tiis.util.TiMath;

import jdo.bil.BilInvoice;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTTool;
import jdo.hl7.Hl7Communications;
import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJReg;

import com.dongyang.util.TypeTool;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * <p>
 * Title: 门诊费用明细查询
 * </p>
 * 
 * <p>
 * Description: 门诊费用明细查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author fudw 2009-07-08
 * @version 1.0
 */
public class OPBBackReceiptControl extends TControl {
	/**
	 * 票据档
	 */
	TTable tableM;
	/**
	 * 医嘱档
	 */
	TTable tableD;
	/**
	 * 计价对象
	 */
	OPB opb;
	/**
	 * table数据
	 */
	TParm tableMParm;
	/**
	 * 印刷号
	 */
	String printNoOnly;
	/**
	 * 退费权限
	 */
	boolean backBill = false;
	private String printNo;// 票号
	private Timestamp bill_date;// 收费日期
	// 批号
	private TParm batchNoParm;

	private TParm ektParm;// 医疗卡操作
	private TParm parmEKT;// 医疗卡读卡数据
	private String nhiRegionCode;// 医保区域号码
	// =====zhangp 20120229
	private int insType = 0;
	TParm sendHL7Parm = new TParm();
	// ====zhangp 20120229
	private boolean cashFLg = false;// 现金退费
	private TParm regionParm;
	private String type = "";// 操作类型 1：现金 2：医疗卡 3:医保卡
	private boolean insFlg = false;// 是否操作医保
	// private double totAmt = 0.00;// 总金额
	private String caseNo;
	private boolean printFlg = false;//遗失补印
	private String CONFIRMNO = "";// 医保号
	private String oldMrNo = "";

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		tableM = (TTable) this.getComponent("TABLEM");
		tableD = (TTable) this.getComponent("TABLED");
		// 处理数据
		dealData();
		batchNoParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT ORDER_CODE, BATCH_NO FROM IND_STOCK"));
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // 获得医保区域代码
		
		
		// 初始化票据  add by huangtt 20150630 
		BilInvoice bilInvoice = new BilInvoice();
		initBilInvoice(bilInvoice.initBilInvoice("OPB"));
		
	}
	
	/**
	 * 初始化票据     add by huangtt 20150630
	 * 
	 * @param bilInvoice
	 *            BilInvoice
	 * @return boolean
	 */
	private boolean initBilInvoice(BilInvoice bilInvoice) {
		// 检核开关帐
		if (bilInvoice == null) {
			this.messageBox_("你尚未开账!");
			return false;
		}
		// 检核当前票号
		if (bilInvoice.getUpdateNo().length() == 0
				|| bilInvoice.getUpdateNo() == null) {
			this.messageBox_("无可打印的票据!");
			// this.onClear();
			return false;
		}
		// 检核当前票号
		if (bilInvoice.getUpdateNo().equals(bilInvoice.getEndInvno())) {
			this.messageBox_("最后一张票据!");
		}
		callFunction("UI|UPDATE_NO|setValue", bilInvoice.getUpdateNo());
		return true;
	}

	/**
	 * 处理数据
	 */
	public void dealData() {
		// 初始化权限
		if (!initPopedem())
			return;
		// //把数据放入界面
		// if (!getReceiptList())
		// return;
		// 得到收费列表
		getReceiptParm();
	}

	/**
	 * 接受前台传来数据和初始化权限
	 * 
	 * @return boolean
	 */
	public boolean initPopedem() {
		TParm parm;
		// 前台传来的计价对象
		if (this.getParameter() != null) {
			parm = (TParm) this.getParameter();
			// 加载opb
			if (!initOpb(parm))
				return false;
		}
		// 退费权限
		if (!getPopedem("BACKBILL")) {
			backBill = false;
		}
		return true;
	}

	/**
	 * 加载opb
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public boolean initOpb(TParm parm) {
		caseNo = parm.getValue("CASE_NO");
		String mrNo = parm.getValue("MR_NO");
		oldMrNo = parm.getValue("MR_NO_OLD");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox("查无此病案号");
			return false;
		}
		// 界面赋值
		setValueForParm("MR_NO;PAT_NAME;IDNO;SEX_CODE;COMPANY_DESC", pat
				.getParm());
		Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
		// 判断挂号信息
		if (reg == null)
			return false;
		// 三身份
		callFunction("UI|CTZ1_CODE|setValue", reg.getCtz1Code());
		callFunction("UI|CTZ2_CODE|setValue", reg.getCtz2Code());
		callFunction("UI|CTZ3_CODE|setValue", reg.getCtz3Code());
		// 通过reg和caseNo得到pat
		opb = OPB.onQueryByCaseNo(reg);
		// 给界面上部分地方赋值
		if (opb == null) {
			this.messageBox("此病人尚未就诊!");
			return false;
		}
		return true;
	}

	/**
	 * 初始化收费列表
	 * 
	 * @return boolean
	 */
	public boolean getReceiptList() {
		OPBReceiptList opbReceiptList = new OPBReceiptList()
				.initReceiptList(opb.getReg().caseNo());
		if (opbReceiptList == null)
			return false;
		opb.setReceiptList(opbReceiptList);
		return true;
	}

	/**
	 * 得到收费界面上收费列表
	 */
	public void getReceiptParm() {
		TParm parm = OPBReceiptTool.getInstance().getReceipt(
				opb.getReg().caseNo());
		// opb.getReceiptList().getParm(opb.getReceiptList().PRIMARY);
		tableM.setParmValue(parm);
		// opb.getReceiptList().initOrder(opb.getPrescriptionList());
	}

	/**
	 * 主表点击事件
	 */
	public void onClickTableM() {
		tableMParm = new TParm();
		cashFLg = false;
		type = "";
		insFlg = false;
		CONFIRMNO = "";
		int row = tableM.getSelectedRow();
		TParm tableParm = tableM.getParmValue();
		printNo = tableParm.getValue("PRINT_NO", row);// 票号
		bill_date = tableParm.getTimestamp("BILL_DATE", row);// 收费日期
		// System.out.println("主表数据"+tableParm);
		// //拿到一张票据
		// OPBReceipt opbReceipt = (OPBReceipt) opb.getReceiptList().get(row);
		// //得到其中的parm
		// TParm parm = opbReceipt.getOrderList().getParm(OrderList.PRIMARY);
		TParm orderParm = new TParm();
		TParm parm = null;
		// String recpType=tableParm.getValue("RECP_TYPE",row);
		// 医疗卡医嘱显示
		TParm tempParm = new TParm();
		tempParm.setData("PRINT_NO", printNo);
		ektParm = OPBReceiptTool.getInstance().seletEktState(tempParm);// 查询此次就诊是否是医疗卡操作
		// System.out.println("ektParm::"+ektParm);
		orderParm.setData("RECEIPT_NO", tableParm.getData("RECEIPT_NO", row));
		orderParm.setData("CASE_NO", tableParm.getData("CASE_NO", row));
		//=====zhangp 20120925 start
		//=====zhangp 20121217 start
//		if(tableParm.getDouble("PAY_INS_CARD", row) == 0){
//			parm = OrderTool.getInstance().queryFill(orderParm);
//		}else{
			parm = getInsRuleParm(orderParm);
//		}
		//=====zhangp 20121217 end
		//=====zhangp 20120925 end
		// System.out.println("明细表数据"+parm);
		tableMParm = parm;
		tableD.setParmValue(parm);
		// // 校验是否已到检或已发药
		// int count = parm.getCount("EXEC_FLG");
		// String exeFlg = "";
		// for (int i = 0; i < count; i++) {
		// exeFlg = parm.getValue("EXEC_FLG", i);
		// if ("Y".equals(exeFlg)) {
		// returnFeeFlg = true;
		// return;
		// }
		// }
	}

	/**
	 * 退费入口
	 * 
	 * @return boolean
	 */
	public boolean onSave() {
		int row = tableM.getSelectedRow();
		if (row < 0) {
			messageBox("请选择要退费的票据!");
		}
		switch (this.messageBox("提示信息", "是否退费", this.YES_NO_OPTION)) {
		case 0:
			if (!backReceipt(row))
				return false;
			break;
		case 1:
			return true;
		}
		return true;
	}

	/**
	 * 退费方法
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean backReceipt(int row) {
		// if (returnFeeFlg) {
		// this.messageBox("已发药或已到检，不可退费!");
		// return false;
		// }
		// if (!opb.onSaveBackReceipt(row)) {
		if (!onSaveBackReceipt(row)) {
			// 保存失败
			// messageBox("E0001");
			return false;
		}
		// 保存成功
		messageBox("P0001");
		if (cashFLg) {
			sendHL7Mes(sendHL7Parm);
		}
		// 保存后重新初始化票据
		afterSave();
		return true;
	}

	/**
	 * 保存后重新初始化票据
	 */
	public void afterSave() {
		// //把数据放入界面
		// if (!getReceiptList())
		// return;
		// 得到收费列表
		dealData();
		getReceiptParm();
		tableD.removeRowAll();
		// ====zhangp 20120229
		insType = 0;
		// =====zhangp 20120229
		// 初始化票据
		BilInvoice bilInvoice = new BilInvoice();
		initBilInvoice(bilInvoice.initBilInvoice("OPB"));
	}

	// /**
	// * 打印费用清单
	// */
	// public void onFill() {
	// if (opb == null)
	// return;
	// System.out.println("tableMParm" + tableMParm);
	// int count = tableMParm.getCount("SETMAIN_FLG");
	// String setmainFlg = "";
	// for (int i = count - 1; i >= 0; i--) {
	// setmainFlg = tableMParm.getValue("SETMAIN_FLG", i);
	// if ("Y".equals(setmainFlg)) {
	// tableMParm.removeRow(i);
	// }
	// }
	// TParm parm = opb.getReceiptList().dealTParm(tableMParm);
	// if (parm == null)
	// return;
	// TParm pringListParm = new TParm();
	// pringListParm.setData("TABLEORDER", parm.getData());
	// //病案号
	// pringListParm.setData("MR_NO", opb.getPat().getMrNo());
	// //病患姓名
	// pringListParm.setData("PAT_NAME", opb.getPat().getName());
	// //就诊序号
	// pringListParm.setData("CASE_NO", opb.getReg().caseNo());
	// String sql =
	// " SELECT CHN_DESC FROM SYS_DICTIONARY " +
	// "  WHERE GROUP_ID ='SYS_SEX' " +
	// "    AND ID = '" + opb.getPat().getSexCode() + "'";
	// TParm sexParm = new TParm(TJDODBTool.getInstance().select(sql));
	// //性别
	// pringListParm.setData("SEX_CODE", sexParm.getValue("CHN_DESC", 0));
	// //就诊日期
	// pringListParm.setData("ADM_DATE", opb.getReg().getAdmDate());
	// //医院名称
	// pringListParm.setData("HOSP",Operator.getHospitalCHNFullName()+"门诊费用清单");
	// this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBOrderList.jhw",
	// pringListParm);
	// }
	/**
	 * 打印费用清单 ============泰心医院费用清单打印
	 */
	public void onFill() {
		if(this.messageBox("提示信息", "是否打印费用清单", 2) != 0)//===pangben 2013-9-2 添加提示
			return;
		DecimalFormat df = new DecimalFormat("##########0.00");
		if (opb == null)
			return;
		// System.out.println("tableMParm" + tableMParm);
		// ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;
		// DOSAGE_QTY;DOSAGE_UNIT;OWN_PRICE;AR_AMT
		int count = tableMParm.getCount("SETMAIN_FLG");
		String setmainFlg = "";
		for (int i = count - 1; i >= 0; i--) {
			setmainFlg = tableMParm.getValue("SETMAIN_FLG", i);// 集合医嘱注记
			if ("Y".equals(setmainFlg)) {
				tableMParm.removeRow(i);
			}
		}
		// System.out.println("batchNoParm:::"+batchNoParm);
		TParm parm = opb.getReceiptList().dealTParm(tableMParm, batchNoParm);
		if (parm == null)
			return;
		double sum = parm.getDouble("SUM");// 合计金额
		TParm pringListParm = new TParm();
		pringListParm.setData("TABLEORDER", parm.getData());
		// 病患姓名
		pringListParm.setData("PAT_NAME", "TEXT", opb.getPat().getName());// 患者姓名
		pringListParm
				.setData("HOSP", "TEXT", Operator.getHospitalCHNFullName());// 医院名称
		pringListParm.setData("TITLE", "TEXT", Operator
				.getHospitalCHNFullName());// 医院名称
		pringListParm.setData("BILL_DATE", "TEXT", StringTool.getString(
				TypeTool.getTimestamp(bill_date), "yyyyMMddHHmmss"));// 收费日期
		pringListParm.setData("PRINT_NO", "TEXT", printNo);// 票号
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // 年月日
		pringListParm.setData("DATE", "TEXT", yMd);// 日期
		pringListParm.setData("TOTAL", "TEXT", df.format(StringTool.round(sum,
				2)));// 日期
		// System.out.println("parm==============" + parm);
//		this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBOrderPrint.jhw",
//				pringListParm);
	      this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBOrderPrint.jhw"),
	                           IReportTool.getInstance().getReportParm("OPBOrderPrint.class", pringListParm));//报表合并modify by wanglong 20130730
	}

	/**
	 * 补打票据
	 */
	public void onPrint() {
		int row = tableM.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要打印的数据");
			return;
		}
		if (opb.getBilInvoice().getUpdateNo().compareTo(
				opb.getBilInvoice().getEndInvno()) > 0) {
			this.messageBox("票据已用完!");
			return;
		}
		if(this.messageBox("提示", "是否执行补印操作", 2) != 0){//===pangben 2013-9-2 添加提示
			return ;
		}
		if (!onSaveRePrint(row)) {
			messageBox("补印失败!");
			
			return;
		}
//		messageBox("保存成功");
		// //拿到一张票据
		// OPBReceipt opbReceipt = (OPBReceipt) opb.getReceiptList().get(row);
		// if (opbReceipt == null)
		// return;
		TParm saveParm = tableM.getParmValue();
		TParm actionParm = saveParm.getRow(row);
		String receiptNo = actionParm.getValue("RECEIPT_NO");
		TParm recpParm = null;
		// 门诊收据档数据:医疗卡收费打票\现金收费打票
		recpParm = OPBReceiptTool.getInstance().getOneReceipt(receiptNo);
		if (ektParm.getCount("CASE_NO") > 0) {
			onPrint(recpParm, true);
		} else {
			onPrint(recpParm, false);
		}
		// 保存后重新初始化票据
		afterSave();
		
		
	}
	
	/**
	 * 遗失补印
	 */
	public void onLostPrint(){
		printFlg = true;
		int row = tableM.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要打印的数据");
			return;
		}
		if (opb.getBilInvoice().getUpdateNo().compareTo(
				opb.getBilInvoice().getEndInvno()) > 0) {
			this.messageBox("票据已用完!");
			return;
		}
		if(this.messageBox("提示", "是否执行遗失补印操作", 2) != 0){//===pangben 2013-9-2 添加提示
			return ;
		}
		
		TParm saveParm = tableM.getParmValue();
		TParm actionParm = saveParm.getRow(row);
		String receiptNo = actionParm.getValue("RECEIPT_NO");
		TParm recpParm = null;
		// 门诊收据档数据:医疗卡收费打票\现金收费打票
		recpParm = OPBReceiptTool.getInstance().getOneReceipt(receiptNo);
		if (ektParm.getCount("CASE_NO") > 0) {
			onPrint(recpParm, true);
			
		} else {
			onPrint(recpParm, false);
		
		}
		
		// 初始化票据
		BilInvoice bilInvoice = new BilInvoice();
		initBilInvoice(bilInvoice.initBilInvoice("OPB"));
		
	}
	
	/**
	 * 打印票据封装===================pangben 20111014
	 * 
	 * @param recpParm
	 *            TParm
	 * @param flg
	 *            boolean
	 */
	private void onPrint(TParm recpParm, boolean flg) {
		DecimalFormat df = new DecimalFormat("0.00");
		TParm oneReceiptParm = new TParm();
		TParm insOpdInParm = new TParm();
		String confirmNo = "";
		String cardNo = "";
		String insCrowdType = "";
		String insPatType = "";
		// 特殊人员类别代码
		String spPatType = "";
		// 特殊人员类别
		String spcPerson = "";
		double startStandard = 0.00; // 起付标准
		double accountPay = 0.00; // 个人实际帐户支付
		double gbNhiPay = 0.00; // 医保支付
		String reimType = ""; // 报销类别
		double gbCashPay = 0.00; // 现金支付
		double agentAmt = 0.00; // 补助金额
		double unreimAmt = 0.00;// 基金未报销金额
		double illnesssubsidyamt = 0.00; //城乡大病金额
		TParm parm = new TParm();
		double servantAmt = 0.00;
		parm.setData("CASE_NO", opb.getReg().caseNo());
		parm.setData("INV_NO", recpParm.getValue("PRINT_NO", 0));
		parm.setData("RECP_TYPE", "OPB");// 收费类型
		// 查询是否医保 退费
//		System.out.println("parm="+parm);
		TParm result = INSOpdTJTool.getInstance().selectInsInvNo(parm);
//		System.out.println("result:::"+result);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		// 医保打票操作
		if (null != result && null != result.getValue("CONFIRM_NO", 0)
				&& result.getValue("CONFIRM_NO", 0).length() > 0) {
			parm.setData("CONFIRM_NO", result.getValue("CONFIRM_NO", 0));
			TParm mzConfirmParm = INSMZConfirmTool.getInstance()
					.queryMZConfirm(parm);
			confirmNo = result.getValue("CONFIRM_NO", 0);
			cardNo = mzConfirmParm.getValue("INSCARD_NO", 0);// 医保卡号
			insOpdInParm.setData("CASE_NO", opb.getReg().caseNo());
			insOpdInParm.setData("CONFIRM_NO", confirmNo);
			TParm insOpdParm = INSOpdTJTool.getInstance().queryForPrint(
					insOpdInParm);
			unreimAmt = insOpdParm.getDouble("UNREIM_AMT", 0);// 基金未报销
			TParm insPatparm = INSOpdTJTool.getInstance().selPatDataForPrint(
					insOpdInParm);
			insCrowdType = insOpdParm.getValue("INS_CROWD_TYPE", 0); // 1.城职
			// 2.城居
			insPatType = insOpdParm.getValue("INS_PAT_TYPE", 0); // 1.普通
			// 特殊人员类别代码
			String sql = " SELECT SPECIAL_PAT" +
			" FROM INS_MZ_CONFIRM " +
			" WHERE CASE_NO = '"+insOpdInParm.getData("CASE_NO")+"'" +
			" AND CONFIRM_NO ='"+confirmNo+"'";
	        TParm PAT = new TParm(TJDODBTool.getInstance().select(sql));			
	        spPatType = PAT.getValue("SPECIAL_PAT", 0);
			// 特殊人员类别
			spcPerson = getSpPatDesc(spPatType);
			//报销类别
			reimType = insOpdParm.getValue("REIM_TYPE", 0);
			
			// 城职普通
			if (insCrowdType.equals("1") && insPatType.equals("1")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				if (reimType.equals("1"))
				{	gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);
				}
				else{
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ARMY_AI_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							;
					}
				gbNhiPay = TiMath.round(gbNhiPay, 2);

				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				// 补助金额
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
				
			}
			// 城职门特
			if (insCrowdType.equals("1") && insPatType.equals("2")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				if (reimType.equals("1"))
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							;
				else
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ARMY_AI_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							;
				gbNhiPay = TiMath.round(gbNhiPay, 2);
				// 个人实际帐户支付
				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				// 现金支付
				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				// 补助金额
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
			// 城居门特
			if (insCrowdType.equals("2") && insPatType.equals("2")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				// 个人实际帐户支付
				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				if (reimType.equals("1"))
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
							+ insOpdParm.getDouble("ARMY_AI_AMT", 0)
							+ insOpdParm.getDouble("FLG_AGENT_AMT", 0)
							+ insOpdParm.getDouble("ILLNESS_SUBSIDY_AMT", 0)//城乡大病金额
							- insOpdParm.getDouble("UNREIM_AMT", 0);
				else
					//医保支付
					gbNhiPay = insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
							+ insOpdParm.getDouble("FLG_AGENT_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);
				gbNhiPay = TiMath.round(gbNhiPay, 2);
				// 现金支付
				gbCashPay = insOpdParm.getDouble("TOT_AMT", 0)
						- insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
						- insOpdParm.getDouble("FLG_AGENT_AMT", 0)
						- insOpdParm.getDouble("ARMY_AI_AMT", 0)
						- insOpdParm.getDouble("ILLNESS_SUBSIDY_AMT", 0)//城乡大病金额
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				gbCashPay = TiMath.round(gbCashPay, 2);
				// 补助金额
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
				
				//城乡大病金额
				illnesssubsidyamt = insOpdParm.getDouble("ILLNESS_SUBSIDY_AMT", 0);
			}
		}
		//查询人员类别
		String CtzDescSql =" SELECT B.CTZ_DESC,C.USER_NAME FROM REG_PATADM A,SYS_CTZ B,SYS_OPERATOR C"+
							" WHERE A.CASE_NO = '"+ opb.getReg().caseNo() +"'"+
							" AND A.CTZ1_CODE = B.CTZ_CODE"+
							" AND A.REALDR_CODE =C.USER_ID";		 
		TParm CtzDescParm = new TParm(TJDODBTool.getInstance().select(CtzDescSql));
	
		//人员类别
		String personClass = CtzDescParm.getValue("CTZ_DESC", 0);
		// 票据信息	
		oneReceiptParm.setData("PAT_NAME", "TEXT", opb.getPat().getName());// 姓名
		// 特殊人员类别
		oneReceiptParm.setData("SPC_PERSON", "TEXT",
				(personClass+spcPerson).length() == 0 ? "自费" : personClass+" "+spcPerson);
		// 人员类别
		oneReceiptParm.setData("CTZ_DESC", "TEXT", "职工医保");
		oneReceiptParm.setData("COPY", "TEXT", "(COPY)");			
		// 费用类别
		if ("1".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "门大联网已结算");
			if (opb.getReg().getAdmType().equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
		} else if ("2".equals(insPatType) || "3".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "门特联网已结算");
			if (opb.getReg().getAdmType().equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
		}else {
			oneReceiptParm.setData("ADVANCE_TITLE", "TEXT", "天津医保垫付患者需回医院补联网");
		}	
		// 医院名称
		oneReceiptParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(opb.getReg().getRegion()));
//		// 起付金额
//		oneReceiptParm.setData("START_AMT", "TEXT", df.format(startStandard));
		//基金未报销显示文字======pangben 2012-7-12
		oneReceiptParm.setData("MAX_DESC", "TEXT", unreimAmt == 0 ? "" : "基金未报销金额:");
		// 最高限额余额
		oneReceiptParm.setData("MAX_AMT", "TEXT", unreimAmt == 0 ? "" : df
				.format(unreimAmt));
		//已超规定刷卡限额个人垫付年终报销
		oneReceiptParm.setData("MAX_DESC2", "TEXT", unreimAmt == 0 ? "" : "/已超规定刷卡限额个人垫付年终报销");
		// 个人账户支付
		oneReceiptParm.setData("DA_AMT", "TEXT", df.format(accountPay));

		// 费用合计
		oneReceiptParm.setData("TOT_AMT", "TEXT", df.format(recpParm.getDouble(
				"TOT_AMT", 0)));
		// 费用显示大写金额
		oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
				.numberToWord(recpParm.getDouble("TOT_AMT", 0)));
		// 现金支付= 医疗卡金额+现金+绿色通道+支付宝
		double payCash = StringTool.round(recpParm.getDouble("PAY_CASH", 0), 2)
				+ StringTool
						.round(recpParm.getDouble("PAY_MEDICAL_CARD", 0), 2)
				+ StringTool.round(recpParm.getDouble("PAY_OTHER1", 0), 2)
				+ StringTool.round(recpParm.getDouble("ALIPAY", 0), 2);
		// 现金支付
		oneReceiptParm.setData("Cash", "TEXT", gbCashPay == 0 ? payCash : df
				.format(gbCashPay));
		
		// 起付金额
		if(startStandard !=0 ){
			oneReceiptParm.setData("START_AMT_NAME", "TEXT","起付标准");
			oneReceiptParm.setData("START_AMT", "TEXT", StringTool.round(
					startStandard, 2));
		}
		if (agentAmt != 0) {
			if(insCrowdType.equals("2")||(insCrowdType.equals("1")&&reimType.equals("0"))){
				oneReceiptParm.setData("AGENT_NAME", "TEXT", "补助金额：");// 医疗救助金额
				oneReceiptParm.setData("AGENT_AMT", "TEXT", df.format(agentAmt));
				}
		}
		if (illnesssubsidyamt != 0) {
			oneReceiptParm.setData("ILLNESS_SUBSIDY_AMT_NAME", "TEXT", "城乡大病支付");// 城乡大病金额
			oneReceiptParm.setData("ILLNESS_SUBSIDY_AMT", "TEXT", df.format(illnesssubsidyamt));
		}
		
		if(insCrowdType.equals("2")||(insCrowdType.equals("1")&&reimType.equals("0"))){
			//其他医保支付总额
			oneReceiptParm.setData("QTYL", "TEXT", (illnesssubsidyamt+agentAmt) == 0? "0.00" : 
				df.format(illnesssubsidyamt+agentAmt));	
		}else{
			oneReceiptParm.setData("QTYL", "TEXT",  "0.00" );
		}
//		oneReceiptParm.setData("MR_NO", "TEXT", opb.getPat().getMrNo());
		oneReceiptParm.setData("MR_NO", "TEXT", oldMrNo);
		// 打印日期
		oneReceiptParm.setData("OPT_DATE", "TEXT", StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy  MM  dd"));	
		//医保统筹支付
		oneReceiptParm.setData("PAY_DEBIT", "TEXT", df
				.format(gbNhiPay));
		if (recpParm.getDouble("PAY_OTHER1", 0) > 0) {
			// 绿色通道金额
			oneReceiptParm.setData("GREEN_PATH", "TEXT", "绿色通道支付");
			// 绿色通道金额
			oneReceiptParm.setData("GREEN_AMT", "TEXT", StringTool.round(
					recpParm.getDouble("PAY_OTHER1", 0), 2));

		}
		// 医生名称
		oneReceiptParm.setData("DR_NAME", "TEXT", CtzDescParm.getValue("USER_NAME", 0));

		// 打印人
		oneReceiptParm.setData("OPT_USER", "TEXT", Operator.getName());
		oneReceiptParm.setData("USER_NAME", "TEXT", Operator.getID());
		oneReceiptParm.setData("SEX", "TEXT", opb.getPat().getSexString());//性别
		oneReceiptParm.setData("ID_NO", "TEXT", opb.getPat().getIdNo());//
		
		oneReceiptParm.setData("DETAIL", "TEXT", "(详见费用清单)");
		// 社会保障号（显示身份证号）
		if (cardNo.equals("")) {
		oneReceiptParm.setData("CARD_CODE", "TEXT", "");// 如果不是医保			
		} else {			
			String idNOBefore = cardNo.substring(0, 3);
            String idNOEnd = cardNo.substring( cardNo.length()-3, cardNo.length());
            oneReceiptParm.setData("CARD_CODE", "TEXT", idNOBefore+"************"+idNOEnd);

		}
		//医疗机构类型
            String regionSql = "SELECT HOSP_CLASS FROM SYS_REGION WHERE REGION_CODE = '"+ Operator.getRegion()+"'";
    	    TParm regionParm = new TParm(TJDODBTool.getInstance().select(regionSql));
    	    String hospClass = regionParm.getValue("HOSP_CLASS",0);
    	    String sqlhospClass = "SELECT CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_HOSPITAL_CLASS' AND ID = '"+hospClass+"'";
    	    TParm  hospClassParm =  new TParm(TJDODBTool.getInstance().select(sqlhospClass));
    	    oneReceiptParm.setData("HOSP_CLASS","TEXT",hospClassParm.getValue("CHN_DESC",0));
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
				oneReceiptParm.setData("CHARGE0" + i, "TEXT", recpParm
						.getDouble("CHARGE0" + i, 0) == 0 ? "0.00" : recpParm
						.getData("CHARGE0" + i, 0));
			} else {
				oneReceiptParm.setData("CHARGE" + i, "TEXT", recpParm
						.getDouble("CHARGE" + i, 0) == 0 ? "0.00" : recpParm
						.getData("CHARGE" + i, 0));
			}
		}
		oneReceiptParm.setData("CHARGE01", "TEXT", df.format(recpParm
				.getDouble("CHARGE01", 0)
				+ recpParm.getDouble("CHARGE02", 0)));
		String caseNo = opb.getReg().caseNo();//
		
		oneReceiptParm.setData("SEX", "TEXT", opb.getPat().getSexString());
		oneReceiptParm.setData("ID_NO", "TEXT", opb.getPat().getIdNo());
		if(!"".equals(result.getValue("CONFIRM_NO", 0))){
			oneReceiptParm.setData("RECEIPT_NO", "TEXT",  result.getValue("CONFIRM_NO", 0));
		}else{
			oneReceiptParm.setData("RECEIPT_NO", "TEXT",  recpParm.getValue("RECEIPT_NO", 0));
		}	 
		TParm dparm = new TParm();
		dparm.setData("CASE_NO", caseNo);
		dparm.setData("ADM_TYPE", opb.getReg().getAdmType());
		onPrintCashParm(oneReceiptParm, recpParm, dparm);
		System.out.println("oneReceiptParm = = = = = = " + oneReceiptParm);
		//遗失补印 add by lich ------------start
		if(printFlg){
			String sql = "";
			sql =  "SELECT INV_NO FROM BIL_INVRCP WHERE RECEIPT_NO = '"+ 
				recpParm.getValue("RECEIPT_NO", 0)+"' AND RECP_TYPE = 'OPB' AND CANCEL_FLG = '0'";
			TParm invNoParm = new TParm(TJDODBTool.getInstance().select(sql));
			oneReceiptParm.setData("NO", "TEXT",  invNoParm.getValue("INV_NO", 0));
			
			this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrintBD.jhw"),
					IReportTool.getInstance().getReportParm("OPBRECTPrintBD.class", oneReceiptParm), false);//报表合并modify by wanglong 20130730
			printFlg = false;
		}else{
			this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
					IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm), true);//报表合并modify by wanglong 20130730
		}
		//遗失补印 add by lich ------------end
//        this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
//                IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm), true);//报表合并modify by wanglong 20130730
		return;

	}

	/**
	 * 现金打票明细入参
	 */
	private void onPrintCashParm(TParm oneReceiptParm, TParm recpParm,
			TParm dparm) {
		String receptNo = recpParm.getData("RECEIPT_NO", 0).toString();
		dparm.setData("NO", receptNo);
		TParm tableresultparm = OPBTool.getInstance().getReceiptDetail(dparm);
		if (tableresultparm.getCount() > 6) {
			tableresultparm.setData("DETAIL", "TEXT", "(详见费用明细表)");
		}
		oneReceiptParm.setData("TABLE", tableresultparm.getData());
	}

	/**
	 * 特殊人员类别
	 * 
	 * @param type
	 *            String
	 * @return String
	 */
	private String getSpPatDesc(String type) {
		if (type == null || type.length() == 0 || type.equals("null"))
			return "";
		if ("04".equals(type))
			return "残疾军人";
		if ("06".equals(type))
			return "公务员";
		if ("07".equals(type))
			return "民政救助";
		if ("08".equals(type))
			return "民政优抚";
		if ("09".equals(type))
			return "非典补助";
		return "";
	}

	/**
	 * 医疗卡打票明细入参
	 * 
	 * @param oneReceiptParm
	 * @param recpParm
	 * @param dparm
	 */
	private void onPrintEktParm(TParm oneReceiptParm, TParm recpParm,
			TParm dparm) {
		String receptNo = recpParm.getData("RECEIPT_NO", 0).toString();
		dparm.setData("NO", receptNo);
		TParm tableresultparm = OPBTool.getInstance().getReceiptDetail(dparm);
		// if(orderParm.getCount()>10){
		// oneReceiptParm.setData("DETAIL", "TEXT", "(详见费用明细表)");
		// }
		oneReceiptParm.setData("TABLE", tableresultparm.getData());
	}

	/**
	 * 医疗卡和现金退废票据
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean onSaveBackReceipt(int row) {
		TParm saveParm = tableM.getParmValue();
		TParm actionParm = saveParm.getRow(row);
		actionParm.setData("OPT_USER_T", Operator.getID());
		actionParm.setData("OPT_TERM_T", Operator.getIP());
		actionParm.setData("OPT_DATE_T", SystemTool.getInstance().getDate());
		actionParm.setData("PRINT_DATE", actionParm.getData("BILL_DATE"));
		actionParm.setData("ADM_TYPE", opb.getReg().getAdmType());	
		

		if (saveParm == null)				
			return false;
		// 调用opbaction
		TParm result = null;
		TParm parm = null;
		
		//获得历史表数据 add by huangtt 20150604
//		TParm makeOrderInvalidParm = OPBMakeRecpAndOrderInvalid.getInstance().getMakeOrderInvalid(actionParm.getValue("RECEIPT_NO"));
		  TParm makeOrderInvalidParm = getMakeOrderInvalid(actionParm.getValue("RECEIPT_NO"),actionParm.getValue("CONFIRM_NO"));
		
//		if(!OPBMakeRecpAndOrderInvalid.getInstance().makeOrderInvalid(actionParm.getValue("RECEIPT_NO"))){
//			this.messageBox("写历史表错误");
//			return false;
//		}
		
		// 医疗卡操作
		if (actionParm.getDouble("PAY_MEDICAL_CARD")
				+ actionParm.getDouble("PAY_OTHER1")
				+ actionParm.getDouble("ALIPAY") > 0) { //add by huangtt 20160612 支付宝支付方式
			type = "医疗卡";
			// 医保操作
			if (!reSetInsSave(actionParm))
				return false;
			if (!insFlg) {
				this.messageBox("票据号码:"+actionParm.getValue("PRINT_NO")+" "+type + "退票操作,不执行退费");
			}
			result = ektResetFee(actionParm);
			if (null == result) {
				return false;
			}
			// 现金操作
		} else if (actionParm.getDouble("PAY_CASH") > 0) {
			type = "现金";
			cashFLg = true;
			isCheckBack(1);
			// 医保操作
			if (!reSetInsSave(actionParm))
				return false;
			if (!insFlg) {
				this.messageBox("票据号码:"+actionParm.getValue("PRINT_NO")+" "+type + "退费金额:"
						+ actionParm.getDouble("PAY_CASH"));
			}
			actionParm.setData("INS_FLG", insFlg);
			actionParm.setData("CONFIRM_NO", CONFIRMNO);
			// 现金退费
			result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"backOPBRecp", actionParm);
			// 医保全部报销操作,执行现金操作
		} else if (actionParm.getDouble("PAY_INS_CARD") > 0
				&& (actionParm.getDouble("PAY_MEDICAL_CARD")
						+ actionParm.getDouble("PAY_OTHER1")+ actionParm.getDouble("ALIPAY")) == 0
				&& actionParm.getDouble("PAY_CASH") == 0 && actionParm.getDouble("PAY_OTHER2") == 0) {
			if (!onExeInsSum(actionParm, result)) {
				return false;							
			}
			result = new TParm();  //add by huangtt 20160622

			//建行操作
		}else if(actionParm.getDouble("PAY_OTHER2") >0){
			if(!isCheckBack(2)){
				return false;
			}
			TParm opbParm=getCcbParm(actionParm);
			this.messageBox("opbParm==="+opbParm);
			//建行接口操作						
			//TParm resultData=REGCcbReTool.getInstance().getCcbRe(opbParm);
			TParm resultData = TIOM_AppServer.executeAction("action.ccb.CCBServerAction",
					"getCcbRe", opbParm);																							
			if (resultData.getErrCode()<0) {
				this.messageBox("建行接口调用出现问题,请联系信息中心");
				return false;				
			}
			//医保操作								
			if(!REGCcbReTool.getInstance().reSetInsSave(opbParm,this)){
				return false;
			}
			resultData.setData("OPT_USER",Operator.getID()); 
			resultData.setData("OPT_TERM",Operator.getIP());
			resultData.setData("BUSINESS_TYPE","OPBT");
			result=REGCcbReTool.getInstance().saveEktCcbTrede(resultData);
			if (result.getErrCode() < 0) {
				this.messageBox("添加交易档失败");
				return false;
			}
			//作废票据
			result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"backOPBRecp", actionParm);
			
		} else if (actionParm.getDouble("TOT_AMT") == 0) {
			result = ektResetFee(actionParm);
			if (null == result) {
				return false;
			}
		}
		
//		System.out.println("result---"+result);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
//		System.out.println("删除医保在徐-----------------"+opb.getReg().caseNo());
		if (!deleteInsRun(opb.getReg().caseNo())) {
			System.out.println("删除在途状态失败：" + opb.getReg().caseNo());
		}
//		System.out.println("写历史表错误----------------");
		//modiby by huagtt 20150604 start
		if(!OPBMakeRecpAndOrderInvalid.getInstance().makeOrderInvalid(makeOrderInvalidParm)){
			this.messageBox("写历史表错误");
			return false;
		}
		//modiby by huagtt 20150604 end
		
		return true;
	}
	/**
	 * 医保全部报销逻辑
	 * @return
	 */
	private boolean onExeInsSum(TParm actionParm,TParm result){
		//添加建行
		TParm ektCcbParm=new TParm(TJDODBTool.getInstance().select("SELECT CASE_NO FROM EKT_CCB_TRADE WHERE CASE_NO='"+opb.getReg().caseNo()+
				"' AND RECEIPT_NO='"+actionParm.getValue("RECEIPT_NO")+"'"));
		if (ektCcbParm.getErrCode()<0) {
			return false;
		}
		if (ektCcbParm.getCount("CASE_NO")>0) {
			if(!isCheckBack(2)){
				return false;
			}
			//建行操作
			TParm opbParm=getCcbParm(actionParm);
			if(!REGCcbReTool.getInstance().reSetInsSave(opbParm,this)){
				return false;
			}
			actionParm.setData("CCB_FLG","Y");
		}else{
			type = "医疗卡";
			if (!reSetInsSave(actionParm))
				return false;
			// 医疗卡退费
			actionParm.setData("INS_UN_FLG", "Y");
			actionParm.setData("INS_FLG", insFlg);
			actionParm.setData("CONFIRM_NO", CONFIRMNO);
		}
		result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"backOPBRecp", actionParm);
		return true;
	}
	/**
	 * 现金、建行操作 是否可以退费
	 * @return
	 * type：1 ：现金操作 2.建行操作
	 */
	private boolean isCheckBack(int type){
		boolean returnFeeFlg = false;
		// 校验是否已到检或已发药
		StringBuffer messagePha = new StringBuffer();
		StringBuffer message = new StringBuffer();
		TParm orderParm = tableD.getParmValue();
		int count = orderParm.getCount("EXEC_FLG");
		String exeFlg = "";
		int HL7Count = 0;
		for (int i = 0; i < count; i++) {
			exeFlg = orderParm.getValue("EXEC_FLG", i);
			if ("Y".equals(exeFlg)) {
				returnFeeFlg = true;
				if (orderParm.getValue("CAT1_TYPE", i).equals("PHA")) {// 药品
					messagePha.append(orderParm.getValue("ORDER_DESC", i)
							+ ",");
				} else {
					if (orderParm.getValue("CAT1_TYPE", i).equals("LIS")
							|| orderParm.getValue("CAT1_TYPE", 0).equals(
									"RIS")) {
						sendHL7Parm.setRowData(HL7Count, orderParm, i);
						HL7Count++;
						if (orderParm.getValue("SETMAIN_FLG", i)
								.equals("Y")) {
							message.append(orderParm.getValue("ORDER_DESC",
									i)
									+ ",");
						}
					}
				}
			}
		}

		if (returnFeeFlg) {
			String sumMessage = "";
			if (messagePha.length() > 0) {
				sumMessage = messagePha.toString().substring(0,
						messagePha.lastIndexOf(","))
						+ " 已经发药\n";
			}
			if (message.length() > 0) {
				sumMessage += message.toString().substring(0,
						message.lastIndexOf(","))
						+ " 已经到检\n";
			}
			switch (type){
			case 1:
				sumMessage += "现金退费操作请注意";
				break;
			case 2:
				sumMessage += "不可以进行建行退费操作";
				break;
			}
			
			this.messageBox(sumMessage);
			return false;
		}
		return true;
	}
	/**
	 * 建行医保操作入参
	 * @param actionParm
	 * @return
	 */
	private TParm getCcbParm(TParm actionParm){
		TParm opbParm=new TParm();
		opbParm.setData("CASE_NO" ,opb.getReg().caseNo());// 退挂使用
		opbParm.setData("PAT_NAME", opb.getPat().getName());
		opbParm.setData("MR_NO", opb.getPat().getMrNo());// 病患号
		opbParm.setData("NHI_NO",regionParm.getValue("NHI_NO", 0));
		opbParm.setData("PRINT_NO",actionParm.getValue("PRINT_NO"));
		opbParm.setData("OPT_USER", Operator.getID());// id
		opbParm.setData("OPT_TERM", Operator.getIP());// ip
		opbParm.setData("OPT_NAME", Operator.getName());// ip
		opbParm.setData("CTZ1_CODE", this.getValue("CTZ1_CODE"));
		opbParm.setData("RECEIPT_NO",actionParm.getValue("RECEIPT_NO"));					
		opbParm.setData("AMT", actionParm.getDouble("PAY_OTHER2"));
		return opbParm;
	}
	/**
	 * 调用HL7
	 */
	private void sendHL7Mes(TParm sendHL7Parm) {
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
		if (null != sendHL7Parm && null != sendHL7Parm.getData("ADM_TYPE"))
			count = ((Vector) sendHL7Parm.getData("ADM_TYPE")).size();
		List list = new ArrayList();
		String patName = opb.getPat().getName();
		for (int i = 0; i < count; i++) {
			TParm temp = sendHL7Parm.getRow(i);
			String admType = temp.getValue("ADM_TYPE");
			String sql = " SELECT CASE_NO,MED_APPLY_NO,CAT1_TYPE FROM OPD_ORDER "
					+ "  WHERE CASE_NO ='"
					+ opb.getReg().caseNo()
					+ "' "
					+ "    AND RX_NO='"
					+ temp.getValue("RX_NO")
					+ "' "
					+ "    AND ORDERSET_CODE IS NOT NULL "
					+ "    AND ORDERSET_CODE = ORDER_CODE "
					+ "    AND SEQ_NO='"
					+ temp.getValue("SEQ_NO")
					+ "' AND ADM_TYPE='" + admType + "'";
			// System.out.println("SQL:"+sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			// System.out.println("查询结果:"+result);
			TParm parm = new TParm();
			parm.setData("PAT_NAME", patName);
			parm.setData("ADM_TYPE", admType);
			parm.setData("FLG", 1);// 退费
			parm.setData("CASE_NO", result.getValue("CASE_NO", 0));
			parm.setData("LAB_NO", result.getValue("MED_APPLY_NO", 0));
			parm.setData("CAT1_TYPE", result.getValue("CAT1_TYPE", 0));

			list.add(parm);
		}
		// System.out.println("发送接口项目:"+list);
		// 调用接口
		TParm resultParm = Hl7Communications.getInstance().Hl7Message(list);
		// System.out.println("resultParm::::"+resultParm);
		if (resultParm.getErrCode() < 0) {
			this.messageBox(resultParm.getErrText());
		}
	}

	/**
	 * 医疗卡退费操作
	 * 
	 * @param parm
	 * @param actionParm
	 * @return
	 */
	private TParm ektResetFee(TParm actionParm) {
		TParm result = new TParm();
		//flg = true;// 判断退费方式
		actionParm.setData("REGION_CODE", Operator.getRegion());
		actionParm.setData("CASE_NO", opb.getReg().caseNo());
		actionParm.setData("MR_NO", opb.getPat().getMrNo());
		actionParm.setData("INS_FLG", insFlg);
		actionParm.setData("CONFIRM_NO", CONFIRMNO);
		// 医疗卡退费
		result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"backEKTOPBRecp", actionParm);
		return result;
	}

	/**
	 * 撤销操作
	 * 
	 * @param parm
	 */
	private void concelResetFee(TParm parm) {
		// 医疗卡退费操作失败回冲数据
		TParm writeParm = new TParm();
		writeParm.setData("CURRENT_BALANCE", parm.getValue("OLD_AMT"));
		writeParm.setData("MR_NO", parmEKT.getValue("MR_NO"));
		writeParm.setData("SEQ", parmEKT.getValue("SEQ"));
		writeParm = EKTIO.getInstance().TXwriteEKTATM(writeParm,
				parmEKT.getValue("MR_NO")); // 回写医疗卡金额
		if (writeParm.getErrCode() < 0)
			System.out.println("err:" + writeParm.getErrText());
		TParm concelParm = new TParm();
		concelParm.setData("TREDE_NO", parm.getValue("TREDE_NO"));// 医疗卡收据号
		concelParm.setData("BUSINESS_NO", parm.getValue("BUSINESS_NO"));// 医疗卡交易号
		writeParm = concelEKT(concelParm);
		if (writeParm.getErrCode() < 0) {
			System.out.println("医疗卡退费撤销医疗卡操作失败");
		} else {
			System.out.println("医疗卡退费撤销医疗卡操作成功");
		}
	}

	/**
	 * 撤销医疗卡操作
	 * 
	 * @return
	 */
	private TParm concelEKT(TParm parm) {
		TParm result = TIOM_AppServer.executeAction("action.ekt.EKTAction",
				"deleteRegOldData", parm);
		return result;
		// EKTTool.getInstance().deleteTrede(parm, connection);
	}

	/**
	 * 票据补打
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean onSaveRePrint(int row) {
		TParm saveRePrintParm = getRePrintData(row);
		if (saveRePrintParm == null)
			return false;
		// 调用opbaction
		TParm result = null;
		if (ektParm.getCount("CASE_NO") > 0) {
//			System.out.println("进入医疗卡补打");
			// 医疗卡补打
			// COUNT
			saveRePrintParm.setData("COUNT", ektParm.getCount("CASE_NO"));
//			System.out.println("进入医疗卡补打11111");
			result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"saveOPBEKTRePrint", saveRePrintParm);
//			System.out.println("进入医疗卡补打22222");

		} else {
//			System.out.println("进入现金补打");
			// 现金补打
			saveRePrintParm.setData("COUNT", -1);
			result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"saveOPBRePrint", saveRePrintParm);
		}
		printNoOnly = result.getValue("PRINT_NO");
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}

		return true;
	}

	/**
	 * 得到补印数据
	 * 
	 * @param row
	 *            int
	 * @return TParm
	 */
	public TParm getRePrintData(int row) {
		TParm saveParm = tableM.getParmValue();
		TParm actionParm = saveParm.getRow(row);
		actionParm.setData("OPT_USER", Operator.getID());
		actionParm.setData("OPT_TERM", Operator.getIP());
		actionParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		// System.out.println("actionParm"+actionParm);
		actionParm.setData("ADM_TYPE", opb.getReg().getAdmType());
		return actionParm;

	}

	/**
	 * 医保获取数据
	 * 
	 * @return
	 */
	private void getInsValue(TParm resultParm, TParm sumParm,String printNo) {
		TParm insFeeParm = new TParm();
		insFeeParm.setData("CASE_NO", opb.getReg().caseNo());// 退挂使用
		insFeeParm.setData("RECP_TYPE", "OPB");// 收费使用
		// insFeeParm.setData("CONFIRM_NO", resultParm.getValue("CONFIRM_NO",
		// 0));// 医保就诊号
		// ---写死了需要修改
		insFeeParm.setData("NAME", opb.getPat().getName());
		insFeeParm.setData("MR_NO", opb.getPat().getMrNo());// 病患号
		if (ektParm.getCount("CASE_NO") > 0) {
			insFeeParm.setData("PAY_TYPE", true);// 支付方式
		} else {
			insFeeParm.setData("PAY_TYPE", false);// 支付方式
		}
		TParm result = INSMZConfirmTool.getInstance().queryMZConfirmOne(
				insFeeParm);// 查询医保信息
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return;
		}
		if (cashFLg) {
			this.messageBox("票据号码:"+printNo+" 医保退费金额:"
					+ StringTool.round(sumParm.getDouble("PAY_INS_CARD", 0), 2)
					+ ","
					+ type
					+ "扣款:"
					+ (StringTool.round(sumParm.getDouble("TOT_AMT", 0)
							- sumParm.getDouble("PAY_INS_CARD", 0), 2))
					+ ",请注意");
		} else {
			this.messageBox("票据号码:"+printNo+" 医保退费金额:"
					+ StringTool.round(sumParm.getDouble("PAY_INS_CARD", 0), 2)
					+ ","
					+ type
					+ "扣款:"
					+ (StringTool.round(sumParm.getDouble("TOT_AMT", 0)
							- sumParm.getDouble("PAY_INS_CARD", 0), 2)));

		}
		nhiRegionCode = result.getValue("REGION_CODE", 0);
		if (result.getInt("INS_CROWD_TYPE", 0) == 1
				&& result.getInt("INS_PAT_TYPE", 0) == 1) {
			resultParm.setData("INS_TYPE", "1");// 医保就医类别
		} else if (result.getInt("INS_CROWD_TYPE", 0) == 1
				&& result.getInt("INS_PAT_TYPE", 0) == 2) {
			resultParm.setData("INS_TYPE", "2");// 医保就医类别
		} else if (result.getInt("INS_CROWD_TYPE", 0) == 2
				&& result.getInt("INS_PAT_TYPE", 0) == 2) {
			resultParm.setData("INS_TYPE", "3");// 医保就医类别
		}
	}

	/**
	 * 医保退费操作
	 */
	private boolean reSetInsSave(TParm opbParm) {

		// 查询医保金额
		String sql = "SELECT PAY_INS_CARD,TOT_AMT,RECEIPT_NO FROM BIL_OPB_RECP WHERE PRINT_NO='"
				+ opbParm.getValue("PRINT_NO") + "'";
		TParm bilParm = new TParm(TJDODBTool.getInstance().select(sql));

		// 医疗卡操作
		if (opbParm.getDouble("PAY_MEDICAL_CARD") + opbParm.getDouble("ALIPAY") //add by huangtt 20160612 支付宝
				+ opbParm.getDouble("PAY_OTHER1") > 0 || opbParm.getDouble("PAY_INS_CARD") > 0
				&& (opbParm.getDouble("PAY_MEDICAL_CARD") + opbParm.getDouble("ALIPAY")
				+ opbParm.getDouble("PAY_OTHER1")) == 0&& opbParm.getDouble("PAY_CASH") == 0) {
//			if (null == parmEKT || parmEKT.getErrCode() < 0) {  //delete by huangtt 20171107医保扣医疗卡金额时，需要先读卡查询余额
				parmEKT = EKTIO.getInstance().TXreadEKT();
				if (null == parmEKT || parmEKT.getErrCode() < 0
						|| parmEKT.getValue("MR_NO").length() <= 0) {
					this.messageBox("请读医疗卡");
					return false;
				}
//			}
			if (!parmEKT.getValue("MR_NO").equals(opb.getPat().getMrNo())) {
				this.messageBox("医疗卡信息与此病患不符");
				return false;
			}
			if (parmEKT.getDouble("CURRENT_BALANCE") < bilParm.getDouble(
					"PAY_INS_CARD", 0)) {
				this.messageBox("医疗卡中金额小于医保退费金额,不能执行医疗卡退费操作");
				return false;
			}
		}
		TParm reSetInsParm = new TParm();
		TParm parm = new TParm();
		// System.out.println("---------------医保退费-------------:" + opbParm);
		parm.setData("CASE_NO", opb.getReg().caseNo());
		parm.setData("INV_NO", opbParm.getValue("PRINT_NO"));
		parm.setData("RECP_TYPE", "OPB");// 收费类型

		// 查询是否医保 退费
		TParm result = INSOpdTJTool.getInstance().selectInsInvNo(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return false;
		}
		if (result.getCount("CASE_NO") <= 0) {// 不是医保退费
			return true;
		}

		// // 查询医保退费金额
		// TParm sumParm = INSOpdTJTool.getInstance().selectInsSumAmt(parm);
		// if (sumParm.getErrCode() < 0) {
		// this.messageBox("E0005");
		// return false;
		// }
		// 医保卡退费 需要修改医疗卡参数
		if (null == opb.getReg().caseNo()
				&& opb.getReg().caseNo().length() <= 0) {
			this.messageBox("E0005");
			return false;
		}

		// int returnType = insExeFee(opbParm, result, false);
		// if (returnType == 0) {// 取消
		// System.out.println("医保操作失败");
		// return false;
		// }
		getInsValue(result, bilParm,opbParm.getValue("PRINT_NO"));
		// ====zhangp 20120229
		insType = result.getInt("INS_TYPE");
		CONFIRMNO = result.getValue("CONFIRM_NO", 0);//医保号
		// =====zhangp 20120229
		reSetInsParm.setData("CASE_NO", opb.getReg().caseNo());// 就诊号
		reSetInsParm.setData("CONFIRM_NO", result.getValue("CONFIRM_NO", 0));// 医保就诊号
		reSetInsParm.setData("INS_TYPE", result.getValue("INS_TYPE"));// 医保就医类型
		reSetInsParm.setData("RECP_TYPE", "OPB");// 收费类型
		reSetInsParm.setData("UNRECP_TYPE", "OPBT");// 退费类型
		reSetInsParm.setData("OPT_USER", Operator.getID());// id
		reSetInsParm.setData("OPT_TERM", Operator.getIP());// ip
		reSetInsParm.setData("REGION_CODE", nhiRegionCode);// 医保区域代码
		reSetInsParm.setData("INV_NO", opbParm.getValue("PRINT_NO"));// 票据号
		reSetInsParm.setData("PAT_TYPE", this.getValue("CTZ1_CODE"));// 身份
		reSetInsParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));// 身份

		// System.out.println("reSetInsParm:::::::" + reSetInsParm);
		result = INSTJReg.getInstance().insResetCommFunction(
				reSetInsParm.getData());

		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return false;
		}
		insFlg = true;// 医保操作
		if (opbParm.getDouble("PAY_MEDICAL_CARD") + opbParm.getDouble("ALIPAY")
				+ opbParm.getDouble("PAY_OTHER1") > 0 || opbParm.getDouble("PAY_INS_CARD") > 0
				&& (opbParm.getDouble("PAY_MEDICAL_CARD") + opbParm.getDouble("ALIPAY")
				+ opbParm.getDouble("PAY_OTHER1")) == 0&& opbParm.getDouble("PAY_CASH") == 0) {

			TParm opdParm = new TParm();
			opdParm.setData("NAME", opb.getPat().getName());
			opdParm.setData("SEX", opb.getPat().getSexCode().equals("1") ? "男"
					: "女");
			opdParm.setData("AMT", bilParm.getDouble("PAY_INS_CARD", 0));
			opdParm.setData("INS_FLG", "Y"); // 医保使用
			// 需要修改的地方
			opdParm.setData("MR_NO", opb.getPat().getMrNo());
			result = insExeUpdate(-bilParm.getDouble("PAY_INS_CARD", 0),
					bilParm.getDouble("TOT_AMT", 0), opb.getReg().caseNo(), "OPBT",bilParm.getValue("RECEIPT_NO", 0),CONFIRMNO);

			// result = EKTIO.getInstance().insUnFee(opdParm, this);
			// System.out.println("修result::::::::"+result);
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return false;
			}
		}
		return true;
	}

	/**
	 * 删除医保在途状态
	 * 
	 * @return
	 */
	public boolean deleteInsRun(String caseNo) {
		if (null == caseNo && caseNo.length() <= 0) {
			return false;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXE_USER", Operator.getID());
		parm.setData("EXE_TERM", Operator.getIP());
		parm.setData("EXE_TYPE", "OPBT");
		TParm result = INSRunTool.getInstance().deleteInsRun(parm);
		// System.out.println("在途状态数据返回：" + result);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			result.setErr(-1, "医保卡执行操作失败");
			return false;
		}
		return true;
	}

	/**
	 * 医疗卡操作保存此次医保卡扣款金额
	 * 
	 * @param returnParm
	 * @return
	 */
	private TParm insExeUpdate(double accountAmt, double totAmt,
			String caseNo, String business_type,String receiptNo,String CONFIRMNO) {
		// 入参:AMT:本次操作金额 BUSINESS_TYPE :本次操作类型 CASE_NO:就诊号码
		TParm orderParm = new TParm();
		orderParm.setData("AMT", -accountAmt);
		orderParm.setData("BUSINESS_TYPE", business_type);
		orderParm.setData("CASE_NO", caseNo);
		orderParm.setData("RECEIPT_NO", receiptNo);
		orderParm.setData("CURRENT_BALANCE",parmEKT.getDouble("CURRENT_BALANCE"));
		orderParm.setData("OPT_USER", Operator.getID());
		orderParm.setData("OPT_TERM", Operator.getIP());
		orderParm.setData("EXE_FLG", "Y");
		orderParm.setData("ID_NO", parmEKT.getValue("IDNO"));
		orderParm.setData("CARD_NO", parmEKT.getValue("PK_CARD_NO"));// 卡号
		orderParm.setData("MR_NO", parmEKT.getValue("MR_NO"));// 病案号
		orderParm.setData("PAT_NAME", parmEKT.getValue("PAT_NAME"));// 病患名称
		orderParm.setData("EKT_USE", totAmt);// 总金额
		orderParm.setData("EKT_OLD_AMT",parmEKT.getDouble("CURRENT_BALANCE"));// 原来金额
		orderParm.setData("CONFIRMNO",CONFIRMNO);// 医保号
		TParm insExeParm = TIOM_AppServer.executeAction("action.ekt.EKTAction",
				"exeInsSave", orderParm);
		return insExeParm;

	}
	/**
	 * 取得带三目字典的清单数据
	 * ======zhangp 20120925
	 * @param orderParm
	 * @return
	 */
	private TParm getInsRuleParm(TParm orderParm){
        String sql = // wanglong modify 20150123 将sql改为不存在医保表也不影响数据行的返回（用于普华医院）
            "SELECT B.*, CASE WHEN B.ZFBL1 IS NULL "
                    + "       THEN (B.ORDER_DESC1 || CASE WHEN TRIM(B.MAN_CHN_DESC) IS NOT NULL OR TRIM(B.MAN_CHN_DESC) <> '' "
                    + "                                    THEN '*' || B.MAN_CHN_DESC ELSE '' END) "
                    + "       ELSE (CASE WHEN B.ZFBL1 = 1 THEN '☆' WHEN B.ZFBL1 > 0 AND B.ZFBL1 < 1  THEN '#' END "
                    + "             || B.NHI_CODE_O || ' ' || B.ORDER_DESC1 || "
                    + "             CASE WHEN TRIM(B.MAN_CHN_DESC) IS NOT NULL OR TRIM(B.MAN_CHN_DESC) <> '' THEN '*' "
                    + "             || B.MAN_CHN_DESC ELSE '' END || ' ' || B.PZWH || ' ' || B.ZFBL1 * 100 || '%') "
                    + "       END AS ORDER_DESC "
                    + " FROM (SELECT A.CASE_NO, A.RX_NO, A.SEQ_NO, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, "
                    + "            A.PRESRT_NO, A.REGION_CODE, A.MR_NO, A.ADM_TYPE, A.RX_TYPE, "
                    + "            A.TEMPORARY_FLG, A.RELEASE_FLG, A.LINKMAIN_FLG, A.LINK_NO, A.ORDER_CODE, "
                    + "            D.NHI_CODE_O,A.ORDER_DESC ORDER_DESC1 ,C.MAN_CHN_DESC, "
                    + "            A.SPECIFICATION, A.GOODS_DESC, A.ORDER_CAT1_CODE, A.MEDI_QTY, A.MEDI_UNIT, "
                    + "            A.FREQ_CODE, A.ROUTE_CODE, A.TAKE_DAYS, A.DOSAGE_QTY, A.DOSAGE_UNIT, A.DISPENSE_QTY, "
                    + "            A.DISPENSE_UNIT, A.GIVEBOX_FLG, A.OWN_PRICE, A.NHI_PRICE, A.DISCOUNT_RATE, A.OWN_AMT,  "
                    + "            A.AR_AMT, A.DR_NOTE, A.NS_NOTE, A.DR_CODE, A.ORDER_DATE, A.DEPT_CODE, A.DC_DR_CODE,  "
                    + "            A.DC_ORDER_DATE, A.DC_DEPT_CODE, A.EXEC_DEPT_CODE, A.SETMAIN_FLG, A.ORDERSET_GROUP_NO, "
                    + "            A.ORDERSET_CODE, A.HIDE_FLG, A.RPTTYPE_CODE, A.OPTITEM_CODE, A.DEV_CODE, A.MR_CODE, "
                    + "            A.FILE_NO, A.DEGREE_CODE, A.URGENT_FLG, A.INSPAY_TYPE, A.PHA_TYPE, A.DOSE_TYPE, "
                    + "            A.PRINTTYPEFLG_INFANT, A.EXPENSIVE_FLG, A.CTRLDRUGCLASS_CODE, A.PRESCRIPT_NO, "
                    + "            A.ATC_FLG, A.SENDATC_DATE, A.RECEIPT_NO, A.BILL_FLG, A.BILL_DATE, A.BILL_USER,  "
                    + "            A.PRINT_FLG, A.REXP_CODE, A.HEXP_CODE, A.CONTRACT_CODE, A.CTZ1_CODE, A.CTZ2_CODE,  "
                    + "            A.CTZ3_CODE, A.PHA_CHECK_CODE, A.PHA_CHECK_DATE, A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE,  "
                    + "            A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE, A.NS_EXEC_CODE, A.NS_EXEC_DATE, A.NS_EXEC_DEPT,  "
                    + "            A.DCTAGENT_CODE, A.DCTEXCEP_CODE, A.DCT_TAKE_QTY, A.PACKAGE_TOT, A.AGENCY_ORG_CODE,  "
                    + "            A.DCTAGENT_FLG, A.DECOCT_CODE, A.EXEC_FLG, A.RECEIPT_FLG, A.CAT1_TYPE, A.MED_APPLY_NO,  "
                    + "            A.BILL_TYPE, A.PHA_RETN_CODE, F.DOSE_CHN_DESC, '0.0%' AS AL, A.BUSINESS_NO, "
                    + "            G.PZWH, G.ZFBL1, G.KSSJ, G.JSSJ "
                    + "       FROM OPD_ORDER A, SYS_MANUFACTURER C, SYS_FEE_HISTORY D, PHA_BASE E, PHA_DOSE F, "
                    + "            INS_RULE G "
                    + "      WHERE A.ORDER_CODE = D.ORDER_CODE(+) "
                    + "        AND D.MAN_CODE = C.MAN_CODE(+) "
                    + "        AND A.ORDER_CODE = E.ORDER_CODE(+) "
                    + "        AND E.DOSE_CODE = F.DOSE_CODE(+) "
                    + "        AND CASE_NO = '" + orderParm.getValue("CASE_NO") + "' "
                    + "        AND RECEIPT_NO = '" + orderParm.getValue("RECEIPT_NO") + "' "
                    + "        AND D.NHI_CODE_O = G.SFXMBM(+) "
//                    + "        AND A.BILL_DATE BETWEEN G.KSSJ AND G.JSSJ "
//                    + "        AND A.BILL_DATE BETWEEN TO_DATE( D.START_DATE, 'YYYYMMDDHH24MISS') AND TO_DATE( D.END_DATE, 'YYYYMMDDHH24MISS') "
                    + "        AND TO_CHAR( A.BILL_DATE, 'YYYYMMDDHH24MISS') >= D.START_DATE(+) "
                    + "        AND TO_CHAR( A.BILL_DATE, 'YYYYMMDDHH24MISS') <= D.END_DATE(+)) B "
                    + " WHERE B.KSSJ IS NULL                    "
                    + "    OR B.JSSJ IS NULL                    "
                    + "    OR (B.KSSJ IS NULL AND B.JSSJ IS NULL) "
                    + "    OR B.BILL_DATE BETWEEN B.KSSJ AND B.JSSJ "
                    + "ORDER BY B.RX_TYPE, B.RX_NO, B.SEQ_NO";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			System.out.println(result.getErrText());
		}
		return result;
	}
	
	 public TParm getMakeOrderInvalid(String receiptNo,String confirmNo) {
		 String sql = OPBMakeRecpAndOrderInvalid.getInstance().SQL.replace("#", receiptNo);
		 TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		 TParm parm = new TParm();
		 TParm parmR = new TParm();
		 for (int i = 0; i < result.getCount(); i++) {
			 result.setData("RECEIPT_NO", i, "");
			 result.setData("PRINT_FLG", i, "N");
			 parm.addRowData(result, i);
			
		}
		 
		 if(parm.getCount("ORDER_CODE") > 0){
			 TParm inParm = new TParm();
				inParm.setData("orderParm", parm.getData());			
				inParm.setData("EKT_HISTORY_NO", "");
				inParm.setData("OPT_TYPE", "UPDATE");
				inParm.setData("OPT_USER", Operator.getID());
				inParm.setData("OPT_TERM", Operator.getIP());
				 if(confirmNo.length() > 0){
					 inParm.setData("MZCONFIRM_NO", "*"+confirmNo);
				 }else{
					 inParm.setData("MZCONFIRM_NO", "");
				 }

				TParm historyParm = AssembleTool.getInstance().parmToSql(inParm);

				TParm sqlParm = historyParm.getParm("sqlParm");
				String ids = historyParm.getValue("LastHistoryIds");
				
				for (int j = 0; j < sqlParm.getCount("SQL"); j++) {
					 parmR.addData("SQL", sqlParm.getValue("SQL",j));
					
				}
			 
				if(ids.length() > 0){
					ids = ids.substring(0, ids.length()-1);
					String sqlU="UPDATE OPD_ORDER_HISTORY_NEW SET ACTIVE_FLG='N' WHERE HISTORY_ID IN ("+ids+")";
					parmR.addData("SQL", sqlU);
				}
			 
			
		 }
		 
		 return parmR;
		 
	 }
	
}
