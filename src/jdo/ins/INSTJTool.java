package jdo.ins;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import jdo.sys.SystemTool;
import jdo.ins.InsManager;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 天津医保流程方法
 * </p>
 * 
 * <p>
 * Description: 天津医保流程方法
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: bluecore 2011-12-23
 * </p>
 * 
 * @author pangben 2011-11-23
 * @version 1.0
 */
public class INSTJTool extends TJDOTool {
	// private TParm ruleParm;// 三目字典
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	// double[] sum = { 3, 3.5, 3, 3.5 };
	/**
	 * 实例
	 */
	public static INSTJTool instanceObject;
	// 添加数据操作校验是否为空
	private String[] insOpd = { "REGION_CODE", "CASE_NO", "CONFIRM_NO",
			"MR_NO", "PHA_AMT", "PHA_NHI_AMT", "EXM_AMT", "EXM_NHI_AMT",
			"TREAT_AMT", "TREAT_NHI_AMT", "OP_AMT", "OP_NHI_AMT", "BED_AMT",
			"BED_NHI_AMT", "MATERIAL_AMT", "MATERIAL_NHI_AMT", "OTHER_AMT",
			"OTHER_NHI_AMT", "BLOODALL_AMT", "BLOODALL_NHI_AMT", "BLOOD_AMT",
			"BLOOD_NHI_AMT", "TOT_AMT", "NHI_AMT", "START_STANDARD_AMTS",
			"OTOT_PAY_AMT", "START_STANDARD_AMT", "ADD_AMT", "OWN_AMT",
			"INS_STANDARD_AMT", "TRANBLOOD_OWN_AMT", "APPLY_AMT", "OTOT_AMT",
			"OINSTOT_AMT", "INS_DATE", "INSAMT_FLG", "OPT_USER", "OPT_DATE",
			"OPT_TERM", "ACCOUNT_PAY_AMT", "UNACCOUNT_PAY_AMT",
			"ACCOUNT_LEFT_AMT", "REFUSE_AMT", "REFUSE_ACCOUNT_PAY_AMT",
			"REFUSE_DESC", "CONFIRM_F_USER", "CONFIRM_F_DATE",
			"CONFIRM_S_USER", "CONFIRM_S_DATE", "SLOW_DATE", "SLOW_PAY_DATE",
			"REFUSE_CODE", "REFUSE_OTOT_AMT", "MZ_DIAG", "DRUG_DAYS",
			"INS_STD_AMT", "INS_CROWD_TYPE", "INS_PAT_TYPE", "OWEN_PAY_SCALE",
			"REDUCE_PAY_SCALE", "REAL_OWEN_PAY_SCALE", "SALVA_PAY_SCALE",
			"BASEMED_BALANCE", "INS_BALANCE", "ISSUE", "BCSSQF_STANDRD_AMT",
			"PERCOPAYMENT_RATE_AMT", "INS_HIGHLIMIT_AMT", "TOTAL_AGENT_AMT",
			"FLG_AGENT_AMT", "APPLY_AMT_R", "FLG_AGENT_AMT_R", "REFUSE_DATE",
			"REAL_PAY_LEVEL", "VIOLATION_REASON_CODE", "ARMY_AI_AMT",
			"SERVANT_AMT", "INS_PAY_AMT", "UNREIM_AMT", "REIM_TYPE",
			"RECP_TYPE", "INV_NO" };

