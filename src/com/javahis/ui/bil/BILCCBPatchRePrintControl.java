package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.bil.BILCCBPatchTool;
import jdo.bil.BILInvoiceTool;
import jdo.bil.BILREGRecpTool;
import jdo.ins.INSMZConfirmTool;
import jdo.opb.OPB;
import jdo.opb.OPBReceiptTool;
import jdo.reg.PatAdmTool;
import jdo.reg.Reg;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import jdo.ins.INSOpdTJTool;
import jdo.opb.OPBTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import com.tiis.util.TiMath;

/**
 * <p>Title: 建行批量补票</p>
 *
 * <p>Description: 建行批量补票</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author zhangp 20120920
 * @version 1.0
 */
public class BILCCBPatchRePrintControl extends TControl{
	
	private static TTable table;
	private static String recpType = "REG";
	private static String admType = "O";
	private static boolean insFlg = false;
	private static String caseNo = "";
	private static String patName = "";
	private static String regionCode = "";
	private static String mrNo = "";
	private static Pat pat;
	
	/**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        table = (TTable)this.getComponent("TABLE");
        //账单table专用的监听
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                               "onTableComponent");
        initPage();
    }
    
    /**
     * 初始化界面数据
     */
    public void initPage() {
        setValue("REGION_CODE", Operator.getRegion());
        Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
        String endDate = today.toString();
        endDate = endDate.substring(0, 4)+"/"+endDate.substring(5, 7)+ "/"+endDate.substring(8, 10)+ " 23:59:59";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", endDate);
        onREG();
    }
    /**
     * 初始化票据
     */
    public void initBil(){
    	TParm selInvoice = new TParm();
        selInvoice.setData("STATUS", "0");
        selInvoice.setData("RECP_TYPE", recpType);
        selInvoice.setData("CASHIER_CODE", Operator.getID());
        selInvoice.setData("TERM_IP", Operator.getIP());
        TParm invoice = BILInvoiceTool.getInstance().selectNowReceipt(
                selInvoice);
        String invNo = invoice.getValue("UPDATE_NO", 0);
        if (invNo == null || invNo.length() == 0) {
            this.messageBox("请先开帐");
            return;
        }
        setValue("UPDATE_NO", invNo);
    }
    /**
     * 挂号点击事件
     */
    public void onREG(){
    	recpType = "REG";
    	initBil();
    	onQuery();
    }
    
    /**
     * 收费点击事件
     */
    public void onOPB(){
    	recpType = "OPB";
    	initBil();
    	onQuery();
    }
    
