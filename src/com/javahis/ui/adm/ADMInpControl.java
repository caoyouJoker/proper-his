package com.javahis.ui.adm;

import com.bluecore.cardreader.CardInfoBO;
import com.bluecore.cardreader.IdCardReaderUtil;
import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;

import jdo.sid.IdCardO;
import jdo.spc.SYSPatinfoClientTool;
import jdo.spc.StringUtils;
import jdo.spc.spcPatInfoSyncClient.SpcPatInfoService_SpcPatInfoServiceImplPort_Client;
import jdo.spc.spcPatInfoSyncClient.SysPatinfo;
import jdo.sys.Pat;
import jdo.adm.ADMResvTool;
import jdo.adm.ADMXMLTool;

import com.dongyang.data.TParm;

import jdo.sys.IReportTool;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import jdo.sys.SYSPostTool;

import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;

import java.sql.Timestamp;

import com.dongyang.ui.TCheckBox;

import jdo.sys.SYSBedTool;
import jdo.adm.ADMInpTool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.dongyang.manager.TIOM_Database;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.dongyang.util.StringTool;
import com.javahis.device.JMStudio;
import com.dongyang.ui.TPanel;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import java.awt.Graphics;
import java.awt.Image;

import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.FileTool;

import java.io.File;

import com.dongyang.util.ImageTool;

import java.io.IOException;
import java.awt.Color;

import com.javahis.device.JMFRegistry;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import jdo.adm.ADMTool;
import jdo.hl7.Hl7Communications;
import jdo.med.MEDApplyTool;
import jdo.mro.MROTool;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TLabel;

import jdo.mro.MROQueueTool;

import com.dongyang.root.client.SocketLink;

import jdo.sys.PatTool;

import javax.swing.SwingUtilities;

import com.dongyang.util.TypeTool;
import com.javahis.device.NJCityInwDriver;
import com.dongyang.ui.TTextFormat;

import jdo.bil.BILPayTool;
import jdo.clp.CLPSingleDiseTool;


//import org.eclipse.wb.swt.SWTshell;
import org.eclipse.swt.widgets.Display;

/**
 * <p>
 * 住院登记
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author JiaoY 2008
 * @version 1.0
 */
public class ADMInpControl extends TControl {
	Pat pat;
	String patType = ""; // 判断修改或新增病患
	String saveType = "NEW"; // 判断新增/修改 “NEW” 新增，“UPDATE”修改
	String caseNo = ""; // 就诊序号
	String McaseNo = ""; // 母亲就诊序号
	String BED_NO = ""; // 床位号
	String IPD_NO = "";
	String MR_NO = ""; // 病案号
	String haveBedNo="";//用于校验该病患是否有床位

    private String dayOpeFlg;
	 /**
     * 传入参数
     */
    //TFrame UI=(TFrame) this.getComponent("UI");
	
	// modified by WangQing 20170411 -start
	// 添加俩字段，用于职业和联系人信息显示问题
	String oldOccCode = "";
	String oldRelationCode = "";
	// modified by WangQing 20170411 -end

    public String getDayOpeFlg() {							//   2017/3/25   	by  yanmm   增加日间手术勾选				
		return dayOpeFlg;
	}

	public void setDayOpeFlg(String dayOpeFlg) {			//   2017/3/25  	by  yanmm   增加日间手术勾选		
		this.dayOpeFlg = dayOpeFlg;
	}
	public void onInit() {
	//	super.onInit();
		TParm parmmeter = new TParm();
        Object obj = this.getParameter();
        if(obj.toString().length()>0){
            parmmeter = (TParm)obj;
            this.setValue("MR_NO",parmmeter.getValue("MR_NO"));
            this.onMrno();
        }
		this.setMenu(false); // menu botton
		callFunction("UI|PHOTO_BOTTON|setEnabled", false);
		callFunction("UI|AGN_CODE|setEnabled", false); // 31天内再次住院等级
		callFunction("UI|AGN_INTENTION|setEnabled", false); // 31天内再次住院等级
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		String date = df.format(SystemTool.getInstance().getDate());
		setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // 预定日期
	}

	/**
	 * menu botton 显示控制
	 * 
	 * @param flg
	 *            boolean
	 */
	public void setMenu(boolean flg) {
		callFunction("UI|save|setEnabled", flg); // 保存按钮
		callFunction("UI|stop|setEnabled", flg); // 取消住院
		// callFunction("UI|picture|setEnabled", flg); // 拍照
		callFunction("UI|patinfo|setEnabled", flg); // 病患资料
		callFunction("UI|bed|setEnabled", flg); // 包床
		callFunction("UI|bilpay|setEnabled", flg); // 预交金
		callFunction("UI|greenpath|setEnabled", flg); // 绿色通道
//		callFunction("UI|print|setEnabled", flg); // 住院证打印
		// callFunction("UI|child|setEnabled", flg); //新生儿注记
		callFunction("UI|immunity|setEnabled", flg); // 新生儿免疫
	}

	/**
	 * 修改病患信息读取参数
	 * 
	 * @param modifyPat
	 *            Pat
	 * @return Pat
	 */
	public Pat readModifyPat(Pat modifyPat) {
		modifyPat.modifyName(getValueString("PAT_NAME")); // 姓名
		modifyPat.modifySexCode(getValueString("SEX_CODE")); // 性别
		modifyPat.modifyBirthdy(TCM_Transform
				.getTimestamp(getValue("BIRTH_DATE"))); // 出生日期
		modifyPat.modifyCtz1Code(getValueString("PAT_CTZ")); // 付款方式1
		modifyPat.modifyhomePlaceCode(getValueString("HOMEPLACE_CODE")); // 出生地
		
			// modified by WangQing 20170411 -start
		//modifyPat.modifyOccCode(getValueString("OCC_CODE")); // 职业
		String sql001 = "SELECT count(1) AS COUNT FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_OCCUPATION' AND ID ='" 
				+ this.getValueString("OCC_CODE") + "' ";
		TParm result001 = new TParm(TJDODBTool.getInstance().select(sql001));
		int count001 = Integer.parseInt(result001.getValue("COUNT", 0));
		if(count001 == 0){
			pat.modifyOccCode(oldOccCode);
		}else{
			pat.modifyOccCode(getValueString("OCC_CODE")); // 职业
		}
		// modified by WangQing 20170411 -end
		
		
		
		
		
		
		modifyPat.modifyIdNo(getValueString("IDNO")); // 身份证号
		modifyPat.modifySpeciesCode(getValueString("SPECIES_CODE")); // 种族
		modifyPat.modifyNationCode(getValueString("NATION_CODE")); // 国籍
		modifyPat.modifyMarriageCode(getValueString("MARRIAGE_CODE")); // 婚姻状态
		modifyPat.modifyCompanyDesc(getValueString("COMPANY_DESC")); // 单位
		modifyPat.modifyTelCompany(getValueString("TEL_COMPANY")); // 公司电话
		modifyPat.modifyPostCode(getValueString("POST_CODE")); // 邮编
		modifyPat.modifyResidAddress(getValueString("RESID_ADDRESS")); // 户籍地址
		modifyPat.modifyResidPostCode(getValueString("RESID_POST_CODE"));
		modifyPat.modifyContactsName(getValueString("CONTACTS_NAME"));
		
		
		// modified by WangQing 20170411 -start
		//pat.modifyRelationCode(getValueString("RELATION_CODE")); // 紧急联系人关系
		String sql002 = "SELECT COUNT(1) AS COUNT FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELATIONSHIP' AND ID ='" 
				+ this.getValueString("RELATION_CODE") + "' ";
		TParm result002 = new TParm(TJDODBTool.getInstance().select(sql002));
		int count002 = Integer.parseInt(result002.getValue("COUNT", 0));
		if(count002 == 0){
			pat.modifyRelationCode(oldRelationCode);
		}else{
			pat.modifyRelationCode(getValueString("RELATION_CODE")); // 紧急联系人关系
		}
		// modified by WangQing 20170411 -end
		
		modifyPat.modifyContactsTel(getValueString("CONTACTS_TEL"));
		modifyPat.modifyContactsAddress(getValueString("CONTACTS_ADDRESS"));
		modifyPat.modifyTelHome(getValueString("TEL_HOME")); // 家庭电话
		modifyPat.modifyAddress(getValueString("ADDRESS")); // 家庭住址
		modifyPat.modifyForeignerFlg(TypeTool
				.getBoolean(getValue("FOREIGNER_FLG"))); // 外国人注记
		// shiblmodify 20120107
		modifyPat.modifyBirthPlace(this.getValueString("BIRTHPLACE")); // 籍贯
		modifyPat.modifyCompanyAddress(this.getValueString("ADDRESS_COMPANY")); // 单位地址
		modifyPat.modifyCompanyPost(this.getValueString("POST_COMPANY")); // 单位地址
		return modifyPat;
	}

	/**
	 * 新增/修改病患 botton 事件
	 */
	public void onNewpat() {
		if ("".equals(getValue("PAT_NAME"))) {
			this.messageBox("病患姓名不可为空！");
			return;
		}
		if ("".equals(getValue("SEX_CODE"))) {
			this.messageBox("性别不可为空！");
			return;
		}
		if ("".equals(getValue("BIRTH_DATE"))) {
			this.messageBox("出生日期不可为空！");
			return;
		}
		if ("".equals(getValue("PAT_CTZ"))) {
			this.messageBox("付款方式！");
			return;
		}
		// ================= 病案室要求在住院登记填写国籍和身份证号
/*		if ("".equals(this.getValue("NATION_CODE"))) {
			this.messageBox_("请输入国籍");
			this.grabFocus("NATION_CODE");
			return;
		}*/
		if (!this.getValueBoolean("FOREIGNER_FLG")) {
			if ("".equals(this.getValue("IDNO"))) {
				this.messageBox_("请输入身份证号");
				this.grabFocus("IDNO");
				return;
			}
		}
		// 得到新增变换checkbox
		TCheckBox checkbox = (TCheckBox) this
				.callFunction("UI|NEW_PAT_INFO|getThis");
		if (pat == null || checkbox.isSelected()) {
			if (!newPatInfo()) // 新增病患
				return;
		} else {
			if (modifyPatInfo()){ // 修改病患信息
            			//duzhw add 20131023(住院登记右边保存按钮操作病患表同时需要同步信息到病案表)
            			this.addMRO("update"); // 修改 病历 MRO
            	
            		}else{
            			return;
           		 }
		}
		this.messageBox("P0005");
		// ===物联网 start
		if (Operator.getSpcFlg().equals("Y")) {
			// SYSPatinfoClientTool sysPatinfoClientTool = new
			// SYSPatinfoClientTool(
			// this.getValue("MR_NO").toString());
			// SysPatinfo syspat = sysPatinfoClientTool.getSysPatinfo();
			// SpcPatInfoService_SpcPatInfoServiceImplPort_Client
			// serviceSpcPatInfoServiceImplPortClient = new
			// SpcPatInfoService_SpcPatInfoServiceImplPort_Client();
			// String msg = serviceSpcPatInfoServiceImplPortClient
			// .onSaveSpcPatInfo(syspat);
			// if (!msg.equals("OK")) {
			// System.out.println(msg);
			// }
			TParm spcParm = new TParm();
			spcParm.setData("MR_NO", this.getValue("MR_NO").toString());
			TParm spcReturn = TIOM_AppServer.executeAction(
					"action.sys.SYSSPCPatAction", "getPatName", spcParm);
		}
		// ===物联网 end
		// callFunction("UI|picture|setEnabled", true); // 拍照
		callFunction("UI|patinfo|setEnabled", true); // 病患资料
		// callFunction("UI|child|setEnabled", true); //新生儿注记
	}

	/**
	 * 新增病患方法
	 * 
	 * @return boolean
	 */
	public boolean newPatInfo() {
		if (!checkPatInfo())
			return false;
		pat = new Pat();
		pat = this.readModifyPat(pat);
		if (!pat.onNew()) {
			this.messageBox("E0005"); // 失败
			return false;
		} else {
			setValue("MR_NO", pat.getMrNo());
			callFunction("UI|MR_NO|setEnabled", false); // 病案号
			callFunction("UI|IPD_NO|setEnabled", false); // 住院号
			callFunction("UI|patinfo|setEnabled", true); // 病患信息
			// callFunction("UI|picture|setEnabled", true); // 拍照
			// callFunction("UI|child|setEnabled", true); //新生儿注记
			this.callFunction("UI|NEW_PAT|setText", "修改病患保存");
			callFunction("UI|NEW_PAT|setEnabled", true); // 病患保存botton
			callFunction("UI|PHOTO_BOTTON|setEnabled", true);
			MR_NO = pat.getMrNo();
			// this.clearValue("NEW_PAT_INFO");//清空 “新建病患”checkbox
			return true;
		}
	}

	/**
	 * 修改病患信息方法
	 * 
	 * @return boolean
	 */
	public boolean modifyPatInfo() {
		if (pat.getMrNo() == null || "".equals(pat.getMrNo())) {
			return false;
		}
		pat = this.readModifyPat(pat);
		if (!pat.onSave()) {
			this.messageBox("E0005"); // 失败
			return false;
		} else {
			setValue("MR_NO", pat.getMrNo());
			callFunction("UI|new|setEnabled", false);
			callFunction("UI|save|setEnabled", true);
			this.setValue("NEW_PAT_INFO", "N");
			return true;
		}
	}

