
package jdo.spc.spcPatInfoSyncClient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sysPatinfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sysPatinfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="addressCompany" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adultExamDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthplace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="blackFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bloodRhType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bloodType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="borninFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="breastfeedEnddate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="breastfeedStartdate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ccbPersonNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cellPhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="companyDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contactsAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contactsName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contactsTel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ctz1Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ctz2Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ctz3Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deadDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deleteFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EMail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="educationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="familyHistory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fatherIdno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstAdmDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="foreignerFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="handicapFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="handleFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="height" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="homeplaceCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ipdNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="kidExamRcntDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="kidInjRcntDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lawProtectFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lmpDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="marriageCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mergeFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mergeTomrno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="motherIdno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="motherMrno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mrNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nameInvisibleFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="newbornSeq" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="nhiCardNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nhiNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="occCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optTerm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pat1Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pat2Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pat3Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="patName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="patName1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="postCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="postCompany" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pregnantDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prematureFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="py1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="py2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rcntEmgDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rcntEmgDept" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rcntIpdDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rcntIpdDept" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rcntMissDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rcntMissDept" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rcntOpdDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rcntOpdDept" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="relationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="religionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="residAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="residPostCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sexCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smearRcntDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="speciesCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="spouseIdno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="telCompany" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="telHome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weight" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sysPatinfo", propOrder = {
    "address",
    "addressCompany",
    "adultExamDate",
    "birthDate",
    "birthplace",
    "blackFlg",
    "bloodRhType",
    "bloodType",
    "borninFlg",
    "breastfeedEnddate",
    "breastfeedStartdate",
    "ccbPersonNo",
    "cellPhone",
    "companyDesc",
    "contactsAddress",
    "contactsName",
    "contactsTel",
    "ctz1Code",
    "ctz2Code",
    "ctz3Code",
    "deadDate",
    "deleteFlg",
    "description",
    "eMail",
    "educationCode",
    "familyHistory",
    "fatherIdno",
    "firstAdmDate",
    "foreignerFlg",
    "handicapFlg",
    "handleFlg",
    "height",
    "homeplaceCode",
    "idno",
    "ipdNo",
    "kidExamRcntDate",
    "kidInjRcntDate",
    "lawProtectFlg",
    "lmpDate",
    "marriageCode",
    "mergeFlg",
    "mergeTomrno",
    "motherIdno",
    "motherMrno",
    "mrNo",
    "nameInvisibleFlg",
    "nationCode",
    "newbornSeq",
    "nhiCardNo",
    "nhiNo",
    "occCode",
    "optDate",
    "optTerm",
    "optUser",
    "pat1Code",
    "pat2Code",
    "pat3Code",
    "patName",
    "patName1",
    "postCode",
    "postCompany",
    "pregnantDate",
    "prematureFlg",
    "py1",
    "py2",
    "rcntEmgDate",
    "rcntEmgDept",
    "rcntIpdDate",
    "rcntIpdDept",
    "rcntMissDate",
    "rcntMissDept",
    "rcntOpdDate",
    "rcntOpdDept",
    "relationCode",
    "religionCode",
    "residAddress",
    "residPostCode",
    "sexCode",
    "smearRcntDate",
    "speciesCode",
    "spouseIdno",
    "telCompany",
    "telHome",
    "weight"
})
public class SysPatinfo {

