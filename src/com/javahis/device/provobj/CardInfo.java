package com.javahis.device.provobj;
/**
 * 卡信息
 * @author lixiang
 *
 */
public class CardInfo {	
	int piInsID;       //O个人ID[N8]
	String psCardID;   //O卡号[C16]
	String psName;//O姓名[C8]
	String psAreaCode; 	//O区属代码[C3]
	String psQueryID; //O身份证号码[C18]
	String psUnitID;    //O单位ID[C8]
	String psUnitName; //O单位名称[C50]
	String psSex;  //O性别[C2](男/女)
	String psKind;   //O人员类别[C4]
	String psBirthday;  //O出生日期[C10](YYYY-MM-DD)
	String psNational;  //O民族[C20]
	String psIndustry;  //O本人身份[C20]
	String psDuty;    //O行政职务[C30]
	String psChronic;  // O门诊特殊病种[C200](脱机返回’未知’/联机未申请时返回’未申请’/其他返回已申请的特殊病种)
	String psOthers1;  // O其它(保留)[C200]
	float pcInHosNum;  //O本年累计住院次数[N9, 4]
	float pcAccIn;     //O帐户总收[N10, 2]
	float pcAccOut;    //O帐户总支[N10, 2]
	float pcFeeNO;     //O支出版本号[N10, 2]
	float pcPubPay;   //O本年统筹支付累计[N10, 2]
	float pcHelpPay;   //O本年大病基金支付累计[N10, 2]
	float pcSupplyPay; //O本年公务员补充/企业补充支付累计[N10, 2]
	float pcOutpatSum;  //O本年普通门诊费用累计[N10, 2]
	float pcOutpatGen1;  //O本年普通门诊三个范围内费用累计[N10, 2]	
	float pcOutpatGen2;  //O本年特殊门诊三个范围内费用累计[N10, 2]
	float pcOutpatGen3;  //O本年比照住院三个范围内费用累计[N10, 2]
	float pcInpatSum;    //O本年普通住院费用累计[N10, 2]
	float pcInpatGen1;   //O本年普通住院三个范围内费用累计[N10, 2]
	float pcInpatGen2;   //O本年家庭病床住院三个范围内累计[N10, 2]
	float pcOther1;		//O本年累计自付[N10, 2]
	float pcOther2;		//O本年累计自费[N10, 2]
	float pcBankAccPay;  //O保留[N10, 2]
	float pcOtrPay;	     //O本年其它基金支付累计[N10, 2]
	float pcCashPay;	//O本年现金支付累计[N10, 2]
	float pcAccLeft;    //O帐户余额[N10, 2]
	public int getPiInsID() {
		return piInsID;
	}
	public void setPiInsID(int piInsID) {
		this.piInsID = piInsID;
	}
	public String getPsCardID() {
		return psCardID;
	}
	public void setPsCardID(String psCardID) {
		this.psCardID = psCardID;
	}
	public String getPsName() {
		return psName;
	}
	public void setPsName(String psName) {
		this.psName = psName;
	}
	public String getPsAreaCode() {
		return psAreaCode;
	}
	public void setPsAreaCode(String psAreaCode) {
		this.psAreaCode = psAreaCode;
	}
	public String getPsQueryID() {
		return psQueryID;
	}
	public void setPsQueryID(String psQueryID) {
		this.psQueryID = psQueryID;
	}
	public String getPsUnitID() {
		return psUnitID;
	}
	public void setPsUnitID(String psUnitID) {
		this.psUnitID = psUnitID;
	}
	public String getPsUnitName() {
		return psUnitName;
	}
	public void setPsUnitName(String psUnitName) {
		this.psUnitName = psUnitName;
	}
	public String getPsSex() {
		return psSex;
	}
	public void setPsSex(String psSex) {
		this.psSex = psSex;
	}
	public String getPsKind() {
		return psKind;
	}
	public void setPsKind(String psKind) {
		this.psKind = psKind;
	}
	public String getPsBirthday() {
		return psBirthday;
	}
	public void setPsBirthday(String psBirthday) {
		this.psBirthday = psBirthday;
	}
	public String getPsNational() {
		return psNational;
	}
	public void setPsNational(String psNational) {
		this.psNational = psNational;
	}
	public String getPsIndustry() {
		return psIndustry;
	}
	public void setPsIndustry(String psIndustry) {
		this.psIndustry = psIndustry;
	}
	public String getPsDuty() {
		return psDuty;
	}
	public void setPsDuty(String psDuty) {
		this.psDuty = psDuty;
	}
	public String getPsChronic() {
		return psChronic;
	}
	public void setPsChronic(String psChronic) {
		this.psChronic = psChronic;
	}
	public String getPsOthers1() {
		return psOthers1;
	}
	public void setPsOthers1(String psOthers1) {
		this.psOthers1 = psOthers1;
	}
	public float getPcInHosNum() {
		return pcInHosNum;
	}
	public void setPcInHosNum(float pcInHosNum) {
		this.pcInHosNum = pcInHosNum;
	}
	public float getPcAccIn() {
		return pcAccIn;
	}
	public void setPcAccIn(float pcAccIn) {
		this.pcAccIn = pcAccIn;
	}
	public float getPcAccOut() {
		return pcAccOut;
	}
	public void setPcAccOut(float pcAccOut) {
		this.pcAccOut = pcAccOut;
	}
	public float getPcFeeNO() {
		return pcFeeNO;
	}
	public void setPcFeeNO(float pcFeeNO) {
		this.pcFeeNO = pcFeeNO;
	}
	public float getPcPubPay() {
		return pcPubPay;
	}
	public void setPcPubPay(float pcPubPay) {
		this.pcPubPay = pcPubPay;
	}
	public float getPcHelpPay() {
		return pcHelpPay;
	}
	public void setPcHelpPay(float pcHelpPay) {
		this.pcHelpPay = pcHelpPay;
	}
	public float getPcSupplyPay() {
		return pcSupplyPay;
	}
	public void setPcSupplyPay(float pcSupplyPay) {
		this.pcSupplyPay = pcSupplyPay;
	}
	public float getPcOutpatSum() {
		return pcOutpatSum;
	}
	public void setPcOutpatSum(float pcOutpatSum) {
		this.pcOutpatSum = pcOutpatSum;
	}
	public float getPcOutpatGen1() {
		return pcOutpatGen1;
	}
	public void setPcOutpatGen1(float pcOutpatGen1) {
		this.pcOutpatGen1 = pcOutpatGen1;
	}
	public float getPcOutpatGen2() {
		return pcOutpatGen2;
	}
	public void setPcOutpatGen2(float pcOutpatGen2) {
		this.pcOutpatGen2 = pcOutpatGen2;
	}
	public float getPcOutpatGen3() {
		return pcOutpatGen3;
	}
	public void setPcOutpatGen3(float pcOutpatGen3) {
		this.pcOutpatGen3 = pcOutpatGen3;
	}
	public float getPcInpatSum() {
		return pcInpatSum;
	}
	public void setPcInpatSum(float pcInpatSum) {
		this.pcInpatSum = pcInpatSum;
	}
	public float getPcInpatGen1() {
		return pcInpatGen1;
	}
	public void setPcInpatGen1(float pcInpatGen1) {
		this.pcInpatGen1 = pcInpatGen1;
	}
	public float getPcInpatGen2() {
		return pcInpatGen2;
	}
	public void setPcInpatGen2(float pcInpatGen2) {
		this.pcInpatGen2 = pcInpatGen2;
	}
	public float getPcOther1() {
		return pcOther1;
	}
	public void setPcOther1(float pcOther1) {
		this.pcOther1 = pcOther1;
	}
	public float getPcOther2() {
		return pcOther2;
	}
	public void setPcOther2(float pcOther2) {
		this.pcOther2 = pcOther2;
	}
	public float getPcBankAccPay() {
		return pcBankAccPay;
	}
	public void setPcBankAccPay(float pcBankAccPay) {
		this.pcBankAccPay = pcBankAccPay;
	}
	public float getPcOtrPay() {
		return pcOtrPay;
	}
	public void setPcOtrPay(float pcOtrPay) {
		this.pcOtrPay = pcOtrPay;
	}
	public float getPcCashPay() {
		return pcCashPay;
	}
	public void setPcCashPay(float pcCashPay) {
		this.pcCashPay = pcCashPay;
	}
	public float getPcAccLeft() {
		return pcAccLeft;
	}
	public void setPcAccLeft(float pcAccLeft) {
		this.pcAccLeft = pcAccLeft;
	}
	
	
}
