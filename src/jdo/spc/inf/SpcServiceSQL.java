package jdo.spc.inf;

import com.dongyang.data.TParm;

import jdo.spc.StringUtils;
import jdo.spc.inf.dto.SpcCommonDto;

import jdo.sys.Operator;

import jdo.pha.inf.dto.SpcOpdDiagrecDto;
import jdo.pha.inf.dto.SpcOpdDrugAllergyDto;
import jdo.pha.inf.dto.SpcOpdOrderDto;
import jdo.pha.inf.dto.SpcRegPatadmDto;
import jdo.pha.inf.dto.SpcSysPatinfoDto;

/**
 * <p>
 * Title: SPC共用SQL封装
 * </p>
 *
 * <p>
 * Description: SPC共用SQL封装
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
 * @author fuwj 2012.10.23
 * @version 1.0
 */

public class SpcServiceSQL {
	

    
    /**
     * 保持门诊医生医嘱从HIS到物联网
     * @param obj
     * @return
     */
    public static  String saveOpdOrderFromHisBySpc(SpcOpdOrderDto obj){   	
    	String sql = " INSERT INTO OPD_ORDER " +
    				 "	(CASE_NO, RX_NO, SEQ_NO, PRESRT_NO, REGION_CODE, MR_NO, ADM_TYPE, RX_TYPE, TEMPORARY_FLG, RELEASE_FLG, LINKMAIN_FLG, LINK_NO,  " + //1行
    				 "	ORDER_CODE, ORDER_DESC, GOODS_DESC, SPECIFICATION, ORDER_CAT1_CODE, MEDI_QTY, MEDI_UNIT, FREQ_CODE,ROUTE_CODE, TAKE_DAYS,　　   " + //2行
    				 "	DOSAGE_QTY, DOSAGE_UNIT, DISPENSE_QTY,DISPENSE_UNIT, GIVEBOX_FLG, OWN_PRICE, NHI_PRICE, DISCOUNT_RATE,OWN_AMT, AR_AMT,DR_NOTE," + //3行
    				 "	NS_NOTE, DR_CODE,ORDER_DATE, DEPT_CODE, DC_DR_CODE, DC_ORDER_DATE, DC_DEPT_CODE, EXEC_DEPT_CODE, EXEC_DR_CODE, SETMAIN_FLG,	  " + //4行
   	  				 "	ORDERSET_GROUP_NO, ORDERSET_CODE, HIDE_FLG, RPTTYPE_CODE, OPTITEM_CODE, DEV_CODE, MR_CODE, FILE_NO, DEGREE_CODE, URGENT_FLG,  " + //5行
   	  				 "  INSPAY_TYPE, PHA_TYPE, DOSE_TYPE, EXPENSIVE_FLG, PRINTTYPEFLG_INFANT, CTRLDRUGCLASS_CODE, PRESCRIPT_NO, ATC_FLG, SENDATC_DATE," + //6行
   	  				 " 	RECEIPT_NO, BILL_FLG, BILL_DATE, BILL_USER, PRINT_FLG, REXP_CODE, HEXP_CODE, CONTRACT_CODE, CTZ1_CODE, CTZ2_CODE, CTZ3_CODE,  " + //7行
   	  				 "	PHA_CHECK_CODE, PHA_CHECK_DATE, PHA_DOSAGE_CODE, PHA_DOSAGE_DATE, PHA_DISPENSE_CODE, PHA_DISPENSE_DATE, PHA_RETN_CODE,		  " + //8行
   	  				 " 	PHA_RETN_DATE, NS_EXEC_CODE, NS_EXEC_DATE,NS_EXEC_DEPT,DCTAGENT_CODE,DCTEXCEP_CODE, DCT_TAKE_QTY, PACKAGE_TOT,AGENCY_ORG_CODE," + //9行
   	  				 "	DCTAGENT_FLG,DECOCT_CODE, REQUEST_FLG, REQUEST_NO, OPT_USER, OPT_DATE, OPT_TERM, MED_APPLY_NO, CAT1_TYPE, TRADE_ENG_DESC, 	  " + //10行
   	  				 "	PRINT_NO,COUNTER_NO, PSY_FLG, EXEC_FLG, RECEIPT_FLG, BILL_TYPE, FINAL_TYPE, DECOCT_REMARK, SEND_DCT_USER, SEND_DCT_DATE,	  " + //11行
   	  				 "	DECOCT_USER,DECOCT_DATE, SEND_ORG_USER, SEND_ORG_DATE, EXM_EXEC_END_DATE, EXEC_DR_DESC,COST_AMT, COST_CENTER_CODE, BATCH_SEQ1, " + //12行
   	  				 " 	VERIFYIN_PRICE1, DISPENSE_QTY1,BATCH_SEQ2, VERIFYIN_PRICE2, DISPENSE_QTY2, BATCH_SEQ3, VERIFYIN_PRICE3,DISPENSE_QTY3, " + 		  //13行
    				 "  BUSINESS_NO,PAT_NAME,BIRTH_DATE,SEX_TYPE " +
    				 " ) VALUES " 	  +
    				 " ( " 			  +
    				 " '" +  obj.getCaseNo() +"', '" + obj.getRxNo() +"'," + obj.getSeqNo()+ ", " + obj.getPresrtNo() +", '" + obj.getRegionCode() +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getMrNo()) +"', '" +  StringUtils.trimToEmpty(obj.getAdmType()) +"', '" +  StringUtils.trimToEmpty(obj.getRxType()) +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getTemporaryFlg()) +"', '" +  StringUtils.trimToEmpty(obj.getReleaseFlg()) +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getLinkmainFlg()) +"', '" +  StringUtils.trimToEmpty(obj.getLinkNo()) +"'," + //1行
    				 " '" +  StringUtils.trimToEmpty(obj.getOrderCode())+ "', '" +  StringUtils.trimToEmpty(obj.getOrderDesc())+ "', " +
    				 " '" +  StringUtils.trimToEmpty(obj.getGoodsDesc()) +"','" +  StringUtils.trimToEmpty(obj.getSpecification())+"', " +
    				 " '" +  StringUtils.trimToEmpty(obj.getOrderCat1Code()) +"', " + obj.getMediQty() + ", '" +  StringUtils.trimToEmpty(obj.getMediUnit()) + "'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getFreqCode()) + "', '" +  StringUtils.trimToEmpty(obj.getRouteCode()) + "', " + obj.getTakeDays() + "," + //2行
    			     "  " +  obj.getDosageQty() +", '" +  StringUtils.trimToEmpty(obj.getDosageUnit()) +"'," + obj.getDispenseQty()+ ",'" +  StringUtils.trimToEmpty(obj.getDispenseUnit()) + "', " +
    			     " '" +  StringUtils.trimToEmpty(obj.getGiveboxFlg())+ "', " + obj.getOwnPrice() +", " + obj.getNhiPrice() +", " + obj.getDiscountRate() +"," +
    			     "  " +  obj.getOwnAmt() +", " + obj.getArAmt() +", '" +  StringUtils.trimToEmpty(obj.getDrNote()) +"', " + //3行
    			     " '" +  StringUtils.trimToEmpty(obj.getNsNote()) +"', '" +  StringUtils.trimToEmpty(obj.getDrCode()) +"', " + StringUtils.trimToDatTimeSql(obj.getOrderDate()) +", " +
    				 " '" +  StringUtils.trimToEmpty(obj.getDeptCode()) +"', '" +  StringUtils.trimToEmpty(obj.getDcDrCode()) +"', " + StringUtils.trimToDatTimeSql(obj.getDcOrderDate()) + "," +
    				 " '" +  StringUtils.trimToEmpty(obj.getDcDeptCode()) +"','" +  StringUtils.trimToEmpty(obj.getExecDeptCode()) + "'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getExecDrCode()) +"', '" + StringUtils.trimToEmpty(obj.getSetmainFlg()) +"', " + //4行
    				 "  " +  obj.getOrdersetGroupNo() +", '" +  StringUtils.trimToEmpty(obj.getOrdersetCode()) +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getHideFlg()) +"', '" +  StringUtils.trimToEmpty(obj.getRpttypeCode()) +"', " +
    				 " '" +  StringUtils.trimToEmpty(obj.getOptitemCode()) +"', '" +  StringUtils.trimToEmpty(obj.getDevCode()) +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getMrCode()) +"'," + obj.getFileNo() +", " +
    				 " '" +  StringUtils.trimToEmpty(obj.getDegreeCode()) +"', '" + StringUtils.trimToEmpty(obj.getUrgentFlg()) +"', " + //5行
    				 " '" +  StringUtils.trimToEmpty(obj.getInspayType()) + "', '" + StringUtils.trimToEmpty(obj.getPhaType()) +"','" + StringUtils.trimToEmpty(obj.getDoseType()) +"', " +
    				 " '" +  StringUtils.trimToEmpty(obj.getExpensiveFlg()) +"', '" + StringUtils.trimToEmpty(obj.getPrinttypeflgInfant()) +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getCtrldrugclassCode()) +"'," + obj.getPrescriptNo() +"," +
    				 " '" +  StringUtils.trimToEmpty(obj.getAtcFlg()) +"'," +StringUtils.trimToDatTimeSql(obj.getSendatcDate())+ "," + //6行
    				 " '" +  StringUtils.trimToEmpty(obj.getReceiptNo()) +"', '" + StringUtils.trimToEmpty(obj.getBillFlg()) +"'," +
    				 "  " +  StringUtils.trimToDatTimeSql(obj.getBillDate())+ ",'" +  StringUtils.trimToEmpty(obj.getBillUser()) +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getPrintFlg()) +"', '" +  StringUtils.trimToEmpty(obj.getRexpCode()) +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getHexpCode()) +"', '" +  StringUtils.trimToEmpty(obj.getContractCode()) +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getCtz1Code()) +"', '" +  StringUtils.trimToEmpty(obj.getCtz2Code()) +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getCtz3Code()) +"'," +  //7行
    				 " '" +  StringUtils.trimToEmpty(obj.getPhaCheckCode()) +"', " + StringUtils.trimToDatTimeSql(obj.getPhaCheckDate())+", " +
    				 " '" +  StringUtils.trimToEmpty(obj.getPhaDosageCode()) +"', " + StringUtils.trimToDatTimeSql(obj.getPhaDosageDate())+", " +
    				 " '" +  StringUtils.trimToEmpty(obj.getPhaDispenseCode()) +"'," + StringUtils.trimToDatTimeSql(obj.getPhaDispenseDate())+"," +
    				 " '" +  StringUtils.trimToEmpty(obj.getPhaRetnCode()) +"'," +  //8行
    				 "  " +  StringUtils.trimToDatTimeSql(obj.getPhaRetnDate())+", '" +  StringUtils.trimToEmpty(obj.getNsExecCode()) +"'," +
    				 "  " +  StringUtils.trimToDatTimeSql(obj.getNsExecDate())+", '" +  StringUtils.trimToEmpty(obj.getNsExecDept()) +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getDctagentCode()) +"', '" +  StringUtils.trimToEmpty(obj.getDctexcepCode()) +"' ," +
    				 "  " +  obj.getDctTakeQty() +" ," + obj.getPackageTot() +",'" +  StringUtils.trimToEmpty(obj.getAgencyOrgCode()) +"', " +  //9行
    				 " '" +  StringUtils.trimToEmpty(obj.getDctagentFlg()) +"', '" +  StringUtils.trimToEmpty(obj.getDecoctCode()) +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getRequestFlg()) +"', '" +  StringUtils.trimToEmpty(obj.getRequestNo()) +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getOptUser()) + "',sysdate, '" +  StringUtils.trimToEmpty(obj.getOptTerm()) + "'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getMedApplyNo()) +"','" +  StringUtils.trimToEmpty(obj.getCat1Type()) +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getTradeEngDesc()) +"', " + //10行
    				 " '" +  StringUtils.trimToEmpty(obj.getPrintNo()) +"'," + obj.getCounterNo() +", '" + StringUtils.trimToEmpty(obj.getPsyFlg()) +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getExecFlg()) +"', '" + StringUtils.trimToEmpty(obj.getReceiptFlg()) +"', " +
    				 " '" +  StringUtils.trimToEmpty(obj.getBillType()) +"','" + StringUtils.trimToEmpty(obj.getFinalType()) +"'," +
    				 " '" +  StringUtils.trimToEmpty(obj.getDecoctRemark()) +"', '" +  StringUtils.trimToEmpty(obj.getSendDctUser()) +"'," +
    				 "  " +  StringUtils.trimToDatTimeSql(obj.getSendDctDate())+", " +   //11行SEND_ORG_DATE, EXM_EXEC_END_DATE
    				 " '" +  StringUtils.trimToEmpty(obj.getDecoctUser()) +"'," + StringUtils.trimToDatTimeSql(obj.getDecoctDate())+"," +
    				 " '" +  StringUtils.trimToEmpty(obj.getSendOrgUser()) +"'," + StringUtils.trimToDatTimeSql(obj.getSendOrgDate()) + "," +
    				 "  " +  StringUtils.trimToDatTimeSql(obj.getExmExecEndDate())+",'" +  StringUtils.trimToEmpty(obj.getExecDrDesc()) +"'," +
    				 "  " +  obj.getCostAmt() +", '" +  StringUtils.trimToEmpty(obj.getCostCenterCode()) +"'," + obj.getBatchSeq1() +", " +      //12行
    				 "  " + obj.getVerifyinPrice1() +"," + obj.getDispenseQty1() +"," + obj.getBatchSeq2() +"," + obj.getVerifyinPrice2() +"," +
    				 "  " + obj.getDispenseQty2() +"," + obj.getBatchSeq3() +"," + obj.getVerifyinPrice3() +"," + obj.getDispenseQty3() +", " +
    				 " '" +  StringUtils.trimToEmpty(obj.getBusinessNo()) +"','" +  StringUtils.trimToEmpty(obj.getPatName()) +"'," +
    				 "  " + StringUtils.trimToDatTimeSql(obj.getBirthDate())+ ",'" + StringUtils.trimToEmpty(obj.getSexType()) +"' " +
    				 " )";
    	return sql;
    }
    
    /**
     * SPC和his药品编码对照查询
     * @param spcOrderCode
     * @param hisRegionCode
     * @param hisOrderCode 
     * @return
     */
    public static String getSpcOrderCodeByHisOrderCode(String spcOrderCode,String hisRegionCode,String hisOrderCode){
    	String sql = " SELECT  REGION_CODE, ORDER_CODE, HIS_ORDER_CODE, ORDER_DESC, SPECIFICATION, " +
    				 " GOODS_DESC, HIS_ORDER_DESC, HIS_SPECIFICATION, HIS_GOODS_DESC " +
    				 " FROM SYS_FEE_SPC WHERE 1=1 " ;
    	if(StringUtils.isNotBlank(hisRegionCode)){
    		 sql += " AND REGION_CODE='" + hisRegionCode + "' ";
    	}
    	if(StringUtils.isNotBlank(spcOrderCode)){
   		 	sql += " AND ORDER_CODE='" + spcOrderCode + "' ";
    	}
    	if(StringUtils.isNotBlank(hisOrderCode)){
   		 	sql += " AND HIS_ORDER_CODE='" + hisOrderCode + "' ";
    	}    	
    	return sql;
    }
    /**
     * 保存病患-过敏信息
     * @param obj
     * @return
     */
    public static String saveOpdDrugallergy(SpcOpdDrugAllergyDto obj){
    	String sql = " INSERT INTO OPD_DRUGALLERGY (MR_NO, ADM_DATE, DRUG_TYPE, DRUGORINGRD_CODE,  " +
    				 " ADM_TYPE, CASE_NO, DEPT_CODE, DR_CODE, ALLERGY_NOTE, OPT_USER,    OPT_DATE, OPT_TERM ) 		" +
    				 " VALUES " +
    				 " ('" + obj.getMrNo() + "', '" + obj.getAdmDate() +"', '" + obj.getDrugType() + "', '" + obj.getDrugoringrdCode() + "'," +
    				 " '','', " + 
    				 " '',''," +
    				 " '" +  StringUtils.trimToEmpty(obj.getAllergyNote()) + "', 'ID', " + 
    				 " sysdate, 'IP') ";
    	return sql;
    }
    
    /**
     * 根据主键-删除病患过敏信息
     * @param obj
     * @return
     */
    public static String deleteOpdDrugallergyById(SpcOpdDrugAllergyDto obj){
    	String sql = " DELETE FROM OPD_DRUGALLERGY WHERE MR_NO='" + obj.getMrNo() + "' AND ADM_DATE='" + obj.getAdmDate() + "' " +
    				 " AND DRUG_TYPE='" + obj.getDrugType() + "' AND DRUGORINGRD_CODE='" + obj.getDrugoringrdCode() + "' ";
    	return sql;
    }
    
    /**
     * 保存-门急诊诊断档-OPD_DIAGREC
     * @param obj
     * @return
     */
    public static String saveOpdDiagrec(SpcOpdDiagrecDto obj){
    	String sql = " INSERT INTO OPD_DIAGREC  (CASE_NO, ICD_TYPE, ICD_CODE, MAIN_DIAG_FLG, ADM_TYPE, " + 
    				 " DIAG_NOTE, DR_CODE, ORDER_DATE, FILE_NO, OPT_USER, OPT_DATE, OPT_TERM ) 		   " +
    				 " VALUES " +
    				 " ('" + obj.getCaseNo()+ "', '" + obj.getIcdType() + "', '" + obj.getIcdCode() + "','N', ''," + 
    				 " '" +  StringUtils.trimToEmpty(obj.getDiagNote()) + "','','',''," + 
    				 " 'ID',sysdate, 'IP')　";
    	return sql ;
    }
    
    /**
     * 删除-门急诊诊断档-OPD_DIAGREC
     * @param obj
     * @return
     */
    public static String deleteOpdDiagrec(SpcOpdDiagrecDto obj){
    	String sql = " DELETE OPD_DIAGREC WHERE CASE_NO='" + obj.getCaseNo()+ "' AND ICD_TYPE='" + obj.getIcdType() + "'" +
    				 " AND ICD_CODE='" + obj.getIcdCode() + "'　";
    	return sql ;
    }
    
    /**
     * 保存-病患基本信息-SYS_PATIONINFO
     * @param obj
     * @return
     */
    public static String saveSysPationInfo(SpcSysPatinfoDto obj){
    	String sql = " INSERT INTO SYS_PATINFO  (MR_NO, PAT_NAME, BIRTH_DATE, SEX_CODE, OPT_USER, OPT_DATE, OPT_TERM  )  " +
    				 " VALUES " +
    				 " ('" + obj.getMrNo()+ "', '" + obj.getPatName() + "'," + StringUtils.trimToDatTimeSql(obj.getBirthDate())+ ", " + 
    				 " '" + obj.getSexCode() + "', 'ID',sysdate, 'IP')　";
    	return sql ;
    }
    
    /**
     * 删除-病患基本信息-SYS_PATIONINFO
     * @param obj
     * @return
     */
    public static String deleteSysPationInfo(SpcSysPatinfoDto obj){
    	String sql = " DELETE SYS_PATINFO WHERE MR_NO='" + obj.getMrNo() + "'　";
    	return sql ;
    }   
    
    /**
     * 删除-病患医嘱信息-OPD_ORDER
     * @param obj
     * @return
     */
    public static String deleteOPDOrder(SpcOpdOrderDto obj){
    	String sql = " DELETE OPD_ORDER WHERE CASE_NO='" + obj.getCaseNo() + "'　AND RX_NO='" + obj.getRxNo() + "' ";
    	return sql ;
    }  
    
    /**
     * 保存-挂号主档-REG_PATADM
     * @param obj
     * @return
     */
    public static String saveRegPatadm(SpcRegPatadmDto obj){
    	String sql = " INSERT INTO REG_PATADM (CASE_NO, MR_NO, REALDEPT_CODE, REALDR_CODE, WEIGHT,HEIGHT, OPT_USER, OPT_DATE, OPT_TERM )  " +
    				 " VALUES " +
    				 " ('" + obj.getCaseNo()+ "', '" + obj.getMrNo() + "','" + obj.getRealdeptCode()+ "','" + obj.getRealdrCode() + "'," +
    				 " " + obj.getWeight() + "," + obj.getHeight() + ",'ID',sysdate, 'IP')　";
    	return sql ;
    }
    
    /**
     * 删除-挂号主档-REG_PATADM
     * @param obj
     * @return
     */
    public static String deleteRegPatadm(SpcRegPatadmDto obj){
    	String sql = " DELETE REG_PATADM WHERE CASE_NO='" + obj.getCaseNo() + "'　";
    	return sql ;
    }       
    
    /**
     * 删除-挂号主档-REG_PATADM
     * @param obj
     * @return
     */
    public static String queryIndMMStock(TParm parm){
    	String sql = "  SELECT　" +
			    	"   ROWID, TRANDATE, ORG_CODE, ORDER_CODE,            " +
			    	"   BATCH_SEQ, BATCH_NO, VALID_DATE,                  " +
			    	"   REGION_CODE, STOCK_QTY, STOCK_AMT,                " +
			    	"   LAST_TOTSTOCK_QTY, LAST_TOTSTOCK_AMT, IN_QTY,     " +
			    	"   IN_AMT, OUT_QTY, OUT_AMT,                         " +
			    	"   CHECKMODI_QTY, CHECKMODI_AMT, VERIFYIN_QTY,       " +
			    	"   VERIFYIN_AMT, FAVOR_QTY, REGRESSGOODS_QTY,        " +
			    	"   REGRESSGOODS_AMT, DOSAGE_QTY, DOSAGE_AMT,         " +
			    	"   REGRESSDRUG_QTY, REGRESSDRUG_AMT, PROFIT_LOSS_AMT," +
			    	"   VERIFYIN_PRICE, STOCK_PRICE, RETAIL_PRICE,        " +
			    	"   TRADE_PRICE, STOCKIN_QTY, STOCKIN_AMT,            " +
			    	"   STOCKOUT_QTY, STOCKOUT_AMT, OPT_USER,             " +
			    	"   OPT_DATE, OPT_TERM, REQUEST_IN_QTY,               " +
			    	"   REQUEST_IN_AMT, REQUEST_OUT_QTY, REQUEST_OUT_AMT, " +
			    	"   GIF_IN_QTY, GIF_IN_AMT, GIF_OUT_QTY,              " +
			    	"   GIF_OUT_AMT, RET_IN_QTY, RET_IN_AMT,              " +
			    	"   RET_OUT_QTY, RET_OUT_AMT, WAS_OUT_QTY,            " +
			    	"   WAS_OUT_AMT, THO_OUT_QTY, THO_OUT_AMT,            " +
			    	"   THI_IN_QTY, THI_IN_AMT, COS_OUT_QTY,              " +
			    	"   COS_OUT_AMT, SUP_CODE                             " +
			    	"   FROM IND_MMSTOCK WHERE ";
    	return sql ;
    }         
    
   
}

