package com.javahis.ui.ekt.testEkt.impl;

import jdo.bil.BIL;
import jdo.ekt.EKTIO;
import jdo.ins.INSTJReg;
import jdo.reg.PatAdmTool;
import jdo.reg.REGSysParmTool;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.ekt.testEkt.EktParam;
import com.javahis.ui.ekt.testEkt.EktRegOrOpbClient;
import com.javahis.ui.ekt.testEkt.IEktTradeStrategy;
import com.javahis.ui.reg.REGPatAdmControl;

public class EktTradeStrategyRegImpl implements IEktTradeStrategy {
	
	private EktParam ektParam;
	private Reg reg;
	private Pat pat;
	public REGPatAdmControl regPatAdmControl;
	private EktRegOrOpbClient ektRegOrOpbClient;
	
	
	public EktTradeStrategyRegImpl(EktParam ektParam){
		this.ektParam = ektParam;
		regPatAdmControl = ektParam.getRegPatAdmControl();
		ektParam.settControl(regPatAdmControl);
		ektRegOrOpbClient = new EktRegOrOpbClient();
	}
	
	
	

	@Override
	public <T> EktParam creatParam(T t) {
		reg = (Reg) t;
		reg.setEktSql(null);
		if (reg == null) {
			return null;
		}
		
		String payWay = ektParam.getOrderParm().getValue("payWay");
		String caseNo = ektParam.getOrderParm().getValue("caseNo");
		pat = ektParam.getPat();

		// 医疗卡支付
		if (payWay.equals("PAY_MEDICAL_CARD")) {
			// 生成CASE_NO 因为医疗卡需要CASE_NO 所以在用医疗卡支付的时候先生成CASE_NO
			if ("N".endsWith(reg.getApptCode())) {
				// System.out.println("222222222222222222");
				if (null != caseNo && caseNo.length() > 0) {
					reg.setCaseNo(caseNo);
				} else {
					reg.setCaseNo(SystemTool.getInstance().getNo("ALL", "REG",
							"CASE_NO", "CASE_NO"));
				}
				// 保存医疗卡
				if (!onTXEktSave("Y",null)) {
//					System.out.println("!!!!!!!!!!!医疗卡保存错误");
					return null;
				}
				if (null != regPatAdmControl.greenParm
						&& null != regPatAdmControl.greenParm.getValue("GREEN_FLG")
						&& regPatAdmControl.greenParm.getValue("GREEN_FLG").equals("Y")) {
					// 使用绿色通道金额
					reg.getRegReceipt().setPayMedicalCard(
							TypeTool.getDouble(regPatAdmControl.greenParm.getDouble("EKT_USE")));
					reg.getRegReceipt().setOtherFee1(
							regPatAdmControl.greenParm.getDouble("GREEN_USE"));
				}
			}
		}
		
		if (payWay.equals("PAY_INS_CARD")) {
			TParm result = null;
			// 医保卡支付
			result = onSaveRegTwo(payWay, regPatAdmControl.ins_exe, caseNo);
			if (null == result) {
				return null;
			}
			regPatAdmControl.ins_exe = result.getBoolean("INS_EXE");
			regPatAdmControl.ins_amt = result.getDouble("INS_AMT");
			regPatAdmControl.accountamtforreg = result.getDouble("ACCOUNT_AMT_FORREG");
			ektParam.setConfirmNo(reg.getConfirmNo());
		}
		
		ektParam.setOpType("");
		return ektParam;
	}

	@Override
	public EktParam openClient(EktParam ektParam) {

		ektParam = ektRegOrOpbClient.openClient(ektParam);
		
		if(ektParam != null){
			
			String type = ektParam.getOpType();
			// System.out.println("type===" + type);
			if (type.equals("3")) {
				regPatAdmControl.messageBox("E0115");
				return null;
			}
			if (type.equals("2")) {
				return null;
			}
			if (type.equals("-1")) {
				regPatAdmControl.messageBox("读卡错误!");
				return null;
			}
			
			System.out.println("ektParam.getSqls()-----"+ektParam.getSqls());
			reg.setEktSql(ektParam.getSqls());
			
			TParm parm = ektParam.getOrderParm().getParm("result");
			regPatAdmControl.tredeNo = parm.getValue("TREDE_NO");
			regPatAdmControl.businessNo = parm.getValue("BUSINESS_NO"); // //出现医疗卡扣款操作问题使用
			regPatAdmControl.ektOldSum = parm.getValue("OLD_AMT"); // 执行失败撤销的金额
			regPatAdmControl.ektNewSum = parm.getValue("EKTNEW_AMT"); // 扣款以后的金额
			// 判断是否操作绿色通道
			if (null != parm.getValue("GREEN_FLG")
					&& parm.getValue("GREEN_FLG").equals("Y")) {
				regPatAdmControl.greenParm = parm;
			}
			
		}
		
		return ektParam;
	}
	
