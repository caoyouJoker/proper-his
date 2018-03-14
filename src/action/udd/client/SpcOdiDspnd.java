
package action.udd.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for spcOdiDspnd complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="spcOdiDspnd">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="barCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="barcode1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="barcode2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="barcode3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="batchCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="billFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="boxEslId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cancelrsnCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="caseNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cashierCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cashierDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dcDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dcNsCheckDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dosageQty" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="dosageUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="execDeptCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="execNote" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ibsCaseNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ibsCaseNoSeq" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="intgmedNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="invCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mediQty" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="mediUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsExecCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsExecCodeReal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsExecDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsExecDateReal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsExecDcCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsExecDcDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nurseDispenseFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optTerm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderDatetime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderSeq" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="phaDispenseCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDispenseDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDispenseNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDosageCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDosageDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaRetnCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaRetnDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stopcheckDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stopcheckUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="takemedOrg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totAmt" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="transmitRsnCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="treatEndTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="treatStartTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spcOdiDspnd", propOrder = {
    "barCode",
    "barcode1",
    "barcode2",
    "barcode3",
    "batchCode",
    "billFlg",
    "boxEslId",
    "cancelrsnCode",
    "caseNo",
    "cashierCode",
    "cashierDate",
    "dcDate",
    "dcNsCheckDate",
    "dosageQty",
    "dosageUnit",
    "execDeptCode",
    "execNote",
    "ibsCaseNo",
    "ibsCaseNoSeq",
    "intgmedNo",
    "invCode",
    "mediQty",
    "mediUnit",
    "nsExecCode",
    "nsExecCodeReal",
    "nsExecDate",
    "nsExecDateReal",
    "nsExecDcCode",
    "nsExecDcDate",
    "nsUser",
    "nurseDispenseFlg",
    "optDate",
    "optTerm",
    "optUser",
    "orderCode",
    "orderDate",
    "orderDatetime",
    "orderNo",
    "orderSeq",
    "phaDispenseCode",
    "phaDispenseDate",
    "phaDispenseNo",
    "phaDosageCode",
    "phaDosageDate",
    "phaRetnCode",
    "phaRetnDate",
    "stopcheckDate",
    "stopcheckUser",
    "takemedOrg",
    "totAmt",
    "transmitRsnCode",
    "treatEndTime",
    "treatStartTime",
    "takemedNo"
})
public class SpcOdiDspnd {

    protected String barCode;
    protected String barcode1;
    protected String barcode2;
    protected String barcode3;
    protected String batchCode;
    protected String billFlg;
    protected String boxEslId;
    protected String cancelrsnCode;
    protected String caseNo;
    protected String cashierCode;
    protected String cashierDate;
    protected String dcDate;
    protected String dcNsCheckDate;
    protected double dosageQty;
    protected String dosageUnit;
    protected String execDeptCode;
    protected String execNote;
    protected String ibsCaseNo;
    protected String ibsCaseNoSeq;
    protected String intgmedNo;
    protected String invCode;
    protected double mediQty;
    protected String mediUnit;
    protected String nsExecCode;
    protected String nsExecCodeReal;
    protected String nsExecDate;
    protected String nsExecDateReal;
    protected String nsExecDcCode;
    protected String nsExecDcDate;
    protected String nsUser;
    protected String nurseDispenseFlg;
    protected String optDate;
    protected String optTerm;
    protected String optUser;
    protected String orderCode;
    protected String orderDate;
    protected String orderDatetime;
    protected String orderNo;
    protected int orderSeq;
    protected String phaDispenseCode;
    protected String phaDispenseDate;
    protected String phaDispenseNo;
    protected String phaDosageCode;
    protected String phaDosageDate;
    protected String phaRetnCode;
    protected String phaRetnDate;
    protected String stopcheckDate;
    protected String stopcheckUser;
    protected String takemedOrg;
    protected double totAmt;
    protected String transmitRsnCode;
    protected String treatEndTime;
    protected String treatStartTime;
    
    protected String takemedNo;

