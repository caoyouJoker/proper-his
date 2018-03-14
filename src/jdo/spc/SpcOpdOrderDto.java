package jdo.spc;

import java.io.Serializable;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Title: 物联网门急诊药房webservice数据传输DTO</p>
 *
 * <p> Description: 物联网门急诊药房webservice数据传输DTO </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Yuanxm
 * 
 * @version 1.0
 */
public class SpcOpdOrderDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private String caseNo;
	private String rxNo;
	private Integer seqNo;
	
	
	private String presrtNo;
	private String regionCode;
	private String mrNo;
	private String admType;
	private String rxType;
	private String temporaryFlg;
	private String releaseFlg;
	private String linkmainFlg;
	private String linkNo;
	private String orderCode;
	private String orderDesc;
	private String goodsDesc;
	private String specification;
	private String orderCat1Code;
	private Double mediQty;
	private String mediUnit;
	private String freqCode;
	private String routeCode;
	private Integer takeDays;
	private Double dosageQty;
	private String dosageUnit;
	private Double dispenseQty;
	private String dispenseUnit;
	private String giveboxFlg;
	private Double ownPrice;
	private Double nhiPrice;
	private Double discountRate;
	private Double ownAmt;
	private Double arAmt;
	private String drNote;
	private String nsNote;
	private String drCode;
	private String orderDate;
	private String deptCode;
	private String dcDrCode;
	private String dcOrderDate;
	private String dcDeptCode;
	private String execDeptCode;
	private String execDrCode;
	private String setmainFlg;
	private Integer ordersetGroupNo;
	private String ordersetCode;
	private String hideFlg;
	private String rpttypeCode;
	private String optitemCode;
	private String devCode;
	private String mrCode;
	private Integer fileNo;
	private String degreeCode;
	private String urgentFlg;
	private String inspayType;
	private String phaType;
	private String doseType;
	private String expensiveFlg;
	private String printtypeflgInfant;
	private String ctrldrugclassCode;
	private Integer prescriptNo;
	private String atcFlg;
	private String sendatcDate;
	private String receiptNo;
	private String billFlg;
	private String billDate;
	private String billUser;
	private String printFlg;
	private String rexpCode;
	private String hexpCode;
	private String contractCode;
	private String ctz1Code;
	private String ctz2Code;
	private String ctz3Code;
	private String phaCheckCode;
	private String phaCheckDate;
	private String phaDosageCode;
	private String phaDosageDate;
	private String phaDispenseCode;
	private String phaDispenseDate;
	private String phaRetnCode;
	private String phaRetnDate;
	private String nsExecCode;
	private String nsExecDate;
	private String nsExecDept;
	private String dctagentCode;
	private String dctexcepCode;
	private Integer dctTakeQty;
	private Integer packageTot;
	private String agencyOrgCode;
	private String dctagentFlg;
	private String decoctCode;
	private String requestFlg;
	private String requestNo;
	private String optUser;
	private String optDate;
	private String optTerm;
	private String medApplyNo;
	private String cat1Type;
	private String tradeEngDesc;
	private String printNo;
	private Integer counterNo;
	private String psyFlg;
	private String execFlg;
	private String receiptFlg;
	private String billType;
	private String finalType;
	private String decoctRemark;
	private String sendDctUser;
	private String sendDctDate;
	private String decoctUser;
	private String decoctDate;
	private String sendOrgUser;
	private String sendOrgDate;
	private String exmExecEndDate;
	private String execDrDesc;
	private Double costAmt;
	private String costCenterCode;
	private Integer batchSeq1;
	private Double verifyinPrice1;
	private Double dispenseQty1;
	private Integer batchSeq2;
	private Double verifyinPrice2;
	private Double dispenseQty2;
	private Integer batchSeq3;
	private Double verifyinPrice3;
	private Double dispenseQty3;
	private String businessNo;
	
	
	//PAT_NAME 
	private String patName; 
	
	//BIRTH_DATE
	private String birthDate ;
	
	//SEX_TYPE
	private String sexType ;

	// Constructors

	/** default constructor */
	public SpcOpdOrderDto() {
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getRxNo() {
		return rxNo;
	}

	public void setRxNo(String rxNo) {
		this.rxNo = rxNo;
	}

	public Integer getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	
	//@Column(name = "PRESRT_NO", length = 20)
	public String getPresrtNo() {
		return this.presrtNo;
	}

	public void setPresrtNo(String presrtNo) {
		this.presrtNo = presrtNo;
	}

	//@Column(name = "REGION_CODE", length = 20)
	public String getRegionCode() {
		return this.regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	//@Column(name = "MR_NO", length = 20)
	public String getMrNo() {
		return this.mrNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	//@Column(name = "ADM_TYPE", length = 1)
	public String getAdmType() {
		return this.admType;
	}

	public void setAdmType(String admType) {
		this.admType = admType;
	}

	//@Column(name = "RX_TYPE", length = 1)
	public String getRxType() {
		return this.rxType;
	}

	public void setRxType(String rxType) {
		this.rxType = rxType;
	}

	//@Column(name = "TEMPORARY_FLG", length = 1)
	public String getTemporaryFlg() {
		return this.temporaryFlg;
	}

	public void setTemporaryFlg(String temporaryFlg) {
		this.temporaryFlg = temporaryFlg;
	}

	//@Column(name = "RELEASE_FLG", length = 1)
	public String getReleaseFlg() {
		return this.releaseFlg;
	}

	public void setReleaseFlg(String releaseFlg) {
		this.releaseFlg = releaseFlg;
	}

	//@Column(name = "LINKMAIN_FLG", length = 1)
	public String getLinkmainFlg() {
		return this.linkmainFlg;
	}

	public void setLinkmainFlg(String linkmainFlg) {
		this.linkmainFlg = linkmainFlg;
	}

	//@Column(name = "LINK_NO", length = 20)
	public String getLinkNo() {
		return this.linkNo;
	}

	public void setLinkNo(String linkNo) {
		this.linkNo = linkNo;
	}

	//@Column(name = "ORDER_CODE", length = 20)
	public String getOrderCode() {
		return this.orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	//@Column(name = "ORDER_DESC", length = 200)
	public String getOrderDesc() {
		return this.orderDesc;
	}

	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}

	//@Column(name = "GOODS_DESC", length = 200)
	public String getGoodsDesc() {
		return this.goodsDesc;
	}

	public void setGoodsDesc(String goodsDesc) {
		this.goodsDesc = goodsDesc;
	}

	//@Column(name = "SPECIFICATION", length = 200)
	public String getSpecification() {
		return this.specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	//@Column(name = "ORDER_CAT1_CODE", length = 20)
	public String getOrderCat1Code() {
		return this.orderCat1Code;
	}

	public void setOrderCat1Code(String orderCat1Code) {
		this.orderCat1Code = orderCat1Code;
	}

	//@Column(name = "MEDI_QTY", precision = 8, scale = 3)
	public Double getMediQty() {
		return this.mediQty;
	}

	public void setMediQty(Double mediQty) {
		this.mediQty = mediQty;
	}

	//@Column(name = "MEDI_UNIT", length = 20)
	public String getMediUnit() {
		return this.mediUnit;
	}

	public void setMediUnit(String mediUnit) {
		this.mediUnit = mediUnit;
	}

	//@Column(name = "FREQ_CODE", length = 20)
	public String getFreqCode() {
		return this.freqCode;
	}

	public void setFreqCode(String freqCode) {
		this.freqCode = freqCode;
	}

	//@Column(name = "ROUTE_CODE", length = 20)
	public String getRouteCode() {
		return this.routeCode;
	}

	public void setRouteCode(String routeCode) {
		this.routeCode = routeCode;
	}

	//@Column(name = "TAKE_DAYS", precision = 3, scale = 0)
	public Integer getTakeDays() {
		return this.takeDays;
	}

	public void setTakeDays(Integer takeDays) {
		this.takeDays = takeDays;
	}

	//@Column(name = "DOSAGE_QTY", precision = 8, scale = 3)
	public Double getDosageQty() {
		return this.dosageQty;
	}

	public void setDosageQty(Double dosageQty) {
		this.dosageQty = dosageQty;
	}

	//@Column(name = "DOSAGE_UNIT", length = 20)
	public String getDosageUnit() {
		return this.dosageUnit;
	}

	public void setDosageUnit(String dosageUnit) {
		this.dosageUnit = dosageUnit;
	}

	//@Column(name = "DISPENSE_QTY", precision = 8, scale = 3)
	public Double getDispenseQty() {
		return this.dispenseQty;
	}

	public void setDispenseQty(Double dispenseQty) {
		this.dispenseQty = dispenseQty;
	}

	//@Column(name = "DISPENSE_UNIT", length = 20)
	public String getDispenseUnit() {
		return this.dispenseUnit;
	}

	public void setDispenseUnit(String dispenseUnit) {
		this.dispenseUnit = dispenseUnit;
	}

	//@Column(name = "GIVEBOX_FLG", length = 1)
	public String getGiveboxFlg() {
		return this.giveboxFlg;
	}

	public void setGiveboxFlg(String giveboxFlg) {
		this.giveboxFlg = giveboxFlg;
	}

	//@Column(name = "OWN_PRICE", precision = 10, scale = 4)
	public Double getOwnPrice() {
		return this.ownPrice;
	}

	public void setOwnPrice(Double ownPrice) {
		this.ownPrice = ownPrice;
	}

	//@Column(name = "NHI_PRICE", precision = 10, scale = 4)
	public Double getNhiPrice() {
		return this.nhiPrice;
	}

	public void setNhiPrice(Double nhiPrice) {
		this.nhiPrice = nhiPrice;
	}

	//@Column(name = "DISCOUNT_RATE", precision = 5, scale = 4)
	public Double getDiscountRate() {
		return this.discountRate;
	}

	public void setDiscountRate(Double discountRate) {
		this.discountRate = discountRate;
	}

	//@Column(name = "OWN_AMT", precision = 10)
	public Double getOwnAmt() {
		return this.ownAmt;
	}

	public void setOwnAmt(Double ownAmt) {
		this.ownAmt = ownAmt;
	}

	//@Column(name = "AR_AMT", precision = 10)
	public Double getArAmt() {
		return this.arAmt;
	}

	public void setArAmt(Double arAmt) {
		this.arAmt = arAmt;
	}

	//@Column(name = "DR_NOTE", length = 200)
	public String getDrNote() {
		return this.drNote;
	}

	public void setDrNote(String drNote) {
		this.drNote = drNote;
	}

	//@Column(name = "NS_NOTE", length = 100)
	public String getNsNote() {
		return this.nsNote;
	}

	public void setNsNote(String nsNote) {
		this.nsNote = nsNote;
	}

	//@Column(name = "DR_CODE", length = 20)
	public String getDrCode() {
		return this.drCode;
	}

	public void setDrCode(String drCode) {
		this.drCode = drCode;
	}

	//@Column(name = "ORDER_DATE", length = 7)
	
	public String getOrderDate() {
		return this.orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	//@Column(name = "DEPT_CODE", length = 20)
	public String getDeptCode() {
		return this.deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	//@Column(name = "DC_DR_CODE", length = 20)
	public String getDcDrCode() {
		return this.dcDrCode;
	}

	public void setDcDrCode(String dcDrCode) {
		this.dcDrCode = dcDrCode;
	}

	//@Column(name = "DC_ORDER_DATE", length = 7)
	public String getDcOrderDate() {
		return this.dcOrderDate;
	}

	public void setDcOrderDate(String dcOrderDate) {
		this.dcOrderDate = dcOrderDate;
	}

	//@Column(name = "DC_DEPT_CODE", length = 20)
	public String getDcDeptCode() {
		return this.dcDeptCode;
	}

	public void setDcDeptCode(String dcDeptCode) {
		this.dcDeptCode = dcDeptCode;
	}

	//@Column(name = "EXEC_DEPT_CODE", length = 20)
	public String getExecDeptCode() {
		return this.execDeptCode;
	}

	public void setExecDeptCode(String execDeptCode) {
		this.execDeptCode = execDeptCode;
	}

	//@Column(name = "EXEC_DR_CODE", length = 20)
	public String getExecDrCode() {
		return this.execDrCode;
	}

	public void setExecDrCode(String execDrCode) {
		this.execDrCode = execDrCode;
	}

	//@Column(name = "SETMAIN_FLG", length = 1)
	public String getSetmainFlg() {
		return this.setmainFlg;
	}

	public void setSetmainFlg(String setmainFlg) {
		this.setmainFlg = setmainFlg;
	}

	//@Column(name = "ORDERSET_GROUP_NO", precision = 3, scale = 0)
	public Integer getOrdersetGroupNo() {
		return this.ordersetGroupNo;
	}

	public void setOrdersetGroupNo(Integer ordersetGroupNo) {
		this.ordersetGroupNo = ordersetGroupNo;
	}

	//@Column(name = "ORDERSET_CODE", length = 20)
	public String getOrdersetCode() {
		return this.ordersetCode;
	}

	public void setOrdersetCode(String ordersetCode) {
		this.ordersetCode = ordersetCode;
	}

	//@Column(name = "HIDE_FLG", length = 1)
	public String getHideFlg() {
		return this.hideFlg;
	}

	public void setHideFlg(String hideFlg) {
		this.hideFlg = hideFlg;
	}

	//@Column(name = "RPTTYPE_CODE", length = 20)
	public String getRpttypeCode() {
		return this.rpttypeCode;
	}

	public void setRpttypeCode(String rpttypeCode) {
		this.rpttypeCode = rpttypeCode;
	}

	//@Column(name = "OPTITEM_CODE", length = 20)
	public String getOptitemCode() {
		return this.optitemCode;
	}

	public void setOptitemCode(String optitemCode) {
		this.optitemCode = optitemCode;
	}

	//@Column(name = "DEV_CODE", length = 20)
	public String getDevCode() {
		return this.devCode;
	}

	public void setDevCode(String devCode) {
		this.devCode = devCode;
	}

	//@Column(name = "MR_CODE", length = 20)
	public String getMrCode() {
		return this.mrCode;
	}

	public void setMrCode(String mrCode) {
		this.mrCode = mrCode;
	}

	//@Column(name = "FILE_NO", precision = 5, scale = 0)
	public Integer getFileNo() {
		return this.fileNo;
	}

	public void setFileNo(Integer fileNo) {
		this.fileNo = fileNo;
	}

	//@Column(name = "DEGREE_CODE", length = 20)
	public String getDegreeCode() {
		return this.degreeCode;
	}

	public void setDegreeCode(String degreeCode) {
		this.degreeCode = degreeCode;
	}

	//@Column(name = "URGENT_FLG", length = 1)
	public String getUrgentFlg() {
		return this.urgentFlg;
	}

	public void setUrgentFlg(String urgentFlg) {
		this.urgentFlg = urgentFlg;
	}

	//@Column(name = "INSPAY_TYPE", length = 1)
	public String getInspayType() {
		return this.inspayType;
	}

	public void setInspayType(String inspayType) {
		this.inspayType = inspayType;
	}

	//@Column(name = "PHA_TYPE", length = 1)
	public String getPhaType() {
		return this.phaType;
	}

	public void setPhaType(String phaType) {
		this.phaType = phaType;
	}

	//@Column(name = "DOSE_TYPE", length = 1)
	public String getDoseType() {
		return this.doseType;
	}

	public void setDoseType(String doseType) {
		this.doseType = doseType;
	}

	//@Column(name = "EXPENSIVE_FLG", length = 1)
	public String getExpensiveFlg() {
		return this.expensiveFlg;
	}

	public void setExpensiveFlg(String expensiveFlg) {
		this.expensiveFlg = expensiveFlg;
	}

	//@Column(name = "PRINTTYPEFLG_INFANT", length = 1)
	public String getPrinttypeflgInfant() {
		return this.printtypeflgInfant;
	}

	public void setPrinttypeflgInfant(String printtypeflgInfant) {
		this.printtypeflgInfant = printtypeflgInfant;
	}

	//@Column(name = "CTRLDRUGCLASS_CODE", length = 20)
	public String getCtrldrugclassCode() {
		return this.ctrldrugclassCode;
	}

	public void setCtrldrugclassCode(String ctrldrugclassCode) {
		this.ctrldrugclassCode = ctrldrugclassCode;
	}

	//@Column(name = "PRESCRIPT_NO", precision = 5, scale = 0)
	public Integer getPrescriptNo() {
		return this.prescriptNo;
	}

	public void setPrescriptNo(Integer prescriptNo) {
		this.prescriptNo = prescriptNo;
	}

	//@Column(name = "ATC_FLG", length = 1)
	public String getAtcFlg() {
		return this.atcFlg;
	}

	public void setAtcFlg(String atcFlg) {
		this.atcFlg = atcFlg;
	}

	//@Column(name = "SENDATC_DATE", length = 7)
	public String getSendatcDate() {
		return this.sendatcDate;
	}

	public void setSendatcDate(String sendatcDate) {
		this.sendatcDate = sendatcDate;
	}

	//@Column(name = "RECEIPT_NO", length = 20)
	public String getReceiptNo() {
		return this.receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	//@Column(name = "BILL_FLG", length = 1)
	public String getBillFlg() {
		return this.billFlg;
	}

	public void setBillFlg(String billFlg) {
		this.billFlg = billFlg;
	}

	//@Column(name = "BILL_DATE", length = 7)
	
	public String getBillDate() {
		return this.billDate;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	//@Column(name = "BILL_USER", length = 20)
	public String getBillUser() {
		return this.billUser;
	}

	public void setBillUser(String billUser) {
		this.billUser = billUser;
	}

	//@Column(name = "PRINT_FLG", length = 1)
	public String getPrintFlg() {
		return this.printFlg;
	}

	public void setPrintFlg(String printFlg) {
		this.printFlg = printFlg;
	}

	//@Column(name = "REXP_CODE", length = 20)
	public String getRexpCode() {
		return this.rexpCode;
	}

	public void setRexpCode(String rexpCode) {
		this.rexpCode = rexpCode;
	}

	//@Column(name = "HEXP_CODE", length = 20)
	public String getHexpCode() {
		return this.hexpCode;
	}

	public void setHexpCode(String hexpCode) {
		this.hexpCode = hexpCode;
	}

	//@Column(name = "CONTRACT_CODE", length = 20)
	public String getContractCode() {
		return this.contractCode;
	}

	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}

	//@Column(name = "CTZ1_CODE", length = 20)
	public String getCtz1Code() {
		return this.ctz1Code;
	}

	public void setCtz1Code(String ctz1Code) {
		this.ctz1Code = ctz1Code;
	}

	//@Column(name = "CTZ2_CODE", length = 20)
	public String getCtz2Code() {
		return this.ctz2Code;
	}

	public void setCtz2Code(String ctz2Code) {
		this.ctz2Code = ctz2Code;
	}

	//@Column(name = "CTZ3_CODE", length = 20)
	public String getCtz3Code() {
		return this.ctz3Code;
	}

	public void setCtz3Code(String ctz3Code) {
		this.ctz3Code = ctz3Code;
	}

	//@Column(name = "PHA_CHECK_CODE", length = 20)
	public String getPhaCheckCode() {
		return this.phaCheckCode;
	}

	public void setPhaCheckCode(String phaCheckCode) {
		this.phaCheckCode = phaCheckCode;
	}

	//@Column(name = "PHA_CHECK_DATE", length = 7)
	public String getPhaCheckDate() {
		return this.phaCheckDate;
	}

	public void setPhaCheckDate(String phaCheckDate) {
		this.phaCheckDate = phaCheckDate;
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

	//@Column(name = "NS_EXEC_DEPT", length = 20)
	public String getNsExecDept() {
		return this.nsExecDept;
	}

	public void setNsExecDept(String nsExecDept) {
		this.nsExecDept = nsExecDept;
	}

	//@Column(name = "DCTAGENT_CODE", length = 20)
	public String getDctagentCode() {
		return this.dctagentCode;
	}

	public void setDctagentCode(String dctagentCode) {
		this.dctagentCode = dctagentCode;
	}

	//@Column(name = "DCTEXCEP_CODE", length = 20)
	public String getDctexcepCode() {
		return this.dctexcepCode;
	}

	public void setDctexcepCode(String dctexcepCode) {
		this.dctexcepCode = dctexcepCode;
	}

	//@Column(name = "DCT_TAKE_QTY", precision = 3, scale = 0)
	public Integer getDctTakeQty() {
		return this.dctTakeQty;
	}

	public void setDctTakeQty(Integer dctTakeQty) {
		this.dctTakeQty = dctTakeQty;
	}

	//@Column(name = "PACKAGE_TOT", precision = 4, scale = 0)
	public Integer getPackageTot() {
		return this.packageTot;
	}

	public void setPackageTot(Integer packageTot) {
		this.packageTot = packageTot;
	}

	//@Column(name = "AGENCY_ORG_CODE", length = 20)
	public String getAgencyOrgCode() {
		return this.agencyOrgCode;
	}

	public void setAgencyOrgCode(String agencyOrgCode) {
		this.agencyOrgCode = agencyOrgCode;
	}

	//@Column(name = "DCTAGENT_FLG", length = 1)
	public String getDctagentFlg() {
		return this.dctagentFlg;
	}

	public void setDctagentFlg(String dctagentFlg) {
		this.dctagentFlg = dctagentFlg;
	}

	//@Column(name = "DECOCT_CODE", length = 20)
	public String getDecoctCode() {
		return this.decoctCode;
	}

	public void setDecoctCode(String decoctCode) {
		this.decoctCode = decoctCode;
	}

	//@Column(name = "REQUEST_FLG", length = 1)
	public String getRequestFlg() {
		return this.requestFlg;
	}

	public void setRequestFlg(String requestFlg) {
		this.requestFlg = requestFlg;
	}

	//@Column(name = "REQUEST_NO", length = 20)
	public String getRequestNo() {
		return this.requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
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

	//@Column(name = "MED_APPLY_NO", length = 20)
	public String getMedApplyNo() {
		return this.medApplyNo;
	}

	public void setMedApplyNo(String medApplyNo) {
		this.medApplyNo = medApplyNo;
	}

	//@Column(name = "CAT1_TYPE", length = 20)
	public String getCat1Type() {
		return this.cat1Type;
	}

	public void setCat1Type(String cat1Type) {
		this.cat1Type = cat1Type;
	}

	//@Column(name = "TRADE_ENG_DESC", length = 200)
	public String getTradeEngDesc() {
		return this.tradeEngDesc;
	}

	public void setTradeEngDesc(String tradeEngDesc) {
		this.tradeEngDesc = tradeEngDesc;
	}

	//@Column(name = "PRINT_NO", length = 20)
	public String getPrintNo() {
		return this.printNo;
	}

	public void setPrintNo(String printNo) {
		this.printNo = printNo;
	}

	//@Column(name = "COUNTER_NO", precision = 2, scale = 0)
	public Integer getCounterNo() {
		return this.counterNo;
	}

	public void setCounterNo(Integer counterNo) {
		this.counterNo = counterNo;
	}

	//@Column(name = "PSY_FLG", length = 1)
	public String getPsyFlg() {
		return this.psyFlg;
	}

	public void setPsyFlg(String psyFlg) {
		this.psyFlg = psyFlg;
	}

	//@Column(name = "EXEC_FLG", length = 1)
	public String getExecFlg() {
		return this.execFlg;
	}

	public void setExecFlg(String execFlg) {
		this.execFlg = execFlg;
	}

	//@Column(name = "RECEIPT_FLG", length = 1)
	public String getReceiptFlg() {
		return this.receiptFlg;
	}

	public void setReceiptFlg(String receiptFlg) {
		this.receiptFlg = receiptFlg;
	}

	//@Column(name = "BILL_TYPE", length = 1)
	public String getBillType() {
		return this.billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	//@Column(name = "FINAL_TYPE", length = 1)
	public String getFinalType() {
		return this.finalType;
	}

	public void setFinalType(String finalType) {
		this.finalType = finalType;
	}

	//@Column(name = "DECOCT_REMARK", length = 50)
	public String getDecoctRemark() {
		return this.decoctRemark;
	}

	public void setDecoctRemark(String decoctRemark) {
		this.decoctRemark = decoctRemark;
	}

	//@Column(name = "SEND_DCT_USER", length = 20)
	public String getSendDctUser() {
		return this.sendDctUser;
	}

	public void setSendDctUser(String sendDctUser) {
		this.sendDctUser = sendDctUser;
	}

	//@Column(name = "SEND_DCT_DATE", length = 7)
	
	public String getSendDctDate() {
		return this.sendDctDate;
	}

	public void setSendDctDate(String sendDctDate) {
		this.sendDctDate = sendDctDate;
	}

	//@Column(name = "DECOCT_USER", length = 20)
	public String getDecoctUser() {
		return this.decoctUser;
	}

	public void setDecoctUser(String decoctUser) {
		this.decoctUser = decoctUser;
	}

	//@Column(name = "DECOCT_DATE", length = 7)
	
	public String getDecoctDate() {
		return this.decoctDate;
	}

	public void setDecoctDate(String decoctDate) {
		this.decoctDate = decoctDate;
	}

	//@Column(name = "SEND_ORG_USER", length = 20)
	public String getSendOrgUser() {
		return this.sendOrgUser;
	}

	public void setSendOrgUser(String sendOrgUser) {
		this.sendOrgUser = sendOrgUser;
	}

	//@Column(name = "SEND_ORG_DATE", length = 7)
	
	public String getSendOrgDate() {
		return this.sendOrgDate;
	}

	public void setSendOrgDate(String sendOrgDate) {
		this.sendOrgDate = sendOrgDate;
	}

	//@Column(name = "EXM_EXEC_END_DATE", length = 7)
	
	public String getExmExecEndDate() {
		return this.exmExecEndDate;
	}

	public void setExmExecEndDate(String exmExecEndDate) {
		this.exmExecEndDate = exmExecEndDate;
	}

	//@Column(name = "EXEC_DR_DESC", length = 20)
	public String getExecDrDesc() {
		return this.execDrDesc;
	}

	public void setExecDrDesc(String execDrDesc) {
		this.execDrDesc = execDrDesc;
	}

	//@Column(name = "COST_AMT", precision = 10)
	public Double getCostAmt() {
		return this.costAmt;
	}

	public void setCostAmt(Double costAmt) {
		this.costAmt = costAmt;
	}

	//@Column(name = "COST_CENTER_CODE", length = 20)
	public String getCostCenterCode() {
		return this.costCenterCode;
	}

	public void setCostCenterCode(String costCenterCode) {
		this.costCenterCode = costCenterCode;
	}

	//@Column(name = "BATCH_SEQ1", precision = 5, scale = 0)
	public Integer getBatchSeq1() {
		return this.batchSeq1;
	}

	public void setBatchSeq1(Integer batchSeq1) {
		this.batchSeq1 = batchSeq1;
	}

	//@Column(name = "VERIFYIN_PRICE1", precision = 10, scale = 4)
	public Double getVerifyinPrice1() {
		return this.verifyinPrice1;
	}

	public void setVerifyinPrice1(Double verifyinPrice1) {
		this.verifyinPrice1 = verifyinPrice1;
	}

	//@Column(name = "DISPENSE_QTY1", precision = 8, scale = 3)
	public Double getDispenseQty1() {
		return this.dispenseQty1;
	}

	public void setDispenseQty1(Double dispenseQty1) {
		this.dispenseQty1 = dispenseQty1;
	}

	//@Column(name = "BATCH_SEQ2", precision = 5, scale = 0)
	public Integer getBatchSeq2() {
		return this.batchSeq2;
	}

	public void setBatchSeq2(Integer batchSeq2) {
		this.batchSeq2 = batchSeq2;
	}

	//@Column(name = "VERIFYIN_PRICE2", precision = 10, scale = 4)
	public Double getVerifyinPrice2() {
		return this.verifyinPrice2;
	}

	public void setVerifyinPrice2(Double verifyinPrice2) {
		this.verifyinPrice2 = verifyinPrice2;
	}

	//@Column(name = "DISPENSE_QTY2", precision = 8, scale = 3)
	public Double getDispenseQty2() {
		return this.dispenseQty2;
	}

	public void setDispenseQty2(Double dispenseQty2) {
		this.dispenseQty2 = dispenseQty2;
	}

	//@Column(name = "BATCH_SEQ3", precision = 5, scale = 0)
	public Integer getBatchSeq3() {
		return this.batchSeq3;
	}

	public void setBatchSeq3(Integer batchSeq3) {
		this.batchSeq3 = batchSeq3;
	}

	//@Column(name = "VERIFYIN_PRICE3", precision = 10, scale = 4)
	public Double getVerifyinPrice3() {
		return this.verifyinPrice3;
	}

	public void setVerifyinPrice3(Double verifyinPrice3) {
		this.verifyinPrice3 = verifyinPrice3;
	}

	//@Column(name = "DISPENSE_QTY3", precision = 8, scale = 3)
	public Double getDispenseQty3() {
		return this.dispenseQty3;
	}

	public void setDispenseQty3(Double dispenseQty3) {
		this.dispenseQty3 = dispenseQty3;
	}

	//@Column(name = "BUSINESS_NO", length = 20)
	public String getBusinessNo() {
		return this.businessNo;
	}

	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}


	public String getPatName() {
		return patName;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	
	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getSexType() {
		return sexType;
	}

	public void setSexType(String sexType) {
		this.sexType = sexType;
	}
	
}
