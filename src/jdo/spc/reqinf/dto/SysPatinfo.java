package jdo.spc.reqinf.dto;
// default package



public class SysPatinfo implements java.io.Serializable {

	// Fields
	private String mrNo;
	private String sexCode;
	private String ipdNo;
	private String deleteFlg;
	private String mergeFlg;
	
	private String motherMrno;
	private String patName;
	private String patName1;
	private String py1;
	private String py2;
	
	private String foreignerFlg;
	private String idno;
	private String birthDate;
	private String ctz1Code;
	private String ctz2Code;
	
	private String ctz3Code;
	private String telCompany;
	private String telHome;
	private String cellPhone;
	private String companyDesc;
	
	private String EMail;
	private String bloodType;
	private String bloodRhType;	
	private String marriageCode;
	private String postCode;
	
	private String address;
	private String residPostCode;
	private String residAddress;
	private String contactsName;
	private String relationCode;
	
	private String contactsTel;
	private String contactsAddress;
	private String spouseIdno;
	private String fatherIdno;
	private String motherIdno;
	
	private String religionCode;
	private String educationCode;
	private String occCode;
	private String nationCode;
	private String speciesCode;
	
	private String firstAdmDate;
	private String rcntOpdDate;
	private String rcntOpdDept;
	private String rcntIpdDate;
	private String rcntIpdDept;
	
	private String rcntEmgDate;
	private String rcntEmgDept;
	private String rcntMissDate;
	private String rcntMissDept;
	private String kidExamRcntDate;
	
	private String kidInjRcntDate;
	private String adultExamDate;
	private String smearRcntDate;
	private String deadDate;
	private Double height;
	
	private Double weight;
	private String description;
	private String borninFlg;
	private Integer newbornSeq;
	private String prematureFlg;
	
	private String handicapFlg;
	private String blackFlg;
	private String nameInvisibleFlg;
	private String lawProtectFlg;
	private String lmpDate;
	
	private String pregnantDate;
	private String breastfeedStartdate;
	private String breastfeedEnddate;
	private String pat1Code;
	private String pat2Code;
	
	private String pat3Code;
	private String mergeTomrno;	
	private String homeplaceCode;
	private String familyHistory;
	private String handleFlg;
	
	private String nhiNo;
	private String addressCompany;
	private String postCompany;
	private String birthplace;
	private String ccbPersonNo;
	
	private String nhiCardNo;
	private String optUser;
	private String optDate;
	private String optTerm;
	
	
	// Constructors

	public String getNhiCardNo() {
		return nhiCardNo;
	}

	public void setNhiCardNo(String nhiCardNo) {
		this.nhiCardNo = nhiCardNo;
	}

	/** default constructor */
	public SysPatinfo() {
	}

	/** minimal constructor */
	public SysPatinfo(String mrNo, String sexCode, String optUser,
			String optDate, String optTerm) {
		this.mrNo = mrNo;
		this.sexCode = sexCode;
		this.optUser = optUser;
		this.optDate = optDate;
		this.optTerm = optTerm;
	}