	public EktParam openClientR(EktParam ektParam) {
		reg = ektParam.getReg();
		
		ektParam = ektRegOrOpbClient.openClient(ektParam);
		
		if(ektParam != null){
			
			String type = ektParam.getOpType();
			// System.out.println("type===" + type);
			if (type.equals("3") || type.equals("-1")) {
				regPatAdmControl.messageBox("E0115");
				return null;
			}
			if (type.equals("2")) {
				return null;
			}
			
			reg.setEktSql(ektParam.getSqls());
			
			TParm parm = ektParam.getOrderParm().getParm("result");
			regPatAdmControl.tradeNoT = parm.getValue("TREDE_NO");
			
			
		}
		
		return ektParam;
	}
	
	
	private boolean onTXEktSave(String FLG, TParm insParm) {
		
		if (EKTIO.getInstance().ektSwitch()) { // 医疗卡开关，记录在后台config文件中
			
			TParm orderParm = orderEKTParm(FLG);  // 准备送入医疗卡接口的数据
			
			if (null == insParm){
				
				orderParm.addData("AMT", TypeTool.getDouble(regPatAdmControl.getValue("FeeY")));
				orderParm.setData("SHOW_AMT", TypeTool.getDouble(regPatAdmControl.getValue("FeeY")));
				orderParm.setData("INS_FLG", "N");
				// 医保出现问题现金收取
				reg.setInsPatType(""); // 就诊医保类型 需要保存到REG_PATADM数据库表中1.城职普通 2.城职门特
				// 3.城居门特
				// 送医疗卡，返回医疗卡的回传值
				orderParm.setData("ektParm", regPatAdmControl.p3.getData());
				orderParm.setData("EXE_AMT", TypeTool.getDouble(regPatAdmControl.getValue("FeeY"))); // 医疗卡已经收费的数据
				orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
				
			}else{
				
				orderParm.addData("AMT", TypeTool.getDouble(regPatAdmControl.getValue("FeeY"))
						- insParm.getDouble("INS_SUMAMT")); // 医保卡自费部分金额
				orderParm.setData("INS_AMT", insParm.getDouble("INS_SUMAMT")); // 医保卡自费部分金额
				orderParm.setData("INS_FLG", "Y"); // 医保卡注记
				orderParm.setData("OPBEKTFEE_FLG", true);// 取消按钮
				orderParm.setData("RECP_TYPE", "REG"); // 添加EKT_ACCNTDETAIL 表数据使用
				orderParm.setData("comminuteFeeParm", insParm.getParm(
						"comminuteFeeParm").getData()); // 费用分割返回参数
				orderParm.setData("ektParm", regPatAdmControl.p3.getData());
				orderParm.setData("EXE_AMT", TypeTool.getDouble(regPatAdmControl.getValue("FeeY"))
						- insParm.getDouble("INS_SUMAMT")); // 此病患所有收费医嘱包括已经打票的
				orderParm.setData("SHOW_AMT", TypeTool.getDouble(regPatAdmControl.getValue("FeeY"))
						- insParm.getDouble("INS_SUMAMT")); // 显示金额
				orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
				
				
			}
				
			ektParam.setOrderParm(orderParm);
		
		} else {
			regPatAdmControl.messageBox_("医疗卡接口未开启");
			return false;
		}
		return true;
		
		
	}
	
