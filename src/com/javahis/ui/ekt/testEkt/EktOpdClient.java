package com.javahis.ui.ekt.testEkt;

import java.math.BigDecimal;

import jdo.ekt.EKTGreenPathTool;
import jdo.ekt.EKTNewIO;
import jdo.ekt.EKTNewTool;
import jdo.reg.PatAdmTool;
import jdo.sys.Operator;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;



public class EktOpdClient extends EktClient{

	public  EktParam openClient(EktParam ektParam){
		
		isNull = ektParam.isNull();
		
		ektParam = onAccntClient(ektParam);
		if(ektParam.getOpType().length() > 0){
			return ektParam;
		}

		if (EKTDialogSwitch()) {
			if (!amt.equals(BigDecimal.ZERO) || !isNull) {
				TParm r = null;
				p.setData("RE_PARM",parm.getData());//医生站医嘱参数，将UPDATE OPD_ORDER BILL='Y',BUSINESS_NO=XXX 写到一个事物
				p.setData("OPB_AMT", opbAmt.doubleValue());// 门诊医生站操作金额=====pangben 2013-7-17
				p.setData("ODO_EKT_FLG", "Y");// 门诊医生站操作,调用EKTChageUI.x界面区分医生站和门诊收费界面公用逻辑=====pangben 2013-7-17
							
				
//				if (amt > (oldAmt + greenParm.getDouble("GREEN_BALANCE", 0))) {// 医疗卡中金额小于此次收费的金额
				if(amt.compareTo(oldAmtGerrenBalance) > 0){	
					if (null != unFlg && unFlg.equals("Y")) {// 医生修改医嘱操作
						parm.setData("OPD_UN_FLG", "Y");
						p.setData("GREEN_BALANCE", greenParm.getDouble(
								"GREEN_BALANCE", 0));// 绿色通道剩余金额
						p.setData("GREEN_PATH_TOTAL", greenParm.getDouble(
								"GREEN_PATH_TOTAL", 0));// 绿色通道审批金额
						p.setData("unParm", parm.getParm("unParm").getData());
						p.setData("EKT_TYPE_FLG",3);//修改医嘱金额不足退回处方签中的金额
						p.setData("OPD_UN_FLG","Y");//操作修改OPD_ORDER表 ===pangben 2013-7-17
						r = (TParm) control.openDialog(
								"%ROOT%\\config\\opd\\OPDOrderPreviewReAmt.x", p);
						
						ektParam.getOrderParm().setData("unParm", r);

						
					} else {//不是修改医嘱，但是此次金额超过医疗卡金额 
						p.setData("EKT_TYPE_FLG",2);
						r = (TParm) control.openDialog(
								"%ROOT%\\config\\opd\\OPDOrderPreviewAmt.x", p);
					}
					
				} else if(null!=parm.getValue("OPBEKTFEE_FLG")&&
						parm.getValue("OPBEKTFEE_FLG").equals("Y")&& 
						null != unFlg && unFlg.equals("Y")) {// 医疗卡金额充足:医生修改医嘱操作//医疗卡金额足够退回医疗卡金额操作
					//不显示扣款界面，直接操作=====pangben 2013-4-27 
					r = exeRefund(p);
				}else {

					r = (TParm) control.openDialog(
							"%ROOT%\\config\\ekt\\EKTChageUI.x", p);
					
				}
				if (r == null) {
					// System.out.println("asdadasd");
					ektParam.setOpType("3");
					return ektParam;
				}
				if (r.getErrCode() < 0) {
					control.messageBox(r.getErrText());
					ektParam.setOpType("3");
					return ektParam;
				}
			
				type = r.getInt("OP_TYPE");
				// 余额不足
				if (type == 2) {
					ektParam.setOpType("3");
					return ektParam;
				}
				
				
//				if (null == unFlg
//						|| unFlg.equals("N")
//						|| amt <= (oldAmt + greenParm.getDouble(
//								"GREEN_BALANCE", 0))) {
				if( null == unFlg
						|| unFlg.equals("N")
						||  amt.compareTo(oldAmtGerrenBalance) <=0){
					
					// cardNo = r.getValue("CARD_NO");
					// =========pangben 20111024 start
					ektAMT =new BigDecimal(r.getDouble("EKTNEW_AMT")) ;// 现在医疗卡中的金额
					ektOldAMT = new BigDecimal(r.getDouble("OLD_AMT"));
					if (null != r.getValue("AMT")
							&& r.getValue("AMT").length() > 0) {
						result.setData("AMT", r.getValue("AMT")); // 收费金额
						result.setData("EKT_USE", r.getDouble("EKT_USE")); // 扣医疗卡金额
						// greenUseAmt=r.getDouble("GREEN_USE");//绿色通道使用金额
						// ektUseAmt= r.getDouble("EKT_USE");//医疗卡使用金额
						result.setData("GREEN_USE", r.getDouble("GREEN_USE")); // 扣绿色通道金额
					}
					greenBalance = new BigDecimal(r.getDouble("GREEN_BALANCE")); // 绿色通道未扣款金额
					greenPathTotal =new BigDecimal( r.getDouble("GREEN_PATH_TOTAL")); // 绿色通道总金额
					// =========pangben 20111024 stop
					
					tradeNo = r.getValue("TRADE_NO");
					historyNo = r.getValue("HISTORY_NO");
					cancelTrede = r.getValue("CANCLE_TREDE");
					greenFlg = r.getValue("GREEN_FLG");
				}
				
				ektParam.setSqls((String[]) r.getData("SQL"));

			} 

		}

		
		setResultValue();
		
		ektParam.getOrderParm().setData("result", result.getData());
		System.out.println(ektParam.getOrderParm().getParm("result"));
		return ektParam;
		
	}
	
	
	/**
	 * 执行退款操作 门急诊医生站 修改医嘱操作，退回医疗卡金额
	 */
	private TParm exeRefund(TParm sumParm){
		TParm parm = new TParm();
		TParm readCard = sumParm.getParm("READ_CARD");
		TParm result = new TParm();
		if (readCard.getErrCode() < 0) {
			// TParm parm = new TParm();
			parm.setErr(-1, "此医疗卡无效");
			return parm;
		}
		//String cardNo = readCard.getValue("CARD_NO");
		String caseNo = sumParm.getValue("CASE_NO");//就诊号
		String tradeSumNo=sumParm.getValue("TRADE_SUM_NO");//此次操作已经收费的内部交易号码,格式'xxx','xxx'
		parm.setData("CASE_NO", caseNo);
		double amt = sumParm.getDouble("AMT");//显示的金额
		String insFlg = sumParm.getValue("INS_FLG");// 医保卡注记
		double exeAmt = sumParm.getDouble("EXE_AMT");//医生站此次操作金额
		TParm reParm=sumParm.getParm("RE_PARM");//==pangben 2013-7-17 界面数据，将修改OPD_ORDER收费状态添加到一个事物里
		// 查询此次就诊病患是否存在医疗卡绿色通道
		TParm patEktParm = EKTGreenPathTool.getInstance().selPatEktGreen(parm);
		double oldAmt = readCard.getDouble("CURRENT_BALANCE");
		double sumAmt = oldAmt - amt;
		double tempAmt = oldAmt;
		if (patEktParm.getInt("COUNT", 0) > 0) {
			// 查询绿色通道扣款金额、总充值金额
			result = PatAdmTool.getInstance().selEKTByMrNo(parm);
			sumAmt += result.getDouble("GREEN_BALANCE", 0);// 扣款之后的金额
			tempAmt += result.getDouble("GREEN_BALANCE", 0);// 未扣款金额
		}
		parm.setData("CARD_NO", readCard.getValue("PK_CARD_NO"));
		if (sumAmt < 0) {
			parm.setData("OP_TYPE", 2);
		} else {
			TParm cp = new TParm();
			cp.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
			cp.setData("CARD_NO", readCard.getValue("PK_CARD_NO"));
			cp.setData("CASE_NO", caseNo);
			cp.setData("MR_NO",  sumParm.getValue("MR_NO"));
			cp.setData("PAT_NAME", readCard.getValue("PAT_NAME"));
			cp.setData("IDNO", readCard.getValue("IDNO"));
			// cp.setData("BUSINESS_NO",businessNo);
			String ektTradeType = sumParm.getValue("EKT_TRADE_TYPE");// 查询条件
			String businessType = sumParm.getValue("BUSINESS_TYPE");// 保存数据参数
			if (businessType == null || businessType.length() == 0)
				businessType = "none";
			cp.setData("BUSINESS_TYPE", businessType);
			cp.setData("EKT_TRADE_TYPE", ektTradeType);//挂号使用 REG_PATADM 表没有TRADE_NO 字段 
			cp.setData("OLD_AMT", oldAmt);// 医疗卡金额
			cp.setData("NEW_AMT",sumAmt);
			cp.setData("INS_FLG", insFlg);// 医保卡注记
			// zhangp 20120106 加入seq
			cp.setData("SEQ", readCard.getValue("SEQ"));// 序号
			//cp.setData("SUMOPDORDER_AMT", sumOpdorderAmt);
			// 扣款
			if (amt >= 0) {
				onFee(cp, result, oldAmt, amt, sumAmt);
			} else {
				// 退费
				onUnFee(cp, result, oldAmt, amt, sumAmt);
			}
			//parm.setData("TRADE_NO",tradeSumNo);
			//需要操作的交易金额
			//TParm ektTradeSumParm=EKTNewTool.getInstance().selectEktTradeUnSum(parm);
			// 医疗卡金额大于此次扣款金额
			if (null != result && result.getCount() > 0) {
				onExeGreenFee(oldAmt, amt, businessType, cp, exeAmt, caseNo, result);
			}else{
				cp.setData("EKT_USE", exeAmt);// 医疗卡扣款金额
				cp.setData("GREEN_USE",0.00);//绿色通道扣款金额
				cp.setData("EKT_OLD_AMT",oldAmt+exeAmt-amt);//医疗卡中在操作之前的金额 （将此次动到的处方签的所有金额 回冲 获得当前 医疗卡的金额）
				cp.setData("GREEN_BALANCE",0.00);//特批款扣款金额
			}
			cp.setData("OPT_USER", Operator.getID());
			cp.setData("OPT_TERM", Operator.getIP());
			cp.setData("TRADE_SUM_NO",tradeSumNo);////UPDATE EKT_TRADE 冲负数据,医疗卡扣款内部交易号码,格式'xxx','xxx'
			cp.setData("RE_PARM",reParm.getData());//==pangben 2013-7-17 界面数据，将修改OPD_ORDER收费状态添加到一个事物里
			cp.setData("OPB_AMT",sumParm.getDouble("OPB_AMT"));// 门诊医生站操作金额=====pangben 2013-7-17
			cp.setData("ODO_EKT_FLG",sumParm.getValue("ODO_EKT_FLG"));// 门诊医生站操作,调用EKTChageUI.x界面区分医生站和门诊收费界面公用逻辑=====pangben 2013-7-17
			parm.setData("ektParm", cp.getData());
			
			// 泰心医院扣款操作
			TParm p = new TParm(EKTNewIO.getInstance().onNewSaveFee(
					cp.getData()));
			// TParm p = EKTIO.getInstance().consume(cp);
			if (p.getErrCode() < 0)
				parm.setErr(-1, p.getErrText());
			else {
				parm.setData("SQL", p.getData("SQL")); //add by huangtt 20160901
				parm.setData("OP_TYPE", 1);
				parm.setData("TRADE_NO", p.getValue("TRADE_NO"));
				parm.setData("OLD_AMT", oldAmt);
				parm.setData("NEW_AMT",sumAmt);
				parm.setData("EKTNEW_AMT", cp.getDouble("EKT_AMT"));// 医疗卡中的金额
				parm.setData("CANCLE_TREDE", p.getValue("CANCLE_TREDE"));// 退费操作执行的医疗卡扣款主档TREDE_NO信息
				if (null != result && result.getCount() > 0) {
					parm.setData("AMT", amt < 0 ? amt * (-1) : amt); // 收费金额
					parm.setData("EKT_USE", cp.getDouble("EKT_USE")); // 扣医疗卡金额
					parm.setData("GREEN_USE", cp.getDouble("GREEN_USE")); // 扣绿色通道金额
					parm.setData("GREEN_FLG", "Y"); // 判断是否操作绿色通道，添加BIL_OPB_RECP
													// 表PAY_MEDICAL_CARD数据时需要判断为0时的操作
					parm.setData("GREEN_BALANCE", result.getDouble(
							"GREEN_BALANCE", 0)); // 绿色通道未扣款金额
					parm.setData("GREEN_PATH_TOTAL", result.getDouble(
							"GREEN_PATH_TOTAL", 0)); // 绿色通道总金额
				}	
			}
		}
		return parm;
	}
	/**
	 * 正流程收费
	 * 
	 * @param cp
	 *            TParm
	 */
	private void onFee(TParm cp,TParm result,double oldAmt,double amt,double sumAmt) {
		// 医疗卡绿色通道存在
		if (null != result && result.getCount() > 0) {
			// cp.setData("OLD_AMT",oldAmt+result.getDouble("GREEN_BALANCE",0));//医疗卡金额+医疗卡绿色钱包的金额
			if (oldAmt - amt >= 0) {
				cp.setData("EKT_AMT", oldAmt - amt); // 医疗卡金额充足
//				cp.setData("EKT_USE", amt);// 医疗卡扣款金额
				cp.setData("SHOW_GREEN_USE",0.00);//绿色通道扣款金额
			} else {
				cp.setData("EKT_AMT", 0.00); // 医疗卡金额不充足
//				cp.setData("EKT_USE", oldAmt);// 医疗卡扣款金额
			    cp.setData("SHOW_GREEN_USE",StringTool.round(amt - oldAmt,2));//绿色通道扣款金额
			}
			cp.setData("FLG", "Y"); // 绿色通道存在注记
			cp.setData("GREEN_PATH_TOTAL", result.getDouble("GREEN_PATH_TOTAL",0));
		} else {
			cp.setData("GREEN_PATH_TOTAL", 0.00);
			cp.setData("EKT_AMT",sumAmt); // 没有医疗卡绿色钱包
			cp.setData("FLG", "N"); // 绿色通道不存在注记
		}
	}

