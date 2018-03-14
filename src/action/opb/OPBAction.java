package action.opb;

import java.util.List;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;

import jdo.opd.OrderList;
import jdo.opd.OrderTool;
import com.dongyang.db.TConnection;
import com.dongyang.manager.TIOM_AppServer;
import jdo.opb.OPBTool;
import jdo.ekt.EKTTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * 
 * <p>
 * Title: 门诊收费动作类
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author lzk 2008.9.26
 * @version 1.0
 */
public class OPBAction extends TAction {
	/**
	 * 查询
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQuery(TParm parm) {
		TParm result = new TParm();
		// 得到问诊号
		String caseNo = parm.getValue("CASE_NO");
		if (caseNo.length() == 0)
			return err(-1, "CASE_NO is null");
		// 拿到病患order
		TParm orderresult = OrderTool.getInstance().query(caseNo);
		// System.out.println("orderresult"+orderresult);
		if (orderresult.getErrCode() != 0) {
			return err(orderresult);
		}
		// 返回order
		result.setData("ORDER", orderresult.getData());
		return result;
	}

	/**
	 * 门急诊保存入口
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSave(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().opbCharge(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 医疗卡门急诊保存入口
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ============pangben 20111025
	 */
	public TParm onSaveEKT(TParm parm) {
		//===================================add by huangjw 20150710
//		double initAmt=0.00;//期初金额
//		double lastAmt=0.00;//期末金额
//		TParm initParm=new TParm();
//		initParm.setData("MR_NO",parm.getValue("MR_NO"));
//		initParm.setData("WRITE_FLG","Y");
//		initAmt=EKTTool.getInstance().queryCurrentBalance(initParm).getDouble("CURRENT_BALANCE",0);
		//===================================add by huangjw 20150710
		System.out.println("action-------------");
		// 执行添加opd_order 表数据 收费状态 现在状态receipt_no is null and bill_flg ='Y'
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().opbCharge(parm, connection);
		System.out.println("result-----opbCharge--------"+result);
		// result.setErr(-1,"错误");
		TParm result1 = null;
		if (result.getErrCode() < 0) {
//			result1 = ektCancel(parm);
//			if (result1.getErrCode() < 0) {
//				System.out.println("医疗卡回滚信息操作失败");
//			}
			connection.close();
			return result;
		}
		TParm orderParm =parm.getParm("newParm");//操作医嘱 
		result = OPBTool.getInstance().updateOpdOrderEkt(parm, orderParm, connection);
		System.out.println("result-----opbCharge--------"+result);
		if (result.getErrCode() < 0) {
//			result = ektCancel(parm);
//			if (result.getErrCode() < 0) {
//				System.out.println("医疗卡回滚信息操作失败");
//			}
			connection.close();
			return result;
		}
		
		//写医疗卡数据  add by huangtt20160918 
		String [] sql = (String[]) parm.getData("EKT_SQL");
		if(sql != null ){
			for(String tempSql:sql){
				if(tempSql.length() > 0){
					result=new TParm(TJDODBTool.getInstance().update(tempSql, connection));
					if(result.getErrCode()!=0){
						System.out.println("ODOSaveTool wrong sql:"+tempSql);
						connection.close();
						return result;
					}
				}
				
			}
		}
		
//		connection.commit();
		//============================================医疗卡日志数据add by huangjw 20150710 start
//		TParm lastParm=new TParm();
//		lastParm.setData("MR_NO",parm.getValue("MR_NO"));
//		lastParm.setData("WRITE_FLG","Y");
//		lastAmt=EKTTool.getInstance().queryCurrentBalance(lastParm).getDouble("CURRENT_BALANCE",0);
//		
//		TParm inParm=new TParm();
//		String date=TJDODBTool.getInstance().getDBTime().toString().substring(0,10).replaceAll("-", "/");
//		inParm.setData("MR_NO",parm.getValue("MR_NO"));
//		inParm.setData("EKT_DATE",date);
//		TParm queryParm=EKTTool.getInstance().queryEktBilLog(inParm);
//		TParm logParm=new TParm();
//		if(queryParm.getCount()>0){//更新数据
//			logParm.setData("MR_NO",parm.getValue("MR_NO"));
//			logParm.setData("EKT_DATE",date);
//			//logParm.setData("INIT_AMT",parm.getDouble("CURRENT_BALANCE"));//期初金额
//			logParm.setData("LAST_AMT",lastAmt);//期末金额
//			logParm.setData("OPT_USER",parm.getValue("OPT_USER"));
//			logParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
//			result=EKTTool.getInstance().updateEktBilLog(logParm, connection); 
//			
//		}else{//插入数据
//			logParm.setData("MR_NO",parm.getValue("MR_NO"));
//			logParm.setData("EKT_DATE",date);
//			logParm.setData("INIT_AMT",initAmt);//期初金额
//			logParm.setData("LAST_AMT",lastAmt);//期末金额
//			logParm.setData("OPT_USER",parm.getValue("OPT_USER"));
//			logParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
//			result=EKTTool.getInstance().insertEktBilLog(logParm, connection);
//		}
//		if (result.getErrCode() != 0) {
//			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
//					+ result.getErrText());
//			connection.close();
//			return result;
//			//return false;
//		}
		//============================================医疗卡日志数据add by huangjw 20150710 end
		connection.commit();
		connection.close();
		return result;
	}
	

