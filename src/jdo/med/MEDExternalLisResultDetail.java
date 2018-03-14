package jdo.med;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Title: 外送检验结果明细对象
 * </p>
 * 
 * <p>
 * Description: 外送检验结果明细对象
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2017.1.19
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Table")
public class MEDExternalLisResultDetail {
	/**
	 * 迪安条码
	 */
	private String BARCODE;
	/**
	 * 送检单位
	 */
	private String SAMPLEFROM;
	/**
	 * 样本类型
	 */
	private String SAMPLETYPE;
	/**
	 * 采样时间
	 */
	private String COLLECTDDATE;
	/**
	 * 提交时间
	 */
	private String SUBMITDATE;
	/**
	 * 主项编码
	 */
	private String TESTCODE;
	/**
	 * 发布时间
	 */
	private String APPRDATE;
	/**
	 * 所属公司
	 */
	private String DEPT;
	/**
	 * 科室
	 */
	private String SERVGRP;
	/**
	 * 检测人
	 */
	private String USRNAM;
	/**
	 * 审核人
	 */
	private String APPRVEDBY;
	/**
	 * 病人姓名
	 */
	private String PATIENTNAME;
	/**
	 * 门诊号
	 */
	private String CLINICID;
	/**
	 * 床位
	 */
	private String BEDNO;
	/**
	 * 病人类型（门诊/体检/住院）
	 */
	private String PATIENTCATEGORY;
	/**
	 * 送检医生
	 */
	private String DOCTOR;
	/**
	 * 性别
	 */
	private String SEX;
	/**
	 * 年龄
	 */
	private String AGE;
	/**
	 * 年龄单位
	 */
	private String AGEUNIT;
	/**
	 * 项目名称
	 */
	private String SINONYM;
	/**
	 * 项目简称
	 */
	private String SHORTNAME;
	/**
	 * 单位
	 */
	private String UNITS;
	/**
	 * 结果
	 */
	private String FINAL;
	/**
	 * 分析项目
	 */
	private String ANALYTE;
	/**
	 * 参考范围
	 */
	private String DISPLOWHIGH;
	/**
	 * 女参考值
	 */
	private String DISPLOWHIGH_F;
	/**
	 * 男参考值
	 */
	private String DISPLOWHIGH_M;
	/**
	 * 结果异常标志位（H L）
	 */
	private String RN10;
	/**
	 * 对接项目ID
	 */
	private String S;
	/**
	 * 项目名称英文
	 */
	private String SYNONIM_EN;
	/**
	 * 结果异常标记位（↑↓）
	 */
	private String RN20;
	/**
	 * 备注
	 */
	private String COMMENTS;
	/**
	 * 正常范围下限
	 */
	private String LOWB;
	/**
	 * 正常范围上限
	 */
	private String HIGHB;
	