	/**
	 * 逆流程退费
	 * 
	 * @param cp
	 *            TParm
	 */
	private void onUnFee(TParm cp,TParm result,double oldAmt,double amt,double sumAmt) {
		// 医疗卡绿色通道存在
		if (null != result && result.getCount() > 0) {
			// cp.setData("OLD_AMT",oldAmt+result.getDouble("GREEN_BALANCE",0));//医疗卡金额+医疗卡绿色钱包的金额
			// 正常退费
			if (result.getDouble("GREEN_BALANCE", 0) >= result.getDouble(
					"GREEN_PATH_TOTAL", 0)) {
				cp.setData("EKT_AMT", oldAmt - amt); // 医疗卡金额退费充值
//				cp.setData("EKT_USE", -amt);// 医疗卡扣款金额
				cp.setData("SHOW_GREEN_USE",0.00);//绿色通道扣款金额
			} else {
				// 首先，医疗卡绿色钱包扣款金额充值 然后 绿色钱包扣款金额等于充值的金额以后，再去充值医疗卡
				double tempFee = result.getDouble("GREEN_BALANCE", 0) - amt;// 查看绿色钱包扣款金额+需要退费金额是否大于充值金额
				// 金额大于充值金额将补齐扣款金额
				if (tempFee > result.getDouble("GREEN_PATH_TOTAL", 0)) {
					cp.setData("EKT_AMT", oldAmt + tempFee
							- result.getDouble("GREEN_PATH_TOTAL", 0));// 医疗卡中金额：补齐扣款金额以后的金额+医疗卡中金额
//					cp.setData("EKT_USE", tempFee
//							- result.getDouble("GREEN_PATH_TOTAL", 0));// 医疗卡扣款金额
					cp.setData("SHOW_GREEN_USE",StringTool.round(result.getDouble("GREEN_BALANCE", 0)-result.getDouble("GREEN_PATH_TOTAL", 0),2));//绿色通道扣款金额
				} else if (tempFee <= result.getDouble("GREEN_PATH_TOTAL", 0)) {
					cp.setData("EKT_AMT", oldAmt);// 医疗卡中的金额不变
//					cp.setData("EKT_USE", 0.00);// 医疗卡扣款金额
					cp.setData("SHOW_GREEN_USE",amt);//绿色通道扣款金额
				}
			}
			cp.setData("GREEN_PATH_TOTAL", result.getDouble("GREEN_PATH_TOTAL",
					0));
			cp.setData("FLG", "Y"); // 绿色通道存在注记
		} else {
			cp.setData("GREEN_PATH_TOTAL", 0.00);
			cp.setData("EKT_AMT", sumAmt); // 没有医疗卡绿色钱包
			cp.setData("FLG", "N"); // 绿色通道不存在注记
		}

	}
	
