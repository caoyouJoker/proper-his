package jdo.device;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Title: 叫号接口当前叫号对象
 * </p>
 * 
 * <p>
 * Description: 叫号接口当前叫号对象
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
 * @author wangb 2017.6.16
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CurrentCamsInfo")
public class CallNoXmlCurrentCamsInfo {
	
	/**
	 * 病案号
	 */
	private String PatientID;
	
	/**
	 * 病患姓名
	 */
	private String Name;
	
	/**
	 * 性别
	 */
	private String Sex;
	
	/**
	 * 出生年月
	 */
	private String Birthday;
	
	/**
	 * 科室编码
	 */
	private String DeptCode;
	
	/**
	 * 科室名称
	 */
	private String DeptName;
	
	/**
	 * 号别编码
	 */
	private String ClinicID;
	
	/**
	 * 号别名称
	 */
	private String ClinicName;
	
	/**
	 * 医生工号
	 */
	private String DoctorID;
	
	/**
	 * 医生姓名
	 */
	private String DoctorName;
	
	/**
	 * 就诊日期
	 */
	private String ClinicDate;
	
	/**
	 * 时间段
	 */
	private String TimeDesc;
	
	/**
	 * 序号
	 */
	private String SerialNum;

	/**
	 * 取得病案号
	 * @return 病案号
	 */
	public String getPatientID() {
		return PatientID;
	}

	/**
	 * 设定病案号
	 * @param patientID 病案号
	 */
	public void setPatientID(String patientID) {
		PatientID = patientID;
	}

	/**
	 * 取得病患姓名
	 * @return 病患姓名
	 */
	public String getName() {
		return Name;
	}

	/**
	 * 设定病患姓名
	 * @param name 病患姓名
	 */
	public void setName(String name) {
		Name = name;
	}

	/**
	 * 取得性别
	 * @return 性别
	 */
	public String getSex() {
		return Sex;
	}

	/**
	 * 设定性别
	 * @param sex 性别
	 */
	public void setSex(String sex) {
		Sex = sex;
	}

	/**
	 * 取得出生年月
	 * @return 出生年月
	 */
	public String getBirthday() {
		return Birthday;
	}

	/**
	 * 设定出生年月
	 * @param birthday 出生年月
	 */
	public void setBirthday(String birthday) {
		Birthday = birthday;
	}

	/**
	 * 取得科室编码
	 * @return 科室编码
	 */
	public String getDeptCode() {
		return DeptCode;
	}

	/**
	 * 设定科室编码
	 * @param deptCode 科室编码
	 */
	public void setDeptCode(String deptCode) {
		DeptCode = deptCode;
	}

	/**
	 * 取得科室名称
	 * @return 科室名称
	 */
	public String getDeptName() {
		return DeptName;
	}

	/**
	 * 设定科室名称
	 * @param deptName 科室名称
	 */
	public void setDeptName(String deptName) {
		DeptName = deptName;
	}

	/**
	 * 取得号别编码
	 * @return 号别编码
	 */
	public String getClinicID() {
		return ClinicID;
	}

	/**
	 * 设定号别编码
	 * @param clinicID 号别编码
	 */
	public void setClinicID(String clinicID) {
		ClinicID = clinicID;
	}

	/**
	 * 取得号别名称
	 * @return 号别名称
	 */
	public String getClinicName() {
		return ClinicName;
	}

	/**
	 * 设定号别名称
	 * @param clinicName 号别名称
	 */
	public void setClinicName(String clinicName) {
		ClinicName = clinicName;
	}

	/**
	 * 取得医生工号
	 * @return 医生工号
	 */
	public String getDoctorID() {
		return DoctorID;
	}

	/**
	 * 设定医生工号
	 * @param doctorID 医生工号
	 */
	public void setDoctorID(String doctorID) {
		DoctorID = doctorID;
	}

	/**
	 * 取得医生姓名
	 * @return 医生姓名
	 */
	public String getDoctorName() {
		return DoctorName;
	}

	/**
	 * 设定医生姓名
	 * @param doctorName 医生姓名
	 */
	public void setDoctorName(String doctorName) {
		DoctorName = doctorName;
	}

	/**
	 * 取得就诊日期
	 * @return 就诊日期
	 */
	public String getClinicDate() {
		return ClinicDate;
	}

	/**
	 * 设定就诊日期
	 * @param clinicDate 就诊日期
	 */
	public void setClinicDate(String clinicDate) {
		ClinicDate = clinicDate;
	}

	/**
	 * 取得时间段
	 * @return 时间段
	 */
	public String getTimeDesc() {
		return TimeDesc;
	}

	/**
	 * 设定时间段
	 * @param timeDesc 时间段
	 */
	public void setTimeDesc(String timeDesc) {
		TimeDesc = timeDesc;
	}

	/**
	 * 取得序号
	 * @return 序号
	 */
	public String getSerialNum() {
		return SerialNum;
	}

	/**
	 * 设定序号
	 * @param serialNum 序号
	 */
	public void setSerialNum(String serialNum) {
		SerialNum = serialNum;
	}
}