	/**
	 * 住院号回车事件
	 */
	public void onIpdNo() {
		TParm parm = new TParm();
		parm.setData("IPD_NO",
				PatTool.getInstance().checkIpdno(this.getValueString("IPD_NO")));
		TParm re = ADMInpTool.getInstance().selectall(parm);
		this.setValue("MR_NO", re.getValue("MR_NO", 0));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					onMrno();
				} catch (Exception e) {
				}
			}
		});
	}

	/**
	 * 病案号回车查询事件
	 */
	public void onMrno() {
		// ============== chenxi ========== 由于住院处不关界面，导致急诊晚班跨天病人入院时间错误
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		String date = df.format(SystemTool.getInstance().getDate());
		setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // 预定日期
		// ============== chenxi =========
		pat = new Pat();
		String mrno = getValue("MR_NO").toString().trim();
		if (!this.queryPat(mrno))
			return;
		pat = pat.onQueryByMrNo(mrno);
		if (pat == null || "".equals(getValueString("MR_NO"))) {
			this.messageBox_("查无病患! ");
			this.onClear(); // 清空
			this.setUi(); // 病患信息可编辑
			this.setUIAdmF(); // 住院登记信息不可编辑
			this.setMenu(false);
			return;
		} else {
			callFunction("UI|MR_NO|setEnabled", false); // 病案号
			callFunction("UI|IPD_NO|setEnabled", false); // 住院号
			callFunction("UI|patinfo|setEnabled", true); // 病患信息
			// callFunction("UI|picture|setEnabled", true); // 拍照
			// callFunction("UI|child|setEnabled", true); //新生儿注记
			this.callFunction("UI|NEW_PAT|setText", "修改病患保存");
			callFunction("UI|NEW_PAT|setEnabled", true); // 病患保存botton
			callFunction("UI|PHOTO_BOTTON|setEnabled", true);
			MR_NO = pat.getMrNo();
		}
		this.setPatForUI(pat);
		if (checkAdmInp(pat.getMrNo())) {
			this.messageBox_("此病患住院中！");
			this.inInpdata();
			this.setUIAdmF();
			
			// 调31天再次住院提醒方法
			this.AgnMessage();
			saveType = "UPDATE";
			callFunction("UI|save|setEnabled", true); // 保存
			callFunction("UI|MR_NO|setEnabled", false); // 病案号
			callFunction("UI|IPD_NO|setEnabled", false); // 住院号
			callFunction("UI|bed|setEnabled", true); // 包床
			callFunction("UI|greenpath|setEnabled", true); // 绿色通道
			// callFunction("UI|child|setEnabled", true); //新生儿注记
			this.setMenu(true);
			return;
		} else {
			// 查询预约住院信息
			TParm parm = ADMResvTool.getInstance().selectNotIn(pat.getMrNo());
			if (parm.getCount() <= 0) {
				this.messageBox_("此病患没有预约信息!");
				callFunction("UI|save|setEnabled", false); // 保存
				return;
			}
			// 判断预约信息是否是新生儿
			if (parm.getBoolean("NEW_BORN_FLG", 0)) {
				this.setValue("NEW_BORN_FLG",
						parm.getBoolean("NEW_BORN_FLG", 0));
				McaseNo = parm.getValue("M_CASE_NO", 0); // 查询预约信息中是否有母亲的病案号
				// 查询母亲的住院信息 获取住院号(婴儿的IPD_NO与母亲相同)
				TParm admParm = new TParm();
				admParm.setData("CASE_NO", McaseNo);
				TParm admInfo = ADMTool.getInstance().getADM_INFO(admParm);
				if (admInfo.getCount() <= 0) {
					this.messageBox_("没有查询到母亲的住院信息");
					callFunction("UI|save|setEnabled", false); // 保存
					return;
				}
				Pat M_PAT = Pat.onQueryByMrNo(admInfo.getValue("MR_NO", 0));
				this.setValue("IPD_NO", admInfo.getValue("IPD_NO", 0));
				this.setValue("M_MR_NO", admInfo.getValue("MR_NO", 0));
				this.setValue("M_NAME", M_PAT.getName());
				this.callFunction("UI|LM1|setVisible", true);
				this.callFunction("UI|LM2|setVisible", true);
				this.callFunction("UI|M_MR_NO|setVisible", true);
				this.callFunction("UI|M_NAME|setVisible", true);
			} else {
				this.setValue("IPD_NO", pat.getIpdNo());
				this.callFunction("UI|LM1|setVisible", false);
				this.callFunction("UI|LM2|setVisible", false);
				this.callFunction("UI|M_MR_NO|setVisible", false);
				this.callFunction("UI|M_NAME|setVisible", false);
			}
			TParm param = new TParm();// add by wanglong 20121025
			param.setData("MR_NO", parm.getValue("MR_NO", 0));// add by wanglong
																// 20121025
			TParm result1 = CLPSingleDiseTool.getInstance().queryADMResvSDInfo(
					param);// add by wanglong 20121025
			this.setValueForParm("DISE_CODE", result1, 0);// add by wanglong
															// 20121025
			callFunction("UI|save|setEnabled", true); // 保存
			this.setValue("ADM_SOURCE", parm.getData("ADM_SOURCE", 0));
			this.setValue("TOTAL_BILPAY", parm.getData("BILPAY", 0));
			// this.setValue("SERVICE_LEVEL", parm.getData("SERVICE_LEVEL", 0));
			this.setValue("CTZ1_CODE", parm.getData("CTZ1_CODE", 0));
			this.setValue("DEPT_CODE", parm.getData("DEPT_CODE", 0));
			this.setValue("STATION_CODE", parm.getData("STATION_CODE", 0));
			this.setValue("OPD_DEPT_CODE", parm.getData("OPD_DEPT_CODE", 0));
			this.setValue("OPD_DR_CODE", parm.getData("OPD_DR_CODE", 0));
			this.setValue("VS_DR_CODE", parm.getData("DR_CODE", 0));
			this.setValue("PATIENT_CONDITION",
					parm.getData("PATIENT_CONDITION", 0));
			this.setValue("YELLOW_SIGN", parm.getData("YELLOW_SIGN", 0));
			this.setValue("RED_SIGN", parm.getData("RED_SIGN", 0));
			this.setValue("BED_NO", getBedDesc(parm.getValue("BED_NO", 0)));
			haveBedNo=parm.getValue("BED_NO", 0);
			this.setUIT();
			this.setValue("SERVICE_LEVEL", "1"); // 服务等级默认是"自费"
			this.setValue("IN_COUNT", getInCount(MR_NO) + 1); // 获取病人住院的次数
			this.setValue("DAY_OPE_FLG", parm.getValue("DAY_OPE_FLG", 0)); //   2017/3/25  yanmm 日间手术
			saveType = "NEW";
			// 调31天再次住院提醒方法
			this.AgnMessage();
			// callFunction("UI|child|setEnabled", true); //新生儿注记
		}
	}

	/**
	 * 查询病患信息
	 * 
	 * @param mrNo
	 *            String
	 * @return boolean
	 */
	public boolean queryPat(String mrNo) {
		this.setMenu(false); // MENU 显示控制
		pat = new Pat();
		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.setMenu(false); // MENU 显示控制
			this.messageBox("E0081");
			return false;
		}
		String allMrNo = PatTool.getInstance().checkMrno(mrNo);
		if (mrNo != null && !allMrNo.equals(pat.getMrNo())) {
			// ============xueyf modify 20120307 start
			messageBox("病案号" + allMrNo + " 已合并至" + pat.getMrNo());
			// ============xueyf modify 20120307 stop
		}

		return true;
	}

	/**
	 * 病患信息赋值
	 * 
	 * @param patInfo
	 *            Pat
	 */
	public void setPatForUI(Pat patInfo) {
		// 病案号,姓名,性别,生日,职业，民族，国籍，身份证号，婚姻,出生地
		this.setValueForParm(
				"MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;OCC_CODE;SPECIES_CODE;NATION_CODE;IDNO;MARRIAGE_CODE;HOMEPLACE_CODE;TEL_HOME",
				patInfo.getParm());
		// 工作单位,单位电话,单位邮编,户口地址,户口邮编，联系人姓名，关系，联系人电话，联系人地址
		this.setValueForParm(
				"COMPANY_DESC;TEL_COMPANY;POST_CODE;ADDRESS;ADDRESS_COMPANY;POST_COMPANY;BIRTHPLACE;RESID_ROAD;RESID_POST_CODE;CONTACTS_NAME;RELATION_CODE;CONTACTS_TEL;CONTACTS_ADDRESS;SERVICE_LEVEL;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE",
				patInfo.getParm());
		this.setValue("PAT_CTZ", patInfo.getCtz1Code());
		this.setText("TEL_O1", patInfo.getTelCompany());
		this.setValue("RESID_ADDRESS", patInfo.getResidAddress());
		this.setValue("FOREIGNER_FLG", patInfo.isForeignerFlg()); // 外国人注记
		setBirth(); // 计算年龄
		// onPOST_CODE();
		// onRESID_POST_CODE();
		// this.onCompanyPost();
		
		
		// modified by WangQing 20170331 -start 
		// 老的职业信息、联系人关系信息显示
		TComboBox occCodeCombo= (TComboBox) this.getComponent("OCC_CODE");
		TComboBox relationCombo= (TComboBox) this.getComponent("RELATION_CODE");
		occCodeCombo.setCanEdit(true);
		relationCombo.setCanEdit(true);
		String sql1 = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_OCCUPATION' AND ID ='" 
				+ patInfo.getOccCode() + "' ";
		String sql2 = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELATIONSHIP' AND ID ='" 
				+ patInfo.getRelationCode() + "' ";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		occCodeCombo.setText((String) result1.getData("CHN_DESC", 0));
		relationCombo.setText((String) result2.getData("CHN_DESC", 0));
		oldOccCode = pat.getOccCode();
		oldRelationCode = pat.getRelationCode();
		// modified by WangQing 20170331 -end
		
		this.viewPhoto(pat.getMrNo());

	}

	/**
	 * 身份证号得到出生地
	 */
	public void onIdNo() {
		String homeCode = "";
		String idNo = this.getValueString("IDNO");
		homeCode = StringUtil.getIdNoToHomeCode(idNo);
		if(PatTool.getInstance().isExistHomePlace(homeCode)){
			setValue("HOMEPLACE_CODE", homeCode);
		}else{
			setValue("HOMEPLACE_CODE", "");
		}
	}

	// /**
	// * 户口邮编得到省市
	// */
	// public void onPOST_CODE() {
	// if (getValueString("POST_CODE") == null
	// || "".equals(getValueString("POST_CODE")))
	// return;
	//
	// String post = getValueString("POST_CODE");
	// TParm parm = this.getPOST_CODE(post);
	//
	// if (parm.getData("POST_CODE", 0) == null
	// || "".equals(parm.getData("POST_CODE", 0)))
	// return;
	// setValue("POST_P",
	// parm.getData("POST_CODE", 0).toString().substring(0, 2));
	// setValue("POST_C", parm.getData("POST_CODE", 0).toString());
	// }
	//
	// /**
	// * 户口邮编得到省市
	// */
	// public void onRESID_POST_CODE() {
	// if (getValueString("RESID_POST_CODE") == null
	// || "".equals(getValueString("POST_CODE")))
	// return;
	// String post = getValueString("RESID_POST_CODE");
	// TParm parm = this.getPOST_CODE(post);
	// if (parm.getData("POST_CODE", 0) == null
	// || "".equals(parm.getData("POST_CODE", 0)))
	// return;
	// setValue("RESID_POST_P", parm.getData("POST_CODE", 0).toString()
	// .substring(0, 2));
	// setValue("RESID_POST_C", parm.getData("POST_CODE", 0).toString());
	// }
	//
	// /**
	// * 得到省市代码
	// *
	// * @param post
	// * String
	// * @return TParm
	// */
	// public TParm getPOST_CODE(String post) {
	// TParm result = SYSPostTool.getInstance().getProvinceCity(post);
	// return result;
	// }
	//
	// /**
	// * 通过城市带出邮政编码
	// */
	// public void selectCode_1() {
	// String post = this.getValue("POST_C").toString();
	// if (post.length() == 0 || "".equals(post))
	// return;
	//
	// this.setValue("POST_CODE", this.getValue("POST_C"));
	// this.onPOST_CODE();
	// }
	//
	// /**
	// * 通过城市带出邮政编码
	// */
	// public void selectCode_2() {
	// if (this.getValue("RESID_POST_C") == null
	// || "".equals(getValueString("POST_CODE")))
	// return;
	// this.setValue("RESID_POST_CODE", this.getValue("RESID_POST_C"));
	// this.onRESID_POST_CODE();
	// }
	//
	// /**
	// * 通过城市带出邮政编码3
	// */
	// public void selectCode_3() {
	// this.setValue("POST_COMPANY", this.getValue("COMPANY_POST_C"));
	// this.onCompanyPost();
	// }
	//
	// /**
	// * 单位邮编的得到省市
	// */
	// public void onCompanyPost() {
	// String post = getValueString("POST_COMPANY");
	// if (post == null || "".equals(post)) {
	// return;
	// }
	// TParm parm = this.getPOST_CODE(post);
	// setValue("COMPANY_POST_P", parm.getData("POST_CODE", 0) == null ? ""
	// : parm.getValue("POST_CODE", 0).substring(0, 2));
	// setValue("COMPANY_POST_C", parm.getValue("POST_CODE", 0).toString());
	// }
	/**
	 * 同通信地址
	 */
	public void onSameto1() {
		setValue("RESID_POST_CODE", getValue("POST_CODE"));
		// this.onRESIDPOST();
		// callFunction("UI|SESSION_CODE|onQuery");
		setValue("RESID_ADDRESS", getValue("ADDRESS"));

	}

	/**
	 * 同通信地址
	 */
	public void onSameto3() {
		setValue("POST_COMPANY", getValue("POST_CODE"));
		// this.onRESIDPOST();
		// callFunction("UI|SESSION_CODE|onQuery");
		setValue("ADDRESS_COMPANY", getValue("ADDRESS"));

	}

	/**
	 * 同通信地址
	 */
	public void onSameto2() {
		setValue("CONTACTS_ADDRESS", getValue("ADDRESS"));
	}

	/**
	 * 住院登记保存
	 */
	public void onSave() {
		// if(!checkPatInfo()){
		// return;
		// }
		if ("NEW".equals(saveType)) {
			this.admInpInsert(); // 新增
		} else if ("UPDATE".equals(saveType)) {
			this.admInpUpdata(); // 修改
		} else {
			this.messageBox_("没有保存类型");
		}
	}

	/**
	 * 31天内再次住院提醒方法
	 */
	public void AgnMessage() {
		Timestamp date = SystemTool.getInstance().getDate();
		String mrNo = this.getValueString("MR_NO");
		if (mrNo == null || mrNo.length() <= 0) {
			return;
		}
		TParm inparm = new TParm();
		inparm.setData("MR_NO", mrNo);
		inparm.setData("CANCEL_FLG", "N");
		inparm.setData("REGION_CODE", Operator.getRegion());
		TParm parm = ADMInpTool.getInstance().selectall(inparm);
		if (parm.getCount() <= 0)
			return;
		// 住院次数
		int count = parm.getCount();
		if (count > 0) {
			parm = ADMInpTool.getInstance().queryLastDsdate(inparm);
			// luhai MODIFY 2012-2-21 modify 处理在病患之前没有住院的情
			// 况下，查询出的上次住院日期为空，但查询出的数据条数显示为1的情况 begin
			// if (parm.getCount() <= 0)
			// return;
			if (parm.getCount("DS_DATE") <= 0)
				return;
			if (parm.getTimestamp("DS_DATE", 0) == null) {
				return;
			}
			// luhai MODIFY 2012-2-21 modify 处理在病患之前没有住院的情
			// 况下，查询出的上次住院日期为空，但查询出的数据条数显示为1的情况 end
			Timestamp lastdate = parm.getTimestamp("DS_DATE", 0);
			int time = StringTool.getDateDiffer(date, lastdate);
			if (time <= 31) {
				// 新增提醒
				if ("NEW".equals(saveType)) {
					this.messageBox("此病人出院31天内再次住院！");
				}
				callFunction("UI|AGN_CODE|setEnabled", true); // 31天内再次住院等级
				callFunction("UI|AGN_INTENTION|setEnabled", true); // 31天内再次住院等级
			} else {
				callFunction("UI|AGN_CODE|setEnabled", false); // 31天内再次住院等级
				callFunction("UI|AGN_INTENTION|setEnabled", false); // 31天内再次住院等级
			}
		}
	}

	/**
	 * 新增
	 */
	public void admInpInsert() {
		if (checkAdmInp(this.getValueString("MR_NO"))) {
			this.messageBox_("此病患住院中！");
			this.inInpdata();
			this.setUIAdmF();
			
			saveType = "UPDATE";
			callFunction("UI|save|setEnabled", true); // 保存
			callFunction("UI|MR_NO|setEnabled", false); // 病案号
			callFunction("UI|IPD_NO|setEnabled", false); // 住院号
			callFunction("UI|bed|setEnabled", true); // 包床
			callFunction("UI|greenpath|setEnabled", true); // 绿色通道
			// callFunction("UI|child|setEnabled", true); //新生儿注记
			this.setMenu(true);
			return;
		}
		if (!this.checkData()) // 检查数据
			return;
		
		/*modified by Eric 20170525 start
		不超过7天再次住院患者系统增添提示*/
		if(this.getValueString("ADM_SOURCE") !=null 
				&& this.getValueString("ADM_SOURCE").equals("02")){// 病患来源为急诊			
			String dsDateSql = "SELECT MAX(DS_DATE) AS DS_DATE FROM ADM_INP WHERE MR_NO='"+this.getValueString("MR_NO")+"' ";
			TParm dsDateResult = new TParm(TJDODBTool.getInstance().select(dsDateSql));
			Timestamp dsDate = (Timestamp) dsDateResult.getData("DS_DATE", 0);
//			Timestamp now = TJDODBTool.getInstance().getDBTime();
			Timestamp inDate = (Timestamp) this.getValue("IN_DATE");
			// modify by wangb 2017/6/23 解决第一次住院病患获得不到上一次出院时间导致的空指针的问题
			if (null != inDate && null != dsDate) {
				int days = StringTool.getDateDiffer(inDate, dsDate);
				System.out.println("------dsDate=" + dsDate);
				// System.out.println("------now="+now);
				System.out.println("------inDate=" + inDate);
				System.out.println("------days=" + days);
				if (days <= 7) {
					this.messageBox("此患者本次住院时间距上次出院时间不超过7天");
				}
			}			
		}
		/*modified by Eric 20170525 end*/
		
		
		
		
		// add by wangb 2015/10/22 住院登记时校验是否已经保存过住院证 START
		if (!this.checkHospCard()) {
			this.messageBox("请点击\"住院证打印\"按钮，并保存住院证");
			return;
		}
		// add by wangb 2015/10/22 住院登记时校验是否已经保存过住院证 END
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		String date = df.format(SystemTool.getInstance().getDate());
		setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // 预定日期
		TParm parm = new TParm();
		parm = this.readData(); // 读取参数
		TParm bed = new TParm();
		bed.setData("BED_NO", parm.getData("BED_NO"));
		TParm checkbed = ADMInpTool.getInstance().QueryBed(bed);
		if (checkbed.getData("ALLO_FLG", 0) != null) {
			if (checkbed.getData("ALLO_FLG", 0).equals("Y")) {
				this.messageBox("此床已占用,请从新选择床位");
				return;
			}
		}
		// System.out.println("adm_1");
		BED_NO = parm.getData("BED_NO").toString();
		if ("Y".equals(getValue("NEW_BORN_FLG"))) {
			IPD_NO = getValue("IPD_NO").toString();
		} else {
			IPD_NO = pat.getIpdNo(); // 判断该病患是否住过院
			if ("".equals(IPD_NO))
				IPD_NO = SystemTool.getInstance().getIpdNo();

			if ("".equals(IPD_NO)) {
				this.messageBox_("住院号取参错误！");
				return;
			}
		}
		// System.out.println("adm_2");
		caseNo = SystemTool.getInstance().getNo("ALL", "REG", "CASE_NO",
				"CASE_NO"); // 调用取号原则
		// System.out.println("adm_3");
		// 读取界面资料
		parm.setData("CASE_NO", caseNo);
		parm.setData("M_CASE_NO", McaseNo);
		parm.setData("IPD_NO", IPD_NO); 
		parm.setData("DATE", SystemTool.getInstance().getDate());
		parm.setData("IN_DEPT_CODE", this.getValue("DEPT_CODE"));
		parm.setData("IN_STATION_CODE", this.getValue("STATION_CODE"));
		parm.setData("VS_DR_CODE", this.getValue("VS_DR_CODE"));
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("ADM_CLERK", Operator.getID()); // 住院登记作业员
        parm.setData("DAY_OPE_FLG",this.getValue("DAY_OPE_FLG"));		// 2017/3/25    yanmm 日间手术
		// ***********modify by lim 2012/02/21 begin
        
        //  带入历次过敏史记录  machao  start
        String sqlAllergy = "SELECT * FROM opd_drugallergy WHERE 1=1 AND "
        		+ " MR_NO = '"+parm.getValue("MR_NO")+"' AND"
        		+ " DRUG_TYPE is not null AND "
        		+ " DRUG_TYPE <> 'N' ";
        
//        if(!StringUtil.isNullString(parm.getValue("CASE_NO"))){
//        	sqlAllergy = sqlAllergy.replace("#", "AND CASE_NO = '"+parm.getValue("CASE_NO")+"'");
//        }else{
//        	sqlAllergy = sqlAllergy.replace("#", "");
//        }
        System.out.println("33333:"+sqlAllergy);
        TParm res = new TParm(TJDODBTool.getInstance().select(sqlAllergy));
        
        parm.setData("ALLERGY", res.getCount("MR_NO")>0?"Y":"N");
        //带入历次过敏史记录  machao  end
        
		String mrNo = parm.getValue("MR_NO");
		String resvNo = "";

		
		String fileServerMainRoot = TIOM_FileServer
				.getPath("FileServer.Main.Root");
		String emrData = TIOM_FileServer.getPath("EmrData");
		TParm resv = ADMResvTool.getInstance().selectNotIn(mrNo);
		if (resv.getCount() < 0) {
			messageBox("该病患没有预约住院");
			return;
		}
		resvNo = resv.getValue("RESV_NO", 0);
		String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO ='" + resvNo
				+ "'  ORDER BY OPT_DATE DESC ";
		// System.out.println("======sql===###################===="+sql);
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
		if (result1.getCount() <= 0) {

		} else {
			// 移动JHW文件并改名称.
			String oldFileName = result1.getValue("FILE_NAME", 0);
			String oldFilePath = result1.getValue("FILE_PATH", 0);
			String seq = result1.getValue("FILE_SEQ", 0);
			System.out.println(fileServerMainRoot + emrData + oldFilePath
					+ "\\" + oldFileName + ".jhw");

			byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					fileServerMainRoot + emrData + oldFilePath + "\\"
							+ oldFileName + ".jhw");

			Timestamp ts = SystemTool.getInstance().getDate();
			String dateStr = StringTool.getString(ts, "yyyyMMdd");
			// 获得新的文件路径
			StringBuilder filePathSb = new StringBuilder();
			filePathSb.append("JHW\\").append(dateStr.substring(2, 4))
					.append("\\").append(dateStr.substring(4, 6)).append("\\")
					.append(mrNo);
			String newFilePath = filePathSb.toString();

			// 获得新的文件名称
			String[] oldFileNameArray = oldFileName.split("_");

			StringBuilder sb = new StringBuilder(caseNo);
			sb.append("_").append(oldFileNameArray[1]).append("_")
					.append(oldFileNameArray[2]);
			String newFileName = sb.toString();

			try {
				// 移动文件
				TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
						fileServerMainRoot + emrData + newFilePath + "\\"
								+ newFileName + ".jhw", data);
				// this.messageBox("=====resvNo====="+resvNo);
				TParm action = new TParm(
						this.getDBTool()
								.select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"
										+ caseNo + "'"));
				int index = action.getInt("MAXFILENO", 0);
				// 更新数据库
				String sql1 = "UPDATE EMR_FILE_INDEX SET CASE_NO='" + caseNo
						+ "',FILE_PATH='" + newFilePath + "',FILE_NAME='"
						+ newFileName + "',FILE_SEQ='" + index
						+ "' WHERE CASE_NO='" + resvNo + "' AND FILE_SEQ='"
						+ seq + "'";

				// System.out.println("======sql11111========"+sql1);
				TParm result2 = new TParm(TJDODBTool.getInstance().update(sql1));
				if (result2.getErrCode() < 0) {
					err(result2.getErrName() + "" + result2.getErrText());
					messageBox("更新失败!");
					return;
				}
				// 删除老文件；
				boolean delFlg = TIOM_FileServer.deleteFile(
						TIOM_FileServer.getSocket(), fileServerMainRoot
								+ emrData + oldFilePath + "\\" + oldFileName
								+ ".jhw");
				if (!delFlg) {
					this.messageBox("删除原文件失败!");
					return;
				}
			} catch (Exception e) {
				this.messageBox("移动文件失败!");
			}
		}
		// ***********modify by lim 2012/02/21 end
		TParm result = TIOM_AppServer.executeAction("action.adm.ADMInpAction",
				"insertADMData", parm); // 住院登记保存
		// System.out.println("adm_4");
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
			
			deletePreInfo();//删除预登记表的数据
            // 床旁接口 wanglong add 20140731
			TParm xmlParm = ADMXMLTool.getInstance().creatADMXMLFile(caseNo, "A01");
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("信息看板接口发送失败 " + xmlParm.getErrText());
            }
            // 电视屏接口 wanglong add 20141010
            xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
            }
			saveType = "UPDATE";
			this.setValue("IPD_NO", IPD_NO);
			pat.modifyIpdNo(IPD_NO);
			pat.modifyRcntIpdDate((Timestamp) getValue("IN_DATE"));
			pat.modifyRcntIpdDept(getValue("DEPT_CODE").toString());
			pat.onSave();
			modifyPatInfo(); // 更新病患基本信息
			if (!this.getValueString("DISE_CODE").trim().equals("")) {// add by
																		// wanglong
																		// 20121025
				updateADMInpSDInfo();// 插入单病种信息
			}
			this.addMRO("new"); // 新建 病历 MRO
			this.setUIAdmF();
			this.setMenu(true);
			// HL7消息
			// this.sendHl7message();
			// 通知护士站socket
			sendInwStationMessages(this.getValueString("MR_NO"), caseNo,
					pat.getName());
			// //生成信息看板XML
			// try{
			// ADMXMLTool.getInstance().creatXMLFile(caseNo);
			// }catch(Exception e){}
			if (!getConfigBoolean("OPENWINDOW.GREEN_PATH_isOpen"))
				return;
			TParm sendParm = new TParm();
			sendParm.setData("CASE_NO", caseNo);
			sendParm.setData("PRE_AMT", this.getValue("TOTAL_BILPAY"));
			String fileName = getConfigString("OPENWINDOW.BIIL_PAY");
			this.openWindow(fileName, sendParm);
			TParm queryData = new TParm();
			queryData.setData("CASE_NO", caseNo);
			TParm bilPay = ADMInpTool.getInstance().queryCaseNo(queryData);
			setValue("TOTAL_BILPAY", bilPay.getData("TOTAL_BILPAY", 0));

		}
	}
	
	/**
	 * 更新床位信息
	 */
	public void deletePreInfo(){
		String sql="DELETE FROM ADM_PRETREAT WHERE MR_NO='"+this.getValue("MR_NO")+"'";
		System.out.println(":::"+sql);
		TParm result=new TParm(TJDODBTool.getInstance().update(sql));
	}
	/**
	 * 修改
	 */
	public void admInpUpdata() {
        if (!this.checkData()) // 检查数据
            return;
		if (caseNo == null || "".equals(caseNo))
			return;
		if (!checkBedNo()) {
			return;
		}
		
		/*modified by Eric 20170525 start
		不超过7天再次住院患者系统增添提示*/
		if(this.getValueString("ADM_SOURCE") !=null 
				&& this.getValueString("ADM_SOURCE").equals("02")){// 病患来源为急诊			
			String dsDateSql = "SELECT MAX(DS_DATE) AS DS_DATE FROM ADM_INP WHERE MR_NO='"+this.getValueString("MR_NO")+"' ";
			TParm dsDateResult = new TParm(TJDODBTool.getInstance().select(dsDateSql));
			Timestamp dsDate = (Timestamp) dsDateResult.getData("DS_DATE", 0);
//			Timestamp now = TJDODBTool.getInstance().getDBTime();
			Timestamp inDate = (Timestamp) this.getValue("IN_DATE");
			// modify by wangb 2017/6/23 解决第一次住院病患获得不到上一次出院时间导致的空指针的问题
			if (null != inDate && null != dsDate) {
				int days = StringTool.getDateDiffer(inDate, dsDate);
				System.out.println("------dsDate=" + dsDate);
				// System.out.println("------now="+now);
				System.out.println("------inDate=" + inDate);
				System.out.println("------days=" + days);
				if (days <= 7) {
					this.messageBox("此患者本次住院时间距上次出院时间不超过7天");
				}
			}			
		}
		/*modified by Eric 20170525 end*/
		
		TParm parm = new TParm();
		parm = this.readData(); // 读取参数
		if (!BED_NO.equals(this.getValueString("BED_DESC"))) {
			parm.setData("UPDATE_BED", "Y");
		} else {
			parm.setData("UPDATE_BED", "N");
		}
		parm.setData("CASE_NO", caseNo);
		parm.setData("IPD_NO", getValue("IPD_NO"));
		
		 //  带入历次过敏史记录  machao  start
		String sqlAllergy = "SELECT * FROM opd_drugallergy WHERE 1=1 AND "
        		+ " MR_NO = '"+parm.getValue("MR_NO")+"' AND"
        		+ " DRUG_TYPE is not null AND "
        		+ " DRUG_TYPE <> 'N' ";        
//        if(!StringUtil.isNullString(parm.getValue("CASE_NO"))){
//        	sqlAllergy = sqlAllergy.replace("#", "AND CASE_NO = '"+parm.getValue("CASE_NO")+"'");
//        }else{
//        	sqlAllergy = sqlAllergy.replace("#", "");
//        }
        System.out.println("22222:"+sqlAllergy);
        TParm res = new TParm(TJDODBTool.getInstance().select(sqlAllergy));
        
        parm.setData("ALLERGY", res.getCount("MR_NO")>0?"Y":"N");
		// machao end
        
		TParm result = TIOM_AppServer.executeAction("action.adm.ADMInpAction",
				"upDataAdmInp", parm); // 住院登记保存
		if (result.getErrCode() < 0)
			this.messageBox("E0005");
		else {
			this.messageBox("P0005");
			modifyPatInfo();
			// //生成信息看板XML
			// try{
			// ADMXMLTool.getInstance().creatXMLFile(caseNo);
			// }catch(Exception e){}
			this.addMRO("update"); // 修改 病历 MRO
			this.setUIAdmF();
		}
	}

	/**
	 * 检查是否可以修改床位 true 允许修改 false 不允许
	 * 
	 * @return boolean
	 */
	public boolean checkBedNo() {
		boolean check = false;
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        TParm result = SYSBedTool.getInstance().checkInBed(parm);
        if (result.getErrCode() < 0) {
            this.messageBox_(result.getErrText());
            return false;
        }
        int count = result.getCount("BED_STATUS");
        if (count == -1 || count == 0) {
            return check;
        }
        for (int i = 0; i < count; i++) {
            if (result.getData("BED_STATUS", i) == null
                || "".equals(result.getData("BED_STATUS", i))
                || "0".equals(result.getData("BED_STATUS", i))) {
                check = true;
            } else if(result.getValue("BED_STATUS", i).equals("1")
            		&&result.getValue("BED_OCCU_FLG", i).equals("N")){
            	BED_NO=result.getValue("BED_NO", i);
            	this.setValue("BED_DESC", BED_NO);
            }
        }
		for (int i = 0; i < count; i++) {
			if (result.getData("BED_OCCU_FLG", i) == null
					|| "".equals(result.getData("BED_OCCU_FLG", i))
					|| "N".equals(result.getData("BED_OCCU_FLG", i))) {
				check = true;
			} else {
				check = false;
			}
		}
		if (!check) {
			int message = this.messageBox("消息", "此病患已包床是否继续？", 0);
			if (message == 0) {
				check = true;
			} else {
				check = false;
				return check;
			}
		}

		TParm bedNo = new TParm();
		bedNo.setData("BED_NO", this.getValue("BED_DESC"));
		TParm checkbed = ADMInpTool.getInstance().QueryBed(bedNo);
		String mrNo = this.getValueString("MR_NO");
		if (checkbed.getData("ALLO_FLG", 0) != null) {
			if (checkbed.getData("ALLO_FLG", 0).equals("Y")
					&& !(mrNo.equals(checkbed.getData("MR_NO", 0)))) {
				this.messageBox("此床已占用,请从新选择床位");
				return false;
			}
		}
		return check;
	}

	/**
	 * 检核是否可以取消住院
	 * 
	 * @return boolean
	 */
	public boolean checkCanInp() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		boolean result = ADMTool.getInstance().checkCancelOutInp(parm);
		return result;
	}

	/**
	 * 校验预交金余额
	 * 
	 * @return boolean
	 */
	public boolean checkBilPay() {
		TParm parm = BILPayTool.getInstance().selBilPayLeft(caseNo);
		if (parm.getErrCode() < 0) {
			return false;
		}
		if (parm.getDouble("PRE_AMT", 0) > 0) {
			return false;
		}

		return true;
	}

	/**
	 * 检核是否可以已经产生费用(耗用记录)
	 * 
	 * @return boolean
	 */
	public boolean checkCanPay() {
		boolean result = true;    
		
		//modify by yangjj 20151110 取消住院 费用总和小于等于0
		String sql = " SELECT SUM(TOT_AMT) AS COUNT FROM IBS_ORDD WHERE CASE_NO = '"+caseNo+"' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(Integer.parseInt(parm.getValue("COUNT", 0)) > 0){
			result = false;
		}
		//String sql = "SELECT CASE_NO FROM IBS_ORDM WHERE CASE_NO = '"+caseNo+"' ";
		//TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//		parm.setData("CASE_NO", caseNo);
		
		//if(parm.getCount()>0){
			//result = false;
		//}
		//boolean result = ADMTool.getInstance().checkCancelOutInp(parm); 
		return result;
	}
	
	/**
	 * 读取UI界面资料
	 * 
	 * @return TParm
	 */
	public TParm readData() {
		TParm parm = new TParm();
		parm.setData("MR_NO", getValue("MR_NO")); // 病案号
		parm.setData("NEW_BORN_FLG", getValue("NEW_BORN_FLG")); // 新生儿注记
		parm.setData("ADM_SOURCE", getValue("ADM_SOURCE")); // 病患来源
		parm.setData("DEPT_CODE", getValue("DEPT_CODE")); // 住院科别
		parm.setData("TOTAL_BILPAY", getValueDouble("TOTAL_BILPAY")); // 预交金
		parm.setData("PATIENT_CONDITION", getValue("PATIENT_CONDITION")); // 入院状态
		parm.setData("SERVICE_LEVEL", getValue("SERVICE_LEVEL")); // 服务等级
		parm.setData("CTZ1_CODE", getValue("CTZ1_CODE")); // 付款方式1
		parm.setData("CTZ2_CODE", getValue("CTZ2_CODE")); // 2
		parm.setData("CTZ3_CODE", getValue("CTZ3_CODE")); // 3
		parm.setData("IN_COUNT", getText("IN_COUNT")); // 入院次数
		parm.setData("IN_DATE", getValue("IN_DATE")); // 3入院日期
		parm.setData("DEPT_CODE", getValue("DEPT_CODE")); // 住院科别
		parm.setData("STATION_CODE", getValue("STATION_CODE")); // 住院病区
		parm.setData("BED_NO", getValue("BED_DESC")); // 床位号
		parm.setData("OPD_DR_CODE", this.getValue("OPD_DR_CODE")); // 门急诊医师
		parm.setData("VS_DR_CODE", getValue("VS_DR_CODE")); // 经治医师
		parm.setData("ATTEND_DR_CODE", getValue("ATTEND_DR_CODE")); // 主治医师
		parm.setData("ADM_DATE", getValue("ADM_DATE")); // 登记日期
		parm.setData("RED_SIGN", getValueDouble("RED_SIGN")); // 红色警戒
		parm.setData("YELLOW_SIGN", getValueDouble("YELLOW_SIGN")); // 黄色警戒
		parm.setData("AGN_CODE", this.getValueString("AGN_CODE"));
		parm.setData("AGN_INTENTION", this.getValueString("AGN_INTENTION"));
		parm.setData("DAY_OPE_FLG",getValue("DAY_OPE_FLG")); //日间手术
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());
		return parm;
	}

	/**
	 * 界面数据检查
	 * 
	 * @return Boolean
	 */
	public Boolean checkData() {
		if ("".equals(this.getValueString("ADM_SOURCE"))) {
			this.messageBox_("请输入病患来源");
			return false;
		}
		if ("".equals(this.getValueString("OPD_DEPT_CODE"))) {
			this.messageBox_("请输入门急诊科室");
			return false;
		}
		if ("".equals(this.getValueString("OPD_DR_CODE"))) {
			this.messageBox_("请输入门急诊医师");
			return false;
		}
		if ("".equals(this.getValueString("PATIENT_CONDITION"))) {
			this.messageBox_("请输入入院状态");
			return false;
		}
		if ("".equals(this.getValueString("SERVICE_LEVEL"))) {
			this.messageBox_("请选择服务等级");
			return false;
		}
		if ("".equals(this.getValueString("CTZ1_CODE"))) {
			this.messageBox_("请输入付款方式");
			return false;
		}
		if ("".equals(this.getValueString("TOTAL_BILPAY"))){
			this.messageBox_("请输预交金");
			return false;
		}
		if ("".equals(this.getValueString("DEPT_CODE"))) {
			this.messageBox_("请输入住院科别");
			return false;
		}
		if ("".equals(this.getValueString("STATION_CODE"))) {
			this.messageBox_("请输入住院病区");
			return false;
		}
		if ("".equals(this.getValueString("VS_DR_CODE"))) {
			this.messageBox_("请输入经治医师");
			return false;
		}
		if ("".equals(this.getValueString("IN_DATE"))) {
			this.messageBox_("请输入入院日期");
			return false;
		}
		// ====zhangp 20120828 start
		if ("".equals(this.getValueString("BIRTH_DATE"))) {
			this.messageBox_("请输入出生年月");
			return false;
		}
		// ===zhangp 20120828 end
		if ("Y".equals(getValue("NEW_BORN_FLG"))) {
			if (getValue("IPD_NO") == null || "".equals(getValue("IPD_NO"))) {
				this.messageBox_("未选择母亲");
				return false;
			}
		}
		// ============chenxi modify 20130422===== 病案室要求在住院登记填写国籍和身份证号
		// modify by wangb 2016/07/26 应住院处为提高工作效率，将民族及其以下必填项改为非必填，数据后续补录
		/*if ("".equals(this.getValueString("NATION_CODE"))) {
			this.messageBox_("请输入国籍");
			this.grabFocus("NATION_CODE");
			return false;
		}*/
		if (!this.getValueBoolean("FOREIGNER_FLG")) {
			if ("".equals(this.getValueString("IDNO"))) {
				this.messageBox_("请输入身份证号");
				this.grabFocus("IDNO");
				return false;
			}
		}
        if ("".equals(this.getValueString("MR_NO"))) {// wanglong add 20140815
            this.messageBox_("请输入病案号");
            return false;
        }
        if ("".equals(this.getValueString("PAT_NAME"))) {
            this.messageBox_("请输入姓名");
            return false;
        }
        if ("".equals(this.getValueString("SEX_CODE"))) {
            this.messageBox_("请选择性别");
            return false;
        }
        if ("".equals(this.getValueString("BIRTH_DATE"))) {
            this.messageBox_("请选择出生日期");
            return false;
        }
        /*if ("".equals(this.getValueString("MARRIAGE_CODE"))) {
            this.messageBox_("请选择婚姻");
            return false;
        }*/
        if ("".equals(this.getValueString("AGE"))) {
            this.messageBox_("请输入年龄");
            return false;
        }
        /*if ("".equals(this.getValueString("SPECIES_CODE"))) {
            this.messageBox_("请选择民族");
            return false;
        }*/
        if ("".equals(this.getValueString("TEL_HOME"))) {
            this.messageBox_("请输入电话");
            return false;
        }
        /*if ("".equals(this.getValueString("OCC_CODE"))) {
            this.messageBox_("请选择职业");
            return false;
        }
        if ("".equals(this.getValueString("ADDRESS"))) {
            this.messageBox_("请输入现住址");
            return false;
        }
        if ("".equals(this.getValueString("RESID_ADDRESS"))) {
            this.messageBox_("请输入户籍地址");
            return false;
        }
        if ("".equals(this.getValueString("CONTACTS_NAME"))) {
            this.messageBox_("请输入联系人姓名");
            return false;
        }
        if ("".equals(this.getValueString("RELATION_CODE"))) {
            this.messageBox_("请选择关系");
            return false;
        }
        if ("".equals(this.getValueString("CONTACTS_TEL"))) {
            this.messageBox_("请输入联系人电话");
            return false;
        }
        if ("".equals(this.getValueString("CONTACTS_ADDRESS"))) {
            this.messageBox_("请输入联系人地址");
            return false;
        }*/
		return true;
	}

	/**
	 * 调用病患信息界面
	 */
	public void onPatInfo() {
		TParm parm = new TParm();
		parm.setData("ADM", "ADM");
		parm.setData("MR_NO", this.getValueString("MR_NO").trim());
		this.openDialog("%ROOT%\\config\\sys\\SYSPatInfo.x", parm);
	}

	/**
	 * 检查是否住院中 false 未住院 true 住院中
	 * 
	 * @param MrNo
	 *            String
	 * @return boolean
	 */
	public boolean checkAdmInp(String MrNo) {
		TParm parm = new TParm();
		parm.setData("MR_NO", MrNo);
		TParm result = ADMInpTool.getInstance().checkAdmInp(parm);
		if (result.checkEmpty("IPD_NO", result))
			return false;
		caseNo = result.getData("CASE_NO", 0).toString();
		return true;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		pat = new Pat();
		caseNo = "";
		McaseNo = "";
		saveType = "NEW";
		BED_NO = "";
		TPanel photo = (TPanel) this.getComponent("VIEW_PANEL");
		Image image = null;
		Pic pic = new Pic(image);
		pic.setSize(photo.getWidth(), photo.getHeight());
		pic.setLocation(0, 0);
		photo.removeAll();
		photo.add(pic);
		pic.repaint();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		String date = df.format(SystemTool.getInstance().getDate());
		setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // 预定日期
		setValue("IN_COUNT", "1"); // 预定日期
		this.setValue("NEW_BORN_FLG", "N");
		callFunction("UI|NEW_PAT|setEnabled", false); // 病患保存
		this.callFunction("UI|NEW_PAT|setText", "新增病患保存");
		callFunction("UI|MR_NO|setEnabled", true); // 病案号
		callFunction("UI|IPD_NO|setEnabled", true); // 住院号
		clearValue("MR_NO");
		clearValue("ADM_SOURCE;PR_DEPT_CODE;OPD_DR_CODE;TOTAL_BILPAY;SERVICE_LEVEL;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;YELLOW_SIGN;RED_SIGN;DEPT_CODE;VS_DR_CODE;PATIENT_CONDITION;DEPT_CODE;BED_NO;BED_DESC;STATION_CODE;DAY_OPE_FLG");
		clearValue("RESID_ADDRESS;MR_NO;IPD_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;AGE;HOMEPLACE_CODE;MARRIAGE_CODE;OCC_CODE;SPECIES_CODE;tComboBox_0;NATION_CODE;IDNO;COMPANY_DESC;TEL_01;POST_CODE;RESID_ROAD;RESID_POST_CODE;CONTACTS_NAME;CONTACTS_ADDRESS;CONTACTS_TEL;NEW_PAT;RELATION_CODE");
		clearValue("PAT_CTZ;;TEL_COMPANY;TEL_HOME;M_MR_NO;M_NAME;FOREIGNER_FLG;ADDRESS");
		clearValue("ADDRESS_COMPANY;POST_COMPANY;AGN_CODE;AGN_INTENTION;BIRTHPLACE");
		
		// this.setValue("POST_C", "");
		// this.setValue("POST_P", "");
		// ((TComboBox) this.getComponent("POST_P")).onQuery();
		// ((TComboBox) this.getComponent("POST_C")).onQuery();
		// this.setValue("RESID_POST_C", "");
		// this.setValue("RESID_POST_P", "");
		// ((TComboBox) this.getComponent("RESID_POST_P")).onQuery();
		// ((TComboBox) this.getComponent("RESID_POST_C")).onQuery();
		// this.setValue("COMPANY_POST_P", "");
		// this.setValue("COMPANY_POST_C", "");
		// ((TComboBox) this.getComponent("COMPANY_POST_P")).onQuery();
		// ((TComboBox) this.getComponent("COMPANY_POST_C")).onQuery();
		this.setValue("OPD_DEPT_CODE", "");
		this.setValue("OPD_DR_CODE", "");
		this.setValue("DEPT_CODE", "");
		this.setValue("STATION_CODE", "");
		this.setValue("VS_DR_CODE", "");
		this.setValue("DAY_OPE_FLG", "");
		setUIT();
		setMenu(false);
		this.callFunction("UI|LM1|setVisible", false);
		this.callFunction("UI|LM2|setVisible", false);
		this.callFunction("UI|M_MR_NO|setVisible", false);
		this.callFunction("UI|M_NAME|setVisible", false);
		callFunction("UI|AGN_CODE|setEnabled", false); // 31天内再次住院等级
		callFunction("UI|AGN_INTENTION|setEnabled", false); // 31天内再次住院等级
		callFunction("UI|PHOTO_BOTTON|setEnabled", false);
		//callFunction("UI|DAY_OPE_FLG|setEnabled", true);
	}

	/**
	 * 病患住院中控件不可编辑
	 */
	public void setUIAdmF() {
		callFunction("UI|ADM_SOURCE|setEnabled", false);
		callFunction("UI|OPD_DEPT_CODE|setEnabled", false);
		callFunction("UI|OPD_DR_CODE|setEnabled", false);
		callFunction("UI|PATIENT_CONDITION|setEnabled", false);
		callFunction("UI|SERVICE_LEVEL|setEnabled", false);
		callFunction("UI|CTZ1_CODE|setEnabled", false);
		callFunction("UI|CTZ2_CODE|setEnabled", false);
		callFunction("UI|CTZ3_CODE|setEnabled", false);
		callFunction("UI|tNumberTextField_3|setEnabled", false);
		callFunction("UI|DEPT_CODE|setEnabled", false);
		callFunction("UI|STATION_CODE|setEnabled", false);
		callFunction("UI|VS_DR_CODE|setEnabled", false);
		callFunction("UI|ATTEND_DR_CODE|setEnabled", false);
		callFunction("UI|TOTAL_BILPAY|setEnabled", false);
		callFunction("UI|tButton_0|setEnabled", false);
	//	callFunction("UI|DAY_OPE_FLG|setEnabled", false);
	}

	/**
	 * 控件可编辑
	 */
	public void setUIT() {
		callFunction("UI|ADM_SOURCE|setEnabled", true);
		callFunction("UI|OPD_DEPT_CODE|setEnabled", true);
		callFunction("UI|OPD_DR_CODE|setEnabled", true);
		callFunction("UI|PATIENT_CONDITION|setEnabled", true);
		callFunction("UI|SERVICE_LEVEL|setEnabled", true);
		callFunction("UI|CTZ1_CODE|setEnabled", true);
		callFunction("UI|CTZ2_CODE|setEnabled", true);
		callFunction("UI|CTZ3_CODE|setEnabled", true);
		callFunction("UI|tNumberTextField_3|setEnabled", true);
		callFunction("UI|DEPT_CODE|setEnabled", true);
		callFunction("UI|STATION_CODE|setEnabled", true);
		callFunction("UI|VS_DR_CODE|setEnabled", true);
		callFunction("UI|ATTEND_DR_CODE|setEnabled", true);
		callFunction("UI|TOTAL_BILPAY|setEnabled", true);
		callFunction("UI|tButton_0|setEnabled", true);
	}

	/**
	 * 查询住院中病患基本信息
	 */
	public void inInpdata() {
		TParm parm = new TParm();
		parm.setData("MR_NO", getValue("MR_NO"));
		parm.setDataN("IPD_NO", getValue("IPD_NO"));
		// 查询在院病患的基本信息
		TParm result = ADMInpTool.getInstance().queryCaseNo(parm);
		TParm resvParm = new TParm();
		resvParm.setData("MR_NO", getValue("MR_NO"));
		resvParm.setData("IN_CASE_NO", result.getData("CASE_NO", 0));
		//modify by yangjj 20150806
		//TParm resv = ADMResvTool.getInstance().selectAll(resvParm);
		
		String resvSql = " SELECT " +
							" OPD_DEPT_CODE, " +
							" PATIENT_CONDITION " +
						 " FROM " +
						 	" ADM_RESV " +
						 " WHERE " +
						 	" CAN_DATE IS NULL " +
						 	" AND MR_NO = '"+getValue("MR_NO")+"' " +
						 	" AND IN_CASE_NO = '"+result.getData("CASE_NO", 0)+"'";
		TParm resv = new TParm(TJDODBTool.getInstance().select(resvSql));
		
		this.setValue("OPD_DEPT_CODE", resv.getData("OPD_DEPT_CODE", 0));
		this.setValue("PATIENT_CONDITION", resv.getData("PATIENT_CONDITION", 0));
		BED_NO = result.getValue("BED_NO", 0);
		IPD_NO = result.getValue("IPD_NO", 0);
		this.setValue("IPD_NO", result.getValue("IPD_NO", 0));
		this.setValue("OPD_DR_CODE", result.getData("OPD_DR_CODE", 0));
		this.setValue("ADM_SOURCE", result.getData("ADM_SOURCE", 0));
		this.setValue("TOTAL_BILPAY", result.getData("TOTAL_BILPAY", 0));
		this.setValue("SERVICE_LEVEL", result.getData("SERVICE_LEVEL", 0));
		this.setValue("CTZ1_CODE", result.getData("CTZ1_CODE", 0));
		this.setValue("CTZ2_CODE", result.getData("CTZ2_CODE", 0));
		this.setValue("CTZ3_CODE", result.getData("CTZ3_CODE", 0));
		this.setValue("DEPT_CODE", result.getData("DEPT_CODE", 0));
		this.setValue("STATION_CODE", result.getData("STATION_CODE", 0));
		this.setValue("VS_DR_CODE", result.getData("VS_DR_CODE", 0));
		this.setValue("YELLOW_SIGN", result.getData("YELLOW_SIGN", 0));
		this.setValue("RED_SIGN", result.getData("RED_SIGN", 0));
		this.setValue("IN_DATE", result.getData("IN_DATE", 0));
		this.setValue("NEW_BORN_FLG", result.getBoolean("NEW_BORN_FLG", 0));
		this.setValue("IN_COUNT", result.getData("IN_COUNT", 0));
		this.setValue("AGN_CODE", result.getData("AGN_CODE", 0)); // 31天再住院等级
		this.setValue("AGN_INTENTION", result.getData("AGN_INTENTION", 0)); // 31天再住院原因
		this.setValue("BED_NO", getBedDesc(BED_NO));
		//this.setValue("DAY_OPE_FLG",  parm.getBoolean("DAY_OPE_FLG", 0)); // 日间手术
		
		((TCheckBox)this.getComponent("DAY_OPE_FLG")).setSelected("Y".equals(result.getValue("DAY_OPE_FLG", 0)) ? true : false);	//   日间手术
		
		
		TParm result1 = CLPSingleDiseTool.getInstance().queryADMResvSDInfo(parm);// add by wanglong 20121025
		this.setValueForParm("DISE_CODE", result1, 0);// add by wanglong
														// 20121025
		// 判断该病患是否是新生儿
		if (result.getBoolean("NEW_BORN_FLG", 0)) {
			McaseNo = result.getValue("M_CASE_NO", 0); // 查询预约信息中是否有母亲的病案号
			// 查询母亲的住院信息 获取住院号(婴儿的IPD_NO与母亲相同)
			TParm admParm = new TParm();
			admParm.setData("CASE_NO", McaseNo);
			TParm admInfo = ADMTool.getInstance().getADM_INFO(admParm);
			Pat M_PAT = Pat.onQueryByMrNo(admInfo.getValue("MR_NO", 0));
			this.setValue("M_MR_NO", admInfo.getValue("MR_NO", 0));
			this.setValue("M_NAME", M_PAT.getName());
			this.callFunction("UI|LM1|setVisible", true);
			this.callFunction("UI|LM2|setVisible", true);
			this.callFunction("UI|M_MR_NO|setVisible", true);
			this.callFunction("UI|M_NAME|setVisible", true);
		} else {
			this.callFunction("UI|LM1|setVisible", false);
			this.callFunction("UI|LM2|setVisible", false);
			this.callFunction("UI|M_MR_NO|setVisible", false);
			this.callFunction("UI|M_NAME|setVisible", false);
		}
	}

	/**
	 * 计算年龄
	 */
	public void setBirth() {
		if (getValue("BIRTH_DATE") == null || "".equals(getValue("BIRTH_DATE")))
			return;
		String AGE = com.javahis.util.StringUtil.showAge(
				(Timestamp) getValue("BIRTH_DATE"),
				(Timestamp) getValue("IN_DATE"));
		setValue("AGE", AGE);
	}

	/**
	 * 新增病患控件可编辑
	 */
	public void setUi() {
		callFunction("UI|PAT_NAME|setEnabled", true);
		callFunction("UI|SEX_CODE|setEnabled", true);
		callFunction("UI|BIRTH_DATE|setEnabled", true);
		callFunction("UI|AGE|setEnabled", true);
		callFunction("UI|OCC_CODE|setEnabled", true);
		callFunction("UI|BORN|setEnabled", true);
		callFunction("UI|IDNO|setEnabled", true);
		callFunction("UI|SPECIES_CODE|setEnabled", true);
		callFunction("UI|NATION_CODE|setEnabled", true);
		callFunction("UI|MARRIAGE_CODE|setEnabled", true);
		callFunction("UI|COMPANY_DESC|setEnabled", true);
		callFunction("UI|POST_CODE|setEnabled", true);
		callFunction("UI|RESID_POST_CODE|setEnabled", true);
		callFunction("UI|TEL_01|setEnabled", true);
		callFunction("UI|RESID_ROAD|setEnabled", true);
		callFunction("UI|CONTACTS_NAME|setEnabled", true);
		callFunction("UI|RELATION_CODE|setEnabled", true);
		callFunction("UI|CONTACTS_TEL|setEnabled", true);
		callFunction("UI|CONTACTS_ADDRESS|setEnabled", true);

	}

	/**
	 * 新增病患点选事件
	 */
	public void onNewPatInfo() {
		this.onClear();
		TCheckBox checkbox = (TCheckBox) this
				.callFunction("UI|NEW_PAT_INFO|getThis");
		if (checkbox.isSelected()) {
			callFunction("UI|NEW_BORN_FLG|setEnabled", false);
			callFunction("UI|MR_NO|setEnabled", false);
			callFunction("UI|IPD_NO|setEnabled", false);
			callFunction("UI|NEW_PAT|setEnabled", true);
			this.callFunction("UI|NEW_PAT|setText", "新增病患保存");
		} else {
			callFunction("UI|NEW_BORN_FLG|setEnabled", true);
			callFunction("UI|MR_NO|setEnabled", true);
			callFunction("UI|IPD_NO|setEnabled", true); // 住院号
			callFunction("UI|NEW_PAT|setEnabled", false);
			this.callFunction("UI|NEW_PAT|setText", "修改病患保存");
		}
	}

	/**
	 * 床位检索
	 */
	public void onBedNo() {
		TParm sendParm = new TParm();
		if (getValue("DEPT_CODE") == null || "".equals(getValue("DEPT_CODE"))) {
			this.messageBox("请选择科室！");
			return;
		}
		if (getValue("STATION_CODE") == null
				|| "".equals(getValue("STATION_CODE"))) {
			this.messageBox("请选择病区！");
			return;
		}
		sendParm.setData("DEPT_CODE", getValue("DEPT_CODE"));
		sendParm.setData("STATION_CODE", getValue("STATION_CODE"));
		sendParm.setData("TYPE", "RESV"); // ===== chenxi modify 20130301
											// 预约床时，占床也可预约
		sendParm.setData("HAVEBEDNO",haveBedNo);
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMQueryBed.x", sendParm);
		if (reParm != null) {
			this.setValue("BED_NO", getBedDesc(reParm.getValue("BED_NO", 0)));
			this.setValue("YELLOW_SIGN", reParm.getData("YELLOW_SIGN", 0));
			this.setValue("RED_SIGN", reParm.getData("RED_SIGN", 0));
		}
	}

	/**
	 * 包床管理
	 */
	public void onBed() {
		if (this.getValue("BED_DESC") == null
				|| "".equals(this.getValue("BED_DESC"))) {
			this.messageBox("此病患还未安排床位！");
			return;
		}
		TParm bed = new TParm();

		TParm parm = new TParm();
		parm.setData("MR_NO", getValue("MR_NO"));
		parm.setData("IPD_NO", getValue("IPD_NO"));
		TParm result = ADMInpTool.getInstance().queryCaseNo(parm);
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", result.getData("CASE_NO", 0));
		sendParm.setData("MR_NO", result.getData("MR_NO", 0));
		sendParm.setData("IPD_NO", result.getData("IPD_NO", 0));
		sendParm.setData("DEPT_CODE", getValue("DEPT_CODE"));
		sendParm.setData("STATION_CODE", getValue("STATION_CODE"));
		sendParm.setData("BED_NO", getValue("BED_DESC"));
		bed.setData("BED_NO", getValue("BED_DESC"));
		TParm check = SYSBedTool.getInstance().queryRoomBed(bed);
		String caseNo = result.getData("CASE_NO", 0).toString().trim();
		int count = check.getCount("BED_NO");

		for (int i = 0; i < count; i++) {
			if ("Y".equals(check.getData("ALLO_FLG", i))
					&& !caseNo.equals(check.getData("CASE_NO", i))) {
				this.messageBox("此病房已有其他病患！");
				return;
			}
		}
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMSysBedAllo.x", sendParm);
	}

	/**
	 * 绿色通道
	 */
	public void onGreenPath() {
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", "O");
		parm.setData("MR_NO", getValue("MR_NO"));
		this.openWindow("%ROOT%\\config\\bil\\BILGreenPath.x", parm);
	}

	/**
	 * 预交金
	 */
	public void onBilpay() {
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", caseNo);
		this.openWindow("%ROOT%\\config\\bil\\BILPay.x", sendParm);
		TParm parm = ADMInpTool.getInstance().queryCaseNo(sendParm);
		this.setValue("TOTAL_BILPAY", parm.getData("TOTAL_BILPAY", 0));

	}

	/**
	 * 新生儿注记 (移到预约住院此方法暂时无用了)
	 */
	public void onChild() {
		if (pat == null) {
			this.messageBox("没有病患信息！");
			return;
		}
		TParm sendParm = new TParm();
		sendParm.setData("MR_NO", this.getValue("MR_NO"));
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMBabyFlg.x", sendParm);
		if (reParm == null) {
			setValue("NEW_BORN_FLG", "N");
			return;
		}
		if (reParm.checkEmpty("IPD_NO", reParm)) {
			setValue("NEW_BORN_FLG", "N");
		} else {
			setValue("NEW_BORN_FLG", "Y");
			this.setValue("IPD_NO", reParm.getData("IPD_NO"));
			McaseNo = reParm.getData("M_CASE_NO").toString();
		}
	}

	/**
	 * 病患查询
	 */
	public void onQuery() {
		TParm sendParm = new TParm();
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMPatQuery.x", sendParm);
		if (reParm == null)
			return;
		this.setValue("MR_NO", reParm.getValue("MR_NO"));
		this.onMrno();
	}

	/**
	 * 取消住院
	 */
	public void onStop() {
		if (!checkCanInp()) {
			this.messageBox_("此病患已经入住到床位,不可取消住院");
			return;
		}
		if (!checkBilPay()) {
			this.messageBox_("此病患还有预交金未退,不可取消住院");
			return;
		}
		//fux modify 2010805  
		if (!checkCanPay()) {
			this.messageBox_("此病患已经产生费用,不可取消住院");
			return;
		}

		int check = this.messageBox("消息", "是否取消？", 0);
		if (check == 0) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			parm.setData("PSF_KIND", "INC");
			parm.setData("PSF_HOSP", "");
			parm.setData("CANCEL_FLG", "Y");
			parm.setData("CANCEL_DATE", SystemTool.getInstance().getDate());
			parm.setData("CANCEL_USER", Operator.getID());
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			// ======pangben modify 20110617 start
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				parm.setData("REGION_CODE", Operator.getRegion());
			}
			// ======pangben modify 20110617 start
			TParm result = TIOM_AppServer.executeAction(
					"action.adm.ADMInpAction", "ADMCanInp", parm); //
			if (result.getErrCode() < 0) {
				this.messageBox("E0005");
			} else {
				this.messageBox("P0005");
				
				// add by wangb 2016/2/2 取消住院发送大屏消息 START
				TParm xmlParm = ADMXMLTool.getInstance().creatADMXMLFile(caseNo, "A04");
	            if (xmlParm.getErrCode() < 0) {
	                this.messageBox("信息看板接口发送失败 " + xmlParm.getErrText());
	            }
	            xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(caseNo);
	            if (xmlParm.getErrCode() < 0) {
	                this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
	            }
	            // add by wangb 2016/2/2 取消住院发送大屏消息 END
			}
			this.setMenu(false);
		} else {
			this.setMenu(true);
			return;
		}

	}

	/**
	 * 拍照
	 * 
	 * @throws IOException
	 */
	public void onPhoto() throws IOException {

		String mrNo = getValue("MR_NO").toString();
		String photoName = mrNo + ".jpg";
		String dir = TIOM_FileServer.getPath("PatInfPIC.LocalPath");
		new File(dir).mkdirs();
		JMStudio jms = JMStudio.openCamera(dir + photoName);
		jms.addListener("onCameraed", this, "sendpic");
	}

	/**
	 * //注册照相组件
	 */
	public void onRegist() {
		// 注册照相组件
		JMFRegistry jmfr = new JMFRegistry();
		jmfr.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent event) {
				event.getWindow().dispose();
				System.exit(0);
			}

		});
		jmfr.setVisible(true);

	}

	/**
	 * 传送照片
	 * 
	 * @param image
	 *            Image
	 */
	public void sendpic(Image image) {
		String mrNo = getValue("MR_NO").toString();
		String photoName = mrNo + ".jpg";
		String dir = TIOM_FileServer.getPath("PatInfPIC.LocalPath");
		String localFileName = dir + photoName;
		try {
			File file = new File(localFileName);
			byte[] data = FileTool.getByte(localFileName);
			if (file.exists()) {
				new File(localFileName).delete();
			}
			String root = TIOM_FileServer.getRoot();
			dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
			dir = root + dir + mrNo.substring(0, 3) + "\\"
					+ mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
			TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(), dir
					+ photoName, data);
		} catch (Exception e) {
			System.out.println("e::::" + e.getMessage());
		}
		this.viewPhoto(pat.getMrNo());

	}

	/**
	 * 显示photo
	 * 
	 * @param mrNo
	 *            String 病案号
	 */
	public void viewPhoto(String mrNo) {

		String photoName = mrNo + ".jpg";
		String fileName = photoName;
		try {
			TPanel viewPanel = (TPanel) getComponent("VIEW_PANEL");
			String root = TIOM_FileServer.getRoot();
			String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
			dir = root + dir + mrNo.substring(0, 3) + "\\"
					+ mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";

			byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					dir + fileName);
			if (data == null) {
				viewPanel.removeAll();
				return;
			}
			double scale = 0.5;
			boolean flag = true;
			Image image = ImageTool.scale(data, scale, flag);
			// Image image = ImageTool.getImage(data);
			Pic pic = new Pic(image);
			pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
			pic.setLocation(0, 0);
			viewPanel.removeAll();
			viewPanel.add(pic);
			pic.repaint();
		} catch (Exception e) {
		}
	}

	class Pic extends JLabel {
		Image image;

		public Pic(Image image) {
			this.image = image;
		}

		public void paint(Graphics g) {
			g.setColor(new Color(161, 220, 230));
			g.fillRect(4, 15, 100, 100);
			if (image != null) {
				g.drawImage(image, 4, 15, null);

			}
		}
	}

	/**
	 * 住院科别Combo事件
	 */
	public void onDEPT_CODE() {
		// 清空住院病区，经治医师，床位号的选中值
		this.clearValue("STATION_CODE;VS_DR_CODE;BED_NO;BED_DESC");
	}

	/**
	 * 门诊科别Combo事件
	 */
	public void onOPD_DEPT_CODE() {
		// 清空门诊医师的选中值
		this.clearValue("OPD_DR_CODE");
	}

	/**
	 * 病案处理
	 * 
	 * @param type
	 *            String new:新建 update:修改
	 */
	public void addMRO(String type) {
		String user_id = Operator.getID();
		String user_ip = Operator.getIP();
		String mr_no = this.getValueString("MR_NO");
		String hospid = "";
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		if (regionParm.getCount() > 0) {
			hospid = regionParm.getValue("NHI_NO", 0);
		}
		// System.out.println("------------------"+hospid);
		TParm result = new TParm();
		// 判断是否新建病案
		if ("new".equals(type)) {
			TParm creat = new TParm();
			creat.setData("MR_NO", mr_no);
			creat.setData("CASE_NO", caseNo);
			creat.setData("OPT_USER", user_id);
			creat.setData("OPT_TERM", user_ip);
			creat.setData("DAY_OPE_FLG", this.getValue("DAY_OPE_FLG"));			
			// ============pangben modify 20110617 start
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0)
				creat.setData("REGION_CODE", Operator.getRegion());
			// ============pangben modify 20110617 stop
			creat.setData("HOSP_ID", hospid);
			// 新建病案
			result = MROTool.getInstance().insertMRO(creat);
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return;
			}
			// 判断该病案号是否已经存在病案主档 如果存在就不再插入
			if (!MROQueueTool.getInstance().checkHasMRO_MRV(mr_no)) {
				TParm mro_mrv = new TParm();
				String region = Operator.getRegion();
				mro_mrv.setData("MR_NO", mr_no);
				mro_mrv.setData("IPD_NO", IPD_NO);
				mro_mrv.setData("CREATE_HOSP", region);
				mro_mrv.setData("IN_FLG", "2");
				mro_mrv.setData("CURT_HOSP", region);
				mro_mrv.setData("CURT_LOCATION", region);
				mro_mrv.setData("TRAN_HOSP", region);
				mro_mrv.setData("BOX_CODE", "");
				mro_mrv.setData("OPT_USER", Operator.getID());
				mro_mrv.setData("OPT_TERM", Operator.getIP());
				result = MROQueueTool.getInstance().insertMRO_MRV(mro_mrv);
				if (result.getErrCode() < 0) {
					this.messageBox_("病历入库失败！");
				}
			}
			// 查询病患的预约信息 用来获取门急诊诊断
			TParm resv = ADMResvTool.getInstance().selectNotIn(mr_no);
			String OE_DIAG_CODE = "";
			// 如果病患有预约信息
			if (resv.getCount() > 0) {
				OE_DIAG_CODE = resv.getValue("DIAG_CODE", 0);
			}
			TParm b_parm = new TParm();
			b_parm.setData("BED_NO", BED_NO);
			TParm bed = SYSBedTool.getInstance().queryAll(b_parm);
			// 修改病案 住院信息
			TParm adm = new TParm();
			adm.setData("IPD_NO", this.getValueString("IPD_NO"));
			adm.setData("IN_DATE", StringTool.getString(
					(Timestamp) this.getValue("IN_DATE"), "yyyyMMddHHmmss"));
			adm.setData("IN_DEPT", this.getValueString("DEPT_CODE"));
			adm.setData("IN_STATION", this.getValueString("STATION_CODE"));
			adm.setData("IN_ROOM_NO", bed.getValue("ROOM_CODE", 0)); // 入院病室
			// 根据床位号
			// 查询出
			adm.setData("OE_DIAG_CODE", OE_DIAG_CODE); // 门急诊诊断
			adm.setData("IN_CONDITION", this.getValue("PATIENT_CONDITION")); // 入院状态
			adm.setData("IN_COUNT", this.getValue("IN_COUNT") == null ? "1"
					: this.getValue("IN_COUNT")); // 住院次数
			adm.setData("PG_OWNER", Operator.getID()); // 首页建立者
			adm.setData("STATUS", "0"); // 状态 0 在院；1 出院未完成；2 出院已完成
			adm.setData("CASE_NO", caseNo);
			adm.setData("ADM_SOURCE", this.getValue("ADM_SOURCE")); // 病患来源
			adm.setData("AGN_CODE", this.getValue("AGN_CODE") == null ? ""
					: this.getValue("AGN_CODE")); // 31天再住院
			adm.setData("AGN_INTENTION", this.getValue("AGN_INTENTION")); // 31天再住院原因
			adm.setData("DAY_OPE_FLG",this.getValue("DAY_OPE_FLG"));	
			// System.out.println("-=-------------------" + adm);
			result = MROTool.getInstance().updateADMData(adm);
			if (result.getErrCode() < 0) {
				this.messageBox_(result.getErrText());
			}
		}

		// 修改病案 患者基本信息
		TParm opt = new TParm();
		opt.setData("MR_NO", mr_no);
		opt.setData("CASE_NO", caseNo);
		opt.setData("OPT_USER", user_id);
		opt.setData("OPT_TERM", user_ip);
		result = MROTool.getInstance().updateMROPatInfo(opt);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
		}
	}

	/**
	 * 根据床位code
	 * 
	 * @param Bed_Code
	 *            String
	 * @return String
	 */
	private String getBedDesc(String Bed_Code) {
		this.setValue("BED_DESC", Bed_Code);
		TComboBox combo = (TComboBox) this.getComponent("BED_DESC");
		return combo.getSelectedName();
	}

    /**
     * 打印住院证
     */
    public void onPrint() {
		if (StringUtils.isEmpty(getValueString("MR_NO"))) {
			return;
		}
    	
        String caseNo = "";
        //this.messageBox("mrNo===="+pat.getMrNo());
        TParm resv = ADMResvTool.getInstance().selectNotIn(pat.getMrNo());
        //this.messageBox("======resv========"+resv);
        TParm mrParm = new TParm();
        mrParm.setData("MR_NO", pat.getMrNo());
        //TParm result = ADMInpTool.getInstance().checkAdmInp(mrParm);
        //this.messageBox("result============"+result);
        //if (result.getCount() < 0) {
        //    messageBox("该病患未入院！");
        //    return;
        //}
        //预约住院号
        caseNo = resv.getValue("RESV_NO", 0);        
        String subClassCode = TConfig.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
        String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
        
        // 已预约已入住
        if (checkAdmInp(pat.getMrNo())) {
        	caseNo = this.caseNo;
        }

        String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO='" + caseNo + "'";
        sql += " AND CLASS_CODE='" + classCode + "' AND  SUBCLASS_CODE='" + subClassCode + "'";

//        System.out.println("===sql===" + sql);
        TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
        if (result1.getErrCode() < 0) {
            this.messageBox("E0005");
            return;
        }
        if (result1.getCount() < 0) {
            this.onPrint1();
        } else {
            String filePath = result1.getValue("FILE_PATH", 0);
            String fileName = result1.getValue("FILE_NAME", 0);
            TParm p = new TParm();
            p.setData("RESV_NO", caseNo);
            TParm resvPrint = ADMResvTool.getInstance().selectFroPrint(p);
            TParm parm = new TParm();
            parm.setData("MR_NO", pat.getMrNo());
            parm.setData("IPD_NO", pat.getIpdNo());
            parm.setData("PAT_NAME", pat.getName());
            parm.setData("SEX", pat.getSexString());
            parm.setData("AGE", StringUtil.showAge(pat.getBirthday(),
                    resvPrint.getTimestamp("APP_DATE", 0))); //年龄
            //parm.setData("CASE_NO", caseNo);
            parm.setData("CASE_NO", caseNo);//duzhw add
            Timestamp ts = SystemTool.getInstance().getDate();
            parm.setData("ADM_TYPE", "O");
            parm.setData("DEPT_CODE", resvPrint.getValue("DEPT_CODE", 0));
            parm.setData("STATION_CODE", resvPrint.getValue("STATION_CODE", 0));
            
//        	this.setDayOpeFlg("Y".equals(resvPrint.getValue("DAY_OPE_FLG",0)) ? "日间手术": "");			//   2017/3/25   	by  yanmm   增加日间手术勾选		
//    		parm.setData("DAY_OPE_FLG","Y".equals(resvPrint.getValue("DAY_OPE_FLG",0)) ? "日间手术": "") ;

 
            parm.setData("ADM_DATE", ts);
            parm.setData("STYLETYPE", "1");
            parm.setData("RULETYPE", "3");
            parm.setData("SYSTEM_TYPE", "ODO");
            TParm emrFileData = new TParm();
            emrFileData.setData("FILE_PATH", filePath);
            emrFileData.setData("FILE_NAME", fileName);
            emrFileData.setData("FILE_SEQ", result1.getValue("FILE_SEQ", 0));
            emrFileData.setData("SUBCLASS_CODE", subClassCode);
            emrFileData.setData("CLASS_CODE", classCode);
            emrFileData.setData("FLG", true);
            parm.setData("EMR_FILE_DATA", emrFileData);
    	//	parm.addListener("EMR_LISTENER",this,"emrListener");				
            this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
        }
    }

    public void onPrint1() {
        //获取预约号
        String sql = " select * from adm_resv where mr_no = '"+MR_NO+"' AND CAN_DATE IS NULL AND IN_CASE_NO IS NULL ORDER BY RESV_NO DESC ";
        TParm resvParm = new TParm(TJDODBTool.getInstance().select(sql));
        String resvNo = resvParm.getValue("RESV_NO", 0);
        TParm myParm = new TParm();
        myParm.setData("CASE_NO", caseNo);
        TParm casePrint = ADMInpTool.getInstance().selectall(myParm);
        TParm actionParm = new TParm();
        this.setDayOpeFlg("Y".equals(resvParm.getValue("DAY_OPE_FLG",0)) ? "日间手术":"");			//   2017/3/25   	by  yanmm   增加日间手术勾选		
        actionParm.setData("DAY_OPE_FLG","Y".equals(resvParm.getValue("DAY_OPE_FLG",0)) ? "日间手术":"");
        actionParm.setData("MR_NO", pat.getMrNo());
        actionParm.setData("IPD_NO", pat.getIpdNo());
        actionParm.setData("PAT_NAME", pat.getName());
        actionParm.setData("SEX", pat.getSexString());
        actionParm.setData("AGE", StringUtil.showAge(pat.getBirthday(), casePrint.getTimestamp("IN_DATE", 0))); //年龄
        Timestamp ts = SystemTool.getInstance().getDate();
        //actionParm.setData("CASE_NO", caseNo);
        actionParm.setData("CASE_NO", resvNo); //duzhw add
        actionParm.setData("ADM_TYPE", "O");
        actionParm.setData("DEPT_CODE", casePrint.getValue("DEPT_CODE", 0));
        actionParm.setData("STATION_CODE", casePrint.getValue("STATION_CODE", 0));
        actionParm.setData("ADM_DATE", ts);
        actionParm.setData("STYLETYPE", "1");
        actionParm.setData("RULETYPE", "3");
        actionParm.setData("SYSTEM_TYPE", "ODO");
        TParm emrFileData = new TParm();
        String path = TConfig.getSystemValue("ADMEmrINHOSPPATH");
        String fileName = TConfig.getSystemValue("ADMEmrINHOSPFILENAME");
        String subClassCode = TConfig.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
        String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
        emrFileData.setData("TEMPLET_PATH", path);
        emrFileData.setData("EMT_FILENAME", fileName);
        emrFileData.setData("SUBCLASS_CODE", subClassCode);
        emrFileData.setData("CLASS_CODE", classCode);
        actionParm.setData("EMR_FILE_DATA", emrFileData);
        actionParm.addListener("EMR_LISTENER",this,"emrListener");			//   2017/3/25   	by  yanmm   增加日间手术勾选	
        this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", actionParm);
    }

    /* 事件
    * @param parm TParm
    */
   public void emrListener(TParm parm)					//   2017/3/25   	by  yanmm   增加日间手术勾选		
   {
       parm.runListener("setCaptureValue","DAY_OPE_FLG",this.getDayOpeFlg());
       
   }
    
    
	/**
	 * 向对应的护士站发送消息
	 * 
	 * @param MR_NO
	 *            String 病案号
	 * @param CASE_NO
	 *            String 就诊序号
	 * @param PAT_NAME
	 *            String 患者姓名
	 */
	public void sendInwStationMessages(String MR_NO, String CASE_NO,
			String PAT_NAME) {

		// $$ ============ Modified by lx 医生按部门,护士按病区收发消息2012/02/27
		// START==================$$//
		// SocketLink client1 = SocketLink.running("", "ODISTATION", "odi");
		SocketLink client1 = SocketLink.running("", Operator.getDept(),
				Operator.getDept());
		if (client1.isClose()) {
			out(client1.getErrText());
			return;
		}
		/**
		 * client1.sendMessage("INWSTATION", "CASE_NO:" + CASE_NO +
		 * "|STATION_CODE:" + Operator.getStation() + "|MR_NO:" + MR_NO +
		 * "|PAT_NAME:" + PAT_NAME);
		 **/
		client1.sendMessage(Operator.getStation(), "CASE_NO:" + CASE_NO
				+ "|STATION_CODE:" + Operator.getStation() + "|MR_NO:" + MR_NO
				+ "|PAT_NAME:" + PAT_NAME);

		if (client1 == null)
			return;
		client1.close();

		// $$ ============ Modified by lx 医生按部门,护士按病区收发消息2012/02/27
		// END==================$$//

	}

	/**
	 * 模糊查询（内部类） 诊断中文替换
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");

		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER
					: dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ICD_CODE");
			Vector d = (Vector) parm.getData("ICD_CHN_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}

	/**
	 * 新生儿免疫
	 */
	public void onImmunity() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", MR_NO);
		parm.setData("IPD_NO", IPD_NO);
		this.openDialog("%ROOT%\\config\\adm\\ADMChildImmunity.x", parm);

	}

	/**
	 * 检查病患基本信息是否可以提交
	 * 
	 * @return boolean
	 */
	private boolean checkPatInfo() {
		// emr结构化病历需要给别字段必填
		if (this.getValueString("TEL_HOME").length() <= 0) {
			this.messageBox_("请填写家庭电话！");
			this.grabFocus("TEL_HOME");
			return false;
		}
		if (this.getValueString("RESID_ADDRESS").length() <= 0) {
			this.messageBox_("请填写户口地址！");
			this.grabFocus("RESID_ADDRESS");
			return false;
		}
		return true;
	}

	/**
	 * 查询病人的住院次数
	 * 
	 * @param mrNo
	 *            String
	 * @return int
	 */
	private int getInCount(String mrNo) {
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		parm.setData("CANCEL_FLG", "N");
		TParm result = ADMInpTool.getInstance().selectall(parm); // 查询该病人的所有未取消的住院信息
		if (result.getErrCode() < 0) {
			return 1;
		}
		return result.getCount();
	}

	/**
	 * 腕带打印
	 */
	public void onWrist() {
		if (this.getValueString("MR_NO").length() == 0 || pat == null) {
			return;
		}
		TParm print = new TParm();
		print.setData("Barcode", "TEXT", pat.getMrNo());
		print.setData("PatName", "TEXT", pat.getName());
		print.setData("Sex", "TEXT", pat.getSexString());
		print.setData("BirthDay", "TEXT",
				StringTool.getString(pat.getBirthday(), "yyyy/MM/dd"));
		// this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMWrist", print);
		
		// 查询一期临床受试者编号
		String recruitNo = MEDApplyTool.getInstance().queryRecruitNo(caseNo);
		
		// 分配有受试者编号的则为一期临床的受试者，打印腕带使用单独模板样式
		if (StringUtils.isEmpty(recruitNo)) {
			// 报表合并modify by wanglong 20130730
			this.openPrintDialog(
					IReportTool.getInstance().getReportPath("ADMWrist.jhw"),
					IReportTool.getInstance()
							.getReportParm("ADMWrist.class", print));
		} else {
			// 查询一期临床方案编号
			String planNo = MEDApplyTool.getInstance().queryPlanNo(caseNo);
			String patName = recruitNo + "-" + pat.getName();
			print.setData("PatName", "TEXT", patName);
			print.setData("PlanNo", "TEXT", planNo);
			this.openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMWrist", print);
		}
		
	}
	
	
	/**
	 * 儿童腕带打印
	 */
	public void onChildWrist() {
		if (this.getValueString("MR_NO").length() == 0 || pat == null) {
			return;
		}
		TParm print = new TParm();
		print.setData("Barcode", "TEXT", pat.getMrNo());
		print.setData("PatName", "TEXT", pat.getName());
		print.setData("Sex", "TEXT", pat.getSexString());
		print.setData("BirthDay", "TEXT",
				StringTool.getString(pat.getBirthday(), "yyyy/MM/dd"));
		this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMChildWrist", print);
	}
	
	/**
	 * 成人软式腕带打印
	 */
	public void onAdultWrist() {//add by guoy 20150818
		if (this.getValueString("MR_NO").length() == 0 || pat == null) {
			return;
		}
		TParm print = new TParm();
		print.setData("Barcode", "TEXT", pat.getMrNo());
		print.setData("PatName", "TEXT", pat.getName());
		print.setData("Sex", "TEXT", pat.getSexString());
		print.setData("BirthDay", "TEXT",
				StringTool.getString(pat.getBirthday(), "yyyy/MM/dd"));
		
		// 一期临床受试者编号
		String recruitNo = "";
		// 一期临床方案编号
		String planNo = "";
		
		// 因住院处操作习惯不同，现区分保存前和保存后两种方式打印腕带
		if (StringUtils.isEmpty(caseNo)) {
			String sql = "SELECT RECRUIT_NO,OPD_CASE_NO FROM ADM_RESV WHERE MR_NO = '"
					+ this.getValueString("MR_NO")
					+ "' AND CAN_DATE IS NULL AND IN_CASE_NO IS NULL";
			// 查询已预约未登记的数据
			TParm admParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (admParm.getErrCode() == 0 && admParm.getCount() > 0) {
				recruitNo = admParm.getValue("RECRUIT_NO", 0);

				if (StringUtils.isNotEmpty(recruitNo)) {
					String opdCaseNo = admParm.getValue("OPD_CASE_NO", 0);
					if (StringUtils.isNotEmpty(opdCaseNo)) {
						opdCaseNo = opdCaseNo.split(",")[0];
						// 查询关联的健检就诊方案号
						sql = "SELECT PLAN_NO FROM HRM_CONTRACTD A,HRM_PATADM B WHERE A.MR_NO = B.MR_NO AND A.CONTRACT_CODE = B.CONTRACT_CODE AND A.ROLE_TYPE = 'PIC' AND B.CASE_NO = '"
								+ opdCaseNo + "'";
						TParm result = new TParm(TJDODBTool.getInstance()
								.select(sql));
						if (result.getErrCode() == 0 && result.getCount() > 0) {
							planNo = result.getValue("PLAN_NO", 0);
						}
					}
				}
			}
		} else {
			// 查询一期临床受试者编号
			recruitNo = MEDApplyTool.getInstance().queryRecruitNo(caseNo);
			// 查询一期临床方案编号
			planNo = MEDApplyTool.getInstance().queryPlanNo(caseNo);
		}
		
		// 分配有受试者编号的则为一期临床的受试者，打印腕带使用单独模板样式
		if (StringUtils.isEmpty(recruitNo)) {
			this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMAdultWrist", print);
		} else {
			String patName = recruitNo + "-" + pat.getName();
			print.setData("PatName", "TEXT", patName);
			print.setData("PlanNo", "TEXT", planNo);
			this.openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMWrist", print);
		}
	}

	/**
	 * 生成xml文件：住院登记xml =======================pangben modify 20110812
	 */
	public void djXML() {
		// 1.构造数据
		if (this.getValueString("MR_NO").length() <= 0) {
			return;
		}
		TParm inparm = new TParm();
		inparm.insertData("TBR", 0, this.getValue("MR_NO")); // 病案号
		inparm.insertData("XM", 0, this.getValue("PAT_NAME")); // 姓名
		inparm.insertData("XB", 0, this.getValue("SEX_CODE")); // 性别
		inparm.insertData("CSNY", 0, StringTool.getString(
				(Timestamp) this.getValue("BIRTH_DATE"), "yyyyMMdd")); // 出生年月
		inparm.insertData("SFZH", 0, this.getValue("IDNO")); // 身份证号
		inparm.insertData("YRXZ", 0, "在职"); // 人员性质
		inparm.insertData("XH", 0, caseNo); // 入院序号
		inparm.insertData("RYSJ", 0, StringTool.getString(
				(Timestamp) this.getValue("IN_DATE"), "yyyyMMddHHmmss")); // 入院时间
		inparm.insertData("LXDH", 0, this.getValue("CONTACTS_TEL")); // 联系电话
		inparm.insertData("KSM", 0, this.getValue("DEPT_CODE")); // 科室码
		inparm.insertData("ZYH", 0, this.getValue("IPD_NO")); // 住院号
		TTextFormat format = (TTextFormat) this.getComponent("STATION_CODE");
		inparm.insertData("BQMC", 0, format.getText()); // 病区名称
		inparm.insertData("CWH", 0, this.getValue("BED_NO")); // 床位号
		inparm.insertData("ZHYE", 0, "0"); // 个人账户余额
		inparm.insertData("YSM", 0, this.getValue("VS_DR_CODE")); // 收治医生码
		inparm.insertData("XZMC", 0, "0"); // 险种
		inparm.addData("SYSTEM", "COLUMNS", "TBR");
		inparm.addData("SYSTEM", "COLUMNS", "XM");
		inparm.addData("SYSTEM", "COLUMNS", "XB");
		inparm.addData("SYSTEM", "COLUMNS", "CSNY");
		inparm.addData("SYSTEM", "COLUMNS", "SFZH");
		inparm.addData("SYSTEM", "COLUMNS", "YRXZ");
		inparm.addData("SYSTEM", "COLUMNS", "XH");
		inparm.addData("SYSTEM", "COLUMNS", "RYSJ");
		inparm.addData("SYSTEM", "COLUMNS", "LXDH");
		inparm.addData("SYSTEM", "COLUMNS", "KSM");
		inparm.addData("SYSTEM", "COLUMNS", "ZYH");
		inparm.addData("SYSTEM", "COLUMNS", "BQMC");
		inparm.addData("SYSTEM", "COLUMNS", "CWH");
		inparm.addData("SYSTEM", "COLUMNS", "ZHYE");
		inparm.addData("SYSTEM", "COLUMNS", "YSM");
		inparm.addData("SYSTEM", "COLUMNS", "XZMC");
		inparm.setCount(1);
		// System.out.println("=======inparm=============" + inparm);
		// 2.生成文件
		NJCityInwDriver.createXMLFile(inparm, "c:/NGYB/zydjxx.xml");
		this.messageBox("生成成功");
	}

	/**
	 * 获得数据:记录医保病人住院序号
	 */
	public void readDjXML() {
		TParm parm = NJCityInwDriver.getPame("c:/NGYB/mzghxx.xml");
		if (null == parm)
			return;
		this.setValue(
				"IPD_NO",
				parm.getValue("ZYH").substring(1,
						parm.getValue("ZYH").indexOf("]")));
	}

	/**
	 * 血糖Hl7接口
	 */
	public void sendHl7message() {
		TParm parm = new TParm();
		String type = "ADM_IN";
		List list = new ArrayList();
		parm.setData("ADM_TYPE", "I");
		parm.setData("CASE_NO", this.caseNo);
		parm.setData("IPD_NO", this.IPD_NO);
		list.add(parm);
		// 调用接口
		TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,
				type);
		if (resultParm.getErrCode() < 0)
			this.messageBox(resultParm.getErrText());
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 更新住院登记表中的单病种信息
	 */
	public void updateADMInpSDInfo() {// add by wanglong 20121025
		TParm action = new TParm();
		action.setData("CASE_NO", caseNo);
		action.setData("DISE_CODE", this.getValue("DISE_CODE") + "");
		TParm result = CLPSingleDiseTool.getInstance().updateADMInpSDInfo(
				action);
		if (result.getErrCode() < 0) {
			messageBox("单病种信息保存失败");
			return;
		}
	}

	// ================= chenxi add 20130319 读身份证卡信息
	public void onIdCardNo() {
		// String dir = "C:/Program Files/Routon/身份证核验机具阅读软件" ;
		String dir = SystemTool.getInstance().Getdir();
		
		// add by yangjj 20150629
		System.out.println("身份证读卡机日志："+dir);
		
		CardInfoBO cardInfo = null;
		try {
			cardInfo = IdCardReaderUtil.getCardInfo(dir);
		} catch (Exception e) {
			this.messageBox("重新获取信息");
			System.out.println("重新获取信息:" + e.getMessage());
			// TODO: handle exception
		}
		// CardInfoBO cardInfo = IdCardReaderUtil.getCardInfo(dir);
		if (cardInfo == null) {
			this.messageBox("未获得身份证信息,请重新操作");
			return;
		}
		
		//add by yangjj 20150629
		System.out.println("患者信息： 身份证号："+cardInfo.getCode()+",姓名："+cardInfo.getName());
		
		// 通过身份证号查询病患信息
		TParm parm = new TParm();
		parm.setData("IDNO", cardInfo.getCode().trim());// 身份证号
		TParm infoParm = PatTool.getInstance().getInfoForIdNo(parm);
		
		//add by yangjj 20150629
		System.out.println("infoParm:"+infoParm);
		
		if (infoParm.getCount() > 0) {
			// this.messageBox("已存在此就诊病患信息");
		} else {
			String sql = "SELECT MR_NO,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS FROM SYS_PATINFO WHERE PAT_NAME LIKE '"
					+ cardInfo.getName() + "%'";
			infoParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (infoParm.getCount() <= 0) {
				this.messageBox("不存在此就诊病患信息");
			}
		}
		if (infoParm.getCount() > 0) {// 多行数据显示===pangben 2013-8-6
			if (infoParm.getCount() == 1) {// 只存在一条数据
				if (!checkPatInfo(infoParm.getValue("MR_NO", 0))) {
					return;
				}
				this.setValue("MR_NO", infoParm.getValue("MR_NO", 0));
			} else {
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x",// 获得唯一的病案号
						infoParm);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
					if (!checkPatInfo(patParm.getValue("MR_NO"))) {
						return;
					}
					this.setValue("MR_NO", patParm.getValue("MR_NO"));
				} else {
					return;
				}
			}
			this.sendpic(cardInfo.getImagesPath().get(2));
			onMrno();
			setValue("RESID_ADDRESS", cardInfo.getAdd()); // 户口地址
			setValue("SPECIES_CODE",
					onGetSPECIES_CODE(cardInfo.getFolk() + "族")); // 名族
		} else {
			this.onClear();// 没有查询到数据将界面数据清空
			setValue("PAT_NAME", cardInfo.getName()); // 姓名
			setValue("IDNO", cardInfo.getCode()); // 身份证号
			setValue("SEX_CODE", cardInfo.getSex().equals("男") ? "1" : "2"); // 性别
			setValue("BIRTH_DATE",
					StringTool.getTimestamp(cardInfo.getBirth(), "yyyyMMdd")); // 生日
			setValue("RESID_ADDRESS", cardInfo.getAdd()); // 户口地址
			setValue("SPECIES_CODE",
					onGetSPECIES_CODE(cardInfo.getFolk() + "族")); // 名族
			setBirth(); // 计算年龄
		}
		// IdCardO.getInstance().delFolder(dir) ;
	}

	/**
	 * 校验病患信息
	 * 
	 * @return ===========pangben 2013-8-6 二代身份证校验
	 */
	private boolean checkPatInfo(String mrNo) {
		if (this.getValue("MR_NO").toString().length() > 0 && null != pat) {
			if (!this.getValue("MR_NO").equals(mrNo)) {
				if (this.messageBox("提示", "身份证信息与当前就诊病患信息不符,是否继续", 2) != 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 传送照片
	 * 
	 * @param image
	 *            Image
	 */
	public void sendpic(String localFileName) {
		String dir = "";
		String mrNo = getValue("MR_NO").toString();
		String photoName = mrNo + ".jpg";
		File file = new File(localFileName);
		try {
			// String root = TIOM_FileServer.getRoot();
			dir = TIOM_FileServer.getPath("PatInfPIC.LocalPath");
			// dir = dir + mrNo.substring(0, 3) + "\\"
			// + mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
			File filepath = new File(dir);
			if (!filepath.exists())
				filepath.mkdirs();
			BufferedImage input = ImageIO.read(file);
			Image scaledImage = input.getScaledInstance(300, 400,
					Image.SCALE_DEFAULT);
			BufferedImage output = new BufferedImage(300, 400,
					BufferedImage.TYPE_INT_BGR);
			output.createGraphics().drawImage(scaledImage, 0, 0, null); // 画图
			ImageIO.write(output, "jpg", new File(dir + photoName));
			sendpic(scaledImage);
		} catch (Exception e) {
		}
	}

	/**
	 * 取名族code
	 */
	public String onGetSPECIES_CODE(String name) {
		String code = "";
		String sql = "SELECT ID FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SPECIES' "
				+ "  AND CHN_DESC = '" + name + "'";
		// System.out.println("name======"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0)
			return code;
		code = parm.getValue("ID", 0);
		return code;

	}

	// ================= chenxi add 20130319 读身份证卡信息
	public void onIdentificationPic() {
		String dir = TIOM_FileServer.getPath("PatInfPIC.LocalPath");
		String mrNo = getValue("MR_NO").toString();
		if (mrNo == null || mrNo.equals("")) {
			this.messageBox("请先读取就诊病患信息");
			return;
		}
		try {
//			Display display = Display.getDefault();
//			SWTshell shell = new SWTshell(display, dir, mrNo);
//			shell.open();
//			shell.layout();
//			while (!shell.isDisposed()) {
//				if (!display.readAndDispatch()) {
//					display.sleep();
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		IdCardO.getInstance().delFolder(dir);
	}

	/**
	 * 验证该病患是否已经生成住院证
	 * 
	 * @return 是否生成住院证
	 * @author wangb
	 */
	private boolean checkHospCard() {
		String querCaseNO = "";
		String subClassCode = TConfig
				.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
		String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
		
		// 查询病患最近一次 预约信息
		String resvSql = "SELECT * FROM ADM_RESV WHERE MR_NO = '" + pat.getMrNo()
				+ "' AND CAN_DATE IS NULL ORDER BY RESV_NO DESC";
		
		TParm resv = new TParm(TJDODBTool.getInstance().select(resvSql));
		
		if (resv.getErrCode() < 0) {
			this.messageBox("查询病患预约住院信息错误");
			err("ERR:" + resv.getErrText());
			return false;
		}
		
		querCaseNO = resv.getValue("RESV_NO", 0);
		
		String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO = '#' AND CLASS_CODE='"
				+ classCode + "' AND SUBCLASS_CODE='" + subClassCode + "'";
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sql.replaceFirst("#", querCaseNO)));
        
		if (result.getErrCode() < 0) {
			this.messageBox("查询住院证病历信息错误");
			err("ERR:" + result.getErrText());
			return false;
		}
		
		if (result.getCount() > 0) {
			return true;
		}
		
		return false;
	}
	
	
	// modified by WangQing 20170315
		/**
		 * Date->String
		 * @param date
		 * @return
		 */
		public String dateToString(Date date){
			//		Date date = new Date();
			String dateStr = "";
			//format的格式可以任意
			//		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss");
			DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				//			dateStr = sdf.format(date);
				//			System.out.println(dateStr);
				dateStr = sdf2.format(date);
				System.out.println(dateStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return dateStr;
		}
		
}
