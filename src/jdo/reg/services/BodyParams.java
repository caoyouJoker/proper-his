package jdo.reg.services;

public class BodyParams {
	
	private String regID;
	private String cardID; //医疗卡号
	
	private String cardNo; //卡号
	private String cardType; //卡类型  01医疗卡，02身份证，03医保卡
	private String terminalCode; //自助机 01  手机APP 02  网站 03   支付宝服务窗  04  窗口   05
	
	private String doctorCode; //医生编码
	private String deptCode; //科室编码
	private String sxw; //上下午
	private String regDate; //日期
	private String visitCode; //出诊编号
	
	private String patientID; //患者ID
	private String businessFrom; //业务来源
	private String lockType; //锁还类型  01锁，02解锁
	
	private String startDate; //出诊开始日期
	private String endDate; //出诊结束日期
	private String appointType; //预约方式
	
	private String visitDate; //出诊日期
	
	private String patientName; //患者姓名
	private String patientSex; //患者性别

	private String phoneNo; //患者电话
	private String iDCardNo; //身份证号
	private String gIDCardNo; //监护人身份证号
	private String cureCardNo; //就诊卡号
	private String periodCode; //时段编号
	private String visitTypeCode; //号别编号
	private String paymentType; //支付方式
	private String orderNo; //订单号
	
	private String medCardNo; //医保卡号

	private String insureSequenceNo; //医保顺序号
	private String medCardPwd; //医保卡密码
	private String insureTypeCode; //医保类型
	private String mtTypeCode; //门特类型
	private String patientAge; //患者年龄
	private String cashPay; //现金支付
	
	private String money;//金额
	private String businessType; //业务类型 01挂号，02缴费
	private String orderTime; //订单时间
	private String originalBusinessSN; //原扣款交易流水号
	private String originalOrderNo; //原扣款业务订单号  
	
	
	
	private String regFee; //挂号费
	private String inspectFee; //检查费
	private String totalFee; //总费用
	private String isEmergency;//是否急诊

	private String totalAmount;//缴费总金额
	private String prescriptionCode;//处方号
	private String prescriptionType;//处方类型
	
	
	private String startTime; //开始时间
	private String endTime; //结束时间

	private String receiptCode; //收据编号
	
	private String inspection_id; //化验编号
	private String pacsID;  //影像ID
	
	private String patientId;
	
	private String name; //Name（姓名）
	private String birthDay; //Birthday（生日）
	private String sex; //Sex（性别）
	private String address; //Address（地址）
	

	private String hISPatientID;//病案号

	private String mrNo;


	public String gethISPatientID() {
		return hISPatientID;
	}


	public void sethISPatientID(String hISPatientID) {
		this.hISPatientID = hISPatientID;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	


	public String getBirthDay() {
		return birthDay;
	}


	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}


	public String getSex() {
		return sex;
	}


	public void setSex(String sex) {
		this.sex = sex;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getPatientId() {
		return patientId;
	}


	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}


	public String getPacsID() {
		return pacsID;
	}


	public void setPacsID(String pacsID) {
		this.pacsID = pacsID;
	}


	public String getInspection_id() {
		return inspection_id;
	}


	public void setInspection_id(String inspectionId) {
		inspection_id = inspectionId;
	}


	public String getReceiptCode() {
		return receiptCode;
	}


	public void setReceiptCode(String receiptCode) {
		this.receiptCode = receiptCode;
	}


