//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.06.30 at 10:28:02 ���� CST 
//


package jdo.sta.bean;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AEDType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AEDType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AED01C" type="{}AED01CType" minOccurs="0"/>
 *         &lt;element name="AED02" type="{}String" minOccurs="0"/>
 *         &lt;element name="AED03" type="{}String" minOccurs="0"/>
 *         &lt;element name="AED04" type="{}Date" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AEDType", propOrder = {
    "aed01C",
    "aed02",
    "aed03",
    "aed04"
})
public class AEDType {

    @XmlElementRef(name = "AED01C", type = JAXBElement.class)
    protected JAXBElement<AED01CType> aed01C;
    @XmlElementRef(name = "AED02", type = JAXBElement.class)
    protected JAXBElement<String> aed02;
    @XmlElementRef(name = "AED03", type = JAXBElement.class)
    protected JAXBElement<String> aed03;
    @XmlElementRef(name = "AED04", type = JAXBElement.class)
    protected JAXBElement<Date> aed04;

    /**
     * Gets the value of the aed01C property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AED01CType }{@code >}
     *     
     */
    public JAXBElement<AED01CType> getAED01C() {
        return aed01C;
    }

    /**
     * Sets the value of the aed01C property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AED01CType }{@code >}
     *     
     */
    public void setAED01C(JAXBElement<AED01CType> value) {
        this.aed01C = ((JAXBElement<AED01CType> ) value);
    }

    /**
     * Gets the value of the aed02 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAED02() {
        return aed02;
    }

    /**
     * Sets the value of the aed02 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAED02(JAXBElement<String> value) {
        this.aed02 = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the aed03 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAED03() {
        return aed03;
    }

    /**
     * Sets the value of the aed03 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAED03(JAXBElement<String> value) {
        this.aed03 = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the aed04 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public JAXBElement<Date> getAED04() {
        return aed04;
    }

    /**
     * Sets the value of the aed04 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public void setAED04(JAXBElement<Date> value) {
        this.aed04 = ((JAXBElement<Date> ) value);
    }

}
