package jdo.reg.services;

import java.util.List;

public class Result {
	
	
	private String DeptCode; //���ұ��
	private String DeptDesc; //��������
	private String ParentCode; //�����ұ��
	private String OrderNum; //��������
	private String DeptType; //�������
	private String DeptAddress; //���ҵ�ַ
	private String IsEnabled; //�Ƿ���Ч
	
	
	private String DoctorCode; //ҽ������
	private String DoctorLevel; //ҽ������
	private String DoctorProf; //ҽ��ְ��
	private String DoctorProfCode; //ҽ��ְ�Ʊ���
	
	private String Sex; //�Ա�
	private String IDCardNO; //���֤��
	private String InsureDocID; //ҽ��ҽ��ID
	private String Photourl; //��Ƭ����

	private String VisitTypeCode; //�ű���ñ���
	
	private String TotalFee; //�ܽ��
	private String RegFee; //�Һŷ�
	private String InspectFee; //����
	
	
	private String VisitCode; //�Ű��
	private String Week; //����
	private String TotalNumber; //�ܿ��ź���
	private String RemainsNumber; //ʣ�����
	private String IsAppoint; //�Ƿ�ԤԼ
	private String StopType; //ͣ������
	
	private String MtCode; //���ز��ֱ��

	private String TotalNum;   //�ܷź���
	private String RemainsNum;  //ʣ�����
	private String MtRemainsNum;  //����ʣ�����
	
	
	private String PatientID; //����ID;
	private String CureCardNo; //���￨��;
	private String IDCardNo; //���֤��;
	private String GIDCardNo; //�໤�����֤��;
	private String MedCardNo; //ҽ������;
	private String HealthCardNo; //��������;
	private String PatientName; //��������;
	private String PatientAge; //��������;
	private String PatientSex; //�����Ա�;
	private String Birthday; //����;
	private String PhoneNo; //�绰����;
	private String Address; //סַ;
	
	private String Balance;//ҽ�ƿ����
	private String CardStatus;//ҽ�ƿ�״̬
	
	private String RegID; //�Һ�ID
	private String RegTime; //�Һ�ʱ��
	private String DoctorName; //ҽ������
	private String DeptName; //��������
	private String SxwMeaning; //������
	private String VisitTypeName; //�ű�����
	
	private String VisitDate; //��������
	private String Sxw; //������
	
	private String Period;//ʱ�α��
	private String PeriodCode; //ʱ�α��
	private String PeriodMeaning; //ʱ�κ���
	private String RemainingNum; //ʣ�����
	
	private String Comment; //��ע
	
	
	private String AppointCode; //ԤԼ���
	private String AppointType; //ԤԼ��ʽ
	private String AppointStatus; //ԤԼ״̬
	private String AppointDate; //ԤԼ����
	
	private String HISAppointCode;//HIS�е�ΨһԤԼ���
	private String HISPatientID;//HIS�е�Ψһ������
	private String AppointStatusMeaning;//ԤԼ״̬����
	private String AppointTime;//ԤԼʱ��
	private String APW;//������Code
	private String APWMeaning;//�����纬��
	
	private String IsEmergency;//�Ƿ���
	private String IsCost;//�Ƿ���֧��
	
	private String BusinessSN; //������ˮ��
	private String Money; //���
	//private String Balance; //���
	private String TraceTime; //����ʱ��
	
	private String InsureTypeCode; //ҽ������
	private String MtTypeCode; //��������
	
	private String PrescriptionCode; //������
	private String PrescriptionType; //��������
	private String DrugRoomName; //ҩ������
	private String ExecuteDeptName; //ִ�п�������
	private String FeeName; //��������
	private String TotalAmount; //�ϼƽ��
	private String FeeDate; //��������
	private String ItemCode; //��Ŀ���
	private String ItemName; //��Ŀ����
	private String ItemSpecs; //���
	private String DoseNoce; //ÿ������
	private String DoseUnit; //������λ
	private String Number; //����
	private String PrePrice; //����
	private String PriceUnit; //�۸�λ
	private String Cost; //��Ŀ���
	
	private String OpTime; //����ʱ��
	private String OpName; //������
	private String ReceiptCode; //�վݱ��
	private String TipMessages; //��ʾ��Ϣ
	private String HISRegID; //�Һű��
	private String TotalMoney; //�ܽ��
	
	private String IsSend; //�Ƿ�ҩ
	
