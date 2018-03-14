package jdo.device;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Title: �кŽӿڵ�ǰ�кŶ���
 * </p>
 * 
 * <p>
 * Description: �кŽӿڵ�ǰ�кŶ���
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
	 * ������
	 */
	private String PatientID;
	
	/**
	 * ��������
	 */
	private String Name;
	
	/**
	 * �Ա�
	 */
	private String Sex;
	
	/**
	 * ��������
	 */
	private String Birthday;
	
	/**
	 * ���ұ���
	 */
	private String DeptCode;
	
	/**
	 * ��������
	 */
	private String DeptName;
	
	/**
	 * �ű����
	 */
	private String ClinicID;
	
	/**
	 * �ű�����
	 */
	private String ClinicName;
	
	/**
	 * ҽ������
	 */
	private String DoctorID;
	
	/**
	 * ҽ������
	 */
	private String DoctorName;
	
	/**
	 * ��������
	 */
	private String ClinicDate;
	
	/**
	 * ʱ���
	 */
	private String TimeDesc;
	
	/**
	 * ���
	 */
	private String SerialNum;

	/**
	 * ȡ�ò�����
	 * @return ������
	 */
	public String getPatientID() {
		return PatientID;
	}

	/**
	 * �趨������
	 * @param patientID ������
	 */
	public void setPatientID(String patientID) {
		PatientID = patientID;
	}

	/**
	 * ȡ�ò�������
	 * @return ��������
	 */
	public String getName() {
		return Name;
	}

	/**
	 * �趨��������
	 * @param name ��������
	 */
	public void setName(String name) {
		Name = name;
	}

	/**
	 * ȡ���Ա�
	 * @return �Ա�
	 */
	public String getSex() {
		return Sex;
	}

	/**
	 * �趨�Ա�
	 * @param sex �Ա�
	 */
	public void setSex(String sex) {
		Sex = sex;
	}

	/**
	 * ȡ�ó�������
	 * @return ��������
	 */
	public String getBirthday() {
		return Birthday;
	}

	/**
	 * �趨��������
	 * @param birthday ��������
	 */
	public void setBirthday(String birthday) {
		Birthday = birthday;
	}

	/**
	 * ȡ�ÿ��ұ���
	 * @return ���ұ���
	 */
	public String getDeptCode() {
		return DeptCode;
	}

	/**
	 * �趨���ұ���
	 * @param deptCode ���ұ���
	 */
	public void setDeptCode(String deptCode) {
		DeptCode = deptCode;
	}

	/**
	 * ȡ�ÿ�������
	 * @return ��������
	 */
	public String getDeptName() {
		return DeptName;
	}

	/**
	 * �趨��������
	 * @param deptName ��������
	 */
	public void setDeptName(String deptName) {
		DeptName = deptName;
	}

	/**
	 * ȡ�úű����
	 * @return �ű����
	 */
	public String getClinicID() {
		return ClinicID;
	}

	/**
	 * �趨�ű����
	 * @param clinicID �ű����
	 */
	public void setClinicID(String clinicID) {
		ClinicID = clinicID;
	}

	/**
	 * ȡ�úű�����
	 * @return �ű�����
	 */
	public String getClinicName() {
		return ClinicName;
	}

	/**
	 * �趨�ű�����
	 * @param clinicName �ű�����
	 */
	public void setClinicName(String clinicName) {
		ClinicName = clinicName;
	}

	/**
	 * ȡ��ҽ������
	 * @return ҽ������
	 */
	public String getDoctorID() {
		return DoctorID;
	}

	/**
	 * �趨ҽ������
	 * @param doctorID ҽ������
	 */
	public void setDoctorID(String doctorID) {
		DoctorID = doctorID;
	}

	/**
	 * ȡ��ҽ������
	 * @return ҽ������
	 */
	public String getDoctorName() {
		return DoctorName;
	}

	/**
	 * �趨ҽ������
	 * @param doctorName ҽ������
	 */
	public void setDoctorName(String doctorName) {
		DoctorName = doctorName;
	}

	/**
	 * ȡ�þ�������
	 * @return ��������
	 */
	public String getClinicDate() {
		return ClinicDate;
	}

	/**
	 * �趨��������
	 * @param clinicDate ��������
	 */
	public void setClinicDate(String clinicDate) {
		ClinicDate = clinicDate;
	}

	/**
	 * ȡ��ʱ���
	 * @return ʱ���
	 */
	public String getTimeDesc() {
		return TimeDesc;
	}

	/**
	 * �趨ʱ���
	 * @param timeDesc ʱ���
	 */
	public void setTimeDesc(String timeDesc) {
		TimeDesc = timeDesc;
	}

	/**
	 * ȡ�����
	 * @return ���
	 */
	public String getSerialNum() {
		return SerialNum;
	}

	/**
	 * �趨���
	 * @param serialNum ���
	 */
	public void setSerialNum(String serialNum) {
		SerialNum = serialNum;
	}
}
