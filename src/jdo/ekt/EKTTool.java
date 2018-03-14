package jdo.ekt;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 医疗卡工具类
 * </p>
 * 
 * <p>
 * Description: 医疗卡工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2010
 * </p>
 * 
 * <p>
 * Company:javahis
 * </p>
 * 
 * @author sundx
 * @version 1.0
 */
public class EKTTool extends TJDOTool {
	Double ar_amt;
	/**
	 * 构造器
	 */
	
	public EKTTool() {
		setModuleName("ekt\\EKTModule.x");
		onInit();
	}

	/**
	 * 实例
	 */
	private static EKTTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return EKTTool
	 */
	public static EKTTool getInstance() {
		if (instanceObject == null)
			instanceObject = new EKTTool();
		return instanceObject;
	}

	/**
	 * 得到交易号
	 * 
	 * @return String
	 */
	public String getBusinessNo() {
		return SystemTool.getInstance().getNo("ALL", "EKT", "BUSINESS_NO",
				"BUSINESS_NO");
	}

	/**
	 * 得到医疗卡充值号
	 * 
	 * @return String =================pangben 20111008
	 */
	public String getBillBusinessNo() {
		return SystemTool.getInstance().getNo("ALL", "EKT", "BIL_BUSINESS_NO",
				"BIL_BUSINESS_NO");
	}

	/**
	 * 得到医疗卡外部交易号
	 * 
	 * @return String ================pangben 20110927
	 */
	public String getTradeNo() {
		return SystemTool.getInstance().getNo("ALL", "EKT", "TRADE_NO",
				"TRADE_NO");
	}

	/**
	 * 写入医疗卡交易表
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insetTrade(TParm parm, TConnection connection) {
		// =====zhangp 20120224 modify start
		parm.setData("BANK_FLG", "N");
		// =====zhangp 20120224 modify end
		TParm result = update("insetTrade", parm, connection);
		return result;
	}

	/**
	 * 写入医疗卡明细表
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertEKTDetail(TParm parm, TConnection connection) {
		TParm result = update("insertEKTDetail", parm, connection);
		return result;
	}

	/**
	 * 确认交易
	 * 
	 * @param TradeNo
	 *            String
	 * @param connection
	 *            TConnection
	 * @return boolean
	 */
//	public boolean consumeConfirmation(String TradeNo, TConnection connection) {
//		TParm parm = new TParm();
//		parm.setData("Trade_NO", TradeNo);
//		parm.setData("STATE", 1);
//		TParm result = update("updateTrade", parm, connection);
//		if (result.getErrCode() != 0) {
//			System.out.println("EKTTool.consumeConfirmation(" + TradeNo + ")->"
//					+ result.getErrText());
//			return false;
//		}
//		return true;
//	}

	public TParm queryTradeNoCancel(TParm parm) {
		return query("queryTradeNoCancel", parm);
	}

	/**
	 * 退还途中交易
	 * 
	 * @param cardNo
	 *            String
	 * @return boolean
	 */
	public boolean checkCancel(String cardNo) {
		TParm parm = new TParm();
		parm.setData("CARD_NO", cardNo);
		TParm result = queryTradeNoCancel(parm);
		int count = result.getCount();
		for (int i = 0; i < count; i++) {
			String TradeNo = result.getValue("Trade_NO", i);
			consumeCancel(TradeNo);
		}
		return true;
	}

	public String check(String TradeNo, String caseNo) {
		TParm parm = new TParm();
		// parm.setData("Trade_NO", TradeNo);
		parm.setData("CASE_NO", caseNo);
		TParm result = queryTrade(parm);
		double amt = result.getDouble("AMT", 0);
		TParm parm3 = query("sumOpbdetailCaseNo", parm);
		double d2 = parm3.getDouble("AR_AMT", 0);
		if (amt != d2) {
			String s = StringTool.getString(TJDODBTool.getInstance()
					.getDBTime(), "yyyy/MM/dd HH:mm:ss");
			s = s + "医疗卡交易错误：交易号" + TradeNo + " 医疗卡交易金额不等于医嘱总金额!" + amt + " "
					+ d2;
			//System.out.println(s);
			return s;
		}
		parm3=queryOpdOrderError(parm);//====pangben 2013-4-27 
		if(parm3.getCount()>0){
			String s = StringTool.getString(TJDODBTool.getInstance()
					.getDBTime(), "yyyy/MM/dd HH:mm:ss");
			s = s + ",医疗卡交易号码出现问题!";
			//System.out.println(s);
			return s;
		}
		return "";
	}

	public double ss(double amt) {
		if (amt > 0)
			amt = ((int) (amt * 100.0 + 0.5)) / 100.0;
		else if (amt < 0)
			amt = ((int) (amt * 100.0 - 0.5)) / 100.0;
		return amt;
	}

