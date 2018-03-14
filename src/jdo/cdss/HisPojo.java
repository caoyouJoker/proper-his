
package jdo.cdss;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>hisPojo complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="hisPojo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="admType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="advicePojos" type="{http://service.zhangp.com/}advicePojo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="advise" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="age" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ageDay" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ageMonth" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="allergyFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="allergyPojos" type="{http://service.zhangp.com/}allergyPojo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="breakPoints" type="{http://service.zhangp.com/}breakPoint" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="calWeight" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="diags" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="erdPojos" type="{http://service.zhangp.com/}erdPojo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="exaPojos" type="{http://service.zhangp.com/}exaPojo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="gestationalWeeks" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="lastExaPojos" type="{http://service.zhangp.com/}exaPojo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lastOrderPojos" type="{http://service.zhangp.com/}orderPojo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="chestpainOrderPojos" type="{http://service.zhangp.com/}orderPojo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lmpFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mainDiag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mrNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="newBornFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="operationDiags" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="orderPojos" type="{http://service.zhangp.com/}orderPojo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="sex" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weight" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="orderCodeList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="singleExeOrderPojos" type="{http://service.zhangp.com/}orderPojo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hisPojo", propOrder = {
    "admType",
    "advicePojos",
    "advise",
    "age",
    "ageDay",
    "ageMonth",
    "allergyFlg",
    "allergyPojos",
    "breakPoints",
    "calWeight",
    "diags",
    "erdPojos",
    "exaPojos",
    "gestationalWeeks",
    "lastExaPojos",
    "lastOrderPojos",
    "lmpFlg",
    "mainDiag",
    "mrNo",
    "newBornFlg",
    "operationDiags",
    "orderPojos",
    "sex",
    "weight",
    "orderCodeList",
    "chestpainOrderPojos",
    "singleExeOrderPojos"
})
public class HisPojo {

    protected String admType;
    @XmlElement(nillable = true)
    protected List<AdvicePojo> advicePojos;
    @XmlElement(nillable = true)
    protected List<String> advise;
    protected Integer age;
    protected Integer ageDay;
    protected Integer ageMonth;
    protected String allergyFlg;
    @XmlElement(nillable = true)
    protected List<AllergyPojo> allergyPojos;
    @XmlElement(nillable = true)
    protected List<BreakPoint> breakPoints;
    protected Double calWeight;
    @XmlElement(nillable = true)
    protected List<String> diags;
    @XmlElement(nillable = true)
    protected List<String> orderCodeList;
    @XmlElement(nillable = true)
    protected List<ErdPojo> erdPojos;
    @XmlElement(nillable = true)
    protected List<ExaPojo> exaPojos;
    protected Integer gestationalWeeks;
    @XmlElement(nillable = true)
    protected List<ExaPojo> lastExaPojos;
    @XmlElement(nillable = true)
    protected List<OrderPojo> lastOrderPojos;
    @XmlElement(nillable = true)
    protected List<OrderPojo> chestpainOrderPojos;
    protected String lmpFlg;
    protected String mainDiag;
    protected String mrNo;
    protected String newBornFlg;
    @XmlElement(nillable = true)
    protected List<String> operationDiags;
    @XmlElement(nillable = true)
    protected List<OrderPojo> orderPojos;
    protected String sex;
    protected Double weight;
    @XmlElement(nillable = true)
    protected List<OrderPojo> singleExeOrderPojos;

    /**
     * 获取admType属性的值。
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
     * 设置admType属性的值。
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
     * Gets the value of the advicePojos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the advicePojos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdvicePojos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdvicePojo }
     * 
     * 
     */
    public List<AdvicePojo> getAdvicePojos() {
        if (advicePojos == null) {
            advicePojos = new ArrayList<AdvicePojo>();
        }
        return this.advicePojos;
    }

    /**
     * Gets the value of the advise property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the advise property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdvise().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAdvise() {
        if (advise == null) {
            advise = new ArrayList<String>();
        }
        return this.advise;
    }

    /**
     * 获取age属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAge() {
        return age;
    }

    /**
     * 设置age属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAge(Integer value) {
        this.age = value;
    }

    /**
     * 获取ageDay属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAgeDay() {
        return ageDay;
    }

    /**
     * 设置ageDay属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAgeDay(Integer value) {
        this.ageDay = value;
    }

    /**
     * 获取ageMonth属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAgeMonth() {
        return ageMonth;
    }

    /**
     * 设置ageMonth属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAgeMonth(Integer value) {
        this.ageMonth = value;
    }

    /**
     * 获取allergyFlg属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAllergyFlg() {
        return allergyFlg;
    }

    /**
     * 设置allergyFlg属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllergyFlg(String value) {
        this.allergyFlg = value;
    }

    /**
     * Gets the value of the allergyPojos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allergyPojos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllergyPojos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AllergyPojo }
     * 
     * 
     */
    public List<AllergyPojo> getAllergyPojos() {
        if (allergyPojos == null) {
            allergyPojos = new ArrayList<AllergyPojo>();
        }
        return this.allergyPojos;
    }

    /**
     * Gets the value of the breakPoints property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the breakPoints property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBreakPoints().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BreakPoint }
     * 
     * 
     */
    public List<BreakPoint> getBreakPoints() {
        if (breakPoints == null) {
            breakPoints = new ArrayList<BreakPoint>();
        }
        return this.breakPoints;
    }

