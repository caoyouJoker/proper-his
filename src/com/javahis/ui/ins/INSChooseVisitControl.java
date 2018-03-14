package com.javahis.ui.ins;

import java.sql.Timestamp;

import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSTJFlow;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * 
 * Title: 门诊对账
 * 
 * Description:门诊对账：查询病患信息
 * 
 * Copyright: BlueCore (c) 2011
 * 
 * @author pangben 2012-1-8
 * @version 2.0
 */
public class INSChooseVisitControl extends TControl {
	TParm regionParm;// 医保区域代码
	int selectrow = -1;
	TTable table;

	/**
	 * 初始化界面
	 */
	public void onInit() {
		super.onInit();
		// 得到前台传来的数据并显示在界面上
		// TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		// cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
		// this.getValueString("REGION_CODE")));
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// 获得医保区域代码
		this.setValue("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));
		//TParm recptype = (TParm) getParameter();
		// 预设就诊时间段
		this.callFunction("UI|STARTTIME|setValue", SystemTool.getInstance()
				.getDate());
		this.callFunction("UI|ENDTIME|setValue", SystemTool.getInstance()
				.getDate());
		table = (TTable) this.getComponent("TABLE");
		// // table1的单击侦听事件
		// callFunction("UI|TABLE|addEventListener", "TABLE->"
		// + TTableEvent.CLICKED, this, "onTableClicked");
		// 默认Table上显示当天挂号记录
		// onQuery();
	}

	// /**
	// *增加对Table的监听
	// *
	// * @param row
	// * int
	// */
	// public void onTableClicked(int row) {
	// // 接收所有事件
	// this.callFunction("UI|TABLE|acceptText");
	// // TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
	// selectrow = row;
	// }

	// public void onTableDoubleClicked(int row) {
	// TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
	// this.setReturnValue((String) data.getData("CONFIRM_NO", row));
	// this.callFunction("UI|onClose");
	// }

	/**
	 * 查询病患信息
	 */
	public void onQuery() {
		TParm parm = new TParm();
		if (this.getValueString("HOSP_NHI_NO").length()<=0) {
			this.messageBox("医保区域不可以为空");
			this.grabFocus("HOSP_NHI_NO");
			return;
		}
		if (this.getValue("MR_NO").toString().length() > 0) {
			parm.setData("MR_NO", this.getValue("MR_NO"));
		}
		if (this.getValueString("PAT_NAME").length() > 0) {
			parm.setData("PAT_NAME", this.getValue("PAT_NAME"));
		}
		if (this.getValueString("HOSP_NHI_NO").length() > 0) {// 医保就诊号查询
			parm.setData("HOSP_NHI_NO", this.getValue("HOSP_NHI_NO"));
		}
		if (getValue("STARTTIME") == null
				|| getValueString("STARTTIME").length() <= 0) {
			messageBox("请选择开始时间!");
			return;
		}
		if (getValue("ENDTIME") == null
				|| getValueString("ENDTIME").length() <= 0) {
			messageBox("请选择结束时间!");
			return;
		}
		parm.setData("START_DATE", StringTool.setTime((Timestamp) this
				.getValue("STARTTIME"), "00:00:00"));
		parm.setData("END_DATE", StringTool.setTime((Timestamp) this
				.getValue("ENDTIME"), "23:59:59"));
		parm = INSOpdTJTool.getInstance().selectAutoAccount(parm);
		if (parm.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (parm.getCount() <= 0) {
			this.messageBox("没有查询的数据");
			table.setParmValue(new TParm());
			return;
		}
		table.setParmValue(parm);
	}

	/**
	 * 病案号回车事件
	 */
	public void onQueryMrNo() {
		Pat pat = Pat.onQueryByMrNo(this.getValue("MR_NO").toString());
		if (pat == null) {
			this.messageBox("无此病案号!");
			return;
		}
		this.setValue("MR_NO", pat.getMrNo());
	}

	/**
	 * 病患名称回车事件
	 */
	public void onQueryPatName() {
		TParm sendParm = new TParm();
		sendParm.setData("PAT_NAME", this.getValue("PAT_NAME"));
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMPatQuery.x", sendParm);
		if (reParm == null)
			return;
		this.setValue("PAT_NAME", reParm.getValue("PAT_NAME"));
	}

	/**
    *
    */
	public void onOK() {
		if (this.getValueString("HOSP_NHI_NO").length()<=0) {
			this.messageBox("医保区域不可以为空");
			this.grabFocus("HOSP_NHI_NO");
			return;
		}
		int row=table.getSelectedRow();
		if (row<0) {
			this.messageBox("请选择要执行的数据");
			return;
		}
		if (this.messageBox("提示","是否执行",2)!=0) {
			return;
		}
		
		
		TParm tempParm = table.getParmValue().getRow(row);
		//tempParm.setData("NHI_REGION_CODE", this.getValue("HOSP_NHI_NO"));
		TParm returnParm=null;
		// 获得医保病患信息
//		TParm mzConfirmParm = INSMZConfirmTool.getInstance()
//				.queryMZConfirmOne(tempParm);
//		if (mzConfirmParm.getErrCode()<0 || mzConfirmParm.getCount()<=0) {
//			this.messageBox("获得数据失败");
//			return;
//		}
		int insType=getInsType(tempParm);
		if (tempParm.getValue("RECP_TYPE").equals("REGT")
				|| tempParm.getValue("RECP_TYPE").equals("OPBT")) {
			
			if (insType==-1) {
				this.messageBox("获得数据失败");
				return;
			}
			tempParm.setData("NHI_REGION_CODE",this.getValue("HOSP_NHI_NO"));
			tempParm.setData("REGION_CODE",this.getValue("HOSP_NHI_NO"));
			tempParm.setData("CHOOSE_FLG", "Y");//对账操作
			//TParm parm=INSTJFlow.getInstance().cancelBalance(commFunction(tempParm),insType);
			returnParm = INSTJFlow.getInstance().reSetAutoAccount(tempParm, insType);
			if (returnParm.getErrCode() < 0) {
				this.messageBox(returnParm.getErrText());
			}
		} else {
			// 正流程
			TParm parm=null;
			if (null != tempParm.getValue("INSAMT_FLG")
					&& tempParm.getValue("INSAMT_FLG").length() > 0
					&& tempParm.getInt("INSAMT_FLG") == 1) {// 状态为1
															// 校验是否已经打票，如果已经打票的病患，调用确认接口直接将状态改变3
				String sql = "";
				String tableName = "";

				if (tempParm.getValue("RECP_TYPE").equals("REG")) {// 挂号
					tableName = "BIL_REG_RECP";
				} else if (tempParm.getValue("RECP_TYPE").equals("OPB")) {// 收费
					tableName = "BIL_OPB_RECP";
				}
				sql = "SELECT CASE_NO,PRINT_NO FROM "
						+ tableName
						+ " WHERE CASE_NO='"
						+ tempParm.getValue("CASE_NO")
						+ "' AND AR_AMT>0 AND (RESET_RECEIPT_NO IS NULL OR RESET_RECEIPT_NO='')";
				TParm bilParm = new TParm(TJDODBTool.getInstance().select(sql));
				if (bilParm.getErrCode() < 0 || bilParm.getCount() > 1) {
					this.messageBox("此病患票据有多笔数据,请联系信息中心操作");
					return;
				}
				if (bilParm.getCount() <= 0) {//不存在票据执行撤销医保结算操作
					parm = INSTJFlow.getInstance().cancelBalance(
							commFunction(tempParm), insType);
				} else {
					sql = "SELECT DISEASE_CODE,PAY_KIND,CTZ_CODE AS PAT_TYPE FROM INS_MZ_CONFIRM"
							+ " WHERE CASE_NO='"
							+ tempParm.getValue("CASE_NO")
							+ "' AND CONFIRM_NO='"
							+ tempParm.getValue("CONFIRM_NO") + "'";
					TParm mzConfirmParm = new TParm(TJDODBTool.getInstance()
							.select(sql));//查询病患医保信息
					if (mzConfirmParm.getErrCode() < 0
							|| mzConfirmParm.getCount() > 1) {
						this.messageBox("此病患票据有多笔数据,请联系信息中心操作");
						return;
					}
					tempParm.setData("PRINT_NO",bilParm.getValue("PRINT_NO",0));//=====pangben 2013-8-29 票号添加
					parm = INSTJFlow.getInstance().confirmBalance(
							commFunction(tempParm), mzConfirmParm.getRow(0),
							insType);
				}
			} else {
				parm = INSTJFlow.getInstance().cancelBalance(
						commFunction(tempParm), insType);
			}
//			returnParm = autoAccount(tempParm, insType, mzConfirmParm,
//					buffer);
			if (parm.getErrCode() < 0) {
				this.messageBox(parm.getErrText());
			}
		}
		this.messageBox("操作成功");
		onQuery();
//		TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
//		this.setReturnValue((String) data.getData("CONFIRM_NO", selectrow));
//		this.callFunction("UI|onClose");
	}
	private TParm commFunction(TParm opbReadCardParm) {
		TParm parm = new TParm();
		//parm.setData("INS_TYPE", getType());
		parm.setData("REGION_CODE", this.getValue("HOSP_NHI_NO"));
		parm.setData("RECP_TYPE", opbReadCardParm.getValue("RECP_TYPE"));
		parm.setData("CASE_NO", opbReadCardParm.getValue("CASE_NO"));
		parm.setData("CONFIRM_NO", opbReadCardParm.getValue("CONFIRM_NO"));
		parm.setData("EXE_USER",Operator.getID());//=====pangben 2013-3-13 添加
		parm.setData("EXE_TERM",Operator.getIP());//=====pangben 2013-3-13 添加
		parm.setData("EXE_TYPE", opbReadCardParm.getValue("RECP_TYPE"));//=====pangben 2013-3-13 添加
		parm.setData("opbReadCardParm", opbReadCardParm.getData());
		parm.setData("ACCOUNT_PAY_AMT", opbReadCardParm.getDouble("ACCOUNT_PAY_AMT"));// 帐户支付金额-----可用帐户报销金额-
		parm.setData("INS_PAY_AMT",opbReadCardParm.getDouble("INS_PAY_AMT"));// 医保支付金额
		parm.setData("UACCOUNT_PAY_AMT", opbReadCardParm.getDouble("UNACCOUNT_PAY_AMT")); // 非医保支付
		parm.setData("UNREIM_AMT", opbReadCardParm.getDouble("UNREIM_AMT"));
		parm.setData("INSAMT_FLG", opbReadCardParm.getValue("INSAMT_FLG"));//对账状态
		parm.setData("CHOOSE_FLG", "Y");//对账操作
		parm.setData("PRINT_NO", opbReadCardParm.getValue("PRINT_NO"));//票据号码//pangben 2013-8-29
		return parm;
	}

	/**
	 * 获得就诊类别
	 * 
	 * @return
	 */
	private int getInsType(TParm parm) {
		int insType = -1;
		if (parm.getInt("INS_CROWD_TYPE") == 1
				&& parm.getInt("INS_PAT_TYPE") == 1) {
			insType = 1;// 城职普通
		} else if (parm.getInt("INS_CROWD_TYPE") == 1
				&& parm.getInt("INS_PAT_TYPE") == 2) {
			insType = 2;// 城职门特
		} else if (parm.getInt("INS_CROWD_TYPE") == 2
				&& parm.getInt("INS_PAT_TYPE") == 2) {
			insType = 3;// 城居门特
		}
		return insType;
	}

}
