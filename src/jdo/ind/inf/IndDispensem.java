package jdo.ind.inf;
import java.util.ArrayList;
import java.util.List;

// default package



public class IndDispensem implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String dispenseNo;
	private String reqtypeCode;
	private String stockType;
	private String requestNo;
	private String requestDate;
	private String appOrgCode;
	private String toOrgCode;
	private String urgentFlg;
	private String description;
	private String dispenseDate;
	private String dispenseUser;
	private String warehousingDate;
	private String warehousingUser;
	private String reasonChnDesc;
	private String unitType;
	private String updateFlg;
	private String optUser;
	private String optDate;
	private String optTerm;
	private String regionCode;
	private String drugCategory;
	
	private ArrayList<IndDispensed> indDispenseds = new ArrayList<IndDispensed>() ;

	// Constructors

	/** default constructor */
	public IndDispensem() {
	}
	
	////@Column(name = "DISPENSE_NO", unique = true, nullable = false, length = 20)
	public String getDispenseNo() {
		return this.dispenseNo;
	}

	public void setDispenseNo(String dispenseNo) {
		this.dispenseNo = dispenseNo;
	}

	//@Column(name = "REQTYPE_CODE", nullable = false, length = 20)
	public String getReqtypeCode() {
		return this.reqtypeCode;
	}

	public void setReqtypeCode(String reqtypeCode) {
		this.reqtypeCode = reqtypeCode;
	}

	//@Column(name = "STOCK_TYPE", length = 1)
	public String getStockType() {
		return this.stockType;
	}

	public void setStockType(String stockType) {
		this.stockType = stockType;
	}

	//@Column(name = "REQUEST_NO", nullable = false, length = 20)
	public String getRequestNo() {
		return this.requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	//@Column(name = "REQUEST_DATE", length = 7)
	public String getRequestDate() {
		return this.requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
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

	//@Column(name = "URGENT_FLG", length = 1)
	public String getUrgentFlg() {
		return this.urgentFlg;
	}

	public void setUrgentFlg(String urgentFlg) {
		this.urgentFlg = urgentFlg;
	}

	//@Column(name = "DESCRIPTION", length = 50)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	//@Column(name = "DISPENSE_DATE", length = 7)
	public String getDispenseDate() {
		return this.dispenseDate;
	}

	public void setDispenseDate(String dispenseDate) {
		this.dispenseDate = dispenseDate;
	}

	//@Column(name = "DISPENSE_USER", length = 20)
	public String getDispenseUser() {
		return this.dispenseUser;
	}

	public void setDispenseUser(String dispenseUser) {
		this.dispenseUser = dispenseUser;
	}

	//@Column(name = "WAREHOUSING_DATE", length = 7)
	public String getWarehousingDate() {
		return this.warehousingDate;
	}

	public void setWarehousingDate(String warehousingDate) {
		this.warehousingDate = warehousingDate;
	}

	//@Column(name = "WAREHOUSING_USER", length = 20)
	public String getWarehousingUser() {
		return this.warehousingUser;
	}

	public void setWarehousingUser(String warehousingUser) {
		this.warehousingUser = warehousingUser;
	}

	//@Column(name = "REASON_CHN_DESC", length = 50)
	public String getReasonChnDesc() {
		return this.reasonChnDesc;
	}

	public void setReasonChnDesc(String reasonChnDesc) {
		this.reasonChnDesc = reasonChnDesc;
	}

	//@Column(name = "UNIT_TYPE", length = 1)
	public String getUnitType() {
		return this.unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	//@Column(name = "UPDATE_FLG", length = 1)
	public String getUpdateFlg() {
		return this.updateFlg;
	}

	public void setUpdateFlg(String updateFlg) {
		this.updateFlg = updateFlg;
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

	public ArrayList<IndDispensed> getIndDispenseds() {
		return indDispenseds;
	}

	public void setIndDispenseds(ArrayList<IndDispensed> indDispenseds) {
		this.indDispenseds = indDispenseds;
	}

	
}