
package action.spc.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for indDispensed complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="indDispensed">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="actualQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="acumOutboundQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="acumPackQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="acumStoreQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="batchNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="batchSeq" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="boxEslId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="boxedDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="boxedUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dispenseNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dosageQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="isBoxed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isPutaway" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isStore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optTerm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="putawayDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="putawayUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="requestSeq" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="retailPrice" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="seqNo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="stockPrice" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="unitCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "indDispensed", propOrder = {
    "actualQty",
    "acumOutboundQty",
    "acumPackQty",
    "acumStoreQty",
    "batchNo",
    "batchSeq",
    "boxEslId",
    "boxedDate",
    "boxedUser",
    "dispenseNo",
    "dosageQty",
    "isBoxed",
    "isPutaway",
    "isStore",
    "optDate",
    "optTerm",
    "optUser",
    "orderCode",
    "phaType",
    "putawayDate",
    "putawayUser",
    "qty",
    "requestSeq",
    "retailPrice",
    "seqNo",
    "stockPrice",
    "unitCode",
    "validDate",
    "verifyinPrice"
})
public class IndDispensed {

    protected Double actualQty;
    protected Double acumOutboundQty;
    protected Double acumPackQty;
    protected Double acumStoreQty;
    protected String batchNo;
    protected Integer batchSeq;
    protected String boxEslId;
    protected String boxedDate;
    protected String boxedUser;
    protected String dispenseNo;
    protected Double dosageQty;
    protected String isBoxed;
    protected String isPutaway;
    protected String isStore;
    protected String optDate;
    protected String optTerm;
    protected String optUser;
    protected String orderCode;
    protected String phaType;
    protected String putawayDate;
    protected String putawayUser;
    protected Double qty;
    protected int requestSeq;
    protected Double retailPrice;
    protected int seqNo;
    protected Double stockPrice;
    protected String unitCode;
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
     * Gets the value of the acumOutboundQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getAcumOutboundQty() {
        return acumOutboundQty;
    }

    /**
     * Sets the value of the acumOutboundQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAcumOutboundQty(Double value) {
        this.acumOutboundQty = value;
    }

    /**
     * Gets the value of the acumPackQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getAcumPackQty() {
        return acumPackQty;
    }

    /**
     * Sets the value of the acumPackQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAcumPackQty(Double value) {
        this.acumPackQty = value;
    }

    /**
     * Gets the value of the acumStoreQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getAcumStoreQty() {
        return acumStoreQty;
    }

    /**
     * Sets the value of the acumStoreQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAcumStoreQty(Double value) {
        this.acumStoreQty = value;
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
     * Gets the value of the boxedDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBoxedDate() {
        return boxedDate;
    }

    /**
     * Sets the value of the boxedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBoxedDate(String value) {
        this.boxedDate = value;
    }

    /**
     * Gets the value of the boxedUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBoxedUser() {
        return boxedUser;
    }

    /**
     * Sets the value of the boxedUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBoxedUser(String value) {
        this.boxedUser = value;
    }

    /**
     * Gets the value of the dispenseNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDispenseNo() {
        return dispenseNo;
    }

    /**
     * Sets the value of the dispenseNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDispenseNo(String value) {
        this.dispenseNo = value;
    }

    /**
     * Gets the value of the dosageQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDosageQty() {
        return dosageQty;
    }

    /**
     * Sets the value of the dosageQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDosageQty(Double value) {
        this.dosageQty = value;
    }

    /**
     * Gets the value of the isBoxed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsBoxed() {
        return isBoxed;
    }

    /**
     * Sets the value of the isBoxed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsBoxed(String value) {
        this.isBoxed = value;
    }

    /**
     * Gets the value of the isPutaway property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsPutaway() {
        return isPutaway;
    }

    /**
     * Sets the value of the isPutaway property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsPutaway(String value) {
        this.isPutaway = value;
    }

    /**
     * Gets the value of the isStore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsStore() {
        return isStore;
    }

    /**
     * Sets the value of the isStore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsStore(String value) {
        this.isStore = value;
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
     * Gets the value of the phaType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaType() {
        return phaType;
    }

    /**
     * Sets the value of the phaType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaType(String value) {
        this.phaType = value;
    }

    /**
     * Gets the value of the putawayDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPutawayDate() {
        return putawayDate;
    }

    /**
     * Sets the value of the putawayDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPutawayDate(String value) {
        this.putawayDate = value;
    }

    /**
     * Gets the value of the putawayUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPutawayUser() {
        return putawayUser;
    }

    /**
     * Sets the value of the putawayUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPutawayUser(String value) {
        this.putawayUser = value;
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
     * Gets the value of the requestSeq property.
     * 
     */
    public int getRequestSeq() {
        return requestSeq;
    }

    /**
     * Sets the value of the requestSeq property.
     * 
     */
    public void setRequestSeq(int value) {
        this.requestSeq = value;
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
