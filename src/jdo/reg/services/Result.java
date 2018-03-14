package jdo.reg.services;

import java.util.List;

public class Result {
	
	
	private String DeptCode; //科室编号
	private String DeptDesc; //科室描述
	private String ParentCode; //父科室编号
	private String OrderNum; //科室排序
	private String DeptType; //科室类别
	private String DeptAddress; //科室地址
	private String IsEnabled; //是否有效
	
	
	private String DoctorCode; //医生编码
	private String DoctorLevel; //医生级别
	private String DoctorProf; //医生职称
	private String DoctorProfCode; //医生职称编码
	
	private String Sex; //性别
	private String IDCardNO; //身份证号
	private String InsureDocID; //医生医保ID
	private String Photourl; //照片链接

	private String VisitTypeCode; //号别费用编码
	
	private String TotalFee; //总金额
	private String RegFee; //挂号费
	private String InspectFee; //诊察费
	
	
	private String VisitCode; //排班号
	private String Week; //星期
	private String TotalNumber; //总开放号数
	private String RemainsNumber; //剩余号数
	private String IsAppoint; //是否预约
	private String StopType; //停诊类型
	
	private String MtCode; //门特病种编号

	private String TotalNum;   //总放号数
	private String RemainsNum;  //剩余号数
	private String MtRemainsNum;  //门特剩余号数
	
	
	private String PatientID; //患者ID;
	private String CureCardNo; //就诊卡号;
	private String IDCardNo; //身份证号;
	private String GIDCardNo; //监护人身份证号;
	private String MedCardNo; //医保卡号;
	private String HealthCardNo; //健康卡号;
	private String PatientName; //患者姓名;
	private String PatientAge; //患者年龄;
	private String PatientSex; //患者性别;
	private String Birthday; //生日;
	private String PhoneNo; //电话号码;
	private String Address; //住址;
	
	private String Balance;//医疗卡余额
	private String CardStatus;//医疗卡状态
	
	private String RegID; //挂号ID
	private String RegTime; //挂号时间
	private String DoctorName; //医生姓名
	private String DeptName; //科室名称
	private String SxwMeaning; //上下午
	private String VisitTypeName; //号别名称
	
	private String VisitDate; //出诊日期
	private String Sxw; //上下午
	
	private String Period;//时段编号
	private String PeriodCode; //时段编号
	private String PeriodMeaning; //时段含义
	private String RemainingNum; //剩余号数
	
	private String Comment; //备注
	
	
	private String AppointCode; //预约编号
	private String AppointType; //预约方式
	private String AppointStatus; //预约状态
	private String AppointDate; //预约日期
	
	private String HISAppointCode;//HIS中的唯一预约编号
	private String HISPatientID;//HIS中的唯一病案号
	private String AppointStatusMeaning;//预约状态含义
	private String AppointTime;//预约时间
	private String APW;//上下午Code
	private String APWMeaning;//上下午含义
	
	private String IsEmergency;//是否急诊
	private String IsCost;//是否已支付
	
	private String BusinessSN; //交易流水号
	private String Money; //金额
	//private String Balance; //余额
	private String TraceTime; //交易时间
	
	private String InsureTypeCode; //医保类型
	private String MtTypeCode; //门特类型
	
	private String PrescriptionCode; //处方号
	private String PrescriptionType; //处方类型
	private String DrugRoomName; //药房名称
	private String ExecuteDeptName; //执行科室名称
	private String FeeName; //费用名称
	private String TotalAmount; //合计金额
	private String FeeDate; //费用日期
	private String ItemCode; //项目编号
	private String ItemName; //项目名称
	private String ItemSpecs; //规格
	private String DoseNoce; //每次用量
	private String DoseUnit; //用量单位
	private String Number; //数量
	private String PrePrice; //单价
	private String PriceUnit; //价格单位
	private String Cost; //项目金额
	
	private String OpTime; //操作时间
	private String OpName; //操作人
	private String ReceiptCode; //收据编号
	private String TipMessages; //提示信息
	private String HISRegID; //挂号编号
	private String TotalMoney; //总金额
	
	private String IsSend; //是否发药
	
	private String PrescriptionName; //处方名称
	private String DrugMaker; //配药人
	private String DrugSender; //发药人
	private String Diagnosis; //临床诊断
	private String InputTime; //录入时间
	
	private String UseDays; //用药天数
	private String DailyTimes; //每天次数
	private String NumberUnit; //数量单位
	private String Usage; //服用方法
	
	private String Inspection_id; //化验编号
	private String Test_order_code; //化验项目名称
	private String Test_order_name; //化验项目名称
	private String Patient_name; //患者姓名
	private String Sample_class_name; //标本类别名称
	private String Sample_number; //标本编码
	private String Patient_sex; //患者性别
	private String Patient_dept_name; //科室名称
	private String Sampling_time; //标本采集时间
	private String Requisition_person; //申请人
	private String Age_input; //年龄
	private String Clinical_diagnoses_name; //临床诊断名称
	private String Incept_time; //接收时间
	private String Check_time; //审核时间
	private String Inspection_person; //检查人
	private String Check_person; //审核人
	private String Xh; //序号
	private String Chinese_name; //项目名称
	private String Quantitative_result; //量化结果
	private String Test_item_reference; //化验项目参考值
	private String Test_item_unit; //化验项目单位

	private String PacsID; //影像ID
	private String PacsCode; //影像项目名称
	private String PacsNam; //影像项目名称
	private String AppDept; //申请科室
	private String AppDoctor; //申请医师
	private String Age; //年龄
	private String YinYang; //阴性阳性
	private String PatientId; //患者ID
	private String Source; //来源
	private String R_date; //检查日期
	private String R_time; //检查时间
	private String ReportDate; //报告日期
	private String ReportTime; //报告时间
	private String ReportDoctor; //报告医师
	private String CheckDate; //审核日期
	private String CheckTime; //审核时间
	private String CheckDoctor; //审核医师
	private String Symptom; //症状
	private String Conclusion; //结论
	private String Impression; //印象
	private String BodyPart; //部位
	
	private String RegStatus; //挂号状态    1-没有缴费信息的挂号、2-待缴费信息的挂号、3-已缴费信息的挂号
	
	private String BusinessFrom;
	
	private String OrderNo;  //订单号
	private String CardId; //就诊卡号
	private String BussType; //业务类型
	private String PayType; //支付类型
	private String OrderTime; //订单时间
	private String BussTime; //业务发生时间
	private String PayTime; //支付时间
	private String PayMoney; //支付钱数
	private String CurrentBalance; //卡余额
	private String BussinessFlg; //交易类型 1.收  2 退
	private String BussinessCode; //缴费单号
	private String RegDate; //缴费单号
	
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
	
	//医保------------------------------------------------begin
	private String InsurePay;   //医保支付
	private String AccountPay;  //个人账户支付
	private String CashPay;  //个人实际支付
	private String InsureSequenceNo;  //医保顺序号
	private String QueueNo;  //排队号
	private String VisitAddress;  //就诊地址
	
	
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
	
	//医保------------------------------------------------end
	
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
