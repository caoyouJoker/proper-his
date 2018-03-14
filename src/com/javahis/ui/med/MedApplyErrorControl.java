package com.javahis.ui.med;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title:门诊医生站操作
 * </p>
 *
 * <p>
 * Description:删除医嘱没有写入 MED_APPLY表问题临时解决
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
 * @author pangben
 * @version 4.0
 */
public class MedApplyErrorControl extends TControl {
	/**
	 * 初始化界面
	 */
	public void onInit() {
		super.onInit();
	}
	public void onExe(){
		if (!this.emptyTextCheck("CASE_NO,RX_NO,SEQ_NO,BILL_FLG")) {
			return ;
		}
		String status="";
		if(this.getValue("BILL_FLG").equals("Y")){
			status="1";
		}else{
			status="0";
		}
		String sql="SELECT O.CASE_NO, O.RX_NO, O.SEQ_NO,"+ 
				"O.PRESRT_NO, O.REGION_CODE, O.MR_NO, "+
				"O.ADM_TYPE, O.RX_TYPE, O.TEMPORARY_FLG, "+
				"O.RELEASE_FLG, O.LINKMAIN_FLG, O.LINK_NO, "+
				"O.ORDER_CODE, O.ORDER_DESC, O.GOODS_DESC, "+
				"O.SPECIFICATION, O.ORDER_CAT1_CODE, O.MEDI_QTY, "+
				"O.MEDI_UNIT, O.FREQ_CODE, O.ROUTE_CODE, "+
				"O.TAKE_DAYS, O.DOSAGE_QTY, O.DOSAGE_UNIT, "+
				"O.DISPENSE_QTY, O.DISPENSE_UNIT, O.GIVEBOX_FLG, "+
				"O.OWN_PRICE, O.NHI_PRICE, O.DISCOUNT_RATE, "+
				"O.OWN_AMT, O.AR_AMT, O.DR_NOTE, "+
				"O.NS_NOTE, O.DR_CODE, O.ORDER_DATE, "+
				"O.DEPT_CODE, O.DC_DR_CODE, O.DC_ORDER_DATE, "+
				"O.DC_DEPT_CODE, O.EXEC_DEPT_CODE, O.EXEC_DR_CODE, "+
				"O.SETMAIN_FLG, O.ORDERSET_GROUP_NO, O.ORDERSET_CODE, "+
				"O.HIDE_FLG, O.RPTTYPE_CODE, O.OPTITEM_CODE, "+
				"O.DEV_CODE, O.MR_CODE, O.FILE_NO, "+
				"O.DEGREE_CODE, O.URGENT_FLG, O.INSPAY_TYPE, "+
				"O.PHA_TYPE, O.DOSE_TYPE, O.EXPENSIVE_FLG, "+
				"O.PRINTTYPEFLG_INFANT, O.CTRLDRUGCLASS_CODE, O.PRESCRIPT_NO, "+
				"O.ATC_FLG, O.SENDATC_DATE, O.RECEIPT_NO, "+
				"O.BILL_FLG, O.BILL_DATE, O.BILL_USER, "+
				"O.PRINT_FLG, O.REXP_CODE, O.HEXP_CODE, "+
				"O.CONTRACT_CODE, O.CTZ1_CODE, O.CTZ2_CODE, "+
				"O.CTZ3_CODE, O.PHA_CHECK_CODE, O.PHA_CHECK_DATE, "+
				"O.PHA_DOSAGE_CODE, O.PHA_DOSAGE_DATE, O.PHA_DISPENSE_CODE, "+
				"O.PHA_DISPENSE_DATE, O.PHA_RETN_CODE, O.PHA_RETN_DATE, "+
				"O.NS_EXEC_CODE, O.NS_EXEC_DATE, O.NS_EXEC_DEPT, "+
				"O.DCTAGENT_CODE, O.DCTEXCEP_CODE, O.DCT_TAKE_QTY, "+
				"O.PACKAGE_TOT, O.AGENCY_ORG_CODE, O.DCTAGENT_FLG, "+
				"O.DECOCT_CODE, O.REQUEST_FLG, O.REQUEST_NO, "+
				"O.OPT_USER, O.OPT_DATE, O.OPT_TERM,"+ 
				"O.MED_APPLY_NO, O.CAT1_TYPE, O.TRADE_ENG_DESC, "+
				"O.PRINT_NO, O.COUNTER_NO, O.PSY_FLG, "+
				"O.EXEC_FLG, O.RECEIPT_FLG, O.BILL_TYPE, "+
				"O.FINAL_TYPE, O.DECOCT_REMARK, O.SEND_DCT_USER, "+
				"O.SEND_DCT_DATE, O.DECOCT_USER, O.DECOCT_DATE, "+
				"O.SEND_ORG_USER, O.SEND_ORG_DATE, O.EXM_EXEC_END_DATE, "+
				"O.EXEC_DR_DESC, O.COST_AMT, O.COST_CENTER_CODE, "+
				"O.BATCH_SEQ1, O.VERIFYIN_PRICE1, O.DISPENSE_QTY1, "+
				"O.BATCH_SEQ2, O.VERIFYIN_PRICE2, O.DISPENSE_QTY2, "+
				"O.BATCH_SEQ3, O.VERIFYIN_PRICE3, O.DISPENSE_QTY3,O.BUSINESS_NO,A.RPTTYPE_CODE,A.DEV_CODE "+
				"FROM OPD_ORDER O,SYS_FEE A WHERE O.ORDER_CODE=A.ORDER_CODE AND O.CASE_NO='"+this.getValue("CASE_NO").toString().trim()+
				"' AND O.RX_NO='"+this.getValueString("RX_NO").trim()+"' AND O.SEQ_NO='"+this.getValueInt("SEQ_NO")+"' AND O.SETMAIN_FLG='Y'";
		TParm opdOrderParm = new TParm(TJDODBTool.getInstance().select(sql));
		if(opdOrderParm.getCount()<=0){
			this.messageBox("OPD_ORDER表中没有查询的数据");
			return;
		}
		if(opdOrderParm.getCount()>1){
			this.messageBox("OPD_ORDER表中查询出多条数据,不能执行");
			return;
		}
		sql="SELECT B.PAT_NAME,B.PAT_NAME1,B.BIRTH_DATE,B.SEX_CODE,B.POST_CODE,B.ADDRESS,B.COMPANY_DESC," +
				"B.TEL_HOME,B.IDNO,A.CLINICROOM_NO,A.CLINICAREA_CODE FROM REG_PATADM A,SYS_PATINFO B WHERE A.MR_NO=B.MR_NO AND A.CASE_NO='"+this.getValue("CASE_NO").toString().trim()+"'";
		TParm regParm = new TParm(TJDODBTool.getInstance().select(sql));
		if(regParm.getCount()<=0){
			this.messageBox("REG_PATADM表中没有查询的数据");
			return;
		}
		sql="SELECT CASE_NO FROM MED_APPLY WHERE CAT1_TYPE='"+opdOrderParm.getValue("CAT1_TYPE",0)+"' AND APPLICATION_NO='"+opdOrderParm.getValue("MED_APPLY_NO",0)+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()>0){
			this.messageBox("MED_APPLY 表里存在条码号为"+opdOrderParm.getValue("MED_APPLY_NO",0)+"的数据,不能执行");
			return;
		}
		sql="SELECT ICD_CODE,ICD_TYPE FROM OPD_DIAGREC WHERE ICD_TYPE='W' AND CASE_NO= '"+this.getValue("CASE_NO").toString().trim()+"' AND MAIN_DIAG_FLG='Y'";
		TParm icdParm = new TParm(TJDODBTool.getInstance().select(sql));
		if(this.messageBox("提示","是否执行操作",2)!=0){
			return;
		}
		sql="INSERT INTO MED_APPLY(CAT1_TYPE, APPLICATION_NO, CASE_NO,"+ 
			"IPD_NO, MR_NO, ADM_TYPE,"+ 
			"PAT_NAME, PAT_NAME1, BIRTH_DATE, "+
			"SEX_CODE, POST_CODE, ADDRESS, "+
			"COMPANY, TEL, IDNO, "+
			"DEPT_CODE, REGION_CODE, CLINICROOM_NO,"+ 
			"STATION_CODE, BED_NO, ORDER_NO, "+
			"SEQ_NO, ORDER_CODE, ORDER_DESC, "+
			"ORDER_DR_CODE, ORDER_DATE, ORDER_DEPT_CODE, "+
			"START_DTTM, EXEC_DEPT_CODE, EXEC_DR_CODE, "+
			"OPTITEM_CODE, OPTITEM_CHN_DESC, ORDER_CAT1_CODE, "+
			"DEAL_SYSTEM, RPTTYPE_CODE, DEV_CODE, "+
			"REMARK, ICD_TYPE, ICD_CODE, "+
			"STATUS, NEW_READ_FLG, "+
			"BILL_FLG, OWN_AMT, AR_AMT, "+
			"OPT_USER, OPT_DATE, OPT_TERM, "+
			"PRINT_FLG, CLINICAREA_CODE,"+ 
			"URGENT_FLG,ISREAD,DR_NOTE) VALUES('"
			+opdOrderParm.getValue("CAT1_TYPE",0)+"','"
			+opdOrderParm.getValue("MED_APPLY_NO",0)+"','"
			+opdOrderParm.getValue("CASE_NO",0)+"','','"
			+opdOrderParm.getValue("MR_NO",0)+"','"
			+opdOrderParm.getValue("ADM_TYPE",0)+"','"
			+regParm.getValue("PAT_NAME",0)+"','"
			+regParm.getValue("PAT_NAME1",0)+"',TO_DATE('"
			+SystemTool.getInstance().getDateReplace(regParm.getValue("BIRTH_DATE",0), true)+"','YYYYMMDDHH24MISS'),'"
			+regParm.getValue("SEX_CODE",0)+"','"
			+regParm.getValue("POST_CODE",0)+"','"
			+regParm.getValue("ADDRESS",0)+"','"
			+regParm.getValue("COMPANY_DESC",0)+"','"
			+regParm.getValue("TEL_HOME",0)+"','"
			+regParm.getValue("IDNO",0)+"','"
			+opdOrderParm.getValue("DEPT_CODE",0)+"','"
			+opdOrderParm.getValue("REGION_CODE",0)+"','"
			+regParm.getValue("CLINICROOM_NO",0)+"','','','" 
			+opdOrderParm.getValue("RX_NO",0)+"','"
			+opdOrderParm.getValue("SEQ_NO",0)+"','"
			+opdOrderParm.getValue("ORDER_CODE",0)+"','"
			+opdOrderParm.getValue("ORDER_DESC",0)+"','"
			+opdOrderParm.getValue("DR_CODE",0)+"',TO_DATE('"
			+SystemTool.getInstance().getDateReplace(opdOrderParm.getValue("ORDER_DATE",0), true)+"','YYYYMMDDHH24MISS'),'"
			+opdOrderParm.getValue("DEPT_CODE",0)+"',TO_DATE('"
			+SystemTool.getInstance().getDateReplace(opdOrderParm.getValue("ORDER_DATE",0), true)+"','YYYYMMDDHH24MISS'),'"
			+opdOrderParm.getValue("EXEC_DEPT_CODE",0)+"','"
			+opdOrderParm.getValue("EXEC_DR_CODE",0)+"','','','"
			+opdOrderParm.getValue("ORDER_CAT1_CODE",0)+"','','"
			+opdOrderParm.getValue("RPTTYPE_CODE",0)+"','" 
			+opdOrderParm.getValue("DEV_CODE",0)+"','"
			+opdOrderParm.getValue("DR_NOTE",0)+"','W','"
			+icdParm.getValue("ICD_CODE",0)+"','"+status+"','N','"
			+this.getValueString("BILL_FLG")+"',"
			+opdOrderParm.getDouble("OWN_AMT",0)+","
			+opdOrderParm.getDouble("AR_AMT",0)+",'"
			+Operator.getID()+"',SYSDATE,'"
			+Operator.getIP()+"','"
			+opdOrderParm.getValue("PRINT_FLG",0)+"','"
			+regParm.getValue("CLINICAREA_CODE",0)+"','N','N','" 
			+opdOrderParm.getValue("DR_NOTE",0)+"')";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
			this.messageBox("执行失败");
			return;
		}
		this.messageBox("执行成功");
	}
}