    protected String address;
    protected String addressCompany;
    protected String adultExamDate;
    protected String birthDate;
    protected String birthplace;
    protected String blackFlg;
    protected String bloodRhType;
    protected String bloodType;
    protected String borninFlg;
    protected String breastfeedEnddate;
    protected String breastfeedStartdate;
    protected String ccbPersonNo;
    protected String cellPhone;
    protected String companyDesc;
    protected String contactsAddress;
    protected String contactsName;
    protected String contactsTel;
    protected String ctz1Code;
    protected String ctz2Code;
    protected String ctz3Code;
    protected String deadDate;
    protected String deleteFlg;
    protected String description;
    @XmlElement(name = "EMail")
    protected String eMail;
    protected String educationCode;
    protected String familyHistory;
    protected String fatherIdno;
    protected String firstAdmDate;
    protected String foreignerFlg;
    protected String handicapFlg;
    protected String handleFlg;
    protected Double height;
    protected String homeplaceCode;
    protected String idno;
    protected String ipdNo;
    protected String kidExamRcntDate;
    protected String kidInjRcntDate;
    protected String lawProtectFlg;
    protected String lmpDate;
    protected String marriageCode;
    protected String mergeFlg;
    protected String mergeTomrno;
    protected String motherIdno;
    protected String motherMrno;
    protected String mrNo;
    protected String nameInvisibleFlg;
    protected String nationCode;
    protected Integer newbornSeq;
    protected String nhiCardNo;
    protected String nhiNo;
    protected String occCode;
    protected String optDate;
    protected String optTerm;
    protected String optUser;
    protected String pat1Code;
    protected String pat2Code;
    protected String pat3Code;
    protected String patName;
    protected String patName1;
    protected String postCode;
    protected String postCompany;
    protected String pregnantDate;
    protected String prematureFlg;
    protected String py1;
    protected String py2;
    protected String rcntEmgDate;
    protected String rcntEmgDept;
    protected String rcntIpdDate;
    protected String rcntIpdDept;
    protected String rcntMissDate;
    protected String rcntMissDept;
    protected String rcntOpdDate;
    protected String rcntOpdDept;
    protected String relationCode;
    protected String religionCode;
    protected String residAddress;
    protected String residPostCode;
    protected String sexCode;
    protected String smearRcntDate;
    protected String speciesCode;
    protected String spouseIdno;
    protected String telCompany;
    protected String telHome;
    protected Double weight;

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * Gets the value of the addressCompany property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressCompany() {
        return addressCompany;
    }