    /**
     * 账单table监听事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable printTable = (TTable) obj;
        printTable.acceptText();
        return true;
    }
    
    /**
     * 全选事件
     */
    public void onSelectAll() {
        String select = getValueString("SELECT");
        TParm parm = table.getParmValue();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            parm.setData("FLG", i, select);
        }
        table.setParmValue(parm);
    }
    
    /**
     * 查询
     */
    public void onQuery() {
    	String startDate = "";
		String endDate = "";
		if (!"".equals(this.getValueString("START_DATE")) &&
	            !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10) + startDate.substring(11, 13) +
			startDate.substring(14, 16) + startDate.substring(17, 19);
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10) + endDate.substring(11, 13) +
			endDate.substring(14, 16) + endDate.substring(17, 19);
		}
    	TParm parm = new TParm();
    	parm.setData("RECP_TYPE", recpType);
    	parm.setData("CASHIER_CODE", getValue("USER"));
    	parm.setData("START_DATE", startDate);
    	parm.setData("END_DATE", endDate);
    	TParm result = BILCCBPatchTool.getInstance().queryPatchReprint(parm);
    	table.setParmValue(result);
    }
    
    /**
     * 补印
     */
    public void onPrint(){
    	table.acceptText();
    	TParm tableParm = table.getParmValue();
    	if(recpType.equals("REG")){
    		for (int i = 0; i < table.getRowCount(); i++) {
				if(tableParm.getValue("FLG", i).equals("Y")){
					String caseNo = tableParm.getValue("CASE_NO", i);
					admType = tableParm.getValue("ADM_TYPE", i);
					String confirmNo = tableParm.getValue("CONFIRM_NO", i);
					TParm onREGReprintParm = new TParm();
		    		onREGReprintParm.setData("CASE_NO", caseNo);
		    		onREGReprintParm.setData("OPT_USER", Operator.getID());
		    		onREGReprintParm.setData("OPT_TERM", Operator.getIP());
		    		onREGReprintParm.setData("ADM_TYPE", admType);
		    		TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
		    				"onREGReprint", onREGReprintParm);
		    		if (result.getErrCode() < 0) {
		    			System.out.println("补印失败case_no = " + caseNo);
		    		}else{
		    			result = PatAdmTool.getInstance().getRegPringDate(caseNo, "COPY");
		    			result.setData("PRINT_NO", "TEXT", this.getValue("NEXT_NO"));
		    			TParm parm = new TParm();
		    			parm.setData("CASE_NO", caseNo);
		    			parm.setData("CONFIRM_NO", confirmNo);
		    			TParm mzConfirmParm = INSMZConfirmTool.getInstance().queryMZConfirm(
		    					parm); // 判断此次操作是否是医保操作
		    			if (mzConfirmParm.getErrCode() < 0) {
		    				return;
		    			}
		    			TParm printParm = null;
		    			if (mzConfirmParm.getCount() > 0) {
		    				printParm = BILREGRecpTool.getInstance().selForRePrint(caseNo);
		    				insFlg = true;
		    			}
		    			onRePrint(result, mzConfirmParm, printParm);
		    		}
				}
			}
    	}
    	if(recpType.equals("OPB")){
    		for (int i = 0; i < table.getRowCount(); i++) {
				if(tableParm.getValue("FLG", i).equals("Y")){
					if (!onSaveRePrint(i)) {
						messageBox("补印失败!");
						return;
					}
					messageBox("保存成功");
					TParm saveParm = table.getParmValue();
					TParm actionParm = saveParm.getRow(i);
					String receiptNo = actionParm.getValue("RECEIPT_NO");
					TParm recpParm = null;
					// 门诊收据档数据:医疗卡收费打票\现金收费打票
					recpParm = OPBReceiptTool.getInstance().getOneReceipt(receiptNo);
					caseNo = tableParm.getValue("CASE_NO", i);
					patName = tableParm.getValue("PAT_NAME", i);
					admType = tableParm.getValue("ADM_TYPE", i);
					regionCode = tableParm.getValue("REGION_CODE", i);
					mrNo = tableParm.getValue("MR_NO", i);
					pat = Pat.onQueryByMrNo(mrNo);
					onPrint(recpParm, false);
				}
    		}
    	}
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
		saveRePrintParm.setData("COUNT", -1);
		result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"saveOPBRePrint", saveRePrintParm);
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
		TParm saveParm = table.getParmValue();
		TParm actionParm = saveParm.getRow(row);
		actionParm.setData("OPT_USER", Operator.getID());
		actionParm.setData("OPT_TERM", Operator.getIP());
		actionParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		actionParm.setData("ADM_TYPE", saveParm.getValue("ADM_TYPE", row));
		return actionParm;
	}
    /**
	 * 补印
	 * 
	 * @param parm
	 *            TParm
	 * @param mzConfirmParm
	 *            TParm
	 * @param printParm
	 *            TParm
	 */
	private void onRePrint(TParm parm, TParm mzConfirmParm, TParm printParm) {
		parm.setData("DEPT_NAME", "TEXT", parm.getValue("DEPT_CODE_OPB")
				+ "   (" + parm.getValue("CLINICROOM_DESC_OPB") + ")"); // 科室诊室名称
		// 显示方式:科室(诊室)
		parm.setData("CLINICTYPE_NAME", "TEXT", this.getText("CLINICTYPE_CODE")
				+ "   (" + parm.getValue("QUE_NO_OPB") + "号)"); // 号别
		// 显示方式:号别(诊号)
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // 年月日
		parm.setData("BALANCE_NAME", "TEXT", "余 额"); // 余额名称
		parm.setData("CURRENT_BALANCE", "TEXT", "￥ " + "0.00" ); // 医疗卡剩余金额

		if (insFlg) {			
			parm.setData("PAY_CASH", "TEXT", "现金:"
					+ StringTool.round(
							(parm.getDouble("TOTAL", "TEXT") - printParm
									.getDouble("PAY_INS_CARD", 0)), 2)); // 现金
			// 个人账户
			String sqlamt = " SELECT ACCOUNT_PAY_AMT  FROM INS_OPD "
					+ " WHERE CASE_NO ='"
					+ mzConfirmParm.getValue("CASE_NO", 0) + "'"
					+ " AND CONFIRM_NO ='"
					+ mzConfirmParm.getValue("CONFIRM_NO", 0) + "'";
			TParm insaccountamtParm = new TParm(TJDODBTool.getInstance()
					.select(sqlamt));
			if (insaccountamtParm.getErrCode() < 0) {

			} else {
				parm.setData("PAY_ACCOUNT", "TEXT", "账户:"
						+ StringTool.round(insaccountamtParm.getDouble(
								"ACCOUNT_PAY_AMT", 0), 2));
				parm.setData("PAY_DEBIT", "TEXT", "医保:"
						+ StringTool.round((printParm.getDouble("PAY_INS_CARD",
								0) - insaccountamtParm.getDouble(
								"ACCOUNT_PAY_AMT", 0)), 2)); // 医保支付
			}
			String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SP_PRESON_TYPE' AND ID='"
					+ mzConfirmParm.getValue("SPECIAL_PAT", 0) + "'";// 医保特殊人员身份显示
			TParm insPresonParm = new TParm(TJDODBTool.getInstance()
					.select(sql));
			if (insPresonParm.getErrCode() < 0) {

			} else {
				parm.setData("SPC_PERSON", "TEXT", insPresonParm.getValue(
						"CHN_DESC", 0));
			}

		}
		parm.setData("DATE", "TEXT", yMd); // 日期
		parm.setData("USER_NAME", "TEXT", Operator.getID()); // 收款人
		if ("1".equals(mzConfirmParm.getValue("INS_CROWD_TYPE", 0))) {
			parm.setData("TEXT_TITLE", "TEXT", "门大联网已结算");
			if (admType.equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
		} else if ("2".equals(mzConfirmParm.getValue("INS_CROWD_TYPE", 0))) {
			parm.setData("TEXT_TITLE", "TEXT", "门特联网已结算");
			if (admType.equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
		}
//		this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGRECPPrint.jhw",
//				parm, true);
	    //parm中缺少RECEIPT_NO，如果某天普华也使用建行打票，票据上将不显示RECEIPT_NO（机打号码）
	    this.openPrintDialog(IReportTool.getInstance().getReportPath("REGRECPPrint.jhw"),
	                         IReportTool.getInstance().getReportParm("REGRECPPrint.class", parm), true);//报表合并modify by wanglong 20130730
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
		double servantAmt = 0.00;
		double illnesssubsidyamt = 0.00; //城乡大病金额
//		Pat pat = Pat.onQueryByMrNo(mrNo);

		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("INV_NO", recpParm.getValue("PRINT_NO", 0));
		parm.setData("RECP_TYPE", "OPB");// 收费类型
		// 查询是否医保 退费
		TParm result = INSOpdTJTool.getInstance().selectInsInvNo(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		// if (result.getCount("CASE_NO") <= 0) {// 不是医保退费
		// return ;
		// }
		// 医保打票操作
		if (null != result && null != result.getValue("CONFIRM_NO", 0)
				&& result.getValue("CONFIRM_NO", 0).length() > 0) {
			parm.setData("CONFIRM_NO", result.getValue("CONFIRM_NO", 0));
			TParm mzConfirmParm = INSMZConfirmTool.getInstance()
					.queryMZConfirm(parm);
			confirmNo = result.getValue("CONFIRM_NO", 0);
			cardNo = mzConfirmParm.getValue("INSCARD_NO", 0);// 医保卡号
			insOpdInParm.setData("CASE_NO", caseNo);
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

					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							;
					
				else

					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ARMY_AI_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							;
					
				
				gbNhiPay = TiMath.round(gbNhiPay, 2);

				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
				
			}
			// 城职门特
			if (insCrowdType.equals("1") && insPatType.equals("2")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				if (reimType.equals("1")){
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);
					
				}else{
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ARMY_AI_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);			
				}
				gbNhiPay = TiMath.round(gbNhiPay, 2);
				// 个人实际帐户支付
				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				// 现金支付
				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				// 补助金额
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
//				//城乡大病金额
//				illnesssubsidyamt = insOpdParm.getDouble("ILLNESS_SUBSIDY_AMT", 0);
				
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
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							//===ZHANGP 20120712 END
							;
					
				else
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
							+ insOpdParm.getDouble("FLG_AGENT_AMT", 0)
							//===ZHANGP 20120712 START
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							//===ZHANGP 20120712 END
							;
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
							" WHERE A.CASE_NO = '"+insOpdInParm.getData("CASE_NO")+"'"+
							" AND A.CTZ1_CODE = B.CTZ_CODE"+
							" AND A.REALDR_CODE =C.USER_ID";		
		TParm CtzDescParm = new TParm(TJDODBTool.getInstance().select(CtzDescSql));;
		
		//人员类别
		String personClass = CtzDescParm.getValue("CTZ_DESC", 0);
		// INS_CROWD_TYPE, INS_PAT_TYPE
		// 票据信息
		// 姓名
		oneReceiptParm.setData("PAT_NAME", "TEXT", patName);
		oneReceiptParm.setData("SEX", "TEXT", pat.getSexString());//性别
		oneReceiptParm.setData("ID_NO", "TEXT", pat.getIdNo());//
		
		
		
		// 特殊人员类别
		oneReceiptParm.setData("SPC_PERSON", "TEXT",
				(personClass+spcPerson).length() == 0 ? "" :personClass+" "+spcPerson);
//		// 社会保障号
//		oneReceiptParm.setData("Social_NO", "TEXT", cardNo);
		// 人员类别
		oneReceiptParm.setData("CTZ_DESC", "TEXT", "职工医保");
		oneReceiptParm.setData("COPY", "TEXT", "(COPY)");
		// 费用类别
		// ======zhangp 20120228 modify start
		if ("1".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "门大联网已结算");
			if (admType.equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
			// oneReceiptParm.setData("Cost_class", "TEXT", "门统");
		} else if ("2".equals(insPatType) || "3".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "门特联网已结算");
			if (admType.equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}

		}
		// 收款单位
		oneReceiptParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(regionCode));
		// 起付金额
		if(startStandard !=0 ){
			oneReceiptParm.setData("START_AMT_NAME", "TEXT","起付标准");
			oneReceiptParm.setData("START_AMT", "TEXT", StringTool.round(
					startStandard, 2));
		}
	
		//基金未报销显示文字======pangben 2012-7-12
		oneReceiptParm.setData("MAX_DESC", "TEXT", unreimAmt == 0 ? "" : "基金未报销金额:");
		//===ZHANGP 20120712 END
		// 最高限额余额
		oneReceiptParm.setData("MAX_AMT", "TEXT", unreimAmt == 0 ? "" : df
				.format(unreimAmt));
		//====zhangp 20120925 start
		//已超规定刷卡限额 个人垫付年终报销
		oneReceiptParm.setData("MAX_DESC2", "TEXT", unreimAmt == 0 ? "" : "/已超规定刷卡限额 个人垫付年终报销");
		//====zhangp 20120925 end
		// 账户支付
		oneReceiptParm.setData("DA_AMT", "TEXT", df.format(accountPay));

		// 费用合计
		oneReceiptParm.setData("TOT_AMT", "TEXT", df.format(recpParm.getDouble(
				"TOT_AMT", 0)));
		// 费用显示大写金额
		oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
				.numberToWord(recpParm.getDouble("TOT_AMT", 0)));