	/**
	 * 获得特批款金额数据，添加EKT_TRADE 表
	 * ==========panben 2013-7-26
	 */
	public void onExeGreenFee(double oldAmt,double amt,String businessType,
			TParm cp,double exeAmt,String caseNo,TParm result){
		if (oldAmt - amt >= 0) {
			if (amt > 0) {//特批款操作，正常扣款流程，amt > 0 医疗卡中存在金额，此次扣款扣除医疗卡中的金额
				cp.setData("EKT_USE", exeAmt);// 医疗卡扣款金额
				cp.setData("GREEN_USE", 0.00);// 绿色通道扣款金额
				// EKT_OLD_AMT:EKT_TRADE 表中 OLD_AMT 金额=原来金额
				// +此次扣款金额(EXE_AMT)-显示金额(AMT)
				cp.setData("EKT_OLD_AMT", oldAmt + exeAmt - amt);// 医疗卡中在操作之前的金额（将此次动到的处方签的所有金额回冲获得当前医疗卡的金额）
			}else{//医疗卡金额不足 
				onGreenFeeTemp(businessType, oldAmt, amt, cp, exeAmt, caseNo, result);	
			}
		} else {
			onGreenFeeTemp(businessType, oldAmt, amt, cp, exeAmt, caseNo, result);
		}
		cp.setData("GREEN_BALANCE",result.getDouble("GREEN_BALANCE", 0));//特批款扣款金额
	}
	
