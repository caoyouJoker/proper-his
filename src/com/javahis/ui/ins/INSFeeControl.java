package com.javahis.ui.ins;

import jdo.ins.INSOpdOrderTJTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.util.StringTool;

/**
 * 
 * <p>
 * Title:天津医保卡收费
 * </p>
 * 
 * <p>
 * Description:执行显示医保预分割金额，选择收费方式
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author pangb 2011.12.28
 * @version 1.0
 */
public class INSFeeControl extends TControl {
	private TParm insParm;// 需要分割的医嘱
	private boolean feeFlg;// 判断此次操作是执行退费还是收费 ：true 收费 false 退费
	private TParm result;// 返回数据 保存费用分割所有数据 和费用结算数据
	private boolean exeError = false;// 执行操作错误
	private boolean exeSplit = false;// 判断是否执行费用分割
	private TParm parm;//病患信息（包括基本资料和分割数据）
	private String accountamtforreg="";//个人账户金额（用于挂号条金额显示）


	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		// 去掉菜单栏
//        TFrame F=(TFrame)this.getComponent("UI");
//        F.setUndecorated(true);
		callFunction("UI|tButton_1|setEnabled", false);//结算取消按钮置灰（不能使用）
		callFunction("UI|tButton_0|setEnabled", false);//执行按钮置灰（不能使用）
		
