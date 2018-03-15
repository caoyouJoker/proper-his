package jdo.ins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import jdo.opd.DiagRecTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 挂号医保管理
 * </p>
 * 
 * <p>
 * Description: 挂号医保管理
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
 * @author pangben 2011-11-07
 * @version 1.0
 */
public class INSTJFlow extends TJDOTool {
	// private TParm ruleParm;// 三目字典
	DateFormat df = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
	DateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	private String message = "现金支付";
	/**
	 * 实例
	 */
	public static INSTJFlow instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INSTJFlow
	 */
	public static INSTJFlow getInstance() {
		if (instanceObject == null)
			instanceObject = new INSTJFlow();
		return instanceObject;
	}

	// 校验是否为空
	String[] insMZConfirm = { "INSCARD_NO", "INSCARD_PASSWORD", "OWN_NO",
			"BANK_NO", "IDNO", "PAT_NAME", "SEX_CODE", "PAT_AGE", "CTZ_CODE",
			"UNIT_NO", "UNIT_CODE", "UNIT_DESC", "INS_CODE", "TOT_AMT",
			"OTOT_AMT", "SPC_MEMO", "PAY_KIND", "DISEASE_CODE",
			"INS_CROWD_TYPE", "INS_PAT_TYPE", "MED_SALVA_FLG",
			"INS_SURPLUS_AMT", "SALVA_SURPLUS_AMT", " INS_PAY_LEVEL",
			"REAL_PAY_LEVEL", "OWEN_PAY_SCALE", "REDUCE_PAY_SCALE",
			"REAL_OWEN_PAY_SCALE", "SALVA_PAY_SCALE", "MED_HELP_COMPANY",
			"ENTERPRISES_TYPE", "SPECIAL_PAT", "OWN_PAY_RATE",
			"PERSON_ACCOUNT_AMT", "SP_PRESON_MEMO", "AUTO_FLG" };
	String[] insOpdOrder = { "SEQ_NO", "CHARGE_DATE", "NHI_ORDER_CODE",
			"OWN_RATE", "DOSE_CODE", "STANDARD", "PRICE", "QTY", "TOTAL_AMT",
			"TOTAL_NHI_AMT", "OWN_AMT", "ADDPAY_AMT", "REFUSE_AMT",
			"REFUSE_REASON_CODE", "REFUSE_REASON_NOTE", "OP_FLG", "CARRY_FLG",
			"PHAADD_FLG", "ADDPAY_FLG", "NHI_ORD_CLASS_CODE", "INSAMT_FLG",
			"RX_SEQNO", "OPB_SEQNO", "TAKE_QTY", "ROUTE", "HYGIENE_TRADE_CODE",
			"INS_CROWD_TYPE", "INS_PAT_TYPE", "ORIGSEQ_NO", "TAKE_DAYS",
			"INV_NO", "RECP_TYPE", "SPECIAL_CASE_DESC" };

