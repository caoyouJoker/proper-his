
package action.device.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for disconnect complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="disconnect">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="readerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "disconnect", propOrder = {
    "readerId"
})
public class Disconnect {

    protected String readerId;

    /**
     * Gets the value of the readerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReaderId() {
        return readerId;
    }

    /**
     * Sets the value of the readerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReaderId(String value) {
        this.readerId = value;
    }

}
