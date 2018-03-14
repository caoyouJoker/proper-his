
package action.spc.accountclient;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for indAccounts complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="indAccounts">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="indAccounts" type="{http://inf.ind.jdo/}indAccount" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="indCodeMaps" type="{http://inf.ind.jdo/}indCodeMap" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "indAccounts", propOrder = {
    "indAccounts",
    "indCodeMaps"
})
public class IndAccounts {

    @XmlElement(nillable = true)
    protected List<IndAccount> indAccounts;
   

	@XmlElement(nillable = true)
    protected List<IndCodeMap> indCodeMaps;

    /**
     * Gets the value of the indAccounts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the indAccounts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIndAccounts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IndAccount }
     * 
     * 
     */
    public List<IndAccount> getIndAccounts() {
        if (indAccounts == null) {
            indAccounts = new ArrayList<IndAccount>();
        }
        return this.indAccounts;
    }

    /**
     * Gets the value of the indCodeMaps property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the indCodeMaps property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIndCodeMaps().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IndCodeMap }
     * 
     * 
     */
    public List<IndCodeMap> getIndCodeMaps() {
        if (indCodeMaps == null) {
            indCodeMaps = new ArrayList<IndCodeMap>();
        }
        return this.indCodeMaps;
    }

    public void setIndAccounts(List<IndAccount> indAccounts) {
		this.indAccounts = indAccounts;
	}

	public void setIndCodeMaps(List<IndCodeMap> indCodeMaps) {
		this.indCodeMaps = indCodeMaps;
	}
}