    /**
     * Gets the value of the barCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBarCode() {
        return barCode;
    }

    /**
     * Sets the value of the barCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBarCode(String value) {
        this.barCode = value;
    }

    /**
     * Gets the value of the barcode1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBarcode1() {
        return barcode1;
    }

    /**
     * Sets the value of the barcode1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBarcode1(String value) {
        this.barcode1 = value;
    }

    /**
     * Gets the value of the barcode2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBarcode2() {
        return barcode2;
    }

    /**
     * Sets the value of the barcode2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBarcode2(String value) {
        this.barcode2 = value;
    }

    /**
     * Gets the value of the barcode3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBarcode3() {
        return barcode3;
    }

    /**
     * Sets the value of the barcode3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBarcode3(String value) {
        this.barcode3 = value;
    }

    /**
     * Gets the value of the batchCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatchCode() {
        return batchCode;
    }

    /**
     * Sets the value of the batchCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchCode(String value) {
        this.batchCode = value;
    }

    /**
     * Gets the value of the billFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillFlg() {
        return billFlg;
    }

    /**
     * Sets the value of the billFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillFlg(String value) {
        this.billFlg = value;
    }

    /**
     * Gets the value of the boxEslId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBoxEslId() {
        return boxEslId;
    }

    /**
     * Sets the value of the boxEslId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBoxEslId(String value) {
        this.boxEslId = value;
    }

    /**
     * Gets the value of the cancelrsnCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCancelrsnCode() {
        return cancelrsnCode;
    }

    /**
     * Sets the value of the cancelrsnCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCancelrsnCode(String value) {
        this.cancelrsnCode = value;
    }

    /**
     * Gets the value of the caseNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCaseNo() {
        return caseNo;
    }

    /**
     * Sets the value of the caseNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCaseNo(String value) {
        this.caseNo = value;
    }

    /**
     * Gets the value of the cashierCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCashierCode() {
        return cashierCode;
    }

    /**
     * Sets the value of the cashierCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCashierCode(String value) {
        this.cashierCode = value;
    }

    /**
     * Gets the value of the cashierDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCashierDate() {
        return cashierDate;
    }

    /**
     * Sets the value of the cashierDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCashierDate(String value) {
        this.cashierDate = value;
    }

    /**
     * Gets the value of the dcDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDcDate() {
        return dcDate;
    }

    /**
     * Sets the value of the dcDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDcDate(String value) {
        this.dcDate = value;
    }

    /**
     * Gets the value of the dcNsCheckDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDcNsCheckDate() {
        return dcNsCheckDate;
    }

    /**
     * Sets the value of the dcNsCheckDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDcNsCheckDate(String value) {
        this.dcNsCheckDate = value;
    }

    /**
     * Gets the value of the dosageQty property.
     * 
     */
    public double getDosageQty() {
        return dosageQty;
    }

    /**
     * Sets the value of the dosageQty property.
     * 
     */
    public void setDosageQty(double value) {
        this.dosageQty = value;
    }

    /**
     * Gets the value of the dosageUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDosageUnit() {
        return dosageUnit;
    }

    /**
     * Sets the value of the dosageUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDosageUnit(String value) {
        this.dosageUnit = value;
    }

    /**
     * Gets the value of the execDeptCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExecDeptCode() {
        return execDeptCode;
    }

    /**
     * Sets the value of the execDeptCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExecDeptCode(String value) {
        this.execDeptCode = value;
    }

    /**
     * Gets the value of the execNote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExecNote() {
        return execNote;
    }

    /**
     * Sets the value of the execNote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExecNote(String value) {
        this.execNote = value;
    }

    /**
     * Gets the value of the ibsCaseNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIbsCaseNo() {
        return ibsCaseNo;
    }

    /**
     * Sets the value of the ibsCaseNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIbsCaseNo(String value) {
        this.ibsCaseNo = value;
    }

    /**
     * Gets the value of the ibsCaseNoSeq property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIbsCaseNoSeq() {
        return ibsCaseNoSeq;
    }

    /**
     * Sets the value of the ibsCaseNoSeq property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIbsCaseNoSeq(String value) {
        this.ibsCaseNoSeq = value;
    }

    /**
     * Gets the value of the intgmedNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIntgmedNo() {
        return intgmedNo;
    }

    /**
     * Sets the value of the intgmedNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIntgmedNo(String value) {
        this.intgmedNo = value;
    }

    /**
     * Gets the value of the invCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvCode() {
        return invCode;
    }

    /**
     * Sets the value of the invCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvCode(String value) {
        this.invCode = value;
    }

    /**
     * Gets the value of the mediQty property.
     * 
     */
    public double getMediQty() {
        return mediQty;
    }

