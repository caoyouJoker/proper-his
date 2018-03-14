//package com.javahis.ui.ins;
//
//public class INSADMTRANSControl {
//
//}
package com.javahis.ui.ins;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import jdo.ins.INSADMConfirmTool;
import jdo.ins.INSTJAdm;
import jdo.ins.INSTJTool;
import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:转外就医登记下载和开立
 * </p>
 * 
 * <p>
 * Description:转外就医登记下载和开立
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) bluecore
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author pangb 2011-11-25
 * @version 2.0
 */
public class INSADMTRANSControl extends TControl {

	private String caseNO;// 就诊号
	private Timestamp Indate;// 住院日期
	private String inStatus;
	DateFormat df = new SimpleDateFormat("yyyyMMdd");
	private TParm regionParm;// 医保区域代码
	private String confirmNo;// 资格确认书号码跨年使用
	// 第二个页签
	private String pageTwo = "REG_CODE;MR_NO;PERSONAL_NO;INS_ADMISSION_HOSPITAL_NO;PAT_NAME;SEX_CODE;PAT_AGE;INS_UNIT;"
		+ "REG_DATE;STRATE_DATE;END_DATE;PAT_TELNO;TRANSIN_HOSP_CODE;TRANSIN_HOSP_NAME;TRANSIN_HOSP_CLASS;"
		+ "TRANSIN_SPECLIST;TRANS_DR_CODE;TRANSOUT_HOSP_CONTACTS;TRANSOUT_HOSP_TELNO;TRANS_REASON;"
		+ "TRANS_DIRECTOR_OPINION;TRANS_HOSP_OPINION;REMARK;REG_TYPE";
	// 第一个页签
//	private String pageOne = "CONFIRM_NO1;RESV_NO;MR_NO;IDNO1;PAT_NAME1;ADM_PRJ1;"
//			+ "ADM_CATEGORY1;SPEDRS_CODE1;DEPT_CODE1;DIAG_DESC1;IN_DATE;"
//			+ "INSBRANCH_CODE1;INSOCC_CODE1;PERSONAL_NO;PRE_CONFIRM_NO;"
//			+ "TRAN_NUM1;GS_CONFIRM_NO;PRE_OWN_AMT;PRE_NHI_AMT;PRE_ADD_AMT;"
//			+ "PRE_OUT_TIME;SPE_DISEASE;OVERINP_FLG1;BEARING_OPERATIONS_TYPE;HOMEDIAG_CODE1";
	// 第三个页签
//	private String pageThree = "REGION_CODE2;ADM_PRJ;ADM_CATEGORY;SPEDRS_CODE;START_STANDARD_AMT;RESTART_STANDARD_AMT;"
//			+ "OWN_RATE;DECREASE_RATE;REALOWN_RATE;INSOWN_RATE;INSCASE_NO;STATION_DESC;BED_NO;"
//			+ "TRANHOSP_RESTANDARD_AMT;DEPT_CODE;OVERINP_FLG;DEPT_DESC";
	private TParm insParm ;// 刷卡集合
	private TComboBox REG_TYPE;
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
//		getEnabledIsFalse(pageTwo,false);// 设置状态
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("REG_DATE", date);

//		this.setValue("REGION_CODE1", Operator.getRegion());// 医院编码
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// 获得医保区域代码
//		 System.out.println("regionParm:::"+date);
			REG_TYPE= (TComboBox)this.getComponent("REG_TYPE") ;
			REG_TYPE.setSelectedIndex(0);
		// 初始默认下载状态
		onExeEnable(true);
		callFunction("UI|readCard|setEnabled", false);
		callFunction("UI|readCard|setEnabled", false);
//		getValue("REG_TYPE")
//		this.setValue("INSOCC_CODE1", "1");
//		this.setValue("ADM_PRJ1", "2");
//		this.setValue("ADM_CATEGORY1", "21");
//		this.setValue("INS_ADVANCE_TYPE", "1");//开立类型默认为正常
		// this.setValue("INS_CROWD_TYPE", 1);// 人群类别
	}

	/**
	 * 查询住院未结案
	 */
	public void onAdmNClose() {
		TParm parm = new TParm();
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSAdmTransNClose.x", parm);

		Indate = result.getTimestamp("IN_DATE");// 住院日期
		caseNO = result.getValue("CASE_NO");//就诊号
		inStatus= result.getValue("IN_STATUS");
//		TParm queryParm = new TParm();
//		queryParm.setData("CASE_NO", caseNO);
//		queryParm = INSADMConfirmTool.getInstance().queryADMConfirm(queryParm);
//		if (queryParm.getErrCode() < 0) {
//			this.messageBox("E0005");
//			return;
//		}CONFIRM_NO
//		System.out.println("resultl:::::::" + result);
		this.setValue("MR_NO", result.getValue("MR_NO"));//
		this.setValue("PAT_NAME", result.getValue("PAT_NAME"));//
		this.setValue("SEX_CODE", result.getValue("SEX_CODE"));
		this.setValue("PAT_AGE", result.getValue("PAT_AGE"));
		this.setValue("INS_UNIT", result.getValue("INS_UNIT"));
		this.setValue("PERSONAL_NO", result.getValue("PERSONAL_NO"));
		this.setValue("INS_ADMISSION_HOSPITAL_NO", result.getValue("CONFIRM_NO"));
		this.setValue("PAT_TELNO", result.getValue("TEL_HOME"));
		
		// System.out.println("resultl:::::::" + result);
//		this.setValueForParm(pageTwo + ";" + pageThree + ";INSCASE_NO",
//				queryParm.getRow(0));
//		this.setValue("DIAG_DESC1", result.getValue("DIAG_CODE")
//				+ result.getValue("ICD_CHN_DESC"));// 住院诊断
		// this.setValue("REGION_CODE1", result.getValue("REGION_CODE"));//医院编码
//		setValue("OVERINP_FLG1", "N");
//		callFunction("UI|OVERINP_FLG1|setEnabled", true);
//		this.setValue("ADM_PRJ1", "2");// 就医专案
		// getComboBox("PAY_TYPE").grabFocus();
//		this.grabFocus("ADM_CATEGORY1");// 就医类别
		// getTextField("ADM_CATEGORY1").grabFocus();
	}

	/**
	 * 验证方法
	 * 
	 * @return
	 */
	private boolean checkSave() {
		if (getRadioButton("RO_Open").isSelected()) {// 开立
			if (!this
					.emptyTextCheck("MR_NO,PERSONAL_NO,INS_ADMISSION_HOSPITAL_NO,REG_DATE,PAT_TELNO,TRANSIN_HOSP_NAME,"+
							"TRANSIN_HOSP_CLASS,TRANSOUT_HOSP_CONTACTS,TRANSOUT_HOSP_TELNO,TRANS_REASON")) {
				return false;
			}
	        if (getValueString("PERSONAL_NO").length() <= 0) {
				this.messageBox("请执行刷卡操作");
				return false;
		    }
	        if(!(inStatus.equals("2")||inStatus.equals("4"))){
	        	this.messageBox("医保上传后方可开立");
				return false;
	        }
//			if (this.getValue("REG_DATE").
//					equals(SystemTool.getInstance().getDate().toString().substring(0, 10))) {// 分中心
//				this.messageBox("申请日期不能小于");
//				this.grabFocus("INSBRANCH_CODE1");
//				return false;
//			}
		} else {// 下载
			if (!this
					.emptyTextCheck("REG_CODE,MR_NO")) {// 资格确认书编号
				return false;
			}
		}

		return true;
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
	 * 下载/开立方法
	 */
	public void onSave() {
		// if (null==insParm ||null==insParm.getValue("SID") ||
		// insParm.getValue("SID").length()<=0) {
		// this.messageBox("请执行刷卡操作");
		// return;
		// }
		if (!checkSave()) {
			return;
		}

		TParm result = null;
		if (getRadioButton("RO_Open").isSelected()) {// 开立		
			result = onSaveOpen();
		} else {// 下载
			result = onSaveDown();
		}
		if (null == result) {
			return;
		}
		if (result.getErrCode() < 0) {
			this.messageBox("E0001");// 执行失败
			return;
		}
//		if (getRadioButton("RO_Upd").isSelected()) {
//			TParm queryParm = new TParm();
//			queryParm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO1"));// 资格确认书号码
//			queryParm = INSADMConfirmTool.getInstance().queryADMConfirm(
//					queryParm);
//			if (queryParm.getErrCode() < 0) {
//				this.messageBox("E0005");
//				return;
//			}
//			// System.out.println("resultl:::::::" + result);
//			this.setValueForParm(pageTwo + ";" + pageThree + ";INSCASE_NO",
//					queryParm.getRow(0));
//			// this.setValueForParm(pageTwoNHI+";"+pageThree,result);
//			this.messageBox("资格确认书下载成功");
//		} else {
//			this.setValueForParm(pageTwo + ";" + pageThree + ";INSCASE_NO",
//					result.getRow(0));
//			this.messageBox("资格确认书开立成功");
//		}
//		getTabbedPane("tTabbedPane_1").setSelectedIndex(1);
		// getEnabledIsFalse(pageTwo, false);
	}

	/**
	 * 资格确认书开立数据
	 */
	private TParm onGetSaveDate() {
//		String[] pageTows = pageTwo.split(";");// 第二个页签数据
//		for (int i = 0; i < pageTows.length; i++) {
//			parm.setData(pageTows[i], this.getValue(pageTows[i]));// 获得第二个页签数据	
//		}
//		String day = StringTool.getString(Indate, "yyyy-MM-dd HH:mm:ss");
//        " MR_NO, CASE_NO, REG_CODE, NHIHOSP_NO, PERSONAL_NO, "+ 
//        " PAT_NAME, SEX_CODE, PAT_AGE, INS_UNIT, REG_DATE, "+
//        " STRATE_DATE, END_DATE, PAT_TELNO, TRANSIN_HOSP_CODE, TRANSIN_HOSP_NAME, "+ 
//        " TRANSIN_HOSP_CLASS, TRANSIN_SPECLIST, TRANSOUT_HOSP_CONTACTS, TRANSOUT_HOSP_TELNO, TRANS_REASON, "+ 
//        " TRANS_DR_CODE, TRANS_DIRECTOR_OPINION, TRANS_HOSP_OPINION, REMARK, REG_TYPE, "+
//        " OPT_USER, OPT_DATE, OPT_TERM, INS_ADMISSION_HOSPITAL_NO ) VALUES ( "+

       //System.out.println("IN_DATE============" +day);
		TParm parm = new TParm();
		parm.setData("MR_NO", this.getValue("MR_NO")); // 病案号
		parm.setData("CASE_NO", caseNO);// 就诊号
		parm.setData("REG_CODE", this.getValue("REG_CODE")); // 登记编码
		parm.setData("NHIHOSP_NO", regionParm.getValue("NHI_NO", 0)); // 医院编码
		parm.setData("PERSONAL_NO", this.getValue("PERSONAL_NO")); // 个人编码
		parm.setData("PAT_NAME", this.getValue("PAT_NAME")); // 姓名
		parm.setData("SEX_CODE", this.getValue("SEX_CODE")); // 性别
		parm.setData("PAT_AGE", this.getValue("PAT_AGE")); // 年龄
		parm.setData("INS_UNIT", this.getValue("INS_UNIT")); // 参保人所属社保机构
		parm.setData("REG_DATE", StringUtil.isNullString(this.getValue("REG_DATE").toString())?"":df.format(this.getValue("REG_DATE")));
//		System.out.println("STRATE_DATE----"+this.getValue("STRATE_DATE"));
//		System.out.println("STRATE_DATE----"+this.getValue("STRATE_DATE")==null);
//		System.out.println("STRATE_DATE----"+getValueString("START_DATE"));
//		System.out.println("STRATE_DATE----"+getValueString("START_DATE").equals(""));
		parm.setData("STRATE_DATE", getValueString("START_DATE").equals("")?"":df.format(this.getValue("STRATE_DATE")));
		parm.setData("END_DATE", getValueString("END_DATE").equals("")?"":df.format(this.getValue("END_DATE")));
		parm.setData("PAT_TELNO", this.getValue("PAT_TELNO")); // 患者联系电话
		parm.setData("TRANSIN_HOSP_CODE", getValueString("TRANSIN_HOSP_CODE").equals("")?"0":getValueString("TRANSIN_HOSP_CODE")); //转入医疗机构编码
		parm.setData("TRANSIN_HOSP_NAME", this.getValue("TRANSIN_HOSP_NAME")); // 转入医疗机构名称
		parm.setData("TRANSIN_HOSP_CLASS", this.getValue("TRANSIN_HOSP_CLASS"));//转入医疗机构等级
		parm.setData("TRANSIN_SPECLIST", this.getValueBoolean("TRANSIN_SPECLIST")?"1":"0");//转入医疗机构专科
		parm.setData("TRANSOUT_HOSP_CONTACTS", this.getValue("TRANSOUT_HOSP_CONTACTS"));//转出医疗机构联系人
		parm.setData("TRANSOUT_HOSP_TELNO", this.getValue("TRANSOUT_HOSP_TELNO"));//转出医疗机构联系电话
		parm.setData("TRANS_REASON", this.getValue("TRANS_REASON"));//转诊原因
		parm.setData("TRANS_DR_CODE", this.getValue("TRANS_DR_CODE"));//转诊责任医师编码
		parm.setData("TRANS_DIRECTOR_OPINION", this.getValue("TRANS_DIRECTOR_OPINION"));//转出医院主任医师意见
		parm.setData("TRANS_HOSP_OPINION", this.getValue("TRANS_HOSP_OPINION"));//转出医院意见
		parm.setData("REMARK", this.getValue("REMARK"));//备注
		parm.setData("REG_TYPE", this.getValue("REG_TYPE"));//备注
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("INS_ADMISSION_HOSPITAL_NO", this.getValue("INS_ADMISSION_HOSPITAL_NO")); // 转出同意住院书编号

//		System.out.println("/获得第二个页签数据insParm::" + parm);
		return parm;
	}

	/**
	 * 执行开立操作
	 * 
	 * @return
	 */
	private TParm onSaveOpen() {
//		TParm result = new TParm();
//		TParm queryParm = new TParm();
        TParm resultParm = InsManager.getInstance().safe(onGetUpload(),null);//上传
		if (!INSTJTool.getInstance().getErrParm(resultParm)) {
			this.messageBox(resultParm.getErrText()+ "\n开立失败");
			return null;
		}
        this.setValue("REG_CODE",resultParm.getValue("REG_CODE", 0));
        this.setValue("PAT_NAME",resultParm.getValue("PAT_NAME", 0));
        this.setValue("SEX_CODE",resultParm.getValue("SEX_CODE", 0));
        this.setValue("PAT_AGE",resultParm.getValue("PAT_AGE", 0));
        this.setValue("INS_UNIT",resultParm.getValue("INS_UNIT", 0));
        this.setValue("STRATE_DATE",resultParm.getValue("STRATE_DATE", 0).equals("")?"":resultParm.getValue("STRATE_DATE", 0));
        this.setValue("END_DATE",resultParm.getValue("END_DATE", 0).equals("")?"":resultParm.getValue("END_DATE", 0));
//        TParm insParm = new TParm();
//        this.onGetSaveDate(insParm);//取得保存数据	
  		return this.insert(this.onGetSaveDate());    				
	}

	/**
	 * 查询信息，下载开立使用
	 * 
	 * @param queryParm
	 */
	private void queryAmdConfrim(TParm queryParm) {
//		this.setValueForParm(pageTwo + ";" + pageThree + ";INSCASE_NO",
//				queryParm.getRow(0));
		this.setValueForParm(pageTwo,queryParm);
//		this.setValue("REGION_CODE2", queryParm.getValue("REGION_CODE", 0));
//		getTabbedPane("tTabbedPane_1").setSelectedIndex(1);
	}

	/**
	 * 执行下载操作
	 * 
	 * @return
	 */
	private TParm onSaveDown() {
		// 下载操作
		TParm parm = new TParm();
		parm.addData("REG_CODE", this.getValue("REG_CODE"));// 
		parm.addData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));// 
		parm.setData("PIPELINE", "DataDown_mts") ;
		parm.setData("PLOT_TYPE", "A18") ;
		parm.addData("PARM_COUNT", 2);
	    TParm resultParm = InsManager.getInstance().safe(parm,null);//上传