	/** full constructor */
	public SysPatinfo(String mrNo, String ipdNo, String deleteFlg,
			String mergeFlg, String motherMrno, String patName,
			String patName1, String py1, String py2, String foreignerFlg,
			String idno, String birthDate, String ctz1Code, String ctz2Code,
			String ctz3Code, String telCompany, String telHome,
			String cellPhone, String companyDesc, String EMail,
			String bloodType, String bloodRhType, String sexCode,
			String marriageCode, String postCode, String address,
			String residPostCode, String residAddress, String contactsName,
			String relationCode, String contactsTel, String contactsAddress,
			String spouseIdno, String fatherIdno, String motherIdno,
			String religionCode, String educationCode, String occCode,
			String nationCode, String speciesCode, String firstAdmDate,
			String rcntOpdDate, String rcntOpdDept, String rcntIpdDate,
			String rcntIpdDept, String rcntEmgDate, String rcntEmgDept,
			String rcntMissDate, String rcntMissDept,
			String kidExamRcntDate, String kidInjRcntDate,
			String adultExamDate, String smearRcntDate,
			String deadDate, Double height, Double weight,
			String description, String borninFlg, Integer newbornSeq,
			String prematureFlg, String handicapFlg, String blackFlg,
			String nameInvisibleFlg, String lawProtectFlg, String lmpDate,
			String pregnantDate, String breastfeedStartdate,
			String breastfeedEnddate, String pat1Code, String pat2Code,
			String pat3Code, String mergeTomrno, String optUser,
			String optDate, String optTerm, String homeplaceCode,
			String familyHistory, String handleFlg, String nhiNo,
			String addressCompany, String postCompany, String birthplace,
			String ccbPersonNo, String nhiCardNo) {
		this.mrNo = mrNo;
		this.ipdNo = ipdNo;
		this.deleteFlg = deleteFlg;
		this.mergeFlg = mergeFlg;
		this.motherMrno = motherMrno;
		this.patName = patName;
		this.patName1 = patName1;
		this.py1 = py1;
		this.py2 = py2;
		this.foreignerFlg = foreignerFlg;
		this.idno = idno;
		this.birthDate = birthDate;
		this.ctz1Code = ctz1Code;
		this.ctz2Code = ctz2Code;
		this.ctz3Code = ctz3Code;
		this.telCompany = telCompany;
		this.telHome = telHome;
		this.cellPhone = cellPhone;
		this.companyDesc = companyDesc;
		this.EMail = EMail;
		this.bloodType = bloodType;
		this.bloodRhType = bloodRhType;
		this.sexCode = sexCode;
		this.marriageCode = marriageCode;
		this.postCode = postCode;
		this.address = address;
		this.residPostCode = residPostCode;
		this.residAddress = residAddress;
		this.contactsName = contactsName;
		this.relationCode = relationCode;
		this.contactsTel = contactsTel;
		this.contactsAddress = contactsAddress;
		this.spouseIdno = spouseIdno;
		this.fatherIdno = fatherIdno;
		this.motherIdno = motherIdno;
		this.religionCode = religionCode;
		this.educationCode = educationCode;
		this.occCode = occCode;
		this.nationCode = nationCode;
		this.speciesCode = speciesCode;
		this.firstAdmDate = firstAdmDate;
		this.rcntOpdDate = rcntOpdDate;
		this.rcntOpdDept = rcntOpdDept;
		this.rcntIpdDate = rcntIpdDate;
		this.rcntIpdDept = rcntIpdDept;
		this.rcntEmgDate = rcntEmgDate;
		this.rcntEmgDept = rcntEmgDept;
		this.rcntMissDate = rcntMissDate;
		this.rcntMissDept = rcntMissDept;
		this.kidExamRcntDate = kidExamRcntDate;
		this.kidInjRcntDate = kidInjRcntDate;
		this.adultExamDate = adultExamDate;
		this.smearRcntDate = smearRcntDate;
		this.deadDate = deadDate;
		this.height = height;
		this.weight = weight;
		this.description = description;
		this.borninFlg = borninFlg;
		this.newbornSeq = newbornSeq;
		this.prematureFlg = prematureFlg;
		this.handicapFlg = handicapFlg;
		this.blackFlg = blackFlg;
		this.nameInvisibleFlg = nameInvisibleFlg;
		this.lawProtectFlg = lawProtectFlg;
		this.lmpDate = lmpDate;
		this.pregnantDate = pregnantDate;
		this.breastfeedStartdate = breastfeedStartdate;
		this.breastfeedEnddate = breastfeedEnddate;
		this.pat1Code = pat1Code;
		this.pat2Code = pat2Code;
		this.pat3Code = pat3Code;
		this.mergeTomrno = mergeTomrno;
		this.optUser = optUser;
		this.optDate = optDate;
		this.optTerm = optTerm;
		this.homeplaceCode = homeplaceCode;
		this.familyHistory = familyHistory;
		this.handleFlg = handleFlg;
		this.nhiNo = nhiNo;
		this.addressCompany = addressCompany;
		this.postCompany = postCompany;
		this.birthplace = birthplace;
		this.ccbPersonNo = ccbPersonNo;
		this.nhiCardNo = nhiCardNo;
	}

