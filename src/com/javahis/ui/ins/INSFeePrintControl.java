package com.javahis.ui.ins;

import java.text.DecimalFormat;

import jdo.ekt.EKTIO;
import jdo.ins.INSOpdOrderTJTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJReg;
import jdo.opb.OPBReceiptTool;
import jdo.opb.OPBTool;
import jdo.opd.OrderTool;
import jdo.bil.BILInvrcptTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import com.tiis.util.TiMath;

/**
 * 
 * <p>
 * Title:天津医保卡收费
 * </p>
 * 
 * <p>
 * Description:门诊收费医疗卡金额不足情况,先执行分割再去收费
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangb 2012.03.29
 * @version 4.0
 */
public class INSFeePrintControl extends TControl {
	private TParm insParm;// 需要分割的医嘱
	private TParm result;// 返回数据 保存费用分割所有数据 和费用结算数据
	private boolean exeError = false;// 执行操作错误
	private boolean exeSplit = false;// 判断是否执行费用分割
	private TParm parm;
	private TParm ektParm = null;
	private String caseNo;
	private TParm ektOrderParm = null;//去除集合医嘱主项的数据
	//private String idNo;
	private String admType = "";
	//private TParm parmSum;//所有的医嘱
	private double billAmt;//未收费的医嘱金额
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		// 去掉菜单栏
		// TFrame F=(TFrame)this.getComponent("UI");
		// F.setUndecorated(true);
		callFunction("UI|tButton_1|setEnabled", false);
		callFunction("UI|tButton_10|setEnabled", false);

		parm = (TParm) getParameter();
		if (null == parm) {
			return;
		}
		//============pangben 2012-7-20 票据号码校验操作
		if (null==parm.getValue("PRINT_NO")||parm.getValue("PRINT_NO").length()<=0) {
			this.messageBox("没有获得票据号码");
			callFunction("UI|tButton_9|setEnabled", false);
			return;
		}
		TParm parmTemp =new TParm();
		parmTemp.setData("RECP_TYPE","OPB");
		parmTemp.setData("INV_NO",parm.getValue("PRINT_NO"));
		//========查询此票号是否使用
		TParm result=BILInvrcptTool.getInstance().selectAllData(parmTemp);
		if (result.getCount()>0) {
			this.messageBox("此票号已经使用,请联系信息中心");
			callFunction("UI|tButton_9|setEnabled", false);
			return;
		}
		//parmSum=parm.getParm("parmSum");//所有的医嘱 包括集合医嘱用来修改 OPD_ORDER MED_APPLY数据
		billAmt=parm.getDouble("billAmt");//未收费的医嘱金额
		admType = parm.getValue("ADM_TYPE");// 类别 门急诊
		ektParm = parm.getParm("ektParm");// 医疗卡信息
		caseNo = parm.getValue("CASE_NO");// 就诊号码
		//idNo = parm.getValue("ID_NO");// 身份证号码
		ektOrderParm = parm.getParm("orderParm");//所有医嘱包括收费未收费数据
		this.setValue("PRINT_NO", parm.getValue("PRINT_NO"));
		TComboBox com = (TComboBox) this.getComponent("PAY_WAY");
		if (parm.getBoolean("PAY_TYPE")) {
			com.setSelectedIndex(2);// 默认 医疗卡支付
		} else {
			com.setSelectedIndex(1);// 默认 现金支付
		}
		this.setValue("MR_NO", parm.getValue("MR_NO"));
		this.setValue("NAME", parm.getValue("NAME"));
		// System.out.println("PARM:::::"+parm);
		this.setValue("INS_TYPE", parm.getValue("INS_TYPE").toString());// 医保类别

