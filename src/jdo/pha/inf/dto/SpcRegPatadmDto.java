package jdo.pha.inf.dto;

import java.io.Serializable;

/**
 * <p>
 * Title: 物联网门急诊药房webservice数据传输DTO REG_PATADM
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
public class SpcRegPatadmDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String caseNo;
//	private String admType;
	private String mrNo;
//	private String regionCode;
//	private String admDate;
//	private String regDate;
//	private String sessionCode;
//	private String clinicareaCode;
//	private String clinicroomNo;
//	private String vipFlg;
//	private String clinictypeCode;
//	private int queNo;
//	private String regAdmTime;
//	private String deptCode;
//	private String drCode;
	private String realdeptCode;
	private String realdrCode;
//	private String apptCode;
//	private String visitCode;
//	private String regmethodCode;
//	private String ctz1Code;
//	private String ctz2Code;
//	private String ctz3Code;
//	private String tranhospCode;
//	private String triageNo;
//	private String contractCode;
//	private String arriveFlg;
//	private String regcanUser;
//	private String regcanDate;
//	private String admRegion;
//	private String preventSchCode;
//	private String drgCode;
//	private String heatFlg;
//	private String admStatus;
//	private String reportStatus;
	private double weight;
	private double height;
//	private String seeDrFlg;
	private String optUser;
	private String optDate;
	private String optTerm;
//	private String erdLevel;
//	private String serviceLevel;
//	private String seenDrTime;
//	private String nhiNo;
//	private double greenBalance;
//	private double greenPathTotal;
//	private String insPatType;
//	private String confirmNo;
//	private String medHistory;
//	private String diseaseHistory;
//	private String assistantExamine;
//	private String asssistantStuff;

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getMrNo() {
		return mrNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
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

	public String getRealdeptCode() {
		return realdeptCode;
	}

	public void setRealdeptCode(String realdeptCode) {
		this.realdeptCode = realdeptCode;
	}

	public String getRealdrCode() {
		return realdrCode;
	}

	public void setRealdrCode(String realdrCode) {
		this.realdrCode = realdrCode;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

}