    /**
     * Sets the value of the mediQty property.
     * 
     */
    public void setMediQty(double value) {
        this.mediQty = value;
    }

    /**
     * Gets the value of the mediUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMediUnit() {
        return mediUnit;
    }

    /**
     * Sets the value of the mediUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMediUnit(String value) {
        this.mediUnit = value;
    }

    /**
     * Gets the value of the nsExecCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsExecCode() {
        return nsExecCode;
    }

    /**
     * Sets the value of the nsExecCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsExecCode(String value) {
        this.nsExecCode = value;
    }

    /**
     * Gets the value of the nsExecCodeReal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsExecCodeReal() {
        return nsExecCodeReal;
    }

    /**
     * Sets the value of the nsExecCodeReal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsExecCodeReal(String value) {
        this.nsExecCodeReal = value;
    }

    /**
     * Gets the value of the nsExecDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsExecDate() {
        return nsExecDate;
    }

    /**
     * Sets the value of the nsExecDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsExecDate(String value) {
        this.nsExecDate = value;
    }

    /**
     * Gets the value of the nsExecDateReal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsExecDateReal() {
        return nsExecDateReal;
    }

    /**
     * Sets the value of the nsExecDateReal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsExecDateReal(String value) {
        this.nsExecDateReal = value;
    }

    /**
     * Gets the value of the nsExecDcCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsExecDcCode() {
        return nsExecDcCode;
    }

    /**
     * Sets the value of the nsExecDcCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsExecDcCode(String value) {
        this.nsExecDcCode = value;
    }

    /**
     * Gets the value of the nsExecDcDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsExecDcDate() {
        return nsExecDcDate;
    }

    /**
     * Sets the value of the nsExecDcDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsExecDcDate(String value) {
        this.nsExecDcDate = value;
    }

    /**
     * Gets the value of the nsUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsUser() {
        return nsUser;
    }

    /**
     * Sets the value of the nsUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsUser(String value) {
        this.nsUser = value;
    }

    /**
     * Gets the value of the nurseDispenseFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNurseDispenseFlg() {
        return nurseDispenseFlg;
    }

    /**
     * Sets the value of the nurseDispenseFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNurseDispenseFlg(String value) {
        this.nurseDispenseFlg = value;
    }

    /**
     * Gets the value of the optDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptDate() {
        return optDate;
    }

    /**
     * Sets the value of the optDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptDate(String value) {
        this.optDate = value;
    }

    /**
     * Gets the value of the optTerm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptTerm() {
        return optTerm;
    }

    /**
     * Sets the value of the optTerm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptTerm(String value) {
        this.optTerm = value;
    }

    /**
     * Gets the value of the optUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptUser() {
        return optUser;
    }

    /**
     * Sets the value of the optUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptUser(String value) {
        this.optUser = value;
    }

    /**
     * Gets the value of the orderCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderCode() {
        return orderCode;
    }

    /**
     * Sets the value of the orderCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderCode(String value) {
        this.orderCode = value;
    }

    /**
     * Gets the value of the orderDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the value of the orderDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderDate(String value) {
        this.orderDate = value;
    }

    /**
     * Gets the value of the orderDatetime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderDatetime() {
        return orderDatetime;
    }

    /**
     * Sets the value of the orderDatetime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderDatetime(String value) {
        this.orderDatetime = value;
    }

    /**
     * Gets the value of the orderNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * Sets the value of the orderNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderNo(String value) {
        this.orderNo = value;
    }

    /**
     * Gets the value of the orderSeq property.
     * 
     */
    public int getOrderSeq() {
        return orderSeq;
    }

