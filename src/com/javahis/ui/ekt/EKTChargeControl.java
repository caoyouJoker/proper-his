package com.javahis.ui.ekt;

import com.dongyang.data.TParm;
import com.dongyang.control.TControl;

import jdo.odo.OpdRxSheetTool;
import com.dongyang.ui.TDialog;
import com.dongyang.ui.TButton;
import jdo.sys.Operator;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.reg.PatAdmTool;
import jdo.ekt.EKTNewIO;
import jdo.ekt.EKTNewTool;
import jdo.ekt.EKTGreenPathTool;
import jdo.sys.SystemTool;

/**
 * <p>
 * Title: 医疗卡缴费界面
 * </p>
 * 
 * <p>
 * Description:医疗卡缴费界面
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
 * @author sundx
 * @version 1.0
 */
public class EKTChargeControl extends TControl {

	private String caseNo;
	private String mrNo;
	private String cardNo;
	private double oldAmt = 0.00;
	private double amt;//显示金额
	private TParm readCard;//医疗卡属性
	private String ektTradeType;// 查询类型获得门诊挂号REG,REGT和收费OPB,OPBT,ODO,ODOT
	private String businessType;//此次操作的类型 ODO OPB REG
	private TParm result;// 医疗卡绿色通道金额
	private String insFlg;// 医保卡注记，医疗卡主档添加参数
	private double insAmt;// 医保金额
	//private double sumOpdorderAmt;// 未打票医嘱总金额
	private double exeAmt;// 此次操作金额
	//private TParm newParm;//操作医生站医嘱，需要操作的医嘱(增删改数据集合)
	private String tradeSumNo;//此次操作已经收费的内部交易号码
	private String odoEktFlg;//==pangben 2013-7-17门诊医生站操作,调用EKTChageUI.x界面区分医生站和门诊收费界面公用逻辑
	private TParm reParm;//==pangben 2013-7-17医生站医嘱参数，将UPDATE OPD_ORDER BILL='Y',BUSINESS_NO=XXX 写到一个事物
	private String confirmNo; 
	
	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		// 去掉菜单栏
		TDialog F = (TDialog) this.getComponent("UI");
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
		// System.out.println("传参====="+parm);
		if (parm == null)
			return;
		readCard = parm.getParm("READ_CARD");
		if (readCard.getErrCode() != 0) {
			((TButton) getComponent("tButton_0")).setEnabled(false);
			setValue("Message_Text", readCard.getErrText());
		}
		cardNo = readCard.getValue("CARD_NO");//卡号
		setValue("READ_CARD_NO", cardNo);
		setValue("READ_NAME1", readCard.getValue("PAT_NAME"));//名称
		setValue("READ_SEX1", readCard.getValue("SEX"));//性别
		caseNo = parm.getValue("CASE_NO");//就诊号
		tradeSumNo=parm.getValue("TRADE_SUM_NO");//此次操作已经收费的内部交易号码,格式'xxx','xxx'
		if (null == caseNo) {
			return;
		}
		odoEktFlg=parm.getValue("ODO_EKT_FLG");//==pangben 2013-7-17门诊医生站操作,调用EKTChageUI.x界面区分医生站和门诊收费界面公用逻辑
		reParm=parm.getParm("RE_PARM");//==pangben 2013-7-17医生站医嘱参数，将UPDATE OPD_ORDER BILL='Y',BUSINESS_NO=XXX 写到一个事物
		amt = parm.getDouble("AMT");//显示的金额
		mrNo = parm.getValue("MR_NO");
		oldAmt = readCard.getDouble("CURRENT_BALANCE");
		ektTradeType = parm.getValue("EKT_TRADE_TYPE");// 查询条件
		businessType = parm.getValue("BUSINESS_TYPE");// 保存数据参数
		insFlg = parm.getValue("INS_FLG");// 医保卡注记
		confirmNo = parm.getValue("CONFIRM_NO");// 医保号
		insAmt = parm.getDouble("INS_AMT");// 医保金额
		//sumOpdorderAmt = parm.getDouble("SUMOPDORDER_AMT");
		exeAmt = parm.getDouble("EXE_AMT");//医生站此次操作金额
		//newParm = parm.getParm("newParm");
		// 门诊收费医保扣款回冲医疗卡不能执行取消操作
		if (null != parm.getValue("OPBEKTFEE_FLG")
				&& parm.getBoolean("OPBEKTFEE_FLG")) {
			callFunction("UI|tButton_1|setEnabled", false);
		}
		if (!onCheck()) {
			this.messageBox("医疗卡初始化失败!");
			return;
		}
		if ("en".equals(getLanguage())) {
			setValue("PAT_NAME", OpdRxSheetTool.getInstance().getPatEngName(
					mrNo));
		} else {
			setValue("PAT_NAME", readCard.getValue("PAT_NAME"));
		}
		setValue("SEX_CODE", readCard.getValue("SEX_CODE"));
		setValue("MR_NO", mrNo);
		setValue("CARD_NO", cardNo);
		// setValue("OLD_AMT",oldAmt);
		setValue("AMT", amt);
		// setValue("NEW_AMT",oldAmt - amt);
		//add by huangtt 20151127 如果交易金额为负数，取消按钮置灰
		if(this.getValueDouble("AMT") < 0){
			this.callFunction("UI|tButton_1|setEnabled", false);
		}
	}

	public void onOK() {
		// zhangp 20111230 验证
		TParm parm = new TParm();
		if (readCard.getErrCode() < 0) {
			this.messageBox("此医疗卡无效");
			// TParm parm = new TParm();
			parm.setErr(-1, "此医疗卡无效");
			setReturnValue(parm);
			closeWindow();
		}
		parm.setData("CARD_NO", readCard.getValue("PK_CARD_NO"));
		if (this.getValueDouble("NEW_AMT") < 0) {
			messageBox_("用户余额不足,本次操作没有扣款!");
			parm.setData("OP_TYPE", 2);
		} else {
			TParm cp = new TParm();
			cp.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
			cp.setData("CARD_NO", readCard.getValue("PK_CARD_NO"));
			cp.setData("CASE_NO", caseNo);
			cp.setData("MR_NO", mrNo);
			cp.setData("PAT_NAME", readCard.getValue("PAT_NAME"));
			cp.setData("IDNO", readCard.getValue("IDNO"));
			// cp.setData("BUSINESS_NO",businessNo);
			if (businessType == null || businessType.length() == 0)
				businessType = "none";
			cp.setData("BUSINESS_TYPE", businessType);
			cp.setData("EKT_TRADE_TYPE", ektTradeType);//挂号使用 REG_PATADM 表没有TRADE_NO 字段 
			cp.setData("OLD_AMT", oldAmt);// 医疗卡金额
			cp.setData("NEW_AMT", this.getValue("NEW_AMT"));
			cp.setData("INS_FLG", insFlg);// 医保卡注记
			// zhangp 20120106 加入seq
			cp.setData("SEQ", readCard.getValue("SEQ"));// 序号
			//cp.setData("SUMOPDORDER_AMT", sumOpdorderAmt);
			// 扣款
			if (amt >= 0) {
				onFee(cp);
			} else {
				// 退费
				onUnFee(cp);
			}
			//parm.setData("TRADE_NO",tradeSumNo);
			//需要操作的交易金额
			//TParm ektTradeSumParm=EKTNewTool.getInstance().selectEktTradeUnSum(parm);
			
			// 医疗卡金额大于此次扣款金额
			if (null != result && result.getCount() > 0) {
				EKTNewIO.getInstance().onExeGreenFee(oldAmt, amt, businessType, cp, exeAmt, caseNo, result);
			}else{
				cp.setData("EKT_USE", exeAmt);// 医疗卡扣款金额
				cp.setData("GREEN_USE",0.00);//绿色通道扣款金额
				cp.setData("EKT_OLD_AMT",oldAmt+exeAmt-amt);//医疗卡中在操作之前的金额 （将此次动到的处方签的所有金额 回冲 获得当前 医疗卡的金额）
				cp.setData("GREEN_BALANCE",0.00);//特批款扣款金额
			}
			cp.setData("OPT_USER", Operator.getID());
			cp.setData("OPT_TERM", Operator.getIP());
			cp.setData("TRADE_SUM_NO",tradeSumNo);////UPDATE EKT_TRADE 冲负数据,医疗卡扣款内部交易号码,格式'xxx','xxx'
			if (null!=odoEktFlg&&odoEktFlg.equals("Y")) {
				cp.setData("RE_PARM",reParm.getData());//==pangben 2013-7-17 界面数据，将修改OPD_ORDER收费状态添加到一个事物里
				cp.setData("OPB_AMT",amt);// 门诊医生站操作金额=====pangben 2013-7-17
				cp.setData("ODO_EKT_FLG",odoEktFlg);// 门诊医生站操作,调用EKTChageUI.x界面区分医生站和门诊收费界面公用逻辑=====pangben 2013-7-17
			}
			// 泰心医院扣款操作
			cp.setData("CONFIRM_NO", confirmNo);
			System.out.println("医疗卡入参====="+cp);  
			TParm p = new TParm(EKTNewIO.getInstance().onNewSaveFee(
					cp.getData()));
			// TParm p = EKTIO.getInstance().consume(cp);
			if (p.getErrCode() < 0)
				parm.setErr(-1, p.getErrText());
			else {
				parm.setData("SQL", p.getData("SQL")); //add by huangtt 20160901   
				parm.setData("OP_TYPE", 1);
				parm.setData("TRADE_NO", p.getValue("TRADE_NO"));
				parm.setData("HISTORY_NO", p.getValue("HISTORY_NO"));
				parm.setData("OLD_AMT", oldAmt);
				parm.setData("NEW_AMT", this.getValue("NEW_AMT"));
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
		this.setReturnValue(parm);
		this.closeWindow();
	}

	/**
	 * 正流程收费
	 * 
	 * @param cp
	 *            TParm
	 */
	private void onFee(TParm cp) {
		// 医疗卡绿色通道存在
		if (null != result && result.getCount() > 0) {
			// cp.setData("OLD_AMT",oldAmt+result.getDouble("GREEN_BALANCE",0));//医疗卡金额+医疗卡绿色钱包的金额
			System.out.println("ekt_user===="+(oldAmt - amt));
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
			cp.setData("EKT_AMT", this.getValue("NEW_AMT")); // 没有医疗卡绿色钱包
			cp.setData("FLG", "N"); // 绿色通道不存在注记
		}
	}

	/**
	 * 逆流程退费
	 * 
	 * @param cp
	 *            TParm
	 */
	private void onUnFee(TParm cp) {
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
			cp.setData("EKT_AMT", this.getValue("NEW_AMT")); // 没有医疗卡绿色钱包
			cp.setData("FLG", "N"); // 绿色通道不存在注记
		}

	}

	public boolean onCheck() {

		cardNo = readCard.getValue("CARD_NO");
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		// 查询此次就诊病患是否存在医疗卡绿色通道
		TParm patEktParm = EKTGreenPathTool.getInstance().selPatEktGreen(parm);
		oldAmt = readCard.getDouble("CURRENT_BALANCE");
		double sumAmt = oldAmt - amt;
		double tempAmt = oldAmt;
		if (patEktParm.getInt("COUNT", 0) > 0) {
			// 查询绿色通道扣款金额、总充值金额
			result = PatAdmTool.getInstance().selEKTByMrNo(parm);
			callFunction("UI|EKT_GREEN_LBL|setVisible", true);// 显示绿色通道金额
			callFunction("UI|GREEN_BALANCE|setVisible", true);
			this.setValue("GREEN_BALANCE", result.getValue("GREEN_BALANCE", 0));
			this.messageBox("此就诊病患存在医疗卡绿色钱包");
			sumAmt += result.getDouble("GREEN_BALANCE", 0);// 扣款之后的金额
			tempAmt += result.getDouble("GREEN_BALANCE", 0);// 未扣款金额
		}
		if (null != insFlg && insFlg.equals("Y")) {
			callFunction("UI|INS_LBL|setVisible", true);// 显示医保扣款金额
			callFunction("UI|INS_AMT|setVisible", true);
			setValue("INS_AMT", insAmt);
		}
		// caseNo
		setValue("OLD_AMT", tempAmt);
		setValue("CARD_NO", cardNo);
		setValue("NEW_AMT", sumAmt);
		return true;// EKTIO.getInstance().createCard(cardNo,mrNo,oldAmt);
	}

	public void onK() {
		TParm parm = new TParm();
		parm.setData("CARD_NO", cardNo);
		parm.setData("OP_TYPE", 2);
		setReturnValue(parm);
		closeWindow();
	}

	/**
	 * 取消键
	 */
	public void onCancel() {
		this.closeWindow();
	}

	/**
	 * 执行添加收据操作
	 */
	private void onOpbRect() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", mrNo);
		parm.setData("INV_NO", "");
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("TOT_AMT", this.getValueDouble("AMT"));// 扣款金额
		parm.setData("billFlg", "Y"); // 记账: N 不记账:Y

	}
}
