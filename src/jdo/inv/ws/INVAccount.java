package jdo.inv.ws;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
/**
*
* <p>Title: 月结对象</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.08.05 
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD)
public class INVAccount {
	
	private  String  regionCode ;
	private  String  accountNo ;
	private  String  closeDate ;
	private  String  orgCode ;
	private  String  invCode ;
	private  String  supCode ;
	private  double  totalOutQty ;
	private  String  totalUnitCode ;
	private  double  verifyinPrice ;
	private  double  verifyinAmt ;
	private  String  optUser ;
	private  String  optDate ;
	private  String  optTerm ;
	public String getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getCloseDate() {
		return closeDate;
	}
	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getInvCode() {
		return invCode;
	}
	public void setInvCode(String invCode) {
		this.invCode = invCode;
	}
	public String getSupCode() {
		return supCode;
	}
	public void setSupCode(String supCode) {
		this.supCode = supCode;
	}
	public double getTotalOutQty() {
		return totalOutQty;
	}
	public void setTotalOutQty(double totalOutQty) {
		this.totalOutQty = totalOutQty;
	}
	public String getTotalUnitCode() {
		return totalUnitCode;
	}
	public void setTotalUnitCode(String totalUnitCode) {
		this.totalUnitCode = totalUnitCode;
	}
	public double getVerifyinPrice() {
		return verifyinPrice;
	}
	public void setVerifyinPrice(double verifyinPrice) {
		this.verifyinPrice = verifyinPrice;
	}
	public double getVerifyinAmt() {
		return verifyinAmt;
	}
	public void setVerifyinAmt(double verifyinAmt) {
		this.verifyinAmt = verifyinAmt;
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
