package com.javahis.ui.ins;

import java.sql.Timestamp;

import jdo.ins.INSTJAdm;
import jdo.ins.INSTJReg;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.StringTool;

public class INSConfirmApplyCardOneControl extends TControl {
	private TParm readParm = new TParm();// 刷卡出参
	// private String case_no;// 就诊号
	private String mr_no;// 病患号码
	TParm regionParm = null;// 获得医保区域代码
	private String advancecode;//医院编码@费用发生时间@类别

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		regionParm  = SYSRegionTool.getInstance().selectdata(Operator.getRegion());//获得医保区域代码
		TParm parm = (TParm) getParameter();
		if (null == parm) {
			return;
		}
		mr_no=parm.getValue("MR_NO");
		callFunction("UI|INS_PAT_TYPE|setEnabled", false);// 病患就诊类型
		onExeEnable(false);
		this.setValue("INS_PAT_TYPE", "");// 1.普通2.门特
		advancecode = parm.getValue("ADVANCE_CODE");//医院编码@费用发生时间@类别
		callFunction("UI|PASSWORD|setEnabled", false);//密码不可编辑
		this.setValue("PASSWORD", "111111");//默认密码
	}

	/**
	 * 刷卡动作
	 */
	public void readCard() {
		if (this.getRadioButton("READ_CARD").isSelected()) {// 读卡
			onReadCard();
		} else if (this.getRadioButton("READ_IDNO").isSelected()) {// 身份证
			onReadIdNo();
		}
		if (readParm.getErrCode() < 0) {
			this.messageBox(readParm.getErrText());
			return;
		}
		this.setValue("NHI_NO", readParm.getValue("CARD_NO"));
		String insReadType = readParm.getValue("CROWD_TYPE");// 人群类别
		this.setValue("INS_READ_TYPE", insReadType);//1.城职2.城居
		this.grabFocus("tButton_0");//确定获得焦点
	}

	/**
	 * 读卡
	 */
	private void onReadCard() {
		TParm parm = new TParm();
		if (this.getValue("READ_TEXT").toString().length() <= 0) {
			this.messageBox("请刷卡");
			this.grabFocus("READ_TEXT");
			return;
		}
		
		parm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));
		parm.setData("TEXT", this.getValue("READ_TEXT"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("PASSWORD", this.getValueString("PASSWORD"));
		parm.setData("MR_NO", mr_no);
		//医院编码@住院时间@类别
		parm.setData("ADVANCE_CODE",advancecode);
		// 返回参数
		readParm = new TParm(INSTJReg.getInstance().readINSCard(parm.getData()));// 读卡动作
	}
	/**
	 * 根据身份证号获得信息
	 */
	private void onReadIdNo() {
		if (!this.emptyTextCheck("IDNO")) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("IDNO", this.getValue("IDNO"));// 身份证号
		parm.setData("PAT_NAME", this.getValue("PAT_NAME"));// 病患名称
		parm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO",
				0));// 医保区域代码
		//医院编码@住院时间@类别
		parm.setData("ADVANCE_CODE",advancecode);
		readParm = new TParm(INSTJAdm.getInstance().getOwnInfo(parm.getData()));
	}
	/**
	 * 
	 * 确定按钮
	 */
	public void onOK() {

		if (null == readParm || readParm.getErrCode() < 0
				|| null == readParm.getValue("CARD_NO")
				|| readParm.getValue("CARD_NO").length() <= 0) {
			this.messageBox("请执行读卡动作");
			return;
		}
		if (!this.emptyTextCheck("PASSWORD")) {
			return;
		}
		readParm.setData("PASSWORD",this.getValue("PASSWORD"));//密码
		readParm.setData("RETURN_TYPE", 1);// 返回执行状态
		this.setReturnValue(readParm);
		this.closeWindow();
	}

	/**
	 * 单选按钮事件
	 */
	public void onExeType() {
		if (this.getRadioButton("READ_CARD").isSelected()) {// 读卡
			onExeEnable(false);
			this.grabFocus("READ_TEXT");
		} else if (this.getRadioButton("READ_IDNO").isSelected()) {// 身份证
			onExeEnable(true);
			this.grabFocus("IDNO");
		}
		String[] name = { "IDNO", "PAT_NAME", "READ_TEXT"};//设置初始值
		for (int i = 0; i < name.length; i++) {
			this.setValue(name[i], "");
		}

	}

	/**
	 * 可执行操作设置
	 * 
	 * @param flg
	 */
	private void onExeEnable(boolean flg) {
		callFunction("UI|IDNO|setEnabled", flg);// 身份证号码
		callFunction("UI|PAT_NAME|setEnabled", flg);// 病患名称
		callFunction("UI|READ_TEXT|setEnabled", flg ? false : true);// 读卡
		//callFunction("UI|PASSWORD|setEnabled", flg ? false : true);// 密码
	}

	/**
	 * 获得单选控件
	 * 
	 * @param name
	 * @return
	 */
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}

	/**
	 * 通过身份证号码获得病患信息
	 */
	public void onGetInfo() {
		TParm parm = new TParm();
		parm.setData("IDNO", this.getValue("IDNO"));// 身份证号码
		TParm result = PatTool.getInstance().getInfoForIdNo(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// 执行失败
			this.grabFocus("IDNO1");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("不存在病患信息");
			this.grabFocus("IDNO1");
			return;
		}
		// 多用户数据通过选择获得病患信息
		if (result.getCount() > 1) {
			result = (TParm) this.openDialog(
					"%ROOT%\\config\\ins\\INSPatInfo.x", parm);
			if (null == result || null == result.getValue("MR_NO")
					|| result.getValue("MR_NO").length() <= 0) {
				this.setValue("IDNO1", "");
				return;
			}
			this.setValue("MR_NO", result.getValue("MR_NO"));
			this.setValue("PAT_NAME1", result.getValue("PAT_NAME"));
			return;
		}
		this.setValue("MR_NO", result.getValue("MR_NO", 0));
		this.setValue("PAT_NAME1", result.getValue("PAT_NAME", 0));
	}
}