    /**
     * Sets the value of the addressCompany property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressCompany(String value) {
        this.addressCompany = value;
    }

    /**
     * Gets the value of the adultExamDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdultExamDate() {
        return adultExamDate;
    }

    /**
     * Sets the value of the adultExamDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdultExamDate(String value) {
        this.adultExamDate = value;
    }

    /**
     * Gets the value of the birthDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the value of the birthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBirthDate(String value) {
        this.birthDate = value;
    }

    /**
     * Gets the value of the birthplace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBirthplace() {
        return birthplace;
    }

    /**
     * Sets the value of the birthplace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBirthplace(String value) {
        this.birthplace = value;
    }

    /**
     * Gets the value of the blackFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBlackFlg() {
        return blackFlg;
    }

    /**
     * Sets the value of the blackFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBlackFlg(String value) {
        this.blackFlg = value;
    }

    /**
     * Gets the value of the bloodRhType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBloodRhType() {
        return bloodRhType;
    }

    /**
     * Sets the value of the bloodRhType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBloodRhType(String value) {
        this.bloodRhType = value;
    }

    /**
     * Gets the value of the bloodType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBloodType() {
        return bloodType;
    }

    /**
     * Sets the value of the bloodType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBloodType(String value) {
        this.bloodType = value;
    }

    /**
     * Gets the value of the borninFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBorninFlg() {
        return borninFlg;
    }

    /**
     * Sets the value of the borninFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBorninFlg(String value) {
        this.borninFlg = value;
    }

    /**
     * Gets the value of the breastfeedEnddate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBreastfeedEnddate() {
        return breastfeedEnddate;
    }

    /**
     * Sets the value of the breastfeedEnddate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBreastfeedEnddate(String value) {
        this.breastfeedEnddate = value;
    }

    /**
     * Gets the value of the breastfeedStartdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBreastfeedStartdate() {
        return breastfeedStartdate;
    }

    /**
     * Sets the value of the breastfeedStartdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBreastfeedStartdate(String value) {
        this.breastfeedStartdate = value;
    }

    /**
     * Gets the value of the ccbPersonNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcbPersonNo() {
        return ccbPersonNo;
    }

    /**
     * Sets the value of the ccbPersonNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcbPersonNo(String value) {
        this.ccbPersonNo = value;
    }

    /**
     * Gets the value of the cellPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCellPhone() {
        return cellPhone;
    }

    /**
     * Sets the value of the cellPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCellPhone(String value) {
        this.cellPhone = value;
    }

    /**
     * Gets the value of the companyDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyDesc() {
        return companyDesc;
    }

    /**
     * Sets the value of the companyDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyDesc(String value) {
        this.companyDesc = value;
    }

    /**
     * Gets the value of the contactsAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactsAddress() {
        return contactsAddress;
    }

    /**
     * Sets the value of the contactsAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactsAddress(String value) {
        this.contactsAddress = value;
    }

    /**
     * Gets the value of the contactsName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactsName() {
        return contactsName;
    }

    /**
     * Sets the value of the contactsName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactsName(String value) {
        this.contactsName = value;
    }

    /**
     * Gets the value of the contactsTel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactsTel() {
        return contactsTel;
    }

    /**
     * Sets the value of the contactsTel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactsTel(String value) {
        this.contactsTel = value;
    }

    /**
     * Gets the value of the ctz1Code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtz1Code() {
        return ctz1Code;
    }

    /**
     * Sets the value of the ctz1Code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtz1Code(String value) {
        this.ctz1Code = value;
    }

    /**
     * Gets the value of the ctz2Code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtz2Code() {
        return ctz2Code;
    }

    /**
     * Sets the value of the ctz2Code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtz2Code(String value) {
        this.ctz2Code = value;
    }

    /**
     * Gets the value of the ctz3Code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtz3Code() {
        return ctz3Code;
    }

    /**
     * Sets the value of the ctz3Code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtz3Code(String value) {
        this.ctz3Code = value;
    }

    /**
     * Gets the value of the deadDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeadDate() {
        return deadDate;
    }

    /**
     * Sets the value of the deadDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeadDate(String value) {
        this.deadDate = value;
    }

    /**
     * Gets the value of the deleteFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeleteFlg() {
        return deleteFlg;
    }

    /**
     * Sets the value of the deleteFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeleteFlg(String value) {
        this.deleteFlg = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the eMail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEMail() {
        return eMail;
    }

    /**
     * Sets the value of the eMail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEMail(String value) {
        this.eMail = value;
    }

    /**
     * Gets the value of the educationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEducationCode() {
        return educationCode;
    }

    /**
     * Sets the value of the educationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEducationCode(String value) {
        this.educationCode = value;
    }

    /**
     * Gets the value of the familyHistory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFamilyHistory() {
        return familyHistory;
    }

    /**
     * Sets the value of the familyHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFamilyHistory(String value) {
        this.familyHistory = value;
    }

    /**
     * Gets the value of the fatherIdno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFatherIdno() {
        return fatherIdno;
    }

    /**
     * Sets the value of the fatherIdno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFatherIdno(String value) {
        this.fatherIdno = value;
    }

    /**
     * Gets the value of the firstAdmDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstAdmDate() {
        return firstAdmDate;
    }

    /**
     * Sets the value of the firstAdmDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstAdmDate(String value) {
        this.firstAdmDate = value;
    }

    /**
     * Gets the value of the foreignerFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForeignerFlg() {
        return foreignerFlg;
    }

    /**
     * Sets the value of the foreignerFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForeignerFlg(String value) {
        this.foreignerFlg = value;
    }

    /**
     * Gets the value of the handicapFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandicapFlg() {
        return handicapFlg;
    }

    /**
     * Sets the value of the handicapFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandicapFlg(String value) {
        this.handicapFlg = value;
    }

    /**
     * Gets the value of the handleFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandleFlg() {
        return handleFlg;
    }

    /**
     * Sets the value of the handleFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandleFlg(String value) {
        this.handleFlg = value;
    }

    /**
     * Gets the value of the height property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setHeight(Double value) {
        this.height = value;
    }

    /**
     * Gets the value of the homeplaceCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHomeplaceCode() {
        return homeplaceCode;
    }

    /**
     * Sets the value of the homeplaceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHomeplaceCode(String value) {
        this.homeplaceCode = value;
    }

    /**
     * Gets the value of the idno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdno() {
        return idno;
    }

    /**
     * Sets the value of the idno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdno(String value) {
        this.idno = value;
    }

    /**
     * Gets the value of the ipdNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpdNo() {
        return ipdNo;
    }

    /**
     * Sets the value of the ipdNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpdNo(String value) {
        this.ipdNo = value;
    }

    /**
     * Gets the value of the kidExamRcntDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKidExamRcntDate() {
        return kidExamRcntDate;
    }

    /**
     * Sets the value of the kidExamRcntDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKidExamRcntDate(String value) {
        this.kidExamRcntDate = value;
    }

    /**
     * Gets the value of the kidInjRcntDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKidInjRcntDate() {
        return kidInjRcntDate;
    }

    /**
     * Sets the value of the kidInjRcntDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKidInjRcntDate(String value) {
        this.kidInjRcntDate = value;
    }

    /**
     * Gets the value of the lawProtectFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLawProtectFlg() {
        return lawProtectFlg;
    }

    /**
     * Sets the value of the lawProtectFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLawProtectFlg(String value) {
        this.lawProtectFlg = value;
    }

    /**
     * Gets the value of the lmpDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLmpDate() {
        return lmpDate;
    }

    /**
     * Sets the value of the lmpDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLmpDate(String value) {
        this.lmpDate = value;
    }

    /**
     * Gets the value of the marriageCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMarriageCode() {
        return marriageCode;
    }

    /**
     * Sets the value of the marriageCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarriageCode(String value) {
        this.marriageCode = value;
    }

    /**
     * Gets the value of the mergeFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMergeFlg() {
        return mergeFlg;
    }

    /**
     * Sets the value of the mergeFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMergeFlg(String value) {
        this.mergeFlg = value;
    }

    /**
     * Gets the value of the mergeTomrno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMergeTomrno() {
        return mergeTomrno;
    }

    /**
     * Sets the value of the mergeTomrno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMergeTomrno(String value) {
        this.mergeTomrno = value;
    }

    /**
     * Gets the value of the motherIdno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMotherIdno() {
        return motherIdno;
    }

    /**
     * Sets the value of the motherIdno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMotherIdno(String value) {
        this.motherIdno = value;
    }

    /**
     * Gets the value of the motherMrno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMotherMrno() {
        return motherMrno;
    }

    /**
     * Sets the value of the motherMrno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMotherMrno(String value) {
        this.motherMrno = value;
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
     * Gets the value of the nameInvisibleFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameInvisibleFlg() {
        return nameInvisibleFlg;
    }

    /**
     * Sets the value of the nameInvisibleFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameInvisibleFlg(String value) {
        this.nameInvisibleFlg = value;
    }

    /**
     * Gets the value of the nationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNationCode() {
        return nationCode;
    }

    /**
     * Sets the value of the nationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNationCode(String value) {
        this.nationCode = value;
    }

    /**
     * Gets the value of the newbornSeq property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNewbornSeq() {
        return newbornSeq;
    }

    /**
     * Sets the value of the newbornSeq property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNewbornSeq(Integer value) {
        this.newbornSeq = value;
    }

    /**
     * Gets the value of the nhiCardNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNhiCardNo() {
        return nhiCardNo;
    }

    /**
     * Sets the value of the nhiCardNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNhiCardNo(String value) {
        this.nhiCardNo = value;
    }

    /**
     * Gets the value of the nhiNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNhiNo() {
        return nhiNo;
    }

    /**
     * Sets the value of the nhiNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNhiNo(String value) {
        this.nhiNo = value;
    }

    /**
     * Gets the value of the occCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOccCode() {
        return occCode;
    }

    /**
     * Sets the value of the occCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOccCode(String value) {
        this.occCode = value;
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
     * Gets the value of the pat1Code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPat1Code() {
        return pat1Code;
    }

    /**
     * Sets the value of the pat1Code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPat1Code(String value) {
        this.pat1Code = value;
    }

    /**
     * Gets the value of the pat2Code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPat2Code() {
        return pat2Code;
    }

    /**
     * Sets the value of the pat2Code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPat2Code(String value) {
        this.pat2Code = value;
    }

    /**
     * Gets the value of the pat3Code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPat3Code() {
        return pat3Code;
    }

    /**
     * Sets the value of the pat3Code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPat3Code(String value) {
        this.pat3Code = value;
    }

    /**
     * Gets the value of the patName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatName() {
        return patName;
    }

    /**
     * Sets the value of the patName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatName(String value) {
        this.patName = value;
    }

    /**
     * Gets the value of the patName1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatName1() {
        return patName1;
    }

    /**
     * Sets the value of the patName1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatName1(String value) {
        this.patName1 = value;
    }

    /**
     * Gets the value of the postCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * Sets the value of the postCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostCode(String value) {
        this.postCode = value;
    }

    /**
     * Gets the value of the postCompany property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostCompany() {
        return postCompany;
    }

    /**
     * Sets the value of the postCompany property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostCompany(String value) {
        this.postCompany = value;
    }

    /**
     * Gets the value of the pregnantDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPregnantDate() {
        return pregnantDate;
    }

    /**
     * Sets the value of the pregnantDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPregnantDate(String value) {
        this.pregnantDate = value;
    }

    /**
     * Gets the value of the prematureFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrematureFlg() {
        return prematureFlg;
    }

    /**
     * Sets the value of the prematureFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrematureFlg(String value) {
        this.prematureFlg = value;
    }

    /**
     * Gets the value of the py1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPy1() {
        return py1;
    }

    /**
     * Sets the value of the py1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPy1(String value) {
        this.py1 = value;
    }

    /**
     * Gets the value of the py2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPy2() {
        return py2;
    }

    /**
     * Sets the value of the py2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPy2(String value) {
        this.py2 = value;
    }

    /**
     * Gets the value of the rcntEmgDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRcntEmgDate() {
        return rcntEmgDate;
    }

    /**
     * Sets the value of the rcntEmgDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRcntEmgDate(String value) {
        this.rcntEmgDate = value;
    }

    /**
     * Gets the value of the rcntEmgDept property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRcntEmgDept() {
        return rcntEmgDept;
    }

    /**
     * Sets the value of the rcntEmgDept property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRcntEmgDept(String value) {
        this.rcntEmgDept = value;
    }

    /**
     * Gets the value of the rcntIpdDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRcntIpdDate() {
        return rcntIpdDate;
    }

    /**
     * Sets the value of the rcntIpdDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRcntIpdDate(String value) {
        this.rcntIpdDate = value;
    }

    /**
     * Gets the value of the rcntIpdDept property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRcntIpdDept() {
        return rcntIpdDept;
    }

    /**
     * Sets the value of the rcntIpdDept property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRcntIpdDept(String value) {
        this.rcntIpdDept = value;
    }

    /**
     * Gets the value of the rcntMissDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRcntMissDate() {
        return rcntMissDate;
    }

    /**
     * Sets the value of the rcntMissDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRcntMissDate(String value) {
        this.rcntMissDate = value;
    }

    /**
     * Gets the value of the rcntMissDept property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRcntMissDept() {
        return rcntMissDept;
    }

    /**
     * Sets the value of the rcntMissDept property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRcntMissDept(String value) {
        this.rcntMissDept = value;
    }

    /**
     * Gets the value of the rcntOpdDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRcntOpdDate() {
        return rcntOpdDate;
    }

    /**
     * Sets the value of the rcntOpdDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRcntOpdDate(String value) {
        this.rcntOpdDate = value;
    }

    /**
     * Gets the value of the rcntOpdDept property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRcntOpdDept() {
        return rcntOpdDept;
    }

    /**
     * Sets the value of the rcntOpdDept property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRcntOpdDept(String value) {
        this.rcntOpdDept = value;
    }

    /**
     * Gets the value of the relationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationCode() {
        return relationCode;
    }

    /**
     * Sets the value of the relationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationCode(String value) {
        this.relationCode = value;
    }

    /**
     * Gets the value of the religionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReligionCode() {
        return religionCode;
    }

    /**
     * Sets the value of the religionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReligionCode(String value) {
        this.religionCode = value;
    }

    /**
     * Gets the value of the residAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResidAddress() {
        return residAddress;
    }

    /**
     * Sets the value of the residAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResidAddress(String value) {
        this.residAddress = value;
    }

    /**
     * Gets the value of the residPostCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResidPostCode() {
        return residPostCode;
    }

    /**
     * Sets the value of the residPostCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResidPostCode(String value) {
        this.residPostCode = value;
    }

    /**
     * Gets the value of the sexCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSexCode() {
        return sexCode;
    }

    /**
     * Sets the value of the sexCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSexCode(String value) {
        this.sexCode = value;
    }

    /**
     * Gets the value of the smearRcntDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmearRcntDate() {
        return smearRcntDate;
    }

    /**
     * Sets the value of the smearRcntDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmearRcntDate(String value) {
        this.smearRcntDate = value;
    }

    /**
     * Gets the value of the speciesCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpeciesCode() {
        return speciesCode;
    }

    /**
     * Sets the value of the speciesCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpeciesCode(String value) {
        this.speciesCode = value;
    }

    /**
     * Gets the value of the spouseIdno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpouseIdno() {
        return spouseIdno;
    }

    /**
     * Sets the value of the spouseIdno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpouseIdno(String value) {
        this.spouseIdno = value;
    }

    /**
     * Gets the value of the telCompany property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelCompany() {
        return telCompany;
    }

    /**
     * Sets the value of the telCompany property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelCompany(String value) {
        this.telCompany = value;
    }

    /**
     * Gets the value of the telHome property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelHome() {
        return telHome;
    }

    /**
     * Sets the value of the telHome property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelHome(String value) {
        this.telHome = value;
    }

    /**
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWeight(Double value) {
        this.weight = value;
    }
    
    /** full constructor */
	public SysPatinfo(String mrNo, String ipdNo, String deleteFlg,
			String mergeFlg, String motherMrno, String patName,
			String patName1, String py1, String py2, String foreignerFlg,
			String idno, String birthDate, String ctz1Code, String ctz2Code,
			String ctz3Code, String telCompany, String telHome,
			String cellPhone, String companyDesc, String EMail,
			String bloodType, String bloodRhType, String sexCode,
			String marriageCode, String postCode, String address,
			String residPostCode, String residAddress, String contactsName,
			String relationCode, String contactsTel, String contactsAddress,
			String spouseIdno, String fatherIdno, String motherIdno,
			String religionCode, String educationCode, String occCode,
			String nationCode, String speciesCode, String firstAdmDate,
			String rcntOpdDate, String rcntOpdDept, String rcntIpdDate,
			String rcntIpdDept, String rcntEmgDate, String rcntEmgDept,
			String rcntMissDate, String rcntMissDept,
			String kidExamRcntDate, String kidInjRcntDate,
			String adultExamDate, String smearRcntDate,
			String deadDate, Double height, Double weight,
			String description, String borninFlg, Integer newbornSeq,
			String prematureFlg, String handicapFlg, String blackFlg,
			String nameInvisibleFlg, String lawProtectFlg, String lmpDate,
			String pregnantDate, String breastfeedStartdate,
			String breastfeedEnddate, String pat1Code, String pat2Code,
			String pat3Code, String mergeTomrno, String optUser,
			String optDate, String optTerm, String homeplaceCode,
			String familyHistory, String handleFlg, String nhiNo,
			String addressCompany, String postCompany, String birthplace,
			String ccbPersonNo, String nhiCardNo) {
		this.mrNo = mrNo;
		this.ipdNo = ipdNo;
		this.deleteFlg = deleteFlg;
		this.mergeFlg = mergeFlg;
		this.motherMrno = motherMrno;
		this.patName = patName;
		this.patName1 = patName1;
		this.py1 = py1;
		this.py2 = py2;
		this.foreignerFlg = foreignerFlg;
		this.idno = idno;
		this.birthDate = birthDate;
		this.ctz1Code = ctz1Code;
		this.ctz2Code = ctz2Code;
		this.ctz3Code = ctz3Code;
		this.telCompany = telCompany;
		this.telHome = telHome;
		this.cellPhone = cellPhone;
		this.companyDesc = companyDesc;
		this.eMail = EMail;
		this.bloodType = bloodType;
		this.bloodRhType = bloodRhType;
		this.sexCode = sexCode;
		this.marriageCode = marriageCode;
		this.postCode = postCode;
		this.address = address;
		this.residPostCode = residPostCode;
		this.residAddress = residAddress;
		this.contactsName = contactsName;
		this.relationCode = relationCode;
		this.contactsTel = contactsTel;
		this.contactsAddress = contactsAddress;
		this.spouseIdno = spouseIdno;
		this.fatherIdno = fatherIdno;
		this.motherIdno = motherIdno;
		this.religionCode = religionCode;
		this.educationCode = educationCode;
		this.occCode = occCode;
		this.nationCode = nationCode;
		this.speciesCode = speciesCode;
		this.firstAdmDate = firstAdmDate;
		this.rcntOpdDate = rcntOpdDate;
		this.rcntOpdDept = rcntOpdDept;
		this.rcntIpdDate = rcntIpdDate;
		this.rcntIpdDept = rcntIpdDept;
		this.rcntEmgDate = rcntEmgDate;
		this.rcntEmgDept = rcntEmgDept;
		this.rcntMissDate = rcntMissDate;
		this.rcntMissDept = rcntMissDept;
		this.kidExamRcntDate = kidExamRcntDate;
		this.kidInjRcntDate = kidInjRcntDate;
		this.adultExamDate = adultExamDate;
		this.smearRcntDate = smearRcntDate;
		this.deadDate = deadDate;
		this.height = height;
		this.weight = weight;
		this.description = description;
		this.borninFlg = borninFlg;
		this.newbornSeq = newbornSeq;
		this.prematureFlg = prematureFlg;
		this.handicapFlg = handicapFlg;
		this.blackFlg = blackFlg;
		this.nameInvisibleFlg = nameInvisibleFlg;
		this.lawProtectFlg = lawProtectFlg;
		this.lmpDate = lmpDate;
		this.pregnantDate = pregnantDate;
		this.breastfeedStartdate = breastfeedStartdate;
		this.breastfeedEnddate = breastfeedEnddate;
		this.pat1Code = pat1Code;
		this.pat2Code = pat2Code;
		this.pat3Code = pat3Code;
		this.mergeTomrno = mergeTomrno;
		this.optUser = optUser;
		this.optDate = optDate;
		this.optTerm = optTerm;
		this.homeplaceCode = homeplaceCode;
		this.familyHistory = familyHistory;
		this.handleFlg = handleFlg;
		this.nhiNo = nhiNo;
		this.addressCompany = addressCompany;
		this.postCompany = postCompany;
		this.birthplace = birthplace;
		this.ccbPersonNo = ccbPersonNo;
		this.nhiCardNo = nhiCardNo;
	}
	
	public SysPatinfo(){
		
	}

}
