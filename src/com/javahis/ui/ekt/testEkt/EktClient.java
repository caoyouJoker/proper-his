package com.javahis.ui.ekt.testEkt;

import java.math.BigDecimal;

import jdo.ekt.EKTNewIO;
import jdo.reg.PatAdmTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.javahis.ui.opd.OdoMainControl;

public class EktClient {
	protected TControl control;
	protected TParm parm;
	protected TParm readCard;
	
	protected String caseNo;
	protected String cardNo;
	protected String mrNo;
	protected String businessType;
	protected String ektTradeType;
	protected String type_flg;// 退费操作
	protected String ins_flg;// 医保卡注记
	protected String unFlg;// 医生修改的医嘱超过医疗卡金额执行的操作
	protected String tradeNo;
	protected String historyNo;
	protected String opbektFeeFlg;// 医保卡回冲金额添加
	protected BigDecimal oldAmt;//医疗卡金额
	protected BigDecimal insAmt;// 医保金额
	protected int type = 1;
	protected BigDecimal ektAMT = new BigDecimal(0.00);// 医疗卡执行以后金额
	protected BigDecimal ektOldAMT =  new BigDecimal(0.00);// 医疗卡原来金额，操作失败时使用
	protected String cancelTrede = null;// 扣款操作失败回滚医疗卡主档数据		
	protected BigDecimal opbAmt =  new BigDecimal(0.00);// 门诊医生站操作的金额
	protected BigDecimal greenBalance =  new BigDecimal(0.00);// 绿色通道总扣款金额
	protected BigDecimal greenPathTotal =  new BigDecimal(0.00);// 绿色通道审批金额
	protected String greenFlg = null;// 判断是否操作绿色通道，添加BIL_OPB_RECP
	protected boolean isNull = true;
	protected TParm result = new TParm();
	protected TParm p = new TParm();
	protected TParm greenParm = new TParm();
	
	protected BigDecimal amt = new BigDecimal(0.00); // 扣款金额
	protected BigDecimal oldAmtGerrenBalance = new BigDecimal(0);
	
