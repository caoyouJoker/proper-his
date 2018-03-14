package com.javahis.ui.odi;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.system.textFormat.TextFormatCLPDuration;

/**
 * <p>
 * Title: 进入时程
 * </p>
 * 
 * <p>
 * Description: 进入时程
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author liling 20140821
 * @version 1.0
 */
public class ODIintoDurationControl extends TControl {
	public ODIintoDurationControl() {

	}

	private TParm sendParm;
	private String case_no = "";
	private String currentDuration;// 当前时程
	private String nextDuration;// 选择时程
	private String clncPathCode;// 临床路径
	String deptFlg;

	/**
	 * 页面初始化方法
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * 初始化页面
	 */
	private void initPage() {
		// System.out.println("页面初始化");
		sendParm = (TParm) this.getParameter();
		case_no = sendParm.getValue("CASE_NO");
		clncPathCode = sendParm.getValue("CLNCPATH_CODE");
		// 清空基础数据begin
		currentDuration = null;
		nextDuration = null;
		this.setValue("currentDuration", "");
		this.setValue("nextDuration", "");
		deptFlg = sendParm.getValue("DEPT_FLG");
		if (null != deptFlg && deptFlg.length() > 0) {
			this.callFunction("UI|LBL_UPDATE|setVisible", true);
		} else {
			this.callFunction("UI|LBL_UPDATE|setVisible", false);
		}
		// 清空基础数据end
		getCurrentDuration();
		initPageData();
	}

	/**
	 * 初始化页面值
	 */
	private void initPageData() {
		if (clncPathCode != null) {
			this.setValue("CLNC_PATHCODE", clncPathCode);
			callFunction("UI|nextDuration|onQuery");
		}
		if (currentDuration != null) {
			this.setValue("currentDuration", currentDuration);
		}
		if (nextDuration != null) {
			this.setValue("nextDuration", nextDuration);
		}
	}

	/**
	 * 得到当前时程
	 */
	private void getCurrentDuration() {
		String sql = "SELECT SCHD_CODE FROM ADM_INP WHERE CASE_NO= '"
				+ this.case_no + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// 得到当前时程
		if (result.getCount("SCHD_CODE") > 0) {
			currentDuration = result.getValue("SCHD_CODE", 0);
			return;
		}
	}

	/**
	 * 进入时程
	 */
	public void inDuration() {
		nextDuration = this.getValueString("nextDuration");
		if (null == nextDuration || nextDuration.length() <= 0) {
			this.messageBox("请选择进入时程");
			return;
		}
		TParm result = new TParm();
		if (null != deptFlg && deptFlg.length() > 0) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", this.case_no);
			parm.setData("SCHD_CODE", this.nextDuration);
			result = TIOM_AppServer.executeAction(
					"action.clp.CLPManagemAction", "onExeInDuration", parm);
		} else {
			String sql = "UPDATE ADM_INP SET SCHD_CODE = '" + nextDuration
					+ "' WHERE CASE_NO= '" + this.case_no + "' ";
			result = new TParm(TJDODBTool.getInstance().update(sql));
		}
		if (result.getErrCode() < 0) {
			this.messageBox("进入失败!");
		} else {
			this.messageBox("进入成功!");
		}
		// 执行后页面初始化
		initPage();
	}

	/**
	 * 临床路径控件触发事件
	 */
	public void onClickClncpathCode() {
		TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this
				.getComponent("nextDuration");
		if (this.getValue("CLNC_PATHCODE").toString().length() > 0) {
			combo_schd.setClncpathCode(this.getValue("CLNC_PATHCODE")
					.toString());
		}
		combo_schd.onQuery();
	}

	/**
	 * 关闭方法
	 */
	public void onWindowClose() {
		if (null != deptFlg && deptFlg.length() > 0) {
			String sql = "UPDATE ADM_TRANS_LOG SET CLP_SCHD_FLG = 'Y' WHERE CASE_NO= '"
	    		+  this.case_no+ "' AND CLP_SCHD_FLG = 'N'";
	    	TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		}
		this.closeWindow();
	}
}
