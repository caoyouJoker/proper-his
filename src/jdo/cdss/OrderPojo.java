
package jdo.cdss;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>orderPojo complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="orderPojo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="freqCycle" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="freqTimes" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="liquidMainFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="liquidNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="medQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="orderCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderDateLong" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="orderDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderTimeLong" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="rxNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seqNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sysDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sysDateLong" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="sysTimeLong" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="tags" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="takeDays" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="transHospCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optitemCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sysPhaClass1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sysPhaClass2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "orderPojo", propOrder = {
    "freqCycle",
    "freqTimes",
    "id",
    "liquidMainFlg",
    "liquidNo",
    "medQty",
    "orderCode",
    "orderDate",
    "orderDateLong",
    "orderDesc",
    "orderTimeLong",
    "rxNo",
    "seqNo",
    "sysDate",
    "sysDateLong",
    "sysTimeLong",
    "tags",
    "takeDays",
    "unit",
    "transHospCode",
    "optitemCode",
    "devCode",
    "sysPhaClass1",
    "sysPhaClass2"
})
public class OrderPojo {

    protected Integer freqCycle;
    protected Integer freqTimes;
    protected String id;
    protected String liquidMainFlg;
    protected String liquidNo;
    protected Double medQty;
    protected String orderCode;
    protected String orderDate;
    protected Long orderDateLong;
    protected String orderDesc;
    protected Long orderTimeLong;
    protected String rxNo;
    protected String seqNo;
    protected String sysDate;
    protected Long sysDateLong;
    protected Long sysTimeLong;
    @XmlElement(nillable = true)
    protected List<String> tags;
    protected Integer takeDays;
    protected String unit;
    protected String transHospCode;
    protected String optitemCode;
    protected String devCode;
    protected String sysPhaClass1;
    protected String sysPhaClass2;

    public String getDevCode() {
		return devCode;
	}

	public void setDevCode(String devCode) {
		this.devCode = devCode;
	}

	public String getSysPhaClass1() {
		return sysPhaClass1;
	}

	public void setSysPhaClass1(String sysPhaClass1) {
		this.sysPhaClass1 = sysPhaClass1;
	}

	public String getSysPhaClass2() {
		return sysPhaClass2;
	}

	public void setSysPhaClass2(String sysPhaClass2) {
		this.sysPhaClass2 = sysPhaClass2;
	}

	public String getTransHospCode() {
		return transHospCode;
	}

	public void setTransHospCode(String transHospCode) {
		this.transHospCode = transHospCode;
	}

	public String getOptitemCode() {
		return optitemCode;
	}

	public void setOptitemCode(String optitemCode) {
		this.optitemCode = optitemCode;
	}

	/**
     * 获取freqCycle属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFreqCycle() {
        return freqCycle;
    }

    /**
     * 设置freqCycle属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFreqCycle(Integer value) {
        this.freqCycle = value;
    }

    /**
     * 获取freqTimes属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFreqTimes() {
        return freqTimes;
    }

    /**
     * 设置freqTimes属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFreqTimes(Integer value) {
        this.freqTimes = value;
    }

    /**
     * 获取id属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * 设置id属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * 获取liquidMainFlg属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLiquidMainFlg() {
        return liquidMainFlg;
    }

    /**
     * 设置liquidMainFlg属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLiquidMainFlg(String value) {
        this.liquidMainFlg = value;
    }

    /**
     * 获取liquidNo属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLiquidNo() {
        return liquidNo;
    }

    /**
     * 设置liquidNo属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLiquidNo(String value) {
        this.liquidNo = value;
    }

    /**
     * 获取medQty属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMedQty() {
        return medQty;
    }

    /**
     * 设置medQty属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMedQty(Double value) {
        this.medQty = value;
    }

    /**
     * 获取orderCode属性的值。
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
     * 设置orderCode属性的值。
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
     * 获取orderDate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderDate() {
        return orderDate;
    }

    /**
     * 设置orderDate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderDate(String value) {
        this.orderDate = value;
    }

    /**
     * 获取orderDateLong属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOrderDateLong() {
        return orderDateLong;
    }

    /**
     * 设置orderDateLong属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOrderDateLong(Long value) {
        this.orderDateLong = value;
    }

    /**
     * 获取orderDesc属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderDesc() {
        return orderDesc;
    }

    /**
     * 设置orderDesc属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderDesc(String value) {
        this.orderDesc = value;
    }

    /**
     * 获取orderTimeLong属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOrderTimeLong() {
        return orderTimeLong;
    }

    /**
     * 设置orderTimeLong属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOrderTimeLong(Long value) {
        this.orderTimeLong = value;
    }

    /**
     * 获取rxNo属性的值。
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
     * 设置rxNo属性的值。
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
     * 获取seqNo属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeqNo() {
        return seqNo;
    }

    /**
     * 设置seqNo属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeqNo(String value) {
        this.seqNo = value;
    }

    /**
     * 获取sysDate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSysDate() {
        return sysDate;
    }

    /**
     * 设置sysDate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSysDate(String value) {
        this.sysDate = value;
    }

    /**
     * 获取sysDateLong属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSysDateLong() {
        return sysDateLong;
    }

    /**
     * 设置sysDateLong属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSysDateLong(Long value) {
        this.sysDateLong = value;
    }

    /**
     * 获取sysTimeLong属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSysTimeLong() {
        return sysTimeLong;
    }

    /**
     * 设置sysTimeLong属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSysTimeLong(Long value) {
        this.sysTimeLong = value;
    }

    /**
     * Gets the value of the tags property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tags property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTags().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTags() {
        if (tags == null) {
            tags = new ArrayList<String>();
        }
        return this.tags;
    }
    
    public void setTags(List<String> tags) {
		this.tags = tags;
	} 

    /**
     * 获取takeDays属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTakeDays() {
        return takeDays;
    }

    /**
     * 设置takeDays属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTakeDays(Integer value) {
        this.takeDays = value;
    }

    /**
     * 获取unit属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 设置unit属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnit(String value) {
        this.unit = value;
    }

}