	public EktParam onAccntClient(EktParam ektParam){
		control = ektParam.gettControl();
//		TParm result = new TParm();
		// 禁止在服务器端调用
		if (EKTNewIO.getInstance().ektIsClientlink() ) {			
			ektParam.setOpType("-1");
			return ektParam;
		}
		// 医疗卡流程是否启动
		if (!ektSwitch()) {
			ektParam.setOpType("0");
			return ektParam;
		}
		 parm = ektParam.getOrderParm();
		if (parm == null) {
			//System.out.println("ERR:EKTIO.onOPDAccntClient TParm 参数为空");
			ektParam.setOpType("-1");
			return ektParam;
		}
		 caseNo = ektParam.getReg().caseNo();
		if (caseNo == null || caseNo.length() == 0) {
			//System.out.println("ERR:EKTIO.onOPDAccntClient caseNo 参数为空");
			ektParam.setOpType("-1");
			return ektParam;
		}

		 readCard = parm.getParm("ektParm");// 泰心医疗卡读卡操作
		if (parm.getBoolean("IS_NEW") && readCard.getErrCode() == -1) {
			if ("无卡".equals(readCard.getValue("ERRCode")))
				control.messageBox("无卡,新增医嘱保存未收费！");
			else
				control
						.messageBox(readCard.getValue("ERRCode")
								+ ",新增医嘱保存未收费！");
			ektParam.setOpType("5");
			return ektParam;

		}

		if (readCard.getErrCode() != 0) {;
			control.messageBox("医疗卡操作,中途失败");
			ektParam.setOpType("-1");
			return ektParam;
		}
		
		// 泰心医院获得卡号
		 cardNo = readCard.getValue("MR_NO") + readCard.getValue("SEQ");
		 oldAmt = new BigDecimal(readCard.getDouble("CURRENT_BALANCE"))  ;//医疗卡金额
		 mrNo = parm.getValue("MR_NO");
		 businessType = parm.getValue("BUSINESS_TYPE");
		//查询类型获得门诊挂号REG,REGT和收费OPB,OPBT,ODO,ODOT
		 ektTradeType = parm.getValue("EKT_TRADE_TYPE");
		 type_flg = parm.getValue("TYPE_FLG");// 退费操作
		 ins_flg = parm.getValue("INS_FLG");// 医保卡注记
		 insAmt =new BigDecimal( parm.getDouble("INS_AMT"));// 医保金额
		 unFlg = parm.getValue("UN_FLG");// 医生修改的医嘱超过医疗卡金额执行的操作
		 tradeNo = "";
		if (!mrNo.equals(readCard.getValue("MR_NO"))) {
			if (parm.getBoolean("IS_NEW")) {
				control.messageBox("此卡片不属于该患者,新增医嘱保存未收费！");
				ektParam.setOpType("5");
				return ektParam;
			}
			control.messageBox("此卡片不属于该患者!");
			ektParam.setOpType("3");
			return ektParam;
		}
		 opbektFeeFlg = parm.getValue("OPBEKTFEE_FLG");// 医保卡回冲金额添加
		 
		 
			if (EKTDialogSwitch()) {
				if (control == null) {
					//System.out.println("ERR:EKTIO.onOPDAccntClient control 参数为空");
					ektParam.setOpType("-1");
					return ektParam;
				}
				p.setData("CASE_NO", caseNo);
				p.setData("CARD_NO", cardNo);
				p.setData("MR_NO", mrNo);
				
				
				// 查询此就诊病患所有数据汇总金额
				//TParm orderSumParm=parm.getParm("orderSumParm");
				//查询此病患已收费未打票的所有数据汇总金额
				//TParm ektSumParm =parm.getParm("ektSumParm");
				amt=new BigDecimal(parm.getDouble("SHOW_AMT"));//未收费的总金额 显示金额
				
				if (amt.equals(BigDecimal.ZERO) && isNull) {
//					control.messageBox("没有需要操作的数据");
					ektParam.setOpType("6");// 没有需要操作的数据
					return ektParam;
				}
				if (!amt.equals(BigDecimal.ZERO) || !isNull) {
					opbAmt =new BigDecimal(amt.doubleValue()) ;
					p.setData("AMT", amt.doubleValue());
					p.setData("EXE_AMT", parm.getDouble("EXE_AMT"));//执行金额(EKT_TRADE 中此次 操作的金额)
					//未打票数据总金额
					readCard.setData("NAME", parm.getValue("NAME"));
					readCard.setData("SEX", parm.getValue("SEX"));
					p.setData("READ_CARD", readCard.getData());
					p.setData("BUSINESS_TYPE", businessType);// 扣款字段
					p.setData("EKT_TRADE_TYPE", ektTradeType);
					p.setData("TYPE_FLG", type_flg);// 退费注记
					p.setData("INS_FLG", ins_flg);// 医保注记
					p.setData("INS_AMT", insAmt);// 医保金额
					p.setData("OPBEKTFEE_FLG", opbektFeeFlg);
					p.setData("TRADE_SUM_NO", parm.getValue("TRADE_SUM_NO"));////UPDATE EKT_TRADE 冲负数据,医疗卡扣款内部交易号码,格式'xxx','xxx'
					//p.setData("newParm", parm.getParm("newParm").getData());//操作医生站医嘱，需要操作的医嘱(增删改数据集合)
					
					// 查询绿色通道使用金额
					TParm tempParm=new TParm();
					tempParm.setData("CASE_NO",caseNo);
				    greenParm = PatAdmTool.getInstance().selEKTByMrNo(tempParm);
					if (greenParm.getErrCode() < 0) {
						ektParam.setOpType("-1");
						return ektParam;
					}
					
					oldAmtGerrenBalance = oldAmtGerrenBalance.add(oldAmt);
					oldAmtGerrenBalance = oldAmtGerrenBalance.add(new BigDecimal(greenParm.getDouble("GREEN_BALANCE", 0)));
					
					
				} 
			}
		 
		 
		 
		
		ektParam.setOpType("");
		return ektParam;
	}
	
	
	public void setResultValue(){
		
		result.setData("EKTNEW_AMT", ektAMT.doubleValue());
		result.setData("OLD_AMT", ektOldAMT.doubleValue()); // 医疗卡已经交易以后的金额
		result.setData("OP_TYPE", type);
		result.setData("TRADE_NO", tradeNo);
		result.setData("HISTORY_NO", historyNo);
		result.setData("CARD_NO", cardNo);
		result.setData("OPD_UN_FLG",parm.getValue("OPD_UN_FLG"));// 医生修改医嘱操作
		result.setData("CANCLE_TREDE", cancelTrede);// 撤销使用
		result.setData("OPB_AMT", opbAmt.doubleValue());// 门诊医生站操作金额
		result.setData("GREEN_FLG", greenFlg);// 添加BIL_OPB_RECP
		result.setData("GREEN_BALANCE", greenBalance.doubleValue()); // 绿色通道未扣款金额
		result.setData("GREEN_PATH_TOTAL", greenPathTotal.doubleValue()); // 绿色通道总金额
		
	}
	
	
	
	/**
	 * ekt开关
	 * 
	 * @return boolean
	 */
	public boolean ektSwitch() {
		return StringTool.getBoolean(TConfig.getSystemValue("ekt.switch"));
	}
	
	/**
	 * 医疗卡开关
	 * 
	 * @return boolean
	 */
	public boolean EKTDialogSwitch() {
		return StringTool.getBoolean(TConfig
				.getSystemValue("ekt.opd.EKTDialogSwitch"));
	}

}
                                                  