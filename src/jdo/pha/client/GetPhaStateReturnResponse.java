
package jdo.pha.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getPhaStateReturnResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getPhaStateReturnResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://inf.spc.jdo/}spcOpdOrderReturnDto" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getPhaStateReturnResponse", propOrder = {
    "_return"
})
public class GetPhaStateReturnResponse {

    @XmlElement(name = "return")
    protected SpcOpdOrderReturnDto _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link SpcOpdOrderReturnDto }
     *     
     */
    public SpcOpdOrderReturnDto getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link SpcOpdOrderReturnDto }
     *     
     */
    public void setReturn(SpcOpdOrderReturnDto value) {
        this._return = value;
    }

}