	/**
	 * 取消交易
	 * 
	 * @param TradeNo
	 *            String
	 * @return boolean
	 */
	public boolean consumeCancel(String TradeNo) {
		TParm parm = new TParm();
		parm.setData("Trade_NO", TradeNo);
		parm.setData("STATE", 2);
		TConnection connection = getConnection();
		TParm result = update("updateTrade", parm, connection);
		if (result.getErrCode() != 0) {
			connection.rollback();
			connection.close();
			// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
			// ")->" + result.getErrText());
			return false;
		}
		result = queryTrade(parm);
		if (result.getErrCode() != 0) {
			connection.rollback();
			connection.close();
			// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
			// ").queryTrade->" + result.getErrText());
			return false;
		}
		String businessNo = result.getValue("BUSINESS_NO", 0);
		parm.setData("BUSINESS_NO", businessNo);
		parm = queryAccntdetail(parm);
		if (parm.getErrCode() != 0) {
			connection.rollback();
			connection.close();
			// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
			// ").queryTrade->" + parm.getErrText());
			return false;
		}
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			TParm p = new TParm();
			p.setData("BUSINESS_SEQ", parm.getData("BUSINESS_SEQ", i));
			p.setData("BUSINESS_NO", businessNo);
			p.setData("CHARGE_FLG",
					"1".equals(parm.getValue("CHARGE_FLG", i)) ? 2 : 1);
			result = updateAccntdetail(p, connection);
			if (result.getErrCode() != 0) {
				connection.rollback();
				connection.close();
				// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
				// ").for" + i + "->" + result.getErrText());
				return false;
			}
		}
		connection.commit();
		connection.close();
		return true;
	}

	/**
	 * 医疗卡退费操作
	 * 
	 * @param TradeNo
	 * @param connection
	 * @return
	 */
	public TParm consumeCancelOne(TParm parm, String TradeNo, int type) {
		String sql = "UPDATE EKT_Trade SET STATE = " + type + ",OPT_USER='"
				+ parm.getValue("OPT_USER") + "',OPT_DATE=SYSDATE,OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "' WHERE Trade_NO in (" + TradeNo
				+ ")";
		// System.out.println("consumeCancelOne 方法 sql::"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() != 0) {
			// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
			// ")->" + result.getErrText());
			return result;
		}
		// result = queryTrade(parm);
		// if(result.getErrCode() != 0)
		// {
		// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
		// ").queryTrade->" + result.getErrText());
		// return result;
		// }
		// String businessNo = result.getValue("BUSINESS_NO",0);
		// parm.setData("BUSINESS_NO",businessNo);
		// parm = queryAccntdetail(parm);
		// if(parm.getErrCode() != 0)
		// {
		// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
		// ").queryTrade->" + parm.getErrText());
		// return result;
		// }
		// int count = parm.getCount();
		// for(int i = 0;i < count;i++)
		// {
		// TParm p = new TParm();
		// p.setData("BUSINESS_SEQ",parm.getData("BUSINESS_SEQ",i));
		// p.setData("BUSINESS_NO",businessNo);
		// p.setData("CHARGE_FLG","1".equals(parm.getValue("CHARGE_FLG",i))?2:1);
		// result = updateAccntdetail(p,connection);
		// if(result.getErrCode() != 0)
		// {
		// System.out.println("EKTTool.consumeConfirmation(" + TradeNo + ").for"
		// + i +
		// "->" + result.getErrText());
		// return result;
		// }
		// }
		return result;
	}

	/**
	 * 医疗卡退费操作
	 * 
	 * @param TradeNo
	 * @param connection
	 * @return
	 */
	public TParm consumeCancelOne(TParm parm, String TradeNo, int type,
			TConnection connection) {
		String sql = "UPDATE EKT_Trade SET STATE = " + type + ",OPT_USER='"
				+ parm.getValue("OPT_USER") + "',OPT_DATE=SYSDATE,OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "' WHERE Trade_NO in (" + TradeNo
				+ ")";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() != 0) {
			// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
			// ")->" + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 根据CASE_NO 获得 Trade_NO
	 * 
	 * @param parm
	 * @return =========pangben 2011-11-17
	 */
	public TParm selectTradeNo(TParm parm) {
		return query("selectTradeNo", parm);
	}

	/**
	 * 查询交易明细状态数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryAccntdetail(TParm parm) {
		return query("queryAccntdetail", parm);
	}

	/**
	 * 查询内部交易号
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryTrade(TParm parm) {
		return query("queryTrade", parm);
	}

	/**
	 * 更新交易明细状态数据
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateAccntdetail(TParm parm, TConnection connection) {
		return update("updateAccntdetail", parm, connection);
	}

	/**
	 * 写入医疗卡主表
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertEKTMaster(TParm parm, TConnection connection) {
		TParm result = update("insertEKTMaster", parm, connection);
		return result;
	}

	/**
	 * 医疗卡与银行卡关联
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateEKTAndBank(TParm parm) {
		TParm result = update("updateEKTAndBank", parm);
		return result;
	}

	/**
	 * 医疗卡收费主档打票完成
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateTradePrint(TParm parm, TConnection connection) {
		TParm result = update("updateTradePrint", parm, connection);
		return result;
	}

	/**
	 * 删除医疗卡主表
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm deleteEKTMaster(TParm parm, TConnection connection) {
		TParm result = update("deleteEKTMaster", parm, connection);
		return result;
	}

	/**
	 * 更新医疗卡明细表对账标记
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateEKTDetail(TParm parm, TConnection connection) {
		TParm result = update("updateEKTDetail", parm, connection);
		return result;
	}

	/**
	 * 查询明细
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getDetail(TParm parm) {
		return query("getDetail", parm);
	}

	/**
	 * 取消收费
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm detailCancel(TParm parm, TConnection connection) {
		TParm result = update("detailCancel", parm, connection);
		return result;
	}

	/**
	 * 操作失败删除医疗卡明细表操作
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteDetail(TParm parm, TConnection connection) {
		TParm result = update("deleteDetail", parm, connection);
		return result;
	}

	/**
	 * 更新医疗卡主表余额
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateEKTMaster(TParm parm, TConnection connection) {
		TParm result = update("updateEKTMaster", parm, connection);
		return result;
	}

	/**
	 * 更新医疗卡交易状态标记
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm confirmEKTDetail(TParm parm, TConnection connection) {
		TParm result = update("confirmEKTDetail", parm, connection);
		return result;
	}

	/**
	 * 取得医疗卡余额
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getEKTCurrentBalance(TParm parm) {
		TParm result = query("getEKTCurrentBalance", parm);
		return result;
	}

	/**
	 * 得到处方医疗卡余额
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getRxBalance(TParm parm) {
		TParm result = query("getRxBalance", parm);
		return result;
	}

	/**
	 * 得到处方医疗卡原额
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getRxOriginal(TParm parm) {
		TParm result = query("getRxOriginal", parm);
		return result;
	}

	/**
	 * 取得规定时间段内医疗卡交易金额
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getEKTBusinessAmt(TParm parm) {
		TParm result = query("getEKTBusinessAmt", parm);
		return result;
	}

	/**
	 * 得到MrNo
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getMrNo(TParm parm) {
		return query("getMrNo", parm);
	}

	/**
	 * 得到卡片号码
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	// zhangp 20120106 注释
	// public TParm getCardNo(TParm parm)
	// {
	// return query("getCardNo",parm);
	// }
	/**
	 * 得到处方费用
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getRxAmt(TParm parm) {
		return query("getRxAmt", parm);
	}

	/**
	 * 医疗卡付费接口参数整理
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm setEKTPayParm(TParm parm) {
		if (parm == null || parm.getCount("MR_NO") <= 0)
			return null;
		TParm result = new TParm();
		TParm eKTParmD = new TParm();
		TParm eKTParmM = new TParm();
		for (int i = 0; i < parm.getCount("MR_NO"); i++) {
			eKTParmD.addData("REGION_CODE", parm.getValue("REGION_CODE", i));
			eKTParmD.addData("MR_NO", parm.getValue("MR_NO", i));
			eKTParmD.addData("CASE_NO", parm.getValue("CASE_NO", i));
			eKTParmD.addData("BUSINESS_SEQ", "" + (i + 1));
			eKTParmD.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
			eKTParmD.addData("RX_NO", parm.getValue("RX_NO", i));
			eKTParmD.addData("SEQ_NO", parm.getValue("SEQ_NO", i));
			eKTParmD.addData("CHARGE_FLG", "1");
			eKTParmD.addData("BUSINESS_AMT", parm.getValue("OWN_AMT", i));
			eKTParmD.addData("CASHIER_CODE", parm.getValue("OPT_USER", i));
			eKTParmD.addData("BUSINESS_DATE", parm.getData("OPT_DATE", i));
			eKTParmD.addData("ACCNT_STATUS", "1");
			eKTParmD.addData("ACCNT_USER", "");
			eKTParmD.addData("ACCNT_DATE", "");
			eKTParmD.addData("OPT_USER", parm.getValue("OPT_USER", i));
			eKTParmD.addData("OPT_DATE", parm.getData("OPT_DATE", i));
			eKTParmD.addData("OPT_TERM", parm.getValue("OPT_TERM", i));
			// 卡号
			eKTParmD.addData("CARD_NO", "001");
			// 交易流水号
			eKTParmD.addData("BUSINESS_NO", "001");
			// 交易前余额
			eKTParmD.addData("ORIGINAL_BALANCE", "66");
			// 交易后余额
			eKTParmD.addData("CURRENT_BALANCE", "66");
			// 交易状态
			eKTParmD.addData("BUSINESS_STATUS", "2");
		}
		// 卡号
		eKTParmM.setData("CARD_NO", "001");
		// 病患身份证号
		eKTParmM.setData("ID_NO", "120102189311083334");
		// 病患姓名
		eKTParmM.setData("NAME", "测试");
		// 交易后余额
		eKTParmM.setData("CURRENT_BALANCE", "66");
		eKTParmM.setData("CREAT_USER", parm.getValue("OPT_USER", 0));
		eKTParmM.setData("OPT_USER", parm.getValue("OPT_USER", 0));
		eKTParmM.setData("OPT_DATE", parm.getData("OPT_DATE", 0));
		eKTParmM.setData("OPT_TERM", parm.getValue("OPT_TERM", 0));
		result.setData("EKT_MASTER", eKTParmM.getData());
		result.setData("EKT_ACCNTDETAIL", eKTParmD.getData());
		return result;
	}

	/**
	 * 医疗卡付费接口
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm eKTPay(TParm parm, TConnection connection) {
		TParm eKTParm = setEKTPayParm(parm);
		TParm result = new TParm();
		if (eKTParm == null) {
			result.setErr(-1, "参数不能为空！");
			return result;
		}
		// 得到保存医疗卡主表数据
		TParm eKTParmM = eKTParm.getParm("EKT_MASTER");
		// 取得医疗卡余额
		result = EKTTool.getInstance().getEKTCurrentBalance(eKTParmM);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// 检核此卡是否已经消费过(检主表是否有数据核医疗卡)
		if (result.getCount("CURRENT_BALANCE") <= 0) {
			// 写入医疗卡主表
			result = EKTTool.getInstance()
					.insertEKTMaster(eKTParmM, connection);
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		} else {
			// 写入医疗卡主表
			result = EKTTool.getInstance()
					.updateEKTMaster(eKTParmM, connection);
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		// 得到保存医疗卡明细表数据
		TParm eKTParmD = eKTParm.getParm("EKT_ACCNTDETAIL");
		// 写入医疗卡明细表
		for (int i = 0; i < eKTParmD.getCount("MR_NO"); i++) {
			result = EKTTool.getInstance().insertEKTDetail(eKTParmD.getRow(i),
					connection);
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * 医疗卡退费费接口参数整理
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm setEKTRefundParm(TParm parm) {
		if (parm == null || parm.getCount("MR_NO") <= 0)
			return null;
		TParm result = new TParm();
		TParm eKTParmD = new TParm();
		TParm eKTParmM = new TParm();
		for (int i = 0; i < parm.getCount("MR_NO"); i++) {
			eKTParmD.addData("REGION_CODE", parm.getValue("REGION_CODE", i));
			eKTParmD.addData("MR_NO", parm.getValue("MR_NO", i));
			eKTParmD.addData("CASE_NO", parm.getValue("CASE_NO", i));
			eKTParmD.addData("BUSINESS_SEQ", "" + (i + 1));
			eKTParmD.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
			eKTParmD.addData("RX_NO", parm.getValue("RX_NO", i));
			eKTParmD.addData("SEQ_NO", parm.getValue("SEQ_NO", i));
			eKTParmD.addData("CHARGE_FLG", "2");
			eKTParmD.addData("BUSINESS_AMT", parm.getValue("OWN_AMT", i));
			eKTParmD.addData("CASHIER_CODE", parm.getValue("OPT_USER", i));
			eKTParmD.addData("BUSINESS_DATE", parm.getData("OPT_DATE", i));
			eKTParmD.addData("ACCNT_STATUS", "1");
			eKTParmD.addData("ACCNT_USER", "");
			eKTParmD.addData("ACCNT_DATE", "");
			eKTParmD.addData("OPT_USER", parm.getValue("OPT_USER", i));
			eKTParmD.addData("OPT_DATE", parm.getData("OPT_DATE", i));
			eKTParmD.addData("OPT_TERM", parm.getValue("OPT_TERM", i));
			// 卡号
			eKTParmD.addData("CARD_NO", "001");
			// 交易流水号
			eKTParmD.addData("BUSINESS_NO", "001");
			// 交易前余额
			eKTParmD.addData("ORIGINAL_BALANCE", "66");
			// 交易后余额
			eKTParmD.addData("CURRENT_BALANCE", "66");
			// 交易状态
			eKTParmD.addData("BUSINESS_STATUS", "2");
		}
		// 卡号
		eKTParmM.setData("CARD_NO", "001");
		// 交易后余额
		eKTParmM.setData("CURRENT_BALANCE", "123");
		eKTParmM.setData("OPT_USER", parm.getValue("OPT_USER", 0));
		eKTParmM.setData("OPT_DATE", parm.getData("OPT_DATE", 0));
		eKTParmM.setData("OPT_TERM", parm.getValue("OPT_TERM", 0));
		result.setData("EKT_MASTER", eKTParmM.getData());
		result.setData("EKT_ACCNTDETAIL", eKTParmD.getData());
		return result;
	}

	/**
	 * 医疗卡退费方法
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm eKTRefund(TParm parm, TConnection connection) {
		TParm eKTParm = setEKTRefundParm(parm);
		TParm result = new TParm();
		if (eKTParm == null) {
			result.setErr(-1, "参数不能为空！");
			return result;
		}
		// 得到保存医疗卡主表数据
		TParm eKTParmM = eKTParm.getParm("EKT_MASTER");
		// 写入医疗卡主表
		result = EKTTool.getInstance().updateEKTMaster(eKTParmM, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// 得到保存医疗卡明细表数据
		TParm eKTParmD = eKTParm.getParm("EKT_ACCNTDETAIL");
		// 写入医疗卡明细表
		for (int i = 0; i < eKTParmD.getCount("MR_NO"); i++) {
			result = EKTTool.getInstance().insertEKTDetail(eKTParmD.getRow(i),
					connection);
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * 医疗卡交易确认
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm eKTConfirm(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "参数不能为空！");
			return result;
		}
		// 写入医疗卡明细表
		result = EKTTool.getInstance().confirmEKTDetail(parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 医疗卡对账方法
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm eKTAccnt(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "参数不能为空！");
			return result;
		}
		// 写入医疗卡明细表
		parm.setData("ACCNT_USER", parm.getData("OPT_USER"));
		parm.setData("ACCNT_DATE", parm.getData("OPT_DATE"));
		result = EKTTool.getInstance().updateEKTDetail(parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 添加医疗卡充值退款档
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertEKTBilPay(TParm parm, TConnection connection) {
		TParm result = update("insertEKTBilPay", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}
	
	/**
	 * 添加医疗卡发卡记录档
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertEKTIssuelog(TParm parm, TConnection connection) {
		// System.out.println("parm==="+parm);
		TParm result = update("insertEKTIssuelog", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * 医疗卡修改密码
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateEKTPWD(TParm parm) {
		TParm result = update("updateEKTPWD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}

	/**
	 * 读取医疗卡信息
	 * 
	 * @return TParm
	 */
	public TParm selectEKTIssuelog(TParm parm) {
		TParm result = query("selectEKTIssuelog", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}

	/**
	 * 修改医疗卡表操作
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateEKTIssuelog(TParm parm, TConnection connection) {
		TParm result = update("updateEKTIssuelog", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * 删除操作：门诊收费出现错误使用
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm deleteTrade(TParm parm, TConnection connection) {
		TParm result = update("deleteTrade", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * 医疗卡密码确认
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectPWDEKTIssuelog(TParm parm) {
		TParm result = query("selectPWDEKTIssuelog", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 门诊医生站将EKT_Trade 表中的数据设置作废状态通过就诊号删除
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteOpbTrade(TParm parm) {
		TParm result = update("deleteOpbTrade", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 医疗卡交易记录总金额 zhangp 20121219
	 */
	public TParm selectEKTTradeTotal(TParm parm) {
		TParm result = query("selectEKTTradeTotal", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 医疗卡交易记录明细金额 zhangp 20121219
	 */
	public TParm selectEKTTradeDetail(TParm parm) {
		TParm result = query("selectEKTTradeDetail", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 医疗卡日结 ====zhangp 20111229
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm executeEKTAccount(TParm parm, TConnection connection) {
		TParm resultAll = new TParm();
		TParm result = new TParm();
		// double amt = 0.00;
		TParm selDateAccountInParm = new TParm();
		selDateAccountInParm.setData("BUSINESS_DATE", parm
				.getData("BUSINESS_DATE"));
		selDateAccountInParm.setData("OPT_USER", parm.getData("OPT_USER"));
		// =======zhangp 20120226 modify start
		selDateAccountInParm
				.setData("ACCOUNT_SEQ", parm.getData("ACCOUNT_SEQ"));
		// ========zhangp 20120226 modify end

		// 购卡笔数
		selDateAccountInParm.setData("CHARGE_FLG", "4");
		result = query("selectEktaccntDetailByChargeFlag",
				selDateAccountInParm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		if (result.getCount() < 1) {
			result.setData("COUNT", 0, 0);
		}
		resultAll.addData("BUY_QTY", result.getData("COUNT", 0));
		int buy_qty = result.getInt("COUNT", 0);
		// double buy_amt =result.getDouble("SUM", 0);

		// 换卡笔数
		selDateAccountInParm.setData("CHARGE_FLG", "8");
		result = query("selectEktaccntDetailByChargeFlag",
				selDateAccountInParm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		if (result.getCount() < 1) {
			result.setData("COUNT", 0, 0);
		}
		resultAll.addData("CHANGE_QTY", result.getData("COUNT", 0));
		int chang_qty = result.getInt("COUNT", 0);
		// ====zhangp 20120226 modify start
		String sql = "SELECT FACTORAGE_FEE FROM EKT_ISSUERSN WHERE ISSUERSN_CODE = '8'";
		TParm singleFact = new TParm(TJDODBTool.getInstance().select(sql));
		if (singleFact.getCount() < 0) {
			// System.out.println("发卡原因手续费不存在");
			return singleFact;
		}
		double chang_amt = chang_qty * singleFact.getDouble("FACTORAGE_FEE", 0);
		// =====zhangp 20120226 modify end
		// 补卡笔数
		selDateAccountInParm.setData("CHARGE_FLG", "5");
		result = query("selectEktaccntDetailByChargeFlag",
				selDateAccountInParm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		if (result.getCount() < 1) {
			result.setData("COUNT", 0, 0);
		}
		resultAll.addData("ADD_QTY", result.getData("COUNT", 0));
		// double add_amt =result.getDouble("SUM", 0);
		int add_qty = result.getInt("COUNT", 0);

		// 发卡笔数
		int sent_count = buy_qty + chang_qty + add_qty;
		resultAll.addData("SENT_COUNT", sent_count);

		// 手续笔数
		// ===zhangp 20120314 start
		// int factorage_qty = sent_count;
		int factorage_qty = chang_qty;
		// ===zhangp 20120314 end
		// =======zhangp 20120226 modify start
		double factorage_amt = chang_amt;
		// ========zhangp 20120226 modify end
		resultAll.addData("FACTORAGE_QTY", factorage_qty);
		resultAll.addData("FACTORAGE_AMT", factorage_amt);

		// 现金笔数
		selDateAccountInParm.setData("CHARGE_FLG", "3");
		result = query("selectEktaccntDetailSum", selDateAccountInParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		if (result.getCount() < 1) {
			result.setData("COUNT", 0, 0);
			result.setData("SUM", 0, 0);
		}
		resultAll.addData("PAY_MEDICAL_QTY", result.getData("COUNT", 0));
		Integer pay_medical_qty = new Integer(result.getData("COUNT", 0)
				.toString());
		double pay_medical_atm = StringTool.getDouble(result.getData("SUM", 0)
				.toString());
		// =======zhangp 20120226 modify start
		// selDateAccountInParm.setData("CHARGE_FLG", "3");
		// result = query("selectEktaccntDetailSum",
		// selDateAccountInParm,connection);
		// if (result.getErrCode() < 0) {
		// err(result.getErrName() + " " + result.getErrText());
		// return result;
		// }
		// if (result.getCount() < 1) {
		// result.setData("COUNT", 0 , 0);
		// result.setData("SUM", 0 , 0);
		// }
		// pay_medical_qty = pay_medical_qty + result.getInt("COUNT", 0);
		// pay_medical_atm = pay_medical_atm + result.getDouble("SUM",0);
		// =======zhangp 20120226 modify end
		resultAll.addData("PAY_MEDICAL_ATM", pay_medical_atm);
		// System.out.println(""+pay_medical_atm+result);
		// 退款金额笔数
		selDateAccountInParm.setData("CHARGE_FLG", "7");
		result = query("selectEktaccntDetailSum", selDateAccountInParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		if (result.getCount() < 1) {
			result.setData("COUNT", 0, 0);
			result.setData("SUM", 0, 0);
		}
		int npay_medical_qty = StringTool.getInt(result.getData("COUNT", 0)
				.toString());
		double npay_medical_amt = StringTool.getDouble(result.getData("SUM", 0)
				.toString());
		resultAll.addData("NPAY_MEDICAL_QTY", npay_medical_qty);
		resultAll.addData("NPAY_MEDICAL_AMT", npay_medical_amt);

		// 实收金额
		// ===zhangp 20120314 start
		// double ar_amt = pay_medical_atm - npay_medical_amt;
		ar_amt = pay_medical_atm - npay_medical_amt + factorage_amt;
		// ===zhangp 20120314 end
		TParm upREGRecp = new TParm();
		upREGRecp.setData("ACCOUNT_SEQ", parm.getData("ACCOUNT_SEQ"));
		upREGRecp.setData("ACCOUNT_USER", parm.getData("ACCOUNT_USER"));
		upREGRecp.setData("ACCOUNT_DATE", parm.getData("ACCOUNT_DATE"));
		upREGRecp.setData("FACTORAGE_AMT", factorage_amt);
		upREGRecp.setData("FACTORAGE_QTY", factorage_qty);
		upREGRecp.setData("ACCOUNT_TYPE", parm.getData("ACCOUNT_TYPE"));
		upREGRecp.setData("BUY_QTY", buy_qty);
		upREGRecp.setData("CHANGE_QTY", chang_qty);
		upREGRecp.setData("ADD_QTY", add_qty);
		upREGRecp.setData("SENT_COUNT", sent_count);
		upREGRecp.setData("FACTORAGE_QTY", factorage_qty);
		upREGRecp.setData("FACTORAGE_AMT", factorage_amt);
		upREGRecp.setData("PAY_MEDICAL_ATM", pay_medical_atm);
		upREGRecp.setData("PAY_MEDICAL_QTY", pay_medical_qty);
		upREGRecp.setData("NPAY_MEDICAL_QTY", npay_medical_qty);
		upREGRecp.setData("NPAY_MEDICAL_AMT", npay_medical_amt);
		upREGRecp.setData("AR_AMT", ar_amt);
		upREGRecp.setData("STATUS", "0");
		upREGRecp.setData("REGION_CODE", parm.getData("REGION_CODE"));
		upREGRecp.setData("OPT_USER", parm.getData("OPT_USER"));
		upREGRecp.setData("OPT_TERM", parm.getData("OPT_TERM"));
		upREGRecp.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		// 如果全为0 即无数据则不日结 20120131 zhangp
		if (factorage_amt == 0 && factorage_qty == 0 && buy_qty == 0
				&& chang_qty == 0 && add_qty == 0 && sent_count == 0
				&& factorage_qty == 0 && factorage_amt == 0
				&& pay_medical_atm == 0 && pay_medical_qty == 0
				&& npay_medical_qty == 0 && npay_medical_amt == 0
				&& ar_amt == 0) {
			result = new TParm();
			result.setErrCode(-1);
			result.setErrText("无日结数据");
			return result;
		}
		selDateAccountInParm.setData("CASHIER_CODE", parm
				.getData("ACCOUNT_USER"));
		result = update("updateEktAccntDetailStatus", selDateAccountInParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		result = update("insertEKTAccount", upREGRecp, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return resultAll;
	}

	/**
	 * 查询日结数据 ======zhangp 20120105
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm accountQuery(TParm parm) {
		TParm result = new TParm();
		result = this.query("accountQuery", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询手续费 =====zhangp 20120201
	 * 
	 * @return
	 */
	public TParm factageFee(String id) {
		String sql = "SELECT ISSUERSN_DESC,FACTORAGE_FEE FROM EKT_ISSUERSN WHERE ISSUERSN_CODE = '"
				+ id + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询发卡原因默认 ===zhangp 20120201
	 * 
	 * @return
	 */
	public TParm sendCause() {
		String sql = "SELECT ISSUERSN_CODE AS ID,ISSUERSN_DESC AS NAME,FLG FROM EKT_ISSUERSN WHERE FLG = 'Y'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询分组票据，医疗卡退费时修改EKT_ACCNTDETAIL表中CHARGE_FLG字段使用
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryGroupAccntdetail(TParm parm) {
		return query("queryGroupAccntdetail", parm);
	}

	/**
	 * 取得支付方式默认值 =======zhangp 20120224
	 * 
	 * @return
	 */
	public String getPayTypeDefault() {
		String sql = "SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_DICTIONARY WHERE GROUP_ID='GATHER_TYPE' ORDER BY SEQ,ID ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			return "";
		}
		if (result.getCount() < 0) {
			return "";
		}
		String id = result.getData("ID", 0).toString();
		return id;
	}

	/**
	 * 医疗卡收费
	 * 
	 * @param ioparm
	 *            收费的医嘱
	 * @param ektParm
	 *            医疗卡信息
	 * @param caseNo
	 *            就诊号码
	 * @param type
	 *            执行扣款
	 * @param businessType
	 *            类型
	 * @return 处方签号码
	 */
	public TParm onOPDAccntClient(TParm sumParm, TParm ioparm, TParm ektParm,
			String caseNo, String businessType, double ektAmt,
			TConnection connection) {
		String businessNo = EKTTool.getInstance().getBusinessNo();// 内部交易号码
		String cardNo = ektParm.getValue("PK_CARD_NO");// 卡号
		String mrNo = ektParm.getValue("MR_NO");
		String cashierCode = sumParm.getValue("OPT_USER");
		double originalBalance = ektParm.getDouble("NEW_CURRENT_BALANCE");// 执行医保操作：医疗卡金额=
																			// 医保分割后的金额+医疗卡余额
		String term = sumParm.getValue("OPT_TERM");
		TParm tempParm = new TParm();
		String TradeNo = EKTTool.getInstance().getTradeNo();// 得到医疗卡外部交易号
		tempParm.setData("CURRENT_BALANCE", ektAmt);//现在医疗卡中剩余的金额
		tempParm.setData("Trade_NO", TradeNo);// 外部交易号
		tempParm.setData("CARD_NO", cardNo);// 卡号
		tempParm.setData("MR_NO", mrNo);// 病案号
		tempParm.setData("BUSINESS_NO", businessNo);// 内部交易号
		tempParm.setData("PAT_NAME", ektParm.getValue("PAT_NAME"));// 病患名称
		tempParm.setData("OLD_AMT", originalBalance);// 原来金额
		tempParm.setData("AMT", sumParm.getDouble("billAmt"));// 操作金额：未收费金额
		tempParm.setData("STATE", 1);// 扣款
		tempParm.setData("BUSINESS_TYPE", businessType);// 收费类型 OPB /REG
		tempParm.setData("OPT_USER", cashierCode);
		tempParm.setData("OPT_TERM", term);
		tempParm.setData("ID_NO", "none");
		tempParm.setData("NAME", ektParm.getValue("PAT_NAME"));
		tempParm.setData("CREAT_USER", cashierCode);
		tempParm.setData("OPT_USER", cashierCode);
		tempParm.setData("CASE_NO", caseNo);
		tempParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		tempParm.setData("OPT_TERM", term);
		TParm result = createCard(tempParm, connection);
		if (result.getErrCode() < 0) {
			return result;
		}
		Map map = new HashMap();
		int count = ioparm.getCount("ORDER_CODE");

		for (int i = 0; i < count; i++) {
			String rxNo = ioparm.getValue("RX_NO", i);
			TParm parm = (TParm) map.get(rxNo);
			if (parm == null) {
				parm = new TParm();
				map.put(rxNo, parm);
			}
			parm.addData("ORDER_CODE", ioparm.getData("ORDER_CODE", i));
			parm.addData("SEQ_NO", ioparm.getData("SEQ_NO", i));
			parm.addData("AMT", ioparm.getData("AMT", i));
			parm.addData("BILL_FLG", ioparm.getData("BILL_FLG", i));
		}
		// }
		Iterator iterator = map.keySet().iterator();
		List list = new ArrayList();
		int[] pv = { 0 };
		while (iterator.hasNext()) {
			String rxNo = (String) iterator.next();
			TParm parm = (TParm) map.get(rxNo);
			// 同一处方金额相同，修改不同药品
			// if(getDoubleSum(parm,"AMT") == getRxAmt(cardNo,caseNo,rxNo))
			// continue;
			if (!accnt(parm, cardNo, mrNo, caseNo, rxNo, cashierCode, term,
					businessNo, originalBalance, connection, businessType, pv))
				return null;
			list.add(rxNo);
		}
		// 更新余额
		// TParm p = new TParm();
		// p.setData("CARD_NO", cardNo);
		// p.setData("CURRENT_BALANCE", ektAmt);
		// p.setData("OPT_USER", cashierCode);
		// p.setData("OPT_DATE", SystemTool.getInstance().getDate());
		// p.setData("OPT_TERM", term);
		// TParm result = EKTTool.getInstance().updateEKTMaster(p, connection);
		// if (result.getErrCode() != 0) {
		// err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
		// + result.getErrText());
		// connection.rollback();
		// connection.close();
		// return null;
		// }
		
		return tempParm;
	}

	/**
	 * 计算
	 * 
	 * @param ioparm
	 *            TParm
	 * @param cardNo
	 *            String
	 * @param mrNo
	 *            String
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @param cashierCode
	 *            String
	 * @param term
	 *            String
	 * @param pv
	 *            Object[]
	 * @param date
	 *            Timestamp
	 * @param connection
	 *            TConnection
	 * @param type
	 *            int
	 * @param greenUseAmt
	 *            double 绿色通道使用金额
	 * @param ektUseAmt
	 *            double 医疗卡使用金额
	 * @param greenBalance
	 *            double //绿色通道扣款剩余总金额
	 * @param greenPathTotAl
	 *            double //绿色通道审批金额
	 * @return boolean
	 */
	private boolean accnt(TParm ioparm, String cardNo, String mrNo,
			String caseNo, String rxNo, String cashierCode, String term,
			String businessNo, double originalBalance, TConnection connection,
			String businessType, int[] pv) {
		TParm p = new TParm();
		p.setData("CASE_NO", caseNo);
		TParm r = new TParm();
		p.setData("RX_NO", rxNo);
		//r = EKTTool.getInstance().getDetail(p);
		int index = pv[0];
		//int count = r.getCount("ORDER_CODE");
		// 医保操作不用添加退款
//		for (int i = 0; i < count; i++) {
//			index++;
//			TParm parm = new TParm();
//			parm.setData("BUSINESS_NO", businessNo);
//			parm.setData("BUSINESS_SEQ", index);
//			parm.setData("CARD_NO", cardNo);
//			parm.setData("MR_NO", mrNo);
//			parm.setData("CASE_NO", caseNo);
//			parm.setData("ORDER_CODE", r.getValue("ORDER_CODE", i));
//			parm.setData("RX_NO", rxNo);
//			parm.setData("SEQ_NO", r.getInt("SEQ_NO", i));
//			double amt = -r.getDouble("BUSINESS_AMT", i);// 这一个医嘱需要扣款的金额
//			parm.setData("CHARGE_FLG", "2");// 状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡,6,作废)
//			parm.setData("ORIGINAL_BALANCE", originalBalance);// 收费前余额
//			parm.setData("BUSINESS_AMT", amt);
//			originalBalance -= amt;
//			parm.setData("CURRENT_BALANCE", originalBalance);
//			parm.setData("GREEN_BALANCE", 0.00);// 绿色通道剩余金额=当前剩余金额-每一笔操作金额
//			parm.setData("GREEN_BUSINESS_AMT", 0.00);// 绿色通道每一笔使用金额
//			parm.setData("CASHIER_CODE", cashierCode);
//			parm.setData("BUSINESS_DATE", SystemTool.getInstance().getDate());
//			// 1：交易执行完成
//			// 2：双方确认完成
//			parm.setData("BUSINESS_STATUS", "1");
//			// 1：未对帐
//			// 2：对账成功
//			// 3：对账失败
//			parm.setData("ACCNT_STATUS", "1");
//			parm.setData("ACCNT_USER", new TNull(String.class));
//			parm.setData("ACCNT_DATE", new TNull(Timestamp.class));
//			parm.setData("OPT_USER", cashierCode);
//			parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
//			parm.setData("OPT_TERM", term);
//			parm.setData("BUSINESS_TYPE", businessType);
//			TParm result = EKTTool.getInstance().insertEKTDetail(parm,
//					connection);
//			if (result.getErrCode() != 0) {
//				err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
//						+ result.getErrText());
//				connection.rollback();
//				connection.close();
//				return false;
//			}
//		}
		r = detailCancel(p, connection);
		// }
		if (r.getErrCode() != 0) {
			err("ERR:EKTIO.onOPDAccnt.detailCancel " + r.getErrCode()
					+ r.getErrText());
			connection.rollback();
			connection.close();
			return false;
		}
		int count = ioparm.getCount("ORDER_CODE");
		for (int i = 0; i < count; i++) {
			if (!ioparm.getBoolean("BILL_FLG", i))
				continue;
			index++;
			TParm parm = new TParm();
			parm.setData("BUSINESS_NO", businessNo);
			parm.setData("BUSINESS_SEQ", index);
			parm.setData("CARD_NO", cardNo);
			parm.setData("MR_NO", mrNo);
			parm.setData("CASE_NO", caseNo);
			parm.setData("ORDER_CODE", ioparm.getValue("ORDER_CODE", i));
			parm.setData("RX_NO", rxNo);
			parm.setData("SEQ_NO", ioparm.getInt("SEQ_NO", i));

			double amt = ioparm.getDouble("AMT", i);
			if (amt > 0)
				amt = ((int) (amt * 100.0 + 0.5)) / 100.0;
			else if (amt < 0)
				amt = ((int) (amt * 100.0 - 0.5)) / 100.0;
			// if (insFlg) {// 医保退款
			// parm.setData("CHARGE_FLG", "2"); //
			// 状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡,6,作废)
			// parm.setData("BUSINESS_AMT", -amt);
			// } else {
			parm.setData("CHARGE_FLG", "1"); // 状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡,6,作废)
			parm.setData("BUSINESS_AMT", amt);
			// }
			parm.setData("ORIGINAL_BALANCE", originalBalance); // 收费前余额
			originalBalance -= amt;// 正常医疗卡扣款
			parm.setData("GREEN_BALANCE", 0.00);// 绿色通道剩余金额=当前剩余金额-每一笔操作金额
			parm.setData("GREEN_BUSINESS_AMT", 0.00);// 绿色通道每一笔使用金额
			// if (insFlg) {
			// originalBalance += amt;// 医保退款操作
			// } else {
			// }
			parm.setData("CURRENT_BALANCE", originalBalance);
			parm.setData("CASHIER_CODE", cashierCode);
			parm.setData("BUSINESS_DATE", SystemTool.getInstance().getDate());
			// 1：交易执行完成
			// 2：双方确认完成

			parm.setData("BUSINESS_STATUS", "1");
			// 1：未对帐
			// 2：对账成功
			// 3：对账失败
			parm.setData("ACCNT_STATUS", "1");
			parm.setData("ACCNT_USER", new TNull(String.class));
			parm.setData("ACCNT_DATE", new TNull(Timestamp.class));
			parm.setData("OPT_USER", cashierCode);
			parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
			parm.setData("OPT_TERM", term);
			parm.setData("BUSINESS_TYPE", businessType);
			TParm result = EKTTool.getInstance().insertEKTDetail(parm,
					connection);
			if (result.getErrCode() != 0) {
				err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
						+ result.getErrText());
				connection.rollback();
				connection.close();
				return false;
			}
		}

		pv[0] = index;
		return true;
	}

	/**
	 * 医疗卡扣款 修改医疗卡金额
	 * 
	 * @param parm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @param control
	 *            TControl
	 * @return TParm ===============pangben 20111007
	 */
	private TParm createCard(TParm parm, TConnection connection) {
		TParm result = insetTrade(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.insetTrade " + result.getErrCode()
					+ result.getErrText());
			connection.rollback();
			connection.close();
			return result;
		}
		result = deleteEKTMaster(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.createCard " + result.getErrCode()
					+ result.getErrText());
			return result;
		}
		result = insertEKTMaster(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.createCard " + result.getErrCode()
					+ result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * 获得病患历次医保数据
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm queryCCBTrade(TParm parm) {
		TParm result = new TParm();
		String optUser = parm.getValue("OPT_USER") ;
		String startDate = parm.getValue("START_DATE").split(" ")[0];
		String endDate = parm.getValue("END_DATE").split(" ")[0];
    	String sql =
    	      "SELECT A.OPT_DATE, C.USER_NAME,"+
    		         "CASE WHEN STATE = '1' THEN '预约收费' ELSE '预约退费' END STATE,"+
    		         "A.BUSINESS_NO, A.MR_NO, A.CARD_NO, D.PAT_NAME,"+
    		         "A.AMT, 0 OTHER "+
    		    "FROM EKT_CCB_TRADE A, REG_PATADM B, SYS_OPERATOR C, SYS_PATINFO D "+
    		   "WHERE (A.BUSINESS_TYPE = 'REG' OR A.BUSINESS_TYPE='REGT') "+
    		     "AND A.BANK_FLG = 'Y' "+
    		     (optUser.trim().equals("")?"":"AND A.OPT_USER = '"+optUser+"' ")+
    		     "AND A.OPT_DATE BETWEEN TO_DATE ('" + startDate + " 00:00:00', 'YYYY/MM/DD hh24:mi:ss') "+
    	                            "AND TO_DATE ('" + endDate + " 23:59:59', 'YYYY/MM/DD hh24:mi:ss') "+
    		     "AND A.CASE_NO = B.CASE_NO "+
    		     "AND A.OPT_USER = C.USER_ID "+	 
    		     "AND A.MR_NO = D.MR_NO "+
    		     "AND B.APPT_CODE = 'Y' "+
    		     "AND ( (A.STATE = 2 AND A.RECEIPT_NO IS NOT NULL) "+
    		        "OR (A.STATE IN (1, 2) AND A.RECEIPT_NO IS NULL) ) "+
    		"ORDER BY A.OPT_USER, A.OPT_DATE";
        result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0)
			err(result.getErrCode() + " " + result.getErrText());
		return result;
	}
	/**
	 * 查询OPD_ORDER 表收费以后 BUSINESS_NO 为空的数据 ====pangben 2013-4-27
	 * @param parm
	 * @return
	 */
	public TParm queryOpdOrderError(TParm parm){
		TParm result = new TParm();
		result = this.query("queryOpdOrderError", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	/**
	 * 根据医疗卡的回传值，写医嘱表相应的字段
	 * 
	 * @param parm
	 *            TParm
	 * @param parm
	 *            orderParm 医疗卡需要收费的医嘱 用来添加 OPD_ORDER_HISTORY 表数据 控制医嘱修改
	 *            需要修改的医嘱是：西药中药 诊查诊疗医嘱
	 * @return boolean
	 * ===============pangben 2013-7-17 门诊医生站使用
	 */
	public TParm getBillSets(TParm parm,TParm ektSumExeParm) {
		//int type = parm.getInt("OP_TYPE");
		// 成功
		TParm result = new TParm();
		if (parm.getValue("OPD_UN_FLG").length()<=0 || parm.getValue("OPD_UN_FLG").equals("N")) {
			result.setData("EXEC_FLG", "N");
			result.setData("RECEIPT_FLG", "N");
			//result.setData("BILL_TYPE", "E");
			//result.setData("BILL_FLG", "Y");
			result.setData("BILL_USER", parm.getValue("OPT_USER"));
			result.setData("BILL_DATE", SystemTool.getInstance().getDate());
		}
		// 只退费，把回传的处方的收费注记置为N，
		else {
			result.setData("EXEC_FLG", "N");
			result.setData("RECEIPT_FLG", "N");
			//result.setData("BILL_TYPE", "C");
			//result.setData("BILL_FLG", "N");
			result.setData("BILL_USER", "");
			result.setData("BILL_DATE", new TNull(String.class));
		}
		result.setData("CASE_NO", parm.getValue("CASE_NO"));//就诊号===查询是否有问题
		// 表数据时使用用来管控是否操作绿色通道
		result.setData("REGION_CODE", Operator.getRegion());
		result.setData("OPT_DATE", SystemTool.getInstance().getDate());
		result.setData("OPT_USER", parm.getValue("OPT_USER"));
		result.setData("OPT_TERM",parm.getValue("OPT_TERM"));
		result.setData("MR_NO", parm.getValue("MR_NO"));//病案号 ===查询是否有问题
		result.setData("OPB_AMT", parm.getDouble("OPB_AMT"));
		result.setData("EKTNEW_AMT", parm.getDouble("EKTNEW_AMT"));// 医疗卡中金额
		result.setData("OLD_AMT", parm.getDouble("OLD_AMT"));// 医疗卡扣款之前金额
		result.setData("TRADE_NO", parm.getValue("TRADE_NO"));
		//result.setData("orderSumParm", parm.getParm("orderParm").getData());// 需要修改的医嘱
		result.setData("orderParm", ektSumExeParm.getParm("newParm").getData());// 需要修改的医嘱
		result.setData("hl7Parm",ektSumExeParm.getParm("hl7Parm").getData());//HL7发送接口集合
		return result;
	}
	
	/**
	 * 查询医疗卡日志表数据
	 * @param parm
	 * @return
	 */
	public TParm queryEktBilLog(TParm parm){
		TParm result=new TParm();
		result = this.query("queryEktBilLog", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	/**
	 * 插入医疗日志表数据
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertEktBilLog(TParm parm,TConnection connection){
		TParm result = update("insertEktBilLog", parm, connection);
		return result;
	}
	
	/**
	 * 插入医疗日志表数据
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm updateEktBilLog(TParm parm,TConnection connection){
		TParm result = update("updateEktBilLog", parm, connection);
		return result;
	}
	
	/**
	 * 查询医疗卡日志表数据
	 * @param parm
	 * @return
	 */
	public TParm queryCurrentBalance(TParm parm){
		TParm result=new TParm();
		result = this.query("queryCurrentBalance", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	/**
	 * 更新EKT_INVRCP表日结字段
	 * @param parm
	 * @return
	 */
	public TParm updateAccountSeq(TParm parm,TConnection connection){ //add by kangy 20160912
		TParm result=new TParm();
		result = this.update("updateAccountSeq", parm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	/**
	 * 向BIL_ACCOUNT表插医疗卡入日结信息
	 * @param parm
	 * @return
	 */
	public TParm insertBilAccount(TParm parm,TConnection connection){// add by kangy 201609012
		TParm result=new TParm();
		TParm invParm=new TParm();
		parm.setData("AR_AMT",ar_amt);
		parm.setData("PRINT_DATE",parm.getData("BUSINESS_DATE"));
		//获得作废票据张数
		invParm=this.query("getEktResetCount",parm,connection);
		parm.setData("INVALID_COUNT",invParm.getValue("COUNT_NO",0));
		result = this.update("insertBilAccount", parm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	/**
	 * 判断新旧设备
	 * @String String
	 * @return
	 */
	// kangy 脱卡还原     
/*	public boolean Equipment(String ip){
		EKTJudge ektJudge=new EKTJudge();
		boolean facilityType=ektJudge.Facility();
		String sql="SELECT * FROM EKT_DEV WHERE IP='"+ip+"'";
		//String sql="SELECT * FROM SYS_EQUITMENT_TYPE WHERE IP='"+ip+"'";
    	TParm type=new TParm(TJDODBTool.getInstance().select(sql));
	    	if(type.getCount()>0){
	    		if(facilityType)
	    		return false;
	    		else 
	    			return true;
	    	}else{
	    		if(facilityType)
		    		return true;
		    		else 
		    		   return false;
	    	}
	}*/
}