		parm = (TParm) getParameter();//病患信息（包括基本资料和分割数据）
		//若无此人信息返回
		if (null == parm) {
			return;
		}
		TComboBox com = (TComboBox) this.getComponent("PAY_WAY");//支付方式
		//PAY_TYPE: Y  医疗卡支付,N 现金支付
		if (parm.getBoolean("PAY_TYPE")) {
			com.setSelectedIndex(2);// 默认 医疗卡支付
		} else {
			com.setSelectedIndex(1);// 默认 现金支付
		}
		this.setValue("MR_NO", parm.getValue("MR_NO"));//病人的病案号（界面显示）
		this.setValue("NAME", parm.getValue("NAME"));//病人姓名（界面显示）
		// 判断此次操作是执行退费还是收费 ：true 收费 false 退费
		feeFlg = parm.getBoolean("FEE_FLG");
		//System.out.println("PARM:::::"+parm);
		this.setValue("INS_TYPE", parm.getValue("INS_TYPE").toString());// 医保类别（界面显示）
		this.setValue("FeeY", parm.getDouble("FeeY"));// 应收金额（界面显示）
		insParm = parm.getParm("insParm");// 需要分割的数据
		// TParm opbReadCardParm=insParm.getParm("opbReadCardParm");
		//		
		// opbReadCardParm.setData("CONFIRM_NO", "MT05511202221342");// 门诊顺序号 E
		// // 或
		// // L方法出参
		// // result.addData("CASE_NO", insParm.getValue("CASE_NO"));// 就诊号 A
		// // 方法中的参数
		// //ssss.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE"));//
		// 医院编码
		// insParm.setData("opbReadCardParm",opbReadCardParm.getData());
		// INSTJFlow.getInstance().cancelBalance(insParm);//取消费用结算操作
	}

	/**
	 * 分割金额
	 */
	public void onSplit() {
		callFunction("UI|tButton_9|setEnabled", false);//解决重复点击按钮造成的问题
		TParm spParm = splitRun();//执行分割操作
		//结算取消按钮恢复使用（用于结算失败作撤销流程）
		callFunction("UI|tButton_1|setEnabled", true);
		if (spParm == null) {
			this.messageBox("结算失败,请执行撤销结算操作");
			return;
		} else {
			this.messageBox("结算成功");	
			callFunction("UI|tButton_0|setEnabled", true);//执行按钮恢复使用
		}
		//医保金额（界面显示）
		this.setValue("INS_FEE", spParm.getValue("ACCOUNT_AMT"));
		// 收费金额（界面显示）
		this.setValue("FeeZ", spParm.getValue("UACCOUNT_AMT"));
		//个人账户金额（用于挂号条金额显示）
		accountamtforreg = spParm.getValue("ACCOUNT_AMT_FORREG");
	}

	/**
	 * 执行操作
	 */
	public void onOK() {
		// 收费方式不可以为空
		if (!this.emptyTextCheck("PAY_WAY")) {
			return;
		}
		//没有获得医保数据返回
		if (!exeSplit) {
			this.messageBox("没有获得医保数据");
			return;
		}
		String type = this.getValue("PAY_WAY").toString();// 收费方式
		result.setData("RETURN_TYPE", 1);// 执行操作
		result.setData("PAY_WAY", type);// 支付方式 1.现金 2.医疗卡
		result.setData("ACCOUNT_AMT", this.getValue("INS_FEE"));//医保金额
		result.setData("UACCOUNT_AMT", this.getValue("FeeZ"));//分割后的金额
		result.setData("ACCOUNT_AMT_FORREG",accountamtforreg);//个人账户
		this.setReturnValue(result);//返回到挂号界面的数据
		this.closeWindow();//关闭窗口
	}

	/**
	 * 执行分割操作
	 * 
	 * @return
	 */
	private TParm splitRun() {
		//执行收费操作时判断是否执行分割
		if (feeFlg) {
			if (null == insParm
					|| insParm.getParm("REG_PARM").getCount("ORDER_CODE") <= 0) {
				this.messageBox("没有需要执行的数据");
				return null;
			}
		}
		// 城居门特 ：函数DataDown_cmts, 门特刷卡交易（E）,得到个人信息
		// 城职普通: 得到个人信息 调用函数DataDown_czys, 门诊刷卡（L）
		// 城职门特 : 函数DataDown_mts, 门特刷卡交易（E）,得到个人信息
		// //System.out.println("ruleParm::::::"+ruleParm);
		// double insSum = 0.00;// 费用分割累计金额
		// if (feeFlg) {// 收费
		// return exeFee(insParm);
		// }
		return exeFee(insParm);
	}

	/**
	 * 执行收费操作
	 * 
	 * @param regParm
	 * @param opbReadCardParm
	 * @return
	 */
	private TParm exeFee(TParm regParm) { 
		// 执行共用的费用分割、添加ins_opd_order 表、明细上传操作
		// 执行费用分割 函数：DataDown_sp1 方法 B
		// 执行上传明细 函数: 函数DataUpload,（B）方法
		regParm.setData("NEW_REGION_CODE", Operator.getRegion());// 区域代码
		regParm.setData("FeeY", parm.getDouble("FeeY"));//收费金额
		result = INSTJFlow.getInstance().comminuteFeeAndInsOrder(regParm);// 费用分割
		exeError = true;// 错误累计
		// 费用分割出现错误
		if (result.getErrCode() < 0) {
			this.messageBox("分割出现错误:" + result.getErrText());
			exeSplit = false;//不执行费用分割
			this.grabFocus("tButton_1");//选择结算取消按钮
			return null;
		} else {//费用分割成功
			//若出现在途数据 即数据库存在INSAMT_FLG=1的数据
			if (null != result.getValue("MESSAGE")
					&& result.getValue("MESSAGE").length() > 0) {
				this.messageBox(result.getValue("MESSAGE"));
				exeSplit = false;//不执行费用分割
				this.grabFocus("tButton_1");//选择结算取消按钮
			} else {
				exeSplit = true;// 执行费用分割操作
				this.grabFocus("tButton_0");//选择执行按钮
			}
		}
		// TParm settlementDetailsParm =
		// result.getParm("settlementDetailsParm");// 费用结算参数
		//获得打印票据（即挂号条）的数据
		TParm parm = INSOpdTJTool.getInstance().queryForPrint(regParm);
		//获得医保专项基金支付金额、现金支付金额和个人账户金额数据
		TParm accountParm = getAmt(regParm.getInt("INS_TYPE"), parm);
		return accountParm;

	}

	public TParm getAmt(int insType, TParm returnParm) {
		// 取得医保专项基金支付金额
		double sOTOT_Amt = 0.00;
		// 取得现金支付金额
		double sUnaccount_pay_amt = 0.00;
		// 取得个人帐户支付金额
		double account_pay_amt = 0.00;
		
		account_pay_amt = returnParm.getDouble("ACCOUNT_PAY_AMT", 0);
		// 城职
		//System.out.println("城职returnParm:::"+returnParm);
		
		if (insType == 1) {
			//取得医保专项基金支付金额
			sOTOT_Amt = returnParm.getDouble("TOT_AMT", 0) - // 总金额
					returnParm.getDouble("UNACCOUNT_PAY_AMT", 0) - // 非账户支付
					returnParm.getDouble("UNREIM_AMT", 0);//基金未报销金额
			// 取得现金支付金额
			sUnaccount_pay_amt = returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					+ returnParm.getDouble("UNREIM_AMT", 0);//现金支付金额
		}
		// 城职门特
		if (insType == 2) {
			//取得医保专项基金支付金额
			sOTOT_Amt = returnParm.getDouble("TOT_AMT", 0)//// 总金额
					- returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)// 非账户支付
					- returnParm.getDouble("UNREIM_AMT", 0);//基金未报销金额
			// 取得现金支付金额
			sUnaccount_pay_amt = returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)// 非账户支付
					+ returnParm.getDouble("UNREIM_AMT", 0);//基金未报销金额
		}
		// 城居门特
		if (insType == 3) {
			//有基金未报销金额病人
			if (null != returnParm.getValue("REIM_TYPE", 0)
					&& returnParm.getInt("REIM_TYPE", 0) == 1) {
				//取得医保专项基金支付金额
				sOTOT_Amt = returnParm.getDouble("TOTAL_AGENT_AMT", 0)//社会统筹金额
						+ returnParm.getDouble("ARMY_AI_AMT", 0)//补助金额
						+ returnParm.getDouble("FLG_AGENT_AMT", 0)//大额救助金额
						+ returnParm.getDouble("ILLNESS_SUBSIDY_AMT", 0)//城乡大病金额
						- returnParm.getDouble("UNREIM_AMT", 0);//基金未报销金额
						

			} else {
				//非基金未报销金额病人
				//取得医保专项基金支付金额
				sOTOT_Amt = returnParm.getDouble("TOTAL_AGENT_AMT", 0)//社会统筹金额
						+ returnParm.getDouble("FLG_AGENT_AMT", 0)//大额救助金额
						+ returnParm.getDouble("ARMY_AI_AMT", 0)//补助金额
				        + returnParm.getDouble("ILLNESS_SUBSIDY_AMT", 0);//城乡大病金额

			}

			// 取得现金支付金额
			sUnaccount_pay_amt = returnParm.getDouble("TOT_AMT", 0)// 总金额
					- returnParm.getDouble("TOTAL_AGENT_AMT", 0)//社会统筹金额
					- returnParm.getDouble("FLG_AGENT_AMT", 0)//大额救助金额
					- returnParm.getDouble("ARMY_AI_AMT", 0)//补助金额
					- returnParm.getDouble("ILLNESS_SUBSIDY_AMT", 0)//城乡大病金额
					+ returnParm.getDouble("UNREIM_AMT", 0);//基金未报销金额
		}
		TParm parm = new TParm();
		parm.setData("ACCOUNT_AMT", sOTOT_Amt);//医保专项基金支付金额
		parm.setData("UACCOUNT_AMT", sUnaccount_pay_amt);//现金支付金额
		parm.setData("ACCOUNT_AMT_FORREG", account_pay_amt);//个人帐户支付金额
