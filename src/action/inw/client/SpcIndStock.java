
package action.inw.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for spcIndStock complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="spcIndStock">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dosageQty" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="optDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optTerm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orgCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendatcFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceLevel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spcIndStock", propOrder = {
    "dosageQty",
    "optDate",
    "optTerm",
    "optUser",
    "orderCode",
    "orderDesc",
    "orgCode",
    "sendatcFlg",
    "serviceLevel"
})
public class SpcIndStock {

    protected double dosageQty;
    protected String optDate;
    protected String optTerm;
    protected String optUser;
    protected String orderCode;
    protected String orderDesc;
    protected String orgCode;
    protected String sendatcFlg;
    protected String serviceLevel;

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
     * Gets the value of the orderDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderDesc() {
        return orderDesc;
    }

    /**
     * Sets the value of the orderDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderDesc(String value) {
        this.orderDesc = value;
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
     * Gets the value of the sendatcFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendatcFlg() {
        return sendatcFlg;
    }

    /**
     * Sets the value of the sendatcFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendatcFlg(String value) {
        this.sendatcFlg = value;
    }

    /**
     * Gets the value of the serviceLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceLevel() {
        return serviceLevel;
    }

    /**
     * Sets the value of the serviceLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceLevel(String value) {
        this.serviceLevel = value;
    }

}
