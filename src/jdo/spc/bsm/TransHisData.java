
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
 *         &lt;element name="opwinid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optype" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="opstr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="opip" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="opmanno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="opmanname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "opwinid",
    "optype",
    "opstr",
    "opip",
    "opmanno",
    "opmanname",
    "retval",
    "retmsg"
})
@XmlRootElement(name = "TransHisData")
public class TransHisData {

    protected String opwinid;
    protected int optype;
    protected String opstr;
    protected String opip;
    protected String opmanno;
    protected String opmanname;
    protected int retval;
    protected String retmsg;

    /**
     * Gets the value of the opwinid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpwinid() {
        return opwinid;
    }

    /**
     * Sets the value of the opwinid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpwinid(String value) {
        this.opwinid = value;
    }

    /**
     * Gets the value of the optype property.
     * 
     */
    public int getOptype() {
        return optype;
    }

    /**
     * Sets the value of the optype property.
     * 
     */
    public void setOptype(int value) {
        this.optype = value;
    }

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
     * Gets the value of the opip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpip() {
        return opip;
    }

    /**
     * Sets the value of the opip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpip(String value) {
        this.opip = value;
    }

    /**
     * Gets the value of the opmanno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpmanno() {
        return opmanno;
    }

    /**
     * Sets the value of the opmanno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpmanno(String value) {
        this.opmanno = value;
    }

    /**
     * Gets the value of the opmanname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpmanname() {
        return opmanname;
    }

    /**
     * Sets the value of the opmanname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpmanname(String value) {
        this.opmanname = value;
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
