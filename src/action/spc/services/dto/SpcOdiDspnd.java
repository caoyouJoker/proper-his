package action.spc.services.dto;
// default package
 


public class SpcOdiDspnd implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//主键
	private String caseNo;
	private String orderNo;
	private int orderSeq;
	private String orderDate;
	private String orderDatetime;
	
	// Fields
	private String batchCode;
	private String treatStartTime;
	private String treatEndTime;
	private String nurseDispenseFlg;
	private String barCode;
	private String orderCode;
	private double mediQty;
	private String mediUnit;
	private double dosageQty;
	private String dosageUnit;
	private double totAmt;
	private String dcDate;
	private String phaDispenseNo;
	private String phaDosageCode;
	private String phaDosageDate;
	private String phaDispenseCode;
	private String phaDispenseDate;
	private String nsExecCode;
	private String nsExecDate;
	private String nsExecDcCode;
	private String nsExecDcDate;
	private String nsUser;
	private String execNote;
	private String execDeptCode;
	private String billFlg;
	private String cashierCode;
	private String cashierDate;
	private String phaRetnCode;
	private String phaRetnDate;
	private String transmitRsnCode;
	private String stopcheckUser;
	private String stopcheckDate;
	private String ibsCaseNo;
	private String ibsCaseNoSeq;
	private String optUser;
	private String optDate;
	private String optTerm;
	private String nsExecDateReal;
	private String nsExecCodeReal;
	private String invCode;
	private String cancelrsnCode;
	private String dcNsCheckDate;
	private String intgmedNo;
	private String boxEslId;
	private String barcode1;
	private String barcode2;
	private String barcode3;
	private String takemedOrg;
	
	private String takemedNo;
	
	//服务等级
	private String serviceLevel;
	

	// Constructors

	/** default constructor */
	public SpcOdiDspnd() {
	}

	
 

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public int getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(int orderSeq) {
		this.orderSeq = orderSeq;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderDatetime() {
		return orderDatetime;
	}

	public void setOrderDatetime(String orderDatetime) {
		this.orderDatetime = orderDatetime;
	}

	//@Column(name = "BATCH_CODE", length = 20)
	public String getBatchCode() {
		return this.batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	//@Column(name = "TREAT_START_TIME", length = 4)
	public String getTreatStartTime() {
		return this.treatStartTime;
	}

	public void setTreatStartTime(String treatStartTime) {
		this.treatStartTime = treatStartTime;
	}

	//@Column(name = "TREAT_END_TIME", length = 4)
	public String getTreatEndTime() {
		return this.treatEndTime;
	}

	public void setTreatEndTime(String treatEndTime) {
		this.treatEndTime = treatEndTime;
	}

	//@Column(name = "NURSE_DISPENSE_FLG", length = 1)
	public String getNurseDispenseFlg() {
		return this.nurseDispenseFlg;
	}

	public void setNurseDispenseFlg(String nurseDispenseFlg) {
		this.nurseDispenseFlg = nurseDispenseFlg;
	}

	//@Column(name = "BAR_CODE", length = 20)
	public String getBarCode() {
		return this.barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	//@Column(name = "ORDER_CODE", length = 20)
	public String getOrderCode() {
		return this.orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	//@Column(name = "MEDI_QTY", precision = 8, scale = 3)
	public double getMediQty() {
		return this.mediQty;
	}

	public void setMediQty(double mediQty) {
		this.mediQty = mediQty;
	}

	//@Column(name = "MEDI_UNIT", length = 20)
	public String getMediUnit() {
		return this.mediUnit;
	}

	public void setMediUnit(String mediUnit) {
		this.mediUnit = mediUnit;
	}

	//@Column(name = "DOSAGE_QTY", precision = 8, scale = 3)
	public double getDosageQty() {
		return this.dosageQty;
	}

	public void setDosageQty(double dosageQty) {
		this.dosageQty = dosageQty;
	}

	//@Column(name = "DOSAGE_UNIT", length = 20)
	public String getDosageUnit() {
		return this.dosageUnit;
	}

	public void setDosageUnit(String dosageUnit) {
		this.dosageUnit = dosageUnit;
	}

	//@Column(name = "TOT_AMT", precision = 10)
	public double getTotAmt() {
		return this.totAmt;
	}

	public void setTotAmt(double totAmt) {
		this.totAmt = totAmt;
	}

	//@Column(name = "DC_DATE", length = 7)
	public String getDcDate() {
		return this.dcDate;
	}

	public void setDcDate(String dcDate) {
		this.dcDate = dcDate;
	}

	//@Column(name = "PHA_DISPENSE_NO", length = 20)
	public String getPhaDispenseNo() {
		return this.phaDispenseNo;
	}

	public void setPhaDispenseNo(String phaDispenseNo) {
		this.phaDispenseNo = phaDispenseNo;
	}

	//@Column(name = "PHA_DOSAGE_CODE", length = 20)
	public String getPhaDosageCode() {
		return this.phaDosageCode;
	}

	public void setPhaDosageCode(String phaDosageCode) {
		this.phaDosageCode = phaDosageCode;
	}

	//@Column(name = "PHA_DOSAGE_DATE", length = 7)
	public String getPhaDosageDate() {
		return this.phaDosageDate;
	}

	public void setPhaDosageDate(String phaDosageDate) {
		this.phaDosageDate = phaDosageDate;
	}

	//@Column(name = "PHA_DISPENSE_CODE", length = 20)
	public String getPhaDispenseCode() {
		return this.phaDispenseCode;
	}

	public void setPhaDispenseCode(String phaDispenseCode) {
		this.phaDispenseCode = phaDispenseCode;
	}

	//@Column(name = "PHA_DISPENSE_DATE", length = 7)
	public String getPhaDispenseDate() {
		return this.phaDispenseDate;
	}

	public void setPhaDispenseDate(String phaDispenseDate) {
		this.phaDispenseDate = phaDispenseDate;
	}

	//@Column(name = "NS_EXEC_CODE", length = 20)
	public String getNsExecCode() {
		return this.nsExecCode;
	}

	public void setNsExecCode(String nsExecCode) {
		this.nsExecCode = nsExecCode;
	}

	//@Column(name = "NS_EXEC_DATE", length = 7)
	public String getNsExecDate() {
		return this.nsExecDate;
	}

	public void setNsExecDate(String nsExecDate) {
		this.nsExecDate = nsExecDate;
	}

	//@Column(name = "NS_EXEC_DC_CODE", length = 20)
	public String getNsExecDcCode() {
		return this.nsExecDcCode;
	}

	public void setNsExecDcCode(String nsExecDcCode) {
		this.nsExecDcCode = nsExecDcCode;
	}

	//@Column(name = "NS_EXEC_DC_DATE", length = 7)
	public String getNsExecDcDate() {
		return this.nsExecDcDate;
	}

	public void setNsExecDcDate(String nsExecDcDate) {
		this.nsExecDcDate = nsExecDcDate;
	}

	//@Column(name = "NS_USER", length = 20)
	public String getNsUser() {
		return this.nsUser;
	}

	public void setNsUser(String nsUser) {
		this.nsUser = nsUser;
	}

	//@Column(name = "EXEC_NOTE", length = 200)
	public String getExecNote() {
		return this.execNote;
	}

	public void setExecNote(String execNote) {
		this.execNote = execNote;
	}

	//@Column(name = "EXEC_DEPT_CODE", length = 20)
	public String getExecDeptCode() {
		return this.execDeptCode;
	}

	public void setExecDeptCode(String execDeptCode) {
		this.execDeptCode = execDeptCode;
	}

	//@Column(name = "BILL_FLG", length = 1)
	public String getBillFlg() {
		return this.billFlg;
	}

	public void setBillFlg(String billFlg) {
		this.billFlg = billFlg;
	}

	//@Column(name = "CASHIER_CODE", length = 20)
	public String getCashierCode() {
		return this.cashierCode;
	}

	public void setCashierCode(String cashierCode) {
		this.cashierCode = cashierCode;
	}

	//@Column(name = "CASHIER_DATE", length = 7)
	public String getCashierDate() {
		return this.cashierDate;
	}

	public void setCashierDate(String cashierDate) {
		this.cashierDate = cashierDate;
	}

	//@Column(name = "PHA_RETN_CODE", length = 20)
	public String getPhaRetnCode() {
		return this.phaRetnCode;
	}

	public void setPhaRetnCode(String phaRetnCode) {
		this.phaRetnCode = phaRetnCode;
	}

	//@Column(name = "PHA_RETN_DATE", length = 7)
	public String getPhaRetnDate() {
		return this.phaRetnDate;
	}

	public void setPhaRetnDate(String phaRetnDate) {
		this.phaRetnDate = phaRetnDate;
	}

	//@Column(name = "TRANSMIT_RSN_CODE", length = 20)
	public String getTransmitRsnCode() {
		return this.transmitRsnCode;
	}

	public void setTransmitRsnCode(String transmitRsnCode) {
		this.transmitRsnCode = transmitRsnCode;
	}

	//@Column(name = "STOPCHECK_USER", length = 20)
	public String getStopcheckUser() {
		return this.stopcheckUser;
	}

	public void setStopcheckUser(String stopcheckUser) {
		this.stopcheckUser = stopcheckUser;
	}

	//@Column(name = "STOPCHECK_DATE", length = 7)
	public String getStopcheckDate() {
		return this.stopcheckDate;
	}

	public void setStopcheckDate(String stopcheckDate) {
		this.stopcheckDate = stopcheckDate;
	}

	//@Column(name = "IBS_CASE_NO", length = 20)
	public String getIbsCaseNo() {
		return this.ibsCaseNo;
	}

	public void setIbsCaseNo(String ibsCaseNo) {
		this.ibsCaseNo = ibsCaseNo;
	}

	//@Column(name = "IBS_CASE_NO_SEQ", length = 20)
	public String getIbsCaseNoSeq() {
		return this.ibsCaseNoSeq;
	}

	public void setIbsCaseNoSeq(String ibsCaseNoSeq) {
		this.ibsCaseNoSeq = ibsCaseNoSeq;
	}

	//@Column(name = "OPT_USER", nullable = false, length = 20)
	public String getOptUser() {
		return this.optUser;
	}

	public void setOptUser(String optUser) {
		this.optUser = optUser;
	}

	//@Column(name = "OPT_DATE", nullable = false, length = 7)
	public String getOptDate() {
		return this.optDate;
	}

	public void setOptDate(String optDate) {
		this.optDate = optDate;
	}

	//@Column(name = "OPT_TERM", nullable = false, length = 20)
	public String getOptTerm() {
		return this.optTerm;
	}

	public void setOptTerm(String optTerm) {
		this.optTerm = optTerm;
	}

	//@Column(name = "NS_EXEC_DATE_REAL", length = 7)
	public String getNsExecDateReal() {
		return this.nsExecDateReal;
	}

	public void setNsExecDateReal(String nsExecDateReal) {
		this.nsExecDateReal = nsExecDateReal;
	}

	//@Column(name = "NS_EXEC_CODE_REAL", length = 20)
	public String getNsExecCodeReal() {
		return this.nsExecCodeReal;
	}

	public void setNsExecCodeReal(String nsExecCodeReal) {
		this.nsExecCodeReal = nsExecCodeReal;
	}

	//@Column(name = "INV_CODE", length = 20)
	public String getInvCode() {
		return this.invCode;
	}

	public void setInvCode(String invCode) {
		this.invCode = invCode;
	}

	//@Column(name = "CANCELRSN_CODE", length = 20)
	public String getCancelrsnCode() {
		return this.cancelrsnCode;
	}

	public void setCancelrsnCode(String cancelrsnCode) {
		this.cancelrsnCode = cancelrsnCode;
	}

	//@Column(name = "DC_NS_CHECK_DATE", length = 7)
	public String getDcNsCheckDate() {
		return this.dcNsCheckDate;
	}

	public void setDcNsCheckDate(String dcNsCheckDate) {
		this.dcNsCheckDate = dcNsCheckDate;
	}

	//@Column(name = "INTGMED_NO", length = 20)
	public String getIntgmedNo() {
		return this.intgmedNo;
	}

	public void setIntgmedNo(String intgmedNo) {
		this.intgmedNo = intgmedNo;
	}

	//@Column(name = "BOX_ESL_ID", length = 20)
	public String getBoxEslId() {
		return this.boxEslId;
	}

	public void setBoxEslId(String boxEslId) {
		this.boxEslId = boxEslId;
	}

	//@Column(name = "BARCODE_1", length = 20)
	public String getBarcode1() {
		return this.barcode1;
	}

	public void setBarcode1(String barcode1) {
		this.barcode1 = barcode1;
	}

	//@Column(name = "BARCODE_2", length = 20)
	public String getBarcode2() {
		return this.barcode2;
	}

	public void setBarcode2(String barcode2) {
		this.barcode2 = barcode2;
	}

	//@Column(name = "BARCODE_3", length = 20)
	public String getBarcode3() {
		return this.barcode3;
	}

	public void setBarcode3(String barcode3) {
		this.barcode3 = barcode3;
	}

	//@Column(name = "TAKEMED_ORG", nullable = false, length = 1)
	public String getTakemedOrg() {
		return this.takemedOrg;
	}

	public void setTakemedOrg(String takemedOrg) {
		this.takemedOrg = takemedOrg;
	}

	//ADM_INP表字段
	public String getServiceLevel() {
		return serviceLevel;
	}

	public void setServiceLevel(String serviceLevel) {
		this.serviceLevel = serviceLevel;
	}




	public String getTakemedNo() {
		return takemedNo;
	}

	public void setTakemedNo(String takemedNo) {
		this.takemedNo = takemedNo;
	}
	
	

	
}