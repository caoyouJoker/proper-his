package jdo.spc.stockinf.dto;


/**
 * <p>
 * Title: indstockD实体
 * </p>
 * 		
 * <p>
 * Description: indstockD实体
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: blueocre
 * </p>
 * 
 * @author fuwj 2013-3-25
 * @version 4.0
 */
public class IndStock implements java.io.Serializable {



	 
	
	private String orgCode;
	private String orderCode;
	private Integer batchSeq;
	
	
	private String batchNo;
	private String validDate;
	private String regionCode;
	private String materialLocCode;
	private String activeFlg;
	private String stockFlg;
	private String readjustpFlg;
	private Double stockQty;
	private Double lastTotstockQty;
	private Double lastTotstockAmt;
	private Double inQty;
	private Double inAmt;
	private Double outQty;
	private Double outAmt;
	private Double checkmodiQty;
	private Double checkmodiAmt;
	private Double verifyinQty;
	private Double verifyinAmt;
	private Double favorQty;
	private Double regressgoodsQty;
	private Double regressgoodsAmt;
	private Double doseageQty;
	private Double dosageAmt;
	private Double regressdrugQty;
	private Double regressdrugAmt;
	private Double freezeTot;
	private Double profitLossAmt;
	private Double verifyinPrice;
	private Double stockinQty;
	private Double stockinAmt;
	private Double stockoutQty;
	private Double stockoutAmt;
	private String optUser;
	private String optDate;
	private String optTerm;
	private Double requestInQty;
	private Double requestInAmt;
	private Double requestOutQty;
	private Double requestOutAmt;
	private Double gifInQty;
	private Double gifInAmt;
	private Double gifOutQty;
	private Double gifOutAmt;
	private Double retInQty;
	private Double retInAmt;
	private Double retOutQty;
	private Double retOutAmt;
	private Double wasOutQty;
	private Double wasOutAmt;
	private Double thoOutQty;
	private Double thoOutAmt;
	private Double thiInQty;
	private Double thiInAmt;
	private Double cosOutQty;
	private Double cosOutAmt;
	private Double retailPrice;
	private String isUpdate;
	private String supCode;

	// Constructors

	/** default constructor */
	public IndStock() {
	}

	/** minimal constructor */
	public IndStock( String optUser, String optDate, String optTerm) {
		 
		this.optUser = optUser;
		this.optDate = optDate;
		this.optTerm = optTerm;
	}

