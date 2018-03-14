
package action.odi.spcUddClient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for odiDspndPkVo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="odiDspndPkVo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="barCode1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="barCode2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="barCode3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="caseNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="endDttm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderDateTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderSeq" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="startDttm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "odiDspndPkVo", propOrder = {
    "barCode1",
    "barCode2",
    "barCode3",
    "caseNo",
    "endDttm",
    "orderCode",
    "orderDate",
    "orderDateTime",
    "orderNo",
    "orderSeq",
    "startDttm"
})
public class OdiDspndPkVo {

    protected String barCode1;
    protected String barCode2;
    protected String barCode3;
    protected String caseNo;
    protected String endDttm;
    protected String orderCode;
    protected String orderDate;
    protected String orderDateTime;
    protected String orderNo;
    protected String orderSeq;
    protected String startDttm;

    /**
     * Gets the value of the barCode1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBarCode1() {
        return barCode1;
    }

    /**
     * Sets the value of the barCode1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBarCode1(String value) {
        this.barCode1 = value;
    }

    /**
     * Gets the value of the barCode2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBarCode2() {
        return barCode2;
    }

    /**
     * Sets the value of the barCode2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBarCode2(String value) {
        this.barCode2 = value;
    }

    /**
     * Gets the value of the barCode3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBarCode3() {
        return barCode3;
    }

    /**
     * Sets the value of the barCode3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBarCode3(String value) {
        this.barCode3 = value;
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
     * Gets the value of the endDttm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndDttm() {
        return endDttm;
    }

    /**
     * Sets the value of the endDttm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndDttm(String value) {
        this.endDttm = value;
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
     * Gets the value of the orderDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderDateTime() {
        return orderDateTime;
    }

    /**
     * Sets the value of the orderDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderDateTime(String value) {
        this.orderDateTime = value;
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
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderSeq() {
        return orderSeq;
    }

    /**
     * Sets the value of the orderSeq property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderSeq(String value) {
        this.orderSeq = value;
    }

    /**
     * Gets the value of the startDttm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartDttm() {
        return startDttm;
    }

    /**
     * Sets the value of the startDttm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartDttm(String value) {
        this.startDttm = value;
    }

}
