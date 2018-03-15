package jdo.pha.inf.dto;

import java.io.Serializable;

/**
 * <p>
 * Title: 物联网门急诊药房webservice数据传输DTO SYS_PATINFO
 * </p>
 * 
 * <p>
 * Description: 物联网门急诊药房webservice数据传输DTO
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author zhangp
 * 
 * @version 1.0
 */
public class SpcSysPatinfoDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String mrNo;
//	private String ipdNo;
//	private String deleteFlg;
//	private String mergeFlg;
//	private String motherMrno;
	private String patName;
//	private String patName1;
//	private String py1;
//	private String py2;
//	private String foreignerFlg;
//	private String idno;
	private String birthDate;
//	private String ctz1Code;
//	private String ctz2Code;
//	private String ctz3Code;
//	private String telCompany;
//	private String telHome;
//	private String cellPhone;
//	private String companyDesc;
//	private String eMail;
//	private String bloodType;
//	private String bloodRhType;
	private String sexCode;
//	private String marriageCode;
//	private String postCode;
//	private String address;
//	private String residPostCode;
//	private String residAddress;
//	private String contactsName;
//	private String relationCode;
//	private String contactsTel;
//	private String contactsAddress;
//	private String spouseIdno;
//	private String fatherIdno;
//	private String motherIdno;
//	private String religionCode;
//	private String educationCode;
//	private String occCode;
//	private String nationCode;
//	private String speciesCode;
//	private String firstAdmDate;
//	private String rcntOpdDate;
//	private String rcntOpdDept;
//	private String rcntIpdDate;
//	private String rcntIpdDept;
//	private String rcntEmgDate;
//	private String rcntEmgDept;
//	private String rcntMissDate;
//	private String rcntMissDept;
//	private String kidExamRcntDate;
//	private String kidInjRcntDate;
//	private String adultExamDate;
//	private String smearRcntDate;
//	private String deadDate;
//	private double height;
//	private double weight;
//	private String description;
//	private String borninFlg;
//	private double newbornSeq;
//	private String prematureFlg;
//	private String handicapFlg;
//	private String blackFlg;
//	private String nameInvisibleFlg;
//	private String lawProtectFlg;
//	private String lmpDate;
//	private String pregnantDate;
//	private String breastfeedStartdate;
//	private String breastfeedEnddate;
//	private String pat1Code;
//	private String pat2Code;
//	private String pat3Code;
//	private String mergeTomrno;
	private String optUser;
	private String optDate;
	private String optTerm;
//	private String homeplaceCode;
//	private String familyHistory;
//	private String handleFlg;
//	private String nhiNo;
//	private String addressCompany;
//	private String postCompany;
//	private String birthplace;

	public String getMrNo() {
		return mrNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	public String getSexCode() {
		return sexCode;
	}

	public void setSexCode(String sexCode) {
		this.sexCode = sexCode;
	}

	public String getOptUser() {
		return optUser;
	}

	public void setOptUser(String optUser) {
		this.optUser = optUser;
	}

	public String getOptDate() {
		return optDate;
	}

	public void setOptDate(String optDate) {
		this.optDate = optDate;
	}

	public String getOptTerm() {
		return optTerm;
	}

	public void setOptTerm(String optTerm) {
		this.optTerm = optTerm;
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

}
