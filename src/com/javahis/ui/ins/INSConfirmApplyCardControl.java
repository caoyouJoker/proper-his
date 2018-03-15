package com.javahis.ui.ins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jdo.ins.INSTJAdm;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJReg;
import jdo.ins.INSTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import manager.InsManager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.FileTool;

/**
 * 
 * <p>
 * Title:医保卡刷卡动作
 * </p>
 * 
 * <p>
 * Description:医保卡刷卡动作
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangb 2011-12-05
 * @version 2.0
 */
public class INSConfirmApplyCardControl extends TControl {
	private TParm readParm = new TParm();// 刷卡出参
	// private String case_no;// 就诊号
	private String mr_no;// 病患号码
	int card_Type = 0;// 读卡请求类型（1：购卡，2：挂号，3：收费，4：住院，5：门特登记）
	TParm regionParm = null;// 获得医保区域代码
	private String advancecode;//医院编码@费用发生时间@类别
	private String advancepersonalno;//个人编码（无卡人员）
	private String advancetype;//延迟垫付
	

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
		// case_no = parm.getValue("CASE_NO");
		mr_no = parm.getValue("MR_NO");
		advancecode = parm.getValue("ADVANCE_CODE");//医院编码@费用发生时间@类别
		card_Type = parm.getInt("CARD_TYPE");
		int insType=parm.getInt("INS_TYPE");//门诊收费医保挂号类别带入
		advancepersonalno = parm.getValue("ADVANCE_PERSONAL_NO");//个人编码（无卡人员）
//		System.out.println("advancepersonalno=====:"+advancepersonalno);
		advancetype = parm.getValue("ADVANCE_TYPE");//延迟垫付
		callFunction("UI|PASSWORD|setEnabled", false);//密码不可编辑
		onExeEnable(false);
		switch (card_Type) {
		case 3://门诊收费
			callFunction("UI|INS_READ_TYPE|setEnabled", false);
			callFunction("UI|INS_PAT_TYPE|setEnabled", false);
			if (insType==1) {
				this.setValue("INS_READ_TYPE", "1");// 1.城职2.城居
				this.setValue("INS_PAT_TYPE", "1");// 1.普通2.门特
			}else if(insType==2){
				this.setValue("INS_READ_TYPE", "1");// 1.城职2.城居
				this.setValue("INS_PAT_TYPE", "2");// 1.普通2.门特
			}else if(insType==3){
				this.setValue("INS_READ_TYPE", "2");// 1.城职2.城居
				this.setValue("INS_PAT_TYPE", "2");// 1.普通2.门特
			}
			break;
		case 4:// 住院
			callFunction("UI|INS_PAT_TYPE|setEnabled", true);
			this.setValue("INS_PAT_TYPE", "");// 1.普通2.门特
			break;
		case 5:// 门特登记
			this.setValue("INS_READ_TYPE", parm.getValue("INS_CROWD_TYPE"));// 1.城职2.城居
			callFunction("UI|INS_PAT_TYPE|setEnabled", false);
			this.setValue("INS_PAT_TYPE", 2);// 1.普通2.门特
			break;
		}
		if(advancetype.equals("2")&&!advancepersonalno.equals("")){			
			callFunction("UI|tButton_2|setEnabled", false);//读卡不可用
			this.setValue("INS_READ_TYPE", "1");//城职
			this.setValue("INS_PAT_TYPE", "1");//普通		
		}
		this.setValue("PASSWORD", "111111");//默认密码
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
		parm.setData("ADVANCE_CODE",advancecode);//医院编码@费用发生时间@类别
		readParm = new TParm(INSTJAdm.getInstance().getOwnInfo(parm.getData()));
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
		// 执行
		// U
		// 方法和
		// A
		// 方法
		if (readParm.getErrCode() < 0) {
			this.messageBox(readParm.getErrText());
			return;
		}
		this.setValue("NHI_NO", readParm.getValue("CARD_NO"));
		String insReadType = readParm.getValue("CROWD_TYPE");// 人群类别
		this.setValue("INS_READ_TYPE", insReadType);
		this.grabFocus("tButton_0");//确定获得焦点
		if (card_Type == 5 || card_Type == 4 || card_Type==3) {// 门特登记// 住院
			return ;
		}
		// 1.城职 2.城居
		TComboBox com = (TComboBox) this.getComponent("INS_PAT_TYPE");
		if (this.getValueString("INS_READ_TYPE").equals("1")) {
			callFunction("UI|INS_PAT_TYPE|setEnabled", true);
			com.setSelectedIndex(1);// 普通默认
		} else if (this.getValueString("INS_READ_TYPE").equals("2")) {
			callFunction("UI|INS_PAT_TYPE|setEnabled", false);// 第二个combo 不可以选择
			com.setSelectedIndex(2);// 门特默认
		}
		
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
	 * 医保卡操作
	 */
	private void onReadCard(){
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
		parm.setData("ADVANCE_CODE",advancecode);//医院编码@费用发生时间@类别
		readParm = new TParm(INSTJReg.getInstance().readINSCard(parm.getData()));// 读卡动作
	}
	/**
	 * 确定按钮
	 */
	public void onOK() {
		if(advancetype.equals("1")||
		  (advancetype.equals("2")&&
		   advancepersonalno.equals(""))){	
		if (null == readParm || readParm.getErrCode() < 0
				|| null == readParm.getValue("CARD_NO")
				|| readParm.getValue("CARD_NO").length() <= 0) {
			this.messageBox("请执行读卡动作");
			return;
		}
		readParm.setData("RETURN_TYPE", 1);// 返回执行状态
		if (this.getValueInt("INS_PAT_TYPE")==2) {
			if (null==this.getValue("DISEASE_CODE") || this.getValue("DISEASE_CODE").toString().length()<=0) {//门特病种不可以为空
				this.messageBox("门特病种不可以为空");
				this.grabFocus("DISEASE_CODE");
				return;
			}
			readParm.setData("DISEASE_CODE",this.getValue("DISEASE_CODE"));
		}
		if (card_Type == 5 || card_Type == 4) {// 门特登记和住院
			this.setReturnValue(readParm);
			this.closeWindow();
			return;
		}
		if (this.getRadioButton("READ_CARD").isSelected()) {// 读卡
			if (!this.emptyTextCheck("INS_READ_TYPE,INS_PAT_TYPE,PASSWORD")) {
				return;
			}
		}else if (this.getRadioButton("READ_IDNO").isSelected()) {// 身份证
			if (!this.emptyTextCheck("IDNO,PASSWORD")) {
				return;
			}
		}
	}
		readParm.setData("PASSWORD",this.getValue("PASSWORD"));//密码
		if(advancetype.equals("2")&&!advancepersonalno.equals("")){				
		readParm.setData("ADVANCE_CODE",advancecode);//医院编码@费用发生时间@类别
		readParm.setData("PERSONAL_NO",advancepersonalno);//个人编码（无卡人员）
		readParm.setData("CHECK_CODES", "b"); // 刷卡验证码
		readParm.setData("RETURN_TYPE", 1);// 返回执行状态
		}
		//System.out.println("readParm::::"+readParm);
		//System.out.println("执行");
		// 城职普通
		int insType = 0;
		TParm testParm = new TParm();
		if (this.getValue("INS_READ_TYPE").equals("1")
				&& this.getValue("INS_PAT_TYPE").equals("1")) {
			insType = 1;
			//System.out.println("城职普通");
			testParm = INSTJFlow.getInstance().insIdentificationChZPt(readParm);
			String ctzCode=testParm.getValue("CTZ_CODE");
			TParm ctzParm=sysCtzParm(this.getValueInt("INS_READ_TYPE"),ctzCode);
			if (ctzParm.getErrCode()<0) {
				this.messageBox("E0005");
				return ;
			}
			readParm.setData("CTZ_CODE",ctzParm.getValue("CTZ_CODE",0));//人员类别
			if (testParm.getErrCode() < 0) {
				this.messageBox(testParm.getErrText());
				readParm.setData("RETURN_TYPE", 0);// 返回执行状态
				return;
			}
			// 城职门特
		} else if (this.getValue("INS_READ_TYPE").equals("1")
				&& this.getValue("INS_PAT_TYPE").equals("2")) {
			insType = 2;
			//System.out.println("城职门特");
			readParm.setData("PAY_KIND", 13);
			// 城职门特刷卡返回参数,得到个人信息
			testParm = INSTJFlow.getInstance().insCreditCardChZMt(readParm);
			//System.out.println("testParm:"+testParm);
			if (testParm.getErrCode() < 0) {
				this.messageBox(testParm.getErrText());
				readParm.setData("RETURN_TYPE", 0);// 返回执行状态
				return;
			}
			String ctzCode=testParm.getValue("PAT_TYPE");
			TParm ctzParm=sysCtzParm(this.getValueInt("INS_READ_TYPE"),ctzCode);
			if (ctzParm.getErrCode()<0) {
				this.messageBox("E0005");
				return ;
			}
			readParm.setData("CTZ_CODE",ctzParm.getValue("CTZ_CODE",0));
		} else if (this.getValue("INS_READ_TYPE").equals("2")
				&& this.getValue("INS_PAT_TYPE").equals("2")) {
			insType = 3;
			//System.out.println("城居门特");
			readParm.setData("PAY_KIND", 41);
			// 城居门特刷卡返回参数,得到个人信息
			testParm = INSTJFlow.getInstance().insCreditCardChJMt(readParm);
			
			if (testParm.getErrCode() < 0) {
				this.messageBox(testParm.getErrText());
				readParm.setData("RETURN_TYPE", 0);// 返回执行状态
				return;
			}
			String ctzCode=testParm.getValue("PAT_TYPE");
			TParm ctzParm=sysCtzParm(this.getValueInt("INS_READ_TYPE"),ctzCode);
			if (ctzParm.getErrCode()<0) {
				this.messageBox("E0005");
				return ;
			}
			readParm.setData("CTZ_CODE",ctzParm.getValue("CTZ_CODE",0));
		}
		//System.out.println("刷卡返回参数,得到个人信息::"+testParm);
		if (null == testParm || null == testParm.getValue("CONFIRM_NO")) {
			this.messageBox("执行刷卡动作失败,没有获得数据");
			return;
		}
		testParm.setData("BED_FEE",regionParm.getValue("TOP_BEDFEE",0));//床位费
		testParm.setData("REGION_CODE",regionParm.getValue("NHI_NO", 0));// 医保区域代码
		readParm.setData("opbReadCardParm", testParm.getData());// 资格确认书出参
		readParm.setData("CONFIRM_NO", testParm.getValue("CONFIRM_NO"));// 门特就医顺序号
		readParm.setData("INS_TYPE", insType);
		readParm.setData("RETURN_TYPE", 1);// 返回数据 1.成功 2.失败
		readParm.setData("INS_PAT_TYPE", this.getValue("INS_PAT_TYPE"));// 1.普通
		readParm.setData("DISEASE_CODE", this.getValue("DISEASE_CODE"));//门特病种
		// 2.门特
		readParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));// 医保区域代码
		//用于垫付延迟无卡患者
		if(advancetype.equals("2")&&!advancepersonalno.equals("")){
		readParm.setData("CROWD_TYPE", "1");// 1.城职
		}
		this.setReturnValue(readParm);
		this.closeWindow();
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
		if (flg) {
			this.grabFocus("IDNO");
		}else{
			this.grabFocus("READ_TEXT");
		}
		//callFunction("UI|PASSWORD|setEnabled", flg ? false : true);// 密码
	}
	/**
	 * 单选按钮事件
	 */
	public void onExeType() {
		if (this.getRadioButton("READ_CARD").isSelected()) {// 读卡
			onExeEnable(false);
		} else if (this.getRadioButton("READ_IDNO").isSelected()) {// 身份证
			onExeEnable(true);
		}
		String[] name = { "IDNO", "PAT_NAME", "READ_TEXT","DISEASE_CODE","NHI_NO" };//设置初始值
		for (int i = 0; i < name.length; i++) {
			this.setValue(name[i], "");
		}
	}
	private TParm sysCtzParm(int type,String ctzCode){
		String ctzSql="SELECT CTZ_CODE FROM SYS_CTZ WHERE NHI_NO='"+ctzCode+"' AND NHI_CTZ_FLG='Y'";
		if (type==1) {
			ctzSql+=" AND INS_CROWD_TYPE = '1'";
		}
		if (type==2) {
			ctzSql+=" AND INS_CROWD_TYPE = '2'";
		}
		TParm ctzParm=new TParm(TJDODBTool.getInstance().select(ctzSql));
		if (ctzParm.getErrCode()<0) {
			//this.messageBox("E0005");
			return ctzParm;
		}
		return ctzParm;
	}
}
