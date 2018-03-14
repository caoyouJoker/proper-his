package com.javahis.ui.ins;

import jdo.ins.INSTJFlow;
import jdo.ins.INSTJTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

/**
 * 
 * <p>
 * Title:医保操作人员撤销管理
 * </p>
 * 
 * <p>
 * Description:医保操作人员撤销管理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author pangben 2012.2.23
 * @version 2.0
 */
public class INSConcelErrorControl extends TControl {
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
	}

	private String[] comm = { "NHI_REGION_CODE", "INS_READ_TYPE",
			"INS_PAT_TYPE", "CONFIRM_NO" };
	private StringBuffer messaGrab;//获得焦点
	/**
	 * 门诊费用结算撤销
	 */
	public void onConcelSettlement() {
		if (!getCheck("门诊费用结算撤销")) {
			return;
		}
		if (this.messageBox("提示","是否确认执行",2)!=0) {
			return;
		}
		TParm parm=INSTJFlow.getInstance().cancelBalance(commFunction(),getType());
		if (parm.getErrCode()<0) {
			this.setValue("TEXT", parm.getErrText());
			return;
		}
		this.setValue("TEXT", parm.getValue("PROGRAM_MESSAGE"));
	}

	private TParm commFunction() {
		TParm parm = new TParm();
		parm.setData("INS_TYPE", getType());
		parm.setData("REGION_CODE", this.getValue("NHI_REGION_CODE"));
		parm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO"));
		TParm opbReadCardParm = new TParm();
		opbReadCardParm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO"));
		parm.setData("opbReadCardParm", opbReadCardParm.getData());
		return parm;
	}

	/**
	 * 门诊退费取消
	 */
	public void onConcelReSetFee() {
		
		if (!getCheck("门诊退费取消")) {
			return;
		}
		if (this.messageBox("提示","是否确认执行",2)!=0) {
			return;
		}
		TParm parm=INSTJFlow.getInstance().resetConcelFee(commFunction());
		if (parm.getErrCode()<0) {
			this.setValue("TEXT", parm.getErrText());
			return;
		}
		this.setValue("TEXT", parm.getValue("PROGRAM_MESSAGE"));
	}

	private boolean getCheck(String messageName) {
		String message = concelComm();
		if (message.length() > 0) {
			message = message.substring(0, message.lastIndexOf(","));
			this.messageBox(messageName + "需要" + message + "数据");
			message = messaGrab.substring(0, messaGrab.indexOf(","));
			this.grabFocus(message);
			return false;
		}
		return true;
	}

	/**
	 * 撤销校验方法
	 */
	private String concelComm() {
		messaGrab=new StringBuffer();
		StringBuffer message = new StringBuffer();
		for (int i = 0; i < comm.length; i++) {
			if (this.getValue(comm[i]).toString().length() <= 0) {
				message.append(this.getTip(comm[i]) + ",");
				messaGrab.append(comm[i]+",");
			}
		}
		return message.toString();
	}

	/**
	 * 获得就医类别
	 * 
	 * @return
	 */
	private int getType() {
		int insType = 0;
		if (this.getValue("INS_READ_TYPE").equals("1")
				&& this.getValue("INS_PAT_TYPE").equals("1")) {
			insType = 1;

			// 城职门特
		} else if (this.getValue("INS_READ_TYPE").equals("1")
				&& this.getValue("INS_PAT_TYPE").equals("2")) {
			insType = 2;

		} else if (this.getValue("INS_READ_TYPE").equals("2")
				&& this.getValue("INS_PAT_TYPE").equals("2")) {
			insType = 3;

		}
		return insType;
	}

	/**
	 * INS_CROWD_TYPE combox 校验
	 */
	public void onSelType() {
		if (this.getValueInt("INS_READ_TYPE") == 1) {	
			this.setValue("INS_PAT_TYPE", 1);
			callFunction("UI|INS_PAT_TYPE|setEnabled", true);
		} else if (this.getValueInt("INS_READ_TYPE") == 2) {
			this.setValue("INS_PAT_TYPE", 2);
			callFunction("UI|INS_PAT_TYPE|setEnabled", false);
		}
	}
	/**
	 * 退费操作
	 */
	public void onReSetFee() {
		if (!getCheck("门诊退费")) {
			return;
		}
		if (this.messageBox("提示","是否确认执行",2)!=0) {
			return;
		}
		int type = getType();
		TParm parm = new TParm();
		parm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO"));
		parm.setData("REGION_CODE", this.getValue("NHI_REGION_CODE"));
		parm.setData("OPT_USER", Operator.getID());
		TParm opdParm = INSTJFlow.getInstance().selectResetFee(parm);
		if (opdParm.getErrCode()<0) {
			this.messageBox("E0005");
			return;
		}
		if (null==opdParm.getValue("CONFIRM_NO")) {
			this.messageBox("没有查询到相关数据");
			return;
		}
		TParm result = new TParm();
		TParm resetInsOPDParm = null;
		StringBuffer message=new StringBuffer();
		int flg = 0;
		switch (type) {
		case 1:
			resetInsOPDParm = INSTJFlow.getInstance().resetFeeChZPt(parm,
					opdParm);
			if (!INSTJTool.getInstance().getErrParm(resetInsOPDParm)) {
				this.setValue("TEXT", resetInsOPDParm.getErrText());
				return;
			}
			message.append("DataDown_yb_C:").append(resetInsOPDParm.getValue("PROGRAM_MESSAGE")+"\n");
			flg = resetInsOPDParm.getInt("INSAMT_FLG");
			if (flg == 1) {
				// 退费确认交易
				result = INSTJFlow.getInstance().resetFeeInsSettleChZPt(
						opdParm, resetInsOPDParm);
				if (!INSTJTool.getInstance().getErrParm(result)) {
					this.setValue("TEXT", result.getErrText());
					return;
				}
			}
			message.append("DataDown_yb_D:").append(result.getValue("PROGRAM_MESSAGE")+"\n");
			break;
		case 2:
			resetInsOPDParm = INSTJFlow.getInstance().resetFeeChZMt(parm,
					opdParm);
			if (!INSTJTool.getInstance().getErrParm(resetInsOPDParm)) {
				this.setValue("TEXT", resetInsOPDParm.getErrText());
				return;
			}
			message.append("DataDown_mts_K:").append(resetInsOPDParm.getValue("PROGRAM_MESSAGE")+"\n");
			flg = resetInsOPDParm.getInt("SOCIAL_FLG");
			if (flg == 1) {
				// 退费确认交易
				result = INSTJFlow.getInstance()
						.resetFeeInsSettleChZMt(opdParm);
				if (!INSTJTool.getInstance().getErrParm(result)) {
					this.setValue("TEXT", result.getErrText());
					return;
				}
			}
			message.append("DataDown_mts_L:").append(result.getValue("PROGRAM_MESSAGE")+"\n");
			break;
		case 3:
			// 门诊退费
			resetInsOPDParm = INSTJFlow.getInstance().resetFeeChJMt(parm,
					opdParm);
			if (!INSTJTool.getInstance().getErrParm(resetInsOPDParm)) {
				this.setValue("TEXT", resetInsOPDParm.getErrText());
				return;
			}
			message.append("DataDown_cmts_K:").append(resetInsOPDParm.getValue("PROGRAM_MESSAGE")+"\n");
			if (flg == 1) {
				// 退费确认交易
				result = INSTJFlow.getInstance()
						.resetFeeInsSettleChJMt(opdParm);
				if (!INSTJTool.getInstance().getErrParm(result)) {
					this.setValue("TEXT", result.getErrText());
					return;
				}
			}
			message.append("DataDown_cmts_L:").append(result.getValue("PROGRAM_MESSAGE")+"\n");
			break;
		}
		this.setValue("TEXT", message.toString());
	}
	/**
	 * 获得就诊号码
	 */
	public void onGetConfirm(){
		String confirmNo = (String) this.openDialog(
				"%ROOT%\\config\\ins\\INSChooseVisit.x", new TParm());
		this.setValue("CONFIRM_NO", confirmNo);
		
	}
	/**
	 * DataDown_yb (D)
	 */
	public void getDataDown_yb(){
		// 退费确认交易
		TParm opdParm=new TParm();
		opdParm.setData("CONFIRM_NO",this.getValue("CONFIRM_NO"));
		opdParm.setData("NHI_REGION_CODE",this.getValue("NHI_REGION_CODE"));
		opdParm.setData("ACCOUNT_PAY_AMT",this.getValue("ACCOUNT_PAY_AMT"));
		TParm resetInsOPDParm=new TParm();
		resetInsOPDParm.setData("OTOT_AMT",this.getValueDouble("OTOT_AMT"));
		TParm result = INSTJFlow.getInstance().resetFeeInsSettleChZPt(
				opdParm, resetInsOPDParm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			this.setValue("TEXT", "DataDown_yb_D:"+result.getErrText());
			return;
		}
	}
	/**
	 * 城职城居退费确认操作
	 */
	public void getDataDown_mtsAndcmts_L(){
		// 退费确认交易
		TParm opdParm=new TParm();
		opdParm.setData("CONFIRM_NO",this.getValue("CONFIRM_NO"));
		opdParm.setData("NHI_REGION_CODE",this.getValue("NHI_REGION_CODE"));
		opdParm.setData("ACCOUNT_PAY_AMT",this.getValueDouble("ACCOUNT_PAY_AMT"));
		opdParm.setData("TOTAL_AGENT_AMT",this.getValueDouble("TOTAL_AGENT_AMT"));
		opdParm.setData("FLG_AGENT_AMT",this.getValueDouble("FLG_AGENT_AMT"));
		if (this.getValueInt("INS_READ_TYPE")==1) {
			// 退费确认交易
			TParm result = INSTJFlow.getInstance()
					.resetFeeInsSettleChZMt(opdParm);
			if (!INSTJTool.getInstance().getErrParm(result)) {
				this.setValue("TEXT", "DataDown_mts_L:"+result.getErrText());
				return;
			}
		}else{
			// 退费确认交易
			TParm result = INSTJFlow.getInstance()
					.resetFeeInsSettleChJMt(opdParm);
			if (!INSTJTool.getInstance().getErrParm(result)) {
				this.setValue("TEXT", "DataDown_cmts_L:"+result.getErrText());
				return;
			}
		}
		
	} 
	/**
	 * 资格确认书撤销操作
	 */
	public void onConcelConfirm(){
		if (!getCheck("资格确认书撤销")) {
			return;
		}
		if (this.messageBox("提示","是否确认执行",2)!=0) {
			return;
		}
		//int type = getType();
		TParm parm = new TParm();
		//parm.setData("INS_TYPE", getType());
		parm.setData("HOSP_NHI_NO", this.getValue("NHI_REGION_CODE"));
		TParm concelParm = null;
		StringBuffer message=new StringBuffer();
		if (this.getValueInt("INS_READ_TYPE") == 1) {// 1 城职 2.城居
			parm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO"));
			concelParm = INSTJTool.getInstance().DataDown_sp_C(parm);
			message.append("DataDown_sp_C:").append(concelParm.getValue("PROGRAM_MESSAGE")+"\n");

		} else if (this.getValueInt("INS_READ_TYPE") == 2) {// 1 城职 2.城居
			parm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO"));
			concelParm = INSTJTool.getInstance().DataDown_czys_F(parm);
			message.append("DataDown_czys_F:").append(concelParm.getValue("PROGRAM_MESSAGE")+"\n");

		}
		this.setValue("TEXT", message.toString());
				
	}
}