//	    this.setValue("REG_CODE",resultParm.getValue("REG_CODE", 0));
		if (!INSTJTool.getInstance().getErrParm(resultParm)) {
			this.messageBox(resultParm.getErrText()+ "\n下载失败");
			return null;
		}
//		System.out.println(resultParm);
	    this.onSetUI(resultParm);
//	    TParm insParm = new TParm();
//        this.onGetSaveDate(insParm);//取得保存数据	
        String sql="DELETE FROM INS_TRANS_HOSP A WHERE A.REG_CODE='"+this.getValue("REG_CODE")+"'";
        TJDODBTool.getInstance().update(sql);
		return this.insert(this.onGetSaveDate());
	}
	

	public void onClear() {
		// 头部
//		clearValue("CONFIRM_NO2;INS_ODI_NO;INS_CROWD_TYPE");
		// 第一个页签
//		clearValue(pageOne);
		getRadioButton("RO_Upd").isSelected();
		// 第二个页签
		clearValue(pageTwo);
		// 第三个页签
//		clearValue(pageThree);
		caseNO = null;// 就诊号
		insParm = null;// 刷卡集合
		confirmNo = null;// 资格确认书号码跨年使用
//		callFunction("UI|IDNO1|setEnabled", true);// 可以修改IDNO
//		this.setValue("INSOCC_CODE1", "1");
//		this.setValue("ADM_PRJ1", "2");
//		this.setValue("ADM_CATEGORY1", "21");
//		callFunction("UI|OVERINP_FLG1|setEnabled", true);
//		this.setValue("INS_ADVANCE_TYPE", "1");//开立类型默认为正常
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("REG_DATE", date);
	}

	/**
	 * 刷卡操作
	 */
	public void onReadCard() {
		TParm parm = new TParm();
		if (!this.emptyTextCheck("MR_NO")) {
			return;
		}
		//判断是否正常或是延迟垫付
//		if(this.getValue("INS_ADVANCE_TYPE").equals("")){
//			this.messageBox("开立类型不能为空");
//		    return;
//		}
		// parm.setData("MR_NO", this.getValue("MR_NO"));// 病案号
		// 人群类别
		String opbadvancetype = "1";//收费类别
		String SQL = " SELECT PERSONAL_NO FROM INS_ADVANCE_OUT"+
        " WHERE CASE_NO = '"+ caseNO+ "'" +
        " AND APPROVE_TYPE ='1'" +
        " AND PAY_FLG = '0'";            
        TParm DATA= new TParm(TJDODBTool.getInstance().select(SQL));
		if(this.getValue("INS_ADVANCE_TYPE").equals("2")){
//            System.out.println("DATA=========="+DATA);
            if (DATA.getCount()<= 0) {
    			messageBox("没有延迟垫付患者");
    			return;
    		}
            opbadvancetype = "2";
		}
		else{
			 if (DATA.getCount()> 0) {
				 messageBox("此患者是垫付延迟患者，开立类型为垫付延迟");	
	    		return;
	    	}
		}
		//医院编码@住院时间@类别
		String inDate = StringTool.getString(Indate, "yyyyMMdd");//住院时间
//		 System.out.println("inDate=========="+inDate);
		String advancecode = regionParm.getValue("NHI_NO", 0)+"@"+inDate+"@"+opbadvancetype;
		parm.setData("ADVANCE_CODE",advancecode);//医院编码@住院时间@类别		
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCardOne.x", parm);
		if (null == insParm)
			return;
		int returnType = insParm.getInt("RETURN_TYPE");// 读取状态 1.成功 2.失败
		if (returnType == 0 || returnType == 2) {
			this.messageBox("读取医保卡失败");
			return;
		}
		setParm(insParm, 1);
//		this.grabFocus("ADM_CATEGORY1");
	}

	/**
	 * 执行刷卡 和 查询个人信息赋值
	 * 
	 * @param parm
	 */
	private void setParm(TParm parm, int type) {
		this.setValue("INS_CROWD_TYPE", parm.getValue("CROWD_TYPE"));// 人群类别赋值
		this.setValue("PERSONAL_NO", parm.getValue("PERSONAL_NO"));// 个人编码
		// 根据返回的身份证号码获得病患信息
		parm.setData("IDNO", parm.getValue("SID"));// 身份证号码
		TParm result = PatTool.getInstance().getInfoForIdNo(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// 执行失败
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("不存在身份证号为:" + parm.getValue("SID") + "的信息");
			// onClear();
			parm = null;
			return;
		}
		// 需要校验使用 =================pangben 2012-3-18 以后需要返回
		if (type == 1) {// 刷卡
			if (this.getValue("IDNO1").toString().length() > 0) {
				if (!parm.getValue("SID").equals(this.getValue("IDNO1"))) {
					this.messageBox("刷卡病患信息与住院病患信息不符,\n医保身份证号码为:"
							+ parm.getValue("SID"));
					// onClear();
					parm = null;
					return;
				}
			}
		}
		// 需要校验使用 =================pangben stop
		if (parm.getInt("CROWD_TYPE") == 1) {// 城职
			getIsEnabled(
					"GS_CONFIRM_NO;PRE_OWN_AMT;PRE_ADD_AMT;PRE_NHI_AMT;PRE_CONFIRM_NO;PRE_OUT_TIME",
					true);
			getIsEnabled("BEARING_OPERATIONS_TYPE;HOMEDIAG_CODE1;TRAMA_ATTEST",
					false);
		} else if (parm.getInt("CROWD_TYPE") == 2) {// 城居
			getIsEnabled(
					"GS_CONFIRM_NO;PRE_OWN_AMT;PRE_ADD_AMT;PRE_NHI_AMT;PRE_CONFIRM_NO;PRE_OUT_TIME",
					false);
			getIsEnabled("BEARING_OPERATIONS_TYPE;HOMEDIAG_CODE1;TRAMA_ATTEST",
					true);
		}

		// this.setValue("MR_NO", result.getRow(0).getValue("MR_NO"));// 病案号
		setValueParm(result.getRow(0));
		callFunction("UI|IDNO1|setEnabled", false);// 执行刷卡不可以修改IDNO
	}

	/**
	 * 赋值
	 * 
	 * @param parm
	 */
	private void setValueParm(TParm parm) {
		this.setValue("PAT_NAME", parm.getValue("PAT_NAME"));// 姓名
//		this.setValue("IDNO1", parm.getValue("IDNO"));// 身份证号码
	}

	/**
	 * 设置编辑状态
	 * 
	 * @param name
	 * @param flg
	 */
	private void getIsEnabled(String name, boolean flg) {
		String[] names = name.split(";");
		for (int i = 0; i < names.length; i++) {
			callFunction("UI|" + names[i] + "|setEnabled", flg);
		}
		this.clearValue(name);
	}

	/**
	 * 下载开立单选框选择
	 */
	public void onExe() {
		this.onClear();
		if (this.getRadioButton("RO_Upd").isSelected()) {// 下载
			onExeEnable(true);
			callFunction("UI|readCard|setEnabled", false);
			// this.setValue("INS_CROWD_TYPE", 1);// 人群类别

		} else {// 开立
			onExeEnable(false);
			callFunction("UI|readCard|setEnabled", true);
			this.setValue("INS_CROWD_TYPE", "");
		}
	}

	private void onExeEnable(boolean flg) {
		callFunction("UI|CONFIRM_NO1|setEnabled", flg);// 资格确认书
		callFunction("UI|INS_CROWD_TYPE|setEnabled", flg);// 人群类别
	}

	/**
	 * 查询按钮操作
	 */