	/**
	 * 取得迪安条码
	 * @return 迪安条码
	 */
	public String getBarCode() {
		return BARCODE;
	}
	/**
	 * 设定迪安条码
	 * @param barCode 迪安条码
	 */
	public void setBarCode(String barCode) {
		BARCODE = barCode;
	}
	/**
	 * 取得送检单位
	 * @return 送检单位
	 */
	public String getSampleFrom() {
		return SAMPLEFROM;
	}
	/**
	 * 设定送检单位
	 * @param sampleFrom 送检单位
	 */
	public void setSampleFrom(String sampleFrom) {
		SAMPLEFROM = sampleFrom;
	}
	/**
	 * 取得样本类型
	 * @return 样本类型
	 */
	public String getSampleType() {
		return SAMPLETYPE;
	}
	/**
	 * 设定样本类型
	 * @param sampleType 样本类型
	 */
	public void setSampleType(String sampleType) {
		SAMPLETYPE = sampleType;
	}
	/**
	 * 取得采样时间
	 * @return 采样时间
	 */
	public String getCollectDate() {
		return COLLECTDDATE;
	}
	/**
	 * 设定采样时间
	 * @param collectDate 采样时间
	 */
	public void setCollectDate(String collectDate) {
		COLLECTDDATE = collectDate;
	}
	/**
	 * 取得提交时间
	 * @return 提交时间
	 */
	public String getSubmitData() {
		return SUBMITDATE;
	}
	/**
	 * 设定提交时间
	 * @param submitData 提交时间
	 */
	public void setSubmitData(String submitData) {
		SUBMITDATE = submitData;
	}
	/**
	 * 取得主项编码
	 * @return 主项编码
	 */
	public String getTestCode() {
		return TESTCODE;
	}
	/**
	 * 设定主项编码
	 * @param testCode 主项编码
	 */
	public void setTestCode(String testCode) {
		TESTCODE = testCode;
	}
	/**
	 * 取得发布时间
	 * @return 发布时间
	 */
	public String getApprDate() {
		return APPRDATE;
	}
	/**
	 * 设定发布时间
	 * @param apprDate 发布时间
	 */
	public void setApprDate(String apprDate) {
		APPRDATE = apprDate;
	}
	/**
	 * 取得所属公司
	 * @return 所属公司
	 */
	public String getDept() {
		return DEPT;
	}
	/**
	 * 设定所属公司
	 * @param dept 所属公司
	 */
	public void setDept(String dept) {
		DEPT = dept;
	}
	/**
	 * 取得科室
	 * @return 科室
	 */
	public String getSerGrp() {
		return SERVGRP;
	}
	/**
	 * 设定科室
	 * @param serGrp 科室
	 */
	public void setSerGrp(String serGrp) {
		SERVGRP = serGrp;
	}
	/**
	 * 取得检测人
	 * @return 检测人
	 */
	public String getUsrNam() {
		return USRNAM;
	}
	/**
	 * 设定检测人
	 * @param usrNam 检测人
	 */
	public void setUsrNam(String usrNam) {
		USRNAM = usrNam;
	}
	/**
	 * 取得审核人
	 * @return 审核人
	 */
	public String getApprvedBy() {
		return APPRVEDBY;
	}
	/**
	 * 设定审核人
	 * @param apprvedBy 审核人
	 */
	public void setApprvedBy(String apprvedBy) {
		APPRVEDBY = apprvedBy;
	}
	/**
	 * 取得病患姓名
	 * @return 病患姓名
	 */
	public String getPatientName() {
		return PATIENTNAME;
	}
	/**
	 * 设定病患姓名
	 * @param patientName 病患姓名
	 */
	public void setPatientName(String patientName) {
		PATIENTNAME = patientName;
	}
	/**
	 * 取得门诊号
	 * @return 门诊号
	 */
	public String getClinicid() {
		return CLINICID;
	}
	/**
	 * 设定门诊号
	 * @param clinicid 门诊号
	 */
	public void setClinicid(String clinicid) {
		CLINICID = clinicid;
	}
	/**
	 * 取得床位
	 * @return 床位
	 */
	public String getBedNo() {
		return BEDNO;
	}
	/**
	 * 设定床位
	 * @param bedNo 床位
	 */
	public void setBedNo(String bedNo) {
		BEDNO = bedNo;
	}
	/**
	 * 取得病人类型（门诊/体检/住院）
	 * @return 病人类型（门诊/体检/住院）
	 */
	public String getPatientCategory() {
		return PATIENTCATEGORY;
	}
	/**
	 * 设定病人类型（门诊/体检/住院）
	 * @param patientCategory 病人类型（门诊/体检/住院）
	 */
	public void setPatientCategory(String patientCategory) {
		PATIENTCATEGORY = patientCategory;
	}
	/**
	 * 取得送检医生
	 * @return 送检医生
	 */
	public String getDoctor() {
		return DOCTOR;
	}
	/**
	 * 设定送检医生
	 * @param doctor 送检医生
	 */
	public void setDoctor(String doctor) {
		DOCTOR = doctor;
	}
	/**
	 * 取得性别
	 * @return 性别
	 */
	public String getSex() {
		return SEX;
	}
	/**
	 * 设定性别
	 * @param sex 性别
	 */
	public void setSex(String sex) {
		SEX = sex;
	}
	/**
	 * 取得年龄
	 * @return 年龄
	 */
	public String getAge() {
		return AGE;
	}
	/**
	 * 设定年龄
	 * @param age 年龄
	 */
	public void setAge(String age) {
		AGE = age;
	}
	/**
	 * 取得年龄单位
	 * @return 年龄单位
	 */
	public String getAgeUnit() {
		return AGEUNIT;
	}
	/**
	 * 设定年龄单位
	 * @param ageUnit 年龄单位
	 */
	public void setAgeUnit(String ageUnit) {
		AGEUNIT = ageUnit;
	}
	/**
	 * 取得项目名称
	 * @return 项目名称
	 */
	public String getSinonym() {
		return SINONYM;
	}
	/**
	 * 设定项目名称
	 * @param sinonym 项目名称
	 */
	public void setSinonym(String sinonym) {
		SINONYM = sinonym;
	}
	/**
	 * 取得项目简称
	 * @return 项目简称
	 */
	public String getShortName() {
		return SHORTNAME;
	}
	/**
	 * 设定项目简称
	 * @param shortName 项目简称
	 */
	public void setShortName(String shortName) {
		SHORTNAME = shortName;
	}
	/**
	 * 取得单位
	 * @return 单位
	 */
	public String getUnits() {
		return UNITS;
	}
	/**
	 * 设定单位
	 * @param units 单位
	 */
	public void setUnits(String units) {
		UNITS = units;
	}
	/**
	 * 取得结果
	 * @return 结果
	 */
	public String getFinal() {
		return FINAL;
	}
	/**
	 * 设定结果
	 * @param finalValue 结果
	 */
	public void setFinal(String finalValue) {
		FINAL = finalValue;
	}
	/**
	 * 取得分析项目
	 * @return 分析项目
	 */
	public String getAnalyte() {
		return ANALYTE;
	}
	/**
	 * 设定分析项目
	 * @param analyte 分析项目
	 */
	public void setAnalyte(String analyte) {
		ANALYTE = analyte;
	}
	/**
	 * 取得参考范围
	 * @return 参考范围
	 */
	public String getDispLowHigh() {
		return DISPLOWHIGH;
	}
	/**
	 * 设定参考范围
	 * @param dispLowHigh 参考范围
	 */
	public void setDispLowHigh(String dispLowHigh) {
		DISPLOWHIGH = dispLowHigh;
	}
	/**
	 * 取得女参考值
	 * @return 女参考值
	 */
	public String getDispLowHighF() {
		return DISPLOWHIGH_F;
	}
	/**
	 * 设定女参考值
	 * @param dispLowHighF 女参考值
	 */
	public void setDispLowHighF(String dispLowHighF) {
		DISPLOWHIGH_F = dispLowHighF;
	}
	/**
	 * 取得男参考值
	 * @return 男参考值
	 */
	public String getDispLowHighM() {
		return DISPLOWHIGH_M;
	}
	/**
	 * 设定男参考值
	 * @param dispLowHighM 男参考值
	 */
	public void setDispLowHighM(String dispLowHighM) {
		DISPLOWHIGH_M = dispLowHighM;
	}
	/**
	 * 取得结果异常标志位（H L）
	 * @return 结果异常标志位（H L）
	 */
	public String getRn10() {
		return RN10;
	}
	/**
	 * 结果异常标志位（H L）
	 * @param rn10 结果异常标志位（H L）
	 */
	public void setRn10(String rn10) {
		RN10 = rn10;
	}
	/**
	 * 取得对接项目ID
	 * @return 对接项目ID
	 */
	public String getS() {
		return S;
	}
	/**
	 * 设定对接项目ID
	 * @param s 对接项目ID
	 */
	public void setS(String s) {
		S = s;
	}
	/**
	 * 取得项目名称英文
	 * @return 项目名称英文
	 */
	public String getSynonimEn() {
		return SYNONIM_EN;
	}
	/**
	 * 设定项目名称英文
	 * @param synonimEn 项目名称英文
	 */
	public void setSynonimEn(String synonimEn) {
		SYNONIM_EN = synonimEn;
	}
	/**
	 * 取得结果异常标记位（↑↓）
	 * @return 结果异常标记位（↑↓）
	 */
	public String getRn20() {
		return RN20;
	}
	/**
	 * 设定结果异常标记位（↑↓）
	 * @param rn20 结果异常标记位（↑↓）
	 */
	public void setRn20(String rn20) {
		RN20 = rn20;
	}
	/**
	 * 取得备注
	 * @return 备注
	 */
	public String getComments() {
		return COMMENTS;
	}
	/**
	 * 设定备注
	 * @param comments 备注
	 */
	public void setComments(String comments) {
		COMMENTS = comments;
	}
	/**
	 * 取得正常范围下限
	 * @return 正常范围下限
	 */
	public String getLowB() {
		return LOWB;
	}
	/**
	 * 设定正常范围下限
	 * @param lowB 正常范围下限
	 */
	public void setLowB(String lowB) {
		LOWB = lowB;
	}
	/**
	 * 取得正常范围上限
	 * @return 正常范围上限
	 */
	public String getHighB() {
		return HIGHB;
	}
	/**
	 * 设定正常范围上限
	 * @param highB 正常范围上限
	 */
	public void setHighB(String highB) {
		HIGHB = highB;
	}
}
