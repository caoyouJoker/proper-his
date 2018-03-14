
package action.ind.stockclient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for indStock complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="indStock">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="activeFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="batchNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="batchSeq" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="checkmodiAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="checkmodiQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="cosOutAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="cosOutQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="dosageAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="doseageQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="favorQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="freezeTot" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="gifInAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="gifInQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="gifOutAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="gifOutQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="inAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="inQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="isUpdate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastTotstockAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="lastTotstockQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="materialLocCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optTerm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orgCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="outAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="outQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="profitLossAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="readjustpFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regressdrugAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="regressdrugQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="regressgoodsAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="regressgoodsQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="requestInAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="requestInQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="requestOutAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="requestOutQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="retInAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="retInQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="retOutAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="retOutQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="retailPrice" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="stockFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stockQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="stockinAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="stockinQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="stockoutAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="stockoutQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="supCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="thiInAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="thiInQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="thoOutAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="thoOutQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="validDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="verifyinAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="verifyinPrice" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="verifyinQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="wasOutAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="wasOutQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "indStock", propOrder = {
    "activeFlg",
    "batchNo",
    "batchSeq",
    "checkmodiAmt",
    "checkmodiQty",
    "cosOutAmt",
    "cosOutQty",
    "dosageAmt",
    "doseageQty",
    "favorQty",
    "freezeTot",
    "gifInAmt",
    "gifInQty",
    "gifOutAmt",
    "gifOutQty",
    "inAmt",
    "inQty",
    "isUpdate",
    "lastTotstockAmt",
    "lastTotstockQty",
    "materialLocCode",
    "optDate",
    "optTerm",
    "optUser",
    "orderCode",
    "orgCode",
    "outAmt",
    "outQty",
    "profitLossAmt",
    "readjustpFlg",
    "regionCode",
    "regressdrugAmt",
    "regressdrugQty",
    "regressgoodsAmt",
    "regressgoodsQty",
    "requestInAmt",
    "requestInQty",
    "requestOutAmt",
    "requestOutQty",
    "retInAmt",
    "retInQty",
    "retOutAmt",
    "retOutQty",
    "retailPrice",
    "stockFlg",
    "stockQty",
    "stockinAmt",
    "stockinQty",
    "stockoutAmt",
    "stockoutQty",
    "supCode",
    "thiInAmt",
    "thiInQty",
    "thoOutAmt",
    "thoOutQty",
    "validDate",
    "verifyinAmt",
    "verifyinPrice",
    "verifyinQty",
    "wasOutAmt",
    "wasOutQty"
})
public class IndStock {

    protected String activeFlg;
    protected String batchNo;
    protected Integer batchSeq;
    protected Double checkmodiAmt;
    protected Double checkmodiQty;
    protected Double cosOutAmt;
    protected Double cosOutQty;
    protected Double dosageAmt;
    protected Double doseageQty;
    protected Double favorQty;
    protected Double freezeTot;
    protected Double gifInAmt;
    protected Double gifInQty;
    protected Double gifOutAmt;
    protected Double gifOutQty;
    protected Double inAmt;
    protected Double inQty;
    protected String isUpdate;
    protected Double lastTotstockAmt;
    protected Double lastTotstockQty;
    protected String materialLocCode;
    protected String optDate;
    protected String optTerm;
    protected String optUser;
    protected String orderCode;
    protected String orgCode;
    protected Double outAmt;
    protected Double outQty;
    protected Double profitLossAmt;
    protected String readjustpFlg;
    protected String regionCode;
    protected Double regressdrugAmt;
    protected Double regressdrugQty;
    protected Double regressgoodsAmt;
    protected Double regressgoodsQty;
    protected Double requestInAmt;
    protected Double requestInQty;
    protected Double requestOutAmt;
    protected Double requestOutQty;
    protected Double retInAmt;
    protected Double retInQty;
    protected Double retOutAmt;
    protected Double retOutQty;
    protected Double retailPrice;
    protected String stockFlg;
    protected Double stockQty;
    protected Double stockinAmt;
    protected Double stockinQty;
    protected Double stockoutAmt;
    protected Double stockoutQty;
    protected String supCode;
    protected Double thiInAmt;
    protected Double thiInQty;
    protected Double thoOutAmt;
    protected Double thoOutQty;
    protected String validDate;
    protected Double verifyinAmt;
    protected Double verifyinPrice;
    protected Double verifyinQty;
    protected Double wasOutAmt;
    protected Double wasOutQty;

