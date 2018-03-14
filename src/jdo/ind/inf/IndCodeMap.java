package jdo.ind.inf;

import java.io.Serializable;
/** 
 * ±‡¬Î∂‘’’VO
 * @author fuwj
 *
 */
public class IndCodeMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String supCode;
	
	private String supOrderCode;
	
	private String orderCode;
	
	private String supplyUnitCode;
	
	private Double conversionRatio;
	
	private String optUser;
	
	private String optDate;
	
	private String optTerm;

	public String getSupCode() {
		return supCode;
	}

	public void setSupCode(String supCode) {
		this.supCode = supCode;
	}

	public String getSupOrderCode() {
		return supOrderCode;
	}

	public void setSupOrderCode(String supOrderCode) {
		this.supOrderCode = supOrderCode;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getSupplyUnitCode() {
		return supplyUnitCode;
	}

	public void setSupplyUnitCode(String supplyUnitCode) {
		this.supplyUnitCode = supplyUnitCode;
	}

	public Double getConversionRatio() {
		return conversionRatio;
	}

	public void setConversionRatio(Double conversionRatio) {
		this.conversionRatio = conversionRatio;
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