	/** full constructor */
	public IndStock(String batchNo, String validDate,
			String regionCode, String materialLocCode, String activeFlg,
			String stockFlg, String readjustpFlg, Double stockQty,
			Double lastTotstockQty, Double lastTotstockAmt, Double inQty,
			Double inAmt, Double outQty, Double outAmt, Double checkmodiQty,
			Double checkmodiAmt, Double verifyinQty, Double verifyinAmt,
			Double favorQty, Double regressgoodsQty, Double regressgoodsAmt,
			Double doseageQty, Double dosageAmt, Double regressdrugQty,
			Double regressdrugAmt, Double freezeTot, Double profitLossAmt,
			Double verifyinPrice, Double stockinQty, Double stockinAmt,
			Double stockoutQty, Double stockoutAmt, String optUser,
			String optDate, String optTerm, Double requestInQty,
			Double requestInAmt, Double requestOutQty, Double requestOutAmt,
			Double gifInQty, Double gifInAmt, Double gifOutQty,
			Double gifOutAmt, Double retInQty, Double retInAmt,
			Double retOutQty, Double retOutAmt, Double wasOutQty,
			Double wasOutAmt, Double thoOutQty, Double thoOutAmt,
			Double thiInQty, Double thiInAmt, Double cosOutQty,
			Double cosOutAmt, Double retailPrice, String isUpdate,
			String supCode) {
	 
		this.batchNo = batchNo;
		this.validDate = validDate;
		this.regionCode = regionCode;
		this.materialLocCode = materialLocCode;
		this.activeFlg = activeFlg;
		this.stockFlg = stockFlg;
		this.readjustpFlg = readjustpFlg;
		this.stockQty = stockQty;
		this.lastTotstockQty = lastTotstockQty;
		this.lastTotstockAmt = lastTotstockAmt;
		this.inQty = inQty;
		this.inAmt = inAmt;
		this.outQty = outQty;
		this.outAmt = outAmt;
		this.checkmodiQty = checkmodiQty;
		this.checkmodiAmt = checkmodiAmt;
		this.verifyinQty = verifyinQty;
		this.verifyinAmt = verifyinAmt;
		this.favorQty = favorQty;
		this.regressgoodsQty = regressgoodsQty;
		this.regressgoodsAmt = regressgoodsAmt;
		this.doseageQty = doseageQty;
		this.dosageAmt = dosageAmt;
		this.regressdrugQty = regressdrugQty;
		this.regressdrugAmt = regressdrugAmt;
		this.freezeTot = freezeTot;
		this.profitLossAmt = profitLossAmt;
		this.verifyinPrice = verifyinPrice;
		this.stockinQty = stockinQty;
		this.stockinAmt = stockinAmt;
		this.stockoutQty = stockoutQty;
		this.stockoutAmt = stockoutAmt;
		this.optUser = optUser;
		this.optDate = optDate;
		this.optTerm = optTerm;
		this.requestInQty = requestInQty;
		this.requestInAmt = requestInAmt;
		this.requestOutQty = requestOutQty;
		this.requestOutAmt = requestOutAmt;
		this.gifInQty = gifInQty;
		this.gifInAmt = gifInAmt;
		this.gifOutQty = gifOutQty;
		this.gifOutAmt = gifOutAmt;
		this.retInQty = retInQty;
		this.retInAmt = retInAmt;
		this.retOutQty = retOutQty;
		this.retOutAmt = retOutAmt;
		this.wasOutQty = wasOutQty;
		this.wasOutAmt = wasOutAmt;
		this.thoOutQty = thoOutQty;
		this.thoOutAmt = thoOutAmt;
		this.thiInQty = thiInQty;
		this.thiInAmt = thiInAmt;
		this.cosOutQty = cosOutQty;
		this.cosOutAmt = cosOutAmt;
		this.retailPrice = retailPrice;
		this.isUpdate = isUpdate;
		this.supCode = supCode;
	}

	// Property accessors
 
