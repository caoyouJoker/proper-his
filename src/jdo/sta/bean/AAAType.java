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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AAAType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AAA01" type="{}String"/>
 *         &lt;element name="AAA02C" type="{}AAA02CType"/>
 *         &lt;element name="AAA03" type="{}Date"/>
 *         &lt;element name="AAA04" type="{}Age" minOccurs="0"/>
 *         &lt;element name="AAA05C" type="{}AAA05CType" minOccurs="0"/>
 *         &lt;element name="AAA40" type="{}birthedDays" minOccurs="0"/>
 *         &lt;element name="AAA42" type="{}AAA42Type" minOccurs="0"/>
 *         &lt;element name="AAA06C" type="{}AAA06CType" minOccurs="0"/>
 *         &lt;element name="AAA07" type="{}IDCard" minOccurs="0"/>
 *         &lt;element name="AAA08C" type="{}AAA08CType"/>
 *         &lt;element name="AAA09" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA10" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA11" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA43" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA44" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA45" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA46" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA47" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA12" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA13C" type="{}AAA13CType" minOccurs="0"/>
 *         &lt;element name="AAA33C" type="{}AAA33CType" minOccurs="0"/>
 *         &lt;element name="AAA14C" type="{}ZipCode" minOccurs="0"/>
 *         &lt;element name="AAA15" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA48" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA49" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA50" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA16C" type="{}AAA16CType"/>
 *         &lt;element name="AAA36C" type="{}AAA33CType" minOccurs="0"/>
 *         &lt;element name="AAA51" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA17C" type="{}ZipCode" minOccurs="0"/>
 *         &lt;element name="AAA18C" type="{}AAA18CType"/>
 *         &lt;element name="AAA19" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA20" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA21C" type="{}ZipCode" minOccurs="0"/>
 *         &lt;element name="AAA22" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA23C" type="{}AAA23CType" minOccurs="0"/>
 *         &lt;element name="AAA24" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA25" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA26C" type="{}AAA26CType"/>
 *         &lt;element name="AAA27" type="{}String" minOccurs="0"/>
 *         &lt;element name="AAA28" type="{}String"/>
 *         &lt;element name="AAA29" type="{}AAA29Type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AAAType", propOrder = {
    "aaa01",
    "aaa02C",
    "aaa03",
    "aaa04",
    "aaa05C",
    "aaa40",
    "aaa42",
    "aaa06C",
    "aaa07",
    "aaa08C",
    "aaa09",
    "aaa10",
    "aaa11",
    "aaa43",
    "aaa44",
    "aaa45",
    "aaa46",
    "aaa47",
    "aaa12",
    "aaa13C",
    "aaa33C",
    "aaa14C",
    "aaa15",
    "aaa48",
    "aaa49",
    "aaa50",
    "aaa16C",
    "aaa36C",
    "aaa51",
    "aaa17C",
    "aaa18C",
    "aaa19",
    "aaa20",
    "aaa21C",
    "aaa22",
    "aaa23C",
    "aaa24",
    "aaa25",
    "aaa26C",
    "aaa27",
    "aaa28",
    "aaa29"
})
public class AAAType {