//	public void onQueryInsInfo() {
//		TParm queryParm = new TParm();
//		// 资格确认书编号和医保住院编号
//		if (this.getValue("CONFIRM_NO2").toString().length() <= 0
//				&& this.getValue("INSCASE_NO1").toString().length() <= 0) {
//			this.messageBox("请输入查询的条件");
//			this.grabFocus("CONFIRM_NO2");
//			return;
//		}
//		if (this.getValue("CONFIRM_NO2").toString().length() > 0) {
//			queryParm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO2"));// 资格确认书号码
//		}
//		if (this.getValue("INSCASE_NO1").toString().length() > 0) {
//			queryParm.setData("INSCASE_NO", this.getValue("INSCASE_NO1"));// 医保住院编号
//		}
//		// 查询数据
//		queryParm = INSADMConfirmTool.getInstance().queryADMConfirm(queryParm);
//		if (queryParm.getErrCode() < 0) {
//			this.messageBox("E0005");
//			return;
//		}
//		if (queryParm.getCount() <= 0) {
//			this.messageBox("没有需要查询的数据");
//			return;
//		}
//		queryAmdConfrim(queryParm);
//	}
	/**
	 * 住院医保资格确认书历史
	 */
	public void onConfirmNo() {
		TParm parm = new TParm();
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSSearchTransHosp.x", parm);
		queryAmdConfrim(result);
	}
	private TParm onGetUpload() {
//		System.out.println("tabParm:"+tabParm);
		TParm parm = new TParm();
		parm.addData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0)); // 医院编码
		parm.addData("PERSONAL_NO", this.getValue("PERSONAL_NO")); // 个人编码
		parm.addData("INS_ADMISSION_HOSPITAL_NO", this.getValue("INS_ADMISSION_HOSPITAL_NO")); // 转出同意住院书编号
		parm.addData("REG_DATE", df.format(this.getValue("REG_DATE"))); // 登记申请开始日期
		parm.addData("PAT_TELNO", this.getValue("PAT_TELNO")); // 患者联系电话
		parm.addData("TRANSIN_HOSP_CODE", this.getValue("TRANSIN_HOSP_CODE").equals("")?"0":this.getValue("TRANSIN_HOSP_CODE")); //转入医疗机构编码
		parm.addData("TRANSIN_HOSP_NAME", this.getValue("TRANSIN_HOSP_NAME")); // 转入医疗机构名称
		parm.addData("TRANSIN_HOSP_CLASS", this.getValue("TRANSIN_HOSP_CLASS"));//转入医疗机构等级
		parm.addData("TRANSIN_SPECLIST", this.getValueBoolean("TRANSIN_SPECLIST")?"1":"0");//转入医疗机构专科
		parm.addData("TRANSOUT_HOSP_CONTACTS", this.getValue("TRANSOUT_HOSP_CONTACTS"));//转出医疗机构联系人
		parm.addData("TRANSOUT_HOSP_TELNO", this.getValue("TRANSOUT_HOSP_TELNO"));//转出医疗机构联系电话
		parm.addData("TRANS_REASON", this.getValue("TRANS_REASON"));//转诊原因
		parm.addData("TRANS_DR_CODE", this.getValue("TRANS_DR_CODE"));//转诊责任医师编码
		parm.addData("TRANS_DIRECTOR_OPINION", this.getValue("TRANS_DIRECTOR_OPINION"));//转出医院主任医师意见
		parm.addData("TRANS_HOSP_OPINION", this.getValue("TRANS_HOSP_OPINION"));//转出医院意见
		parm.addData("REMARK", this.getValue("REMARK"));//备注
		parm.setData("PIPELINE", "DataDown_mts") ;
		parm.setData("PLOT_TYPE", "A17") ;
		parm.addData("PARM_COUNT", 16);
	
