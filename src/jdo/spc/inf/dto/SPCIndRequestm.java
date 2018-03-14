package jdo.spc.inf.dto;


import java.util.ArrayList;
import java.util.List;


public class SPCIndRequestm implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String requestNo;
	private String reqtypeCode;
	private String appOrgCode;
	private String toOrgCode;
	private String requestDate;
	private String requestUser;
	private String reasonChnDesc;
	private String description;
	private String unitType;
	private String urgentFlg;
	private String optUser;
	private String optDate;
	private String optTerm;
	private String regionCode;
	private String drugCategory;
	private String applyType;
	private List<SPCIndRequestd> indRequestds = new ArrayList<SPCIndRequestd>();


	public SPCIndRequestm() {
	}

	public String getRequestNo() {
		return this.requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	//@Column(name = "REQTYPE_CODE", nullable = false, length = 20)
	public String getReqtypeCode() {
		return this.reqtypeCode;
	}

	public void setReqtypeCode(String reqtypeCode) {
		this.reqtypeCode = reqtypeCode;
	}

	//@Column(name = "APP_ORG_CODE", length = 20)
	public String getAppOrgCode() {
		return this.appOrgCode;
	}

	public void setAppOrgCode(String appOrgCode) {
		this.appOrgCode = appOrgCode;
	}

	//@Column(name = "TO_ORG_CODE", length = 20)
	public String getToOrgCode() {
		return this.toOrgCode;
	}

	public void setToOrgCode(String toOrgCode) {
		this.toOrgCode = toOrgCode;
	}

	//@Column(name = "REQUEST_DATE", length = 7)
	public String getRequestDate() {
		return this.requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}

	//@Column(name = "REQUEST_USER", length = 20)
	public String getRequestUser() {
		return this.requestUser;
	}

	public void setRequestUser(String requestUser) {
		this.requestUser = requestUser;
	}

	//@Column(name = "REASON_CHN_DESC", length = 50)
	public String getReasonChnDesc() {
		return this.reasonChnDesc;
	}

	public void setReasonChnDesc(String reasonChnDesc) {
		this.reasonChnDesc = reasonChnDesc;
	}

	//@Column(name = "DESCRIPTION", length = 50)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	//@Column(name = "UNIT_TYPE", length = 1)
	public String getUnitType() {
		return this.unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	//@Column(name = "URGENT_FLG", length = 1)
	public String getUrgentFlg() {
		return this.urgentFlg;
	}

	public void setUrgentFlg(String urgentFlg) {
		this.urgentFlg = urgentFlg;
	}

	//@Column(name = "OPT_USER", nullable = false, length = 20)
	public String getOptUser() {
		return this.optUser;
	}

	public void setOptUser(String optUser) {
		this.optUser = optUser;
	}

	//@Column(name = "OPT_DATE", nullable = false, length = 7)
	public String getOptDate() {
		return this.optDate;
	}

	public void setOptDate(String optDate) {
		this.optDate = optDate;
	}

	//@Column(name = "OPT_TERM", nullable = false, length = 20)
	public String getOptTerm() {
		return this.optTerm;
	}

	public void setOptTerm(String optTerm) {
		this.optTerm = optTerm;
	}

	//@Column(name = "REGION_CODE", length = 20)
	public String getRegionCode() {
		return this.regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	//@Column(name = "DRUG_CATEGORY", length = 1)
	public String getDrugCategory() {
		return this.drugCategory;
	}

	public void setDrugCategory(String drugCategory) {
		this.drugCategory = drugCategory;
	}

	//@Column(name = "APPLY_TYPE", length = 1)
	public String getApplyType() {
		return this.applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public List<SPCIndRequestd> getIndRequestds() {
		return indRequestds;
	}

	public void setIndRequestds(List<SPCIndRequestd> indRequestds) {
		this.indRequestds = indRequestds;
	}

	

}