    @XmlElement(name = "AAA01", required = true)
    protected jdo.sta.bean.String aaa01;
    @XmlElement(name = "AAA02C", required = true)
    protected AAA02CType aaa02C;
    @XmlElement(name = "AAA03", required = true)
    protected Date aaa03;
    @XmlElementRef(name = "AAA04", type = JAXBElement.class)
    protected JAXBElement<Age> aaa04;
    @XmlElementRef(name = "AAA05C", type = JAXBElement.class)
    protected JAXBElement<AAA05CType> aaa05C;
    @XmlElementRef(name = "AAA40", type = JAXBElement.class)
    protected JAXBElement<BirthedDays> aaa40;
    @XmlElementRef(name = "AAA42", type = JAXBElement.class)
    protected JAXBElement<Integer> aaa42;
    @XmlElementRef(name = "AAA06C", type = JAXBElement.class)
    protected JAXBElement<AAA06CType> aaa06C;
    @XmlElementRef(name = "AAA07", type = JAXBElement.class)
    protected JAXBElement<IDCard> aaa07;
    @XmlElement(name = "AAA08C", required = true)
    protected AAA08CType aaa08C;
    @XmlElementRef(name = "AAA09", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa09;
    @XmlElementRef(name = "AAA10", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa10;
    @XmlElementRef(name = "AAA11", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa11;
    @XmlElementRef(name = "AAA43", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa43;
    @XmlElementRef(name = "AAA44", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa44;
    @XmlElementRef(name = "AAA45", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa45;
    @XmlElementRef(name = "AAA46", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa46;
    @XmlElementRef(name = "AAA47", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa47;
    @XmlElementRef(name = "AAA12", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa12;
    @XmlElementRef(name = "AAA13C", type = JAXBElement.class)
    protected JAXBElement<AAA13CType> aaa13C;
    @XmlElementRef(name = "AAA33C", type = JAXBElement.class)
    protected JAXBElement<java.lang.String> aaa33C;
    @XmlElementRef(name = "AAA14C", type = JAXBElement.class)
    protected JAXBElement<ZipCode> aaa14C;
    @XmlElementRef(name = "AAA15", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa15;
    @XmlElementRef(name = "AAA48", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa48;
    @XmlElementRef(name = "AAA49", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa49;
    @XmlElementRef(name = "AAA50", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa50;
    @XmlElement(name = "AAA16C", required = true)
    protected AAA16CType aaa16C;
    @XmlElementRef(name = "AAA36C", type = JAXBElement.class)
    protected JAXBElement<java.lang.String> aaa36C;
    @XmlElementRef(name = "AAA51", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa51;
    @XmlElementRef(name = "AAA17C", type = JAXBElement.class)
    protected JAXBElement<ZipCode> aaa17C;
    @XmlElement(name = "AAA18C", required = true)
    protected AAA18CType aaa18C;
    @XmlElementRef(name = "AAA19", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa19;
    @XmlElementRef(name = "AAA20", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa20;
    @XmlElementRef(name = "AAA21C", type = JAXBElement.class)
    protected JAXBElement<ZipCode> aaa21C;
    @XmlElementRef(name = "AAA22", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa22;
    @XmlElementRef(name = "AAA23C", type = JAXBElement.class)
    protected JAXBElement<AAA23CType> aaa23C;
    @XmlElementRef(name = "AAA24", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa24;
    @XmlElementRef(name = "AAA25", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa25;
    @XmlElement(name = "AAA26C", required = true)
    protected AAA26CType aaa26C;
    @XmlElementRef(name = "AAA27", type = JAXBElement.class)
    protected JAXBElement<jdo.sta.bean.String> aaa27;
    @XmlElement(name = "AAA28", required = true)
    protected jdo.sta.bean.String aaa28;
    @XmlElement(name = "AAA29")
    protected int aaa29;

    /**
     * Gets the value of the aaa01 property.
     * 
     * @return
     *     possible object is
     *     {@link jdo.sta.bean.String }
     *     
     */
    public jdo.sta.bean.String getAAA01() {
        return aaa01;
    }

    /**
     * Sets the value of the aaa01 property.
     * 
     * @param value
     *     allowed object is
     *     {@link jdo.sta.bean.String }
     *     
     */
    public void setAAA01(jdo.sta.bean.String value) {
        this.aaa01 = value;
    }

    /**
     * Gets the value of the aaa02C property.
     * 
     * @return
     *     possible object is
     *     {@link AAA02CType }
     *     
     */
    public AAA02CType getAAA02C() {
        return aaa02C;
    }

    /**
     * Sets the value of the aaa02C property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAA02CType }
     *     
     */
    public void setAAA02C(AAA02CType value) {
        this.aaa02C = value;
    }

    /**
     * Gets the value of the aaa03 property.
     * 
     * @return
     *     possible object is
     *     {@link Date }
     *     
     */
    public Date getAAA03() {
        return aaa03;
    }

    /**
     * Sets the value of the aaa03 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Date }
     *     
     */
    public void setAAA03(Date value) {
        this.aaa03 = value;
    }

    /**
     * Gets the value of the aaa04 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Age }{@code >}
     *     
     */
    public JAXBElement<Age> getAAA04() {
        return aaa04;
    }

    /**
     * Sets the value of the aaa04 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Age }{@code >}
     *     
     */
    public void setAAA04(JAXBElement<Age> value) {
        this.aaa04 = ((JAXBElement<Age> ) value);
    }

    /**
     * Gets the value of the aaa05C property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AAA05CType }{@code >}
     *     
     */
    public JAXBElement<AAA05CType> getAAA05C() {
        return aaa05C;
    }

    /**
     * Sets the value of the aaa05C property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AAA05CType }{@code >}
     *     
     */
    public void setAAA05C(JAXBElement<AAA05CType> value) {
        this.aaa05C = ((JAXBElement<AAA05CType> ) value);
    }

    /**
     * Gets the value of the aaa40 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BirthedDays }{@code >}
     *     
     */
    public JAXBElement<BirthedDays> getAAA40() {
        return aaa40;
    }

    /**
     * Sets the value of the aaa40 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BirthedDays }{@code >}
     *     
     */
    public void setAAA40(JAXBElement<BirthedDays> value) {
        this.aaa40 = ((JAXBElement<BirthedDays> ) value);
    }

    /**
     * Gets the value of the aaa42 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getAAA42() {
        return aaa42;
    }

    /**
     * Sets the value of the aaa42 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setAAA42(JAXBElement<Integer> value) {
        this.aaa42 = ((JAXBElement<Integer> ) value);
    }

    /**
     * Gets the value of the aaa06C property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AAA06CType }{@code >}
     *     
     */
    public JAXBElement<AAA06CType> getAAA06C() {
        return aaa06C;
    }

    /**
     * Sets the value of the aaa06C property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AAA06CType }{@code >}
     *     
     */
    public void setAAA06C(JAXBElement<AAA06CType> value) {
        this.aaa06C = ((JAXBElement<AAA06CType> ) value);
    }

    /**
     * Gets the value of the aaa07 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link IDCard }{@code >}
     *     
     */
    public JAXBElement<IDCard> getAAA07() {
        return aaa07;
    }

    /**
     * Sets the value of the aaa07 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link IDCard }{@code >}
     *     
     */
    public void setAAA07(JAXBElement<IDCard> value) {
        this.aaa07 = ((JAXBElement<IDCard> ) value);
    }

    /**
     * Gets the value of the aaa08C property.
     * 
     * @return
     *     possible object is
     *     {@link AAA08CType }
     *     
     */
    public AAA08CType getAAA08C() {
        return aaa08C;
    }

    /**
     * Sets the value of the aaa08C property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAA08CType }
     *     
     */
    public void setAAA08C(AAA08CType value) {
        this.aaa08C = value;
    }

    /**
     * Gets the value of the aaa09 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA09() {
        return aaa09;
    }

    /**
     * Sets the value of the aaa09 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA09(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa09 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa10 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA10() {
        return aaa10;
    }

    /**
     * Sets the value of the aaa10 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA10(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa10 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa11 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA11() {
        return aaa11;
    }

    /**
     * Sets the value of the aaa11 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA11(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa11 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa43 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA43() {
        return aaa43;
    }

    /**
     * Sets the value of the aaa43 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA43(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa43 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa44 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA44() {
        return aaa44;
    }

    /**
     * Sets the value of the aaa44 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA44(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa44 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa45 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA45() {
        return aaa45;
    }

    /**
     * Sets the value of the aaa45 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA45(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa45 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa46 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA46() {
        return aaa46;
    }

    /**
     * Sets the value of the aaa46 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA46(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa46 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa47 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA47() {
        return aaa47;
    }

    /**
     * Sets the value of the aaa47 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA47(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa47 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa12 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA12() {
        return aaa12;
    }

    /**
     * Sets the value of the aaa12 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA12(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa12 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa13C property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AAA13CType }{@code >}
     *     
     */
    public JAXBElement<AAA13CType> getAAA13C() {
        return aaa13C;
    }

    /**
     * Sets the value of the aaa13C property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AAA13CType }{@code >}
     *     
     */
    public void setAAA13C(JAXBElement<AAA13CType> value) {
        this.aaa13C = ((JAXBElement<AAA13CType> ) value);
    }

    /**
     * Gets the value of the aaa33C property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getAAA33C() {
        return aaa33C;
    }

    /**
     * Sets the value of the aaa33C property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setAAA33C(JAXBElement<java.lang.String> value) {
        this.aaa33C = ((JAXBElement<java.lang.String> ) value);
    }

    /**
     * Gets the value of the aaa14C property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ZipCode }{@code >}
     *     
     */
    public JAXBElement<ZipCode> getAAA14C() {
        return aaa14C;
    }

    /**
     * Sets the value of the aaa14C property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ZipCode }{@code >}
     *     
     */
    public void setAAA14C(JAXBElement<ZipCode> value) {
        this.aaa14C = ((JAXBElement<ZipCode> ) value);
    }

    /**
     * Gets the value of the aaa15 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA15() {
        return aaa15;
    }

    /**
     * Sets the value of the aaa15 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA15(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa15 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa48 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA48() {
        return aaa48;
    }

    /**
     * Sets the value of the aaa48 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA48(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa48 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa49 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA49() {
        return aaa49;
    }

    /**
     * Sets the value of the aaa49 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA49(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa49 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa50 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA50() {
        return aaa50;
    }

    /**
     * Sets the value of the aaa50 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA50(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa50 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa16C property.
     * 
     * @return
     *     possible object is
     *     {@link AAA16CType }
     *     
     */
    public AAA16CType getAAA16C() {
        return aaa16C;
    }

    /**
     * Sets the value of the aaa16C property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAA16CType }
     *     
     */
    public void setAAA16C(AAA16CType value) {
        this.aaa16C = value;
    }

    /**
     * Gets the value of the aaa36C property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getAAA36C() {
        return aaa36C;
    }

    /**
     * Sets the value of the aaa36C property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setAAA36C(JAXBElement<java.lang.String> value) {
        this.aaa36C = ((JAXBElement<java.lang.String> ) value);
    }

    /**
     * Gets the value of the aaa51 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA51() {
        return aaa51;
    }

    /**
     * Sets the value of the aaa51 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA51(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa51 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa17C property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ZipCode }{@code >}
     *     
     */
    public JAXBElement<ZipCode> getAAA17C() {
        return aaa17C;
    }

    /**
     * Sets the value of the aaa17C property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ZipCode }{@code >}
     *     
     */
    public void setAAA17C(JAXBElement<ZipCode> value) {
        this.aaa17C = ((JAXBElement<ZipCode> ) value);
    }

    /**
     * Gets the value of the aaa18C property.
     * 
     * @return
     *     possible object is
     *     {@link AAA18CType }
     *     
     */
    public AAA18CType getAAA18C() {
        return aaa18C;
    }

    /**
     * Sets the value of the aaa18C property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAA18CType }
     *     
     */
    public void setAAA18C(AAA18CType value) {
        this.aaa18C = value;
    }

    /**
     * Gets the value of the aaa19 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA19() {
        return aaa19;
    }

    /**
     * Sets the value of the aaa19 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA19(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa19 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa20 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA20() {
        return aaa20;
    }

    /**
     * Sets the value of the aaa20 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA20(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa20 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa21C property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ZipCode }{@code >}
     *     
     */
    public JAXBElement<ZipCode> getAAA21C() {
        return aaa21C;
    }

    /**
     * Sets the value of the aaa21C property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ZipCode }{@code >}
     *     
     */
    public void setAAA21C(JAXBElement<ZipCode> value) {
        this.aaa21C = ((JAXBElement<ZipCode> ) value);
    }

    /**
     * Gets the value of the aaa22 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA22() {
        return aaa22;
    }

    /**
     * Sets the value of the aaa22 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA22(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa22 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa23C property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AAA23CType }{@code >}
     *     
     */
    public JAXBElement<AAA23CType> getAAA23C() {
        return aaa23C;
    }

    /**
     * Sets the value of the aaa23C property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AAA23CType }{@code >}
     *     
     */
    public void setAAA23C(JAXBElement<AAA23CType> value) {
        this.aaa23C = ((JAXBElement<AAA23CType> ) value);
    }

    /**
     * Gets the value of the aaa24 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA24() {
        return aaa24;
    }

    /**
     * Sets the value of the aaa24 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA24(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa24 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa25 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA25() {
        return aaa25;
    }

    /**
     * Sets the value of the aaa25 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA25(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa25 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa26C property.
     * 
     * @return
     *     possible object is
     *     {@link AAA26CType }
     *     
     */
    public AAA26CType getAAA26C() {
        return aaa26C;
    }

    /**
     * Sets the value of the aaa26C property.
     * 
     * @param value
     *     allowed object is
     *     {@link AAA26CType }
     *     
     */
    public void setAAA26C(AAA26CType value) {
        this.aaa26C = value;
    }

    /**
     * Gets the value of the aaa27 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public JAXBElement<jdo.sta.bean.String> getAAA27() {
        return aaa27;
    }

    /**
     * Sets the value of the aaa27 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link jdo.sta.bean.String }{@code >}
     *     
     */
    public void setAAA27(JAXBElement<jdo.sta.bean.String> value) {
        this.aaa27 = ((JAXBElement<jdo.sta.bean.String> ) value);
    }

    /**
     * Gets the value of the aaa28 property.
     * 
     * @return
     *     possible object is
     *     {@link jdo.sta.bean.String }
     *     
     */
    public jdo.sta.bean.String getAAA28() {
        return aaa28;
    }

    /**
     * Sets the value of the aaa28 property.
     * 
     * @param value
     *     allowed object is
     *     {@link jdo.sta.bean.String }
     *     
     */
    public void setAAA28(jdo.sta.bean.String value) {
        this.aaa28 = value;
    }

    /**
     * Gets the value of the aaa29 property.
     * 
     */
    public int getAAA29() {
        return aaa29;
    }

    /**
     * Sets the value of the aaa29 property.
     * 
     */
    public void setAAA29(int value) {
        this.aaa29 = value;
    }

}
