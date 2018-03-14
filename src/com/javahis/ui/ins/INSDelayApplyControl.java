package com.javahis.ui.ins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.ins.INSTJTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;

/**
 * <p>
 * Title: 延迟申报
 * </p>
 * 
 * <p>
 * Description: 资格确认书 延迟申报
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 20111214
 * @version 1.0
 */
public class INSDelayApplyControl extends TControl {

	private String ownNo;// 个人编码
	private String inDate;// 入院时间
	private String regionCode;// 医院编码
	private String diagDesc;// 入院诊断

	public void onInit() {
		super.onInit();
		// 得到前台传来的数据并显示在界面上
		TParm parm = (TParm) getParameter();
		ownNo = parm.getValue("OWN_NO");
		inDate = parm.getValue("IN_DATE");
		regionCode = parm.getValue("REGION_CODE");
		diagDesc = parm.getValue("DIAG_DESC");
		((TComboBox) this.getComponent("COM_TYPE")).setSelectedID("1");
	}

	// public void onSave() {
	//
	// TParm result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
	// "comm", new TParm());
	// if (result.getErrCode() < 0) {
	// this.messageBox("添加失败");
	// }else{
	// this.messageBox("添加成功");
	// }
	// }
	/**
	 * 确定
	 */
	public void onOK() {
		if (this.getValue("DELAY_APP").toString().length() <= 0) {
			this.messageBox("请输入延迟申请信息");
			return;
		}
		if (this.getValue("DELAY_APP").toString().length() > 400) {
			this.messageBox("原因过长，最多200字!");
			return;
		}
		TParm parm = new TParm();
		parm.setData("PERSONAL_NO", ownNo);
		parm.setData("HOSPITAL_SIS", diagDesc);
		parm.setData("HOSPITAL_DATE", inDate.replace("/", ""));
		parm.setData("HOSPITAL_NO", regionCode);
		parm.setData("DELAY_REASON", this.getValue("DELAY_APP"));// 延迟信息
		parm.setData("INSURANCE_TYPE", this.getValue("COM_TYPE"));// 界面combox
		TParm result=INSTJTool.getInstance().DataDown_yb_I(parm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			this.messageBox("延迟申报失败");
			return;
		}else{
			this.messageBox("P0001");
		}
		//添加调用接口方法
		this.callFunction("UI|onClose");
	}
}
