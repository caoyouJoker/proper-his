package jdo.spc.bsm.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: ͬ��his������Ϣҽ��ϸ��</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.09.10 
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement(name = "ROOT") 
public class SPCHisPrescriptionOrder {
	@XmlElement(name = "SEQNO")
	private String seqNo ; //ҽ����ˮ��
	@XmlElement(name = "PHATYPE")
	private String phaType  ;//ҩƷ����
	@XmlElement(name = "ORDERCODE")
	private String orderCode  ;//ҩƷ����
	@XmlElement(name = "ORDERDESC")
	private String orderDesc  ;//ҩƷ����
	@XmlElement(name = "GOODSDESC")
	private String goodsDesc  ;//��Ʒ��
	@XmlElement(name = "SPECIFICATION")
	private String specification  ;//���
	@XmlElement(name = "MEDIQTY")
	private String mediQty  ;//��ҩ����
	@XmlElement(name = "MEDIUNIT")
	private String mediUnit  ;//��ҩ��λ
	@XmlElement(name = "FREQCODE")
	private String freqCode  ;//Ƶ�δ���
	@XmlElement(name = "ROUTECODE")
	private String routeCode  ;//�÷�����
	@XmlElement(name = "TAKEDAYS")
	private String takeDays  ;//�շ�
	@XmlElement(name = "DOSAGEQTY")
	private String dosageQty  ;//��ҩ����
	@XmlElement(name = "DOSAGEUNIT")
	private String dosageUnit  ;//��ҩ��λ
	@XmlElement(name = "DISPENSEQTY")
	private String dispenseQty  ;//��ҩ����
	@XmlElement(name = "DISPENSEUNIT")
	private String dispenseUnit  ;//��ҩ��λ
	@XmlElement(name = "GIVEBOXFLG")
	private String giveBoxFlg  ;//�Ƿ����з�ҩ
	@XmlElement(name = "OWNPRICE")
	private String ownPrice  ;//����
	@XmlElement(name = "OWNAMT")
	private String ownAmt  ;//���
	@XmlElement(name = "CTRLFLG")
	private String ctrlFlg  ;//�Ƿ��龫ҩƷ
	@XmlElement(name = "ORDERCATCODE")
	private String orderCatCode  ;//ҽ������
	@XmlElement(name = "BILLFLG")
	private String billFlg  ;//�Ƿ����շ�
	@XmlElement(name = "RXNO")
	private String rxNo ; //����ǩ��
	@XmlElement(name = "ORDERDATE")
	private String orderDate ; //��������ʱ��
	public String getPhaType() {
		return phaType;
	}
	public void setPhaType(String phaType) {
		this.phaType = phaType;
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
	public String getGoodsDesc() {
		return goodsDesc;
	}
	public void setGoodsDesc(String goodsDesc) {
		this.goodsDesc = goodsDesc;
	}
	public String getSpecification() {
		return specification;
	}
	public void setSpecification(String specification) {
		this.specification = specification;
	}
	public String getMediQty() {
		return mediQty;
	}
	public void setMediQty(String mediQty) {
		this.mediQty = mediQty;
	}
	public String getMediUnit() {
		return mediUnit;
	}
	public void setMediUnit(String mediUnit) {
		this.mediUnit = mediUnit;
	}
	public String getFreqCode() {
		return freqCode;
	}
	public void setFreqCode(String freqCode) {
		this.freqCode = freqCode;
	}
	public String getRouteCode() {
		return routeCode;
	}
	public void setRouteCode(String routeCode) {
		this.routeCode = routeCode;
	}
	public String getTakeDays() {
		return takeDays;
	}
	public void setTakeDays(String takeDays) {
		this.takeDays = takeDays;
	}
	public String getDosageQty() {
		return dosageQty;
	}
	public void setDosageQty(String dosageQty) {
		this.dosageQty = dosageQty;
	}
	public String getDosageUnit() {
		return dosageUnit;
	}
	public void setDosageUnit(String dosageUnit) {
		this.dosageUnit = dosageUnit;
	}
	public String getDispenseQty() {
		return dispenseQty;
	}
	public void setDispenseQty(String dispenseQty) {
		this.dispenseQty = dispenseQty;
	}
	public String getDispenseUnit() {
		return dispenseUnit;
	}
	public void setDispenseUnit(String dispenseUnit) {
		this.dispenseUnit = dispenseUnit;
	}
	public String getGiveBoxFlg() {
		return giveBoxFlg;
	}
	public void setGiveBoxFlg(String giveBoxFlg) {
		this.giveBoxFlg = giveBoxFlg;
	}
	public String getOwnPrice() {
		return ownPrice;
	}
	public void setOwnPrice(String ownPrice) {
		this.ownPrice = ownPrice;
	}
	public String getOwnAmt() {
		return ownAmt;
	}
	public void setOwnAmt(String ownAmt) {
		this.ownAmt = ownAmt;
	}
	public String getCtrlFlg() {
		return ctrlFlg;
	}
	public void setCtrlFlg(String ctrlFlg) {
		this.ctrlFlg = ctrlFlg;
	}
	public String getOrderCatCode() {
		return orderCatCode;
	}
	public void setOrderCatCode(String orderCatCode) {
		this.orderCatCode = orderCatCode;
	}
	public String getBillFlg() {
		return billFlg;
	}
	public void setBillFlg(String billFlg) {
		this.billFlg = billFlg;
	}
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public String getRxNo() {
		return rxNo;
	}
	public void setRxNo(String rxNo) {
		this.rxNo = rxNo;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	
}
