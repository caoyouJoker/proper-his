package jdo.pha.inf.dto;

import java.io.Serializable;

/**
 * <p>
 * Title: 物联网门急诊药房webservice数据传输DTO OPD_DIAGREC
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
 * Company: Bluecore
 * </p>
 * 
 * @author zhangp
 * 
 * @version 1.0
 */
public class SpcOpdDiagrecDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String caseNo;
	private String icdType;
	private String icdCode;
	// private String mainDiagFlg;
	// private String admType;
	private String diagNote;
	// private String drCode;
	// private String orderDate;
	// private int fileNo;
	private String optUser;
	private String optDate;
	private String optTerm;

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getIcdType() {
		return icdType;
	}

	public void setIcdType(String icdType) {
		this.icdType = icdType;
	}

	public String getIcdCode() {
		return icdCode;
	}

	public void setIcdCode(String icdCode) {
		this.icdCode = icdCode;
	}

	public String getDiagNote() {
		return diagNote;
	}

	public void setDiagNote(String diagNote) {
		this.diagNote = diagNote;
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