//		// 统筹支付
//		oneReceiptParm.setData("Overall_pay", "TEXT", StringTool.round(recpParm
//				.getDouble("Overall_pay", 0), 2));
//		// 个人支付
//		oneReceiptParm.setData("Individual_pay", "TEXT", df.format(recpParm
//				.getDouble("TOT_AMT", 0)));
		// 现金支付= 医疗卡金额+现金+绿色通道
		double payCash = StringTool.round(recpParm.getDouble("PAY_CASH", 0), 2)
				+ StringTool
						.round(recpParm.getDouble("PAY_MEDICAL_CARD", 0), 2)
				+ StringTool.round(recpParm.getDouble("PAY_OTHER1", 0), 2);
		// 现金支付
		oneReceiptParm.setData("Cash", "TEXT", gbCashPay == 0 ? payCash : df
				.format(gbCashPay));

		// 账户支付---医疗卡支付
		oneReceiptParm.setData("Recharge", "TEXT", 0.00);
		
		// 医疗救助金额
		if (agentAmt != 0) {
			oneReceiptParm.setData("AGENT_NAME", "TEXT", "补助金额");
			oneReceiptParm.setData("AGENT_AMT", "TEXT", df.format(agentAmt));
		}
		// 城乡大病金额
		if (illnesssubsidyamt != 0) {
			oneReceiptParm.setData("ILLNESS_SUBSIDY_AMT_NAME", "TEXT", "城乡大病支付");
			oneReceiptParm.setData("ILLNESS_SUBSIDY_AMT", "TEXT", df.format(illnesssubsidyamt));
		}
		oneReceiptParm.setData("QTYL", "TEXT", (illnesssubsidyamt+agentAmt) == 0? "0.00" : 
		df.format(illnesssubsidyamt+agentAmt));
		//医疗机构类型（三甲医院）
		String regionSql = "SELECT HOSP_CLASS FROM SYS_REGION WHERE REGION_CODE = '"+ Operator.getRegion()+"'";
	    TParm regionParm = new TParm(TJDODBTool.getInstance().select(regionSql));
	    String hospClass = regionParm.getValue("HOSP_CLASS",0);
	    String sqlhospClass = "SELECT CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_HOSPITAL_CLASS' AND ID = '"+hospClass+"'";
	    TParm  hospClassParm =  new TParm(TJDODBTool.getInstance().select(sqlhospClass));
	    oneReceiptParm.setData("HOSP_CLASS","TEXT",hospClassParm.getValue("CHN_DESC",0));

		
		oneReceiptParm.setData("MR_NO", "TEXT", mrNo);
		// =====zhangp 20120229 modify end
		// 打印日期
		oneReceiptParm.setData("OPT_DATE", "TEXT", StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy  MM  dd"));
		// 医保金额
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
		
		oneReceiptParm.setData("DETAIL", "TEXT", "(详见费用清单)");
		if (cardNo.equals("")) {
			
			oneReceiptParm.setData("CARD_CODE", "TEXT","");// 如果不是医保
			// 显示身份证号
		} else {			
			String idNOBefore = cardNo.substring(0, 3);
            String idNOEnd = cardNo.substring(cardNo.length()-3, cardNo.length());
            oneReceiptParm.setData("CARD_CODE", "TEXT", idNOBefore+"************"+idNOEnd);
		}
			// =====20120229 zhangp modify end
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
		TParm dparm = new TParm();
		dparm.setData("CASE_NO", caseNo);
		dparm.setData("ADM_TYPE", admType);
		onPrintCashParm(oneReceiptParm, recpParm, dparm);
		
		//业务流水号
		if(null != result && null != result.getValue("CONFIRM_NO",0)
				&& result.getValue("CONFIRM_NO").length() > 0){
			oneReceiptParm.setData("RECEIPT_NO", "TEXT",  result.getValue("CONFIRM_NO",0));
		}else{
			oneReceiptParm.setData("RECEIPT_NO", "TEXT",  recpParm.getValue("RECEIPT_NO", 0));
		}
		//oneReceiptParm中缺少RECEIPT_NO，如果某天普华也使用建行打票，票据上将不显示RECEIPT_NO（机打号码）
	    this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
                             IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm), true);//报表合并modify by wanglong 20130730
		return;

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
	 * 现金打票明细入参
	 */
	private void onPrintCashParm(TParm oneReceiptParm, TParm recpParm,
			TParm dparm) {
		String receptNo = recpParm.getData("RECEIPT_NO", 0).toString();
		dparm.setData("NO", receptNo);
		TParm tableresultparm = OPBTool.getInstance().getReceiptDetail(dparm);
		if (oneReceiptParm.getCount() > 10) {
			tableresultparm.setData("DETAIL", "TEXT", "(详见费用明细表)");
		}
		oneReceiptParm.setData("TABLE", tableresultparm.getData());
	}
}
