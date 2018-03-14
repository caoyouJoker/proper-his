package com.javahis.ui.inw;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
//import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
//import com.dongyang.manager.TIOM_AppServer;
//import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
//import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TRadioButton;
//import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
//import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
//import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.util.StringUtil;

public class INWTransferSHeetWoControl extends TControl {
	//单据信息
//	private TextFormatDept FROM_DEPT;//转出科室
//	private TTextField BED;//床号
//	private TTextField PAT_NAME;//姓名
//	private TComboBox SEX;//性别
	private TTextFormat DIAGNOSIS;//诊断
	private TTextFormat OPERATION_CODE;//拟行手术
//	private TTextFormat TRANSFER_DATE;//交接时间
	//交接查核表
//	private TCheckBox SKIN_PREPARATION_FLG;//皮肤准备	
//	private TCheckBox CROSSMATCH_FLG;//交叉配血
//	private TCheckBox SKIN_TEST_FLG;//皮试
//	private TCheckBox BOWEL_PREPARATION_FLG;//肠道准备
//	private TCheckBox PREPARE_EDUCATION_FLG;//术前宣教
//	private TCheckBox DENTAL_CARE_FLG;//口腔清洁
//	private TCheckBox NASAL_CARE_FLG;//鼻腔清洁
	//患者情况
//	private TNumberTextField TEMPERATURE;//体温
//	private TNumberTextField PULSE;//脉搏
//	private TNumberTextField RESPIRE;//呼吸
//	private TTextField BP;//收缩压/舒张压
//	private TComboBox ACTIVE_TOOTH_FLG;//活动牙齿
//	private TComboBox FALSE_TOOTH_FLG;//义齿
//	private TTextField GENERAL_MARK;//一般情况备注
//	private TComboBox ALLERGIC_FLG;//过敏
//	private TComboBox INFECT_FLG;//传染病
	//术前准备
//	private TNumberTextField WEIGHT;//术晨体重
//	private TComboBox SKIN_BREAK_FLG;//皮损
//	private TComboBox SKIN_BREAK_POSITION;//皮损部位
//	private TComboBox BLOOD_TYPE;//血型
	private TRadioButton RHPOSITIVE_FLG_P;//RH阳性
	private TRadioButton RHPOSITIVE_FLG_R;//RH阴性
	private TComboBox CROSS_MATCH;//交叉配血
//	private TTextField OPE_PRE_MARK;//术前准备备注
	//病历交接
//	private TCheckBox OPE_INFORM_FLG;//手术同意书
//	private TCheckBox ANA_SINFORM_FLG;//麻醉同意书
//	private TCheckBox BLOOD_INFORM_FLG;//输血同意书
    	
	private boolean updateFlg = false;
//	private String TRANSFER_CODE="";
	private TParm recptype=null;
	private String bp []=null;

	public void onInit() {
		super.onInit();
		recptype = this.getInputParm();
//		recptype=this.getRecptype();
//		TRANSFER_CODE=recptype.getValue("TRANSFER_CODE");
//		System.out.println("recptype:"+recptype);
		if (recptype == null) {
			this.messageBox("界面初始化失败请重新打开");
			this.onClosing();
			return;
		}
		onComponentInit();// 界面组件初始化
		CROSS_MATCH.setSelectedIndex(0);
//		System.out.println(recptype.getValue("TRANSFER_CODE"));
		if (StringUtil.isNullString(recptype.getValue("TRANSFER_CODE"))) {
			updateFlg = false;
            //汇总患者数据
			this.onSetUI(getComponentValue());
		} else {
			updateFlg = true;
			this.callFunction("UI|save|setVisible", false);
			this.onSetUI(this.onQuery(recptype.getValue("TRANSFER_CODE")));
		}
		
		
		  //20170327 zhanglei 在界面如果ADM_INP表DAY_OPE_FLG是Y显示日间手术
		  String sqlRJ = " SELECT DAY_OPE_FLG FROM ADM_INP WHERE MR_NO = '"+recptype.getValue("MR_NO")+"'  AND  "
		  		+ "CASE_NO = '"+recptype.getValue("CASE_NO") + "'";
				   
		  TParm parmRJ = new TParm(TJDODBTool.getInstance().select(sqlRJ));  
		  
		  //this.messageBox(parmRJ.getValue("DAY_OPE_FLG"));
		  
		  if( parmRJ.getValue("DAY_OPE_FLG").equals("[Y]")){
			  callFunction("UI|DAY_OPE_FLG|Visible", true);	  
		  }
		  else{
			  callFunction("UI|DAY_OPE_FLG|Visible", false);
		  }
		
		
		
		
	}

