package jdo.spc.uddinfo;

import java.io.Serializable;

/**
 * 护士单次执行-webService 返回对象
 * @author liyanhui
 *
 */
public class OdiDspndPkVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3123805470555850638L;

	private String caseNo;
	private String orderNo;
	private String orderSeq;
	private String orderDate;
	private String orderDateTime;
	private String startDttm;
	private String endDttm;
	private String orderCode;
	private String barCode1;
	private String barCode2;
	private String barCode3;
	
	public void OidDspndPkVo(){
		
	}
	public String getBarCode1() {
		return barCode1;
	}
	public String getBarCode2() {
		return barCode2;
	}
	public String getBarCode3() {
		return barCode3;
	}
	public void setBarCode1(String barCode1) {
		this.barCode1 = barCode1;
	}
	public void setBarCode2(String barCode2) {
		this.barCode2 = barCode2;
	}
	public void setBarCode3(String barCode3) {
		this.barCode3 = barCode3;
	}
	public String getCaseNo() {
		return caseNo;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderSeq() {
		return orderSeq;
	}
	public void setOrderSeq(String orderSeq) {
		this.orderSeq = orderSeq;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getStartDttm() {
		return startDttm;
	}
	public String getEndDttm() {
		return endDttm;
	}
	public void setStartDttm(String startDttm) {
		this.startDttm = startDttm;
	}
	public void setEndDttm(String endDttm) {
		this.endDttm = endDttm;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public String getOrderDateTime() {
		return orderDateTime;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public void setOrderDateTime(String orderDateTime) {
		this.orderDateTime = orderDateTime;
	}
	
}
