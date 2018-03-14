
package jdo.cdss;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>clpPojo complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="clpPojo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="clpAdvicePojos" type="{http://service.zhangp.com/}clpAdvicePojo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ctzCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ctzDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deptCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deptDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="diagCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="diagDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="diags" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "clpPojo", propOrder = {
    "clpAdvicePojos",
    "ctzCode",
    "ctzDesc",
    "deptCode",
    "deptDesc",
    "diagCode",
    "diagDesc",
    "diags"
})
public class ClpPojo {

    @XmlElement(nillable = true)
    protected List<ClpAdvicePojo> clpAdvicePojos;
    protected String ctzCode;
    protected String ctzDesc;
    protected String deptCode;
    protected String deptDesc;
    protected String diagCode;
    protected String diagDesc;
    @XmlElement(nillable = true)
    protected List<String> diags;

    /**
     * Gets the value of the clpAdvicePojos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the clpAdvicePojos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClpAdvicePojos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClpAdvicePojo }
     * 
     * 
     */
    public List<ClpAdvicePojo> getClpAdvicePojos() {
        if (clpAdvicePojos == null) {
            clpAdvicePojos = new ArrayList<ClpAdvicePojo>();
        }
        return this.clpAdvicePojos;
    }

    /**
     * 获取ctzCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtzCode() {
        return ctzCode;
    }

    /**
     * 设置ctzCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtzCode(String value) {
        this.ctzCode = value;
    }

    /**
     * 获取ctzDesc属性的值。
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
     * 设置ctzDesc属性的值。
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
     * 获取deptCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeptCode() {
        return deptCode;
    }

    /**
     * 设置deptCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeptCode(String value) {
        this.deptCode = value;
    }

    /**
     * 获取deptDesc属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeptDesc() {
        return deptDesc;
    }

    /**
     * 设置deptDesc属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeptDesc(String value) {
        this.deptDesc = value;
    }

    /**
     * 获取diagCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiagCode() {
        return diagCode;
    }

    /**
     * 设置diagCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiagCode(String value) {
        this.diagCode = value;
    }

    /**
     * 获取diagDesc属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiagDesc() {
        return diagDesc;
    }

    /**
     * 设置diagDesc属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiagDesc(String value) {
        this.diagDesc = value;
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
    public void setDiags(List<String> diags) {
		this.diags = diags;
	}
    
    public void setClpAdvicePojos(List<ClpAdvicePojo> clpAdvicePojos){
    	this.clpAdvicePojos = clpAdvicePojos;
    }

}
