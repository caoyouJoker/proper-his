
package action.spc.accountclient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for indAccount complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="indAccount">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accountQty" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="accountUnitCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="closeDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractPrice" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="isUpdate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastOddQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="oddAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="oddQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="optDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optTerm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orgCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="outQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="supCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="supOrderCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalOutQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="totalUnitCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="verifyinAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
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
@XmlType(name = "indAccount", propOrder = {
    "accountQty",
    "accountUnitCode",
    "closeDate",
    "contractPrice",
    "isUpdate",
    "lastOddQty",
    "oddAmt",
    "oddQty",
    "optDate",
    "optTerm",
    "optUser",
    "orderCode",
    "orgCode",
    "outQty",
    "supCode",
    "supOrderCode",
    "totalOutQty",
    "totalUnitCode",
    "verifyinAmt",
    "verifyinPrice"
})
public class IndAccount {

    protected Long accountQty;
    protected String accountUnitCode;
    protected String closeDate;
    protected Double contractPrice;
    protected String isUpdate;
    protected Double lastOddQty;
    protected Double oddAmt;
    protected Double oddQty;
    protected String optDate;
    protected String optTerm;
    protected String optUser;
    protected String orderCode;
    protected String orgCode;
    protected Double outQty;
    protected String supCode;
    protected String supOrderCode;
    protected Double totalOutQty;
    protected String totalUnitCode;
    protected Double verifyinAmt;
    protected Double verifyinPrice;

    /**
     * Gets the value of the accountQty property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAccountQty() {
        return accountQty;
    }

    /**
     * Sets the value of the accountQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAccountQty(Long value) {
        this.accountQty = value;
    }

    /**
     * Gets the value of the accountUnitCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountUnitCode() {
        return accountUnitCode;
    }

    /**
     * Sets the value of the accountUnitCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountUnitCode(String value) {
        this.accountUnitCode = value;
    }

    /**
     * Gets the value of the closeDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCloseDate() {
        return closeDate;
    }

    /**
     * Sets the value of the closeDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCloseDate(String value) {
        this.closeDate = value;
    }

    /**
     * Gets the value of the contractPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getContractPrice() {
        return contractPrice;
    }

    /**
     * Sets the value of the contractPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setContractPrice(Double value) {
        this.contractPrice = value;
    }

    /**
     * Gets the value of the isUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsUpdate() {
        return isUpdate;
    }

    /**
     * Sets the value of the isUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsUpdate(String value) {
        this.isUpdate = value;
    }

    /**
     * Gets the value of the lastOddQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getLastOddQty() {
        return lastOddQty;
    }

    /**
     * Sets the value of the lastOddQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setLastOddQty(Double value) {
        this.lastOddQty = value;
    }

    /**
     * Gets the value of the oddAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getOddAmt() {
        return oddAmt;
    }

    /**
     * Sets the value of the oddAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setOddAmt(Double value) {
        this.oddAmt = value;
    }

    /**
     * Gets the value of the oddQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getOddQty() {
        return oddQty;
    }

    /**
     * Sets the value of the oddQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setOddQty(Double value) {
        this.oddQty = value;
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
     * Gets the value of the orgCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgCode() {
        return orgCode;
    }

    /**
     * Sets the value of the orgCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgCode(String value) {
        this.orgCode = value;
    }

    /**
     * Gets the value of the outQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getOutQty() {
        return outQty;
    }

    /**
     * Sets the value of the outQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setOutQty(Double value) {
        this.outQty = value;
    }

    /**
     * Gets the value of the supCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupCode() {
        return supCode;
    }

    /**
     * Sets the value of the supCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupCode(String value) {
        this.supCode = value;
    }

    /**
     * Gets the value of the supOrderCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupOrderCode() {
        return supOrderCode;
    }

    /**
     * Sets the value of the supOrderCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupOrderCode(String value) {
        this.supOrderCode = value;
    }

    /**
     * Gets the value of the totalOutQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getTotalOutQty() {
        return totalOutQty;
    }

    /**
     * Sets the value of the totalOutQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setTotalOutQty(Double value) {
        this.totalOutQty = value;
    }

    /**
     * Gets the value of the totalUnitCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalUnitCode() {
        return totalUnitCode;
    }

    /**
     * Sets the value of the totalUnitCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalUnitCode(String value) {
        this.totalUnitCode = value;
    }

    /**
     * Gets the value of the verifyinAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getVerifyinAmt() {
        return verifyinAmt;
    }

    /**
     * Sets the value of the verifyinAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setVerifyinAmt(Double value) {
        this.verifyinAmt = value;
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
