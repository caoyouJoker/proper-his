package action.ins;

import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSOpdOrderTJTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJReg;

import com.dongyang.action.TAction;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: 医保管理
 * </p>
 * 
 * <p>
 * Description: 公共方法 执行：1.城职普通 2.城职门特 3.城居门特
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author pangben 2011-11-25
 * @version 1.0
 */
public class INSTJAction extends TAction {

//	/**
//	 * 公共方法 执行：1.城职普通 2.城职门特 3.城居门特
//	 * 
//	 * @param parm
//	 *            调用接口返回出参 U 方法 A 方法 2011-11-25
//	 * @return
//	 */
//	public TParm insCommFunction(TParm parm) {
//		int insType = parm.getInt("INS_TYPE"); // 1.城职 普通 2.城职门特 3.城居门特
//		TParm result = new TParm();
//		TConnection connection = getConnection();
//		switch (insType) {
//
//		case 1:// 城职普通
//			result = cityStaffCommon(parm, connection);
//			break;
//		case 2:// 城职门特
//			result = cityStaffClement(parm, connection);
//			break;
//		case 3://城居门特
//			result = specialCityDenizen(parm, connection);
//			break;
//		}
//		if (result.getErrCode() < 0) {
//			err(result.getErrCode() + " " + result.getErrText());
//			connection.close();
//			return result;
//		}
//		connection.commit();
//		connection.close();
//		return result;
//	}

//	/**
//	 * 城职门特
//	 * 
//	 * @param parm
//	 * @param connection
//	 * @return
//	 */
//	public TParm cityStaffClement(TParm parm, TConnection connection) {
//		return INSTJReg.getInstance().cityStaffClement(parm, connection);
//	}
//
//	/**
//	 * 城居门特
//	 * 
//	 * @param parm
//	 * @param connection
//	 * @return
//	 */
//	public TParm specialCityDenizen(TParm parm, TConnection connection) {
//		return INSTJReg.getInstance().specialCityDenizen(parm, connection);
//	}
//
//	/**
//	 * 城职普通
//	 * 
//	 * @param parm
//	 * @param connection
//	 * @return
//	 */
//	public TParm cityStaffCommon(TParm parm, TConnection connection) {
//
//		return INSTJReg.getInstance().cityStaffCommon(parm, connection);
//	}

