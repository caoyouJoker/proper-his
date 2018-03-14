package jdo.spc.accountinf;


// default package



public class IndDispensed implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String dispenseNo;
	private int seqNo;

	private int requestSeq;
	private String orderCode;
	private Integer batchSeq;
	private String batchNo;
	private String validDate;
	private Double qty;
	private String unitCode;
	private Double retailPrice;
	private Double stockPrice;
	private Double actualQty;
	private String phaType;
	private String optUser;
	private String optDate;
	private String optTerm;
	private Double verifyinPrice;
	private String isBoxed;
	private String boxedUser;
	private String boxedDate;
	private String boxEslId;
	private String isPutaway;
	private String putawayUser;
	private String putawayDate;
	private Double acumPackQty;
	private Double acumOutboundQty;
	private Double acumStoreQty;
	private String isStore;
	
	//DOSAGE_QTY
	private Double dosageQty ;

	// Constructors

	/** default constructor */
	public IndDispensed() {
	}

	public String getDispenseNo() {
		return dispenseNo;
	}

	public void setDispenseNo(String dispenseNo) {
		this.dispenseNo = dispenseNo;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	//@Column(name = "REQUEST_SEQ", nullable = false, precision = 3, scale = 0)
	public int getRequestSeq() {
		return this.requestSeq;
	}

	public void setRequestSeq(int requestSeq) {
		this.requestSeq = requestSeq;
	}

	//@Column(name = "ORDER_CODE", length = 20)
	public String getOrderCode() {
		return this.orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	//@Column(name = "BATCH_SEQ", precision = 5, scale = 0)
	public Integer getBatchSeq() {
		return this.batchSeq;
	}

	public void setBatchSeq(Integer batchSeq) {
		this.batchSeq = batchSeq;
	}

	//@Column(name = "BATCH_NO", length = 20)
	public String getBatchNo() {
		return this.batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	//@Column(name = "VALID_DATE", length = 7)
	public String getValidDate() {
		return this.validDate;
	}

	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}

	//@Column(name = "QTY", precision = 10, scale = 4)
	public Double getQty() {
		return this.qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	//@Column(name = "UNIT_CODE", length = 20)
	public String getUnitCode() {
		return this.unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	//@Column(name = "RETAIL_PRICE", precision = 10, scale = 4)
	public Double getRetailPrice() {
		return this.retailPrice;
	}

	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}

	//@Column(name = "STOCK_PRICE", precision = 10, scale = 4)
	public Double getStockPrice() {
		return this.stockPrice;
	}

	public void setStockPrice(Double stockPrice) {
		this.stockPrice = stockPrice;
	}

	//@Column(name = "ACTUAL_QTY", precision = 10, scale = 4)
	public Double getActualQty() {
		return this.actualQty;
	}

	public void setActualQty(Double actualQty) {
		this.actualQty = actualQty;
	}

	//@Column(name = "PHA_TYPE", length = 1)
	public String getPhaType() {
		return this.phaType;
	}

	public void setPhaType(String phaType) {
		this.phaType = phaType;
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

	//@Column(name = "VERIFYIN_PRICE", precision = 10, scale = 4)
	public Double getVerifyinPrice() {
		return this.verifyinPrice;
	}

	public void setVerifyinPrice(Double verifyinPrice) {
		this.verifyinPrice = verifyinPrice;
	}

	//@Column(name = "IS_BOXED", nullable = false, length = 1)
	public String getIsBoxed() {
		return this.isBoxed;
	}

	public void setIsBoxed(String isBoxed) {
		this.isBoxed = isBoxed;
	}

	//@Column(name = "BOXED_USER", length = 20)
	public String getBoxedUser() {
		return this.boxedUser;
	}

	public void setBoxedUser(String boxedUser) {
		this.boxedUser = boxedUser;
	}

	//@Column(name = "BOXED_DATE", length = 7)
	public String getBoxedDate() {
		return this.boxedDate;
	}

	public void setBoxedDate(String boxedDate) {
		this.boxedDate = boxedDate;
	}

	//@Column(name = "BOX_ESL_ID", length = 20)
	public String getBoxEslId() {
		return this.boxEslId;
	}

	public void setBoxEslId(String boxEslId) {
		this.boxEslId = boxEslId;
	}

	//@Column(name = "IS_PUTAWAY", nullable = false, length = 1)
	public String getIsPutaway() {
		return this.isPutaway;
	}

	public void setIsPutaway(String isPutaway) {
		this.isPutaway = isPutaway;
	}

	//@Column(name = "PUTAWAY_USER", length = 20)
	public String getPutawayUser() {
		return this.putawayUser;
	}

	public void setPutawayUser(String putawayUser) {
		this.putawayUser = putawayUser;
	}

	//@Column(name = "PUTAWAY_DATE", length = 7)
	public String getPutawayDate() {
		return this.putawayDate;
	}

	public void setPutawayDate(String putawayDate) {
		this.putawayDate = putawayDate;
	}

	//@Column(name = "ACUM_PACK_QTY", precision = 10, scale = 4)
	public Double getAcumPackQty() {
		return this.acumPackQty;
	}

	public void setAcumPackQty(Double acumPackQty) {
		this.acumPackQty = acumPackQty;
	}

	//@Column(name = "ACUM_OUTBOUND_QTY", precision = 10, scale = 4)
	public Double getAcumOutboundQty() {
		return this.acumOutboundQty;
	}

	public void setAcumOutboundQty(Double acumOutboundQty) {
		this.acumOutboundQty = acumOutboundQty;
	}

	//@Column(name = "ACUM_STORE_QTY", precision = 10, scale = 4)
	public Double getAcumStoreQty() {
		return this.acumStoreQty;
	}

	public void setAcumStoreQty(Double acumStoreQty) {
		this.acumStoreQty = acumStoreQty;
	}

	//@Column(name = "IS_STORE", length = 1)
	public String getIsStore() {
		return this.isStore;
	}

	public void setIsStore(String isStore) {
		this.isStore = isStore;
	}

	public Double getDosageQty() {
		return dosageQty;
	}

	public void setDosageQty(Double dosageQty) {
		this.dosageQty = dosageQty;
	}
	
	

}