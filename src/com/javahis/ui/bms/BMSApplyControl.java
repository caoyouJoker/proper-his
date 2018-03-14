package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;

import java.sql.Timestamp;

import jdo.spc.StringUtils;
import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;

import jdo.adm.ADMTool;
import jdo.bms.BMSSQL;
import jdo.sys.SYSSQL;
import jdo.sys.Pat;

import com.javahis.util.StringUtil;
import com.dongyang.manager.TIOM_AppServer;

import jdo.bms.BMSTool;
import jdo.sys.Operator;

import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;

import jdo.bms.BMSApplyDTool;
import jdo.bms.BMSApplyMTool;
import jdo.bms.BMSBloodTool;
import jdo.util.Manager;

import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: 备血申请
 * </p>
 *
 * <p>
 * Description: 备血申请
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSApplyControl
extends TControl {

	// 外部调用传参
	private TParm parm;

	private String from_flg;

	private String apply_no;

	// 门急住别
	private String adm_type;

	private String mr_no;

	//    private Timestamp use_date;

	private String dept_code;

	private String dr_code;

	private String icd_code_1 = "";

	private String icd_desc_1 = "";

	private String icd_code_2 = "";

	private String icd_desc_2 = "";

	private String icd_code_3 = "";

	private String icd_desc_3 = "";

	private String case_no;

	private String ipd_no;

	private String action = "insert";

	//    private String user;//当前操作人

	//    private String BmsType;//申请医师

	//    private String BmsFlg;//审核状态



	//登陆语言
	private String language = "";

	public BMSApplyControl() {
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		Object obj = this.getParameter();   
		//        user = Operator.getPosition();

		if (obj!=null && obj instanceof TParm) {// modified by wangqing 20171211 obj非空校验
			parm = (TParm) obj;
			from_flg = parm.getValue("FROM_FLG");
			// add by wangqing 20171211 start 
			// from_flg非空校验
			if(from_flg==null || from_flg.trim().length()<=0){
				this.messageBox("bug:::from_flg is null");
				return;
			}else{

			}
			// add by wangqing 20171211 end

			if ("1".equals(from_flg)) {
				// 新建备血单
				adm_type = parm.getValue("ADM_TYPE");
				mr_no = parm.getValue("MR_NO");
				//                use_date = parm.getTimestamp("USE_DATE");
				dept_code = parm.getValue("DEPT_CODE");
				dr_code = parm.getValue("DR_CODE");
				icd_code_1 = parm.getValue("ICD_CODE");
				icd_desc_1 = parm.getValue("ICD_DESC");
				case_no = parm.getValue("CASE_NO");

			}
			else if ("2".equals(from_flg)) {
				// 存在备血单
				apply_no = parm.getValue("APPLY_NO");
				//bms_type = parm.getValue("BMS_TYPE");
			}
			this.getTextField("MR_NO").setEnabled(false);
		}
		else {
			from_flg = "";
			this.getTextField("MR_NO").setEnabled(true);
		}

		// 初始化画面数据
		initPage();
	}

	/**
	 * 审核
	 */
	public void onExamine(){
		// add by wangqing 20171208 start	
		// 校验是否已经审核
		if(apply_no!=null && apply_no.trim().length()>0 && action!=null && action.equals("update")){
			TParm checkInfo = queryCheckInfo(apply_no);
			if(checkInfo.getErrCode()<0){
				this.messageBox("bug:::查询审核信息失败");
				return;
			}else if(checkInfo.getCount()<=0){
				this.messageBox("bug:::没有申请单数据");
				return;
			}else{
				String checkFlg = checkInfo.getValue("CHECK_FLG", 0);
				if(checkFlg!=null && checkFlg.equals("Y")){
					this.messageBox("已经审核，不能重复审核");
					return;
				}else{

				}
			}					
		}else{
			this.messageBox("申请单尚未保存");
			return;
		}
		String checkDrCode = Operator.getID();
		String caseNo = this.getValueString("CASE_NO");
		// 校验是否有审核权限
		if(isHaveCheckPermission(apply_no, checkDrCode, caseNo)){

		}else{
			this.messageBox("没有审核权限");
			return;
		}		
		TParm parm = new TParm();
		TParm result = new TParm();
		parm.setData("APPLY_NO", apply_no);
		parm.setData("CHECK_FLG", "Y");
		
		// add by wangqing 20180110  审核时，插入审核者和审核日期
		parm.setData("CHECK_USER", Operator.getID());
		
		result = TIOM_AppServer.executeAction(
				"action.bms.BMSApplyAction", "onCheckBMSApply", parm);
		if(result.getErrCode()<0){
			this.messageBox("审核失败");
			return;
		}else{
			this.messageBox("审核成功");
			this.setValue("CHECK_FLG", "Y");
			return;
		}
		// add by wangqing 20171208 end	
	}

	/**
	 * 取消审核
	 */
	public void onRemoveExamine(){
		// add by wangqing 20171208 start	
		// 校验是否已经审核
		if(apply_no!=null && apply_no.trim().length()>0 && action!=null && action.equals("update")){
			TParm checkInfo = queryCheckInfo(apply_no);
			if(checkInfo.getErrCode()<0){
				this.messageBox("bug:::查询审核信息失败");
				return;
			}else if(checkInfo.getCount()<=0){
				this.messageBox("bug:::没有申请单数据");
				return;
			}else{
				String checkFlg = checkInfo.getValue("CHECK_FLG", 0);
				if(checkFlg!=null && checkFlg.equals("Y")){

				}else{
					this.messageBox("尚未审核");
					return;
				}
			}					
		}else{
			this.messageBox("申请单尚未保存");
			return;
		}
		String checkDrCode = Operator.getID();
		String caseNo = this.getValueString("CASE_NO");
		// 校验是否有取消审核权限
		if(isHaveCheckPermission(apply_no, checkDrCode, caseNo)){

		}else{
			this.messageBox("没有取消审核权限");
			return;
		}	
		// 校验是否已配血或出库
		TParm parm11 = new TParm();
		TParm result11 = new TParm();
		parm11.setData("APPLY_NO", this.getValue("APPLY_NO"));   
		result11 = BMSBloodTool.getInstance().onQuery(parm11);
		if (result11.getCount() > 0) {
			this.messageBox("有血品已经交叉配血或出库");
			return;
		}
				
		/*TTable table = this.getTable("TABLE");
		TParm tblParm = table.getParmValue();
		for(int i=0; i<tblParm.getCount(); i++){
			String bldCode = tblParm.getValue("BLD_CODE", i);
			parm11.setData("BLD_CODE", bldCode);
			result11 = BMSBloodTool.getInstance().onQuery(parm11);
			if (result11.getCount() > 0) {
				this.messageBox("有血品已经交叉配血或出库");
				return;
			}
		}*/

		TParm parm = new TParm();
		TParm result = new TParm();
		parm.setData("APPLY_NO", apply_no);
		parm.setData("CHECK_FLG", "N");
		// add by wangqing 20180110 新增审核人、审核日期字段
		parm.setData("CHECK_USER", Operator.getID());
		result = TIOM_AppServer.executeAction(
				"action.bms.BMSApplyAction", "onCheckBMSApply", parm);
		if(result.getErrCode()<0){
			this.messageBox("取消审核失败");
			return;
		}else{
			this.messageBox("取消审核成功");
			this.setValue("CHECK_FLG", "N");
			return;
		}
		// add by wangqing 20171208 end
	}

	/**
	 * 显示入院诊断包括主诊断和次诊断
	 * =====pangben 2016-5-5
	 * @param caseNo
	 */
	private void showDiag(String caseNo){

		TParm parm = new TParm();
		parm.setData("CASE_NO",caseNo);
		//parm.setData("MAIN_FLG","Y");//主诊断
		parm.setData("IO_TYPE","M");//入院诊断
		//查询病患入院诊断以及次诊断=====pangben 2016-5-5
		TParm adm_daily = ADMTool.getInstance().queryDailyData(parm);
		if(adm_daily.getCount()>0){
			int index=0;
			if(adm_daily.getCount()>3){
				index=3;
			}else{
				index=adm_daily.getCount();
			}
			//将诊断数据转换成Grid能够识别的格式
			for (int i = 0; i < index; i++) {
				this.setValue("DIAG_CODE"+(i+1), adm_daily.getValue("ICD_CHN_DESC",i));
				if(i==0){
					icd_code_1=adm_daily.getValue("ICD_CODE",0);
				}else if(i==1){
					icd_code_2=adm_daily.getValue("ICD_CODE",1);
				}else if(i==2){
					icd_code_3=adm_daily.getValue("ICD_CODE",2);
				} 
			}
		}
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
		if (!checkData()) {
			return;
		}

		//add by yangjj 20150515
		if((!this.getRadioButton("BLOOD_RH_TYPE_A").isSelected())&&(!this.getRadioButton("BLOOD_RH_TYPE_B").isSelected())){
			this.messageBox("请根据检验结果填写患者RH血型！");
			return ;
		}

		// add by wangqing 20171208 start
		// 校验是否已经审核
		if(apply_no!=null && apply_no.trim().length()>0 && action!=null && action.equals("update")){
			TParm checkInfo = queryCheckInfo(apply_no);
			if(checkInfo.getErrCode()<0){
				this.messageBox("bug:::查询审核信息失败");
				return;
			}else if(checkInfo.getCount()<=0){
				this.messageBox("bug:::没有申请单数据");
				return;
			}else{
				String checkFlg = checkInfo.getValue("CHECK_FLG", 0);
				if(checkFlg!=null && checkFlg.equals("Y")){
					this.messageBox("已经审核，不可保存");
					return;
				}else{

				}
			}
		}else{

		}
		// 判断是否有申请权限
		TTable table = this.getTable("TABLE");
		TParm tblParm = table.getParmValue();
		if(tblParm==null || tblParm.getCount()<=0){
			this.messageBox("申请血品不能为空！！！");
			return;
		}else{
			for(int i=0; i<tblParm.getCount(); i++){
				String bldCode = tblParm.getValue("BLD_CODE", i);
				double applyQty = tblParm.getDouble("APPLY_QTY", i);
				String applyUnitCode = tblParm.getValue("UNIT_CODE", i);
				String applyDrCode = Operator.getID();
				String caseNo = getValueString("CASE_NO");
				if(isHaveApplyPermission(bldCode, applyQty, applyUnitCode, applyDrCode, caseNo)){

				}else{
					this.messageBox("没有申请权限");
					return;
				}
			}
		}			
		// add by wangqing 20171208 end

		TParm inparm = new TParm();
		inparm.setData("ADM_TYPE", adm_type);
		inparm.setData("CASE_NO", this.getValue("CASE_NO"));
		inparm.setData("MR_NO", this.getValue("MR_NO"));
		inparm.setData("IPD_NO", this.getValue("IPD_NO"));
		inparm.setData("APPLY_TYPE", "");
		inparm.setData("PRE_DATE", this.getValue("PRE_DATE"));
		inparm.setData("END_DAYS", this.getValue("END_DAYS"));
		inparm.setData("USE_DATE", this.getValue("USE_DATE"));
		//
		inparm.setData("HBSAG", this.getValueString("HBSAG"));
		inparm.setData("ANTI_HCV", this.getValueString("ANTI_HCV"));
		inparm.setData("ANTI_HIV", this.getValueString("ANTI_HIV"));
		inparm.setData("SY", this.getValueString("SY"));
		inparm.setData("RBC", this.getValueDouble("RBC"));
		inparm.setData("HB", this.getValueDouble("HB"));
		inparm.setData("HCT", this.getValueDouble("HCT"));
		inparm.setData("WBC", this.getValueDouble("WBC"));
		inparm.setData("PLT", this.getValueDouble("PLT"));
		//
		inparm.setData("URG_FLG",
				this.getRadioButton("URG_FLG_Y").isSelected() ? "Y" :
				"N");
		inparm.setData("BED_NO", this.getValue("BED_NO"));
		inparm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
		
		// 开立医师
		inparm.setData("DR_CODE", Operator.getID());
				
		inparm.setData("TRANRSN_CODE1", this.getValue("TRANRSN_CODE1"));
		inparm.setData("TRANRSN_CODE2", this.getValue("TRANRSN_CODE2"));
		inparm.setData("TRANRSN_CODE3", this.getValue("TRANRSN_CODE3"));
		inparm.setData("DIAG_CODE1", icd_code_1);
		inparm.setData("DIAG_CODE2", icd_code_2);
		inparm.setData("DIAG_CODE3", icd_code_3);
		inparm.setData("CLS_FLG", "N");
		inparm.setData("TEST_BLD", this.getValue("TEST_BLD"));
		//        inparm.setData("BMS_TYPE",user);

		//add by yangjj 20150515
		String rh = "";
		if(this.getRadioButton("BLOOD_RH_TYPE_A").isSelected()){
			rh = "+";
		}else if(this.getRadioButton("BLOOD_RH_TYPE_B").isSelected()){
			rh = "-";
		}
		inparm.setData("BLOOD_RH_TYPE",rh);

		inparm.setData("BLD_SIFT_FLG", "N");
		if(((TCheckBox)this.getComponent("CH_CSF")).isSelected()){
			inparm.setData("SIFT_FLG", "Y");
		}else{
			inparm.setData("SIFT_FLG", "N");
		}

		// 开立医师
		inparm.setData("APPLY_USER", Operator.getID());
		
		Timestamp date = SystemTool.getInstance().getDate();
		inparm.setData("APPLY_DATE", date);
		inparm.setData("OPT_USER", Operator.getID());
		inparm.setData("OPT_DATE", date);
		inparm.setData("OPT_TERM", Operator.getIP());
		inparm.setData("REMARK", this.getValue("REMARK"));
		this.getTable("TABLE").acceptText();
		//		Double Db = 0.0;
		//		Double Da = 0.0;
		TParm parmD = new TParm();
		for (int i = 0; i < this.getTable("TABLE").getRowCount(); i++) {
			parmD.addData("BLD_CODE",
					this.getTable("TABLE").getItemData(i, "BLD_CODE"));
			parmD.addData("APPLY_QTY",
					this.getTable("TABLE").getItemData(i, "APPLY_QTY"));
			//			Db = (Double) this.getTable("TABLE").getItemData(i, "APPLY_QTY");
			//			Da += Db;
			parmD.addData("UNIT_CODE",
					this.getTable("TABLE").getItemData(i, "UNIT_CODE"));
		}
		//this.messageBox(Da.toString());
		/*	//wuxy 20170628 start 审核字段
		if(Da < 800.0){
			inparm.setData("BMS_FLG","Y");
		}else{
			inparm.setData("BMS_FLG","N");
		}
		//wuxy 20170628 end
		 */		
		inparm.setData("BMS_APPLYD", parmD.getData());

		// add by wangqing 20171212 审核状态
		inparm.setData("CHECK_FLG", this.getValue("CHECK_FLG"));


		TParm result = new TParm();
		System.out.println("33333"+inparm.getValue("BMS_TYPE"));
		if ("insert".equals(action)) {
			apply_no = SystemTool.getInstance().getNo("ALL", "BMS",
					"BMS_APPLY", "No");
			inparm.setData("APPLY_NO", apply_no);

			result = TIOM_AppServer.executeAction(
					"action.bms.BMSApplyAction", "onInsertBMSApply", inparm);
			// 保存判断
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			this.messageBox("P0001");
			action = "update";
			this.setValue("APPLY_NO", apply_no);
			onPrint();
		}
		else {
			inparm.setData("APPLY_NO", this.getValue("APPLY_NO"));

			result = TIOM_AppServer.executeAction(
					"action.bms.BMSApplyAction", "onUpdateBMSApply", inparm);
			// 保存判断
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			this.messageBox("P0001");
			// 更新开立医师
			this.setValue("DR_CODE", Operator.getID());
			action = "update";
			//this.setValue("APPLY_NO", apply_no);
		}

	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		if ("".equals(this.getValueString("APPLY_NO"))) {
			this.messageBox("E0135");
			return;
		}
		TParm parm = new TParm();
		parm.setData("APPLY_NO", this.getValueString("APPLY_NO"));
		TParm result = BMSTool.getInstance().onQueryBMSApply(parm);
		//System.out.println("result--" + result);

		TParm resultM = result.getParm("BMS_APPLYM");
		TParm resultD = result.getParm("BMS_APPLYD");
		if (resultM == null || resultM.getCount("APPLY_NO") == 0 ||
				resultM.getCount() == 0) {
			this.messageBox("E0116");
			return;
		}
		/*    //add By wuxy 20170629
         BmsType = resultM.getValue("BMS_TYPE",0);//申请医师
        // this.messageBox(BmsType);

         BmsFlg = resultM.getValue("BMS_FLG",0);//审核状态
         if(BmsFlg.equals("Y")){
        	 callFunction("UI|examine|setEnabled", false);
         }else{
        	 callFunction("UI|removeExamine|setEnabled", false);
         }*/

		// add by wangqing 20171208 审核状态
		this.setValue("CHECK_FLG", resultM.getData("CHECK_FLG", 0));

		// 主项信息
		adm_type = resultM.getValue("ADM_TYPE", 0);
		if ("O".equals(adm_type)) {
			if (!"en".equals(language)) {
				this.setValue("ADM_TYPE", "门诊");
			}
			else {
				this.setValue("ADM_TYPE", "O");
			}
		}
		else if ("E".equals(adm_type)) {
			if (!"en".equals(language)) {
				this.setValue("ADM_TYPE", "急诊");
			}
			else {
				this.setValue("ADM_TYPE", "E");
			}
		}
		else if ("I".equals(adm_type)) {
			if (!"en".equals(language)) {
				this.setValue("ADM_TYPE", "住院");
			}
			else {
				this.setValue("ADM_TYPE", "I");
			}
		}

		this.setValue("MR_NO", resultM.getData("MR_NO", 0));
		this.setValue("IPD_NO", resultM.getData("IPD_NO", 0));
		this.setValue("CASE_NO", resultM.getData("CASE_NO", 0));
		this.setValue("PRE_DATE", resultM.getData("PRE_DATE", 0));
		this.setValue("END_DAYS", resultM.getData("END_DAYS", 0));
		this.setValue("USE_DATE", resultM.getData("USE_DATE", 0));
		//
		this.setValue("HBSAG", resultM.getData("HBSAG", 0));
		this.setValue("ANTI_HCV", resultM.getData("ANTI_HCV", 0));
		this.setValue("ANTI_HIV", resultM.getData("ANTI_HIV", 0));
		this.setValue("SY", resultM.getData("SY", 0));
		this.setValue("RBC", resultM.getData("RBC", 0));
		this.setValue("HB", resultM.getData("HB", 0));
		this.setValue("HCT", resultM.getData("HCT", 0));
		this.setValue("WBC", resultM.getData("WBC", 0));
		this.setValue("PLT", resultM.getData("PLT", 0));
		//
		if ("Y".equals(resultM.getValue("URG_FLG", 0))) {
			this.getRadioButton("URG_FLG_Y").setSelected(true);
		}
		else {
			this.getRadioButton("URG_FLG_N").setSelected(true);
		}
		this.setValue("DEPT_CODE", resultM.getData("DEPT_CODE", 0));
		this.setValue("BED_NO", resultM.getData("BED_NO", 0));
		this.setValue("DR_CODE", resultM.getData("DR_CODE", 0));
		this.setValue("TRANRSN_CODE1", resultM.getData("TRANRSN_CODE1", 0));
		this.setValue("TRANRSN_CODE2", resultM.getData("TRANRSN_CODE2", 0));
		this.setValue("TRANRSN_CODE3", resultM.getData("TRANRSN_CODE3", 0));

		if ("".equals(resultM.getValue("DIAG_CODE1", 0))) {
			this.setValue("DIAG_CODE1", "");
		}
		else {
			TParm icdParm = new TParm(TJDODBTool.getInstance().select(SYSSQL.
					getSYSIcdByCode(resultM.getValue("DIAG_CODE1", 0))));
			this.setValue("DIAG_CODE1", icdParm.getValue("ICD_CHN_DESC", 0));
		}
		if ("".equals(resultM.getValue("DIAG_CODE2", 0))) {
			this.setValue("DIAG_CODE2", "");
		}
		else {
			TParm icdParm = new TParm(TJDODBTool.getInstance().select(SYSSQL.
					getSYSIcdByCode(resultM.getValue("DIAG_CODE2", 0))));
			this.setValue("DIAG_CODE2", icdParm.getValue("ICD_CHN_DESC", 0));
		}
		if ("".equals(resultM.getValue("DIAG_CODE3", 0))) {
			this.setValue("DIAG_CODE3", "");
		}
		else {
			TParm icdParm = new TParm(TJDODBTool.getInstance().select(SYSSQL.
					getSYSIcdByCode(resultM.getValue("DIAG_CODE3", 0))));
			this.setValue("DIAG_CODE3", icdParm.getValue("ICD_CHN_DESC", 0));
		}

		this.setValue("REMARK", resultM.getData("REMARK", 0));

		Pat pat = Pat.onQueryByMrNo(resultM.getValue("MR_NO", 0));
		if (!"en".equals(language)) {
			this.setValue("PAT_NAME", pat.getName());
		}
		else {
			this.setValue("PAT_NAME", pat.getName1());
		}
		this.setValue("SEX_CODE", pat.getSexCode());
		Timestamp date = SystemTool.getInstance().getDate();
		if (!"en".equals(language)) {
			this.setValue("AGE",
					StringUtil.getInstance().showAge(pat.getBirthday(),
							date));
		}
		else {
			String[] args = StringTool.CountAgeByTimestamp(pat.getBirthday(), date);
			this.setValue("AGE", args[0] + "Y");
		}
		this.setValue("IDNO", pat.getIdNo());

		TParm parmPat = new TParm();
		parmPat.setData("MR_NO", resultM.getData("MR_NO", 0));
		parmPat.setData("IO_TYPE", adm_type);
		TParm resultPat = BMSTool.getInstance().onQueryPat(parmPat);
		this.setValue("CTZ1_CODE", resultPat.getValue("CTZ1_CODE", 0));
		if (resultPat.getValue("BLOOD_RH_TYPE", 0).equals("+")) {
			this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(true);
			this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
		}
		else if (resultPat.getValue("BLOOD_RH_TYPE", 0).equals("-")) {
			this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
			this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(true);
		}
		else {
			this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
			this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
		}
		this.setValue("TEST_BLD", resultPat.getValue("BLOOD_TYPE", 0));
		this.setValue("DEPT", resultPat.getValue("DEPT_CODE", 0));
		String bed_no = resultPat.getValue("BED_NO", 0);
		if (!"".equals(bed_no)) {
			TParm bedParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT STATION_CODE FROM SYS_BED WHERE BED_NO = '" + bed_no + "'"));
			this.setValue("STATION_CODE", bedParm.getValue("STATION_CODE", 0));
		}

		// 细项信息
		TTable table = this.getTable("TABLE");
		
		table.setParmValue(resultD);
		
		/*for (int i = 0; i < resultD.getCount("BLD_CODE"); i++) {
			int row = table.addRow();
			// BLD_CODE;APPLY_QTY;UNIT_CODE
			table.setItem(row, "BLD_CODE", resultD.getData("BLD_CODE", i));
			table.setItem(row, "APPLY_QTY", resultD.getData("APPLY_QTY", i));
			table.setItem(row, "UNIT_CODE", resultD.getData("UNIT_CODE", i));
		}*/
		action = "update";
	}

	/**
	 * 删除方法
	 */
	public void onDelete() {
		if (!"".equals(this.getValueString("APPLY_NO")) &&
				"update".equals(action)) {

			// add by wangqing 20171208 start
			// 校验是否已经审核
			TParm checkInfo = queryCheckInfo(apply_no);
			if(checkInfo.getErrCode()<0){
				this.messageBox("bug:::查询审核信息失败");
				return;
			}else if(checkInfo.getCount()<=0){
				this.messageBox("bug:::没有申请单数据");
				return;
			}else{
				String checkFlg = checkInfo.getValue("CHECK_FLG", 0);
				if(checkFlg!=null && checkFlg.equals("Y")){
					this.messageBox("已经审核，不可删除");
					return;
				}else{

				}
			}	
			// 校验是否有删除权限
			TTable table = this.getTable("TABLE");
			TParm tblParm = table.getParmValue();
			if(tblParm==null){

			}else{
				for(int i=0; i<tblParm.getCount(); i++){
					String bldCode = tblParm.getValue("BLD_CODE", i);
					double applyQty = tblParm.getDouble("APPLY_QTY", i);
					String applyUnitCode = tblParm.getValue("UNIT_CODE", i);
					String applyDrCode = Operator.getID();
					String caseNo = getValueString("CASE_NO");
					if(isHaveApplyPermission(bldCode, applyQty, applyUnitCode, applyDrCode, caseNo)){

					}else{
						this.messageBox("没有删除权限");
						return;
					}
				}
			}				
			// add by wangqing 20171208 end

			TParm parm = new TParm();
			parm.setData("APPLY_NO", this.getValue("APPLY_NO"));
			TParm result = BMSBloodTool.getInstance().onQuery(parm);
			if (result.getCount() > 0) {
				this.messageBox("E0136");
				return;
			}

			result = TIOM_AppServer.executeAction(
					"action.bms.BMSApplyAction", "onDeleteBMSApply", parm);
			// 保存判断
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0003");
				return;
			}
			this.messageBox("P0003");
			this.onClear();
		}
		else {
			this.messageBox("E0116");
		}
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		action = "insert";
		ClearPatInfo();
		ClearApplyMInfo();
		ClearApplyDInfo();
	}

	/**
	 * 打印方法
	 */
	public void onPrint() {
		if ("".equals(this.getValueString("APPLY_NO"))) {
			this.messageBox("E0137");
			return;
		}
		TParm parmApply = new TParm(TJDODBTool.getInstance().select(BMSSQL.
				getBMSApplyPrtData(this.getValueString("APPLY_NO"))));
		if (parmApply.getCount() <= 0) {
			this.messageBox("E0137");
			return;
		}
		// 打印数据
		TParm date = new TParm();
		date.setData("TITLE", "TEXT", Manager.getOrganization().
				getHospitalCHNFullName(Operator.getRegion()) +
				"备血申请单");
		date.setData("PRE_DATE", "TEXT",
				"申请日期:" +
						this.getValueString("PRE_DATE").substring(0, 19).
						replaceAll("-", "/"));
		date.setData("USE_DATE", "TEXT",
				"预定输血日期:" +
						this.getValueString("USE_DATE").substring(0, 19).
						replaceAll("-", "/"));
		date.setData("APPLY_NO", "TEXT",
				this.getValueString("APPLY_NO"));
		date.setData("PAT_NAME", "TEXT",
				"受血者名称:" + this.getValueString("PAT_NAME"));
		date.setData("SEX", "TEXT", "性别:" + getComboBox("SEX_CODE").getSelectedName());
		Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
		date.setData("AGE", "TEXT",
				"年龄:" + StringUtil.showAge(pat.getBirthday(),
						SystemTool.getInstance().getDate()));
		date.setData("MR_NO", "TEXT", " 病案号:" + this.getValueString("MR_NO"));
		date.setData("STATION_CODE", "TEXT", "病区:" +
				( (TTextFormat)this.getComponent("STATION_CODE")).getText());
		date.setData("IPD_NO", "TEXT", " 住院号:" + this.getValueString("IPD_NO"));

		date.setData("DEPT_CODE", "TEXT",
				"科别:" + getComboBox("DEPT").getSelectedName());
		date.setData("BED_NO", "TEXT", "床号:" + this.getValueString("BED_NO"));
		date.setData("DIAG_CODE", "TEXT", "医生诊断:"
				+ this.getValueString("DIAG_CODE1")
				+ " " + this.getValueString("DIAG_CODE2")
				+ " " + this.getValueString("DIAG_CODE3"));
		date.setData("TRANRSN_CODE", "TEXT", "输血原因:"
				+ getComboBox("TRANRSN_CODE1").getSelectedName()
				+ " " + getComboBox("TRANRSN_CODE2").getSelectedName()
				+ " " + getComboBox("TRANRSN_CODE3").getSelectedName());
		date.setData("REMARK", "TEXT", "备注:" + this.getValueString("REMARK"));
		date.setData("TEST_BLD", "TEXT",
				"ABO血型:" + getComboBox("TEST_BLD").getSelectedName());
		date.setData("RH_TYPE", "TEXT",
				"Y".equals(getValueString("BLOOD_RH_TYPE_A")) ?
						"RH(D):阳性" : "RH(D):阴性");
		//
		date.setData("HBSAG", "TEXT", "乙肝表面抗原:" + this.getValueString("HBSAG"));
		date.setData("ANTI_A", "TEXT", "丙肝抗体:" + this.getValueString("ANTI_HCV"));
		date.setData("ANTI_B", "TEXT", "艾滋病抗体:" + this.getValueString("ANTI_HIV"));
		date.setData("ANTI_D", "TEXT", "梅毒:" + this.getValueString("SY"));
		date.setData("RBC", "TEXT",
				"血常规:红细胞 " + this.getValueDouble("RBC") + " ×10^12/L");
		date.setData("HB", "TEXT", "血红蛋白:" + this.getValueDouble("HB") + " g/L");
		date.setData("HCT", "TEXT", "血细胞压积:" + this.getValueDouble("HCT") + " %");
		date.setData("WBC", "TEXT",
				"白细胞:" + this.getValueDouble("WBC") + " ×10^9/L");
		date.setData("PLT", "TEXT",
				"血小板:" + this.getValueDouble("PLT") + " ×10^9/L");
		//
		// 表格数据
		TParm parm = new TParm();
		TTable table = this.getTable("TABLE");
		String boolean_code = "";
		for (int i = 0; i < table.getRowCount(); i++) {
			boolean_code = table.getItemString(i, "BLD_CODE");
			TParm inparm = new TParm(TJDODBTool.getInstance().select(BMSSQL.
					getBMSBldCodeInfo(boolean_code)));
			if (inparm == null || inparm.getErrCode() < 0) {
				this.messageBox("E0034");
				return;
			}
			parm.addData("BLDCODE_DESC",
					inparm.getValue("BLDCODE_DESC", 0));
			parm.addData("QTY", table.getItemDouble(i, "APPLY_QTY"));
			parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
		}
		parm.setCount(parm.getCount("BLDCODE_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "BLDCODE_DESC");
		parm.addData("SYSTEM", "COLUMNS", "QTY");
		parm.addData("SYSTEM", "COLUMNS", "UNIT");

		date.setData("TABLE", parm.getData());
		
		// add by wangqing 20180110 start 泰心#6203：备血申请单打印，自动带入备血人，审核人姓名
		String applyDrName = getUserInfo(this.getValueString("DR_CODE")).getValue("USER_NAME", 0);
		date.setData("APPLY_DR_CODE", "TEXT", applyDrName);
		TParm checkR = queryCheckInfo(apply_no);
		if(checkR.getErrCode()<0){
			return;
		}
		if(checkR.getCount()>0 && checkR.getValue("CHECK_FLG", 0).equals("Y")){
			String checkDrName = getUserInfo(checkR.getValue("CHECK_USER", 0)).getValue("USER_NAME", 0);
			date.setData("CHECK_DR_CODE", "TEXT", checkDrName);
		}		
		// add by wangqing 20180110 end
		
		// 调用打印方法
		this.openPrintDialog("%ROOT%\\config\\prt\\BMS\\ApplyNo.jhw", date);
		//this.openPrintWindow("%ROOT%\\config\\prt\\BMS\\ApplyNo.jhw", date);
	}

	/**
	 * 查询病患信息
	 */
	public void onQueryPatInfo() {
		mr_no = this.getValueString("MR_NO");
		Pat pat = Pat.onQueryByMrNo(mr_no);
		if (pat == null) {
			this.messageBox("E0116");
			ClearPatInfo();
			return;
		}
		mr_no = pat.getMrNo();
		TParm parm = new TParm();
		parm.setData("MR_NO", mr_no);
		if(!"".equals(adm_type)){
			parm.setData("IO_TYPE", adm_type);
		}
		TParm result = BMSTool.getInstance().onQueryPat(parm);
		if (result == null || result.getCount("ADM_TYPE") <= 0) {
			this.messageBox("E0116");
			ClearPatInfo();
			return;
		}
		else {
			result = (TParm) openDialog("%ROOT%\\config\\bms\\BMSQueryPat.x",
					parm);
			if (result == null || result.getCount("ADM_TYPE") == 0) {
				this.messageBox("E0116");
				ClearPatInfo();
				return;
			}
			adm_type = result.getValue("ADM_TYPE");
			mr_no = result.getValue("MR_NO");
			pat = Pat.onQueryByMrNo(mr_no);
			this.setValue("MR_NO", mr_no);
			ipd_no = pat.getIpdNo();
			this.setValue("IPD_NO", ipd_no);
			case_no = result.getValue("CASE_NO");
			this.setValue("CASE_NO", case_no);
			if (!"en".equals(language)) {
				this.setValue("PAT_NAME", pat.getName());
			}
			else {
				this.setValue("PAT_NAME", pat.getName1());
			}
			this.setValue("SEX_CODE", pat.getSexCode());
			Timestamp date = SystemTool.getInstance().getDate();
			if (!"en".equals(language)) {
				this.setValue("AGE",
						StringUtil.getInstance().showAge(pat.getBirthday(),
								date));
			}
			else {
				String[] args = StringTool.CountAgeByTimestamp(pat.getBirthday(),
						date);
				this.setValue("AGE", args[0] + "Y");
			}
			this.setValue("IDNO", pat.getIdNo());
			this.setValue("CTZ1_CODE", pat.getCtz1Code());
			if (pat.getBloodRHType().equals("+")) {
				this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(true);
				this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
			}
			else if (pat.getBloodRHType().equals("-")) {
				this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
				this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(true);
			}
			else {
				this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
				this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
			}
			// 病患来源
			if ("O".equals(adm_type)) {
				if (!"en".equals(language)) {
					this.setValue("ADM_TYPE", "门诊");
				}
				else {
					this.setValue("ADM_TYPE", "O");
				}
			}
			else if ("E".equals(adm_type)) {
				if (!"en".equals(language)) {
					this.setValue("ADM_TYPE", "急诊");
				}
				else {
					this.setValue("ADM_TYPE", "E");
				}
			}
			else if ("I".equals(adm_type)) {
				if (!"en".equals(language)) {
					this.setValue("ADM_TYPE", "住院");
				}
				else {
					this.setValue("ADM_TYPE", "I");
				}
			}
			this.setValue("DEPT", result.getValue("DEPT_CODE"));
			this.setValue("BED_NO", result.getValue("BED_NO"));

			this.setValue("TEST_BLD", pat.getBloodType());
			this.setValue("DEPT_CODE", result.getValue("DEPT_CODE"));

			String bed_no = result.getValue("BED_NO", 0);
			if (!"".equals(bed_no)) {
				TParm bedParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT STATION_CODE FROM SYS_BED WHERE BED_NO = '" +
								bed_no + "'"));
				this.setValue("STATION_CODE",
						bedParm.getValue("STATION_CODE", 0));
			}

			this.onFilterDeptCode();
			( (TComboBox)this.getComponent("HBSAG")).grabFocus();
		}
	}

	/**
	 * 过滤输血原因_1
	 */
	public void onFilterTranrsnCode1() {
		String tranrsn_code_1 = getValueString("TRANRSN_CODE1");
	}

	/**
	 * 过滤输血原因_2
	 */
	public void onFilterTranrsnCode2() {
		String tranrsn_code_1 = getValueString("TRANRSN_CODE1");
		String tranrsn_code_2 = getValueString("TRANRSN_CODE2");
	}

	/**
	 * 过滤医生
	 */
	public void onFilterDeptCode() {
		String dept_code = this.getValueString("DEPT_CODE");
		TextFormatSYSOperator operator = (TextFormatSYSOperator)this.
				getComponent("DR_CODE");
		operator.setDept(dept_code);
		operator.onQuery();
	}

	/**
	 * 增加血液种类
	 */
	public void onAdd() {
		//add by yangjj 20150331 检查库存
		//    	if("".equals(this.getComboBox("TEST_BLD").getValue())){
		//    		this.messageBox("请输入血型");
		//    		return;
		//    	}
		//====pangben 20160505 非紧急备血申请，“血型”为必填项，紧急备血申请，“血型”为非必填项
		if(this.getRadioButton("URG_FLG_N").isSelected()){
			//检验血型
			if("".equals(this.getComboBox("TEST_BLD").getValue())){
				this.messageBox("请输入血型");
				return;
			}
		}
		/*TParm checkParm = new TParm();
    	checkParm.setData("BLD_CODE", this.getComboBox("BL00D_CODE").getValue());
    	checkParm.setData("APPLY_QTY", this.getValueDouble("APPLY_QTY")+"");
    	checkParm.setData("BLD_TYPE", this.getValueString("TEST_BLD"));*/
		//20150403泰心医院会议 不需要提示库存不足
		/*
    	if(!checkQty(checkParm)){
    		this.messageBox("申请的血液，库存不足");
    	}
		 */


		TTable table = this.getTable("TABLE");
		if (this.getComboBox("BL00D_CODE").getSelectedIndex() <= 0) {
			this.messageBox("E0138");
			return;
		}
		if (this.getValueDouble("APPLY_QTY") <= 0) {
			this.messageBox("E0139");
			return;
		}

		// add by wangqing 20171211 start
		// 校验是否已经审核
		if(apply_no!=null && apply_no.trim().length()>0 && action!=null && action.equals("update")){
			TParm checkInfo = queryCheckInfo(apply_no);
			if(checkInfo.getErrCode()<0){
				this.messageBox("bug:::查询审核信息失败");
				return;
			}else if(checkInfo.getCount()<=0){
				this.messageBox("bug:::没有申请单数据");
				return;
			}else{
				String checkFlg = checkInfo.getValue("CHECK_FLG", 0);
				if(checkFlg!=null && checkFlg.equals("Y")){
					this.messageBox("已经审核，不可添加");
					return;
				}else{

				}
			} 	
		}else{

		}
		// 校验是否有申请权限
		String bldCode = getValueString("BL00D_CODE");
		double applyQty = getValueDouble("APPLY_QTY");
		String applyUnitCode = getValueString("UNIT_CODE");
		String applyDrCode = Operator.getID();
		String caseNo = getValueString("CASE_NO");
		if(isHaveApplyPermission(bldCode, applyQty, applyUnitCode, applyDrCode, caseNo)){

		}else{
			this.messageBox("没有申请权限");
			return;
		}  	
		// add by wangqing 20171211 end

		for (int i = 0; i < table.getRowCount(); i++) {
			if (getValueString("BL00D_CODE").equals(table.getItemString(i,
					"BLD_CODE"))) {
				this.messageBox("E0140");
				return;
			}
		}
		//add by huzc 20160308
		Double point = getValueDouble("APPLY_QTY");
		String point1 = point.toString();
		if(point1.endsWith("0") || point1.endsWith("5")){
			
			// add by wangqing 20171221 start
			TParm tblParm = table.getParmValue();
			if(tblParm==null){
				tblParm = new TParm();
				tblParm.setCount(0);
				table.setParmValue(tblParm);
			}
			// add by wangqing 20171221 end
					
			int row = table.addRow();
			table.setItem(row, "BLD_CODE", this.getComboBox("BL00D_CODE").getValue());
			table.setItem(row, "APPLY_QTY", this.getValueDouble("APPLY_QTY"));
			table.setItem(row, "UNIT_CODE", this.getComboBox("UNIT_CODE").getValue());		
		}else{
			this.messageBox("数量小数点后只能为0或5");
			return;
		}
	}


	/*
	 * 检查库存
	 * 有库存为true，超过库存为false
	 * */
	public boolean checkQty(TParm parm){
		Double applyQty = Double.parseDouble(parm.getValue("APPLY_QTY"));
		TParm result = new TParm(TJDODBTool.getInstance().select(
				" SELECT SUM(TOT_VOL) AS ACC_VOL FROM BMS_BLDSTOCK "+
						" WHERE BLD_CODE = '"+parm.getValue("BLD_CODE")+"' "+
						" AND BLD_TYPE = '"+parm.getValue("BLD_TYPE")+"'"
				));

		Double accVol = Double.parseDouble(result.getValue("ACC_VOL",0));
		return applyQty>accVol?false:true;
	}




	/**
	 * 删除血液种类
	 */
	public void onRemove() {
		TTable table = this.getTable("TABLE");
		int row = table.getSelectedRow();
		if (row == -1) {
			this.messageBox("E0134");
			return;
		}
		// add by wangqing 20171208 start
		// 校验是否已经审核
		if(apply_no!=null && apply_no.trim().length()>0 && action!=null && action.equals("update")){
			TParm checkInfo = queryCheckInfo(apply_no);
			if(checkInfo.getErrCode()<0){
				this.messageBox("bug:::查询审核信息失败");
				return;
			}else if(checkInfo.getCount()<=0){
				this.messageBox("bug:::没有申请单数据");
				return;
			}else{
				String checkFlg = checkInfo.getValue("CHECK_FLG", 0);
				if(checkFlg!=null && checkFlg.equals("Y")){
					this.messageBox("已经审核，不可删除");
					return;
				}else{

				}
			}
		}else{

		}
		// 校验是否有删除权限
		TParm tblParm = table.getParmValue();	
		String bldCode = tblParm.getValue("BLD_CODE", row);
		double applyQty = tblParm.getDouble("APPLY_QTY", row);
		String applyUnitCode = tblParm.getValue("UNIT_CODE", row);
		String applyDrCode = Operator.getID();
		String caseNo = getValueString("CASE_NO");
		if(isHaveApplyPermission(bldCode, applyQty, applyUnitCode, applyDrCode, caseNo)){

		}else{
			this.messageBox("没有删除权限");
			return;
		}		
		// add by wangqing 20171208 end


		/*        // 更新申请单
        if ("update".equals(action)) {
            // 该删除项是否已配血或出库
            TParm parm = new TParm();
            parm.setData("APPLY_NO", this.getValue("APPLY_NO"));
            parm.setData("BLD_CODE", table.getItemData(row, "BLD_CODE"));
            TParm result = BMSBloodTool.getInstance().onQuery(parm);
            if (result.getCount() > 0) {
                this.messageBox("E0136");
                return;
            }
        }*/
		table.removeRow(row);
	}


	/**
	 * 血液种类改变事件
	 */
	public void onBloodCodeChange() {
		TParm parm = new TParm(TJDODBTool.getInstance().select(BMSSQL.
				getBMSUnit(this.getComboBox("BL00D_CODE").getSelectedID())));
		this.getComboBox("UNIT_CODE").setValue(parm.getValue("UNIT_CODE", 0));

		//add by yangjj 20150331 查询该患者对于申请的血品是否有输血不良反应史
		TParm rParm = new TParm();
		rParm.setData("MRNO", this.getValueString("MR_NO"));
		rParm.setData("BLDCODE", this.getValueString("BLOOD_CODE"));
		TParm reactParm = new TParm(TJDODBTool.getInstance().select(getReactDateSql(rParm)));
		if (reactParm.getErrCode() < 0) {
			messageBox(reactParm.getErrText());
		}
		if(!"".equals(reactParm.getValue("LASTDATE", 0))){
			this.messageBox(
					"该患者对于申请的血品，有输血不良反应史，最近一次发生于"
							+reactParm.getValue("LASTDATE", 0)
					);
		}
	}

	//add by yangjj 20150331 查询该患者对于申请的血品是否有输血不良反应史
	public String getReactDateSql(TParm parm){
		String sql = "";
		String mrNo = parm.getValue("MRNO");
		String bldCode = parm.getValue("BLDCODE");

		sql += " SELECT " +
				" MAX(REACTION_DATE) AS LASTDATE " +
				" FROM BMS_SPLREACT " +
				" WHERE MR_NO='"+mrNo+"' " +
				" AND BLD_CODE='"+bldCode+"'";

		return sql;
	}

	/**
	 * 接受返回值方法
	 *
	 * @param tag
	 * @param obj
	 */
	public void popReturn1(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icd_code = parm.getValue("ICD_CODE");
		String icd_desc = parm.getValue("ICD_CHN_DESC");
		if (!StringUtil.isNullString(icd_code)) {
			icd_code_1 = icd_code;
			icd_desc_1 = icd_desc;
			if (!"en".equals(language)) {
				this.setValue("DIAG_CODE1", icd_desc_1);
			}
			else {
				this.setValue("DIAG_CODE1", parm.getValue("ICD_ENG_DESC"));
			}
		}
	}

	/**
	 * 接受返回值方法
	 *
	 * @param tag
	 * @param obj
	 */
	public void popReturn2(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icd_code = parm.getValue("ICD_CODE");
		String icd_desc = parm.getValue("ICD_CHN_DESC");
		if (!StringUtil.isNullString(icd_code)) {
			icd_code_2 = icd_code;
			icd_desc_2 = icd_desc;
			if (!"en".equals(language)) {
				this.setValue("DIAG_CODE2", icd_desc_2);
			}
			else {
				this.setValue("DIAG_CODE2", parm.getValue("ICD_ENG_DESC"));
			}
		}
	}

	/**
	 * 接受返回值方法
	 *
	 * @param tag
	 * @param obj
	 */
	public void popReturn3(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icd_code = parm.getValue("ICD_CODE");
		String icd_desc = parm.getValue("ICD_CHN_DESC");
		if (!StringUtil.isNullString(icd_code)) {
			icd_code_3 = icd_code;
			icd_desc_3 = icd_desc;
			if (!"en".equals(language)) {
				this.setValue("DIAG_CODE3", icd_desc_3);
			}
			else {
				this.setValue("DIAG_CODE3", parm.getValue("ICD_ENG_DESC"));
			}
		}
	}

	/**
	 * 初始画面数据
	 */
	private void initPage() {
		language = this.getLanguage();

		// 初始化血液种类
		TParm bldParm = new TParm(TJDODBTool.getInstance().select(BMSSQL.
				getBMSBloodCode()));
		this.getComboBox("BL00D_CODE").setParmValue(bldParm);

		Timestamp date = SystemTool.getInstance().getDate();
		TNull tnull = new TNull(Timestamp.class);

		// 初始化诊断
		// 设置弹出菜单
		getTextField("DIAG_CODE1").setPopupMenuParameter("UD",
				getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// 定义接受返回值方法
		getTextField("DIAG_CODE1").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn1");
		// 设置弹出菜单
		getTextField("DIAG_CODE2").setPopupMenuParameter("UD",
				getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// 定义接受返回值方法
		getTextField("DIAG_CODE2").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn2");
		// 设置弹出菜单
		getTextField("DIAG_CODE3").setPopupMenuParameter("UD",
				getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// 定义接受返回值方法
		getTextField("DIAG_CODE3").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn3");

		if (from_flg.equals("1")) {
			// 初始化备血时间
			this.setValue("PRE_DATE", date);
			// modified by wangqing 20171211 start
			// 屏蔽此代码
			/*// 用血日期
            if (tnull.equals(use_date) || use_date == null) {
                this.setValue("USE_DATE", StringTool.rollDate(date, 1));
            }
            else {
                this.setValue("USE_DATE", use_date);
            }*/
			// modified by wangqing 20171211 end
			// 备血期限
			TParm sysParm = new TParm(TJDODBTool.getInstance().select(BMSSQL.
					getBMSysParm()));
			this.setValue("END_DAYS", sysParm.getInt("END_DAYS", 0));
			// 病案号
			this.setValue("MR_NO", mr_no);
			if (!"".equals(mr_no)) {
				Pat pat = Pat.onQueryByMrNo(mr_no);
				if (!"en".equals(language)) {
					this.setValue("PAT_NAME", pat.getName());
				}else{
					this.setValue("PAT_NAME", pat.getName1());
				}
				this.setValue("SEX_CODE", pat.getSexCode());
				if (!"en".equals(language)) {
					this.setValue("AGE", StringUtil.getInstance().showAge(pat.
							getBirthday(), date));
				}
				else {
					String[] args = StringTool.CountAgeByTimestamp(pat.
							getBirthday(), date);
					this.setValue("AGE", args[0] + "Y");
				}

				this.setValue("IDNO", pat.getIdNo());
			}
			// 院内序号
			this.setValue("CASE_NO", case_no);
			// 病患来源
			if ("O".equals(adm_type)) {
				if (!"en".equals(language)) {
					this.setValue("ADM_TYPE", "门诊");
				}
				else {
					this.setValue("ADM_TYPE", "O");
				}
			}
			else if ("E".equals(adm_type)) {
				if (!"en".equals(language)) {
					this.setValue("ADM_TYPE", "急诊");
				}
				else {
					this.setValue("ADM_TYPE", "E");
				}
			}
			else if ("I".equals(adm_type)) {
				if (!"en".equals(language)) {
					this.setValue("ADM_TYPE", "住院");
				}
				else {
					this.setValue("ADM_TYPE", "I");
				}
			}
			// 申请科室
			this.setValue("DEPT_CODE",dept_code);
			// 申请医师
			this.setValue("DR_CODE",dr_code);

			parm.setData("MR_NO", mr_no);
			parm.setData("IO_TYPE", adm_type);
			parm.setData("CASE_NO", case_no);
			TParm result = BMSTool.getInstance().onQueryPat(parm);
			this.setValue("DEPT", result.getValue("DEPT_CODE", 0));
			this.setValue("BED_NO", result.getValue("BED_NO", 0));
			this.setValue("IPD_NO", result.getValue("IPD_NO", 0));
			this.setValue("CTZ1_CODE", result.getValue("CTZ1_CODE", 0));
			showDiag(case_no);//获得诊断

			// add by wangb 2016/2/22 将血型鉴定结果传回备血申请单 START
			TParm queryParm = new TParm();
			queryParm.setData("CASE_NO", case_no);
			TParm bmsResult = BMSTool.getInstance().queryBmsLisData(queryParm);
			String bmsAbo = "";
			String bmsRh = "";

			if (bmsResult.getErrCode() > -1 && bmsResult.getCount() > 0) {
				for (int i = 0; i < bmsResult.getCount(); i++) {
					if (StringUtils.isNotEmpty(bmsAbo)
							&& StringUtils.isNotEmpty(bmsRh)) {
						break;
					}

					// ABO血型
					if (StringUtils.equals("1000081", bmsResult.getValue(
							"TESTITEM_CODE", i))) {
						bmsAbo = bmsResult.getValue("TEST_VALUE", i).replace(
								"型", "").trim();
					} else if (StringUtils.equals("1000082", bmsResult
							.getValue("TESTITEM_CODE", i))) {
						bmsRh = bmsResult.getValue("TEST_VALUE", i);
					}
				}

				if (bmsRh.contains("阳")) {
					this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(true);
					this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
				} else if (bmsRh.contains("阴")) {
					this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
					this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(true);
				}

				this.setValue("TEST_BLD", bmsAbo);
			} else {
				if (result.getValue("BLOOD_RH_TYPE", 0).equals("+")) {
					this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(true);
					this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
				}
				else if (result.getValue("BLOOD_RH_TYPE", 0).equals("-")) {
					this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
					this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(true);
				}
				else {
					this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
					this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
				}

				this.setValue("TEST_BLD", result.getValue("BLOOD_TYPE", 0));
			}
			// add by wangb 2016/2/22 将血型鉴定结果传回备血申请单 END

			String bed_no = result.getValue("BED_NO", 0);
			if (!"".equals(bed_no)) {
				TParm bedParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT STATION_CODE FROM SYS_BED WHERE BED_NO = '" + bed_no + "'"));
				this.setValue("STATION_CODE", bedParm.getValue("STATION_CODE", 0));
			}
			callFunction("UI|RBC|onQueryData");
			callFunction("UI|HB|onQueryData");
			callFunction("UI|HCT|onQueryData");
			callFunction("UI|PLT|onQueryData");
			callFunction("UI|WBC|onQueryData");
			callFunction("UI|HBSAG|onQueryData");
			callFunction("UI|ANTI_HCV|onQueryData");
			callFunction("UI|ANTI_HIV|onQueryData");
			callFunction("UI|SY|onQueryData");
		}
		else if (from_flg.equals("2")) {
			this.setValue("APPLY_NO", apply_no);
			this.onQuery();
		}
		else {
			// 初始化备血时间
			this.setValue("PRE_DATE", date);
		}

	}

	/**
	 * 数据检核
	 * @return boolean
	 */
	private boolean checkData() {
		// add by wangqing 20171211 start
		if(this.getValueString("USE_DATE")==null || this.getValueString("USE_DATE").trim().length()<=0){
			this.messageBox("用血时间不能为空");
			return false;
		}		
		// add by wangqing 20171211 end

		//申请科别
		if (this.getValue("DEPT_CODE").equals("")) {
			this.messageBox("E0141");
			return false;
		}
		//申请医师
		if (this.getValue("DR_CODE").equals("")) {
			this.messageBox("E0142");
			return false;
		}
		//====pangben 20160505 非紧急备血申请，“血型”为必填项，紧急备血申请，“血型”为非必填项
		if(this.getRadioButton("URG_FLG_N").isSelected()){
			//检验血型
			if (this.getValue("TEST_BLD").equals("")) {
				this.messageBox("E0143");
				return false;
			}
		}
		//临床诊断
		if (!this.getValue("DIAG_CODE1").equals("") &&
				!this.getValue("DIAG_CODE2").equals("") &&
				!this.getValue("DIAG_CODE3").equals("")) {
			if (this.getValue("DIAG_CODE1").equals(this.getValue("DIAG_CODE2")) ||
					this.getValue("DIAG_CODE1").equals(this.getValue("DIAG_CODE3")) ||
					this.getValue("DIAG_CODE2").equals(this.getValue("DIAG_CODE3"))) {
				this.messageBox("E0144");
				return false;
			}
		}
		else if (!this.getValue("DIAG_CODE1").equals("") &&
				!this.getValue("DIAG_CODE2").equals("")) {
			if (this.getValue("DIAG_CODE1").equals(this.getValue("DIAG_CODE2"))) {
				this.messageBox("E0144");
				return false;
			}
		}
		else if (!this.getValue("DIAG_CODE1").equals("") &&
				!this.getValue("DIAG_CODE3").equals("")) {
			if (this.getValue("DIAG_CODE1").equals(this.getValue("DIAG_CODE3"))) {
				this.messageBox("E0144");
				return false;
			}
		}
		else if (!this.getValue("DIAG_CODE2").equals("") &&
				!this.getValue("DIAG_CODE3").equals("")) {
			if (this.getValue("DIAG_CODE2").equals(this.getValue("DIAG_CODE3"))) {
				this.messageBox("E0144");
				return false;
			}
		}

		//输血原因
		if (!this.getValue("TRANRSN_CODE1").equals("") &&
				!this.getValue("TRANRSN_CODE2").equals("") &&
				!this.getValue("TRANRSN_CODE3").equals("")) {
			if (this.getValue("TRANRSN_CODE1").equals(this.getValue(
					"TRANRSN_CODE2")) ||
					this.getValue("TRANRSN_CODE1").equals(this.getValue(
							"TRANRSN_CODE3")) ||
					this.getValue("TRANRSN_CODE2").equals(this.getValue(
							"TRANRSN_CODE3"))) {
				this.messageBox("E0145");
				return false;
			}
		}
		else if (!this.getValue("TRANRSN_CODE1").equals("") &&
				!this.getValue("TRANRSN_CODE2").equals("")) {
			if (this.getValue("TRANRSN_CODE1").equals(this.getValue(
					"TRANRSN_CODE2"))) {
				this.messageBox("E0145");
				return false;
			}
		}
		else if (!this.getValue("TRANRSN_CODE1").equals("") &&
				!this.getValue("TRANRSN_CODE3").equals("")) {
			if (this.getValue("TRANRSN_CODE1").equals(this.getValue(
					"TRANRSN_CODE3"))) {
				this.messageBox("E0145");
				return false;
			}
		}
		else if (!this.getValue("TRANRSN_CODE2").equals("") &&
				!this.getValue("TRANRSN_CODE3").equals("")) {
			if (this.getValue("TRANRSN_CODE2").equals(this.getValue(
					"TRANRSN_CODE3"))) {
				this.messageBox("E0145");
				return false;
			}
		}

		//申请血品
		if (this.getTable("TABLE").getRowCount() < 1) {
			this.messageBox("E0146");
			return false;
		}
		//=============pangben modify 20110623 start
		if(this.getValueDouble("HB")>=1000){
			this.messageBox("血红蛋白超过最大数值");
			return false;
		}
		if(this.getValueDouble("HCT")>=100){
			this.messageBox("血细胞压积数值无效");
			return false;
		}
		//=============pangben modify 20110623 stop
		return true;
	}

	/**
	 * 清空病患信息
	 */
	private void ClearPatInfo() {
		String clearStr = "MR_NO;IPD_NO;CASE_NO;PAT_NAME;SEX_CODE;AGE;IDNO;"
				+ "ADM_TYPE;CTZ1_CODE;DEPT;BED_NO;STATION_CODE";
		this.clearValue(clearStr);
	}

	/**
	 * 清空申请单主项信息
	 */
	private void ClearApplyMInfo() {
		String clearStr =
				"APPLY_NO;PRE_DATE;END_DAYS;USE_DATE;CH_CBD;CH_CSF;CH_CTS;"
						+ "CH_CTS;DEPT_CODE;DR_CODE;TRANRSN_CODE1;TRANRSN_CODE2;"
						+ "TRANRSN_CODE3;TEST_BLD;DIAG_CODE1;DIAG_CODE2;DIAG_CODE3;"
						+ "PTT;PT;ALB;HB;HCT;OTHER;OTHER;HBSAG;ANTI_HCV;ANTI_HIV;"
						+ "SY;RBC;HB;HCT;WBC;PLT";
		this.clearValue(clearStr);
		this.getRadioButton("URG_FLG_N").setSelected(true);
		this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
		this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("PRE_DATE", date);
	}

	/**
	 * 清空申请单细项信息
	 */
	private void ClearApplyDInfo() {
		String clearStr = "BL00D_CODE;APPLY_QTY;UNIT_CODE";
		this.clearValue(clearStr);
		this.getTable("TABLE").removeRowAll();
	}


	/**
	 * 得到Table对象
	 *
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 得到ComboBox对象
	 *
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * 得到RadioButton对象
	 *
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	/**
	 * 得到TextField对象
	 *
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}


	/**
	 * 取血单
	 */
	public void onTake() {
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", this.adm_type);
		parm.setData("PAT_NAME", getValueString("PAT_NAME"));
		parm.setData("APPLY_NO", getValueString("APPLY_NO"));
		parm.setData("CASE_NO", getValueString("CASE_NO"));
		parm.setData("IPD_NO", getValueString("IPD_NO"));
		parm.setData("MR_NO", getValueString("MR_NO"));
		parm.setData("BED_NO", getValueString("BED_NO"));
		parm.setData("STATION_CODE", getValueString("STATION_CODE"));
		parm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
		parm.setData("BLOOD_TYPE", this.getComboBox("TEST_BLD").getValue());
		if (this.getRadioButton("BLOOD_RH_TYPE_A").isSelected()) {
			parm.setData("BLOOD_RH_TYPE", "+");
		} else {
			parm.setData("BLOOD_RH_TYPE", "-");
		}
		parm.setData("TYPE", "INSERT");
		this.openDialog("%ROOT%\\config\\bms\\BMSBloodTake.x", parm);
		onShowTakeNO();
	}
	/**
	 * 
	 */
	public void onShowTakeNO() {
		String applyNo=this.getValueString("APPLY_NO");
		String caseNo=this.getValueString("CASE_NO");
		String mrNo=this.getValueString("MR_NO");
		String sql = "SELECT * FROM BMS_BLDTAKEM WHERE " + " MR_NO='" + mrNo
				+ "' AND CASE_NO='" + caseNo + "' AND APPLY_NO='" + applyNo
				+ "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		//this.getTable("TABLETK").setParmValue(result);
	}

	// -------------------------------tool start-------------------------


	// ------------------------tool start-----------------
	/**
	 * 判断是否有申请权限
	 * @param bldCode
	 * @param bldQty
	 * @param bldUnitCode
	 * @param applyDrCode
	 * @param caseNo
	 * @return
	 */
	public boolean isHaveApplyPermission(String bldCode, double bldQty, String bldUnitCode, String applyDrCode, String caseNo){
		if(bldCode==null || bldCode.trim().length()<=0 ){
			this.messageBox("申请血液种类不能为空 ");
			return false;
		}else if(bldQty<=0){
			this.messageBox("申请血液数量不能为空 ");
			return false;
		}else if(bldUnitCode==null || bldUnitCode.trim().length()<=0){
			this.messageBox("申请血液单位不能为空 ");
			return false;
		}else if(applyDrCode==null || applyDrCode.trim().length()<=0){
			this.messageBox("申请医师不能为空 ");
			return false;
		}else{

		}
		// 申请医师职称
		String applyDrPos = "";
		TParm applyDrInfo = this.getUserInfo(applyDrCode);
		if(applyDrInfo.getErrCode()<0){
			this.messageBox("bug:::查询申请医师信息出错");
			return false;
		}else if(applyDrInfo.getCount()<=0){
			this.messageBox("没有申请医师信息");
			return false;
		}else{
			applyDrPos = applyDrInfo.getValue("POS_CODE", 0);
		}	
		// 校验紧急备血
		String urgFlg = this.getValueString("URG_FLG_Y");
		if(urgFlg!=null && urgFlg.equals("Y")){
			return true;
		}else{

		}
		if(bldCode.equals("01")){// 全血
			if(bldUnitCode.equals("9")){
				if(this.isAttendDr(applyDrCode, caseNo)){// 主治医师
					return true;
				}else if(applyDrPos!=null && applyDrPos.equals("232")){// 副主任
					return true;
				}else if(applyDrPos!=null && applyDrPos.equals("231")){// 主任
					return true;
				}else{
					return false;
				}		
			}else{
				return false;
			}	
		}else if(bldCode.equals("02")){// 悬浮红细胞
			if(bldUnitCode.equals("162")){
				if(this.isAttendDr(applyDrCode, caseNo)){// 主治医师
					return true;
				}else if(applyDrPos!=null && applyDrPos.equals("232")){// 副主任
					return true;
				}else if(applyDrPos!=null && applyDrPos.equals("231")){// 主任
					return true;
				}else{
//					this.messageBox("5555555555555555555");
					return false;
				}		
			}else{
				
//				this.messageBox("{bldUnitCode:"+bldUnitCode+"}");
				return false;
			}			
		}else{
			return true;
		}	
	}



	/**
	 * 判断是否有审核权限
	 * @param applyNo 申请单号
	 * @param userId 审核医师
	 * @param caseNo 就诊号
	 * @return true，有权限；false，没有权限
	 */
	public boolean isHaveCheckPermission(String applyNo, String userId, String caseNo){
		if(userId==null || userId.trim().length()<=0){
			this.messageBox("bug:::userId is null");
			return false;
		}else if(applyNo==null || applyNo.trim().length()<=0){
			this.messageBox("bug:::applyNo is null");
			return false;
		}else if(caseNo==null || caseNo.trim().length()<=0){
			this.messageBox("bug:::caseNo is null");
			return false;
		}else{

		}
		TParm parm = new TParm();
		parm.setData("APPLY_NO", applyNo);
		TParm resultM = BMSApplyMTool.getInstance().onApplyQuery(parm);
		if(resultM.getErrCode()<0){
			this.messageBox("bug:::查询申请单主项出错");
			return false;
		}else if(resultM.getCount()<=0){
			this.messageBox("没有申请单主项信息");
			return false;
		}else{

		}
		TParm resultD = BMSApplyDTool.getInstance().onApplyQuery(parm);
		if(resultD.getErrCode()<0){
			this.messageBox("bug:::查询申请细项出错");
			return false;
		}else if(resultD.getCount()<=0){
			this.messageBox("没有申请细项信息");
			return false;
		}else{

		}
		String applyDrCode = resultM.getValue("DR_CODE", 0);// 申请医师
		if(applyDrCode==null || applyDrCode.trim().length()<=0){
			this.messageBox("bug:::申请医师不能为空");
			return false;
		}else{

		}
		// 申请医师职称
		String applyDrPos = "";
		TParm applyDrInfo = this.getUserInfo(applyDrCode);
		if(applyDrInfo.getErrCode()<0){
			this.messageBox("bug:::查询申请医师信息出错");
			return false;
		}else if(applyDrInfo.getCount()<=0){
			this.messageBox("没有申请医师信息");
			return false;
		}else{
			applyDrPos = applyDrInfo.getValue("POS_CODE", 0);
		}	
		// 审核医师职称
		String userPos = "";
		TParm userInfo = this.getUserInfo(userId);
		if(userInfo.getErrCode()<0){
			this.messageBox("bug:::查询用户信息出错");
			return false;
		}else if(userInfo.getCount()<=0){
			this.messageBox("没有用户信息");
			return false;
		}else{
			userPos = userInfo.getValue("POS_CODE", 0);
		}	
		
		// 校验紧急备血
		String urgFlg = this.getValueString("URG_FLG_Y");
		if(urgFlg!=null && urgFlg.equals("Y")){
			return true;
		}else{

		}
				
		boolean flg = false;// 权限助记
		for (int i = 0; i < resultD.getCount("BLD_CODE"); i++) {
			String bldCode = resultD.getValue("BLD_CODE", i);// 血液种类
			double apppyQty = resultD.getDouble("APPLY_QTY", i);// 申请数量
			String unitCode = resultD.getValue("UNIT_CODE", i);// 单位
			if(bldCode!=null && bldCode.equals("01")){// 全血
				if(unitCode !=null && unitCode.equals("9")){// ML
					if(apppyQty<800){
						/*if(isAttendDr(applyDrCode, caseNo)){// 申请医生是主治医师
							if(userPos!=null && userPos.equals("232") || userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(applyDrPos!=null && applyDrPos.equals("232")){// 申请医生是副主任
							if(userPos!=null && userPos.equals("232") || userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}	
						}else if(applyDrPos!=null && applyDrPos.equals("231")){// 申请医生是主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else{
							this.messageBox("bug:::申请医师权限错误");
							flg = false;
						}*/
						
						if(applyDrPos!=null && applyDrPos.equals("231")){// 申请医生是主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(applyDrPos!=null && applyDrPos.equals("232")){// 申请医生是副主任
							if(userPos!=null && userPos.equals("232") || userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}	
						}else if(isAttendDr(applyDrCode, caseNo)){// 申请医生是主治医师
							if(userPos!=null && userPos.equals("232") || userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else{
							this.messageBox("bug:::申请医师权限错误");
							flg = false;
						}
						
					}else if(apppyQty>=800 && apppyQty<1600){
						/*if(isAttendDr(applyDrCode, caseNo)){// 申请医生是主治医师
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(applyDrPos!=null && applyDrPos.equals("232")){// 申请医生是副主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(applyDrPos!=null && applyDrPos.equals("231")){// 申请医生是主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else{
							this.messageBox("bug:::申请医师权限错误");
							flg = false;
						}*/
						
						if(applyDrPos!=null && applyDrPos.equals("231")){// 申请医生是主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(applyDrPos!=null && applyDrPos.equals("232")){// 申请医生是副主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(isAttendDr(applyDrCode, caseNo)){// 申请医生是主治医师
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else{
							this.messageBox("bug:::申请医师权限错误");
							flg = false;
						}	
					}else if(apppyQty>=1600){
						/*if(isAttendDr(applyDrCode, caseNo)){// 申请医生是主治医师
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(applyDrPos!=null && applyDrPos.equals("232")){// 申请医生是副主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(applyDrPos!=null && applyDrPos.equals("231")){// 申请医生是主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else{
							this.messageBox("bug:::申请医师权限错误");
							flg = false;
						}*/
						
						if(applyDrPos!=null && applyDrPos.equals("231")){// 申请医生是主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(applyDrPos!=null && applyDrPos.equals("232")){// 申请医生是副主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(isAttendDr(applyDrCode, caseNo)){// 申请医生是主治医师
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else{
							this.messageBox("bug:::申请医师权限错误");
							flg = false;
						}	
					}else{
						this.messageBox("bug:::申请数量错误");
						flg = false;
					}					
				}else{
					this.messageBox("bug:::单位错误");
					flg = false;
				}	
			}else if(bldCode!=null && bldCode.equals("02")){// 悬浮红细胞
				if(unitCode !=null && unitCode.equals("162")){
					if(apppyQty<=4){
						/*if(isAttendDr(applyDrCode, caseNo)){// 申请医生是主治医师
							if(userPos!=null && userPos.equals("232") || userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(applyDrPos!=null && applyDrPos.equals("232")){// 申请医生是副主任
							if(userPos!=null && userPos.equals("232") || userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(applyDrPos!=null && applyDrPos.equals("231")){// 申请医生是主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else{
							this.messageBox("bug:::申请医师权限错误");
							flg = false;
						}*/
						
						if(applyDrPos!=null && applyDrPos.equals("231")){// 申请医生是主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(applyDrPos!=null && applyDrPos.equals("232")){// 申请医生是副主任
							if(userPos!=null && userPos.equals("232") || userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(isAttendDr(applyDrCode, caseNo)){// 申请医生是主治医师
							if(userPos!=null && userPos.equals("232") || userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else{
							this.messageBox("bug:::申请医师权限错误");
							flg = false;
						}	
					}else if(apppyQty>4){
						/*if(isAttendDr(applyDrCode, caseNo)){// 申请医生是主治医师
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(applyDrPos!=null && applyDrPos.equals("232")){// 申请医生是副主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(applyDrPos!=null && applyDrPos.equals("231")){// 申请医生是主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else{
							this.messageBox("bug:::申请医师权限错误");
							flg = false;
						}*/
						
						if(applyDrPos!=null && applyDrPos.equals("231")){// 申请医生是主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(applyDrPos!=null && applyDrPos.equals("232")){// 申请医生是副主任
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else if(isAttendDr(applyDrCode, caseNo)){// 申请医生是主治医师
							if(userPos!=null && userPos.equals("231")){
								flg = true;
							}else{
								flg = false;
							}
						}else{
							this.messageBox("bug:::申请医师权限错误");
							flg = false;
						}	
					}else{
						this.messageBox("申请数量错误");
						flg = false;
					}
				}else{
					this.messageBox("单位错误");
					flg = false;
				}				
			}else{// 所有人都有权限
				flg = true;
			}	
			if(!flg){
				break;
			}else{		

			}
		}
		return flg;
	}



	/**
	 * 判断是否是主治医师
	 * @param userId
	 * @param caseNo
	 * @return
	 */
	public boolean isAttendDr(String userId, String caseNo){
		if(userId==null || userId.trim().length()<=0){
			this.messageBox("bug:::userId is null");
			return false;
		}else if(caseNo==null || caseNo.trim().length()<=0){
			this.messageBox("bug:::caseNo is null");
			return false;
		}else{

		}
		String attendingDrCode = "";// 当前患者的主治医生
		// 查询当前病患的主治医生
		String sql = " SELECT CASE_NO, ATTEND_DR_CODE FROM ADM_INP WHERE CASE_NO='"+caseNo+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			this.messageBox("bug:::查询当前病患的主治医师出错");
			return false;
		}else if(result.getCount()<=0){
			this.messageBox("没有当前病患的住院信息");
			return false;
		}else if(result.getValue("ATTEND_DR_CODE", 0)==null || result.getValue("ATTEND_DR_CODE", 0).trim().length()<=0){
			this.messageBox("当前患者的主治医生为空");
			return false;
		}else{
			attendingDrCode = result.getValue("ATTEND_DR_CODE", 0);
		}
		if(userId.equals(attendingDrCode)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 获取用户信息
	 * @param userId
	 * @return
	 */
	public TParm getUserInfo(String userId){
		// add by wangqing 20180110 新增患者姓名
		String  sql = " SELECT USER_ID, POS_CODE, USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"+userId+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;		
	}

	/**
	 * 查询审核信息
	 * @param applyNo
	 * @return
	 */
	public TParm queryCheckInfo(String applyNo){
		// add by wangqing 20180110 新增审核人、审核日期
		String sql = " SELECT APPLY_NO, CHECK_FLG, CHECK_USER, CHECK_DATE FROM BMS_APPLYM WHERE APPLY_NO='"+applyNo+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	// ------------------------------tool end------------------------------

}