	/**
	 * 删除老数据：INS_OPD_ORDER 表 INS_OPD 表中的数据
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteOldData(TParm parm) {
		TConnection connection = getConnection();
		TParm result = INSOpdTJTool.getInstance()
				.deleteINSOpd(parm, connection);// INSOpd 表
		if (result.getErrCode() < 0) {
			result.setErr(-1, "deleteOldData方法:" + result.getErrName() + ":"
					+ result.getErrText());
			connection.close();
			return result;
		}
		result = INSOpdOrderTJTool.getInstance().deleteINSOpdOrder(parm,
				connection);// INSOpdOrder 表
		if (result.getErrCode() < 0) {
			result.setErr(-1, "deleteOldData方法:" + result.getErrName() + ":"
					+ result.getErrText());
			connection.close();
			return result;
		}
		if (null == parm.getValue("OPB_FLG")) {
			result = INSMZConfirmTool.getInstance().deleteInsMZConfirm(parm,
					connection);// INSMZConfirm 表
			if (result.getErrCode() < 0) {
				result.setErr(-1, "deleteOldData方法:" + result.getErrName()
						+ ":" + result.getErrText());
				connection.close();
				return result;
			}
		}
		//删除医保在途状态
		result=INSRunTool.getInstance().deleteInsRun(parm, connection);
		if (result.getErrCode() < 0) {
			result.setErr(-1, "deleteOldData方法:" + result.getErrName() + ":"
					+ result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 测试
	 * 
	 * @param parm
	 * @return
	 */
	public TParm comm(TParm parm) {

		TConnection connection = getConnection();
		TParm invOrderParm = new TParm();
		// 执行添加INS_OPD_ORDER 表数据
		invOrderParm.setData("REGION_CODE", "1");
		invOrderParm.setData("CASE_NO", "1");
		invOrderParm.setData("SEQ_NO", "1"); // 序号
		invOrderParm.setData("CONFIRM_NO", "1"); // 资格确认号
		invOrderParm.setData("CHARGE_DATE", new TNull(String.class)); // 计价日期
		// 医保代码----？？？
		invOrderParm.setData("NHI_ORDER_CODE", "1");
		// 医嘱代码
		invOrderParm.setData("ORDER_CODE", "1");
		// 医嘱名称
		invOrderParm.setData("ORDER_DESC", "1");
		invOrderParm.setData("OWN_RATE", "1"); // 自负比例
		invOrderParm.setData("DOSE_CODE", "");
		invOrderParm.setData("STANDARD", ""); // 起付标准
		invOrderParm.setData("PRICE", "1"); // 单价
		invOrderParm.setData("QTY", "1"); // 数量
		invOrderParm.setData("TOTAL_AMT", "1"); // 总金额-----？？？
		invOrderParm.setData("TOTAL_NHI_AMT", "1"); // 医保总金额-----？？？
		// REG_PARM.getDouble("OWN_PRICE", j) * REG_PARM.getDouble("QTY", j)
		invOrderParm.setData("OWN_AMT", "1"); // 自费金额-------？？？？？
		invOrderParm.setData("ADDPAY_AMT", "1"); // 增付金额
		invOrderParm.setData("REFUSE_AMT", 0.00); // 拒付金额
		invOrderParm.setData("REFUSE_REASON_CODE", new TNull(String.class)); // 拒付原因代码
		invOrderParm.setData("REFUSE_REASON_NOTE", new TNull(String.class)); // 拒付原因注记
		invOrderParm.setData("OP_FLG", "N"); // 手术注记（Y：手术；N：非手术）:挂号
		invOrderParm.setData("CARRY_FLG", "N"); // 出院带药注记（Y：带药；N：不带药）
		invOrderParm.setData("PHAADD_FLG", "N"); // 增付注记（Y：增付；N：不增付）
		invOrderParm.setData("ADDPAY_FLG", "N"); // 累计增付注记（Y：累计增付；N：不累计增付）
		invOrderParm.setData("NHI_ORD_CLASS_CODE", new TNull(String.class));
		invOrderParm.setData("INSAMT_FLG", 0);// 对账标志为0
		invOrderParm.setData("RX_SEQNO", new TNull(String.class));// 处方号
		invOrderParm.setData("OPB_SEQNO", new TNull(String.class));// 门诊号
		invOrderParm.setData("REG_OPB_FLG", new TNull(String.class));// 门诊挂号标记
		invOrderParm.setData("TAKE_QTY", new TNull(String.class));// 收取数量
		invOrderParm.setData("ROUTE", new TNull(String.class));
		invOrderParm.setData("HYGIENE_TRADE_CODE", new TNull(String.class));
		invOrderParm.setData("INS_CROWD_TYPE", new TNull(String.class));
		invOrderParm.setData("INS_PAT_TYPE", new TNull(String.class));
		invOrderParm.setData("ORIGSEQ_NO", new TNull(String.class));
		invOrderParm.setData("TAKE_DAYS", new TNull(String.class));
		invOrderParm.setData("INV_NO", new TNull(String.class)); // 就诊日期时间---???
		invOrderParm.setData("OPT_USER", "1"); // 就诊日期时间---???
		invOrderParm.setData("OPT_TERM", "1"); // // 医保卡收据号
		TParm result = INSOpdOrderTJTool.getInstance().insertINSOpdOrder(
				invOrderParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
		TParm invParm = new TParm();
		invParm.setData("REGION_CODE", "1"); // 医院编码
		invParm.setData("CASE_NO", "1");
		invParm.setData("CONFIRM_NO", "1"); // 资格确认号
		invParm.setData("MR_NO", "1"); // 病案号
		invParm.setData("PHA_AMT", "1"); // 药品费发生金额
		invParm.setData("PHA_NHI_AMT", "1"); // 医保药品费
		invParm.setData("EXM_AMT", "1"); // 检查费
		invParm.setData("EXM_NHI_AMT", "1"); // 医保检查费
		invParm.setData("TREAT_AMT", "1"); // 治疗费（治疗费发生金额）
		invParm.setData("TREAT_NHI_AMT", "1"); // 医保治疗费（治疗费申报金额）
		invParm.setData("OP_AMT", "1"); // 手术费发生金额
		invParm.setData("OP_NHI_AMT", "1"); // 手术费申报金额
		invParm.setData("BED_AMT", "1"); // 床位费(床位费发生金额)
		invParm.setData("BED_NHI_AMT", "1"); // 医保床位费
		invParm.setData("MATERIAL_AMT", "1"); // 材料费
		invParm.setData("MATERIAL_NHI_AMT", "1"); // 医保材料费
		invParm.setData("OTHER_AMT", "1"); // 其它费用
		invParm.setData("OTHER_NHI_AMT", "1"); // 医保其它费用
		invParm.setData("BLOODALL_AMT", "1"); // 输全血发生金额
		invParm.setData("BLOODALL_NHI_AMT", "1"); // 输全血医保金额
		invParm.setData("BLOOD_AMT", "1"); // 成分输血发生金额
		invParm.setData("BLOOD_NHI_AMT", "1"); // 成分输血申报金额
		invParm.setData("TOT_AMT", "1"); // 发生金额----------？？？？？？（总金额）
		invParm.setData("NHI_AMT", "1"); // 医保金额----？？？？(申报金额)
		invParm.setData("START_STANDARD_AMTS", "1"); // 起付标准剩余额
		invParm.setData("START_STANDARD_AMT", "1"); // 起付标准剩余额
		invParm.setData("OTOT_PAY_AMT", "1"); // 专项基金支付
		invParm.setData("ADD_AMT", "1"); // 增负金额
		invParm.setData("OWN_AMT", "1"); // 自费金额
		invParm.setData("INS_STANDARD_AMT", "1"); // 起付标准金额
		invParm.setData("TRANBLOOD_OWN_AMT", "1"); // 输血自付金额
		invParm.setData("APPLY_AMT", "1"); // 申报金额
		invParm.setData("OTOT_AMT", "1"); // 专项基金支付限额
		invParm.setData("OINSTOT_AMT", "1"); //
		invParm.setData("INS_DATE", new TNull(String.class)); // 医保结算时间
		invParm.setData("INSAMT_FLG", 1); // 社保对帐标志INS_OPD中INSAMT_FLG对账标志为1
		invParm.setData("ACCOUNT_PAY_AMT", new TNull(String.class)); //
		invParm.setData("UNACCOUNT_PAY_AMT", new TNull(String.class)); //
		invParm.setData("ACCOUNT_LEFT_AMT", new TNull(String.class)); //
		invParm.setData("REFUSE_AMT", new TNull(String.class)); //
		invParm.setData("REFUSE_ACCOUNT_PAY_AMT", new TNull(String.class)); //
		invParm.setData("REFUSE_DESC", new TNull(String.class)); //
		invParm.setData("CONFIRM_F_USER", new TNull(String.class)); //
		invParm.setData("CONFIRM_F_DATE", new TNull(String.class)); //
		invParm.setData("CONFIRM_S_USER", new TNull(String.class)); //
		invParm.setData("CONFIRM_S_DATE", new TNull(String.class)); //
		invParm.setData("SLOW_DATE", new TNull(String.class)); //
		invParm.setData("SLOW_PAY_DATE", new TNull(String.class)); //
		invParm.setData("REFUSE_CODE", new TNull(String.class)); //
		invParm.setData("REFUSE_OTOT_AMT", new TNull(String.class)); //
		invParm.setData("RECEIPT_NO", new TNull(String.class)); //
		invParm.setData("MZ_DIAG", new TNull(String.class)); //
		invParm.setData("DRUG_DAYS", new TNull(String.class)); //
		invParm.setData("INS_STD_AMT", new TNull(String.class)); //
		invParm.setData("INS_CROWD_TYPE", new TNull(String.class)); //
		invParm.setData("INS_PAT_TYPE", new TNull(String.class)); //
		invParm.setData("REG_OPB_FLG", new TNull(String.class)); //
		invParm.setData("OWEN_PAY_SCALE", new TNull(String.class)); //
		invParm.setData("REDUCE_PAY_SCALE", new TNull(String.class)); //
		invParm.setData("REAL_OWEN_PAY_SCALE", new TNull(String.class)); //
		invParm.setData("SALVA_PAY_SCALE", new TNull(String.class)); //
		invParm.setData("BASEMED_BALANCE", new TNull(String.class)); //
		invParm.setData("INS_BALANCE", new TNull(String.class)); //
		invParm.setData("ISSUE", new TNull(String.class)); //
		invParm.setData("BCSSQF_STANDRD_AMT", new TNull(String.class)); //
		invParm.setData("PERCOPAYMENT_RATE_AMT", new TNull(String.class)); //
		invParm.setData("INS_HIGHLIMIT_AMT", new TNull(String.class)); //
		invParm.setData("TOTAL_AGENT_AMT", new TNull(String.class)); //
		invParm.setData("FLG_AGENT_AMT", new TNull(String.class)); //
		invParm.setData("APPLY_AMT_R", new TNull(String.class)); //
		invParm.setData("FLG_AGENT_AMT_R", new TNull(String.class)); //
		invParm.setData("REFUSE_DATE", new TNull(String.class)); //
		invParm.setData("REAL_PAY_LEVEL", new TNull(String.class)); //
		invParm.setData("VIOLATION_REASON_CODE", new TNull(String.class)); //
		invParm.setData("ARMY_AI_AMT", new TNull(String.class)); //
		invParm.setData("SERVANT_AMT", new TNull(String.class)); //
		invParm.setData("INS_PAY_AMT", new TNull(String.class)); //
		invParm.setData("UNREIM_AMT", new TNull(String.class)); //
		invParm.setData("REIM_TYPE", new TNull(String.class)); //
		invParm.setData("OPT_USER", "1"); //
		invParm.setData("OPT_TERM", "1"); //
		result = INSOpdTJTool.getInstance().insertINSOpd(invParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			// connection.close();
			return result;
		}
		TParm p = new TParm();
		p.setData("REGION_CODE", "1");
		p.setData("CASE_NO", "1");
		p.setData("CONFIRM_NO", "1");
		result = INSOpdTJTool.getInstance().updateINSOpd(p, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * 执行医保操作后修改收据值
	 * @param parm
	 * @return
	 */
	public TParm updateINSReceiptNo(TParm parm){
		TConnection connection = getConnection();
		TParm result = INSRunTool.getInstance().deleteInsRun(parm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
		}
		result=INSOpdTJTool.getInstance().updateINSReceiptNo(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * 执行医保操作后修改收据值
	 * @param parm
	 * @return
	 */
	public TParm updateINSPrintNo(TParm parm){
		TConnection connection = getConnection();
		TParm result = INSRunTool.getInstance().deleteInsRun(parm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
		}
		result=INSTJFlow.getInstance().updateInsAmtFlgPrint(parm, parm.getValue("RECP_TYPE"),connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
		}
		connection.commit();
		connection.close();
		return result;
	}
}