	private String PrescriptionName; //��������
	private String DrugMaker; //��ҩ��
	private String DrugSender; //��ҩ��
	private String Diagnosis; //�ٴ����
	private String InputTime; //¼��ʱ��
	
	private String UseDays; //��ҩ����
	private String DailyTimes; //ÿ�����
	private String NumberUnit; //������λ
	private String Usage; //���÷���
	
	private String Inspection_id; //������
	private String Test_order_code; //������Ŀ����
	private String Test_order_name; //������Ŀ����
	private String Patient_name; //��������
	private String Sample_class_name; //�걾�������
	private String Sample_number; //�걾����
	private String Patient_sex; //�����Ա�
	private String Patient_dept_name; //��������
	private String Sampling_time; //�걾�ɼ�ʱ��
	private String Requisition_person; //������
	private String Age_input; //����
	private String Clinical_diagnoses_name; //�ٴ��������
	private String Incept_time; //����ʱ��
	private String Check_time; //���ʱ��
	private String Inspection_person; //�����
	private String Check_person; //�����
	private String Xh; //���
	private String Chinese_name; //��Ŀ����
	private String Quantitative_result; //�������
	private String Test_item_reference; //������Ŀ�ο�ֵ
	private String Test_item_unit; //������Ŀ��λ

	private String PacsID; //Ӱ��ID
	private String PacsCode; //Ӱ����Ŀ����
	private String PacsNam; //Ӱ����Ŀ����
	private String AppDept; //�������
	private String AppDoctor; //����ҽʦ
	private String Age; //����
	private String YinYang; //��������
	private String PatientId; //����ID
	private String Source; //��Դ
	private String R_date; //�������
	private String R_time; //���ʱ��
	private String ReportDate; //��������
	private String ReportTime; //����ʱ��
	private String ReportDoctor; //����ҽʦ
	private String CheckDate; //�������
	private String CheckTime; //���ʱ��
	private String CheckDoctor; //���ҽʦ
	private String Symptom; //֢״
	private String Conclusion; //����
	private String Impression; //ӡ��
	private String BodyPart; //��λ
	
	private String RegStatus; //�Һ�״̬    1-û�нɷ���Ϣ�ĹҺš�2-���ɷ���Ϣ�ĹҺš�3-�ѽɷ���Ϣ�ĹҺ�
	
	private String BusinessFrom;
	
	private String OrderNo;  //������
	private String CardId; //���￨��
	private String BussType; //ҵ������
	private String PayType; //֧������
	private String OrderTime; //����ʱ��
	private String BussTime; //ҵ����ʱ��
	private String PayTime; //֧��ʱ��
	private String PayMoney; //֧��Ǯ��
	private String CurrentBalance; //�����
	private String BussinessFlg; //�������� 1.��  2 ��
	private String BussinessCode; //�ɷѵ���
	private String RegDate; //�ɷѵ���
	
	private String IDNO;
	private String YBCard;
	private String RegCode;
	private String InsureType;
	private String ReceiptNO;
	




	private String BilDate;
	private String FeeTypeDesc;
	private String OrderDesc;
	private String UnitDesc;
	private String OwnPrice;
	private String DosageQty;
	private String TotAmt;
	private String ExecDept;
	private String ExecDate;
	private String TotAmtDay;
	private String SignType;
	
	//ҽ��------------------------------------------------begin
	private String InsurePay;   //ҽ��֧��
	private String AccountPay;  //�����˻�֧��
	private String CashPay;  //����ʵ��֧��
	private String InsureSequenceNo;  //ҽ��˳���
	private String QueueNo;  //�ŶӺ�
	private String VisitAddress;  //�����ַ
	
	
	private List<Detail> Details;
	
	
	