    /**
     * 获取calWeight属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getCalWeight() {
        return calWeight;
    }

    /**
     * 设置calWeight属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setCalWeight(Double value) {
        this.calWeight = value;
    }

    /**
     * Gets the value of the diags property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the diags property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDiags().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDiags() {
        if (diags == null) {
            diags = new ArrayList<String>();
        }
        return this.diags;
    }

    /**
     * Gets the value of the erdPojos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the erdPojos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getErdPojos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ErdPojo }
     * 
     * 
     */
    public List<ErdPojo> getErdPojos() {
        if (erdPojos == null) {
            erdPojos = new ArrayList<ErdPojo>();
        }
        return this.erdPojos;
    }

    /**
     * Gets the value of the exaPojos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the exaPojos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExaPojos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExaPojo }
     * 
     * 
     */
    public List<ExaPojo> getExaPojos() {
        if (exaPojos == null) {
            exaPojos = new ArrayList<ExaPojo>();
        }
        return this.exaPojos;
    }

    /**
     * 获取gestationalWeeks属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getGestationalWeeks() {
        return gestationalWeeks;
    }

    /**
     * 设置gestationalWeeks属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setGestationalWeeks(Integer value) {
        this.gestationalWeeks = value;
    }

    /**
     * Gets the value of the lastExaPojos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lastExaPojos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLastExaPojos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExaPojo }
     * 
     * 
     */
    public List<ExaPojo> getLastExaPojos() {
        if (lastExaPojos == null) {
            lastExaPojos = new ArrayList<ExaPojo>();
        }
        return this.lastExaPojos;
    }

    /**
     * Gets the value of the lastOrderPojos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lastOrderPojos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLastOrderPojos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrderPojo }
     * 
     * 
     */
    public List<OrderPojo> getLastOrderPojos() {
        if (lastOrderPojos == null) {
            lastOrderPojos = new ArrayList<OrderPojo>();
        }
        return this.lastOrderPojos;
    }

    /**
     * 获取lmpFlg属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLmpFlg() {
        return lmpFlg;
    }

    /**
     * 设置lmpFlg属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLmpFlg(String value) {
        this.lmpFlg = value;
    }

    /**
     * 获取mainDiag属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMainDiag() {
        return mainDiag;
    }

    /**
     * 设置mainDiag属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMainDiag(String value) {
        this.mainDiag = value;
    }

    /**
     * 获取mrNo属性的值。
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
     * 设置mrNo属性的值。
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
     * 获取newBornFlg属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewBornFlg() {
        return newBornFlg;
    }

    /**
     * 设置newBornFlg属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewBornFlg(String value) {
        this.newBornFlg = value;
    }

    /**
     * Gets the value of the operationDiags property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operationDiags property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperationDiags().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getOperationDiags() {
        if (operationDiags == null) {
            operationDiags = new ArrayList<String>();
        }
        return this.operationDiags;
    }

    /**
     * Gets the value of the orderPojos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the orderPojos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrderPojos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrderPojo }
     * 
     * 
     */
    public List<OrderPojo> getOrderPojos() {
        if (orderPojos == null) {
            orderPojos = new ArrayList<OrderPojo>();
        }
        return this.orderPojos;
    }

    /**
     * 获取sex属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSex() {
        return sex;
    }

    /**
     * 设置sex属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSex(String value) {
        this.sex = value;
    }

    /**
     * 获取weight属性的值。
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
     * 设置weight属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWeight(Double value) {
        this.weight = value;
    }
    
    public void setAdvicePojos(List<AdvicePojo> advicePojos) {
		this.advicePojos = advicePojos;
	}

	public void setAdvise(List<String> advise) {
		this.advise = advise;
	}

	public void setBreakPoints(List<BreakPoint> breakPoints) {
		this.breakPoints = breakPoints;
	}

	public void setDiags(List<String> diags) {
		this.diags = diags;
	}

	public void setExaPojos(List<ExaPojo> exaPojos) {
		this.exaPojos = exaPojos;
	}
	

	public void setLastExaPojos(List<ExaPojo> lastExaPojos) {
		this.lastExaPojos = lastExaPojos;
	}
	

	public void setLastOrderPojos(List<OrderPojo> lastOrderPojos) {
		this.lastOrderPojos = lastOrderPojos;
	}

	public void setOperationDiags(List<String> operationDiags) {
		this.operationDiags = operationDiags;
	}

	public void setOrderPojos(List<OrderPojo> orderPojos) {
		this.orderPojos = orderPojos;
	}
	
	public void setErdPojos(List<ErdPojo> erdPojos){
		this.erdPojos = erdPojos;
	}

	public void setAllergyPojos(List<AllergyPojo> allergyPojos){
		this.allergyPojos = allergyPojos;
	}

	public List<String> getOrderCodeList() {
		 if (orderCodeList == null) {
			 orderCodeList = new ArrayList<String>();
	      }
		return orderCodeList;
	}

	public void setOrderCodeList(List<String> orderCodeList) {
		
		this.orderCodeList = orderCodeList;
	}

	public List<OrderPojo> getChestpainOrderPojos() {
		if (chestpainOrderPojos == null) {
			chestpainOrderPojos = new ArrayList<OrderPojo>();
        }
		return chestpainOrderPojos;
	}

	public void setChestpainOrderPojos(List<OrderPojo> chestpainOrderPojos) {

		this.chestpainOrderPojos = chestpainOrderPojos;
	}
	
	 public List<OrderPojo> getSingleExeOrderPojos() {
	        if (singleExeOrderPojos == null) {
	        	singleExeOrderPojos = new ArrayList<OrderPojo>();
	        }
	        return this.singleExeOrderPojos;
	    }
	 public void setSingleExeOrderPojos(List<OrderPojo> singleExeOrderPojos) {
			this.singleExeOrderPojos = singleExeOrderPojos;
		}
}
