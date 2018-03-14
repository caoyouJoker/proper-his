package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.ins.INSDRQualifyLogTool;
import jdo.sys.SystemTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;

/**
 * 
 * <p>
 * Title:职业医师证书号管理
 * </p>
 * 
 * <p>
 * Description:职业医师证书号管理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author xueyf 2008.09.16
 * @version 1.0
 */
public class INSDRQualifyLogContrl extends TControl {
	int selectRow = -1;

	public void onInit() {
		super.onInit();
		callFunction("UI|TABLETYPE|addEventListener", "TABLETYPE->"
				+ TTableEvent.CLICKED, this, "onTABLETYPEClicked");
		onQuery();
	}

	/**
	 * 增加对TABLETYPE的监听
	 * 
	 * @param row
	 *            int
	 */
	public void onTABLETYPEClicked(int row) {

		if (row < 0)
			return;
		TParm data = (TParm) callFunction("UI|TABLETYPE|getParmValue");
		setValueForParm("USERID;USER_NAME;ID_NO;DR_QUALIFY_CODE_NEW", data, row);
		selectRow = row;
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm parm = new TParm();
		TParm data = new TParm();
		String USERID = (String) ((TTextFormat) getComponent("USERID"))
				.getValue();

		if (USERID == null || USERID.equals("")) {
			data = INSDRQualifyLogTool.getInstance().selectdata(parm);
		} else {
			parm.setData("USERID", USERID);
			data = INSDRQualifyLogTool.getInstance().selectdatabyuser(parm);
		}
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		this.callFunction("UI|TABLETYPE|setParmValue", data);
	}

	/**
	 * 医生列表发生变化后自动查询
	 */
	public void changeUser() {
		TParm parm = new TParm();
		String USERID = ((TTextFormat) getComponent("USERID")).getValue()
				.toString();
		if (USERID.equals("")) {
			clearValue("USERID;USER_NAME;ID_NO;DR_QUALIFY_CODE_NEW");
			return;
		}
		parm.setData("USERID", USERID);
		TParm operatorData = INSDRQualifyLogTool.getInstance().selectOperator(
				parm);
		((TTextField) getComponent("USER_NAME")).setText(operatorData.getValue(
				"USER_NAME", 0));
		((TTextField) getComponent("ID_NO")).setText(operatorData.getValue(
				"ID_NO", 0));
		((TTextField) getComponent("DR_QUALIFY_CODE_NEW")).setText(operatorData
				.getValue("DR_QUALIFY_CODE", 0));
		TParm data = INSDRQualifyLogTool.getInstance().selectdatabyuser(parm);

		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		this.callFunction("UI|TABLETYPE|setParmValue", data);

	}

	/**
	 * 查询
	 */
	public void onQueryOperator() {
		TParm parm = new TParm();
		parm.setData("USERID", getText("USERID"));
		TParm data = INSDRQualifyLogTool.getInstance().selectOperator(parm);

		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}

	}

	/**
	 * 保存
	 */
	public void onSave() {
		String USERID = (String) ((TTextFormat) getComponent("USERID"))
				.getValue();

		if (null == USERID || getText("USERID").equals("")) {
			this.messageBox("输入医生信息有误！");
			return;
		}
		if (null == getText("DR_QUALIFY_CODE_NEW")
				|| getText("DR_QUALIFY_CODE_NEW").equals("")) {
			this.messageBox("职业医师资格证书号不能为空！");
			return;
		}
		TParm actionParm = new TParm();
		actionParm.setData("OPT_USER", Operator.getID());
		actionParm.setData("OPT_TERM", Operator.getIP());
		actionParm.setData("REGION_CODE", Operator.getRegion());
		actionParm.setData("USERID", USERID);
		actionParm.setData("USER_NAME", getText("USER_NAME"));
		actionParm.setData("DR_QUALIFY_CODE_NEW",
				getText("DR_QUALIFY_CODE_NEW"));
		TParm selectParm = INSDRQualifyLogTool.getInstance().selectdatabyuser(
				actionParm);
		if (selectParm.getCount() > 0) {
			actionParm.setData("DR_QUALIFY_CODE_OLD", selectParm.getValue(
					"DR_QUALIFY_CODE_NEW", 0));
		} else {
			actionParm.setData("DR_QUALIFY_CODE_OLD", "");
		}
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSDRQualifyLogAction", "insertdata", actionParm);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		onQuery();
		this.messageBox("P0005");
	}

	/**
	 *清空
	 */
	public void onClear() {
		clearValue("USERID;USER_NAME;ID_NO;DR_QUALIFY_CODE_NEW");
		this.callFunction("UI|TABLETYPE|clearSelection");
		selectRow = -1;

	}

}
