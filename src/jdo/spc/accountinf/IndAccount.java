package jdo.spc.accountinf;



/**
 * ÎïÁªÍø½áËãvo 
 */
public class IndAccount implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -8439485914091372324L;
	private String closeDate;
	private String orgCode;
	private String orderCode;
	
	private Double lastOddQty;
	private Double outQty;
	private Double totalOutQty;
	private String totalUnitCode;
	private Double verifyinPrice;
	private Double verifyinAmt;
	private Long accountQty;
	private String accountUnitCode;
	private Double oddQty;
	private Double oddAmt;
	private String optUser;
	private String optDate;
	private String optTerm;
	private String isUpdate;

	// Constructors

	/** default constructor */
	public IndAccount() {
	}

	 

	public String getCloseDate() {
		return this.closeDate;
	}

	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}

	public String getOrgCode() {
		return this.orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrderCode() {
		return this.orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	 
 

	public Double getLastOddQty() {
		return this.lastOddQty;
	}

	public void setLastOddQty(Double lastOddQty) {
		this.lastOddQty = lastOddQty;
	}

	public Double getOutQty() {
		return this.outQty;
	}

	public void setOutQty(Double outQty) {
		this.outQty = outQty;
	}

	public Double getTotalOutQty() {
		return this.totalOutQty;
	}

	public void setTotalOutQty(Double totalOutQty) {
		this.totalOutQty = totalOutQty;
	}

	public String getTotalUnitCode() {
		return this.totalUnitCode;
	}

	public void setTotalUnitCode(String totalUnitCode) {
		this.totalUnitCode = totalUnitCode;
	}

	public Double getVerifyinPrice() {
		return this.verifyinPrice;
	}

	public void setVerifyinPrice(Double verifyinPrice) {
		this.verifyinPrice = verifyinPrice;
	}

	public Double getVerifyinAmt() {
		return this.verifyinAmt;
	}

	public void setVerifyinAmt(Double verifyinAmt) {
		this.verifyinAmt = verifyinAmt;
	}

	public Long getAccountQty() {
		return this.accountQty;
	}

	public void setAccountQty(Long accountQty) {
		this.accountQty = accountQty;
	}

	public String getAccountUnitCode() {
		return this.accountUnitCode;
	}

	public void setAccountUnitCode(String accountUnitCode) {
		this.accountUnitCode = accountUnitCode;
	}

	public Double getOddQty() {
		return this.oddQty;
	}

	public void setOddQty(Double oddQty) {
		this.oddQty = oddQty;
	}

	public Double getOddAmt() {
		return this.oddAmt;
	}

	public void setOddAmt(Double oddAmt) {
		this.oddAmt = oddAmt;
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

	public String getIsUpdate() {
		return this.isUpdate;
	}

	public void setIsUpdate(String isUpdate) {
		this.isUpdate = isUpdate;
	}

}