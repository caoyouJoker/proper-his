package jdo.spc.bsm.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
*
* <p>Title: 处方细表对象</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.05.14 
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement(name = "CONSIS_PRESC_DTLVW")
public class PrescriptionTableDetail {

	private  String  PRESC_DATE  ; //处方时间
	private  String  PRESC_NO ;    //处方编号
	private  String  ITEM_NO;      //药品序号
	private  String  FLG;          //是否包药机包药注记(区分盒装)
	private  String  ATC_FLG;      //区分是否送口服包药机的标志
	private  String  ADVICE_CODE;  //医嘱编号
	private  String  DRUG_CODE ;   //药品编号
	private  String  DRUG_NAME;    //药品名称
	private  String  TRADE_NAME;   //药品商品名
	private  String  DRUG_SPEC;    //药品规格
	private  String  DRUG_PACKAGE; //药品包装规格
	private  String  DRUG_UNIT;    //药品单位
	private  String  FIRM_ID;      //药品厂家
	private  String  DRUG_PRICE;   //药品价格
	private  String  QUANTITY;     //数量
	private  String  COSTS;        //费用
	private  String  PAYMENTS;     //实付费用
	private  String  DOSAGE;	   //药品剂量（每次服用量）
	private  String  DOSAGE_UNITS; //剂量单位（每次服用单位）
	private  String  ADMINISTRATION;//药品用法（使用方法）
	private  String  FREQUENCY;     //药品用量 （使用频率 每天几次）
    private  String  DISPESEDDOSE ;  //一包里的摆药量
    private  String  DISPENSEDTOTALDOSE ;//摆药量总数
    private  String  DISPENSEDUNIT ;     //摆药单位
    private  String  AMOUNT_PER_PACKAGE; //一片的剂量 (数值)
    private  String  DISPENSE_DAYS;      //摆药天数
    private  String  FREQ_DESC_DETAIL_CODE;//服用时间编码 
    private  String  FREQ_DESC_DETAIL;     //服用时间
    private  String  BARCODE ;    //物联网编码
	/**
	 * 得到处方时间
	 * @return
	 */
	public String getPRESC_DATE() {
		return PRESC_DATE;
	}
	/**
	 * 设置处方时间
	 * @param pRESCDATE
	 */
	public void setPRESC_DATE(String pRESCDATE) {
		PRESC_DATE = pRESCDATE;
	}
	/**
	 * 处方编号
	 * @return
	 */
	public String getPRESC_NO() {
		return PRESC_NO;
	}
	/**
	 * 处方编号
	 * @param pRESCNO
	 */
	public void setPRESC_NO(String pRESCNO) {
		PRESC_NO = pRESCNO;
	}
	/**
	 * 药品序号
	 * @return
	 */
	public String getITEM_NO() {
		return ITEM_NO;
	}
    /**
     * 药品序号
     * @param iTEMNO
     */
	public void setITEM_NO(String iTEMNO) {
		ITEM_NO = iTEMNO;
	}
	/**
	 * 医嘱编号
	 * @return
	 */
	public String getADVICE_CODE() {
		return ADVICE_CODE;
	}
	/**
	 * 医嘱编号
	 * @param aDVICECODE
	 */
	public void setADVICE_CODE(String aDVICECODE) {
		ADVICE_CODE = aDVICECODE;
	}
	/**
	 * 药品编号
	 * @return
	 */
	public String getDRUG_CODE() {
		return DRUG_CODE;
	}
	/**
	 * 药品编号
	 * @param dRUGCODE
	 */
	public void setDRUG_CODE(String dRUGCODE) {
		DRUG_CODE = dRUGCODE;
	}
	/**
	 * 药品名称
	 * @return
	 */
	public String getDRUG_NAME() {
		return DRUG_NAME;
	}
	/**
	 * 药品名称	
	 * @param dRUGNAME
	 */
	public void setDRUG_NAME(String dRUGNAME) {
		DRUG_NAME = dRUGNAME;
	}
	/**
	 * 药品商品名
	 * @return
	 */
	public String getTRADE_NAME() {
		return TRADE_NAME;
	}
	/**
	 * 药品商品名
	 * @param tRADENAME
	 */
	public void setTRADE_NAME(String tRADENAME) {
		TRADE_NAME = tRADENAME;
	}
	/**
	 * 药品规格
	 * @return
	 */
	public String getDRUG_SPEC() {
		return DRUG_SPEC;
	}
	/**
	 * 药品规格
	 * @param dRUGSPEC
	 */
	public void setDRUG_SPEC(String dRUGSPEC) {
		DRUG_SPEC = dRUGSPEC;
	}
	/**
	 * 药品包装规格
	 * @return
	 */
	public String getDRUG_PACKAGE() {
		return DRUG_PACKAGE;
	}
	/**药品包装规格
	 * 
	 * @param dRUGPACKAGE
	 */
	public void setDRUG_PACKAGE(String dRUGPACKAGE) {
		DRUG_PACKAGE = dRUGPACKAGE;
	}
	/**
	 * 药品单位
	 * @return
	 */
	public String getDRUG_UNIT() {
		return DRUG_UNIT;
	}
	/**
	 * 药品单位
	 * @param dRUGUNIT
	 */
	public void setDRUG_UNIT(String dRUGUNIT) {
		DRUG_UNIT = dRUGUNIT;
	}
	/**
	 * 药品厂家
	 * @return
	 */
	public String getFIRM_ID() {
		return FIRM_ID;
	}
	/**
	 * 药品厂家
	 * @param fIRMID
	 */
	public void setFIRM_ID(String fIRMID) {
		FIRM_ID = fIRMID;
	}
	/**
	 * 药品价格
	 * @return
	 */
	public String getDRUG_PRICE() {
		return DRUG_PRICE;
	}
	/**
	 * 药品价格
	 * @param dRUGPRICE
	 */
	public void setDRUG_PRICE(String dRUGPRICE) {
		DRUG_PRICE = dRUGPRICE;
	}
	/**
	 * 数量
	 * @return
	 */
	public String getQUANTITY() {
		return QUANTITY;
	}
	/**
	 * 数量
	 * @param qUANTITY
	 */
	public void setQUANTITY(String qUANTITY) {
		QUANTITY = qUANTITY;
	}
	/**
	 * 费用
	 * @return
	 */
	public String getCOSTS() {
		return COSTS;
	}
	/**
	 * 费用
	 * @param cOSTS
	 */
	public void setCOSTS(String cOSTS) {
		COSTS = cOSTS;
	}
	/**
	 * 实付费用
	 * @return
	 */
	public String getPAYMENTS() {
		return PAYMENTS;
	}
	/**
	 * 实付费用
	 * @param pAYMENTS
	 */
	public void setPAYMENTS(String pAYMENTS) {
		PAYMENTS = pAYMENTS;
	}
	/**
	 * 药品剂量（每次服用量）
	 * @return
	 */
	public String getDOSAGE() {
		return DOSAGE;
	}
	/**
	 * 药品剂量（每次服用量）
	 * @param dOSAGE
	 */
	public void setDOSAGE(String dOSAGE) {
		DOSAGE = dOSAGE;
	}
	/**
	 * 剂量单位（每次服用单位） 
	 * @return
	 */
	public String getDOSAGE_UNITS() {
		return DOSAGE_UNITS;
	}
	/**
	 * 剂量单位（每次服用单位） 
	 * @param dOSAGEUNITS
	 */
	public void setDOSAGE_UNITS(String dOSAGEUNITS) {
		DOSAGE_UNITS = dOSAGEUNITS;
	}
	/**
	 * 药品用法（使用方法）
	 * @return
	 */
	public String getADMINISTRATION() {
		return ADMINISTRATION;
	}
	/**
	 * 药品用法（使用方法）
	 * @param aDMINISTRATION
	 */
	public void setADMINISTRATION(String aDMINISTRATION) {
		ADMINISTRATION = aDMINISTRATION;
	}
	/**
	 * 药品用量 （使用频率 每天几次）
	 * @return
	 */
	public String getFREQUENCY() {
		return FREQUENCY;
	}
	/**
	 * 药品用量 （使用频率 每天几次）
	 * @param fREQUENCY
	 */
	public void setFREQUENCY(String fREQUENCY) {
		FREQUENCY = fREQUENCY;
	}
	/**
	 * 包药机是否包药注记
	 * @return
	 */
	public String getFLG() {
		return FLG;
	}
	/**
	 * 包药机是否包药注记
	 * @param fLG
	 */
	public void setFLG(String fLG) {
		FLG = fLG;
	}
	/**
	 * 一包里的摆药量
	 * @return
	 */
	public String getDISPESEDDOSE() {
		return DISPESEDDOSE;
	}
	/**
	 * 一包里的摆药量
	 * @param dISPESEDDOSE
	 */
	public void setDISPESEDDOSE(String dISPESEDDOSE) {
		DISPESEDDOSE = dISPESEDDOSE;
	}
	/**
	 * 摆药量总数
	 * @return
	 */
	public String getDISPENSEDTOTALDOSE() {
		return DISPENSEDTOTALDOSE;
	}
	/**
	 * 摆药量总数
	 * @param dISPENSEDTOTALDOSE
	 */
	public void setDISPENSEDTOTALDOSE(String dISPENSEDTOTALDOSE) {
		DISPENSEDTOTALDOSE = dISPENSEDTOTALDOSE;
	}
	/**
	 * 摆药单位
	 * @return
	 */
	public String getDISPENSEDUNIT() {
		return DISPENSEDUNIT;
	}
	/**
	 * 摆药单位
	 * @param dISPENSEDUNIT
	 */
	public void setDISPENSEDUNIT(String dISPENSEDUNIT) {
		DISPENSEDUNIT = dISPENSEDUNIT;
	}
	/**
	 * 一片的剂量 (数值)
	 * @return
	 */
	public String getAMOUNT_PER_PACKAGE() {
		return AMOUNT_PER_PACKAGE;
	}
	/**
	 * 一片的剂量 (数值)
	 * @param aMOUNTPERPACKAGE
	 */
	public void setAMOUNT_PER_PACKAGE(String aMOUNTPERPACKAGE) {
		AMOUNT_PER_PACKAGE = aMOUNTPERPACKAGE;
	}
	/**
	 * 摆药天数
	 * @return
	 */
	public String getDISPENSE_DAYS() {
		return DISPENSE_DAYS;
	}
	/**
	 * 摆药天数
	 * @param dISPENSEDAYS
	 */
	public void setDISPENSE_DAYS(String dISPENSEDAYS) {
		DISPENSE_DAYS = dISPENSEDAYS;
	}
	/**
	 * 服用时间编码
	 * @return
	 */
	public String getFREQ_DESC_DETAIL_CODE() {
		return FREQ_DESC_DETAIL_CODE;
	}
	/**
	 * 服用时间编码
	 * @param fREQDESCDETAILCODE
	 */
	public void setFREQ_DESC_DETAIL_CODE(String fREQDESCDETAILCODE) {
		FREQ_DESC_DETAIL_CODE = fREQDESCDETAILCODE;
	}
	/**
	 * 服用时间
	 * @return
	 */
	public String getFREQ_DESC_DETAIL() {
		return FREQ_DESC_DETAIL;
	}
	/**
	 *服用时间 
	 * @param fREQDESCDETAIL
	 */
	public void setFREQ_DESC_DETAIL(String fREQDESCDETAIL) {
		FREQ_DESC_DETAIL = fREQDESCDETAIL;
	}
	/**
	 * 区分是否送口服包药机的标志
	 * @return
	 */
	public String getATC_FLG() {
		return ATC_FLG;
	}
	/**
	 * 区分是否送口服包药机的标志
	 * @param aTCFLG
	 */
	public void setATC_FLG(String aTCFLG) {
		ATC_FLG = aTCFLG;
	}
	/**
	 * 物联网编码
	 * @return
	 */
	public String getBAT_CODE() {
		return BARCODE;
	}
	/**
	 * 物联网编码
	 * @param bATCODE
	 */
	public void setBAT_CODE(String bATCODE) {
		BARCODE = bATCODE;
	}
	
}
