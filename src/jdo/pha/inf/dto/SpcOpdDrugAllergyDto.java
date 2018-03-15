package jdo.pha.inf.dto;

import java.io.Serializable;

/**
 * <p>
 * Title: 物联网门急诊药房webservice数据传输DTO OPD_DRUGALLERGY
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
public class SpcOpdDrugAllergyDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String mrNo;
	private String admDate;
	private String drugType;
	private String drugoringrdCode;
	// private String admType;
	// private String caseNo;
	// private String deptCode;
	// private String drCode;
	private String allergyNote;
	private String optUser;
	private String optDate;
	private String optTerm;

	public String getMrNo() {
		return mrNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	public String getAdmDate() {
		return admDate;
	}

	public void setAdmDate(String admDate) {
		this.admDate = admDate;
	}

	public String getDrugType() {
		return drugType;
	}

	public void setDrugType(String drugType) {
		this.drugType = drugType;
	}

	public String getDrugoringrdCode() {
		return drugoringrdCode;
	}

	public void setDrugoringrdCode(String drugoringrdCode) {
		this.drugoringrdCode = drugoringrdCode;
	}

	public String getAllergyNote() {
		return allergyNote;
	}

	public void setAllergyNote(String allergyNote) {
		this.allergyNote = allergyNote;
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

}