	public String getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public String getEndTime() {
		return endTime;
	}


	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}


	public String getRegFee() {
		return regFee;
	}


	public void setRegFee(String regFee) {
		this.regFee = regFee;
	}


	public String getInspectFee() {
		return inspectFee;
	}


	public void setInspectFee(String inspectFee) {
		this.inspectFee = inspectFee;
	}


	public String getTotalFee() {
		return totalFee;
	}


	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}


	public String getIsEmergency() {
		return isEmergency;
	}


	public void setIsEmergency(String isEmergency) {
		this.isEmergency = isEmergency;
	}


	public String getTotalAmount() {
		return totalAmount;
	}


	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}


	public String getPrescriptionCode() {
		return prescriptionCode;
	}


	public void setPrescriptionCode(String prescriptionCode) {
		this.prescriptionCode = prescriptionCode;
	}


	public String getPrescriptionType() {
		return prescriptionType;
	}


	public void setPrescriptionType(String prescriptionType) {
		this.prescriptionType = prescriptionType;
	}


	public String getOriginalOrderNo() {
		return originalOrderNo;
	}


	public void setOriginalOrderNo(String originalOrderNo) {
		this.originalOrderNo = originalOrderNo;
	}


	public String getOriginalBusinessSN() {
		return originalBusinessSN;
	}


	public void setOriginalBusinessSN(String originalBusinessSN) {
		this.originalBusinessSN = originalBusinessSN;
	}


	

	public String getMoney() {
		return money;
	}


	public void setMoney(String money) {
		this.money = money;
	}


	public String getBusinessType() {
		return businessType;
	}


	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}


	public String getOrderTime() {
		return orderTime;
	}


	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}


	public String getCardID() {
		return cardID;
	}


	public void setCardID(String cardID) {
		this.cardID = cardID;
	}


	public String getRegID() {
		return regID;
	}


	public void setRegID(String regID) {
		this.regID = regID;
	}


	public String getMedCardNo() {
		return medCardNo;
	}


	public void setMedCardNo(String medCardNo) {
		this.medCardNo = medCardNo;
	}


	public String getInsureSequenceNo() {
		return insureSequenceNo;
	}


	public void setInsureSequenceNo(String insureSequenceNo) {
		this.insureSequenceNo = insureSequenceNo;
	}


	public String getMedCardPwd() {
		return medCardPwd;
	}


	public void setMedCardPwd(String medCardPwd) {
		this.medCardPwd = medCardPwd;
	}


	public String getInsureTypeCode() {
		return insureTypeCode;
	}


	public void setInsureTypeCode(String insureTypeCode) {
		this.insureTypeCode = insureTypeCode;
	}


	public String getMtTypeCode() {
		return mtTypeCode;
	}


	public void setMtTypeCode(String mtTypeCode) {
		this.mtTypeCode = mtTypeCode;
	}


	public String getPatientAge() {
		return patientAge;
	}


	public void setPatientAge(String patientAge) {
		this.patientAge = patientAge;
	}


	public String getCashPay() {
		return cashPay;
	}


	public void setCashPay(String cashPay) {
		this.cashPay = cashPay;
	}


	


	private String machineIP;
	
	
	

	public String getMachineIP() {
		return machineIP;
	}


	public void setMachineIP(String machineIP) {
		this.machineIP = machineIP;
	}


	public String getCardNo() {
		return cardNo;
	}


	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}


	public String getCardType() {
		return cardType;
	}


	public void setCardType(String cardType) {
		this.cardType = cardType;
	}


	public String getTerminalCode() {
		return terminalCode;
	}


	public void setTerminalCode(String terminalCode) {
		this.terminalCode = terminalCode;
	}


	public String getDoctorCode() {
		return doctorCode;
	}


	public void setDoctorCode(String doctorCode) {
		this.doctorCode = doctorCode;
	}


	public String getDeptCode() {
		return deptCode;
	}


	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}


	public String getSxw() {
		return sxw;
	}


	public void setSxw(String sxw) {
		this.sxw = sxw;
	}


	public String getRegDate() {
		return regDate;
	}


	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}


	public String getVisitCode() {
		return visitCode;
	}


	public void setVisitCode(String visitCode) {
		this.visitCode = visitCode;
	}


	public String getPatientID() {
		return patientID;
	}


	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}


	public String getBusinessFrom() {
		return businessFrom;
	}


	public void setBusinessFrom(String businessFrom) {
		this.businessFrom = businessFrom;
	}


	public String getLockType() {
		return lockType;
	}


	public void setLockType(String lockType) {
		this.lockType = lockType;
	}


	public String getStartDate() {
		return startDate;
	}


	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}


	public String getEndDate() {
		return endDate;
	}


	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}


	public String getAppointType() {
		return appointType;
	}


	public void setAppointType(String appointType) {
		this.appointType = appointType;
	}


	public String getVisitDate() {
		return visitDate;
	}


	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}


	public String getPatientName() {
		return patientName;
	}


	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}


	public String getPatientSex() {
		return patientSex;
	}


	public void setPatientSex(String patientSex) {
		this.patientSex = patientSex;
	}


	public String getPhoneNo() {
		return phoneNo;
	}


	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}


	public String getiDCardNo() {
		return iDCardNo;
	}


	public void setiDCardNo(String iDCardNo) {
		this.iDCardNo = iDCardNo;
	}


	public String getgIDCardNo() {
		return gIDCardNo;
	}


	public void setgIDCardNo(String gIDCardNo) {
		this.gIDCardNo = gIDCardNo;
	}


	public String getCureCardNo() {
		return cureCardNo;
	}


	public void setCureCardNo(String cureCardNo) {
		this.cureCardNo = cureCardNo;
	}


	public String getPeriodCode() {
		return periodCode;
	}


	public void setPeriodCode(String periodCode) {
		this.periodCode = periodCode;
	}


	public String getVisitTypeCode() {
		return visitTypeCode;
	}


	public void setVisitTypeCode(String visitTypeCode) {
		this.visitTypeCode = visitTypeCode;
	}


	public String getPaymentType() {
		return paymentType;
	}


	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}


	public String getOrderNo() {
		return orderNo;
	}


	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}


	public String getAppointCode() {
		return appointCode;
	}


	public void setAppointCode(String appointCode) {
		this.appointCode = appointCode;
	}


	private String appointCode; //预约编号


	public String getMrNo() {
		return mrNo;
	}


	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}
	

}
