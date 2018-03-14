package com.javahis.ui.opd;

import jdo.ekt.EKTGreenPathTool;
import jdo.ekt.EKTNewIO;
import jdo.ekt.EKTNewTool;
import jdo.odo.OpdRxSheetTool;
import jdo.reg.PatAdmTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TDialog;
/**
 * 
 * <p>
 * Title:就诊金额查询
 * </p>
 * 
 * <p>
 * Description:门急诊医生站查询就诊病患操作金额
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author pangb 2013.03.27
 * @version 4.0
 */
public class OPDOrderPreviewAmtControl extends TControl {
	private String caseNo;
	private double amt;//显示金额
	private TParm readCard;//医疗卡属性
	private TParm result;// 医疗卡绿色通道金额
	//private TParm newParm;//操作医生站医嘱，需要操作的医嘱(增删改数据集合)
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}
	private void initPage(){
		TParm parm = (TParm) getParameter();
		int type =parm.getInt("EKT_TYPE_FLG");
		switch (type){//type:1 .查询就诊金额 2.不是修改医嘱，但是此次金额超过医疗卡金额 ,3.修改医嘱金额不足退回处方签中的金额
		case 1:
			this.setValue("EKT_AMT", parm.getDouble("CURRENT_BALANCE"));//医疗卡金额
			String sql = "SELECT SUM(AR_AMT) AS AR_AMT FROM OPD_ORDER WHERE CASE_NO='"+ parm.getValue("CASE_NO")+ "' AND RELEASE_FLG<>'Y'";
			TParm sumParm = new TParm(TJDODBTool.getInstance().select(sql)); //总金额（未收费和已收费）
			this.setValue("FEE_Y", sumParm.getDouble("AR_AMT",0));//应收金额
			sql = "SELECT SUM(AR_AMT) AS AR_AMT FROM OPD_ORDER WHERE CASE_NO='"+ parm.getValue("CASE_NO")+ "' AND RELEASE_FLG<>'Y' AND BILL_FLG='Y' ";
			TParm billParm = new TParm(TJDODBTool.getInstance().select(sql)); // 已收费
			this.setValue("BALANCE_AMT", parm.getDouble("CURRENT_BALANCE")-(sumParm.getDouble("AR_AMT",0)-billParm.getDouble("AR_AMT",0)));//差额
			break;
		case 2:
			onInitParm(parm);
			break;
		}
		
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
			return ;
		}
		caseNo = parm.getValue("CASE_NO");//就诊号
		if (null == caseNo) {
			return;
		}
		amt = parm.getDouble("AMT");//显示的金额
		//sumOpdorderAmt = parm.getDouble("SUMOPDORDER_AMT");
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
		// setValue("OLD_AMT",oldAmt);
		setValue("FEE_Y", amt);//显示金额
		this.setValue("EKT_AMT",readCard.getDouble("CURRENT_BALANCE"));//医疗卡金额
		this.setValue("BALANCE_AMT",readCard.getDouble("CURRENT_BALANCE")-amt);//差额
		// setValue("NEW_AMT",oldAmt - amt);
	}
	public boolean onCheck() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		// 查询此次就诊病患是否存在医疗卡绿色通道
		TParm patEktParm = EKTGreenPathTool.getInstance().selPatEktGreen(parm);
		if (patEktParm.getInt("COUNT", 0) > 0) {
			// 查询绿色通道扣款金额、总充值金额
			result = PatAdmTool.getInstance().selEKTByMrNo(parm);
			callFunction("UI|EKT_GREEN_LBL|setVisible", true);// 显示绿色通道金额
			callFunction("UI|GREEN_BALANCE|setVisible", true);
			this.setValue("GREEN_BALANCE", result.getValue("GREEN_BALANCE", 0));
			this.messageBox("此就诊病患存在医疗卡绿色钱包");
		}
		return true;// EKTIO.getInstance().createCard(cardNo,mrNo,oldAmt);
	}
}