	/**
	 * 医疗卡使用特批款计算金额
	 * ==========pangben 2013-7-26
	 */
	private void onGreenFeeTemp(String businessType,double oldAmt,double amt,
			TParm cp,double exeAmt,String caseNo,TParm result){
		//EKT_OLD_AMT: OLD_AMT 金额=原来金额+(此次扣款金额+特批款剩余金额-审批金额)
		double ektOldAmt=0.00;
		if(businessType.equals("REG") || businessType.equals("REGT")){//挂号操作
			ektOldAmt=oldAmt;
			cp.setData("EKT_OLD_AMT",ektOldAmt);//医疗卡中在操作之前的金额 （将此次动到的处方签的所有金额 回冲 获得当前 医疗卡的金额）
			cp.setData("EKT_USE", ektOldAmt);// 医疗卡扣款金额
			cp.setData("GREEN_USE",exeAmt-ektOldAmt);//绿色通道扣款金额
		}else{
			TParm ektTradeParm=new TParm();
			ektTradeParm.setData("CASE_NO",caseNo);
			ektTradeParm.setData("EKT_TRADE_TYPE","'REG'");
			TParm ektSumTradeParm = EKTNewTool.getInstance().selectEktTrade(
					ektTradeParm);
			//剩余金额+此次执行的金额-显示金额<审批金额-挂号特批款使用金额
			ektOldAmt=oldAmt+result.getDouble("GREEN_BALANCE", 0)+exeAmt-amt;
			double greenPath=result.getDouble("GREEN_PATH_TOTAL", 0)-ektSumTradeParm.getDouble("GREEN_BUSINESS_AMT",0);
			if(ektOldAmt>=greenPath){
				//存在一部分是医疗卡中的金额
				cp.setData("EKT_OLD_AMT",ektOldAmt-greenPath);//医疗卡中在操作之前的金额 （将此次动到的处方签的所有金额 回冲 获得当前 医疗卡的金额）
				cp.setData("EKT_USE", ektOldAmt-greenPath);// 医疗卡扣款金额
				cp.setData("GREEN_USE",exeAmt-ektOldAmt+greenPath);//绿色通道扣款金额
			}else{
				cp.setData("EKT_OLD_AMT",0.00);//医疗卡中在操作之前的金额 （将此次动到的处方签的所有金额 回冲 获得当前 医疗卡的金额）
				cp.setData("EKT_USE", 0.00);// 医疗卡扣款金额
				cp.setData("GREEN_USE",exeAmt);//绿色通道扣款金额
			}
		}
	}
	

}