	/**
	 * 医疗卡执行收费出现错误回滚信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm ektCancel(TParm parm) {
		// System.out.println("ektCancel 方法 parm:::" + parm);
		TParm result = null;
		TParm orderParm = parm.getParm("orderParm");
		TConnection connection = getConnection();
		// 扣款操作
		if ("Y".equals(parm.getValue("FLG"))) {
			result = ektTempCancel(parm, connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		} else {
			result = ektTempCancel(parm, connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
			// 退款操作
			if (null != parm.getValue("CANCLE_TREDE")
					&& parm.getValue("CANCLE_TREDE").length() > 0) {
				result = EKTTool.getInstance().consumeCancelOne(parm,
						parm.getValue("CANCLE_TREDE"), 0);
				if (result.getErrCode() < 0) {
					connection.close();
					return result;
				}
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 医疗卡操作失败公共部分
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	private TParm ektTempCancel(TParm parm, TConnection connection) {
		TParm result = null;
		result = EKTTool.getInstance().deleteTrade(parm, connection);
		if (result.getErrCode() < 0) {
			// connection.close();
			return result;
		}
		return result;
	}

	/**
	 * 保存退费
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm backReceipt(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().backReceipt(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 门诊收费补印
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm saveOPBRePrint(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().saveOPBRePrint(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 门诊收费补印
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm saveOPBEKTRePrint(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance()
				.saveOPBEKTRePrint(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 医疗卡打票
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onOPBEktprint(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().onOPBEktprint(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 现金打票
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onOPBCashprint(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().onOPBCashprint(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 现金退费作废票据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm backOPBRecp(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().backOPBRecp(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 医疗卡退费作废票据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm backEKTOPBRecp(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().backEKTOPBRecp(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 医疗卡退费作废票据:记账操作，没有执行结算的票据，不执行修改BIL_INVRCP表
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ==================pangben modify 20110822
	 */
	public TParm backOPBRecpStatus(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance()
				.backOPBRecpStatus(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;

	}

	/**
	 * 门诊医生站医疗卡收费操作修改医嘱数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm =============pangben 201109015
	 */
	public TParm updateBillSets(TParm parm) {
		TParm result = new TParm();

		TParm orderParm = parm.getParm("orderParm"); // 需要操作的医嘱
		TConnection connection = getConnection();
		result = OPBTool.getInstance().updateOpdOrderEkt(parm, orderParm, connection);
		if (result.getErrCode() < 0) {
			result = ektCancel(parm);
			if (result.getErrCode() < 0) {
				System.out.println("医疗卡回滚信息操作失败");
			}
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;

	}

	/*
	 * @param args String[]
	 */
	public static void main(String args[]) {
		com.javahis.util.JavaHisDebug.initClient();
		TIOM_AppServer.resetAction();

		//
	}

	/**
	 * 删除医嘱(门诊收费界面) ====pangben 20120414
	 * 
	 * @param order
	 * @return
	 */
	public TParm deleteOPBCharge(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OrderTool.getInstance()
				.deleteOPBCharge(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 删除医嘱(门诊收费界面) ====pangben 20120414
	 * 
	 * @param order
	 * @return
	 */
	public TParm deleteOPBChargeSet(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OrderTool.getInstance().deleteOPBChargeSet(parm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	
	/**
	 * 门诊收费补印 (Q医)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm saveQeAppOPBRePrint(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().saveQeAppOPBRePrint(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

}
