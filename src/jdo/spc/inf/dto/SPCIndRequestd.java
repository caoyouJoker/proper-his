package jdo.spc.inf.dto;




public class SPCIndRequestd implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String requestNo;
	private int seqNo;
	private String orderCode;
	private String batchNo;
	private String validDate;
	private Double qty;
	private String unitCode;
	private Double retailPrice;
	private Double stockPrice;
	private Double actualQty;
	private String updateFlg;
	private String optUser;
	private String optDate;
	private String optTerm;
	private Double verifyinPrice;
	private Integer batchSeq;

	/** default constructor */
	public SPCIndRequestd() {
	}

	public String getOrderCode() {
		return this.orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getBatchNo() {
		return this.batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getValidDate() {
		return this.validDate;
	}

	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}

	public Double getQty() {
		return this.qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public String getUnitCode() {
		return this.unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public Double getRetailPrice() {
		return this.retailPrice;
	}

	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}

	public Double getStockPrice() {
		return this.stockPrice;
	}

	public void setStockPrice(Double stockPrice) {
		this.stockPrice = stockPrice;
	}

	public Double getActualQty() {
		return this.actualQty;
	}

	public void setActualQty(Double actualQty) {
		this.actualQty = actualQty;
	}

	public String getUpdateFlg() {
		return this.updateFlg;
	}

	public void setUpdateFlg(String updateFlg) {
		this.updateFlg = updateFlg;
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

	public Double getVerifyinPrice() {
		return this.verifyinPrice;
	}

	public void setVerifyinPrice(Double verifyinPrice) {
		this.verifyinPrice = verifyinPrice;
	}

	public Integer getBatchSeq() {
		return this.batchSeq;
	}

	public void setBatchSeq(Integer batchSeq) {
		this.batchSeq = batchSeq;
	}

	public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

}