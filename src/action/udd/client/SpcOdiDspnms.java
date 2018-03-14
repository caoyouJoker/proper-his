
package action.udd.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for spcOdiDspnms complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="spcOdiDspnms">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="spcIndStocks" type="{http://services.spc.action/}spcIndStock" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="spcOdiDspnms" type="{http://services.spc.action/}spcOdiDspnm" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spcOdiDspnms", propOrder = {
    "spcIndStocks",
    "spcOdiDspnms"
})
public class SpcOdiDspnms {

    @XmlElement(nillable = true)
    protected List<SpcIndStock> spcIndStocks;
    @XmlElement(nillable = true)
    protected List<SpcOdiDspnm> spcOdiDspnms;

    /**
     * Gets the value of the spcIndStocks property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spcIndStocks property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpcIndStocks().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpcIndStock }
     * 
     * 
     */
    public List<SpcIndStock> getSpcIndStocks() {
        if (spcIndStocks == null) {
            spcIndStocks = new ArrayList<SpcIndStock>();
        }
        return this.spcIndStocks;
    }

    /**
     * Gets the value of the spcOdiDspnms property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spcOdiDspnms property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpcOdiDspnms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpcOdiDspnm }
     * 
     * 
     */
    public List<SpcOdiDspnm> getSpcOdiDspnms() {
        if (spcOdiDspnms == null) {
            spcOdiDspnms = new ArrayList<SpcOdiDspnm>();
        }
        return this.spcOdiDspnms;
    }

	public void setSpcIndStocks(List<SpcIndStock> spcIndStocks) {
		this.spcIndStocks = spcIndStocks;
	}

	public void setSpcOdiDspnms(List<SpcOdiDspnm> spcOdiDspnms) {
		this.spcOdiDspnms = spcOdiDspnms;
	}

    
}