		this.setValue("FeeY", parm.getDouble("FeeY"));// 应收金额
		this.setValue("INS_FEE", ektParm.getDouble("CURRENT_BALANCE"));// 实收金额
		// =医保金额+医疗卡金额
		this.setValue("EKT_SUMAMT", ektParm.getDouble("CURRENT_BALANCE"));// 医疗卡余额
		insParm = parm.getParm("insParm");// 需要分割的医嘱
	}

	/**
	 * 分割金额
	 */
	public void onSplit() {
		callFunction("UI|tButton_9|setEnabled", false);//=======pangben 2013-3-7 添加避免重复点击按钮
		TParm spParm = splitRun();
		//=====pangben 2013-3-13 添加校验
		if(null!=result.getValue("NO_EXE_FLG") && result.getValue("NO_EXE_FLG").equals("Y"))
			return;	
		else
			callFunction("UI|tButton_1|setEnabled", true);
		if (spParm == null) {
			this.messageBox("结算失败,请执行撤销结算操作");
			return;
		} else {
			this.messageBox("结算成功");
			callFunction("UI|tButton_10|setEnabled", true);
		}
		onNewAmt(spParm);
	}

	/**
	 * 金额 显示
	 * 
	 * @param spParm
	 */
	private void onNewAmt(TParm spParm) {
		// 医保支付金额
		this.setValue("INS_AMT", spParm.getDouble("ACCOUNT_AMT"));
		// 医疗卡支付金额
		this.setValue("EKT_AMT", spParm.getDouble("UACCOUNT_AMT"));
		// 实收金额
		this.setValue("INS_FEE", spParm.getDouble("ACCOUNT_AMT")
				+ this.getValueDouble("EKT_SUMAMT")+this.getValueDouble("FeeY")-billAmt);
		double cashAmt = parm.getDouble("FeeY")
				- this.getValueDouble("INS_FEE");
		// 找零
		this.setValue("FeeZ", cashAmt <= 0 ? Math.abs(cashAmt) : -cashAmt);
		// 现金支付金额
		this.setValue("CASH_AMT", cashAmt <= 0 ? 0 : cashAmt);
	}

	/**
	 * 执行操作
	 */
	public void onOK() {
		// 收费方式不可以为空
		if (!this.emptyTextCheck("PAY_WAY")) {
			return;
		}
		if (!exeSplit) {
			this.messageBox("没有获得医保数据");
			return;
		}
		if (this.getValueDouble("INS_FEE") < this.getValueDouble("FeeY")) {
			this.messageBox("金额不足,请执行医保结算或医疗卡充值操作");
			return;
		}
		insParm.setData("comminuteFeeParm", result.getParm(
				"comminuteFeeParm").getData()); // 费用分割数据
		insParm.setData("settlementDetailsParm", result.getParm(
				"settlementDetailsParm").getData()); // 费用结算
		TParm exeParm = INSTJReg.getInstance()
				.insCommFunction(insParm.getData());
		if (exeParm.getErrCode() < 0) {
			err(exeParm.getErrCode() + " " + exeParm.getErrText());
			this.messageBox(exeParm.getErrText());
			return;
		}
		// opdParm.setData("INS_FLG", "Y"); // 医保使用
		// // 需要修改的地方
		// opdParm.setData("MR_NO", pat.getMrNo());
		// opdParm.setData("RECP_TYPE", "OPB"); // 收费类型
		ektParm.setData("NEW_CURRENT_BALANCE", this
				.getValueDouble("EKT_SUMAMT")
				+ this.getValueDouble("INS_AMT"));// 医保金额与医疗卡中的金额总和
		TParm opdParm = insExeUpdate(this.getValueDouble("INS_AMT"), ektParm,
				caseNo, "OPBT");
		if (opdParm.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		// 调用HL7
		TParm resultParm=OPBTool.getInstance().sendHL7Mes(ektOrderParm.getParm("hl7Parm"),parm.getValue("NAME"),false,caseNo);
		if (resultParm.getErrCode() < 0) {
			this.messageBox(resultParm.getErrText());
		}
		exeParm = new TParm();
		exeParm.setData("CASE_NO", caseNo);
		exeParm.setData("MR_NO", this.getValueString("MR_NO"));
		exeParm.setData("INV_NO", this.getValue("PRINT_NO"));
		exeParm.setData("OPT_USER", Operator.getID());
		exeParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		exeParm.setData("OPT_TERM", Operator.getIP());
		exeParm.setData("REGION_CODE", Operator.getRegion());
		exeParm.setData("START_INVNO", parm.getValue("START_INVNO"));
		exeParm.setData("TOT_AMT", this.getValueDouble("FeeY"));//应收金额
		exeParm.setData("ADM_TYPE", admType); // 挂号方式 :0 \E
		exeParm.setData("ACCOUNT_AMT", this.getValueDouble("INS_AMT")); //医保金额
		
//		TParm selOpdParm = new TParm();
//		selOpdParm.setData("CASE_NO", caseNo);
		TParm opdOrderParm = insParm.getParm("REG_PARM");
		opdOrderParm.setData("INS_AMT", -this.getValueDouble("INS_AMT"));
		opdOrderParm.setData("NAME", this.getValue("NAME"));
		opdOrderParm.setData("INS_FLG", "Y"); // 医保使用
		// 需要修改的地方
		opdOrderParm.setData("MR_NO", this.getValue("MR_NO"));
		opdOrderParm.setData("RECP_TYPE", "OPB"); // 收费类型
		exeParm.setData("opdParm", opdOrderParm.getData()); // 获得一条汇总金额
		result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onOPBEktprint", exeParm);
		if(result.getErrCode()<0){
			this.messageBox("E0005");
			return ;
		}
		//更新医保表中的RECEIPT_NO
		if (!updateReceiptNo(result,caseNo,"OPB"))
			return;
		
		this.messageBox("P0005");
		
		// String type = this.getValue("PAY_WAY").toString();// 收费方式
		// result.setData("RETURN_TYPE", 1);// 执行操作
		// result.setData("PAY_WAY", type);// 支付方式 1.现金 2.医疗卡
		// result.setData("ACCOUNT_AMT", this.getValue("INS_FEE"));
		// result.setData("UACCOUNT_AMT", this.getValue("FeeZ"));
		// 门诊收据档数据:医疗卡收费打票|现金收费打票||医保打票
		TParm recpParm = OPBReceiptTool.getInstance().getOneReceipt(
				result.getValue("RECEIPT_NO", 0));
		onPrint(recpParm);
		this.setReturnValue(opdParm);
		this.closeWindow();
	}
	/**
	 * 更新医保表中的RECEIPT_NO
	 * 
	 */
	public boolean updateReceiptNo(TParm parm,String caseNo,String recpType) {
	     String receiptNo = parm.getValue("RECEIPT_NO", 0);
	     String confirmNo = insParm.getValue("CONFIRM_NO");
	     String sql1 = " UPDATE INS_OPD SET RECEIPT_NO ='"+ receiptNo+ "'" +
					   " WHERE CASE_NO ='" + caseNo + "'" +
					   " AND CONFIRM_NO = '" + confirmNo + "'" +
					   " AND RECP_TYPE = '" + recpType + "'";
		TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql1));
		if (updateParm.getErrCode() < 0) {
			err(updateParm.getErrCode() + " " + updateParm.getErrText());
			updateParm.setErr(-1, "更新医保表失败");
			return false;
		}
		return true;	
	}
	/**
	 * 打印票据封装===================pangben 20111014
	 * 
	 * @param recpParm
	 *            TParm
	 * @param flg
	 *            boolean
	 */
	private void onPrint(TParm recpParm) {
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
		Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
		// 医保打票操作
		if (null != insParm && null != insParm.getValue("CONFIRM_NO")
				&& insParm.getValue("CONFIRM_NO").length() > 0) {
			confirmNo = insParm.getValue("CONFIRM_NO");
			insOpdInParm.setData("CASE_NO", caseNo);
			insOpdInParm.setData("CONFIRM_NO", confirmNo);
			cardNo = insParm.getValue("CARD_NO");
			TParm insOpdParm = INSOpdTJTool.getInstance().queryForPrint(
					insOpdInParm);
			TParm insPatparm = INSOpdTJTool.getInstance().selPatDataForPrint(
					insOpdInParm);		
			unreimAmt = insOpdParm.getDouble("UNREIM_AMT", 0);// 基金未报销
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
			reimType=insOpdParm.getValue("REIM_TYPE", 0);
			// 城职普通
			if (insCrowdType.equals("1") && insPatType.equals("1")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				if (reimType.equals("1"))
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);
				else
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ARMY_AI_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);										
				gbNhiPay = TiMath.round(gbNhiPay, 2);
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);

				
			}
			// 城职门特
			if (insCrowdType.equals("1") && insPatType.equals("2")) {

				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);
				if (reimType.equals("1"))
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);
				else
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ARMY_AI_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);
					
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
					// 医保支付
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
			//START_AMT_NAME
		}
			
		//查询人员类别
		String CtzDescSql =" SELECT B.CTZ_DESC,C.USER_NAME FROM REG_PATADM A,SYS_CTZ B,SYS_OPERATOR C"+
		                   " WHERE A.CASE_NO = '"+insOpdInParm.getData("CASE_NO")+"'"+
		                   " AND A.CTZ1_CODE = B.CTZ_CODE"+
		                   " AND A.REALDR_CODE =C.USER_ID";		 
		TParm CtzDescParm = new TParm(TJDODBTool.getInstance().select(CtzDescSql));
		
		//人员类别
		String personClass = CtzDescParm.getValue("CTZ_DESC", 0);
		// 票据信息
		oneReceiptParm.setData("PAT_NAME", "TEXT", this.getValue("NAME"));	// 姓名
		//医保类别
		oneReceiptParm.setData("SPC_PERSON", "TEXT",
				(personClass+spcPerson).length() == 0 ? "自费" : personClass+" "+spcPerson);
		// 人员类别
		oneReceiptParm.setData("CTZ_DESC", "TEXT", "职工医保");
		// 费用类别
		
		oneReceiptParm.setData("SEX", "TEXT", pat.getSexString());//性别
		//医疗机构类型
		String regionSql = "SELECT HOSP_CLASS FROM SYS_REGION WHERE REGION_CODE = '"+ Operator.getRegion()+"'";
	    TParm regionParm = new TParm(TJDODBTool.getInstance().select(regionSql));
	    String hospClass = regionParm.getValue("HOSP_CLASS",0);
	    String sqlhospClass = "SELECT CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_HOSPITAL_CLASS' AND ID = '"+hospClass+"'";
	    TParm  hospClassParm =  new TParm(TJDODBTool.getInstance().select(sqlhospClass));
	    oneReceiptParm.setData("HOSP_CLASS","TEXT",hospClassParm.getValue("CHN_DESC",0));
		if ("1".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "门大联网已结算");
			if (admType.equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
		} else if ("2".equals(insPatType) || "3".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "门特联网已结算");
			if (admType.equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
		}
		// 医疗机构名称
		oneReceiptParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(Operator.getRegion()));
		System.out.println("HOSP_DESC="+oneReceiptParm.getValue("HOSP_DESC"));
		// 起付金额
		if(startStandard !=0 ){
			oneReceiptParm.setData("START_AMT_NAME", "TEXT","起付标准");
			oneReceiptParm.setData("START_AMT", "TEXT", StringTool.round(
					startStandard, 2));
		}
		// 最高限额余额
		oneReceiptParm.setData("MAX_AMT", "TEXT", unreimAmt == 0 ? "" : df
				.format(unreimAmt));
		//基金未报销显示文字
		oneReceiptParm.setData("MAX_DESC", "TEXT", unreimAmt == 0 ? "" : "基金未报销金额:");
		//已超规定刷卡限额 个人垫付年终报销
		oneReceiptParm.setData("MAX_DESC2", "TEXT", unreimAmt == 0 ? "" : "/已超规定刷卡限额 个人垫付年终报销");
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
		}
		oneReceiptParm.setData("MR_NO", "TEXT", this.getValue("MR_NO"));
		
		// 打印日期
		oneReceiptParm.setData("OPT_DATE", "TEXT", StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy  MM  dd"));
		System.out.println("OPT_DATE="+oneReceiptParm.getValue("OPT_DATE"));
		// 医保统筹支付
		oneReceiptParm.setData("PAY_DEBIT", "TEXT", gbNhiPay);
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
		// 社会保障号（显示身份证号）
		if (cardNo.equals("")) {
			oneReceiptParm.setData("CARD_CODE", "TEXT", "");		
		} else {			
			String idNOBefore = cardNo.substring(0, 3);
			String idNOEnd = cardNo.substring(cardNo.length()-3, cardNo.length());
            oneReceiptParm.setData("CARD_CODE", "TEXT", idNOBefore+"************"+idNOEnd);

		}
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
		if(null != insParm && null != insParm.getValue("CONFIRM_NO")
				&& insParm.getValue("CONFIRM_NO").length() > 0){
			oneReceiptParm.setData("RECEIPT_NO", "TEXT",  insParm.getValue("CONFIRM_NO"));
		}else{
			oneReceiptParm.setData("RECEIPT_NO", "TEXT",  recpParm.getValue("RECEIPT_NO", 0));
		}
	    this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
	                         IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm), true);//报表合并modify by wanglong 20130730

	}

	/**
	 * 医疗卡打票明细入参
	 * 
	 * @param oneReceiptParm
	 *            TParm
	 * @param recpParm
	 *            TParm
	 * @param dparm
	 *            TParm
	 */
	private void onPrintCashParm(TParm oneReceiptParm, TParm recpParm,
			TParm dparm) {
		String receptNo = recpParm.getData("RECEIPT_NO", 0).toString();
		dparm.setData("NO", receptNo);
		TParm tableresultparm = OPBTool.getInstance().getReceiptDetail(dparm);
		if (tableresultparm.getCount() > 6) {
			oneReceiptParm.setData("DETAIL", "TEXT", "(详见费用明细表)");
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
	 * 医疗卡操作保存此次医保卡扣款金额
	 * 
	 * @param returnParm
	 * @return
	 */
	private TParm insExeUpdate(double accountAmt, TParm readCardParm,
			String caseNo, String business_type) {
		// 入参:AMT:本次操作金额 BUSINESS_TYPE :本次操作类型 CASE_NO:就诊号码
		TParm orderParm = new TParm();
		orderParm.setData("AMT", -accountAmt);//医保支付金额
		orderParm.setData("INS_AMT", accountAmt);//医保支付金额
		orderParm.setData("BUSINESS_TYPE", business_type);
		orderParm.setData("CASE_NO", caseNo);
		//orderParm.setData("EXE_FLG", "Y");
		orderParm.setData("readCard", readCardParm.getData());
		orderParm.setData("OPT_USER", Operator.getID());
		orderParm.setData("FeeY", this.getValueDouble("FeeY"));// 应收金额
		orderParm.setData("OPT_TERM", Operator.getIP());
		orderParm.setData("orderParm", ektOrderParm.getData());// 医疗卡执行医嘱
		orderParm.setData("billAmt", billAmt);// 未执行的医嘱金额
		orderParm.setData("INS_EXE_FLG", "Y");// 门诊收费医疗卡金额不足,医保分割金额 特殊
		orderParm.setData("PRINT_NO", this.getValue("PRINT_NO"));
		orderParm.setData("CONFIRM_NO",insParm.getValue("CONFIRM_NO"));
		orderParm.setData("RECP_TYPE",insParm.getValue("RECP_TYPE"));
		//orderParm.setData("parmSum",parmSum.getData());
		//orderParm.setData("ORDER", parm.getParm("ORDER").getData());
		TParm insExeParm = TIOM_AppServer.executeAction("action.ekt.EKTAction",
				"exeInsSave", orderParm);
		return insExeParm;

	}

	/**
	 * 执行分割操作
	 * 
	 * @return
	 */
	private TParm splitRun() {
		if (null == insParm
				|| insParm.getParm("REG_PARM").getCount("ORDER_CODE") <= 0) {
			this.messageBox("没有需要执行的数据");
			return null;
		}
		// 城居门特 ：函数DataDown_cmts, 门特刷卡交易（E）,得到个人信息
		// 城职普通: 得到个人信息 调用函数DataDown_czys, 门诊刷卡（L）
		// 城职门特 : 函数DataDown_mts, 门特刷卡交易（E）,得到个人信息
		// //System.out.println("ruleParm::::::"+ruleParm);
		// double insSum = 0.00;// 费用分割累计金额
		// if (feeFlg) {// 收费
		// return exeFee(insParm);
		// }
		return exeFee(insParm);
	}

	/**
	 * 执行收费操作
	 * 
	 * @param regParm
	 * @param opbReadCardParm
	 * @return
	 */
	private TParm exeFee(TParm regParm) {
		// 执行共用的费用分割、添加ins_opd_order 表、明细上传操作
		// 执行费用分割 函数：DataDown_sp1 方法 B
		// 执行上传明细 函数: 函数DataUpload,（B）方法
		regParm.setData("NEW_REGION_CODE", Operator.getRegion());// 区域代码
		regParm.setData("FeeY", parm.getDouble("FeeY"));
		regParm.setData("CASE_NO_SUM", caseNo);
		regParm.setData("OPB_RECP_TYPE", "Y");//门诊收费操作
		result = INSTJFlow.getInstance().comminuteFeeAndInsOrder(regParm);// 费用分割
		exeError = true;// 错误累计
		// 所有医嘱
		if (result.getErrCode() < 0) {
			this.messageBox("分割出现错误:" + result.getErrText());
			exeSplit = false;
			this.grabFocus("tButton_1");
			return null;
		} else {
			if (null != result.getValue("MESSAGE")
					&& result.getValue("MESSAGE").length() > 0) {
				this.messageBox(result.getValue("MESSAGE"));
				exeSplit = false;
				this.grabFocus("tButton_1");
			    return null;
			} else {
				exeSplit = true;// 执行费用分割操作
				this.grabFocus("tButton_10");
			}
		}
		// TParm settlementDetailsParm =
		// result.getParm("settlementDetailsParm");// 费用结算参数
		TParm parm = INSOpdTJTool.getInstance().queryForPrint(regParm);
		TParm accountParm = getAmt(regParm.getInt("INS_TYPE"), parm);
		return accountParm;

	}

	public TParm getAmt(int insType, TParm returnParm) {
		// 取得医保专项基金支付金额
		double sOTOT_Amt = 0.00;
		// 取得现金支付金额
		double sUnaccount_pay_amt = 0.00;

		// 城职
		// System.out.println("城职returnParm:::"+returnParm);

		if (insType == 1) {
			sOTOT_Amt = returnParm.getDouble("TOT_AMT", 0) - // 总金额
					returnParm.getDouble("UNACCOUNT_PAY_AMT", 0) - // 非账户支付
					returnParm.getDouble("UNREIM_AMT", 0);// 基金未报销
			sUnaccount_pay_amt = returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					+ returnParm.getDouble("UNREIM_AMT", 0);// 现金支付金额
		}
		// 城职门特
		if (insType == 2) {
			sOTOT_Amt = returnParm.getDouble("TOT_AMT", 0)
					- returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					- returnParm.getDouble("UNREIM_AMT", 0);
			sUnaccount_pay_amt = returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					+ returnParm.getDouble("UNREIM_AMT", 0);
		}
		// 城居门特
		if (insType == 3) {
			if (null != returnParm.getValue("REIM_TYPE", 0)
					&& returnParm.getInt("REIM_TYPE", 0) == 1) {
				sOTOT_Amt = returnParm.getDouble("TOTAL_AGENT_AMT", 0)
						+ returnParm.getDouble("ARMY_AI_AMT", 0)
						+ returnParm.getDouble("FLG_AGENT_AMT", 0)
						+ returnParm.getDouble("ILLNESS_SUBSIDY_AMT", 0)//城乡大病金额
						- returnParm.getDouble("UNREIM_AMT", 0);

			} else {
				sOTOT_Amt = returnParm.getDouble("TOTAL_AGENT_AMT", 0)
						+ returnParm.getDouble("FLG_AGENT_AMT", 0)
						+ returnParm.getDouble("ARMY_AI_AMT", 0)
						+ returnParm.getDouble("ILLNESS_SUBSIDY_AMT", 0);//城乡大病金额

			}

			// 现金支付
			sUnaccount_pay_amt = returnParm.getDouble("TOT_AMT", 0)
					- returnParm.getDouble("TOTAL_AGENT_AMT", 0)
					- returnParm.getDouble("FLG_AGENT_AMT", 0)
					- returnParm.getDouble("ARMY_AI_AMT", 0)
					- returnParm.getDouble("ILLNESS_SUBSIDY_AMT", 0)//城乡大病金额
					+ returnParm.getDouble("UNREIM_AMT", 0);
		}
		TParm parm = new TParm();
		parm.setData("ACCOUNT_AMT", sOTOT_Amt);
		parm.setData("UACCOUNT_AMT", sUnaccount_pay_amt);
		// parm.setData("ACCOUNT_AMT", 1.5);
		// parm.setData("UACCOUNT_AMT", 3.5);
		return parm;
	}

	/**
	 * 
	 * 执行退费操作
	 * 
	 * @return
	 */
	public double reSetExeFee(TParm parm) {
		TParm result = INSTJFlow.getInstance().selectResetFee(parm);
		if (result.getInt("INS_PAT_TYPE", 0) == 1) {
			return result.getDouble("NHI_AMT");
		} else {
			return result.getDouble("INS_PAY_AMT");
		}

	}

	/**
	 * 取消操作
	 */
	public void onCancel() {
		// System.out.println("EXEERROR::" + exeError);
		// 撤销费用结算操作
		if (exeError) {
			if (this.messageBox("提示", "是否执行结算撤销操作", 2) != 0) {
				return;
			}
			TParm result = INSTJFlow.getInstance().cancelBalance(insParm);// 取消费用结算操作
			if (result.getErrCode() < 0) {
				// System.out.println("撤销费用结算操作:" + result.getErrText());
				this.messageBox(result.getValue("PROGRAM_MESSAGE"));
			} else {
				INSOpdTJTool.getInstance().deleteINSOpd(insParm);// 删除数据
				INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(insParm);
				TParm parm=new TParm();
				parm.setData("CASE_NO",insParm.getValue("CASE_NO"));
				parm.setData("EXE_USER",Operator.getID());
				parm.setData("EXE_TERM",Operator.getIP());
				parm.setData("EXE_TYPE",insParm.getValue("RECP_TYPE"));//类型
				INSRunTool.getInstance().deleteInsRun(parm);//取消操作删除在途状态
				this.messageBox("P0005");
				this.setValue("INS_FEE", 0.00);
				this.setValue("INS_AMT", 0.00);
				this.setValue("EKT_AMT", 0.00);
				this.setValue("CASH_AMT", 0.00);
				// 分割后的金额
				this.setValue("FeeZ", 0.00);
				callFunction("UI|tButton_1|setEnabled", false);
				callFunction("UI|tButton_10|setEnabled", false);
				exeSplit = false;
				callFunction("UI|tButton_9|setEnabled", true);
			}
		}

	}

	/**
	 * 读卡操作
	 */
	public void onReadEKT() {
		// 读取医疗卡
		ektParm = EKTIO.getInstance().TXreadEKT();
		if (null == ektParm || ektParm.getErrCode() < 0
				|| ektParm.getValue("MR_NO").length() <= 0) {
			this.messageBox(ektParm.getErrText());
			ektParm = null;
			return;
		}
		// 判断病患是否相同
		if (!ektParm.getValue("MR_NO").equals(this.getValue("MR_NO"))) {
			this.messageBox("此医疗卡病患信息与当前病患不符");
			return;
		}
		this.setValue("EKT_SUMAMT", ektParm.getDouble("CURRENT_BALANCE"));// 医疗卡金额
		
		double cashAmt = billAmt
				- this.getValueDouble("INS_AMT")
				- this.getValueDouble("EKT_SUMAMT");
		// 实收金额
		this.setValue("INS_FEE", this.getValueDouble("FeeY")-cashAmt);//=====pangben 2013-3-1修改显示金额问题
		// 找零
		this.setValue("FeeZ", cashAmt <= 0 ? Math.abs(cashAmt) : -cashAmt);
		// 现金支付金额
		this.setValue("CASH_AMT", cashAmt <= 0 ? 0 : cashAmt);
	}

	/**
	 * 充值
	 */
	public void onFee() {
		TParm parm =new TParm();
		parm.setData("FLG","Y");
		parm = (TParm) openDialog("%ROOT%\\config\\ekt\\EKTTopUp.x",
				parm);
		onReadEKT();
	}
}
