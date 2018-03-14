package jdo.reg.services;

public class BodyParams {
	
	private String regID;
	private String cardID; //ҽ�ƿ���
	
	private String cardNo; //����
	private String cardType; //������  01ҽ�ƿ���02���֤��03ҽ����
	private String terminalCode; //������ 01  �ֻ�APP 02  ��վ 03   ֧��������  04  ����   05
	
	private String doctorCode; //ҽ������
	private String deptCode; //���ұ���
	private String sxw; //������
	private String regDate; //����
	private String visitCode; //������
	
	private String patientID; //����ID
	private String businessFrom; //ҵ����Դ
	private String lockType; //��������  01����02����
	
	private String startDate; //���￪ʼ����
	private String endDate; //�����������
	private String appointType; //ԤԼ��ʽ
	
	private String visitDate; //��������
	
	private String patientName; //��������
	private String patientSex; //�����Ա�

	private String phoneNo; //���ߵ绰
	private String iDCardNo; //���֤��
	private String gIDCardNo; //�໤�����֤��
	private String cureCardNo; //���￨��
	private String periodCode; //ʱ�α��
	private String visitTypeCode; //�ű���
	private String paymentType; //֧����ʽ
	private String orderNo; //������
	
	private String medCardNo; //ҽ������

	private String insureSequenceNo; //ҽ��˳���
	private String medCardPwd; //ҽ��������
	private String insureTypeCode; //ҽ������
	private String mtTypeCode; //��������
	private String patientAge; //��������
	private String cashPay; //�ֽ�֧��
	
	private String money;//���
	private String businessType; //ҵ������ 01�Һţ�02�ɷ�
	private String orderTime; //����ʱ��
	private String originalBusinessSN; //ԭ�ۿ����ˮ��
	private String originalOrderNo; //ԭ�ۿ�ҵ�񶩵���  
	
	
	
	private String regFee; //�Һŷ�
	private String inspectFee; //����
	private String totalFee; //�ܷ���
	private String isEmergency;//�Ƿ���

	private String totalAmount;//�ɷ��ܽ��
	private String prescriptionCode;//������
	private String prescriptionType;//��������
	
	
	private String startTime; //��ʼʱ��
	private String endTime; //����ʱ��

	private String receiptCode; //�վݱ��
	
	private String inspection_id; //������
	private String pacsID;  //Ӱ��ID
	
	private String patientId;
	
	private String name; //Name��������
	private String birthDay; //Birthday�����գ�
	private String sex; //Sex���Ա�
	private String address; //Address����ַ��
	

	private String hISPatientID;//������

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


	private String appointCode; //ԤԼ���


	public String getMrNo() {
		return mrNo;
	}


	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}
	

}