//		System.out.println("parm:"+parm);
		return parm;
	}
	/**
	 * 保存数据
	 */
	public TParm insert(TParm parm) {
//		System.out.println("下载操作:"+parm);
			String sql=
				" INSERT INTO INS_TRANS_HOSP ( "+
		        " MR_NO, CASE_NO, REG_CODE, NHIHOSP_NO, PERSONAL_NO, "+ 
		        " PAT_NAME, SEX_CODE, PAT_AGE, INS_UNIT, REG_DATE, "+
		        " STRATE_DATE, END_DATE, PAT_TELNO, TRANSIN_HOSP_CODE, TRANSIN_HOSP_NAME, "+ 
		        " TRANSIN_HOSP_CLASS, TRANSIN_SPECLIST, TRANSOUT_HOSP_CONTACTS, TRANSOUT_HOSP_TELNO, TRANS_REASON, "+ 
		        " TRANS_DR_CODE, TRANS_DIRECTOR_OPINION, TRANS_HOSP_OPINION, REMARK, REG_TYPE, "+
		        " OPT_USER, OPT_DATE, OPT_TERM, INS_ADMISSION_HOSPITAL_NO ) VALUES ( "+
		        " '"+parm.getValue("MR_NO")+"', '"+parm.getValue("CASE_NO")+"', '"+parm.getValue("REG_CODE")+"', '"+parm.getValue("NHIHOSP_NO")+"', '"+parm.getValue("PERSONAL_NO")+"', "+ 
		        " '"+parm.getValue("PAT_NAME")+"', '"+parm.getValue("SEX_CODE")+"', '"+parm.getValue("PAT_AGE")+"', '"+parm.getValue("INS_UNIT")+"', to_date('"+parm.getValue("REG_DATE")+"','YYYYMMDD'), "+
		        " to_date('"+parm.getValue("STRATE_DATE")+"','YYYYMMDD'), to_date('"+parm.getValue("END_DATE")+"','YYYYMMDD'), '"+parm.getValue("PAT_TELNO")+"', '"+parm.getValue("TRANSIN_HOSP_CODE")+"', '"+parm.getValue("TRANSIN_HOSP_NAME")+"', "+
		        " '"+parm.getValue("TRANSIN_HOSP_CLASS")+"', '"+parm.getValue("TRANSIN_SPECLIST")+"', '"+parm.getValue("TRANSOUT_HOSP_CONTACTS")+"', '"+parm.getValue("TRANSOUT_HOSP_TELNO")+"', '"+parm.getValue("TRANS_REASON")+"', "+ 
		        " '"+parm.getValue("TRANS_DR_CODE")+"', '"+parm.getValue("TRANS_DIRECTOR_OPINION")+"', '"+parm.getValue("TRANS_HOSP_OPINION")+"', '"+parm.getValue("REMARK")+"', '"+parm.getValue("REG_TYPE")+"', "+ 
		        " '"+parm.getValue("OPT_USER")+"', SYSDATE, '"+parm.getValue("OPT_TERM")+"', '"+parm.getValue("INS_ADMISSION_HOSPITAL_NO")+"' ) ";
//			System.out.println("onInsItemRegDown_sql:"+sql);
			return new TParm(TJDODBTool.getInstance().update(sql));
	}
	/**
	 * 设置界面数据
	 * 
	 * @return void
	 */
	private void onSetUI(TParm tabParm) {
		if(tabParm==null){
			return;
		}
		this.setValue("PAT_NAME", tabParm.getValue("PAT_NAME",0));//参保人姓名
		this.setValue("SEX_CODE", tabParm.getValue("SEX_CODE",0));//参保人性别
		this.setValue("PAT_AGE", tabParm.getValue("PAT_AGE",0));//参保人年龄
		this.setValue("INS_UNIT", tabParm.getValue("INS_UNIT",0));//参保人所属社保机构
		this.setValue("INS_ADMISSION_HOSPITAL_NO", tabParm.getValue("INS_ADMISSION_HOSPITAL_NO",0));//转出同意住院书编号
//		String date=tabParm.getValue("REG_DATE",0).substring(0,4)+"-"+
//		tabParm.getValue("REG_DATE",0).substring(4,6)+"-"+
//		tabParm.getValue("REG_DATE",0).substring(6,8)+" 00:00:00.00";
		this.setValue("REG_DATE", this.getUpDateFromat(tabParm.getValue("REG_DATE",0)));//登记申请开始日期
		this.setValue("STRATE_DATE", this.getUpDateFromat(tabParm.getValue("STRATE_DATE",0)));//登记有效开始日期
		this.setValue("END_DATE", this.getUpDateFromat(tabParm.getValue("END_DATE",0)));//登记有效终止日期
		if(!tabParm.getValue("TRANSIN_HOSP_CODE",0).equals("0")){
			this.setValue("TRANSIN_HOSP_CODE", tabParm.getValue("TRANSIN_HOSP_CODE",0));//转入医疗机构编码	
		}
		this.setValue("TRANSIN_HOSP_NAME", tabParm.getValue("TRANSIN_HOSP_NAME",0));//转入医疗机构名称
	    this.setValue("TRANSIN_HOSP_CLASS", tabParm.getValue("TRANSIN_HOSP_CLASS",0));//转入医疗机构等级
		this.setValue("TRANSIN_SPECLIST", tabParm.getValue("TRANSIN_SPECLIST", 0));//转入医疗机构专科
		this.setValue("TRANSOUT_HOSP_CONTACTS", tabParm.getValue("TRANSOUT_HOSP_CONTACTS",0));//转出医疗机构联系人
		this.setValue("TRANSOUT_HOSP_TELNO", tabParm.getValue("TRANSOUT_HOSP_TELNO",0));//转出医疗机构联系电话
		this.setValue("TRANS_REASON", tabParm.getValue("TRANS_REASON",0));//转诊原因
		this.setValue("TRANS_DR_CODE", tabParm.getValue("TRANS_DR_CODE",0));//转诊责任医师编码
		this.setValue("TRANS_DIRECTOR_OPINION", tabParm.getValue("TRANS_DIRECTOR_OPINION",0));//转出医院主任医师意见
		this.setValue("TRANS_HOSP_OPINION", tabParm.getValue("TRANS_HOSP_OPINION",0));//转出医院意见
		this.setValue("REMARK", tabParm.getValue("REMARK",0));//备注
	}
	/**
	 * 转外就医登记撤销
	 * @param parm
	 * @return
	 */
	public void onRevoke() {
		TParm parm = new TParm(); 
		String regCode=this.getValueString("REG_CODE");
		parm.setData("PIPELINE", "DataDown_mts");
		parm.setData("PLOT_TYPE", "A19");
        parm.addData("REG_CODE", regCode);
		parm.addData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));			
		parm.addData("PARM_COUNT", 2);
