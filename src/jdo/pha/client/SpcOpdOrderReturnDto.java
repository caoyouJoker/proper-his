
package jdo.pha.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for spcOpdOrderReturnDto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="spcOpdOrderReturnDto">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="caseNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mrNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaCheckCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaCheckDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDispenseCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDispenseDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDosageCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDosageDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaRetnCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaRetnDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rxNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seqNO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spcOpdOrderReturnDto", propOrder = {
    "caseNo",
    "mrNo",
    "phaCheckCode",
    "phaCheckDate",
    "phaDispenseCode",
    "phaDispenseDate",
    "phaDosageCode",
    "phaDosageDate",
    "phaRetnCode",
    "phaRetnDate",
    "rxNo",
    "seqNO"
})
public class SpcOpdOrderReturnDto {

    protected String caseNo;
    protected String mrNo;
    protected String phaCheckCode;
    protected String phaCheckDate;
    protected String phaDispenseCode;
    protected String phaDispenseDate;
    protected String phaDosageCode;
    protected String phaDosageDate;
    protected String phaRetnCode;
    protected String phaRetnDate;
    protected String rxNo;
    protected String seqNO;

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
     * Gets the value of the phaCheckCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaCheckCode() {
        return phaCheckCode;
    }

    /**
     * Sets the value of the phaCheckCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaCheckCode(String value) {
        this.phaCheckCode = value;
    }

    /**
     * Gets the value of the phaCheckDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaCheckDate() {
        return phaCheckDate;
    }

    /**
     * Sets the value of the phaCheckDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaCheckDate(String value) {
        this.phaCheckDate = value;
    }

    /**
     * Gets the value of the phaDispenseCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaDispenseCode() {
        return phaDispenseCode;
    }

    /**
     * Sets the value of the phaDispenseCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaDispenseCode(String value) {
        this.phaDispenseCode = value;
    }

    /**
     * Gets the value of the phaDispenseDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaDispenseDate() {
        return phaDispenseDate;
    }

    /**
     * Sets the value of the phaDispenseDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaDispenseDate(String value) {
        this.phaDispenseDate = value;
    }

    /**
     * Gets the value of the phaDosageCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaDosageCode() {
        return phaDosageCode;
    }

    /**
     * Sets the value of the phaDosageCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaDosageCode(String value) {
        this.phaDosageCode = value;
    }

    /**
     * Gets the value of the phaDosageDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaDosageDate() {
        return phaDosageDate;
    }

    /**
     * Sets the value of the phaDosageDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaDosageDate(String value) {
        this.phaDosageDate = value;
    }

    /**
     * Gets the value of the phaRetnCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaRetnCode() {
        return phaRetnCode;
    }

    /**
     * Sets the value of the phaRetnCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaRetnCode(String value) {
        this.phaRetnCode = value;
    }

    /**
     * Gets the value of the phaRetnDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaRetnDate() {
        return phaRetnDate;
    }

    /**
     * Sets the value of the phaRetnDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaRetnDate(String value) {
        this.phaRetnDate = value;
    }

    /**
     * Gets the value of the rxNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRxNo() {
        return rxNo;
    }

    /**
     * Sets the value of the rxNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRxNo(String value) {
        this.rxNo = value;
    }

    /**
     * Gets the value of the seqNO property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeqNO() {
        return seqNO;
    }

    /**
     * Sets the value of the seqNO property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeqNO(String value) {
        this.seqNO = value;
    }

}
