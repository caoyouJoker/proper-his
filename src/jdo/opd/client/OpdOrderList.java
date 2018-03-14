
package jdo.opd.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for opdOrderList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="opdOrderList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="opdOrderList" type="{http://ws.opd.jdo/}opdOrder" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="rxNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "opdOrderList", propOrder = {
    "opdOrderList",
    "rxNo"
})
public class OpdOrderList {

    @XmlElement(nillable = true)
    protected List<OpdOrder> opdOrderList;
    protected String rxNo;

    /**
     * Gets the value of the opdOrderList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the opdOrderList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOpdOrderList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OpdOrder }
     * 
     * 
     */
    public List<OpdOrder> getOpdOrderList() {
        if (opdOrderList == null) {
            opdOrderList = new ArrayList<OpdOrder>();
        }
        return this.opdOrderList;
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

	public void setOpdOrderList(List<OpdOrder> opdOrderList) {
		this.opdOrderList = opdOrderList;
	}

}