	public void onComponentInit() {
		//单据信息
//		  FROM_DEPT =(TextFormatDept) getComponent("FROM_DEPT");//转出科室
//		  BED = (TTextField) getComponent("BED");//床号
//		  PAT_NAME = (TTextField) getComponent("PAT_NAME");//姓名
//		  SEX = (TComboBox) getComponent("SEX");//性别
//		  AGE = (TTextField) getComponent("AGE");//年龄
		  DIAGNOSIS = (TTextFormat) getComponent("DIAGNOSIS");//诊断
		  OPERATION_CODE = (TTextFormat) getComponent("OPERATION_CODE");//拟行手术
//		  TRANSFER_DATE = (TTextFormat) getComponent("TRANSFER_DATE");//交接时间
		//交接查核表
//		  SKIN_PREPARATION_FLG = (TCheckBox) getComponent("SKIN_PREPARATION_FLG");//皮肤准备	
//		  CROSSMATCH_FLG = (TCheckBox) getComponent("CROSSMATCH_FLG");//交叉配血
//		  SKIN_TEST_FLG = (TCheckBox) getComponent("SKIN_TEST_FLG");//皮试
//		  BOWEL_PREPARATION_FLG = (TCheckBox) getComponent("BOWEL_PREPARATION_FLG");//肠道准备
//		  PREPARE_EDUCATION_FLG = (TCheckBox) getComponent("PREPARE_EDUCATION_FLG");//术前宣教
//		  DENTAL_CARE_FLG = (TCheckBox) getComponent("DENTAL_CARE_FLG");//口腔清洁
//		  NASAL_CARE_FLG = (TCheckBox) getComponent("NASAL_CARE_FLG");//鼻腔清洁
		//患者情况
//		  TEMPERATURE = (TNumberTextField) getComponent("TEMPERATURE");//体温
//		  PULSE = (TNumberTextField) getComponent("PULSE");//脉搏
//		  RESPIRE = (TNumberTextField) getComponent("RESPIRE");//呼吸
//		  BP = (TTextField) getComponent("BP");//收缩压/舒张压
//		  ACTIVE_TOOTH_FLG = (TComboBox) getComponent("ACTIVE_TOOTH_FLG");//活动牙齿
//		  FALSE_TOOTH_FLG = (TComboBox) getComponent("FALSE_TOOTH_FLG");//义齿
//		  GENERAL_MARK = (TTextField) getComponent("GENERAL_MARK");//一般情况备注
//		  ALLERGIC_FLG = (TComboBox) getComponent("ALLERGIC_FLG");//过敏
//		  INFECT_FLG = (TComboBox) getComponent("INFECT_FLG");//传染病
		//术前准备
//		  WEIGHT = (TNumberTextField) getComponent("WEIGHT");//术晨体重
//		  SKIN_BREAK_FLG = (TComboBox) getComponent("SKIN_BREAK_FLG");//皮损
//		  SKIN_BREAK_POSITION = (TComboBox) getComponent("SKIN_BREAK_POSITION");//皮损部位
//		  BLOOD_TYPE = (TComboBox) getComponent("BLOOD_TYPE");//血型
		  RHPOSITIVE_FLG_P = (TRadioButton) getComponent("RHPOSITIVE_FLG_P");//RH阳性
		  RHPOSITIVE_FLG_R = (TRadioButton) getComponent("RHPOSITIVE_FLG_R");//RH阴性
		  CROSS_MATCH = (TComboBox) getComponent("CROSS_MATCH");//交叉配血
//		  CROSS_MATCHuy.setSelectedIndex(0);
//		  OPE_PRE_MARK = (TTextField) getComponent("OPE_PRE_MARK");//术前准备备注
		//病历交接
//		  OPE_INFORM_FLG = (TCheckBox) getComponent("OPE_INFORM_FLG");//手术同意书
//		  ANA_SINFORM_FLG = (TCheckBox) getComponent("ANA_SINFORM_FLG");//麻醉同意书
//		  BLOOD_INFORM_FLG = (TCheckBox) getComponent("BLOOD_INFORM_FLG");//输血同意书
	}

