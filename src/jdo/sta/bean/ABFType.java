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
 * <p>Java class for ABFType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ABFType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ABF01C" type="{}String" minOccurs="0"/>
 *         &lt;element name="ABF01N" type="{}String" minOccurs="0"/>
 *         &lt;element name="ABF04" type="{}String" minOccurs="0"/>
 *         &lt;element name="ABF02C" type="{}ABF02CType" minOccurs="0"/>
 *         &lt;element name="ABF03C" type="{}ABF03CType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ABFType", propOrder = {
    "abf01C",
    "abf01N",
    "abf04",
    "abf02C",
    "abf03C"
})
public class ABFType {

    @XmlElementRef(name = "ABF01C", type = JAXBElement.class)
    protected JAXBElement<String> abf01C;
    @XmlElementRef(name = "ABF01N", type = JAXBElement.class)
    protected JAXBElement<String> abf01N;
    @XmlElementRef(name = "ABF04", type = JAXBElement.class)
    protected JAXBElement<String> abf04;
    @XmlElementRef(name = "ABF02C", type = JAXBElement.class)
    protected JAXBElement<ABF02CType> abf02C;
    @XmlElementRef(name = "ABF03C", type = JAXBElement.class)
    protected JAXBElement<ABF03CType> abf03C;

    /**
     * Gets the value of the abf01C property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getABF01C() {
        return abf01C;
    }

    /**
     * Sets the value of the abf01C property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setABF01C(JAXBElement<String> value) {
        this.abf01C = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the abf01N property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getABF01N() {
        return abf01N;
    }

    /**
     * Sets the value of the abf01N property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setABF01N(JAXBElement<String> value) {
        this.abf01N = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the abf04 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getABF04() {
        return abf04;
    }

    /**
     * Sets the value of the abf04 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setABF04(JAXBElement<String> value) {
        this.abf04 = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the abf02C property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ABF02CType }{@code >}
     *     
     */
    public JAXBElement<ABF02CType> getABF02C() {
        return abf02C;
    }

    /**
     * Sets the value of the abf02C property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ABF02CType }{@code >}
     *     
     */
    public void setABF02C(JAXBElement<ABF02CType> value) {
        this.abf02C = ((JAXBElement<ABF02CType> ) value);
    }

    /**
     * Gets the value of the abf03C property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ABF03CType }{@code >}
     *     
     */
    public JAXBElement<ABF03CType> getABF03C() {
        return abf03C;
    }

    /**
     * Sets the value of the abf03C property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ABF03CType }{@code >}
     *     
     */
    public void setABF03C(JAXBElement<ABF03CType> value) {
        this.abf03C = ((JAXBElement<ABF03CType> ) value);
    }

}
