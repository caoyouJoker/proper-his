package jdo.opd.ws;

import java.io.Serializable;

/**
*
* <p>
* Title: 医嘱类
* </p>
*
* <p>
* Description:医生站医嘱调用webService 添加物联网数据
* </p>
*
* <p>
* Copyright: Copyright (c) 
* </p>
*
* <p>
* Company: ProperSoft
* </p>
*
* @author pangben 2013-5-20
* @version 1.0
*/
public class OpdOrder implements Serializable{
	private String CASE_NO;
	private String RX_NO;
	private int SEQ_NO;
	private String PRESRT_NO;
	private String REGION_CODE;
	private String MR_NO;
	private String ADM_TYPE;
	private String RX_TYPE;
	private String TEMPORARY_FLG;
	private String RELEASE_FLG;
	private String LINKMAIN_FLG;
	private String LINK_NO;
	private String ORDER_CODE;
	private String ORDER_DESC;
	private String GOODS_DESC;
	private String SPECIFICATION;
	private String ORDER_CAT1_CODE;
	private double MEDI_QTY;
	private String MEDI_UNIT;
	private String FREQ_CODE;
	private String ROUTE_CODE;
	private int TAKE_DAYS;
	private double DOSAGE_QTY;
	private String DOSAGE_UNIT;
	private double DISPENSE_QTY;
	private String DISPENSE_UNIT;
	private String GIVEBOX_FLG;
	private double OWN_PRICE;
	private double NHI_PRICE;
	private double DISCOUNT_RATE;
	private double OWN_AMT;
	private double AR_AMT;
	private String DR_NOTE;
	private String NS_NOTE;
	private String DR_CODE;
	private String ORDER_DATE;
	private String DEPT_CODE;
	private String DC_DR_CODE;
	private String DC_ORDER_DATE;
	private String DC_DEPT_CODE;
	private String EXEC_DEPT_CODE;
	private String EXEC_DR_CODE;
	private String SETMAIN_FLG;
	private double ORDERSET_GROUP_NO;
	private String ORDERSET_CODE;
	private String HIDE_FLG;
	private String RPTTYPE_CODE;
	private String OPTITEM_CODE;
	private String DEV_CODE;
	private String MR_CODE;
	private double FILE_NO;
	private String DEGREE_CODE;
	private String URGENT_FLG;
	private String INSPAY_TYPE;
	private String PHA_TYPE;
	private String DOSE_TYPE;
	private String EXPENSIVE_FLG;
	private String PRINTTYPEFLG_INFANT;
	private String CTRLDRUGCLASS_CODE;
	private double PRESCRIPT_NO;
	private String ATC_FLG;
	private String SENDATC_DATE;
	private String RECEIPT_NO;
	private String BILL_FLG;
	private String BILL_DATE;
	private String BILL_USER;
	private String PRINT_FLG;
	private String REXP_CODE;
	private String HEXP_CODE;
	private String CONTRACT_CODE;
	private String CTZ1_CODE;
	private String CTZ2_CODE;
	private String CTZ3_CODE;
	private String PHA_CHECK_CODE;
	private String PHA_CHECK_DATE;
	private String PHA_DOSAGE_CODE;
	private String PHA_DOSAGE_DATE;
	private String PHA_DISPENSE_CODE;
	private String PHA_DISPENSE_DATE;
	private String PHA_RETN_CODE;
	private String PHA_RETN_DATE;
	private String NS_EXEC_CODE;
	private String NS_EXEC_DATE;
	private String NS_EXEC_DEPT;
	private String DCTAGENT_CODE;
	private String DCTEXCEP_CODE;
	private double DCT_TAKE_QTY;
	private double PACKAGE_TOT;
	private String AGENCY_ORG_CODE;
	private String DCTAGENT_FLG;
	private String DECOCT_CODE;
	private String REQUEST_FLG;
	private String REQUEST_NO;
	private String OPT_USER;
	private String OPT_DATE;
	private String OPT_TERM;
	private String MED_APPLY_NO;
	private String CAT1_TYPE;
	private String TRADE_ENG_DESC;
	private String PRINT_NO;
	private double COUNTER_NO;
	private String PSY_FLG;
	private String EXEC_FLG;
	private String RECEIPT_FLG;
	private String BILL_TYPE;
	private String FINAL_TYPE;
	private String DECOCT_REMARK;
	private String SEND_DCT_USER;
	private String SEND_DCT_DATE;
	private String DECOCT_USER;
	private String DECOCT_DATE;
	private String SEND_ORG_USER;
	private String SEND_ORG_DATE;
	private String EXM_EXEC_END_DATE;
	private String EXEC_DR_DESC;
	private double COST_AMT;
	private String COST_CENTER_CODE;
	private double BATCH_SEQ1;
	private double VERIFYIN_PRICE1;
	private double DISPENSE_QTY1;
	private double BATCH_SEQ2;
	private double VERIFYIN_PRICE2;
	private double DISPENSE_QTY2;
	private double BATCH_SEQ3;
	private double VERIFYIN_PRICE3;
	private double DISPENSE_QTY3;
	private String BUSINESS_NO;
	private String PAT_NAME;
	private String SEX_TYPE;
	private String BIRTH_DATE;
	public String getCASE_NO() {
		return CASE_NO;
	}
	public void setCASE_NO(String cASENO) {
		CASE_NO = cASENO;
	}
	public String getRX_NO() {
		return RX_NO;
	}
	public void setRX_NO(String rXNO) {
		RX_NO = rXNO;
	}
	public int getSEQ_NO() {
		return SEQ_NO;
	}
	public void setSEQ_NO(int sEQNO) {
		SEQ_NO = sEQNO;
	}
	public String getPRESRT_NO() {
		return PRESRT_NO;
	}
	public void setPRESRT_NO(String pRESRTNO) {
		PRESRT_NO = pRESRTNO;
	}
	public String getREGION_CODE() {
		return REGION_CODE;
	}
	public void setREGION_CODE(String rEGIONCODE) {
		REGION_CODE = rEGIONCODE;
	}
	public String getMR_NO() {
		return MR_NO;
	}
	public void setMR_NO(String mRNO) {
		MR_NO = mRNO;
	}
	public String getADM_TYPE() {
		return ADM_TYPE;
	}
	public void setADM_TYPE(String aDMTYPE) {
		ADM_TYPE = aDMTYPE;
	}
	public String getRX_TYPE() {
		return RX_TYPE;
	}
	public void setRX_TYPE(String rXTYPE) {
		RX_TYPE = rXTYPE;
	}
	public String getTEMPORARY_FLG() {
		return TEMPORARY_FLG;
	}
	public void setTEMPORARY_FLG(String tEMPORARYFLG) {
		TEMPORARY_FLG = tEMPORARYFLG;
	}
	public String getRELEASE_FLG() {
		return RELEASE_FLG;
	}
	public void setRELEASE_FLG(String rELEASEFLG) {
		RELEASE_FLG = rELEASEFLG;
	}
	public String getLINKMAIN_FLG() {
		return LINKMAIN_FLG;
	}
	public void setLINKMAIN_FLG(String lINKMAINFLG) {
		LINKMAIN_FLG = lINKMAINFLG;
	}
	public String getLINK_NO() {
		return LINK_NO;
	}
	public void setLINK_NO(String lINKNO) {
		LINK_NO = lINKNO;
	}
	public String getORDER_CODE() {
		return ORDER_CODE;
	}
	public void setORDER_CODE(String oRDERCODE) {
		ORDER_CODE = oRDERCODE;
	}
	public String getORDER_DESC() {
		return ORDER_DESC;
	}
	public void setORDER_DESC(String oRDERDESC) {
		ORDER_DESC = oRDERDESC;
	}
	public String getGOODS_DESC() {
		return GOODS_DESC;
	}
	public void setGOODS_DESC(String gOODSDESC) {
		GOODS_DESC = gOODSDESC;
	}
	public String getSPECIFICATION() {
		return SPECIFICATION;
	}
	public void setSPECIFICATION(String sPECIFICATION) {
		SPECIFICATION = sPECIFICATION;
	}
	public String getORDER_CAT1_CODE() {
		return ORDER_CAT1_CODE;
	}
	public void setORDER_CAT1_CODE(String oRDERCAT1CODE) {
		ORDER_CAT1_CODE = oRDERCAT1CODE;
	}
	public double getMEDI_QTY() {
		return MEDI_QTY;
	}
	public void setMEDI_QTY(double mEDIQTY) {
		MEDI_QTY = mEDIQTY;
	}
	public String getMEDI_UNIT() {
		return MEDI_UNIT;
	}
	public void setMEDI_UNIT(String mEDIUNIT) {
		MEDI_UNIT = mEDIUNIT;
	}
	public String getFREQ_CODE() {
		return FREQ_CODE;
	}
	public void setFREQ_CODE(String fREQCODE) {
		FREQ_CODE = fREQCODE;
	}
	public String getROUTE_CODE() {
		return ROUTE_CODE;
	}
	public void setROUTE_CODE(String rOUTECODE) {
		ROUTE_CODE = rOUTECODE;
	}
	public int getTAKE_DAYS() {
		return TAKE_DAYS;
	}
	public void setTAKE_DAYS(int tAKEDAYS) {
		TAKE_DAYS = tAKEDAYS;
	}
	public double getDOSAGE_QTY() {
		return DOSAGE_QTY;
	}
	public void setDOSAGE_QTY(double dOSAGEQTY) {
		DOSAGE_QTY = dOSAGEQTY;
	}
	public String getDOSAGE_UNIT() {
		return DOSAGE_UNIT;
	}
	public void setDOSAGE_UNIT(String dOSAGEUNIT) {
		DOSAGE_UNIT = dOSAGEUNIT;
	}
	public double getDISPENSE_QTY() {
		return DISPENSE_QTY;
	}
	public void setDISPENSE_QTY(double dISPENSEQTY) {
		DISPENSE_QTY = dISPENSEQTY;
	}
	public String getDISPENSE_UNIT() {
		return DISPENSE_UNIT;
	}
	public void setDISPENSE_UNIT(String dISPENSEUNIT) {
		DISPENSE_UNIT = dISPENSEUNIT;
	}
	public String getGIVEBOX_FLG() {
		return GIVEBOX_FLG;
	}
	public void setGIVEBOX_FLG(String gIVEBOXFLG) {
		GIVEBOX_FLG = gIVEBOXFLG;
	}
	public double getOWN_PRICE() {
		return OWN_PRICE;
	}
	public void setOWN_PRICE(double oWNPRICE) {
		OWN_PRICE = oWNPRICE;
	}
	public double getNHI_PRICE() {
		return NHI_PRICE;
	}
	public void setNHI_PRICE(double nHIPRICE) {
		NHI_PRICE = nHIPRICE;
	}
	public double getDISCOUNT_RATE() {
		return DISCOUNT_RATE;
	}
	public void setDISCOUNT_RATE(double dISCOUNTRATE) {
		DISCOUNT_RATE = dISCOUNTRATE;
	}
	public double getOWN_AMT() {
		return OWN_AMT;
	}
	public void setOWN_AMT(double oWNAMT) {
		OWN_AMT = oWNAMT;
	}
	public double getAR_AMT() {
		return AR_AMT;
	}
	public void setAR_AMT(double aRAMT) {
		AR_AMT = aRAMT;
	}
	public String getDR_NOTE() {
		return DR_NOTE;
	}
	public void setDR_NOTE(String dRNOTE) {
		DR_NOTE = dRNOTE;
	}
	public String getNS_NOTE() {
		return NS_NOTE;
	}
	public void setNS_NOTE(String nSNOTE) {
		NS_NOTE = nSNOTE;
	}
	public String getDR_CODE() {
		return DR_CODE;
	}
	public void setDR_CODE(String dRCODE) {
		DR_CODE = dRCODE;
	}
	public String getORDER_DATE() {
		return ORDER_DATE;
	}
	public void setORDER_DATE(String oRDERString) {
		ORDER_DATE = oRDERString;
	}
	public String getDEPT_CODE() {
		return DEPT_CODE;
	}
	public void setDEPT_CODE(String dEPTCODE) {
		DEPT_CODE = dEPTCODE;
	}
	public String getDC_DR_CODE() {
		return DC_DR_CODE;
	}
	public void setDC_DR_CODE(String dCDRCODE) {
		DC_DR_CODE = dCDRCODE;
	}
	public String getDC_ORDER_DATE() {
		return DC_ORDER_DATE;
	}
	public void setDC_ORDER_DATE(String dCORDERString) {
		DC_ORDER_DATE = dCORDERString;
	}
	public String getDC_DEPT_CODE() {
		return DC_DEPT_CODE;
	}
	public void setDC_DEPT_CODE(String dCDEPTCODE) {
		DC_DEPT_CODE = dCDEPTCODE;
	}
	public String getEXEC_DEPT_CODE() {
		return EXEC_DEPT_CODE;
	}
	public void setEXEC_DEPT_CODE(String eXECDEPTCODE) {
		EXEC_DEPT_CODE = eXECDEPTCODE;
	}
	public String getEXEC_DR_CODE() {
		return EXEC_DR_CODE;
	}
	public void setEXEC_DR_CODE(String eXECDRCODE) {
		EXEC_DR_CODE = eXECDRCODE;
	}
	public String getSETMAIN_FLG() {
		return SETMAIN_FLG;
	}
	public void setSETMAIN_FLG(String sETMAINFLG) {
		SETMAIN_FLG = sETMAINFLG;
	}
	public double getORDERSET_GROUP_NO() {
		return ORDERSET_GROUP_NO;
	}
	public void setORDERSET_GROUP_NO(double oRDERSETGROUPNO) {
		ORDERSET_GROUP_NO = oRDERSETGROUPNO;
	}
	public String getORDERSET_CODE() {
		return ORDERSET_CODE;
	}
	public void setORDERSET_CODE(String oRDERSETCODE) {
		ORDERSET_CODE = oRDERSETCODE;
	}
	public String getHIDE_FLG() {
		return HIDE_FLG;
	}
	public void setHIDE_FLG(String hIDEFLG) {
		HIDE_FLG = hIDEFLG;
	}
	public String getRPTTYPE_CODE() {
		return RPTTYPE_CODE;
	}
	public void setRPTTYPE_CODE(String rPTTYPECODE) {
		RPTTYPE_CODE = rPTTYPECODE;
	}
	public String getOPTITEM_CODE() {
		return OPTITEM_CODE;
	}
	public void setOPTITEM_CODE(String oPTITEMCODE) {
		OPTITEM_CODE = oPTITEMCODE;
	}
	public String getDEV_CODE() {
		return DEV_CODE;
	}
	public void setDEV_CODE(String dEVCODE) {
		DEV_CODE = dEVCODE;
	}
	public String getMR_CODE() {
		return MR_CODE;
	}
	public void setMR_CODE(String mRCODE) {
		MR_CODE = mRCODE;
	}
	public double getFILE_NO() {
		return FILE_NO;
	}
	public void setFILE_NO(double fILENO) {
		FILE_NO = fILENO;
	}
	public String getDEGREE_CODE() {
		return DEGREE_CODE;
	}
	public void setDEGREE_CODE(String dEGREECODE) {
		DEGREE_CODE = dEGREECODE;
	}
	public String getURGENT_FLG() {
		return URGENT_FLG;
	}
	public void setURGENT_FLG(String uRGENTFLG) {
		URGENT_FLG = uRGENTFLG;
	}
	public String getINSPAY_TYPE() {
		return INSPAY_TYPE;
	}
	public void setINSPAY_TYPE(String iNSPAYTYPE) {
		INSPAY_TYPE = iNSPAYTYPE;
	}
	public String getPHA_TYPE() {
		return PHA_TYPE;
	}
	public void setPHA_TYPE(String pHATYPE) {
		PHA_TYPE = pHATYPE;
	}
	public String getDOSE_TYPE() {
		return DOSE_TYPE;
	}
	public void setDOSE_TYPE(String dOSETYPE) {
		DOSE_TYPE = dOSETYPE;
	}
	public String getEXPENSIVE_FLG() {
		return EXPENSIVE_FLG;
	}
	public void setEXPENSIVE_FLG(String eXPENSIVEFLG) {
		EXPENSIVE_FLG = eXPENSIVEFLG;
	}
	public String getPRINTTYPEFLG_INFANT() {
		return PRINTTYPEFLG_INFANT;
	}
	public void setPRINTTYPEFLG_INFANT(String pRINTTYPEFLGINFANT) {
		PRINTTYPEFLG_INFANT = pRINTTYPEFLGINFANT;
	}
	public String getCTRLDRUGCLASS_CODE() {
		return CTRLDRUGCLASS_CODE;
	}
	public void setCTRLDRUGCLASS_CODE(String cTRLDRUGCLASSCODE) {
		CTRLDRUGCLASS_CODE = cTRLDRUGCLASSCODE;
	}
	public double getPRESCRIPT_NO() {
		return PRESCRIPT_NO;
	}
	public void setPRESCRIPT_NO(double pRESCRIPTNO) {
		PRESCRIPT_NO = pRESCRIPTNO;
	}
	public String getATC_FLG() {
		return ATC_FLG;
	}
	public void setATC_FLG(String aTCFLG) {
		ATC_FLG = aTCFLG;
	}
	public String getSENDATC_DATE() {
		return SENDATC_DATE;
	}
	public void setSENDATC_DATE(String sENDATCDate) {
		SENDATC_DATE = sENDATCDate;
	}
	public String getRECEIPT_NO() {
		return RECEIPT_NO;
	}
	public void setRECEIPT_NO(String rECEIPTNO) {
		RECEIPT_NO = rECEIPTNO;
	}
	public String getBILL_FLG() {
		return BILL_FLG;
	}
	public void setBILL_FLG(String bILLFLG) {
		BILL_FLG = bILLFLG;
	}
	public String getBILL_DATE() {
		return BILL_DATE;
	}
	public void setBILL_DATE(String bILLDate) {
		BILL_DATE = bILLDate;
	}
	public String getBILL_USER() {
		return BILL_USER;
	}
	public void setBILL_USER(String bILLUSER) {
		BILL_USER = bILLUSER;
	}
	public String getPRINT_FLG() {
		return PRINT_FLG;
	}
	public void setPRINT_FLG(String pRINTFLG) {
		PRINT_FLG = pRINTFLG;
	}
	public String getREXP_CODE() {
		return REXP_CODE;
	}
	public void setREXP_CODE(String rEXPCODE) {
		REXP_CODE = rEXPCODE;
	}
	public String getHEXP_CODE() {
		return HEXP_CODE;
	}
	public void setHEXP_CODE(String hEXPCODE) {
		HEXP_CODE = hEXPCODE;
	}
	public String getCONTRACT_CODE() {
		return CONTRACT_CODE;
	}
	public void setCONTRACT_CODE(String cONTRACTCODE) {
		CONTRACT_CODE = cONTRACTCODE;
	}
	public String getCTZ1_CODE() {
		return CTZ1_CODE;
	}
	public void setCTZ1_CODE(String cTZ1CODE) {
		CTZ1_CODE = cTZ1CODE;
	}
	public String getCTZ2_CODE() {
		return CTZ2_CODE;
	}
	public void setCTZ2_CODE(String cTZ2CODE) {
		CTZ2_CODE = cTZ2CODE;
	}
	public String getCTZ3_CODE() {
		return CTZ3_CODE;
	}
	public void setCTZ3_CODE(String cTZ3CODE) {
		CTZ3_CODE = cTZ3CODE;
	}
	public String getPHA_CHECK_CODE() {
		return PHA_CHECK_CODE;
	}
	public void setPHA_CHECK_CODE(String pHACHECKCODE) {
		PHA_CHECK_CODE = pHACHECKCODE;
	}
	public String getPHA_CHECK_DATE() {
		return PHA_CHECK_DATE;
	}
	public void setPHA_CHECK_DATE(String pHACHECKDate) {
		PHA_CHECK_DATE = pHACHECKDate;
	}
	public String getPHA_DOSAGE_CODE() {
		return PHA_DOSAGE_CODE;
	}
	public void setPHA_DOSAGE_CODE(String pHADOSAGECODE) {
		PHA_DOSAGE_CODE = pHADOSAGECODE;
	}
	public String getPHA_DOSAGE_DATE() {
		return PHA_DOSAGE_DATE;
	}
	public void setPHA_DOSAGE_DATE(String pHADOSAGEDate) {
		PHA_DOSAGE_DATE = pHADOSAGEDate;
	}
	public String getPHA_DISPENSE_CODE() {
		return PHA_DISPENSE_CODE;
	}
	public void setPHA_DISPENSE_CODE(String pHADISPENSECODE) {
		PHA_DISPENSE_CODE = pHADISPENSECODE;
	}
	public String getPHA_DISPENSE_DATE() {
		return PHA_DISPENSE_DATE;
	}
	public void setPHA_DISPENSE_DATE(String pHADISPENSEDate) {
		PHA_DISPENSE_DATE = pHADISPENSEDate;
	}
	public String getPHA_RETN_CODE() {
		return PHA_RETN_CODE;
	}
	public void setPHA_RETN_CODE(String pHARETNCODE) {
		PHA_RETN_CODE = pHARETNCODE;
	}
	public String getPHA_RETN_DATE() {
		return PHA_RETN_DATE;
	}
	public void setPHA_RETN_DATE(String pHARETNDate) {
		PHA_RETN_DATE = pHARETNDate;
	}
	public String getNS_EXEC_CODE() {
		return NS_EXEC_CODE;
	}
	public void setNS_EXEC_CODE(String nSEXECCODE) {
		NS_EXEC_CODE = nSEXECCODE;
	}
	public String getNS_EXEC_DATE() {
		return NS_EXEC_DATE;
	}
	public void setNS_EXEC_DATE(String nSEXECDate) {
		NS_EXEC_DATE = nSEXECDate;
	}
	public String getNS_EXEC_DEPT() {
		return NS_EXEC_DEPT;
	}
	public void setNS_EXEC_DEPT(String nSEXECDEPT) {
		NS_EXEC_DEPT = nSEXECDEPT;
	}
	public String getDCTAGENT_CODE() {
		return DCTAGENT_CODE;
	}
	public void setDCTAGENT_CODE(String dCTAGENTCODE) {
		DCTAGENT_CODE = dCTAGENTCODE;
	}
	public String getDCTEXCEP_CODE() {
		return DCTEXCEP_CODE;
	}
	public void setDCTEXCEP_CODE(String dCTEXCEPCODE) {
		DCTEXCEP_CODE = dCTEXCEPCODE;
	}
	public double getDCT_TAKE_QTY() {
		return DCT_TAKE_QTY;
	}
	public void setDCT_TAKE_QTY(double dCTTAKEQTY) {
		DCT_TAKE_QTY = dCTTAKEQTY;
	}
	public double getPACKAGE_TOT() {
		return PACKAGE_TOT;
	}
	public void setPACKAGE_TOT(double pACKAGETOT) {
		PACKAGE_TOT = pACKAGETOT;
	}
	public String getAGENCY_ORG_CODE() {
		return AGENCY_ORG_CODE;
	}
	public void setAGENCY_ORG_CODE(String aGENCYORGCODE) {
		AGENCY_ORG_CODE = aGENCYORGCODE;
	}
	public String getDCTAGENT_FLG() {
		return DCTAGENT_FLG;
	}
	public void setDCTAGENT_FLG(String dCTAGENTFLG) {
		DCTAGENT_FLG = dCTAGENTFLG;
	}
	public String getDECOCT_CODE() {
		return DECOCT_CODE;
	}
	public void setDECOCT_CODE(String dECOCTCODE) {
		DECOCT_CODE = dECOCTCODE;
	}
	public String getREQUEST_FLG() {
		return REQUEST_FLG;
	}
	public void setREQUEST_FLG(String rEQUESTFLG) {
		REQUEST_FLG = rEQUESTFLG;
	}
	public String getREQUEST_NO() {
		return REQUEST_NO;
	}
	public void setREQUEST_NO(String rEQUESTNO) {
		REQUEST_NO = rEQUESTNO;
	}
	public String getOPT_USER() {
		return OPT_USER;
	}
	public void setOPT_USER(String oPTUSER) {
		OPT_USER = oPTUSER;
	}
	public String getOPT_DATE() {
		return OPT_DATE;
	}
	public void setOPT_DATE(String oPTString) {
		OPT_DATE = oPTString;
	}
	public String getOPT_TERM() {
		return OPT_TERM;
	}
	public void setOPT_TERM(String oPTTERM) {
		OPT_TERM = oPTTERM;
	}
	public String getMED_APPLY_NO() {
		return MED_APPLY_NO;
	}
	public void setMED_APPLY_NO(String mEDAPPLYNO) {
		MED_APPLY_NO = mEDAPPLYNO;
	}
	public String getCAT1_TYPE() {
		return CAT1_TYPE;
	}
	public void setCAT1_TYPE(String cAT1TYPE) {
		CAT1_TYPE = cAT1TYPE;
	}
	public String getTRADE_ENG_DESC() {
		return TRADE_ENG_DESC;
	}
	public void setTRADE_ENG_DESC(String tRADEENGDESC) {
		TRADE_ENG_DESC = tRADEENGDESC;
	}
	public String getPRINT_NO() {
		return PRINT_NO;
	}
	public void setPRINT_NO(String pRINTNO) {
		PRINT_NO = pRINTNO;
	}
	public double getCOUNTER_NO() {
		return COUNTER_NO;
	}
	public void setCOUNTER_NO(double cOUNTERNO) {
		COUNTER_NO = cOUNTERNO;
	}
	public String getPSY_FLG() {
		return PSY_FLG;
	}
	public void setPSY_FLG(String pSYFLG) {
		PSY_FLG = pSYFLG;
	}
	public String getEXEC_FLG() {
		return EXEC_FLG;
	}
	public void setEXEC_FLG(String eXECFLG) {
		EXEC_FLG = eXECFLG;
	}
	public String getRECEIPT_FLG() {
		return RECEIPT_FLG;
	}
	public void setRECEIPT_FLG(String rECEIPTFLG) {
		RECEIPT_FLG = rECEIPTFLG;
	}
	public String getBILL_TYPE() {
		return BILL_TYPE;
	}
	public void setBILL_TYPE(String bILLTYPE) {
		BILL_TYPE = bILLTYPE;
	}
	public String getFINAL_TYPE() {
		return FINAL_TYPE;
	}
	public void setFINAL_TYPE(String fINALTYPE) {
		FINAL_TYPE = fINALTYPE;
	}
	public String getDECOCT_REMARK() {
		return DECOCT_REMARK;
	}
	public void setDECOCT_REMARK(String dECOCTREMARK) {
		DECOCT_REMARK = dECOCTREMARK;
	}
	public String getSEND_DCT_USER() {
		return SEND_DCT_USER;
	}
	public void setSEND_DCT_USER(String sENDDCTUSER) {
		SEND_DCT_USER = sENDDCTUSER;
	}
	public String getSEND_DCT_DATE() {
		return SEND_DCT_DATE;
	}
	public void setSEND_DCT_DATE(String sENDDCTDate) {
		SEND_DCT_DATE = sENDDCTDate;
	}
	public String getDECOCT_USER() {
		return DECOCT_USER;
	}
	public void setDECOCT_USER(String dECOCTUSER) {
		DECOCT_USER = dECOCTUSER;
	}
	public String getDECOCT_DATE() {
		return DECOCT_DATE;
	}
	public void setDECOCT_DATE(String dECOCTString) {
		DECOCT_DATE = dECOCTString;
	}
	public String getSEND_ORG_USER() {
		return SEND_ORG_USER;
	}
	public void setSEND_ORG_USER(String sENDORGUSER) {
		SEND_ORG_USER = sENDORGUSER;
	}
	public String getSEND_ORG_DATE() {
		return SEND_ORG_DATE;
	}
	public void setSEND_ORG_DATE(String sENDORGString) {
		SEND_ORG_DATE = sENDORGString;
	}
	public String getEXM_EXEC_END_DATE() {
		return EXM_EXEC_END_DATE;
	}
	public void setEXM_EXEC_END_DATE(String eXMEXECENDDate) {
		EXM_EXEC_END_DATE = eXMEXECENDDate;
	}
	public String getEXEC_DR_DESC() {
		return EXEC_DR_DESC;
	}
	public void setEXEC_DR_DESC(String eXECDRDESC) {
		EXEC_DR_DESC = eXECDRDESC;
	}
	public double getCOST_AMT() {
		return COST_AMT;
	}
	public void setCOST_AMT(double cOSTAMT) {
		COST_AMT = cOSTAMT;
	}
	public String getCOST_CENTER_CODE() {
		return COST_CENTER_CODE;
	}
	public void setCOST_CENTER_CODE(String cOSTCENTERCODE) {
		COST_CENTER_CODE = cOSTCENTERCODE;
	}
	public double getBATCH_SEQ1() {
		return BATCH_SEQ1;
	}
	public void setBATCH_SEQ1(double bATCHSEQ1) {
		BATCH_SEQ1 = bATCHSEQ1;
	}
	public double getVERIFYIN_PRICE1() {
		return VERIFYIN_PRICE1;
	}
	public void setVERIFYIN_PRICE1(double vERIFYINPRICE1) {
		VERIFYIN_PRICE1 = vERIFYINPRICE1;
	}
	public double getDISPENSE_QTY1() {
		return DISPENSE_QTY1;
	}
	public void setDISPENSE_QTY1(double dISPENSEQTY1) {
		DISPENSE_QTY1 = dISPENSEQTY1;
	}
	public double getBATCH_SEQ2() {
		return BATCH_SEQ2;
	}
	public void setBATCH_SEQ2(double bATCHSEQ2) {
		BATCH_SEQ2 = bATCHSEQ2;
	}
	public double getVERIFYIN_PRICE2() {
		return VERIFYIN_PRICE2;
	}
	public void setVERIFYIN_PRICE2(double vERIFYINPRICE2) {
		VERIFYIN_PRICE2 = vERIFYINPRICE2;
	}
	public double getDISPENSE_QTY2() {
		return DISPENSE_QTY2;
	}
	public void setDISPENSE_QTY2(double dISPENSEQTY2) {
		DISPENSE_QTY2 = dISPENSEQTY2;
	}
	public double getBATCH_SEQ3() {
		return BATCH_SEQ3;
	}
	public void setBATCH_SEQ3(double bATCHSEQ3) {
		BATCH_SEQ3 = bATCHSEQ3;
	}
	public double getVERIFYIN_PRICE3() {
		return VERIFYIN_PRICE3;
	}
	public void setVERIFYIN_PRICE3(double vERIFYINPRICE3) {
		VERIFYIN_PRICE3 = vERIFYINPRICE3;
	}
	public double getDISPENSE_QTY3() {
		return DISPENSE_QTY3;
	}
	public void setDISPENSE_QTY3(double dISPENSEQTY3) {
		DISPENSE_QTY3 = dISPENSEQTY3;
	}
	public String getBUSINESS_NO() {
		return BUSINESS_NO;
	}
	public void setBUSINESS_NO(String bUSINESSNO) {
		BUSINESS_NO = bUSINESSNO;
	}
	public String getPAT_NAME() {
		return PAT_NAME;
	}
	public void setPAT_NAME(String pATNAME) {
		PAT_NAME = pATNAME;
	}
	public String getSEX_TYPE() {
		return SEX_TYPE;
	}
	public void setSEX_TYPE(String sEXTYPE) {
		SEX_TYPE = sEXTYPE;
	}
	public String getBIRTH_DATE() {
		return BIRTH_DATE;
	}
	public void setBIRTH_DATE(String bIRTHdate) {
		BIRTH_DATE = bIRTHdate;
	}

}