	public INSTJFlow() {
		super();
		this.onInit();
		// String sql = "SELECT * FROM INS_RULE";
		// ruleParm = new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 出现在途状态 执行自动对账操作
	 */
	private TParm accountComm(TParm parm) {
		// 需要自动对账的数据
		TParm insOpdParm = INSOpdTJTool.getInstance().selectAutoAccount(parm);
		if (insOpdParm.getErrCode() < 0) {
			// exeProgress=true;
			return insOpdParm;
		}
		if (insOpdParm.getCount() <= 0) {
			// exeProgress=true;
			return insOpdParm;
		}
		insOpdParm.setData("INS_TYPE", parm.getValue("INS_TYPE"));// 1.城职普通
		// 2.城职门特
		// 3.城居门特
		insOpdParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		insOpdParm.setData("ACCOUNT_DATE", df1.format(SystemTool.getInstance()
				.getDate()));// 对账时间
		return new TParm(autoAccountComm(insOpdParm));
	}

	/**
	 * 费用分割费用分割、添加ins_opd_order 表、明细上传操作 执行费用分割 城职：函数：DataDown_sp1 方法 B
	 * 城居：函数：DataDown_sp1 方法 G 执行上传明细 城职普通：函数: 函数DataUpload,（B）方法
	 * 城职门特：函数DataUpload, 方法 C 城居门特：函数DataDown_cmts, 方法 F 城职 城居 费用结算 费用结算
	 * 结算、账户确认操作
	 * 
	 * @param connection
	 * @return
	 */
	public TParm comminuteFeeAndInsOrder(TParm parm) {
		// TParm ruleParm = parm.getParm("ruleParm");
		// 判断是否存在在途数据
		TParm result = new TParm();
		if (!INSTJFlow.getInstance().queryRun(parm)) {
			if(null!=parm.getValue("OPB_RECP_TYPE") && parm.getValue("OPB_RECP_TYPE").equals("Y")){
				//门诊收费在途状态，不可以执行后面的接口调用，避免出现重复收费的逻辑
				result.setData("MESSAGE", "存在在途信息,请执行自动对账操作");
				result.setData("NO_EXE_FLG","Y");
				return result;
			}
			// 执行自动对账操作
			result = accountComm(parm);
			// System.out.println("执行自动对账");
			// System.out.println(result.getErrCode()+":"+result.getErrText());
			TParm runParm = new TParm();
			runParm.setData("CASE_NO", parm.getValue("CASE_NO"));
			runParm.setData("EXE_USER", parm.getValue("OPT_USER"));
			runParm.setData("EXE_TERM", parm.getValue("OPT_TERM"));
			runParm.setData("EXE_TYPE", parm.getValue("RECP_TYPE"));
			result = INSRunTool.getInstance().deleteInsRun(runParm);// 删除在途数据
			if (result.getErrCode() < 0) {
				err(result.getErrText());
				return result;
			}
		}
		// 添加在途数据
		parm.setData("EXE_TYPE", parm.getValue("RECP_TYPE"));// 类别
		if (!INSTJFlow.getInstance().insertRun(parm, "test")) {
			err(result.getErrText());
			return result;
		}
		int insReadType = parm.getInt("CROWD_TYPE");// 医保读卡类型
		TParm opbReadCardParm = parm.getParm("opbReadCardParm");// L方法出参
		// 添加 或修改 INS_MZ_COMFIRM 表数据
		result = INSTJFlow.getInstance().onSaveInsMZConfirm(opbReadCardParm,
				parm, parm.getInt("INS_TYPE"));
		if (result.getErrCode() < 0) {
			err(result.getErrText());
			return result;
		}
		int type = parm.getInt("INS_TYPE");// 1 .城职普通 2. 城职门特 3. 城居门特
		//System.out.println("-----------readINSParm---------" + parm);
		// System.out.println("type == :::"+type );// 城职普通需要校验
//		if (type == 1) {
//			if (opbReadCardParm.getDouble("TOT_AMT") <= 0
//					&& opbReadCardParm.getDouble("OTOT_AMT") <= 0) {
//				result.setData("MESSAGE", "专项基金剩余额为0,现金支付");
//				return result;
//			}
//		} else 
			if (type == 2 || type == 3) {// 城职、城居门特校验
			  if (opbReadCardParm.getValue("MED_SALVA_FLG").equals("0")) {
				System.out.println("不享受医疗救助");
				// result.setData("MESSAGE", "不享受医疗救助,自费支付");
				// return result;
			}
		}

		// 费用分割
		TParm comminuteFeeParm = INSTJTool.getInstance()
				.comminuteFeeAndInsOrder(parm, opbReadCardParm, insReadType,
						type);
		if (comminuteFeeParm.getErrCode() < 0) {
			err(comminuteFeeParm.getErrCode() + " "
					+ comminuteFeeParm.getErrText());
			// connection.close();
			return comminuteFeeParm;
		}
		// double addAmt = 0.00;
		// for (int i = 0; i < comminuteFeeParm.getCount("ADD_AMT"); i++) {
		// if (null != comminuteFeeParm.getValue("ADDPAY_FLG", i)
		// && comminuteFeeParm.getInt("ADDPAY_FLG", i) == 1) {
		// addAmt += comminuteFeeParm.getDouble("ADD_AMT", i);//
		// 累计增负金额存在值,添加累计增负金额
		// }
		//
		// }
		// System.out.println("累计增负金额：：：：" + addAmt);
		TParm spParm = new TParm();
		if (parm.getValue("RECP_TYPE").equals("OPB")) {
			// 累计增负操作
			spParm = addExe(parm, comminuteFeeParm);
			if (spParm.getErrCode() < 0) {
				return spParm;

			}

		}

		// 费用上传明细
		spParm = detailUpLoad(parm, comminuteFeeParm, type);
		if (spParm.getErrCode() < 0) {
			return spParm;
		}
		TParm settlementDetailsParm = new TParm();
		//TParm REG_PARM = parm.getParm("REG_PARM");// 获得挂号门诊费用相关数据
		// 获得最大用药天数
		int day = 0;
		String sqlMax = "SELECT MAX(TAKE_DAYS) AS TAKE_DAYS FROM OPD_ORDER WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND RECEIPT_NO IS NULL";
		TParm maxParm = new TParm(TJDODBTool.getInstance().select(sqlMax));
		if(maxParm.getErrCode()<0){
			err(maxParm.getErrText());
			return maxParm;
		}
		if(null!=maxParm.getValue("TAKE_DAYS",0)){
			day=maxParm.getInt("TAKE_DAYS",0);
		}
		parm.setData("TAKE_DAYS", day);
		// 获得医保数据 诊断 医生代码 、科别代码
		TParm diagRecparm = DiagRecTool.getInstance().queryInsData(parm);
		String sql =" SELECT A.INS_DEPT_CODE,B.DR_QUALIFY_CODE " +
				    " FROM SYS_DEPT A ,SYS_OPERATOR B ,REG_SCHDAY C " +
		            " WHERE A.DEPT_CODE=C.REALDEPT_CODE " +
		            " AND B.USER_ID = C.REALDR_CODE " +
		            " AND B.USER_ID='"+ parm.getValue("DR_CODE") + "'" + 
		            " AND C.ADM_DATE = '"+ parm.getValue("ADM_DATE") + "'";
		TParm deptParm = new TParm(TJDODBTool.getInstance().select(sql));
		parm.setData("diagRecparm", diagRecparm.getData());
		parm.setData("deptParm", deptParm.getRow(0).getData());
		// System.out.println("获得医保数据 诊断 医生代码 、科别代码deptParm::::::::"+deptParm);
		switch (type) {
		case 1:
			// 费用结算-------获得出参 执行医嘱金额分割操作
			// 函数DataDown_sp, 门诊结算上传交易（M）方法
			settlementDetailsParm = insFeeSettleChZPt(parm);// 费用结算出参
			if (settlementDetailsParm.getErrCode() < 0) {
				err(settlementDetailsParm.getErrCode() + " "
						+ settlementDetailsParm.getErrText());
				return settlementDetailsParm;
			}
			// 帐户支付确认交易返回参数
			// 函数DataDown_rs参数R方法
			parm.setData("settlementDetailsParm", settlementDetailsParm
					.getData());
			result = INSTJFlow.getInstance().insPayAccountChZPt(parm);
			if (result.getErrCode() < 0) {
				err(result.getErrText());
				return result;
			}
			break;
		case 2:
			// 费用结算 函数DataDown_mts, 门诊结算上传交易（F）方法

			settlementDetailsParm = insFeeSettleChZMtAndChJMt(parm);
			if (settlementDetailsParm.getErrCode() < 0) {
				err(settlementDetailsParm.getErrCode() + " "
						+ settlementDetailsParm.getErrText());
				return settlementDetailsParm;
			}
			// 统筹支付确认交易:函数DataDown_mts, 门特统筹支付确认交易（G）
			parm.setData("settlementDetailsParm", settlementDetailsParm
					.getData());
			result = INSTJFlow.getInstance().insPayAccountChZMt(parm);
			if (result.getErrCode() < 0) {
				err(result.getErrText());
				return result;
			}
			break;
		case 3:
			// 费用结算 函数DataDown_cmts, 门诊结算上传交易（F）方法
			settlementDetailsParm = insFeeSettleChZMtAndChJMt(parm);
			if (settlementDetailsParm.getErrCode() < 0) {
				err(settlementDetailsParm.getErrCode() + " "
						+ settlementDetailsParm.getErrText());
				return settlementDetailsParm;
			}
			// 统筹支付确认交易:函数DataDown_cmts, 门特统筹支付确认交易（G）
			parm.setData("settlementDetailsParm", settlementDetailsParm
					.getData());
			result = INSTJFlow.getInstance().insPayAccountChJMt(parm);
			if (result.getErrCode() < 0) {
				err(result.getErrText());
				return result;
			}
			break;
		}

		// 修改INS_OPD 表 数据
		if (!updateINSopdSettle(parm)) {
			result.setErr(-1, "执行修改INS_OPD表数据错误");
			// cancelBalance(parm);
			return result;
		}
		// System.out.println("帐户支付确认交易返回参数");
		result.setData("comminuteFeeParm", comminuteFeeParm.getData());// 费用分割数据
		result
				.setData("settlementDetailsParm", settlementDetailsParm
						.getData());// 费用结算

		return result;
	}

	/**
	 * 累计增负执行操作
	 * 
	 * @return
	 */
	private TParm addExe(TParm parm, TParm comminuteFeeParm) {
		TParm spParm = new TParm();
		// 累计增负查询所有金额
		TParm result = new TParm();
		TParm addParm = INSOpdOrderTJTool.getInstance().queryAddInsOpdOrder(
				parm);
		TParm seqParm = INSOpdOrderTJTool.getInstance().selectMAXSeqNo(parm);
		if (addParm.getErrCode() < 0) {
			return addParm;
		}
		if (addParm.getDouble("TOTAL_AMT", 0) <= 0) {
			return new TParm();
		}
		spParm.setData("ADDPAY_ADD", addParm.getDouble("TOTAL_AMT", 0));// 累计增负发生总额
		spParm.setData("HOSP_START_DATE", df1.format(SystemTool.getInstance()
				.getDate()));
		int type = parm.getInt("CROWD_TYPE");// 医保读卡类型
		if (type == 1)// 城职
			result = INSTJTool.getInstance().DataDown_sp1_C(spParm);
		else {
			result = INSTJTool.getInstance().DataDown_sp1_H(spParm);
		}
		// 添加出错信息
		if (!INSTJTool.getInstance().getErrParm(result)) {
			// result.setData("MESSAGE", "现金支付");
			return result;
		}
		// 添加累计增负
		spParm = INSTJTool.getInstance().exeAdd(parm, addParm, result, seqParm);
		if (spParm.getErrCode() < 0) {
			return spParm;
		}
		parm.setData("TOT_AMT", addParm.getDouble("TOTAL_AMT", 0));
		return spParm;
	}

	/**
	 * 明细上传
	 * 
	 * @param parm
	 * @return
	 */
	private TParm detailUpLoad(TParm parm, TParm comminuteFeeParm,
			int insOrderType) {
		TParm opdOrderParm = new TParm();
		opdOrderParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		opdOrderParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));
		String nhiRegionCode = parm.getValue("REGION_CODE");
		opdOrderParm = INSOpdOrderTJTool.getInstance().selectOpdOrder(
				opdOrderParm);// 费用分割后查询，执行上传操作
		if (opdOrderParm.getErrCode() < 0) {
			return opdOrderParm;
		}
		TParm result = new TParm();
		TParm interFaceParm = new TParm();// 接口回参
		TParm tempParm = null;// 中间数据
		TParm sumUpLoadParm = new TParm();// 上传汇总
		boolean flg = true;
		for (int i = 0; i < opdOrderParm.getCount(); i++) {
			tempParm = opdOrderParm.getRow(i);
			// System.out.println("上传明细:::"+tempParm);
			flg = INSTJTool.getInstance().upInterfaceINSOrderParm(tempParm,
					sumUpLoadParm, nhiRegionCode);
			// 添加出错信息
			if (!flg) {
				result.setErr(-1, "明细上传接口调用出现错误");
				// result.setData("MESSAGE", "现金支付");
				return result;
			}

		}
		// System.out.println("insOrderType::::"+insOrderType);
		switch (insOrderType) {
		case 1:// 城职普通
			interFaceParm = INSTJTool.getInstance().DataUpload_B(sumUpLoadParm);
			break;
		case 2:// 城职门特
			interFaceParm = INSTJTool.getInstance().DataUpload_C(sumUpLoadParm);
			break;
		case 3:// 城居门特
			// System.out.println("城居门特进入---------------------");
			interFaceParm = INSTJTool.getInstance().DataUpload_F(sumUpLoadParm);
			break;
		}
		if (!INSTJTool.getInstance().getErrParm(interFaceParm)) {
			// result.setData("MESSAGE", "现金支付");
			return interFaceParm;
		}
		// 删除主档数据
		result = INSOpdTJTool.getInstance().deleteINSOpd(parm);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 插入INS_opd(comminuteFeeParm)
		if (!INSTJTool.getInstance().insterInsOpdParm(comminuteFeeParm, parm)) {
			result.setErr(-1, "费用分割接口调用出现错误");
			return result;
		}
		return result;
	}

	/**
	 * 城职普通 费用结算 函数DataDown_sp, 门诊结算上传交易（M）方法
	 */
	public TParm insFeeSettleChZPt(TParm parm) {
		TParm settlementDetailsParm = INSTJTool.getInstance().DataDown_sp_M(
				parm, parm.getValue("REG_TYPE"));
		if (!INSTJTool.getInstance().getErrParm(settlementDetailsParm)) {
			// 添加结算接口失败实现结算取消交易 ----只有收费没有退费
			cancelBalance(parm);
			return settlementDetailsParm;
		}
		return settlementDetailsParm;
	}

	/**
	 * 城职\城居门特 费用结算 函数DataDown_mts, 门诊结算上传交易（F）方法
	 */
	public TParm insFeeSettleChZMtAndChJMt(TParm parm) {
		TParm identificationParm = parm.getParm("opbReadCardParm");// DataDown_mts_E
		// AND
		// DataDown_cmts_E
		// 方法出参
		TParm settlementDetailsParm = null;
		// System.out.println("方法入参:::"+parm);
		if (parm.getInt("CROWD_TYPE") == 1) {

			settlementDetailsParm = INSTJTool.getInstance().DataDown_mts_F(
					parm, identificationParm, parm.getValue("REG_TYPE"));
		} else if (parm.getInt("CROWD_TYPE") == 2) {
			settlementDetailsParm = INSTJTool.getInstance().DataDown_cmts_F(
					parm, identificationParm, parm.getValue("REG_TYPE"));
		}

		if (!INSTJTool.getInstance().getErrParm(settlementDetailsParm)) {
			// 添加结算接口失败实现结算取消交易 ----只有收费没有退费
			cancelBalance(parm);
			return settlementDetailsParm;
		}

		return settlementDetailsParm;
	}

	/**
	 * 账户支付 函数DataDown_rs参数R方法
	 * 
	 * @return
	 */
	public TParm insPayAccountChZPt(TParm parm) {
		TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// 费用结算出参
		TParm accountPayParm = INSTJTool.getInstance().DataDown_rs_R(parm,
				settlementDetailsParm);
		if (!INSTJTool.getInstance().getErrParm(accountPayParm)) {
			cancelBalance(parm);
			return accountPayParm;
		}
		// 账户支付确认后,修改对账标志 设置1
		if (accountPayParm.getInt("INSAMT_FLG") == 1) {
			// 修改INS_OPD_ORDER表中INSAMT_FLG对账标志为1
			if (!updateInsAmtFlg(parm, "1", parm.getValue("RECP_TYPE"))) {
				cancelBalance(parm);
				accountPayParm.setErr(-1, "帐户支付确认交易接口调用出现错误");
				return accountPayParm;
			}
		}
		return accountPayParm;
	}

	/**
	 * 修改INS_OPD 表 数据
	 * 
	 * @param connection
	 * @return
	 */
	public boolean updateINSopdSettle(TParm parm) {
		// INS_OPD 结算表添加数据
		// System.out.println("------readINSParm---------" + parm);
		TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// 费用结算出参
		// System.out.println("------interFaceParm---------"
		// + settlementDetailsParm);
		TParm result = INSTJTool.getInstance().balanceParm(parm,
				settlementDetailsParm); // 添加结算档数据
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			cancelBalance(parm);// 结算取消
			return false;
		}
		return true;
	}

	/**
	 * 城职门特 统筹支付确认交易：函数DataDown_mts, 门特统筹支付确认交易（G）
	 * 
	 * @return
	 */
	public TParm insPayAccountChZMt(TParm parm) {
		TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// 费用结算出参
		TParm overallPayParm = INSTJTool.getInstance().DataDown_mts_G(parm,
				settlementDetailsParm);
		if (!INSTJTool.getInstance().getErrParm(overallPayParm)) {
			// 添加结算接口失败实现结算取消交易 ----只有收费没有退费
			// System.out.println("11111111111111111111111111111:::统筹支付确认交易");
			cancelBalance(parm);
			overallPayParm.setData("MESSAGE", "现金支付");
			return overallPayParm;
		}
		// 统筹支付确认交易后修改INS_OPD 和 INS_OPD_ORDER表中INSAMT_FLG对账标志为1
		if (overallPayParm.getInt("SOCIAL_FLG") == 1) {
			// 修改INS_OPD 表 数据
			// 修改INS_OPD_ORDER表中INSAMT_FLG对账标志为1
			if (!updateInsAmtFlg(parm, "1", parm.getValue("RECP_TYPE"))) {
				cancelBalance(parm);
				overallPayParm.setData("MESSAGE", "现金支付");
				overallPayParm.setErr(-1, "统筹支付确认交易接口调用出现错误");
				return overallPayParm;
			}
		}
		return overallPayParm;
	}

	/**
	 * 城居门特 统筹支付确认交易:函数DataDown_cmts, 门特统筹支付确认交易（G）
	 * 
	 * @param connection
	 * @return
	 */
	public TParm insPayAccountChJMt(TParm parm) {
		TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// 费用结算出参
		TParm overallPayParm = INSTJTool.getInstance().DataDown_cmts_G(parm,
				settlementDetailsParm);
		if (!INSTJTool.getInstance().getErrParm(overallPayParm)) {
			// 添加结算接口失败实现结算取消交易 ----只有收费没有退费
			cancelBalance(parm);
			overallPayParm.setData("MESSAGE", "现金支付");
			return overallPayParm;
		}
		if (overallPayParm.getInt("SOCIAL_FLG") == 1) {
			// 修改INS_OPD 表 数据
			// 修改INS_OPD 表中INSAMT_FLG对账标志为1
			if (!updateInsAmtFlg(parm, "1", parm.getValue("RECP_TYPE"))) {
				cancelBalance(parm);
				overallPayParm.setData("MESSAGE", "现金支付");
				overallPayParm.setErr(-1, "统筹支付确认交易接口调用出现错误");
				return overallPayParm;
			}
		}
		return overallPayParm;
	}

	/**
	 * 结算取消
	 * 
	 * @return
	 */
	public TParm cancelBalance(TParm parm) {
		// 添加结算接口失败实现结算取消交易
		TParm opbReadCardParm = parm.getParm("opbReadCardParm");// DataDown_sp_L\DataDown_mts_E\
		// DataDown_cmts_E
		TParm result1 = null;
		switch (parm.getInt("INS_TYPE")) {
		case 1:
			result1 = INSTJTool.getInstance().DataDown_sp_S(
					opbReadCardParm.getValue("CONFIRM_NO"),
					parm.getValue("REGION_CODE"), "30");// 取消
			break;
		case 2:
			result1 = INSTJTool.getInstance().DataDown_mts_J(
					opbReadCardParm.getValue("CONFIRM_NO"),
					parm.getValue("REGION_CODE"), "30");// 取消
			break;
		case 3:
			result1 = INSTJTool.getInstance().DataDown_cmts_J(
					opbReadCardParm.getValue("CONFIRM_NO"),
					parm.getValue("REGION_CODE"), "30");// 取消
			break;
		}
		// System.out.println(" 结算取消result1::::"+result1);
		if (!INSTJTool.getInstance().getErrParm(result1)) {
			return result1;
		}
		return result1;
	}

	/**
	 * 结算取消
	 * 
	 * @return
	 */
	public TParm cancelBalance(TParm parm, int type) {
		// 添加结算接口失败实现结算取消交易
		TParm opbReadCardParm = parm.getParm("opbReadCardParm");// DataDown_sp_L\DataDown_mts_E\
		// DataDown_cmts_E
		TParm result1 = null;
		switch (type) {
		case 1:
			result1 = INSTJTool.getInstance().DataDown_sp_S(
					opbReadCardParm.getValue("CONFIRM_NO"),
					parm.getValue("REGION_CODE"), "30");// 取消
			break;
		case 2:
			result1 = INSTJTool.getInstance().DataDown_mts_J(
					opbReadCardParm.getValue("CONFIRM_NO"),
					parm.getValue("REGION_CODE"), "30");// 取消
			break;
		case 3:
			result1 = INSTJTool.getInstance().DataDown_cmts_J(
					opbReadCardParm.getValue("CONFIRM_NO"),
					parm.getValue("REGION_CODE"), "30");// 取消
			break;
		}
		if (!INSTJTool.getInstance().getErrParm(result1)) {
			return result1;
		}
		// 执行删除INS_OPD \INS_OPD_ORDER\INS_RUN操作
		result1 = delInsOpdAndOpdOrder(parm);
		return result1;
	}
	/**
	 * 医保表中状态INSAMT_FLG=1的数据 存在票据的数据，执行确认接口
	 * @param parm
	 * @param type
	 * @return
	 * =============pangben 2013-7-29
	 */
	public TParm confirmBalance(TParm parm,TParm mzConfirmParm,int type){
		// 对已出票的正常消费，出票的时候对账标志至少是1 建议为3时出票
		// 程序中现在执行打票的条件是 对账标志是 3时才可以打票
		// 出现 对账为1 执行结算交易接口
		TParm interFaceParmOne=new TParm();
		if (parm.getInt("INSAMT_FLG") == 1) {
			// 结算交易
			switch (type) {
			case 1:
				interFaceParmOne = INSTJTool.getInstance().DataDown_sp_R(parm);
				break;
			case 2:
				interFaceParmOne = INSTJTool.getInstance().DataDown_mts_I(parm,
						mzConfirmParm);
				break;
			case 3:
				interFaceParmOne = INSTJTool.getInstance().DataDown_cmts_I(
						parm, mzConfirmParm);
				break;
			}
			if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
				return interFaceParmOne;
			} else {
				if (!updateInsAmtFlg(parm, "3", parm.getValue("RECP_TYPE"))) {
					cancelBalance(parm);
					interFaceParmOne.setErr(-1, "再次调用结算确认交易失败");
					return interFaceParmOne;
				}
				//======pangben 2013-3-13 添加在途删除
				TParm result =INSRunTool.getInstance().deleteInsRunConcel(parm);//取消操作删除在途状态
				if (result.getErrCode() < 0) {
					// System.out.println("err:" + parm.getErrText());
					return result;
				}
			}
		}
		return interFaceParmOne;
	}
	/**
	 * 城职普通 结算确认返回参数:函数DataDown_sp，门诊结算确认交易（R）方法
	 * 
	 * @return
	 */
	public TParm insSettleConfirmChZPt(TParm parm) {
		TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// 费用结算出参
		TParm interFaceParmOne = INSTJTool.getInstance().DataDown_sp_R(parm,
				settlementDetailsParm);
		// IF失败，再次调用结算确认交易，如果继续失败，请查找原因或与社保协商解决
		if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
			interFaceParmOne = INSTJTool.getInstance().DataDown_sp_R(parm,
					settlementDetailsParm);
			if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
				interFaceParmOne.setErr(-1, "再次调用结算确认交易失败");
				// 添加结算接口失败实现结算取消交易 ----只有收费没有退费
				cancelBalance(parm);
				interFaceParmOne.setData("MESSAGE", "调用结算确认交易失败,请查找原因或与社保协商解决");
				interFaceParmOne.setData("FLG", "Y");// 结算确认失败 不可以执行下面的程序
				return interFaceParmOne;
			}
		}
		if (interFaceParmOne.getInt("INSAMT_FLG") == 3) {
			// 结算确认后修改INS_OPD 和 INS_OPD_ORDER表中INSAMT_FLG对账标志为3
			if (!updateInsAmtFlg(parm, "3", parm.getValue("RECP_TYPE"))) {
				cancelBalance(parm);
				interFaceParmOne.setErr(-1, "再次调用结算确认交易失败");
				return interFaceParmOne;
			}
		}
		return interFaceParmOne;
	}

	/**
	 * 城职门特 结算确认返回参数:函数DataDown_mts，门诊结算确认交易（I）方法
	 * 
	 * @return
	 */
	public TParm insSettleConfirmChZMt(TParm parm) {
		TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// 费用结算出参
		TParm identificationParm = parm.getParm("opbReadCardParm");// 刷卡出参
		TParm interFaceParmOne = INSTJTool.getInstance().DataDown_mts_I(parm,
				settlementDetailsParm, identificationParm);
		// IF失败，再次调用结算确认交易，如果继续失败，请查找原因或与社保协商解决
		if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
			interFaceParmOne = INSTJTool.getInstance().DataDown_mts_I(parm,
					settlementDetailsParm, identificationParm);
			if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
				interFaceParmOne.setErr(-1, "再次调用结算确认交易失败");
				interFaceParmOne.setData("MESSAGE", "调用结算确认交易失败,请查找原因或与社保协商解决");
				interFaceParmOne.setData("FLG", "Y");// 结算确认失败 不可以执行下面的程序
				// 添加结算接口失败实现结算取消交易 ----只有收费没有退费
				cancelBalance(parm);
				return interFaceParmOne;
			}
		}
		// 结算确认后,修改对账标志 设置3
		if (interFaceParmOne.getInt("SOCIAL_FLG") == 3) {
			// 结算确认后修改INS_OPD 和 INS_OPD_ORDER表中INSAMT_FLG对账标志为3
			if (!updateInsAmtFlg(parm, "3", parm.getValue("RECP_TYPE"))) {
				cancelBalance(parm);
				interFaceParmOne.setErr(-1, "再次调用结算确认交易失败");
				interFaceParmOne.setData("FLG", "Y");// 结算确认失败 不可以执行下面的程序
				return interFaceParmOne;
			}
		}
		return interFaceParmOne;
	}

	/**
	 * 城居门特 结算确认返回参数:函数DataDown_cmts，门诊结算确认交易（I）方法
	 * 
	 * @return
	 */
	public TParm insSettleConfirmChJMt(TParm parm) {
		TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// 费用结算出参
		TParm identificationParm = parm.getParm("opbReadCardParm");// 刷卡出参
		TParm interFaceParmOne = INSTJTool.getInstance().DataDown_cmts_I(parm,
				settlementDetailsParm, identificationParm);
		// IF失败，再次调用结算确认交易，如果继续失败，请查找原因或与社保协商解决
		if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
			interFaceParmOne = INSTJTool.getInstance().DataDown_cmts_I(parm,
					settlementDetailsParm, identificationParm);
			if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
				interFaceParmOne.setErr(-1, "再次调用结算确认交易失败");
				interFaceParmOne.setData("MESSAGE", "调用结算确认交易失败,请查找原因或与社保协商解决");
				interFaceParmOne.setData("FLG", "Y");// 结算确认失败 不可以执行下面的程序
				// 添加结算接口失败实现结算取消交易 ----只有收费没有退费
				cancelBalance(parm);
				return interFaceParmOne;
			}
		}
		// 结算确认后修改INS_OPD 表中INSAMT_FLG对账标志为3
		if (interFaceParmOne.getInt("SOCIAL_FLG") == 3) {
			// 结算确认后修改INS_OPD 和 INS_OPD_ORDER表中INSAMT_FLG对账标志为3
			if (!updateInsAmtFlg(parm, "3", parm.getValue("RECP_TYPE"))) {
				cancelBalance(parm);
				interFaceParmOne.setErr(-1, "再次调用结算确认交易失败");
				interFaceParmOne.setData("MESSAGE", "调用结算确认交易失败,请查找原因或与社保协商解决");
				interFaceParmOne.setData("FLG", "Y");// 结算确认失败 不可以执行下面的程序
				return interFaceParmOne;
			}
		}
		return interFaceParmOne;
	}

	/**
	 * 修改INS_OPD 表数据
	 */
	private boolean updateInsOpds(TParm parm, String status, String type) {
		TParm result = updateInsOpd(parm, status, type);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			// 添加结算接口失败实现结算取消交易
			cancelBalance(parm);
			return false;
		}
		return true;
	}

	/**
	 * 修改INS_OPD 表数据
	 */
	public TParm updateInsOpd(TParm readINSParm, String status, String type) {
		TParm parm = getUpdateParm(readINSParm, status, type);
		// 表中INSAMT_FLG对账标志为3
		TParm result = INSOpdTJTool.getInstance().updateINSOpd(parm);// 修改INS_OPD
		if (result.getErrCode() < 0) {
			err("updateInsOpd 修改对账标志失败");
			return result;
		}
		return result;
	}

	/**
	 * 修改INS_OPD 表数据
	 */
	public TParm updateInsOpd(TParm readINSParm, String status, String type,
			TConnection connection) {
		TParm parm = getUpdateParm(readINSParm, status, type);
		// 表中INSAMT_FLG对账标志为3
		TParm result = INSOpdTJTool.getInstance()
				.updateINSOpd(parm, connection);// 修改INS_OPD
		if (result.getErrCode() < 0) {
			err("updateInsOpd 修改对账标志失败");
			return result;
		}
		return result;
	}

	/**
	 * NSAMT_FLG对账标志和票据号
	 * 
	 * @param readINSParm
	 * @param status
	 * @param type
	 * @param connection
	 * @return
	 */
	private TParm updateINSOpdPrint(TParm readINSParm, String type,
			TConnection connection) {
		TParm parm = getUpdateParm(readINSParm, "", type);

		TParm result = INSOpdTJTool.getInstance().updateINSOpdPrint(parm,
				connection);// 修改INS_OPD 表中INSAMT_FLG对账标志为3
		return result;
	}

	private TParm getUpdateParm(TParm readINSParm, String status, String type) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", readINSParm.getData("CASE_NO"));
		parm.setData("RECP_TYPE", type);
		parm.setData("CONFIRM_NO", readINSParm.getData("CONFIRM_NO"));
		parm.setData("INSAMT_FLG", status);// 对账状态
		parm.setData("INV_NO", readINSParm.getValue("PRINT_NO"));// 对账状态
		return parm;
	}

	/**
	 * 退费操作修改INS_OPD 表数据
	 */
	public boolean resetUpdateInsOpd(TParm insOPDParm, String status,
			String type, int insType, TConnection connection) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", insOPDParm.getData("CASE_NO"));
		parm.setData("CONFIRM_NO", "*" + insOPDParm.getData("CONFIRM_NO"));
		parm.setData("INV_NO", insOPDParm.getValue("INV_NO"));// 对账状态
		// 修改INS_OPD 表中INSAMT_FLG对账标志为3
		if (!updateInsAmtFlg(parm, status, type, connection)) {
			resetConcelFee(insOPDParm, insType);
			return false;
		}
		return true;
	}

	/**
	 * 退费明细生成本地对账标志置0
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm resetInsetOpdandOpdOrder(TParm parm, TConnection connection) {
		TParm opdParm = selectResetFee(parm);
		TParm result = new TParm();
		if (opdParm.getErrCode() < 0) {
			result.setErr(-1, "城职普通,查询要退费记录信息操作有问题");
			return result;
		}
		TParm opdOrderParm = selectResetOpdOrderFee(parm);
		if (opdOrderParm.getErrCode() < 0) {
			result.setErr(-1, "城职普通,查询要退费记录信息操作有问题");
			return result;
		}
		// System.out.println("RESULT:::::::::::::::::::::::::" + opdOrderParm);

		// 退费明细生成本地对账标志置0
		TParm opdNewParm = opdParm;
		opdNewParm.setData("CONFIRM_NO", "*" + opdParm.getValue("CONFIRM_NO"));// 违反约束

		TParm insOPDParm = insertReSetInsOpd(opdNewParm, parm
				.getValue("UNRECP_TYPE"), connection);
		if (insOPDParm.getErrCode() < 0) {
			result.setErr(-1, "城职普通,退费明细生成本地对账操作有问题");
			return result;
		}
		TParm insOpdOrderParm = insertReSetInsOpdOrder(opdOrderParm, parm
				.getValue("UNRECP_TYPE"), connection);
		if (insOpdOrderParm.getErrCode() < 0) {
			result.setErr(-1, "城职普通,退费明细生成本地对账操作有问题");
			return result;
		}
		opdParm = selectResetFee(parm);
		return opdParm;
	}

	/**
	 * 费用分割明细修改对账标志
	 * 
	 * @param status
	 * @param connection
	 * @return
	 */
	public TParm updateINSOpdOrder(TParm readINSParm, String status, String type) {
		TParm parm = getUpdateParm(readINSParm, status, type);
		TParm result = INSOpdOrderTJTool.getInstance().updateINSOpdOrder(parm);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateINSOpdOrder修改对账标志失败");
			return result;
		}
		return result;
	}

	/**
	 * 费用分割明细修改对账标志
	 * 
	 * @param status
	 * @param connection
	 * @return
	 */
	public TParm updateINSOpdOrder(TParm readINSParm, String status,
			String type, TConnection connection) {
		TParm parm = getUpdateParm(readINSParm, status, type);
		TParm result = INSOpdOrderTJTool.getInstance().updateINSOpdOrder(parm,
				connection);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateINSOpdOrder修改对账标志失败");
			return result;
		}
		return result;
	}

	/**
	 * INSAMT_FLG对账标志和票据号
	 * 
	 * @param readINSParm
	 * @param status
	 * @param type
	 * @param connection
	 * @return
	 */
	private TParm updateINSOpdOrderPrint(TParm readINSParm, String type,
			TConnection connection) {
		TParm parm = getUpdateParm(readINSParm, "", type);
		TParm result = INSOpdOrderTJTool.getInstance().updateINSOpdOrderPrint(
				parm, connection);
		return result;
	}

	/**
	 * 修改对账标志 逆流程使用
	 * 
	 * @param status
	 * @param connection
	 * @return
	 */
	public boolean updateInsAmtFlg(TParm parm, String status, String type) {
		TParm result = updateInsOpd(parm, status, type);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateInsOpd修改对账标志失败");
			return false;
		}
		result = updateINSOpdOrder(parm, status, type);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateINSOpdOrder修改对账标志失败");
			return false;
		}
		return true;
	}

	/**
	 * 修改对账标志 逆流程使用
	 * 
	 * @param status
	 * @param connection
	 * @return
	 */
	public boolean updateInsAmtFlg(TParm parm, String status, String type,
			TConnection connection) {
		TParm result = updateInsOpd(parm, status, type, connection);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateInsOpd修改对账标志失败");
			return false;
		}
		result = updateINSOpdOrder(parm, status, type, connection);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateINSOpdOrder修改对账标志失败");
			return false;
		}
		return true;
	}

	/**
	 * 对账使用
	 * 
	 * @param parm
	 * @return
	 */
	private boolean updateInsAmtFlg(TParm parm, String flg) {
		parm.setData("INSAMT_FLG", flg);
		TParm result = INSOpdTJTool.getInstance().updateINSOpd(parm);// 修改INS_OPD
		// 表中INSAMT_FLG对账标志为3
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateInsOpd修改对账标志失败");
			return false;
		}
		result = INSOpdOrderTJTool.getInstance().updateINSOpdOrder(parm);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateINSOpdOrder修改对账标志失败");
			return false;
		}
		return true;
	}

	/**
	 * 修改对账标志和票据号码 正流程使用
	 * 
	 * @param status
	 * @param connection
	 * @return
	 */
	public TParm updateInsAmtFlgPrint(TParm parm, String type,
			TConnection connection) {
		TParm result = updateINSOpdPrint(parm, type, connection);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateInsOpd修改对账标志失败");
			return result;
		}
		result = updateINSOpdOrderPrint(parm, type, connection);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateINSOpdOrder修改对账标志失败");
			return result;
		}
		return result;
	}

	/**
	 * 修改对账标记
	 * 
	 * @param status
	 * @param connection
	 * @return
	 */
	public boolean updateINSOpdOrderOne(TParm parm, String status, String type) {
		TParm result = updateINSOpdOrder(parm, status, type);
		if (result.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 刷卡动作 刷卡 执行函数：DataDown_sp, 方法 U 调用险种识别交易 函数DataDown_czys,方法 A
	 * 
	 * @return
	 */
	public TParm readINSCard(TParm parm) {
		TParm readParm = INSTJTool.getInstance().DataDown_sp_U(
				parm.getValue("TEXT"));// U方法，取卡号
		TParm readINSParm = new TParm();
		parm.setData("CARD_NO", readParm.getValue("CARD_NO"));// 医保卡号
		parm.setData("TYPE", 1);// 传入类型:1社保卡卡号2 身份证号码 传固定值 1
		readINSParm = INSTJTool.getInstance().DataDown_czys_A(parm);// A方法，人群类别信息
		// System.out.println("刷卡动作 刷卡 出参 readINSParm::::"+readINSParm);
		if (!INSTJTool.getInstance().getErrParm(readINSParm)) {
			return readINSParm;
		}
		readINSParm.setData("CARD_NO", readParm.getValue("CARD_NO"));// 卡号
		readINSParm.setData("PERSONAL_NO", readINSParm.getValue("PERSONAL_NO"));// 个人编码
		readINSParm.setData("MR_NO", parm.getValue("MR_NO"));// 病案号
		readINSParm.setData("REGION_CODE", parm.getValue("NHI_REGION_CODE"));// 医保区域代码
		readINSParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		readINSParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		readINSParm.setData("ADVANCE_CODE", parm.getValue("ADVANCE_CODE"));//医院编码@费用发生时间@类别
		return readINSParm;
	}

	/**
	 * 身份识别 ：DataDown_sp门诊刷卡（L） 城职普通
	 * 
	 * @return
	 */
	public TParm insIdentificationChZPt(TParm readINSParm) {
		// System.out.println("后台执行");
		if (null == readINSParm || readINSParm.getErrCode() < 0) {
			readINSParm.setErr(-1, "刷卡操作有问题 ");
			return readINSParm;
		}
		// System.out.println("-------readINSParm--------" + readINSParm);
		TParm identificationParm = INSTJTool.getInstance().DataDown_sp_L(
				readINSParm);// L
		// System.out.println("---------------parm-------------"
		// + identificationParm);
		if (!INSTJTool.getInstance().getErrParm(identificationParm)) {
			return identificationParm;
		}

		// 方法
		return identificationParm;
	}

	/**
	 * 城职门特 刷卡返回参数：DataDown_mts门特刷卡交易（E） 得到个人信息
	 * 
	 * @return
	 */
	public TParm insCreditCardChZMt(TParm readINSParm) {
		TParm identificationParm = INSTJTool.getInstance().DataDown_mts_E(
				readINSParm);// 城职门特刷卡返回参数
		if (!INSTJTool.getInstance().getErrParm(identificationParm)) {
			return identificationParm;
		}
		return identificationParm;
	}

	/**
	 * 城居门特 刷卡返回参数,得到个人信息 DataDown_cmts门特刷卡交易（E） 得到个人信息
	 * 
	 * @return
	 */
	public TParm insCreditCardChJMt(TParm readINSParm) {
		TParm identificationParm = INSTJTool.getInstance().DataDown_cmts_E(
				readINSParm);
		if (!INSTJTool.getInstance().getErrParm(identificationParm)) {
			return identificationParm;
		}
		return identificationParm;
	}

	/**
	 * 
	 * 城职门特特殊情况方法：函数DataDown_sp，门特特殊情况上传交易（H）
	 * 城居门特特殊情况方法：函数DataDown_cmts，门特特殊情况上传交易（H）
	 * 
	 * @return
	 */
	public boolean insSpcUpload(TParm parm) {
		// System.out.println("----------城职门特特殊情况方法----------");
		int type = parm.getInt("INS_TYPE");// 1 .城职普通 2. 城职门特 3. 城居门特
		TParm result = INSTJTool.getInstance()
				.specialCaseCommReturn(parm, type);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			result.setErr(-1, result.getValue("PROGRAM_MESSAGE"));
			return false;
		}
		return true;
	}

	/**
	 * 城职普通 添加或修改InsMZConfirm表数据
	 * 
	 * @return
	 */
	private void onSaveInsMZConfirmChZPt(TParm identificationParm,
			TParm readINSParm) {
		identificationParm.setData("PAT_NAME", identificationParm
				.getValue("NAME"));// 患者姓名
		identificationParm.setData("OWN_NO", readINSParm
				.getValue("PERSONAL_NO"));// 个人编码
		identificationParm.setData("IDNO", identificationParm.getValue("SID"));// 身份证号
		identificationParm.setData("SEX_CODE", identificationParm
				.getValue("SEX"));// 性别
		identificationParm.setData("BIRTH_DATE", identificationParm
				.getData("BIRTHDAY"));// 出生日期

		identificationParm.setData("OWN_PAY_RATE", identificationParm
				.getValue("ZFBL"));// 自负比例
		identificationParm.setData("ENTERPRISES_TYPE", identificationParm
				.getValue("COM_TYPE"));// 企业类别
		identificationParm.setData("SPECIAL_PAT", identificationParm
				.getValue("SP_PRESON_TYPE"));// 特殊人员类别 SPECIAL_PAT 表字段
		identificationParm.setData("PAY_KIND", identificationParm
				.getData("PAY_KIND"));// 出生日期
	}

	/**
	 * 添加INS_MZ_COMFIRM 表数据
	 * 
	 * @param identificationParm
	 *            身份识别 ：DataDown_sp门诊刷卡 L 方法出参
	 * @return
	 */
	public TParm onSaveInsMZConfirm(TParm identificationParm,
			TParm readINSParm, int insType) {
		// if (readINSParm.getValue("RECP_TYPE").equals("OPB")) {
		// //
		// identificationParm.setData("BIRTH_DATE",df.format(identificationParm
		// // .getValue("BIRTH_DATE")) );// 出生日期
		// } else if (readINSParm.getValue("RECP_TYPE").equals("REG")) {
		// System.out.println("readINSParm::" + readINSParm);
		identificationParm.setData("REGION_CODE", readINSParm
				.getValue("NEW_REGION_CODE"));
		identificationParm.setData("CASE_NO", readINSParm.getValue("CASE_NO"));
		identificationParm.setData("MR_NO", readINSParm.getValue("MR_NO"));
		identificationParm.setData("INSCARD_NO", readINSParm
				.getValue("CARD_NO"));// 医保号
		identificationParm.setData("INSCARD_PASSWORD", readINSParm
				.getValue("PASSWORD"));// 医保密码

		identificationParm.setData("INS_PAT_TYPE", readINSParm
				.getValue("INS_PAT_TYPE"));// 就医类别
		identificationParm.setData("INS_CROWD_TYPE", readINSParm
				.getValue("CROWD_TYPE"));// 卡类型

		identificationParm.setData("AUTO_FLG", "Y");//
		readINSParm.setData("CONFIRM_NO", identificationParm
				.getValue("CONFIRM_NO"));// 资格确认书
		identificationParm.setData("DISEASE_CODE", readINSParm
				.getValue("DISEASE_CODE"));// 门特类别
		// }
		identificationParm
				.setData("OPT_USER", readINSParm.getValue("OPT_USER"));
		identificationParm
				.setData("OPT_TERM", readINSParm.getValue("OPT_TERM"));
		switch (insType) {
		case 1:
			onSaveInsMZConfirmChZPt(identificationParm, readINSParm);
			break;
		case 2:
			onSaveInsMZConfirmChZMt(identificationParm, readINSParm);
			break;
		case 3:
			onSaveInsMZConfirmChJMt(identificationParm, readINSParm);
		}
		checkOutParm(identificationParm, insMZConfirm);
		// System.out.println("入参---------------------" + identificationParm);
		TParm result = INSMZConfirmTool.getInstance().onSaveInsMZConfirm(
				identificationParm);
		if (result.getErrCode() < 0) {
			// System.out.println("执行INSTJFlow.insIdentification方法出错误");
			return result;
		}
		return result;
	}

	/**
	 * 城职门特 添加或修改InsMZConfirm表数据
	 * 
	 * @param identificationParm
	 * @param readINSParm
	 */
	private void onSaveInsMZConfirmChZMt(TParm identificationParm,
			TParm readINSParm) {
		identificationParm.setData("BANK_NO", null == identificationParm
				.getValue("BANK_NO") ? "" : identificationParm
				.getValue("BANK_NO"));//
		identificationParm.setData("OWN_NO", readINSParm
				.getValue("PERSONAL_NO"));// 个人编码
		identificationParm.setData("IDNO", identificationParm.getValue("SID"));// 身份证号
		identificationParm.setData("CTZ_CODE", identificationParm
				.getValue("PAT_TYPE"));// 人员类别
		identificationParm.setData("UNIT_NO", identificationParm
				.getValue("COMPANY_NO"));// 单位编码
		identificationParm.setData("UNIT_CODE", identificationParm
				.getValue("COMPANY_CODE"));// 单位代码
		identificationParm.setData("UNIT_DESC", identificationParm
				.getValue("COMPANY_DESC"));// 单位名称
		identificationParm.setData("INS_CODE", identificationParm
				.getValue("BRANCH_CODE"));// 所属社保机构
		identificationParm.setData("TOT_AMT", identificationParm
				.getDouble("PERSON_ACCOUNT_AMT"));// 账户结余金额
		identificationParm.setData("OTOT_AMT", 0.00);// 门诊专项基金剩余
		identificationParm.setData("ENTERPRISES_TYPE", identificationParm
				.getValue("COM_TYPE"));// 企业类别
		identificationParm.setData("OWN_PAY_RATE", identificationParm
				.getValue("OWEN_PAY_SCALE"));// 自负比例
		identificationParm.setData("SPECIAL_PAT", identificationParm
				.getValue("SP_PRESON_TYPE"));// 特殊人员类别
		identificationParm.setData("BIRTH_DATE", identificationParm
				.getData("BIRTH_DATE"));// 出生日期

		// SP_PERSON_MEMO
	}

	/**
	 * 城居门特 添加或修改InsMZConfirm表数据
	 * 
	 * @param identificationParm
	 * @param readINSParm
	 */
	private void onSaveInsMZConfirmChJMt(TParm identificationParm,
			TParm readINSParm) {
		onSaveInsMZConfirmChZMt(identificationParm, readINSParm);
	}

	/**
	 * 查询退费的信息 挂号/门诊收费 查询表INS_OPD 查询条件 CASE_NO RECP_TYPE REGION_CODE CONFIRM_NO
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectResetFee(TParm parm) {
		// TParm result=INSMZConfirmTool.getInstance().queryMZConfirm(parm);
		// if (result.getErrCode() < 0) {
		// //System.out.println("执行INSTJFlow.queryMZConfirm方法出错误");
		// return false;
		// }
		// if (result.getCount()<=0) {
		// //System.out.println("没有查询的数据");
		// return false;
		// }
		// parm.setData("CONFIRM_NO",result.getValue("CONFIRM_NO",0));//资格确认书号
		TParm insOPDParm = INSOpdTJTool.getInstance().selectResetFee(parm);
		if (insOPDParm.getErrCode() < 0) {
			insOPDParm.setErr(-1, "执行INSTJFlow.selectResetFee方法出错误");
			return insOPDParm;
		}
		insOPDParm = insOPDParm.getRow(0);
		return insOPDParm;
	}

	/**
	 * 退费使用查询需要添加的退费数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectResetOpdOrderFee(TParm parm) {
		TParm result = INSOpdOrderTJTool.getInstance()
				.selectResetOpdOrder(parm);
		if (result.getErrCode() < 0) {
			result.setErr(-1, "执行selectResetOpdOrderFee方法出错误");
			return result;
		}
		return result;
	}

	/**
	 * 城职普通退费接口
	 * 
	 * @param parm
	 * @return
	 */
	public TParm resetFeeChZPt(TParm parm, TParm insOPDParm) {
		insOPDParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		insOPDParm.setData("HOSP_OPT_USER_CODE", parm.getValue("OPT_USER"));
		TParm resetInsOPDParm = INSTJTool.getInstance().DataDown_yb_C(
				insOPDParm);
		if (!INSTJTool.getInstance().getErrParm(resetInsOPDParm)) {
			// System.out.println("城职普通退费操作失败");
			return resetInsOPDParm;
		}

		// .getInt("INSAMT_FLG")
		return resetInsOPDParm;
	}

	/**
	 * 城职门特退费接口
	 * 
	 * @param parm
	 * @return
	 */
	public TParm resetFeeChZMt(TParm parm, TParm insOPDParm) {
		insOPDParm.setData("HOSP_NHI_NO", parm.getValue("REGION_CODE"));
		insOPDParm.setData("HOSP_OPT_USER_CODE", parm.getValue("OPT_USER"));
		insOPDParm.setData("PAT_TYPE", parm.getValue("PAT_TYPE"));
		TParm resetInsOPDParm = INSTJTool.getInstance().DataDown_mts_K(
				insOPDParm);
		if (!INSTJTool.getInstance().getErrParm(resetInsOPDParm)) {
			// System.out.println("城职普通退费操作失败");
			return resetInsOPDParm;
		}
		// .getInt("INSAMT_FLG")
		return resetInsOPDParm;
	}

	/**
	 * 城居门特退费接口
	 * 
	 * @param parm
	 * @return
	 */
	public TParm resetFeeChJMt(TParm parm, TParm insOPDParm) {
		insOPDParm.setData("HOSP_NHI_NO", parm.getValue("REGION_CODE"));
		insOPDParm.setData("HOSP_OPT_USER_CODE", parm.getValue("OPT_USER"));
		insOPDParm.setData("PAT_TYPE", parm.getValue("PAT_TYPE"));
		TParm resetInsOPDParm = INSTJTool.getInstance().DataDown_cmts_K(
				insOPDParm);
		if (!INSTJTool.getInstance().getErrParm(resetInsOPDParm)) {
			// System.out.println("城职普通退费操作失败");
			return resetInsOPDParm;
		}
		// .getInt("INSAMT_FLG")
		return resetInsOPDParm;
	}

	/**
	 * 退费取消操作
	 * 
	 * @return
	 */
	public boolean resetConcelFee(TParm insOPDParm, int insType) {
		TParm result = null;
		switch (insType) {// 1.城职普通 2.城职门特 3.城居门特
		case 1:
			result = INSTJTool.getInstance().DataDown_sp_S(
					insOPDParm.getValue("CONFIRM_NO"),
					insOPDParm.getValue("REGION_CODE"), "31");// 退费取消
			break;
		case 2:
			result = INSTJTool.getInstance().DataDown_mts_J(
					insOPDParm.getValue("CONFIRM_NO"),
					insOPDParm.getValue("REGION_CODE"), "31");// 退费取消
			break;
		case 3:
			result = INSTJTool.getInstance().DataDown_cmts_J(
					insOPDParm.getValue("CONFIRM_NO"),
					insOPDParm.getValue("REGION_CODE"), "31");// 退费取消
			break;
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			// System.out.println("退费取消操作失败");
			return false;
		}
		return true;
	}

	/**
	 * 退费取消操作 操作人员执行取消操作
	 * 
	 * @return
	 */
	public TParm resetConcelFee(TParm insOPDParm) {
		TParm result = null;
		int insType = insOPDParm.getInt("INS_TYPE");
		switch (insType) {// 1.城职普通 2.城职门特 3.城居门特
		case 1:
			result = INSTJTool.getInstance().DataDown_sp_S(
					insOPDParm.getValue("CONFIRM_NO"),
					insOPDParm.getValue("REGION_CODE"), "31");// 退费取消
			break;
		case 2:
			result = INSTJTool.getInstance().DataDown_mts_J(
					insOPDParm.getValue("CONFIRM_NO"),
					insOPDParm.getValue("REGION_CODE"), "31");// 退费取消
			break;
		case 3:
			result = INSTJTool.getInstance().DataDown_cmts_J(
					insOPDParm.getValue("CONFIRM_NO"),
					insOPDParm.getValue("REGION_CODE"), "31");// 退费取消
			break;
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			// System.out.println("退费取消操作失败");
			return result;
		}
		return result;
	}

	/**
	 * 城职普通退费确认 执行添加一条退费信息 INS_OPD 表操作 函数DataDown_yb (D)方法
	 * 
	 * @param parm
	 * @return
	 */
	public TParm resetFeeInsSettleChZPt(TParm insOPDParm, TParm resetInsOPDParm) {

		resetInsOPDParm
				.setData("CONFIRM_NO", insOPDParm.getValue("CONFIRM_NO"));
		resetInsOPDParm.setData("REGION_CODE", insOPDParm
				.getValue("NHI_REGION_CODE"));
		resetInsOPDParm.setData("ACCOUNT_PAY_AMT", insOPDParm
				.getDouble("ACCOUNT_PAY_AMT"));
		// System.out.println("resetInsOPDParm:::::" + resetInsOPDParm);
		TParm result = INSTJTool.getInstance().DataDown_yb_D(resetInsOPDParm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			result = INSTJTool.getInstance().DataDown_yb_D(resetInsOPDParm);
			if (!INSTJTool.getInstance().getErrParm(result)) {
				// System.out.println("再次调用结算确认交易,继续失败");
				return result;
			}
		}
		// .getInt("INS_FLG")
		return result;
	}

	/**
	 * 城职门特退费确认 执行添加一条退费信息 INS_OPD 表操作 函数DataDown_mts(L)方法
	 * 
	 * @param parm
	 * @return
	 */
	public TParm resetFeeInsSettleChZMt(TParm insOPDParm) {

		TParm result = INSTJTool.getInstance().DataDown_mts_L(insOPDParm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			result = INSTJTool.getInstance().DataDown_mts_L(insOPDParm);
			if (!INSTJTool.getInstance().getErrParm(result)) {
				// System.out.println("再次调用结算确认交易,继续失败");
				return result;
			}
		}
		// .getInt("INS_FLG")
		return result;
	}

	/**
	 * 城居门特退费确认 执行添加一条退费信息 INS_OPD 表操作 函数DataDown_cmts(L)方法
	 * 
	 * @param parm
	 * @return
	 */
	public TParm resetFeeInsSettleChJMt(TParm insOPDParm) {
		TParm result = INSTJTool.getInstance().DataDown_cmts_L(insOPDParm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			result = INSTJTool.getInstance().DataDown_cmts_L(insOPDParm);
			if (!INSTJTool.getInstance().getErrParm(result)) {
				// System.out.println("再次调用结算确认交易,继续失败");
				return result;
			}
		}
		// .getInt("INS_FLG")
		return result;
	}

	/**
	 * 在途状态
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public boolean insertRun(TParm parm, String wayName) {
		TParm runParm = runTempParm(parm);
		runParm.setData("STUTS", "1");// 1.在途 2.成功
		runParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		runParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		runParm.setData("EXE_WAY", wayName);
		TParm result = INSRunTool.getInstance().insertInsRun(runParm);
		if (result.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 在途状态
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public boolean insertRun(TParm parm, String wayName, TConnection connection) {
		TParm runParm = runTempParm(parm);
		runParm.setData("STUTS", "1");// 1.在途 2.成功
		runParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		runParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		runParm.setData("EXE_WAY", wayName);
		TParm result = INSRunTool.getInstance().insertInsRun(runParm,
				connection);
		if (result.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否存在在途状态，如果存在不执行 STUTS="1"
	 * 
	 * @param parm
	 * @return
	 */
	public boolean queryRun(TParm parm) {
		TParm runParm = runTempParm(parm);
		runParm.setData("STUTS", "1");
		runParm = INSRunTool.getInstance().queryInsRun(runParm);
		if (runParm.getErrCode() < 0) {
			return false;
		}
		for (int i = 0; i < runParm.getCount(); i++) {
			if (runParm.getInt("STUTS", i) == 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 添加医保退费数据 退费明细生成本地对账标志置0
	 * 
	 * @param connection
	 * @return
	 */
	public TParm insertReSetInsOpd(TParm insOPDParm, String unRecpType,
			TConnection connection) {
		// insOPDParm.setData("INSAMT_FLG", 0);
		insOPDParm.setData("UNRECP_TYPE", unRecpType);// 退费类型
		INSTJTool.getInstance().balanceParm(insOPDParm);
		TParm result = INSOpdTJTool.getInstance().insertResetINSOpd(insOPDParm,
				connection);
		// System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			// connection.close();
			return result;
		}
		result = INSOpdTJTool.getInstance().selectResetFee(insOPDParm);
		return insOPDParm;
	}

	/**
	 * 添加退费INS_OPD_ORDER数据
	 * 
	 * @param insOpdOrderParm
	 * @param unRecpType
	 * @param connection
	 * @return
	 */
	public TParm insertReSetInsOpdOrder(TParm insOpdOrderParm,
			String unRecpType, TConnection connection) {
		TParm result = new TParm();
		// System.out.println("insOpdOrderParm::::" + insOpdOrderParm);
		if (insOpdOrderParm.getCount() <= 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		TParm seqParm = new TParm();
		seqParm.setData("CASE_NO", insOpdOrderParm.getValue("CASE_NO", 0));
		seqParm.setData("CONFIRM_NO", "*"
				+ insOpdOrderParm.getValue("CONFIRM_NO", 0));
		seqParm.setData("RECP_TYPE", insOpdOrderParm.getValue("RECP_TYPE", 0));
		TParm maxSeqOpdOrderParm = INSOpdOrderTJTool.getInstance()
				.selectMAXSeqNo(seqParm);
		int maxSeq = 0;// 最大号码
		if (null != maxSeqOpdOrderParm.getValue("SEQ_NO", 0)
				&& maxSeqOpdOrderParm.getInt("SEQ_NO", 0) > 0) {
			maxSeq = maxSeqOpdOrderParm.getInt("SEQ_NO", 0);
		}
		for (int i = 0; i < insOpdOrderParm.getCount("CASE_NO"); i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, insOpdOrderParm, i);
			inParm.setData("RECP_TYPE", unRecpType);// 退费
			inParm.setData("INSAMT_FLG", 0);// 未对账操作
			inParm.setData("OWN_AMT", -inParm.getDouble("OWN_AMT"));// 自费
			inParm.setData("TOTAL_AMT", -inParm.getDouble("TOTAL_AMT"));// 发生金额
			inParm.setData("TOTAL_NHI_AMT", -inParm.getDouble("TOTAL_NHI_AMT"));// 医保金额
			inParm.setData("ADDPAY_AMT", -inParm.getDouble("ADDPAY_AMT"));//
			inParm.setData("SEQ_NO", maxSeq + i + 1);
			inParm.setData("CONFIRM_NO", "*" + inParm.getValue("CONFIRM_NO"));
			checkOutParm(inParm, insOpdOrder);
			result = INSOpdOrderTJTool.getInstance().insertINSOpdOrder(inParm);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				// connection.close();
				return result;
			}
		}
		return result;
	}

	/**
	 * 删除医保在途状态d\
	 * 
	 * @return
	 */
	public boolean deleteInsRun(TParm runParm) {
		TParm parm = runTempParm(runParm);
		parm = INSRunTool.getInstance().deleteInsRun(parm);
		if (parm.getErrCode() < 0) {
			// System.out.println("err:" + parm.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * 医保在途参数操作
	 * 
	 * @param runParm
	 */
	private TParm runTempParm(TParm runParm) {
		TParm parm = new TParm();
		if (null!=runParm.getValue("CASE_NO_SUM") && runParm.getValue("CASE_NO_SUM").length()>0) {
			parm.setData("CASE_NO", runParm.getValue("CASE_NO_SUM"));
		}else{
			parm.setData("CASE_NO", runParm.getValue("CASE_NO"));
		}
		parm.setData("EXE_USER", runParm.getValue("OPT_USER"));
		parm.setData("EXE_TERM", runParm.getValue("OPT_TERM"));
		parm.setData("EXE_TYPE", runParm.getValue("EXE_TYPE"));
		return parm;
	}

	/**
	 * 校验是否为空
	 * 
	 * @param parm
	 */
	private void checkOutParm(TParm parm, String[] data) {
		for (int i = 0; i < data.length; i++) {
			if (null == parm.getValue(data[i])
					|| parm.getValue(data[i]).trim().length() <= 0) {
				parm.setData(data[i], "");
			}

		}
	}

	/**
	 * 对账使用 删除数据
	 * 
	 * @param parm
	 */
	private TParm delInsOpdAndOpdOrder(TParm parm) {
		TParm result = new TParm();
		result = INSOpdTJTool.getInstance().deleteINSOpd(parm);
		if (result.getErrCode() < 0) {
			// System.out.println("err:" + parm.getErrText());
			return result;
		}
		result = INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(parm);
		if (result.getErrCode() < 0) {
			// System.out.println("err:" + parm.getErrText());
			return result;
		}
		//======pangben 2013-3-13 添加在途删除
		if (null!=parm.getValue("CHOOSE_FLG")&&parm.getValue("CHOOSE_FLG").equals("Y")) {
			result =INSRunTool.getInstance().deleteInsRunConcel(parm);//取消操作删除在途状态，对账界面操作
		}else{
			result =INSRunTool.getInstance().deleteInsRun(parm);//取消操作删除在途状态
		}
		if (result.getErrCode() < 0) {
			// System.out.println("err:" + parm.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 门诊自动对账
	 * 
	 * @param parm
	 * @return
	 */
	public Map autoAccountComm(TParm parm) {
		TParm returnParm = new TParm();// 累计出现错误的数据
		StringBuffer buffer = new StringBuffer();
		boolean errFlg = false;// 是否出现错误
		buffer.append("医保就诊号:");
		// TParm interFaceParmOne = new TParm();
		int insType = parm.getInt("INS_TYPE");
		// System.out.println("自动对账数据parm::::" + parm);
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm tempParm = new TParm();
			tempParm.setRowData(-1, parm, i);// 获得一条要对账的数据
			tempParm.setData("NHI_REGION_CODE", parm
					.getValue("NHI_REGION_CODE"));

			// 获得医保病患信息
			TParm mzConfirmParm = INSMZConfirmTool.getInstance()
					.queryMZConfirmOne(tempParm);
			// 退费
			if (tempParm.getValue("RECP_TYPE").equals("REGT")
					|| tempParm.getValue("RECP_TYPE").equals("OPBT")) {
				returnParm = reSetAutoAccount(tempParm, insType);
				if (returnParm.getErrCode() < 0) {
					errFlg = true;
				}
			} else {
				// 正流程
				returnParm = autoAccount(tempParm, insType, mzConfirmParm,
						buffer);
				if (returnParm.getErrCode() < 0) {
					errFlg = true;
				}
			}

		}
		buffer.append("自动对账失败");
		if (errFlg)
			returnParm.setErr(-1, buffer.toString());
		return returnParm.getData();
	}

	/**
	 * 退费自动对账
	 * 
	 * @return
	 */
	public TParm reSetAutoAccount(TParm tempParm, int insType) {
		TParm result = new TParm();
		TParm interFaceParmOne = new TParm();
		// 取消结算交易

		// 对账标志为0调用取消交易
		if (tempParm.getInt("INSAMT_FLG") == 0) {
			switch (insType) {
			case 1:
				result = INSTJTool.getInstance().DataDown_sp_S(tempParm, "31");
				break;
			case 2:
				result = INSTJTool.getInstance().DataDown_mts_J(tempParm, "31");
				break;
			case 3:
				result = INSTJTool.getInstance()
						.DataDown_cmts_J(tempParm, "31");
				break;

			}
			if (!INSTJTool.getInstance().getErrParm(result)) {
				return result;
			}
			// 执行删除INS_OPD \INS_OPD_ORDER操作
			result = delInsOpdAndOpdOrder(tempParm);
			// System.out.println("退费自动对账取消结算交易:"+result.getValue("PROGRAM_MESSAGE"));
		} else if (tempParm.getInt("INSAMT_FLG") == 1) {
			switch (insType) {
			case 1:
				interFaceParmOne = INSTJTool.getInstance().DataDown_yb_D(
						tempParm);
				break;
			case 2:
				interFaceParmOne = INSTJTool.getInstance().DataDown_mts_L(
						tempParm);
				break;
			case 3:
				interFaceParmOne = INSTJTool.getInstance().DataDown_cmts_L(
						tempParm);
				break;
			}
			if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
				return interFaceParmOne;
			} else {
				if (interFaceParmOne.getInt("INSAMT_FLG") == 3) {
					// 执行修改INS_OPD \INS_OPD_ORDER操作
					if (!updateInsAmtFlg(tempParm, "3")) {
					}
					result =INSRunTool.getInstance().deleteInsRunConcel(tempParm);//取消操作删除在途状态，对账界面操作
				}
			}
		}
		
		return result;
	}

	/**
	 * 正流程 自动对账
	 * 
	 * @param tempParm
	 * @param insType
	 *            1.城职普通 2.城职门特 3.城居门特
	 * @param mzConfirmParm
	 *            病患信息
	 * @return
	 */
	private TParm autoAccount(TParm tempParm, int insType, TParm mzConfirmParm,
			StringBuffer buffer) {
		// 对未出票并且对账标志为3以下的正常消费记录
		TParm returnParm = new TParm();// 累计出现错误的数据
		boolean errFlg = false;// 是否出现错误
		TParm result = new TParm();
		TParm interFaceParmOne = new TParm();
		// System.out.println("票据号码:::::" + tempParm);
		if (null == tempParm.getValue("INV_NO")
				|| tempParm.getValue("INV_NO").length() <= 0) {
			// 取消结算交易
			switch (insType) {
			case 1:
				result = INSTJTool.getInstance().DataDown_sp_S(tempParm, "30");
				break;
			case 2:
				result = INSTJTool.getInstance().DataDown_mts_J(tempParm, "30");
				break;
			case 3:
				result = INSTJTool.getInstance()
						.DataDown_cmts_J(tempParm, "30");
				break;

			}
			if (!INSTJTool.getInstance().getErrParm(result)) {
				errFlg = true;
				buffer.append(tempParm.getValue("CONFIRM_NO") + ",\n");
			} else {
				// 执行删除INS_OPD \INS_OPD_ORDER操作
				result = delInsOpdAndOpdOrder(tempParm);
				if (result.getErrCode() < 0) {
					errFlg = true;
					buffer.append(tempParm.getValue("CONFIRM_NO") + ",\n");
				}
			}
		} else {
			// 对已出票的正常消费，出票的时候对账标志至少是1 建议为3时出票
			// 程序中现在执行打票的条件是 对账标志是 3时才可以打票
			// 出现 对账为1 执行结算交易接口

			if (tempParm.getInt("INSAMT_FLG") == 1) {
				// 结算交易
				switch (insType) {
				case 1:
					interFaceParmOne = INSTJTool.getInstance().DataDown_sp_R(
							tempParm);
					break;
				case 2:
					interFaceParmOne = INSTJTool.getInstance().DataDown_mts_I(
							tempParm, mzConfirmParm.getRow(0));
					break;
				case 3:
					interFaceParmOne = INSTJTool.getInstance().DataDown_cmts_I(
							tempParm, mzConfirmParm.getRow(0));
					break;
				}
				if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
					errFlg = true;
					buffer.append(tempParm.getValue("CONFIRM_NO") + ",\n");
				} else {
					if (interFaceParmOne.getInt("INSAMT_FLG") == 3) {
						// 执行修改INS_OPD \INS_OPD_ORDER操作
						if (!updateInsAmtFlg(tempParm, "3")) {
							errFlg = true;
							buffer.append(tempParm.getValue("CONFIRM_NO")
									+ ",\n");
						}
					}
				}
			} else {
				// INSAMT_FLG ==0
				// 执行删除INS_OPD \INS_OPD_ORDER操作
				// result = INSTJTool.getInstance().DataDown_sp_S(parm);
				// if (!INSTJTool.getInstance().getErrParm(result)) {
				// result.setErr(-1, "再次调用结算确认交易,继续失败");
				// return result;
				// }
				result = delInsOpdAndOpdOrder(tempParm);
				if (result.getErrCode() < 0) {
					errFlg = true;
					buffer.append(tempParm.getValue("CONFIRM_NO") + ",\n");
				}
			}
		}
		if (errFlg) {
			returnParm.setErr(-1, "出错");
		}
		return returnParm;
	}

	/**
	 * 对总账共用方法
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm opdAccountComm(TParm parm) {
		// buffer.append("医保就诊号:");
		// 门诊总对账汇总数据
		TParm restult = sumOpdParm(parm);
		if (!INSTJTool.getInstance().getErrParm(restult)) {
			// restult.setErr(-1, "对总账失败");
			return restult;
		}
		return restult;
	}

	/**
	 * 对明细帐共用方法
	 * 
	 * @param parm
	 * @return
	 */
	public TParm opdOrderAccountComm(TParm opdOrderParm, TParm parm) {
		TParm result = new TParm();
		int insType = parm.getInt("INS_TYPE");// 1.城职普通 2.城职门特 3.城居门特
		opdOrderParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		opdOrderParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		// opdOrderParm.setData("PAT_TYPE", parm.getValue("PAT_TYPE"));// 支付类别
		opdOrderParm.setData("ACCOUNT_DATE", parm.getValue("ACCOUNT_DATE"));// 对账时间
		// opdOrderParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));//
		// 人员类别
		switch (insType) {
		case 1:
			result = sumOpdOrderChZPt(opdOrderParm);
			break;
		case 2:
			result = sumOpdOrderChZMt(opdOrderParm);
			break;
		case 3:
			result = sumOpdOrderChJMt(opdOrderParm);
			break;
		}
		return result;
	}

	/**
	 * 门诊对总账汇总数据
	 * 
	 * @param insOpdParm
	 * @return
	 */
	private TParm sumOpdParm(TParm insOpdParm) {
		TParm result = new TParm();
		int insType = insOpdParm.getInt("INS_TYPE");// 1.城职普通 2.城职门特 3.城居门特
		switch (insType) {
		case 1:
			result = sumOpdChZPt(insOpdParm);
			break;
		case 2:
			result = sumOpdChZMtAndChJMt(insOpdParm);
			break;
		case 3:
			result = sumOpdChZMtAndChJMt(insOpdParm);
			break;
		}
		return result;
	}

	/**
	 * 城职普通 门诊对总账
	 * 
	 * @param insOpdParm
	 * @return
	 */
	private TParm sumOpdChZPt(TParm insOpdParm) {
		TParm parm = new TParm();
		String regionCode = insOpdParm.getValue("REGION_CODE");// 1 HOSP_NHI_NO
		// 医院编码
		String hospOptUserCode = insOpdParm.getValue("USER_ID");// 2
		// HOSP_OPT_USER_CODE
		// 医院操作员编码
		String ownNo = insOpdParm.getValue("OWN_NO", 0);// 3 OWN_NO 个人编码
		String collateAccountTime = insOpdParm.getValue("ACCOUNT_DATE");// 4
		// COLLATE_ACCOUNT_TIME
		// 对帐时间
		String payKind = insOpdParm.getValue("PAY_KIND");// 5 PAY_TYPE 支付类别
		String patType = insOpdParm.getValue("PAT_TYPE");// PAT_TYPE 人员类别
		TParm specialParm = insOpdParm.getParm("specialParm");// 特殊人员类别金额
		// -----?????
		double totalAmt = 0.00;// 6 TOTAL_AMT 发生金额
		double nhiAmt = 0.00;// 7 NHI_AMT 申报金额
		double ownAmt = 0.00;// 8 OWN_AMT 全自费金额
		double addpayAmt = 0.00;// 9 ADDPAY_AMT 增负金额
		double ototAmt = 0.00;// 10 OTOT_AMT 专项基金社保支付
		double accountPayAmt = 0.00;// 11 ACCOUNT_PAY_AMT 个人帐户实际支付金额
		int allTime = 0;// 12 ALL_TIME 总人次
		double ototOutAmt = 0.00;// 13 OTOT_OUT_AMT 专项基金社保支付(退费)
		double accountPayAmtExit = 0.00;// 14 ACCOUNT_PAY_AMT_EXIT
		// 个人帐户实际支付金额(退费)
		int allTimeExit = 0;// 15 ALL_TIME_EXIT 总人次(退费)
		double agentAmt = 0.00;// 16 AGENT_AMT 民政救助补助金额
		double agentAmtOut = 0.00;// 17 AGENT_AMT_OUT 民政救助补助金额(退费)
		double fyAgentAmt = 0.00;// 18 FY_AGENT_AMT 优抚对象补助金额
		double fyAgentAmtB = 0.00;// 19 FY_AGENT_AMT_B 优抚对象补助金额(退费)
		double fdAgentAmt = 0.00;// 20 FD_AGENT_AMT 非典后遗症补助金额
		double fdAgentAmtB = 0.00;// 21 FD_AGENT_AMT_B 非典后遗症补助金额退费)
		double unReimAmt = 0.00;// 22 UNREIM_AMT 基金未报销金
		double unReimAmtB = 0.00;// 23 UNREIM_AMT_B 基金未报销金额（退费）
		double armyAiAmt = 0.00;// 补助金额ARMY_AI_AMT
		double servantAmt= 0.00;//公务员补助金额
		double servantAmtB= 0.00;//公务员补助金额(退费)
		double JCarmyAiAmt = 0.00;//军残补助金额
		double JCarmyAiAmtB = 0.00;//军残补助金额(退费)
		for (int i = 0; i < insOpdParm.getCount(); i++) {
			// 特殊人员类别SPECIAL_PAT:04 伤残军人06 公务员07 民政救助人员08 优抚对象09 非典后遗症
			// 退费数据
			if (insOpdParm.getValue("RECP_TYPE", i).equals("REGT")
					|| insOpdParm.getValue("RECP_TYPE", i).equals("OPBT")) {
				ototOutAmt += insOpdParm.getDouble("OTOT_AMT", i);
				accountPayAmtExit += insOpdParm.getDouble("ACCOUNT_PAY_AMT", i);
				unReimAmtB += insOpdParm.getDouble("UNREIM_AMT", i);
				allTimeExit++;
			} else {
				totalAmt += insOpdParm.getDouble("TOT_AMT", i);
				nhiAmt += insOpdParm.getDouble("NHI_AMT", i);
				ownAmt += insOpdParm.getDouble("OWN_AMT", i);
				addpayAmt += insOpdParm.getDouble("ADD_AMT", i);

				ototAmt += insOpdParm.getDouble("OTOT_AMT", i);
				accountPayAmt += insOpdParm.getDouble("ACCOUNT_PAY_AMT", i);
				unReimAmt += insOpdParm.getDouble("UNREIM_AMT", i);
				allTime++;
			}
		}
		for (int i = 0; i < specialParm.getCount(); i++) {
			armyAiAmt = specialParm.getDouble("ARMY_AI_AMT", i);
			if (specialParm.getValue("RECP_TYPE", i).equals("REGT")
					|| specialParm.getValue("RECP_TYPE", i).equals("OPBT")) {
				// 退费操作 获得特殊人员编号累计金额
				String special_pat = "";
				String confirmno = specialParm.getValue("CONFIRM_NO", i).
				substring(1, specialParm.getValue("CONFIRM_NO", i).length());
				String sql = " SELECT CONFIRM_NO,SPECIAL_PAT FROM INS_MZ_CONFIRM"+
                             " WHERE CONFIRM_NO = '" + confirmno + "'";
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				special_pat = result.getValue("SPECIAL_PAT", 0);
//				for (int j = 0; j < specialParm.getCount(); j++) {
//					if (specialParm.getValue("CONFIRM_NO", i).equals(
//							"*" + specialParm.getValue("CONFIRM_NO", j))) {
//						special_pat = specialParm.getValue("SPECIAL_PAT", j);
//						break;
//					}
//				}
				if (null != special_pat && special_pat.length() > 0) {
					if (special_pat.equals("04")) {// 04
						// 伤残军人
						JCarmyAiAmtB+=armyAiAmt;
					} else if (special_pat.equals("06")) {// 06
						// 公务员
						servantAmtB+=armyAiAmt;
					} else if (special_pat.equals("07")) {// 07
						// 民政救助人员
						agentAmtOut += armyAiAmt;
					} else if (special_pat.equals("08")) {// 08
						// 优抚对象
						fyAgentAmtB += armyAiAmt;
					} else if (special_pat.equals("09")) {// 09
						// 非典后遗症
						fdAgentAmtB += armyAiAmt;
					}
				}
			} else {
				if (null != specialParm.getValue("SPECIAL_PAT", i)) {
					if (specialParm.getValue("SPECIAL_PAT", i).equals("04")) {// 04
						// 伤残军人
						JCarmyAiAmt+=armyAiAmt;
					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"06")) {// 06
						// 公务员
						servantAmt+=armyAiAmt;
					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"07")) {// 07
						// 民政救助人员
						agentAmt += armyAiAmt;
					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"08")) {// 08
						// 优抚对象
						fyAgentAmt += armyAiAmt;
					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"09")) {// 09
						// 非典后遗症
						fdAgentAmt += armyAiAmt;
					}
				}
			}
		}
		parm.setData("REGION_CODE", regionCode);
		parm.setData("OPT_USER", hospOptUserCode);
		parm.setData("ACCOUNT_DATE", collateAccountTime);
		parm.setData("PAY_KIND", payKind);
		parm.setData("OWN_NO", ownNo);
		parm.setData("TOTAL_AMT", getDoubleValue(totalAmt));
		parm.setData("NHI_AMT", getDoubleValue(nhiAmt));
		parm.setData("OWN_AMT", getDoubleValue(ownAmt));
		parm.setData("ADDPAY_AMT", getDoubleValue(addpayAmt));
		parm.setData("OTOT_AMT", getDoubleValue(ototAmt));
		parm.setData("ACCOUNT_PAY_AMT", getDoubleValue(accountPayAmt));
		parm.setData("ALL_TIME", allTime);
		parm.setData("OTOT_OUT_AMT", getDoubleValue(Math.abs(ototOutAmt)));
		parm.setData("ACCOUNT_PAY_AMT_EXIT", getDoubleValue(Math
				.abs(accountPayAmtExit)));
		parm.setData("ALL_TIME_EXIT", allTimeExit);
		parm.setData("AGENT_AMT", getDoubleValue(agentAmt));
		parm.setData("AGENT_AMT_OUT", getDoubleValue(Math.abs(agentAmtOut)));
		parm.setData("FY_AGENT_AMT", getDoubleValue(fyAgentAmt));
		parm.setData("FY_AGENT_AMT_B", getDoubleValue(Math.abs(fyAgentAmtB)));
		parm.setData("FD_AGENT_AMT", getDoubleValue(fdAgentAmt));
		parm.setData("FD_AGENT_AMT_B", getDoubleValue(Math.abs(fdAgentAmtB)));
		parm.setData("UNREIM_AMT", getDoubleValue(unReimAmt));
		parm.setData("UNREIM_AMT_B", getDoubleValue(Math.abs(unReimAmtB)));
		parm.setData("SERVANT_AMT", getDoubleValue(servantAmt));
		parm.setData("SERVANT_AMT_B", getDoubleValue(Math.abs(servantAmtB)));
		parm.setData("ARMY_AI_AMT", getDoubleValue(JCarmyAiAmt));
		parm.setData("ARMY_AI_AMT_B", getDoubleValue(Math.abs(JCarmyAiAmtB)));
		// System.out.println("对总账下载操作入参数据：：：：："+parm);
		TParm result = INSTJTool.getInstance().DataDown_sp_O(parm);
		// if (!INSTJTool.getInstance().getErrParm(result)) {
		// // System.out.println("对总账失败");
		// return result;
		// }
		return result;
	}

	/**
	 * 城职门特 对总账操作
	 * 
	 * @param insOpdParm
	 * @return
	 */
	private TParm sumOpdChZMtAndChJMt(TParm insOpdParm) {
		TParm parm = new TParm();
		String regionCode = insOpdParm.getValue("REGION_CODE");// 1 HOSP_NHI_NO
		// 医院编码
		String optUser = insOpdParm.getValue("USER_ID");// 2 OPT_USER门特操作员编码
		String ownNo = "";// 3 OWN_NO 个人编码
		String collateAccountTime = insOpdParm.getValue("ACCOUNT_DATE");// 4
		// COLLATE_ACCOUNT_TIME
		TParm specialParm = insOpdParm.getParm("specialParm");// 特殊人员类别金额
		// 对帐时间
		double totalAmt = 0.00;// 5 TOTAL_AMT 发生金额(所有正常)
		double applyAmt = 0.00;// 6 APPLY_AMT 统筹基金申报金额(所有正常)
		double flgAgentAmt = 0.00;// 7 TOTAL_AGENT_AMT 救助基金申报金额(所有正常)
		double ownAmt = 0.00;// 8 OWN_AMT 全自费金额(所有正常)
		double addAmt = 0.00;// 9 ADD_AMT 增负金额(所有正常)
		int sumPertime = 0;// 10 SUM_PERTIME 总人次(所有正常)
		double applyAmtB = 0.00;// 11 APPLY_AMT_B 统筹基金社保支付金额（退费）
		double flgAgentAmtB = 0.00;// 12 TOTAL_AGENT_AMT 医疗救助支付金额（退费）
		int sumPertimeB = 0;// 13 SUM_PERTIME_B 总人次（退费）
		double armyAiAmt = 0.00;// 14 ARMY_AI_AMT 军残补助金额(所有正常) -----
		double armyAiAmtB = 0.00;// 15 ARMY_AI_AMT_B 军残补助金额(所有退费) ------
		double servantAmt = 0.00;// 16 SERVANT_AMT 公务员补助金额(所有正常) ----
		double servantAmtB = 0.00;// 17 SERVANT_AMT_B 公务员补助金额(所有退费) -----
		double accountPayAmt = 0.00;// 18 ACCOUNT_PAY_AMT 个人帐户实际支付金额(所有正常) ----
		double accountPayAmtB = 0.00;// 19 ACCOUNT_PAY_AMT_B 个人帐户实际支付金额(所有退费)
		// ----
		double mzAgentAmt = 0.00;// 20 MZ_AGENT_AMT 民政救助补助金额
		double mzAgentAmtB = 0.00;// 21 MZ_AGENT_AMT_B 民政救助补助金额(退费)
		double fyAgentAmt = 0.00;// 22 FY_AGENT_AMT 优抚对象补助金额 NUMBER
		double fyAgentAmtB = 0.00;// 23 FY_AGENT_AMT_B 优抚对象补助金额(退费)
		double fdAgentAmt = 0.00;// 24 FD_AGENT_AMT 非典后遗症补助金额
		double fdAgentAmtB = 0.00;// 25 FD_AGENT_AMT_B 非典后遗症补助金额退费)
		double unReimAmt = 0.00;// 26 UNREIM_AMT 基金未报销金额
		double unReimAmtB = 0.00;// 27 UNREIM_AMT_B 基金未报销金额（退费）
		double illnessSubsidyAmt = 0.00;// 28 ILLNESS_SUBSIDY_AMT 城乡大病补助金额
		double illnessSubsidyAmtB = 0.00;// 29 ILLNESS_SUBSIDY_AMT_B 城乡大病补助金额（退费）
		double tempAmt = 0.00;// 中间变量
		for (int i = 0; i < insOpdParm.getCount(); i++) {
			tempAmt = insOpdParm.getDouble("ARMY_AI_AMT", i);
			// 退费数据
			if (insOpdParm.getValue("RECP_TYPE", i).equals("REGT")
					|| insOpdParm.getValue("RECP_TYPE", i).equals("OPBT")) {
				// ototOutAmt+=insOpdParm.getDouble("OTOT_AMT",i);
				accountPayAmtB += insOpdParm.getDouble("ACCOUNT_PAY_AMT", i);
				applyAmtB += insOpdParm.getDouble("TOTAL_AGENT_AMT", i);
				// agentAmtOut=+insOpdParm.getDouble("OTOT_AMT");
				flgAgentAmtB += insOpdParm.getDouble("FLG_AGENT_AMT", i);

				// servantAmtB += insOpdParm.getDouble("SERVANT_AMT", i);
				unReimAmtB += insOpdParm.getDouble("UNREIM_AMT", i);
				//城乡大病补助金额（退费）
				illnessSubsidyAmtB += insOpdParm.getDouble("ILLNESS_SUBSIDY_AMT", i);
				sumPertimeB++;

			} else {
				totalAmt += insOpdParm.getDouble("TOT_AMT", i);
				ownAmt += insOpdParm.getDouble("OWN_AMT", i);

				addAmt += insOpdParm.getDouble("ADD_AMT", i);
				// ototAmt+=insOpdParm.getDouble("OTOT_AMT",i);
				flgAgentAmt += insOpdParm.getDouble("FLG_AGENT_AMT", i);
				applyAmt += insOpdParm.getDouble("TOTAL_AGENT_AMT", i);
				accountPayAmt += insOpdParm.getDouble("ACCOUNT_PAY_AMT", i);

				// servantAmt += insOpdParm.getDouble("SERVANT_AMT", i);
				unReimAmt += insOpdParm.getDouble("UNREIM_AMT", i);
				//城乡大病补助金额
				illnessSubsidyAmt +=insOpdParm.getDouble("ILLNESS_SUBSIDY_AMT", i);
				sumPertime++;

			}
		}
		for (int i = 0; i < specialParm.getCount(); i++) {
			tempAmt = specialParm.getDouble("ARMY_AI_AMT", i);
			// 退费数据
			if (specialParm.getValue("RECP_TYPE", i).equals("REGT")
					|| specialParm.getValue("RECP_TYPE", i).equals("OPBT")) {
				String special_pat = "";
				// 退费操作 获得特殊人员编号累计金额
				for (int j = 0; j < specialParm.getCount(); j++) {
					if (specialParm.getValue("CONFIRM_NO", i).equals(
							"*" + specialParm.getValue("CONFIRM_NO", j))) {
						special_pat = specialParm.getValue("SPECIAL_PAT", j);
						break;
					}

				}
				if (null != special_pat && special_pat.length() > 0) {
					if (special_pat.equals("04")) {// 04
						armyAiAmtB += tempAmt;// 伤残军人

					} else if (special_pat.equals("06")) {// 06
						// 公务员
						servantAmtB += tempAmt;
					} else if (special_pat.equals("07")) {// 07
						// 民政救助人员
						mzAgentAmtB += tempAmt;
					} else if (special_pat.equals("08")) {// 08
						// 优抚对象
						fyAgentAmtB += tempAmt;
					} else if (special_pat.equals("09")) {// 09
						// 非典后遗症
						fdAgentAmtB += tempAmt;
					}
				}
			} else {
				if (null != specialParm.getValue("SPECIAL_PAT", i)) {
					if (specialParm.getValue("SPECIAL_PAT", i).equals("04")) {// 04
						armyAiAmt += tempAmt; // 伤残军人

					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"06")) {// 06
						// 公务员
						servantAmt += tempAmt;
					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"07")) {// 07
						// 民政救助人员
						mzAgentAmt += tempAmt;
					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"08")) {// 08
						// 优抚对象
						fyAgentAmt += tempAmt;
					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"09")) {// 09
						// 非典后遗症
						fdAgentAmt += tempAmt;
					}
				}
			}
		}

		parm.setData("REGION_CODE", regionCode);
		parm.setData("OPT_USER", optUser);
		parm.setData("OWN_NO", ownNo);
		parm.setData("ACCOUNT_DATE", collateAccountTime);
		parm.setData("TOTAL_AMT", getDoubleValue(totalAmt));
		parm.setData("APPLY_AMT", getDoubleValue(applyAmt));
		parm.setData("FLG_AGENT_AMT", getDoubleValue(flgAgentAmt));
		parm.setData("OWN_AMT", getDoubleValue(ownAmt));
		parm.setData("ADD_AMT", getDoubleValue(addAmt));
		parm.setData("SUM_PERTIME", sumPertime);
		parm.setData("APPLY_AMT_B", getDoubleValue(Math.abs(applyAmtB)));
		parm.setData("FLG_AGENT_AMT_B", getDoubleValue(Math.abs(flgAgentAmtB)));
		parm.setData("SUM_PERTIME_B", getDoubleValue(Math.abs(sumPertimeB)));
		parm.setData("ARMY_AI_AMT", getDoubleValue(armyAiAmt));
		parm.setData("ARMY_AI_AMT_B", getDoubleValue(Math.abs(armyAiAmtB)));
		parm.setData("SERVANT_AMT", getDoubleValue(servantAmt));
		parm.setData("SERVANT_AMT_B", getDoubleValue(Math.abs(servantAmtB)));
		parm.setData("ACCOUNT_PAY_AMT", getDoubleValue(accountPayAmt));
		parm.setData("ACCOUNT_PAY_AMT_B", getDoubleValue(Math
				.abs(accountPayAmtB)));
		parm.setData("MZ_AGENT_AMT", getDoubleValue(mzAgentAmt));
		parm.setData("MZ_AGENT_AMT_B", getDoubleValue(Math.abs(mzAgentAmtB)));
		parm.setData("FY_AGENT_AMT", getDoubleValue(fyAgentAmt));
		parm.setData("FY_AGENT_AMT_B", getDoubleValue(Math.abs(fyAgentAmtB)));
		parm.setData("FD_AGENT_AMT", getDoubleValue(fdAgentAmt));
		parm.setData("FD_AGENT_AMT_B", getDoubleValue(Math.abs(fdAgentAmtB)));
		parm.setData("UNREIM_AMT", getDoubleValue(unReimAmt));
		parm.setData("UNREIM_AMT_B", getDoubleValue(Math.abs(unReimAmtB)));
		parm.setData("ILLNESS_SUBSIDY_AMT", getDoubleValue(illnessSubsidyAmt));
		parm.setData("ILLNESS_SUBSIDY_AMT_B", getDoubleValue(Math.abs(illnessSubsidyAmtB)));
		TParm result = null;
		if (insOpdParm.getInt("INS_TYPE") == 2) {
			result = INSTJTool.getInstance().DataDown_mts_M(parm);
		} else if (insOpdParm.getInt("INS_TYPE") == 3) {
			result = INSTJTool.getInstance().DataDown_cmts_M(parm);
		}
		// if (!INSTJTool.getInstance().getErrParm(result)) {
		// // System.out.println("对总账失败");
		// return result;
		// }
		return result;

	}

	private double getDoubleValue(double amt) {
		return StringTool.round(amt, 2);
	}

	/**
	 * 城职普通 明细对账
	 * 
	 * @param parm
	 * @return
	 */
	private TParm sumOpdOrderChZPt(TParm parm) {
		TParm result = INSTJTool.getInstance().DataDown_rs_M(parm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			// System.out.println("对明细账失败");
			return result;
		}
		return result;
	}

	/**
	 * 城职门特 明细对账
	 * 
	 * @param parm
	 * @return
	 */
	private TParm sumOpdOrderChZMt(TParm parm) {
		TParm result = INSTJTool.getInstance().DataDown_mtd_E(parm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			// System.out.println("对明细账失败");
			return result;
		}
		return result;
	}

	/**
	 * 城职门特 明细对账
	 * 
	 * @param parm
	 * @return
	 */
	private TParm sumOpdOrderChJMt(TParm parm) {
		TParm result = INSTJTool.getInstance().DataDown_cmtd_E(parm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			// System.out.println("对明细账失败");
			return result;
		}
		return result;
	}

	/**
	 * 获得明细帐下载数据 城职普通
	 * 
	 * @return
	 */
	public TParm getOpdOrderChZPtParm(TParm parm, TParm opdReturnParm,
			TParm mzConfirmParm) {
		TParm chZPtParm = opdOrderCommParm(parm, opdReturnParm, mzConfirmParm);
		chZPtParm.setData("TOTAL_AMT", opdReturnParm.getDouble("TOT_AMT"));// 42
		// TOTAL_AMT
		// 发生金额
		// ··
		chZPtParm.setData("OTOT_AMT", opdReturnParm.getDouble("OTOT_AMT"));// 46
		// OTOT_AMT
		// 专项基金社保支付
		// -----

		// ------
		chZPtParm.setData("HOSP_OPT_USER_CODE", parm.getData("OPT_USER"));// 48
		// HOSP_OPT_USER_CODE
		// 医院操作员编号
		// -----
		return chZPtParm;
	}

	/**
	 * 明细下载 返回数据共用部分
	 * 
	 * @param parm
	 * @return
	 */
	private TParm opdOrderCommParm(TParm parm, TParm opdReturnParm,
			TParm mzConfirmParm) {
		TParm insParm = new TParm();
		insParm.setData("CONFIRM_NO", opdReturnParm.getValue("CONFIRM_NO"));// 8
		// CONFIRM_NO
		// 就诊顺序号
		// ···
		insParm.setData("HOSP_NHI_NO", parm.getValue("HOSP_NHI_NO"));// 9
		// HOSP_NHI_NO
		insParm.setData("NHI_AMT", opdReturnParm.getDouble("NHI_AMT"));// 43
		// NHI_AMT
		// 申报金额
		// -----
		insParm.setData("ARMY_AI_AMT", opdReturnParm.getDouble("ARMY_AI_AMT"));// 47
																				// 补助金额

		insParm.setData("REIM_TYPE", opdReturnParm.getValue("REIM_TYPE"));// 47
																			// 报销类别
		insParm.setData("SPECIAL_PAT", mzConfirmParm.getValue("SPECIAL_PAT"));// 47
																				// 特殊人员

		insParm.setData("ACCOUNT_PAY_AMT", opdReturnParm
				.getDouble("ACCOUNT_PAY_AMT"));// 47 ACCOUNT_PAY_AMT 个人帐户实际支付金额

		insParm.setData("CONTER_DATE", parm.getData("INS_DATE"));// 49// 费用结算时间
		insParm.setData("PAY_KIND", mzConfirmParm.getData("PAY_KIND"));// 50//
																		// PAY_KIND
		// 支付类别---??城职普通
		insParm.setData("INSAMT_FLG", opdReturnParm.getValue("INSAMT_FLG"));// 50
		insParm.setData("OWN_AMT", opdReturnParm.getDouble("OWN_AMT"));// 45自费金额
		insParm.setData("PAT_NAME", mzConfirmParm.getValue("PAT_NAME"));// 14
																		// NAME
		// 姓名
		// ----
		insParm.setData("SEX_CODE", mzConfirmParm.getValue("SEX_CODE"));// 15
																		// SEX
		// OWN_AMT
		// 自费项目金额
		// ···
		if (parm.getValue("RECP_TYPE").equals("REGT")
				|| parm.getValue("RECP_TYPE").equals("OPBT")) {
			insParm.setData("PAY_BACK_FLG", 1);// 51 PAY_BACK_FLG 退费标志 ··· 0/1 0
			// 正常 1 退费
		} else {
			insParm.setData("PAY_BACK_FLG", 0);// 51 PAY_BACK_FLG 退费标志 ··· 0/1 0
			// 正常 1 退费
		}
		insParm.setData("PAT_TYPE", mzConfirmParm.getValue("CTZ_CODE"));// 18
		// CTZ_CODE
		// 人员类别
		// ---11/21/51
		// 11
		// 在职
		// 21
		// 退休
		// 51
		// 建国前老工人
		insParm.setData("UNIT_DESC", mzConfirmParm.getValue("UNIT_DESC"));// 21
		// UNIT_DESC
		// 单位名称
		// ----

		insParm.setData("ADD_AMT", opdReturnParm.getDouble("ADD_AMT"));// 45
		// ADDPAY_AMT
		// 增负金额
		// ------
		return insParm;
	}

	/**
	 * 获得明细帐下载数据
	 * 
	 * 城居门特
	 * 
	 * @return
	 */
	public TParm getOpdOrderChJMtParm(TParm parm, TParm opdReturnParm,
			TParm mzConfirmParm) {
		TParm chJMtParm = opdOrderCommParm(parm, opdReturnParm, mzConfirmParm);
		chJMtParm.setData("DISEASE_CODE", mzConfirmParm
				.getValue("DISEASE_CODE"));// 7 DISEASE_CODE 门特类别 1 肾透析 2 肾移植抗排异
		// 3 癌症放化疗 21 血友病 22 肝移植术后抗排异治疗 4
		// 糖尿病 5 肺心病 6 红斑狼疮 7 精神病 8 偏瘫
		chJMtParm.setData("SUM_BATCH", "SUM_BATCH");// 36汇总批号
		// 医疗救助最高限额以上金额 =====
		chJMtParm.setData("BCSSQF_STANDRD_AMT", opdReturnParm
				.getDouble("BCSSQF_STANDRD_AMT"));// 本次实收起付标准金额
		// ======
		chJMtParm.setData("INS_STANDARD_AMT", opdReturnParm
				.getDouble("INS_STANDARD_AMT"));// 起付标准以上自负比例金额
		// =====
		chJMtParm.setData("OPT_USER", opdReturnParm.getValue("OPT_USER"));// 门特操作员
		chJMtParm.setData("TRANBLOOD_OWN_AMT", opdReturnParm
				.getDouble("TRANBLOOD_OWN_AMT"));// 医疗救助最高限额以上金额
		chJMtParm.setData("PERCOPAYMENT_RATE_AMT", opdReturnParm
				.getDouble("PERCOPAYMENT_RATE_AMT"));// 医疗救助个人按比例负担金额
		chJMtParm.setData("INS_HIGHLIMIT_AMT", opdReturnParm
				.getDouble("INS_HIGHLIMIT_AMT"));// 医疗救助最高限额以上金额
		chJMtParm.setData("TOTAL_AGENT_AMT", opdReturnParm
				.getDouble("TOTAL_AGENT_AMT"));// 统筹基金支付医院申请
		return chJMtParm;
	}

	/**
	 * 获得明细帐下载数据 城职门特
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getOpdOrderChZMtParm(TParm parm, TParm opdReturnParm,
			TParm mzConfirmParm) {
		TParm chJMtParm = getOpdOrderChJMtParm(parm, opdReturnParm,
				mzConfirmParm);
		chJMtParm.setData("ARMY_AI_AMT1", opdReturnParm
				.getDouble("ARMY_AI_AMT1"));// 补助金额1
		return chJMtParm;
	}
}