	/**
	 * 查询备案信息
	 * 
	 * @return TParm
	 */
   private TParm getComponentValue(){
	   TParm tabParm = new TParm();
	   //MR_NO;IPD_NO;BED_NO_DESC;PAT_NAME;SEX_CODE;AGE;
	   //IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;
	   //PATIENT_STATUS;CTZ1_CODE;CUR_AMT;CLNCPATH_CODE;
	   //DISE_CODE;MRO_CHAT_FLG;SERVICE_LEVEL;TOTAL_AMT;
	   //TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;
	   //STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE;
	   //HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;
	   //ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE"
		//单据信息
//		  FROM_DEPT = (TComboBox) getComponent("FROM_DEPT");//转出科室
	   tabParm.setData("FROM_DEPT", recptype.getValue("DEPT_CODE"));
//		  BED = (TTextField) getComponent("BED");//床号
	   tabParm.setData("BED", recptype.getValue("BED_NO_DESC"));
//		  PAT_NAME = (TTextField) getComponent("PAT_NAME");//姓名
	   tabParm.setData("PAT_NAME", recptype.getValue("PAT_NAME"));
//	   SEX = (TComboBox) getComponent("SEX");//性别
	   tabParm.setData("SEX", recptype.getValue("SEX_CODE"));
//	   AGE = (TTextField) getComponent("AGE");//年龄
	   tabParm.setData("AGE", recptype.getValue("AGE"));
//		TParm parm = new TParm();
//		parm.setData("CASE_NO", recptype.getValue(CASE_NO));

	   //确认 过敏说明是带出(病患信息)  还是  从交接单时再填写
	   // modified by wangqing 20180302 -start 解决手术室交接单查询不到的问题
//	   TParm result = (TParm) this.openDialog("%ROOT%\\config\\ope\\OPEOpDetailList.x", recptype);
	   TParm p = new TParm();
	   p.setData("CASE_NO", recptype.getValue("CASE_NO"));
	   p.setData("TYPE_CODE", "1");
	   TParm result = (TParm) this.openDialog("%ROOT%\\config\\ope\\OPEOpDetailList.x", p);
	   // modified by wangqing 20180302 -end
	   
//	   DIAGNOSIS = (TComboBox) getComponent("DIAGNOSIS");//诊断
	   tabParm.setData("DIAGNOSIS", result.getValue("DIAG_CODE1"));
//	   OPERATION_CODE = (TComboBox) getComponent("OPERATION_CODE");//拟行手术
	   tabParm.setData("OPERATION_CODE", result.getValue("OP_CODE1"));
	   //手术单号 add lij 20170516
	   tabParm.setData("OPBOOK_SEQ", result.getValue("OPBOOK_SEQ"));
		//交接时间赋值start
	   Timestamp date = StringTool.getTimestamp(new Date());
//	   System.out.println("date"+date.toString());
//	   System.out.println(date.toString()
//				.substring(0, 10).replace('-', '/')
//				+ " 23:59:59");
	   tabParm.setData("TRANSFER_DATE", date.toString());
//		this.setValue("TRANSFER_DATE", date.toString().substring(0, 10).replace('-', '/'));
		//交接时间end

		//交接查核表
//		  SKIN_PREPARATION_FLG = (TCheckBox) getComponent("SKIN_PREPARATION_FLG");//皮肤准备	
//		  CROSSMATCH_FLG = (TCheckBox) getComponent("CROSSMATCH_FLG");//交叉配血
//		  SKIN_TEST_FLG = (TCheckBox) getComponent("SKIN_TEST_FLG");//皮试
//		  BOWEL_PREPARATION_FLG = (TCheckBox) getComponent("BOWEL_PREPARATION_FLG");//肠道准备
//		  PREPARE_EDUCATION_FLG = (TCheckBox) getComponent("PREPARE_EDUCATION_FLG");//术前宣教
//		  DENTAL_CARE_FLG = (TCheckBox) getComponent("DENTAL_CARE_FLG");//口腔清洁
//		  NASAL_CARE_FLG = (TCheckBox) getComponent("NASAL_CARE_FLG");//鼻腔清洁
		//患者情况
	   //取得体温单信息
	   TParm resultT = this.onQueryVtsntprdtl();
//		  TEMPERATURE = (TNumberTextField) getComponent("TEMPERATURE");//体温
		  tabParm.setData("TEMPERATURE", resultT.getValue("TEMPERATURE",0));
//		  PULSE = (TNumberTextField) getComponent("PULSE");//脉搏
		  tabParm.setData("PULSE", resultT.getValue("PLUSE",0));
//		  RESPIRE = (TNumberTextField) getComponent("RESPIRE");//呼吸
		  tabParm.setData("RESPIRE", resultT.getValue("RESPIRE",0));
//		  BP = (TTextField) getComponent("BP");//收缩压/舒张压
		  tabParm.setData("SBP", resultT.getValue("SYSTOLICPRESSURE",0));
		  tabParm.setData("DBP", resultT.getValue("DIASTOLICPRESSURE",0));
//		  ACTIVE_TOOTH_FLG = (TComboBox) getComponent("ACTIVE_TOOTH_FLG");//活动牙齿
//		  FALSE_TOOTH_FLG = (TComboBox) getComponent("FALSE_TOOTH_FLG");//义齿
//		  GENERAL_MARK = (TTextField) getComponent("GENERAL_MARK");//一般情况备注
//		  ALLERGIC_FLG = (TComboBox) getComponent("ALLERGIC_FLG");//过敏
		  tabParm.setData("ALLERGIC_FLG", result.getValue("ALLERGY"));
		  //fux modify 20160919  ---  OPEOpDetailList adm_inp 传入
//		  tabParm.setData("ALLERGIC_MARK", result.getValue("ALLERGIC_MARK"));
		  String sqlAdm = " SELECT ALLERGIC_MARK FROM ADM_INP WHERE MR_NO = '"+tabParm.getValue("MR_NO")+"'  ";
		  TParm parmAdm = new TParm(TJDODBTool.getInstance().select(sqlAdm));  
		  this.setValue("ALLERGIC_MARK", parmAdm.getValue("ALLERGIC_MARK",0));//过敏备注

//		  INFECT_FLG = (TComboBox) getComponent("INFECT_FLG");//传染病
		  tabParm.setData("INFECT_FLG", result.getValue("INFECT_SCR_RESULT"));
		  tabParm.setData("INFECT_SCR_RESULT_CONT", result.getValue("INFECT_SCR_RESULT_CONT"));
		//术前准备
//		  WEIGHT = (TNumberTextField) getComponent("WEIGHT");//术晨体重
		  tabParm.setData("WEIGHT", resultT.getValue("WEIGHT",0));
//		  SKIN_BREAK_FLG = (TComboBox) getComponent("SKIN_BREAK_FLG");//皮损
//		  SKIN_BREAK_POSITION = (TComboBox) getComponent("SKIN_BREAK_POSITION");//皮损部位
//		  BLOOD_TYPE = (TComboBox) getComponent("BLOOD_TYPE");//血型
		  tabParm.setData("BLOOD_TYPE", result.getValue("BLOOD_TYPE"));
		  if(result.getValue("BLlOOD_RH_TYPE").trim().equals("+")){
			  tabParm.setData("RHPOSITIVE_FLG", "Y");  
		  }else if(result.getValue("BLlOOD_RH_TYPE").trim().equals("-")){
			  tabParm.setData("RHPOSITIVE_FLG", "N");
		  }
//		  RHPOSITIVE_FLG_P = (TRadioButton) getComponent("RHPOSITIVE_FLG_P");//RH阳性
//		  RHPOSITIVE_FLG_R = (TRadioButton) getComponent("RHPOSITIVE_FLG_R");//RH阴性
		  
//		  CROSS_MATCHuy = (TComboBox) getComponent("CROSS_MATCHuy");//交叉配血
//		  OPE_PRE_MARK = (TTextField) getComponent("OPE_PRE_MARK");//术前准备备注
		//病历交接
//		  OPE_INFORM_FLG = (TCheckBox) getComponent("OPE_INFORM_FLG");//手术同意书
//		  ANA_SINFORM_FLG = (TCheckBox) getComponent("ANA_SINFORM_FLG");//麻醉同意书
//		  BLOOD_INFORM_FLG = (TCheckBox) getComponent("BLOOD_INFORM_FLG");//输血同意书
	return tabParm;   
   }
	/**
	 * 查询备案信息
	 * 
	 * @return TParm
	 */
	private TParm onQuery(String transferCode) {
		String Sql = " SELECT "+
			" A.CASE_NO,A.MR_NO,A.FROM_DEPT,A.TO_DEPT,A.BED, "+ 
			" A.PAT_NAME,A.SEX,A.AGE,B.TRANSFER_DATE,A.DIAGNOSIS, "+ 
			" A.OPERATION_CODE,A.TEMPERATURE,A.PULSE,A.RESPIRE,A.SBP, "+ 
			" A.DBP,A.ACTIVE_TOOTH_FLG,A.FALSE_TOOTH_FLG,A.GENERAL_MARK,A.ALLERGIC_FLG, "+ 
			" A.INFECT_FLG,A.INFECT_SCR_RESULT_CONT,A.WEIGHT,A.SKIN_BREAK_FLG,A.SKIN_BREAK_POSITION,A.BLOOD_TYPE, "+
			" A.RHPOSITIVE_FLG,A.CROSS_MATCHUY,A.OPE_PRE_MARK,A.OPE_INFORM_FLG,A.ANA_SINFORM_FLG, "+ 
			" A.BLOOD_INFORM_FLG,A.SKIN_PREPARATION_FLG,A.CROSSMATCH_FLG,A.SKIN_TEST_FLG,A.BOWEL_PREPARATION_FLG, "+
			" A.OPBOOK_SEQ, "+//手术单号 add lij 20170516
			//fux modify 20160919 加入 过敏说明
			" A.PREPARE_EDUCATION_FLG,A.DENTAL_CARE_FLG,A.NASAL_CARE_FLG,A.INFECT_SCR_RESULT_CONT,A.ALLERGIC_MARK "+
			" FROM INW_TRANSFERSHEET_WO A,INW_TRANSFERSHEET B "+
			" WHERE A.TRANSFER_CODE='"+transferCode+"' "+
			" AND B.TRANSFER_CODE=A.TRANSFER_CODE ";
//		System.out.println("onQuery==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("CASE_NO") < 0) {
			this.messageBox("没有查询到相应记录");
			return null;
		}
		TParm parm=new TParm();
		String names[]=tabParm.getNames();
		for (String name:names){
			parm.setData(name, tabParm.getValue(name, 0));
		}
		return parm;
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
		
//		this.setValue("NHI_CODE", tabParm.getValue("NHI_CODE",0));
//		this.setValue("NHI_ORDER_DESC", tabParm.getValue("NHI_ORDER_DESC",0));
//		this.setValue("ORDER_CODE", tabParm.getValue("ORDER_CODE",0));
//		this.setValue("ORDER_DESC", tabParm.getValue("ORDER_DESC",0));
//		this.setValue("PRICE", tabParm.getValue("PRICE",0));
//		this.setValue("ORDER_TYPE", tabParm.getValue("ORDER_TYPE",0));
//		if (this.updateFlg) {
//			this.setValue("INS_TYPE", tabParm.getValue("INS_TYPE",0));
//			this.setValue("START_DATE", this.getUpDateFromat(tabParm.getValue("START_DATE", 0)));
//			this.setValue("CHANGE_DATE", this.getUpDateFromat(tabParm.getValue("CHANGE_DATE",0)));
//			this.setValue("REG_TYPE", tabParm.getValue("REG_TYPE",0));
//	    }
		//单据信息
		  this.setValue("FROM_DEPT", tabParm.getValue("FROM_DEPT"));//转出科室
		  this.setValue("BED", tabParm.getValue("BED"));//床号
		  this.setValue("PAT_NAME", tabParm.getValue("PAT_NAME"));//姓名
		  this.setValue("SEX", tabParm.getValue("SEX"));//性别
		  this.setValue("AGE", tabParm.getValue("AGE"));//年龄
		   DIAGNOSIS.setPopupMenuSQL("SELECT A.ICD_CODE ID,A.ICD_CHN_DESC NAME,A.PY1 FROM SYS_DIAGNOSIS A WHERE A.ICD_CODE='"+
				   tabParm.getValue("DIAGNOSIS")+"' ORDER BY A.ICD_CODE");
		  this.setValue("DIAGNOSIS", tabParm.getValue("DIAGNOSIS"));//诊断
		   OPERATION_CODE.setPopupMenuSQL("SELECT A.OPERATION_ICD ID,A.OPT_CHN_DESC NAME,A.PY1 FROM SYS_OPERATIONICD A WHERE A.OPERATION_ICD='"+
				   tabParm.getValue("OPERATION_CODE")+"' ORDER BY A.OPERATION_ICD");
		  this.setValue("OPERATION_CODE", tabParm.getValue("OPERATION_CODE"));//拟行手术
//		  System.out.println("TRANSFER_DATE:"+tabParm.getValue("TRANSFER_DATE"));
		  if(!StringUtil.isNullString(tabParm.getValue("TRANSFER_DATE"))){
			  this.setValue("TRANSFER_DATE", 
					  tabParm.getValue("TRANSFER_DATE").substring(0, 19).replace('-', '/'));//交接时间			  
		  }
		//手术单号 add lij 20170516
		  this.setValue("OPBOOK_SEQ", tabParm.getValue("OPBOOK_SEQ"));
		//交接查核表
//		  this.setValue("SKIN_PREPARATION_FLG", tabParm.getValue("SKIN_PREPARATION_FLG"));//皮肤准备
//		  this.setValue("CROSSMATCH_FLG", tabParm.getValue("CROSSMATCH_FLG"));//交叉配血
//		  this.setValue("SKIN_TEST_FLG", tabParm.getValue("SKIN_TEST_FLG"));//皮试
//		  this.setValue("BOWEL_PREPARATION_FLG", tabParm.getValue("BOWEL_PREPARATION_FLG"));//肠道准备
//		  this.setValue("PREPARE_EDUCATION_FLG", tabParm.getValue("PREPARE_EDUCATION_FLG"));//术前宣教
//		  this.setValue("DENTAL_CARE_FLG", tabParm.getValue("DENTAL_CARE_FLG"));//口腔清洁
//		  this.setValue("NASAL_CARE_FLG", tabParm.getValue("NASAL_CARE_FLG"));//鼻腔清洁
		//患者情况
		  this.setValue("TEMPERATURE", tabParm.getValue("TEMPERATURE"));//体温
		  this.setValue("PULSE", tabParm.getValue("PULSE"));//脉搏
		  this.setValue("RESPIRE", tabParm.getValue("RESPIRE"));//呼吸
		  this.setValue("BP", tabParm.getValue("SBP")+"/"+tabParm.getValue("DBP"));//收缩压/舒张压
//		  this.setValue("ACTIVE_TOOTH_FLG", tabParm.getValue("ACTIVE_TOOTH_FLG"));//活动牙齿
//		  this.setValue("FALSE_TOOTH_FLG", tabParm.getValue("FALSE_TOOTH_FLG"));//义齿
//		  this.setValue("GENERAL_MARK", tabParm.getValue("GENERAL_MARK"));//一般情况备注
		  this.setValue("ALLERGIC_FLG", tabParm.getValue("ALLERGIC_FLG"));//过敏
		  //fux modify 20160919  tabParm.getValue("ALLERGIC_MARK")
//		  String sqlAdm = " SELECT ALLERGIC_MARK FROM ADM_INP WHERE MR_NO = '"+tabParm.getValue("MR_NO")+"'  ";
//		  TParm parmAdm = new TParm(TJDODBTool.getInstance().select(sqlAdm));  
//		  this.setValue("ALLERGIC_MARK", parmAdm.getValue("ALLERGIC_MARK",0));//过敏备注
		  this.setValue("ALLERGIC_MARK", tabParm.getValue("ALLERGIC_MARK"));//过敏备注
		  this.setValue("INFECT_FLG", tabParm.getValue("INFECT_FLG"));//传染病
		  this.setValue("INFECT_SCR_RESULT_CONT", tabParm.getValue("INFECT_SCR_RESULT_CONT"));//传染病结果
		  //术前准备
		  this.setValue("WEIGHT", tabParm.getValue("WEIGHT"));//术晨体重
//		  this.setValue("SKIN_BREAK_FLG", tabParm.getValue("SKIN_BREAK_FLG"));//皮损
//		  this.setValue("SKIN_BREAK_POSITION", tabParm.getValue("SKIN_BREAK_POSITION"));//皮损部位
		  this.setValue("BLOOD_TYPE", tabParm.getValue("BLOOD_TYPE"));//血型
		  if(tabParm.getValue("RHPOSITIVE_FLG").equals("Y")){
			  RHPOSITIVE_FLG_P.setSelected(true);
		  }else if(tabParm.getValue("RHPOSITIVE_FLG").equals("N")){
			  RHPOSITIVE_FLG_R.setSelected(true);
		  }
//		  this.setValue("CROSS_MATCHuy", tabParm.getValue("CROSS_MATCHuy"));//交叉配血
//		  this.setValue("OPE_PRE_MARK", tabParm.getValue("OPE_PRE_MARK"));//术前准备备注
		//病历交接
//		  this.setValue("OPE_INFORM_FLG", tabParm.getValue("OPE_INFORM_FLG"));//手术同意书
//		  this.setValue("ANA_SINFORM_FLG", tabParm.getValue("ANA_SINFORM_FLG"));//麻醉同意书
//		  this.setValue("BLOOD_INFORM_FLG", tabParm.getValue("BLOOD_INFORM_FLG"));//输血同意书
		  if (this.updateFlg) {
				//交接查核表
			  this.setValue("SKIN_PREPARATION_FLG", tabParm.getValue("SKIN_PREPARATION_FLG"));//皮肤准备
			  this.setValue("CROSSMATCH_FLG", tabParm.getValue("CROSSMATCH_FLG"));//交叉配血
			  this.setValue("SKIN_TEST_FLG", tabParm.getValue("SKIN_TEST_FLG"));//皮试
			  this.setValue("BOWEL_PREPARATION_FLG", tabParm.getValue("BOWEL_PREPARATION_FLG"));//肠道准备
			  this.setValue("PREPARE_EDUCATION_FLG", tabParm.getValue("PREPARE_EDUCATION_FLG"));//术前宣教
			  this.setValue("DENTAL_CARE_FLG", tabParm.getValue("DENTAL_CARE_FLG"));//口腔清洁
			  this.setValue("NASAL_CARE_FLG", tabParm.getValue("NASAL_CARE_FLG"));//鼻腔清洁
			//患者情况
			  this.setValue("ACTIVE_TOOTH_FLG", tabParm.getValue("ACTIVE_TOOTH_FLG"));//活动牙齿
			  this.setValue("FALSE_TOOTH_FLG", tabParm.getValue("FALSE_TOOTH_FLG"));//义齿
			  this.setValue("GENERAL_MARK", tabParm.getValue("GENERAL_MARK"));//一般情况备注
			//术前准备
			  this.setValue("SKIN_BREAK_FLG", tabParm.getValue("SKIN_BREAK_FLG"));//皮损
			  this.setValue("SKIN_BREAK_POSITION", tabParm.getValue("SKIN_BREAK_POSITION"));//皮损部位
			  this.setValue("CROSS_MATCH", tabParm.getValue("CROSS_MATCHUY"));//交叉配血
			  this.setValue("OPE_PRE_MARK", tabParm.getValue("OPE_PRE_MARK"));//术前准备备注
			//病历交接
			  this.setValue("OPE_INFORM_FLG", tabParm.getValue("OPE_INFORM_FLG"));//手术同意书
			  this.setValue("ANA_SINFORM_FLG", tabParm.getValue("ANA_SINFORM_FLG"));//麻醉同意书
			  this.setValue("BLOOD_INFORM_FLG", tabParm.getValue("BLOOD_INFORM_FLG"));//输血同意书
		  }
	}

	private TParm onGetSaveDate() {
		TParm parm = new TParm();
//		parm.setData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // 医院编码
//		parm.setData("INS_TYPE", this.getValueString("INS_TYPE")); // 医保类型
//		parm.setData("NHI_CODE", NHI_CODE.getValue()); // 收费项目编码
//		parm.setData("NHI_ORDER_DESC", NHI_ORDER_DESC.getValue()); // 收费项目名称
//		parm.setData("ORDER_CODE", ORDER_CODE.getValue()); // 院内医嘱编码
//		parm.setData("ORDER_DESC", ORDER_DESC.getValue()); // 院内医嘱名称
//		parm.setData("START_DATE", START_DATE.getValue().toString().substring(0, 10).replace("-", "")); // 开始时间
//		parm.setData("REG_TYPE", this.getValueString("REG_TYPE")); //备案状态
//		parm.setData("PRICE", PRICE.getValue()); // 实际价格
//		parm.setData("ORDER_TYPE", this.getValueString("ORDER_TYPE")); //医嘱类别
//		parm.setData("OPT_USER", Operator.getID());
//		parm.setData("OPT_TERM", Operator.getIP());
//		parm.setData("APPROVE_TYPE", "2");
		if(this.updateFlg){
			parm.setData("TRANSFER_CODE", this.recptype.getValue("TRANSFER_CODE"));
		}else{
			parm.setData("TRANSFER_CODE", 
					SystemTool.getInstance().getNo("ALL", "MRO", "TRANSFER_NO", "TRANSFER_NO"));
		}
		//fux modify 20171204
		parm.setData("CASE_NO", this.recptype.getValue("CASE_NO").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("MR_NO", this.recptype.getValue("MR_NO").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("FROM_DEPT", this.getValueString("FROM_DEPT").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("TO_DEPT", "030503");
		parm.setData("BED", this.getValueString("BED").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("PAT_NAME", this.getValueString("PAT_NAME").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("SEX", this.getValueString("SEX").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("AGE", this.getValueString("AGE").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("TRANSFER_DATE", this.getValueString("TRANSFER_DATE").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("DIAGNOSIS", this.getValueString("DIAGNOSIS").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("OPERATION_CODE", this.getValueString("OPERATION_CODE").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("TEMPERATURE", this.getValueString("TEMPERATURE").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("PULSE", this.getValueString("PULSE").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("RESPIRE", this.getValueString("RESPIRE").replace('[',' ').replace(']', ' ').toString().trim());
		//手术单号 add lij 20170516
		parm.setData("OPBOOK_SEQ", this.getValueString("OPBOOK_SEQ").replace('[',' ').replace(']', ' ').toString().trim());
		
		bp=this.getValueString("BP").split("/");
//		for(String b:bp){
//			System.out.println(b);
//		}
		if(bp.length>=2){
		    parm.setData("SBP",bp[0] );
		    parm.setData("DBP", bp[1]);
		}else{
			parm.setData("SBP","" );
			parm.setData("DBP","");
		}
		parm.setData("ACTIVE_TOOTH_FLG", this.getValueString("ACTIVE_TOOTH_FLG").replace('[',' ').replace(']', ' ').toString().trim()); 
		parm.setData("FALSE_TOOTH_FLG", this.getValueString("FALSE_TOOTH_FLG").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("GENERAL_MARK", this.getValueString("GENERAL_MARK").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("ALLERGIC_FLG", this.getValueString("ALLERGIC_FLG").replace('[',' ').replace(']', ' ').toString().trim());
		//fux modify 20160919
		parm.setData("ALLERGIC_MARK", this.getValueString("ALLERGIC_MARK").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("INFECT_FLG", this.getValueString("INFECT_FLG").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("INFECT_SCR_RESULT_CONT", this.getValueString("INFECT_SCR_RESULT_CONT").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("WEIGHT", this.getValueString("WEIGHT").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("SKIN_BREAK_FLG", this.getValueString("SKIN_BREAK_FLG").replace('[',' ').replace(']', ' ').toString().trim()); 
		parm.setData("SKIN_BREAK_POSITION", this.getValueString("SKIN_BREAK_POSITION").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("BLOOD_TYPE", this.getValueString("BLOOD_TYPE").replace('[',' ').replace(']', ' ').toString().trim());
		if(this.getValueString("RHPOSITIVE_FLG_P").equals("Y")){
			parm.setData("RHPOSITIVE_FLG", "Y");	
		}else if(this.getValueString("RHPOSITIVE_FLG_R").equals("Y")){
			parm.setData("RHPOSITIVE_FLG", "N");     
		}
		parm.setData("CROSS_MATCHUY", this.getValueString("CROSS_MATCH").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("OPE_PRE_MARK", this.getValueString("OPE_PRE_MARK").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("OPE_INFORM_FLG", this.getValueString("OPE_INFORM_FLG").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("ANA_SINFORM_FLG", this.getValueString("ANA_SINFORM_FLG").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("BLOOD_INFORM_FLG", this.getValueString("BLOOD_INFORM_FLG").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("SKIN_PREPARATION_FLG", this.getValueString("SKIN_PREPARATION_FLG").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("CROSSMATCH_FLG", this.getValueString("CROSSMATCH_FLG").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("SKIN_TEST_FLG", this.getValueString("SKIN_TEST_FLG").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("BOWEL_PREPARATION_FLG", this.getValueString("BOWEL_PREPARATION_FLG").replace('[',' ').replace(']', ' ').toString().trim()); 
		parm.setData("PREPARE_EDUCATION_FLG", this.getValueString("PREPARE_EDUCATION_FLG").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("DENTAL_CARE_FLG", this.getValueString("DENTAL_CARE_FLG").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("NASAL_CARE_FLG", this.getValueString("NASAL_CARE_FLG").replace('[',' ').replace(']', ' ').toString().trim());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("STATUS_FLG", "4");
		parm.setData("TRANSFER_CLASS", "WO");
		parm.setData("CRE_USER", Operator.getID());
//		System.out.println("onGetSaveDate"+parm);
		return parm;
	}

	public void onSave() {
//		System.out.println("onSave:"+this.updateFlg);
		if(!onIsNull()){
			return;
		}
//		System.out.println("onSave:"+this.updateFlg);
		if (this.updateFlg) {
			if(this.update(this.onGetSaveDate())){
				this.messageBox("保存成功");
			}else{
				this.messageBox("保存失败");
			}
		} else {
			if(this.insert(this.onGetSaveDate())){
				this.messageBox("保存成功");
			}else{
				this.messageBox("保存失败");
			}
		}

	}

	private boolean insert(TParm saveData) {
//		System.out.println("insertsaveData:"+saveData);
		String sql=" INSERT INTO INW_TRANSFERSHEET "+
			" ( "+
			" TRANSFER_CODE, MR_NO,  CASE_NO, PAT_NAME, FROM_DEPT, "+
			" TO_DEPT, STATUS_FLG, TRANSFER_CLASS,CRE_USER, CRE_DATE, "+
			" OPT_DATE, OPT_TERM, OPT_USER "+
			" ) VALUES ( "+
			" '"+saveData.getValue("TRANSFER_CODE")+"', '"+
			saveData.getValue("MR_NO")+"', '"+
			saveData.getValue("CASE_NO")+"','"+
			saveData.getValue("PAT_NAME")+"', '"+
			saveData.getValue("FROM_DEPT")+"', "+
			" '"+saveData.getValue("TO_DEPT")+"', '"+
			saveData.getValue("STATUS_FLG")+"', '"+
			saveData.getValue("TRANSFER_CLASS")+"','"+
			saveData.getValue("CRE_USER")+"', SYSDATE, "+
			" SYSDATE, '"+saveData.getValue("OPT_TERM")+"', '"+
			saveData.getValue("OPT_USER")+"' "+
			" )";
//		System.out.println("insert_sql:"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return false;
		}
		String sqlWo=" INSERT INTO INW_TRANSFERSHEET_WO ( "+
			" TRANSFER_CODE,CASE_NO,MR_NO,FROM_DEPT,TO_DEPT,BED, "+ 
			" PAT_NAME,SEX,AGE,TRANSFER_DATE,DIAGNOSIS, "+ 
			" OPERATION_CODE,TEMPERATURE,PULSE,RESPIRE,SBP, "+ 
			" DBP,ACTIVE_TOOTH_FLG,FALSE_TOOTH_FLG,GENERAL_MARK,ALLERGIC_FLG,ALLERGIC_MARK, "+ 
			" INFECT_FLG,INFECT_SCR_RESULT_CONT,WEIGHT,SKIN_BREAK_FLG,SKIN_BREAK_POSITION,BLOOD_TYPE, "+ 
			" RHPOSITIVE_FLG,CROSS_MATCHUY,OPE_PRE_MARK,OPE_INFORM_FLG,ANA_SINFORM_FLG, "+ 
			" BLOOD_INFORM_FLG,SKIN_PREPARATION_FLG,CROSSMATCH_FLG,SKIN_TEST_FLG,BOWEL_PREPARATION_FLG, "+ 
			" PREPARE_EDUCATION_FLG,DENTAL_CARE_FLG,NASAL_CARE_FLG,OPBOOK_SEQ, "+ 
			" OPT_TERM,OPT_USER,OPT_DATE "+
			" )   VALUES  ( "+
			" '"+saveData.getValue("TRANSFER_CODE")+"', "+ 
			" '"+saveData.getValue("CASE_NO")+"', "+  
			" '"+saveData.getValue("MR_NO")+"', "+
			" '"+saveData.getValue("FROM_DEPT")+"', "+  
			" '"+saveData.getValue("TO_DEPT")+"', "+
			" '"+saveData.getValue("BED")+"', "+
			" '"+saveData.getValue("PAT_NAME")+"', "+
			" '"+saveData.getValue("SEX")+"', "+
			" '"+saveData.getValue("AGE")+"', "+
			" SYSDATE, "+
			" '"+saveData.getValue("DIAGNOSIS")+"', "+
			" '"+saveData.getValue("OPERATION_CODE")+"', "+
			" '"+saveData.getValue("TEMPERATURE")+"', "+
			" '"+saveData.getValue("PULSE")+"', "+
			" '"+saveData.getValue("RESPIRE")+"', "+
			" '"+saveData.getValue("SBP")+"', "+
			" '"+saveData.getValue("DBP")+"', "+
			" '"+saveData.getValue("ACTIVE_TOOTH_FLG")+"', "+
			" '"+saveData.getValue("FALSE_TOOTH_FLG")+"', "+
			" '"+saveData.getValue("GENERAL_MARK")+"', "+
			" '"+saveData.getValue("ALLERGIC_FLG")+"', "+
			//fux modify 20160919
			" '"+saveData.getValue("ALLERGIC_MARK")+"', "+
			" '"+saveData.getValue("INFECT_FLG")+"', "+
			" '"+saveData.getValue("INFECT_SCR_RESULT_CONT")+"', "+
			" '"+saveData.getValue("WEIGHT")+"', "+
			" '"+saveData.getValue("SKIN_BREAK_FLG")+"', "+
			" '"+saveData.getValue("SKIN_BREAK_POSITION")+"', "+
			" '"+saveData.getValue("BLOOD_TYPE")+"', "+
			" '"+saveData.getValue("RHPOSITIVE_FLG")+"', "+
			" '"+saveData.getValue("CROSS_MATCHUY")+"', "+
			" '"+saveData.getValue("OPE_PRE_MARK")+"', "+
			" '"+saveData.getValue("OPE_INFORM_FLG")+"', "+
			" '"+saveData.getValue("ANA_SINFORM_FLG")+"', "+
			" '"+saveData.getValue("BLOOD_INFORM_FLG")+"', "+
			" '"+saveData.getValue("SKIN_PREPARATION_FLG")+"', "+
			" '"+saveData.getValue("CROSSMATCH_FLG")+"', "+
			" '"+saveData.getValue("SKIN_TEST_FLG")+"', "+
			" '"+saveData.getValue("BOWEL_PREPARATION_FLG")+"', "+
			" '"+saveData.getValue("PREPARE_EDUCATION_FLG")+"', "+
			" '"+saveData.getValue("DENTAL_CARE_FLG")+"', "+
			" '"+saveData.getValue("NASAL_CARE_FLG")+"', "+
			" '"+saveData.getValue("OPBOOK_SEQ")+"', "+//手术单号 add lij 20170516
			" '"+saveData.getValue("OPT_TERM")+"', "+
			" '"+saveData.getValue("OPT_USER")+"', "+
			" SYSDATE  ) ";
//		System.out.println("insert_sqlWo:"+sqlWo);
		TParm resultWo = new TParm(TJDODBTool.getInstance().update(sqlWo));
		if (resultWo.getErrCode() < 0) {
			this.messageBox("E0005");
			return false;
		}else{
			this.updateFlg=true;
		}
		return true;
	}

	private boolean update(TParm saveData) {
//		System.out.println("updatesaveData:"+saveData);
		String sql=" UPDATE INW_TRANSFERSHEET_WO SET "+
			" FROM_DEPT             = '"+saveData.getValue("FROM_DEPT")+"', "+
			" BED                   = '"+saveData.getValue("BED")+"', "+
			" PAT_NAME              = '"+saveData.getValue("PAT_NAME")+"', "+
			" SEX                   = '"+saveData.getValue("SEX")+"', "+
			" AGE                   = '"+saveData.getValue("AGE")+"', "+
			" DIAGNOSIS             = '"+saveData.getValue("DIAGNOSIS")+"', "+
			" OPERATION_CODE        = '"+saveData.getValue("OPERATION_CODE")+"', "+
			" TEMPERATURE           = '"+saveData.getValue("TEMPERATURE")+"', "+
			" PULSE                 = '"+saveData.getValue("PULSE")+"', "+
			" RESPIRE               = '"+saveData.getValue("RESPIRE")+"', "+
			" SBP                   = '"+saveData.getValue("SBP")+"', "+
			" DBP                   = '"+saveData.getValue("DBP")+"', "+
			" ACTIVE_TOOTH_FLG      = '"+saveData.getValue("ACTIVE_TOOTH_FLG")+"', "+
			" FALSE_TOOTH_FLG       = '"+saveData.getValue("FALSE_TOOTH_FLG")+"', "+
			" GENERAL_MARK          = '"+saveData.getValue("GENERAL_MARK")+"', "+
			" ALLERGIC_FLG          = '"+saveData.getValue("ALLERGIC_FLG")+"', "+
			//fux modify 20160919
			" ALLERGIC_MARK         = '"+saveData.getValue("ALLERGIC_MARK")+"', "+
			" INFECT_FLG            = '"+saveData.getValue("INFECT_FLG")+"', "+
			" INFECT_SCR_RESULT_CONT= '"+saveData.getValue("INFECT_SCR_RESULT_CONT")+"', "+
			" WEIGHT                = '"+saveData.getValue("WEIGHT")+"', "+
			" SKIN_BREAK_FLG        = '"+saveData.getValue("SKIN_BREAK_FLG")+"', "+
			" SKIN_BREAK_POSITION   = '"+saveData.getValue("SKIN_BREAK_POSITION")+"', "+
			" BLOOD_TYPE            = '"+saveData.getValue("BLOOD_TYPE")+"', "+
			" RHPOSITIVE_FLG        = '"+saveData.getValue("RHPOSITIVE_FLG")+"', "+
			" CROSS_MATCHUY         = '"+saveData.getValue("CROSS_MATCHUY")+"', "+
			" OPE_PRE_MARK          = '"+saveData.getValue("OPE_PRE_MARK")+"', "+
			" OPE_INFORM_FLG        = '"+saveData.getValue("OPE_INFORM_FLG")+"', "+
			" ANA_SINFORM_FLG       = '"+saveData.getValue("ANA_SINFORM_FLG")+"', "+
			" BLOOD_INFORM_FLG      = '"+saveData.getValue("BLOOD_INFORM_FLG")+"', "+
			" SKIN_PREPARATION_FLG  = '"+saveData.getValue("SKIN_PREPARATION_FLG")+"', "+
			" CROSSMATCH_FLG        = '"+saveData.getValue("CROSSMATCH_FLG")+"', "+
			" SKIN_TEST_FLG         = '"+saveData.getValue("SKIN_TEST_FLG")+"', "+
			" BOWEL_PREPARATION_FLG = '"+saveData.getValue("BOWEL_PREPARATION_FLG")+"', "+
			" PREPARE_EDUCATION_FLG = '"+saveData.getValue("PREPARE_EDUCATION_FLG")+"', "+
			" DENTAL_CARE_FLG       = '"+saveData.getValue("DENTAL_CARE_FLG")+"', "+
			" NASAL_CARE_FLG       = '"+saveData.getValue("NASAL_CARE_FLG")+"', "+
			" OPBOOK_SEQ			= '"+saveData.getValue("OPBOOK_SEQ")+"', "+	//手术单号 add lij 20170516		
			" OPT_TERM              = '"+saveData.getValue("OPT_TERM")+"', "+
			" OPT_USER              = '"+saveData.getValue("OPT_USER")+"', "+
			" OPT_DATE              = SYSDATE "+ 
			" WHERE TRANSFER_CODE     = '"+saveData.getValue("TRANSFER_CODE")+"' ";
//		System.out.println("update_sql:"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return false;
		}
		return true;
	}

	/**
	 * 清空
	 */
	public void onClear(){
		
	}
	
	/**
	 * 空值检查
	 */
	public boolean onIsNull() {
	//单据信息	
		if(getValueString("FROM_DEPT").equals("")){
			this.messageBox("转出科室不能为空");
			return false;
		}
	//交接查核表
//		System.out.println("SKIN_PREPARATION_FLG"+getValueString("SKIN_PREPARATION_FLG"));
		if(getValueString("SKIN_PREPARATION_FLG").equals("N")){
			this.messageBox("请做皮肤准备");
			return false;
		}
		if(getValueString("CROSSMATCH_FLG").equals("N")){
			this.messageBox("请做交叉配血");
			return false;
		}
		if(getValueString("SKIN_TEST_FLG").equals("N")){
			this.messageBox("请做皮试");
			return false;
		}
		if(getValueString("BOWEL_PREPARATION_FLG").equals("N")){
			this.messageBox("请做肠道准备");
			return false;
		}
		if(getValueString("PREPARE_EDUCATION_FLG").equals("N")){
			this.messageBox("请做术前宣教");
			return false;
		}
		if(getValueString("DENTAL_CARE_FLG").equals("N")){
			this.messageBox("请做口腔清洁");
			return false;
		}
		if(getValueString("NASAL_CARE_FLG").equals("N")){
			this.messageBox("请做鼻腔清洁");
			return false;
		}
///////////////////////////////////////////////////////////////////		
		if(getValueString("OPE_INFORM_FLG").equals("N")){
			this.messageBox("请检查手术同意书");
			return false;
		}
		if(getValueString("ANA_SINFORM_FLG").equals("N")){
			this.messageBox("请检查麻醉同意书");
			return false;
		}
		if(getValueString("BLOOD_INFORM_FLG").equals("N")){
			this.messageBox("请检查输血同意书");
			return false;
		}
		
		return true;
	}
	private TParm getRecptype() {
		TParm parm = new TParm();
		parm.setData("DEPT_CODE", "0304",0); // 险种
		parm.setData("BED_NO_DESC", "CU404",0); // 医院编码
		parm.setData("PAT_NAME", "王文萍",0); // 收费项目编码
		parm.setData("SEX_CODE", "2",0); // 开始时间
		parm.setData("AGE", "60岁",0); // 备案状态
		parm.setData("CASE_NO", "150929000018",0); //变更/终止时间
		parm.setData("MR_NO", "000000575166",0); // 实际价格
//		parm.setData("TRANSFER_CODE", "1511096");
//		System.out.println("parm:"+parm);
		return parm;
	}
	private TParm onQueryVtsntprdtl(){
		String Sql =
			" SELECT   *  "+
			" FROM   SUM_VTSNTPRDTL A, SUM_VITALSIGN B  "+
			" WHERE  A.CASE_NO = '"+recptype.getValue("CASE_NO")+"' "+
			" AND A.ADM_TYPE = 'I' "+
			" AND A.ADM_TYPE = B.ADM_TYPE "+ 
			" AND A.CASE_NO = B.CASE_NO  "+
			" AND A.EXAMINE_DATE = B.EXAMINE_DATE  "+
			" AND B.DISPOSAL_FLG IS NULL  "+
			" AND A.RECTIME IS NOT NULL "+
			" ORDER BY A.EXAMINE_DATE DESC,A.RECTIME DESC ";
//		System.out.println("queryM==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));
		
		if(tabParm.getCount("CASE_NO")<0){
			this.messageBox("未找到手术申请！");
			return null;
		}
		return tabParm;
	}
}