	// Property accessors
	public String getMrNo() {
		return this.mrNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	public String getIpdNo() {
		return this.ipdNo;
	}

	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	public String getDeleteFlg() {
		return this.deleteFlg;
	}

	public void setDeleteFlg(String deleteFlg) {
		this.deleteFlg = deleteFlg;
	}

	public String getMergeFlg() {
		return this.mergeFlg;
	}

	public void setMergeFlg(String mergeFlg) {
		this.mergeFlg = mergeFlg;
	}

	public String getMotherMrno() {
		return this.motherMrno;
	}

	public void setMotherMrno(String motherMrno) {
		this.motherMrno = motherMrno;
	}

	public String getPatName() {
		return this.patName;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public String getPatName1() {
		return this.patName1;
	}

	public void setPatName1(String patName1) {
		this.patName1 = patName1;
	}

	public String getPy1() {
		return this.py1;
	}

	public void setPy1(String py1) {
		this.py1 = py1;
	}

	public String getPy2() {
		return this.py2;
	}

	public void setPy2(String py2) {
		this.py2 = py2;
	}

	public String getForeignerFlg() {
		return this.foreignerFlg;
	}

	public void setForeignerFlg(String foreignerFlg) {
		this.foreignerFlg = foreignerFlg;
	}

	public String getIdno() {
		return this.idno;
	}

	public void setIdno(String idno) {
		this.idno = idno;
	}

	public String getBirthDate() {
		return this.birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getCtz1Code() {
		return this.ctz1Code;
	}

	public void setCtz1Code(String ctz1Code) {
		this.ctz1Code = ctz1Code;
	}

	public String getCtz2Code() {
		return this.ctz2Code;
	}

	public void setCtz2Code(String ctz2Code) {
		this.ctz2Code = ctz2Code;
	}

	public String getCtz3Code() {
		return this.ctz3Code;
	}

	public void setCtz3Code(String ctz3Code) {
		this.ctz3Code = ctz3Code;
	}

	public String getTelCompany() {
		return this.telCompany;
	}

	public void setTelCompany(String telCompany) {
		this.telCompany = telCompany;
	}

	public String getTelHome() {
		return this.telHome;
	}

	public void setTelHome(String telHome) {
		this.telHome = telHome;
	}

	public String getCellPhone() {
		return this.cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getCompanyDesc() {
		return this.companyDesc;
	}

	public void setCompanyDesc(String companyDesc) {
		this.companyDesc = companyDesc;
	}

	public String getEMail() {
		return this.EMail;
	}

	public void setEMail(String EMail) {
		this.EMail = EMail;
	}

	public String getBloodType() {
		return this.bloodType;
	}

	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}

	public String getBloodRhType() {
		return this.bloodRhType;
	}

	public void setBloodRhType(String bloodRhType) {
		this.bloodRhType = bloodRhType;
	}

	public String getSexCode() {
		return this.sexCode;
	}

	public void setSexCode(String sexCode) {
		this.sexCode = sexCode;
	}

	public String getMarriageCode() {
		return this.marriageCode;
	}

	public void setMarriageCode(String marriageCode) {
		this.marriageCode = marriageCode;
	}

	public String getPostCode() {
		return this.postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getResidPostCode() {
		return this.residPostCode;
	}

	public void setResidPostCode(String residPostCode) {
		this.residPostCode = residPostCode;
	}

	public String getResidAddress() {
		return this.residAddress;
	}

	public void setResidAddress(String residAddress) {
		this.residAddress = residAddress;
	}

	public String getContactsName() {
		return this.contactsName;
	}

	public void setContactsName(String contactsName) {
		this.contactsName = contactsName;
	}

	public String getRelationCode() {
		return this.relationCode;
	}

	public void setRelationCode(String relationCode) {
		this.relationCode = relationCode;
	}

	public String getContactsTel() {
		return this.contactsTel;
	}

	public void setContactsTel(String contactsTel) {
		this.contactsTel = contactsTel;
	}

	public String getContactsAddress() {
		return this.contactsAddress;
	}

	public void setContactsAddress(String contactsAddress) {
		this.contactsAddress = contactsAddress;
	}

	public String getSpouseIdno() {
		return this.spouseIdno;
	}

	public void setSpouseIdno(String spouseIdno) {
		this.spouseIdno = spouseIdno;
	}

	public String getFatherIdno() {
		return this.fatherIdno;
	}

	public void setFatherIdno(String fatherIdno) {
		this.fatherIdno = fatherIdno;
	}

	public String getMotherIdno() {
		return this.motherIdno;
	}

	public void setMotherIdno(String motherIdno) {
		this.motherIdno = motherIdno;
	}

	public String getReligionCode() {
		return this.religionCode;
	}

	public void setReligionCode(String religionCode) {
		this.religionCode = religionCode;
	}

	public String getEducationCode() {
		return this.educationCode;
	}

	public void setEducationCode(String educationCode) {
		this.educationCode = educationCode;
	}

	public String getOccCode() {
		return this.occCode;
	}

	public void setOccCode(String occCode) {
		this.occCode = occCode;
	}

	public String getNationCode() {
		return this.nationCode;
	}

	public void setNationCode(String nationCode) {
		this.nationCode = nationCode;
	}

	public String getSpeciesCode() {
		return this.speciesCode;
	}

	public void setSpeciesCode(String speciesCode) {
		this.speciesCode = speciesCode;
	}

	public String getFirstAdmDate() {
		return this.firstAdmDate;
	}

	public void setFirstAdmDate(String firstAdmDate) {
		this.firstAdmDate = firstAdmDate;
	}

	public String getRcntOpdDate() {
		return this.rcntOpdDate;
	}

	public void setRcntOpdDate(String rcntOpdDate) {
		this.rcntOpdDate = rcntOpdDate;
	}

	public String getRcntOpdDept() {
		return this.rcntOpdDept;
	}

	public void setRcntOpdDept(String rcntOpdDept) {
		this.rcntOpdDept = rcntOpdDept;
	}

	public String getRcntIpdDate() {
		return this.rcntIpdDate;
	}

	public void setRcntIpdDate(String rcntIpdDate) {
		this.rcntIpdDate = rcntIpdDate;
	}

	public String getRcntIpdDept() {
		return this.rcntIpdDept;
	}

	public void setRcntIpdDept(String rcntIpdDept) {
		this.rcntIpdDept = rcntIpdDept;
	}

	public String getRcntEmgDate() {
		return this.rcntEmgDate;
	}

	public void setRcntEmgDate(String rcntEmgDate) {
		this.rcntEmgDate = rcntEmgDate;
	}

	public String getRcntEmgDept() {
		return this.rcntEmgDept;
	}

	public void setRcntEmgDept(String rcntEmgDept) {
		this.rcntEmgDept = rcntEmgDept;
	}

	public String getRcntMissDate() {
		return this.rcntMissDate;
	}

	public void setRcntMissDate(String rcntMissDate) {
		this.rcntMissDate = rcntMissDate;
	}

	public String getRcntMissDept() {
		return this.rcntMissDept;
	}

	public void setRcntMissDept(String rcntMissDept) {
		this.rcntMissDept = rcntMissDept;
	}

	public String getKidExamRcntDate() {
		return this.kidExamRcntDate;
	}

	public void setKidExamRcntDate(String kidExamRcntDate) {
		this.kidExamRcntDate = kidExamRcntDate;
	}

	public String getKidInjRcntDate() {
		return this.kidInjRcntDate;
	}

	public void setKidInjRcntDate(String kidInjRcntDate) {
		this.kidInjRcntDate = kidInjRcntDate;
	}

	public String getAdultExamDate() {
		return this.adultExamDate;
	}

	public void setAdultExamDate(String adultExamDate) {
		this.adultExamDate = adultExamDate;
	}

	public String getSmearRcntDate() {
		return this.smearRcntDate;
	}

	public void setSmearRcntDate(String smearRcntDate) {
		this.smearRcntDate = smearRcntDate;
	}

	public String getDeadDate() {
		return this.deadDate;
	}

	public void setDeadDate(String deadDate) {
		this.deadDate = deadDate;
	}

	public Double getHeight() {
		return this.height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getWeight() {
		return this.weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBorninFlg() {
		return this.borninFlg;
	}

	public void setBorninFlg(String borninFlg) {
		this.borninFlg = borninFlg;
	}

	public Integer getNewbornSeq() {
		return this.newbornSeq;
	}

	public void setNewbornSeq(Integer newbornSeq) {
		this.newbornSeq = newbornSeq;
	}

	public String getPrematureFlg() {
		return this.prematureFlg;
	}

	public void setPrematureFlg(String prematureFlg) {
		this.prematureFlg = prematureFlg;
	}

	public String getHandicapFlg() {
		return this.handicapFlg;
	}

	public void setHandicapFlg(String handicapFlg) {
		this.handicapFlg = handicapFlg;
	}

	public String getBlackFlg() {
		return this.blackFlg;
	}

	public void setBlackFlg(String blackFlg) {
		this.blackFlg = blackFlg;
	}

	public String getNameInvisibleFlg() {
		return this.nameInvisibleFlg;
	}

	public void setNameInvisibleFlg(String nameInvisibleFlg) {
		this.nameInvisibleFlg = nameInvisibleFlg;
	}

	public String getLawProtectFlg() {
		return this.lawProtectFlg;
	}

	public void setLawProtectFlg(String lawProtectFlg) {
		this.lawProtectFlg = lawProtectFlg;
	}

	public String getLmpDate() {
		return this.lmpDate;
	}

	public void setLmpDate(String lmpDate) {
		this.lmpDate = lmpDate;
	}

	public String getPregnantDate() {
		return this.pregnantDate;
	}

	public void setPregnantDate(String pregnantDate) {
		this.pregnantDate = pregnantDate;
	}

	public String getBreastfeedStartdate() {
		return this.breastfeedStartdate;
	}

	public void setBreastfeedStartdate(String breastfeedStartdate) {
		this.breastfeedStartdate = breastfeedStartdate;
	}

	public String getBreastfeedEnddate() {
		return this.breastfeedEnddate;
	}

	public void setBreastfeedEnddate(String breastfeedEnddate) {
		this.breastfeedEnddate = breastfeedEnddate;
	}

	public String getPat1Code() {
		return this.pat1Code;
	}

	public void setPat1Code(String pat1Code) {
		this.pat1Code = pat1Code;
	}

	public String getPat2Code() {
		return this.pat2Code;
	}

	public void setPat2Code(String pat2Code) {
		this.pat2Code = pat2Code;
	}

	public String getPat3Code() {
		return this.pat3Code;
	}

	public void setPat3Code(String pat3Code) {
		this.pat3Code = pat3Code;
	}

	public String getMergeTomrno() {
		return this.mergeTomrno;
	}

	public void setMergeTomrno(String mergeTomrno) {
		this.mergeTomrno = mergeTomrno;
	}

	public String getOptUser() {
		return this.optUser;
	}

	public void setOptUser(String optUser) {
		this.optUser = optUser;
	}

	public String getOptDate() {
		return this.optDate;
	}

	public void setOptDate(String optDate) {
		this.optDate = optDate;
	}

	public String getOptTerm() {
		return this.optTerm;
	}

	public void setOptTerm(String optTerm) {
		this.optTerm = optTerm;
	}

	public String getHomeplaceCode() {
		return this.homeplaceCode;
	}

	public void setHomeplaceCode(String homeplaceCode) {
		this.homeplaceCode = homeplaceCode;
	}

	public String getFamilyHistory() {
		return this.familyHistory;
	}

	public void setFamilyHistory(String familyHistory) {
		this.familyHistory = familyHistory;
	}

	public String getHandleFlg() {
		return this.handleFlg;
	}

	public void setHandleFlg(String handleFlg) {
		this.handleFlg = handleFlg;
	}

	public String getNhiNo() {
		return this.nhiNo;
	}

	public void setNhiNo(String nhiNo) {
		this.nhiNo = nhiNo;
	}

	public String getAddressCompany() {
		return this.addressCompany;
	}

	public void setAddressCompany(String addressCompany) {
		this.addressCompany = addressCompany;
	}

	public String getPostCompany() {
		return this.postCompany;
	}

	public void setPostCompany(String postCompany) {
		this.postCompany = postCompany;
	}

	public String getBirthplace() {
		return this.birthplace;
	}

	public void setBirthplace(String birthplace) {
		this.birthplace = birthplace;
	}

	public String getCcbPersonNo() {
		return this.ccbPersonNo;
	}

	public void setCcbPersonNo(String ccbPersonNo) {
		this.ccbPersonNo = ccbPersonNo;
	}

}