//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.08.13 at 10:02:29 ���� CST 
//


package jdo.sum.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}admType"/>
 *         &lt;element ref="{}dept"/>
 *         &lt;element ref="{}room"/>
 *         &lt;element ref="{}bed"/>
 *         &lt;element ref="{}vsDrCode"/>
 *         &lt;element ref="{}admDr"/>
 *         &lt;element ref="{}caseNo"/>
 *         &lt;element ref="{}ctzDesc"/>
 *         &lt;element ref="{}inDate"/>
 *         &lt;element ref="{}outDate"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "admType",
    "dept",
    "room",
    "bed",
    "vsDrCode",
    "admDr",
    "caseNo",
    "ctzDesc",
    "inDate",
    "outDate"
})
@XmlRootElement(name = "pv1")
public class Pv1 {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String admType;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String dept;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String room;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String bed;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String vsDrCode;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String admDr;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String caseNo;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String ctzDesc;
    @XmlElement(required = true)
    protected String inDate;
    @XmlElement(required = true)
    protected String outDate;

    /**
     * Gets the value of the admType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdmType() {
        return admType;
    }

    /**
     * Sets the value of the admType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdmType(String value) {
        this.admType = value;
    }

    /**
     * Gets the value of the dept property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDept() {
        return dept;
    }

    /**
     * Sets the value of the dept property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDept(String value) {
        this.dept = value;
    }

    /**
     * Gets the value of the room property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoom() {
        return room;
    }

    /**
     * Sets the value of the room property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoom(String value) {
        this.room = value;
    }

    /**
     * Gets the value of the bed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBed() {
        return bed;
    }

    /**
     * Sets the value of the bed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBed(String value) {
        this.bed = value;
    }

    /**
     * Gets the value of the vsDrCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVsDrCode() {
        return vsDrCode;
    }

    /**
     * Sets the value of the vsDrCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVsDrCode(String value) {
        this.vsDrCode = value;
    }

    /**
     * Gets the value of the admDr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdmDr() {
        return admDr;
    }

    /**
     * Sets the value of the admDr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdmDr(String value) {
        this.admDr = value;
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
     * Gets the value of the ctzDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtzDesc() {
        return ctzDesc;
    }

    /**
     * Sets the value of the ctzDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtzDesc(String value) {
        this.ctzDesc = value;
    }

    /**
     * Gets the value of the inDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInDate() {
        return inDate;
    }

    /**
     * Sets the value of the inDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInDate(String value) {
        this.inDate = value;
    }

    /**
     * Gets the value of the outDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutDate() {
        return outDate;
    }

    /**
     * Sets the value of the outDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutDate(String value) {
        this.outDate = value;
    }

}
