package jdo.spc.stockinf.dto;

import java.io.Serializable;

/**
 * <p>
 * Title: ind_agent实体
 * </p>
 * 		
 * <p>
 * Description: ind_agent实体
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
public class IndAgent implements Serializable {
	
	private String orderCode;
	private String mainFlg;
	private String contractNo;
	private Double contractPrice;
	private String lastOrderDate;
	private Double lastOrderQty;
	private Double lastOrderPrice;
	private String lastOrderNo;
	private String lastVerifyDate;
	private Double lastVerifyPrice;
	private String optUser;
	private String optDate;
	private String optTerm;
	private String supCode;
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getMainFlg() {
		return mainFlg;
	}
	public void setMainFlg(String mainFlg) {
		this.mainFlg = mainFlg;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public Double getContractPrice() {
		return contractPrice;
	}
	public void setContractPrice(Double contractPrice) {
		this.contractPrice = contractPrice;
	}
	public String getLastOrderDate() {
		return lastOrderDate;
	}
	public void setLastOrderDate(String lastOrderDate) {
		this.lastOrderDate = lastOrderDate;
	}
	public Double getLastOrderQty() {
		return lastOrderQty;
	}
	public void setLastOrderQty(Double lastOrderQty) {
		this.lastOrderQty = lastOrderQty;
	}
	public Double getLastOrderPrice() {
		return lastOrderPrice;
	}
	public void setLastOrderPrice(Double lastOrderPrice) {
		this.lastOrderPrice = lastOrderPrice;
	}
	public String getLastOrderNo() {
		return lastOrderNo;
	}
	public void setLastOrderNo(String lastOrderNo) {
		this.lastOrderNo = lastOrderNo;
	}
	public String getLastVerifyDate() {
		return lastVerifyDate;
	}
	public void setLastVerifyDate(String lastVerifyDate) {
		this.lastVerifyDate = lastVerifyDate;
	}
	public Double getLastVerifyPrice() {
		return lastVerifyPrice;
	}
	public void setLastVerifyPrice(Double lastVerifyPrice) {
		this.lastVerifyPrice = lastVerifyPrice;
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
	public String getSupCode() {
		return supCode;
	}
	public void setSupCode(String supCode) {
		this.supCode = supCode;
	}
	
}
