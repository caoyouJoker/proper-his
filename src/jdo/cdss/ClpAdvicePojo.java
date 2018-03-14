
package jdo.cdss;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>clpAdvicePojo complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="clpAdvicePojo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="clncpathCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="clncpathDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "clpAdvicePojo", propOrder = {
    "clncpathCode",
    "clncpathDesc"
})
public class ClpAdvicePojo {

    protected String clncpathCode;
    protected String clncpathDesc;

    /**
     * 获取clncpathCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClncpathCode() {
        return clncpathCode;
    }

    /**
     * 设置clncpathCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClncpathCode(String value) {
        this.clncpathCode = value;
    }

    /**
     * 获取clncpathDesc属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClncpathDesc() {
        return clncpathDesc;
    }

    /**
     * 设置clncpathDesc属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClncpathDesc(String value) {
        this.clncpathDesc = value;
    }

}