	/**
	 * 医疗卡入参
	 * 
	 * @param FLG
	 *            String
	 * @return TParm
	 */
	private TParm orderEKTParm(String FLG) {
		TParm orderParm = new TParm();
		orderParm.addData("RX_NO", "REG"); // 写固定值
		orderParm.addData("ORDER_CODE", "REG"); // 写固定值
		orderParm.addData("SEQ_NO", "1"); // 写固定值
		orderParm.addData("EXEC_FLG", "N"); // 写固定值
		orderParm.addData("RECEIPT_FLG", "N"); // 写固定值
		orderParm.addData("BILL_FLG", FLG);
		orderParm.setData("MR_NO", pat.getMrNo());
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "男" : "女");
		orderParm.setData("BUSINESS_TYPE", "REG");
		return orderParm;
	}
	
	/**
	 * 执行保存 医保表数据操作
	 * 
	 * @param payWay
	 *            String
	 * @param ins_amt
	 *            double
	 * @param ins_exe
	 *            boolean
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	private TParm onSaveRegTwo(String payWay, boolean ins_exe, String caseNo) {
		double ins_amtTemp = 0.00;// 医保金额
		TParm result = new TParm();
		if (payWay.equals("PAY_INS_CARD")) {
			// 查询是否存在特批款操作
			if (null == caseNo || caseNo.length() <= 0) {
				caseNo = SystemTool.getInstance().getNo("ALL", "REG",
						"CASE_NO", "CASE_NO"); // 获得就诊号
			}
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			parm = PatAdmTool.getInstance().selEKTByMrNo(parm);
			if (parm.getErrCode() < 0) {
				regPatAdmControl.messageBox("E0005");
				return null;
			}

			if (parm.getDouble("GREEN_BALANCE", 0) > 0) {
				regPatAdmControl.messageBox("此就诊病患使用特批款,不可以使用医保操作");
				return null;
			}
			if (regPatAdmControl.getValue("REG_CTZ1").toString().length() <= 0) {
				regPatAdmControl.messageBox("请选择医保卡就诊类型");
				return null;
			}
			// 需要保存到REG_PATADM数据库表中1.城职普通
			// 2.城职门特 3.城居门特
			// 医保卡挂号
			// 获得挂号费用代码，费用金额，费用
			
			TParm mtParm=PatAdmTool.getInstance().getMTClinicFee(regPatAdmControl.insParm);

			
			//获得当前时间
			String sysdate =StringTool.getString(SystemTool.
					getInstance().getDate(),"yyyyMMddHHmmss");
			String regFeesql = "SELECT A.ORDER_CODE,B.ORDER_DESC,B.NHI_CODE_O, B.NHI_CODE_E, B.NHI_CODE_I,B.OWN_PRICE ,"
					+ "B.OWN_PRICE AS AR_AMT ,'1' AS DOSAGE_QTY, '0' AS TAKE_DAYS, '' AS NS_NOTE, '' AS SPECIFICATION,'' AS DR_CODE,A.RECEIPT_TYPE,"
					+ "C.DOSE_CODE FROM REG_CLINICTYPE_FEE A,SYS_FEE_HISTORY B,PHA_BASE C WHERE A.ORDER_CODE=B.ORDER_CODE(+) "
					+ "AND A.ORDER_CODE=C.ORDER_CODE(+) AND  A.ADM_TYPE='"
					+ regPatAdmControl.admType
					+ "'"
					+ " AND A.CLINICTYPE_CODE='"
					+ regPatAdmControl.getValue("CLINICTYPE_CODE") + "'" 
					+ " AND '" + sysdate+ "' BETWEEN B.START_DATE AND B.END_DATE";
			
			//add by huangtt 20170504 门特加10块钱
			if(mtParm.getValue("mrCliniFeeCode").trim().length() > 0){
				regFeesql += " UNION ALL "
						+ " SELECT B.ORDER_CODE,B.ORDER_DESC,B.NHI_CODE_O, "
						+ " B.NHI_CODE_E, B.NHI_CODE_I,B.OWN_PRICE ,"
						+ " B.OWN_PRICE AS AR_AMT ,'1' AS DOSAGE_QTY, "
						+ " '0' AS TAKE_DAYS, '' AS NS_NOTE, '' AS SPECIFICATION,'' AS DR_CODE,'' AS RECEIPT_TYPE,"
						+ " C.DOSE_CODE FROM SYS_FEE_HISTORY B,PHA_BASE C"
						+ " WHERE B.ORDER_CODE = '" + mtParm.getValue("mrCliniFeeCode") + "'"
						+ " AND B.ORDER_CODE=C.ORDER_CODE(+)	"
						+ " AND '" + sysdate+ "' BETWEEN B.START_DATE AND B.END_DATE";
			}

			// 挂号费
			double reg_fee = BIL.getRegDetialFee(regPatAdmControl.admType, TypeTool
					.getString(regPatAdmControl.getValue("CLINICTYPE_CODE")), "REG_FEE",
					TypeTool.getString(regPatAdmControl.getValue("REG_CTZ1")), TypeTool
							.getString(regPatAdmControl.getValue("REG_CTZ2")), TypeTool
							.getString(regPatAdmControl.getValue("CTZ3_CODE")), regPatAdmControl
							.getValueString("SERVICE_LEVEL") == null ? ""
							: regPatAdmControl.getValueString("SERVICE_LEVEL"));
			// 诊查费 计算折扣
			double clinic_fee = BIL.getRegDetialFee(regPatAdmControl.admType, TypeTool
					.getString(regPatAdmControl.getValue("CLINICTYPE_CODE")), "CLINIC_FEE",
					TypeTool.getString(regPatAdmControl.getValue("REG_CTZ1")), TypeTool
							.getString(regPatAdmControl.getValue("REG_CTZ2")), TypeTool
							.getString(regPatAdmControl.getValue("CTZ3_CODE")), 
							regPatAdmControl.getValueString("SERVICE_LEVEL") == null ? ""
							: regPatAdmControl.getValueString("SERVICE_LEVEL"));
			
			
			// System.out.println("regFeesql:::::" + regFeesql); 
			TParm regFeeParm = new TParm(TJDODBTool.getInstance().select(
					regFeesql));
			if (regFeeParm.getErrCode() < 0) {
				regPatAdmControl.err(regFeeParm.getErrCode() + " " + regFeeParm.getErrText());
				regPatAdmControl.messageBox("医保执行操作失败");
				return null;
			}
			for (int i = 0; i < regFeeParm.getCount(); i++) {
				if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("REG_FEE")) {
					regFeeParm.setData("RECEIPT_TYPE", i, reg_fee);
					regFeeParm.setData("AR_AMT", i, reg_fee);
				}
				if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("CLINIC_FEE")) {
					regFeeParm.setData("RECEIPT_TYPE", i, clinic_fee);
					regFeeParm.setData("AR_AMT", i, clinic_fee);
				}
				if(mtParm.getValue("mrCliniFeeCode").trim().length() > 0){ //modify by huangtt 20170504
					if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("")) {
						regFeeParm.setData("RECEIPT_TYPE", i, mtParm.getDouble("fee"));
						regFeeParm.setData("AR_AMT", i, mtParm.getDouble("fee"));
					}
				}
			}
			// System.out.println("regFeesql::" + regFeesql);
			result = TXsaveINSCard(regFeeParm, caseNo); // 执行操作
			// System.out.println("RESULT::::" + result);
			if (null == result)
				return null;
			if (result.getErrCode() < 0) {
				regPatAdmControl.err(result.getErrCode() + " " + result.getErrText());
				regPatAdmControl.messageBox("医保执行操作失败");
				return null;
			}
			// 24医保卡支付(REG_RECEIPT)
			if (null != result.getValue("MESSAGE_FLG")
					&& result.getValue("MESSAGE_FLG").equals("Y")) {
				System.out.println("医保卡出现错误现金收取");
			} else {
				// 医保支付
				ins_amtTemp = regPatAdmControl.tjInsPay(result, regFeeParm);
				ins_exe = true; // 医保执行操作 需要判断在途状态
				reg.setInsPatType(regPatAdmControl.insParm.getValue("INS_TYPE")); // 就诊医保类型
				reg.setConfirmNo(regPatAdmControl.insParm.getValue("CONFIRM_NO")); // 医保就诊号
				// CONFIRM_NO
			}

		}
		result.setData("INS_AMT", ins_amtTemp);
		result.setData("INS_EXE", ins_exe);

		return result;
	}
	
	/**
	 * 泰心医院医保卡保存操作
	 * 
	 * @param parm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	private TParm TXsaveINSCard(TParm parm, String caseNo) {
		// 没有获得医疗卡信息 判断是否执行现金收费
		if (!regPatAdmControl.tjINS && !regPatAdmControl.insFlg) {
			if (regPatAdmControl.messageBox("提示", "没有获得医疗卡信息,执行现金收费是否继续", 2) != 0) {
				return null;
			}
		}
		if (regPatAdmControl.tjINS) { // 医疗卡操作
			if (regPatAdmControl.p3.getDouble("CURRENT_BALANCE") < regPatAdmControl.getValueDouble("FeeY")) {
				regPatAdmControl.messageBox("医疗卡金额不足,请充值");
				return null;
			}
		}
		TParm result = new TParm();
		regPatAdmControl.insParm.setData("REG_PARM", parm.getData()); // 医嘱信息
		regPatAdmControl.insParm.setData("DEPT_CODE", regPatAdmControl.getValue("DEPT_CODE")); // 科室代码
		regPatAdmControl.insParm.setData("MR_NO", pat.getMrNo()); // 病患号

		reg.setCaseNo(caseNo);
		regPatAdmControl.insParm.setData("RECP_TYPE", "REG"); // 类型：REG / OPB
		regPatAdmControl.insParm.setData("CASE_NO", reg.caseNo());
		regPatAdmControl.insParm.setData("REG_TYPE", "1"); // 挂号标志:1 挂号0 非挂号
		regPatAdmControl.insParm.setData("OPT_USER", Operator.getID());
		regPatAdmControl.insParm.setData("OPT_TERM", Operator.getIP());
		regPatAdmControl.insParm.setData("DR_CODE", regPatAdmControl.getValue("DR_CODE"));// 医生代码
		// insParm.setData("PAY_KIND", "11");// 4 支付类别:11门诊、药店21住院//支付类别12、
		if (regPatAdmControl.getValueString("ERD_LEVEL").length() > 0) {
			regPatAdmControl.insParm.setData("EREG_FLG", "1"); // 急诊
		} else {
			regPatAdmControl.insParm.setData("EREG_FLG", "0"); // 普通
		}

		regPatAdmControl.insParm.setData("PRINT_NO", regPatAdmControl.getValue("NEXT_NO")); // 票号
		regPatAdmControl.insParm.setData("QUE_NO", reg.getQueNo());

		TParm returnParm = regPatAdmControl.insExeFee(true);
		if (null == returnParm || null == returnParm.getValue("RETURN_TYPE")) {
			return null;
		}
		int returnType = returnParm.getInt("RETURN_TYPE"); // 0.失败 1. 成功
		if (returnType == 0 || returnType == -1) { // 取消操作
			return null;
		}

		regPatAdmControl.insParm.setData("comminuteFeeParm", returnParm.getParm(
				"comminuteFeeParm").getData()); // 费用分割数据
		regPatAdmControl.insParm.setData("settlementDetailsParm", returnParm.getParm(
				"settlementDetailsParm").getData()); // 费用结算

		// System.out.println("insParm:::::::"+insParm);
		result = INSTJReg.getInstance().insCommFunction(regPatAdmControl.insParm.getData());

		if (result.getErrCode() < 0) {
			regPatAdmControl.err(result.getErrCode() + " " + result.getErrText());
			// this.messageBox("医保执行操作失败");
			return result;
		}
		// System.out.println("医保操作出参:" + insParm);

		result.setData("INS_SUMAMT", returnParm.getDouble("ACCOUNT_AMT")); // 医保金额
		result.setData("ACCOUNT_AMT_FORREG", returnParm
				.getDouble("ACCOUNT_AMT_FORREG")); // 账户金额
		regPatAdmControl.insParm.setData("INS_SUMAMT", returnParm.getDouble("ACCOUNT_AMT")); // 医保金额
		if (regPatAdmControl.tjINS) { // 医疗卡操作
			// 执行医疗卡扣款操作：需要判断医保金额与医疗卡金额
			if (!onTXEktSave("Y", result)) {
//				result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
//						"deleteOldData", regPatAdmControl.insParm);
//				if (result.getErrCode() < 0) {
//					regPatAdmControl.err(result.getErrCode() + " " + result.getErrText());
//					result.setErr(-1, "医保卡执行操作失败");
//					// return result;
//				}
//				result.setErr(-1, "医疗卡执行操作失败");
				return null;
			}

		}
		return result;
	}
	
	
	

}