    /**
     * Gets the value of the activeFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActiveFlg() {
        return activeFlg;
    }

    /**
     * Sets the value of the activeFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActiveFlg(String value) {
        this.activeFlg = value;
    }

    /**
     * Gets the value of the batchNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatchNo() {
        return batchNo;
    }

    /**
     * Sets the value of the batchNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchNo(String value) {
        this.batchNo = value;
    }

    /**
     * Gets the value of the batchSeq property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBatchSeq() {
        return batchSeq;
    }

    /**
     * Sets the value of the batchSeq property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBatchSeq(Integer value) {
        this.batchSeq = value;
    }

    /**
     * Gets the value of the checkmodiAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getCheckmodiAmt() {
        return checkmodiAmt;
    }

    /**
     * Sets the value of the checkmodiAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setCheckmodiAmt(Double value) {
        this.checkmodiAmt = value;
    }

    /**
     * Gets the value of the checkmodiQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getCheckmodiQty() {
        return checkmodiQty;
    }

    /**
     * Sets the value of the checkmodiQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setCheckmodiQty(Double value) {
        this.checkmodiQty = value;
    }

    /**
     * Gets the value of the cosOutAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getCosOutAmt() {
        return cosOutAmt;
    }

    /**
     * Sets the value of the cosOutAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setCosOutAmt(Double value) {
        this.cosOutAmt = value;
    }

    /**
     * Gets the value of the cosOutQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getCosOutQty() {
        return cosOutQty;
    }

    /**
     * Sets the value of the cosOutQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setCosOutQty(Double value) {
        this.cosOutQty = value;
    }

    /**
     * Gets the value of the dosageAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDosageAmt() {
        return dosageAmt;
    }

    /**
     * Sets the value of the dosageAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDosageAmt(Double value) {
        this.dosageAmt = value;
    }

    /**
     * Gets the value of the doseageQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDoseageQty() {
        return doseageQty;
    }

    /**
     * Sets the value of the doseageQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDoseageQty(Double value) {
        this.doseageQty = value;
    }

    /**
     * Gets the value of the favorQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getFavorQty() {
        return favorQty;
    }

    /**
     * Sets the value of the favorQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setFavorQty(Double value) {
        this.favorQty = value;
    }

    /**
     * Gets the value of the freezeTot property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getFreezeTot() {
        return freezeTot;
    }

    /**
     * Sets the value of the freezeTot property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setFreezeTot(Double value) {
        this.freezeTot = value;
    }

    /**
     * Gets the value of the gifInAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getGifInAmt() {
        return gifInAmt;
    }

    /**
     * Sets the value of the gifInAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setGifInAmt(Double value) {
        this.gifInAmt = value;
    }

    /**
     * Gets the value of the gifInQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getGifInQty() {
        return gifInQty;
    }

    /**
     * Sets the value of the gifInQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setGifInQty(Double value) {
        this.gifInQty = value;
    }

    /**
     * Gets the value of the gifOutAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getGifOutAmt() {
        return gifOutAmt;
    }

    /**
     * Sets the value of the gifOutAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setGifOutAmt(Double value) {
        this.gifOutAmt = value;
    }

    /**
     * Gets the value of the gifOutQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getGifOutQty() {
        return gifOutQty;
    }

    /**
     * Sets the value of the gifOutQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setGifOutQty(Double value) {
        this.gifOutQty = value;
    }

    /**
     * Gets the value of the inAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getInAmt() {
        return inAmt;
    }

    /**
     * Sets the value of the inAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setInAmt(Double value) {
        this.inAmt = value;
    }

    /**
     * Gets the value of the inQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getInQty() {
        return inQty;
    }

    /**
     * Sets the value of the inQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setInQty(Double value) {
        this.inQty = value;
    }

    /**
     * Gets the value of the isUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsUpdate() {
        return isUpdate;
    }

    /**
     * Sets the value of the isUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsUpdate(String value) {
        this.isUpdate = value;
    }

    /**
     * Gets the value of the lastTotstockAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getLastTotstockAmt() {
        return lastTotstockAmt;
    }

    /**
     * Sets the value of the lastTotstockAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setLastTotstockAmt(Double value) {
        this.lastTotstockAmt = value;
    }

    /**
     * Gets the value of the lastTotstockQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getLastTotstockQty() {
        return lastTotstockQty;
    }

    /**
     * Sets the value of the lastTotstockQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setLastTotstockQty(Double value) {
        this.lastTotstockQty = value;
    }

    /**
     * Gets the value of the materialLocCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterialLocCode() {
        return materialLocCode;
    }

    /**
     * Sets the value of the materialLocCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterialLocCode(String value) {
        this.materialLocCode = value;
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
     * Gets the value of the orgCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgCode() {
        return orgCode;
    }

    /**
     * Sets the value of the orgCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgCode(String value) {
        this.orgCode = value;
    }

    /**
     * Gets the value of the outAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getOutAmt() {
        return outAmt;
    }

    /**
     * Sets the value of the outAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setOutAmt(Double value) {
        this.outAmt = value;
    }

    /**
     * Gets the value of the outQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getOutQty() {
        return outQty;
    }

    /**
     * Sets the value of the outQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setOutQty(Double value) {
        this.outQty = value;
    }

    /**
     * Gets the value of the profitLossAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getProfitLossAmt() {
        return profitLossAmt;
    }

    /**
     * Sets the value of the profitLossAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setProfitLossAmt(Double value) {
        this.profitLossAmt = value;
    }

    /**
     * Gets the value of the readjustpFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReadjustpFlg() {
        return readjustpFlg;
    }

    /**
     * Sets the value of the readjustpFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReadjustpFlg(String value) {
        this.readjustpFlg = value;
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
     * Gets the value of the regressdrugAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRegressdrugAmt() {
        return regressdrugAmt;
    }

    /**
     * Sets the value of the regressdrugAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRegressdrugAmt(Double value) {
        this.regressdrugAmt = value;
    }

    /**
     * Gets the value of the regressdrugQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRegressdrugQty() {
        return regressdrugQty;
    }

    /**
     * Sets the value of the regressdrugQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRegressdrugQty(Double value) {
        this.regressdrugQty = value;
    }

    /**
     * Gets the value of the regressgoodsAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRegressgoodsAmt() {
        return regressgoodsAmt;
    }

    /**
     * Sets the value of the regressgoodsAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRegressgoodsAmt(Double value) {
        this.regressgoodsAmt = value;
    }

    /**
     * Gets the value of the regressgoodsQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRegressgoodsQty() {
        return regressgoodsQty;
    }

    /**
     * Sets the value of the regressgoodsQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRegressgoodsQty(Double value) {
        this.regressgoodsQty = value;
    }

    /**
     * Gets the value of the requestInAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRequestInAmt() {
        return requestInAmt;
    }

    /**
     * Sets the value of the requestInAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRequestInAmt(Double value) {
        this.requestInAmt = value;
    }

    /**
     * Gets the value of the requestInQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRequestInQty() {
        return requestInQty;
    }

    /**
     * Sets the value of the requestInQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRequestInQty(Double value) {
        this.requestInQty = value;
    }

    /**
     * Gets the value of the requestOutAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRequestOutAmt() {
        return requestOutAmt;
    }

    /**
     * Sets the value of the requestOutAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRequestOutAmt(Double value) {
        this.requestOutAmt = value;
    }

    /**
     * Gets the value of the requestOutQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRequestOutQty() {
        return requestOutQty;
    }

    /**
     * Sets the value of the requestOutQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRequestOutQty(Double value) {
        this.requestOutQty = value;
    }

    /**
     * Gets the value of the retInAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRetInAmt() {
        return retInAmt;
    }

    /**
     * Sets the value of the retInAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRetInAmt(Double value) {
        this.retInAmt = value;
    }

    /**
     * Gets the value of the retInQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRetInQty() {
        return retInQty;
    }

    /**
     * Sets the value of the retInQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRetInQty(Double value) {
        this.retInQty = value;
    }

    /**
     * Gets the value of the retOutAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRetOutAmt() {
        return retOutAmt;
    }

    /**
     * Sets the value of the retOutAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRetOutAmt(Double value) {
        this.retOutAmt = value;
    }

    /**
     * Gets the value of the retOutQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRetOutQty() {
        return retOutQty;
    }

    /**
     * Sets the value of the retOutQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRetOutQty(Double value) {
        this.retOutQty = value;
    }

    /**
     * Gets the value of the retailPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRetailPrice() {
        return retailPrice;
    }

    /**
     * Sets the value of the retailPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRetailPrice(Double value) {
        this.retailPrice = value;
    }

    /**
     * Gets the value of the stockFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStockFlg() {
        return stockFlg;
    }

    /**
     * Sets the value of the stockFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStockFlg(String value) {
        this.stockFlg = value;
    }

    /**
     * Gets the value of the stockQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getStockQty() {
        return stockQty;
    }

    /**
     * Sets the value of the stockQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setStockQty(Double value) {
        this.stockQty = value;
    }

    /**
     * Gets the value of the stockinAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getStockinAmt() {
        return stockinAmt;
    }

    /**
     * Sets the value of the stockinAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setStockinAmt(Double value) {
        this.stockinAmt = value;
    }

    /**
     * Gets the value of the stockinQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getStockinQty() {
        return stockinQty;
    }

    /**
     * Sets the value of the stockinQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setStockinQty(Double value) {
        this.stockinQty = value;
    }

    /**
     * Gets the value of the stockoutAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getStockoutAmt() {
        return stockoutAmt;
    }

    /**
     * Sets the value of the stockoutAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setStockoutAmt(Double value) {
        this.stockoutAmt = value;
    }

    /**
     * Gets the value of the stockoutQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getStockoutQty() {
        return stockoutQty;
    }

    /**
     * Sets the value of the stockoutQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setStockoutQty(Double value) {
        this.stockoutQty = value;
    }

    /**
     * Gets the value of the supCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupCode() {
        return supCode;
    }

    /**
     * Sets the value of the supCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupCode(String value) {
        this.supCode = value;
    }

    /**
     * Gets the value of the thiInAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getThiInAmt() {
        return thiInAmt;
    }

    /**
     * Sets the value of the thiInAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setThiInAmt(Double value) {
        this.thiInAmt = value;
    }

    /**
     * Gets the value of the thiInQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getThiInQty() {
        return thiInQty;
    }

    /**
     * Sets the value of the thiInQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setThiInQty(Double value) {
        this.thiInQty = value;
    }

    /**
     * Gets the value of the thoOutAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getThoOutAmt() {
        return thoOutAmt;
    }

    /**
     * Sets the value of the thoOutAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setThoOutAmt(Double value) {
        this.thoOutAmt = value;
    }

    /**
     * Gets the value of the thoOutQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getThoOutQty() {
        return thoOutQty;
    }

    /**
     * Sets the value of the thoOutQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setThoOutQty(Double value) {
        this.thoOutQty = value;
    }

    /**
     * Gets the value of the validDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidDate() {
        return validDate;
    }

    /**
     * Sets the value of the validDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidDate(String value) {
        this.validDate = value;
    }

    /**
     * Gets the value of the verifyinAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getVerifyinAmt() {
        return verifyinAmt;
    }

    /**
     * Sets the value of the verifyinAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setVerifyinAmt(Double value) {
        this.verifyinAmt = value;
    }

    /**
     * Gets the value of the verifyinPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getVerifyinPrice() {
        return verifyinPrice;
    }

    /**
     * Sets the value of the verifyinPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setVerifyinPrice(Double value) {
        this.verifyinPrice = value;
    }

    /**
     * Gets the value of the verifyinQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getVerifyinQty() {
        return verifyinQty;
    }

    /**
     * Sets the value of the verifyinQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setVerifyinQty(Double value) {
        this.verifyinQty = value;
    }

    /**
     * Gets the value of the wasOutAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWasOutAmt() {
        return wasOutAmt;
    }

    /**
     * Sets the value of the wasOutAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWasOutAmt(Double value) {
        this.wasOutAmt = value;
    }

    /**
     * Gets the value of the wasOutQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWasOutQty() {
        return wasOutQty;
    }

    /**
     * Sets the value of the wasOutQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWasOutQty(Double value) {
        this.wasOutQty = value;
    }

}
