package action.spc.services.dto;

import java.util.ArrayList;
import java.util.List;

// default package



public class SpcOdiDspnm implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//Ö÷¼ü
	private String caseNo;
	private String orderNo;
	private int orderSeq;
	private String startDttm;
	
	// Fields
	private String endDttm;
	private String regionCode;
	private String stationCode;
	private String deptCode;
	private String vsDrCode;
	private String bedNo;
	private String ipdNo;
	private String mrNo;
	private String dspnKind;
	private String dspnDate;
	private String dspnUser;
	private String dispenseEffDate;
	private String rxNo;
	private String orderCat1Code;
	private String cat1Type;
	private String dispenseEndDate;
	private String execDeptCode;
	private String dispenseFlg;
	private String agencyOrgCode;
	private String prescriptNo;
	private String linkmainFlg;
	private String linkNo;
	private String orderCode;
	private String orderDesc;
	private String goodsDesc;
	private String specification;
	private double mediQty;
	private String mediUnit;
	private String freqCode;
	private String routeCode;
	private int takeDays;
	private double dosageQty;
	private String dosageUnit;
	private double dispenseQty;
	private String dispenseUnit;
	private String giveboxFlg;
	private double ownPrice;
	private double nhiPrice;
	private double discountRate;
	private double ownAmt;
	private double totAmt;
	private String orderDate;
	private String orderDeptCode;
	private String orderDrCode;
	private String drNote;
	private String atcFlg;
	private String sendatcFlg;
	private String sendatcDttm;
	private int injpracGroup;
	private String dcDate;
	private double dcTot;
	private String rtnNo;
	private int rtnNoSeq;
	private double rtnDosageQty;
	private String rtnDosageUnit;
	private double cancelDosageQty;
	private String cancelrsnCode;
	private String transmitRsnCode;
	private String phaRetnCode;
	private String phaRetnDate;
	private String phaCheckCode;
	private String phaCheckDate;
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
	private String ctrldrugclassCode;
	private String phaType;
	private String doseType;
	private String dctagentCode;
	private String dctexcepCode;
	private int dctTakeQty;
	private int packageAmt;
	private String dctagentFlg;
	private String presrtNo;
	private String decoctCode;
	private String urgentFlg;
	private String setmainFlg;
	private String ordersetGroupNo;
	private String ordersetCode;
	private String rpttypeCode;
	private String optitemCode;
	private String hideFlg;
	private String degreeCode;
	private String billFlg;
	private String cashierUser;
	private String cashierDate;
	private int ibsCaseNoSeq;
	private int ibsSeqNo;
	private String optUser;
	private String optDate;
	private String optTerm;
	private String antibioticCode;
	private String dcDrCode;
	private String finalType;
	private String decoctRemark;
	private String sendDctUser;
	private String sendDctDate;
	private String decoctUser;
	private String decoctDate;
	private String sendOrgUser;
	private String sendOrgDate;
	private String parentCaseNo;
	private String parentOrderNo;
	private int parentOrderSeq;
	private String parentStartDttm;
	private int batchSeq1;
	private double verifyinPrice1;
	private double dispenseQty1;
	private double returnQty1;
	private int batchSeq2;
	private double verifyinPrice2;
	private double dispenseQty2;
	private double returnQty2;
	private int batchSeq3;
	private double verifyinPrice3;
	private double dispenseQty3;
	private double returnQty3;
	private String barCode;
	private String lisReDate;
	private String lisReUser;
	private String dcNsCheckDate;
	private String isIntg;
	private String intgmedNo;
	private String turnEslId;
	private String boxEslId;
	private String takemedOrg;
	private double acumOutboundQty;
	private String takemedNo;
	
	//ÍË¿âÓÃ
	private String serviceLevel ;
	
	private String batchNo ;
	  
	private List<SpcOdiDspnd>  spcOdiDspnds = new ArrayList<SpcOdiDspnd>() ;

	// Constructors

	/** default constructor */
	public SpcOdiDspnm() {
	}
	
	public SpcOdiDspnm(String caseNo,String orderNo,int orderSeq,String startDttm,
			String rtnNo,int rtnNoSeq,double rtnDosageQty,String rtnDosageUnit,
			String transmitRsnCode,String  phaRetnCode,String phaRetunDate,String orderCode){
		this.caseNo = caseNo ;
		this.orderNo = orderNo ;
		this.orderSeq = orderSeq ;
		this.startDttm = startDttm ;
		this.rtnNo = rtnNo ;
		this.rtnNoSeq = rtnNoSeq ;
		this.rtnDosageQty = rtnDosageQty ;
		this.rtnDosageUnit = rtnDosageUnit ;
		this.transmitRsnCode = transmitRsnCode ;
		this.phaRetnCode = phaRetnCode ;
		this.phaRetnDate = phaRetunDate ;
		this.orderCode = orderCode ;
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

	public String getStartDttm() {
		return startDttm;
	}

	public void setStartDttm(String startDttm) {
		this.startDttm = startDttm;
	}

	//@Column(name = "END_DTTM", length = 20)
	public String getEndDttm() {
		return this.endDttm;
	}

	public void setEndDttm(String endDttm) {
		this.endDttm = endDttm;
	}

	//@Column(name = "REGION_CODE", length = 20)
	public String getRegionCode() {
		return this.regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	//@Column(name = "STATION_CODE", length = 20)
	public String getStationCode() {
		return this.stationCode;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	//@Column(name = "DEPT_CODE", length = 20)
	public String getDeptCode() {
		return this.deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	//@Column(name = "VS_DR_CODE", length = 20)
	public String getVsDrCode() {
		return this.vsDrCode;
	}

	public void setVsDrCode(String vsDrCode) {
		this.vsDrCode = vsDrCode;
	}

	//@Column(name = "BED_NO", length = 20)
	public String getBedNo() {
		return this.bedNo;
	}

	public void setBedNo(String bedNo) {
		this.bedNo = bedNo;
	}

	//@Column(name = "IPD_NO", length = 20)
	public String getIpdNo() {
		return this.ipdNo;
	}

	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	//@Column(name = "MR_NO", length = 20)
	public String getMrNo() {
		return this.mrNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	//@Column(name = "DSPN_KIND", length = 2)
	public String getDspnKind() {
		return this.dspnKind;
	}

	public void setDspnKind(String dspnKind) {
		this.dspnKind = dspnKind;
	}

	//@Column(name = "DSPN_DATE", length = 7)
	public String getDspnDate() {
		return this.dspnDate;
	}

	public void setDspnDate(String dspnDate) {
		this.dspnDate = dspnDate;
	}

	//@Column(name = "DSPN_USER", length = 20)
	public String getDspnUser() {
		return this.dspnUser;
	}

	public void setDspnUser(String dspnUser) {
		this.dspnUser = dspnUser;
	}

	//@Column(name = "DISPENSE_EFF_DATE", length = 7)
	public String getDispenseEffDate() {
		return this.dispenseEffDate;
	}

	public void setDispenseEffDate(String dispenseEffDate) {
		this.dispenseEffDate = dispenseEffDate;
	}

	//@Column(name = "RX_NO", length = 20)
	public String getRxNo() {
		return this.rxNo;
	}

	public void setRxNo(String rxNo) {
		this.rxNo = rxNo;
	}

	//@Column(name = "ORDER_CAT1_CODE", length = 20)
	public String getOrderCat1Code() {
		return this.orderCat1Code;
	}

	public void setOrderCat1Code(String orderCat1Code) {
		this.orderCat1Code = orderCat1Code;
	}

	//@Column(name = "CAT1_TYPE", length = 20)
	public String getCat1Type() {
		return this.cat1Type;
	}

	public void setCat1Type(String cat1Type) {
		this.cat1Type = cat1Type;
	}

	//@Column(name = "DISPENSE_END_DATE", length = 7)
	public String getDispenseEndDate() {
		return this.dispenseEndDate;
	}

	public void setDispenseEndDate(String dispenseEndDate) {
		this.dispenseEndDate = dispenseEndDate;
	}

	//@Column(name = "EXEC_DEPT_CODE", length = 20)
	public String getExecDeptCode() {
		return this.execDeptCode;
	}

	public void setExecDeptCode(String execDeptCode) {
		this.execDeptCode = execDeptCode;
	}

	//@Column(name = "DISPENSE_FLG", length = 1)
	public String getDispenseFlg() {
		return this.dispenseFlg;
	}

	public void setDispenseFlg(String dispenseFlg) {
		this.dispenseFlg = dispenseFlg;
	}

	//@Column(name = "AGENCY_ORG_CODE", length = 20)
	public String getAgencyOrgCode() {
		return this.agencyOrgCode;
	}

	public void setAgencyOrgCode(String agencyOrgCode) {
		this.agencyOrgCode = agencyOrgCode;
	}

	//@Column(name = "PRESCRIPT_NO", length = 20)
	public String getPrescriptNo() {
		return this.prescriptNo;
	}

	public void setPrescriptNo(String prescriptNo) {
		this.prescriptNo = prescriptNo;
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
	public int getTakeDays() {
		return this.takeDays;
	}

	public void setTakeDays(int takeDays) {
		this.takeDays = takeDays;
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

	//@Column(name = "DISPENSE_QTY", precision = 8, scale = 3)
	public double getDispenseQty() {
		return this.dispenseQty;
	}

	public void setDispenseQty(double dispenseQty) {
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
	public double getOwnPrice() {
		return this.ownPrice;
	}

	public void setOwnPrice(double ownPrice) {
		this.ownPrice = ownPrice;
	}

	//@Column(name = "NHI_PRICE", precision = 10, scale = 4)
	public double getNhiPrice() {
		return this.nhiPrice;
	}

	public void setNhiPrice(double nhiPrice) {
		this.nhiPrice = nhiPrice;
	}

	//@Column(name = "DISCOUNT_RATE", precision = 5, scale = 4)
	public double getDiscountRate() {
		return this.discountRate;
	}

	public void setDiscountRate(double discountRate) {
		this.discountRate = discountRate;
	}

	//@Column(name = "OWN_AMT", precision = 10)
	public double getOwnAmt() {
		return this.ownAmt;
	}

	public void setOwnAmt(double ownAmt) {
		this.ownAmt = ownAmt;
	}

	//@Column(name = "TOT_AMT", precision = 10)
	public double getTotAmt() {
		return this.totAmt;
	}

	public void setTotAmt(double totAmt) {
		this.totAmt = totAmt;
	}

	//@Column(name = "ORDER_DATE", length = 7)
	public String getOrderDate() {
		return this.orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	//@Column(name = "ORDER_DEPT_CODE", length = 20)
	public String getOrderDeptCode() {
		return this.orderDeptCode;
	}

	public void setOrderDeptCode(String orderDeptCode) {
		this.orderDeptCode = orderDeptCode;
	}

	//@Column(name = "ORDER_DR_CODE", length = 20)
	public String getOrderDrCode() {
		return this.orderDrCode;
	}

	public void setOrderDrCode(String orderDrCode) {
		this.orderDrCode = orderDrCode;
	}

	//@Column(name = "DR_NOTE", length = 200)
	public String getDrNote() {
		return this.drNote;
	}

	public void setDrNote(String drNote) {
		this.drNote = drNote;
	}

	//@Column(name = "ATC_FLG", length = 1)
	public String getAtcFlg() {
		return this.atcFlg;
	}

	public void setAtcFlg(String atcFlg) {
		this.atcFlg = atcFlg;
	}

	//@Column(name = "SENDATC_FLG", length = 1)
	public String getSendatcFlg() {
		return this.sendatcFlg;
	}

	public void setSendatcFlg(String sendatcFlg) {
		this.sendatcFlg = sendatcFlg;
	}

	//@Column(name = "SENDATC_DTTM", length = 7)
	public String getSendatcDttm() {
		return this.sendatcDttm;
	}

	public void setSendatcDttm(String sendatcDttm) {
		this.sendatcDttm = sendatcDttm;
	}

	//@Column(name = "INJPRAC_GROUP", precision = 4, scale = 0)
	public int getInjpracGroup() {
		return this.injpracGroup;
	}

	public void setInjpracGroup(int injpracGroup) {
		this.injpracGroup = injpracGroup;
	}

	//@Column(name = "DC_DATE", length = 7)
	public String getDcDate() {
		return this.dcDate;
	}

	public void setDcDate(String dcDate) {
		this.dcDate = dcDate;
	}

	//@Column(name = "DC_TOT", precision = 8)
	public double getDcTot() {
		return this.dcTot;
	}

	public void setDcTot(double dcTot) {
		this.dcTot = dcTot;
	}

	//@Column(name = "RTN_NO", length = 20)
	public String getRtnNo() {
		return this.rtnNo;
	}

	public void setRtnNo(String rtnNo) {
		this.rtnNo = rtnNo;
	}

	//@Column(name = "RTN_NO_SEQ", precision = 3, scale = 0)
	public int getRtnNoSeq() {
		return this.rtnNoSeq;
	}

	public void setRtnNoSeq(int rtnNoSeq) {
		this.rtnNoSeq = rtnNoSeq;
	}

	//@Column(name = "RTN_DOSAGE_QTY", precision = 8)
	public double getRtnDosageQty() {
		return this.rtnDosageQty;
	}

	public void setRtnDosageQty(double rtnDosageQty) {
		this.rtnDosageQty = rtnDosageQty;
	}

	//@Column(name = "RTN_DOSAGE_UNIT", length = 20)
	public String getRtnDosageUnit() {
		return this.rtnDosageUnit;
	}

	public void setRtnDosageUnit(String rtnDosageUnit) {
		this.rtnDosageUnit = rtnDosageUnit;
	}

	//@Column(name = "CANCEL_DOSAGE_QTY", precision = 8)
	public double getCancelDosageQty() {
		return this.cancelDosageQty;
	}

	public void setCancelDosageQty(double cancelDosageQty) {
		this.cancelDosageQty = cancelDosageQty;
	}

	//@Column(name = "CANCELRSN_CODE", length = 20)
	public String getCancelrsnCode() {
		return this.cancelrsnCode;
	}

	public void setCancelrsnCode(String cancelrsnCode) {
		this.cancelrsnCode = cancelrsnCode;
	}

	//@Column(name = "TRANSMIT_RSN_CODE", length = 20)
	public String getTransmitRsnCode() {
		return this.transmitRsnCode;
	}

	public void setTransmitRsnCode(String transmitRsnCode) {
		this.transmitRsnCode = transmitRsnCode;
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

	//@Column(name = "CTRLDRUGCLASS_CODE", length = 20)
	public String getCtrldrugclassCode() {
		return this.ctrldrugclassCode;
	}

	public void setCtrldrugclassCode(String ctrldrugclassCode) {
		this.ctrldrugclassCode = ctrldrugclassCode;
	}

	//@Column(name = "PHA_TYPE", length = 1)
	public String getPhaType() {
		return this.phaType;
	}

	public void setPhaType(String phaType) {
		this.phaType = phaType;
	}

	//@Column(name = "DOSE_TYPE", length = 20)
	public String getDoseType() {
		return this.doseType;
	}

	public void setDoseType(String doseType) {
		this.doseType = doseType;
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
	public int getDctTakeQty() {
		return this.dctTakeQty;
	}

	public void setDctTakeQty(int dctTakeQty) {
		this.dctTakeQty = dctTakeQty;
	}

	//@Column(name = "PACKAGE_AMT", precision = 4, scale = 0)
	public int getPackageAmt() {
		return this.packageAmt;
	}

	public void setPackageAmt(int packageAmt) {
		this.packageAmt = packageAmt;
	}

	//@Column(name = "DCTAGENT_FLG", length = 1)
	public String getDctagentFlg() {
		return this.dctagentFlg;
	}

	public void setDctagentFlg(String dctagentFlg) {
		this.dctagentFlg = dctagentFlg;
	}

	//@Column(name = "PRESRT_NO", length = 20)
	public String getPresrtNo() {
		return this.presrtNo;
	}

	public void setPresrtNo(String presrtNo) {
		this.presrtNo = presrtNo;
	}

	//@Column(name = "DECOCT_CODE", length = 20)
	public String getDecoctCode() {
		return this.decoctCode;
	}

	public void setDecoctCode(String decoctCode) {
		this.decoctCode = decoctCode;
	}

	//@Column(name = "URGENT_FLG", length = 1)
	public String getUrgentFlg() {
		return this.urgentFlg;
	}

	public void setUrgentFlg(String urgentFlg) {
		this.urgentFlg = urgentFlg;
	}

	//@Column(name = "SETMAIN_FLG", length = 1)
	public String getSetmainFlg() {
		return this.setmainFlg;
	}

	public void setSetmainFlg(String setmainFlg) {
		this.setmainFlg = setmainFlg;
	}

	//@Column(name = "ORDERSET_GROUP_NO", length = 20)
	public String getOrdersetGroupNo() {
		return this.ordersetGroupNo;
	}

	public void setOrdersetGroupNo(String ordersetGroupNo) {
		this.ordersetGroupNo = ordersetGroupNo;
	}

	//@Column(name = "ORDERSET_CODE", length = 20)
	public String getOrdersetCode() {
		return this.ordersetCode;
	}

	public void setOrdersetCode(String ordersetCode) {
		this.ordersetCode = ordersetCode;
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

	//@Column(name = "HIDE_FLG", length = 1)
	public String getHideFlg() {
		return this.hideFlg;
	}

	public void setHideFlg(String hideFlg) {
		this.hideFlg = hideFlg;
	}

	//@Column(name = "DEGREE_CODE", length = 20)
	public String getDegreeCode() {
		return this.degreeCode;
	}

	public void setDegreeCode(String degreeCode) {
		this.degreeCode = degreeCode;
	}

	//@Column(name = "BILL_FLG", length = 1)
	public String getBillFlg() {
		return this.billFlg;
	}

	public void setBillFlg(String billFlg) {
		this.billFlg = billFlg;
	}

	//@Column(name = "CASHIER_USER", length = 20)
	public String getCashierUser() {
		return this.cashierUser;
	}

	public void setCashierUser(String cashierUser) {
		this.cashierUser = cashierUser;
	}

	//@Column(name = "CASHIER_DATE", length = 7)
	public String getCashierDate() {
		return this.cashierDate;
	}

	public void setCashierDate(String cashierDate) {
		this.cashierDate = cashierDate;
	}

	//@Column(name = "IBS_CASE_NO_SEQ", precision = 5, scale = 0)
	public int getIbsCaseNoSeq() {
		return this.ibsCaseNoSeq;
	}

	public void setIbsCaseNoSeq(int ibsCaseNoSeq) {
		this.ibsCaseNoSeq = ibsCaseNoSeq;
	}

	//@Column(name = "IBS_SEQ_NO", precision = 3, scale = 0)
	public int getIbsSeqNo() {
		return this.ibsSeqNo;
	}

	public void setIbsSeqNo(int ibsSeqNo) {
		this.ibsSeqNo = ibsSeqNo;
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

	//@Column(name = "ANTIBIOTIC_CODE", length = 20)
	public String getAntibioticCode() {
		return this.antibioticCode;
	}

	public void setAntibioticCode(String antibioticCode) {
		this.antibioticCode = antibioticCode;
	}

	//@Column(name = "DC_DR_CODE", length = 20)
	public String getDcDrCode() {
		return this.dcDrCode;
	}

	public void setDcDrCode(String dcDrCode) {
		this.dcDrCode = dcDrCode;
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

	//@Column(name = "PARENT_CASE_NO", length = 20)
	public String getParentCaseNo() {
		return this.parentCaseNo;
	}

	public void setParentCaseNo(String parentCaseNo) {
		this.parentCaseNo = parentCaseNo;
	}

	//@Column(name = "PARENT_ORDER_NO", length = 20)
	public String getParentOrderNo() {
		return this.parentOrderNo;
	}

	public void setParentOrderNo(String parentOrderNo) {
		this.parentOrderNo = parentOrderNo;
	}

	//@Column(name = "PARENT_ORDER_SEQ", precision = 5, scale = 0)
	public int getParentOrderSeq() {
		return this.parentOrderSeq;
	}

	public void setParentOrderSeq(int parentOrderSeq) {
		this.parentOrderSeq = parentOrderSeq;
	}

	//@Column(name = "PARENT_START_DTTM", length = 20)
	public String getParentStartDttm() {
		return this.parentStartDttm;
	}

	public void setParentStartDttm(String parentStartDttm) {
		this.parentStartDttm = parentStartDttm;
	}

	//@Column(name = "BATCH_SEQ1", precision = 5, scale = 0)
	public int getBatchSeq1() {
		return this.batchSeq1;
	}

	public void setBatchSeq1(int batchSeq1) {
		this.batchSeq1 = batchSeq1;
	}

	//@Column(name = "VERIFYIN_PRICE1", precision = 10, scale = 4)
	public double getVerifyinPrice1() {
		return this.verifyinPrice1;
	}

	public void setVerifyinPrice1(double verifyinPrice1) {
		this.verifyinPrice1 = verifyinPrice1;
	}

	//@Column(name = "DISPENSE_QTY1", precision = 8, scale = 3)
	public double getDispenseQty1() {
		return this.dispenseQty1;
	}

	public void setDispenseQty1(double dispenseQty1) {
		this.dispenseQty1 = dispenseQty1;
	}

	//@Column(name = "RETURN_QTY1", precision = 8, scale = 3)
	public double getReturnQty1() {
		return this.returnQty1;
	}

	public void setReturnQty1(double returnQty1) {
		this.returnQty1 = returnQty1;
	}

	//@Column(name = "BATCH_SEQ2", precision = 5, scale = 0)
	public int getBatchSeq2() {
		return this.batchSeq2;
	}

	public void setBatchSeq2(int batchSeq2) {
		this.batchSeq2 = batchSeq2;
	}

	//@Column(name = "VERIFYIN_PRICE2", precision = 10, scale = 4)
	public double getVerifyinPrice2() {
		return this.verifyinPrice2;
	}

	public void setVerifyinPrice2(double verifyinPrice2) {
		this.verifyinPrice2 = verifyinPrice2;
	}

	//@Column(name = "DISPENSE_QTY2", precision = 8, scale = 3)
	public double getDispenseQty2() {
		return this.dispenseQty2;
	}

	public void setDispenseQty2(double dispenseQty2) {
		this.dispenseQty2 = dispenseQty2;
	}

	//@Column(name = "RETURN_QTY2", precision = 8, scale = 3)
	public double getReturnQty2() {
		return this.returnQty2;
	}

	public void setReturnQty2(double returnQty2) {
		this.returnQty2 = returnQty2;
	}

	//@Column(name = "BATCH_SEQ3", precision = 5, scale = 0)
	public int getBatchSeq3() {
		return this.batchSeq3;
	}

	public void setBatchSeq3(int batchSeq3) {
		this.batchSeq3 = batchSeq3;
	}

	//@Column(name = "VERIFYIN_PRICE3", precision = 10, scale = 4)
	public double getVerifyinPrice3() {
		return this.verifyinPrice3;
	}

	public void setVerifyinPrice3(double verifyinPrice3) {
		this.verifyinPrice3 = verifyinPrice3;
	}

	//@Column(name = "DISPENSE_QTY3", precision = 8, scale = 3)
	public double getDispenseQty3() {
		return this.dispenseQty3;
	}

	public void setDispenseQty3(double dispenseQty3) {
		this.dispenseQty3 = dispenseQty3;
	}

	//@Column(name = "RETURN_QTY3", precision = 8, scale = 3)
	public double getReturnQty3() {
		return this.returnQty3;
	}

	public void setReturnQty3(double returnQty3) {
		this.returnQty3 = returnQty3;
	}

	//@Column(name = "BAR_CODE", length = 20)
	public String getBarCode() {
		return this.barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	//@Column(name = "LIS_RE_DATE", length = 7)
	public String getLisReDate() {
		return this.lisReDate;
	}

	public void setLisReDate(String lisReDate) {
		this.lisReDate = lisReDate;
	}

	//@Column(name = "LIS_RE_USER", length = 20)
	public String getLisReUser() {
		return this.lisReUser;
	}

	public void setLisReUser(String lisReUser) {
		this.lisReUser = lisReUser;
	}

	//@Column(name = "DC_NS_CHECK_DATE", length = 7)
	public String getDcNsCheckDate() {
		return this.dcNsCheckDate;
	}

	public void setDcNsCheckDate(String dcNsCheckDate) {
		this.dcNsCheckDate = dcNsCheckDate;
	}

	//@Column(name = "IS_INTG", nullable = false, length = 1)
	public String getIsIntg() {
		return this.isIntg;
	}

	public void setIsIntg(String isIntg) {
		this.isIntg = isIntg;
	}

	//@Column(name = "INTGMED_NO", length = 20)
	public String getIntgmedNo() {
		return this.intgmedNo;
	}

	public void setIntgmedNo(String intgmedNo) {
		this.intgmedNo = intgmedNo;
	}

	//@Column(name = "TURN_ESL_ID", length = 20)
	public String getTurnEslId() {
		return this.turnEslId;
	}

	public void setTurnEslId(String turnEslId) {
		this.turnEslId = turnEslId;
	}

	//@Column(name = "BOX_ESL_ID", length = 20)
	public String getBoxEslId() {
		return this.boxEslId;
	}

	public void setBoxEslId(String boxEslId) {
		this.boxEslId = boxEslId;
	}

	//@Column(name = "TAKEMED_ORG", length = 1)
	public String getTakemedOrg() {
		return this.takemedOrg;
	}

	public void setTakemedOrg(String takemedOrg) {
		this.takemedOrg = takemedOrg;
	}

	//@Column(name = "ACUM_OUTBOUND_QTY", precision = 8, scale = 3)
	public double getAcumOutboundQty() {
		return this.acumOutboundQty;
	}

	public void setAcumOutboundQty(double acumOutboundQty) {
		this.acumOutboundQty = acumOutboundQty;
	}

	//@Column(name = "TAKEMED_NO", length = 20)
	public String getTakemedNo() {
		return this.takemedNo;
	}

	public void setTakemedNo(String takemedNo) {
		this.takemedNo = takemedNo;
	}

	public List<SpcOdiDspnd> getSpcOdiDspnds() {
		return spcOdiDspnds;
	}

	public void setSpcOdiDspnds(List<SpcOdiDspnd> spcOdiDspnds) {
		this.spcOdiDspnds = spcOdiDspnds;
	}

	public String getServiceLevel() {
		return serviceLevel;
	}

	public void setServiceLevel(String serviceLevel) {
		this.serviceLevel = serviceLevel;
	}
	
	public String getBatchNo() {
		return batchNo;  
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	

}