	//@Column(name = "BATCH_NO", length = 20)
	public String getBatchNo() {
		return this.batchNo;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public Integer getBatchSeq() {
		return batchSeq;
	}

	public void setBatchSeq(Integer batchSeq) {
		this.batchSeq = batchSeq;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	//@Temporal(TemporalType.DATE)
	//@Column(name = "VALID_DATE", length = 7)
	public String getValidDate() {
		return this.validDate;
	}

	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}

	//@Column(name = "REGION_CODE", length = 20)
	public String getRegionCode() {
		return this.regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	//@Column(name = "MATERIAL_LOC_CODE", length = 20)
	public String getMaterialLocCode() {
		return this.materialLocCode;
	}

	public void setMaterialLocCode(String materialLocCode) {
		this.materialLocCode = materialLocCode;
	}

	//@Column(name = "ACTIVE_FLG", length = 1)
	public String getActiveFlg() {
		return this.activeFlg;
	}

	public void setActiveFlg(String activeFlg) {
		this.activeFlg = activeFlg;
	}

	//@Column(name = "STOCK_FLG", length = 1)
	public String getStockFlg() {
		return this.stockFlg;
	}

	public void setStockFlg(String stockFlg) {
		this.stockFlg = stockFlg;
	}

	//@Column(name = "READJUSTP_FLG", length = 1)
	public String getReadjustpFlg() {
		return this.readjustpFlg;
	}

	public void setReadjustpFlg(String readjustpFlg) {
		this.readjustpFlg = readjustpFlg;
	}

	//@Column(name = "STOCK_QTY", precision = 12, scale = 3)
	public Double getStockQty() {
		return this.stockQty;
	}

	public void setStockQty(Double stockQty) {
		this.stockQty = stockQty;
	}

	//@Column(name = "LAST_TOTSTOCK_QTY", precision = 12, scale = 3)
	public Double getLastTotstockQty() {
		return this.lastTotstockQty;
	}

	public void setLastTotstockQty(Double lastTotstockQty) {
		this.lastTotstockQty = lastTotstockQty;
	}

	//@Column(name = "LAST_TOTSTOCK_AMT", precision = 12)
	public Double getLastTotstockAmt() {
		return this.lastTotstockAmt;
	}

	public void setLastTotstockAmt(Double lastTotstockAmt) {
		this.lastTotstockAmt = lastTotstockAmt;
	}

	//@Column(name = "IN_QTY", precision = 12, scale = 3)
	public Double getInQty() {
		return this.inQty;
	}

	public void setInQty(Double inQty) {
		this.inQty = inQty;
	}

	//@Column(name = "IN_AMT", precision = 12)
	public Double getInAmt() {
		return this.inAmt;
	}

	public void setInAmt(Double inAmt) {
		this.inAmt = inAmt;
	}

	//@Column(name = "OUT_QTY", precision = 12, scale = 3)
	public Double getOutQty() {
		return this.outQty;
	}

	public void setOutQty(Double outQty) {
		this.outQty = outQty;
	}

	//@Column(name = "OUT_AMT", precision = 12)
	public Double getOutAmt() {
		return this.outAmt;
	}

	public void setOutAmt(Double outAmt) {
		this.outAmt = outAmt;
	}

	//@Column(name = "CHECKMODI_QTY", precision = 12, scale = 3)
	public Double getCheckmodiQty() {
		return this.checkmodiQty;
	}

	public void setCheckmodiQty(Double checkmodiQty) {
		this.checkmodiQty = checkmodiQty;
	}

	//@Column(name = "CHECKMODI_AMT", precision = 12)
	public Double getCheckmodiAmt() {
		return this.checkmodiAmt;
	}

	public void setCheckmodiAmt(Double checkmodiAmt) {
		this.checkmodiAmt = checkmodiAmt;
	}

	//@Column(name = "VERIFYIN_QTY", precision = 12, scale = 3)
	public Double getVerifyinQty() {
		return this.verifyinQty;
	}

	public void setVerifyinQty(Double verifyinQty) {
		this.verifyinQty = verifyinQty;
	}

	//@Column(name = "VERIFYIN_AMT", precision = 12)
	public Double getVerifyinAmt() {
		return this.verifyinAmt;
	}

	public void setVerifyinAmt(Double verifyinAmt) {
		this.verifyinAmt = verifyinAmt;
	}

	//@Column(name = "FAVOR_QTY", precision = 12, scale = 3)
	public Double getFavorQty() {
		return this.favorQty;
	}

	public void setFavorQty(Double favorQty) {
		this.favorQty = favorQty;
	}

	//(name = "REGRESSGOODS_QTY", precision = 12, scale = 3)
	public Double getRegressgoodsQty() {
		return this.regressgoodsQty;
	}

	public void setRegressgoodsQty(Double regressgoodsQty) {
		this.regressgoodsQty = regressgoodsQty;
	}

	//@Column(name = "REGRESSGOODS_AMT", precision = 12)
	public Double getRegressgoodsAmt() {
		return this.regressgoodsAmt;
	}

	public void setRegressgoodsAmt(Double regressgoodsAmt) {
		this.regressgoodsAmt = regressgoodsAmt;
	}

	//@Column(name = "DOSEAGE_QTY", precision = 8, scale = 3)
	public Double getDoseageQty() {
		return this.doseageQty;
	}

	public void setDoseageQty(Double doseageQty) {
		this.doseageQty = doseageQty;
	}

	//@Column(name = "DOSAGE_AMT", precision = 12)
	public Double getDosageAmt() {
		return this.dosageAmt;
	}

	public void setDosageAmt(Double dosageAmt) {
		this.dosageAmt = dosageAmt;
	}

	//@Column(name = "REGRESSDRUG_QTY", precision = 12, scale = 3)
	public Double getRegressdrugQty() {
		return this.regressdrugQty;
	}

	public void setRegressdrugQty(Double regressdrugQty) {
		this.regressdrugQty = regressdrugQty;
	}

	//@Column(name = "REGRESSDRUG_AMT", precision = 12)
	public Double getRegressdrugAmt() {
		return this.regressdrugAmt;
	}

	public void setRegressdrugAmt(Double regressdrugAmt) {
		this.regressdrugAmt = regressdrugAmt;
	}

	//@Column(name = "FREEZE_TOT", precision = 12, scale = 3)
	public Double getFreezeTot() {
		return this.freezeTot;
	}

	public void setFreezeTot(Double freezeTot) {
		this.freezeTot = freezeTot;
	}

	//@Column(name = "PROFIT_LOSS_AMT", precision = 12)
	public Double getProfitLossAmt() {
		return this.profitLossAmt;
	}

	public void setProfitLossAmt(Double profitLossAmt) {
		this.profitLossAmt = profitLossAmt;
	}

	//@Column(name = "VERIFYIN_PRICE", precision = 10, scale = 4)
	public Double getVerifyinPrice() {
		return this.verifyinPrice;
	}

	public void setVerifyinPrice(Double verifyinPrice) {
		this.verifyinPrice = verifyinPrice;
	}

	//@Column(name = "STOCKIN_QTY", precision = 12, scale = 3)
	public Double getStockinQty() {
		return this.stockinQty;
	}

	public void setStockinQty(Double stockinQty) {
		this.stockinQty = stockinQty;
	}

	//@Column(name = "STOCKIN_AMT", precision = 12)
	public Double getStockinAmt() {
		return this.stockinAmt;
	}

	public void setStockinAmt(Double stockinAmt) {
		this.stockinAmt = stockinAmt;
	}

	//@Column(name = "STOCKOUT_QTY", precision = 12, scale = 3)
	public Double getStockoutQty() {
		return this.stockoutQty;
	}

	public void setStockoutQty(Double stockoutQty) {
		this.stockoutQty = stockoutQty;
	}

	//@Column(name = "STOCKOUT_AMT", precision = 12)
	public Double getStockoutAmt() {
		return this.stockoutAmt;
	}

	public void setStockoutAmt(Double stockoutAmt) {
		this.stockoutAmt = stockoutAmt;
	}

	//@Column(name = "OPT_USER", nullable = false, length = 20)
	public String getOptUser() {
		return this.optUser;
	}

	public void setOptUser(String optUser) {
		this.optUser = optUser;
	}

	//@Temporal(TemporalType.DATE)
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

	//@Column(name = "REQUEST_IN_QTY", precision = 12, scale = 3)
	public Double getRequestInQty() {
		return this.requestInQty;
	}

	public void setRequestInQty(Double requestInQty) {
		this.requestInQty = requestInQty;
	}

	//@Column(name = "REQUEST_IN_AMT", precision = 12)
	public Double getRequestInAmt() {
		return this.requestInAmt;
	}

	public void setRequestInAmt(Double requestInAmt) {
		this.requestInAmt = requestInAmt;
	}

	//@Column(name = "REQUEST_OUT_QTY", precision = 12, scale = 3)
	public Double getRequestOutQty() {
		return this.requestOutQty;
	}

	public void setRequestOutQty(Double requestOutQty) {
		this.requestOutQty = requestOutQty;
	}

	//@Column(name = "REQUEST_OUT_AMT", precision = 12)
	public Double getRequestOutAmt() {
		return this.requestOutAmt;
	}

	public void setRequestOutAmt(Double requestOutAmt) {
		this.requestOutAmt = requestOutAmt;
	}

	//@Column(name = "GIF_IN_QTY", precision = 12, scale = 3)
	public Double getGifInQty() {
		return this.gifInQty;
	}

	public void setGifInQty(Double gifInQty) {
		this.gifInQty = gifInQty;
	}

	//@Column(name = "GIF_IN_AMT", precision = 12)
	public Double getGifInAmt() {
		return this.gifInAmt;
	}

	public void setGifInAmt(Double gifInAmt) {
		this.gifInAmt = gifInAmt;
	}

	//@Column(name = "GIF_OUT_QTY", precision = 12, scale = 3)
	public Double getGifOutQty() {
		return this.gifOutQty;
	}

	public void setGifOutQty(Double gifOutQty) {
		this.gifOutQty = gifOutQty;
	}

	//@Column(name = "GIF_OUT_AMT", precision = 12)
	public Double getGifOutAmt() {
		return this.gifOutAmt;
	}

	public void setGifOutAmt(Double gifOutAmt) {
		this.gifOutAmt = gifOutAmt;
	}

	//@Column(name = "RET_IN_QTY", precision = 12, scale = 3)
	public Double getRetInQty() {
		return this.retInQty;
	}

	public void setRetInQty(Double retInQty) {
		this.retInQty = retInQty;
	}

	//@Column(name = "RET_IN_AMT", precision = 12)
	public Double getRetInAmt() {
		return this.retInAmt;
	}

	public void setRetInAmt(Double retInAmt) {
		this.retInAmt = retInAmt;
	}

	//@Column(name = "RET_OUT_QTY", precision = 12, scale = 3)
	public Double getRetOutQty() {
		return this.retOutQty;
	}

	public void setRetOutQty(Double retOutQty) {
		this.retOutQty = retOutQty;
	}

	//@Column(name = "RET_OUT_AMT", precision = 12)
	public Double getRetOutAmt() {
		return this.retOutAmt;
	}

	public void setRetOutAmt(Double retOutAmt) {
		this.retOutAmt = retOutAmt;
	}

	//@Column(name = "WAS_OUT_QTY", precision = 12, scale = 3)
	public Double getWasOutQty() {
		return this.wasOutQty;
	}

	public void setWasOutQty(Double wasOutQty) {
		this.wasOutQty = wasOutQty;
	}

	//@Column(name = "WAS_OUT_AMT", precision = 12)
	public Double getWasOutAmt() {
		return this.wasOutAmt;
	}

	public void setWasOutAmt(Double wasOutAmt) {
		this.wasOutAmt = wasOutAmt;
	}

	//@Column(name = "THO_OUT_QTY", precision = 12, scale = 3)
	public Double getThoOutQty() {
		return this.thoOutQty;
	}

	public void setThoOutQty(Double thoOutQty) {
		this.thoOutQty = thoOutQty;
	}

	//@Column(name = "THO_OUT_AMT", precision = 12)
	public Double getThoOutAmt() {
		return this.thoOutAmt;
	}

	public void setThoOutAmt(Double thoOutAmt) {
		this.thoOutAmt = thoOutAmt;
	}

	//@Column(name = "THI_IN_QTY", precision = 12, scale = 3)
	public Double getThiInQty() {
		return this.thiInQty;
	}

	public void setThiInQty(Double thiInQty) {
		this.thiInQty = thiInQty;
	}

	//@Column(name = "THI_IN_AMT", precision = 12)
	public Double getThiInAmt() {
		return this.thiInAmt;
	}

	public void setThiInAmt(Double thiInAmt) {
		this.thiInAmt = thiInAmt;
	}

	//@Column(name = "COS_OUT_QTY", precision = 12, scale = 3)
	public Double getCosOutQty() {
		return this.cosOutQty;
	}

	public void setCosOutQty(Double cosOutQty) {
		this.cosOutQty = cosOutQty;
	}

	//@Column(name = "COS_OUT_AMT", precision = 12)
	public Double getCosOutAmt() {
		return this.cosOutAmt;
	}

	public void setCosOutAmt(Double cosOutAmt) {
		this.cosOutAmt = cosOutAmt;
	}

	//@Column(name = "RETAIL_PRICE", precision = 10, scale = 4)
	public Double getRetailPrice() {
		return this.retailPrice;
	}

	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}

	//@Column(name = "IS_UPDATE", length = 20)
	public String getIsUpdate() {
		return this.isUpdate;
	}

	public void setIsUpdate(String isUpdate) {
		this.isUpdate = isUpdate;
	}

	//@Column(name = "SUP_CODE", length = 20)
	public String getSupCode() {
		return this.supCode;
	}

	public void setSupCode(String supCode) {
		this.supCode = supCode;
	}

}