
package jdo.pha.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for spcIndRequestd complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="spcIndRequestd">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="actualQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="batchNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="batchSeq" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="optDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optTerm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="requestNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="retailPrice" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="seqNo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="stockPrice" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="unitCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updateFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="validDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="verifyinPrice" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spcIndRequestd", propOrder = {
    "actualQty",
    "batchNo",
    "batchSeq",
    "optDate",
    "optTerm",
    "optUser",
    "orderCode",
    "qty",
    "requestNo",
    "retailPrice",
    "seqNo",
    "stockPrice",
    "unitCode",
    "updateFlg",
    "validDate",
    "verifyinPrice"
})
public class SpcIndRequestd {

    protected Double actualQty;
    protected String batchNo;
    protected Integer batchSeq;
    protected String optDate;
    protected String optTerm;
    protected String optUser;
    protected String orderCode;
    protected Double qty;
    protected String requestNo;
    protected Double retailPrice;
    protected int seqNo;
    protected Double stockPrice;
    protected String unitCode;
    protected String updateFlg;
    protected String validDate;
    protected Double verifyinPrice;

    /**
     * Gets the value of the actualQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getActualQty() {
        return actualQty;
    }

    /**
     * Sets the value of the actualQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setActualQty(Double value) {
        this.actualQty = value;
    }

    /**
     * Gets the value of the batchNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatchNo() {
        return batchNo;
    }

    /**
     * Sets the value of the batchNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchNo(String value) {
        this.batchNo = value;
    }

    /**
     * Gets the value of the batchSeq property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBatchSeq() {
        return batchSeq;
    }

    /**
     * Sets the value of the batchSeq property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBatchSeq(Integer value) {
        this.batchSeq = value;
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
     * Gets the value of the qty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getQty() {
        return qty;
    }

    /**
     * Sets the value of the qty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setQty(Double value) {
        this.qty = value;
    }

    /**
     * Gets the value of the requestNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestNo() {
        return requestNo;
    }

    /**
     * Sets the value of the requestNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestNo(String value) {
        this.requestNo = value;
    }

    /**
     * Gets the value of the retailPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRetailPrice() {
        return retailPrice;
    }

    /**
     * Sets the value of the retailPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRetailPrice(Double value) {
        this.retailPrice = value;
    }

    /**
     * Gets the value of the seqNo property.
     * 
     */
    public int getSeqNo() {
        return seqNo;
    }

    /**
     * Sets the value of the seqNo property.
     * 
     */
    public void setSeqNo(int value) {
        this.seqNo = value;
    }

    /**
     * Gets the value of the stockPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getStockPrice() {
        return stockPrice;
    }

    /**
     * Sets the value of the stockPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setStockPrice(Double value) {
        this.stockPrice = value;
    }

    /**
     * Gets the value of the unitCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitCode() {
        return unitCode;
    }

    /**
     * Sets the value of the unitCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitCode(String value) {
        this.unitCode = value;
    }

    /**
     * Gets the value of the updateFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdateFlg() {
        return updateFlg;
    }

    /**
     * Sets the value of the updateFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdateFlg(String value) {
        this.updateFlg = value;
    }

    /**
     * Gets the value of the validDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidDate() {
        return validDate;
    }

    /**
     * Sets the value of the validDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidDate(String value) {
        this.validDate = value;
    }

    /**
     * Gets the value of the verifyinPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getVerifyinPrice() {
        return verifyinPrice;
    }

    /**
     * Sets the value of the verifyinPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setVerifyinPrice(Double value) {
        this.verifyinPrice = value;
    }

}