//		parm.setData("ACCOUNT_AMT", 1.5);
//		parm.setData("UACCOUNT_AMT", 3.5);
		return parm;
	}

	/**
	 * 
	 * 执行退费操作
	 * 
	 * @return
	 */
	public double reSetExeFee(TParm parm) {
		TParm result = INSTJFlow.getInstance().selectResetFee(parm);
		if (result.getInt("INS_PAT_TYPE", 0) == 1) {
			return result.getDouble("NHI_AMT");
		} else {
			return result.getDouble("INS_PAY_AMT");
		}

	}

	/**
	 * 取消操作
	 */
	public void onCancel() {
		// System.out.println("EXEERROR::" + exeError);
		// 撤销费用结算操作
		if (exeError) {
			if (this.messageBox("提示","是否执行结算撤销操作",2)!=0) {
				return;
			}
			//获得费用结算数据
			TParm result = INSTJFlow.getInstance().cancelBalance(insParm);
			//没有取得费用结算数据
			if (result.getErrCode() < 0) {
				// System.out.println("撤销费用结算操作:" + result.getErrText());
				this.messageBox(result.getValue("PROGRAM_MESSAGE"));
			} else {
				INSOpdTJTool.getInstance().deleteINSOpd(insParm);// 删除INS_OPD表数据
				INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(insParm);// 删除INS_OPD_ORDER表数据
				TParm parm=new TParm();
				parm.setData("CASE_NO",insParm.getValue("CASE_NO"));//病人就诊号
				parm.setData("EXE_USER",Operator.getID());//操作人员
				parm.setData("EXE_TERM",Operator.getIP());//操作地址
				parm.setData("EXE_TYPE",insParm.getValue("RECP_TYPE"));//类型：REG 挂号,REGT 退挂
				INSRunTool.getInstance().deleteInsRun(parm);//取消操作删除在途状态
				this.messageBox("P0005");
				//医保金额（界面显示）
				this.setValue("INS_FEE",0.00);
				// 收费金额（界面显示）
				this.setValue("FeeZ", 0.00);
				//个人账户（撤消后回到原始状态）
				accountamtforreg ="";
				callFunction("UI|tButton_1|setEnabled", false);//结算取消按钮置灰（不能使用）
				callFunction("UI|tButton_0|setEnabled", false);//执行按钮置灰（不能使用）
				exeSplit=false;//不执行费用分割
				callFunction("UI|tButton_9|setEnabled", true);//解决重复点击按钮造成的问题
			}
		}
		
	}
}
