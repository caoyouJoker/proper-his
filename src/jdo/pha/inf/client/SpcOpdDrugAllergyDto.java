
package jdo.pha.inf.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for spcOpdDrugAllergyDto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="spcOpdDrugAllergyDto">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="admDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="allergyNote" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="drugType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="drugoringrdCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mrNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "spcOpdDrugAllergyDto", propOrder = {
    "admDate",
    "allergyNote",
    "drugType",
    "drugoringrdCode",
    "mrNo",
    "optDate",
    "optTerm",
    "optUser"
})
public class SpcOpdDrugAllergyDto {

    protected String admDate;
    protected String allergyNote;
    protected String drugType;
    protected String drugoringrdCode;
    protected String mrNo;
    protected String optDate;
    protected String optTerm;
    protected String optUser;

    /**
     * Gets the value of the admDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdmDate() {
        return admDate;
    }

    /**
     * Sets the value of the admDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdmDate(String value) {
        this.admDate = value;
    }

    /**
     * Gets the value of the allergyNote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAllergyNote() {
        return allergyNote;
    }

    /**
     * Sets the value of the allergyNote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllergyNote(String value) {
        this.allergyNote = value;
    }

    /**
     * Gets the value of the drugType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrugType() {
        return drugType;
    }

    /**
     * Sets the value of the drugType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrugType(String value) {
        this.drugType = value;
    }

    /**
     * Gets the value of the drugoringrdCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrugoringrdCode() {
        return drugoringrdCode;
    }

    /**
     * Sets the value of the drugoringrdCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrugoringrdCode(String value) {
        this.drugoringrdCode = value;
    }

    /**
     * Gets the value of the mrNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMrNo() {
        return mrNo;
    }

    /**
     * Sets the value of the mrNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMrNo(String value) {
        this.mrNo = value;
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
