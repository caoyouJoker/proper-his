
package action.spc.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for indDispensem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="indDispensem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="appOrgCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dispenseDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dispenseNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dispenseUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="drugCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="indDispenseds" type="{http://ind.jdo/}indDispensed" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="optDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optTerm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reasonChnDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reqtypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stockType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="toOrgCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="unitType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updateFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="urgentFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="warehousingDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="warehousingUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "indDispensem", propOrder = {
    "appOrgCode",
    "description",
    "dispenseDate",
    "dispenseNo",
    "dispenseUser",
    "drugCategory",
    "indDispenseds",
    "optDate",
    "optTerm",
    "optUser",
    "reasonChnDesc",
    "regionCode",
    "reqtypeCode",
    "requestDate",
    "requestNo",
    "stockType",
    "toOrgCode",
    "unitType",
    "updateFlg",
    "urgentFlg",
    "warehousingDate",
    "warehousingUser"
})
public class IndDispensem {

    protected String appOrgCode;
    protected String description;
    protected String dispenseDate;
    protected String dispenseNo;
    protected String dispenseUser;
    protected String drugCategory;
    @XmlElement(nillable = true)
    protected List<IndDispensed> indDispenseds;
    protected String optDate;
    protected String optTerm;
    protected String optUser;
    protected String reasonChnDesc;
    protected String regionCode;
    protected String reqtypeCode;
    protected String requestDate;
    protected String requestNo;
    protected String stockType;
    protected String toOrgCode;
    protected String unitType;
    protected String updateFlg;
    protected String urgentFlg;
    protected String warehousingDate;
    protected String warehousingUser;

    /**
     * Gets the value of the appOrgCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppOrgCode() {
        return appOrgCode;
    }

    /**
     * Sets the value of the appOrgCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppOrgCode(String value) {
        this.appOrgCode = value;
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
     * Gets the value of the dispenseDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDispenseDate() {
        return dispenseDate;
    }

    /**
     * Sets the value of the dispenseDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDispenseDate(String value) {
        this.dispenseDate = value;
    }

    /**
     * Gets the value of the dispenseNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDispenseNo() {
        return dispenseNo;
    }

    /**
     * Sets the value of the dispenseNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDispenseNo(String value) {
        this.dispenseNo = value;
    }

    /**
     * Gets the value of the dispenseUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDispenseUser() {
        return dispenseUser;
    }

    /**
     * Sets the value of the dispenseUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDispenseUser(String value) {
        this.dispenseUser = value;
    }

    /**
     * Gets the value of the drugCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrugCategory() {
        return drugCategory;
    }

    /**
     * Sets the value of the drugCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrugCategory(String value) {
        this.drugCategory = value;
    }

    /**
     * Gets the value of the indDispenseds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the indDispenseds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIndDispenseds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IndDispensed }
     * 
     * 
     */
    public List<IndDispensed> getIndDispenseds() {
        if (indDispenseds == null) {
            indDispenseds = new ArrayList<IndDispensed>();
        }
        return this.indDispenseds;
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
     * Gets the value of the reasonChnDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReasonChnDesc() {
        return reasonChnDesc;
    }

    /**
     * Sets the value of the reasonChnDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReasonChnDesc(String value) {
        this.reasonChnDesc = value;
    }

    /**
     * Gets the value of the regionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * Sets the value of the regionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegionCode(String value) {
        this.regionCode = value;
    }

    /**
     * Gets the value of the reqtypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReqtypeCode() {
        return reqtypeCode;
    }

    /**
     * Sets the value of the reqtypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReqtypeCode(String value) {
        this.reqtypeCode = value;
    }

    /**
     * Gets the value of the requestDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestDate() {
        return requestDate;
    }

    /**
     * Sets the value of the requestDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestDate(String value) {
        this.requestDate = value;
    }

    /**
     * Gets the value of the requestNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestNo() {
        return requestNo;
    }

    /**
     * Sets the value of the requestNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestNo(String value) {
        this.requestNo = value;
    }

    /**
     * Gets the value of the stockType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStockType() {
        return stockType;
    }

    /**
     * Sets the value of the stockType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStockType(String value) {
        this.stockType = value;
    }

    /**
     * Gets the value of the toOrgCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToOrgCode() {
        return toOrgCode;
    }

    /**
     * Sets the value of the toOrgCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToOrgCode(String value) {
        this.toOrgCode = value;
    }

    /**
     * Gets the value of the unitType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitType() {
        return unitType;
    }

    /**
     * Sets the value of the unitType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitType(String value) {
        this.unitType = value;
    }

    /**
     * Gets the value of the updateFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdateFlg() {
        return updateFlg;
    }

    /**
     * Sets the value of the updateFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdateFlg(String value) {
        this.updateFlg = value;
    }

    /**
     * Gets the value of the urgentFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrgentFlg() {
        return urgentFlg;
    }

    /**
     * Sets the value of the urgentFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrgentFlg(String value) {
        this.urgentFlg = value;
    }

    /**
     * Gets the value of the warehousingDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWarehousingDate() {
        return warehousingDate;
    }

    /**
     * Sets the value of the warehousingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWarehousingDate(String value) {
        this.warehousingDate = value;
    }

    /**
     * Gets the value of the warehousingUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWarehousingUser() {
        return warehousingUser;
    }

    /**
     * Sets the value of the warehousingUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWarehousingUser(String value) {
        this.warehousingUser = value;
    }

	public void setIndDispenseds(List<IndDispensed> indDispenseds) {
		this.indDispenseds = indDispenseds;
	}

}
