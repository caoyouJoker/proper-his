package action.spc.services.dto;


import java.io.Serializable;

public class SpcIndStock implements Serializable  {
	
	/**
	 *   
	 */  
	private static final long serialVersionUID = 1L;
	private String orgCode ;
	private String orderCode ;
	private String orderDesc;   
	private double dosageQty ;
	private String optUser;
	private String optDate;
	private String optTerm;
	
	//扣库级别 根据服务等级选择对应的零售金额列
	private String serviceLevel;
	
	private String sendatcFlg; 
	
	
	private String batchNo;

	public SpcIndStock(){
		
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
	
	public String getOrderDesc() {
		return orderDesc;
	}

	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}

	public double getDosageQty() {
		return dosageQty;
	}
	public void setDosageQty(double dosageQty) {
		this.dosageQty = dosageQty;
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

	public String getServiceLevel() {
		return serviceLevel;
	}

	public void setServiceLevel(String serviceLevel) {
		this.serviceLevel = serviceLevel;
	}

	public String getSendatcFlg() {
		return sendatcFlg;
	}

	public void setSendatcFlg(String sendatcFlg) {
		this.sendatcFlg = sendatcFlg;
	}
	  
	public String getBatchNo() {
		return batchNo;     
	}
	public void setBatchNo(String batchNo) {  
		this.batchNo = batchNo;
	}
	

}