	public String getSignType() {
		return SignType;
	}
	public void setSignType(String signType) {
		SignType = signType;
	}
	public List<Detail> getDetails() {
		return Details;
	}
	public void setDetails(List<Detail> details) {
		Details = details;
	}
	public String getReceiptNO() {
		return ReceiptNO;
	}
	public void setReceiptNO(String receiptNO) {
		ReceiptNO = receiptNO;
	}
	public String getInsureType() {
		return InsureType;
	}
	public void setInsureType(String insureType) {
		InsureType = insureType;
	}
	public String getIDNO() {
		return IDNO;
	}
	public void setIDNO(String iDNO) {
		IDNO = iDNO;
	}
	public String getYBCard() {
		return YBCard;
	}
	public void setYBCard(String yBCard) {
		YBCard = yBCard;
	}
	public String getRegCode() {
		return RegCode;
	}
	public void setRegCode(String regCode) {
		RegCode = regCode;
	}
	public String getRegDate() {
		return RegDate;
	}
	public void setRegDate(String regDate) {
		RegDate = regDate;
	}
	public String getBussinessFlg() {
		return BussinessFlg;
	}
	public void setBussinessFlg(String bussinessFlg) {
		BussinessFlg = bussinessFlg;
	}
	public String getBussinessCode() {
		return BussinessCode;
	}
	public void setBussinessCode(String bussinessCode) {
		BussinessCode = bussinessCode;
	}
	public String getTest_order_code() {
		return Test_order_code;
	}
	public void setTest_order_code(String testOrderCode) {
		Test_order_code = testOrderCode;
	}
	public String getPacsCode() {
		return PacsCode;
	}
	public void setPacsCode(String pacsCode) {
		PacsCode = pacsCode;
	}
	public String getCurrentBalance() {
		return CurrentBalance;
	}
	public void setCurrentBalance(String currentBalance) {
		CurrentBalance = currentBalance;
	}
	public String getPayMoney() {
		return PayMoney;
	}
	public void setPayMoney(String payMoney) {
		PayMoney = payMoney;
	}
	public String getBussType() {
		return BussType;
	}
	public void setBussType(String bussType) {
		BussType = bussType;
	}
	public String getPayType() {
		return PayType;
	}
	public void setPayType(String payType) {
		PayType = payType;
	}
	public String getOrderTime() {
		return OrderTime;
	}
	public void setOrderTime(String orderTime) {
		OrderTime = orderTime;
	}
	public String getBussTime() {
		return BussTime;
	}
	public void setBussTime(String bussTime) {
		BussTime = bussTime;
	}
	public String getPayTime() {
		return PayTime;
	}
	public void setPayTime(String payTime) {
		PayTime = payTime;
	}
	public String getCardId() {
		return CardId;
	}
	public void setCardId(String cardId) {
		CardId = cardId;
	}
	public String getOrderNo() {
		return OrderNo;
	}
	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}
	public String getBusinessFrom() {
		return BusinessFrom;
	}
	public void setBusinessFrom(String businessFrom) {
		BusinessFrom = businessFrom;
	}
	public String getRegStatus() {
		return RegStatus;
	}
	public void setRegStatus(String regStatus) {
		RegStatus = regStatus;
	}
	public String getPacsID() {
		return PacsID;
	}
	public void setPacsID(String pacsID) {
		PacsID = pacsID;
	}
	public String getPacsNam() {
		return PacsNam;
	}
	public void setPacsNam(String pacsNam) {
		PacsNam = pacsNam;
	}
	public String getAppDept() {
		return AppDept;
	}
	public void setAppDept(String appDept) {
		AppDept = appDept;
	}
	public String getAppDoctor() {
		return AppDoctor;
	}
	public void setAppDoctor(String appDoctor) {
		AppDoctor = appDoctor;
	}
	public String getAge() {
		return Age;
	}
	public void setAge(String age) {
		Age = age;
	}
	public String getYinYang() {
		return YinYang;
	}
	public void setYinYang(String yinYang) {
		YinYang = yinYang;
	}
	public String getPatientId() {
		return PatientId;
	}
	public void setPatientId(String patientId) {
		PatientId = patientId;
	}
	public String getSource() {
		return Source;
	}
	public void setSource(String source) {
		Source = source;
	}
	public String getR_date() {
		return R_date;
	}
	public void setR_date(String rDate) {
		R_date = rDate;
	}
	public String getR_time() {
		return R_time;
	}
	public void setR_time(String rTime) {
		R_time = rTime;
	}
	public String getReportDate() {
		return ReportDate;
	}
	public void setReportDate(String reportDate) {
		ReportDate = reportDate;
	}
	public String getReportTime() {
		return ReportTime;
	}
	public void setReportTime(String reportTime) {
		ReportTime = reportTime;
	}
	public String getReportDoctor() {
		return ReportDoctor;
	}
	public void setReportDoctor(String reportDoctor) {
		ReportDoctor = reportDoctor;
	}
	public String getCheckDate() {
		return CheckDate;
	}
	public void setCheckDate(String checkDate) {
		CheckDate = checkDate;
	}
	public String getCheckTime() {
		return CheckTime;
	}
	public void setCheckTime(String checkTime) {
		CheckTime = checkTime;
	}
	public String getCheckDoctor() {
		return CheckDoctor;
	}
	public void setCheckDoctor(String checkDoctor) {
		CheckDoctor = checkDoctor;
	}
	public String getSymptom() {
		return Symptom;
	}
	public void setSymptom(String symptom) {
		Symptom = symptom;
	}
	public String getConclusion() {
		return Conclusion;
	}
	public void setConclusion(String conclusion) {
		Conclusion = conclusion;
	}
	public String getImpression() {
		return Impression;
	}
	public void setImpression(String impression) {
		Impression = impression;
	}
	public String getBodyPart() {
		return BodyPart;
	}
	public void setBodyPart(String bodyPart) {
		BodyPart = bodyPart;
	}
	public String getInspection_id() {
		return Inspection_id;
	}
	public void setInspection_id(String inspectionId) {
		Inspection_id = inspectionId;
	}
	public String getTest_order_name() {
		return Test_order_name;
	}
	public void setTest_order_name(String testOrderName) {
		Test_order_name = testOrderName;
	}
	public String getPatient_name() {
		return Patient_name;
	}
	public void setPatient_name(String patientName) {
		Patient_name = patientName;
	}
	public String getSample_class_name() {
		return Sample_class_name;
	}
	public void setSample_class_name(String sampleClassName) {
		Sample_class_name = sampleClassName;
	}
	public String getSample_number() {
		return Sample_number;
	}
	public void setSample_number(String sampleNumber) {
		Sample_number = sampleNumber;
	}
	public String getPatient_sex() {
		return Patient_sex;
	}
	public void setPatient_sex(String patientSex) {
		Patient_sex = patientSex;
	}
	public String getPatient_dept_name() {
		return Patient_dept_name;
	}
	public void setPatient_dept_name(String patientDeptName) {
		Patient_dept_name = patientDeptName;
	}
	public String getSampling_time() {
		return Sampling_time;
	}
	public void setSampling_time(String samplingTime) {
		Sampling_time = samplingTime;
	}
	public String getRequisition_person() {
		return Requisition_person;
	}
	public void setRequisition_person(String requisitionPerson) {
		Requisition_person = requisitionPerson;
	}
	public String getAge_input() {
		return Age_input;
	}
	public void setAge_input(String ageInput) {
		Age_input = ageInput;
	}
	public String getClinical_diagnoses_name() {
		return Clinical_diagnoses_name;
	}
	public void setClinical_diagnoses_name(String clinicalDiagnosesName) {
		Clinical_diagnoses_name = clinicalDiagnosesName;
	}
	public String getIncept_time() {
		return Incept_time;
	}
	public void setIncept_time(String inceptTime) {
		Incept_time = inceptTime;
	}
	public String getCheck_time() {
		return Check_time;
	}
	public void setCheck_time(String checkTime) {
		Check_time = checkTime;
	}
	public String getInspection_person() {
		return Inspection_person;
	}
	public void setInspection_person(String inspectionPerson) {
		Inspection_person = inspectionPerson;
	}
	public String getCheck_person() {
		return Check_person;
	}
	public void setCheck_person(String checkPerson) {
		Check_person = checkPerson;
	}
	public String getXh() {
		return Xh;
	}
	public void setXh(String xh) {
		Xh = xh;
	}
	public String getChinese_name() {
		return Chinese_name;
	}
	public void setChinese_name(String chineseName) {
		Chinese_name = chineseName;
	}
	public String getQuantitative_result() {
		return Quantitative_result;
	}
	public void setQuantitative_result(String quantitativeResult) {
		Quantitative_result = quantitativeResult;
	}
	public String getTest_item_reference() {
		return Test_item_reference;
	}
	public void setTest_item_reference(String testItemReference) {
		Test_item_reference = testItemReference;
	}
	public String getTest_item_unit() {
		return Test_item_unit;
	}
	public void setTest_item_unit(String testItemUnit) {
		Test_item_unit = testItemUnit;
	}
	public String getUseDays() {
		return UseDays;
	}
	public void setUseDays(String useDays) {
		UseDays = useDays;
	}
	public String getDailyTimes() {
		return DailyTimes;
	}
	public void setDailyTimes(String dailyTimes) {
		DailyTimes = dailyTimes;
	}
	public String getNumberUnit() {
		return NumberUnit;
	}
	public void setNumberUnit(String numberUnit) {
		NumberUnit = numberUnit;
	}
	public String getUsage() {
		return Usage;
	}
	public void setUsage(String usage) {
		Usage = usage;
	}
	public String getDrugMaker() {
		return DrugMaker;
	}
	public void setDrugMaker(String drugMaker) {
		DrugMaker = drugMaker;
	}
	public String getDrugSender() {
		return DrugSender;
	}
	public void setDrugSender(String drugSender) {
		DrugSender = drugSender;
	}
	public String getDiagnosis() {
		return Diagnosis;
	}
	public void setDiagnosis(String diagnosis) {
		Diagnosis = diagnosis;
	}
	public String getInputTime() {
		return InputTime;
	}
	public void setInputTime(String inputTime) {
		InputTime = inputTime;
	}
	public String getPrescriptionName() {
		return PrescriptionName;
	}
	public void setPrescriptionName(String prescriptionName) {
		PrescriptionName = prescriptionName;
	}
	public String getIsSend() {
		return IsSend;
	}
	public void setIsSend(String isSend) {
		IsSend = isSend;
	}
	public String getTotalMoney() {
		return TotalMoney;
	}
	public void setTotalMoney(String totalMoney) {
		TotalMoney = totalMoney;
	}
	public String getHISRegID() {
		return HISRegID;
	}
	public void setHISRegID(String hISRegID) {
		HISRegID = hISRegID;
	}
	public String getOpTime() {
		return OpTime;
	}
	public void setOpTime(String opTime) {
		OpTime = opTime;
	}
	public String getOpName() {
		return OpName;
	}
	public void setOpName(String opName) {
		OpName = opName;
	}
	public String getReceiptCode() {
		return ReceiptCode;
	}
	public void setReceiptCode(String receiptCode) {
		ReceiptCode = receiptCode;
	}
	public String getTipMessages() {
		return TipMessages;
	}
	public void setTipMessages(String tipMessages) {
		TipMessages = tipMessages;
	}
	public String getPrescriptionCode() {
		return PrescriptionCode;
	}
	public void setPrescriptionCode(String prescriptionCode) {
		PrescriptionCode = prescriptionCode;
	}
	public String getPrescriptionType() {
		return PrescriptionType;
	}
	public void setPrescriptionType(String prescriptionType) {
		PrescriptionType = prescriptionType;
	}
	public String getDrugRoomName() {
		return DrugRoomName;
	}
	public void setDrugRoomName(String drugRoomName) {
		DrugRoomName = drugRoomName;
	}
	public String getExecuteDeptName() {
		return ExecuteDeptName;
	}
	public void setExecuteDeptName(String executeDeptName) {
		ExecuteDeptName = executeDeptName;
	}
	public String getFeeName() {
		return FeeName;
	}
	public void setFeeName(String feeName) {
		FeeName = feeName;
	}
	public String getTotalAmount() {
		return TotalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		TotalAmount = totalAmount;
	}
	public String getFeeDate() {
		return FeeDate;
	}
	public void setFeeDate(String feeDate) {
		FeeDate = feeDate;
	}
	public String getItemCode() {
		return ItemCode;
	}
	public void setItemCode(String itemCode) {
		ItemCode = itemCode;
	}
	public String getItemName() {
		return ItemName;
	}
	public void setItemName(String itemName) {
		ItemName = itemName;
	}
	public String getItemSpecs() {
		return ItemSpecs;
	}
	public void setItemSpecs(String itemSpecs) {
		ItemSpecs = itemSpecs;
	}
	public String getDoseNoce() {
		return DoseNoce;
	}
	public void setDoseNoce(String doseNoce) {
		DoseNoce = doseNoce;
	}
	public String getDoseUnit() {
		return DoseUnit;
	}
	public void setDoseUnit(String doseUnit) {
		DoseUnit = doseUnit;
	}
	public String getNumber() {
		return Number;
	}
	public void setNumber(String number) {
		Number = number;
	}
	public String getPrePrice() {
		return PrePrice;
	}
	public void setPrePrice(String prePrice) {
		PrePrice = prePrice;
	}
	public String getPriceUnit() {
		return PriceUnit;
	}
	public void setPriceUnit(String priceUnit) {
		PriceUnit = priceUnit;
	}
	public String getCost() {
		return Cost;
	}
	public void setCost(String cost) {
		Cost = cost;
	}
	public String getInsureTypeCode() {
		return InsureTypeCode;
	}
	public void setInsureTypeCode(String insureTypeCode) {
		InsureTypeCode = insureTypeCode;
	}
	public String getMtTypeCode() {
		return MtTypeCode;
	}
	public void setMtTypeCode(String mtTypeCode) {
		MtTypeCode = mtTypeCode;
	}
	public String getHISPatientID() {
		return HISPatientID;
	}
	public void setHISPatientID(String hISPatientID) {
		HISPatientID = hISPatientID;
	}
	public String getPeriod() {
		return Period;
	}
	public void setPeriod(String period) {
		Period = period;
	}
	public String getDoctorProfCode() {
		return DoctorProfCode;
	}
	public void setDoctorProfCode(String doctorProfCode) {
		DoctorProfCode = doctorProfCode;
	}
	public String getBusinessSN() {
		return BusinessSN;
	}
	public void setBusinessSN(String businessSN) {
		BusinessSN = businessSN;
	}
	public String getMoney() {
		return Money;
	}
	public void setMoney(String money) {
		Money = money;
	}
	public String getTraceTime() {
		return TraceTime;
	}
	public void setTraceTime(String traceTime) {
		TraceTime = traceTime;
	}
	public String getInsurePay() {
		return InsurePay;
	}
	public void setInsurePay(String insurePay) {
		InsurePay = insurePay;
	}
	public String getAccountPay() {
		return AccountPay;
	}
	public void setAccountPay(String accountPay) {
		AccountPay = accountPay;
	}
	public String getCashPay() {
		return CashPay;
	}
	public void setCashPay(String cashPay) {
		CashPay = cashPay;
	}
	public String getInsureSequenceNo() {
		return InsureSequenceNo;
	}
	public void setInsureSequenceNo(String insureSequenceNo) {
		InsureSequenceNo = insureSequenceNo;
	}
	public String getQueueNo() {
		return QueueNo;
	}
	public void setQueueNo(String queueNo) {
		QueueNo = queueNo;
	}
	public String getVisitAddress() {
		return VisitAddress;
	}
	public void setVisitAddress(String visitAddress) {
		VisitAddress = visitAddress;
	}
	
	//ҽ��------------------------------------------------end
	
	public String getMtCode() {
		return MtCode;
	}
	public void setMtCode(String mtCode) {
		MtCode = mtCode;
	}
	public String getDeptCode() {
		return DeptCode;
	}
	public void setDeptCode(String deptCode) {
		DeptCode = deptCode;
	}
	public String getDeptDesc() {
		return DeptDesc;
	}
	public void setDeptDesc(String deptDesc) {
		DeptDesc = deptDesc;
	}
	public String getParentCode() {
		return ParentCode;
	}
	public void setParentCode(String parentCode) {
		ParentCode = parentCode;
	}
	public String getOrderNum() {
		return OrderNum;
	}
	public void setOrderNum(String orderNum) {
		OrderNum = orderNum;
	}
	public String getDeptType() {
		return DeptType;
	}
	public void setDeptType(String deptType) {
		DeptType = deptType;
	}
	public String getDeptAddress() {
		return DeptAddress;
	}
	public void setDeptAddress(String deptAddress) {
		DeptAddress = deptAddress;
	}
	public String getIsEnabled() {
		return IsEnabled;
	}
	public void setIsEnabled(String isEnabled) {
		IsEnabled = isEnabled;
	}
	public String getDoctorCode() {
		return DoctorCode;
	}
	public void setDoctorCode(String doctorCode) {
		DoctorCode = doctorCode;
	}
	public String getDoctorLevel() {
		return DoctorLevel;
	}
	public void setDoctorLevel(String doctorLevel) {
		DoctorLevel = doctorLevel;
	}
	public String getDoctorProf() {
		return DoctorProf;
	}
	public void setDoctorProf(String doctorProf) {
		DoctorProf = doctorProf;
	}
	public String getSex() {
		return Sex;
	}
	public void setSex(String sex) {
		Sex = sex;
	}
	public String getIDCardNO() {
		return IDCardNO;
	}
	public void setIDCardNO(String iDCardNO) {
		IDCardNO = iDCardNO;
	}
	public String getInsureDocID() {
		return InsureDocID;
	}
	public void setInsureDocID(String insureDocID) {
		InsureDocID = insureDocID;
	}
	public String getPhotourl() {
		return Photourl;
	}
	public void setPhotourl(String photourl) {
		Photourl = photourl;
	}
	public String getVisitTypeCode() {
		return VisitTypeCode;
	}
	public void setVisitTypeCode(String visitTypeCode) {
		VisitTypeCode = visitTypeCode;
	}
	public String getTotalFee() {
		return TotalFee;
	}
	public void setTotalFee(String totalFee) {
		TotalFee = totalFee;
	}
	public String getRegFee() {
		return RegFee;
	}
	public void setRegFee(String regFee) {
		RegFee = regFee;
	}
	public String getInspectFee() {
		return InspectFee;
	}
	public void setInspectFee(String inspectFee) {
		InspectFee = inspectFee;
	}
	public String getVisitCode() {
		return VisitCode;
	}
	public void setVisitCode(String visitCode) {
		VisitCode = visitCode;
	}
	public String getWeek() {
		return Week;
	}
	public void setWeek(String week) {
		Week = week;
	}
	public String getTotalNumber() {
		return TotalNumber;
	}
	public void setTotalNumber(String totalNumber) {
		TotalNumber = totalNumber;
	}
	public String getRemainsNumber() {
		return RemainsNumber;
	}
	public void setRemainsNumber(String remainsNumber) {
		RemainsNumber = remainsNumber;
	}
	public String getIsAppoint() {
		return IsAppoint;
	}
	public void setIsAppoint(String isAppoint) {
		IsAppoint = isAppoint;
	}
	public String getStopType() {
		return StopType;
	}
	public void setStopType(String stopType) {
		StopType = stopType;
	}


	
	public String getTotalNum() {
		return TotalNum;
	}
	public void setTotalNum(String totalNum) {
		TotalNum = totalNum;
	}
	public String getRemainsNum() {
		return RemainsNum;
	}
	public void setRemainsNum(String remainsNum) {
		RemainsNum = remainsNum;
	}
	public String getMtRemainsNum() {
		return MtRemainsNum;
	}
	public void setMtRemainsNum(String mtRemainsNum) {
		MtRemainsNum = mtRemainsNum;
	}
	public String getPatientID() {
		return PatientID;
	}
	public void setPatientID(String patientID) {
		PatientID = patientID;
	}
	public String getCureCardNo() {
		return CureCardNo;
	}
	public void setCureCardNo(String cureCardNo) {
		CureCardNo = cureCardNo;
	}
	public String getIDCardNo() {
		return IDCardNo;
	}
	public void setIDCardNo(String iDCardNo) {
		IDCardNo = iDCardNo;
	}
	public String getGIDCardNo() {
		return GIDCardNo;
	}
	public void setGIDCardNo(String gIDCardNo) {
		GIDCardNo = gIDCardNo;
	}
	public String getMedCardNo() {
		return MedCardNo;
	}
	public void setMedCardNo(String medCardNo) {
		MedCardNo = medCardNo;
	}
	public String getHealthCardNo() {
		return HealthCardNo;
	}
	public void setHealthCardNo(String healthCardNo) {
		HealthCardNo = healthCardNo;
	}
	public String getPatientName() {
		return PatientName;
	}
	public void setPatientName(String patientName) {
		PatientName = patientName;
	}
	public String getPatientAge() {
		return PatientAge;
	}
	public void setPatientAge(String patientAge) {
		PatientAge = patientAge;
	}
	public String getPatientSex() {
		return PatientSex;
	}
	public void setPatientSex(String patientSex) {
		PatientSex = patientSex;
	}
	public String getBirthday() {
		return Birthday;
	}
	public void setBirthday(String birthday) {
		Birthday = birthday;
	}
	public String getPhoneNo() {
		return PhoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		PhoneNo = phoneNo;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getRegID() {
		return RegID;
	}
	public void setRegID(String regID) {
		RegID = regID;
	}
	public String getRegTime() {
		return RegTime;
	}
	public void setRegTime(String regTime) {
		RegTime = regTime;
	}
	public String getDoctorName() {
		return DoctorName;
	}
	public void setDoctorName(String doctorName) {
		DoctorName = doctorName;
	}
	public String getDeptName() {
		return DeptName;
	}
	public void setDeptName(String deptName) {
		DeptName = deptName;
	}
	public String getSxwMeaning() {
		return SxwMeaning;
	}
	public void setSxwMeaning(String sxwMeaning) {
		SxwMeaning = sxwMeaning;
	}
	public String getVisitTypeName() {
		return VisitTypeName;
	}
	public void setVisitTypeName(String visitTypeName) {
		VisitTypeName = visitTypeName;
	}
	public String getVisitDate() {
		return VisitDate;
	}
	public void setVisitDate(String visitDate) {
		VisitDate = visitDate;
	}
	public String getSxw() {
		return Sxw;
	}
	public void setSxw(String sxw) {
		Sxw = sxw;
	}
	public String getPeriodCode() {
		return PeriodCode;
	}
	public void setPeriodCode(String periodCode) {
		PeriodCode = periodCode;
	}
	public String getPeriodMeaning() {
		return PeriodMeaning;
	}
	public void setPeriodMeaning(String periodMeaning) {
		PeriodMeaning = periodMeaning;
	}
	public String getRemainingNum() {
		return RemainingNum;
	}
	public void setRemainingNum(String remainingNum) {
		RemainingNum = remainingNum;
	}
	public String getComment() {
		return Comment;
	}
	public void setComment(String comment) {
		Comment = comment;
	}
	public String getAppointCode() {
		return AppointCode;
	}
	public void setAppointCode(String appointCode) {
		AppointCode = appointCode;
	}
	public String getAppointType() {
		return AppointType;
	}
	public void setAppointType(String appointType) {
		AppointType = appointType;
	}
	public String getAppointStatus() {
		return AppointStatus;
	}
	public void setAppointStatus(String appointStatus) {
		AppointStatus = appointStatus;
	}
	public String getAppointDate() {
		return AppointDate;
	}
	public void setAppointDate(String appointDate) {
		AppointDate = appointDate;
	}
	
	
	public String getBalance() {
		return Balance;
	}
	public void setBalance(String balance) {
		Balance = balance;
	}
	public String getCardStatus() {
		return CardStatus;
	}
	public void setCardStatus(String cardStatus) {
		CardStatus = cardStatus;
	}
	public String getHISAppointCode() {
		return HISAppointCode;
	}
	public void setHISAppointCode(String hISAppointCode) {
		HISAppointCode = hISAppointCode;
	}
	public String getAppointStatusMeaning() {
		return AppointStatusMeaning;
	}
	public void setAppointStatusMeaning(String appointStatusMeaning) {
		AppointStatusMeaning = appointStatusMeaning;
	}
	public String getAppointTime() {
		return AppointTime;
	}
	public void setAppointTime(String appointTime) {
		AppointTime = appointTime;
	}
	public String getAPW() {
		return APW;
	}
	public void setAPW(String aPW) {
		APW = aPW;
	}
	public String getAPWMeaning() {
		return APWMeaning;
	}
	public void setAPWMeaning(String aPWMeaning) {
		APWMeaning = aPWMeaning;
	}
	public String getIsEmergency() {
		return IsEmergency;
	}
	public void setIsEmergency(String isEmergency) {
		IsEmergency = isEmergency;
	}
	public String getIsCost() {
		return IsCost;
	}
	public void setIsCost(String isCost) {
		IsCost = isCost;
	}
	public String getBilDate() {
		return BilDate;
	}
	public void setBilDate(String biiDate) {
		BilDate = biiDate;
	}
	public String getFeeTypeDesc() {
		return FeeTypeDesc;
	}
	public void setFeeTypeDesc(String feeTypeDesc) {
		FeeTypeDesc = feeTypeDesc;
	}
	public String getOrderDesc() {
		return OrderDesc;
	}
	public void setOrderDesc(String orderDesc) {
		OrderDesc = orderDesc;
	}
	public String getUnitDesc() {
		return UnitDesc;
	}
	public void setUnitDesc(String unitDesc) {
		UnitDesc = unitDesc;
	}
	public String getOwnPrice() {
		return OwnPrice;
	}
	public void setOwnPrice(String ownPrice) {
		OwnPrice = ownPrice;
	}
	public String getDosageQty() {
		return DosageQty;
	}
	public void setDosageQty(String dosageQty) {
		DosageQty = dosageQty;
	}
	public String getTotAmt() {
		return TotAmt;
	}
	public void setTotAmt(String totAmt) {
		TotAmt = totAmt;
	}
	public String getExecDept() {
		return ExecDept;
	}
	public void setExecDept(String execDept) {
		ExecDept = execDept;
	}
	public String getExecDate() {
		return ExecDate;
	}
	public void setExecDate(String execDate) {
		ExecDate = execDate;
	}
	public String getTotAmtDay() {
		return TotAmtDay;
	}
	public void setTotAmtDay(String totAmtDay) {
		TotAmtDay = totAmtDay;
	}
	
	

}
