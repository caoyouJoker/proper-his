package com.javahis.ui.ins;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;
import org.jawin.COMException;
import org.jawin.DispatchPtr;
import org.jawin.Variant.ByrefHolder;
import org.jawin.win32.Ole32;

import jdo.ins.INSTJAdm;
import jdo.ins.INSTJReg;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.ins.INSTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.StringTool;

public class INSConfirmApplyCardOneYDControl extends TControl {
	private TParm readParm = new TParm();// 刷卡出参
	// private String case_no;// 就诊号
	private String mr_no;// 病患号码
	TParm regionParm = null;// 获得医保区域代码
	private String advancecode;//医院编码@费用发生时间@类别
	DispatchPtr app = null;
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
//		 System.out.println("advancecode===============:"+advancecode);
//		try {		
//		    if (app == null){
//		   	Ole32.CoInitialize();
//				app = new DispatchPtr("PB90.n_yhinterface");
//				    }		    
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
	}

	/**
	 * 刷卡动作
	 */
	public void readCard() {
		//调用银海刷卡界面--------------------------------------------begin
		  String ref = new String();
		  ByrefHolder refh = new ByrefHolder("");
		  String data = advancecode;
		  try { 
			  if (app == null){
				  Ole32.CoInitialize();
				app = new DispatchPtr("PB90.n_yhinterface");
			}
				app.invoke("f_sblwsk",data,refh);
//				Ole32.CoUninitialize();
			} catch (COMException e) {
				e.printStackTrace();
			}
		 ref  = ""+ refh.getRef();
		 System.out.println("PB90.n_yhinterface========1=======:"+ref);
		  // 得到医保基本参数
		 TParm sysparm = new TParm();	   
		   sysparm.setData("HOSP_AREA", "HIS");
			TParm sysParm = getTool().getSysParm(sysparm);
		 System.out.println("sysParm===============:"+sysParm);
			if (sysParm.getErrCode() < 0) {
				sysparm.setErr(-1, sysParm.getErrText(), sysParm.getErrName());
				return;
			}
			if (sysParm.getErrCode() > 0) {
				sysparm.setErr(-1, "INS_SYSPARM 表中没有找到默认的医保参数!");
				return;
			}
			// 默认的分割符
			String separator = sysParm.getValue("SEPARATOR", 0);
			// 默认的行结束符
			String newline = initNewline(sysParm.getValue("NEWLINE", 0));
			// 默认的结束符
			String finish = initNewline(sysParm.getValue("FINISH", 0));
			ref = ref.trim();
			if (ref.endsWith(finish)) {
				ref = ref.substring(0, ref.length() - finish.length());
			}
			if (!ref.endsWith(newline))
				ref += newline;
			String[] sData = parseNewLine(ref, newline, false);
			//输出异地病人信息
			this.messageBox(ref);
		    String[] cData = parseNewLine(sData[0], separator, true);		
			readParm.setData("RETURN_TYPE", cData[0]);//程序执行状态
			readParm.setData("SID", cData[2]);//身份证号
			readParm.setData("PAT_NAME", cData[3]);//姓名
			readParm.setData("PAT_AGE", cData[4]);//年龄
			readParm.setData("SEX_CODE", cData[5]);//性别
			readParm.setData("COMPANY_DESC", cData[6]);//单位名称	
			readParm.setData("PERSONAL_NO", cData[7]);//个人编码
			readParm.setData("CROWD_TYPE", cData[8]);//人群类别
			readParm.setData("CHECK_CODES", cData[9]);//刷卡验证码			
			readParm.setData("REGION_CODE", regionParm.getValue("NHI_NO",0));// 医保区域代码
			readParm.setData("ADVANCE_CODE", advancecode);//医院编码@住院时间@类别				
			readParm.setData("OPT_USER", Operator.getID());
			readParm.setData("OPT_TERM", Operator.getIP());
			readParm.setData("MR_NO", mr_no);//病案号
			readParm.setData("CARD_NO", "");//卡号			
			System.out.println("readParm===============:"+readParm);
//		调用银海刷卡界面--------------------------------------------end
		if (readParm.getErrCode() < 0) {
			this.messageBox(readParm.getErrText());
			return;
		}
		this.setValue("NHI_NO", readParm.getValue("CARD_NO"));
		String insReadType = readParm.getValue("CROWD_TYPE");// 人群类别
		this.setValue("INS_READ_TYPE", insReadType);//1.城职2.城居3.异地
		this.grabFocus("PASSWORD");
	}
	/**
	 * 获得数据模型INSTool
	 *
	 * @alias 获得数据模型INSTool
	 * @return INSTool
	 */
	public INSTool getTool() {
		return INSTool.getInstance();
	}
	/**
	 * 初始化换行符
	 *
	 * @param s
	 *            String
	 * @return String
	 */
	public static String initNewline(String s) {
		if (s.startsWith("char(") && s.endsWith(")"))
			return ""
					+ ((char) Integer.parseInt(s.substring(5, s.length() - 1)));
		return s;
	}
	/**
	 * 将数据拆分成多行
	 *
	 * @param s
	 *            String
	 * @param newline
	 *            String
	 * @param b
	 *            boolean
	 * @return String[]
	 */
	private static String[] parseNewLine(String s, String newline, boolean  flg) {
		List list = new ArrayList();
		if (s.startsWith(newline))
		s = s.substring(newline.length(), s.length());
		int index = s.indexOf(newline);
		while (index >= 0) {
			list.add(s.substring(0, index));
			s = s.substring(index + newline.length(), s.length());
			index = s.indexOf(newline);
		}
		if (flg)
			list.add(s);
		return (String[]) list.toArray(new String[] {});
	}
	/**
	 * 
	 * 确定按钮
	 */
	public void onOK() {

//		if (null == readParm || readParm.getErrCode() < 0
//				|| null == readParm.getValue("CARD_NO")
//				|| readParm.getValue("CARD_NO").length() <= 0) {
//			this.messageBox("请执行读卡动作");
//			return;
//		}
//		if (!this.emptyTextCheck("PASSWORD")) {
//			return;
//		}
		readParm.setData("PASSWORD","111111");//密码
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
		String[] name = { "IDNO", "PAT_NAME", "READ_TEXT", "PASSWORD" };//设置初始值
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
