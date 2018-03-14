package jdo.med;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Title: ���ͼ�������ϸ����
 * </p>
 * 
 * <p>
 * Description: ���ͼ�������ϸ����
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
	 * �ϰ�����
	 */
	private String BARCODE;
	/**
	 * �ͼ쵥λ
	 */
	private String SAMPLEFROM;
	/**
	 * ��������
	 */
	private String SAMPLETYPE;
	/**
	 * ����ʱ��
	 */
	private String COLLECTDDATE;
	/**
	 * �ύʱ��
	 */
	private String SUBMITDATE;
	/**
	 * �������
	 */
	private String TESTCODE;
	/**
	 * ����ʱ��
	 */
	private String APPRDATE;
	/**
	 * ������˾
	 */
	private String DEPT;
	/**
	 * ����
	 */
	private String SERVGRP;
	/**
	 * �����
	 */
	private String USRNAM;
	/**
	 * �����
	 */
	private String APPRVEDBY;
	/**
	 * ��������
	 */
	private String PATIENTNAME;
	/**
	 * �����
	 */
	private String CLINICID;
	/**
	 * ��λ
	 */
	private String BEDNO;
	/**
	 * �������ͣ�����/���/סԺ��
	 */
	private String PATIENTCATEGORY;
	/**
	 * �ͼ�ҽ��
	 */
	private String DOCTOR;
	/**
	 * �Ա�
	 */
	private String SEX;
	/**
	 * ����
	 */
	private String AGE;
	/**
	 * ���䵥λ
	 */
	private String AGEUNIT;
	/**
	 * ��Ŀ����
	 */
	private String SINONYM;
	/**
	 * ��Ŀ���
	 */
	private String SHORTNAME;
	/**
	 * ��λ
	 */
	private String UNITS;
	/**
	 * ���
	 */
	private String FINAL;
	/**
	 * ������Ŀ
	 */
	private String ANALYTE;
	/**
	 * �ο���Χ
	 */
	private String DISPLOWHIGH;
	/**
	 * Ů�ο�ֵ
	 */
	private String DISPLOWHIGH_F;
	/**
	 * �вο�ֵ
	 */
	private String DISPLOWHIGH_M;
	/**
	 * ����쳣��־λ��H L��
	 */
	private String RN10;
	/**
	 * �Խ���ĿID
	 */
	private String S;
	/**
	 * ��Ŀ����Ӣ��
	 */
	private String SYNONIM_EN;
	/**
	 * ����쳣���λ��������
	 */
	private String RN20;
	/**
	 * ��ע
	 */
	private String COMMENTS;
	/**
	 * ������Χ����
	 */
	private String LOWB;
	/**
	 * ������Χ����
	 */
	private String HIGHB;
	
	/**
	 * ȡ�õϰ�����
	 * @return �ϰ�����
	 */
	public String getBarCode() {
		return BARCODE;
	}
	/**
	 * �趨�ϰ�����
	 * @param barCode �ϰ�����
	 */
	public void setBarCode(String barCode) {
		BARCODE = barCode;
	}
	/**
	 * ȡ���ͼ쵥λ
	 * @return �ͼ쵥λ
	 */
	public String getSampleFrom() {
		return SAMPLEFROM;
	}
	/**
	 * �趨�ͼ쵥λ
	 * @param sampleFrom �ͼ쵥λ
	 */
	public void setSampleFrom(String sampleFrom) {
		SAMPLEFROM = sampleFrom;
	}
	/**
	 * ȡ����������
	 * @return ��������
	 */
	public String getSampleType() {
		return SAMPLETYPE;
	}
	/**
	 * �趨��������
	 * @param sampleType ��������
	 */
	public void setSampleType(String sampleType) {
		SAMPLETYPE = sampleType;
	}
	/**
	 * ȡ�ò���ʱ��
	 * @return ����ʱ��
	 */
	public String getCollectDate() {
		return COLLECTDDATE;
	}
	/**
	 * �趨����ʱ��
	 * @param collectDate ����ʱ��
	 */
	public void setCollectDate(String collectDate) {
		COLLECTDDATE = collectDate;
	}
	/**
	 * ȡ���ύʱ��
	 * @return �ύʱ��
	 */
	public String getSubmitData() {
		return SUBMITDATE;
	}
	/**
	 * �趨�ύʱ��
	 * @param submitData �ύʱ��
	 */
	public void setSubmitData(String submitData) {
		SUBMITDATE = submitData;
	}
	/**
	 * ȡ���������
	 * @return �������
	 */
	public String getTestCode() {
		return TESTCODE;
	}
	/**
	 * �趨�������
	 * @param testCode �������
	 */
	public void setTestCode(String testCode) {
		TESTCODE = testCode;
	}
	/**
	 * ȡ�÷���ʱ��
	 * @return ����ʱ��
	 */
	public String getApprDate() {
		return APPRDATE;
	}
	/**
	 * �趨����ʱ��
	 * @param apprDate ����ʱ��
	 */
	public void setApprDate(String apprDate) {
		APPRDATE = apprDate;
	}
	/**
	 * ȡ��������˾
	 * @return ������˾
	 */
	public String getDept() {
		return DEPT;
	}
	/**
	 * �趨������˾
	 * @param dept ������˾
	 */
	public void setDept(String dept) {
		DEPT = dept;
	}
	/**
	 * ȡ�ÿ���
	 * @return ����
	 */
	public String getSerGrp() {
		return SERVGRP;
	}
	/**
	 * �趨����
	 * @param serGrp ����
	 */
	public void setSerGrp(String serGrp) {
		SERVGRP = serGrp;
	}
	/**
	 * ȡ�ü����
	 * @return �����
	 */
	public String getUsrNam() {
		return USRNAM;
	}
	/**
	 * �趨�����
	 * @param usrNam �����
	 */
	public void setUsrNam(String usrNam) {
		USRNAM = usrNam;
	}
	/**
	 * ȡ�������
	 * @return �����
	 */
	public String getApprvedBy() {
		return APPRVEDBY;
	}
	/**
	 * �趨�����
	 * @param apprvedBy �����
	 */
	public void setApprvedBy(String apprvedBy) {
		APPRVEDBY = apprvedBy;
	}
	/**
	 * ȡ�ò�������
	 * @return ��������
	 */
	public String getPatientName() {
		return PATIENTNAME;
	}
	/**
	 * �趨��������
	 * @param patientName ��������
	 */
	public void setPatientName(String patientName) {
		PATIENTNAME = patientName;
	}
	/**
	 * ȡ�������
	 * @return �����
	 */
	public String getClinicid() {
		return CLINICID;
	}
	/**
	 * �趨�����
	 * @param clinicid �����
	 */
	public void setClinicid(String clinicid) {
		CLINICID = clinicid;
	}
	/**
	 * ȡ�ô�λ
	 * @return ��λ
	 */
	public String getBedNo() {
		return BEDNO;
	}
	/**
	 * �趨��λ
	 * @param bedNo ��λ
	 */
	public void setBedNo(String bedNo) {
		BEDNO = bedNo;
	}
	/**
	 * ȡ�ò������ͣ�����/���/סԺ��
	 * @return �������ͣ�����/���/סԺ��
	 */
	public String getPatientCategory() {
		return PATIENTCATEGORY;
	}
	/**
	 * �趨�������ͣ�����/���/סԺ��
	 * @param patientCategory �������ͣ�����/���/סԺ��
	 */
	public void setPatientCategory(String patientCategory) {
		PATIENTCATEGORY = patientCategory;
	}
	/**
	 * ȡ���ͼ�ҽ��
	 * @return �ͼ�ҽ��
	 */
	public String getDoctor() {
		return DOCTOR;
	}
	/**
	 * �趨�ͼ�ҽ��
	 * @param doctor �ͼ�ҽ��
	 */
	public void setDoctor(String doctor) {
		DOCTOR = doctor;
	}
	/**
	 * ȡ���Ա�
	 * @return �Ա�
	 */
	public String getSex() {
		return SEX;
	}
	/**
	 * �趨�Ա�
	 * @param sex �Ա�
	 */
	public void setSex(String sex) {
		SEX = sex;
	}
	/**
	 * ȡ������
	 * @return ����
	 */
	public String getAge() {
		return AGE;
	}
	/**
	 * �趨����
	 * @param age ����
	 */
	public void setAge(String age) {
		AGE = age;
	}
	/**
	 * ȡ�����䵥λ
	 * @return ���䵥λ
	 */
	public String getAgeUnit() {
		return AGEUNIT;
	}
	/**
	 * �趨���䵥λ
	 * @param ageUnit ���䵥λ
	 */
	public void setAgeUnit(String ageUnit) {
		AGEUNIT = ageUnit;
	}
	/**
	 * ȡ����Ŀ����
	 * @return ��Ŀ����
	 */
	public String getSinonym() {
		return SINONYM;
	}
	/**
	 * �趨��Ŀ����
	 * @param sinonym ��Ŀ����
	 */
	public void setSinonym(String sinonym) {
		SINONYM = sinonym;
	}
	/**
	 * ȡ����Ŀ���
	 * @return ��Ŀ���
	 */
	public String getShortName() {
		return SHORTNAME;
	}
	/**
	 * �趨��Ŀ���
	 * @param shortName ��Ŀ���
	 */
	public void setShortName(String shortName) {
		SHORTNAME = shortName;
	}
	/**
	 * ȡ�õ�λ
	 * @return ��λ
	 */
	public String getUnits() {
		return UNITS;
	}
	/**
	 * �趨��λ
	 * @param units ��λ
	 */
	public void setUnits(String units) {
		UNITS = units;
	}
	/**
	 * ȡ�ý��
	 * @return ���
	 */
	public String getFinal() {
		return FINAL;
	}
	/**
	 * �趨���
	 * @param finalValue ���
	 */
	public void setFinal(String finalValue) {
		FINAL = finalValue;
	}
	/**
	 * ȡ�÷�����Ŀ
	 * @return ������Ŀ
	 */
	public String getAnalyte() {
		return ANALYTE;
	}
	/**
	 * �趨������Ŀ
	 * @param analyte ������Ŀ
	 */
	public void setAnalyte(String analyte) {
		ANALYTE = analyte;
	}
	/**
	 * ȡ�òο���Χ
	 * @return �ο���Χ
	 */
	public String getDispLowHigh() {
		return DISPLOWHIGH;
	}
	/**
	 * �趨�ο���Χ
	 * @param dispLowHigh �ο���Χ
	 */
	public void setDispLowHigh(String dispLowHigh) {
		DISPLOWHIGH = dispLowHigh;
	}
	/**
	 * ȡ��Ů�ο�ֵ
	 * @return Ů�ο�ֵ
	 */
	public String getDispLowHighF() {
		return DISPLOWHIGH_F;
	}
	/**
	 * �趨Ů�ο�ֵ
	 * @param dispLowHighF Ů�ο�ֵ
	 */
	public void setDispLowHighF(String dispLowHighF) {
		DISPLOWHIGH_F = dispLowHighF;
	}
	/**
	 * ȡ���вο�ֵ
	 * @return �вο�ֵ
	 */
	public String getDispLowHighM() {
		return DISPLOWHIGH_M;
	}
	/**
	 * �趨�вο�ֵ
	 * @param dispLowHighM �вο�ֵ
	 */
	public void setDispLowHighM(String dispLowHighM) {
		DISPLOWHIGH_M = dispLowHighM;
	}
	/**
	 * ȡ�ý���쳣��־λ��H L��
	 * @return ����쳣��־λ��H L��
	 */
	public String getRn10() {
		return RN10;
	}
	/**
	 * ����쳣��־λ��H L��
	 * @param rn10 ����쳣��־λ��H L��
	 */
	public void setRn10(String rn10) {
		RN10 = rn10;
	}
	/**
	 * ȡ�öԽ���ĿID
	 * @return �Խ���ĿID
	 */
	public String getS() {
		return S;
	}
	/**
	 * �趨�Խ���ĿID
	 * @param s �Խ���ĿID
	 */
	public void setS(String s) {
		S = s;
	}
	/**
	 * ȡ����Ŀ����Ӣ��
	 * @return ��Ŀ����Ӣ��
	 */
	public String getSynonimEn() {
		return SYNONIM_EN;
	}
	/**
	 * �趨��Ŀ����Ӣ��
	 * @param synonimEn ��Ŀ����Ӣ��
	 */
	public void setSynonimEn(String synonimEn) {
		SYNONIM_EN = synonimEn;
	}
	/**
	 * ȡ�ý���쳣���λ��������
	 * @return ����쳣���λ��������
	 */
	public String getRn20() {
		return RN20;
	}
	/**
	 * �趨����쳣���λ��������
	 * @param rn20 ����쳣���λ��������
	 */
	public void setRn20(String rn20) {
		RN20 = rn20;
	}
	/**
	 * ȡ�ñ�ע
	 * @return ��ע
	 */
	public String getComments() {
		return COMMENTS;
	}
	/**
	 * �趨��ע
	 * @param comments ��ע
	 */
	public void setComments(String comments) {
		COMMENTS = comments;
	}
	/**
	 * ȡ��������Χ����
	 * @return ������Χ����
	 */
	public String getLowB() {
		return LOWB;
	}
	/**
	 * �趨������Χ����
	 * @param lowB ������Χ����
	 */
	public void setLowB(String lowB) {
		LOWB = lowB;
	}
	/**
	 * ȡ��������Χ����
	 * @return ������Χ����
	 */
	public String getHighB() {
		return HIGHB;
	}
	/**
	 * �趨������Χ����
	 * @param highB ������Χ����
	 */
	public void setHighB(String highB) {
		HIGHB = highB;
	}
}