    /**
     * Sets the value of the orderSeq property.
     * 
     */
    public void setOrderSeq(int value) {
        this.orderSeq = value;
    }

    /**
     * Gets the value of the phaDispenseCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaDispenseCode() {
        return phaDispenseCode;
    }

    /**
     * Sets the value of the phaDispenseCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaDispenseCode(String value) {
        this.phaDispenseCode = value;
    }

    /**
     * Gets the value of the phaDispenseDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaDispenseDate() {
        return phaDispenseDate;
    }

    /**
     * Sets the value of the phaDispenseDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaDispenseDate(String value) {
        this.phaDispenseDate = value;
    }

    /**
     * Gets the value of the phaDispenseNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaDispenseNo() {
        return phaDispenseNo;
    }

    /**
     * Sets the value of the phaDispenseNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaDispenseNo(String value) {
        this.phaDispenseNo = value;
    }

    /**
     * Gets the value of the phaDosageCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaDosageCode() {
        return phaDosageCode;
    }

    /**
     * Sets the value of the phaDosageCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaDosageCode(String value) {
        this.phaDosageCode = value;
    }

    /**
     * Gets the value of the phaDosageDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaDosageDate() {
        return phaDosageDate;
    }

    /**
     * Sets the value of the phaDosageDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaDosageDate(String value) {
        this.phaDosageDate = value;
    }

    /**
     * Gets the value of the phaRetnCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaRetnCode() {
        return phaRetnCode;
    }

    /**
     * Sets the value of the phaRetnCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaRetnCode(String value) {
        this.phaRetnCode = value;
    }

    /**
     * Gets the value of the phaRetnDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaRetnDate() {
        return phaRetnDate;
    }

    /**
     * Sets the value of the phaRetnDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaRetnDate(String value) {
        this.phaRetnDate = value;
    }

    /**
     * Gets the value of the stopcheckDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStopcheckDate() {
        return stopcheckDate;
    }

    /**
     * Sets the value of the stopcheckDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStopcheckDate(String value) {
        this.stopcheckDate = value;
    }

    /**
     * Gets the value of the stopcheckUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStopcheckUser() {
        return stopcheckUser;
    }

    /**
     * Sets the value of the stopcheckUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStopcheckUser(String value) {
        this.stopcheckUser = value;
    }

    /**
     * Gets the value of the takemedOrg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTakemedOrg() {
        return takemedOrg;
    }

    /**
     * Sets the value of the takemedOrg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTakemedOrg(String value) {
        this.takemedOrg = value;
    }

    /**
     * Gets the value of the totAmt property.
     * 
     */
    public double getTotAmt() {
        return totAmt;
    }

    /**
     * Sets the value of the totAmt property.
     * 
     */
    public void setTotAmt(double value) {
        this.totAmt = value;
    }

    /**
     * Gets the value of the transmitRsnCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransmitRsnCode() {
        return transmitRsnCode;
    }

    /**
     * Sets the value of the transmitRsnCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransmitRsnCode(String value) {
        this.transmitRsnCode = value;
    }

    /**
     * Gets the value of the treatEndTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTreatEndTime() {
        return treatEndTime;
    }

    /**
     * Sets the value of the treatEndTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTreatEndTime(String value) {
        this.treatEndTime = value;
    }

    /**
     * Gets the value of the treatStartTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTreatStartTime() {
        return treatStartTime;
    }

    /**
     * Sets the value of the treatStartTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTreatStartTime(String value) {
        this.treatStartTime = value;
    }

	public String getTakemedNo() {
		return takemedNo;
	}

	public void setTakemedNo(String takemedNo) {
		this.takemedNo = takemedNo;
	}
    
    

}
