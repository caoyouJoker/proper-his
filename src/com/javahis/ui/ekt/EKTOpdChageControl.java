package com.javahis.ui.ekt;

import jdo.ekt.EKTIO;
import jdo.ekt.EKTNewIO;
import jdo.ekt.EKTNewTool;
import jdo.odo.OpdRxSheetTool;
import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TDialog;

/**
 * <p>
 * Title: 医生站医疗卡金额不足退还医疗卡金额界面
 * </p>
 * 
 * <p>
 * Description:医生修改医嘱后，执行收费操作 医疗卡中,如过金额不足执行将此处方签设置为没有收费状态，退还医疗卡中金额
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author pangben 2012-3-7
 * @version 4.1
 */
public class EKTOpdChageControl extends TControl {
	private String caseNo;
	private String mrNo;
	private double oldAmt = 0.00;
	private TParm readCard;
	private TParm returnParm = new TParm();
	private String ektTradeType;//查询类型获得门诊挂号REG,REGT和收费OPB,OPBT,ODO,ODOT
	String cardNo = "";

	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		// 去掉菜单栏
		TDialog F=(TDialog)this.getComponent("UI");
        F.setUndecorated(true);
		onInitParm((TParm) getParameter());
	}

	/**
	 * 初始化参数
	 * 
	 * @param parm
	 *            TParm
	 */
	private void onInitParm(TParm parm) {
		//System.out.println("传参OPD====="+parm);
		if (parm == null)
			return;
		String insFlg=parm.getValue("INS_FLG");
		readCard = parm.getParm("READ_CARD");
		cardNo = readCard.getValue("CARD_NO");
		mrNo = parm.getValue("MR_NO");
		oldAmt = readCard.getDouble("CURRENT_BALANCE");
		if (null!=insFlg && insFlg.equals("Y")) {
		}else{
			this.messageBox("金额不足,退还医疗卡金额");
			opdExe(parm);
		}
		setValue("READ_CARD_NO", cardNo);
		// ZHANGP 20120106
//		setValue("CARD_NO",cardNo);
		setValue("READ_NAME1", readCard.getValue("NAME"));
		setValue("READ_SEX1", readCard.getValue("SEX"));
		if ("en".equals(getLanguage())) {
			setValue("PAT_NAME", OpdRxSheetTool.getInstance().getPatEngName(
					mrNo));
		} else {
			setValue("PAT_NAME", readCard.getValue("PAT_NAME"));
		}
		setValue("SEX_CODE", readCard.getValue("SEX_CODE"));
		setValue("MR_NO", mrNo);
		setValue("CARD_NO", cardNo);
	}

	public void onConcle() {
		//System.out.println("sdfsdfsdfreturnParm:::::"+returnParm);
		setReturnValue(returnParm);
		closeWindow();
	}
	/**
	 * 门诊医生站金额不足回退动作
	 * @param parm
	 */
	private void opdExe(TParm parm){
		double greeBalance = parm.getDouble("GREEN_BALANCE");// 绿色通道剩余金额
		double greenPathTotal = parm.getDouble("GREEN_PATH_TOTAL");// 绿色通道审批金额
		TParm unParm = parm.getParm("unParm");// 需要退还的医嘱
		double amt = 0.00;
		StringBuffer trade_no = new StringBuffer();
		caseNo = parm.getValue("CASE_NO");
		if (null == caseNo) {
			return;
		}
		for (int i = 0; i < unParm.getCount(); i++) {
			// 获得医嘱收费内部交易号码
			if (!trade_no.toString().contains(unParm.getValue("BUSINESS_NO", i))) {
				trade_no.append("'" + unParm.getValue("BUSINESS_NO", i) + "',");
			}
		}

		String tradeNo = "''";
		if (trade_no.toString().length() > 0) {
			tradeNo = trade_no.substring(0, trade_no.lastIndexOf(","));
		}
		// 查询需要退还的金额
		// 医生修改的医嘱超过医疗卡金额执行的操作 查询
		String sql = "SELECT CASE_NO,RX_NO,SEQ_NO,MR_NO,AR_AMT AS AMT ,'N' BILL_FLG,'C' BILL_TYPE FROM OPD_ORDER "
				+ "WHERE CASE_NO='"
				+ caseNo+ "' AND BILL_FLG='Y' AND BILL_TYPE='E'"
				+ " AND BUSINESS_NO IN ("
				+ tradeNo + ") ";
		returnParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (returnParm.getErrCode() < 0) {
			returnParm.setErr(-1, returnParm.getErrText());
			return;
		}
		//退费时，新修改的金额不需要,查找 未修改之前的医嘱金额
		boolean flg=false;
		for (int i = 0; i < returnParm.getCount(); i++) {
			flg=false;
			for (int j = 0; j < unParm.getCount(); j++) {
				if (unParm.getValue("RX_NO", j).equals(returnParm.getValue("RX_NO",i))
						&& unParm.getValue("SEQ_NO", j).equals(returnParm.getValue("SEQ_NO",i))) {
					returnParm.setData("AMT",i,unParm.getDouble("AR_AMT", j));
					amt += unParm.getDouble("AR_AMT", j);// 获得未修改之前的金额
					flg=true;
					break;
				}
			}
			if(!flg)
			amt += returnParm.getDouble("AMT", i);// 获得金额
		}
		setValue("AMT", amt);
		setValue("OLD_AMT", oldAmt + greeBalance);
		//医疗卡中的金额=医疗卡原来的金额+已经扣款的医疗卡金额+绿色通道使用金额+绿色通道剩余-医嘱总金额
		setValue("NEW_AMT",oldAmt+ greeBalance+amt);
		TParm cp = new TParm();
		//存在已经使用特批款
		if(greeBalance>0 && greenPathTotal>greeBalance){			
			//特批款总金额>此次回退金额+特批款扣款金额 ，医疗卡中金额=0
			if(greeBalance+amt<greenPathTotal){
				cp.setData("SHOW_GREEN_USE",-amt);//此次特批款扣款金额
				cp.setData("EKT_AMT", 0); // 医疗卡当前金额
			}else{
//				//将超过审批总金额的部分回冲到医疗卡中
//				cp.setData("AMT",orderSumParm.getDouble("TOT_AMT",0));//医疗卡扣款金额
//				cp.setData("GREEN_USE",0);//此次特批款扣款金额
				cp.setData("SHOW_GREEN_USE",greeBalance-greenPathTotal);//此次特批款扣款金额
				cp.setData("EKT_AMT", greeBalance+amt-greenPathTotal); // 医疗卡当前金额
			}
			cp.setData("GREEN_BALANCE",greeBalance);//特批款金额
			cp.setData("FLG","Y");//特批款当前金额修改 REG_PATADM 表中 特批款当前剩余金额
		}else{
			cp.setData("EKT_AMT",this.getValueDouble("NEW_AMT")); // 医疗卡当前金额
			cp.setData("GREEN_BALANCE",0.00);//特批款金额
			cp.setData("SHOW_GREEN_USE",0.00);//此次特批款扣款金额
		}
		cp.setData("MR_NO", parm.getValue("MR_NO"));//病案号
		cp.setData("CASE_NO", caseNo);//就诊号
		//cp.setData("BUSINESS_NO", parm.getValue("BUSINESS_NO"));
		cp.setData("PAT_NAME", readCard.getValue("PAT_NAME"));//病患名称
		cp.setData("OLD_AMT", oldAmt);//医疗卡原来金额
		cp.setData("BUSINESS_TYPE", parm.getValue("BUSINESS_TYPE"));//收费类型
		//查询类型获得门诊挂号REG,REGT和收费OPB,OPBT,ODO,ODOT
		cp.setData("EKT_TRADE_TYPE", parm.getValue("EKT_TRADE_TYPE"));//查询条件
		cp.setData("CARD_NO", readCard.getValue("PK_CARD_NO"));//卡号
		cp.setData("SEQ", readCard.getValue("SEQ"));//序号
		cp.setData("IDNO", readCard.getValue("IDNO"));//序号
		cp.setData("GREEN_PATH_TOTAL",greenPathTotal);//绿色通道审批金额
		cp.setData("OPT_USER", Operator.getID());//id
		cp.setData("OPT_TERM", Operator.getIP());//IP
		cp.setData("TRADE_SUM_NO", tradeNo);//内部交易号码
		TParm returnParm  = new TParm(EKTNewIO.getInstance().onNewSaveFee(cp.getData()));
		if (greenPathTotal > 0) {
			callFunction("UI|EKT_GREEN_LBL|setVisible", true);// 显示绿色通道金额
			callFunction("UI|GREEN_BALANCE|setVisible", true);
			this.setValue("GREEN_BALANCE", greeBalance);
		}
	}
}
