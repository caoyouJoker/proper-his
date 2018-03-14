
package jdo.pha.inf.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for spcOpdDiagrecDto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="spcOpdDiagrecDto">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="caseNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="diagNote" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="icdCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="icdType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optTerm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spcOpdDiagrecDto", propOrder = {
    "caseNo",
    "diagNote",
    "icdCode",
    "icdType",
    "optDate",
    "optTerm",
    "optUser"
})
public class SpcOpdDiagrecDto {

    protected String caseNo;
    protected String diagNote;
    protected String icdCode;
    protected String icdType;
    protected String optDate;
    protected String optTerm;
    protected String optUser;

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
     * Gets the value of the diagNote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiagNote() {
        return diagNote;
    }

    /**
     * Sets the value of the diagNote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiagNote(String value) {
        this.diagNote = value;
    }

    /**
     * Gets the value of the icdCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIcdCode() {
        return icdCode;
    }

    /**
     * Sets the value of the icdCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIcdCode(String value) {
        this.icdCode = value;
    }

    /**
     * Gets the value of the icdType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIcdType() {
        return icdType;
    }

    /**
     * Sets the value of the icdType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIcdType(String value) {
        this.icdType = value;
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

}