	/**
	 * 得到实例
	 * 
	 * @return INSTJTool
	 */
	public static INSTJTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSTJTool();
		return instanceObject;
	}

	/**
	 * 执行共用的费用分割、添加ins_opd_order 表、明细上传操作
	 * 
	 * @param parm
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param type
	 *            int
	 * @param insOrderType
	 *            int
	 * @return TParm
	 */
	public TParm comminuteFeeAndInsOrder(TParm parm, TParm tempParm, int type,
			int insOrderType) {
		TParm REG_PARM = parm.getParm("REG_PARM"); // 获得挂号费用相关数据
		String regionCode = parm.getValue("REGION_CODE");
		// 门诊医疗卡收费注记----扣款打票时使用用来操作 收据表 BIL_OPB_RECP 医保金额修改
		String opbEktFeeFlg = parm.getValue("OPBEKTFEE_FLG");
		tempParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		String printNo = parm.getValue("PRINT_NO"); // 票据号
		TParm comminuteFeeParm = new TParm(); // 费用分割返回参数
		TParm interFaceParm = null; // 接口返回数据
		TParm result = new TParm();
		// 获得最大号，防止主键冲突
		// TParm SeqParm = parm.getParm("opbReadCardParm");
		tempParm.setData("RECP_TYPE", parm.getValue("RECP_TYPE")); // 操作类型 ：REG
		TParm maxSeqOpdOrderParm = INSOpdOrderTJTool.getInstance()
				.selectMAXSeqNo(tempParm);
		tempParm.setData("TYPE", type); // 1.城职 2.城居
		tempParm.setData("EREG_FLG", parm.getValue("EREG_FLG")); // 门急诊 0.普通
		tempParm.setData("ADM_DATE", parm.getValue("ADM_DATE")); //就诊日期
//		System.out.println("tempParm=====:"+tempParm);
		// 1.急诊
		int maxSeq = 0; // 最大号码
		if (null != maxSeqOpdOrderParm.getValue("SEQ_NO", 0)
				&& maxSeqOpdOrderParm.getInt("SEQ_NO", 0) > 0) {
			maxSeq = maxSeqOpdOrderParm.getInt("SEQ_NO", 0);
		}
		//
		result = INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(parm);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 遍历医嘱
		for (int j = 0; j < REG_PARM.getCount("ORDER_CODE"); j++) {
			// 费用分割方法添加
			if (type == 1) // 城职
				interFaceParm = DataDown_sp1_B(REG_PARM, tempParm, regionCode,
						j);
			else
				// 城居
				interFaceParm = DataDown_sp1_G(REG_PARM, tempParm, regionCode,
						j);
			if (!getErrParm(interFaceParm)) {
				return interFaceParm;
			}
			interFaceParm.setData("ORDER_CODE", REG_PARM.getValue("ORDER_CODE",
					j)); // 需要通过ORDER_CODE 比较金额
			if (null != REG_PARM.getValue("RECEIPT_TYPE", j)) { // 挂号使用判断费用类型
				interFaceParm.setData("RECEIPT_TYPE", REG_PARM.getValue(
						"RECEIPT_TYPE", j));
			}
			// comminuteFeeParm.addRowData(interFaceParm,j);
			// 费用分割数据保存
//			 System.out.println("comminuteFeeParm:" + comminuteFeeParm);
			// 添加INS_OPD_ORDER 表数据DataDown_sp1
			comminuteFeeParm(comminuteFeeParm, interFaceParm, j, opbEktFeeFlg);
			result = insOrderTemp(parm, REG_PARM, interFaceParm, maxSeq, j,
					printNo);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				// connection.close();
				return result;
			}
		}
		return comminuteFeeParm; // 费用分割回参
	}

	/**
	 * 执行累计增负操作
	 * 
	 * @param parm
	 *            TParm
	 * @param addParm
	 *            TParm
	 * @param result
	 *            TParm
	 * @param seqParm
	 *            TParm
	 * @return TParm
	 */
	public TParm exeAdd(TParm parm, TParm addParm, TParm result, TParm seqParm) {
		TParm tempParm = new TParm();
		tempParm.setData("CASE_NO", parm.getValue("CASE_NO")); // 就诊号
		tempParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 资格确认书号
		tempParm.setData("REGION_CODE", parm.getValue("NEW_REGION_CODE"));
		tempParm.setData("SEQ_NO", seqParm.getInt("SEQ_NO", 0) + 1);
		tempParm.setData("CHARGE_DATE", SystemTool.getInstance().getDate()); // 费用时间
		tempParm.setData("NHI_ORDER_CODE", "***018"); // 医保代码
		tempParm.setData("ORDER_CODE", "***018"); // 医嘱代码
		tempParm.setData("ORDER_DESC", "累计增负"); // 名称
		tempParm.setData("OWN_RATE", 1); // 自负标志
		tempParm.setData("PRICE", 0); // 单价
		tempParm.setData("QTY", 0); // 数量
		tempParm.setData("TOTAL_AMT", result.getDouble("NHI_AMT")
				+ result.getDouble("ADDPAY_AMT")); // 发生金额
		tempParm.setData("TOTAL_NHI_AMT", result.getDouble("NHI_AMT")); // 申报金额
		tempParm.setData("ADDPAY_AMT", result.getDouble("ADDPAY_AMT")); // 累计增负金额
		tempParm.setData("ADDPAY_FLG", "Y"); // 累计增负 标志
		tempParm.setData("NHI_ORD_CLASS_CODE", "06"); // 统计代码
		tempParm.setData("INV_NO", seqParm.getValue("INV_NO", 0)); // 票号
		tempParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		tempParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		tempParm.setData("RECP_TYPE", parm.getValue("RECP_TYPE"));
		TParm resultParm = INSOpdOrderTJTool.getInstance()
				.deleteAddINSOpdOrder(tempParm);
		if (resultParm.getErrCode() < 0) {
			return resultParm;
		}
		resultParm = INSOpdOrderTJTool.getInstance()
				.insertAddOpdOpder(tempParm);
		if (resultParm.getErrCode() < 0) {
			return resultParm;
		}
		return resultParm;
	}

	/**
	 * 添加INS_OPD数据(费用分割数据;A 方法 出参和界面数据;L 方法出参 获得CONFIRM_NO)
	 * 
	 * @param parm
	 *            TParm
	 * @param insParm
	 *            TParm
	 * @return boolean
	 */
	public boolean insterInsOpdParm(TParm parm, TParm insParm) {
		TParm result = new TParm();
		double ownAmt = 0.00; // 自费金额
		double addAmt = 0.00; // 增负金额
		double applyAmt = 0.00; // 申报金额
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			ownAmt += parm.getDouble("OWN_AMT"); // 自费金额
			addAmt += parm.getDouble("ADD_AMT"); // 增负金额
			applyAmt += parm.getDouble("APPLY_AMT"); // 申报金额
		}
		result.setData("REGION_CODE", insParm.getValue("NEW_REGION_CODE")); // 医保区域代码
		result.setData("CASE_NO", insParm.getValue("CASE_NO")); // 就诊号码
		result.setData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 医保区域代码
		result.setData("MR_NO", insParm.getValue("MR_NO")); // 病案号
		result.setData("OWN_AMT", ownAmt);
		result.setData("ADD_AMT", addAmt);
		result.setData("APPLY_AMT", applyAmt);
		result.setData("INS_CROWD_TYPE", insParm.getValue("CROWD_TYPE")); // 医保卡类型
		result.setData("INS_PAT_TYPE", insParm.getValue("INS_PAT_TYPE")); // 就诊类型
		result.setData("INSAMT_FLG", 0); // 36社保对帐标志INS_OPD中INSAMT_FLG对账标志为0
		result.setData("RECP_TYPE", insParm.getValue("RECP_TYPE")); // 类型
		result.setData("OPT_USER", insParm.getValue("OPT_USER"));
		result.setData("OPT_TERM", insParm.getValue("OPT_TERM"));
		// result.setData("INV_NO", insParm.getValue("PRINT_NO")); // 票号
		// result.setData("SEQ_NO", maxSeq + 1); // 序号
		// ：REG/OPB
		result = INSOpdTJTool.getInstance().insertINSOpd(result);
		if (result.getErrCode() < 0) {
			// System.out.println("出错");
			return false;
		}
		return true;
	}

	/**
	 * 费用分割数据保存
	 * 
	 * @param comminuteFeeParm
	 *            TParm
	 * @param insOrderParm
	 *            TParm
	 * @param i
	 *            int
	 * @param opbEktFeeFlg
	 *            String
	 */
	private void comminuteFeeParm(TParm comminuteFeeParm, TParm insOrderParm,
			int i, String opbEktFeeFlg) {
		comminuteFeeParm.addData("OWN_AMT", StringTool.round(insOrderParm
				.getDouble("OWN_AMT"), 2)); // 全自费金额(ALL_OWN_ATM)
		comminuteFeeParm.addData("ADD_AMT", StringTool.round(insOrderParm
				.getDouble("ADDPAY_AMT"), 2)); // 增负金额(ADDN_AMT)
		comminuteFeeParm.addData("APPLY_AMT", StringTool.round(insOrderParm
				.getDouble("NHI_AMT"), 2)); // 申报金额(APPLY_AMT)
		comminuteFeeParm.addData("OWN_RATE", insOrderParm.getValue("OWN_RATE")); // 自负比例（OWN_RATE）
		comminuteFeeParm.addData("ADDPAY_FLG", insOrderParm
				.getInt("ADDPAY_FLG")); // 累计增负标志(GRAND_ADDN_FLG)
		comminuteFeeParm.addData("NHI_ORD_CLASS_CODE", insOrderParm
				.getValue("NHI_ORD_CLASS_CODE")); // 统计代码(STATS_CODE)
		comminuteFeeParm.addData("ORDER_CODE", insOrderParm
				.getValue("ORDER_CODE")); // 医嘱代码
		comminuteFeeParm.addData("RECEIPT_TYPE", insOrderParm
				.getValue("RECEIPT_TYPE")); // 类型，挂号使用
	}

	/**
	 * 城职费用分割返回数据:函数DataDown_sp1（B）方法
	 * 
	 * @param REG_PARM
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param regionCode
	 *            String
	 * @param j
	 *            int
	 * @return TParm
	 */
	public TParm DataDown_sp1_B(TParm REG_PARM, TParm tempParm,
			String regionCode, int j) {
		TParm parm = DataDown_sp1_B_OR_G(REG_PARM, tempParm, regionCode, j);
		parm.addData("PARM_COUNT", 9);
		TParm result = commInterFace("DataDown_sp1", "B", parm);
		// System.out.println("城职费用分割返回数据:::::::" + result);
		return result;
	}

	/**
	 * 校验医嘱代码
	 * 
	 * @param tempParm
	 *            TParm
	 * @param REG_PARM
	 *            TParm
	 * @param i
	 *            int
	 * @param insOrderParm
	 *            TParm
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	private void getParmValue(TParm tempParm, TParm REG_PARM, int i,
			TParm insOrderParm, boolean flg) {
		//TParm orderParm = new TParm();
		if (flg) {
			if (null != tempParm.getValue("EREG_FLG")
					&& tempParm.getInt("EREG_FLG") == 0) { // 急诊
				insOrderParm.setData("NHI_ORDER_CODE", REG_PARM.getValue(
						"NHI_CODE_O", i)); // 5
				//orderParm = checkCode(REG_PARM.getValue("NHI_CODE_O", i));
			} else if (null != tempParm.getValue("EREG_FLG")
					&& tempParm.getInt("EREG_FLG") == 1) { // 急诊
				insOrderParm.setData("NHI_ORDER_CODE", REG_PARM.getValue(
						"NHI_CODE_E", i)); // 5
				//orderParm = checkCode(REG_PARM.getValue("NHI_CODE_E", i));
			}
		} else {
			if (null != tempParm.getValue("EREG_FLG")
					&& tempParm.getInt("EREG_FLG") == 0) { // 门诊
				insOrderParm.addData("NHI_ORDER_CODE", REG_PARM.getValue(
						"NHI_CODE_O", i)); // 5
			//	orderParm = checkCode(REG_PARM.getValue("NHI_CODE_O", i));
			} else if (null != tempParm.getValue("EREG_FLG")
					&& tempParm.getInt("EREG_FLG") == 1) { // 急诊
				insOrderParm.addData("NHI_ORDER_CODE", REG_PARM.getValue(
						"NHI_CODE_E", i)); // 5
			//	orderParm = checkCode(REG_PARM.getValue("NHI_CODE_E", i));
			}
		}
	}

	/**
	 * 增负药品标志赋值
	 * 
	 * @param tempParm
	 *            TParm
	 * @param insOrderParm
	 *            TParm
	 * @param regParm
	 *            TParm
	 * @param i
	 *            int
	 * @param flg
	 *            boolean
	 */
	private void getPhaAdd(TParm tempParm, TParm insOrderParm, TParm regParm,
			int i, boolean flg) {
		if (flg) {
			//TParm orderParm = checkCode(insOrderParm.getValue("NHI_ORDER_CODE"));
			if (null != tempParm.getValue("RECP_TYPE")
					&& tempParm.getValue("RECP_TYPE").equals("REG")) {
				insOrderParm.setData("PHAADD_FLG", 0); // 6 增负药品标志

			} else if (null != tempParm.getValue("RECP_TYPE")
					&& tempParm.getValue("RECP_TYPE").equals("OPB")) {
				insOrderParm.setData("PHAADD_FLG", null != regParm
						.getValue("ZFBL1")
						&& regParm.getDouble("ZFBL1") > 0 ? "1" : "0"); // 6
				// 增负药品标志-
			}
		} else {
//			TParm orderParm = checkCode(insOrderParm.getValue("NHI_ORDER_CODE",
//					i));
			if (null != tempParm.getValue("RECP_TYPE")
					&& tempParm.getValue("RECP_TYPE").equals("REG")) {
				insOrderParm.addData("PHAADD_FLG", 0); // 6 增负药品标志

			} else if (null != tempParm.getValue("RECP_TYPE")
					&& tempParm.getValue("RECP_TYPE").equals("OPB")) {
				insOrderParm.addData("PHAADD_FLG", null != regParm
						.getValue("ZFBL1")
						&& regParm.getDouble("ZFBL1") > 0 ? "1" : "0"); // 6
				// 增负药品标志----
			}
		}

	}

	/**
	 * 费用分割返回数据共用方法(三目字典;A 方法中的医嘱信息;L OR E 方法出参)
	 * 
	 * @param REG_PARM
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param regionCode
	 *            String
	 * @param i
	 *            int
	 * @return TParm
	 */
	private TParm DataDown_sp1_B_OR_G(TParm REG_PARM, TParm tempParm,
			String regionCode, int i) {
		TParm insOrderParm = new TParm();
		// 调用借口G方法执行费用分割

		 System.out.println("REG_PARMREG_PARMREG_PARM::::::::" + REG_PARM);
		if (null != tempParm.getValue("EREG_FLG")
				&& tempParm.getInt("EREG_FLG") == 1) { // 急诊
			insOrderParm.addData("FULL_OWN_FLG", null != REG_PARM.getValue(
					"NHI_CODE_E", i) ? "0" : "1"); // 7 全自费标志
			insOrderParm.addData("NHI_ORDER_CODE", REG_PARM.getValue(
					"NHI_CODE_E", i)); // 1收费项目编码
		} else if (null != tempParm.getValue("EREG_FLG")
				&& tempParm.getInt("EREG_FLG") == 0) { // 门诊
			insOrderParm.addData("NHI_ORDER_CODE", REG_PARM.getValue(
					"NHI_CODE_O", i)); // 1收费项目编码
			insOrderParm.addData("FULL_OWN_FLG", null != REG_PARM.getValue(
					"NHI_CODE_O", i) ? "0" : "1"); // 7 全自费标志
		}
		 System.out.println("insOrderParm:::::"+insOrderParm);
		getPhaAdd(tempParm, insOrderParm, REG_PARM, i, false);
		if (null != tempParm.getValue("TYPE") && tempParm.getInt("TYPE") == 1) { // 城职
			insOrderParm.addData("CTZ1_CODE", tempParm.getValue("CTZ_CODE")); // 2人员类别
		} else if (null != tempParm.getValue("TYPE")
				&& tempParm.getInt("TYPE") == 2) { // 城居
			insOrderParm.addData("CTZ1_CODE", tempParm.getValue("PAT_TYPE")); // 2人员类别
		}
		// L方法的CTZ_CODE
		// 人员类别
		// A方法
		// 数量 挂号设置:1
		insOrderParm.addData("QTY", REG_PARM.getDouble("DOSAGE_QTY", i)); // 3
		insOrderParm.addData("TOTAL_AMT", REG_PARM.getDouble("AR_AMT", i)); // 4
																			// 发生金额-
		insOrderParm.addData("TIPTOP_BED_AMT", StringTool.round(tempParm
				.getDouble("BED_FEE"), 3)); // 5

		// 最高床位费
		// SYS_REGION
		// 中获得

		insOrderParm.addData("HOSP_NHI_NO", regionCode); // 8 医院编码
		insOrderParm.addData("CHARGE_DATE", tempParm.getValue("ADM_DATE")); // 9
		 System.out.println("费用分割后返回:DataDown_sp1_B_OR_D:" + insOrderParm);
		return insOrderParm;
	}

	/**
	 * 城居费用分割返回数据:函数DataDown_sp1（G）方法
	 * 
	 * @param REG_PARM
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param regionCode
	 *            String
	 * @param i
	 *            int
	 * @return TParm
	 */
	public TParm DataDown_sp1_G(TParm REG_PARM, TParm tempParm,
			String regionCode, int i) {
		TParm parm = DataDown_sp1_B_OR_G(REG_PARM, tempParm, regionCode, i);
		parm.addData("PARM_COUNT", 9);
		TParm result = commInterFace("DataDown_sp1", "G", parm);
		return result;
	}

	/**
	 * 身份识别：DataDown_sp门诊刷卡（L） 城职普通
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_L(TParm insParm) {

		// TParm result = new TParm();
		TParm parmL = new TParm();
		// parm.addData("TEXT", text);
		// System.out.println("-------insParm--------" + insParm);
		parmL.addData("CARD_NO", insParm.getValue("PERSONAL_NO")); // 卡号（）A 方法出参
		parmL.addData("PASSWORD", insParm.getValue("PASSWORD")); // 密码
		parmL.addData("HOSP_NHI_CODE", insParm.getValue("ADVANCE_CODE"));//医院编码@费用发生时间@类别
		parmL.addData("COM_CODE", null); // 社区编码
		parmL.addData("CHECK_CODES", insParm.getValue("CHECK_CODES")); // 刷卡验证码
		parmL.addData("PARM_COUNT", 5); // 参数数量
		TParm result = commInterFace("DataDown_sp", "L", parmL); // 出参21
		return result;
	}

	/**
	 * 结算明细生成：函数DataDown_sp, 门诊结算上传交易（M）方法
	 * 
	 * @param insParm
	 *            TParm
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_sp_M(TParm insParm, String type) {
		TParm parm = new TParm();
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1门诊顺序号
		// L方法出参
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2 医院编码
		parm.addData("ODO_FLG", 1); // 3 门诊结算标志写死
		// TParm opbReadCardParm = insParm.getParm("opbReadCardParm");// L方法出参
		parm.addData("PAY_TYPE", "11"); // 4
		// 支付类别:11门诊、21住院、
		// 31药店
		TParm diagRecparm = insParm.getParm("diagRecparm"); // 诊断名称
		TParm deptParm = insParm.getParm("deptParm"); // 医保科室代码
		if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("REG")) {
			parm.addData("ODO_DIAG", "挂号"); // 5
			// parm.addData("DOCTOR_SID", "挂号");// 7医生代码
		} else if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("OPB")) {
			StringBuffer diagString = new StringBuffer();
			for (int i = 0; i < diagRecparm.getCount(); i++) {
				diagString.append(diagRecparm.getValue("ICD_CODE", i)
						+ diagRecparm.getValue("ICD_CHN_DESC", i));
			}
			parm.addData("ODO_DIAG", diagString.toString()); // 5
		}
		if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("REG")) {
			parm.addData("DOCTOR_SID", "挂号"); // 7医生代码
			// 门诊诊断需要和收费区分
			parm.addData("DRUG_DAYS", 1); // 6 用药天数
		} else if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("OPB")) {
			parm.addData("DOCTOR_SID", deptParm.getValue("DR_QUALIFY_CODE")); // 7医生代码
			// 门诊诊断需要和收费区分
			parm.addData("DRUG_DAYS", insParm.getInt("TAKE_DAYS")); // 6 用药天数
		}
		// 医师编码----没有这个参数*****************医师身份证号码
		parm.addData("OPT_USER_CODE", insParm.getValue("OPT_USER")); // 8 操作员编号
		// parm.addData("OPT_USER_CODE", "000484"); // 8 操作员编号
		parm.addData("REG_FLG", type); // 9 挂号标志:1 挂号0 非挂号
		parm.addData("DEPT_CODE", deptParm.getValue("INS_DEPT_CODE")); // 10科别代码
		parm.addData("COMMUNITY_NO", null); // 11 社区编码
		parm.addData("ADM_TYPE", insParm.getValue("EREG_FLG")); //12 是否急诊:1急诊0普通
		//===============pangben 2012-4-13 添加处方时间
		parm.addData("RX_DATE",df.format(SystemTool.getInstance().getDate())); // 13 处方时间		
		//===============pangben 2012-4-13 stop
		if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("REG")) {
			parm.addData("OTHER_DIAGE_CODE", ""); //14诊断编码
		} else if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("OPB")) {
			String otherdiagecode = "";
			for (int i = 0; i < diagRecparm.getCount(); i++) {
				otherdiagecode +=diagRecparm.getValue("ICD_CODE", i)+"@";
			}
			parm.addData("OTHER_DIAGE_CODE", otherdiagecode.length()>0? 
					otherdiagecode.substring(0, otherdiagecode.length() - 1):"");//14诊断编码
		}
		parm.addData("PARM_COUNT", 14);
		TParm result = commInterFace("DataDown_sp", "M", parm);
		result.setData("diagRecparm", diagRecparm.getData());
		// System.out.println("结算明细生成settlementDetailsParm::" + result);
		return result;
	}

	/**
	 * 城职门特结算明细生成： 门特结算上传交易（F）方法
	 * 
	 * @param insParm
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_mts_F(TParm insParm, TParm tempParm, String type) {
		TParm parm = DataDown_mtsORcmts_F(insParm, tempParm, type);
		TParm result = commInterFace("DataDown_mts", "F", parm);
		return result;
	}

	/**
	 * 城职\城居门特结算明细生成
	 * 
	 * @param insParm
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param type
	 *            String
	 * @return TParm
	 */
	private TParm DataDown_mtsORcmts_F(TParm insParm, TParm tempParm,
			String type) {
		TParm parm = new TParm();
		// System.out.println("insParm::::::::"+insParm);
		parm.addData("CONFIRM_NO", tempParm.getValue("CONFIRM_NO")); // 1门诊顺序号 
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2医院编码
		parm.addData("PAT_TYPE", tempParm.getValue("CTZ_CODE")); // 3人员类别：11在职21退休51建国前老工人
		parm.addData("PAY_KIND", tempParm.getValue("PAY_KIND")); // 4支付类别
		// 支付类别:11门诊、药店21住院//支付类别12
		// Ⅰ类门诊特殊病13 Ⅱ类门诊特殊病
		// 门特类别:1肾透析2肾移植抗排异3癌症放化疗4 糖尿病5肺心病6红斑狼疮7精神病8偏瘫9癫痫10 再生障碍性贫血11
		// 慢性血小板减少性紫癫21血友病22肝移植术后抗排异治疗
		TParm diagRecparm = insParm.getParm("diagRecparm");
		TParm deptParm = insParm.getParm("deptParm"); // 医保科室代码
		// 查询医保门特诊断、 科别代码 、医师代码
		if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("REG")) {
			parm.addData("DIAG_CODE", "挂号"); // 6门特诊断
			parm.addData("DR_CODE", "挂号"); // 9门特医师编码(或门特药师编码)

		} else if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("OPB")) {

			StringBuffer diagString = new StringBuffer();
			for (int i = 0; i < diagRecparm.getCount(); i++) {
				diagString.append(diagRecparm.getValue("ICD_CODE", i)
						+ diagRecparm.getValue("ICD_CHN_DESC", i));
			}
			parm.addData("DIAG_CODE", diagString.toString()); //6门特诊断
			parm.addData("DR_CODE", deptParm.getValue("DR_QUALIFY_CODE")); //9门特医师编码(或门特药师编码)
		}

		parm.addData("DISEASE_CODE", insParm.getValue("DISEASE_CODE")); // 5门特类别
		parm.addData("DRUG_DAYS", insParm.getInt("TAKE_DAYS")); // 7用药天数
		parm.addData("DEPT_CODE", deptParm.getValue("INS_DEPT_CODE")); // 8科别代码
		parm.addData("OPT_USER", insParm.getValue("OPT_USER")); // 10操作员编号
		parm.addData("REG_FLG", type); // 11 挂号标志:1 挂号0 非挂号
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 12
		// 处方医院:上传处方医院编码，中心有编码的按照中心医院编码上传，中心没有的传处方医院名称(汉字)
		parm.addData("COMMUNITY_NO", null); // 13 社区编码
		parm.addData("RX_DATE", df.format(SystemTool.getInstance().getDate())); // 14 处方时间
		parm.addData("IS_EMG",insParm.getValue("EREG_FLG"));//15是否急诊:1急诊,0普通
		if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("REG")) {
			parm.addData("OTHER_DIAGE_CODE", ""); //16诊断编码
		} else if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("OPB")) {
			String otherdiagecode = "";
			for (int i = 0; i < diagRecparm.getCount(); i++) {
				otherdiagecode +=diagRecparm.getValue("ICD_CODE", i)+"@";
			}
			parm.addData("OTHER_DIAGE_CODE", otherdiagecode.length()>0? 
					otherdiagecode.substring(0, otherdiagecode.length() - 1):"");//16诊断编码
		}
		parm.addData("PARM_COUNT", 16);
		// System.out.println("DataDown_mtsORcmts_F：：：：：parm："+parm);
		return parm;
	}

	/**
	 * 明细上传参数:公共方法
	 * 
	 * @param insParm
	 *            TParm
	 * @param result
	 *            TParm
	 * @param nhiCode
	 *            String
	 * @return boolean
	 */
	public boolean upInterfaceINSOrderParm(TParm insParm, TParm result,
			String nhiCode) {
		// 查询医保数据
		//TParm orderParm = checkCode(insParm.getValue("NHI_ORDER_CODE"));
//		if (orderParm.getErrCode() < 0) {
//			return false;
//		}
		result.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1就医顺序号：L方法出参或E方法出参
		// 方法中获得的参数
		result.addData("CHARGE_DATE", df1.format(insParm.getTimestamp("CHARGE_DATE"))); // 2 明细输入时间
		result.addData("SEQ_NO", insParm.getValue("SEQ_NO")); // 3 序号
		result.addData("HOSP_NHI_NO", nhiCode); // 4医院编码
		result.addData("NHI_ORDER_CODE", insParm.getValue("NHI_ORDER_CODE")); // 5获得医保三目字典数据
		result.addData("PHAADD_FLG", null != insParm.getValue("PHAADD_FLG")
				&& insParm.getValue("PHAADD_FLG").equals("Y") ? "1" : "0"); // 19
		result.addData("USAGE", insParm.getValue("ROUTE")); // 21 用法---医嘱获得
		result.addData("DOSAGE", insParm.getValue("TAKE_QTY")); // 20 用量----医嘱获得
		result.addData("TAKE_DAYS", insParm.getValue("TAKE_DAYS")); // 23 用药天数

		// 收费项目编码

		result.addData("ORDER_DESC", insParm.getValue("ORDER_DESC")); // 6医院服务项目名称
		result.addData("OWN_RATE", insParm.getValue("OWN_RATE")); // 7
		// result.addData("OWN_RATE", 1.00); // 7
		// 自负比例:三目字典自负比例
		result.addData("DOSE_CODE", insParm.getValue("DOSE_CODE")); // 8 剂型
		result.addData("STANDARD", insParm.getValue("STANDARD")); // 9 规格
		result.addData("PRICE", insParm.getDouble("PRICE")); // 10
		// 单价:服务费用价格
		result.addData("QTY", insParm.getDouble("QTY")); // 11 数量
		result.addData("TOTAL_AMT", StringTool.round(insParm
				.getDouble("TOTAL_AMT"), 2)); // 12
		// 发生金额
		result.addData("TOTAL_NHI_AMT", StringTool.round(insParm
				.getDouble("TOTAL_NHI_AMT"), 2)); // 13
		// 申报金额
		result.addData("OWN_AMT", StringTool.round(
				insParm.getDouble("OWN_AMT"), 2)); // 14
		// 全自费金额
		result.addData("ADDPAY_AMT", StringTool.round(insParm
				.getDouble("ADDPAY_AMT"), 2)); // 15增负金额
		result.addData("OP_FLG", "0"); // 16 手术费用标志:1是、0否
		result.addData("ADDPAY_FLG", null != insParm.getValue("ADDPAY_FLG")
				&& insParm.getValue("ADDPAY_FLG").equals("Y") ? "1" : "0"); // 17累计增负标志
		result.addData("NHI_ORD_CLASS_CODE", insParm
				.getValue("NHI_ORD_CLASS_CODE")); // 18 统计代码

		result.addData("CONFIRM_ID", insParm.getValue("HYGIENE_TRADE_CODE")); // 22
		result.addData("SPECIAL_CASE_DESC", insParm
				.getValue("SPECIAL_CASE_DESC")); // 特殊情况说明--医生备注
		// 批准文号:需要添加

		result.addData("INV_NO", insParm.getValue("INV_NO")); // 24 医保专用票据号
		System.out.println("明细上传:" + result);
		return true;
	}

	/**
	 * 城职普通明细上传参数:函数DataUpload,（B）方法
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataUpload_B(TParm insParm) {
		insParm.addData("PARM_COUNT", 24); // 参数数量
		TParm result = commInterFace("DataUpload", "B", insParm);
		return result;
	}

	/**
	 * 城居门特明细上传参数:函数DataDown_cmts,（F）方法
	 * 
	 * @param insParm
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_cmts_F(TParm insParm, TParm tempParm, String type) {
		TParm parm = DataDown_mtsORcmts_F(insParm, tempParm, type);
		TParm result = commInterFace("DataDown_cmts", "F", parm);
		return result;
	}

	/**
	 * 城职门特明细上传参数:函数DataUpload,（C）方法
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataUpload_C(TParm insParm) {
		insParm.addData("PARM_COUNT", 25); // 参数数量
		TParm result = commInterFace("DataUpload", "C", insParm);
		return result;
	}

	/**
	 * 城居门特明细上传参数:函数DataUpload,（F）方法
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataUpload_F(TParm insParm) {
		insParm.addData("PARM_COUNT", 25); // 参数数量
		TParm result = commInterFace("DataUpload", "F", insParm);
		return result;
	}

	/**
	 * 退费操作添加一条负值数据
	 * 
	 * @param parm
	 *            TParm
	 */
	public void balanceParm(TParm parm) {
		// TParm maxSeqOpdParm =
		// INSOpdTJTool.getInstance().selectMAXSeqNo(parm);
		// int maxSeq = 0;// 最大号码
		// if (null != maxSeqOpdParm.getValue("SEQ_NO", 0)
		// && maxSeqOpdParm.getInt("SEQ_NO", 0) > 0) {
		// maxSeq = maxSeqOpdParm.getInt("SEQ_NO", 0);
		// }
		if (null == parm) {
			return;
		}
		checkOutParm(parm);
		parm.setData("RECP_TYPE", parm.getValue("UNRECP_TYPE")); // 退费
		parm.setData("INSAMT_FLG", 0); // 未对账操作
		// 发生金额
		parm.setData("TOT_AMT", parm.getDouble("TOT_AMT") == 0 ? "0" : "-"
				+ parm.getDouble("TOT_AMT")); // 负值
		// 申报金额
		parm.setData("NHI_AMT", parm.getDouble("NHI_AMT") == 0 ? "0" : "-"
				+ parm.getDouble("NHI_AMT")); // 负值
		// 增负金额
		parm.setData("ADD_AMT", parm.getDouble("ADD_AMT") == 0 ? "0" : "-"
				+ parm.getDouble("ADD_AMT")); // 负值
		// 自费金额
		parm.setData("OWN_AMT", parm.getDouble("OWN_AMT") == 0 ? "0" : "-"
				+ parm.getDouble("OWN_AMT")); // 负值
		// 专项基金社保支付
		parm.setData("OTOT_AMT", parm.getDouble("OTOT_AMT") == 0 ? "0" : "-"
				+ parm.getDouble("OTOT_AMT")); // 负值
		// 医保支付金额
		parm.setData("INS_PAY_AMT", parm.getDouble("INS_PAY_AMT") == 0 ? "0"
				: "-" + parm.getDouble("INS_PAY_AMT")); // 负值
		// 统筹基金申报金额
		parm.setData("APPLY_AMT", parm.getDouble("APPLY_AMT") == 0 ? "0" : "-"
				+ parm.getDouble("APPLY_AMT")); // 负值
		// 个人帐户实际支付金额
		parm.setData("ACCOUNT_PAY_AMT",
				parm.getDouble("ACCOUNT_PAY_AMT") == 0 ? "0" : "-"
						+ parm.getDouble("ACCOUNT_PAY_AMT")); // 负值
		//基金未报销金额
		parm.setData("UNREIM_AMT",
				parm.getDouble("UNREIM_AMT") == 0 ? "0" : "-"
						+ parm.getDouble("UNREIM_AMT")); // 负值
		// 非账户支付
		parm.setData("UACCOUNT_PAY_AMT",
				parm.getDouble("UACCOUNT_PAY_AMT") == 0 ? "0" : "-"
						+ parm.getDouble("UACCOUNT_PAY_AMT")); // 负值
		// 可用帐户报销金额
		parm.setData("OINSTOT_AMT", parm.getDouble("OINSTOT_AMT") == 0 ? "0"
				: "-" + parm.getDouble("OINSTOT_AMT")); // 负值
		// 统筹支付金额
		parm.setData("TOTAL_AGENT_AMT",
				parm.getDouble("TOTAL_AGENT_AMT") == 0 ? "0" : "-"
						+ parm.getDouble("TOTAL_AGENT_AMT")); // 负值

		parm.setData("ARMY_AI_AMT", parm.getDouble("ARMY_AI_AMT") == 0 ? "0"
				: "-" + parm.getDouble("ARMY_AI_AMT")); // 负值
		parm.setData("SERVANT_AMT", parm.getDouble("SERVANT_AMT") == 0 ? "0"
				: "-" + parm.getDouble("SERVANT_AMT")); // 负值
		parm.setData("FLG_AGENT_AMT",
				parm.getDouble("FLG_AGENT_AMT") == 0 ? "0" : "-"
						+ parm.getDouble("FLG_AGENT_AMT")); // 负值
		String[] type = { "PHA_", "EXM_", "TREAT_", "OP_", "BED_", "MATERIAL_",
				"OTHER_", "BLOOD_", "BLOODALL_" };
		for (int i = 0; i < type.length; i++) {
			parm.setData(type[i] + "AMT",
					parm.getDouble(type[i] + "AMT") == 0 ? "0" : "-"
							+ parm.getDouble(type[i] + "AMT"));
			parm.setData(type[i] + "NHI_AMT", parm.getDouble(type[i]
					+ "NHI_AMT") == 0 ? "0" : "-"
					+ parm.getDouble(type[i] + "NHI_AMT"));
		}
		//城乡大病补助金额
		parm.setData("ILLNESS_SUBSIDY_AMT",
				parm.getDouble("ILLNESS_SUBSIDY_AMT") == 0 ? "0" : "-"
						+ parm.getDouble("ILLNESS_SUBSIDY_AMT")); // 负值
		// parm.setData("SEQ_NO", maxSeq + 1);// 2 序号

	}

	/**
	 * 校验是否为空
	 * 
	 * @param parm
	 *            TParm
	 */
	private void checkOutParm(TParm parm) {
		for (int i = 0; i < insOpd.length; i++) {
			if (null == parm.getValue(insOpd[i])
					|| parm.getValue(insOpd[i]).length() <= 0) {
				parm.setData(insOpd[i], "");
			}

		}

	}

	/**
	 * 结算参数：修改INS_OPD数据---需要确定字段名称？？？？？
	 * 
	 * @param insParm
	 *            TParm
	 * @param interFaceParm
	 *            TParm
	 * @return TParm
	 */
	public TParm balanceParm(TParm insParm, TParm interFaceParm) {
		// System.out.println("结算参数::::::::parm::" + insParm);
		String[] type = { "PHA_", "EXM_", "TREAT_", "OP_", "BED_", "MATERIAL_",
				"OTHER_", "BLOOD_", "BLOODALL_" };
		TParm result = null;
		TParm inParm = new TParm();
		TParm diagRecparm = insParm.getParm("diagRecparm"); // 医保数据
		// 个人账户实际支付金额=“可用帐户报销金额”和“个人帐户余额”的比较，
		// 如果“可用帐户报销金额” 大于 “个人帐户余额” 则“个人帐户实际支付金额”值为“个人帐户余额”
		// ，“非帐户支付金额”的值为 （“可用帐户报销金额” －“个人帐户余额”） ＋ “自费金额”
		// 反之 “个人帐户实际支付金额” 为值 “可用帐户报销金额”，“非帐户支付金额”的值为“自费金额”。
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		TParm accountParm = getAccountAmt(interFaceParm, opbReadCardParm,
				"TOT_AMT");
		double accountPayAmt = accountParm.getDouble("ACCOUNT_AMT");
		double uaccountPayAmt = accountParm.getDouble("UACCOUNT_AMT");
		inParm.setData("REGION_CODE", insParm.getValue("REGION_CODE")); // 1
		// 医院编码
		inParm.setData("CASE_NO", insParm.getValue("CASE_NO")); // 2
		inParm.setData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 3资格确认号
		inParm.setData("MR_NO", insParm.getValue("MR_NO")); // 4 病案号
		double NHI_AMT = 0.00;
		for (int i = 0; i < type.length; i++) {
			NHI_AMT += interFaceParm.getDouble(type[i] + "NHI_AMT");
		}
		inParm.setData("PHA_AMT", interFaceParm.getDouble("PHA_AMT")); // 5
		// 药品费发生金额
		inParm.setData("PHA_NHI_AMT", interFaceParm.getDouble("PHA_NHI_AMT")); // 6
		// 医保药品费
		inParm.setData("EXM_AMT", interFaceParm.getDouble("EXM_AMT")); // 7 检查费
		inParm.setData("EXM_NHI_AMT", interFaceParm.getDouble("EXM_NHI_AMT")); // 8
		// 医保检查费
		inParm.setData("TREAT_AMT", interFaceParm.getDouble("TREAT_AMT")); // 9
		// 治疗费（治疗费发生金额）
		inParm.setData("TREAT_NHI_AMT", interFaceParm
				.getDouble("TREAT_NHI_AMT")); // 10 医保治疗费（治疗费申报金额）
		inParm.setData("OP_AMT", interFaceParm.getDouble("OP_AMT")); // 11
		// 手术费发生金额
		inParm.setData("OP_NHI_AMT", interFaceParm.getDouble("OP_NHI_AMT")); // 12手术费申报金额
		inParm.setData("BED_AMT", interFaceParm.getValue("BED_AMT")); // 13床位费(床位费发生金额)
		inParm.setData("BED_NHI_AMT", interFaceParm.getDouble("BED_NHI_AMT")); // 14
		// 医保床位费
		inParm.setData("MATERIAL_AMT", interFaceParm.getDouble("MATERIAL_AMT")); // 15
		// 材料费
		inParm.setData("MATERIAL_NHI_AMT", interFaceParm
				.getDouble("MATERIAL_NHI_AMT")); // 16 医保材料费
		inParm.setData("OTHER_AMT", interFaceParm.getDouble("OTHER_AMT")); // 17
																			// 其它费用
		inParm.setData("OTHER_NHI_AMT", interFaceParm
				.getDouble("OTHER_NHI_AMT")); // 18 医保其它费用
		inParm.setData("BLOODALL_AMT", interFaceParm.getDouble("BLOODALL_AMT")); // 19
		// 输全血发生金额
		inParm.setData("BLOODALL_NHI_AMT", interFaceParm
				.getDouble("BLOODALL_NHI_AMT")); // 20输全血医保金额
		inParm.setData("BLOOD_AMT", interFaceParm.getDouble("BLOOD_AMT")); // 21
		// 成分输血发生金额
		inParm.setData("BLOOD_NHI_AMT", interFaceParm
				.getDouble("BLOOD_NHI_AMT")); // 22 成分输血申报金额
		inParm.setData("TOT_AMT", insParm.getDouble("FeeY")); // 23
		// 发生金额---------（总金额）
		inParm.setData("NHI_AMT", NHI_AMT); // 24
		// (申报金额)
		inParm.setData("START_STANDARD_AMTS", interFaceParm
				.getDouble("START_STANDARD_AMTS")); // 25 起付标准剩余额
		inParm.setData("OTOT_PAY_AMT", interFaceParm.getDouble("OTOT_PAY_AMT")); // 26
		// 专项基金支付
		inParm.setData("START_STANDARD_AMT", interFaceParm
				.getDouble("START_STANDARD_AMT")); // 27 起付标准
		inParm.setData("ADD_AMT", interFaceParm.getDouble("ADD_AMT")); // 28
		// 增负金额
		inParm.setData("OWN_AMT", interFaceParm.getDouble("OWN_AMT")); // 29
		// 自费金额
		inParm.setData("INS_STANDARD_AMT", interFaceParm
				.getDouble("INS_STANDARD_AMT")); // 30 起付标准金额
		inParm.setData("TRANBLOOD_OWN_AMT", interFaceParm
				.getDouble("TRANBLOOD_OWN_AMT")); // 31 输血自付金额
		inParm.setData("APPLY_AMT", interFaceParm.getDouble("APPLY_AMT")); // 32
		// 最高限额以上金额
		inParm.setData("OTOT_AMT", interFaceParm.getDouble("OTOT_AMT")); // 33
		// 专项基金支付限额
		inParm.setData("OINSTOT_AMT", interFaceParm.getDouble("OINSTOT_AMT")); // 34可用帐户报销金额
		// System.out.println("***********************************************");
		// //System.out.println("INS_DATE:::" +
		// interFaceParm.getData("INS_DATE"));
		inParm.setData("INSAMT_FLG", 1); // 36社保对帐标志INS_OPD中INSAMT_FLG对账标志为0
		inParm.setData("OPT_USER", insParm.getValue("OPT_USER")); // 37
		inParm.setData("OPT_TERM", insParm.getValue("OPT_TERM")); // 38 39
		inParm.setData("UNACCOUNT_PAY_AMT", uaccountPayAmt); // 40非账户支付金额
		inParm.setData("ACCOUNT_PAY_AMT", accountPayAmt); // 41 账户支付金额
		inParm.setData("ACCOUNT_LEFT_AMT", 0.00); // 42账户余额
		inParm.setData("REFUSE_AMT", 0.00); // 43//拒付金额
		inParm.setData("REFUSE_ACCOUNT_PAY_AMT", 0.00); // 44
		inParm.setData("REFUSE_DESC", ""); // 45//拒付原因
		inParm.setData("CONFIRM_F_USER", ""); // 46初审人
		inParm.setData("CONFIRM_F_DATE", new TNull(String.class)); // 47初审时间
		inParm.setData("CONFIRM_S_USER", ""); // 48复审人
		inParm.setData("CONFIRM_S_DATE", new TNull(String.class)); // 49复审时间
		inParm.setData("SLOW_DATE", new TNull(String.class)); // 50缓之时间
		inParm.setData("SLOW_PAY_DATE", new TNull(String.class)); // 51缓之支付时间
		inParm.setData("REFUSE_CODE", ""); // 52拒付原因代码
		inParm.setData("REFUSE_OTOT_AMT", 0.00); // 53拒付后专项基金支付金额

		inParm.setData("INV_NO", insParm.getValue("PRINT_NO")); // 54 票据号
		if (insParm.getValue("RECP_TYPE").equals("REG")) {
			inParm.setData("MZ_DIAG", "挂号"); // 55
			inParm.setData("OTHER_DIAGE_CODE", "");//诊断编码
		} else {
			StringBuffer diagString = new StringBuffer();
			String otherdiagecode = "";			
			for (int i = 0; i < diagRecparm.getCount(); i++) {
				diagString.append(diagRecparm.getValue("ICD_CODE", i)
						+ diagRecparm.getValue("ICD_CHN_DESC", i));
				otherdiagecode +=diagRecparm.getValue("ICD_CODE", i)+"@";
			}
			inParm.setData("MZ_DIAG", diagString.toString()); // 55
			inParm.setData("OTHER_DIAGE_CODE",  otherdiagecode.length()>0? 
			    otherdiagecode.substring(0, otherdiagecode.length() - 1):"");//诊断编码
		}
		// 诊断类别
		// 医保关联数据
		inParm.setData("DRUG_DAYS", insParm.getInt("TAKE_DAYS")); // 56 用药天数
		inParm.setData("INS_STD_AMT", interFaceParm.getDouble("INS_STD_AMT")); // 57年度起付标准
		inParm.setData("INS_CROWD_TYPE", insParm.getValue("CROWD_TYPE")); // 58身份别1
		// 城职2
		// 城居
		// 医保卡类型
		inParm.setData("INS_PAT_TYPE", insParm.getValue("INS_PAT_TYPE")); // 59身份别1
		// 普通2
		// 门特
		inParm.setData("RECP_TYPE", insParm.getValue("RECP_TYPE")); // 60挂号收费类别
		inParm.setData("OWEN_PAY_SCALE", checkOutIsNull(interFaceParm
				.getValue("OWEN_PAY_SCALE"))); // 61自负比例
		inParm.setData("REDUCE_PAY_SCALE", checkOutIsNull(interFaceParm
				.getValue("REDUCE_PAY_SCALE"))); // 62减负比例
		inParm.setData("REAL_OWEN_PAY_SCALE", checkOutIsNull(interFaceParm
				.getValue("REAL_OWEN_PAY_SCALE"))); // 63实际自负比例
		inParm.setData("SALVA_PAY_SCALE", checkOutIsNull(interFaceParm
				.getValue("SALVA_PAY_SCALE"))); // 64医疗救助自负比例
		inParm.setData("BASEMED_BALANCE", checkOutIsNull(interFaceParm
				.getValue("BASEMED_BALANCE"))); // 65基本医疗剩余额
		inParm.setData("INS_BALANCE", checkOutIsNull(interFaceParm
				.getValue("INS_BALANCE"))); // 66 医疗救助剩余额
		inParm.setData("ISSUE", interFaceParm.getValue("ISSUE")); // 67期号
		inParm.setData("BCSSQF_STANDRD_AMT", checkOutIsNull(interFaceParm
				.getValue("BCSSQF_STANDRD_AMT"))); // 68本次实收起付标准金额
		inParm.setData("PERCOPAYMENT_RATE_AMT", checkOutIsNull(interFaceParm
				.getValue("PERCOPAYMENT_RATE_AMT"))); // 69医疗救助个人按比例负担金额
		inParm.setData("INS_HIGHLIMIT_AMT", checkOutIsNull(interFaceParm
				.getValue("INS_HIGHLIMIT_AMT"))); // 70医疗救助最高限额以上金额
		inParm.setData("TOTAL_AGENT_AMT", interFaceParm
				.getDouble("TOTAL_AGENT_AMT")); // 71基本医疗社保申请金额TOTAL_AGENT_AMT
		inParm.setData("FLG_AGENT_AMT", checkOutIsNull(interFaceParm
				.getValue("FLG_AGENT_AMT"))); // 72救助基金申报金额(所有正常)FLG_AGENT_AMT
		inParm.setData("APPLY_AMT_R", 0.00); // 73统筹基金拒付金额
		inParm.setData("FLG_AGENT_AMT_R", 0.00); // 74救助拒付金额
		inParm.setData("REFUSE_DATE", new TNull(String.class)); // 75拒付时间

		inParm.setData("REAL_PAY_LEVEL", checkOutIsNull(interFaceParm
				.getValue("BCSSQF_STANDRD_AMT"))); // 76本次实际起付标准
		inParm.setData("VIOLATION_REASON_CODE", ""); // 77医保缓支信息
		if (insParm.getInt("INS_TYPE") == 1) {
			inParm.setData("INS_DATE", interFaceParm.getData("INS_DATE")); // 35医保结算时间-中心交易时间
			inParm.setData("ARMY_AI_AMT", interFaceParm.getDouble("AGENT_AMT")); // 78军残补助金额(所有正常)
		} else if (insParm.getInt("INS_TYPE") == 2) {
			inParm.setData("INS_DATE", interFaceParm.getData("INSBRANCH_TIME")); // 35医保结算时间-中心交易时间
			inParm.setData("ARMY_AI_AMT", interFaceParm
					.getDouble("ARMY_AI_AMT")); // 78军残补助金额(所有正常)
		} else if (insParm.getInt("INS_TYPE") == 3) {
			inParm.setData("INS_DATE", interFaceParm.getData("INSBRANCH_TIME")); // 35医保结算时间-中心交易时间
			inParm.setData("ARMY_AI_AMT", interFaceParm.getDouble("AGENT_AMT")); // 78军残补助金额(所有正常)
		}
		inParm.setData("SERVANT_AMT", checkOutIsNull(interFaceParm
				.getValue("SERVANT_AMT"))); // 79公务员补助金额(所有正常)
		inParm.setData("INS_PAY_AMT", interFaceParm.getDouble("INS_PAY_AMT")); // 80医保支付金额(INS_PAY_AMT)
		inParm.setData("UNREIM_AMT", interFaceParm.getDouble("UNREIM_AMT")); // 81UNREIM_AMT
		// 基金未报销金额REIM_TYPE
		inParm.setData("REIM_TYPE", interFaceParm.getInt("REIM_TYPE")); // 82报销类别报销类别0联网结算1 联网垫付
		// inParm.setData("RECEIPT_NO", insParm.getValue("RECEIPT_NO")); //83收据号REG/OPB
		//城乡大病补助金额
		if (insParm.getInt("INS_TYPE") == 1)
			inParm.setData("ILLNESS_SUBSIDY_AMT",0.00);	
		else if(insParm.getInt("INS_TYPE") == 2)
			inParm.setData("ILLNESS_SUBSIDY_AMT",0.00);		
		else if(insParm.getInt("INS_TYPE") == 3)
		inParm.setData("ILLNESS_SUBSIDY_AMT", interFaceParm.getDouble("ILLNESS_SUBSIDY_AMT"));

		result = INSOpdTJTool.getInstance().updateINSopdSettle(inParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			// connection.close();
			return result;
		}
		return result;
	}

	/**
	 * 校验是否为空
	 * 
	 * @param name
	 *            String
	 * @return double
	 */
	private double checkOutIsNull(String name) {
		return null == name || name.trim().length() <= 0 ? 0.00 : Double
				.parseDouble(name);
	}

	 /**
     * 添加费用分割数据：添加INS_OPD_ORDER 明细
     * @param parm
     * @param REG_PARM
     * @param interfaceParm
     * @param maxSeq
     * @param j
     * @param printNo
     * @return
     */
	private TParm insOrderTemp(TParm parm, TParm REG_PARM, TParm interfaceParm,
			int maxSeq, int j, String printNo) {
		TParm result = null;
		TParm insOrderParm = new TParm();
		System.out.println("REG_PARM:"+REG_PARM);
		//执行添加INS_OPD_ORDER 表数据
		insOrderParm.setData("REGION_CODE", parm.getValue("NEW_REGION_CODE"));
		insOrderParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		//序号
		insOrderParm.setData("SEQ_NO", maxSeq + j + 1); 
		//资格确认号
		insOrderParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); 
		Timestamp date = StringTool.getTimestamp(
				parm.getValue("ADM_DATE")+"000000", "yyyyMMddHHmmss");
		//计价日期
		insOrderParm.setData("CHARGE_DATE", date); 
		//医保码
		getParmValue(parm, REG_PARM, j, insOrderParm, true);
		//医嘱代码
		insOrderParm.setData("ORDER_CODE", REG_PARM.getValue("ORDER_CODE", j));
		//医嘱名称
		insOrderParm.setData("ORDER_DESC", REG_PARM.getValue("ORDER_DESC", j));
		//自负比例
		insOrderParm.setData("OWN_RATE", interfaceParm.getValue("OWN_RATE")); 
		//剂型代码
		insOrderParm.setData("DOSE_CODE", REG_PARM.getValue("DOSE_CODE", j)); 
		//起付标准
		insOrderParm.setData("STANDARD", REG_PARM.getValue("SPECIFICATION", j));
		//单价
		insOrderParm.setData("PRICE", REG_PARM.getDouble("OWN_PRICE", j));
		//数量DISPENSE_QTY modify by liuf 
		insOrderParm.setData("QTY", REG_PARM.getDouble("DOSAGE_QTY", j)); 
		//总金额
		insOrderParm.setData("TOTAL_AMT", REG_PARM.getDouble("AR_AMT", j));
		//申报金额
		insOrderParm.setData("TOTAL_NHI_AMT", interfaceParm.getDouble("NHI_AMT")); 
		//自费金额
		insOrderParm.setData("OWN_AMT", interfaceParm.getDouble("OWN_AMT"));
		//增付金额
		insOrderParm.setData("ADDPAY_AMT", interfaceParm.getDouble("ADDPAY_AMT"));
		//拒付金额
		insOrderParm.setData("REFUSE_AMT", 0.00); 
		//拒付原因代码
		insOrderParm.setData("REFUSE_REASON_CODE", new TNull(String.class)); 
		//拒付原因注记
		insOrderParm.setData("REFUSE_REASON_NOTE", new TNull(String.class));
		//手术注记（Y：手术；N：非手术）:挂号
		insOrderParm.setData("OP_FLG", "N"); 
		//出院带药注记（Y：带药；N：不带药）
		insOrderParm.setData("CARRY_FLG", "N"); 
		getPhaAdd(parm, insOrderParm, REG_PARM, j, true);
		//累计增付注记（Y：累计增付；N：不累计增付）
		insOrderParm.setData("ADDPAY_FLG",null != interfaceParm.getValue("")
								&& interfaceParm.getValue("ADDPAY_FLG").equals("1") ? "Y" : "N"); 
		insOrderParm.setData("NHI_ORD_CLASS_CODE", interfaceParm.getValue("NHI_ORD_CLASS_CODE"));
		//对账标志为0
		insOrderParm.setData("INSAMT_FLG", 0); 
		//医嘱处方号OPD_ORDER.RX_NO
		insOrderParm.setData("RX_SEQNO", REG_PARM.getValue("RX_NO", j)); 
		//医嘱处方顺序号OPD_ORDER.SEQ
		insOrderParm.setData("OPB_SEQNO", REG_PARM.getValue("SEQ_NO", j)); 
		//用量  MEDI_QTY  modify by liuf
		insOrderParm.setData("TAKE_QTY", REG_PARM.getDouble("MEDI_QTY", j));
		//用法  ROUTE_CODE modify by liuf 
		insOrderParm.setData("ROUTE", REG_PARM.getValue("ROUTE_CODE",j)); 
		insOrderParm.setData("HYGIENE_TRADE_CODE",REG_PARM.getValue("PZWH", j));
		insOrderParm.setData("INS_CROWD_TYPE", parm.getValue("CROWD_TYPE"));
		insOrderParm.setData("INS_PAT_TYPE", parm.getValue("INS_PAT_TYPE"));
		insOrderParm.setData("ORIGSEQ_NO", new TNull(String.class));
		insOrderParm.setData("TAKE_DAYS", REG_PARM.getValue("TAKE_DAYS", j));
		//收据号
		insOrderParm.setData("INV_NO", printNo); 
		//操作员
		insOrderParm.setData("OPT_USER", parm.getValue("OPT_USER")); 
		//IP
		insOrderParm.setData("OPT_TERM", parm.getValue("OPT_TERM")); 
		//类型
		insOrderParm.setData("RECP_TYPE", parm.getValue("RECP_TYPE")); 
		insOrderParm.setData("SPECIAL_CASE_DESC", REG_PARM.getValue("DR_NOTE",j));
		System.out.println("insOrderParm=======:"+insOrderParm);
		result = INSOpdOrderTJTool.getInstance().insertINSOpdOrder(insOrderParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

    /**
     * 调用接口方法出现错误
     *
     * @param parm
     *            TParm
     * @return boolean
     */
    public boolean getErrParm(TParm parm) {
        // System.out.println("调用接口方法:::"+parm);
        if (null == parm) {
            parm = new TParm();
            parm.setErr( -1, "调用接口失败");
            return false;
        }
        if (parm.getErrCode()<0) {
        	return false;
		}
        if (null != parm.getValue("PROGRAM_STATE")
            && (parm.getInt("PROGRAM_STATE") == 0 || parm
                .getInt("PROGRAM_STATE") == 1 || parm
                .getInt("PROGRAM_STATE") == 2) ) {
            return true;

		} else {
			parm.setErr(-1, parm.getValue("PROGRAM_MESSAGE"));
			return false;
		}

	}

	/**
	 * 城职普通 帐户支付确认交易返回:函数DataDown_rs参数R方法
	 * 
	 * @param insParm
	 *            TParm
	 * @param interFaceParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_rs_R(TParm insParm, TParm interFaceParm) {
		// 个人账户实际支付金额=“可用帐户报销金额”和“个人帐户余额”的比较，
		// 如果“可用帐户报销金额” 大于 “个人帐户余额” 则“个人帐户实际支付金额”值为“个人帐户余额”
		// ，“非帐户支付金额”的值为 （“可用帐户报销金额” －“个人帐户余额”） ＋ “自费金额”
		// 反之 “个人帐户实际支付金额” 为值 “可用帐户报销金额”，“非帐户支付金额”的值为“自费金额”。
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		TParm accountParm = getAccountAmt(interFaceParm, opbReadCardParm,
				"TOT_AMT");
		double accountPayAmt = accountParm.getDouble("ACCOUNT_AMT");
		double uaccountPayAmt = accountParm.getDouble("UACCOUNT_AMT");
		TParm parm = new TParm();
		// parm.addData("CASE_NO", insParm.getValue("CASE_NO"));// 1就诊号 A 方法中的参数
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1门诊顺序号:L方法出参
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2 医院编码
		parm.addData("UNACCOUNT_PAY_AMT", StringTool.round(uaccountPayAmt, 2)); // 3非账户支付金额
		parm.addData("ACCOUNT_PAY_AMT", StringTool.round(accountPayAmt, 2)); // 4
																				// 个人帐户实际支付金额
		parm.addData("INSCARD_NO", insParm.getValue("PERSONAL_NO")); // 5 个人编码
		// A方法出参
		parm.addData("INSCARD_PASSWORD", insParm.getValue("PASSWORD")); // 6 密码
		parm.addData("OTOT_AMT", StringTool.round(interFaceParm
				.getDouble("OTOT_AMT"), 2)); // 7专项基金支付金额
		parm.addData("AGENT_AMT", StringTool.round(interFaceParm
				.getDouble("AGENT_AMT"), 2)); // 8
		// 补助金额
		parm.addData("PARM_COUNT", 8);
		TParm result = commInterFace("DataDown_rs", "R", parm);
		// System.out.println("帐户支付确认交易accPaymentSureParm::" + result);
		// result.setData("PROGRAM_STATE", "Y");// 程序执行状态PROEXE_FLG)
		// result.setData("PROGRAM_MESSAGE", "执行成功");// 程序执行信息
		// result.setData("ACCOUNT_BALANCE_AMT", interFaceParm
		// .getValue("ACCOUNT_BALANCE_AMT"));// 帐户余额(ACCOUNT_BALANCE_AMT)
		// result.setData("OTOT_AMT", interFaceParm.getValue("OTOT_AMT"));//
		// 专项基金支付限额(OTOT_AMT)
		// result.setData("GRANT_AMT", interFaceParm.getValue("GRANT_AMT"));//
		// 对帐标志

		return result;
	}

	/**
	 * 城职普通结算确认返回参数:函数DataDown_sp，门诊结算确认交易（R）方法
	 * 
	 * @param insParm
	 *            TParm
	 * @param interFaceParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_R(TParm insParm, TParm interFaceParm) {
		TParm parm = new TParm();
		// parm.addData("CASE_NO", insParm.getValue("CASE_NO"));// 1就诊号 A 方法中的参数
		// 个人账户实际支付金额=“可用帐户报销金额”和“个人帐户余额”的比较，
		// 如果“可用帐户报销金额” 大于 “个人帐户余额” 则“个人帐户实际支付金额”值为“个人帐户余额”
		// ，“非帐户支付金额”的值为 （“可用帐户报销金额” －“个人帐户余额”） ＋ “自费金额”
		// 反之 “个人帐户实际支付金额” 为值 “可用帐户报销金额”，“非帐户支付金额”的值为“自费金额”。
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		TParm accountParm = getAccountAmt(interFaceParm, opbReadCardParm,
				"TOT_AMT");
		double accountPayAmt = accountParm.getDouble("ACCOUNT_AMT");
		double uaccountPayAmt = accountParm.getDouble("UACCOUNT_AMT");
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 2 门诊顺序号
		// L方法出参
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 3 医院编码
		parm.addData("ACCOUNT_PAY_AMT", StringTool.round(accountPayAmt, 2)); //
		// 帐户支付金额
		parm.addData("INS_PAY_AMT", StringTool.round(interFaceParm
				.getDouble("INS_PAY_AMT"), 2)); // 5
		// 医保支付金额
		parm.addData("CASH_AMT", StringTool.round(uaccountPayAmt
				+ interFaceParm.getDouble("UNREIM_AMT"), 2)); // 6
																// 现金支付金额UNREIM_AMT
		parm.addData("UNREIM_AMT", StringTool.round(interFaceParm
				.getDouble("UNREIM_AMT"), 2)); // 7
		// 基金未报销金额
		parm.addData("PARM_COUNT", 6);
		// System.out
		// .println("城职普通结算确认::cityStaffCommonBalanceSureParm:入参" + parm);
		TParm result = commInterFace("DataDown_sp", "R", parm);
		// System.out
		// .println("城职普通结算确认::cityStaffCommonBalanceSureParm:" + result);
		// result.setData("PROGRAM_STATE", "Y");// 程序执行状态PROEXE_FLG)
		// result.setData("PROGRAM_MESSAGE", "执行成功");// 程序执行信息
		// result.setData("R_FLG", "Y");// 对帐标志
		return result;

	}

	/**
	 * 城职普通结算确认返回参数:函数DataDown_sp，门诊结算确认交易（R）方法 门诊自动对账操作
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_R(TParm insParm) {
		TParm parm = new TParm();
		// parm.addData("CASE_NO", insParm.getValue("CASE_NO"));// 1就诊号 A 方法中的参数
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 2 门诊顺序号
		// L方法出参
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 3 医院编码
		parm.addData("ACCOUNT_PAY_AMT", StringTool.round(insParm
				.getDouble("ACCOUNT_PAY_AMT"), 2)); // 4
		// 帐户支付金额-----可用帐户报销金额---???????????
		parm.addData("INS_PAY_AMT", StringTool.round(insParm
				.getDouble("INS_PAY_AMT"), 2)); // 5
		// 医保支付金额
		parm.addData("CASH_AMT", StringTool.round(insParm
				.getDouble("UACCOUNT_PAY_AMT")
				+ insParm.getDouble("UNREIM_AMT"), 2)); // 6 现金支付金额
		parm.addData("UNREIM_AMT", StringTool.round(insParm
				.getDouble("UNREIM_AMT"), 2)); // 7
		// 基金未报销金额
		parm.addData("PARM_COUNT", 6);
		// System.out.println("城职普通结算确认:门诊自动对账入参" + parm);
		TParm result = commInterFace("DataDown_sp", "R", parm);
		// System.out.println("城职普通结算确认::门诊自动对账:" + result);
		// result.setData("PROGRAM_STATE", "Y");// 程序执行状态PROEXE_FLG)
		// result.setData("PROGRAM_MESSAGE", "执行成功");// 程序执行信息
		// result.setData("R_FLG", "Y");// 对帐标志
		return result;
	}

	/**
	 * 城职门特刷卡返回参数：DataDown_mts门特刷卡交易（E）
	 * 
	 * @param insParm
	 *            TParm A 方法出参
	 * @return TParm
	 */
	public TParm DataDown_mts_E(TParm insParm) {
		TParm parm = new TParm();
		parm.addData("CARD_NO", insParm.getValue("PERSONAL_NO")); // 个人编码
		// A方法出参
		parm.addData("PASSWORD", insParm.getValue("PASSWORD")); // 密码
		parm.addData("PAY_KIND", insParm.getValue("PAY_KIND")); // 支付类别12Ⅰ类门诊特殊病13
		// Ⅱ类门诊特殊病
		// 门特类别:1肾透析2肾移植抗排异3癌症放化疗4 糖尿病5肺心病6红斑狼疮7精神病8偏瘫9癫痫10 再生障碍性贫血11
		// 慢性血小板减少性紫癫21血友病22肝移植术后抗排异治疗
		parm.addData("DISEASE_CODE", insParm.getValue("DISEASE_CODE"));
		parm.addData("HOSP_NHI_NO", insParm.getValue("ADVANCE_CODE"));//医院编码@费用发生时间@类别
		parm.addData("CHECK_CODES", insParm.getValue("CHECK_CODES")); // 刷卡验证码
		parm.addData("PARM_COUNT", 6);
		// System.out.println("城职门特刷卡入参：：：" + parm);
		TParm result = commInterFace("DataDown_mts", "E", parm);
		// System.out.println("城职门特刷卡返回::result::" + result);

		return result;
	}

	/**
	 * 城职门特返回参数：统筹支付确认交易：函数DataDown_mts, 门特统筹支付确认交易（G）
	 * 
	 * @param insParm
	 *            TParm
	 * @param interFaceParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_G(TParm insParm, TParm interFaceParm) {
		TParm parm = new TParm();

		// 个人账户实际支付金额=“可用帐户报销金额”和“个人帐户余额”的比较，
		// 如果“可用帐户报销金额” 大于 “个人帐户余额” 则“个人帐户实际支付金额”值为“个人帐户余额”
		// ，“非帐户支付金额”的值为 （“可用帐户报销金额” －“个人帐户余额”） ＋ “自费金额”
		// 反之 “个人帐户实际支付金额” 为值 “可用帐户报销金额”，“非帐户支付金额”的值为“自费金额”。
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		TParm accountParm = getAccountAmt(interFaceParm, opbReadCardParm,
				"PERSON_ACCOUNT_AMT");
		double accountPayAmt = accountParm.getDouble("ACCOUNT_AMT");
		double uaccountPayAmt = accountParm.getDouble("UACCOUNT_AMT");
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1
		// 门特就医顺序号E方法出参
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2
		// 医院(或药店)编码
		parm.addData("TOTAL_AGENT_AMT", StringTool.round(interFaceParm
				.getDouble("TOTAL_AGENT_AMT"), 2)); // 3 基本医疗社保申请金额
		parm.addData("FLG_AGENT_AMT", StringTool.round(interFaceParm
				.getDouble("FLG_AGENT_AMT"), 2)); // 4
		// 医疗救助社保申请金额
		// 个人编码
		parm.addData("CARD_NO", insParm.getValue("PERSONAL_NO")); // 5 卡号
		// A方法出参
		parm.addData("PASSWORD", insParm.getValue("PASSWORD")); // 6密码
		parm.addData("UNACCOUNT_PAY_AMT", StringTool.round(uaccountPayAmt, 2)); // 7
		// 非账户支付金额
		parm.addData("ACCOUNT_PAY_AMT", StringTool.round(accountPayAmt, 2)); // 8
																				// 个人帐户实际支付金额
		parm.addData("AGENT_AMT", StringTool.round(interFaceParm
				.getDouble("ARMY_AI_AMT"), 2)); // 9
		// 补助金额
		parm.addData("AGENT_AMT2", StringTool.round(interFaceParm
				.getDouble("SERVANT_AMT"), 2)); // 10
		// 补助金额2
		parm.addData("PARM_COUNT", 10);
		// System.out.println("统筹支付确认交易：函数DataDown_mts, 门特统筹支付确认交易（G）入参:" +
		// parm);
		TParm result = commInterFace("DataDown_mts", "G", parm);
		// System.out.println("统筹支付确认交易：函数DataDown_mts, 门特统筹支付确认交易（G）:" +
		// result);
		return result;
	}

	/**
	 * 城职门特结算确认返回参数:函数DataDown_mts，门诊结算确认交易（I）方法
	 * 
	 * @param insParm
	 *            parm A 方法出参
	 * @param interFaceParm
	 *            parm 费用结算出参
	 * @param tempParm
	 *            parm 城职门特刷卡出参
	 * @return TParm
	 */
	public TParm DataDown_mts_I(TParm insParm, TParm interFaceParm,
			TParm tempParm) {
		TParm parm = new TParm();
		TParm accountParm = getAccountAmt(interFaceParm, tempParm,
				"PERSON_ACCOUNT_AMT");
		double accountPayAmt = accountParm.getDouble("ACCOUNT_AMT");
		double uaccountPayAmt = accountParm.getDouble("UACCOUNT_AMT");
		parm.addData("CONFIRM_NO", tempParm.getValue("CONFIRM_NO")); // 1 门诊顺序号
		// E
		// 方法出参
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2 医院编码
		parm.addData("PAT_TYPE", tempParm.getValue("PAT_TYPE")); // 3
		// 人员类别：11在职21退休51建国前老工人
		parm.addData("PAY_KIND", tempParm.getValue("PAY_KIND")); // 支付类别12Ⅰ类门诊特殊病13
		// Ⅱ类门诊特殊病
		// 门特类别:1肾透析2肾移植抗排异3癌症放化疗4 糖尿病5肺心病6红斑狼疮7精神病8偏瘫9癫痫10 再生障碍性贫血11
		// 慢性血小板减少性紫癫21血友病22肝移植术后抗排异治疗
		parm.addData("DISEASE_CODE", insParm.getValue("DISEASE_CODE")); // 4界面获得
		parm.addData("ACCOUNT_PAY_AMT", StringTool.round(accountPayAmt, 2)); // 5帐户支付金额
		parm.addData("INS_PAY_AMT", StringTool.round(interFaceParm
				.getDouble("INS_PAY_AMT"), 2)); // 6
		// 医保支付金额
		parm.addData("CASH_AMT", StringTool.round(uaccountPayAmt
				+ interFaceParm.getDouble("UNREIM_AMT"), 2)); // 7// 现金支付金额
		parm.addData("UNREIM_AMT", StringTool.round(interFaceParm
				.getDouble("UNREIM_AMT"), 2)); // 8
		// 基金未报销金额
		parm.addData("PARM_COUNT", 9);
		// System.out.println("城职门特函数DataDown_mts，门诊结算确认交易（I）入参:" + parm);
		TParm result = commInterFace("DataDown_mts", "I", parm);
		// System.out.println("城职门特函数DataDown_mts，门诊结算确认交易（I）出参:" + result);
		return result;
	}

	/**
	 * 获得个人账户支付金额 与非账户金额
	 * 
	 * @param interFaceParm
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param amtName
	 *            String
	 * @return TParm
	 */
	public TParm getAccountAmt(TParm interFaceParm, TParm tempParm,
			String amtName) {
		// 个人账户实际支付金额=“可用帐户报销金额”和“个人帐户余额”的比较，
		// 如果“可用帐户报销金额” 大于 “个人帐户余额” 则“个人帐户实际支付金额”值为“个人帐户余额”
		// ，“非帐户支付金额”的值为 （“可用帐户报销金额” －“个人帐户余额”） ＋ “自费金额”
		// 反之 “个人帐户实际支付金额” 为值 “可用帐户报销金额”，“非帐户支付金额”的值为“自费金额”。
		double accountPayAmt = 0.00;
		double uaccountPayAmt = 0.00;
		if (interFaceParm.getDouble("OINSTOT_AMT") > tempParm
				.getDouble(amtName)) {
			accountPayAmt = tempParm.getDouble(amtName); // 个人帐户余额
			// 可用帐户报销金额-帐户余额+自费金额
			uaccountPayAmt = interFaceParm.getDouble("OINSTOT_AMT")
					- accountPayAmt + interFaceParm.getDouble("OWN_AMT");
		} else {
			accountPayAmt = interFaceParm.getDouble("OINSTOT_AMT"); // 可用帐户报销金额
			uaccountPayAmt = interFaceParm.getDouble("OWN_AMT"); // 自费金额
		}
		TParm parm = new TParm();
		parm.setData("ACCOUNT_AMT", StringTool.round(accountPayAmt, 2));
		parm.setData("UACCOUNT_AMT", StringTool.round(uaccountPayAmt, 2));
		return parm;
	}

	/**
	 * 城职门特结算确认返回参数:函数DataDown_mts，门诊结算确认交易（I）方法 门诊对账使用
	 * 
	 * @param insParm
	 *            TParm
	 * @param mzConfirmParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_I(TParm insParm, TParm mzConfirmParm) {
		TParm parm = new TParm();
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1 门诊顺序号 E
		// 方法出参
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2 医院编码
		parm.addData("PAT_TYPE", mzConfirmParm.getValue("PAT_TYPE")); // 3
		// 人员类别：11在职21退休51建国前老工人
		parm.addData("PAY_KIND", mzConfirmParm.getValue("PAY_KIND")); // 支付类别12Ⅰ类门诊特殊病13
		// Ⅱ类门诊特殊病
		// 门特类别:1肾透析2肾移植抗排异3癌症放化疗4 糖尿病5肺心病6红斑狼疮7精神病8偏瘫9癫痫10 再生障碍性贫血11
		// 慢性血小板减少性紫癫21血友病22肝移植术后抗排异治疗
		parm.addData("DISEASE_CODE", mzConfirmParm.getValue("DISEASE_CODE")); // 4
		parm.addData("ACCOUNT_PAY_AMT", StringTool.round(insParm
				.getDouble("ACCOUNT_PAY_AMT"), 2)); // 5帐户支付金额-------?????
		parm.addData("INS_PAY_AMT", StringTool.round(insParm
				.getDouble("INS_PAY_AMT"), 2)); // 6
		// 医保支付金额
		parm.addData("CASH_AMT", StringTool.round(insParm
				.getDouble("UACCOUNT_PAY_AMT")
				+ insParm.getDouble("UNREIM_AMT"), 2)); // 7// 现金支付金额
		parm.addData("UNREIM_AMT", StringTool.round(insParm
				.getDouble("UNREIM_AMT"), 2)); // 8
		// 基金未报销金额
		parm.addData("PARM_COUNT", 9);
		// System.out.println("城职门特函数DataDown_mts，门诊结算确认交易（I）入参:" + parm);
		TParm result = commInterFace("DataDown_mts", "I", parm);
		// System.out.println("城职门特函数DataDown_mts，门诊结算确认交易（I）出参:" + result);
		return result;
	}

	/**
	 * 城居门特刷卡函数DataDown_cmts, 门特刷卡交易（E）,得到个人信息
	 * 
	 * @param insParm
	 *            TParm A 方法出参
	 * @return TParm
	 */
	public TParm DataDown_cmts_E(TParm insParm) {
		TParm parm = new TParm();
		parm.addData("CARD_NO", insParm.getValue("PERSONAL_NO")); // 1 个人编码
		// A方法出参
		parm.addData("PASSWORD", insParm.getValue("PASSWORD")); // 2 密码
		parm.addData("PAY_KIND", insParm.getValue("PAY_KIND")); // 3
		// 支付类别:11门诊、药店21住院//支付类别12
		// Ⅰ类门诊特殊病13 Ⅱ类门诊特殊病
		// 门特类别:1肾透析2肾移植抗排异3癌症放化疗4 糖尿病5肺心病6红斑狼疮7精神病8偏瘫9癫痫10 再生障碍性贫血11
		// 慢性血小板减少性紫癫21血友病22肝移植术后抗排异治疗
		parm.addData("DISEASE_CODE", insParm.getValue("DISEASE_CODE")); // 读卡界面获得?
		parm.addData("HOSP_NHI_NO", insParm.getValue("ADVANCE_CODE"));//医院编码@费用发生时间@类别
		parm.addData("COMU_NO", null); // 6 社区编码
		parm.addData("CHECK_CODES", insParm.getValue("CHECK_CODES")); // 7 刷卡验证码
		parm.addData("PARM_COUNT", 7);
		TParm result = commInterFace("DataDown_cmts", "E", parm);
		return result;
	}

	/**
	 * 城居门特:统筹支付确认交易:函数DataDown_cmts, 门特统筹支付确认交易（G）
	 * 
	 * @param insParm
	 *            TParm
	 * @param interFaceParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_G(TParm insParm, TParm interFaceParm) {
		TParm parm = new TParm();
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1
		// 门特就医顺序号E方法出参
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2
		// 医院(或药店)编码
		parm.addData("TOTAL_AGENT_AMT", StringTool.round(interFaceParm
				.getDouble("TOTAL_AGENT_AMT"), 2)); // 3 基本医疗社保申请金额
		parm.addData("FLG_AGENT_AMT", StringTool.round(interFaceParm
				.getDouble("FLG_AGENT_AMT"), 2)); // 4
		// 医疗救助社保申请金额
		// 个人编码-
		parm.addData("CARD_NO", insParm.getValue("PERSONAL_NO")); // 5卡号
		// A方法出参
		parm.addData("PASSWORD", insParm.getValue("PASSWORD")); // 6 密码
		parm.addData("AGENT_AMT", StringTool.round(interFaceParm
				.getDouble("AGENT_AMT"), 2)); //7补助金额
		parm.addData("ILLNESS_SUBSIDY_AMT", StringTool.round(interFaceParm
				.getDouble("ILLNESS_SUBSIDY_AMT"), 2)); //8城乡大病补助金额
		parm.addData("PARM_COUNT", 8);
		TParm result = commInterFace("DataDown_cmts", "G", parm);
		return result;

	}

	/**
	 * 城居门特结算确认返回参数:函数DataDown_cmts, 门特结算确认交易（I）
	 * 
	 * @param insParm
	 *            parm A 方法出参
	 * @param interFaceParm
	 *            parm 费用结算出参
	 * @param tempParm
	 *            parm 城居门特刷卡出参
	 * @return TParm
	 */
	public TParm DataDown_cmts_I(TParm insParm, TParm interFaceParm,
			TParm tempParm) {
		// double totAmt = insParm.getDouble("TOT_AMT");
		// // 医保支付金额
		// double accountAmt = interFaceParm.getDouble("TOTAL_AGENT_AMT")
		// + interFaceParm.getDouble("FLG_AGENT_AMT")
		// // + interFaceParm.getDouble("AGENT_AMT");
		// TParm accountParm = getAccountAmt(interFaceParm, tempParm,
		// "PERSON_ACCOUNT_AMT");
		// double accountPayAmt = accountParm.getDouble("ACCOUNT_AMT");
		// double uaccountPayAmt = accountParm.getDouble("UACCOUNT_AMT");
		TParm parm = new TParm();
		// System.out.println("accountPayAmt::::::"+accountParm);
		parm.addData("CONFIRM_NO", tempParm.getValue("CONFIRM_NO")); // 1 门诊顺序号
		// E
		// 方法出参
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2 医院编码
		parm.addData("PAT_TYPE", tempParm.getValue("PAT_TYPE")); // 3
		// 人员类别：11在职21退休51建国前老工人
		parm.addData("PAY_KIND", tempParm.getValue("PAY_KIND")); // 4
		// 支付类别12Ⅰ类门诊特殊病13
		// Ⅱ类门诊特殊病
		// 门特类别:1肾透析2肾移植抗排异3癌症放化疗4 糖尿病5肺心病6红斑狼疮7精神病8偏瘫9癫痫10 再生障碍性贫血11
		// 慢性血小板减少性紫癫21血友病22肝移植术后抗排异治疗
		parm.addData("DISEASE_CODE", insParm.getValue("DISEASE_CODE")); // 5
		// result.setData("ACCOUNT_PAY_AMT",
		// insParm.getDouble("TOT_ATM"));//帐户支付金额-------?????
		//
		parm.addData("INS_PAY_AMT", StringTool.round(interFaceParm
				.getDouble("INS_PAY_AMT"), 2)); // 6
		// 医保支付金额
		// 现金支付

		parm.addData("CASH_AMT", StringTool.round(insParm.getDouble("FeeY")
				- interFaceParm.getDouble("INS_PAY_AMT")
				- interFaceParm.getDouble("ILLNESS_SUBSIDY_AMT"), 2)); // 7
		// 现金支付金额--------???????
		parm.addData("UNREIM_AMT", StringTool.round(interFaceParm
				.getDouble("UNREIM_AMT"), 2)); // 8
		// 基金未报销金额
		parm.addData("PARM_COUNT", 8);
		TParm result = commInterFace("DataDown_cmts", "I", parm);
		return result;
	}

	/**
	 * 城居门特结算确认返回参数:函数DataDown_cmts, 门特结算确认交易（I） 门诊对账使用
	 * 
	 * @param insParm
	 *            TParm
	 * @param mzConfirmParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_I(TParm insParm, TParm mzConfirmParm) {
		TParm parm = new TParm();
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1 门诊顺序号 E
		// 方法出参
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2 医院编码
		parm.addData("PAT_TYPE", mzConfirmParm.getValue("CTZ_CODE")); // 3
		// 人员类别：11在职21退休51建国前老工人
		parm.addData("PAY_KIND", mzConfirmParm.getValue("PAY_KIND")); // 4
		// 支付类别12Ⅰ类门诊特殊病13
		// Ⅱ类门诊特殊病
		// 门特类别:1肾透析2肾移植抗排异3癌症放化疗4 糖尿病5肺心病6红斑狼疮7精神病8偏瘫9癫痫10 再生障碍性贫血11
		// 慢性血小板减少性紫癫21血友病22肝移植术后抗排异治疗
		parm.addData("DISEASE_CODE", mzConfirmParm.getValue("DISEASE_CODE")); // 5
		// result.setData("ACCOUNT_PAY_AMT",
		// insParm.getDouble("TOT_ATM"));//帐户支付金额-------?????
		//
		parm.addData("INS_PAY_AMT", StringTool.round(insParm
				.getDouble("INS_PAY_AMT"), 2)); // 6
		// 医保支付金额
		parm.addData("CASH_AMT", StringTool.round(insParm
				.getDouble("UACCOUNT_PAY_AMT")
				+ insParm.getDouble("UNREIM_AMT"), 2)); // 7
		// 现金支付金额--------???????
		parm.addData("UNREIM_AMT", StringTool.round(insParm
				.getDouble("UNREIM_AMT"), 2)); // 8
		// 基金未报销金额
		parm.addData("PARM_COUNT", 8);
		TParm result = commInterFace("DataDown_cmts", "I", parm);
		return result;
	}

	/**
	 * 取消交易公共方法
	 * 
	 * @param confirmNO
	 *            String
	 * @param regionCode
	 *            String
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm cancelDealClear(String confirmNO, String regionCode,
			String type) {
		TParm result = new TParm();
		result.addData("CONFIRM_NO", confirmNO); // 门诊顺序号 E
		// 或
		// L方法出参
		// result.addData("CASE_NO", insParm.getValue("CASE_NO"));// 就诊号 A
		// 方法中的参数
		result.addData("HOSP_NHI_NO", regionCode); // 医院编码
		// 如果为门诊退费费用撤销，那么撤销类型‘31’
		return result;
	}

	/**
	 * 城职普通取消交易方法:函数 DataDown_sp (S)方法
	 * 
	 * @param confirmNO
	 *            String
	 * @param regionCode
	 *            String
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_sp_S(String confirmNO, String regionCode, String type) {
		TParm parm = cancelDealClear(confirmNO, regionCode, type);
		parm.addData("DIS_GENRE", type); // 撤销类型如果为门诊正常费用撤销，那么撤销类型‘30’。
		parm.addData("PARM_COUNT", 3);
		TParm result = commInterFace("DataDown_sp", "S", parm);
		return result;
	}

	/**
	 * 自动对账使用 城职普通取消交易方法:函数 DataDown_sp (S)方法
	 * 
	 * @param insParm
	 *            TParm
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_sp_S(TParm insParm, String type) {
		TParm parm = cancelDealClear(insParm.getValue("CONFIRM_NO"), insParm
				.getValue("NHI_REGION_CODE"), "");
		parm.addData("DIS_GENRE", type); // 撤销类型如果为门诊正常费用撤销，那么撤销类型‘30’。
		parm.addData("PARM_COUNT", 3);
		TParm result = commInterFace("DataDown_sp", "S", parm);
		return result;
	}

	/**
	 * 城居门特取消交易方法:函数DataDown_cmts (J)方法
	 * 
	 * @param confirmNO
	 *            String
	 * @param regionCode
	 *            String
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_cmts_J(String confirmNO, String regionCode,
			String type) {
		TParm parm = cancelDealClear(confirmNO, regionCode, type);
		parm.addData("CANCEL_TYPE", type); // 撤销类型如果为门诊正常费用撤销，那么撤销类型‘30’。
		parm.addData("PARM_COUNT", 3);
		TParm result = commInterFace("DataDown_cmts", "J", parm);
		return result;
	}

	/**
	 * 城居门特取消交易方法:函数DataDown_cmts (J)方法 门诊对账
	 * 
	 * @param insParm
	 *            TParm
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_cmts_J(TParm insParm, String type) {
		TParm parm = cancelDealClear(insParm.getValue("CONFIRM_NO"), insParm
				.getValue("REGION_CODE"), type);
		parm.addData("CANCEL_TYPE", type); // 撤销类型如果为门诊正常费用撤销，那么撤销类型‘30’。
		parm.addData("PARM_COUNT", 3);
		TParm result = commInterFace("DataDown_cmts", "J", parm);
		return result;
	}

	/**
	 * 城职门特取消交易方法:函数DataDown_mts (J)方法
	 * 
	 * @param confirmNO
	 *            String
	 * @param regionCode
	 *            String
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_mts_J(String confirmNO, String regionCode, String type) {
		TParm parm = cancelDealClear(confirmNO, regionCode, type);
		parm.addData("CANCEL_TYPE", type); // 撤销类型如果为门诊正常费用撤销，那么撤销类型‘30’。
		parm.addData("PARM_COUNT", 3);
		TParm result = commInterFace("DataDown_mts", "J", parm);
		return result;
	}

	/**
	 * 城职门特取消交易方法:函数DataDown_mts (J)方法 对账使用
	 * 
	 * @param insParm
	 *            TParm
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_mts_J(TParm insParm, String type) {
		TParm parm = cancelDealClear(insParm.getValue("CONFIRM_NO"), insParm
				.getValue("REGION_CODE"), type);
		parm.addData("CANCEL_TYPE", type); // 撤销类型如果为门诊正常费用撤销，那么撤销类型‘30’。
		parm.addData("PARM_COUNT", 3);
		TParm result = commInterFace("DataDown_mts", "J", parm);
		return result;
	}

	/**
	 * 特殊情况公共方法
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm specialCase(TParm insParm) {
		// 查询是否可以存在特殊情况:挂号医保门特操作可以使用
		TParm secipalParm = new TParm();
		// secipalParm.setData("REGION_CODE", insParm.getValue("REGION_CODE"));
		secipalParm.setData("CASE_NO", insParm.getValue("CASE_NO"));
//		secipalParm.setData("CONFIRM_NO", insParm.getValue("CONFIRM_NO"));
		TParm result = INSMZConfirmTool.getInstance()
				.selectSpcMemo(secipalParm);
		if (result.getErrCode() < 0) {
			err("特殊情况" + result.getNames() + ":" + result.getErrText());
			return result;
		}
		if (result.getCount() <= 0
				|| result.getValue("SPC_MEMO", 0).length() <= 0) {
			result.setData("MESSAGE", "不存在特殊情况");
			return result;
		}
		return result;
	}

	/**
	 * 城职普通特殊情况方法：函数DataDown_sp 门诊特殊情况上传（Y）
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_Y(TParm insParm) {
		TParm parm = specialCase(insParm);
		// 不存在特殊情况
		if (null != parm.getValue("MESSAGE")
				&& parm.getValue("MESSAGE").length() > 0) {
			return parm;
		}
		TParm inparm = new TParm();
		inparm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1门诊顺序号
		// E
		// 或
		// L方法出参
		inparm.addData("SPC_MEMO", parm.getValue("SPC_MEMO", 0)); // 2特殊情况说明INS_MZ_CONFIRM中SPC_MEMO
		inparm.addData("PARM_COUNT", 2);
		TParm result = commInterFace("DataDown_sp", "Y", parm);
		return result;
	}

	/**
	 * 城居门特特殊情况方法：函数DataDown_cmts，门特特殊情况上传交易（H）
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_H(TParm insParm) {
		TParm parm = specialCase(insParm);
		if (null != parm.getValue("MESSAGE")
				&& parm.getValue("MESSAGE").length() > 0) {
			return parm;
		}
		TParm inparm = new TParm();
		inparm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1门诊顺序号
		// E
		// 或
		// L方法出参
		inparm.addData("SPECIAL_REMARK", parm.getValue("SPC_MEMO", 0)); // 2特殊情况说明INS_MZ_CONFIRM中SPC_MEMO
		parm.addData("PARM_COUNT", 2);
		TParm result = commInterFace("DataDown_cmts", "H", inparm);
		return result;
	}

	/**
	 * 城职门特特殊情况方法：函数DataDown_sp，门特特殊情况上传交易（H）
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_H(TParm insParm) {
		TParm parm = specialCase(insParm);
		if (null != parm.getValue("MESSAGE")
				&& parm.getValue("MESSAGE").length() > 0) {
			return parm;
		}
		TParm inparm = new TParm();
		inparm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1门诊顺序号
		// E
		// 或
		// L方法出参
		inparm.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE", 0)); // 2特殊情况说明INS_MZ_CONFIRM中REGION_CODE
		inparm.addData("PARM_COUNT", 2);
		TParm result = commInterFace("DataDown_sp", "H", inparm);
		return result;
	}

	/**
	 * 特殊情况(类型 :1.城职普通 、2.城职门特、3.城居门特)
	 * 
	 * @param insParm
	 *            TParm
	 * @param type
	 *            int
	 * @return TParm
	 */
	public TParm specialCaseCommReturn(TParm insParm, int type) {
		TParm result = new TParm();
		// System.out.println("type--------------------------------------" +
		// type);
		switch (type) {
		case 1:
			result = DataDown_sp_Y(insParm);
			break;
		case 2:
			result = DataDown_sp_H(insParm);
			break;
		case 3:
			result = DataDown_cmts_H(insParm);
			break;
		}
		if (result.getErrCode() < 0) {
			result.setErr(-1, "特殊信息上传出现错误");
			return result;
		}
		if (null != result.getValue("MESSAGE")
				&& result.getValue("MESSAGE").length() > 0) {
			// System.out.println(result.getValue("MESSAGE"));
		}
		return result;
	}

	/**
	 * 刷卡动作 函数DataDown_sp,取卡号（U）
	 * 
	 * @param text
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_sp_U(String text) {
		// System.out.println("进入读卡");
		TParm parm = new TParm();
		parm.addData("TEXT", text);
		parm.addData("PARM_COUNT", 1); // 参数数量
		// TParm result =new TParm();
		// result.setData("NHI_NO","622331206241731001");
		TParm result = commInterFace("DataDown_sp", "U", parm);
		// System.out.println("------------U方法----------------" + result);
		// String cardNo = result.getValue("CARD_NO");
		// result.setData("CARD_NO",text);// 医保卡号
		// result.setData("CARD_NO", "609120106281322899");// 医保卡号
		return result;
	}

	/**
	 * 调用险种识别交易 函数DataDown_czys, 获取人群类别信息（A）
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_A(TParm insParm) {
		TParm parmA = new TParm();
		parmA.addData("CARD_NO", insParm.getValue("CARD_NO")); // 医保卡号
		parmA.addData("TYPE", insParm.getValue("TYPE")); // 传入类型:1社保卡卡号2 身份证号码
		// 传固定值 1
		parmA.addData("NHI_HOSPCODE", insParm.getValue("ADVANCE_CODE")); // 医院编码
		parmA.addData("COMU_NO", null); // 社区编码
		parmA.addData("PARM_COUNT", 4); // 参数数量
		// parmA.setData("PROGRAM_STATE", "Y");// 程序执行状态
		// parmA.setData("PROGRAM_MESSAGE", "执行成功");// 程序执行信息
		// 返回参数
		// parm.addData("TEXT", text);
		// System.out.println("ERRORRRSSSSSS");
		TParm result = commInterFace("DataDown_czys", "A", parmA);
		// System.out.println("---------------a方法---------------" + result);
		// result.setData("SID", "130323197812152627");// 身份号码
		// result.setData("PAT_NAME", "丽思");// 姓名
		// result.setData("PAT_AGE", "21");// 年龄
		// result.setData("SEX_CODE", "1");// 性别
		// result.setData("COMPANY_DESC", "天津市");// 单位名称
		// result.setData("OWN_NO", "1011231111");// 个人编码
		// result.setData("CROWD_TYPE", "1");// 人群类别1 城镇职工(包括城职、生育、工伤)2
		// // 城乡居民当参数保存,后续用做程序分支判断
		// result.setData("CHECK_CODES", "12232312321");// 刷卡验证码
		// result.setData("CARD_NO", parm.getValue("CARD_NO"));// 医保卡号
		// result.setData("PROGRAM_STATE", "Y");// 程序执行状态
		// result.setData("PROGRAM_MESSAGE", "执行成功");// 程序执行信息
		return result;
	}

	/**
	 * 住院城居 资格确认书开立操作 查询社保个人信息 函数DataDown_czyd,（A）方法
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czyd_A(TParm insParm) {
		TParm parmA = new TParm();
		parmA.addData("SID", insParm.getValue("IDNO1")); // 身份证号
		parmA.addData("NAME", insParm.getValue("PAT_NAME1")); // 姓名
		parmA.addData("PARM_COUNT", 2); // 参数数量
		// parmA.setData("PROGRAM_STATE", "Y");// 程序执行状态
		// parmA.setData("PROGRAM_MESSAGE", "执行成功");// 程序执行信息
		// 返回参数

		// parm.addData("TEXT", text);
		TParm result = commInterFace("DataDown_czyd", "A", parmA);
		// System.out.println("---------------a方法---------------" + result);
		// result.setData("SID", "130323197812152627");// 身份号码
		// result.setData("PAT_NAME", "丽思");// 姓名
		// result.setData("PAT_AGE", "21");// 年龄
		// result.setData("SEX_CODE", "1");// 性别
		// result.setData("COMPANY_DESC", "天津市");// 单位名称
		// result.setData("OWN_NO", "1011231111");// 个人编码
		// result.setData("CROWD_TYPE", "1");// 人群类别1 城镇职工(包括城职、生育、工伤)2
		// // 城乡居民当参数保存,后续用做程序分支判断
		// result.setData("CHECK_CODES", "12232312321");// 刷卡验证码
		// result.setData("CARD_NO", parm.getValue("CARD_NO"));// 医保卡号
		// result.setData("PROGRAM_STATE", "Y");// 程序执行状态
		// result.setData("PROGRAM_MESSAGE", "执行成功");// 程序执行信息
		return result;
	}

	/**
	 * 调用公共接口方法
	 * 
	 * @param object
	 *            String
	 * @param function
	 *            String
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm commInterFace(String object, String function, TParm parm) {
		// return ()callServerMethod(object,function,function);
		parm.setData("PIPELINE", object);
		parm.setData("PLOT_TYPE", function);
		parm.setData("HOSP_AREA", "HIS");
		// System.out.println("-----------commInterFace--------------" + parm);
		// INSInterface Interface=new INSInterface();
		TParm result = InsManager.getInstance().safe(parm);
		// result = testDataTool.getInstance().testOut(parm);
		// System.out.println("-----------commInterFace--------------" +result);
		// if(result.getErrCode()<0){
		// //System.out.println("出参公共接口错误:"+result.getErrText());
		// }
		// parm.setData("PIPELINE",object);
		// parm.setData("PLOT_TYPE",function);
		// parm.setData("HOSP_AREA","HIS");
		// TParm result=insDatatestTool.getInstance().safe(parm);
		// result=testDataTool.getInstance().testOut(parm);
		return result;
	}

	/**
	 * 住院城职 累计增负计算 函数 DataDown_sp1 (C)方法
	 * 
	 * @return TParm
	 */
	public TParm DataDown_sp1_C() {
		TParm parm = new TParm();
		parm.addData("ADDPAY_ADD", ""); // 累计增负发生总额:在费用明细分割后返回的所有累计增负标志为1的所有明细的发生金额之和
		parm.addData("HOSP_START_DATE", ""); // 住院开始时间
		parm.addData("PARM_COUNT", 2);
		TParm result = commInterFace("DataDown_sp1", "C", parm);
		return result;
	}

	// /**
	// * 城居 ：门诊退费 函数 DataDown_cmzs （F）方法
	// *
	// * @return
	// */
	// public TParm DataDown_cmzs_F(TParm parm) {
	// TParm parmF = new TParm();
	// parmF.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE"));// 医院编码
	// parmF.addData("COMU_NO", null);// 社区编码
	// parmF.addData("PARM_COUNT", 4);// 参数数量
	// parmF.addData("PARM_COUNT", 4);// 门诊顺序号
	// parmF.addData("PARM_COUNT", 4);// 医院编码
	// parmF.addData("PARM_COUNT", 4);// 医院操作员编码
	// parmF.addData("PARM_COUNT", 4);// 发生金额
	// parmF.addData("PARM_COUNT", 4);// 申报金额
	// parmF.addData("PARM_COUNT", 4);// 全自费金额
	// parmF.addData("PARM_COUNT", 4);// 增负金额
	// parmF.addData("PARM_COUNT", 4);// 专项基金社保支付
	// parmF.addData("PARM_COUNT", 4);// 补助金额
	// TParm result = commInterFace("DataDown_cmzs", "F", parm);
	// //System.out.println("城职普通 门诊退费   函数 DataDown_cmzs （F）:" + parmF);
	// // 程序执行状态
	// // 程序执行信息
	// // 社保对帐标志
	// // 中心交易时间
	// return result;
	// }
	/**
	 * 城职普通 门诊退费 函数 DataDown_yb （C）方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_yb_C(TParm parm) {
		TParm parmC = new TParm();
		parmC.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 顺序号
		parmC.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE")); // 2 医院编码
		parmC
				.addData("HOSP_OPT_USER_CODE", parm
						.getValue("HOSP_OPT_USER_CODE")); // 3 医院操作员编码
		parmC.addData("TOTAL_AMT", StringTool.round(Math.abs(parm
				.getDouble("TOT_AMT")), 2)); // 4
		// 发生金额
		parmC.addData("TOTAL_NHI_AMT", StringTool.round(Math.abs(parm
				.getDouble("NHI_AMT")), 2)); // 5
		// 申报金额
		parmC.addData("OWN_AMT", StringTool.round(Math.abs(parm
				.getDouble("OWN_AMT")), 2)); // 6
		// 全自费金额
		parmC.addData("ADDPAY_AMT", StringTool.round(Math.abs(parm
				.getDouble("ADD_AMT")), 2)); // 7
		// 增负金额
		parmC.addData("ACCOUNT_PAY_AMT", StringTool.round(Math.abs(parm
				.getDouble("ACCOUNT_PAY_AMT")), 2)); // 8
		// 专项基金社保支付---？？？
		parmC.addData("OTOT_AMT", StringTool.round(Math.abs(parm
				.getDouble("OTOT_AMT")), 2)); // 9
		// 个人帐户实际支付金额
		parmC.addData("AGENT_AMT", StringTool.round(Math.abs(parm
				.getDouble("ARMY_AI_AMT")), 2)); // 10
		// 补助金额
		parmC.addData("PARM_COUNT", 10); // PARM_COUNT
		// System.out.println("parmC::::::::::" + parmC);
		TParm result = commInterFace("DataDown_yb", "C", parmC);
		return result;
	}

	/**
	 * 城职普通 退费确认交易 DataDown_yb 函数 （D）方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_yb_D(TParm parm) {
		TParm parmD = new TParm();
		parmD.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 顺序号
		parmD.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE")); // 医院编码
		parmD.addData("OTOT_AMT", parm.getDouble("OTOT_AMT")); // 专项基金社保支付
		parmD.addData("ACCOUNT_PAY_AMT", StringTool.round(parm
				.getDouble("ACCOUNT_PAY_AMT"), 2)); // 个人帐户实际支付金额----?????
		parmD.addData("PARM_COUNT", 4);
		// System.out.println("城职普通 退费确认交易 DataDown_yb 函数  （D）方法入参:" + parmD);
		TParm result = commInterFace("DataDown_yb", "D", parmD);
		return result;
	}

	/**
	 * 城职门特 ： 门特退费交易 DataDown_mts 函数 （K）
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_K(TParm parm) {
		TParm parmK = DataDown_cmtsOrmts_K(parm);
		parmK.addData("SERVANT_AMT", 0.00); // 补助金额2
		parmK.addData("ACCOUNT_PAY_AMT", StringTool.round(Math.abs(parm
				.getDouble("ACCOUNT_PAY_AMT")), 2)); // 个人帐户实际支付金额
		parmK.addData("PARM_COUNT", 17);
		TParm result = commInterFace("DataDown_mts", "K", parmK);
		// System.out.println("城职门特 ：门特退费交易 DataDown_mts 函数 （K）:" + result);
		return result;
	}

	/**
	 * 城职门特 ： 退费确认交易 DataDown_mts 函数 （L）
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_L(TParm parm) {
		TParm parmL = DataDown_mtsOrcmts_L(parm);
		parmL.addData("ACCOUNT_PAY_AMT", null == parm
				.getValue("ACCOUNT_PAY_AMT") ? 0.00 : parm
				.getDouble("ACCOUNT_PAY_AMT")); // 个人帐户实际支付金额
		parmL.addData("PARM_COUNT", 5);
		// System.out.println("城职门特 ：退费确认交易 DataDown_mts 函数 (L)入参:" + parmL);
		TParm result = commInterFace("DataDown_mts", "L", parmL);
		// System.out.println("城职门特 ：退费确认交易 DataDown_mts 函数 （L）:" + result);
		return result;
	}

	/**
	 * 城居门特 退费确认交易 DataDown_cmts 函数 （K）
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_K(TParm parm) {
		TParm parmK = DataDown_cmtsOrmts_K(parm);
		parmK.addData("PARM_COUNT", 16);
		// System.out.println("城居门特 退费确认交易 DataDown_cmts 函数 （K）入参:" + parmK);
		TParm result = commInterFace("DataDown_cmts", "K", parmK);
		// System.out.println("城居门特 退费确认交易 DataDown_cmts 函数 （K）:" + result);
		return result;
	}

	/**
	 * 城职门特/城居门特 城居门特: 退费确认交易 DataDown_cmts 函数 （K） 城职门特: 门特退费交易 DataDown_mts 函数
	 * K方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_cmtsOrmts_K(TParm parm) {
		TParm parmK = new TParm();
		parmK.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 门特就医顺序号
		parmK.addData("HOSP_NHI_NO", parm.getValue("HOSP_NHI_NO")); // 2
		// 医院(或药店)编码
		parmK.addData("OPT_USER", parm.getValue("OPT_USER")); // 3 门特操作员编码
		String sql = "SELECT NHI_NO FROM SYS_CTZ WHERE CTZ_CODE='"
				+ parm.getValue("PAT_TYPE") + "'";
		// System.out.println("SEL::::::::"+sql);
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(sql));
		parmK.addData("PAT_TYPE", ctzParm.getValue("NHI_NO", 0)); // 4
		// 人员类别--？？？
		parmK.addData("PAY_KIND", parm.getValue("PAY_KIND")); // 5
		// 支付类别--？？？INS_PAT_TYPE
		parmK.addData("DISEASE_CODE", parm.getValue("DISEASE_CODE")); // 6
		// 门特类别--？？？
		parmK.addData("BCSSQF_STANDRD_AMT", StringTool.round(Math.abs(parm
				.getDouble("BCSSQF_STANDRD_AMT")), 2)); // 7
		// 本次实收起付标准金额
		parmK.addData("INS_STANDARD_AMT", StringTool.round(Math.abs(parm
				.getDouble("INS_STANDARD_AMT")), 2)); // 8
		// 起付标准以上自负比例金额
		parmK.addData("OWN_AMT", Math.abs(parm.getDouble("OWN_AMT"))); // 9
		// 自费项目金额
		parmK.addData("PERCOPAYMENT_RATE_AMT", StringTool.round(Math.abs(parm
				.getDouble("PERCOPAYMENT_RATE_AMT")), 2)); // 10 医疗救助个人按比例负担金额
		parmK.addData("ADD_AMT", StringTool.round(Math.abs(parm
				.getDouble("ADD_AMT")), 2)); // 11
		// 增负项目金额
		parmK.addData("INS_HIGHLIMIT_AMT", StringTool.round(Math.abs(parm
				.getDouble("INS_HIGHLIMIT_AMT")), 2)); // 12
		// 医疗救助最高限额以上金额
		parmK.addData("TOTAL_AGENT_AMT", StringTool.round(Math.abs(parm
				.getDouble("TOTAL_AGENT_AMT")), 2)); // 13
		// 基本医疗社保申请金额
		parmK.addData("FLG_AGENT_AMT", StringTool.round(Math.abs(parm
				.getDouble("FLG_AGENT_AMT")), 2)); // 14
		// 医疗救助社保申请金额
		parmK.addData("ARMY_AI_AMT", StringTool.round(Math.abs(parm
				.getDouble("ARMY_AI_AMT")), 2)); // 15补助金额
		//城乡大病补助金额
		parmK.addData("ILLNESS_SUBSIDY_AMT", StringTool.round(Math.abs(parm
				.getDouble("ILLNESS_SUBSIDY_AMT")), 2));
		return parmK;
	}

	/**
	 * 城居门特 ： 退费确认交易 DataDown_cmts 函数 （L）
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_L(TParm parm) {
		TParm parmL = DataDown_mtsOrcmts_L(parm);
		parmL.addData("PARM_COUNT", 4);
		// System.out.println("城居门特 ：退费确认交易 DataDown_cmts 函数 （L）入参:" + parmL);
		TParm result = commInterFace("DataDown_cmts", "L", parmL);
		// System.out.println("城居门特 ：退费确认交易 DataDown_cmts 函数 （L）:" + result);
		return result;
	}

	/**
	 * 城职门特/城居门特 城居门特 ： 退费确认交易 DataDown_cmts 函数 （L） 城职门特: 退费确认交易 DataDown_mts
	 * 函数L方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mtsOrcmts_L(TParm parm) {
		TParm parmL = new TParm();
		parmL.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 门特就医顺序号
		parmL.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 2
		// 医院(或药店)编码
		parmL.addData("TOTAL_AGENT_AMT", StringTool.round(parm
				.getDouble("TOTAL_AGENT_AMT"), 2)); // 3
		// 基本医疗社保申请金额
		parmL.addData("FLG_AGENT_AMT", StringTool.round(parm
				.getDouble("FLG_AGENT_AMT"), 2)); // 4
		// 医疗救助社保申请金额
		return parmL;
	}

	/**
	 * 门诊对总账操作 城职普通 ：DataDown_sp函数 O 方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_O(TParm parm) {
		TParm parmO = new TParm();
		parmO.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE")); // 1
		// HOSP_NHI_NO
		// 医院编码
		parmO.addData("HOSP_OPT_USER_CODE", parm.getValue("OPT_USER")); // 2
		// HOSP_OPT_USER_CODE
		// 医院操作员编码
		parmO.addData("OWN_NO", parm.getValue("OWN_NO")); // 3 OWN_NO 个人编码
		// -----?????
		parmO.addData("COLLATE_ACCOUNT_TIME", parm.getData("ACCOUNT_DATE")); // 4
		// COLLATE_ACCOUNT_TIME
		// 对帐时间
		parmO.addData("PAY_TYPE", parm.getValue("PAY_KIND")); // 5 PAY_TYPE 支付类别
		parmO.addData("TOTAL_AMT", StringTool.round(
				parm.getDouble("TOTAL_AMT"), 2)); // 6 TOTAL_AMT
		// 发生金额
		parmO
				.addData("NHI_AMT", StringTool.round(parm.getDouble("NHI_AMT"),
						2)); // 7 NHI_AMT 申报金额
		parmO
				.addData("OWN_AMT", StringTool.round(parm.getDouble("OWN_AMT"),
						2)); // 8 OWN_AMT 全自费金额
		parmO.addData("ADDPAY_AMT", StringTool.round(parm
				.getDouble("ADDPAY_AMT"), 2)); // 9
		// ADDPAY_AMT
		// 增负金额
		parmO.addData("OTOT_AMT", StringTool.round(parm.getDouble("OTOT_AMT"),
				2)); // 10 OTOT_AMT
		// 专项基金社保支付
		parmO.addData("ACCOUNT_PAY_AMT", StringTool.round(parm
				.getDouble("ACCOUNT_PAY_AMT"), 2)); // 11
		// ACCOUNT_PAY_AMT
		// 个人帐户实际支付金额
		parmO.addData("ALL_TIME", parm.getInt("ALL_TIME")); // 12 ALL_TIME 总人次
		parmO.addData("OTOT_OUT_AMT", parm.getDouble("OTOT_OUT_AMT")); // 13
		// OTOT_OUT_AMT
		// 专项基金社保支付(退费)
		parmO.addData("ACCOUNT_PAY_AMT_EXIT", StringTool.round(parm
				.getDouble("ACCOUNT_PAY_AMT_EXIT"), 2)); // 14
															// ACCOUNT_PAY_AMT_EXIT
		// 个人帐户实际支付金额(退费)
		parmO.addData("ALL_TIME_EXIT", parm.getInt("ALL_TIME_EXIT")); // 15
		// ALL_TIME_EXIT
		// 总人次(退费)
		parmO.addData("AGENT_AMT", StringTool.round(
				parm.getDouble("AGENT_AMT"), 2)); // 16 AGENT_AMT
		// 民政救助补助金额
		parmO.addData("AGENT_AMT_OUT", StringTool.round(parm
				.getDouble("AGENT_AMT_OUT"), 2)); // 17
		// AGENT_AMT_OUT
		// 民政救助补助金额(退费)
		parmO.addData("FY_AGENT_AMT", StringTool.round(parm
				.getDouble("FY_AGENT_AMT"), 2)); // 18
		// FY_AGENT_AMT
		// 优抚对象补助金额
		parmO.addData("FY_AGENT_AMT_B", StringTool.round(parm
				.getDouble("FY_AGENT_AMT_B"), 2)); // 19
		// FY_AGENT_AMT_B
		// 优抚对象补助金额(退费)
		parmO.addData("FD_AGENT_AMT", StringTool.round(parm
				.getDouble("FD_AGENT_AMT"), 2)); // 20
		// FD_AGENT_AMT
		// 非典后遗症补助金额
		parmO.addData("FD_AGENT_AMT_B", StringTool.round(parm
				.getDouble("FD_AGENT_AMT_B"), 2)); // 21
		// FD_AGENT_AMT_B
		// 非典后遗症补助金额退费)
		parmO.addData("UNREIM_AMT", StringTool.round(parm
				.getDouble("UNREIM_AMT"), 2)); // 22
		// UNREIM_AMT
		// 基金未报销金
		parmO.addData("UNREIM_AMT_B", StringTool.round(parm
				.getDouble("UNREIM_AMT_B"), 2)); // 23
		// UNREIM_AMT_B
		// 基金未报销金额（退费）
		//公务员补助金额
		parmO.addData("SERVANT_AMT", StringTool.round(parm
				.getDouble("SERVANT_AMT"), 2)); // 24
		//公务员补助金额(退费)
		parmO.addData("SERVANT_AMT_B", StringTool.round(parm
				.getDouble("SERVANT_AMT_B"), 2)); // 25
		//军残补助金额
		parmO.addData("ARMY_AI_AMT", StringTool.round(parm
				.getDouble("ARMY_AI_AMT"), 2)); // 26
		//军残补助金额(退费)
		parmO.addData("ARMY_AI_AMT_B", StringTool.round(parm
				.getDouble("ARMY_AI_AMT_B"), 2)); // 27
		parmO.addData("PARM_COUNT", 27);
		// System.out.println("城职普通 ：DataDown_sp函数 O 方法入参:" + parmO);
		TParm result = commInterFace("DataDown_sp", "O", parmO);
		// System.out.println("城职普通 ：DataDown_sp函数 O 方法:" + result);
		return result;
	}

	/**
	 * 门诊对总账操作 城职门特 ：DataDown_mts函数 M 方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_M(TParm parm) {
		TParm parmM = DataDown_mtsAndcmtsUp(parm);
		parmM.addData("ARMY_AI_AMT", StringTool.round(parm
				.getDouble("ARMY_AI_AMT"), 2)); // 14
		// ARMY_AI_AMT
		// 军残补助金额(所有正常)
		// -----
		parmM.addData("ARMY_AI_AMT_B", StringTool.round(parm
				.getDouble("ARMY_AI_AMT_B"), 2)); // 15
		// ARMY_AI_AMT_B
		// 军残补助金额(所有退费)
		// ------
		parmM.addData("SERVANT_AMT", StringTool.round(parm
				.getDouble("SERVANT_AMT"), 2)); // 16
		// SERVANT_AMT
		// 公务员补助金额(所有正常)
		// ----
		parmM.addData("SERVANT_AMT_B", StringTool.round(parm
				.getDouble("SERVANT_AMT_B"), 2)); // 17
		// SERVANT_AMT_B
		// 公务员补助金额(所有退费)
		// -----
		parmM.addData("ACCOUNT_PAY_AMT", StringTool.round(parm
				.getDouble("ACCOUNT_PAY_AMT"), 2)); // 18
		// ACCOUNT_PAY_AMT
		// 个人帐户实际支付金额(所有正常)
		// ----
		parmM.addData("ACCOUNT_PAY_AMT_B", StringTool.round(parm
				.getDouble("ACCOUNT_PAY_AMT_B"), 2)); // 19
		// ACCOUNT_PAY_AMT_B
		// 个人帐户实际支付金额(所有退费)
		// ----
		parmM = DataDown_mtsAndcmtsDown(parmM);
		parmM.addData("PARM_COUNT", 27);
		// System.out.println("城职门特 ：DataDown_mts函数 M 方法入参:" + parmM);
		TParm result = commInterFace("DataDown_mts", "M", parmM);
		// System.out.println("城职门特 ：DataDown_mts函数 M方法:" + result);
		return result;
	}

	/**
	 * 门诊对总账操作 城居门特 ：DataDown_cmts函数 M 方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_M(TParm parm) {
		TParm parmM = DataDown_mtsAndcmtsDown(DataDown_mtsAndcmtsUp(parm));
		parmM.addData("PARM_COUNT", 23);
		// System.out.println("城居门特 ：DataDown_cmts函数 O 方法入参:" + parmM);
		TParm result = commInterFace("DataDown_cmts", "M", parmM);
		// System.out.println("城居门特 ：DataDown_cmts函数 M方法:" + result);
		return result;
	}

	/**
	 * 门诊对总账入参
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_mtsAndcmtsUp(TParm parm) {
		TParm parmM = parm;
		parmM.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE")); // 1
		// HOSP_NHI_NO
		// 医院编码
		parmM.addData("OPT_USER", parm.getValue("OPT_USER")); // 2 OPT_USER
		// 门特操作员编码
		parmM.addData("OWN_NO", parm.getValue("OWN_NO")); // 3 OWN_NO 个人编码
		// VARCHAR2
		parmM.addData("COLLATE_ACCOUNT_TIME", parm.getData("ACCOUNT_DATE")); // 4
		// COLLATE_ACCOUNT_TIME
		// 对帐时间
		parmM.addData("TOTAL_AMT", parm.getDouble("TOTAL_AMT")); // 5 TOTAL_AMT
		// 发生金额(所有正常)
		parmM.addData("APPLY_AMT", parm.getDouble("APPLY_AMT")); // 6 APPLY_AMT
		// 统筹基金申报金额(所有正常)
		parmM.addData("FLG_AGENT_AMT", parm.getDouble("FLG_AGENT_AMT")); // 7
		// FLG_AGENT_AMT
		// 救助基金申报金额(所有正常)
		parmM.addData("OWN_AMT", parm.getDouble("OWN_AMT")); // 8 OWN_AMT
		// 全自费金额(所有正常)
		parmM.addData("ADD_AMT", parm.getDouble("ADD_AMT")); // 9 ADD_AMT
		// 增负金额(所有正常)
		parmM.addData("SUM_PERTIME", parm.getInt("SUM_PERTIME")); // 10
		// SUM_PERTIME
		// 总人次(所有正常)
		parmM.addData("APPLY_AMT_B", parm.getDouble("APPLY_AMT_B")); // 11
		// APPLY_AMT_B
		// 统筹基金社保支付金额（退费）
		parmM.addData("FLG_AGENT_AMT_B", parm.getDouble("FLG_AGENT_AMT_B")); // 12
		// FLG_AGENT_AMT_B
		// 医疗救助支付金额（退费）
		parmM.addData("SUM_PERTIME_B", parm.getDouble("SUM_PERTIME_B")); // 13
		// SUM_PERTIME_B
		// 总人次（退费）	
		return parmM;
	}

	/**
	 * 门诊对总账入参
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_mtsAndcmtsDown(TParm parm) {
		TParm parmM = parm;
		parmM.addData("MZ_AGENT_AMT", StringTool.round(parm
				.getDouble("MZ_AGENT_AMT"), 2)); // MZ_AGENT_AMT
		// 民政救助补助金额
		parmM.addData("MZ_AGENT_AMT_B", StringTool.round(parm
				.getDouble("MZ_AGENT_AMT_B"), 2)); // MZ_AGENT_AMT_B
		// 民政救助补助金额(退费)
		parmM.addData("FY_AGENT_AMT", StringTool.round(parm
				.getDouble("FY_AGENT_AMT"), 2)); // FY_AGENT_AMT
		// 优抚对象补助金额
		parmM.addData("FY_AGENT_AMT_B", StringTool.round(parm
				.getDouble("FY_AGENT_AMT_B"), 2)); // FY_AGENT_AMT_B
		// 优抚对象补助金额(退费)
		parmM.addData("FD_AGENT_AMT", StringTool.round(parm
				.getDouble("FD_AGENT_AMT"), 2)); // FD_AGENT_AMT
		// 非典后遗症补助金额
		parmM.addData("FD_AGENT_AMT_B", StringTool.round(parm
				.getDouble("FD_AGENT_AMT_B"), 2)); // FD_AGENT_AMT_B
		// 非典后遗症补助金额退费)
		parmM.addData("UNREIM_AMT", StringTool.round(parm
				.getDouble("UNREIM_AMT"), 2)); // UNREIM_AMT
		// 基金未报销金额
		parmM.addData("UNREIM_AMT_B", StringTool.round(parm
				.getDouble("UNREIM_AMT_B"), 2)); // UNREIM_AMT_B
		// 基金未报销金额（退费）		
		parmM.addData("ILLNESS_SUBSIDY_AMT", StringTool.round(parm
				.getDouble("ILLNESS_SUBSIDY_AMT"), 2)); // ILLNESS_SUBSIDY_AMT
		// 城乡大病补助金额
		parmM.addData("ILLNESS_SUBSIDY_AMT_B", StringTool.round(parm
				.getDouble("ILLNESS_SUBSIDY_AMT_B"), 2)); // ILLNESS_SUBSIDY_AMT_B
		// 城乡大病补助金额（退费）
		
		return parmM;
	}

	/**
	 * 城职普通 明细对账 函数DataDown_rs (M) 方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_rs_M(TParm parm) {
		TParm parmM = new TParm();
		// System.out.println("parm:::::" + parm);
		parmM.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE")); // 医院编码HOSP_NHI_NO
		parmM.addData("OPT_USER_CODE", parm.getValue("OPT_USER")); // 操作员编号OPT_USER_CODE
		parmM.addData("PERSONAL_NO", parm.getValue("PERSONAL_NO"));// 个人编码
		parmM.addData("COLLATE_ACCOUNT_TIME", parm.getData("ACCOUNT_DATE")); // 对帐时间COLLATE_ACCOUNT_TIME
		// 门特就医顺序号
		// 退费标志
		parmM.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 门特就医顺序号
		parmM.addData("UN_FLG", parm.getValue("UN_FLG")); // 退费标志
		// 参数不同
		parmM.addData("PARM_COUNT", 6);
		// System.out.println("城职普通：DataDown_rs_M函数 M 方法入参:" + parmM);
		TParm result = commInterFace("DataDown_rs", "M", parmM);
		// System.out.println("城职普通 ：DataDown_rs函数 M方法:" + result);
		return result;
	}

	/**
	 * 城职门特 明细对账 函数DataDown_mtd （E）方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mtd_E(TParm parm) {
		TParm parmE = new TParm();
		parmE.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE")); // 医院编码HOSP_NHI_NO
		parmE.addData("OPT_USER_CODE", parm.getValue("OPT_USER")); // 操作员编号OPT_USER_CODE
		parmE.addData("PERSONAL_NO", parm.getValue("PERSONAL_NO"));// 个人编码
		parmE.addData("COLLATE_ACCOUNT_TIME", parm.getData("ACCOUNT_DATE")); // 对帐时间COLLATE_ACCOUNT_TIME
		// 门特就医顺序号
		// 退费标志
		parmE.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 门特就医顺序号
		parmE.addData("UN_FLG", parm.getValue("UN_FLG")); // 退费标志
		// 参数不同
		parmE.addData("PARM_COUNT", 6);
		// System.out.println("城职门特 ：DataDown_mtd_E函数 M 方法入参:" + parmE);
		TParm result = commInterFace("DataDown_mtd", "E", parmE);
		// System.out.println("城职门特 ：DataDown_mtd_E函数 E方法:" + result);
		return result;
	}

	/**
	 * 城居门特 明细对账 函数DataDown_cmtd （E）方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmtd_E(TParm parm) {
		TParm parmE = new TParm();
		parmE.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE")); // 医院编码HOSP_NHI_NO
		parmE.addData("OPT_USER_CODE", parm.getValue("OPT_USER")); // 操作员编号OPT_USER_CODE
		parmE.addData("PERSONAL_NO", parm.getValue("PERSONAL_NO"));// 个人编码
		parmE.addData("COLLATE_ACCOUNT_TIME", parm.getData("ACCOUNT_DATE")); // 对帐时间COLLATE_ACCOUNT_TIME
		// 门特就医顺序号
		// 退费标志
		parmE.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 门特就医顺序号
		parmE.addData("UN_FLG", parm.getValue("UN_FLG")); // 退费标志
		// 参数不同
		parmE.addData("PARM_COUNT", 6);
		// System.out.println("城居门特 ：DataDown_cmtd_E函数 M 方法入参:" + parmE);
		TParm result = commInterFace("DataDown_cmtd", "E", parmE);
		// System.out.println("城居门特 ：DataDown_cmtd_E函数 E方法:" + result);
		return result;
	}

	/**
	 * 城职 门特 门特登记锁病患 函数 DataDown_mts （A）方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_A(TParm parm) {
		// System.out.println("城职 门特 门特登记锁病患::"+parm);
		TParm parmA = DataDown_mtsAndcmts_A(parm);
		// System.out.println("城职门特：DataDown_mts函数 A 方法入参:" + parmA);
		TParm result = commInterFace("DataDown_mts", "A", parmA);
		// System.out.println("城职 门特：DataDown_mts函数 A方法:" + result);
		return result;
	}

	/**
	 * 城居门特 门特登记锁病患 函数 DataDown_cmts （A）方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_A(TParm parm) {
		TParm parmA = DataDown_mtsAndcmts_A(parm);
		// System.out.println("城居 门特：DataDown_cmts函数 A 方法入参:" + parmA);
		TParm result = commInterFace("DataDown_cmts", "A", parmA);
		// System.out.println("城居门特 ：DataDown_cmts函数 A方法:" + result);
		return result;
	}

	/**
	 * 门特登记锁病患 共用
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_mtsAndcmts_A(TParm parm) {
		TParm parmA = new TParm();
		parmA.addData("CARD_NO", parm.getValue("PERSONAL_NO")); // 社保卡号CARD_NO
		parmA.addData("DISEASE_CODE", parm.getValue("DISEASE_CODE")); // 门特病种编码DISEASE_CODE
		parmA.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 医院(或药店)编码HOSP_NHI_NO----?????
		parmA.addData("PARM_COUNT", 3);
		return parmA;
	}

	/**
	 * 城职门特 医院下载上次门特信息 函数DataDown_mts （C）方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_C(TParm parm) {
		TParm parmC = DataDown_mtsAndcmts_C(parm);
		// System.out.println("城职门特：DataDown_mts函数 C 方法入参:" + parmC);
		TParm result = commInterFace("DataDown_mts", "C", parmC);
		// System.out.println("城职门特 ：DataDown_mts函数 C方法:" + result);
		return result;
	}

	/**
	 * 城居门特 医院下载上次门特信息 函数DataDown_cmts （C）方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_C(TParm parm) {
		TParm parmC = DataDown_mtsAndcmts_C(parm);
		// System.out.println("城居门特：DataDown_cmts函数 C 方法入参:" + parmC);
		TParm result = commInterFace("DataDown_cmts", "C", parmC);
		// System.out.println("城居门特 ：DataDown_cmts函数 C方法:" + result);
		return result;
	}

	/**
	 * 医院下载上次门特信息 共用
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_mtsAndcmts_C(TParm parm) {
		TParm parmC = new TParm();
		parmC.addData("REGISTER_NO", parm.getValue("REGISTER_NO")); // 门特登记编号
		parmC.addData("BEGIN_DATE", parm.getValue("BEGIN_DATE")); // 门特开始时间
		parmC.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 医院编码---？？？？
		parmC.addData("PARM_COUNT", 3);
		return parmC;
	}

	/**
	 * 城职门特 生成门特登记信息 函数DataDown_mts （B）方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_B(TParm parm) {
		TParm parmB = DataDown_mtsAndcmts_B(parm);
		// System.out.println("城职门特：DataDown_mts函数 B 方法入参:" + parmB);
		TParm result = commInterFace("DataDown_mts", "B", parmB);
		// System.out.println("城职门特 ：DataDown_mts函数 B方法:" + result);
		return result;
	}

	/**
	 * 城居门特 生成门特登记信息 函数DataDown_cmts（B）方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_B(TParm parm) {
		TParm parmB = DataDown_mtsAndcmts_B(parm);
		// System.out.println("城居门特：DataDown_cmts函数 B 方法入参:" + parmB);
		TParm result = commInterFace("DataDown_cmts", "B", parmB);
		// System.out.println("城居门特 ：DataDown_cmts函数 B方法:" + result);
		return result;
	}

	/**
	 * 生成门特登记信息 共用
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_mtsAndcmts_B(TParm parm) {
		TParm parmB = new TParm();

		parmB.addData("PERSONAL_NO", parm.getValue("PERSONAL_NO")); // 1
		// PERSONAL_NO
		// 个人编码
		parmB.addData("REGISTER_NO", parm.getValue("REGISTER_NO")); // 2
		// REGISTER_NO
		// 门特登记编号
		parmB.addData("DISEASE_CODE", parm.getValue("DISEASE_CODE")); // 3
		// DISEASE_CODE
		// 门特病种编码
		parmB.addData("DIAG_HOSP_CODE", parm.getValue("DIAG_HOSP_CODE")); // 4
		// DIAG_HOSP_CODE
		// 门特诊断医院编码
		parmB.addData("REGISTER_DR_CODE1", parm.getValue("REGISTER_DR_CODE1")); // 5
		// REGISTER_DR_CODE1
		// 门特登记医师编码
		parmB.addData("REGISTER_DR_CODE2", parm.getValue("REGISTER_DR_CODE2")); // 6
		// REGISTER_DR_CODE2
		// 门特登记医师编码1
		parmB.addData("PAT_PHONE", parm.getValue("PAT_PHONE")); // 7 PAT_PHONE
		// 患者联系电话
		parmB.addData("PAT_ZIP_CODE", parm.getValue("PAT_ZIP_CODE")); // 8
		// PAT_ZIP_CODE
		// 邮政编码
		parmB.addData("PAT_ADDRESS", parm.getValue("PAT_ADDRESS")); // 9
		// PAT_ADDRESS
		// 家庭详细住址
		parmB.addData("DISEASE_HISTORY", parm.getValue("DISEASE_HISTORY")); // 10
		// DISEASE_HISTORY
		// 病史
		parmB.addData("ASSISTANT_EXAMINE", parm.getValue("ASSISTANT_EXAMINE")); // 11
		// ASSISTANT_EXAMINE
		// 辅助检查
		parmB.addData("DIAG_CODE", parm.getValue("DIAG_CODE")); // 12 DIAG_CODE
		// 临床诊断
		parmB.addData("HOSP_CODE_LEVEL1", parm.getValue("HOSP_CODE_LEVEL1")); // 13
		// HOSP_CODE_LEVEL1
		// 选定医保治疗医院(一级)
		parmB.addData("HOSP_CODE_LEVEL2", parm.getValue("HOSP_CODE_LEVEL2")); // 14
		// HOSP_CODE_LEVEL2
		// 选定医保治疗医院(二级)
		parmB.addData("HOSP_CODE_LEVEL3", parm.getValue("HOSP_CODE_LEVEL3")); // 15
		// HOSP_CODE_LEVEL3
		// 选定医保治疗医院(三级)
		parmB.addData("HOSP_CODE_LEVEL3_PRO", parm
				.getValue("HOSP_CODE_LEVEL3_PRO")); // 16 HOSP_CODE_LEVEL3_PRO
		// 选定医保治疗医院(三级专科)
		parmB.addData("DRUGSTORE_CODE", parm.getValue("DRUGSTORE_CODE")); // 17
		// DRUGSTORE_CODE
		// 选定医保治疗医院(定点零售药店)
		parmB.addData("REGISTER_USER", parm.getValue("REGISTER_USER")); // 18
		// REGISTER_USER
		// 门特登记经办人
		parmB.addData("REGISTER_RESPONSIBLE", parm
				.getValue("REGISTER_RESPONSIBLE")); // 19 REGISTER_RESPONSIBLE
		parmB.addData("MED_HISTORY", parm.getValue("MED_HISTORY")); // 20既往史(糖尿病)
		
		if (null != parm.getValue("FLG") && parm.getValue("FLG").equals("Y")) {
			parmB.addData("ASSISTANT_STUFF", parm.getValue("ASSSISTANT_STUFF")); // 21
																					// 辅助材料(糖尿病)
			parmB.addData("JUDGE_CONTER_I", parm.getValue("JUDGE_CONTER_I")); // 22
																				// 鉴定中心意见(糖尿病)
			parmB.addData("JUDGE_END", parm.getValue("JUDGE_END")); // 23
																	// 鉴定结论(糖尿病)
			parmB.addData("THE_JUDGE_START_DATE", parm
					.getValue("THE_JUDGE_START_DATE")); // 24 本次鉴定开始时间(糖尿病)
			parmB.addData("THE_JUDGE_END_DATE", parm
					.getValue("THE_JUDGE_END_DATE")); // 25 本次鉴定结束时间(糖尿病)
			parmB.addData("THE_JUDGE_TOT_AMT", parm
					.getDouble("THE_JUDGE_TOT_AMT")); // 26 本次鉴定发生金额合计(糖尿病)
			parmB.addData("THE_JUDGE_APPLY_AMT", parm
					.getDouble("THE_JUDGE_APPLY_AMT")); // 27 本次鉴定申报金额合计(糖尿病)
		} else {
			parmB.addData("ASSISTANT_STUFF", ""); // 21 辅助材料(糖尿病)
			parmB.addData("JUDGE_CONTER_I", ""); // 22 鉴定中心意见(糖尿病)
			parmB.addData("JUDGE_END", "1"); // 23 鉴定结论(糖尿病)
			parmB.addData("THE_JUDGE_START_DATE", ""); // 24 本次鉴定开始时间(糖尿病)
			parmB.addData("THE_JUDGE_END_DATE", ""); // 25 本次鉴定结束时间(糖尿病)
			parmB.addData("THE_JUDGE_TOT_AMT", 0.00); // 26 本次鉴定发生金额合计(糖尿病)
			parmB.addData("THE_JUDGE_APPLY_AMT", 0.00); // 27 本次鉴定申报金额合计(糖尿病)
		}

		// 门特登记负责人
		parmB.addData("PARM_COUNT", 27);
		return parmB;
	}

	/**
	 * 城居门特 门特登记共享资源下载交易 DataDown_cmtd （H）方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmtd_H(TParm parm) {
		TParm parmH = DataDown_mtdAndcmtd_H(parm);
		// System.out.println("城居门特：DataDown_cmtd函数 H 方法入参:" + parmH);
		TParm result = commInterFace("DataDown_cmtd", "H", parmH);
		// System.out.println("城居门特 ：DataDown_cmtd函数 H方法:" + result);
		return result;
	}

	/**
	 * 城职门特 门特登记共享资源下载交易 DataDown_mtd （H）方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mtd_H(TParm parm) {
		TParm parmH = DataDown_mtdAndcmtd_H(parm);
		// System.out.println("城职门特：DataDown_mtd函数 H 方法入参:" + parmH);
		TParm result = commInterFace("DataDown_mtd", "H", parmH);
		// System.out.println("城职门特 ：DataDown_mtd函数 H方法:" + result);
		return result;
	}

	/**
	 * 门特登记共享资源下载交易 共用
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_mtdAndcmtd_H(TParm parm) {
		TParm parmH = new TParm();
		parmH.addData("CARD_NO", parm.getValue("PERSONAL_NO")); // 社保卡号CARD_NO
		parmH.addData("MT_DISEASE_CODE", parm.getValue("DISEASE_CODE")); // 门特病种编码MT_DISEASE_CODE
		parmH.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 医疗机构编码
		parmH.addData("PARM_COUNT", 3);
		return parmH;
	}

	/**
	 * 城居门特审核查询 函数DataDown_cmts （D） 方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_D(TParm parm) {
		TParm parmD = DataDown_mtsAndcmts_D(parm);
		// System.out.println("城居门特：DataDown_cmts函数 D 方法入参:" + parmD);
		TParm result = commInterFace("DataDown_cmts", "D", parmD);
		// System.out.println("城居门特 ：DataDown_cmts函数 D方法:" + result);
		return result;
	}

	/**
	 * 城职门特审核查询 函数DataDown_mts （D） 方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_D(TParm parm) {
		TParm parmD = DataDown_mtsAndcmts_D(parm);
		// System.out.println("城职门特：DataDown_mts函数 D 方法入参:" + parmD);
		TParm result = commInterFace("DataDown_mts", "D", parmD);
		// System.out.println("城职门特 ：DataDown_mts函数D方法:" + result);
		return result;
	}

	/**
	 * 城职门特登记撤销交易
	 * 
	 * @param parm
	 * @return
	 */
	public TParm DataDown_mts_P(TParm parm){
		TParm parmP = DataDown_mtsAndcmts_P(parm);
		TParm result = commInterFace("DataDown_mts", "P", parmP);
		return result;
    }

	/**
	 * 城居门特登记撤销交易
	 * 
	 * @param parm
	 * @return
	 */
	public TParm DataDown_cmts_P(TParm parm) {
		TParm parmP = DataDown_mtsAndcmts_P(parm);
		TParm result = commInterFace("DataDown_cmts", "P", parmP);
		return result;
	}

	/**
	 * 门特登记撤销交易共用
	 * 
	 * @param parm
	 * @return
	 */
	public TParm DataDown_mtsAndcmts_P(TParm parm) {
		TParm parmP = new TParm();
		parmP.addData("REGISTER_NO", parm.getValue("REGISTER_NO")); // REGISTER_NO
		// 门特登记编号
		parmP.addData("DISEASE_CODE", parm.getValue("DISEASE_CODE")); // DISEASE_CODE
		// 门特病种编码
		parmP.addData("BEGIN_DATE", parm.getValue("BEGIN_DATE")); // BEGIN_DATE
		// 门特开始时间
		parmP.addData("HOSP_NHI_NO", parm.getValue("HOSP_NHI_NO")); // HOSP_NHI_NO
		// 医院编码
		parmP.addData("PARM_COUNT", 3);
		return parmP;
	}

	/**
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_mtsAndcmts_D(TParm parm) {
		TParm parmD = new TParm();
		parmD.addData("REGISTER_NO", parm.getValue("REGISTER_NO")); // REGISTER_NO
		// 门特登记编号
		parmD.addData("BEGIN_DATE", parm.getValue("BEGIN_DATE")); // BEGIN_DATE
		// 门特开始时间
		parmD.addData("HOSP_NHI_NO", parm.getValue("HOSP_NHI_NO")); // HOSP_NHI_NO
		// 医院编码
		parmD.addData("PARM_COUNT", 3);
		return parmD;
	}

	/**
	 * 住院城居 获得个人封锁信息 函数 DataDown_czys B方法(DataDown_czys A 方法出参)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_B(TParm parm) {
		TParm parmB = new TParm();
		parmB.addData("CARD_NO", parm.getValue("PERSONAL_NO")); // 1 CARD_NO
		// 社保卡号(或个人编码)
		parmB.addData("HOSP_NHI_CODE", parm.getValue("NHI_REGION_CODE")); // 2
		// HOSP_NHI_CODE
		// 医院编码
		parmB.addData("HAPPEN_DATE", parm.getValue("APP_DATE")); // 3
		// HAPPEN_DATE
		// 发生日期
		// =====申请日期
		parmB.addData("CHECK_CODES", parm.getValue("CHECK_CODES")); // 4
		// CHECK_CODES
		// 刷卡验证码
		parmB.addData("PARM_COUNT", 4);
		// System.out.println("住院城居：DataDown_czys函数B 方法入参:" + parmB);
		TParm result = commInterFace("DataDown_czys", "B", parmB);
		// System.out.println("住院城居 ：DataDown_czys函数 B方法:" + result);
		return result;
	}

	/**
	 * 住院城居 生成资格确认书 函数DataDown_czys (C)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_C(TParm parm) {
		TParm parmC = new TParm();
		parmC.addData("OWN_NO", parm.getValue("PERSONAL_NO")); // 1 OWN_NO 个人编码
		parmC.addData("SFBEST_TRANHOSP", parm.getValue("SFBEST_TRANHOSP")); // 2
		// SFBEST_TRANHOSP
		// 是否跨年度住院
		parmC.addData("ADM_CATEGORY", parm.getValue("ADM_CATEGORY1")); // 3
		// ADM_CATEGORY
		// 就医类别
		// 15
		// 急诊留观转住院21
		// 普通住院23社区家床39急诊留观死亡40生育住院41门诊特殊病42
		// A病43
		// AB病
		parmC.addData("HOSP_NHI_NO", parm.getValue("ADVANCE_CODE")); // 4
		// HOSP_NHI_NO
		// 医院编码
		parmC.addData("DEPT", parm.getValue("DEPT_DESC")); // 5 DEPT 住院科别
		parmC.addData("HOSP_DIAGNOSE",
				null != parm.getValue("DIAG_DESC1") ? parm.getValue(
						"DIAG_DESC1").length() > 127 ? parm.getValue(
						"DIAG_DESC1").substring(0, 127) : parm
						.getValue("DIAG_DESC1") : ""); // 6 HOSP_DIAGNOSE 住院病种
		parmC.addData("HOME_DIAGNOSE_NO", parm.getValue("HOMEDIAG_DESC1")); // 7
		// HOME_DIAGNOSE_NO
		// 家床病种编码
		parmC.addData("HOME_DIAGNOSE_NAME", parm.getValue("HOMEDIAG_DESC1")); // 8
		// HOME_DIAGNOSE_NAME
		// 家床病种名称
		parmC.addData("HOSP_START_DATE", parm.getValue("IN_DATE")); // 9
		// HOSP_START_DATE
		// 住院开始时间YYYYMMDD
		// parmC.addData("SF_EMERGENCY", null != parm.getValue("EMG_FLG")
		// && parm.getValue("EMG_FLG").equals("Y") ? "1" : "0"); // 10
		parmC.addData("SF_EMERGENCY", parm.getValue("EMG_FLG")); // 10
		// SF_EMERGENCY
		// 是否急诊
		parmC.addData("TRANHOSP_NUM_NO", parm.getValue("TRAN_NUM1")); // 11
		// TRANHOSP_NUM_NO
		// 转诊转院审批号
		parmC.addData("CONFIRM_ITEM", parm.getValue("ADM_PRJ1")); // 12
		// CONFIRM_ITEM
		// 就医项目 非空
		parmC.addData("SPEDRS_CODE", parm.getValue("SPEDRS_CODE1")); // 13
		// SPEDRS_CODE
		// 门特类别
		parmC.addData("BEARING_OPERATIONS_TYPE", parm
				.getValue("BEARING_OPERATIONS_TYPE")); // 14
		// BEARING_OPERATIONS_TYPE
		// 计生手术类别 01放置宫内节育环
		// 02女职工绝育术 03男职工绝育术
		// 04正常取出宫内节育环
		// 05人工流产术合并放置宫内节育环
		// 06取出宫内节育器合并人工流产术
		// 07更换宫内节育器 08流产
		// 09高危人工流产 10引产
		parmC.addData("TRAMA_ATTEST", parm.getValue("TRAMA_ATTEST")); // 15
		// TRAMA_ATTEST
		// 外伤证明
		parmC.addData("OUT_HOSP_NO", parm.getValue("OUT_HOSP_NO")); // 16
		// OUT_HOSP_NO
		// 转出医院编码
		parmC.addData("SPECIAL_DISEASE", parm.getValue("SPE_DISEASE")); // 17
		// SPECIAL_DISEASE
		// 专科疾病
		// 01精神病02传染病03其他专科疾病
		parmC.addData("ZHUNSHENG_NO", parm.getValue("ZHUNSHENG_NO")); // 18
		// ZHUNSHENG_NO
		// 准生证号
		parmC.addData("PARM_COUNT", 18);
		// System.out.println("住院城居：DataDown_czys函数C 方法入参:" + parmC);
		TParm result = commInterFace("DataDown_czys", "C", parmC);
		// System.out.println("住院城居 ：DataDown_czys函数 C方法:" + result);
		return result;
	}

	/**
	 * 住院城居 在途结算信息查询 函数 DataDown_czys (N)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_N(TParm parm) {
		TParm parmN = new TParm();
		parmN.addData("CARD_NO", parm.getValue("PERSONAL_NO")); // 1社保卡号(或个人编码)
		parmN.addData("HOSP_NHI_CODE", parm.getValue("NHI_REGION_CODE")); // 2区域编码
		parmN.addData("PARM_COUNT", 2);
		// System.out.println("住院城居：DataDown_czys函数N 方法入参:" + parmN);
		TParm result = commInterFace("DataDown_czys", "N", parmN);
		// System.out.println("住院城居 ：DataDown_czys函数 N方法:" + result);
		return result;
	}

	/**
	 * 住院城居在途结算信息补入 函数 DataDown_czys (N)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_O(TParm parm) {
		TParm parmO = new TParm();
		parmO.addData("CARD_NO", parm.getValue("PERSONAL_NO")); // 1社保卡号(或个人编码)
		parmO.addData("HOSP_NHI_CODE", parm.getValue("NHI_REGION_CODE")); // 2区域编码
		parmO.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 ADM_SEQ 就诊顺序号
		parmO.addData("HOSP_NHI_CODE", parm.getValue("NHI_REGION_CODE")); // 2
		// HOSP_NHI_CODE
		// 医院编码
		parmO.addData("TOTAL_AMT", parm.getValue("ADM_SEQ")); // 3 TOTAL_AMT
		// 发生金额
		parmO.addData("OWN_AMT", parm.getValue("ADM_SEQ")); // 4 OWN_AMT 自费金额
		parmO.addData("ADDPAY_AMT", parm.getValue("ADM_SEQ")); // 5 ADDPAY_AMT
		// 增负金额
		parmO.addData("TRANBLOOD_OWN_AMT", parm.getValue("ADM_SEQ")); // 6
		// TRANBLOOD_OWN_AMT
		// 输血自负
		parmO.addData("DS_DATE", parm.getValue("ADM_SEQ")); // 7 DS_DATE 出院时间
		// DATE
		parmO.addData("USER_CODE", parm.getValue("OPT_USER")); // 8 USER_CODE
		// 医院补入操作员
		parmO.addData("PARM_COUNT", 2);
		// System.out.println("住院城居：DataDown_czys函数N 方法入参:" + parmO);
		TParm result = commInterFace("DataDown_czys", "N", parmO);
		// System.out.println("住院城居 ：DataDown_czys函数 N方法:" + result);
		return result;
	}

	/**
	 * 住院城职 获得个人封锁信息 函数 DataDown_sp (D)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_D(TParm parm) {
		TParm parmD = new TParm();
		parmD.addData("OWN_NO", parm.getValue("PERSONAL_NO")); // 1 OWN_NO
		// 如果手工开具资格确认书，则传入的为"个人社保号*医院编码"、如果刷卡开具资格确认书，则传入的为"社保卡号*医院
		parmD.addData("HOSP_NHI_CODE", parm.getValue("NHI_REGION_CODE")); // 2
		// HOSP_NHI_CODE
		// 医院编码
		parmD.addData("HAPPEN_DATE", parm.getValue("APP_DATE")); // 3
		// HAPPEN_DATE
		// 发生日期
		// =====申请日期
		parmD.addData("CHECK_CODES", parm.getValue("CHECK_CODES")); // 4
		// CHECK_CODES
		// 刷卡验证码
		parmD.addData("PARM_COUNT", 4);
		// System.out.println("住院城居：DataDown_sp函数D 方法入参:" + parmD);
		TParm result = commInterFace("DataDown_sp", "D", parmD);
		// System.out.println("住院城居 ：DataDown_sp函数 D方法:" + result);
		return result;
	}

	/**
	 * 住院城居 生成资格确认书 函数DataDown_sp (B)方法
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_B(TParm insParm) {
		TParm parmB = new TParm();
		parmB.addData("OWN_NO", insParm.getValue("PERSONAL_NO")); // 1 OWN_NO
		// 个人编码
		parmB.addData("SFBEST_TRANHOSP", insParm.getValue("SFBEST_TRANHOSP")); // 2
		// SFBEST_TRANHOSP
		// 是否跨年度住院
		parmB.addData("ADM_CATEGORY", insParm.getValue("ADM_CATEGORY1")); // 3
		// ADM_CATEGORY
		// 就医类别
		parmB.addData("HOSP_NHI_NO", insParm.getValue("ADVANCE_CODE")); // 4
		// HOSP_NHI_NO
		// 医院编码
		parmB.addData("DEPT", insParm.getValue("DEPT_DESC")); // 5 DEPT 住院科别
		parmB.addData("HOSP_DIAGNOSE",
				null != insParm.getValue("DIAG_DESC1") ? insParm.getValue(
						"DIAG_DESC1").length() > 127 ? insParm.getValue(
						"DIAG_DESC1").substring(0, 127) : insParm
						.getValue("DIAG_DESC1") : ""); // 6
		// HOSP_DIAGNOSE
		// 住院病种
		// 如果该病人不是家床住院，则住院病种传入"住院病种"、如果该病人家床住院，则住院病种传入"住院病种@家床病种编码@家床病种名称"
		parmB.addData("HOSP_START_DATE", insParm.getValue("IN_DATE")); // 7
		// HOSP_START_DATE
		// 住院开始时间
		// YYYYMMDD
		parmB.addData("SF_EMERGENCY", insParm.getValue("EMG_FLG")); // 8
		// SF_EMERGENCY
		// 是否急诊
		parmB.addData("TRANHOSP_NUM_NO", insParm.getValue("TRAN_NUM1")); // 9
		// TRANHOSP_NUM_NO
		// 转诊转院审批号
		parmB.addData("INPAT_ITEM", insParm.getValue("ADM_PRJ1")); // 10
		// INPAT_ITEM
		// 就医项目
		parmB.addData("MT_DISEASE_CODE", insParm.getValue("SPEDRS_CODE1")); // 11
		// MT_DISEASE_CODE
		// 门特类别
		parmB.addData("GS_CONFIRM_NO", insParm.getValue("GS_CONFIRM_NO")); // 12
		// GS_CONFIRM_NO
		// 工伤资格确认书编号
		parmB.addData("PRE_CONFIRM_NO", insParm.getValue("PRE_CONFIRM_NO")); // 13
		// PRE_CONFIRM_NO
		// 上次资格确认书编号
		// 转诊转院时传入
		parmB.addData("PRE_OWN_AMT", StringTool.round(insParm
				.getDouble("PRE_OWN_AMT"), 2)); // 14
		// PRE_OWN_AMT
		// 上次自费项目金额
		// 转诊转院时传入
		parmB.addData("PRE_ADD_AMT", StringTool.round(insParm
				.getDouble("PRE_ADD_AMT"), 2)); // 15
		// PRE_ADD_AMT
		// 上次增负项目金额
		// 转诊转院时传入
		parmB.addData("PRE_NHI_AMT", StringTool.round(insParm
				.getDouble("PRE_NHI_AMT"), 2)); // 16
		// PRE_NHI_AMT
		// 上次申报金额
		// 转诊转院时传入
		parmB.addData("PRE_OUT_TIME", insParm.getValue("PRE_OUT_TIME")
				.toString().replace("/", "")); // 17
		// PRE_OUT_TIME
		// 上次出院时间
		// 转诊转院时传入YYYYMMDD
		parmB.addData("SPE_DISEASE", insParm.getValue("SPE_DISEASE")); // 18
		// SPE_DISEASE
		// 专科疾病
		// 用于优抚人员住院01精神病02传染病03其他专科疾病
		parmB.addData("PARM_COUNT", 18);
		// System.out.println("住院城居：DataDown_sp函数B 方法入参:" + parmB);
		TParm result = commInterFace("DataDown_sp", "B", parmB);
		// System.out.println("住院城居 ：DataDown_sp函数 B方法:" + result);
		return result;
	}

	/**
	 * 住院城职 资格确认书下载操作 资格确认书查询就诊顺序号 函数 DataDown_rs (B)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_rs_B(TParm parm) {
		TParm parmB = new TParm();
		parmB.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 资格确认书编号
		parmB.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 2
		// 医院编码
		parmB.addData("IDNO", parm.getValue("IDNO")); // 3 身份证号
		parmB.addData("PARM_COUNT", 3);
		// System.out.println("住院城职：DataDown_rs函数B 方法入参:" + parmB);
		TParm result = commInterFace("DataDown_rs", "B", parmB);
		// System.out.println("住院城职：DataDown_rs函数 B方法:" + result);
		return result;
	}
	/**
	 * 住院城职 资格确认书下载操作 资格确认书查询就诊顺序号 函数 DataDown_rs (B)方法
	 * 复数
	 * @param parm
	 *            TParm
	 * @return TParm
	 * ================pangben 2012-7-12
	 */
	public TParm DataDown_rs_B(TParm parm,String str){
		TParm parmB = new TParm();
		parmB.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 资格确认书编号
		parmB.addData("HOSP_NHI_NO", parm.getValue("ADVANCE_CODE")); // 2//医院编码@异地标志
		parmB.addData("IDNO", parm.getValue("IDNO")); // 3 身份证号
		parmB.addData("PARM_COUNT", 3);
		parmB.setData("PIPELINE", "DataDown_rs");
		parmB.setData("PLOT_TYPE", "B");
		//parm.setData("HOSP_AREA", "HIS");
		// System.out.println("-----------commInterFace--------------" + parm);
		// INSInterface Interface=new INSInterface();
		TParm result = InsManager.getInstance().safe(parmB,str);
		// System.out.println("住院城职：DataDown_rs函数B 方法入参:" + parmB);
		//TParm result = commInterFace("DataDown_rs", "B", parmB);
		// System.out.println("住院城职：DataDown_rs函数 B方法:" + result);
		return result;
	}
	/**
	 * 住院城居 资格确认书下载操作 资格确认书查询就诊顺序号 函数 DataDown_czyd (B)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czyd_B(TParm parm) {
		TParm parmB = new TParm();
		parmB.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 资格确认书编号
		parmB.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 2
		// 医院编码
		parmB.addData("SID", parm.getValue("IDNO")); // 3 身份证号
		parmB.addData("PARM_COUNT", 3);
		// System.out.println("住院城居：DataDown_czyd函数B 方法入参:" + parmB);
		TParm result = commInterFace("DataDown_czyd", "B", parmB);
		// System.out.println("住院城居：DataDown_czyd函数 B方法:" + result);
		return result;
	}
	/**
	 * 住院城居 资格确认书下载操作 资格确认书查询就诊顺序号 函数 DataDown_czyd (B)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czyd_B(TParm parm,String str) {
		TParm parmB = new TParm();
		parmB.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 资格确认书编号
		parmB.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 2
		// 医院编码
		parmB.addData("SID", parm.getValue("IDNO")); // 3 身份证号
		parmB.addData("PARM_COUNT", 3);
		parmB.setData("PIPELINE", "DataDown_czyd");
		parmB.setData("PLOT_TYPE", "B");
		// System.out.println("住院城居：DataDown_czyd函数B 方法入参:" + parmB);
		TParm result = InsManager.getInstance().safe(parmB,str);
		// System.out.println("住院城居：DataDown_czyd函数 B方法:" + result);
		return result;
	}
	/**
	 * 住院城居 资格确认书下载 函数 DataDown_czys (E)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_E(TParm parm) {
		TParm parmE = new TParm();
		parmE.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 顺序号
		parmE.addData("HOSP_NHI_CODE", parm.getValue("NHI_REGION_CODE")); // 2
		// 医院编码
		parmE.addData("PARM_COUNT", 2);
		// System.out.println("住院城居：DataDown_czys函数E 方法入参:" + parmE);
		TParm result = commInterFace("DataDown_czys", "E", parmE);
		// System.out.println("住院城居：DataDown_czys函数 E方法:" + result);
		return result;
	}

	/**
	 * 住院城居 资格确认书下载撤销操作 函数 DataDown_czys (F)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_F(TParm parm) {
		TParm parmF = new TParm();
		parmF.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 顺序号
		parmF.addData("HOSP_NHI_CODE", parm.getValue("NHI_REGION_CODE")); // 2
		// 医院编码
		parmF.addData("PARM_COUNT", 2);
		// System.out.println("住院城居：DataDown_czys函数F 方法入参:" + parmF);
		TParm result = commInterFace("DataDown_czys", "F", parmF);
		// System.out.println("住院城居：DataDown_czys函数 F方法:" + result);
		return result;
	}

	/**
	 * 住院城职 资格确认书下载 函数 DataDown_sp (A)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_A(TParm parm) {
		TParm parmA = new TParm();
		parmA.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 顺序号
		parmA.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 2
		// 医院编码
		parmA.addData("PARM_COUNT", 2);
		// System.out.println("住院城居：DataDown_sp函数A 方法入参:" + parmA);
		TParm result = commInterFace("DataDown_sp", "A", parmA);
		// System.out.println("住院城居：DataDown_sp函数 A方法:" + result);
		return result;
	}

	/**
	 * 住院城职 资格确认书下载撤销操作 函数 DataDown_sp (C)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_C(TParm parm) {
		TParm parmC = new TParm();
		parmC.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 顺序号
		parmC.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 2
		// 医院编码
		parmC.addData("PARM_COUNT", 2);
		// System.out.println("住院城居：DataDown_sp函数C 方法入参:" + parmC);
		TParm result = commInterFace("DataDown_sp", "C", parmC);
		// System.out.println("住院城居：DataDown_sp函数 C方法:" + result);
		return result;
	}

	/**
	 * 资格确认书 延迟申报
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_yb_I(TParm parm) {
		TParm parmI = new TParm();
		parmI.addData("PERSONAL_NO", parm.getValue("PERSONAL_NO")); // 1个人编码
		parmI.addData("HOSPITAL_SIS", parm.getValue("HOSPITAL_SIS")); // 2住院诊断
		parmI.addData("HOSPITAL_DATE", parm.getValue("HOSPITAL_DATE")); // 3住院日期
		parmI.addData("HOSPITAL_NO", parm.getValue("HOSPITAL_NO")); // 4医院编码
		parmI.addData("DELAY_REASON", parm.getValue("DELAY_REASON")); // 5延迟信息
		parmI.addData("INSURANCE_TYPE", parm.getValue("INSURANCE_TYPE")); // 6险种类型
		parmI.addData("PARM_COUNT", 6);
		// System.out.println("延迟申报：DataDown_yb函数I 方法入参:" + parmI);
		TParm result = commInterFace("DataDown_yb", "I", parmI);
		// System.out.println("延迟申报：DataDown_yb函数 I方法:" + result);
		return result;
	}

	/**
	 * 城职 住院费用明细分割 函数DataDown_sp1 (B)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp1_B(TParm parm) {
		TParm parmB = DataDown_sp1_BAndG(parm);
		parmB.addData("PARM_COUNT", 9);
		// System.out.println("城职 住院费用明细分割：DataDown_sp1函数B 方法入参:" + parmB);
		TParm result = commInterFace("DataDown_sp1", "B", parmB);
		// System.out.println("城职 住院费用明细分割：DataDown_sp1函数 B方法:" + result);
		return result;
	}

	/**
	 * 城职 住院累计增负计算 函数DataDown_sp1 (C)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp1_C(TParm parm) {
		TParm parmC = DataDown_sp1_CAndH(parm);
		parmC.addData("PARM_COUNT", 2);
		// System.out.println("城职住院累计增负计算：DataDown_sp1函数C 方法入参:" + parmC);
		TParm result = commInterFace("DataDown_sp1", "C", parmC);
		// System.out.println("城职住院累计增负计算：DataDown_sp1函数 C方法:" + result);
		return result;
	}

	/**
	 * 城居 住院费用明细分割 函数DataDown_sp1 (G)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp1_G(TParm parm) {
		TParm parmG = DataDown_sp1_BAndG(parm);
		parmG.addData("PARM_COUNT", 9);
		// System.out.println("城居 住院费用明细分割：DataDown_sp1函数G 方法入参:" + parmG);
		TParm result = commInterFace("DataDown_sp1", "G", parmG);
		// System.out.println("城居 住院费用明细分割：DataDown_sp1函数G方法:" + result);
		return result;
	}

	/**
	 * 城职 and 城居 住院费用明细分割
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp1_BAndG(TParm parm) {
		TParm temp = new TParm();
		temp.addData("NHI_ORDER_CODE", parm.getValue("NHI_ORDER_CODE")); // 1
		// NHI_ORDER_CODE
		// 收费项目编码
		// 六位码
		temp.addData("CTZ1_CODE", parm.getValue("CTZ1_CODE")); // 2 CTZ1_CODE
		// 人员类别
		temp.addData("QTY", parm.getValue("QTY")); // 3 QTY 数量
		temp.addData("TOTAL_AMT", StringTool.round(parm.getDouble("TOTAL_AMT"),
				2)); // 4 TOTAL_AMT
		// 发生金额
		temp.addData("TIPTOP_BED_AMT", StringTool.round(parm
				.getDouble("TIPTOP_BED_AMT"), 2)); // 5
		// TIPTOP_BED_AMT
		// 最高床位费
		temp.addData("PHAADD_FLG", parm.getValue("PHAADD_FLG")); // 6
		// PHAADD_FLG
		// 增负药品标志
		temp.addData("FULL_OWN_FLG", parm.getValue("FULL_OWN_FLG")); // 7
		// FULL_OWN_FLG
		// 全自费标志
		temp.addData("HOSP_NHI_NO", parm.getValue("HOSP_NHI_NO")); // 8
		// HOSP_NHI_NO
		// 医院编码
		temp.addData("CHARGE_DATE", parm.getValue("CHARGE_DATE")); // 9
		// CHARGE_DATE费用发生时间
		// YYYYMMDD
		return temp;
	}

	/**
	 * 城居 住院累计增负计算 函数DataDown_sp1 (H)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp1_H(TParm parm) {
		TParm parmH = DataDown_sp1_CAndH(parm);
		parmH.addData("PARM_COUNT", 2);
		// System.out.println("城职住院累计增负计算：DataDown_sp1函数H 方法入参:" + parmH);
		TParm result = commInterFace("DataDown_sp1", "H", parmH);
		// System.out.println("城职住院累计增负计算：DataDown_sp1函数 H方法:" + result);
		return result;
	}

	/**
	 * 城职 住院累计增负计算
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_sp1_CAndH(TParm parm) {
		TParm temp = new TParm();
		temp.addData("ADDPAY_ADD", StringTool.round(parm
				.getDouble("ADDPAY_ADD"), 2)); // 1
		// ADDPAY_ADD累计增负发生总额
		// 在费用明细分割后返回的所有累计增负标志为1的所用明细的发生金额之和
		temp.addData("HOSP_START_DATE", parm.getValue("HOSP_START_DATE")); // 2HOSP_START_DATE
		// 住院开始时间
		return temp; // YYYYMMDD
	}

	/**
	 * 城职费用结算 函数DataDown_sp(I) 方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_I(TParm parm) {
		TParm parmI = new TParm();
		parmI.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 ADM_SEQ 就诊顺序号
		parmI
				.addData("TOT_AMT", StringTool.round(parm.getDouble("TOT_AMT"),
						2)); // 2 TOT_AMT 发生金额
		parmI
				.addData("OWN_AMT", StringTool.round(parm.getDouble("OWN_AMT"),
						2)); // 3 OWN_AMT 自费金额
		parmI
				.addData("ADD_AMT", StringTool.round(parm.getDouble("ADD_AMT"),
						2)); // 4 ADD_AMT 增负金额
		parmI
				.addData("NHI_AMT", StringTool.round(parm.getDouble("NHI_AMT"),
						2)); // 5 NHI_AMT 申报金额
		parmI.addData("PARM_COUNT", 5);
		// System.out.println("城职费用结算：DataDown_sp函数I 方法入参:" + parmI);
		TParm result = commInterFace("DataDown_sp", "I", parmI);
		// System.out.println("城职费用结算：DataDown_sp函数 I方法:" + result);
		return result;
	}

	/**
	 * 单病种 城职费用结算 函数DataDown_sp(I1) 方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_I1(TParm parm) {
		TParm parmI1 = new TParm();
		parmI1.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 ADM_SEQ 就诊顺序号
		parmI1.addData("TOT_AMT", StringTool
				.round(parm.getDouble("TOT_AMT"), 2)); // 2 TOT_AMT 发生金额
		parmI1.addData("OWN_AMT", StringTool
				.round(parm.getDouble("OWN_AMT"), 2)); // 3 OWN_AMT 自费金额
		parmI1.addData("ADD_AMT", StringTool
				.round(parm.getDouble("ADD_AMT"), 2)); // 4 ADD_AMT 增负金额
		parmI1.addData("NHI_AMT", StringTool
				.round(parm.getDouble("NHI_AMT"), 2)); // 5 NHI_AMT
		// 本次申报金额
		parmI1.addData("SIN_DISEASE_CODE", parm.getValue("SIN_DISEASE_CODE")); // 6
		// SIN_DISEASE_CODE
		// 单病种编码
		parmI1.addData("IN_DAY", parm.getValue("IN_DAY")); // 7 IN_DAY 住院天数
		parmI1.addData("OUT_TIME", parm.getValue("OUT_TIME").substring(0,8)); // 8 OUT_TIME
		// 出院时间yyyymmdd
		parmI1.addData("SPECIAL_AMT", StringTool.round(parm
				.getDouble("SPECIAL_AMT"), 2)); // 9
		// SPECIAL_AMT
		// 特需项目金额
		parmI1.addData("PARM_COUNT", 9);
		// System.out.println("单病种城职费用结算：DataDown_sp函数I1 方法入参:" + parmI1);
		TParm result = commInterFace("DataDown_sp", "I1", parmI1);
		// System.out.println("单病种城职费用结算：DataDown_sp函数 I1方法:" + result);
		return result;
	}
    /**
     * 医保医嘱管控
     * @param orderCode String
     * @param ctzCode String
     * @param admType String
     * @param insPatType String
     * @return TParm
     */
	
    public TParm orderCheck(String orderCode, String ctzCode, String admType,
                            String insPatType) {
        TParm result = new TParm();
        String ctzSql =
                " SELECT NHI_CTZ_FLG " + "   FROM SYS_CTZ " +
                "  WHERE CTZ_CODE = '" + ctzCode + "' ";
        TParm ctzParm = new TParm(TJDODBTool.getInstance().select(ctzSql));
        if (!ctzParm.getBoolean("NHI_CTZ_FLG", 0)) {
//            ctzParm.setErr( -1, "不是医保身份");
            return ctzParm;
        }
        String nhiOrderSql =
                " SELECT  ORDER_CODE, NHI_CODE_O, NHI_CODE_E, NHI_CODE_I,ORDERSET_FLG " +
                "   FROM SYS_FEE " +
                "  WHERE ORDER_CODE = '" + orderCode + "' " +
                "    AND (NHI_CODE_O IS NOT NULL " +
                "     OR NHI_CODE_E IS NOT NULL " +
                "     OR NHI_CODE_I IS NOT NULL )";
        TParm nhiOrderParm = new TParm(TJDODBTool.getInstance().select(
                nhiOrderSql));
//        System.out.println("医嘱代码》》》"+nhiOrderParm.getValue("ORDER_CODE",0)+"======nhiOrderParm>>>>>>>"+nhiOrderParm);
        if (nhiOrderParm.getData("ORDER_CODE", 0) == null ||
            nhiOrderParm.getValue("ORDER_CODE", 0).length() > 0 &&
            "Y".equals(nhiOrderParm.getValue("ORDERSET_FLG", 0))) {
            return nhiOrderParm;
        }
        if (nhiOrderParm.getData("ORDER_CODE", 0) == null
                || nhiOrderParm.getValue("ORDER_CODE", 0).length() <= 0) {
//                nhiOrderParm.setErr( -2, "此项目为自费项目");
                return nhiOrderParm;
            }
        //门诊对应医保编码
        String nhiCodeO = nhiOrderParm.getValue("NHI_CODE_O", 0);
        //急诊对应医保编码   yanj 20130719 
        String nhiCodeE = nhiOrderParm.getValue("NHI_CODE_E", 0);
        //住院对应医保编码
        String nhiCodeI = nhiOrderParm.getValue("NHI_CODE_I", 0);
      //20130719 yanj 添加医保自费药嘱校验 
    	String nhi_ownfee_codes[] = {"005306","005307","005308","005309","005310",
    			"005311","005312","005322","005323"};//医保自费码
    	String nhi_addpay_codes[] = {"005333","005338","005351"};//医保自费增付码
    	
        // MZYYBZ 门诊
        // ETYYBZ 儿童
        // YKD242 住院
        String orderTypeSql = "";
        TParm orderTypeParm = new TParm();
        if ("O".equals(admType)||"E".equals(admType)) {
        	
            orderTypeSql =
                    " SELECT MZYYBZ, YKD242, ETYYBZ " +
                    "   FROM INS_RULE " +
                    "  WHERE SFXMBM = '" + nhiCodeI + "' ";
            orderTypeParm = new TParm(TJDODBTool.getInstance().select(
                    orderTypeSql));
            if (orderTypeParm.getData("MZYYBZ", 0) != null
                && "1".equals(orderTypeParm.getValue("MZYYBZ", 0))) {
                orderTypeParm.setErr( -3, "住院用药");
                return orderTypeParm;
            }
            //22 城居学生儿童
            if (!"22".equals(ctzCode)) {
                if (orderTypeParm.getData("ETYYBZ", 0) != null
                    && "1".equals(orderTypeParm.getValue("ETYYBZ", 0))) {
                    orderTypeParm.setErr( -5, "儿童用药");
                    return orderTypeParm;
                }
            }
          //yanjing 20130719 门诊部分医保自费校验 start
            for(int i = 0;i<nhi_ownfee_codes.length;i++){
            	String ins_code = nhi_ownfee_codes[i];
            	if(nhiCodeO.equals(ins_code)||nhiCodeE.equals(ins_code)){
            		orderTypeParm.setErr( -6,"医保自费药品");
            		return orderTypeParm;
            	}
            }
            for(int j = 0;j<nhi_addpay_codes.length;j++){
            	String ins_code = nhi_addpay_codes[j];
            	if(nhiCodeO.equals(ins_code)||nhiCodeE.equals(ins_code)){
            		orderTypeParm.setErr( -7,"医保自费增付药品");
            		return orderTypeParm;
            	}
            }
          //yanjing 20130719 门诊部分医保自费校验 end
        }
        if ("I".equals(admType)) {
            orderTypeSql =
                    " SELECT MZYYBZ, YKD242, ETYYBZ " +
                    "   FROM INS_RULE " +
                    "  WHERE SFXMBM = '" + nhiCodeO + "' ";
            orderTypeParm = new TParm(TJDODBTool.getInstance().select(
                    orderTypeSql));
            if (orderTypeParm.getData("YKD242", 0) != null
                && "1".equals(orderTypeParm.getValue("YKD242", 0))) {
                orderTypeParm.setErr( -4, "门诊用药");
                return orderTypeParm;
            }
            //22 城居学生儿童
            if (!"22".equals(ctzCode)) {
                if (orderTypeParm.getData("ETYYBZ", 0) != null
                    && "1".equals(orderTypeParm.getValue("ETYYBZ", 0))) {
                    orderTypeParm.setErr( -5, "儿童用药");
                    return orderTypeParm;
                }
            }
            //yanjing 20130719 住院部分医保自费校验 start
            for(int i = 0;i<nhi_ownfee_codes.length;i++){
            	String ins_code = nhi_ownfee_codes[i];
            	if(nhiCodeI.equals(ins_code)||nhiCodeE.equals(ins_code)){
            		orderTypeParm.setErr( -6,"医保自费药品");
            		return orderTypeParm;
            	}
            }
            for(int j = 0;j<nhi_addpay_codes.length;j++){
            	String ins_code = nhi_addpay_codes[j];
            	if(nhiCodeI.equals(ins_code)||nhiCodeE.equals(ins_code)){
            		orderTypeParm.setErr( -7,"医保自费增付药品");
            		return orderTypeParm;
            	}
            }
            //yanjing 20130719 住院部分医保自费校验 end
            //门特身份才去校验 INS_PAT_TYPE ='2' 门特身份
            if ("2".equals(insPatType)) {
                String mtOrderSql =
                        " SELECT YKD241 " + "   FROM INS_RULE " +
                        "  WHERE YKD241 LIKE '%D%' " +
                        "    AND SFXMBM = '" + nhiCodeO + "' ";
                TParm mtOrderParm = new TParm(TJDODBTool.getInstance().select(
                        mtOrderSql));
                if (mtOrderParm.getData("YKD241", 0) == null
                    || mtOrderParm.getValue("YKD241", 0).length() < 0) {
                    mtOrderParm.setErr( -6, "非门特用药");
                    return mtOrderParm;
                }
            }
        }
        
        return result;
    }

	/**
	 * 城居费用结算 函数DataDown_czys(G) 方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_G(TParm parm) {
		TParm parmG = new TParm();
		parmG.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 ADM_SEQ 就诊顺序号
		parmG.addData("FACT_PAYMENT_SCALE", StringTool.round(parm
				.getDouble("FACT_PAYMENT_SCALE") / 100, 2)); // 2
																// FACT_PAYMENT_SCALE
		// 实际支付比例
		// ---？？
		parmG.addData("SALVATION_PAYMENT_SCALE", StringTool.round(parm
				.getDouble("SALVATION_PAYMENT_SCALE") / 100, 2)); // 3
		// SALVATION_PAYMENT_SCALE
		// 救助支付比例--？？
		parmG.addData("RESTART_STANDARD_AMT", StringTool.round(parm
				.getDouble("RESTART_STANDARD_AMT"), 2)); // 4
															// RESTART_STANDARD_AMT
		// 本次实际起付标准
		parmG.addData("TOTAL_PAYMENT_AMT", StringTool.round(parm
				.getDouble("TOTAL_PAYMENT_AMT"), 2)); // 5
		// TOTAL_PAYMENT_AMT
		// 基本医疗支付限额
		parmG.addData("APPLY1_AMT", StringTool.round(parm
				.getDouble("APPLY1_AMT"), 2)); // 6
		// APPLY1_AMT
		// 医疗救助支付限额
		parmG
				.addData("NHI_AMT", StringTool.round(parm.getDouble("NHI_AMT"),
						2)); // 7 NHI_AMT 申报金额
		// BEARING_OPERATIONS_TYPE 计生手术类别 01放置宫内节育环 02女职工绝育术 03男职工绝育术
		// 04正常取出宫内节育环 05人工流产术合并放置宫内节育环 06取出宫内节育器合并人工流产术
		// 07更换宫内节育器 08流产 09高危人工流产 10引产
		parmG.addData("BEARING_OPERATIONS_TYPE", parm
				.getValue("BEARING_OPERATIONS_TYPE")); // 8
		// BEARING_OPERATIONS_TYPE
		parmG
				.addData("TOT_AMT", StringTool.round(parm.getDouble("TOT_AMT"),
						2)); // 9 TOT_AMT
		parmG
				.addData("OWN_AMT", StringTool.round(parm.getDouble("OWN_AMT"),
						2)); // 10 OWN_AMT
		parmG
				.addData("ADD_AMT", StringTool.round(parm.getDouble("ADD_AMT"),
						2)); // 11 ADD_AMT 增负金额
		parmG.addData("BIRTH_TYPE", parm.getValue("BIRTH_TYPE")); // 12
		// BIRTH_TYPE
		// 生育方式
		// 11剖宫产12自然分娩13终止妊娠
		parmG.addData("BABY_NO", 0); // 13 BABY_NO 分娩胎儿数量
		parmG.addData("PARM_COUNT", 13);
		// System.out.println("城居费用结算：DataDown_czys函数G 方法入参:" + parmG);
		TParm result = commInterFace("DataDown_czys", "G", parmG);
		// System.out.println("城居费用结算：DataDown_czys函数 G方法:" + result);
		return result;
	}

	/**
	 * 单病种 城居费用结算 函数DataDown_czys(G1) 方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_G1(TParm parm) {
		TParm parmG1 = new TParm();
		parmG1.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 ADM_SEQ 就诊顺序号
		parmG1.addData("TOT_AMT", StringTool
				.round(parm.getDouble("TOT_AMT"), 2)); // 2 TOT_AMT 发生金额
		parmG1.addData("OWN_AMT", StringTool
				.round(parm.getDouble("OWN_AMT"), 2)); // 3 OWN_AMT 自费金额
		parmG1.addData("ADD_AMT", StringTool
				.round(parm.getDouble("ADD_AMT"), 2)); // 4 ADD_AMT 增负金额
		parmG1.addData("NHI_AMT", StringTool
				.round(parm.getDouble("NHI_AMT"), 2)); // 5 NHI_AMT
		// 本次申报金额
		parmG1.addData("SIN_DISEASE_CODE", parm.getValue("SIN_DISEASE_CODE")); // 6
		// SIN_DISEASE_CODE
		// 单病种编码
		parmG1.addData("IN_DAY", parm.getValue("IN_DAY")); // 7 IN_DAY 住院天数
		parmG1.addData("OUT_TIME", parm.getValue("OUT_TIME").substring(0,8)); // 8 OUT_TIME
		// 出院时间yyyymmdd
		parmG1.addData("SPECIAL_AMT", StringTool.round(parm
				.getDouble("SPECIAL_AMT"), 2)); // 9
		// SPECIAL_AMT
		// 特需项目金额
		parmG1.addData("PARM_COUNT", 9);
		// System.out.println("城居费用结算：DataDown_czys函数G1 方法入参:" + parmG1);
		TParm result = commInterFace("DataDown_czys", "G1", parmG1);
		// System.out.println("城居费用结算：DataDown_czys函数 G1方法:" + result);
		return result;
	}

	/**
	 * 获得三目字典中需要查询的数据
	 * 
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	private TParm checkCode(String orderCode) {
		String sql = "SELECT SFXMBM, JX, GG, DW, YF, "
				+ "YL, SL, PZWH, BZJG, ZFBL1,"
				+ "KSSJ, JSSJ, YYSMBM, SPMC, FLZB1, LJZFBZ,"
				+ "XMLB, TXBZ, XMRJ FROM INS_RULE WHERE  SFXMBM='" + orderCode
				+ "'";
		// System.out.println("sql::::::" + sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getRow(0);
	}

	/**
	 * 医保诊断码
	 * 
	 * @param icdCode
	 *            String
	 * @return TParm
	 */
	public String selInsICDCode(String icdCode) {
		TParm result = new TParm();
		int count = icdCode.length();
		String insIcdCode = "";
		for (int i = 0; i < count; i++) {
			icdCode = icdCode.substring(0, count - i);
			// System.out.println("医保诊断码"+icdCode);
			String selIcdCode = " SELECT ICD_CODE, ICD_CHN_DESC "
					+ "   FROM INS_DIAGNOSIS " + "  WHERE ICD_CODE = '"
					+ icdCode + "' ";
			// System.out.println("诊断码sql = "+selIcdCode);
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
	////////////////////////////////////////////////////////////////////
	//zhangs start
	/**
	 * 需备案目录信息下载 函数DataDown_zjkd (L)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_zjkd_L(TParm parm) {
		TParm result = commInterFace("DataDown_zjkd", "L", parm,"");
		return result;
	}
	/**
	 * 诊疗项目备案信息下载 函数DataDown_zjkd (J)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_zjkd_J(TParm parm) {
		TParm result = commInterFace("DataDown_zjkd", "J", parm,"");
		return result;
	}
	/**
	 * 诊疗项目备案信息取消 函数DataDown_zjks (T)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_zjks_T(TParm parm) {
		TParm result = commInterFace("DataDown_zjks", "T", parm,"");
		return result;
	}
	/**
	 * 诊疗项目备案信息上传 函数DataDown_zjks (S)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_zjks_S(TParm parm) {
		TParm result = commInterFace("DataDown_zjks", "S", parm,"");
		return result;
	}
	/**
	 * 调用复数公共接口方法
	 * 
	 * @param object
	 *            String
	 * @param function
	 *            String
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm commInterFace(String object, String function, TParm parm,String str) {
		// return ()callServerMethod(object,function,function);
		parm.setData("PIPELINE", object);
		parm.setData("PLOT_TYPE", function);
		parm.setData("HOSP_AREA", "HIS");
		// System.out.println("-----------commInterFace--------------" + parm);
		// INSInterface Interface=new INSInterface();
		TParm result = InsManager.getInstance().safe(parm,str);
		System.out.println("commInterFace:"+result);
		// result = testDataTool.getInstance().testOut(parm);
		// System.out.println("-----------commInterFace--------------" +result);
		// if(result.getErrCode()<0){
		// //System.out.println("出参公共接口错误:"+result.getErrText());
		// }
		// parm.setData("PIPELINE",object);
		// parm.setData("PLOT_TYPE",function);
		// parm.setData("HOSP_AREA","HIS");
		// TParm result=insDatatestTool.getInstance().safe(parm);
		// result=testDataTool.getInstance().testOut(parm);
		return result;
	}
	//zhangs end
        /**
	 * 备案信息上传 函数DataDown_zjks (X)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_zjks_X(TParm parm) {
		TParm result = commInterFace("DataDown_zjks", "X", parm,"");
		return result;
	}
	
	/**
	 * 备案信息下载 函数DataDown_zjkd (M)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_zjkd_M(TParm parm) {
		TParm result = commInterFace("DataDown_zjkd", "M", parm,"");
		return result;
	}
	 /**
	 * 目录外备案信息上传 函数DataDown_zjks (Y)方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_zjks_Y(TParm parm) {
		TParm result = commInterFace("DataDown_zjks", "Y", parm,"");
		return result;
	}
}
