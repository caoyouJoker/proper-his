package action.ekt;

//import java.sql.Timestamp;
//import java.util.Map;

import java.text.SimpleDateFormat;

import com.dongyang.action.TAction;
//import com.dongyang.data.TNull;
import com.dongyang.data.TParm;

import jdo.bil.BILInvoiceTool;
import jdo.ekt.EKTNewIO;
//import jdo.ekt.EKTNewTool;
import jdo.ekt.EKTTool;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
//import com.dongyang.jdo.TJDODBTool;
//import com.dongyang.manager.TIOM_AppServer;

import jdo.ekt.EKTIO;
//import jdo.bil.BILGreenPathTool;
import jdo.ekt.EKTGreenPathTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJFlow;
import jdo.opb.OPBTool;
//import jdo.opd.OrderTool;
import jdo.reg.PatAdmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import junit.textui.ResultPrinter;
//import jdo.reg.REGTool;
//import jdo.sys.Operator;
//import jdo.sys.PatTool;
//import jdo.sys.SystemTool;

import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 医疗卡操作
 * </p>
 * 
 * <p>
 * Description:医疗卡操作
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
 * @author pangben 20111007
 * @version 2.0
 */
public class EKTAction extends TAction {
	public EKTAction() {
	}
	 SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
	/**
	 * 医疗卡补卡写卡
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm TXEKTRenewCard(TParm parm) {
		TConnection connection = getConnection();
		// 修改医疗卡表数据:将以前的数据设置失败状态
		TParm result = EKTTool.getInstance()
				.updateEKTIssuelog(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// 添加医疗卡表数据
		result = EKTTool.getInstance().insertEKTIssuelog(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// 医疗卡主档表
		result = EKTIO.getInstance().createCard(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// 医疗卡明细档插入数据
		String businessNo = EKTTool.getInstance().getBusinessNo();
		TParm businessParm = parm.getParm("businessParm");
		businessParm.setData("BUSINESS_NO", businessNo);
		businessParm.setData("GREEN_BUSINESS_AMT", 0);
		businessParm.setData("GREEN_BALANCE", 0);
		businessParm.setData("BUSINESS_TYPE", "");
		result = EKTTool.getInstance()
				.insertEKTDetail(businessParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		
		//add by huangtt 20160921 插入医疗卡历史 表中数据
		TParm historyParm = EKTNewIO.getInstance().getEktMasterHistoryParm(businessParm);
		String sql = EKTNewIO.getInstance().getEktMasterHistorySql(historyParm,"");
		result = new TParm(TJDODBTool.getInstance().update(sql,connection));
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		
		// zhangp 20111222 医疗卡充值退款档插入数据
		TParm bilParm = parm.getParm("bilParm");
		// =======zhangp 20120227 modify start
		// String billBusinessNo = EKTTool.getInstance().getBillBusinessNo();
		bilParm.setData("BIL_BUSINESS_NO", businessNo);
		bilParm.setData("PRINT_NO", "");
		// bilParm.setData("BIL_BUSINESS_NO", billBusinessNo);
		// =======zhangp 20120227 modify end
		// System.out.println("写卡bilParm==="+bilParm);
		result = EKTTool.getInstance().insertEKTBilPay(bilParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 医疗卡充值方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ===============pangben 20111007
	 */
	public TParm TXEKTonFee(TParm parm) {
		//System.out.println("actiompamrm-----"+parm);
		TConnection connection = getConnection();
		//===================================add by huangjw 20150710
		double initAmt=0.00;//期初金额
		double lastAmt=0.00;//期末金额
		TParm initParm=new TParm();
		initParm.setData("MR_NO",parm.getValue("MR_NO"));
		initParm.setData("WRITE_FLG","Y");
		initAmt=EKTTool.getInstance().queryCurrentBalance(initParm).getDouble("CURRENT_BALANCE",0);
		//===================================add by huangjw 20150710
		// 更新余额
		TParm result = null;
		//======20130509 yanjing modify将医疗卡EKT-MASER表写到一个事务里
//		 if (parm.getBoolean("FLG")) {
//			 // 新建医疗卡信息
		 result = EKTIO.getInstance().createCard(parm, connection);
//		 } else {
		 // 更新医疗卡信息
//		 result = EKTTool.getInstance().updateEKTMaster(parm, connection);
//		 }
		 if (result.getErrCode() < 0) {
			 connection.close();
			 return result;
		 }
		// 医疗卡明细档插入数据
		String businessNo = EKTTool.getInstance().getBusinessNo();
		TParm businessParm = parm.getParm("businessParm");
		businessParm.setData("BUSINESS_NO", businessNo);
		businessParm.setData("GREEN_BALANCE", 0.00);
		businessParm.setData("GREEN_BUSINESS_AMT", 0.00);
		businessParm.setData("BUSINESS_TYPE", "");
		result = EKTTool.getInstance()
				.insertEKTDetail(businessParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		
		//add by huangtt 20160921 插入医疗卡历史 表中数据
		TParm historyParm = EKTNewIO.getInstance().getEktMasterHistoryParm(businessParm);
		String sql = EKTNewIO.getInstance().getEktMasterHistorySql(historyParm,"");
		result = new TParm(TJDODBTool.getInstance().update(sql,connection));
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		
		
		TParm billParm = parm.getParm("billParm");
		// 医疗卡充值号
		// ===zhangp 20120314 start
		// String billBusinessNo = EKTTool.getInstance().getBillBusinessNo();
		// billParm.setData("BIL_BUSINESS_NO", billBusinessNo);
		billParm.setData("BIL_BUSINESS_NO", businessNo);
		billParm.setData("PRINT_NO", parm.getValue("PRINT_NO"));
		// 医疗卡充值操作KET_BIL_PAY
		result = EKTTool.getInstance().insertEKTBilPay(billParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		
		//connection.close();
		
		//============================================医疗卡日志数据add by huangjw 20150710 start
		TParm lastParm=new TParm();
		lastParm.setData("MR_NO",parm.getValue("MR_NO"));
		lastParm.setData("WRITE_FLG","Y");
		lastAmt=EKTTool.getInstance().queryCurrentBalance(lastParm).getDouble("CURRENT_BALANCE",0);
		//===================================add by huangjw 20150710
		TParm inParm=new TParm();
		String date=TJDODBTool.getInstance().getDBTime().toString().substring(0,10).replaceAll("-", "/");
		inParm.setData("MR_NO",parm.getValue("MR_NO"));
		inParm.setData("EKT_DATE",date);
		TParm queryParm=EKTTool.getInstance().queryEktBilLog(inParm);
		TParm logParm=new TParm();
		if(queryParm.getCount()>0){//更新数据
			logParm.setData("MR_NO",parm.getValue("MR_NO"));
			logParm.setData("EKT_DATE",date);
			//logParm.setData("INIT_AMT",parm.getDouble("CURRENT_BALANCE"));//期初金额
			logParm.setData("LAST_AMT",lastAmt);//期末金额
			logParm.setData("OPT_USER",parm.getValue("OPT_USER"));
			logParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
			result=EKTTool.getInstance().updateEktBilLog(logParm, connection); 
			
		}else{//插入数据
			logParm.setData("MR_NO",parm.getValue("MR_NO"));
			logParm.setData("EKT_DATE",date);
			logParm.setData("INIT_AMT",initAmt);//期初金额
			logParm.setData("LAST_AMT",lastAmt);//期末金额
			logParm.setData("OPT_USER",parm.getValue("OPT_USER"));
			logParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
			result=EKTTool.getInstance().insertEktBilLog(logParm, connection);
		}
		if (result.getErrCode() != 0) {
			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		//============================================医疗卡日志数据add by huangjw 20150710 end
		
		//==============================医疗卡充值、退费数据add by kangy 20160804 start
		result=EKTNewIO.getInstance().EKTFee(parm, connection, businessNo);
		if (result.getErrCode() != 0) {
			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		//=============================医疗卡充值退费数据add by kangy 20160804 end

		
		connection.commit();
		connection.close();

		// result.setData("BIL_BUSINESS_NO", billBusinessNo);// 充值收据号
		result.setData("BIL_BUSINESS_NO", businessNo);// 充值收据号
		// ===zhangp 20120314 end
		return result;
	}

	/**
	 * 插入新数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertData(TParm parm) {
		TConnection conn = this.getConnection();
		TParm result = EKTGreenPathTool.getInstance().insertdata(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		// 更新挂号主档表中绿色通道金额
		result = updateEKTGreen(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 更新挂号主档表中绿色通道金额
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	private TParm updateEKTGreen(TParm parm, TConnection conn) {
		// 更新挂号主档表中绿色通道金额
		TParm result = PatAdmTool.getInstance().updateEKTGreen(
				ektParmTemp(parm), conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	private TParm ektParmTemp(TParm parm) {
		TParm ektParm = new TParm();
		ektParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		// 获得此就诊病患绿色通道金额
		TParm check = PatAdmTool.getInstance().selEKTByMrNo(ektParm);
		double GREEN_BALANCE = check.getDouble("GREEN_BALANCE", 0);
		double GREEN_PATH_TOTAL = check.getDouble("GREEN_PATH_TOTAL", 0);
		ektParm.setData("GREEN_BALANCE", StringTool.round(parm
				.getDouble("APPROVE_AMT"), 2)
				+ GREEN_BALANCE);
		ektParm.setData("GREEN_PATH_TOTAL", StringTool.round(parm
				.getDouble("APPROVE_AMT"), 2)
				+ GREEN_PATH_TOTAL);
		return ektParm;
	}

	/**
	 * 作废一条绿色通道信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm cancelGreenPath(TParm parm) {
		TConnection conn = this.getConnection();
		TParm result = EKTGreenPathTool.getInstance().cancleGreenPath(parm,
				conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		parm.setData("APPROVE_AMT", -parm.getDouble("APPROVE_AMT"));
		// 更新挂号主档表中绿色通道金额
		result = updateEKTGreen(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 删除挂号失败撤销操作
	 * 
	 * @param parm
	 * @return
	 */
	public TParm deleteRegOldData(TParm parm) {
		TConnection conn = this.getConnection();
		TParm result = EKTTool.getInstance().deleteTrade(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		result = EKTTool.getInstance().deleteDetail(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 医疗卡日结操作 ==============zhangp
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onEKTAccount(TParm parm) {
		TConnection connection = this.getConnection();
		TParm result = new TParm();
		result = EKTTool.getInstance().executeEKTAccount(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.close();
			return result;
		}
		//更新BIL_INVRCP表日结字段
		result=EKTTool.getInstance().updateAccountSeq(parm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.close();
			return result;
		}
		//向BIL_ACCOUNT表插入日结信息
		result=EKTTool.getInstance().insertBilAccount(parm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 执行医保保存操作 医疗卡交易表 EKT_TREDE 和 EKT_ACCNTDETAIL 添加医保操作数据
	 * 
	 * @param parm
	 *            AMT:本次操作金额 BUSINESS_TYPE :本次操作类型 CASE_NO:就诊号码
	 * @param type
	 *            ：9, 医保扣款 0,医保回冲
	 * @return
	 */
	public TParm exeInsSave(TParm parm) {
		// 禁止在服务器端调用
		TParm result = new TParm();
		TParm p = null;
		TConnection connection = getConnection();
		/**
		 * 门诊收费/退费使用
		 */
		if (null != parm.getValue("EXE_FLG")
				&& parm.getValue("EXE_FLG").equals("Y")) {
			parm.setData("ID_NO",parm.getValue("ID_NO"));
			// parm.setData("CASE_NO", caseNo);
			parm.setData("NAME", parm.getValue("PAT_NAME"));
			parm.setData("CREAT_USER", parm.getValue("OPT_USER"));
			parm.setData("CURRENT_BALANCE", parm
					.getDouble("CURRENT_BALANCE")
					- parm.getDouble("AMT"));
			result = EKTTool.getInstance().deleteEKTMaster(parm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:EKTIO.createCard " + result.getErrCode()
						+ result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
			result = EKTTool.getInstance().insertEKTMaster(parm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:EKTIO.createCard " + result.getErrCode()
						+ result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
			//医保医疗卡退票操作
			p =EKTNewIO.getInstance().unInsOpbReceiptNo(parm, connection);
			if (p.getErrCode() != 0) {
				err("ERR:EKTIO.createCard " + p.getErrCode()
						+ p.getErrText());
				connection.rollback();
				connection.close();
				return p;
			}
			//医保退费操作 将修改OPD_ORDER 表中内部交易号码
//			parm.setData("BUSINESS_NO",p.getValue("TRADE_NO"));
//			result=EKTNewTool.getInstance().updateOpdOrderBusinessNo(parm, connection);
//			if (result.getErrCode() != 0) {
//				err("ERR:EKTIO.createCard " + result.getErrCode()
//						+ result.getErrText());
//				connection.rollback();
//				connection.close();
//				return result;
//			}
		}
		// 门诊收费医疗卡金额不足,医保分割金额 特殊INSFEEPrintControl类操作
		if (null != parm.getValue("INS_EXE_FLG")
				&& parm.getValue("INS_EXE_FLG").equals("Y")) {
			TParm orderParm = parm.getParm("orderParm");
			TParm readCard = parm.getParm("readCard");// 泰心医疗卡读卡操作
			// TParm cp=new TParm();
			// 查询此就诊病患所有数据汇总金额
			TParm cp = new TParm();
			cp.setData("EKT_USE", - parm.getDouble("INS_AMT"));// 医疗卡扣款金额=没有打票的总金额-医保金额
			cp.setData("EKT_OLD_AMT", readCard.getDouble("CURRENT_BALANCE"));// 医疗卡中在操作之前的金额
			// （将此次动到的处方签的所有金额
			// 回冲
			// 获得当前
			// 医疗卡的金额）
			cp.setData("GREEN_BALANCE", 0.00);// 特批款扣款金额
			cp.setData("CARD_NO", readCard.getValue("PK_CARD_NO"));// 卡号主键
			cp.setData("MR_NO", readCard.getValue("MR_NO"));// 病案号
			cp.setData("CASE_NO", parm.getValue("CASE_NO"));// 就诊号
			cp.setData("PAT_NAME", readCard.getValue("PAT_NAME"));// 病患名称
			// cp.setData("OLD_AMT", readCard.getDouble("CURRENT_BALANCE"));//
			// 医疗卡原有金额
			cp.setData("BUSINESS_TYPE", "OPBT");// 类型
			cp.setData("GREEN_PATH_TOTAL", 0);// 特批款审批金额
			cp.setData("GREEN_USE", 0);// 特批款此次扣款金额
			cp.setData("OPT_USER", parm.getValue("OPT_USER"));// 操作人
			cp.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
			cp.setData("IDNO", readCard.getValue("IDNO"));// 身份证号
			cp.setData("billAmt", parm.getDouble("billAmt"));//未收费金额
			//cp.setData("TRADE_SUM_NO",orderParm.getValue("TRADE_SUM_NO"));////UPDATE EKT_TRADE 冲负数据,医疗卡扣款内部交易号码,格式'xxx','xxx'
			// // CURRENT_BALANCE:医疗卡现在金额
			double ektAmt = readCard.getDouble("CURRENT_BALANCE")
					+ parm.getDouble("INS_AMT") - parm.getDouble("billAmt");
			cp.setData("EKT_AMT", ektAmt);// 医疗卡当前金额
			
			//TParm billParm = orderParm.getParm("parmBill");//未收费医嘱集合
			
			cp.setData("CONFIRM_NO", parm.getData("CONFIRM_NO")); //医保须序号 add by huangtt 20160920
			// 泰心医院扣款操作
			p = new TParm(EKTNewIO.getInstance().onNewSaveInsFee(cp.getData()));
			if (p.getErrCode() < 0) {
				connection.close();
				return p;
			}
			//TParm newParm = orderParm.getParm("parmSum");// 操作医嘱
			orderParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));//医保顺序号 add by huangtt 20160920
			orderParm.setData("HISTORY_NO",p.getValue("HISTORY_NO"));  //add by huangtt 20160920医疗卡历史表上的ID号
			orderParm.setData("TRADE_NO",p.getValue("TRADE_NO"));
			orderParm.setData("OPT_USER", parm.getValue("OPT_USER"));// 操作人
			orderParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
			orderParm.setData("CASE_NO", parm.getValue("CASE_NO"));// 就诊号
			//orderParm.setData("billParm", orderParm.getParm("parmBill").getData());//未收费医嘱集合
			result = OPBTool.getInstance().onHl7ExeBillFlg(orderParm, connection);
			if (result.getErrCode() < 0) {
				// result = ektCancel(parm);
				if (result.getErrCode() < 0) {
					System.out.println("医疗卡回滚信息操作失败");
				}
				connection.close();
				return result;
			}
			parm.setData("EXE_USER", parm.getValue("OPT_USER"));
			parm.setData("EXE_TERM", parm.getValue("OPT_TERM"));
			parm.setData("EXE_TYPE", parm.getValue("RECP_TYPE"));//=====pangben 2013-3-13 修改从前台录入
			result = INSRunTool.getInstance().deleteInsRun(parm, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				connection.close();
				return result;
			}
			result = INSTJFlow.getInstance().updateInsAmtFlgPrint(parm,
					parm.getValue("RECP_TYPE"), connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return p;
	}
	/**
	 * 医疗卡充值退费补印方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ===============kangy 20160805
	 */
	public TParm TXEKTReprint(TParm parm) {
		//System.out.println("sssss"+parm);
		TParm result = new TParm(); 
		TParm insertParm=new TParm();
		TParm updataParm=new TParm();
		TParm updateinvoiceParm=new TParm();
		TParm updateektbilpayparm=new TParm();
		TConnection connection = getConnection();
	
		
		//String updateno = StringTool.addString(parm.getValue("INV_NO"));
		
		insertParm=parm.getParm("insertparm");
		result=BILInvoiceTool.getInstance().insertFeeDate(insertParm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
		
		updataParm=parm.getParm("updateparm");
		//System.out.println("update===="+updataParm);
		result=BILInvoiceTool.getInstance().cancelDate(updataParm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
		
		updateinvoiceParm=parm.getParm("updateinvoiceparm");
		result=BILInvoiceTool.getInstance().updateDatePrint(updateinvoiceParm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
 	    
		updateektbilpayparm=parm.getParm("updateektbilpayparm");
		result=BILInvoiceTool.getInstance().updateprintNo(updateektbilpayparm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
}
