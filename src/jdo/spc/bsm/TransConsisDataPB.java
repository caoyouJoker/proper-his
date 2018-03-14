
package jdo.spc.bsm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="opstr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="retval" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="retmsg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "opstr",
    "retval",
    "retmsg"
})
@XmlRootElement(name = "TransConsisDataPB")
public class TransConsisDataPB {

    protected String opstr;
    protected int retval;
    protected String retmsg;

    /**
     * Gets the value of the opstr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpstr() {
        return opstr;
    }

    /**
     * Sets the value of the opstr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpstr(String value) {
        this.opstr = value;
    }

    /**
     * Gets the value of the retval property.
     * 
     */
    public int getRetval() {
        return retval;
    }

    /**
     * Sets the value of the retval property.
     * 
     */
    public void setRetval(int value) {
        this.retval = value;
    }

    /**
     * Gets the value of the retmsg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRetmsg() {
        return retmsg;
    }

    /**
     * Sets the value of the retmsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRetmsg(String value) {
        this.retmsg = value;
    }

}