//		System.out.println("parm:"+parm);
		TParm resultParm = InsManager.getInstance().safe(parm,null);
//		System.out.println("parm:"+parm);
//System.out.println("resultParm:"+resultParm);
        if (!INSTJTool.getInstance().getErrParm(resultParm)) {
	        messageBox(resultParm.getErrText());
	        return;
	    }
		String sql=" UPDATE INS_TRANS_HOSP SET "+
        " REG_TYPE='1', "+
        " OPT_USER='"+Operator.getID()+"',OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"' "+
        " WHERE MR_NO ='"+this.getValueString("MR_NO")+"' AND CASE_NO ='"+caseNO+"' AND REG_CODE='"+regCode+"' ";
//     System.out.println("onRevoke:"+sql);
     TParm result = new TParm(TJDODBTool.getInstance().update(sql));

	}
	  /**
	    * 转外就医登记表
	    */
	   public void onPrint(){
			String Sql = 
				 " SELECT A.*,B.IN_COUNT,B.IDNO,C.CTZ_DESC,D.CHN_DESC SEX_DESC,CASE WHEN A.TRANSIN_HOSP_CLASS='01' THEN '一级' WHEN A.TRANSIN_HOSP_CLASS='02' THEN '二级' WHEN A.TRANSIN_HOSP_CLASS='03' THEN '三级' END HOSP_CLASS "+//E.CHN_DESC HOSP_CLASS " +
				 " FROM INS_TRANS_HOSP A,MRO_RECORD B,SYS_CTZ C,SYS_DICTIONARY D " + //,SYS_DICTIONARY E "+
				 " WHERE A.REG_CODE='"+this.getValue("REG_CODE")+"' "+
				 " AND B.CASE_NO=A.CASE_NO "+
				 " AND C.CTZ_CODE=B.CTZ1_CODE "+
				 " AND D.ID=A.SEX_CODE "+
				 " AND D.GROUP_ID='SYS_SEX' ";
//				 " AND E.ID=A.TRANSIN_HOSP_CLASS "+
//				 " AND E.GROUP_ID='SYS_HOSPITAL_CLASS' ";
//				 System.out.println("regSql==="+Sql);
				TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

				if (tabParm.getCount("REG_CODE") < 0) {
					this.messageBox("没有要查询的数据！");
//					onClear();
					return;
				}
	       
		   TParm printParm =new TParm();
		   printParm.setData("HOSP_NHI_NO", "TEXT",regionParm.getData("NHI_NO", 0).toString()) ;//医院代码
		   printParm.setData("REGION_CHN_DESC","TEXT", regionParm.getData("REGION_CHN_DESC", 0).toString()) ;//医院名称
		   printParm.setData("REG_CODE","TEXT",this.getValue("REG_CODE")) ;//编号
		   printParm.setData("IDNO","TEXT",tabParm.getValue("IDNO", 0)) ;//身份证号
		   printParm.setData("PAT_NAME","TEXT",tabParm.getValue("PAT_NAME", 0)) ;//姓名
		   printParm.setData("SEX_DESC","TEXT",tabParm.getValue("SEX_DESC", 0)) ;//性别
		   printParm.setData("PAT_AGE","TEXT",tabParm.getValue("PAT_AGE", 0)) ;//年龄
		   printParm.setData("PAT_TELNO","TEXT",tabParm.getValue("PAT_TELNO", 0)) ;//手机号码
		   printParm.setData("IN_COUNT","TEXT",tabParm.getValue("IN_COUNT", 0)) ;//第几次住院
		   printParm.setData("CTZ_DESC","TEXT",tabParm.getValue("CTZ_DESC", 0)) ;//参保类别
		   printParm.setData("COMPANY_DESC","TEXT",tabParm.getValue("COMPANY_DESC", 0)) ;//单位名称
		   printParm.setData("COMPANY_CODE","TEXT",tabParm.getValue("COMPANY_CODE", 0)) ;//单位代码
		   printParm.setData("TRANSIN_HOSP_NAME","TEXT",tabParm.getValue("TRANSIN_HOSP_NAME", 0)) ;//转入医院名称
		   printParm.setData("TRANSIN_HOSP_CLASS","TEXT",tabParm.getValue("HOSP_CLASS", 0)) ;//级别
		   printParm.setData("TRANS_REASON","TEXT",tabParm.getValue("TRANS_REASON", 0)) ;//转诊原因
		   printParm.setData("TRANS_DIRECTOR_OPINION","TEXT",tabParm.getValue("TRANS_DIRECTOR_OPINION", 0)) ;//转出医院主任医师意见
		   printParm.setData("TRANS_HOSP_OPINION","TEXT",tabParm.getValue("TRANS_HOSP_OPINION", 0)) ;//转出医院意见
		   
	       this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSTransHosp.jhw",printParm);    	   

	   } 
	   public void transInHospCodeAction(){
		   this.setValue("TRANSIN_HOSP_NAME", this.getText("TRANSIN_HOSP_CODE"));//转入医疗机构名称   
	   }
		private String getUpDateFromat(String str){
			if(str.length()<=0){
				return "";
			}
			return str.substring(0, 4)+"/"+str.substring(4, 6)+"/"+str.substring(6, 8);
		}

}
